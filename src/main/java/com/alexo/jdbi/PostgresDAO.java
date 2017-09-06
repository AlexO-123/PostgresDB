package com.alexo.jdbi;

import com.alexo.api.EmployeePojo;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

/**
 * Data access for the PostgresDB
 * CRUD features
 */
public abstract class PostgresDAO {

    @SqlUpdate("create table if not exists test (" +
            "id serial primary key, name varchar(32), age integer, timestamp varchar(32), active char default 'Y')")
    public abstract void createTable();

    /*@SqlUpdate(
            "insert into employees (name, age, timestamp) " +
            "values (:name, :age, :timestamp)")
    public abstract void insert(@Bind("name") String name, @Bind("age") int age, @Bind("timestamp") String timestamp);*/

    @SqlUpdate(
            "insert into test (name, age, timestamp) " +
                    "values (:name, :age, :timestamp)")
    public abstract void insertNew(@BindBean EmployeePojo e, @Bind("timestamp") String timestamp);

    /*@SqlUpdate(
            "update employees " +
            "set name = :name, age = :age, timestamp = :timestamp " +
            "where id = :id")
    public abstract int update(@BindBean Employee s, @Bind("timestamp") String timestamp, @Optional(@Bind("age") int age));*/

    @SqlUpdate(
            "update test " +
                    "set age = :age, timestamp = :timestamp " +
                    "where id = :id")
    public abstract int updateAge(@Bind("id") int id, @Bind("age") int age, @Bind("timestamp") String timestamp);

    @SqlUpdate(
            "update test " +
                    "set active = 'N' " +
                    "where id = :id")
    public abstract int delete(@Bind("id") int id);

    @SqlQuery(
            "select name " +
                    "from test " +
                    "where active = 'Y'")
    public abstract List<String> findName();

    @SqlQuery(
            "select count(*) AS exact_count FROM test;")
    public abstract int dbSize();
}
