package org.javaee7.cdi.bean.discovery.annotated;


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
    public void should_bean_be_simple() throws Exception {
        // because SimpleGreeting is annotated (scope)
        Assert.assertThat(bean, CoreMatchers.instanceOf(SimpleGreeting.class));
    }
}

