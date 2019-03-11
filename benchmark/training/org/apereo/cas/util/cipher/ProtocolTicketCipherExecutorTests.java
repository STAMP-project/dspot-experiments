package org.apereo.cas.util.cipher;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ProtocolTicketCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ProtocolTicketCipherExecutorTests {
    @Test
    public void verifyAction() {
        val cipher = new ProtocolTicketCipherExecutor();
        val encoded = cipher.encode("ST-1234567890");
        Assertions.assertEquals("ST-1234567890", cipher.decode(encoded));
        Assertions.assertNotNull(cipher.getName());
        Assertions.assertNotNull(cipher.getSigningKeySetting());
        Assertions.assertNotNull(cipher.getEncryptionKeySetting());
    }
}

