package org.javaee7.jta.user.transaction;


import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexis Hassler
 */
@RunWith(Arquillian.class)
public class UserTransactionTest {
    @Inject
    UserTransaction ut;

    @Test
    public void should_work_with_cdi() throws HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        ut.begin();
        ut.commit();
    }

    @Test
    public void should_work_with_jndi() throws NamingException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, RollbackException, SystemException {
        Context context = new InitialContext();
        UserTransaction ut = ((UserTransaction) (context.lookup("java:comp/UserTransaction")));
        ut.begin();
        ut.commit();
    }
}

