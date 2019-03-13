package com.baeldug.groovyconfig;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class JavaConfigurationUnitTest {
    @Test
    public void whenJavaConfig_thenCorrectPerson() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(JavaBeanConfig.class);
        ctx.refresh();
        JavaPersonBean j = ctx.getBean(JavaPersonBean.class);
        Assert.assertEquals("31", j.getAge());
        Assert.assertEquals("green", j.getEyesColor());
        Assert.assertEquals("blond", j.getHairColor());
    }
}

