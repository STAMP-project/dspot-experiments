package org.jboss.as.test.integration.ejb.transaction.annotation;


import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class AnnotatedTransactionTestCase {
    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void testMethodHasTransaction() throws NamingException, NotSupportedException, SystemException {
        final UserTransaction userTransaction = ((UserTransaction) (new InitialContext().lookup("java:jboss/UserTransaction")));
        final AnnotatedTx bean = ((AnnotatedTx) (initialContext.lookup(((("java:module/" + (AnnotatedTxBean.class.getSimpleName())) + "!") + (AnnotatedTx.class.getName())))));
        userTransaction.begin();
        try {
            Assert.assertEquals(STATUS_ACTIVE, bean.getActiveTransaction());
        } finally {
            userTransaction.rollback();
        }
    }

    @Test
    public void testMethodHasNoTransaction() throws NamingException, NotSupportedException, SystemException {
        final UserTransaction userTransaction = ((UserTransaction) (new InitialContext().lookup("java:jboss/UserTransaction")));
        final AnnotatedTx bean = ((AnnotatedTx) (initialContext.lookup(((("java:module/" + (AnnotatedTxBean.class.getSimpleName())) + "!") + (AnnotatedTx.class.getName())))));
        bean.getNonActiveTransaction();
        Assert.assertEquals(STATUS_NO_TRANSACTION, bean.getNonActiveTransaction());
        try {
            userTransaction.begin();
            bean.getNonActiveTransaction();
            Assert.fail();
        } catch (EJBException e) {
            Assert.assertTrue(true);
        } finally {
            userTransaction.rollback();
        }
    }
}

