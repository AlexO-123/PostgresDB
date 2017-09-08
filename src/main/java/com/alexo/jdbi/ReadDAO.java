package com.alexo.jdbi;

import com.alexo.api.Book;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public abstract class ReadDAO {

    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS books (" +
            "isbn varchar(32) primary key NOT NULL, " +
            "title varchar(32) NOT NULL, " +
            "pages varchar(32) NOT NULL)")
    public abstract void createBookTable();

    @SqlUpdate(
            "INSERT into books (isbn, title, pages) " +
            "VALUES (:isbn, :title, :pages)")
    public abstract int addBook(@BindBean Book book);

    @SqlQuery(
            "SELECT count(*) AS exact_count " +
            "FROM books " +
            "WHERE isbn = :isbn")
    public abstract int findBook(@Bind("isbn") String isbn);
}
