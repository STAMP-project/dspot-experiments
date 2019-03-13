package org.javaee7.interceptor.aroundconstruct;


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
public class GreetingBeanTest {
    @Inject
    private Greeting bean;

    @Test
    public void should_be_ready() throws Exception {
        Assert.assertThat(bean, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(bean, CoreMatchers.instanceOf(GreetingBean.class));
        Assert.assertTrue(bean.isConstructed());
        Assert.assertTrue(bean.isInitialized());
        Assert.assertThat(bean.getParam(), CoreMatchers.instanceOf(GreetingParam.class));
    }
}

