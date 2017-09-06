package com.alexo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Properties for an employee
 * Jackson mapped from json file
 */
public class EmployeePojo {

    @JsonProperty
    String name;

    @JsonProperty
    int age;

    /*
    BindBean claims to work with JavaBeans. Without getters and setters,
    UnableToExecuteStatementException is thrown and mapping failed
     */
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
