## User Management System

This project is used to create, get, update, and delete user details in MySQL database using Scala programming Language.


### Pre-requisites

* Java 11
* Scala 2.13.5
* sbt 1.4.7
* docker 19.03.9
* docker-compose 1.25.0

## Commands

### docker

This command set up MySQL server on system using docker file.
Enter the project folder and run the  command:
````
docker-compose up

MySQL details
-------------
user: root
password: root
schema: user
port: 3306
````

### clean

This command cleans the sbt project by deleting the target directory. The command output relevant messages.
````
sbt clean
````

### compile

This command compiles the scala source classes of the sbt project.
````
sbt compile
````
### run

Enter the project folder and type project module name along with the sbt run command:
````
sbt "project crud" run
````
More details about project (e.g. version etc..) can be found in file build.sbt


### Tests

Code is developed by applying [TDD](https://en.wikipedia.org/wiki/Test-driven_development) and tests are located in
folder **/src/test/scala-2.13**,  For running all tests enter the project folder and type:

 ```
 sbt test
 ```

### Coverage

scoverage plugin is used in the code for checking code coverage. Code coverage is 100%


 ```
 sbt "project crud" clean coverage test coverageReport
 ```

More details about project libraraies (e.g. version etc..) can be found in files:
**build.sbt**
**Dependencies**
**CommonSettings**
**plugins.sbt**


## Routes

######Note: postman json file is added in the directory. 

### create user

This route stores user details in the database

````
route(PUT): http://localhost:8003/user/insert-user

data-format: body -> raw (JSON)

{
    "userType": "user-type",
    "name": "name",
    "username": "user-name"
}
````

### list users

This route displays all the users from the database


````
route (GET): http://localhost:8003/user/get-all-users
````

### update admin username

This route updates the username of admin in the database


````
route(PUT): http://localhost:8003/user/update-admin-username

data-format: body -> raw (JSON)

{
    "userId":"user-id",
    "username":"user-name"
}
````

### delete customer

This route deletes the customer details from the database

````
route(DELETE): http://localhost:8003/user/delete-customer?userId=user-id

data-format: Query Parameter (String)
````
A number of samples is given in test files in packages:
* actor
* dao
* models
* routes
* servicehandler

Source files that are implementing this functionality are in packages:
* actor
* dao
* models
* routes
* servicehandler

## Generate scalastyle configuration file

###### Note: The configuration for scalastyle is already created.

````
sbt scalastyleGenerateConfig
````

## Check scalastyle for code

````
sbt scalastyle
````

## END
