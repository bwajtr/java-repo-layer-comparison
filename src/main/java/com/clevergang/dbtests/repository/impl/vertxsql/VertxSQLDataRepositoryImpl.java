package com.clevergang.dbtests.repository.impl.vertxsql;

import com.clevergang.dbtests.repository.api.DataRepository;
import com.clevergang.dbtests.repository.api.data.*;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
public class VertxSQLDataRepositoryImpl implements DataRepository {

    private static final Logger log = LoggerFactory.getLogger(VertxSQLDataRepositoryImpl.class);

    private final PgPool client;

    private SqlConnection connection;
    private Transaction transaction;

    @Autowired
    public VertxSQLDataRepositoryImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public Company findCompany(Integer pid) {

        CompletableFuture<Company> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM company WHERE pid = $1", Tuple.of(pid),
                Collectors.mapping(rowToCompany(), first()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Company findCompanyUsingSimpleStaticStatement(Integer pid) {

        CompletableFuture<Company> res = new CompletableFuture<>();

        connection.query("SELECT * FROM company WHERE pid = " + pid,
                Collectors.mapping(rowToCompany(), first()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Department findDepartment(Integer pid) {

        CompletableFuture<Department> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM department WHERE pid = $1", Tuple.of(pid),
                Collectors.mapping(rowToDepartment(), first()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<Department> findDepartmentsOfCompany(Company company) {
        Assert.notNull(company);
        Assert.notNull(company.getPid());

        CompletableFuture<List<Department>> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM department WHERE company_pid = $1 ORDER BY pid", Tuple.of(company.getPid()),
                Collectors.mapping(rowToDepartment(), Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value());

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void deleteDepartments(List<Department> departmentsToDelete) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        connection.preparedBatch("DELETE FROM department WHERE pid = $1",
                departmentsToDelete.stream()
                        .map(d -> Tuple.of(d.getPid()))
                        .collect(Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(null);

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void updateDepartments(List<Department> departmentsToUpdate) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        connection.preparedBatch("UPDATE department SET company_pid = $1, name = $2 WHERE pid = $3",
                departmentsToUpdate.stream()
                        .map(d -> Tuple.of(d.getCompanyPid(), d.getName(), d.getPid()))
                        .collect(Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(null);

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void insertDepartments(List<Department> departmentsToInsert) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        connection.preparedBatch("INSERT INTO department (company_pid, name) VALUES ($1, $2)",
                departmentsToInsert.stream()
                        .map(d -> Tuple.of(d.getCompanyPid(), d.getName()))
                        .collect(Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(null);

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Project findProject(Integer pid) {

        CompletableFuture<Project> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM project WHERE pid = $1", Tuple.of(pid),
                Collectors.mapping(rowToProject(), first()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Integer insertProject(Project project) {

        CompletableFuture<Integer> future = new CompletableFuture<>();

        connection.preparedQuery("INSERT INTO project (name, datestarted) VALUES ($1, $2) RETURNING pid",
                Tuple.of(project.getName(), project.getDate()),
                Collectors.mapping(row -> row.getInteger(0), first()),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(ar.result().value().orElse(null));

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            return future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<Integer> insertProjects(List<Project> projects) {

        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        connection.preparedBatch("INSERT INTO project (name, datestarted) VALUES ($1, $2) RETURNING pid",
                projects.stream()
                        .map(p -> Tuple.of(p.getName(), p.getDate()))
                        .collect(Collectors.toList()),
                Collectors.mapping(row -> row.getInteger(0), Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(ar.result().value());

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            return future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<ProjectsWithCostsGreaterThanOutput> getProjectsWithCostsGreaterThan(int totalCostBoundary) {

        CompletableFuture<List<ProjectsWithCostsGreaterThanOutput>> res = new CompletableFuture<>();

        connection.preparedQuery("WITH project_info AS (\n" +
                        "    SELECT project.pid project_pid, project.name project_name, salary monthly_cost, company.name company_name\n" +
                        "    FROM project\n" +
                        "      JOIN projectemployee ON project.pid = projectemployee.project_pid\n" +
                        "      JOIN employee ON projectemployee.employee_pid = employee.pid\n" +
                        "      LEFT JOIN department ON employee.department_pid = department.pid\n" +
                        "      LEFT JOIN company ON department.company_pid = company.pid\n" +
                        "),\n" +
                        "project_cost AS (\n" +
                        "    SELECT project_pid, sum(monthly_cost) total_cost\n" +
                        "    FROM project_info GROUP BY project_pid\n" +
                        ")\n" +
                        "SELECT project_name, total_cost, company_name, sum(monthly_cost) company_cost " +
                        "  FROM project_info\n" +
                        "  JOIN project_cost USING (project_pid)\n" +
                        "WHERE total_cost > $1\n" +
                        "GROUP BY project_name, total_cost, company_name\n" +
                        "ORDER BY company_name",
                Tuple.of(totalCostBoundary),
                Collectors.mapping(rowToProjectsWithCostsGreaterThanOutput(), Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value());

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Employee findEmployee(Integer pid) {

        CompletableFuture<Employee> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM employee WHERE pid = $1", Tuple.of(pid),
                Collectors.mapping(rowToEmployee(), first()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<Employee> employeesWithSalaryGreaterThan(Integer minSalary) {

        CompletableFuture<List<Employee>> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM employee WHERE salary > $1", Tuple.of(minSalary),
                Collectors.mapping(rowToEmployee(), Collectors.toList()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value());

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void updateEmployee(Employee employeeToUpdate) {
        Assert.notNull(employeeToUpdate);
        Assert.notNull(employeeToUpdate.getPid());

        CompletableFuture<Void> future = new CompletableFuture<>();

        connection.preparedQuery(" UPDATE EMPLOYEE SET " +
                        " department_pid = $1, " +
                        " name = $2," +
                        " surname = $3," +
                        " email = $4," +
                        " salary = $5" +
                        " WHERE pid = $6",
                Tuple.of(
                        employeeToUpdate.getDepartmentPid(),
                        employeeToUpdate.getName(),
                        employeeToUpdate.getSurname(),
                        employeeToUpdate.getEmail(),
                        employeeToUpdate.getSalary(),
                        employeeToUpdate.getPid()),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(null);

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public RegisterEmployeeOutput callRegisterEmployee(String name,
                                                       String surname,
                                                       String email,
                                                       BigDecimal salary,
                                                       String departmentName,
                                                       String companyName) {

        CompletableFuture<RegisterEmployeeOutput> res = new CompletableFuture<>();

        connection.preparedQuery("SELECT * FROM register_employee($1, $2, $3, $4, $5, $6)",
                Tuple.of(name, surname, email, salary, departmentName, companyName),
                Collectors.mapping(rowToEmployeeOutput(), first()), ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Integer getProjectsCount() {

        CompletableFuture<Integer> res = new CompletableFuture<>();

        connection.query("SELECT COUNT(*) FROM project",
                Collectors.mapping(row -> row.getInteger(0), first()),
                ar -> {

                    if (ar.succeeded()) {
                        res.complete(ar.result().value().orElse(null));

                    } else {
                        res.completeExceptionally(ar.cause());
                    }
                });

        try {
            return res.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void removeProject(Integer pid) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        connection.preparedQuery("DELETE FROM project WHERE pid = $1", Tuple.of(pid),
                ar -> {

                    if (ar.succeeded()) {
                        future.complete(null);

                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });

        try {
            future.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static Function<Row, Company> rowToCompany() {
        return row -> {
            Company company = new Company();
            company.setPid(row.getInteger("pid"));
            company.setName(row.getString("name"));
            company.setAddress(row.getString("address"));
            return company;
        };
    }

    private static Function<Row, Department> rowToDepartment() {
        return row -> {
            Department department = new Department();
            department.setPid(row.getInteger("pid"));
            department.setName(row.getString("name"));
            department.setCompanyPid(row.getInteger("company_pid"));
            return department;
        };
    }

    private static Function<Row, RegisterEmployeeOutput> rowToEmployeeOutput() {
        return row -> {
            RegisterEmployeeOutput output = new RegisterEmployeeOutput();
            output.setEmployeePid(row.getInteger("employee_id"));
            output.setDepartmentPid(row.getInteger("department_id"));
            output.setCompanyPid(row.getInteger("company_id"));
            return output;
        };
    }

    private static Function<Row, Project> rowToProject() {
        return row -> {
            Project project = new Project();
            project.setPid(row.getInteger("pid"));
            project.setName(row.getString("name"));
            project.setDate(row.getLocalDate("datestarted"));
            return project;
        };
    }

    private static Function<Row, Employee> rowToEmployee() {
        return row -> {
            Employee employee = new Employee();
            employee.setPid(row.getInteger("pid"));
            employee.setName(row.getString("name"));
            employee.setSurname(row.getString("surname"));
            employee.setEmail(row.getString("email"));
            employee.setDepartmentPid(row.getInteger("department_pid"));
            employee.setSalary(row.getBigDecimal("salary"));
            return employee;
        };
    }

    private static Function<Row, ProjectsWithCostsGreaterThanOutput> rowToProjectsWithCostsGreaterThanOutput() {
        return row -> {
            ProjectsWithCostsGreaterThanOutput project = new ProjectsWithCostsGreaterThanOutput();
            project.setProjectName(row.getString("project_name"));
            project.setTotalCost(row.getBigDecimal("total_cost"));
            project.setCompanyName(row.getString("company_name"));
            project.setCompanyCost(row.getBigDecimal("company_cost"));
            return project;
        };
    }

    private <R> Collector<R, ?, Optional<R>> first() {
        return Collectors.reducing((o1, o2) -> o1);
    }

    public void begin() {
        CompletableFuture<SqlConnection> futureConnection = new CompletableFuture<>();
        client.getConnection(connectionHandler -> futureConnection.complete(connectionHandler.result()));
        try {
            connection = futureConnection.get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        transaction = connection.begin();
    }

    public void rollback() {
        transaction.close();
        connection.close();
    }
}
