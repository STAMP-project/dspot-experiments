package org.keycloak.social.openshift;


import org.junit.Test;
import org.keycloak.models.IdentityProviderModel;


public class OpenshiftV3IdentityProviderTest {
    @Test
    public void shouldConstructProviderUrls() throws Exception {
        final OpenshiftV3IdentityProviderConfig config = new OpenshiftV3IdentityProviderConfig(new IdentityProviderModel());
        config.setBaseUrl("http://openshift.io:8443");
        final OpenshiftV3IdentityProvider openshiftV3IdentityProvider = new OpenshiftV3IdentityProvider(null, config);
        assertConfiguredUrls(openshiftV3IdentityProvider);
    }

    @Test
    public void shouldConstructProviderUrlsForBaseUrlWithTrailingSlash() throws Exception {
        final OpenshiftV3IdentityProviderConfig config = new OpenshiftV3IdentityProviderConfig(new IdentityProviderModel());
        config.setBaseUrl("http://openshift.io:8443/");
        final OpenshiftV3IdentityProvider openshiftV3IdentityProvider = new OpenshiftV3IdentityProvider(null, config);
        assertConfiguredUrls(openshiftV3IdentityProvider);
    }
}

