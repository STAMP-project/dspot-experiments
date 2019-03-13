package org.javaee7.cdi.alternatives.priority;


import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexis Hassler
 * @author Radim Hanus
 */
@RunWith(Arquillian.class)
public class GreetingTest {
    @Inject
    Greeting bean;

    @Test
    public void should_bean_be_injected() throws Exception {
        Assert.assertThat(bean, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void should_bean_be_priority() throws Exception {
        // because it has the highest priority from Priority annotated alternatives
        Assert.assertThat(bean, CoreMatchers.instanceOf(PriorityGreeting.class));
    }
}

