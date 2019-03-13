package org.axonframework.springboot;


import junit.framework.TestCase;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Tests JPA auto-configuration
 *
 * @author Sara Pellegrini
 */
@ContextConfiguration
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class JpaAutoConfigurationTest {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private SagaStore sagaStore;

    @Test
    public void testContextInitialization() {
        TestCase.assertTrue(((tokenStore) instanceof JpaTokenStore));
        TestCase.assertTrue(((sagaStore) instanceof JpaSagaStore));
    }
}

