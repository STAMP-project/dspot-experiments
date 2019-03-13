package org.apereo.cas.ticket.registry;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import lombok.Setter;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.proxy.ProxyGrantingTicket;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Setter
public class DistributedTicketRegistryTests {
    private static final String TGT_NAME = "TGT";

    private static final String TGT_ID = "test";

    private DistributedTicketRegistryTests.TestDistributedTicketRegistry ticketRegistry;

    private boolean wasTicketUpdated;

    @Test
    public void verifyProxiedInstancesEqual() {
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl(DistributedTicketRegistryTests.TGT_ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        this.ticketRegistry.addTicket(t);
        val returned = ((TicketGrantingTicket) (this.ticketRegistry.getTicket(DistributedTicketRegistryTests.TGT_ID)));
        Assertions.assertEquals(t, returned);
        Assertions.assertEquals(returned, t);
        Assertions.assertEquals(t.getCreationTime(), returned.getCreationTime());
        Assertions.assertEquals(t.getAuthentication(), returned.getAuthentication());
        Assertions.assertEquals(t.getCountOfUses(), returned.getCountOfUses());
        Assertions.assertEquals(t.getTicketGrantingTicket(), returned.getTicketGrantingTicket());
        Assertions.assertEquals(t.getId(), returned.getId());
        Assertions.assertEquals(t.getChainedAuthentications(), returned.getChainedAuthentications());
        Assertions.assertEquals(t.isExpired(), returned.isExpired());
        Assertions.assertEquals(t.isRoot(), returned.isRoot());
        val s = t.grantServiceTicket("stest", RegisteredServiceTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, true);
        this.ticketRegistry.addTicket(s);
        val sreturned = ((ServiceTicket) (this.ticketRegistry.getTicket("stest")));
        Assertions.assertEquals(s, sreturned);
        Assertions.assertEquals(sreturned, s);
        Assertions.assertEquals(s.getCreationTime(), sreturned.getCreationTime());
        Assertions.assertEquals(s.getCountOfUses(), sreturned.getCountOfUses());
        Assertions.assertEquals(s.getTicketGrantingTicket(), sreturned.getTicketGrantingTicket());
        Assertions.assertEquals(s.getId(), sreturned.getId());
        Assertions.assertEquals(s.isExpired(), sreturned.isExpired());
        Assertions.assertEquals(s.getService(), sreturned.getService());
        Assertions.assertEquals(s.isFromNewLogin(), sreturned.isFromNewLogin());
    }

    @Test
    public void verifyUpdateOfRegistry() throws AbstractTicketException {
        val t = new org.apereo.cas.ticket.TicketGrantingTicketImpl(DistributedTicketRegistryTests.TGT_ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        this.ticketRegistry.addTicket(t);
        val returned = ((TicketGrantingTicket) (this.ticketRegistry.getTicket(DistributedTicketRegistryTests.TGT_ID)));
        val s = returned.grantServiceTicket("test2", RegisteredServiceTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, true);
        this.ticketRegistry.addTicket(s);
        val s2 = ((ServiceTicket) (this.ticketRegistry.getTicket("test2")));
        Assertions.assertNotNull(s2.grantProxyGrantingTicket("ff", CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy()));
        Assertions.assertTrue(s2.isValidFor(RegisteredServiceTestUtils.getService()));
        Assertions.assertTrue(this.wasTicketUpdated);
        returned.markTicketExpired();
        Assertions.assertTrue(t.isExpired());
    }

    @Test
    public void verifyTicketDoesntExist() {
        Assertions.assertNull(this.ticketRegistry.getTicket("fdfas"));
    }

    @Test
    public void verifyDeleteTicketWithPGT() {
        val a = CoreAuthenticationTestUtils.getAuthentication();
        this.ticketRegistry.addTicket(new org.apereo.cas.ticket.TicketGrantingTicketImpl(DistributedTicketRegistryTests.TGT_NAME, a, new NeverExpiresExpirationPolicy()));
        val tgt = this.ticketRegistry.getTicket(DistributedTicketRegistryTests.TGT_NAME, TicketGrantingTicket.class);
        val service = CoreAuthenticationTestUtils.getService("TGT_DELETE_TEST");
        val st1 = tgt.grantServiceTicket("ST1", service, new NeverExpiresExpirationPolicy(), true, true);
        this.ticketRegistry.addTicket(st1);
        Assertions.assertNotNull(this.ticketRegistry.getTicket(DistributedTicketRegistryTests.TGT_NAME, TicketGrantingTicket.class));
        Assertions.assertNotNull(this.ticketRegistry.getTicket("ST1", ServiceTicket.class));
        val pgt = st1.grantProxyGrantingTicket("PGT-1", a, new NeverExpiresExpirationPolicy());
        Assertions.assertEquals(a, pgt.getAuthentication());
        this.ticketRegistry.addTicket(pgt);
        Assertions.assertSame(3, this.ticketRegistry.deleteTicket(tgt.getId()));
        Assertions.assertNull(this.ticketRegistry.getTicket(DistributedTicketRegistryTests.TGT_NAME, TicketGrantingTicket.class));
        Assertions.assertNull(this.ticketRegistry.getTicket("ST1", ServiceTicket.class));
        Assertions.assertNull(this.ticketRegistry.getTicket("PGT-1", ProxyGrantingTicket.class));
    }

    private static class TestDistributedTicketRegistry extends AbstractTicketRegistry {
        private final DistributedTicketRegistryTests parent;

        private final Map<String, Ticket> tickets = new HashMap<>();

        TestDistributedTicketRegistry(final DistributedTicketRegistryTests parent) {
            this.parent = parent;
        }

        @Override
        public Ticket updateTicket(final Ticket ticket) {
            setWasTicketUpdated(true);
            return ticket;
        }

        @Override
        public void addTicket(final Ticket ticket) {
            this.tickets.put(ticket.getId(), ticket);
            updateTicket(ticket);
        }

        @Override
        public Ticket getTicket(final String ticketId) {
            return this.tickets.get(ticketId);
        }

        @Override
        public Ticket getTicket(final String ticketId, final Predicate<Ticket> predicate) {
            return getTicket(ticketId);
        }

        @Override
        public Collection<Ticket> getTickets() {
            return this.tickets.values();
        }

        @Override
        public boolean deleteSingleTicket(final String ticketId) {
            return (this.tickets.remove(ticketId)) != null;
        }

        @Override
        public long deleteAll() {
            val size = this.tickets.size();
            this.tickets.clear();
            return size;
        }
    }
}

