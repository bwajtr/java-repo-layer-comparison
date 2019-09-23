package com.clevergang.dbtests.repository.impl.jdbi;

import com.clevergang.dbtests.repository.api.DataRepository;
import com.clevergang.dbtests.repository.api.data.*;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * JDBI implementation of the DataRepository interface
 *
 * @author Bretislav Wajtr
 */
@Repository
public class JDBIDataRepositoryImpl implements DataRepository {

    private Jdbi dbi;

    @Autowired
    public JDBIDataRepositoryImpl(Jdbi dbi) {
        this.dbi = dbi;
    }

    @Override
    public Company findCompany(Integer pid) {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT * FROM company WHERE pid = :pid")
                    .bind("pid", pid).mapToBean(Company.class)
                    .first();
        }
    }

    @Override
    public Company findCompanyUsingSimpleStaticStatement(Integer pid) {
        // I didn't find a way how to setup JDBI so the query is executed statically (using JDBC regular Statement
        // not PreparedStatement). However, JDBI is polite and provides way how to access the underlying JDBC connection
        // -> therefore we can actually fall back to JDBC way of doing things (which is not such a big deal since we need simple
        // Statements only rarely):
        try (Handle h = dbi.open()) {
            String query = "SELECT pid, address, name " +
                    "FROM company " +
                    "WHERE pid = " + pid;

            Statement statement = h.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            Company result = null;
            if (resultSet.next()) {
                result = new Company();
                result.setPid(resultSet.getInt("pid"));
                result.setAddress(resultSet.getString("address"));
                result.setName(resultSet.getString("name"));

            }
            return result;
        } catch (SQLException e) {
            // just wrap checked exceptions as e
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProject(Integer pid) {
        try (Handle h = dbi.open()) {
            h.createUpdate("DELETE FROM project WHERE pid = :pid")
                    .bind("pid", pid)
                    .execute();
        }
    }

    @Override
    public Department findDepartment(Integer pid) {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT pid, name, company_pid companyPid FROM department WHERE pid = :pid")
                    .bind("pid", pid).mapToBean(Department.class)
                    .first();
        }
    }

    @Override
    public List<Department> findDepartmentsOfCompany(Company company) {
        Assert.notNull(company);
        Assert.notNull(company.getPid());

        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT pid, name, company_pid companyPid " +
                    "FROM department " +
                    "WHERE company_pid = :company_pid " +
                    "ORDER BY pid ")
                    .bind("company_pid", company.getPid()).mapToBean(Department.class)
                    .list();
        }
    }

    @Override
    public void deleteDepartments(List<Department> departmentsToDelete) {
        try (Handle h = dbi.open()) {
            PreparedBatch preparedBatch = h.prepareBatch("DELETE FROM department WHERE pid = :pid");

            for (Department department : departmentsToDelete) {
                preparedBatch.bind("pid", department.getPid()).add();
            }

            preparedBatch.execute();
        }
    }

    @Override
    public void updateDepartments(List<Department> departmentsToUpdate) {
        try (Handle h = dbi.open()) {
            PreparedBatch preparedBatch = h.prepareBatch("UPDATE department SET company_pid = :company_pid, name = :name WHERE pid = :pid");
            for (Department department : departmentsToUpdate) {
                preparedBatch
                        .bind("company_pid", department.getCompanyPid())
                        .bind("name", department.getName())
                        .bind("pid", department.getPid())
                        .add();
            }
            preparedBatch.execute();
        }
    }

    @Override
    public void insertDepartments(List<Department> departmentsToInsert) {
        try (Handle h = dbi.open()) {
            PreparedBatch preparedBatch = h.prepareBatch("INSERT INTO department (company_pid, name) VALUES (:company_pid, :name)");
            for (Department department : departmentsToInsert) {
                preparedBatch
                        .bind("company_pid", department.getCompanyPid())
                        .bind("name", department.getName())
                        .add();
            }
            preparedBatch.execute();
        }
    }

    @Override
    public Project findProject(Integer pid) {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT pid, name, datestarted FROM project WHERE pid = :pid")
                    .bind("pid", pid)
                    .map((rs, ctx) -> {
                        Project project = new Project();
                        project.setPid(rs.getInt("pid"));
                        project.setName(rs.getString("name"));
                        project.setDate(rs.getDate("datestarted").toLocalDate());
                        return project;
                    })
                    .first();
        }
    }

    @Override
    public Integer insertProject(Project project) {
        try (Handle h = dbi.open()) {
            return h.createUpdate("INSERT INTO project (name, datestarted) VALUES (:name, :datestarted)")
                    .bind("name", project.getName())
                    .bind("datestarted", project.getDate())
                    .executeAndReturnGeneratedKeys("pid")
                    .mapTo(Integer.TYPE)
                    .first();
        }
    }

    @Override
    public List<Integer> insertProjects(List<Project> projects) {
        try (Handle h = dbi.open()) {
            PreparedBatch preparedBatch = h.prepareBatch("INSERT INTO project (name, datestarted) VALUES (:name, :datestarted)");
            for (Project project : projects) {
                preparedBatch
                        .bind("name", project.getName())
                        .bind("datestarted", project.getDate())
                        .add();
            }
            return preparedBatch.executeAndReturnGeneratedKeys("pid").mapTo(Integer.TYPE).list();
        }
    }

    @Override
    public List<ProjectsWithCostsGreaterThanOutput> getProjectsWithCostsGreaterThan(int totalCostBoundary) {
        try (Handle h = dbi.open()) {
            String query;
            query = "WITH project_info AS (\n" +
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
                    "SELECT project_name projectName, total_cost totalCost, company_name companyName, sum(monthly_cost) companyCost FROM project_info\n" +
                    "  JOIN project_cost USING (project_pid)\n" +
                    "WHERE total_cost > :totalCostBoundary\n" +
                    "GROUP BY project_name, total_cost, company_name\n" +
                    "ORDER BY company_name";

            return h.createQuery(query)
                    .bind("totalCostBoundary", totalCostBoundary).mapToBean(ProjectsWithCostsGreaterThanOutput.class)
                    .list();
        }
    }

    @Override
    public Employee findEmployee(Integer pid) {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT pid, name, surname, email, department_pid departmentPid, salary FROM employee WHERE pid = :pid")
                    .bind("pid", pid).mapToBean(Employee.class)
                    .first();
        }
    }

    @Override
    public List<Employee> employeesWithSalaryGreaterThan(Integer minSalary) {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT * FROM employee WHERE salary > :salary")
                    .bind("salary", minSalary).mapToBean(Employee.class)
                    .list();
        }
    }

    @Override
    public void updateEmployee(Employee employeeToUpdate) {
        Assert.notNull(employeeToUpdate);
        Assert.notNull(employeeToUpdate.getPid());

        try (Handle h = dbi.open()) {
            h.createUpdate(" UPDATE EMPLOYEE SET " +
                    " department_pid = :department_pid, " +
                    " name = :name," +
                    " surname = :surname," +
                    " email = :email," +
                    " salary = :salary" +
                    " WHERE pid = :pid")
                    .bind("department_pid", employeeToUpdate.getDepartmentPid())
                    .bind("name", employeeToUpdate.getName())
                    .bind("surname", employeeToUpdate.getSurname())
                    .bind("email", employeeToUpdate.getEmail())
                    .bind("salary", employeeToUpdate.getSalary())
                    .bind("pid", employeeToUpdate.getPid())
                    .execute();
        }
    }

    @Override
    public RegisterEmployeeOutput callRegisterEmployee(String name, String surname, String email, BigDecimal salary, String departmentName, String companyName) {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT employee_id employeePid, department_id departmentPid, company_id companyPid FROM register_employee(" +
                    "  :name, \n" +
                    "  :surname, \n" +
                    "  :email, \n" +
                    "  :salary, \n" +
                    "  :departmentName, \n" +
                    "  :companyName\n" +
                    ")")
                    .bind("name", name)
                    .bind("surname", surname)
                    .bind("email", email)
                    .bind("salary", salary)
                    .bind("departmentName", departmentName)
                    .bind("companyName", companyName).mapToBean(RegisterEmployeeOutput.class)
                    .first();
        }
    }

    @Override
    public Integer getProjectsCount() {
        try (Handle h = dbi.open()) {
            return h.createQuery("SELECT count(*) FROM project")
                    .mapTo(Integer.TYPE)
                    .first();
        }

    }
}
