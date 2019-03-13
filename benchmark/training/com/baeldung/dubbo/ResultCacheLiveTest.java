package com.baeldung.dubbo;


import com.baeldung.dubbo.remote.GreetingsService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author aiet
 */
public class ResultCacheLiveTest {
    private ClassPathXmlApplicationContext remoteContext;

    @Test
    public void givenProvider_whenConsumerSaysHi_thenGotResponse() {
        ClassPathXmlApplicationContext localContext = new ClassPathXmlApplicationContext("multicast/consumer-app.xml");
        localContext.start();
        GreetingsService greetingsService = ((GreetingsService) (localContext.getBean("greetingsService")));
        long before = System.currentTimeMillis();
        String hiMessage = greetingsService.sayHi("baeldung");
        long timeElapsed = (System.currentTimeMillis()) - before;
        Assert.assertTrue((timeElapsed > 5000));
        Assert.assertNotNull(hiMessage);
        Assert.assertEquals("hi, baeldung", hiMessage);
        before = System.currentTimeMillis();
        hiMessage = greetingsService.sayHi("baeldung");
        timeElapsed = (System.currentTimeMillis()) - before;
        Assert.assertTrue((timeElapsed < 1000));
        Assert.assertNotNull(hiMessage);
        Assert.assertEquals("hi, baeldung", hiMessage);
    }
}

