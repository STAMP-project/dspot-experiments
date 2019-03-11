package org.apereo.cas.web.flow;


import CasProtocolConstants.PARAMETER_SERVICE;
import CasWebflowConstants.TRANSITION_ID_FINISH;
import CasWebflowConstants.TRANSITION_ID_FRONT;
import LogoutRequestStatus.SUCCESS;
import java.util.Collections;
import javax.servlet.http.Cookie;
import lombok.val;
import org.apereo.cas.configuration.model.core.logout.LogoutProperties;
import org.apereo.cas.logout.DefaultSingleLogoutRequest;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.DefaultServicesManager;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.web.flow.logout.LogoutAction;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.execution.RequestContext;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class LogoutActionTests extends AbstractWebflowActionsTests {
    private static final String COOKIE_TGC_ID = "CASTGC";

    private static final String TEST_SERVICE_ID = "TestService";

    private LogoutAction logoutAction;

    private DefaultServicesManager serviceManager;

    private MockHttpServletRequest request;

    private RequestContext requestContext;

    @Test
    public void verifyLogoutNoCookie() {
        val properties = new LogoutProperties();
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
    }

    @Test
    public void verifyLogoutForServiceWithFollowRedirectsAndMatchingService() {
        this.request.addParameter("service", LogoutActionTests.TEST_SERVICE_ID);
        val impl = new RegexRegisteredService();
        impl.setServiceId(LogoutActionTests.TEST_SERVICE_ID);
        impl.setName(LogoutActionTests.TEST_SERVICE_ID);
        this.serviceManager.save(impl);
        val properties = new LogoutProperties();
        properties.setFollowServiceRedirects(true);
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
        Assertions.assertEquals(LogoutActionTests.TEST_SERVICE_ID, this.requestContext.getFlowScope().get("logoutRedirectUrl"));
    }

    @Test
    public void logoutForServiceWithNoFollowRedirects() {
        this.request.addParameter(PARAMETER_SERVICE, LogoutActionTests.TEST_SERVICE_ID);
        val properties = new LogoutProperties();
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
        Assertions.assertNull(this.requestContext.getFlowScope().get("logoutRedirectUrl"));
    }

    @Test
    public void logoutForServiceWithFollowRedirectsNoAllowedService() {
        this.request.addParameter(PARAMETER_SERVICE, LogoutActionTests.TEST_SERVICE_ID);
        val impl = new RegexRegisteredService();
        impl.setServiceId("http://FooBar");
        impl.setName("FooBar");
        this.serviceManager.save(impl);
        val properties = new LogoutProperties();
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
        Assertions.assertNull(this.requestContext.getFlowScope().get("logoutRedirectUrl"));
    }

    @Test
    public void verifyLogoutCookie() {
        val cookie = new Cookie(LogoutActionTests.COOKIE_TGC_ID, "test");
        this.request.setCookies(cookie);
        val properties = new LogoutProperties();
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
    }

    @Test
    public void verifyLogoutRequestBack() {
        val cookie = new Cookie(LogoutActionTests.COOKIE_TGC_ID, "test");
        this.request.setCookies(cookie);
        val logoutRequest = DefaultSingleLogoutRequest.builder().registeredService(RegisteredServiceTestUtils.getRegisteredService()).ticketGrantingTicket(new MockTicketGrantingTicket("casuser")).build();
        logoutRequest.setStatus(SUCCESS);
        WebUtils.putLogoutRequests(this.requestContext, Collections.singletonList(logoutRequest));
        val properties = new LogoutProperties();
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FINISH, event.getId());
    }

    @Test
    public void verifyLogoutRequestFront() {
        val cookie = new Cookie(LogoutActionTests.COOKIE_TGC_ID, "test");
        this.request.setCookies(cookie);
        val logoutRequest = DefaultSingleLogoutRequest.builder().registeredService(RegisteredServiceTestUtils.getRegisteredService()).ticketGrantingTicket(new MockTicketGrantingTicket("casuser")).build();
        WebUtils.putLogoutRequests(this.requestContext, Collections.singletonList(logoutRequest));
        val properties = new LogoutProperties();
        this.logoutAction = new LogoutAction(getWebApplicationServiceFactory(), this.serviceManager, properties);
        val event = this.logoutAction.doExecute(this.requestContext);
        Assertions.assertEquals(TRANSITION_ID_FRONT, event.getId());
        val logoutRequests = WebUtils.getLogoutRequests(this.requestContext);
        Assertions.assertEquals(1, logoutRequests.size());
        Assertions.assertEquals(logoutRequest, logoutRequests.get(0));
    }
}

