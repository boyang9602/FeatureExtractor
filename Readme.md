# FeatureExtractor

Extract code features from source code files. Need to specify data root path for main. data sturcture please refer to [RefactoringDetector](https://github.com/boyang9602/RefactoringDetector)  
Currently it is a basic framework. In the RefInfoHandler's instance, you can get the source class content, which is the whole class's content in String.   
dependency management uses maven

Some useless information like visibility, method name, etc., is ignored. The granularity is tunable.  
[Sample data](./EXTRACT_METHOD)
