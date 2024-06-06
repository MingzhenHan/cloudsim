package org.cloudbus.cloudsim.container.resourceAllocatorMigrationEnabled;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.container.containerSelectionPolicies.PowerContainerSelectionPolicy;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.placementPolicies.PlacementPolicy;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.List;

/**
 * Created by sareh on 3/08/15.
 * Modified by Remo Andreoli (Feb 2024)
 */
public class PowerContainerVmAllocationPolicyMigrationStaticThresholdMC extends PowerContainerVmAllocationPolicyMigrationAbstractContainerHostSelection {
//public class PowerContainerVmAllocationPolicyMigrationStaticThresholdMC extends PowerContainerVmAllocationPolicyMigrationAbstractContainerHostSelectionUnderUtilizedAdded {


    /**
     * The utilization threshold.
     */
    private double utilizationThreshold = 0.9;

    /**
     * Instantiates a new power vm allocation policy migration mad.
     *
     * @param hostList             the host list
     * @param vmSelectionPolicy    the vm selection policy
     * @param utilizationThreshold the utilization threshold
     */
    public PowerContainerVmAllocationPolicyMigrationStaticThresholdMC(
            List<? extends Host> hostList,
            PowerContainerVmSelectionPolicy vmSelectionPolicy, PowerContainerSelectionPolicy containerSelectionPolicy,
            PlacementPolicy hostSelectionPolicy, double utilizationThreshold,
            int numberOfVmTypes, int[] vmPes, int[] vmRam, long vmBw, long vmSize, double[] vmMips) {
        super(hostList, vmSelectionPolicy, containerSelectionPolicy, hostSelectionPolicy,
        		 numberOfVmTypes, vmPes, vmRam, vmBw, vmSize, vmMips);
        setUtilizationThreshold(utilizationThreshold);
    }

    /**
     * Checks if is host over utilized.
     *
     * @param host the _host
     * @return true, if is host over utilized
     */
    @Override
    protected boolean isHostOverUtilized(PowerHost host) {
        addHistoryEntry(host, getUtilizationThreshold());
        double totalRequestedMips = 0;
        for (ContainerVm vm : host.<ContainerVm>getGuestList()) {
            totalRequestedMips += vm.getCurrentRequestedTotalMips();
        }
        double utilization = totalRequestedMips / host.getTotalMips();
        return utilization > getUtilizationThreshold();
    }

    @Override
    protected boolean isHostUnderUtilized(PowerHost host) {
        return false;
    }

    /**
     * Sets the utilization threshold.
     *
     * @param utilizationThreshold the new utilization threshold
     */
    protected void setUtilizationThreshold(double utilizationThreshold) {
        this.utilizationThreshold = utilizationThreshold;
    }

    /**
     * Gets the utilization threshold.
     *
     * @return the utilization threshold
     */
    protected double getUtilizationThreshold() {
        return utilizationThreshold;
    }
    @Override
    public void setDatacenter(ContainerDatacenter datacenter) {
        super.setDatacenter(datacenter);
    }

}



