package org.javaee7.cdi.alternatives;


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
    public void should_bean_be_fancy() throws Exception {
        // because it is declared as the alternative in beans.xml
        Assert.assertThat(bean, CoreMatchers.instanceOf(FancyGreeting.class));
    }
}

