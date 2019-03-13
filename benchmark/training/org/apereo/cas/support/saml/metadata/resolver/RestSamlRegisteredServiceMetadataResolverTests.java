package org.apereo.cas.support.saml.metadata.resolver;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.CoreSamlConfiguration;
import org.apereo.cas.config.SamlIdPAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.SamlIdPConfiguration;
import org.apereo.cas.config.SamlIdPEndpointsConfiguration;
import org.apereo.cas.config.SamlIdPMetadataConfiguration;
import org.apereo.cas.config.SamlIdPRestMetadataConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.cache.resolver.SamlRegisteredServiceMetadataResolver;
import org.apereo.cas.util.MockWebServer;
import org.apereo.cas.validation.config.CasCoreValidationConfiguration;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link RestSamlRegisteredServiceMetadataResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
@SpringBootTest(classes = { SamlIdPRestMetadataConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasCoreHttpConfiguration.class, CasCoreServicesConfiguration.class, CasCoreWebConfiguration.class, CasCoreWebflowConfiguration.class, SamlIdPConfiguration.class, SamlIdPAuthenticationServiceSelectionStrategyConfiguration.class, SamlIdPEndpointsConfiguration.class, SamlIdPMetadataConfiguration.class, RefreshAutoConfiguration.class, AopAutoConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreLogoutConfiguration.class, CasCookieConfiguration.class, CasCoreValidationConfiguration.class, CasCoreConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CoreSamlConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreUtilConfiguration.class })
@TestPropertySource(properties = { "cas.authn.samlIdp.metadata.rest.url=http://localhost:8078", "cas.authn.samlIdp.metadata.location=file:/tmp" })
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class RestSamlRegisteredServiceMetadataResolverTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("restSamlRegisteredServiceMetadataResolver")
    private SamlRegisteredServiceMetadataResolver resolver;

    private MockWebServer webServer;

    @Test
    public void verifyRestEndpointProducesMetadata() {
        val service = new SamlRegisteredService();
        service.setName("SAML Wiki Service");
        service.setServiceId("https://carmenwiki.osu.edu/shibboleth");
        service.setDescription("Testing");
        service.setMetadataLocation("rest://");
        Assertions.assertTrue(resolver.supports(service));
        val resolvers = resolver.resolve(service);
        Assertions.assertEquals(1, resolvers.size());
    }
}

