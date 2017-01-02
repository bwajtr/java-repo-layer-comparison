package com.clevergang.dbtests;

import com.clevergang.dbtests.repository.impl.jdbctemplate.SfmJDBCDataRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = DbTestsApplication.class)
@Transactional
@Rollback
public class SfmJdbcTemplateScenariosTest {

    @Autowired
    private SfmJDBCDataRepositoryImpl jdbcRepository;

    private Scenarios scenarios;

    @Before
    public void setup() {
        scenarios = new Scenarios(jdbcRepository);
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
