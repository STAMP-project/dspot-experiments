package org.javaee7.jta.transaction.scope;


import javax.enterprise.context.ContextNotActiveException;
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
public class MyTransactionalBeanTest {
    @Inject
    MyTransactionalBean bean;

    @Test
    public void should_withTransaction_have_only_one_instance_injected() {
        bean.withTransaction();
        Assert.assertThat("bean1 and bean2 should the same object", bean.id1, CoreMatchers.is(bean.id2));
    }

    @Test
    public void should_withTransaction_called_twice_have_different_instances_injected() {
        bean.withTransaction();
        String firstId1 = bean.id1;
        bean.withTransaction();
        String secondId1 = bean.id1;
        Assert.assertThat("bean1 should change between scenarios", firstId1, CoreMatchers.is(CoreMatchers.not(secondId1)));
    }

    @Test
    public void should_withoutTransaction_fail() {
        try {
            bean.withoutTransaction();
            Assert.fail("No ContextNotActiveException");
        } catch (ContextNotActiveException e) {
        }
    }
}

