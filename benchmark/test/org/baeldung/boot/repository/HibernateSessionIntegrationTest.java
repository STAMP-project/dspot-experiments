package org.baeldung.boot.repository;


import org.baeldung.boot.DemoApplicationIntegrationTest;
import org.baeldung.demo.model.Foo;
import org.baeldung.demo.repository.FooRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class HibernateSessionIntegrationTest extends DemoApplicationIntegrationTest {
    @Autowired
    private FooRepository fooRepository;

    @Test
    public void whenSavingWithCurrentSession_thenThrowNoException() {
        Foo foo = new Foo("Exception Solved");
        fooRepository.save(foo);
        foo = null;
        foo = fooRepository.findByName("Exception Solved");
        Assert.assertThat(foo, CoreMatchers.notNullValue());
        Assert.assertThat(foo.getId(), CoreMatchers.is(1));
        Assert.assertThat(foo.getName(), CoreMatchers.is("Exception Solved"));
    }
}

