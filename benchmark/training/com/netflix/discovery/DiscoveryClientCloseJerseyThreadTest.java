package com.netflix.discovery;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DiscoveryClientCloseJerseyThreadTest extends AbstractDiscoveryClientTester {
    private static final String THREAD_NAME = "Eureka-JerseyClient-Conn-Cleaner";

    @Test
    public void testThreadCount() throws InterruptedException {
        Assert.assertThat(containsJerseyThread(), CoreMatchers.equalTo(true));
        client.shutdown();
        // Give up control for cleaner thread to die
        Thread.sleep(5);
        Assert.assertThat(containsJerseyThread(), CoreMatchers.equalTo(false));
    }
}

