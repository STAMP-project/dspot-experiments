package org.apereo.cas.util.cipher;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link BaseStringCipherExecutor}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class TicketGrantingCookieCipherExecutorTests {
    @Test
    public void verifyAction() {
        val cipher = new TicketGrantingCookieCipherExecutor();
        val encoded = cipher.encode("ST-1234567890");
        Assertions.assertEquals("ST-1234567890", cipher.decode(encoded));
        Assertions.assertNotNull(cipher.getName());
        Assertions.assertNotNull(cipher.getSigningKeySetting());
        Assertions.assertNotNull(cipher.getEncryptionKeySetting());
    }

    @Test
    public void checkEncryptionWithDefaultSettings() {
        val cipherExecutor = new TicketGrantingCookieCipherExecutor("1PbwSbnHeinpkZOSZjuSJ8yYpUrInm5aaV18J2Ar4rM", "szxK-5_eJjs-aUj-64MpUZ-GPPzGLhYPLGl0wrYjYNVAGva2P0lLe6UGKGM7k8dWxsOVGutZWgvmY3l5oVPO3w", 0, 0);
        val result = cipherExecutor.decode(cipherExecutor.encode("CAS Test"));
        Assertions.assertEquals("CAS Test", result);
    }
}

