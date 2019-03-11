package org.baeldung.aspectj;


import org.junit.Assert;
import org.junit.Test;


public class AccountUnitTest {
    private Account account;

    @Test
    public void givenBalance20AndMinBalance10_whenWithdraw5_thenSuccess() {
        Assert.assertTrue(account.withdraw(5));
    }

    @Test
    public void givenBalance20AndMinBalance10_whenWithdraw100_thenFail() {
        Assert.assertFalse(account.withdraw(100));
    }
}

