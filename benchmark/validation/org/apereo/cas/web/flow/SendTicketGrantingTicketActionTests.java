package org.apereo.cas.web.flow;


import javax.servlet.http.Cookie;
import lombok.val;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;
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
 * @author Marvin S. Addison
 * @since 3.4.0
 */
public class SendTicketGrantingTicketActionTests extends AbstractWebflowActionsTests {
    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final String TEST_STRING = "test";

    private static final String SUCCESS = "success";

    @Autowired
    @Qualifier("sendTicketGrantingTicketAction")
    private Action action;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    private MockRequestContext context;

    @Test
    public void verifyNoTgtToSet() throws Exception {
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        Assertions.assertEquals(SendTicketGrantingTicketActionTests.SUCCESS, this.action.execute(this.context).getId());
    }

    @Test
    public void verifyTgtToSet() throws Exception {
        val request = new MockHttpServletRequest();
        request.setRemoteAddr(SendTicketGrantingTicketActionTests.LOCALHOST_IP);
        request.setLocalAddr(SendTicketGrantingTicketActionTests.LOCALHOST_IP);
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val response = new MockHttpServletResponse();
        request.addHeader("User-Agent", "Test");
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn(SendTicketGrantingTicketActionTests.TEST_STRING);
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        Assertions.assertEquals(SendTicketGrantingTicketActionTests.SUCCESS, this.action.execute(this.context).getId());
        request.setCookies(response.getCookies());
        Assertions.assertEquals(tgt.getId(), this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request));
    }

    @Test
    public void verifyTgtToSetRemovingOldTgt() throws Exception {
        val request = new MockHttpServletRequest();
        request.setRemoteAddr(SendTicketGrantingTicketActionTests.LOCALHOST_IP);
        request.setLocalAddr(SendTicketGrantingTicketActionTests.LOCALHOST_IP);
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val response = new MockHttpServletResponse();
        request.addHeader("User-Agent", "Test");
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn(SendTicketGrantingTicketActionTests.TEST_STRING);
        request.setCookies(new Cookie("TGT", "test5"));
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        Assertions.assertEquals(SendTicketGrantingTicketActionTests.SUCCESS, this.action.execute(this.context).getId());
        request.setCookies(response.getCookies());
        Assertions.assertEquals(tgt.getId(), this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request));
    }
}

