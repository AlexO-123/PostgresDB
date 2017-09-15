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
 * Basic CRUD capabilities
 */
@Path("/postgres")
public class TestResource {
    private PostgresDAO postgresDAO;
    private ReadDAO readDAO;
    private static Logger logger = LoggerFactory.getLogger(TestResource.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson = new Gson();
    private final String FILEPATH = "/home/alex/Documents/booksJson.json";
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
        } catch (UnableToExecuteStatementException e) {
            e.printStackTrace();
        }
        return "Table created";
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





/*
    *//**
     * Method 1 - add a book specified by URL parameters
     * Adds a specific book to DB
     * Checks if book is present in read model
     * If it succeeds ... CREATED event in event table
     * @param isbn is the isbn of the book
     * @param title is book title
     * @return String confirmation
     *//*
    @POST
    @Path("/add-book")
    public String createBook(
            @DefaultValue("1111") @QueryParam("isbn") String isbn,
            @DefaultValue("Book1") @QueryParam("title") String title) {

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        *//*
        Read json format from file
        Update w/ query param from url
         *//*
        try{
            File jsonDataFile = new File("/home/alex/Documents/jsonDataBasic.json");
            Book book;
            book = objectMapper.readValue(jsonDataFile, Book.class);
            book.setIsbn(isbn);
            book.setTitle(title);

            String json = gson.toJson(book);
            logger.debug(json);

            *//*
            Check the read model
            If book not found ... update the db and fire the event
             *//*
            if (readDAO.findBook(isbn) != 0) {
                return "Book already in Table ... event not created";
            } else {
                postgresDAO.createFunc();
                postgresDAO.triggerCreateFunc();
                postgresDAO.createTrigger();
                postgresDAO.insertEvent(isbn, "CREATED", json, timeStamp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Book Added";
    }

    *//**
     * Method 2 - add batch books from json file
     * Adds a batch of books from json file to the DB
     * Checks if each book is present in read model
     * If it succeeds ... CREATED event in event table
     * @return String confirmation
     *//*
    @POST
    @Path("/add-book-batch")
    public String createBookBatch() {

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        int added = 0;
        int failed = 0;

        try{
            File jsonDataFile = new File("/home/alex/Documents/booksJson.json");
            Books books;
            books = objectMapper.readValue(jsonDataFile, Books.class);

            String json = gson.toJson(books);
            logger.debug(json);

            for(Book currentBook: books.getBooks()) {
                String bookJson = gson.toJson(currentBook);
                if (readDAO.findBook(currentBook.getIsbn()) != 0) {
                    failed += 1;
                } else {
                    postgresDAO.createFunc();
                    postgresDAO.triggerCreateFunc();
                    postgresDAO.createTrigger();
                    postgresDAO.insertEvent(currentBook.getIsbn(), "CREATED", bookJson, timeStamp);
                    added += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Books Added = " + added + '\n' +
                "Number failed = " + failed;
    }  */
}
