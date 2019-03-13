package com.baeldung.cxf.spring;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class StudentLiveTest {
    private ApplicationContext context = new AnnotationConfigApplicationContext(ClientConfiguration.class);

    private Baeldung baeldungProxy = ((Baeldung) (context.getBean("client")));

    @Test
    public void whenUsingHelloMethod_thenCorrect() {
        String response = baeldungProxy.hello("John Doe");
        Assert.assertEquals("Hello John Doe!", response);
    }

    @Test
    public void whenUsingRegisterMethod_thenCorrect() {
        Student student1 = new Student("Adam");
        Student student2 = new Student("Eve");
        String student1Response = baeldungProxy.register(student1);
        String student2Response = baeldungProxy.register(student2);
        Assert.assertEquals("Adam is registered student number 1", student1Response);
        Assert.assertEquals("Eve is registered student number 2", student2Response);
    }
}

