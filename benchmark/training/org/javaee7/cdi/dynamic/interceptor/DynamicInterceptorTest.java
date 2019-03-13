package org.javaee7.cdi.dynamic.interceptor;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class DynamicInterceptorTest {
    @Inject
    private MyBean myBean;

    @Test
    public void test() {
        Assert.assertEquals("Hello, John", myBean.getName());
    }
}

