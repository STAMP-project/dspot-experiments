package org.apereo.cas.support.saml.idp.metadata;


import org.apereo.cas.config.AmazonS3SamlIdPMetadataConfiguration;
import org.apereo.cas.config.AmazonS3SamlMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.SamlIdPMetadataConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.support.saml.idp.metadata.generator.SamlIdPMetadataGenerator;
import org.apereo.cas.support.saml.idp.metadata.locator.SamlIdPMetadataLocator;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link AmazonS3SamlIdPMetadataGeneratorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, RefreshAutoConfiguration.class, CasCoreWebflowConfiguration.class, CasWebflowContextConfiguration.class, CasCoreServicesConfiguration.class, CasCoreWebConfiguration.class, CasCoreHttpConfiguration.class, CasCoreConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreTicketsConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCookieConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreUtilConfiguration.class, AmazonS3SamlMetadataConfiguration.class, AmazonS3SamlIdPMetadataConfiguration.class, SamlIdPMetadataConfiguration.class })
@TestPropertySource(properties = { "cas.authn.samlIdp.metadata.amazonS3.idpMetadataBucketName=thebucket", "cas.authn.samlIdp.metadata.amazonS3.endpoint=http://127.0.0.1:4572", "cas.authn.samlIdp.metadata.amazonS3.credentialAccessKey=test", "cas.authn.samlIdp.metadata.amazonS3.credentialSecretKey=test", "cas.authn.samlIdp.metadata.amazonS3.crypto.encryption.key=AZ5y4I9qzKPYUVNL2Td4RMbpg6Z-ldui8VEFg8hsj1M", "cas.authn.samlIdp.metadata.amazonS3.crypto.signing.key=cAPyoHMrOMWrwydOXzBA-ufZQM-TilnLjbRgMQWlUlwFmy07bOtAgCIdNBma3c5P4ae_JV6n1OpOAYqSh2NkmQ" })
@EnabledIfPortOpen(port = 4572)
@EnabledIfContinuousIntegration
@Tag("AmazonWebServicesS3")
public class AmazonS3SamlIdPMetadataGeneratorTests {
    @Autowired
    @Qualifier("samlIdPMetadataLocator")
    protected SamlIdPMetadataLocator samlIdPMetadataLocator;

    @Autowired
    @Qualifier("samlIdPMetadataGenerator")
    private SamlIdPMetadataGenerator samlIdPMetadataGenerator;

    static {
        System.setProperty("com.amazonaws.services.s3.disableGetObjectMD5Validation", "true");
    }

    @Test
    public void verifyOperation() {
        samlIdPMetadataGenerator.generate();
        Assertions.assertNotNull(samlIdPMetadataLocator.getMetadata());
        Assertions.assertNotNull(samlIdPMetadataLocator.getEncryptionCertificate());
        Assertions.assertNotNull(samlIdPMetadataLocator.getEncryptionKey());
        Assertions.assertNotNull(samlIdPMetadataLocator.getSigningCertificate());
        Assertions.assertNotNull(samlIdPMetadataLocator.getSigningKey());
    }
}

