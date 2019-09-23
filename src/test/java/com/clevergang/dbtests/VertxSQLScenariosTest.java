package com.clevergang.dbtests;

import com.clevergang.dbtests.repository.api.data.Company;
import com.clevergang.dbtests.repository.api.data.Employee;
import com.clevergang.dbtests.repository.api.data.RegisterEmployeeOutput;
import com.clevergang.dbtests.repository.impl.jdbi.JDBIDataRepositoryImpl;
import com.clevergang.dbtests.repository.impl.vertxsql.VertxSQLDataRepositoryImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = DbTestsApplication.class)
public class VertxSQLScenariosTest {

    @Autowired
    private VertxSQLDataRepositoryImpl vertxSQLDataRepository;

    private Scenarios scenarios;

    @Before
    public void setup() {
        vertxSQLDataRepository.begin();
        scenarios = new Scenarios(vertxSQLDataRepository);
    }

    @After
    public void dispose() {
        vertxSQLDataRepository.rollback();
    }

    @Test
    public void scenarioOne() {
        scenarios.fetchSingleEntityScenario(1);
    }

    @Test
    public void scenarioTwo() {
        scenarios.fetchListOfEntitiesScenario(25000);
    }

    @Test
    public void scenarioThree() {
        scenarios.saveNewEntityScenario();
    }

    @Test
    public void scenarioFour() {
        scenarios.batchInsertMultipleEntitiesScenario();
    }

    @Test
    public void scenarioFive() {
        scenarios.updateCompleteEntityScenario();
    }

    @Test
    public void scenarioSix() {
        scenarios.fetchManyToOneRelationScenario();
    }

    @Test
    public void scenarioSeven() {
        scenarios.fetchOneToManyRelationScenario();
    }

    @Test
    public void scenarioEight() {
        scenarios.updateCompleteOneToManyRelationScenario();
    }

    @Test
    public void scenarioNine() {
        scenarios.executeComplexSelectScenario();
    }

    @Test
    public void scenarioTen() {
        scenarios.callStoredProcedureScenario();
    }

    @Test
    public void scenarioEleven() {
        scenarios.executeSimpleStaticStatementScenario();
    }

    @Test
    public void scenarioTwelve() {
        scenarios.removeSingleEntityScenario();
    }
}



