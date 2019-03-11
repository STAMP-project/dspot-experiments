package com.baeldung.aop;


import com.baeldung.config.TestConfig;
import com.baeldung.dao.FooDao;
import com.baeldung.model.Foo;
import java.util.List;
import java.util.logging.Handler;
import java.util.regex.Pattern;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class AopLoggingIntegrationTest {
    @Autowired
    private FooDao dao;

    private Handler logEventHandler;

    private List<String> messages;

    @Test
    public void givenLoggingAspect_whenCallDaoMethod_thenBeforeAdviceIsCalled() {
        dao.findById(1L);
        Assert.assertThat(messages, Matchers.hasSize(1));
        String logMessage = messages.get(0);
        Pattern pattern = Pattern.compile("^\\[\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2}:\\d{3}\\]findById$");
        Assert.assertTrue(pattern.matcher(logMessage).matches());
    }

    @Test
    public void givenLoggingAspect_whenCallLoggableAnnotatedMethod_thenMethodIsLogged() {
        dao.create(42L, "baz");
        Assert.assertThat(messages, IsCollectionContaining.hasItem("Executing method: create"));
    }

    @Test
    public void givenLoggingAspect_whenCallMethodAcceptingAnnotatedArgument_thenArgumentIsLogged() {
        Foo foo = new Foo(42L, "baz");
        dao.merge(foo);
        Assert.assertThat(messages, IsCollectionContaining.hasItem(("Accepting beans with @Entity annotation: " + foo)));
    }
}

