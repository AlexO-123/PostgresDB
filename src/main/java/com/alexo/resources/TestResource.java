package com.alexo.resources;

import com.alexo.Event.AddBookEvent;
import com.alexo.Event.LoanBookEvent;
import com.alexo.Event.ReturnBookEvent;
import com.alexo.api.Book;
import com.alexo.api.Books;
import com.alexo.command.*;
import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.postgresql.util.PGobject;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.sql.SQLException;

/**
 * Resource to handle method calls
 * Fires commands to do a 'thing' depending on the context of the request
 */
@Path("/postgres")
public class TestResource {
    private PostgresDAO postgresDAO;
    private ReadDAO readDAO;
    private static Logger logger = LoggerFactory.getLogger(TestResource.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson = new Gson();
    private CommandHandler commandHandler = new CommandHandler();

    public TestResource(PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;
    }

    /**
     * Get method to check localhost is working
     * @return String confirmation
     */
    @GET
    public String hello() {
        return "OiOi";
    }

    /**
     * Creates tables if not already created
     * @return String confirmation
     */
    @POST
    @Path("/create-table")
    public String createTable() {
        try {
            postgresDAO.createTable();
            readDAO.createBookTable();
            readDAO.materializedView();
        } catch (UnableToExecuteStatementException e) {
            e.printStackTrace();
        }
        return "Table created";
    }

    @GET
    @Path("/query")
    public void Query() {
        logger.debug(readDAO.queryView().toString());
    }

    /**
     * Map command to an event to execute
     * @return String confirmation - assume success
     */
    @POST
    @Path("/add-book-command")

    public String addBookCommand() {
        int failed = 0;
        int added = 0;
        try{
            String FILEPATH = "/home/alex/Documents/booksJson.json";
            Books books = objectMapper.readValue(
                    new File(FILEPATH), Books.class);

            logger.debug(gson.toJson(books));

            for(Book currentBook: books.getBooks()) {
                AddBookEvent addBookEvent = new AddBookEvent(currentBook, postgresDAO, readDAO);
                Command addBook = new AddBookCommand(addBookEvent);
                commandHandler.setCommand(addBook);
                if (commandHandler.triggerEvent()) {
                    added++;
                } else {
                    failed++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("Number added: " + added + ", Number failed: " + failed);
        return "Number added = " + added + "\n" + "Failed = " + failed;
    }

    /**
     * Command to handle loaning of a book
     * Triggers a loan event to insert in Event Table
     * @param isbn is the book we want to loan
     * @return String confirmation
     */
    @POST
    @Path("/loan-book")
    public String loanBook(@QueryParam("isbn") String isbn) {
        if(isbn == null) {
            return "Please specify an ISBN for the book";
        } else {
            LoanBookEvent loanBookEvent = new LoanBookEvent(isbn, postgresDAO, readDAO);
            Command loanBook = new LoanBookCommand(loanBookEvent);
            commandHandler.setCommand(loanBook);
            if (commandHandler.triggerEvent()) {
                return "Book Loaned";
            } else {
                return "Failed to Loan book";
            }
        }
    }

    /**
     * Return command
     * Makes a book available to be loaned
     * @param isbn is the book to be returned
     * @return String confirmation
     */
    @POST
    @Path("/return-book")
    public String returnBook(@QueryParam("isbn") String isbn) {
        if(isbn == null) {
            return "Please specify an ISBN for the book";
        } else {
            ReturnBookEvent returnBookEvent = new ReturnBookEvent(isbn, postgresDAO, readDAO);
            Command returnBook = new ReturnBookCommand(returnBookEvent);
            commandHandler.setCommand(returnBook);
            if (commandHandler.triggerEvent()) {
                return "Book returned";
            } else {
                return "Failed to return book";
            }
        }
    }

    /**
     * Interface to bind json data type to postgres table ... JDBI can only bind to single argument <code>@Bind</code> or <code>@BindBean</code>
     * To solve this, we create a PGobject instance and bind to that
     * For that, we need to create our own <code>@BindAnnotation</code>
     * Credit for code :-http://blog.anorakgirl.co.uk/2016/01/using-jdbi-with-postgres-json-data/
     */
    @BindingAnnotation(BindJson.JsonBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindJson {
        String value();
        class JsonBinderFactory implements BinderFactory {
            @Override
            public Binder build(Annotation annotation) {
                //Lambda
                return (Binder<BindJson, String>) (q, bind, jsonString) -> {
                    try {
                        PGobject data = new PGobject();
                        data.setType("jsonb");
                        data.setValue(jsonString);
                        q.bind(bind.value(), data);
                    } catch (SQLException e) {
                        throw new IllegalStateException("Error Binding JSON",e);
                    }
                };
            }
        }
    }
}
