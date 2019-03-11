package org.apereo.cas.ws.idp.services;


import WSFederationClaims.COMMON_NAME;
import WSFederationClaims.EMAIL_ADDRESS;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link WSFederationClaimsReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class WSFederationClaimsReleasePolicyTests {
    @Test
    public void verifyAttributeReleaseNone() {
        val service = RegisteredServiceTestUtils.getRegisteredService("verifyAttributeRelease");
        val policy = new WSFederationClaimsReleasePolicy(CollectionUtils.wrap("uid", "casuser", "cn", "CAS"));
        val principal = CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("uid", "casuser", "cn", "CAS", "givenName", "CAS User"));
        val results = policy.getAttributes(principal, CoreAuthenticationTestUtils.getService(), service);
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    public void verifyAttributeRelease() {
        val service = RegisteredServiceTestUtils.getRegisteredService("verifyAttributeRelease");
        val policy = new WSFederationClaimsReleasePolicy(CollectionUtils.wrap(COMMON_NAME.name(), "cn", EMAIL_ADDRESS.name(), "email"));
        val principal = CoreAuthenticationTestUtils.getPrincipal("casuser", CollectionUtils.wrap("cn", "casuser", "email", "cas@example.org"));
        val results = policy.getAttributes(principal, CoreAuthenticationTestUtils.getService(), service);
        Assertions.assertSame(2, results.size());
        Assertions.assertTrue(results.containsKey(COMMON_NAME.getUri()));
        Assertions.assertTrue(results.containsKey(EMAIL_ADDRESS.getUri()));
    }
}

