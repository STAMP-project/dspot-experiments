package com.baeldung.definition;


import com.baeldung.definition.domain.Company;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class SpringBeanIntegrationTest {
    @Test
    public void whenUsingIoC_thenDependenciesAreInjected() {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Company company = context.getBean("company", Company.class);
        Assert.assertEquals("High Street", company.getAddress().getStreet());
        Assert.assertEquals(1000, company.getAddress().getNumber());
    }
}

