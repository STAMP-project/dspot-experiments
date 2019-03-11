package org.apereo.cas.ticket.registry.queue;


import lombok.val;
import org.apereo.cas.StringBean;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DeleteTicketMessageQueueCommandTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class DeleteTicketMessageQueueCommandTests extends AbstractTicketMessageQueueCommandTests {
    @Test
    public void verifyDeleteTicket() {
        val ticket = new org.apereo.cas.ticket.TicketGrantingTicketImpl("TGT", CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        ticketRegistry.addTicket(ticket);
        val cmd = new DeleteTicketMessageQueueCommand(new StringBean(), ticket.getId());
        cmd.execute(ticketRegistry);
        Assertions.assertTrue(ticketRegistry.getTickets().isEmpty());
    }
}

