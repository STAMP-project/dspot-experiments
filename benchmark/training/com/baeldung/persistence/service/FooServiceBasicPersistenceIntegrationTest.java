package com.baeldung.persistence.service;


import com.baeldung.persistence.model.Foo;
import com.baeldung.spring.config.PersistenceTestConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceTestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class FooServiceBasicPersistenceIntegrationTest {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IFooService fooService;

    private Session session;

    // tests
    @Test
    public final void whenContextIsBootstrapped_thenNoExceptions() {
        // 
    }

    @Test
    public final void whenEntityIsCreated_thenNoExceptions() {
        fooService.create(new Foo(RandomStringUtils.randomAlphabetic(6)));
    }
}

