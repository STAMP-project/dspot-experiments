package org.javaee7.cdi.alternatives.priority;


import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
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
public class MixedGreetingTest {
    @Inject
    BeanManager beanManager;

    @Test
    public void should_be_ambiguous() throws Exception {
        Set<Bean<?>> beans = beanManager.getBeans(Greeting.class);
        Assert.assertTrue(((beans.size()) == 2));
    }
}

