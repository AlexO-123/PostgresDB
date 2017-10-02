package com.alexo.Event;

import com.alexo.api.Book;
import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;

/**
 * Event class to create an event in the event table and update the read model
 */
public class AddBookEvent {
    private Book book;
    private String json;
    private PostgresDAO postgresDAO;
    private ReadDAO readDAO;

    public AddBookEvent(Book book, PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.book = book;
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;

        Gson gson = new Gson();
        json = gson.toJson(book);
    }

    /**
     * If book is not currently in table, create the event
     * @return number of books added/failed
     */
    public boolean addEvent() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if (readDAO.findBook(book.getIsbn()) == 0) {
            postgresDAO.createFunc();
            postgresDAO.triggerCreateFunc();
            postgresDAO.createTrigger();
            postgresDAO.insertEvent(book.getIsbn(), "CREATED", json, timeStamp);
            readDAO.refresh();
            return true;
        }
        return false;
    }
}
