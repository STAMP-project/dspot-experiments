package org.apereo.cas.adaptors.yubikey.dao;


import org.apereo.cas.adaptors.yubikey.YubiKeyAccountRegistry;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccountValidator;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.JpaYubiKeyConfiguration;
import org.apereo.cas.config.YubiKeyConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.config.support.authentication.YubiKeyAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.services.web.config.CasThemesConfiguration;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JpaYubiKeyAccountRegistryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { YubiKeyConfiguration.class, JpaYubiKeyConfiguration.class, JpaYubiKeyAccountRegistryTests.JpaYubiKeyAccountRegistryTestConfiguration.class, YubiKeyAuthenticationEventExecutionPlanConfiguration.class, CasCoreServicesConfiguration.class, CasWebflowContextConfiguration.class, CasCoreHttpConfiguration.class, AopAutoConfiguration.class, CasThemesConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreLogoutConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreWebflowConfiguration.class, CasCoreConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCookieConfiguration.class, CasCoreUtilConfiguration.class, CasCoreWebConfiguration.class, CasCoreHttpConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, RefreshAutoConfiguration.class })
@EnableConfigurationProperties(CasConfigurationProperties.class)
@TestPropertySource(locations = { "classpath:/yubikey-jpa.properties" })
public class JpaYubiKeyAccountRegistryTests {
    private static final String BAD_TOKEN = "123456";

    @Autowired
    @Qualifier("yubiKeyAccountRegistry")
    private YubiKeyAccountRegistry yubiKeyAccountRegistry;

    @Test
    public void verifyAccountNotRegistered() {
        Assertions.assertFalse(yubiKeyAccountRegistry.isYubiKeyRegisteredFor("missing-user"));
    }

    @Test
    public void verifyAccountNotRegisteredWithBadToken() {
        Assertions.assertFalse(yubiKeyAccountRegistry.registerAccountFor("casuser", JpaYubiKeyAccountRegistryTests.BAD_TOKEN));
        Assertions.assertFalse(yubiKeyAccountRegistry.isYubiKeyRegisteredFor("casuser"));
    }

    @Test
    public void verifyAccountRegistered() {
        Assertions.assertTrue(yubiKeyAccountRegistry.registerAccountFor("casuser", "cccccccvlidchlffblbghhckbctgethcrtdrruchvlud"));
        Assertions.assertTrue(yubiKeyAccountRegistry.isYubiKeyRegisteredFor("casuser"));
        Assertions.assertEquals(1, yubiKeyAccountRegistry.getAccounts().size());
    }

    @TestConfiguration("JpaYubiKeyAccountRegistryTestConfiguration")
    public static class JpaYubiKeyAccountRegistryTestConfiguration {
        @Bean
        @RefreshScope
        public YubiKeyAccountValidator yubiKeyAccountValidator() {
            return ( uid, token) -> !(token.equals(org.apereo.cas.adaptors.yubikey.dao.BAD_TOKEN));
        }
    }
}

