package com.baeldung.passwordhashing;


import org.junit.Assert;
import org.junit.Test;


public class PBKDF2HasherUnitTest {
    private PBKDF2Hasher mPBKDF2Hasher;

    @Test
    public void givenCorrectMessageAndHash_whenAuthenticated_checkAuthenticationSucceeds() throws Exception {
        String message1 = "password123";
        String hash1 = mPBKDF2Hasher.hash(message1.toCharArray());
        Assert.assertTrue(mPBKDF2Hasher.checkPassword(message1.toCharArray(), hash1));
    }

    @Test
    public void givenWrongMessage_whenAuthenticated_checkAuthenticationFails() throws Exception {
        String message1 = "password123";
        String hash1 = mPBKDF2Hasher.hash(message1.toCharArray());
        String wrongPasswordAttempt = "IamWrong";
        Assert.assertFalse(mPBKDF2Hasher.checkPassword(wrongPasswordAttempt.toCharArray(), hash1));
    }
}

