package com.baeldung.creational.builder;


import org.junit.Assert;
import org.junit.Test;


public class BuilderPatternIntegrationTest {
    @Test
    public void whenCreatingObjectThroughBuilder_thenObjectValid() {
        BankAccount newAccount = new BankAccount.BankAccountBuilder("Jon", "22738022275").withEmail("jon@example.com").wantNewsletter(true).build();
        Assert.assertEquals(newAccount.getName(), "Jon");
        Assert.assertEquals(newAccount.getAccountNumber(), "22738022275");
        Assert.assertEquals(newAccount.getEmail(), "jon@example.com");
        Assert.assertEquals(newAccount.isNewsletter(), true);
    }

    @Test
    public void whenSkippingOptionalParameters_thenObjectValid() {
        BankAccount newAccount = new BankAccount.BankAccountBuilder("Jon", "22738022275").build();
        Assert.assertEquals(newAccount.getName(), "Jon");
        Assert.assertEquals(newAccount.getAccountNumber(), "22738022275");
        Assert.assertEquals(newAccount.getEmail(), null);
        Assert.assertEquals(newAccount.isNewsletter(), false);
    }
}

