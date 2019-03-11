package com.baeldung.jasypt;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;


public class JasyptUnitTest {
    @Test
    public void givenTextPrivateData_whenDecrypt_thenCompareToEncrypted() {
        // given
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        String privateData = "secret-data";
        textEncryptor.setPasswordCharArray("some-random-data".toCharArray());
        // when
        String myEncryptedText = textEncryptor.encrypt(privateData);
        Assert.assertNotSame(privateData, myEncryptedText);// myEncryptedText can be save in db

        // then
        String plainText = textEncryptor.decrypt(myEncryptedText);
        TestCase.assertEquals(plainText, privateData);
    }

    @Test
    public void givenTextPassword_whenOneWayEncryption_thenCompareEncryptedPasswordsShouldBeSame() {
        String password = "secret-pass";
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);
        // when
        boolean result = passwordEncryptor.checkPassword("secret-pass", encryptedPassword);
        // then
        Assert.assertTrue(result);
    }

    @Test
    public void givenTextPassword_whenOneWayEncryption_thenCompareEncryptedPasswordsShouldNotBeSame() {
        String password = "secret-pass";
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);
        // when
        boolean result = passwordEncryptor.checkPassword("secret-pass-not-same", encryptedPassword);
        // then
        Assert.assertFalse(result);
    }
}

