package com.baeldung.beanfactory;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class BeanFactoryWithClassPathResourceIntegrationTest {
    @Test
    public void createBeanFactoryAndCheckEmployeeBean() {
        Resource res = new ClassPathResource("beanfactory-example.xml");
        BeanFactory factory = new org.springframework.beans.factory.xml.XmlBeanFactory(res);
        Employee emp = ((Employee) (factory.getBean("employee")));
        Assert.assertTrue(factory.isSingleton("employee"));
        Assert.assertTrue(((factory.getBean("employee")) instanceof Employee));
        Assert.assertTrue(factory.isTypeMatch("employee", Employee.class));
        Assert.assertTrue(((factory.getAliases("employee").length) > 0));
    }
}

