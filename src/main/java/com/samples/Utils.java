package com.samples;

import java.util.List;
import java.util.Map;

import com.microsoft.azure.management.compute.v2017_03_30.DataDisk;
import com.microsoft.azure.management.compute.v2017_03_30.NetworkInterfaceReference;
import com.microsoft.azure.management.compute.v2017_03_30.VirtualMachine;
import com.microsoft.azure.management.compute.v2017_03_30.VirtualMachineExtension;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkPeering;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.ServiceEndpointType;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.v2018_02_01.implementation.ResourceGroupInner;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.management.storage.StorageAccountEncryptionStatus;
import com.microsoft.azure.management.storage.StorageService;

/**
* Common utils for Azure management samples.
*/

public final class Utils {
    /**
     * Print resource group info.
     *
     * @param resource a resource group
     */
    public static void print(ResourceGroup resource) {
        StringBuilder info = new StringBuilder();
        info.append("Resource Group: ").append(resource.id())
                .append("\n\tName: ").append(resource.name())
                .append("\n\tRegion: ").append(resource.region())
                .append("\n\tTags: ").append(resource.tags());
        System.out.println(info.toString());
    }

    public static void print(ResourceGroupInner resource) {
        StringBuilder info = new StringBuilder();
        info.append("Resource Group: ").append(resource.id())
                .append("\n\tName: ").append(resource.name())
                .append("\n\tRegion: ").append(resource.location())
                .append("\n\tTags: ").append(resource.tags());
        System.out.println(info.toString());
    }

    public static void print(com.microsoft.azure.management.resources.v2018_02_01.ResourceGroup resource) {
        StringBuilder info = new StringBuilder();
        info.append("Resource Group: ").append(resource.id())
                .append("\n\tName: ").append(resource.name())
                .append("\n\tRegion: ").append(resource.location())
                .append("\n\tTags: ").append(resource.tags());
        System.out.println(info.toString());
    }

/**
     * Print network info.
     *
     * @param resource a network
     * @throws CloudException Cloud errors
     */
    public static void print(Network resource) {
        StringBuilder info = new StringBuilder();
        
        info.append("Network: ").append(resource.id())
                .append("Name: ").append(resource.name())
                .append("\n\tResource group: ").append(resource.resourceGroupName())
                .append("\n\tRegion: ").append(resource.region())
                .append("\n\tTags: ").append(resource.tags())
                .append("\n\tAddress spaces: ").append(resource.addressSpaces())
                .append("\n\tDNS server IPs: ").append(resource.dnsServerIPs());

        // Output subnets
        for (Subnet subnet : resource.subnets().values()) {
            info.append("\n\tSubnet: ").append(subnet.name())
                    .append("\n\t\tAddress prefix: ").append(subnet.addressPrefix());

            // Output associated NSG
            NetworkSecurityGroup subnetNsg = subnet.getNetworkSecurityGroup();
            if (subnetNsg != null) {
                info.append("\n\t\tNetwork security group ID: ").append(subnetNsg.id());
            }

            // Output associated route table
            RouteTable routeTable = subnet.getRouteTable();
            if (routeTable != null) {
                info.append("\n\tRoute table ID: ").append(routeTable.id());
            }

            // Output services with access
            Map<ServiceEndpointType, List<Region>> services = subnet.servicesWithAccess();
            if (services.size() > 0) {
                info.append("\n\tServices with access");
                for (Map.Entry<ServiceEndpointType, List<Region>> service : services.entrySet()) {
                    //info.append("\n\t\tService: ")
                            // .append(service.getKey())
                            // .append(" Regions: " + service.getValue() + "");
                }
            }
        }

        // Output peerings
        // for (NetworkPeering peering : resource.peerings().list()) {
        //     info.append("\n\tPeering: ").append(peering.name())
        //             .append("\n\t\tRemote network ID: ").append(peering.remoteNetworkId())
        //             .append("\n\t\tPeering state: ").append(peering.state())
        //             .append("\n\t\tIs traffic forwarded from remote network allowed? ").append(peering.isTrafficForwardingFromRemoteNetworkAllowed())
        //             .append("\n\t\tGateway use: ").append(peering.gatewayUse());
        // }
        System.out.println(info.toString());
    }

        /**
     * Print storage account.
     *
     * @param storageAccount a storage account
     */
    public static void print(StorageAccount storageAccount) {
        System.out.println(storageAccount.name()
                + " created @ " + storageAccount.creationTime());

        StringBuilder info = new StringBuilder().append("Storage Account: ").append(storageAccount.id())
                .append("Name: ").append(storageAccount.name())
                .append("\n\tResource group: ").append(storageAccount.resourceGroupName())
                .append("\n\tRegion: ").append(storageAccount.region())
                .append("\n\tSKU: ").append(storageAccount.skuType().name().toString())
                .append("\n\tAccessTier: ").append(storageAccount.accessTier())
                .append("\n\tKind: ").append(storageAccount.kind());

        info.append("\n\tNetwork Rule Configuration: ")
                .append("\n\t\tAllow reading logs from any network: ").append(storageAccount.canReadLogEntriesFromAnyNetwork())
                .append("\n\t\tAllow reading metrics from any network: ").append(storageAccount.canReadMetricsFromAnyNetwork())
                .append("\n\t\tAllow access from all azure services: ").append(storageAccount.canAccessFromAzureServices());

        if (storageAccount.networkSubnetsWithAccess().size() > 0) {
            info.append("\n\t\tNetwork subnets with access: ");
            for (String subnetId : storageAccount.networkSubnetsWithAccess()) {
                info.append("\n\t\t\t").append(subnetId);
            }
        }
        if (storageAccount.ipAddressesWithAccess().size() > 0) {
            info.append("\n\t\tIP addresses with access: ");
            for (String ipAddress : storageAccount.ipAddressesWithAccess()) {
                info.append("\n\t\t\t").append(ipAddress);
            }
        }
        if (storageAccount.ipAddressRangesWithAccess().size() > 0) {
            info.append("\n\t\tIP address-ranges with access: ");
            for (String ipAddressRange : storageAccount.ipAddressRangesWithAccess()) {
                info.append("\n\t\t\t").append(ipAddressRange);
            }
        }
        info.append("\n\t\tTraffic allowed from only HTTPS: ").append(storageAccount.inner().enableHttpsTrafficOnly());

        info.append("\n\tEncryption status: ");
        for (Map.Entry<StorageService, StorageAccountEncryptionStatus> eStatus : storageAccount.encryptionStatuses().entrySet()) {
            info.append("\n\t\t").append(eStatus.getValue().storageService()).append(": ").append(eStatus.getValue().isEnabled() ? "Enabled" : "Disabled");
        }

        System.out.println(info.toString());
    }

        /**
     * Print virtual machine info.
     *
     * @param resource a virtual machine
     */
    public static void print(VirtualMachine resource) {

        StringBuilder storageProfile = new StringBuilder().append("\n\tStorageProfile: ");
        if (resource.storageProfile().imageReference() != null) {
            storageProfile.append("\n\t\tImageReference:");
            storageProfile.append("\n\t\t\tPublisher: ").append(resource.storageProfile().imageReference().publisher());
            storageProfile.append("\n\t\t\tOffer: ").append(resource.storageProfile().imageReference().offer());
            storageProfile.append("\n\t\t\tSKU: ").append(resource.storageProfile().imageReference().sku());
            storageProfile.append("\n\t\t\tVersion: ").append(resource.storageProfile().imageReference().version());
        }

        if (resource.storageProfile().osDisk() != null) {
            storageProfile.append("\n\t\tOSDisk:");
            storageProfile.append("\n\t\t\tOSType: ").append(resource.storageProfile().osDisk().osType());
            storageProfile.append("\n\t\t\tName: ").append(resource.storageProfile().osDisk().name());
            storageProfile.append("\n\t\t\tCaching: ").append(resource.storageProfile().osDisk().caching());
            storageProfile.append("\n\t\t\tCreateOption: ").append(resource.storageProfile().osDisk().createOption());
            storageProfile.append("\n\t\t\tDiskSizeGB: ").append(resource.storageProfile().osDisk().diskSizeGB());
            if (resource.storageProfile().osDisk().image() != null) {
                storageProfile.append("\n\t\t\tImage Uri: ").append(resource.storageProfile().osDisk().image().uri());
            }
            if (resource.storageProfile().osDisk().vhd() != null) {
                storageProfile.append("\n\t\t\tVhd Uri: ").append(resource.storageProfile().osDisk().vhd().uri());
            }
            if (resource.storageProfile().osDisk().encryptionSettings() != null) {
                storageProfile.append("\n\t\t\tEncryptionSettings: ");
                storageProfile.append("\n\t\t\t\tEnabled: ").append(resource.storageProfile().osDisk().encryptionSettings().enabled());
                storageProfile.append("\n\t\t\t\tDiskEncryptionKey Uri: ").append(resource
                        .storageProfile()
                        .osDisk()
                        .encryptionSettings()
                        .diskEncryptionKey().secretUrl());
                storageProfile.append("\n\t\t\t\tKeyEncryptionKey Uri: ").append(resource
                        .storageProfile()
                        .osDisk()
                        .encryptionSettings()
                        .keyEncryptionKey().keyUrl());
            }
        }

        if (resource.storageProfile().dataDisks() != null) {
            int i = 0;
            for (DataDisk disk : resource.storageProfile().dataDisks()) {
                storageProfile.append("\n\t\tDataDisk: #").append(i++);
                storageProfile.append("\n\t\t\tName: ").append(disk.name());
                storageProfile.append("\n\t\t\tCaching: ").append(disk.caching());
                storageProfile.append("\n\t\t\tCreateOption: ").append(disk.createOption());
                storageProfile.append("\n\t\t\tDiskSizeGB: ").append(disk.diskSizeGB());
                storageProfile.append("\n\t\t\tLun: ").append(disk.lun());
                    if (disk.vhd().uri() != null) {
                        storageProfile.append("\n\t\t\tVhd Uri: ").append(disk.vhd().uri());
                }
                if (disk.image() != null) {
                    storageProfile.append("\n\t\t\tImage Uri: ").append(disk.image().uri());
                }
            }
        }

        StringBuilder osProfile = new StringBuilder().append("\n\tOSProfile: ");
        if (resource.osProfile() != null) {
            osProfile.append("\n\t\tComputerName:").append(resource.osProfile().computerName());
            if (resource.osProfile().windowsConfiguration() != null) {
                osProfile.append("\n\t\t\tWindowsConfiguration: ");
                osProfile.append("\n\t\t\t\tProvisionVMAgent: ")
                        .append(resource.osProfile().windowsConfiguration().provisionVMAgent());
                osProfile.append("\n\t\t\t\tEnableAutomaticUpdates: ")
                        .append(resource.osProfile().windowsConfiguration().enableAutomaticUpdates());
                osProfile.append("\n\t\t\t\tTimeZone: ")
                        .append(resource.osProfile().windowsConfiguration().timeZone());
            }

            if (resource.osProfile().linuxConfiguration() != null) {
                osProfile.append("\n\t\t\tLinuxConfiguration: ");
                osProfile.append("\n\t\t\t\tDisablePasswordAuthentication: ")
                        .append(resource.osProfile().linuxConfiguration().disablePasswordAuthentication());
            }
        } else {
            // OSProfile will be null for a VM attached to specialized VHD.
            osProfile.append("null");
        }

        StringBuilder networkProfile = new StringBuilder().append("\n\tNetworkProfile: ");
        for (NetworkInterfaceReference networkInterfaceReference : resource.networkProfile().networkInterfaces()) {
            networkProfile.append("\n\t\tId:").append(networkInterfaceReference.id());
        }

        StringBuilder extensions = new StringBuilder().append("\n\tExtensions: ");
        for (VirtualMachineExtension extension : resource.resources()) {
            //VirtualMachineExtension extension = extensionEntry.getValue();
            extensions.append("\n\t\tExtension: ").append(extension.id())
                    .append("\n\t\t\tName: ").append(extension.name())
                    .append("\n\t\t\tTags: ").append(extension.tags())
                    .append("\n\t\t\tProvisioningState: ").append(extension.provisioningState())
                    .append("\n\t\t\tAuto upgrade minor version enabled: ").append(extension.autoUpgradeMinorVersion())
                    .append("\n\t\t\tPublisher: ").append(extension.publisher())
                    .append("\n\t\t\tType: ").append(extension.type())
                    .append("\n\t\t\tVersion: ").append(extension.typeHandlerVersion())
                    .append("\n\t\t\tPublic Settings: ").append(extension.settings().toString());
        }

        StringBuilder zones = new StringBuilder().append("\n\tZones: ");
        zones.append(resource.zones());
        // resource.hardwareProfile().vmSize();
        System.out.println(new StringBuilder().append("Virtual Machine: ").append(resource.id())
                .append("Name: ").append(resource.name())
                .append("\n\tResource group: ").append(resource.resourceGroupName())
                .append("\n\tRegion: ").append(resource.region())
                .append("\n\tTags: ").append(resource.tags())
                .append("\n\tHardwareProfile: ")
                .append("\n\t\tSize: ")
                .append(""));
            //     .append(storageProfile)
            //     .append(osProfile)
            //     .append(networkProfile)
            //     .append(extensions)
            //    .append(zones)
            //     .toString());
    }

}
