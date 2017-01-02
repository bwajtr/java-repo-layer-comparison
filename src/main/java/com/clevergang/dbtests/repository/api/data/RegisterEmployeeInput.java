package com.clevergang.dbtests.repository.api.data;

import java.math.BigDecimal;

public class RegisterEmployeeInput {

    private final String name;
    private final String surname;
    private final String email;
    private final BigDecimal salary;
    private final String departmentName;
    private final String companyName;


    public RegisterEmployeeInput(String name, String surname, String email, BigDecimal salary, String departmentName, String companyName) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.salary = salary;
        this.departmentName = departmentName;
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getCompanyName() {
        return companyName;
    }
}
