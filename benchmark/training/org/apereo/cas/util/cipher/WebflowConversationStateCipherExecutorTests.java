package org.apereo.cas.util.cipher;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link WebflowConversationStateCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class WebflowConversationStateCipherExecutorTests {
    @Test
    public void verifyAction() {
        val cipher = new WebflowConversationStateCipherExecutor(null, null, "AES", 512, 16, "webflow");
        val encoded = cipher.encode("ST-1234567890".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals("ST-1234567890", new String(cipher.decode(encoded), StandardCharsets.UTF_8));
        Assertions.assertNotNull(cipher.getName());
        Assertions.assertNotNull(cipher.getSigningKeySetting());
        Assertions.assertNotNull(cipher.getEncryptionKeySetting());
    }
}

