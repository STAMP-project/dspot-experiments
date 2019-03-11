package org.apereo.cas.web.flow;


import CasProtocolConstants.PARAMETER_RENEW;
import CasProtocolConstants.PARAMETER_SERVICE;
import CasWebflowConstants.STATE_ID_WARN;
import CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE;
import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import RegisteredServiceTestUtils.CONST_TEST_URL;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class AuthenticationViaFormActionTests extends AbstractWebflowActionsTests {
    private static final String TEST = "test";

    private static final String USERNAME_PARAM = "username";

    private static final String PASSWORD_PARAM = "password";

    @Autowired
    @Qualifier("authenticationViaFormAction")
    private Action action;

    @Autowired
    @Qualifier("warnCookieGenerator")
    private CookieGenerator warnCookieGenerator;

    @Test
    public void verifySuccessfulAuthenticationWithNoService() throws Exception {
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        request.addParameter(AuthenticationViaFormActionTests.USERNAME_PARAM, AuthenticationViaFormActionTests.TEST);
        request.addParameter(AuthenticationViaFormActionTests.PASSWORD_PARAM, AuthenticationViaFormActionTests.TEST);
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, c);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, this.action.execute(context).getId());
    }

    @Test
    public void verifySuccessfulAuthenticationWithNoServiceAndWarn() throws Exception {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val context = new MockRequestContext();
        request.addParameter(AuthenticationViaFormActionTests.USERNAME_PARAM, AuthenticationViaFormActionTests.TEST);
        request.addParameter(AuthenticationViaFormActionTests.PASSWORD_PARAM, AuthenticationViaFormActionTests.TEST);
        request.addParameter("warn", "true");
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, c);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, this.action.execute(context).getId());
    }

    @Test
    public void verifySuccessfulAuthenticationWithServiceAndWarn() throws Exception {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val context = new MockRequestContext();
        request.addParameter(AuthenticationViaFormActionTests.USERNAME_PARAM, AuthenticationViaFormActionTests.TEST);
        request.addParameter(AuthenticationViaFormActionTests.PASSWORD_PARAM, AuthenticationViaFormActionTests.TEST);
        request.addParameter("warn", "true");
        request.addParameter(PARAMETER_SERVICE, AuthenticationViaFormActionTests.TEST);
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, c);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, this.action.execute(context).getId());
        Assertions.assertNotNull(response.getCookie(this.warnCookieGenerator.getCookieName()));
    }

    @Test
    public void verifyFailedAuthenticationWithNoService() throws Exception {
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        request.addParameter(AuthenticationViaFormActionTests.USERNAME_PARAM, AuthenticationViaFormActionTests.TEST);
        request.addParameter(AuthenticationViaFormActionTests.PASSWORD_PARAM, "test2");
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword();
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, c);
        context.getRequestScope().put("org.springframework.validation.BindException.credentials", new org.springframework.validation.BindException(c, "credential"));
        Assertions.assertEquals(TRANSITION_ID_AUTHENTICATION_FAILURE, this.action.execute(context).getId());
    }

    @Test
    public void verifyRenewWithServiceAndSameCredentials() throws Exception {
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val service = RegisteredServiceTestUtils.getService(CONST_TEST_URL);
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service, c);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        WebUtils.putTicketGrantingTicketInScopes(context, ticketGrantingTicket);
        request.addParameter(PARAMETER_RENEW, "true");
        request.addParameter(PARAMETER_SERVICE, RegisteredServiceTestUtils.getService(CONST_TEST_URL).getId());
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        context.getFlowScope().put(PARAMETER_SERVICE, RegisteredServiceTestUtils.getService());
        val ev = this.action.execute(context);
        Assertions.assertEquals(STATE_ID_WARN, ev.getId());
    }

    @Test
    public void verifyRenewWithServiceAndDifferentCredentials() throws Exception {
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), RegisteredServiceTestUtils.getService(AuthenticationViaFormActionTests.TEST), c);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        WebUtils.putTicketGrantingTicketInScopes(context, ticketGrantingTicket);
        request.addParameter(PARAMETER_RENEW, "true");
        request.addParameter(PARAMETER_SERVICE, RegisteredServiceTestUtils.getService(AuthenticationViaFormActionTests.TEST).getId());
        val c2 = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, c2);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, this.action.execute(context).getId());
    }

    @Test
    public void verifyRenewWithServiceAndBadCredentials() throws Exception {
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val service = RegisteredServiceTestUtils.getService(AuthenticationViaFormActionTests.TEST);
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service, c);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        WebUtils.putTicketGrantingTicketInScopes(context, ticketGrantingTicket);
        request.addParameter(PARAMETER_RENEW, "true");
        request.addParameter(PARAMETER_SERVICE, service.getId());
        val c2 = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        AuthenticationViaFormActionTests.putCredentialInRequestScope(context, c2);
        Assertions.assertEquals(TRANSITION_ID_AUTHENTICATION_FAILURE, this.action.execute(context).getId());
    }
}

