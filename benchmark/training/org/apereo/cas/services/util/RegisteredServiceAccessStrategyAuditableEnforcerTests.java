package org.apereo.cas.services.util;


import lombok.val;
import org.apereo.cas.audit.AuditableContext;
import org.apereo.cas.services.RegisteredServiceAccessStrategyAuditableEnforcer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Class to test {@link RegisteredServiceAccessStrategyAuditableEnforcer}.
 *
 * @author Travis Schmidt
 * @since 6.1
 */
public class RegisteredServiceAccessStrategyAuditableEnforcerTests {
    @Test
    public void verifyRegisteredServicePresentAndEnabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        val context = AuditableContext.builder().registeredService(service).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyRegisteredServicePresentButDisabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(false);
        val context = AuditableContext.builder().registeredService(service).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyServiceAndRegisteredServicePresentAndEnabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyServiceAndRegisteredServicePresentButDisabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(false);
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyAuthAndServiceAndRegisteredServicePresentAndEnabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).authentication(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthentication()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyAuthAndServiceAndRegisteredServicePresentButDisabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(false);
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).authentication(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthentication()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyRejectedPrincipalAttributes() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        setRejectedAttributes(RegisteredServiceAccessStrategyAuditableEnforcerTests.reject(false));
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).authentication(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthentication()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyRejectedPrincipalAttributesNoFail() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        setRejectedAttributes(RegisteredServiceAccessStrategyAuditableEnforcerTests.reject(true));
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).authentication(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthentication()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyTgtAndServiceAndRegisteredServicePresentAndEnabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).ticketGrantingTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createTicketGrantingTicket()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyTgtAndServiceAndRegisteredServicePresentButDisabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(false);
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).ticketGrantingTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createTicketGrantingTicket()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyTgtRejectedPrincipalAttributes() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        setRejectedAttributes(RegisteredServiceAccessStrategyAuditableEnforcerTests.reject(false));
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).ticketGrantingTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createTicketGrantingTicket()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyTgtRejectedPrincipalAttributesNoFail() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        setRejectedAttributes(RegisteredServiceAccessStrategyAuditableEnforcerTests.reject(true));
        val context = AuditableContext.builder().registeredService(service).service(RegisteredServiceAccessStrategyAuditableEnforcerTests.createService()).ticketGrantingTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createTicketGrantingTicket()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyStAndServiceAndRegisteredServicePresentAndEnabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        val context = AuditableContext.builder().registeredService(service).serviceTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createServiceTicket()).authenticationResult(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthenticationResult()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyStAndServiceAndRegisteredServicePresentButDisabled() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(false);
        val context = AuditableContext.builder().registeredService(service).serviceTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createServiceTicket()).authenticationResult(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthenticationResult()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyStRejectedPrincipalAttributes() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        setRejectedAttributes(RegisteredServiceAccessStrategyAuditableEnforcerTests.reject(false));
        val context = AuditableContext.builder().registeredService(service).serviceTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createServiceTicket()).authenticationResult(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthenticationResult()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }

    @Test
    public void verifyStRejectedPrincipalAttributesNoFail() {
        val service = RegisteredServiceAccessStrategyAuditableEnforcerTests.createRegisteredService(true);
        setRejectedAttributes(RegisteredServiceAccessStrategyAuditableEnforcerTests.reject(true));
        val context = AuditableContext.builder().registeredService(service).serviceTicket(RegisteredServiceAccessStrategyAuditableEnforcerTests.createServiceTicket()).authenticationResult(RegisteredServiceAccessStrategyAuditableEnforcerTests.createAuthenticationResult()).build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertFalse(result.isExecutionFailure());
        Assertions.assertFalse(result.getException().isPresent());
    }

    @Test
    public void verifyExceptionNotThrown() {
        val context = AuditableContext.builder().build();
        val result = new RegisteredServiceAccessStrategyAuditableEnforcer().execute(context);
        Assertions.assertTrue(result.isExecutionFailure());
        Assertions.assertTrue(result.getException().isPresent());
    }
}

