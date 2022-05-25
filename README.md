# REST API Service Project

### Used Technologies & Tools:
<p align="left">
<img height="40" width="40" src="images/java-logo.svg" alt="java">
<img height="40" width="40" src="images/junit5-logo.svg" alt="junit5">
<img height="40" width="40" src="images/rest-assured-logo.png" alt="rest-assured">
<img height="40" width="40" src="images/gradle-logo.svg" alt="gradle">
<img height="40" width="40" src="images/IDEA-logo.svg" alt="IDEA">
<img height="40" width="40" src="images/git-logo.svg" alt="git">
<img height="40" width="40" src="images/swagger-logo.png" alt="swagger">
<img height="40" width="40" src="images/allure-Report-logo.svg" alt="allure">
</p>

### Swagger as a REST API Service Self-Documentation:
Swagger is available on by link http://localhost:9090/swagger-ui/ after Service is started.
![image](images/swagger-view.png)

### How To Run Service:
The repository should be cloned and Service should be started. Service runs locally on 9090 port.
![image](images/launch-service.png)

### List of the Automated Tests:
- [X] Get all books
- [X] Get book by existing ISBN
- [X] Get book by existing ISBN
- [X] Get book by not existing ISBN
- [X] Add new book
- [X] Get all authors
- [X] Get book by existing author
- [X] Get book by not existing author
- [X] Add new author

### How To Run Tests </br>
```bash
gradle clean test apiTests
```

### Allure Report For Test Results Presentation
#### How to Run Allure Report
After tests are executed run the following command at Terminal
```bash
gradle allureServe
```
or run report using GUI
![image](images/allure-serve.png)

#### Allure Report Results
![image](images/allure-report-overview.png)
![image](images/allure-report-behavior.png)


:heart: <a target="_blank" href="https://qa.guru">qa.guru</a><br/>
:blue_heart: <a target="_blank" href="https://t.me/qa_automation">t.me/qa_automation</a>