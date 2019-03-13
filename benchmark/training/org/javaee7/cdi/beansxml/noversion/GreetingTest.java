package org.javaee7.cdi.beansxml.noversion;


import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexis Hassler
 */
@RunWith(Arquillian.class)
public class GreetingTest {
    @Inject
    AnnotatedBean annotatedBean;

    @Inject
    NotAnnotatedBean notAnnotatedBean;

    @Test
    public void should_bean_be_injected() throws Exception {
        Assert.assertThat(annotatedBean, CoreMatchers.is(CoreMatchers.notNullValue()));
        // notAnnotatedBean is injected because CDI acts as version 1.0 if version is not explicit
        Assert.assertThat(notAnnotatedBean, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

