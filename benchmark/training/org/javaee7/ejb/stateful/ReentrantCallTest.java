package org.javaee7.ejb.stateful;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that a stateful bean is capable of calling a method via
 * a business proxy on itself.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class ReentrantCallTest {
    @Inject
    private ReentrantStatefulBean reentrantStatefulBean;

    @Test
    public void doReentrantCall() {
        // initialMethod() will internally call another method on itself.
        // This should not throw an exception. See e.g. https://issues.apache.org/jira/browse/OPENEJB-1099
        reentrantStatefulBean.initialMethod();
    }
}

