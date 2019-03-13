package org.javaee7.ejb.stateless;


import javax.ejb.EJB;
import org.hamcrest.CoreMatchers;
import org.javaee7.ejb.stateless.remote.Account;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 * @author Rafa? Roppel
 */
@RunWith(Arquillian.class)
public class AccountSessionBeanWithInterfaceTest {
    @EJB
    private Account sut;

    /**
     * Test of withdraw method, of class AccountSessionBean.
     */
    @Test
    public void shouldWithdrawGivenAmount() {
        // given
        final float amount = 5.0F;
        // when
        final String actual = sut.withdraw(amount);
        // then
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(("Withdrawn: " + amount))));
    }

    /**
     * Test of deposit method, of class AccountSessionBean.
     */
    @Test
    public void shouldDepositGivenAmount() {
        // given
        final float amount = 10.0F;
        // when
        final String actual = sut.deposit(amount);
        // then
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.equalTo(("Deposited: " + amount))));
    }
}

