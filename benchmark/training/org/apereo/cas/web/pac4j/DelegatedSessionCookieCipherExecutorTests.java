package org.apereo.cas.web.pac4j;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DelegatedSessionCookieCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DelegatedSessionCookieCipherExecutorTests {
    public static final String ST = "ST-1234567890";

    @Test
    public void verifyCipheredToken() {
        val c = new DelegatedSessionCookieCipherExecutor(null, null, 0, 0);
        val token = c.encode(DelegatedSessionCookieCipherExecutorTests.ST);
        Assertions.assertEquals(DelegatedSessionCookieCipherExecutorTests.ST, c.decode(token));
        Assertions.assertNotNull(c.getName());
        Assertions.assertNotNull(c.getEncryptionKeySetting());
        Assertions.assertNotNull(c.getSigningKeySetting());
    }
}

