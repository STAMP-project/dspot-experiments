package org.baeldung.boot.repository;


import org.baeldung.boot.DemoApplicationIntegrationTest;
import org.baeldung.demo.model.Foo;
import org.baeldung.demo.repository.FooRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class FooRepositoryIntegrationTest extends DemoApplicationIntegrationTest {
    @Autowired
    private FooRepository fooRepository;

    @Test
    public void testFindByName() {
        Foo foo = fooRepository.findByName("Bar");
        Assert.assertThat(foo, Matchers.notNullValue());
        Assert.assertThat(foo.getId(), Matchers.is(2));
    }
}

