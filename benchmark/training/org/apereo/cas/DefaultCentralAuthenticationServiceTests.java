package org.apereo.cas;


import ProxyGrantingTicket.PROXY_GRANTING_TICKET_PREFIX;
import ProxyTicket.PROXY_TICKET_PREFIX;
import lombok.val;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PrincipalException;
import org.apereo.cas.authentication.exceptions.MixedPrincipalException;
import org.apereo.cas.authentication.principal.AbstractWebApplicationService;
import org.apereo.cas.logout.LogoutManager;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.services.UnauthorizedSsoServiceException;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.apereo.cas.util.MockOnlyOneTicketRegistry;
import org.apereo.cas.validation.Cas20WithoutProxyingValidationSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class DefaultCentralAuthenticationServiceTests extends AbstractCentralAuthenticationServiceTests {
    @Test
    public void verifyBadCredentialsOnTicketGrantingTicketCreation() {
        Assertions.assertThrows(AuthenticationException.class, () -> CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword()));
    }

    @Test
    public void verifyGoodCredentialsOnTicketGrantingTicketCreation() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        Assertions.assertNotNull(getCentralAuthenticationService().createTicketGrantingTicket(ctx));
    }

    @Test
    public void verifyDestroyTicketGrantingTicketWithNonExistingTicket() {
        getCentralAuthenticationService().destroyTicketGrantingTicket("test");
    }

    @Test
    public void verifyDestroyTicketGrantingTicketWithValidTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketId.getId());
    }

    @Test
    public void verifyDisallowNullCredentialsWhenCreatingTicketGrantingTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), new Credential[]{ null });
        Assertions.assertThrows(RuntimeException.class, () -> getCentralAuthenticationService().createTicketGrantingTicket(ctx));
    }

    @Test
    public void verifyDisallowNullCredentialsArrayWhenCreatingTicketGrantingTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), new Credential[]{ null, null });
        Assertions.assertThrows(RuntimeException.class, () -> getCentralAuthenticationService().createTicketGrantingTicket(ctx));
    }

    @Test
    public void verifyDestroyTicketGrantingTicketWithInvalidTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicketId = getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        Assertions.assertThrows(ClassCastException.class, () -> getCentralAuthenticationService().destroyTicketGrantingTicket(serviceTicketId.getId()));
    }

    @Test
    public void verifyGrantingOfServiceTicketUsingDefaultTicketIdGen() {
        val mockService = RegisteredServiceTestUtils.getService("testDefault");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), mockService);
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicketId = getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), mockService, ctx);
        Assertions.assertNotNull(serviceTicketId);
    }

    @Test
    public void verifyGrantServiceTicketWithValidTicketGrantingTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
    }

    @Test
    public void verifyGrantServiceTicketFailsAuthzRule() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService("TestServiceAttributeForAuthzFails"));
        Assertions.assertThrows(PrincipalException.class, () -> getCentralAuthenticationService().createTicketGrantingTicket(ctx));
    }

    @Test
    public void verifyGrantServiceTicketPassesAuthzRule() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService("TestServiceAttributeForAuthzPasses"));
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService("TestServiceAttributeForAuthzPasses"), ctx);
    }

    @Test
    public void verifyGrantProxyTicketWithValidTicketGrantingTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicketId = getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        val ctx2 = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), RegisteredServiceTestUtils.getHttpBasedServiceCredentials());
        val pgt = getCentralAuthenticationService().createProxyGrantingTicket(serviceTicketId.getId(), ctx2);
        val pt = getCentralAuthenticationService().grantProxyTicket(pgt.getId(), DefaultCentralAuthenticationServiceTests.getService());
        Assertions.assertTrue(pt.getId().startsWith(PROXY_TICKET_PREFIX));
    }

    @Test
    public void verifyGrantServiceTicketWithInvalidTicketGrantingTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketId.getId());
        Assertions.assertThrows(AbstractTicketException.class, () -> getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx));
    }

    @Test
    public void verifyDelegateTicketGrantingTicketWithProperParams() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicketId = getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        val ctx2 = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), RegisteredServiceTestUtils.getHttpBasedServiceCredentials());
        val pgt = getCentralAuthenticationService().createProxyGrantingTicket(serviceTicketId.getId(), ctx2);
        Assertions.assertTrue(pgt.getId().startsWith(PROXY_GRANTING_TICKET_PREFIX));
    }

    @Test
    public void verifyProxyGrantingTicketHasRootAuthenticationAsPrincipal() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService());
        val ticket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicketId = getCentralAuthenticationService().grantServiceTicket(ticket.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        val service = ((AbstractWebApplicationService) (serviceTicketId.getService()));
        Assertions.assertEquals(service.getPrincipal(), ticket.getAuthentication().getPrincipal().getId());
    }

    @Test
    public void verifyDelegateTicketGrantingTicketWithBadServiceTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService());
        val ticketId = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicketId = getCentralAuthenticationService().grantServiceTicket(ticketId.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketId.getId());
        val ctx2 = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), RegisteredServiceTestUtils.getHttpBasedServiceCredentials());
        Assertions.assertThrows(AbstractTicketException.class, () -> getCentralAuthenticationService().createProxyGrantingTicket(serviceTicketId.getId(), ctx2));
    }

    @Test
    public void verifyGrantServiceTicketWithValidCredentials() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService());
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
    }

    @Test
    public void verifyGrantServiceTicketWithDifferentCredentials() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("testA"));
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val ctx2 = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("testB"));
        Assertions.assertThrows(MixedPrincipalException.class, () -> getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx2));
    }

    @Test
    public void verifyValidateServiceTicketWithValidService() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), DefaultCentralAuthenticationServiceTests.getService());
    }

    @Test
    public void verifyValidateServiceTicketWithInvalidService() {
        val service = DefaultCentralAuthenticationServiceTests.getService("badtestservice");
        Assertions.assertThrows(UnauthorizedServiceException.class, () -> CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service));
    }

    @Test
    public void verifyValidateServiceTicketWithInvalidServiceTicket() {
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService());
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        getCentralAuthenticationService().destroyTicketGrantingTicket(ticketGrantingTicket.getId());
        Assertions.assertThrows(AbstractTicketException.class, () -> getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), DefaultCentralAuthenticationServiceTests.getService()));
    }

    @Test
    public void verifyValidateServiceTicketNonExistantTicket() {
        Assertions.assertThrows(AbstractTicketException.class, () -> getCentralAuthenticationService().validateServiceTicket("google", DefaultCentralAuthenticationServiceTests.getService()));
    }

    @Test
    public void verifyValidateServiceTicketWithoutUsernameAttribute() {
        val cred = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), DefaultCentralAuthenticationServiceTests.getService());
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), DefaultCentralAuthenticationServiceTests.getService(), ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), DefaultCentralAuthenticationServiceTests.getService());
        val auth = assertion.getPrimaryAuthentication();
        Assertions.assertEquals(auth.getPrincipal().getId(), cred.getUsername());
    }

    @Test
    public void verifyValidateServiceTicketWithDefaultUsernameAttribute() {
        val svc = DefaultCentralAuthenticationServiceTests.getService("testDefault");
        val cred = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), svc, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), svc);
        val auth = assertion.getPrimaryAuthentication();
        Assertions.assertEquals(auth.getPrincipal().getId(), cred.getUsername());
    }

    @Test
    public void verifyValidateServiceTicketWithUsernameAttribute() {
        val svc = DefaultCentralAuthenticationServiceTests.getService("eduPersonTest");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), svc, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), svc);
        Assertions.assertEquals("developer", assertion.getPrimaryAuthentication().getPrincipal().getId());
    }

    @Test
    public void verifyGrantServiceTicketWithCredsAndSsoFalse() {
        val svc = DefaultCentralAuthenticationServiceTests.getService("TestSsoFalse");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), svc, ctx);
        Assertions.assertNotNull(serviceTicket);
    }

    @Test
    public void verifyGrantServiceTicketWithNoCredsAndSsoFalse() {
        val svc = DefaultCentralAuthenticationServiceTests.getService("TestSsoFalse");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        Assertions.assertNotNull(getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), svc, ctx));
    }

    @Test
    public void verifyGrantServiceTicketWithNoCredsAndSsoFalseAndSsoFalse() {
        val svc = DefaultCentralAuthenticationServiceTests.getService("TestSsoFalse");
        val ctx = Mockito.mock(AuthenticationResult.class);
        Mockito.when(ctx.getAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication());
        Mockito.when(ctx.isCredentialProvided()).thenReturn(true);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val service = DefaultCentralAuthenticationServiceTests.getService("eduPersonTest");
        getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), service, ctx);
        Mockito.when(ctx.isCredentialProvided()).thenReturn(false);
        Assertions.assertThrows(UnauthorizedSsoServiceException.class, () -> getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), svc, ctx));
    }

    @Test
    public void verifyValidateServiceTicketNoAttributesReturned() {
        val service = DefaultCentralAuthenticationServiceTests.getService();
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), service, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), service);
        val auth = assertion.getPrimaryAuthentication();
        Assertions.assertEquals(0, auth.getPrincipal().getAttributes().size());
    }

    @Test
    public void verifyValidateServiceTicketReturnAllAttributes() {
        val service = DefaultCentralAuthenticationServiceTests.getService("eduPersonTest");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), service, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), service);
        val auth = assertion.getPrimaryAuthentication();
        Assertions.assertEquals(3, auth.getPrincipal().getAttributes().size());
    }

    @Test
    public void verifyValidateServiceTicketReturnOnlyAllowedAttribute() {
        val service = DefaultCentralAuthenticationServiceTests.getService("eduPersonTestInvalid");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), service, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), service);
        val auth = assertion.getPrimaryAuthentication();
        val attributes = auth.getPrincipal().getAttributes();
        Assertions.assertEquals(1, attributes.size());
        Assertions.assertEquals("adopters", attributes.get("groupMembership"));
    }

    @Test
    public void verifyValidateServiceTicketAnonymous() {
        val service = DefaultCentralAuthenticationServiceTests.getService("testAnonymous");
        val cred = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), service);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), service, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), service);
        val auth = assertion.getPrimaryAuthentication();
        Assertions.assertNotEquals(cred.getUsername(), auth.getPrincipal().getId());
    }

    @Test
    public void verifyValidateServiceTicketWithInvalidUsernameAttribute() {
        val svc = DefaultCentralAuthenticationServiceTests.getService("eduPersonTestInvalid");
        val cred = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        val serviceTicket = getCentralAuthenticationService().grantServiceTicket(ticketGrantingTicket.getId(), svc, ctx);
        val assertion = getCentralAuthenticationService().validateServiceTicket(serviceTicket.getId(), svc);
        val auth = assertion.getPrimaryAuthentication();
        /* The attribute specified for this service does not resolve.
        Therefore, we expect the default to be returned.
         */
        Assertions.assertEquals(auth.getPrincipal().getId(), cred.getUsername());
    }

    /**
     * This test simulates :
     * - a first authentication for a default service
     * - a second authentication with the renew parameter and the same service (and same credentials)
     * - a validation of the second ticket.
     * When supplemental authentications were returned with the chained authentications, the validation specification
     * failed as it only expects one authentication. Thus supplemental authentications should not be returned in the
     * chained authentications. Both concepts are orthogonal.
     */
    @Test
    public void verifyAuthenticateTwiceWithRenew() throws AuthenticationException, AbstractTicketException {
        val cas = getCentralAuthenticationService();
        val svc = DefaultCentralAuthenticationServiceTests.getService("testDefault");
        val ctx = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport(), svc);
        val tgtId = cas.createTicketGrantingTicket(ctx);
        cas.grantServiceTicket(tgtId.getId(), svc, ctx);
        // simulate renew with new good same credentials
        val st2Id = cas.grantServiceTicket(tgtId.getId(), svc, ctx);
        val assertion = cas.validateServiceTicket(st2Id.getId(), svc);
        val validationSpecification = new Cas20WithoutProxyingValidationSpecification();
        Assertions.assertTrue(validationSpecification.isSatisfiedBy(assertion, new MockHttpServletRequest()));
    }

    /**
     * This test checks that the TGT destruction happens properly for a remote registry.
     * It previously failed when the deletion happens before the ticket was marked expired because an update was necessary for that.
     */
    @Test
    public void verifyDestroyRemoteRegistry() throws AuthenticationException, AbstractTicketException {
        val registry = new MockOnlyOneTicketRegistry();
        val tgt = new TicketGrantingTicketImpl("TGT-1", Mockito.mock(Authentication.class), Mockito.mock(ExpirationPolicy.class));
        val logoutManager = Mockito.mock(LogoutManager.class);
        Mockito.when(logoutManager.performLogout(ArgumentMatchers.any(TicketGrantingTicket.class))).thenAnswer(( invocation) -> {
            tgt.markTicketExpired();
            registry.updateTicket(tgt);
            return null;
        });
        registry.addTicket(tgt);
        val cas = new DefaultCentralAuthenticationService(Mockito.mock(ApplicationEventPublisher.class), registry, null, logoutManager, null, null, null, null, null, Mockito.mock(AuditableExecution.class));
        cas.destroyTicketGrantingTicket(tgt.getId());
    }
}

