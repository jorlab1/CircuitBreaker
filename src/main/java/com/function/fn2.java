package com.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.appservice.models.FunctionApp;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.http.policy.HttpLogDetailLevel;
import org.json.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class fn2 {
    /**
     * This function listens at endpoint "/api/fn2". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/fn2
     * 2. curl {your host}/api/fn2?name=HTTP%20Query
     */
    @FunctionName("disableHttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse http message request
        String query = request.getQueryParameters().get("name");
        String msgBody = request.getBody().orElse(query);

        JSONObject obj = new JSONObject(msgBody);
        String functionAppID = obj.get("functionAppID").toString();
        String functionName = obj.get("functionName").toString();
        String disabled = obj.get("disabled").toString();

        // instantiate azure profile and credential from ids stored in environment variables 
        final AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        final TokenCredential credential = new DefaultAzureCredentialBuilder()
            .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
            .build();
 
        // this object does the actual managing 
        AzureResourceManager azureResourceManager = AzureResourceManager
            .configure()
            .withLogLevel(HttpLogDetailLevel.BASIC)
            .authenticate(credential, profile)
            .withDefaultSubscription();

        // grab target function app (disabling is not restricted to functions in same app or even resource group (as long as permissions allow))
        FunctionApp functionApp = azureResourceManager.functionApps().getById(functionAppID);

        // define target function and disable it
        functionApp.update().withAppSetting(("AzureWebJobs."+functionName+".Disabled"),disabled).apply();

        if (msgBody == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + msgBody).build();
        }
    }
}
