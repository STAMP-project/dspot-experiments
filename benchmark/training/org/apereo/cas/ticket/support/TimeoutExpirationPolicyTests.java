package org.apereo.cas.ticket.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class TimeoutExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "timeoutExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final long TIMEOUT = 1;

    private ExpirationPolicy expirationPolicy;

    private Ticket ticket;

    @Test
    public void verifyTicketIsNull() {
        Assertions.assertTrue(this.expirationPolicy.isExpired(null));
    }

    @Test
    public void verifyTicketIsNotExpired() {
        Assertions.assertFalse(this.ticket.isExpired());
    }

    @Test
    public void verifyTicketIsExpired() {
        ticket = new org.apereo.cas.ticket.TicketGrantingTicketImpl("test", CoreAuthenticationTestUtils.getAuthentication(), new TimeoutExpirationPolicy((-100)));
        Assertions.assertTrue(ticket.isExpired());
    }

    @Test
    public void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        TimeoutExpirationPolicyTests.MAPPER.writeValue(TimeoutExpirationPolicyTests.JSON_FILE, expirationPolicy);
        val policyRead = TimeoutExpirationPolicyTests.MAPPER.readValue(TimeoutExpirationPolicyTests.JSON_FILE, TimeoutExpirationPolicy.class);
        Assertions.assertEquals(expirationPolicy, policyRead);
    }
}

