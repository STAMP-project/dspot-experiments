package com.baeldung.hashing;


import org.junit.Assert;
import org.junit.Test;


public class SHA3HashingUnitTest {
    private static String originalValue = "abc123";

    private static String hashedValue = "f58fa3df820114f56e1544354379820cff464c9c41cb3ca0ad0b0843c9bb67ee";

    @Test
    public void testHashWithJavaMessageDigest() throws Exception {
        final String currentHashedValue = SHA3Hashing.hashWithJavaMessageDigest(SHA3HashingUnitTest.originalValue);
        Assert.assertEquals(SHA3HashingUnitTest.hashedValue, currentHashedValue);
    }

    @Test
    public void testHashWithBouncyCastle() {
        final String currentHashedValue = SHA3Hashing.hashWithBouncyCastle(SHA3HashingUnitTest.originalValue);
        Assert.assertEquals(SHA3HashingUnitTest.hashedValue, currentHashedValue);
    }
}

