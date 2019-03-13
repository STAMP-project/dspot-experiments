package org.apereo.cas.util;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class DefaultUniqueTicketIdGeneratorTests {
    @Test
    public void verifyUniqueGenerationOfTicketIds() {
        val generator = new DefaultUniqueTicketIdGenerator(10);
        Assertions.assertNotSame(generator.getNewTicketId("TEST"), generator.getNewTicketId("TEST"));
    }

    @Test
    public void verifySuffix() {
        val suffix = "suffix";
        val generator = new DefaultUniqueTicketIdGenerator(10, suffix);
        Assertions.assertTrue(generator.getNewTicketId("test").endsWith(suffix));
    }

    @Test
    public void verifyNullSuffix() {
        val lengthWithoutSuffix = 23;
        val generator = new DefaultUniqueTicketIdGenerator(12, null);
        val ticketId = generator.getNewTicketId("test");
        Assertions.assertEquals(lengthWithoutSuffix, ticketId.length());
    }
}

