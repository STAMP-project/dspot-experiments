package org.javaee7.cdi.interceptors.priority;


import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Note that beans.xml doesn't define any interceptor. Interceptors declared using interceptor bindings
 * are enabled for the entire application and ordered using the Priority annotation.
 *
 * @author Radim Hanus
 */
@RunWith(Arquillian.class)
public class GreetingTest {
    @Inject
    Greeting bean;

    @Test
    public void test() throws Exception {
        Assert.assertThat(bean, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(bean, CoreMatchers.instanceOf(SimpleGreeting.class));
        bean.setGreet("Arun");
        Assert.assertEquals(bean.getGreet(), "Hi Arun ! Nice to meet you.");
    }
}

