package com.baeldung.dubbo;


import com.baeldung.dubbo.remote.GreetingsService;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.ToLongFunction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author aiet
 */
public class ClusterDynamicLoadBalanceLiveTest {
    private ExecutorService executorService;

    @Test
    public void givenProviderCluster_whenConsumerSaysHi_thenResponseBalanced() throws InterruptedException {
        ClassPathXmlApplicationContext localContext = new ClassPathXmlApplicationContext("cluster/consumer-app-lb.xml");
        localContext.start();
        GreetingsService greetingsService = ((GreetingsService) (localContext.getBean("greetingsService")));
        List<Long> elapseList = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            long current = System.currentTimeMillis();
            String hiMessage = greetingsService.sayHi("baeldung");
            Assert.assertNotNull(hiMessage);
            elapseList.add(((System.currentTimeMillis()) - current));
            TimeUnit.SECONDS.sleep(1);
        }
        OptionalDouble avgElapse = elapseList.stream().mapToLong(( e) -> e).average();
        Assert.assertTrue(avgElapse.isPresent());
        Assert.assertTrue(((avgElapse.getAsDouble()) > 1666.0));
    }
}

