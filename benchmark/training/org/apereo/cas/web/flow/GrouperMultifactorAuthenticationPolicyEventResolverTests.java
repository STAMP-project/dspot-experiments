package org.apereo.cas.web.flow;


import TestMultifactorAuthenticationProvider.ID;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
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
import org.apereo.cas.config.GrouperMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.grouper.GrouperFacade;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link GrouperMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasMultifactorAuthenticationWebflowConfiguration.class, CasCoreWebflowConfiguration.class, CasWebflowContextConfiguration.class, CasCoreServicesConfiguration.class, CasCoreWebConfiguration.class, CasCoreHttpConfiguration.class, CasCoreConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreTicketsConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCookieConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationServiceSelectionStrategyConfiguration.class, CasCoreUtilConfiguration.class, GrouperMultifactorAuthenticationPolicyEventResolverTests.GrouperTestConfiguration.class, GrouperMultifactorAuthenticationConfiguration.class })
@TestPropertySource(properties = "cas.authn.mfa.grouperGroupField=name")
public class GrouperMultifactorAuthenticationPolicyEventResolverTests {
    @Autowired
    @Qualifier("grouperMultifactorAuthenticationWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void verifyOperation() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        val targetResolver = new org.springframework.webflow.engine.support.DefaultTargetStateResolver(TestMultifactorAuthenticationProvider.ID);
        val transition = new org.springframework.webflow.engine.Transition(new DefaultTransitionCriteria(new org.springframework.binding.expression.support.LiteralExpression(TestMultifactorAuthenticationProvider.ID)), targetResolver);
        context.getRootFlow().getGlobalTransitionSet().add(transition);
        val event = resolver.resolve(context);
        Assertions.assertEquals(1, event.size());
        Assertions.assertEquals(ID, event.iterator().next().getId());
    }

    @TestConfiguration
    public static class GrouperTestConfiguration {
        @Bean
        public GrouperFacade grouperFacade() {
            val group = new WsGroup();
            group.setName(ID);
            group.setDisplayName("Apereo CAS");
            group.setDescription("CAS Authentication with Apereo");
            val result = new WsGetGroupsResult();
            result.setWsGroups(new WsGroup[]{ group });
            val facade = Mockito.mock(GrouperFacade.class);
            Mockito.when(facade.getGroupsForSubjectId(ArgumentMatchers.anyString())).thenReturn(CollectionUtils.wrapList(result));
            return facade;
        }
    }
}

