package org.activiti.spring.test.engine;


import org.activiti.engine.ProcessEngines;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Spring process engine base test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/engine/springProcessEngine-context.xml")
public class SpringProcessEngineTest {
    @Test
    public void testGetEngineFromCache() {
        Assert.assertNotNull(ProcessEngines.getDefaultProcessEngine());
        Assert.assertNotNull(ProcessEngines.getProcessEngine("default"));
    }
}

