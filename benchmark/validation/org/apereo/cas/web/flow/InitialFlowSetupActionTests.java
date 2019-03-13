package org.apereo.cas.web.flow;


import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.config.CasSupportActionsConfiguration;
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
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@TestPropertySource(properties = { "cas.authn.policy.any.tryAll=true", "spring.aop.proxy-target-class=true", "cas.ticket.st.timeToKillInSeconds=30" })
@SpringBootTest(classes = { CasRegisteredServicesTestConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreServicesConfiguration.class, CasSupportActionsConfiguration.class, CasCookieConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreLogoutConfiguration.class, CasCoreWebConfiguration.class, RefreshAutoConfiguration.class })
public class InitialFlowSetupActionTests extends AbstractWebflowActionsTests {
    @Autowired
    @Qualifier("initialFlowSetupAction")
    private Action action;

    @Test
    @SneakyThrows
    public void verifyNoServiceFound() {
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        val event = this.action.execute(context);
        Assertions.assertNull(WebUtils.getService(context));
        Assertions.assertEquals("success", event.getId());
    }

    @Test
    @SneakyThrows
    public void verifyServiceFound() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.setParameter("service", "test");
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val event = this.action.execute(context);
        Assertions.assertEquals("test", WebUtils.getService(context).getId());
        Assertions.assertNotNull(WebUtils.getRegisteredService(context));
        Assertions.assertEquals("success", event.getId());
    }
}

