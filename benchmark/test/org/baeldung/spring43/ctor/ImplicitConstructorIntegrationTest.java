package org.baeldung.spring43.ctor;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration("classpath:implicit-ctor-context.xml")
public class ImplicitConstructorIntegrationTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private FooService fooService;

    @Test
    public void whenBeanWithoutAutowiredCtor_thenInjectIntoSingleCtor() {
        Assert.assertNotNull(fooService.getRepository());
    }
}

