package org.apereo.cas.support.oauth.web.flow;


import java.io.Serializable;
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
import org.apereo.cas.config.CasOAuthAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasOAuthWebflowConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.web.config.CasThemesConfiguration;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.flow.services.DefaultRegisteredServiceUserInterfaceInfo;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link OAuth20RegisteredServiceUIActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreServicesConfiguration.class, CasCoreUtilConfiguration.class, CasCoreWebflowConfiguration.class, CasCoreWebConfiguration.class, CasCoreConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasDefaultServiceTicketIdGeneratorsConfiguration.class, CasCoreHttpConfiguration.class, CasCoreLogoutConfiguration.class, CasWebflowContextConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCookieConfiguration.class, CasThemesConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasOAuthAuthenticationServiceSelectionStrategyConfiguration.class, CasOAuthWebflowConfiguration.class })
public class OAuth20RegisteredServiceUIActionTests {
    @Autowired
    @Qualifier("oauth20RegisteredServiceUIAction")
    private Action oauth20RegisteredServiceUIAction;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Test
    public void verifyOAuthActionWithoutMDUI() throws Exception {
        val ctx = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(ctx, RegisteredServiceTestUtils.getService());
        val event = oauth20RegisteredServiceUIAction.execute(ctx);
        Assertions.assertEquals("success", event.getId());
        val mdui = WebUtils.getServiceUserInterfaceMetadata(ctx, Serializable.class);
        Assertions.assertNull(mdui);
    }

    @Test
    public void verifyOAuthActionWithMDUI() throws Exception {
        val svc = new OAuthRegisteredService();
        svc.setClientId("id");
        svc.setName("oauth");
        svc.setDescription("description");
        svc.setClientSecret("secret");
        svc.setInformationUrl("info");
        svc.setPrivacyUrl("privacy");
        svc.setServiceId("https://oauth\\.example\\.org.*");
        svc.setLogo("logo");
        servicesManager.save(svc);
        val ctx = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(ctx, RegisteredServiceTestUtils.getService("https://www.example.org?client_id=id&client_secret=secret&redirect_uri=https://oauth.example.org"));
        val event = oauth20RegisteredServiceUIAction.execute(ctx);
        Assertions.assertEquals("success", event.getId());
        val mdui = WebUtils.getServiceUserInterfaceMetadata(ctx, DefaultRegisteredServiceUserInterfaceInfo.class);
        Assertions.assertNotNull(mdui);
        Assertions.assertEquals(mdui.getDisplayName(), svc.getName());
        Assertions.assertEquals(mdui.getInformationURL(), svc.getInformationUrl());
        Assertions.assertEquals(mdui.getDescription(), svc.getDescription());
        Assertions.assertEquals(mdui.getPrivacyStatementURL(), svc.getPrivacyUrl());
        Assertions.assertEquals(mdui.getLogoUrl(), svc.getLogo());
    }
}

