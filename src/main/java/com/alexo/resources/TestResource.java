package com.alexo.resources;

import com.alexo.api.Book;
import com.alexo.api.Books;
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

import javax.ws.rs.*;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

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
     * Adds a specific book to DB
     * Checks if book is present in read model
     * If it succeeds ... CREATED event in event table
     * @param isbn is the isbn of the book
     * @param title is book title
     * @return String confirmation
     */
    @POST
    @Path("/add-book")
    public String createBook(
            @DefaultValue("1111") @QueryParam("isbn") String isbn,
            @DefaultValue("Book1") @QueryParam("title") String title) {

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        /*
        Read json format from file
        Update w/ query param from url
         */
        try{
            File jsonDataFile = new File("/home/alex/Documents/jsonDataBasic.json");
            Book book;
            book = objectMapper.readValue(jsonDataFile, Book.class);
            book.setIsbn(isbn);
            book.setTitle(title);

            String json = gson.toJson(book);
            logger.debug(json);

            /*
            Check the read model
            If book not found ... update the db and fire the event
             */
            if (readDAO.findBook(isbn) != 0) {
                return "Book already in Table ... event not created";
            } else {
                postgresDAO.createFunc();
                postgresDAO.triggerFunc();
                postgresDAO.createTrigger();
                postgresDAO.insertEvent(isbn, "CREATED", json, timeStamp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Book Added";
    }

    /**
     * Adds a batch of books from json file to the DB
     * Checks if each book is present in read model
     * If it succeeds ... CREATED event in event table
     * @return String confirmation
     */
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
                    //return currentBook.getTitle() + " is already in Table ... event not created";
                } else {
                    postgresDAO.createFunc();
                    postgresDAO.triggerFunc();
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
    }

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
                    } catch (SQLException ex) {
                        throw new IllegalStateException("Error Binding JSON",ex);
                    }
                };
            }
        }
    }


    public String loanBook(@QueryParam("isbn") String isbn) {

        return "";
    }

    /**
     * Return command
     * Makes a book available to be loaned
     * @param isbn is the book to be returned
     * @return String confirmation
     */
    @POST
    @Path("/return")
    public String returnBook(@QueryParam("isbn") String isbn) {
        return "";
    }
}
