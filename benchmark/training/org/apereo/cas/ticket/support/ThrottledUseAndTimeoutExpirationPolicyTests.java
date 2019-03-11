package org.apereo.cas.ticket.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class ThrottledUseAndTimeoutExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "throttleUseAndTimeoutExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final long TIMEOUT = 2000;

    private ThrottledUseAndTimeoutExpirationPolicy expirationPolicy;

    private TicketGrantingTicket ticket;

    @Test
    public void verifyTicketIsNotExpired() {
        Assertions.assertFalse(this.ticket.isExpired());
    }

    @Test
    public void verifyTicketIsExpired() {
        expirationPolicy.setTimeToKillInSeconds((-(ThrottledUseAndTimeoutExpirationPolicyTests.TIMEOUT)));
        Assertions.assertTrue(this.ticket.isExpired());
    }

    @Test
    public void verifyTicketUsedButWithTimeout() {
        this.ticket.grantServiceTicket("test", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false, true);
        expirationPolicy.setTimeToKillInSeconds(ThrottledUseAndTimeoutExpirationPolicyTests.TIMEOUT);
        expirationPolicy.setTimeInBetweenUsesInSeconds((-10));
        Assertions.assertFalse(this.ticket.isExpired());
    }

    @Test
    public void verifyNotWaitingEnoughTime() {
        this.ticket.grantServiceTicket("test", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false, true);
        expirationPolicy.setTimeToKillInSeconds(ThrottledUseAndTimeoutExpirationPolicyTests.TIMEOUT);
        Assertions.assertTrue(this.ticket.isExpired());
    }

    @Test
    public void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        ThrottledUseAndTimeoutExpirationPolicyTests.MAPPER.writeValue(ThrottledUseAndTimeoutExpirationPolicyTests.JSON_FILE, expirationPolicy);
        val policyRead = ThrottledUseAndTimeoutExpirationPolicyTests.MAPPER.readValue(ThrottledUseAndTimeoutExpirationPolicyTests.JSON_FILE, ThrottledUseAndTimeoutExpirationPolicy.class);
        Assertions.assertEquals(expirationPolicy, policyRead);
    }
}

