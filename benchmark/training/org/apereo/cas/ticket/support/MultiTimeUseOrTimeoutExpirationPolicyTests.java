package org.apereo.cas.ticket.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class MultiTimeUseOrTimeoutExpirationPolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "multiTimeUseOrTimeoutExpirationPolicy.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final long TIMEOUT_SECONDS = 1;

    private static final int NUMBER_OF_USES = 5;

    private static final int TIMEOUT_BUFFER = 50;

    private ExpirationPolicy expirationPolicy;

    private TicketGrantingTicket ticket;

    @Test
    public void verifyTicketIsNull() {
        Assertions.assertTrue(this.expirationPolicy.isExpired(null));
    }

    @Test
    public void verifyTicketIsNotExpired() {
        Assertions.assertFalse(this.ticket.isExpired());
    }

    @Test
    public void verifyTicketIsExpiredByTime() throws InterruptedException {
        Thread.sleep((((MultiTimeUseOrTimeoutExpirationPolicyTests.TIMEOUT_SECONDS) * 1000) + (MultiTimeUseOrTimeoutExpirationPolicyTests.TIMEOUT_BUFFER)));
        Assertions.assertTrue(this.ticket.isExpired());
    }

    @Test
    public void verifyTicketIsExpiredByCount() {
        IntStream.range(0, MultiTimeUseOrTimeoutExpirationPolicyTests.NUMBER_OF_USES).forEach(( i) -> this.ticket.grantServiceTicket("test", RegisteredServiceTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, true));
        Assertions.assertTrue(this.ticket.isExpired());
    }

    @Test
    public void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        MultiTimeUseOrTimeoutExpirationPolicyTests.MAPPER.writeValue(MultiTimeUseOrTimeoutExpirationPolicyTests.JSON_FILE, expirationPolicy);
        val policyRead = MultiTimeUseOrTimeoutExpirationPolicyTests.MAPPER.readValue(MultiTimeUseOrTimeoutExpirationPolicyTests.JSON_FILE, MultiTimeUseOrTimeoutExpirationPolicy.class);
        Assertions.assertEquals(expirationPolicy, policyRead);
    }
}

