# PostgresSQL

The Project
---
The purpose of this project was to gain a better understanding of CQRS and Eventsouring using a PostreSQL database. The reasoning behind Postgres to hold events was its functionality with Json data types. In order to achieve my goal, I decided to model a simple concept of a library. This library has 3 simple commands; They may want to add a new book, loan an existing book to someone, or return a book to the library. From this we can see 3 simple events we need to capture; 'CREATED', 'LOANED', 'RETURNED'. The framework behind this will be Dropwizard and Maven due to its packaging useful libraries out of the box.

My first challenge was to get connected to a postgres DB. To do this, I used Docker images of postgres and pgadmin to manage the DB. My config.yml file contains instructions on how to connect to the database, including host name etc. Once this was done, I needed to interact with the DB. For this I used JDBI SQL Object API to access my DB. This enables the ability to create a table and insert data to it.

How to start the PostgresSQL application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/postgres-es-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
