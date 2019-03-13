package com.baeldung.encrypt;


import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FileEncrypterDecrypterIntegrationTest {
    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned() throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        String originalContent = "foobar";
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey, "AES/CBC/PKCS5Padding");
        fileEncrypterDecrypter.encrypt(originalContent, "baz.enc");
        String decryptedContent = fileEncrypterDecrypter.decrypt("baz.enc");
        Assert.assertThat(decryptedContent, Matchers.is(originalContent));
        new File("baz.enc").delete();// cleanup

    }
}

