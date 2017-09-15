package com.alexo.jdbi;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Read data model for booked created
 * Gets queried when a new book wants to be added to the db
 */
public abstract class ReadDAO {

    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS books (" +
            "isbn varchar(32) primary key NOT NULL, " +
            "title varchar(32) NOT NULL, " +
            "pages varchar(32) NOT NULL, " +
            "available char DEFAULT 'Y')")
    public abstract void createBookTable();

    /*@SqlUpdate(
            "INSERT into books (isbn, title, pages) " +
            "VALUES (:isbn, :title, :pages)")
    public abstract int addBook(@BindBean Book book);*/

    @SqlQuery(
            "SELECT count(*) AS exact_count " +
            "FROM books " +
            "WHERE isbn = :isbn")
    public abstract int findBook(@Bind("isbn") String isbn);

    @SqlQuery(
            "SELECT available " +
            "FROM books " +
            "WHERE isbn = :isbn")
    public abstract String checkAvailable(@Bind("isbn") String isbn);

}
