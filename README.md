
# A simple API server

  

## General Description

The solution consists of a Java Spring Boot app and an embedded H2 database that acts as a MySQL instance.<br>
Authentication is handled via JWT.<br><br>The app is serverlessly deployed (with continuous deployment) on Google Cloud Run at:
 - https://iakta-social-cj5d2qfngq-ew.a.run.app
<br><br>
<img src="docs/screenshots/cloud_build.png" alt="Trigger" width="600"/>
<img src="docs/screenshots/cloud_run.png" alt="Deployement" width="600"/>
<br><br>

You can access the H2 database console at the URL:
<br>

 - <a href="https://iakta-social-cj5d2qfngq-ew.a.run.app/h2-console" target="_blank">https://iakta-social-cj5d2qfngq-ew.a.run.app/h2-console</a>
 - **JDBC URL**: *jdbc:h2:file:./mydatabase*
 - **User Name**: *davide*
 - **Password**: *T4ScE3L5Tf58srdRp*
<br><br><img src="docs/screenshots/h2-console.png" alt="h2-console" width="400"/>

<br><br>A <a href="https://github.com/davideatzori294/iakta-social/releases/tag/v0.0.2" target="_blank">.jar file</a> containing the application is present under the <i>Releases</i> tab of this repo for local testing.<br><br><br>
Postman collections are present under the folder <a href="https://github.com/davideatzori294/iakta-social/tree/main/postman" target="_blank">/postman</a><br><br><br>
You can also find a Swagger UI at the following url:<br>

- <a href="https://iakta-social-cj5d2qfngq-ew.a.run.app/swagger-ui/index.html" target="_blank">https://iakta-social-cj5d2qfngq-ew.a.run.app/swagger-ui/index.html</a>