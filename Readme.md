# FeatureExtractor

### WHAT'S NEW?
Due to the millions of small file, used [seaweed](https://github.com/chrislusf/seaweedfs). Both file system and seaweed filer are supported.

Extract code features from source code files. Need to specify data root path for `main` method. data folder sturcture please refer to [RefactoringDetector](https://github.com/boyang9602/RefactoringDetector)  

The extracted features are under the [data](./data) folder in this repo  
#### In the abstraction, we
1. record the different sources of `variable` which are `PARAM`, `LOCALVAR`, `FIELD`.  
2. differentiate the different `variable` and `method` by an id number. e.g. `PARAM0`, `METHOD1`, etc.  
3. record the method call with the caller. e.g. `PARAM0.METHOD0`, `THIS.METHOD0`.  
4. record the nested block. e.g. `TRY.IF.FOR`  
5. record the usage of variable. e.g. `FIELD0.USE`  
6. record the const's use, which are `null`, any kinds of `number` and `String`.  
7. record the different `return` point with an id number.  
8. record the variable's assignment.  
9. record the `override` behavior in the `anonymous` class.  
10. record other special things， which are `super` statement, `yield` statement, local class defination statement, `lambda` expression, `instanceof`'s usage, variable cast's usage.  

#### A simple example:  
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
`[PARAM0, PARAM1, PARAM2, PARAM3, IF, FIELD0.METHOD0, CONST0.USE, FIELD1.METHOD0, CONST1.USE, IF.IF, CONST0.USE, PARAM2.METHOD0, THROW, CONST2.USE, IF.IF, CONST0.USE, PARAM3.METHOD0, THROW, CONST3.USE, RETURN0, THIS.METHOD0, FIELD1.METHOD0, CONST4.USE, CONST5.USE, RETURN1, CONST0.USE]`  
`PARAM[0~3]` means 4 params are passed in.  Then there's an `IF` statement. Then a `FIELD` calls one of its `METHOD`s (`UserGroupInformation.isSecurityEnabled()`, we don't differentiate *"a class call it's static method"* and *"a field call its instance method"*). A `CONST`(`null`) is used. Another `FIELD` calls one of its `METHOD`s. Another `CONST`(`"UGI security is enabled. Setting up ZK auth."`) is used. Then `IF.IF` means there's a nested `IF` statement. The `CONST`(null) used previous is used again. Another `PARAM` calls one of its `METHOD` ... There is a `THROW` statement. ... A `RETURN` statement (`return setZookeeperClientKerberosJaasConfig(saslLoginContextName, zkPrincipal, zkKeytab);`). ... A `THIS`.`METHOD` is called (`setZookeeperClientKerberosJaasConfig(saslLoginContextName, zkPrincipal, zkKeytab);`) ... Another `RETURN` statement and the first appearred `CONST`(`null`) appears again.  

#### Granularity is tunable  
The granularity is tunable by simply changing the code. For example, you may only consider about the `variable`'s source but don't care about which one is used, then you can remove the `id` number of the `variable` simply. Also, it's very easy to record whether a `variable` is inside a (nested) block statement like `IF` or `TRY`, etc. If only nested structure is important but what type of the block is not important, it's also easy to turn the `keyword` into a general one (e.g. `BLOCK.BLOCK.BLOCK`). And other things the combination of the different source of `variable`, the parameter of the `METHOD` call, etc. are also easy to be recorded.  
