package com.baeldung.passwordhashing;


import java.security.SecureRandom;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by PhysicsSam on 06-Sep-18.
 */
public class SHA512HasherUnitTest {
    private SHA512Hasher hasher;

    private SecureRandom secureRandom;

    @Test
    public void givenSamePasswordAndSalt_whenHashed_checkResultingHashesAreEqual() throws Exception {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String hash1 = hasher.hash("password", salt);
        String hash2 = hasher.hash("password", salt);
        Assert.assertEquals(hash1, hash2);
    }

    @Test
    public void givenSamePasswordAndDifferentSalt_whenHashed_checkResultingHashesNotEqual() throws Exception {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String hash1 = hasher.hash("password", salt);
        // generate a second salt
        byte[] secondSalt = new byte[16];
        String hash2 = hasher.hash("password", secondSalt);
        Assert.assertNotEquals(hash1, hash2);
    }

    @Test
    public void givenPredefinedHash_whenCorrectAttemptGiven_checkAuthenticationSucceeds() throws Exception {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String originalHash = hasher.hash("password123", salt);
        Assert.assertTrue(hasher.checkPassword(originalHash, "password123", salt));
    }

    @Test
    public void givenPredefinedHash_whenIncorrectAttemptGiven_checkAuthenticationFails() throws Exception {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String originalHash = hasher.hash("password123", salt);
        Assert.assertFalse(hasher.checkPassword(originalHash, "password124", salt));
    }
}

