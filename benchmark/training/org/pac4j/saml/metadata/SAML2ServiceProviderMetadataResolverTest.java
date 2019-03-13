package org.pac4j.saml.metadata;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;


public class SAML2ServiceProviderMetadataResolverTest {
    private SAML2ServiceProviderMetadataResolver metadataResolver;

    private SAML2Configuration configuration;

    @Test
    public void resolveServiceProviderMetadata() {
        metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration, "http://localhost", new org.pac4j.saml.crypto.KeyStoreCredentialProvider(configuration));
        Assert.assertNotNull(metadataResolver.resolve());
    }
}

