package com.alexo.Event;

import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Event class to create an event in the event table and update the read model
 */
public class LoanBookEvent {

    private String isbn;
    private PostgresDAO postgresDAO;
    private ReadDAO readDAO;
    private static Logger logger = LoggerFactory.getLogger(LoanBookEvent.class.getName());

    public LoanBookEvent(String isbn, PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.isbn = isbn;
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;
    }

    /**
     * Checks if book is currently available and inserts a 'LOANED' event if available
     * @return boolean true/false if event fails
     */
    public boolean loanEvent() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if(readDAO.findBook(isbn) != 0) {
            if(readDAO.checkAvailable(isbn).equals("Y")) {
                postgresDAO.loanFunc();
                postgresDAO.triggerLoanFunc();
                postgresDAO.loanTrigger();
                postgresDAO.insertEvent(isbn, "LOANED", "{\"name\": \"User\"}", timeStamp);
            } else {
                logger.debug("Book found in table but already on loan!");
                return false;
            }
        } else {
            logger.debug("Book not in table!");
            return false;
        }
        return true;
    }
}
