package org.jboss.as.test.integration.deployment.excludesubsystem;


import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests excluding a subsystem via jboss-deployment-structure.xml
 */
@RunWith(Arquillian.class)
public class ExcludeEjbSubsystemTestCase {
    private static final Logger logger = Logger.getLogger(ExcludeEjbSubsystemTestCase.class);

    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void testEjbNotInstalled() throws NamingException {
        try {
            Object result = initialContext.lookup(("java:module/" + (SimpleEjb.class.getSimpleName())));
            Assert.fail((("Expected lookup to fail, instead " + result) + " was returned"));
        } catch (NameNotFoundException expected) {
        }
    }
}

