package org.apereo.cas.discovery;


import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasDiscoveryProfileConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.support.pac4j.config.support.authentication.Pac4jAuthenticationEventExecutionPlanConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is {@link CasServerProfileRegistrarTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, Pac4jAuthenticationEventExecutionPlanConfiguration.class, CasDiscoveryProfileConfiguration.class, CasCoreServicesConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreUtilConfiguration.class, CasCoreAuditConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class })
@DirtiesContext
public class CasServerProfileRegistrarTests {
    @Autowired
    @Qualifier("casServerProfileRegistrar")
    private CasServerProfileRegistrar casServerProfileRegistrar;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void verifyAction() {
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val profile = casServerProfileRegistrar.getProfile();
        Assertions.assertNotNull(profile);
        Assertions.assertNotNull(profile.getAvailableAttributes());
        Assertions.assertNotNull(profile.getDelegatedClientTypes());
        Assertions.assertNotNull(profile.getDelegatedClientTypesSupported());
        Assertions.assertNotNull(profile.getMultifactorAuthenticationProviderTypes());
        Assertions.assertNotNull(profile.getMultifactorAuthenticationProviderTypesSupported());
        Assertions.assertNotNull(profile.getRegisteredServiceTypes());
        Assertions.assertNotNull(profile.getRegisteredServiceTypesSupported());
    }
}

