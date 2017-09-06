package com.alexo.resources;

import com.alexo.api.EmployeePojo;
import com.alexo.api.Employees;
import com.alexo.jdbi.PostgresDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Resource to handle method calls
 * Basic CRUD capabilities
 */
@Path("/postgres")
public class TestResource {
    private PostgresDAO postgresDAO;
    private static Logger logger = LoggerFactory.getLogger(TestResource.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

    public TestResource(PostgresDAO postgresDAO) {
        this.postgresDAO = postgresDAO;
    }

    /**
     * Get method to check localhost is working
     * @return simple string
     */
    @GET
    public String hello() {
        return "OiOi";
    }

    /**
     * Creates the db if not already created
     * @return check string
     */
    @POST
    @Path("/create")
    public String createDB() {
        try {
            postgresDAO.createTable();
        } catch (UnableToExecuteStatementException e) {
            e.printStackTrace();
        }
        return "Table created";
    }

    /**
     * Inserts data from a json file to the db
     * @return confirmation string
     */
    @POST
    @Path("/insert")
    public String insert() {
        /*
        Read json file
        Map to POJO
        Iterate through each employee adding them to postgres db
         */
        try {
            File jsonDataFile = new File("/home/alex/Documents/jsonData.json");
            Employees employees;
            employees = objectMapper.readValue(jsonDataFile, Employees.class);
            Gson gson = new Gson();
            String json = gson.toJson(employees);
            logger.debug(json);

            String size = (String.valueOf(employees.getEmployees().size()));

            logger.debug(size);

            for (EmployeePojo emplo: employees.getEmployees()) {
                try{
                    postgresDAO.insertNew(emplo, timeStamp);
                } catch (UnableToExecuteStatementException exception) {
                    exception.printStackTrace();
                }
            }

            return "Upload successful";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading";
        }
    }

    /**
     * Deletes a chosen row from db
     * @param id is the id of the row to delete
     * @return confirmation message
     */
    @POST
    @Path("/delete")
    public String delete(@DefaultValue("-1") @QueryParam("q") String id) {
        if(id.matches("-1")) {
            return "Please specify an ID";
        }

        postgresDAO.delete(Integer.parseInt(id));

        return "Delete Complete!";
    }

    /**
     * Updates a random row with a random integer for the age
     * @return confirmation message
     * TODO: Allow user to update a specific row with specific data
     */
    @POST
    @Path("/update")
    public String update() {
        Random rn = new Random();

        int randomAge = rn.nextInt(100);
        int dbSize = postgresDAO.dbSize();

        logger.debug(String.valueOf(randomAge));

        //helloDAO.update(new Employee((rn.nextInt(dbSize)),, randomAge), timeStamp);

        postgresDAO.updateAge(rn.nextInt(dbSize), randomAge, timeStamp);

        return "update";
    }
}
