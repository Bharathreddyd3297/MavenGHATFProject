# ---------------------------------------------------------------------------
# Solution stack (platform) lookup
# ---------------------------------------------------------------------------
# Our app is a single executable JAR (java -jar streamflix-1.0.0.jar), so we
# need the plain "Java SE" Elastic Beanstalk platform - NOT one of the
# "running Tomcat ..." stacks, which are for deploying .war files instead.
#
# Rather than hardcoding a specific platform version string (which AWS
# periodically retires), this data source asks AWS for the most recent
# Amazon Linux 2023 / Corretto 21 Java SE platform available right now.
data "aws_elastic_beanstalk_solution_stack" "java21" {
  most_recent = true
  name_regex  = "^64bit Amazon Linux 2023 .* running Corretto 21$"
}

# ---------------------------------------------------------------------------
# IAM for the EC2 instances Elastic Beanstalk launches
# ---------------------------------------------------------------------------
# Elastic Beanstalk
#         v
# EC2 instances
#         v
# IAM instance profile
#
# The EC2 instances in the environment need an instance profile so they can
# call other AWS services (e.g. to publish logs/metrics) without us ever
# placing AWS access keys on the instance itself.
data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "eb_ec2_role" {
  name               = "${var.application_name}-eb-ec2-role"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json
}

# AWS-managed policy with the minimum permissions a Java SE web tier
# environment's EC2 instances need.
resource "aws_iam_role_policy_attachment" "eb_ec2_web_tier" {
  role       = aws_iam_role.eb_ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier"
}

resource "aws_iam_instance_profile" "eb_ec2_profile" {
  name = "${var.application_name}-eb-ec2-profile"
  role = aws_iam_role.eb_ec2_role.name
}

# ---------------------------------------------------------------------------
# IAM for the Elastic Beanstalk service itself
# ---------------------------------------------------------------------------
# This role lets Elastic Beanstalk (not our EC2 instances) manage resources
# on our behalf, e.g. monitoring environment health.
data "aws_iam_policy_document" "eb_service_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["elasticbeanstalk.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "eb_service_role" {
  name               = "${var.application_name}-eb-service-role"
  assume_role_policy = data.aws_iam_policy_document.eb_service_assume_role.json
}

resource "aws_iam_role_policy_attachment" "eb_service_enhanced_health" {
  role       = aws_iam_role.eb_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkEnhancedHealth"
}

resource "aws_iam_role_policy_attachment" "eb_service_managed_updates" {
  role       = aws_iam_role.eb_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkManagedUpdatesCustomerRolePolicy"
}

# ---------------------------------------------------------------------------
# Elastic Beanstalk application + environment
# ---------------------------------------------------------------------------
resource "aws_elastic_beanstalk_application" "streamflix" {
  name        = var.application_name
  description = "StreamFlix - a simple Netflix-inspired movie catalog Java application (DevOps training project)"
}

# NOTE: no application version is created or referenced here on purpose.
# Terraform's job in this phase is infrastructure only. When an Elastic
# Beanstalk environment is created without a version_label, AWS deploys its
# own built-in "Sample Application" so the environment still comes up
# healthy. Building streamflix-1.0.0.jar and deploying it onto this
# infrastructure is the job of GitHub Actions in the next phase.
resource "aws_elastic_beanstalk_environment" "streamflix" {
  name                = var.environment_name
  application         = aws_elastic_beanstalk_application.streamflix.name
  solution_stack_name = data.aws_elastic_beanstalk_solution_stack.java21.name

  # Single instance, no load balancer - the cheapest/simplest environment
  # type, and enough for a training project. Elastic Beanstalk still manages
  # everything (the underlying EC2 instance, restarts, etc.) automatically -
  # we are not manually creating an Auto Scaling group or load balancer.
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "EnvironmentType"
    value     = "SingleInstance"
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "ServiceRole"
    value     = aws_iam_role.eb_service_role.name
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = aws_iam_instance_profile.eb_ec2_profile.name
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = var.instance_type
  }

  # Application listens on port 8080
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "PORT"
    value     = "8080"
  }

  # No VPC/subnet settings here - Elastic Beanstalk automatically uses the
  # AWS account's default VPC and default subnets for the region when none
  # are specified, which is exactly what this training project wants.
}

# ---------------------------------------------------------------------------
# Outputs
# ---------------------------------------------------------------------------
output "elastic_beanstalk_environment_url" {
  description = "URL of the StreamFlix Elastic Beanstalk environment"
  value       = "http://${aws_elastic_beanstalk_environment.streamflix.cname}"
}

output "elastic_beanstalk_application_name" {
  description = "Elastic Beanstalk application name"
  value       = aws_elastic_beanstalk_application.streamflix.name
}

output "elastic_beanstalk_environment_name" {
  description = "Elastic Beanstalk environment name"
  value       = aws_elastic_beanstalk_environment.streamflix.name
}
