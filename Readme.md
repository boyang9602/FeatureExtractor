# FeatureExtractor

Extract code features from source code files. Need to specify data root path for main. data sturcture please refer to [RefactoringDetector](https://github.com/boyang9602/RefactoringDetector)  
Currently it is a basic framework. In the RefInfoHandler's instance, you can get the source class content, which is the whole class's content in String.   
dependency management uses maven  

[Sample data](./EXTRACT_METHOD)  
The data tries to describe the method.  
1. separate var into parameter, local variable and field of the object.  PARAM(n), LOCALVAR(n), FIELD(n)
2. it records the parameter and created local variable and take others as field.  
3. it records the method call with the caller.  e.g. PARAM0.METHOD0 means the first parameter calls one of its methods. PARAM0.METHOD1 means the first parameter calls its another method. FIELD0.METHOD0 means the first recorded field calls one of its methods.  
4. it records the nested block statements. e.g. TRY.IF.SWITCH meas there is a `try` block where an `if` block insides it and a `switch` block insides the `if` block.  
5. it records the other behaviors: VAR.USE means a variable is used in the method. VAR.ASSIGN means a variable is assigned in the method. VAR.INSTOF mean a variable is checked type in the method. VAR.CAST means a variable is casted in the method. RETURN(n) means nth appearence of return statement.
6. ...others...
Some useless information like visibility, method name, etc., is ignored. The granularity is tunable.  
