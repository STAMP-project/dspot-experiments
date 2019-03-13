package org.javaee7.jta.transactional;


import javax.inject.Inject;
import javax.transaction.TransactionalException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexis Hassler
 */
@RunWith(Arquillian.class)
public class MyTransactionalTxTypeBeanTest {
    @Inject
    MyTransactionalTxTypeBean bean;

    @Test
    public void should_required_work() {
        bean.required();
    }

    @Test
    public void should_requiresNew_work() {
        bean.requiresNew();
    }

    @Test(expected = TransactionalException.class)
    public void should_mandatory_throw_exception() {
        bean.mandatory();
    }

    @Test
    public void should_supports_work() {
        bean.supports();
    }

    @Test
    public void should_notSupported_work() {
        bean.notSupported();
    }

    @Test
    public void should_never_work() {
        bean.never();
    }
}

