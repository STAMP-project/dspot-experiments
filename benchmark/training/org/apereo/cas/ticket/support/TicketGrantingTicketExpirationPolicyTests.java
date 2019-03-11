package org.apereo.cas.ticket.support;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.joda.time.DateTimeUtils;
import org.joda.time.org.apereo.cas.util.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;


/**
 *
 *
 * @author William G. Thompson, Jr.
 * @since 3.4.10
 */
@DirtiesContext
public class TicketGrantingTicketExpirationPolicyTests {
    private static final long HARD_TIMEOUT = 2;

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "ticketGrantingTicketExpirationPolicyTests.json");

    private static final long SLIDING_TIMEOUT = 2;

    private static final long TIMEOUT_BUFFER = 2;

    private static final String TGT_ID = "test";

    private ExpirationPolicy expirationPolicy;

    private TicketGrantingTicket ticketGrantingTicket;

    @Test
    public void verifyTgtIsExpiredByHardTimeOut() {
        // keep tgt alive via sliding window until within SLIDING_TIME / 2 of the HARD_TIMEOUT
        val creationTime = ticketGrantingTicket.getCreationTime();
        while (creationTime.plus(((TicketGrantingTicketExpirationPolicyTests.HARD_TIMEOUT) - ((TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT) / 2)), ChronoUnit.SECONDS).isAfter(org.apereo.cas.util.DateTimeUtils.zonedDateTimeOf(DateTimeUtils.currentTimeMillis()))) {
            ticketGrantingTicket.grantServiceTicket(TicketGrantingTicketExpirationPolicyTests.TGT_ID, RegisteredServiceTestUtils.getService(), expirationPolicy, false, true);
            val tt = (DateTimeUtils.currentTimeMillis()) + (((TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT) - (TicketGrantingTicketExpirationPolicyTests.TIMEOUT_BUFFER)) * 1000);
            DateTimeUtils.setCurrentMillisFixed(tt);
            Assertions.assertFalse(this.ticketGrantingTicket.isExpired());
        } 
        // final sliding window extension past the HARD_TIMEOUT
        ticketGrantingTicket.grantServiceTicket(TicketGrantingTicketExpirationPolicyTests.TGT_ID, RegisteredServiceTestUtils.getService(), expirationPolicy, false, true);
        val tt = (DateTimeUtils.currentTimeMillis()) + ((((TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT) / 2) + (TicketGrantingTicketExpirationPolicyTests.TIMEOUT_BUFFER)) * 1000);
        DateTimeUtils.setCurrentMillisFixed(tt);
        Assertions.assertTrue(ticketGrantingTicket.isExpired());
    }

    @Test
    public void verifyTgtIsExpiredBySlidingWindow() {
        ticketGrantingTicket.grantServiceTicket(TicketGrantingTicketExpirationPolicyTests.TGT_ID, RegisteredServiceTestUtils.getService(), expirationPolicy, false, true);
        val tt1 = (System.currentTimeMillis()) + (((TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT) - (TicketGrantingTicketExpirationPolicyTests.TIMEOUT_BUFFER)) * 1000);
        DateTimeUtils.setCurrentMillisFixed(tt1);
        Assertions.assertFalse(ticketGrantingTicket.isExpired());
        ticketGrantingTicket.grantServiceTicket(TicketGrantingTicketExpirationPolicyTests.TGT_ID, RegisteredServiceTestUtils.getService(), expirationPolicy, false, true);
        val tt2 = (DateTimeUtils.currentTimeMillis()) + (((TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT) - (TicketGrantingTicketExpirationPolicyTests.TIMEOUT_BUFFER)) * 1000);
        DateTimeUtils.setCurrentMillisFixed(tt2);
        Assertions.assertFalse(ticketGrantingTicket.isExpired());
        ticketGrantingTicket.grantServiceTicket(TicketGrantingTicketExpirationPolicyTests.TGT_ID, RegisteredServiceTestUtils.getService(), expirationPolicy, false, true);
        val tt3 = (DateTimeUtils.currentTimeMillis()) + (((TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT) + (TicketGrantingTicketExpirationPolicyTests.TIMEOUT_BUFFER)) * 1000);
        DateTimeUtils.setCurrentMillisFixed(tt3);
        Assertions.assertTrue(ticketGrantingTicket.isExpired());
    }

    @Test
    public void verifySerializeAnExpirationPolicyToJson() throws IOException {
        val policy = new TicketGrantingTicketExpirationPolicy(100, 100);
        TicketGrantingTicketExpirationPolicyTests.MAPPER.writeValue(TicketGrantingTicketExpirationPolicyTests.JSON_FILE, policy);
        val policyRead = TicketGrantingTicketExpirationPolicyTests.MAPPER.readValue(TicketGrantingTicketExpirationPolicyTests.JSON_FILE, TicketGrantingTicketExpirationPolicy.class);
        Assertions.assertEquals(policy, policyRead);
    }

    private static class MovingTimeTicketExpirationPolicy extends TicketGrantingTicketExpirationPolicy {
        private static final long serialVersionUID = -3901717185202249332L;

        MovingTimeTicketExpirationPolicy() {
            super(TicketGrantingTicketExpirationPolicyTests.HARD_TIMEOUT, TicketGrantingTicketExpirationPolicyTests.SLIDING_TIMEOUT);
        }

        @Override
        protected ZonedDateTime getCurrentSystemTime() {
            return org.apereo.cas.util.DateTimeUtils.zonedDateTimeOf(DateTimeUtils.currentTimeMillis());
        }
    }
}

