variable "STAGE" {
    type    = string
    default = "local"
}

variable "AWS_REGION" {
    type    = string
    default = "us-east-1"
}

variable "JAR_PATH" {
    type    = string
    default = "../../build/libs/localstack-with-spring-cloud-function-all.jar"
}

variable "LAMBDA_MOUNT_CWD" {
    type    = string
}

provider "aws" {
    access_key                  = "test_access_key"
    secret_key                  = "test_secret_key"
    region                      = var.AWS_REGION
    s3_force_path_style         = true
    skip_credentials_validation = true
    skip_metadata_api_check     = true
    skip_requesting_account_id  = true

    endpoints {
        apigateway       = var.STAGE == "local" ? "http://localhost:4566" : null
        cloudformation   = var.STAGE == "local" ? "http://localhost:4566" : null
        cloudwatch       = var.STAGE == "local" ? "http://localhost:4566" : null
        cloudwatchevents = var.STAGE == "local" ? "http://localhost:4566" : null
        iam              = var.STAGE == "local" ? "http://localhost:4566" : null
        lambda           = var.STAGE == "local" ? "http://localhost:4566" : null
        s3               = var.STAGE == "local" ? "http://localhost:4566" : null
    }
}

resource "aws_iam_role" "lambda-execution-role" {
    name = "lambda-execution-role"

    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_lambda_function" "restApiLambdaFunction" {
    filename      = var.JAR_PATH
    function_name = "RestApiFunction"
    role          = aws_iam_role.lambda-execution-role.arn
    handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker"
    runtime       = "java11"
    timeout       = 30
    source_code_hash = filebase64sha256(var.JAR_PATH)
}

resource "aws_api_gateway_rest_api" "rest-api" {
    name = "ExampleRestApi"
}

resource "aws_api_gateway_resource" "proxy" {
    rest_api_id = aws_api_gateway_rest_api.rest-api.id
    parent_id   = aws_api_gateway_rest_api.rest-api.root_resource_id
    path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
    rest_api_id   = aws_api_gateway_rest_api.rest-api.id
    resource_id   = aws_api_gateway_resource.proxy.id
    http_method   = "ANY"
    authorization = "NONE"
}

resource "aws_api_gateway_integration" "proxy" {
    rest_api_id = aws_api_gateway_rest_api.rest-api.id
    resource_id = aws_api_gateway_method.proxy.resource_id
    http_method = aws_api_gateway_method.proxy.http_method

    integration_http_method = "POST"
    type                    = "AWS_PROXY"
    uri                     = aws_lambda_function.restApiLambdaFunction.invoke_arn
}

resource "aws_api_gateway_deployment" "rest-api-deployment" {
    depends_on = [aws_api_gateway_integration.proxy]
    rest_api_id = aws_api_gateway_rest_api.rest-api.id
    stage_name  = var.STAGE
}

resource "aws_cloudwatch_event_rule" "warmup" {
    name = "warmup-event-rule"
    schedule_expression = "rate(10 minutes)"
}

resource "aws_cloudwatch_event_target" "warmup" {
    target_id = "warmup"
    rule = aws_cloudwatch_event_rule.warmup.name
    arn = aws_lambda_function.restApiLambdaFunction.arn
    input = "{\"httpMethod\": \"SCHEDULE\", \"path\": \"warmup\"}"
}

resource "aws_lambda_permission" "warmup-permission" {
    statement_id = "AllowExecutionFromCloudWatch"
    action = "lambda:InvokeFunction"
    function_name = aws_lambda_function.restApiLambdaFunction.function_name
    principal = "events.amazonaws.com"
    source_arn = aws_cloudwatch_event_rule.warmup.arn
}

resource "aws_lambda_function" "exampleFunctionOne" {
    filename      = var.JAR_PATH
    function_name = "ExampleFunctionOne"
    role          = aws_iam_role.lambda-execution-role.arn
    handler       = "org.localstack.sampleproject.api.LambdaApi"
    runtime       = "java11"
    timeout       = 30
    source_code_hash = filebase64sha256(var.JAR_PATH)
    environment {
        variables = {
            FUNCTION_NAME = "functionOne"
        }
    }
}

resource "aws_lambda_function" "exampleFunctionTwo" {
    filename      = var.JAR_PATH
    function_name = "ExampleFunctionTwo"
    role          = aws_iam_role.lambda-execution-role.arn
    handler       = "org.localstack.sampleproject.api.LambdaApi"
    runtime       = "java11"
    timeout       = 30
    source_code_hash = filebase64sha256(var.JAR_PATH)
    environment {
        variables = {
            FUNCTION_NAME = "functionTwo"
        }
    }
}