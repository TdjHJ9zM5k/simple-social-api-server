
# Simple Social platform exercise

  

#### General Description

This is a Java Spring Boot app utilizing an embedded H2 Database that simulates a MySQL instance.<br><br>The app is serverlessly deployed on Google Cloud Run at:
 - [https://iakta-social-cj5d2qfngq-ey.a.run.app](https://iakta-social-cj5d2qfngq-ey.a.run.app)
<br><br>
<img src="docs/screenshots/cloud_build.png" alt="Trigger" width="600"/>
<img src="docs/screenshots/cloud_run.png" alt="Deployement" width="600"/>
<br><br>

The H2 database console can be access at the url:
<br>

 - [https://iakta-social-cj5d2qfngq-ey.a.run.app/h2-console](https://iakta-social-cj5d2qfngq-ey.a.run.app/h2-console)
 - **JDBC URL**: *jdbc:h2:file:./mydatabase*
 - **User Name**: *davide*
 - **Password**: *T4ScE3L5Tf58srdRp*
<br><img src="docs/screenshots/h2-console.png" alt="h2-console" width="400"/>

<br><br>A jar file containing the application for local testing is present under the *Releases* tab of this repo.<br><br><br>
A postman collection is present under the folder /postman<br><br><br>
A Swagger UI is also present at the url:<br>

-  [https://iakta-social-cj5d2qfngq-ey.a.run.app/swagger-ui/index.html](https://iakta-social-cj5d2qfngq-ey.a.run.app/swagger-ui/index.html)
