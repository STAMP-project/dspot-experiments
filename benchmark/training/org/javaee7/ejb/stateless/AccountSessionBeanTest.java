package org.javaee7.ejb.stateless;


import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
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
public class AccountSessionBeanTest {
    @Inject
    private AccountSessionBean sut;

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

