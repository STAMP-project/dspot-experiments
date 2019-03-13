package org.javaee7.cdi.bean.discovery.none;


import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class GreetingTest {
    @Inject
    BeanManager beanManager;

    @Test
    public void should_bean_be_injected() throws Exception {
        // Cannot try to inject the bean because it would fail at deployment time (in WildFly 8)
        Set<Bean<?>> beans = beanManager.getBeans(Greeting.class);
        Assert.assertThat(beans, CoreMatchers.is(empty()));
    }
}

