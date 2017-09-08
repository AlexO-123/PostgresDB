package com.alexo.jdbi;

import com.alexo.resources.TestResource;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Data access for the PostgresDB
 * CRUD features
 */
public abstract class PostgresDAO {

    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS book_events (" +
            "id serial primary key NOT NULL, " +
            "isbn varchar(32) NOT NULL, " +
            "event_type varchar(32) NOT NULL, " +
            "data jsonb NOT NULL, " +
            "timestamp varchar(32) NOT NULL)")
    public abstract void createTable();

    @SqlUpdate(
            "INSERT into book_events (isbn, event_type, data, timestamp) " +
            "VALUES (:isbn, :event_type, :data, :timestamp)")
    public abstract void insertEvent(
            @Bind("isbn") String isbn,
            @Bind("event_type") String event_type,
            @TestResource.BindJson("data") String data,
            @Bind("timestamp") String timestamp);
}
