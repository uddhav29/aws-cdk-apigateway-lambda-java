package com.uap.cdk;

import java.util.Arrays;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.ApiKeyOptions;
import software.amazon.awscdk.services.apigateway.Deployment;
import software.amazon.awscdk.services.apigateway.DeploymentProps;
import software.amazon.awscdk.services.apigateway.IApiKey;
import software.amazon.awscdk.services.apigateway.IModel;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.Integration;
import software.amazon.awscdk.services.apigateway.JsonSchema;
import software.amazon.awscdk.services.apigateway.JsonSchemaType;
import software.amazon.awscdk.services.apigateway.JsonSchemaVersion;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.ModelOptions;
import software.amazon.awscdk.services.apigateway.Period;
import software.amazon.awscdk.services.apigateway.QuotaSettings;
import software.amazon.awscdk.services.apigateway.RequestValidatorOptions;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.apigateway.RestApiProps;
import software.amazon.awscdk.services.apigateway.Stage;
import software.amazon.awscdk.services.apigateway.StageProps;
import software.amazon.awscdk.services.apigateway.ThrottleSettings;
import software.amazon.awscdk.services.apigateway.UsagePlanPerApiStage;
import software.amazon.awscdk.services.apigateway.UsagePlanProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

public class CdkStack extends Stack {

	private final String modelName = "ProductModel";
	private final String apiKey = ""; // add api key here

	public CdkStack(final Construct parent, final String id) {
		this(parent, id, null);
	}

	public CdkStack(final Construct parent, final String id, final StackProps props) {
		super(parent, id, props);

		final Function function =  Function.Builder.create(this, "function").functionName("Lambda-Test").
				runtime(Runtime.PYTHON_3_8).
				code(Code.fromAsset("lambda")).
				handler("event.handler").build();

		RestApi api = new RestApi(this, "productApi",
				RestApiProps.builder().restApiName("Product Service").build());
		
		Deployment deployment = new Deployment(this, "deploy", DeploymentProps.builder().api(api).build());
		Stage stage = new Stage(this, "QA", StageProps.builder().stageName("QA"). deployment(deployment).build());
		api.setDeploymentStage(stage);
	
		IResource items = api.getRoot().addResource("product");

		ModelOptions model = ModelOptions.builder().contentType("application/json").
				description("requestBody"). modelName(modelName).schema(
						JsonSchema.builder().schema(JsonSchemaVersion.DRAFT4).
						title("product").
						//properties(properties)
						type(JsonSchemaType.OBJECT).
						required(Arrays.asList("product")).
						build()).build();

		api.addModel(modelName, model);

		IApiKey key = api.addApiKey("apiKey", ApiKeyOptions.builder().apiKeyName("apiKey").value(apiKey).build());
		api.addUsagePlan("usagePlan", UsagePlanProps.builder().apiStages(Arrays.asList(UsagePlanPerApiStage.builder().stage(stage).build())).apiKey(key).quota(QuotaSettings.builder().limit(20).period(Period.WEEK).build()).throttle(ThrottleSettings.builder().burstLimit(5).rateLimit(5).build()).build());
		Integration integration = new LambdaIntegration(function);
		MethodOptions options = MethodOptions.builder().requestModels(Map.of("application/json", new IModel() {

			@Override
			public @NotNull String getModelId() {
				return modelName;
			}

		})).apiKeyRequired(true).requestValidatorOptions(RequestValidatorOptions.builder().requestValidatorName("payload validator").validateRequestBody(true).build()).build();

		items.addMethod("POST", integration, options );
	}
}
