package org.apereo.cas.ticket;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class InvalidTicketExceptionTests {
    @Test
    public void verifyCodeNoThrowable() {
        val t = new InvalidTicketException("InvalidTicketId");
        Assertions.assertEquals("INVALID_TICKET", t.getCode());
    }
}

