package com.baeldung.cipher;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.junit.Test;


public class EncryptorUnitTest {
    private String encKeyString;

    private String message;

    private String certificateString;

    private Encryptor encryptor;

    @Test
    public void givenEncryptionKey_whenMessageIsPassedToEncryptor_thenMessageIsEncrypted() throws Exception {
        byte[] encryptedMessage = encryptor.encryptMessage(message.getBytes(), encKeyString.getBytes());
        assertThat(encryptedMessage).isNotNull();
        assertThat(((encryptedMessage.length) % 32)).isEqualTo(0);
    }

    @Test
    public void givenCertificateWithPublicKey_whenMessageIsPassedToEncryptor_thenMessageIsEncrypted() throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        InputStream is = new ByteArrayInputStream(certificateString.getBytes());
        X509Certificate certificate = ((X509Certificate) (factory.generateCertificate(is)));
        byte[] encryptedMessage = encryptor.encryptMessage(message.getBytes(), certificate);
        assertThat(encryptedMessage).isNotNull();
        assertThat(((encryptedMessage.length) % 128)).isEqualTo(0);
    }

    @Test
    public void givenEncryptionKey_whenMessageIsEncrypted_thenDecryptMessage() throws Exception {
        byte[] encryptedMessageBytes = encryptor.encryptMessage(message.getBytes(), encKeyString.getBytes());
        byte[] clearMessageBytes = encryptor.decryptMessage(encryptedMessageBytes, encKeyString.getBytes());
        assertThat(message).isEqualTo(new String(clearMessageBytes));
    }
}

