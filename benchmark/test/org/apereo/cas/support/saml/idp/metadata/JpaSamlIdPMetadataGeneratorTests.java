package org.apereo.cas.support.saml.idp.metadata;


import org.apereo.cas.support.saml.BaseJpaSamlMetadataTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JpaSamlIdPMetadataGeneratorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@TestPropertySource(properties = "cas.authn.samlIdp.metadata.jpa.idpMetadataEnabled=true")
public class JpaSamlIdPMetadataGeneratorTests extends BaseJpaSamlMetadataTests {
    @Test
    public void verifyOperation() {
        this.samlIdPMetadataGenerator.generate();
        Assertions.assertNotNull(samlIdPMetadataLocator.getMetadata());
        Assertions.assertNotNull(samlIdPMetadataLocator.getEncryptionCertificate());
        Assertions.assertNotNull(samlIdPMetadataLocator.getEncryptionKey());
        Assertions.assertNotNull(samlIdPMetadataLocator.getSigningCertificate());
        Assertions.assertNotNull(samlIdPMetadataLocator.getSigningKey());
    }
}

