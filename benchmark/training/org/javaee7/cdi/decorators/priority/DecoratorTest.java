package org.javaee7.cdi.decorators.priority;


import javax.inject.Inject;
import org.hamcrest.core.Is;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Korneliusz Rabczak
 */
@RunWith(Arquillian.class)
public class DecoratorTest {
    @Inject
    private Greeting greeting;

    @Test
    public void test() {
        Assert.assertThat(greeting.greet("Duke"), Is.is("Hello Duke very much!"));
    }
}

