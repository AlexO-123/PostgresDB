package com.alexo.resources;

import com.alexo.jdbi.PostgresDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.lang.annotation.*;
import java.sql.SQLException;
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
     * Creates the table if not already created
     * @return check string
     */
    @POST
    @Path("/create-table")
    public String createTable() {
        try {
            postgresDAO.createTable();
        } catch (UnableToExecuteStatementException e) {
            e.printStackTrace();
        }
        return "Table created";
    }

    @POST
    @Path("/add-book")
    public String addBook(@DefaultValue("1111") @QueryParam("isbn") String isbn) {
        //Look for isbn in event table
        //if not found - create an event to say book has been added
        //else - fail

        if (postgresDAO.findBook(isbn) != 0) {
            return "Book already in Table ... event not created";
        } else {

        }


        return "Book Added";
    }

    @BindingAnnotation(BindJson.JsonBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindJson {
        String value();

        public static class JsonBinderFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                return new Binder<BindJson, String>() {
                    @Override
                    public void bind(SQLStatement q, BindJson bind, String jsonString) {
                        try {
                            PGobject data = new PGobject();
                            data.setType("jsonb");
                            data.setValue(jsonString);
                            q.bind(bind.value(), data);
                        } catch (SQLException ex) {
                            throw new IllegalStateException("Error Binding JSON",ex);
                        }
                    }
                };
            }
        }
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
        /*try {
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
                  //  postgresDAO.insertNew(emplo, timeStamp);
                } catch (UnableToExecuteStatementException exception) {
                    exception.printStackTrace();
                }
            }

            return "Upload successful";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading";
        }*/

        return "";
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
        //int dbSize = postgresDAO.dbSize();

        logger.debug(String.valueOf(randomAge));

        //helloDAO.update(new Employee((rn.nextInt(dbSize)),, randomAge), timeStamp);

        //postgresDAO.updateAge(rn.nextInt(dbSize), randomAge, timeStamp);

        return "update";
    }
}
