package org.apereo.cas.web.flow;


import CasProtocolConstants.PARAMETER_GATEWAY;
import CasProtocolConstants.PARAMETER_SERVICE;
import CasWebflowConstants.STATE_ID_GATEWAY;
import CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE;
import WebUtils.PARAMETER_TICKET_GRANTING_TICKET_ID;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class GenerateServiceTicketActionTests extends AbstractWebflowActionsTests {
    private static final String SERVICE_PARAM = "service";

    @Autowired
    @Qualifier("generateServiceTicketAction")
    private Action action;

    private TicketGrantingTicket ticketGrantingTicket;

    @Test
    public void verifyServiceTicketFromCookie() throws Exception {
        val context = new MockRequestContext();
        context.getFlowScope().put(GenerateServiceTicketActionTests.SERVICE_PARAM, RegisteredServiceTestUtils.getService());
        context.getFlowScope().put(PARAMETER_TICKET_GRANTING_TICKET_ID, this.ticketGrantingTicket.getId());
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        request.addParameter(PARAMETER_SERVICE, GenerateServiceTicketActionTests.SERVICE_PARAM);
        request.setCookies(new javax.servlet.http.Cookie("TGT", this.ticketGrantingTicket.getId()));
        this.action.execute(context);
        Assertions.assertNotNull(WebUtils.getServiceTicketFromRequestScope(context));
    }

    @Test
    public void verifyTicketGrantingTicketFromRequest() throws Exception {
        val context = new MockRequestContext();
        context.getFlowScope().put(GenerateServiceTicketActionTests.SERVICE_PARAM, RegisteredServiceTestUtils.getService());
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        request.addParameter(PARAMETER_SERVICE, GenerateServiceTicketActionTests.SERVICE_PARAM);
        WebUtils.putTicketGrantingTicketInScopes(context, this.ticketGrantingTicket);
        this.action.execute(context);
        Assertions.assertNotNull(WebUtils.getServiceTicketFromRequestScope(context));
    }

    @Test
    public void verifyTicketGrantingTicketNoTgt() throws Exception {
        val context = new MockRequestContext();
        context.getFlowScope().put(GenerateServiceTicketActionTests.SERVICE_PARAM, RegisteredServiceTestUtils.getService());
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        request.addParameter(PARAMETER_SERVICE, GenerateServiceTicketActionTests.SERVICE_PARAM);
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn("bleh");
        WebUtils.putTicketGrantingTicketInScopes(context, tgt);
        Assertions.assertEquals(TRANSITION_ID_AUTHENTICATION_FAILURE, this.action.execute(context).getId());
    }

    @Test
    public void verifyTicketGrantingTicketExpiredTgt() throws Exception {
        val context = new MockRequestContext();
        context.getFlowScope().put(GenerateServiceTicketActionTests.SERVICE_PARAM, RegisteredServiceTestUtils.getService());
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        request.addParameter(PARAMETER_SERVICE, GenerateServiceTicketActionTests.SERVICE_PARAM);
        WebUtils.putTicketGrantingTicketInScopes(context, this.ticketGrantingTicket);
        this.ticketGrantingTicket.markTicketExpired();
        getTicketRegistry().updateTicket(this.ticketGrantingTicket);
        Assertions.assertEquals(TRANSITION_ID_AUTHENTICATION_FAILURE, this.action.execute(context).getId());
    }

    @Test
    public void verifyTicketGrantingTicketNotTgtButGateway() throws Exception {
        val context = new MockRequestContext();
        context.getFlowScope().put(GenerateServiceTicketActionTests.SERVICE_PARAM, RegisteredServiceTestUtils.getService());
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        request.addParameter(PARAMETER_SERVICE, GenerateServiceTicketActionTests.SERVICE_PARAM);
        request.addParameter(PARAMETER_GATEWAY, "true");
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn("bleh");
        WebUtils.putTicketGrantingTicketInScopes(context, tgt);
        Assertions.assertEquals(STATE_ID_GATEWAY, this.action.execute(context).getId());
    }
}

