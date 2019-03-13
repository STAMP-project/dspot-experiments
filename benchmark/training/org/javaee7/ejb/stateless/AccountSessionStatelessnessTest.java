package org.javaee7.ejb.stateless;


import javax.ejb.EJB;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jakub Marchwicki
 */
@RunWith(Arquillian.class)
public class AccountSessionStatelessnessTest {
    @EJB
    AccountSessionBean account1;

    @EJB
    AccountSessionBean account2;

    /**
     * JSR 318: Enterprise JavaBeans, Version 3.1
     * 3.4.7.2 Session Object Identity / Stateless Session Beans
     *
     * All business object references of the same interface type for the same
     * stateless session bean have the same object identity, which is assigned
     * by the container. All references to the no-interface view of the same
     * stateless session bean have the same object identity.
     */
    @Test
    public void should_be_identical_beans() {
        MatcherAssert.assertThat("Expect same instances", account1, is(account2));
    }
}

