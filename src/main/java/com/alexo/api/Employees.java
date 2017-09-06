package com.alexo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO to hold json data
 */
public class Employees {
    @JsonProperty
    private List<EmployeePojo> employees = new ArrayList<>();

    /**
     * Gets a list of EmployeePojo
     * @return list of employees
     */
    public List<EmployeePojo> getEmployees() {
        return employees;
    }
}
