package org.baeldung.persistence.service;


import org.apache.commons.lang3.RandomStringUtils;
import org.baeldung.persistence.model.Foo;
import org.baeldung.spring.PersistenceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class }, loader = AnnotationConfigContextLoader.class)
public class FooServicePersistenceIntegrationTest extends AbstractServicePersistenceIntegrationTest<Foo> {
    @Autowired
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
    public final void whenInvalidEntityIsCreated_thenDataException() {
        service.create(new Foo());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public final void whenEntityWithLongNameIsCreated_thenDataException() {
        service.create(new Foo(RandomStringUtils.randomAlphabetic(2048)));
    }

    // custom Query method
    @Test
    public final void givenUsingCustomQuery_whenRetrievingEntity_thenFound() {
        final String name = RandomStringUtils.randomAlphabetic(6);
        service.create(new Foo(name));
        final Foo retrievedByName = service.retrieveByName(name);
        Assert.assertNotNull(retrievedByName);
    }
}

