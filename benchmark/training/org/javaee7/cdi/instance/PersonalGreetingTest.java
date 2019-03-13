package org.javaee7.cdi.instance;


import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Radim Hanus
 */
@RunWith(Arquillian.class)
public class PersonalGreetingTest {
    /**
     * Qualifier @Personal is not qualifying any bean.
     */
    @Inject
    @Personal
    private Instance<Greeting> instance;

    @Test
    public void test() throws Exception {
        // no instance should be available
        Assert.assertTrue(instance.isUnsatisfied());
    }
}

