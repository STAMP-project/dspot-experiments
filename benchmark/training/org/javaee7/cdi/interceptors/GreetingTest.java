package org.javaee7.cdi.interceptors;


import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
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
public class GreetingTest {
    @Inject
    Greeting bean;

    @Test
    public void test() throws Exception {
        Assert.assertThat(bean, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(bean, CoreMatchers.instanceOf(SimpleGreeting.class));
        bean.setGreet("Arun");
        Assert.assertEquals(bean.getGreet(), "Hi Arun !");
    }
}

