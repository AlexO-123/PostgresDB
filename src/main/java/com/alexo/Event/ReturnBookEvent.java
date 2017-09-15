package com.alexo.Event;

import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;

import java.text.SimpleDateFormat;

public class ReturnBookEvent {

    private String isbn;
    private PostgresDAO postgresDAO;
    private ReadDAO readDAO;

    public ReturnBookEvent(String isbn, PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.isbn = isbn;
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;
    }

    public void returnBook() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if(readDAO.findBook(isbn) != 0) {
            if(readDAO.checkAvailable(isbn).equals("N")) {
                postgresDAO.returnFunc();
                postgresDAO.triggerReturnFunc();
                postgresDAO.returnTrigger();
                postgresDAO.insertEvent(isbn, "RETURNED", "{\"name\": \"User\"}", timeStamp);
            }
        }
    }
}
