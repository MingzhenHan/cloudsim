package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.GuestEntity;
import org.cloudbus.cloudsim.core.HostEntity;
import org.cloudbus.cloudsim.core.VirtualEntity;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static org.cloudbus.cloudsim.examples.power.Helper.printCloudletList;

/**
 * A simple example showing the use of containers (ContainerCloudSim).
 * 可以只用vm，也可以只用container,也可以同时使用。
 */

public class CloudSimOnlyContainerExample {
    /**
     * The host list.
     */
    private static List<HostEntity> hostList = new ArrayList<>();

    /**
     * The cloudlet list.
     */
    private static List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<VirtualEntity> vmlist;

    /**
     * the containerlist
     */
    private static List<GuestEntity> containerlist;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        Log.println("Starting CloudSimMultiExtensionExample1...");

        try {
            // Initialize the CloudSim package. It should be called before creating any entities.
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
            boolean trace_flag = false; // trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Create host
            int mips = 1000;
            int hostId = 0;
            int ram = 2048; // host memory (MB)
            long storage = 1000000; // host storage
            int bw = 10000;

            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(0, new PeProvisionerSimple(mips)));

            hostList.add(
                    new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList))
            );


//            mips = 1000;
            long size = 10000; // image size (MB)
//            ram = 512; // vm memory (MB)
//            bw = 1000;
            int pesNumber = 1; // number of cpus
//            String vmm = "Xen"; // VMM name


            // Create container
            containerlist = new ArrayList<>();
            GuestEntity container = new Container(2, brokerId, mips / 2, pesNumber, ram / 2, bw / 2, size / 2, "Docker",
                    new CloudletSchedulerSpaceShared(), 0);
            containerlist.add(container);

            GuestEntity container2 = new Container(3, brokerId, mips / 2, pesNumber, ram / 2, bw / 2, size / 2, "Docker",
                    new CloudletSchedulerSpaceShared(), 0);
            containerlist.add(container2);

            // Create Datacenters
            createDatacenter("Datacenter_0");

            // submit vm and container list to the broker
            broker.submitGuestList(containerlist);

            // Create Cloudlets
            cloudletList = new ArrayList<>();

            long length = 400000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet = new Cloudlet(0, length, pesNumber, fileSize,
                    outputSize, utilizationModel, utilizationModel,
                    utilizationModel);
            cloudlet.setUserId(brokerId);
            //cloudlet.setGuestId(2);
            cloudletList.add(cloudlet);

            cloudlet = new Cloudlet(1, length, pesNumber, fileSize,
                    outputSize, utilizationModel, utilizationModel,
                    utilizationModel);
            cloudlet.setUserId(brokerId);
            //cloudlet.setGuestId(2);
            cloudletList.add(cloudlet);


            // submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            // Starts the simulation
            CloudSim.startSimulation();

            CloudSim.stopSimulation();

            // Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            Log.println("CloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.println("Unwanted errors happen");
        }
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name) {
        // Create datacenter
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<>(); // we are not adding SAN
        // devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario

    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

}