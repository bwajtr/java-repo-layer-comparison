package com.clevergang.dbtests.repository.impl.jdbctemplate;

import com.clevergang.dbtests.repository.api.DataRepository;
import com.clevergang.dbtests.repository.api.data.Company;
import com.clevergang.dbtests.repository.api.data.Department;
import com.clevergang.dbtests.repository.api.data.Employee;
import com.clevergang.dbtests.repository.api.data.Project;
import com.clevergang.dbtests.repository.api.data.ProjectsWithCostsGreaterThanOutput;
import com.clevergang.dbtests.repository.api.data.RegisterEmployeeInput;
import com.clevergang.dbtests.repository.api.data.RegisterEmployeeOutput;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateCrud;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.SqlParameterSourceFactory;
import org.simpleflatmapper.map.property.RenameProperty;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ListCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring JDBCTemplate with simpleflatmapper implementation of the DataRepository
 *
 */
@Repository
public class SfmJDBCDataRepositoryImpl implements DataRepository {
    private static final Logger logger = LoggerFactory.getLogger(SfmJDBCDataRepositoryImpl.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final JdbcTemplateCrud<Company, Integer> companyCrud;
    private final JdbcTemplateCrud<Project, Integer> projectCrud;
    private final JdbcTemplateCrud<Employee, Integer> employeeCrud;
    private final JdbcTemplateCrud<Department, Integer> departmentCrud;

    private final RowMapper<Company> companyMapper;
    private final RowMapper<Department> departmentMapper;
    private final RowMapper<Employee> employeeMapper;
    private final RowMapper<ProjectsWithCostsGreaterThanOutput> projectsWithCostsGreaterThanOutputRowMapper;
    private final RowMapper<RegisterEmployeeOutput> registerEmployeeOutputRowMapper;

    private final SqlParameterSourceFactory<RegisterEmployeeInput> registerEmployeeInputSqlParameterSourceFactory;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public SfmJDBCDataRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        JdbcOperations jdbcOperations = jdbcTemplate.getJdbcOperations();

        JdbcTemplateMapperFactory mapperFactory = JdbcTemplateMapperFactory
                .newInstance();


        companyCrud =
                mapperFactory
                        .crud(Company.class, Integer.class)
                        .to(jdbcOperations, "company");
        employeeCrud =
                mapperFactory
                        .crud(Employee.class, Integer.class)
                        .to(jdbcOperations, "employee");


        departmentCrud =
                mapperFactory
                        .crud(Department.class, Integer.class)
                        .to(jdbcOperations, "department");

        projectCrud =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .addAlias("datestarted", "date")
                        .crud(Project.class, Integer.class)
                        .to(jdbcOperations, "project");

        companyMapper =
                mapperFactory.newRowMapper(Company.class);

        departmentMapper = mapperFactory.newRowMapper(Department.class);
        employeeMapper = mapperFactory.newRowMapper(Employee.class);
        projectsWithCostsGreaterThanOutputRowMapper = mapperFactory.newRowMapper(ProjectsWithCostsGreaterThanOutput.class);
        registerEmployeeOutputRowMapper = JdbcTemplateMapperFactory
                .newInstance()
                .addColumnProperty(
                        k -> k.getName().contains("id"),
                        k -> new RenameProperty(k.getName().replace("id", "pid")))
                .newRowMapper(RegisterEmployeeOutput.class);

        registerEmployeeInputSqlParameterSourceFactory = mapperFactory.newSqlParameterSourceFactory(RegisterEmployeeInput.class);
    }

    @Override
    public Company findCompany(Integer pid) {
        logger.info("Finding Company by ID using companyCrud");

        Company company = companyCrud.read(pid);

        logger.info("Found company: " + company);

        return company;
    }

    @Override
    public Company findCompanyUsingSimpleStaticStatement(Integer pid) {
        String query;
        query = "SELECT pid, address, name " +
                "FROM company " +
                "WHERE pid = " + pid;


        Company company = jdbcTemplate.getJdbcOperations().queryForObject(query, companyMapper);

        logger.info("Found company: " + company);

        return company;
    }

    @Override
    public void removeProject(Integer pid) {
        projectCrud.delete(pid);
    }

    @Override
    public Department findDepartment(Integer pid) {
        return departmentCrud.read(pid);
    }

    @Override
    public List<Employee> employeesWithSalaryGreaterThan(Integer minSalary) {
        logger.info("Looking for employeesWithSalaryGreaterThan using JDBCTemplate");

        String query = "SELECT * " +
                "           FROM employee" +
                "           WHERE salary > :salary";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("salary", minSalary);

        // using BeanPropertyRowMapper is easier, but with much worse performance than custom RowMapper
        return jdbcTemplate.query(query, params, employeeMapper);
    }

    @Override
    public Integer insertProject(Project project) {
        return projectCrud.create(project, new CheckedConsumer<Integer>() {
            Integer key;
            @Override
            public void accept(Integer integer) throws Exception {
               this.key = integer;
            }
        }).key;
    }

    @Override
    public List<Integer> insertProjects(List<Project> projects) {
        return projectCrud.create(projects, new ListCollector<Integer>()).getList();
    }

    @Override
    public void updateEmployee(Employee employeeToUpdate) {
        logger.info("Updating employee using JDBC Template");
        employeeCrud.update(employeeToUpdate);
    }

    @Override
    public List<ProjectsWithCostsGreaterThanOutput> getProjectsWithCostsGreaterThan(int totalCostBoundary) {
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
                "SELECT project_name, total_cost, company_name, sum(monthly_cost) company_cost FROM project_info\n" +
                "  JOIN project_cost USING (project_pid)\n" +
                "WHERE total_cost > :totalCostBoundary\n" +
                "GROUP BY project_name, total_cost, company_name\n" +
                "ORDER BY company_name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("totalCostBoundary", totalCostBoundary);

        return jdbcTemplate.query(query, params, projectsWithCostsGreaterThanOutputRowMapper);
    }

    @Override
    public Employee findEmployee(Integer pid) {
        return employeeCrud.read(pid);
    }

    @Override
    public RegisterEmployeeOutput callRegisterEmployee(String name, String surname, String email, BigDecimal salary, String departmentName, String companyName) {
        String query;
        //noinspection SqlResolve
        query = "SELECT employee_id, department_id, company_id FROM register_employee(\n" +
                "  _name := :name, \n" +
                "  _surname := :surname, \n" +
                "  _email := :email, \n" +
                "  _salary := :salary, \n" +
                "  _department_name := :departmentName, \n" +
                "  _company_name := :companyName\n" +
                ")";

        SqlParameterSource params =
                registerEmployeeInputSqlParameterSourceFactory
                    .newSqlParameterSource(
                            new RegisterEmployeeInput(name, surname, email, salary, departmentName, companyName));

        return jdbcTemplate.queryForObject(query, params, registerEmployeeOutputRowMapper);
    }

    @Override
    public Integer getProjectsCount() {
        String query = "SELECT count(*) FROM project";
        return jdbcTemplate.getJdbcOperations().queryForObject(query, Integer.class);
    }

    @Override
    public List<Department> findDepartmentsOfCompany(Company company) {
        String query = "SELECT pid, company_pid, name" +
                "           FROM department " +
                "           WHERE company_pid = :pid" +
                "           ORDER BY pid";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("pid", company.getPid());

        return jdbcTemplate.query(query, params, departmentMapper);
    }

    @Override
    public void deleteDepartments(List<Department> departmentsToDelete) {
        List<Integer> keys = departmentsToDelete.stream()
                .map(Department::getPid)
                .collect(Collectors.toList());

        departmentCrud.delete(keys);
    }

    @Override
    public void updateDepartments(List<Department> departmentsToUpdate) {
        departmentCrud.update(departmentsToUpdate);
    }

    @Override
    public void insertDepartments(List<Department> departmentsToInsert) {
        departmentCrud.create(departmentsToInsert);
    }

    @Override
    public Project findProject(Integer pid) {
        return projectCrud.read(pid);
    }
}
