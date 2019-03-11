package org.apereo.cas.ticket.registry.queue;


import lombok.val;
import org.apereo.cas.StringBean;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AddTicketMessageQueueCommandTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class AddTicketMessageQueueCommandTests extends AbstractTicketMessageQueueCommandTests {
    @Test
    public void verifyAddTicket() {
        TicketGrantingTicket ticket = new org.apereo.cas.ticket.TicketGrantingTicketImpl("TGT", CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        ticketRegistry.addTicket(ticket);
        val cmd = new AddTicketMessageQueueCommand(new StringBean(), ticket);
        cmd.execute(ticketRegistry);
        ticket = ticketRegistry.getTicket(ticket.getId(), ticket.getClass());
        Assertions.assertNotNull(ticket);
        Assertions.assertEquals("TGT", ticket.getId());
    }
}

