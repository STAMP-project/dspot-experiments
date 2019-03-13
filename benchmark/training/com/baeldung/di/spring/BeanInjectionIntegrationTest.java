package com.baeldung.di.spring;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


public class BeanInjectionIntegrationTest {
    private ApplicationContext applicationContext;

    @Test
    public void singletonBean_getBean_returnsSingleInstance() {
        final IndexApp indexApp1 = applicationContext.getBean("indexApp", IndexApp.class);
        final IndexApp indexApp2 = applicationContext.getBean("indexApp", IndexApp.class);
        Assert.assertEquals(indexApp1, indexApp2);
    }

    @Test
    public void getBean_returnsInstance() {
        final IndexApp indexApp = applicationContext.getBean("indexApp", IndexApp.class);
        Assert.assertNotNull(indexApp);
    }
}

