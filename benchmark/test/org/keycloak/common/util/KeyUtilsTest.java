package org.keycloak.common.util;


import java.util.concurrent.ThreadLocalRandom;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Assert;
import org.junit.Test;


public class KeyUtilsTest {
    @Test
    public void loadSecretKey() throws Exception {
        byte[] secretBytes = new byte[32];
        ThreadLocalRandom.current().nextBytes(secretBytes);
        SecretKeySpec expected = new SecretKeySpec(secretBytes, "HmacSHA256");
        SecretKey actual = KeyUtils.loadSecretKey(secretBytes, "HmacSHA256");
        Assert.assertEquals(expected.getAlgorithm(), actual.getAlgorithm());
        Assert.assertArrayEquals(expected.getEncoded(), actual.getEncoded());
    }
}

