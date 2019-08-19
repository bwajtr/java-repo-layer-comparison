package com.clevergang.dbtests.repository.impl.vertxsql;

import com.clevergang.dbtests.repository.api.DataRepository;
import com.clevergang.dbtests.repository.api.data.*;
import io.vertx.pgclient.PgPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class VertxSQLDataRepositoryImpl implements DataRepository {

  private final PgPool client;

  @Autowired
  public VertxSQLDataRepositoryImpl(PgPool client) {
    this.client = client;
  }

  @Override
  public Company findCompany(Integer pid) {
    return null;
  }

  @Override
  public Company findCompanyUsingSimpleStaticStatement(Integer pid) {
    return null;
  }

  @Override
  public Department findDepartment(Integer pid) {
    return null;
  }

  @Override
  public List<Department> findDepartmentsOfCompany(Company company) {
    return null;
  }

  @Override
  public void deleteDepartments(List<Department> departmentsToDelete) {

  }

  @Override
  public void updateDepartments(List<Department> departmentsToUpdate) {

  }

  @Override
  public void insertDepartments(List<Department> departmentsToInsert) {

  }

  @Override
  public Project findProject(Integer pid) {
    return null;
  }

  @Override
  public Integer insertProject(Project project) {
    return null;
  }

  @Override
  public List<Integer> insertProjects(List<Project> projects) {
    return null;
  }

  @Override
  public List<ProjectsWithCostsGreaterThanOutput> getProjectsWithCostsGreaterThan(int totalCostBoundary) {
    return null;
  }

  @Override
  public Employee findEmployee(Integer pid) {
    return null;
  }

  @Override
  public List<Employee> employeesWithSalaryGreaterThan(Integer minSalary) {
    return null;
  }

  @Override
  public void updateEmployee(Employee employeeToUpdate) {

  }

  @Override
  public RegisterEmployeeOutput callRegisterEmployee(String name,
      String surname,
      String email,
      BigDecimal salary,
      String departmentName,
      String companyName) {
    return null;
  }

  @Override
  public Integer getProjectsCount() {
    return null;
  }

  @Override
  public void removeProject(Integer pid) {

  }
}
