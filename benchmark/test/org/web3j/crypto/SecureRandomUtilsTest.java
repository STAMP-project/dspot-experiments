package org.web3j.crypto;


import org.junit.Assert;
import org.junit.Test;


public class SecureRandomUtilsTest {
    @Test
    public void testSecureRandom() {
        SecureRandomUtils.secureRandom().nextInt();
    }

    @Test
    public void testIsNotAndroidRuntime() {
        Assert.assertFalse(SecureRandomUtils.isAndroidRuntime());
    }
}

