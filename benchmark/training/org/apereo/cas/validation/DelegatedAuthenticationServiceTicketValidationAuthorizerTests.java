package org.apereo.cas.validation;


import ClientCredential.AUTHENTICATION_ATTRIBUTE_CLIENT_NAME;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.DefaultRegisteredServiceDelegatedAuthenticationPolicy;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * This is {@link DelegatedAuthenticationServiceTicketValidationAuthorizerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DelegatedAuthenticationServiceTicketValidationAuthorizerTests {
    @Test
    public void verifyAction() {
        val servicesManager = Mockito.mock(ServicesManager.class);
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService();
        val policy = new DefaultRegisteredServiceDelegatedAuthenticationPolicy();
        policy.setAllowedProviders(CollectionUtils.wrapList("SomeClient"));
        Mockito.when(registeredService.getAccessStrategy().getDelegatedAuthenticationPolicy()).thenReturn(policy);
        Mockito.when(servicesManager.findServiceBy(ArgumentMatchers.any(Service.class))).thenReturn(registeredService);
        val assertion = Mockito.mock(Assertion.class);
        val principal = CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap(AUTHENTICATION_ATTRIBUTE_CLIENT_NAME, "CasClient"));
        Mockito.when(assertion.getPrimaryAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication(principal, principal.getAttributes()));
        val az = new DelegatedAuthenticationServiceTicketValidationAuthorizer(servicesManager, new RegisteredServiceDelegatedAuthenticationPolicyAuditableEnforcer());
        Assertions.assertThrows(UnauthorizedServiceException.class, () -> az.authorize(new MockHttpServletRequest(), CoreAuthenticationTestUtils.getService(), assertion));
    }
}

