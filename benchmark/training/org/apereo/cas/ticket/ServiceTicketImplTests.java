package org.apereo.cas.ticket;


import ServiceTicket.PREFIX;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.support.MultiTimeUseOrTimeoutExpirationPolicy;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class ServiceTicketImplTests {
    private static final String ST_ID = "stest1";

    private static final File ST_JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "st.json");

    private static final String ID = "test";

    private final TicketGrantingTicketImpl tgt = new TicketGrantingTicketImpl(ServiceTicketImplTests.ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());

    private final DefaultUniqueTicketIdGenerator idGenerator = new DefaultUniqueTicketIdGenerator();

    private ObjectMapper mapper;

    @Test
    public void verifySerializeToJson() throws IOException {
        val stWritten = new ServiceTicketImpl(ServiceTicketImplTests.ST_ID, tgt, RegisteredServiceTestUtils.getService(), true, new NeverExpiresExpirationPolicy());
        mapper.writeValue(ServiceTicketImplTests.ST_JSON_FILE, stWritten);
        val stRead = mapper.readValue(ServiceTicketImplTests.ST_JSON_FILE, ServiceTicketImpl.class);
        Assertions.assertEquals(stWritten, stRead);
    }

    @Test
    public void verifyNoService() {
        Assertions.assertThrows(Exception.class, () -> new ServiceTicketImpl(ServiceTicketImplTests.ST_ID, tgt, null, false, new NeverExpiresExpirationPolicy()));
    }

    @Test
    public void verifyNoTicket() {
        Assertions.assertThrows(NullPointerException.class, () -> new ServiceTicketImpl(ServiceTicketImplTests.ST_ID, null, CoreAuthenticationTestUtils.getService(), false, new NeverExpiresExpirationPolicy()));
    }

    @Test
    public void verifyIsFromNewLoginTrue() {
        val s = new ServiceTicketImpl(ServiceTicketImplTests.ST_ID, tgt, CoreAuthenticationTestUtils.getService(), true, new NeverExpiresExpirationPolicy());
        Assertions.assertTrue(s.isFromNewLogin());
    }

    @Test
    public void verifyIsFromNewLoginFalse() {
        val s = tgt.grantServiceTicket(ServiceTicketImplTests.ST_ID, CoreAuthenticationTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, false);
        Assertions.assertTrue(s.isFromNewLogin());
        val s1 = tgt.grantServiceTicket(ServiceTicketImplTests.ST_ID, CoreAuthenticationTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, false);
        Assertions.assertFalse(s1.isFromNewLogin());
    }

    @Test
    public void verifyGetService() {
        val simpleService = CoreAuthenticationTestUtils.getService();
        val s = new ServiceTicketImpl(ServiceTicketImplTests.ST_ID, tgt, simpleService, false, new NeverExpiresExpirationPolicy());
        Assertions.assertEquals(simpleService, s.getService());
    }

    @Test
    public void verifyGetTicket() {
        val simpleService = CoreAuthenticationTestUtils.getService();
        val s = new ServiceTicketImpl(ServiceTicketImplTests.ST_ID, tgt, simpleService, false, new NeverExpiresExpirationPolicy());
        Assertions.assertEquals(tgt, s.getTicketGrantingTicket());
    }

    @Test
    public void verifyTicketNeverExpires() {
        val t = new TicketGrantingTicketImpl(ServiceTicketImplTests.ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        val s = t.grantServiceTicket(idGenerator.getNewTicketId(PREFIX), CoreAuthenticationTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, true);
        t.markTicketExpired();
        Assertions.assertFalse(s.isExpired());
    }

    @Test
    public void verifyIsExpiredFalse() {
        val t = new TicketGrantingTicketImpl(ServiceTicketImplTests.ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        val s = t.grantServiceTicket(idGenerator.getNewTicketId(PREFIX), CoreAuthenticationTestUtils.getService(), new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false, true);
        Assertions.assertFalse(s.isExpired());
    }

    @Test
    public void verifyTicketGrantingTicket() throws AbstractTicketException {
        val a = CoreAuthenticationTestUtils.getAuthentication();
        val t = new TicketGrantingTicketImpl(ServiceTicketImplTests.ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        val s = t.grantServiceTicket(idGenerator.getNewTicketId(PREFIX), CoreAuthenticationTestUtils.getService(), new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false, true);
        val t1 = s.grantProxyGrantingTicket(idGenerator.getNewTicketId(TicketGrantingTicket.PREFIX), a, new NeverExpiresExpirationPolicy());
        Assertions.assertEquals(a, t1.getAuthentication());
    }

    @Test
    public void verifyTicketGrantingTicketGrantedTwice() throws AbstractTicketException {
        val a = CoreAuthenticationTestUtils.getAuthentication();
        val t = new TicketGrantingTicketImpl(ServiceTicketImplTests.ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        val s = t.grantServiceTicket(idGenerator.getNewTicketId(PREFIX), CoreAuthenticationTestUtils.getService(), new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false, true);
        s.grantProxyGrantingTicket(idGenerator.getNewTicketId(TicketGrantingTicket.PREFIX), a, new NeverExpiresExpirationPolicy());
        Assertions.assertThrows(Exception.class, () -> s.grantProxyGrantingTicket(idGenerator.getNewTicketId(TicketGrantingTicket.PREFIX), a, new NeverExpiresExpirationPolicy()));
    }
}

