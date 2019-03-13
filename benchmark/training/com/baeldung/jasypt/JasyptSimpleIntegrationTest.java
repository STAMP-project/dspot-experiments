package com.baeldung.jasypt;


import com.baeldung.jasypt.simple.PropertyServiceForJasyptSimple;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JasyptSimpleIntegrationTest {
    @Autowired
    ApplicationContext appCtx;

    @Test
    public void whenDecryptedPasswordNeeded_GetFromService() {
        System.setProperty("jasypt.encryptor.password", "password");
        PropertyServiceForJasyptSimple service = appCtx.getBean(PropertyServiceForJasyptSimple.class);
        Assert.assertEquals("Password@2", service.getProperty());
    }
}

