package org.apereo.cas.logout;


import LogoutRequestStatus.NOT_ATTEMPTED;
import LogoutRequestStatus.SUCCESS;
import RegisteredServiceLogoutType.BACK_CHANNEL;
import RegisteredServiceLogoutType.FRONT_CHANNEL;
import RegisteredServiceLogoutType.NONE;
import lombok.val;
import org.apereo.cas.authentication.principal.AbstractWebApplicationService;
import org.apereo.cas.logout.slo.DefaultSingleLogoutServiceMessageHandler;
import org.apereo.cas.services.AbstractRegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.util.http.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class DefaultLogoutManagerTests {
    private static final String ID = "id";

    private static final String URL = "http://www.github.com";

    private LogoutManager logoutManager;

    private TicketGrantingTicket tgt;

    private AbstractWebApplicationService simpleWebApplicationServiceImpl;

    private AbstractRegisteredService registeredService;

    @Mock
    private ServicesManager servicesManager;

    @Mock
    private HttpClient client;

    private DefaultSingleLogoutServiceMessageHandler singleLogoutServiceMessageHandler;

    public DefaultLogoutManagerTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyServiceLogoutUrlIsUsed() {
        this.registeredService.setLogoutUrl("https://www.apereo.org");
        val logoutRequests = this.logoutManager.performLogout(tgt);
        val logoutRequest = logoutRequests.iterator().next();
        Assertions.assertEquals(this.registeredService.getLogoutUrl(), logoutRequest.getLogoutUrl().toExternalForm());
    }

    @Test
    public void verifyLogoutDisabled() {
        val plan = new DefaultLogoutExecutionPlan();
        plan.registerSingleLogoutServiceMessageHandler(singleLogoutServiceMessageHandler);
        this.logoutManager = new DefaultLogoutManager(true, plan);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(0, logoutRequests.size());
    }

    @Test
    public void verifyLogoutAlreadyLoggedOut() {
        this.simpleWebApplicationServiceImpl.setLoggedOutAlready(true);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(0, logoutRequests.size());
    }

    @Test
    public void verifyLogoutTypeNotSet() {
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(1, logoutRequests.size());
        val logoutRequest = logoutRequests.iterator().next();
        Assertions.assertEquals(DefaultLogoutManagerTests.ID, logoutRequest.getTicketId());
        Assertions.assertEquals(this.simpleWebApplicationServiceImpl, logoutRequest.getService());
        Assertions.assertEquals(SUCCESS, logoutRequest.getStatus());
    }

    @Test
    public void verifyLogoutTypeBack() {
        this.registeredService.setLogoutType(BACK_CHANNEL);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(1, logoutRequests.size());
        val logoutRequest = logoutRequests.iterator().next();
        Assertions.assertEquals(DefaultLogoutManagerTests.ID, logoutRequest.getTicketId());
        Assertions.assertEquals(this.simpleWebApplicationServiceImpl, logoutRequest.getService());
        Assertions.assertEquals(SUCCESS, logoutRequest.getStatus());
    }

    @Test
    public void verifyLogoutTypeNone() {
        this.registeredService.setLogoutType(NONE);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(0, logoutRequests.size());
    }

    @Test
    public void verifyLogoutTypeNull() {
        this.registeredService.setLogoutType(null);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(1, logoutRequests.size());
        val logoutRequest = logoutRequests.iterator().next();
        Assertions.assertEquals(DefaultLogoutManagerTests.ID, logoutRequest.getTicketId());
    }

    @Test
    public void verifyLogoutTypeFront() {
        this.registeredService.setLogoutType(FRONT_CHANNEL);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(1, logoutRequests.size());
        val logoutRequest = logoutRequests.iterator().next();
        Assertions.assertEquals(DefaultLogoutManagerTests.ID, logoutRequest.getTicketId());
        Assertions.assertEquals(this.simpleWebApplicationServiceImpl, logoutRequest.getService());
        Assertions.assertEquals(NOT_ATTEMPTED, logoutRequest.getStatus());
    }

    @Test
    public void verifyAsynchronousLogout() {
        this.registeredService.setLogoutType(BACK_CHANNEL);
        val logoutRequests = this.logoutManager.performLogout(tgt);
        Assertions.assertEquals(1, logoutRequests.size());
    }
}

