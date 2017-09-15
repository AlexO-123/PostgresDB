package com.alexo.Event;

import com.alexo.api.Book;
import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;

public class AddBookEvent {
    Book book;
    Gson gson = new Gson();
    String json;

    PostgresDAO postgresDAO;
    ReadDAO readDAO;

    public AddBookEvent(Book book, PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.book = book;
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;

        json = gson.toJson(book);
    }

    public void addEvent() {

        int failed =0;
        int added =0;

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    //String bookJson = gson.toJson(currentBook);
        if (readDAO.findBook(book.getIsbn()) != 0) {
            failed += 1;
        } else {
            postgresDAO.createFunc();
            postgresDAO.triggerCreateFunc();
            postgresDAO.createTrigger();
            postgresDAO.insertEvent(book.getIsbn(), "CREATED", json, timeStamp);
            added += 1;
        }

        /*return "Books Added = " + added + '\n' +
                "Number failed = " + failed;*/

    }
}
