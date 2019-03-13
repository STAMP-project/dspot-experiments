package org.pac4j.saml.client;


import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.util.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


/**
 * Generic tests on the {@link SAML2Client}.
 */
public final class SAML2ClientTests {
    public SAML2ClientTests() {
        Assert.assertNotNull(Configuration.getParserPool());
        Assert.assertNotNull(Configuration.getMarshallerFactory());
        Assert.assertNotNull(Configuration.getUnmarshallerFactory());
        Assert.assertNotNull(Configuration.getBuilderFactory());
    }

    @Test
    public void testIdpMetadataParsing_fromFile() {
        internalTestIdpMetadataParsing(new ClassPathResource("testshib-providers.xml"));
    }

    @Test
    public void testIdpMetadataParsing_fromUrl() throws MalformedURLException {
        internalTestIdpMetadataParsing(new UrlResource("http://www.pac4j.org/testshib-providers.xml"));
    }

    @Test
    public void testSaml2ConfigurationOfKeyStore() throws IOException {
        final Resource rs = new FileSystemResource("testKeystore.jks");
        if ((rs.exists()) && (!(rs.getFile().delete()))) {
            throw new TechnicalException("File could not be deleted");
        }
        final SAML2Configuration cfg = new SAML2Configuration("testKeystore.jks", "pac4j-test-passwd", "pac4j-test-passwd", "resource:testshib-providers.xml");
        cfg.init();
        final KeyStoreCredentialProvider p = new KeyStoreCredentialProvider(cfg);
        Assert.assertNotNull(p.getKeyInfoGenerator());
        Assert.assertNotNull(p.getCredentialResolver());
        Assert.assertNotNull(p.getKeyInfo());
        Assert.assertNotNull(p.getKeyInfoCredentialResolver());
        Assert.assertNotNull(p.getCredential());
    }

    @Test
    public void testSaml2ConfigurationOfKeyStoreUsingResource() throws IOException {
        final Resource rs = new FileSystemResource("testKeystore.jks");
        if ((rs.exists()) && (!(rs.getFile().delete()))) {
            throw new TechnicalException("File could not be deleted");
        }
        final SAML2Configuration cfg = new SAML2Configuration(new FileSystemResource("testKeystore.jks"), "pac4j-test-passwd", "pac4j-test-passwd", new ClassPathResource("testshib-providers.xml"));
        cfg.init();
        final KeyStoreCredentialProvider p = new KeyStoreCredentialProvider(cfg);
        Assert.assertNotNull(p.getKeyInfoGenerator());
        Assert.assertNotNull(p.getCredentialResolver());
        Assert.assertNotNull(p.getKeyInfo());
        Assert.assertNotNull(p.getKeyInfoCredentialResolver());
        Assert.assertNotNull(p.getCredential());
    }
}

