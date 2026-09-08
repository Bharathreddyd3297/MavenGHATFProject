# SOP: GitHub Actions OIDC Authentication with AWS

## 1. Document Information

| Item                             | Details                                                                     |
| -------------------------------- | --------------------------------------------------------------------------- |
| Purpose                          | Configure GitHub Actions to authenticate to AWS using OpenID Connect (OIDC) |
| GitHub Repository                | `Bharathreddyd3297/MavenGHATFProject`                                       |
| GitHub Branch                    | `main`                                                                      |
| AWS Region                       | `ap-south-1`                                                                |
| AWS Account                      | `229378727493`                                                              |
| OIDC Provider                    | `https://token.actions.githubusercontent.com`                               |
| OIDC Audience                    | `sts.amazonaws.com`                                                         |
| IAM Role                         | `GitHubLink`                                                                |
| Authentication Method            | GitHub OIDC                                                                 |
| AWS Credentials Stored in GitHub | No                                                                          |
| Test Command                     | `aws sts get-caller-identity`                                               |

---

# 2. Objective

Configure GitHub Actions so that the `main` branch of the following repository can authenticate to AWS without storing a permanent AWS access key or secret access key in GitHub:

```text
Bharathreddyd3297/MavenGHATFProject
```

The authentication flow is:

```text
GitHub Repository
       |
       | GitHub Actions workflow
       ↓
GitHub OIDC Provider
       |
       | OIDC token
       ↓
AWS STS
       |
       | AssumeRoleWithWebIdentity
       ↓
AWS IAM Role
       |
       | Temporary AWS credentials
       ↓
GitHub Actions Runner
       |
       ↓
AWS CLI / Terraform / AWS services
```

---

# 3. Prerequisites

Before starting:

* An AWS account must be available.
* Access to the AWS IAM Console is required.
* Access to the GitHub repository is required.
* The GitHub repository must be:

```text
Bharathreddyd3297/MavenGHATFProject
```

* The branch used for this SOP is:

```text
main
```

---

# 4. Configuration Values

Use the following values.

```text
GitHub Owner:
Bharathreddyd3297

GitHub Repository:
MavenGHATFProject

GitHub Branch:
main

OIDC Provider URL:
https://token.actions.githubusercontent.com

OIDC Audience:
sts.amazonaws.com

AWS Region:
ap-south-1

AWS Account ID:
229378727493

IAM Role:
GitHubLink
```

---

# 5. AWS Configuration

## 5.1 Open IAM

Sign in to the AWS Management Console.

Navigate to:

```text
AWS Console
    ↓
IAM
```

---

# 6. Create GitHub OIDC Identity Provider

Navigate to:

```text
IAM
 ↓
Identity providers
 ↓
Add provider
```

Select:

```text
Provider type:
OpenID Connect
```

---

## 6.1 Provider URL

Enter:

```text
https://token.actions.githubusercontent.com
```

---

## 6.2 Audience

Enter:

```text
sts.amazonaws.com
```

The final configuration should be:

```text
Provider type:
OpenID Connect

Provider URL:
https://token.actions.githubusercontent.com

Audience:
sts.amazonaws.com
```

Click:

```text
Add provider
```

---

# 7. Verify OIDC Provider

Navigate to:

```text
IAM
 ↓
Identity providers
```

Verify that the following provider exists:

```text
token.actions.githubusercontent.com
```

The provider must use:

```text
https://token.actions.githubusercontent.com
```

and the audience:

```text
sts.amazonaws.com
```

---

# 8. Create IAM Role

Navigate to:

```text
IAM
 ↓
Roles
 ↓
Create role
```

Under **Trusted entity type**, select:

```text
Web identity
```

---

# 9. Configure Web Identity

For the identity provider, select:

```text
token.actions.githubusercontent.com
```

For the audience, use:

```text
sts.amazonaws.com
```

Configure the GitHub repository information for:

```text
Organization/User:
Bharathreddyd3297

Repository:
MavenGHATFProject

Branch:
main
```

Continue to the permissions section.

---

# 10. Attach IAM Permissions

Attach:

```text
AdministratorAccess
```

The role will therefore have administrator permissions within the AWS account.

Continue to the role naming section.

---

# 11. Role Name

The IAM role used in this configuration is:

```text
GitHubLink
```

Create the role.

After creation, navigate to:

```text
IAM
 ↓
Roles
 ↓
GitHubLink
```

---

# 12. Configure the Trust Relationship

Open:

```text
GitHubLink
 ↓
Trust relationships
 ↓
Edit trust policy
```

The trust relationship must allow GitHub's OIDC provider to perform:

```text
sts:AssumeRoleWithWebIdentity
```

The repository-specific trust relationship is based on the GitHub OIDC `sub` claim.

The OIDC token generated for this repository produced the following subject:

```text
repo:Bharathreddyd3297@217679465/MavenGHATFProject@1360496808:ref:refs/heads/main
```

Use the following trust policy:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::229378727493:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com",
          "token.actions.githubusercontent.com:sub": "repo:Bharathreddyd3297@217679465/MavenGHATFProject@1360496808:ref:refs/heads/main"
        }
      }
    }
  ]
}
```

Save the trust policy.

---

# 13. Trust Policy Explanation

The policy contains four important components.

## 13.1 Federated Principal

```json
"Principal": {
  "Federated": "arn:aws:iam::229378727493:oidc-provider/token.actions.githubusercontent.com"
}
```

This establishes GitHub's OIDC provider as the trusted identity provider.

---

## 13.2 Assume Role Action

```json
"Action": "sts:AssumeRoleWithWebIdentity"
```

This allows the GitHub OIDC identity to request an AWS role session through AWS STS.

---

## 13.3 Audience

```json
"token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
```

The GitHub OIDC token must have:

```text
aud = sts.amazonaws.com
```

---

## 13.4 Subject

```json
"token.actions.githubusercontent.com:sub": "repo:Bharathreddyd3297@217679465/MavenGHATFProject@1360496808:ref:refs/heads/main"
```

This restricts the role to the specified repository and branch.

The workflow must therefore originate from:

```text
Bharathreddyd3297/MavenGHATFProject
```

and:

```text
main
```

---

# 14. Copy the IAM Role ARN

Inside:

```text
IAM
 ↓
Roles
 ↓
GitHubLink
```

copy the Role ARN.

It will have the following format:

```text
arn:aws:iam::229378727493:role/GitHubLink
```

---

# 15. Configure GitHub Repository Secret

Open the GitHub repository:

```text
Bharathreddyd3297/MavenGHATFProject
```

Navigate to:

```text
Settings
 ↓
Secrets and variables
 ↓
Actions
```

Under **Repository secrets**, select:

```text
New repository secret
```

Create:

```text
Name:
AWS_ROLE_ARN
```

For the value, enter the IAM role ARN:

```text
arn:aws:iam::229378727493:role/GitHubLink
```

Save the secret.

The GitHub repository should now contain:

```text
Repository secrets
└── AWS_ROLE_ARN
```

No AWS access key or AWS secret access key is required.

---

# 16. GitHub Actions Permissions

The workflow must contain:

```yaml
permissions:
  id-token: write
  contents: read
```

The important permission for OIDC is:

```yaml
id-token: write
```

This allows GitHub Actions to request an OIDC token.

---

# 17. Initial OIDC Debugging Test

If authentication fails, the GitHub OIDC token claims can be inspected.

Create a temporary workflow:

```text
.github/workflows/aws-oidc-debug.yml
```

Use:

```yaml
name: AWS OIDC Authentication Test

on:
  workflow_dispatch:

permissions:
  id-token: write
  contents: read

jobs:
  aws-authentication:
    runs-on: ubuntu-latest

    steps:
      - name: Get OIDC token
        id: oidc
        uses: actions/github-script@v7
        with:
          script: |
            const token = await core.getIDToken('sts.amazonaws.com');
            core.setOutput('token', token);

      - name: Decode OIDC token
        env:
          OIDC_TOKEN: ${{ steps.oidc.outputs.token }}
        run: |
          python - <<'PY'
          import os
          import base64
          import json

          token = os.environ["OIDC_TOKEN"]

          header, payload, signature = token.split(".")

          payload += "=" * (-len(payload) % 4)

          decoded = base64.urlsafe_b64decode(payload)

          claims = json.loads(decoded)

          print(json.dumps({
              "iss": claims.get("iss"),
              "aud": claims.get("aud"),
              "sub": claims.get("sub"),
              "repository": claims.get("repository"),
              "repository_owner": claims.get("repository_owner"),
              "ref": claims.get("ref"),
              "event_name": claims.get("event_name")
          }, indent=2))
          PY
```

Run the workflow from:

```text
main
```

---

# 18. Verify the OIDC Claims

The debug workflow should display claims similar to:

```json
{
  "iss": "https://token.actions.githubusercontent.com",
  "aud": "sts.amazonaws.com",
  "sub": "repo:Bharathreddyd3297@217679465/MavenGHATFProject@1360496808:ref:refs/heads/main",
  "repository": "Bharathreddyd3297/MavenGHATFProject",
  "repository_owner": "Bharathreddyd3297",
  "ref": "refs/heads/main",
  "event_name": "workflow_dispatch"
}
```

The important claim for the AWS trust policy is:

```text
sub
```

For this repository, the value is:

```text
repo:Bharathreddyd3297@217679465/MavenGHATFProject@1360496808:ref:refs/heads/main
```

This value is used in the AWS trust relationship.

---

# 19. Final AWS OIDC Authentication Test

After the OIDC configuration has been verified, use the following workflow to test AWS authentication.

Create:

```text
.github/workflows/aws-oidc-test.yml
```

Use:

```yaml
name: AWS OIDC Authentication Test

on:
  workflow_dispatch:

permissions:
  id-token: write
  contents: read

jobs:
  aws-authentication:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v6
        with:
          role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
          role-session-name: GitHubLink
          aws-region: ap-south-1

      - name: Verify AWS authentication
        run: aws sts get-caller-identity
```

---

# 20. Workflow Execution

Navigate to:

```text
GitHub Repository
 ↓
Actions
 ↓
AWS OIDC Authentication Test
 ↓
Run workflow
```

Select:

```text
main
```

Run the workflow.

---

# 21. Expected Authentication Flow

The workflow performs the following:

```text
GitHub Actions Runner
        |
        | Request OIDC token
        ↓
GitHub OIDC
        |
        | OIDC JWT
        ↓
AWS STS
        |
        | AssumeRoleWithWebIdentity
        ↓
IAM Role: GitHubLink
        |
        | Temporary credentials
        ↓
GitHub Actions Runner
        |
        ↓
aws sts get-caller-identity
```

---

# 22. Expected Successful Output

The `Configure AWS credentials` step should show:

```text
Assuming role with OIDC
```

followed by:

```text
Authenticated as assumedRoleId ...
```

The next step:

```text
aws sts get-caller-identity
```

should return information similar to:

```json
{
    "UserId": "AROATK2AJTZCUBR5TIVKO:GitHubLink",
    "Account": "229378727493",
    "Arn": "arn:aws:sts::229378727493:assumed-role/GitHubLink/GitHubLink"
}
```

The exact `UserId` and session information can vary between executions.

The important values are:

```text
Account:
229378727493
```

and:

```text
assumed-role/GitHubLink/GitHubLink
```

---

# 23. Authentication Validation

A successful:

```bash
aws sts get-caller-identity
```

confirms that:

```text
GitHub Actions
      ↓
GitHub OIDC
      ↓
AWS STS
      ↓
IAM Role
      ↓
Temporary AWS credentials
      ↓
AWS CLI
```

is working successfully.

At this point, GitHub Actions has authenticated to AWS.

---

# 24. Final Configuration Summary

### GitHub

```text
Repository:
Bharathreddyd3297/MavenGHATFProject

Branch:
main

Repository Secret:
AWS_ROLE_ARN
```

### AWS

```text
OIDC Provider:
https://token.actions.githubusercontent.com

Audience:
sts.amazonaws.com

IAM Role:
GitHubLink

AWS Account:
229378727493

AWS Region:
ap-south-1
```

### Trust relationship

```text
repo:Bharathreddyd3297@217679465/MavenGHATFProject@1360496808:ref:refs/heads/main
```

### GitHub Actions permissions

```yaml
permissions:
  id-token: write
  contents: read
```

### AWS authentication action

```yaml
uses: aws-actions/configure-aws-credentials@v6
```

### Authentication verification

```bash
aws sts get-caller-identity
```

---

# 25. Authentication Architecture

The completed setup is:

```text
                         GitHub
                            |
                            |
              MavenGHATFProject / main
                            |
                            ↓
                    GitHub Actions
                            |
                    id-token: write
                            |
                            ↓
                 GitHub OIDC Provider
                            |
                    OIDC JWT Token
                            |
                            ↓
                        AWS STS
                            |
             AssumeRoleWithWebIdentity
                            |
                            ↓
                     IAM Role: GitHubLink
                            |
                     AdministratorAccess
                            |
                            ↓
                       AWS Account
                      229378727493
                            |
                   Temporary Credentials
                            |
                            ↓
                 GitHub Actions Runner
                            |
                 ┌──────────┴──────────┐
                 ↓                     ↓
             AWS CLI               Terraform
```

---

# 26. Completion Criteria

The OIDC configuration is considered successfully completed when all of the following are true:

* [x] GitHub OIDC provider exists in AWS IAM.
* [x] Provider URL is `https://token.actions.githubusercontent.com`.
* [x] Audience is `sts.amazonaws.com`.
* [x] IAM role `GitHubLink` exists.
* [x] IAM role has `AdministratorAccess`.
* [x] IAM trust policy allows `sts:AssumeRoleWithWebIdentity`.
* [x] Trust policy contains the repository-specific OIDC `sub`.
* [x] GitHub repository contains the `AWS_ROLE_ARN` secret.
* [x] GitHub workflow has `id-token: write`.
* [x] `aws-actions/configure-aws-credentials@v6` successfully assumes the role.
* [x] `aws sts get-caller-identity` successfully returns AWS account information.
* [x] The returned ARN contains `assumed-role/GitHubLink`.

**OIDC authentication between GitHub Actions and AWS is complete.**

The authenticated GitHub Actions runner can now be used by subsequent workflow steps to execute AWS CLI commands and Terraform operations.
