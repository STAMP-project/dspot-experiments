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
public class MulticastRegistryLiveTest {
    private ClassPathXmlApplicationContext remoteContext;

    @Test
    public void givenProvider_whenConsumerSaysHi_thenGotResponse() {
        ClassPathXmlApplicationContext localContext = new ClassPathXmlApplicationContext("multicast/consumer-app.xml");
        localContext.start();
        GreetingsService greetingsService = ((GreetingsService) (localContext.getBean("greetingsService")));
        String hiMessage = greetingsService.sayHi("baeldung");
        Assert.assertNotNull(hiMessage);
        Assert.assertEquals("hi, baeldung", hiMessage);
    }
}

