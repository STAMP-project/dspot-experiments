package org.apereo.cas.support.saml;


import net.shibboleth.utilities.java.support.xml.ParserPool;
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
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.CoreSamlConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.util.SchedulingUtils;
import org.apereo.cas.validation.config.CasCoreValidationConfiguration;
import org.apereo.cas.web.config.CasProtocolViewsConfiguration;
import org.apereo.cas.web.config.CasValidationConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring5.SpringTemplateEngine;


/**
 * OpenSaml context loading tests.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@SpringBootTest(classes = { AbstractOpenSamlTests.SamlTestConfiguration.class, CasRegisteredServicesTestConfiguration.class, CoreSamlConfiguration.class, RefreshAutoConfiguration.class, CasCoreWebConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreServicesConfiguration.class, CasCoreValidationConfiguration.class, CasProtocolViewsConfiguration.class, CasValidationConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreHttpConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreLogoutConfiguration.class, CasCoreUtilConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreConfiguration.class })
public abstract class AbstractOpenSamlTests {
    protected static final String SAML_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((("<samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " + "ID=\"5545454455\" Version=\"2.0\" IssueInstant=\"Value\" ") + "ProtocolBinding=\"urn:oasis:names.tc:SAML:2.0:bindings:HTTP-Redirect\" ") + "ProviderName=\"https://localhost:8443/myRutgers\" ") + "AssertionConsumerServiceURL=\"https://localhost:8443/myRutgers\"/>");

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    @Qualifier("shibboleth.OpenSAMLConfig")
    protected OpenSamlConfigBean configBean;

    @Autowired
    @Qualifier("shibboleth.ParserPool")
    protected ParserPool parserPool;

    @Autowired
    @Qualifier("shibboleth.BuilderFactory")
    protected XMLObjectBuilderFactory builderFactory;

    @Autowired
    @Qualifier("shibboleth.MarshallerFactory")
    protected MarshallerFactory marshallerFactory;

    @Autowired
    @Qualifier("shibboleth.UnmarshallerFactory")
    protected UnmarshallerFactory unmarshallerFactory;

    @Test
    public void autowireApplicationContext() {
        Assertions.assertNotNull(this.applicationContext);
        Assertions.assertNotNull(this.configBean);
        Assertions.assertNotNull(this.parserPool);
        Assertions.assertNotNull(this.builderFactory);
        Assertions.assertNotNull(this.unmarshallerFactory);
        Assertions.assertNotNull(this.marshallerFactory);
        Assertions.assertNotNull(this.configBean.getParserPool());
    }

    @Test
    public void loadStaticContextFactories() {
        Assertions.assertNotNull(XMLObjectProviderRegistrySupport.getParserPool());
        Assertions.assertNotNull(XMLObjectProviderRegistrySupport.getBuilderFactory());
        Assertions.assertNotNull(XMLObjectProviderRegistrySupport.getMarshallerFactory());
        Assertions.assertNotNull(XMLObjectProviderRegistrySupport.getUnmarshallerFactory());
    }

    @Test
    public void ensureParserIsInitialized() throws Exception {
        Assertions.assertNotNull(this.parserPool);
        Assertions.assertNotNull(this.parserPool.getBuilder());
    }

    @TestConfiguration
    public static class SamlTestConfiguration implements InitializingBean {
        @Autowired
        protected ApplicationContext applicationContext;

        @Bean
        public SpringTemplateEngine springTemplateEngine() {
            return new SpringTemplateEngine();
        }

        @Bean
        public ThymeleafProperties thymeleafProperties() {
            return new ThymeleafProperties();
        }

        @Override
        public void afterPropertiesSet() {
            SchedulingUtils.prepScheduledAnnotationBeanPostProcessor(applicationContext);
        }
    }
}

