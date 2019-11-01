# FeatureExtractor

Extract code features from source code files. Need to specify data root path for main. data sturcture please refer to [RefactoringDetector](https://github.com/boyang9602/RefactoringDetector)  
In the RefInfoHandler's instance, you can get the source class content, which is the whole class's content in String.   
Dependency management uses maven  

[data](./data)  
Here is a simple example:  
```java
public static String setupZookeeperAuth(Configuration conf, String saslLoginContextName, String zkPrincipal, String zkKeytab) throws IOException {
    // If the login context name is not set, we are in the client and don't need auth.
    if (UserGroupInformation.isSecurityEnabled() && saslLoginContextName != null) {
        LOG.info("UGI security is enabled. Setting up ZK auth.");
        if (zkPrincipal == null || zkPrincipal.isEmpty()) {
            throw new IOException("Kerberos principal is empty");
        }
        if (zkKeytab == null || zkKeytab.isEmpty()) {
            throw new IOException("Kerberos keytab is empty");
        }
        // Install the JAAS Configuration for the runtime
        return setZookeeperClientKerberosJaasConfig(saslLoginContextName, zkPrincipal, zkKeytab);
    } else {
        LOG.info("UGI security is not enabled, or no SASL context name. " + "Skipping setting up ZK auth.");
        return null;
    }
}
```
Abstraction:  
[PARAM0, PARAM1, PARAM2, PARAM3, IF, FIELD0.METHOD0, CONST0.USE, FIELD1.METHOD0, CONST1.USE, IF.IF, CONST0.USE, PARAM2.METHOD0, THROW, CONST2.USE, IF.IF, CONST0.USE, PARAM3.METHOD0, THROW, CONST3.USE, RETURN0, THIS.METHOD0, FIELD1.METHOD0, CONST4.USE, CONST5.USE, RETURN1, CONST0.USE]  
`PARAM[0~3]` means three params are passed in.  Then there's an `IF` statement. Then a `FIELD` calls one of its `METHOD`s (`UserGroupInformation.isSecurityEnabled()`, we don't differentiate a class call it's static method or a field call its instance method). A `CONST`(`null`) is used. Another `FIELD` calls one of its `METHOD`s. Another `CONST`(`"UGI security is enabled. Setting up ZK auth."`) is used. Then `IF.IF` means there's a nested `IF` statement. The `CONST`(null) used previous is used again. Another `PARAM` calls one of its `METHOD` ... There is a `THROW` statement. ... A `RETURN` statement (`return setZookeeperClientKerberosJaasConfig(saslLoginContextName, zkPrincipal, zkKeytab);`). ... A `THIS`.`METHOD` is called (`setZookeeperClientKerberosJaasConfig(saslLoginContextName, zkPrincipal, zkKeytab);`) ... Another `RETURN` statement and the first appearred `CONST`(`null`) appears again.  

The grandularity is tunable easily. For example, you may only consider about the `variable`'s source but don't care about which one is used, then you can remove the `id` number of the `variable` simply. Also, it's very easy to record whether a `variable` is inside a (nested) block statement like `IF` or `TRY`, etc. If only nested structure is important but what type of the block is not important, it's also easy to turn the `key` word into a general one (e.g. `BLOCK.BLOCK.BLOCK`). And other things the combination of the different source of `variable`, the parameter of the `METHOD` call, etc. are also easy to be recorded.  

### TODOs:
1. Cannot find the method information before extract variable refactoring.