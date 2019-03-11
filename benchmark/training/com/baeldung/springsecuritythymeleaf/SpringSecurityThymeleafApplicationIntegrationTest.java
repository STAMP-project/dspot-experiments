package com.baeldung.springsecuritythymeleaf;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringSecurityThymeleafApplicationIntegrationTest {
    @Autowired
    ViewController viewController;

    @Autowired
    WebApplicationContext wac;

    @Test
    public void whenConfigured_thenLoadsContext() {
        Assert.assertNotNull(viewController);
        Assert.assertNotNull(wac);
    }
}

