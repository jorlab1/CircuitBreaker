# CircuitBreaker
An HTTP Triggered Azure Function that can disable and enable a separate Azure Function (tested on functions in the same app, API indicates it should work for functions in separate apps). The key players are fn2.java for the circuit breaker function, pom.xml for the dependencies, and then local.settings.json for the environment variables needed. The function app this is deployed to must also have a role with read/write access to the function apps holding the functions you wish to disable/enable. The body of the HTTP request triggering must contain at the top level JSON objects for "functionAppID", "functionName", and "disable". Note the "disabled" field is restricted to "1" for disabling the named function and "0" for enabling it.

## Dependencies and Requirements
Note I use the [Azure BOM](https://github.com/Azure/azure-sdk-for-java/tree/azure-core_1.44.0/sdk/boms/azure-sdk-bom) for client libraries to help with dependency management. This is hopefully everything you need, if not please cross reference the imports with the pom file.
* Java Development Kit (JDK) version 8 or above (I used 11)
* Azure Subscription
* Azure Functions Java Library v4
* [Azure Management Client Library for Java](https://github.com/Azure/azure-sdk-for-java/tree/azure-resourcemanager_2.31.0/sdk/resourcemanager)
* [Azure Identity Client Library for Java](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/identity/azure-identity)
Note the README for the management library includes details on authentication, along with some [additional authentication instructions]([https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/identity/azure-identity](https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/resourcemanager/docs/AUTH.md)https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/resourcemanager/docs/AUTH.md)

## Example Body for HTTP Request
```
{
  "functionAppID": "<full function app id from JSON view of function details in Azure Portal>",
  "functionName": "HttpExample",
  "disabled": "1"
}
```

## Notes
This solution took main inspiration from Ben Sagi's answer on [this stackoverflow post](https://stackoverflow.com/questions/74948485/disable-azure-function-from-java-sdk#:~:text=found%20the%20following%20solution.%20the%20function%20enable%2Fdisable%20property,the%20function%20app%3A%20functionApp.update%20%28%29.withAppSetting%20%28%22AzureWebJobs.%3Cfunction_name%3E.Disabled%22%2C%20%221%22%29.apply%20%28%29%3B)
