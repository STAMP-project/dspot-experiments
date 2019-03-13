package com.baeldung.hashcode.entities;


import org.junit.Assert;
import org.junit.Test;


public class UserUnitTest {
    private User user;

    private User comparisonUser;

    @Test
    public void equals_EqualUserInstance_TrueAssertion() {
        Assert.assertTrue(user.equals(comparisonUser));
    }

    @Test
    public void hashCode_UserHash_TrueAssertion() {
        Assert.assertEquals(1792276941, user.hashCode());
    }
}

