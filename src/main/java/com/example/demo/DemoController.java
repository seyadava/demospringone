package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.samples.*;
import java.util.HashMap;
// import com.microsoft.azure.AzureEnvironment;
// import com.microsoft.azure.credentials.ApplicationTokenCredentials;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLContext;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.CloudException;
import com.microsoft.azure.arm.model.CreatedResources;
import com.microsoft.azure.arm.utils.SdkContext;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.profile_2018_03_01_hybrid.Azure;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.v2018_02_01.ResourceGroup;
import com.microsoft.azure.management.resources.v2018_02_01.implementation.ResourceGroupInner;
import com.microsoft.azure.management.storage.v2016_01_01.Kind;
import com.microsoft.azure.management.storage.v2016_01_01.Sku;
import com.microsoft.azure.management.storage.v2016_01_01.SkuName;
import com.microsoft.azure.management.storage.v2016_01_01.StorageAccount;
import com.microsoft.azure.management.storage.v2016_01_01.StorageAccountListKeysResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

@Controller
public class DemoController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/result")
    public String push(
            Model model,
            @RequestParam(name = "azsaname", required = true) String azureStorageName,
            @RequestParam(name = "azrgname", required = true) String azureRgName,
            @RequestParam(name = "azsrgname", required = true) String azsRgName,
            @RequestParam(name = "saname", required = true) String azsStorageNname) throws CloudException, IOException {

        
        final String armEndpoint = "https://management.local.azurestack.external/";
        final String location = "local";

        // Get Azure Stack Active Directory Endpoints
        final HashMap<String, String> settings = ManageResourceGroup.getActiveDirectorySettings(armEndpoint);

        // Add Azure Stack environment
        AzureEnvironment AZURE_STACK = new AzureEnvironment(new HashMap<String, String>() {
            {
                put("managementEndpointUrl", settings.get("audience"));
                put("resourceManagerEndpointUrl", armEndpoint);
                put("galleryEndpointUrl", settings.get("galleryEndpoint"));
                put("activeDirectoryEndpointUrl", settings.get("login_endpoint"));
                put("activeDirectoryResourceId", settings.get("audience"));
                put("activeDirectoryGraphResourceId", settings.get("graphEndpoint"));
                put("storageEndpointSuffix", armEndpoint.substring(armEndpoint.indexOf('.')));
                put("keyVaultDnsSuffix", ".adminvault" + armEndpoint.substring(armEndpoint.indexOf('.')));
            }
        });

        // Public Azure SP Credentials
        String client1 = "77d1dd1e-ef5e-4103-ba24-6425f11159f4";
        String tenant1 = "72f988bf-86f1-41af-91ab-2d7cd011db47";
        String key1 = "Lt57o3JZ700B6JMRnmCwxuVuWtVyfViI4eCUJG7UTN8=";
        String subscriptionId1 = "7dec1fd6-2e45-4798-9be5-efba704319b4";

        // Authenticate against Public Azure
        ApplicationTokenCredentials azureCredential = (ApplicationTokenCredentials) new ApplicationTokenCredentials(
                client1, tenant1, key1, AzureEnvironment.AZURE).withDefaultSubscriptionId(subscriptionId1);

        MyAzure publicAzure = MyAzure.configure().withLogLevel(com.microsoft.rest.LogLevel.BODY_AND_HEADERS)
                .authenticate(azureCredential, subscriptionId1);

        // Azure Stack SP credentials
        String client = "f12066d3-e76c-4b33-885f-e71b2ee10b83";
        String tenant = "2b3697e6-a7a2-4cdd-a3d4-f4ef6505cd4f";
        String key = "4cbZjabPlukB2C9hCaAQxTbZXWo6XV1Wn/zOlA2uSCY=";
        String subscriptionId = "21a84263-a707-4dc3-b38b-3f91c959725e";

        // Authenticate against Azure Stack
        AzureTokenCredentials credentials = new ApplicationTokenCredentials(client, tenant, key, AZURE_STACK)
                .withDefaultSubscriptionId(subscriptionId);

        MyAzure azureStack = MyAzure.configure().withLogLevel(com.microsoft.rest.LogLevel.BODY_AND_HEADERS)
                .authenticate(credentials, credentials.defaultSubscriptionId());

        // Get 2 random names for Azure Stack if not provided
        String rgName = SdkContext.randomResourceName("rg", 20);
        String saName = SdkContext.randomResourceName("sa", 20);
        if (azsRgName != "") {
            rgName = azsRgName;
        }
        if (azsStorageNname != "")
        {
            saName = azsStorageNname;
        }

        // Create a resource group in Azure Stack
        ResourceGroup resourceGroup = createResourceGroup(location, azureStack, rgName);

        // Create a RG in Azure
        String rgName_Azure = SdkContext.randomResourceName("rg", 20);
        String saName_Azure = SdkContext.randomResourceName("sa", 20);
        if (azureRgName != "") {
            rgName_Azure = azureRgName;
        }
        if (azureStorageName != "")
        {
           saName_Azure = azureStorageName;
        }

        model.addAttribute("azsaname", saName_Azure);
        model.addAttribute("azrgname", rgName_Azure);
        model.addAttribute("azsrgname", rgName);
        model.addAttribute("saname", saName);

        ResourceGroup rg_Azure = createResourceGroup(Region.US_WEST.name(), publicAzure, rgName_Azure);

        // Create a storage account in the resource group in Azure Stack
        StorageAccount storageAccount = createStorageAccount(location, azureStack, rgName, saName);

        System.out.println("Storage account: " + storageAccount.id() + "\nKeys:");

        // List storage account keys
        azureStack.storageAccounts().listKeysAsync(rgName, saName).flatMapIterable(StorageAccountListKeysResult::keys)
                .doOnNext(k -> System.out.println("\t" + k.keyName() + ": " + k.value())).toBlocking().subscribe();

        // Create a storage account in the resource group in Public Azure
        StorageAccount storageAccount_azure = createStorageAccount(Region.US_WEST.name(), publicAzure, rgName_Azure, saName_Azure);

        // List Resource Groups in Azure Stack
        List<ResourceGroupInner> stackGroups = azureStack.resourceGroups().inner().list();
        
        // Print all the RGs in Azure subscription
        for (ResourceGroupInner rg : stackGroups) {
            Utils.print(rg);
        }

        // Interact with public Azure; List Resource Groups
        List<ResourceGroupInner> publicGroups = publicAzure.resourceGroups().inner().list();

        // Print all the RGs in Azure subscription
        for (ResourceGroupInner rg : publicGroups) {
            Utils.print(rg);
        }

        // Delete Rg created in Azure
        publicAzure.resourceGroups().inner().delete(rgName_Azure);
        System.out.println("Resource Group Deleted:" + rgName_Azure);

        // Delete Rg created in Azure Stack
        azureStack.resourceGroups().inner().delete(rgName);

        return "greeting";
    }

    private StorageAccount createStorageAccount(final String location, MyAzure azureCloud, String rgName, String saName) {
        StorageAccount storageAccount = azureCloud.storageAccounts().define(saName).withRegion(location)
                .withExistingResourceGroup(rgName).withKind(Kind.STORAGE)
                .withSku(new Sku().withName(SkuName.STANDARD_LRS)).create();

        // List storage account keys for Storage account in Public Azure
        System.out.println("Storage account: " + storageAccount.id() + "\nKeys:");
        azureCloud.storageAccounts().listKeysAsync(rgName, saName).flatMapIterable(StorageAccountListKeysResult::keys)
                .doOnNext(k -> System.out.println("\t" + k.keyName() + ": " + k.value())).toBlocking().subscribe();

        return storageAccount;
    }

    private ResourceGroup createResourceGroup(final String location, MyAzure azureCloud, String rgName) {
        ResourceGroup resourceGroup = azureCloud.resourceGroups().define(rgName).withExistingSubscription()
                .withLocation(location).create();

        // Print the resource group created 
        Utils.print(resourceGroup);

        return resourceGroup;
    }
}
