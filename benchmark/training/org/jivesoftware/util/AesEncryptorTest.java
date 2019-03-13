package org.jivesoftware.util;


import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AesEncryptorTest {
    @Test
    public void testEncryptionUsingDefaultKey() {
        String test = UUID.randomUUID().toString();
        Encryptor encryptor = new AesEncryptor();
        String b64Encrypted = encryptor.encrypt(test);
        Assert.assertFalse(test.equals(b64Encrypted));
        Assert.assertEquals(test, encryptor.decrypt(b64Encrypted));
    }

    @Test
    public void testEncryptionUsingCustomKey() {
        String test = UUID.randomUUID().toString();
        Encryptor encryptor = new AesEncryptor(UUID.randomUUID().toString());
        String b64Encrypted = encryptor.encrypt(test);
        Assert.assertFalse(test.equals(b64Encrypted));
        Assert.assertEquals(test, encryptor.decrypt(b64Encrypted));
    }

    @Test
    public void testEncryptionForEmptyString() {
        String test = "";
        Encryptor encryptor = new AesEncryptor();
        String b64Encrypted = encryptor.encrypt(test);
        Assert.assertFalse(test.equals(b64Encrypted));
        Assert.assertEquals(test, encryptor.decrypt(b64Encrypted));
    }

    @Test
    public void testEncryptionForNullString() {
        Encryptor encryptor = new AesEncryptor();
        String b64Encrypted = encryptor.encrypt(null);
        Assert.assertNull(b64Encrypted);
    }

    @Test
    public void testEncryptionWithKeyAndIV() {
        final String plainText = UUID.randomUUID().toString();
        final byte[] iv = "0123456789abcdef".getBytes();
        final Encryptor encryptor = new AesEncryptor(UUID.randomUUID().toString());
        final String encryptedText = encryptor.encrypt(plainText, iv);
        final String decryptedText = encryptor.decrypt(encryptedText, iv);
        Assert.assertThat(decryptedText, Matchers.is(plainText));
    }

    @Test
    public void testEncryptionWithKeyAndBadIV() {
        final String plainText = UUID.randomUUID().toString();
        final byte[] iv = "0123456789abcdef".getBytes();
        final Encryptor encryptor = new AesEncryptor(UUID.randomUUID().toString());
        final String encryptedText = encryptor.encrypt(plainText, iv);
        final String decryptedText = encryptor.decrypt(encryptedText);
        Assert.assertThat(decryptedText, Matchers.is(Matchers.not(plainText)));
    }
}

