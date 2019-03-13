package org.apereo.cas.util;


import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Handles tests for {@link HostNameBasedUniqueTicketIdGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class HostNameBasedUniqueTicketIdGeneratorTests {
    @Test
    public void verifyUniqueGenerationOfTicketIds() {
        val generator = new HostNameBasedUniqueTicketIdGenerator(10, StringUtils.EMPTY);
        val id1 = generator.getNewTicketId("TEST");
        val id2 = generator.getNewTicketId("TEST");
        Assertions.assertNotSame(id1, id2);
    }
}

