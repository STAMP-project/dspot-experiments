package org.apereo.cas.oidc.discovery;


import org.apereo.cas.oidc.AbstractOidcTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OidcServerDiscoverySettingsFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcServerDiscoverySettingsFactoryTests extends AbstractOidcTests {
    @Test
    public void verifyAction() {
        Assertions.assertFalse(oidcServerDiscoverySettings.getClaimsSupported().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getClaimTypesSupported().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getGrantTypesSupported().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getIntrospectionSupportedAuthenticationMethods().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getIdTokenSigningAlgValuesSupported().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getSubjectTypesSupported().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getResponseTypesSupported().isEmpty());
        Assertions.assertFalse(oidcServerDiscoverySettings.getScopesSupported().isEmpty());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getEndSessionEndpoint());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getIntrospectionEndpoint());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getRegistrationEndpoint());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getTokenEndpoint());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getUserinfoEndpoint());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getIssuer());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getJwksUri());
        Assertions.assertNotNull(oidcServerDiscoverySettings.getServerPrefix());
    }
}

