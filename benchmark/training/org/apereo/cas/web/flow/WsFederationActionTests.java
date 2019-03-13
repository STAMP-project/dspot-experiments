package org.apereo.cas.web.flow;


import WsFederationRequestBuilder.PARAMETER_NAME_WSFED_CLIENTS;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.CoreSamlConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.support.wsfederation.config.WsFederationAuthenticationConfiguration;
import org.apereo.cas.support.wsfederation.config.support.authentication.WsFedAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.flow.config.WsFederationAuthenticationWebflowConfiguration;
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
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link WsFederationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreWebflowConfiguration.class, CasWebflowContextConfiguration.class, CasCoreServicesConfiguration.class, CasCoreWebConfiguration.class, CasCoreHttpConfiguration.class, CasCoreConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreUtilConfiguration.class, CoreSamlConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, WsFedAuthenticationEventExecutionPlanConfiguration.class, WsFederationAuthenticationWebflowConfiguration.class, WsFederationAuthenticationConfiguration.class })
@TestPropertySource(locations = "classpath:wsfedauthn.properties")
public class WsFederationActionTests {
    @Autowired
    @Qualifier("wsFederationAction")
    protected Action wsFederationAction;

    @Test
    public void verifyRequestOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        wsFederationAction.execute(context);
        Assertions.assertTrue(context.getFlowScope().contains(PARAMETER_NAME_WSFED_CLIENTS));
    }
}

