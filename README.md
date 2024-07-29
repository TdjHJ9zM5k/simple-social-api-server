# Simple Social Network API Server

## General Description

This is a week-long project for a Simple Social Network API Server. The features are authentication, user following, posting, and commenting functionalities.

- **Technology Stack**: Java Spring Boot
- **Database**: Embedded H2 (acting as a MySQL instance)
- **Authentication**: JWT
- **Deployment**: Serverless, with Continuous Deployment pipeline on Google Cloud Run

The frontend repository can be found at:
[Simple Social Network Web App](https://github.com/TdjHJ9zM5k/simple-social-api-web)

You can access the live deployment at:
[https://social-network-backend-cpkhh4qfda-ew.a.run.app](https://social-network-backend-cpkhh4qfda-ew.a.run.app)



## Screenshots

### Cloud Build Trigger
<img src="docs/screenshots/cloud_build.png" alt="Trigger" width="600"/>

### Deployment on Cloud Run
<img src="docs/screenshots/cloud_run.png" alt="Deployment" width="600"/>

## H2 Database Console

You can access the H2 database console at:
[https://social-network-backend-cpkhh4qfda-ew.a.run.app/h2-console](https://social-network-backend-cpkhh4qfda-ew.a.run.app/h2-console)

- **JDBC URL**: `jdbc:h2:file:./mydatabase`
- **User Name**: `davide`
- **Password**: `T4ScE3L5Tf58srdRp`

<img src="docs/screenshots/h2-console.png" alt="H2 Console" width="400"/>

## Local Testing

A [`.jar file`](https://github.com/TdjHJ9zM5k/simple-social-api-server/releases/latest) containing the application is available under the Releases tab of this repository for local testing.

## Swagger UI

Explore the API with Swagger UI at:
[https://social-network-backend-cpkhh4qfda-ew.a.run.app/swagger-ui/index.html](https://social-network-backend-cpkhh4qfda-ew.a.run.app/swagger-ui/index.html)
