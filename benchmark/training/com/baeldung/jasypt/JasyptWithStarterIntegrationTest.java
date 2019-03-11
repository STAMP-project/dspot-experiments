package com.baeldung.jasypt;


import com.baeldung.jasypt.starter.PropertyServiceForJasyptStarter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JasyptWithStarterIntegrationTest {
    @Autowired
    ApplicationContext appCtx;

    @Test
    public void whenDecryptedPasswordNeeded_GetFromService() {
        System.setProperty("jasypt.encryptor.password", "password");
        PropertyServiceForJasyptStarter service = appCtx.getBean(PropertyServiceForJasyptStarter.class);
        Assert.assertEquals("Password@1", service.getProperty());
        Environment environment = appCtx.getBean(Environment.class);
        Assert.assertEquals("Password@1", service.getPasswordUsingEnvironment(environment));
    }
}

