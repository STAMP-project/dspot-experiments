package org.apereo.cas.token.cipher;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link JwtTicketCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class JwtTicketCipherExecutorTests {
    public static final String ST = "ST-1234567890";

    @Test
    public void verifyCipheredToken() {
        val c = new JWTTicketCipherExecutor(null, "qeALfMKRSME3mkHy0Qis6mhbGQFzps0ZiU-qyjsPOq_tYyR4fk2uAQR3wZfYTAlGGO3yhpJAMsq2JufeEC4fQg", true, 0, 0);
        val token = c.encode(JwtTicketCipherExecutorTests.ST);
        Assertions.assertEquals(JwtTicketCipherExecutorTests.ST, c.decode(token));
    }

    @Test
    public void verifyCipheredTokenWithoutEncryption() {
        val c = new JWTTicketCipherExecutor(null, "qeALfMKRSME3mkHy0Qis6mhbGQFzps0ZiU-qyjsPOq_tYyR4fk2uAQR3wZfYTAlGGO3yhpJAMsq2JufeEC4fQg", false, 0, 0);
        val token = c.encode(JwtTicketCipherExecutorTests.ST);
        Assertions.assertEquals(JwtTicketCipherExecutorTests.ST, c.decode(token));
    }

    @Test
    public void verifyCipheredTokenWithoutEncryptionAndSigning() {
        val c = new JWTTicketCipherExecutor();
        val token = c.encode(JwtTicketCipherExecutorTests.ST);
        Assertions.assertEquals(JwtTicketCipherExecutorTests.ST, c.decode(token));
    }
}

