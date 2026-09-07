variable "aws_region" {
  description = "AWS region where resources will be created"
  type        = string
}

variable "application_name" {
  description = "Elastic Beanstalk application name"
  type        = string
}

variable "environment_name" {
  description = "Elastic Beanstalk environment name"
  type        = string
}

variable "instance_type" {
  description = "EC2 instance type used by the Elastic Beanstalk environment"
  type        = string
  default     = "t3.micro"
}
