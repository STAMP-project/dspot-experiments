package org.apereo.cas.support.rest;


import lombok.val;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.support.rest.resources.TicketStatusResource;
import org.apereo.cas.ticket.InvalidTicketException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;


/**
 * This is {@link TicketStatusResourceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@ExtendWith(MockitoExtension.class)
public class TicketStatusResourceTests {
    private static final String TICKETS_RESOURCE_URL = "/cas/v1/tickets";

    @Mock
    private CentralAuthenticationService casMock;

    @InjectMocks
    private TicketStatusResource ticketStatusResource;

    private MockMvc mockMvc;

    @Test
    public void verifyStatus() throws Exception {
        val tgt = new MockTicketGrantingTicket("casuser");
        Mockito.when(casMock.getTicket(ArgumentMatchers.anyString())).thenReturn(tgt);
        this.mockMvc.perform(get(((TicketStatusResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1"))).andExpect(status().isOk()).andExpect(content().string(tgt.getId()));
    }

    @Test
    public void verifyStatusNotFound() throws Exception {
        Mockito.when(casMock.getTicket(ArgumentMatchers.anyString())).thenThrow(InvalidTicketException.class);
        this.mockMvc.perform(get(((TicketStatusResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1"))).andExpect(status().isNotFound());
    }

    @Test
    public void verifyStatusError() throws Exception {
        Mockito.when(casMock.getTicket(ArgumentMatchers.anyString())).thenThrow(RuntimeException.class);
        this.mockMvc.perform(get(((TicketStatusResourceTests.TICKETS_RESOURCE_URL) + "/TGT-1"))).andExpect(status().isInternalServerError());
    }
}

