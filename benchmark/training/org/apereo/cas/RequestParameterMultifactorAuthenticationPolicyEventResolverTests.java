package org.apereo.cas;


import TestMultifactorAuthenticationProvider.ID;
import lombok.val;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link RequestParameterMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class RequestParameterMultifactorAuthenticationPolicyEventResolverTests extends BaseCasWebflowMultifactorAuthenticationTests {
    @Autowired
    @Qualifier("httpRequestAuthenticationPolicyWebflowEventResolver")
    private CasWebflowEventResolver requestParameterAuthenticationPolicyWebflowEventResolver;

    @Test
    public void verifyOperation() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        WebUtils.putRegisteredService(context, RegisteredServiceTestUtils.getRegisteredService());
        WebUtils.putAuthentication(RegisteredServiceTestUtils.getAuthentication(), context);
        WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService());
        var results = requestParameterAuthenticationPolicyWebflowEventResolver.resolve(context);
        Assertions.assertNull(results);
        val targetResolver = new org.springframework.webflow.engine.support.DefaultTargetStateResolver(TestMultifactorAuthenticationProvider.ID);
        val transition = new org.springframework.webflow.engine.Transition(new DefaultTransitionCriteria(new org.springframework.binding.expression.support.LiteralExpression(TestMultifactorAuthenticationProvider.ID)), targetResolver);
        context.getRootFlow().getGlobalTransitionSet().add(transition);
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        request.addParameter(casProperties.getAuthn().getMfa().getRequestParameter(), ID);
        results = requestParameterAuthenticationPolicyWebflowEventResolver.resolve(context);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(ID, results.iterator().next().getId());
    }
}

