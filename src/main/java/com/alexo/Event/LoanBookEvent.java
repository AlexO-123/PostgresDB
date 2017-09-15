package com.alexo.Event;

import com.alexo.jdbi.PostgresDAO;
import com.alexo.jdbi.ReadDAO;

import java.text.SimpleDateFormat;

public class LoanBookEvent {

    String isbn;
    PostgresDAO postgresDAO;
    ReadDAO readDAO;

    public LoanBookEvent(String isbn, PostgresDAO postgresDAO, ReadDAO readDAO) {
        this.isbn = isbn;
        this.postgresDAO = postgresDAO;
        this.readDAO = readDAO;
    }

    public void loanEvent() {

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if(readDAO.findBook(isbn) != 0) {
            if(readDAO.checkAvailable(isbn).equals("Y")) {
                postgresDAO.loanFunc();
                postgresDAO.triggerLoanFunc();
                postgresDAO.loanTrigger();
                postgresDAO.insertEvent(isbn, "LOANED", "{\"name\": \"User\"}", timeStamp);
            }
        }
    }
}
