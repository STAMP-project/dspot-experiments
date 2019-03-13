package org.javaee7.cdi.beanmanager;


import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Radim Hanus
 */
@RunWith(Arquillian.class)
public class GreetingTest {
    // First way to get BeanManager
    @Inject
    private BeanManager bm;

    @Test
    public void testInject() throws Exception {
        test(this.bm);
    }

    @Test
    public void testCurrent() throws Exception {
        // Second way to get BeanManager: current CDI container
        BeanManager bm = CDI.current().getBeanManager();
        test(bm);
    }

    @Test
    public void testJNDI() throws Exception {
        // Third way to get BeanManager: name service
        BeanManager bm = InitialContext.doLookup("java:comp/BeanManager");
        test(bm);
    }
}

