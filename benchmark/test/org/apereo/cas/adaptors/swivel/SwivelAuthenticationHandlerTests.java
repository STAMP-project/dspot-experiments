package org.apereo.cas.adaptors.swivel;


import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
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
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.SwivelAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.SwivelConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.services.web.config.CasThemesConfiguration;
import org.apereo.cas.util.MockWebServer;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SwivelAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreHttpConfiguration.class, CasCoreUtilConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreServicesConfiguration.class, CasCoreWebflowConfiguration.class, CasWebflowContextConfiguration.class, CasCoreConfiguration.class, CasCoreLogoutConfiguration.class, CasCookieConfiguration.class, CasThemesConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, SwivelConfiguration.class, SwivelAuthenticationEventExecutionPlanConfiguration.class })
@TestPropertySource(properties = { "cas.authn.mfa.swivel.swivelUrl=http://localhost:9191", "cas.authn.mfa.swivel.sharedSecret=$ecret", "cas.authn.mfa.swivel.ignoreSslErrors=true" })
public class SwivelAuthenticationHandlerTests {
    @Autowired
    @Qualifier("swivelAuthenticationHandler")
    private AuthenticationHandler swivelAuthenticationHandler;

    private MockWebServer webServer;

    @Test
    public void verifySupports() {
        Assertions.assertFalse(swivelAuthenticationHandler.supports(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
        Assertions.assertTrue(swivelAuthenticationHandler.supports(new SwivelTokenCredential("123456")));
    }

    @Test
    @SneakyThrows
    public void verifyAuthn() {
        val c = new SwivelTokenCredential("123456");
        val context = new MockRequestContext();
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        RequestContextHolder.setRequestContext(context);
        Assertions.assertNotNull(swivelAuthenticationHandler.authenticate(c));
    }
}

