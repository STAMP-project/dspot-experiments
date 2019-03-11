package com.baeldung.dubbo;


import com.baeldung.dubbo.remote.GreetingsService;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author aiet
 */
public class ClusterFailoverLiveTest {
    private ExecutorService executorService;

    @Test
    public void givenProviderCluster_whenConsumerSaysHi_thenGotFailoverResponse() {
        ClassPathXmlApplicationContext localContext = new ClassPathXmlApplicationContext("cluster/consumer-app-failtest.xml");
        localContext.start();
        GreetingsService greetingsService = ((GreetingsService) (localContext.getBean("greetingsService")));
        String hiMessage = greetingsService.sayHi("baeldung");
        Assert.assertNotNull(hiMessage);
        Assert.assertEquals("hi, failover baeldung", hiMessage);
    }
}

