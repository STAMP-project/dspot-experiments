package org.jivesoftware.util;


import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class BlowfishEncryptorTest {
    @Test
    public void testEncryptionUsingDefaultKey() {
        String test = UUID.randomUUID().toString();
        Encryptor encryptor = new Blowfish();
        String b64Encrypted = encryptor.encrypt(test);
        Assert.assertFalse(test.equals(b64Encrypted));
        Assert.assertEquals(test, encryptor.decrypt(b64Encrypted));
    }

    @Test
    public void testEncryptionUsingCustomKey() {
        String test = UUID.randomUUID().toString();
        Encryptor encryptor = new Blowfish(UUID.randomUUID().toString());
        String b64Encrypted = encryptor.encrypt(test);
        Assert.assertFalse(test.equals(b64Encrypted));
        Assert.assertEquals(test, encryptor.decrypt(b64Encrypted));
    }

    @Test
    public void testEncryptionForEmptyString() {
        String test = "";
        Encryptor encryptor = new Blowfish();
        String b64Encrypted = encryptor.encrypt(test);
        Assert.assertFalse(test.equals(b64Encrypted));
        Assert.assertEquals(test, encryptor.decrypt(b64Encrypted));
    }

    @Test
    public void testEncryptionForNullString() {
        Encryptor encryptor = new Blowfish();
        String b64Encrypted = encryptor.encrypt(null);
        Assert.assertNull(b64Encrypted);
    }
}

