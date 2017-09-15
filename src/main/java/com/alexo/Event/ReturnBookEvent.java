package com.alexo.Event;

import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class ReturnBookEvent {

    private String isbn;
    private PostgresDAO postgresDAO;
    private ReadDAO readDAO;
    private static Logger logger = LoggerFactory.getLogger(ReturnBookEvent.class.getName());

    public ReturnBookEvent(String isbn, PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.isbn = isbn;
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;
    }

    public boolean returnBook() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if(readDAO.findBook(isbn) != 0) {
            if(readDAO.checkAvailable(isbn).equals("N")) {
                postgresDAO.returnFunc();
                postgresDAO.triggerReturnFunc();
                postgresDAO.returnTrigger();
                postgresDAO.insertEvent(isbn, "RETURNED", "{\"name\": \"User\"}", timeStamp);
                return true;
            } else {
                logger.debug("Book already in library!");
                return false;
            }
        } else {
            logger.debug("Book not in table!");
            return false;
        }
    }
}
