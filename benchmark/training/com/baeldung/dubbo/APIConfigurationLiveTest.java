package com.baeldung.dubbo;


import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.baeldung.dubbo.remote.GreetingsService;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author aiet
 */
public class APIConfigurationLiveTest {
    @Test
    public void givenProviderConsumer_whenSayHi_thenGotResponse() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("demo-consumer");
        application.setVersion("1.0");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("multicast://224.1.1.1:9090");
        ReferenceConfig<GreetingsService> reference = new ReferenceConfig();
        reference.setApplication(application);
        reference.setRegistry(registryConfig);
        reference.setInterface(GreetingsService.class);
        GreetingsService greetingsService = reference.get();
        String hiMessage = greetingsService.sayHi("baeldung");
        Assert.assertEquals("hi, baeldung", hiMessage);
    }
}

