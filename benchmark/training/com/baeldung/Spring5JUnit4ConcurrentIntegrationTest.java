package com.baeldung;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Spring5JUnit4ConcurrentIntegrationTest.SimpleConfiguration.class)
public class Spring5JUnit4ConcurrentIntegrationTest implements InitializingBean , ApplicationContextAware {
    @Configuration
    public static class SimpleConfiguration {}

    private ApplicationContext applicationContext;

    private boolean beanInitialized = false;

    @Test
    public final void verifyApplicationContextSet() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        Assert.assertNotNull("The application context should have been set due to ApplicationContextAware semantics.", this.applicationContext);
    }

    @Test
    public final void verifyBeanInitialized() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        Assert.assertTrue("This test bean should have been initialized due to InitializingBean semantics.", this.beanInitialized);
    }
}

