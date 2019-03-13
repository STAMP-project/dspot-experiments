package org.apereo.cas.web.flow;


import CasProtocolConstants.PARAMETER_RENEW;
import CasProtocolConstants.PARAMETER_SERVICE;
import HttpRequestUtils.USER_AGENT_HEADER;
import javax.servlet.http.Cookie;
import lombok.val;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.ticket.TicketGrantingTicket;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 *
 *
 * @author Marvin S. Addison
 * @since 3.4.0
 */
@TestPropertySource(properties = "cas.sso.createSsoCookieOnRenewAuthn=false")
public class SendTicketGrantingTicketActionSsoTests extends AbstractWebflowActionsTests {
    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final String TEST_STRING = "test";

    private static final String SUCCESS = "success";

    @Autowired
    @Qualifier("sendTicketGrantingTicketAction")
    private Action action;

    private MockRequestContext context;

    @Test
    public void verifySsoSessionCookieOnRenewAsParameter() throws Exception {
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_RENEW, "true");
        request.setRemoteAddr(SendTicketGrantingTicketActionSsoTests.LOCALHOST_IP);
        request.setLocalAddr(SendTicketGrantingTicketActionSsoTests.LOCALHOST_IP);
        request.addHeader(USER_AGENT_HEADER, "test");
        ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn(SendTicketGrantingTicketActionSsoTests.TEST_STRING);
        request.setCookies(new Cookie("TGT", "test5"));
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        Assertions.assertEquals(SendTicketGrantingTicketActionSsoTests.SUCCESS, action.execute(this.context).getId());
        Assertions.assertEquals(0, response.getCookies().length);
    }

    @Test
    public void verifySsoSessionCookieOnServiceSsoDisallowed() throws Exception {
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        val svc = Mockito.mock(WebApplicationService.class);
        Mockito.when(svc.getId()).thenReturn("TestSsoFalse");
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getId()).thenReturn(SendTicketGrantingTicketActionSsoTests.TEST_STRING);
        request.setCookies(new Cookie("TGT", "test5"));
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        this.context.getFlowScope().put(PARAMETER_SERVICE, svc);
        Assertions.assertEquals(SendTicketGrantingTicketActionSsoTests.SUCCESS, action.execute(this.context).getId());
        Assertions.assertEquals(0, response.getCookies().length);
    }
}

