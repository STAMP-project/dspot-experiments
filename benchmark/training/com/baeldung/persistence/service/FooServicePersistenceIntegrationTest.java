package com.baeldung.persistence.service;


import com.baeldung.persistence.model.Foo;
import com.baeldung.spring.config.PersistenceTestConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceTestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class FooServicePersistenceIntegrationTest {
    @Autowired
    @Qualifier("fooHibernateService")
    private IFooService service;

    // tests
    @Test
    public final void whenContextIsBootstrapped_thenNoExceptions() {
        // 
    }

    @Test
    public final void whenEntityIsCreated_thenNoExceptions() {
        service.create(new Foo(RandomStringUtils.randomAlphabetic(6)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public final void whenEntityWithLongNameIsCreated_thenDataException() {
        service.create(new Foo(RandomStringUtils.randomAlphabetic(2048)));
    }

    @Test(expected = DataAccessException.class)
    public final void temp_whenInvalidEntityIsCreated_thenDataException() {
        service.create(new Foo(RandomStringUtils.randomAlphabetic(2048)));
    }
}

