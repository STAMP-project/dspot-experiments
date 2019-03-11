package org.activiti.spring.test.components.scope;


import org.activiti.engine.ProcessEngine;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * tests the scoped beans
 */
// Ignored for the moment. Josh is working on this.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/activiti/spring/test/components/ScopingTests-context.xml")
@Ignore
public class XmlNamespaceProcessScopeTest {
    private ProcessScopeTestEngine processScopeTestEngine;

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void testScopedProxyCreation() throws Throwable {
        processScopeTestEngine.testScopedProxyCreation();
    }
}

