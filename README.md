# Welcome to Java CDK project for defining cloud infrastructure in code!

This project creates a REST endpoint(POST method) using API Gateway. Validates Requestbody and integrates it with a lambda function. 

 * `event.py`    	  Lambda function code
 * `CdkStack.java`    Java code for creating AWS resources(Lambda function, API Gateway REST endpoint)

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!
