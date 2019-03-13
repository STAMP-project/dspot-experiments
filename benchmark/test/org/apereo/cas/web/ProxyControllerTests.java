package org.apereo.cas.web;


import CasProtocolConstants.ERROR_CODE_INVALID_REQUEST_PROXY;
import CasProtocolConstants.PARAMETER_PROXY_GRANTING_TICKET;
import CasProtocolConstants.PARAMETER_TARGET_SERVICE;
import CasProtocolConstants.PARAMETER_TICKET;
import lombok.val;
import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.apereo.cas.web.config.CasProtocolViewsConfiguration;
import org.apereo.cas.web.config.CasValidationConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.thymeleaf.spring5.SpringTemplateEngine;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@SpringBootTest(classes = { CasWebApplicationServiceFactoryConfiguration.class, CasCoreServicesConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreTicketIdGeneratorsConfiguration.class, CasCoreConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreWebConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreTicketCatalogConfiguration.class, RefreshAutoConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, AopAutoConfiguration.class, ProxyControllerTests.ProxyTestConfiguration.class, CasProtocolViewsConfiguration.class, CasValidationConfiguration.class })
public class ProxyControllerTests extends AbstractCentralAuthenticationServiceTests {
    @Autowired
    @Qualifier("proxyController")
    private ProxyController proxyController;

    @Test
    public void verifyNoParams() {
        Assertions.assertEquals(ERROR_CODE_INVALID_REQUEST_PROXY, this.proxyController.handleRequestInternal(new MockHttpServletRequest(), new MockHttpServletResponse()).getModel().get("code"));
    }

    @Test
    public void verifyNonExistentPGT() {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET, "TestService");
        request.addParameter("targetService", "testDefault");
        Assertions.assertTrue(this.proxyController.handleRequestInternal(request, new MockHttpServletResponse()).getModel().containsKey("code"));
    }

    @Test
    public void verifyExistingPGT() {
        val ticket = new org.apereo.cas.ticket.ProxyGrantingTicketImpl(WebUtils.PARAMETER_TICKET_GRANTING_TICKET_ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        getTicketRegistry().addTicket(ticket);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET, ticket.getId());
        request.addParameter("targetService", "testDefault");
        Assertions.assertTrue(this.proxyController.handleRequestInternal(request, new MockHttpServletResponse()).getModel().containsKey(PARAMETER_TICKET));
    }

    @Test
    public void verifyNotAuthorizedPGT() {
        val ticket = new org.apereo.cas.ticket.ProxyGrantingTicketImpl(WebUtils.PARAMETER_TICKET_GRANTING_TICKET_ID, CoreAuthenticationTestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
        getTicketRegistry().addTicket(ticket);
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROXY_GRANTING_TICKET, ticket.getId());
        request.addParameter(PARAMETER_TARGET_SERVICE, "service");
        val map = this.proxyController.handleRequestInternal(request, new MockHttpServletResponse()).getModel();
        Assertions.assertFalse(map.containsKey(PARAMETER_TICKET));
    }

    @TestConfiguration
    public static class ProxyTestConfiguration {
        @Bean
        public SpringTemplateEngine springTemplateEngine() {
            return new SpringTemplateEngine();
        }

        @Bean
        public ThymeleafProperties thymeleafProperties() {
            return new ThymeleafProperties();
        }
    }
}

