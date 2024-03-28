# Drone project for Musala Soft

## Pre-requisites

The following resources need to be installed to deploy and run the application

- MySQL 
- Java 17 
- Maven

## Setup Application 


#### Step 01
1. Open your MySQL client or command line interface
2. Execute the following SQL command to create the schema named "MUSALA" <br />
`CREATE SCHEMA MUSALA;`
3. Clone the repository from GitHub using the following command: <br >
`git clone https://github.com/rajinda1980/DronesDataModel.git`
4. Once you've cloned the repository, navigate to the root directory of the project where the pom.xml file is located
5. Run the following Maven command to clean the project and install dependencies <br />
`mvn clean install`
6. Once the build is successful, navigate to the target directory of your project and verify jar file is created. Jar file name should be<br />
`DronesDataModel-1.0-SNAPSHOT.jar`

#### Step 02
1. Clone the repository from GitHub using the following command: <br >
`git clone https://github.com/rajinda1980/Drones.git`
2. Once you've cloned the repository, navigate to the root directory of the project where the pom.xml file is located
3. Run the following Maven command to clean the project and install dependencies <br />
`mvn clean install`
4. Once the build is successful, Navigate to the target directory of the project <br >
`cd target`
5. Please verify that the following JAR file has been created in target folder <br />
`Drones-1.0-SNAPSHOT.jar` <br >
<b>Note: All the test cases should pass, and the following entry should be visible in the console <br /> </b>
   `Results: `<br />
   `[INFO]` <br />
   `[INFO] Tests run: 97, Failures: 0, Errors: 0, Skipped: 0`
6. Run the JAR file using the java -jar command <br />
`java -jar Drones-1.0-SNAPSHOT.jar`
7. Please verify the application is up and running. The console should display an entry similar to the following <br />
`INFO 829970 --- [    main] com.musala.drones.DronesApp   : Started DronesApp in 4.945 seconds (process running for 5.356)`
8. Navigate to the Swagger UI endpoint in your web browser using the following URL <br />
`http://localhost:9090/swagger-ui/index.html`

### Assumptions
1. Database username and password are both set to "root". If these credentials differ, 
please update the application.properties file accordingly in the Drone application before run
Step 02 -> 3 line. <br >
`File: Drones/src/main/resources/application.properties` <br >
`To update username : datasource.mysql.username=root` <br />
`To update password : datasource.mysql.password=root` <br />
2. You can test the application either by using Postman or by accessing the Swagger UI <br />
   `Swagger URL : http://localhost:9090/swagger-ui/index.html`
3. Run the following command if you want to view the test coverage on SonarQube <br />
`mvn clean verify sonar:sonar -Dsonar.projectKey=<Project> -Dsonar.projectName='<Project>' -Dsonar.host.url=http://<sonarqube hostname:port> -Dsonar.token=<Project token>` <br />
`Ex : mvn clean verify sonar:sonar -Dsonar.projectKey=Drone -Dsonar.projectName='Drone' -Dsonar.host.url=http://localhost:9000 -Dsonar.token=squ_0478d3e297e9b0d93063502688fb882626b8fb92` <br />
<b>Note: SonarQube must be up and running. In this example, "Drone" is the project created for this application </b> 

### Statistics:
1. All functionalities are covered by the test cases with a test coverage of 88%.
2. Database-related configurations can be found in the README file of the DronesDataModel module
