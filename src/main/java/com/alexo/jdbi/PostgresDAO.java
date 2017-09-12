package com.alexo.jdbi;

import com.alexo.resources.TestResource;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Data access for the Book Event Database
 * Can insert immutable events to the table
 * Triggers and functions work to update read models for eventual consistency
 */
public abstract class PostgresDAO {

    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS book_events (" +
            "id serial PRIMARY KEY NOT NULL, " +
            "isbn varchar(32) NOT NULL, " +
            "event_type varchar(32) NOT NULL, " +
            "data jsonb NOT NULL, " +
            "timestamp varchar(32) NOT NULL)")
    public abstract void createTable();

    /**
     * I wrap a function in a trigger
     * This is because a trigger can not pass data to a function
     * This trigger gets executed every time a 'CREATED' event happens
     */
    @SqlUpdate(
            "DROP TRIGGER IF EXISTS add_book ON book_events; " +
            "CREATE TRIGGER add_book AFTER INSERT ON book_events " +
            "FOR EACH ROW " +
            "WHEN (NEW.event_type = 'CREATED') " +
            "EXECUTE PROCEDURE fn_trigger_book_create();")
    public abstract void createTrigger();

    /**
     * Pass the data we want to store to a function which gets triggered when a book is created
     */
    @SqlUpdate(
           "CREATE OR REPLACE FUNCTION fn_trigger_book_create() " +
           "RETURNS trigger " +
           "security definer " +
           "LANGUAGE plpgsql " +
           "AS $$ " +
           "BEGIN " +
           "    PERFORM fn_project_book_create(new.data); " +
           "    RETURN new; " +
           "END; " +
           "$$;")
   public abstract void triggerFunc();

    /**
     * Map our Json data to the values in Books table
     * Return the isbn number as an integer ... currently not in use
     */
    @SqlUpdate(
            "CREATE OR REPLACE FUNCTION fn_project_book_create(data jsonb) " +
            "RETURNS integer AS " +
            "$BODY$ " +
            "DECLARE result int; " +
            "BEGIN " +
            "    INSERT INTO books(isbn, title, pages) " +
            "    VALUES(data->>'isbn', data->>'title', data->>'pages') " +
            "    RETURNING isbn INTO result; " +
            "RETURN result; " +
            "END; " +
            "$BODY$ " +
            "LANGUAGE plpgsql")
    public abstract void createFunc();

    @SqlUpdate(
            "INSERT into book_events (isbn, event_type, data, timestamp) " +
            "VALUES (:isbn, :event_type, :data, :timestamp)")
    public abstract void insertEvent(
            @Bind("isbn") String isbn,
            @Bind("event_type") String event_type,
            @TestResource.BindJson("data") String data,
            @Bind("timestamp") String timestamp);
}
