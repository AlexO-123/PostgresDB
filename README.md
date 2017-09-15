# PostgresSQL

The Project
---
The purpose of this project was to gain a better understanding of CQRS and Eventsouring using a PostreSQL database. The reasoning behind Postgres to hold events was its functionality with Json data types. In order to achieve my goal, I decided to model a simple concept of a library. This library has 3 simple commands; They may want to add a new book, loan an existing book to someone, or return a book to the library. From this we can see 3 simple events we need to capture; 'CREATED', 'LOANED', 'RETURNED'. The framework behind this will be Dropwizard and Maven due to it packaging useful libraries out of the box.

My first challenge was to get connected to a postgres DB. To do this, I used Docker images for postgres and pgadmin to manage the DB. My config.yml file contains instructions on how to connect to the database, including host name etc. Once this was done, I needed to interact with the DB. For this I used JDBI SQL Object API to access my DB. This enables the ability to create a table and insert data to it.

To Create a book, I first explicitly made the user input the ISBN and title of the book, whilst defaulting the page numbers to 20. This was useful as it showed me how I can utilise JDBI to insert data in the table. My second iteration of this saw me mapping a json file with a list of books to some POJO and inserting each book into the table. This worked, however was not inline with common CQRS techniques that I discovered. 

The concept of Command Handler to handle commands and trigger events was something I wanted to implement. For this, I created a simple command interface which contained an <code>execute()</code> method. I implement this method in my command handler, which can execute the correct command depending on what called it.

To update my read model, I am using triggers and functions in postgres. This concept see me wrapping a function in a trigger (beacuse you can't pass data via a trigger) and calling some other function to update my projection.

How to start the PostgresSQL application
---
1. Install docker
2. Grab a postgres image and pgadmin4 (web) image
3. Link pgadmin with postgres -- look at host postgres is running on
4. Create a db from pgadmin
5. Update config.yml with details of your db -- update data currently there
6. For the app to work ... need some file on your computer which represents an array of books ... make sure names match classes in api
7. Update file mapping in TestResource to wherever your file is located on your system
1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/postgres-es-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
