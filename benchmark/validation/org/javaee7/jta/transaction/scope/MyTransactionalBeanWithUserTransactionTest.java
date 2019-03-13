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
public class MyTransactionalBeanWithUserTransactionTest {
    @Inject
    MyTransactionalBean bean;

    @Inject
    UserTransaction ut;

    @Test
    public void should_withTransaction_have_only_one_instance_injected() throws Exception {
        ut.begin();
        bean.withTransaction();
        ut.commit();
        Assert.assertThat("bean1 and bean2 should the same object", bean.id1, CoreMatchers.is(bean.id2));
    }

    @Test
    public void should_withTransaction_called_twice_have_same_instances_injected() throws Exception {
        ut.begin();
        bean.withTransaction();
        String firstId1 = bean.id1;
        bean.withTransaction();
        String secondId1 = bean.id1;
        ut.commit();
        Assert.assertThat("bean1 should change between scenarios", firstId1, CoreMatchers.is(secondId1));
    }

    @Test
    public void should_withoutTransaction_NOT_fail() throws Exception {
        try {
            ut.begin();
            bean.withoutTransaction();
            ut.commit();
        } catch (ContextNotActiveException e) {
            Assert.fail(e.toString());
        }
    }
}

