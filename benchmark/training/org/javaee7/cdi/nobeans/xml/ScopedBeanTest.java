package org.javaee7.cdi.nobeans.xml;


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
public class ScopedBeanTest {
    @Inject
    ScopedBean bean;

    @Test
    public void should_scope_bean_be_injected() throws Exception {
        Assert.assertThat(bean, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

