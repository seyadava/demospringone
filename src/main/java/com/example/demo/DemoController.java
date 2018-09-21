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
import com.microsoft.azure.arm.utils.SdkContext;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.profile_2018_03_01_hybrid.Azure;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.v2018_02_01.ResourceGroup;
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
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
    
    @GetMapping("/result")
    public String push(@RequestParam(name="vmname", required=true) String vmname, Model model, @RequestParam(name="azrgname", required=true) String azrgname, @RequestParam(name="azsrgname", required=true) String azsrgname, @RequestParam(name="saname", required=true) String saname) throws CloudException, IOException {
        model.addAttribute("vmname", vmname);
        model.addAttribute("azrgname", azrgname);
        model.addAttribute("azsrgname", azsrgname);
        model.addAttribute("saname", saname);
        
//         // // final String armEndpoint = System.getenv("ARM_ENDPOINT");
//         // // final String location = System.getenv("RESOURCE_LOCATION");
//         // final String armEndpoint = "https://management.local.azurestack.external/";
//         // final String location = "local";
//         // final HashMap<String, String> settings = ManageResourceGroup.getActiveDirectorySettings(armEndpoint);

//         // // Get AzureStack cloud endpoints
//         // AzureEnvironment AZURE_STACK = new AzureEnvironment(new HashMap<String, String>() {
//         // {
//         //             put("managementEndpointUrl", settings.get("audience"));
//         //             put("resourceManagerEndpointUrl", armEndpoint);
//         //             put("galleryEndpointUrl", settings.get("galleryEndpoint"));
//         //             put("activeDirectoryEndpointUrl", settings.get("login_endpoint"));
//         //             put("activeDirectoryResourceId", settings.get("audience"));
//         //             put("activeDirectoryGraphResourceId", settings.get("graphEndpoint"));
//         //             put("storageEndpointSuffix", armEndpoint.substring(armEndpoint.indexOf('.')));
//         //             put("keyVaultDnsSuffix", ".adminvault" + armEndpoint.substring(armEndpoint.indexOf('.')));
//         //         }
//         // });

//         // //String client = System.getenv("CLIENT_ID");
//         // String client = "f12066d3-e76c-4b33-885f-e71b2ee10b83";
//         // String tenant = "2b3697e6-a7a2-4cdd-a3d4-f4ef6505cd4f";
//         // String key = "4cbZjabPlukB2C9hCaAQxTbZXWo6XV1Wn/zOlA2uSCY=";
//         // String subscriptionId ="00606c96-1a53-4bc2-abd9-5f345d0f0895";
//         // // Authenticate to AzureStack using Service principal creds
//         // ApplicationTokenCredentials credentials = (ApplicationTokenCredentials) new ApplicationTokenCredentials(
//         //         client, tenant, key, AZURE_STACK).withDefaultSubscriptionId(subscriptionId);
//         // MyAzure azureStack = MyAzure.configure().withLogLevel(com.microsoft.rest.LogLevel.BASIC)
//         //         .authenticate(credentials, subscriptionId);
//         // // Manage resource groups
//         // ManageResourceGroup.runSample(azureStack, location);
//         //return "greeting";

//         // final String armEndpoint = "https://adminmanagement.redmond.ext-v.masd.stbtest.microsoft.com/";
//         final String armEndpoint = "https://management.local.azurestack.external/";
//                 final String location = "local";

//                 // Get Azure Stack Active Directory Endpoints
//                 final HashMap<String, String> settings = ManageResourceGroup.getActiveDirectorySettings(armEndpoint);

//                  // Add Azure Stack environment
//                  AzureEnvironment AZURE_STACK = new AzureEnvironment(new HashMap<String, String>() {
//                     {
//                         put("managementEndpointUrl", settings.get("audience"));
//                         put("resourceManagerEndpointUrl", armEndpoint);
//                         put("galleryEndpointUrl", settings.get("galleryEndpoint"));
//                         put("activeDirectoryEndpointUrl", settings.get("login_endpoint"));
//                         put("activeDirectoryResourceId", settings.get("audience"));
//                         put("activeDirectoryGraphResourceId", settings.get("graphEndpoint"));
//                         put("storageEndpointSuffix", armEndpoint.substring(armEndpoint.indexOf('.')));
//                         put("keyVaultDnsSuffix", ".adminvault" + armEndpoint.substring(armEndpoint.indexOf('.')));
//                     }
//                 });
    
//                 // Public Azure SP Credentials
//                 String client1 = "77d1dd1e-ef5e-4103-ba24-6425f11159f4";
//                 String tenant1 = "72f988bf-86f1-41af-91ab-2d7cd011db47";
//                 String key1 = "Lt57o3JZ700B6JMRnmCwxuVuWtVyfViI4eCUJG7UTN8=";
//                 String subscriptionId1 = "7dec1fd6-2e45-4798-9be5-efba704319b4";

//                 // Authenticate against Public Azure
//                 ApplicationTokenCredentials azureCredential = (ApplicationTokenCredentials) new ApplicationTokenCredentials(
//                                 client1, tenant1, key1, AzureEnvironment.AZURE)
//                                                 .withDefaultSubscriptionId(subscriptionId1);
//                 // com.microsoft.azure.management.Azure publicAzure = com.microsoft.azure.management.Azure
//                 //                 .configure().withLogLevel(com.microsoft.rest.LogLevel.BODY_AND_HEADERS)
//                 //                 .authenticate(azureCredential).withDefaultSubscription();
//                 MyAzure publicAzure = MyAzure.configure().withLogLevel(com.microsoft.rest.LogLevel.BODY_AND_HEADERS)
//                                 .authenticate(azureCredential, subscriptionId1);
//                 // Azure Stack SP credentials
//                 // String client = "13b6b0c1-93c3-4a03-ac06-a9297b4236cd";
//                 // String tenant = "0a9c9a9e-2774-489f-8560-bae5824f8b46";
//                 // String key = "System.Security.SecureString";
//                 // String subscriptionId = "7ace1fe3-2056-403e-b1b4-bd4b1faa4794";
//  String client = "f12066d3-e76c-4b33-885f-e71b2ee10b83";
//         String tenant = "2b3697e6-a7a2-4cdd-a3d4-f4ef6505cd4f";
//         String key = "4cbZjabPlukB2C9hCaAQxTbZXWo6XV1Wn/zOlA2uSCY=";
//         String subscriptionId ="21a84263-a707-4dc3-b38b-3f91c959725e";
//                 // Authenticate against Azure Stack
//                 AzureTokenCredentials credentials = new ApplicationTokenCredentials(client, tenant, key, AZURE_STACK)
//                                 .withDefaultSubscriptionId(subscriptionId);

//                 MyAzure azureStack = MyAzure.configure().withLogLevel(com.microsoft.rest.LogLevel.BODY_AND_HEADERS)
//                                 .authenticate(credentials, credentials.defaultSubscriptionId());

//                 // Get 2 random names
//                 String rgName = SdkContext.randomResourceName("rg", 20);
//                 String saName = SdkContext.randomResourceName("sa", 20);

//                 // Create a resource group in Azure Stack
//                 ResourceGroup resourceGroup = azureStack.resourceGroups().define(rgName).withExistingSubscription()
//                                 .withLocation(location).create();

//                 // Create a storage account in the resource group in Azure Stack
//                 StorageAccount storageAccount = azureStack.storageAccounts().define(saName).withRegion(location)
//                                 .withExistingResourceGroup(rgName).withKind(Kind.STORAGE)
//                                 .withSku(new Sku().withName(SkuName.STANDARD_LRS)).create();

//                 System.out.println("Storage account: " + storageAccount.id() + "\nKeys:");

//                 // List storage account keys
//                 azureStack.storageAccounts().listKeysAsync(rgName, saName)
//                                 .flatMapIterable(StorageAccountListKeysResult::keys)
//                                 .doOnNext(k -> System.out.println("\t" + k.keyName() + ": " + k.value())).toBlocking()
//                                 .subscribe();

//                 // Interact with public Azure; List Resource Groups
//                 // List<com.microsoft.azure.management.resources.ResourceGroup> publicGroups = publicAzure.resourceGroups().inner().list();
                                

//                 // Print all the RGs in Azure subscription
//                // for(com.microsoft.azure.management.resources.ResourceGroup rg : publicGroups)
//                 //{
//                 //        Utils.print(rg);
//                 //}

//                 // Create a RG in Azure
//                 String rgName_Azure = SdkContext.randomResourceName("rg", 20);
//                 ResourceGroup rg_Azure = publicAzure.resourceGroups()
//                                 .define(rgName_Azure).withExistingSubscription().withLocation(Region.US_WEST.name()).create();

//                 Utils.print(rg_Azure);

//                 // Delete Rg created in Azure
//                 publicAzure.resourceGroups().inner().delete(rgName_Azure);
//                 System.out.println("Resource Group Deleted:" + rgName_Azure);

//                 // Delete Rg created in Azure Stack
//                 azureStack.resourceGroups().inner().delete(rgName);















        return "greeting";
    }
}
