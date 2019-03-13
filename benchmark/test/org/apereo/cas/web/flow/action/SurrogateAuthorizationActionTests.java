package org.apereo.cas.web.flow.action;


import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED;
import java.util.LinkedHashMap;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.PrincipalException;
import org.apereo.cas.services.SurrogateRegisteredServiceAccessStrategy;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SurrogateAuthorizationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SurrogateAuthorizationActionTests extends BaseSurrogateInitialAuthenticationActionTests {
    @Autowired
    @Qualifier("surrogateAuthorizationCheck")
    private Action surrogateAuthorizationCheck;

    @Test
    public void verifyAuthorized() {
        try {
            val context = new MockRequestContext();
            WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
            WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
            val registeredService = CoreAuthenticationTestUtils.getRegisteredService();
            val strategy = new SurrogateRegisteredServiceAccessStrategy();
            Mockito.when(registeredService.getAccessStrategy()).thenReturn(strategy);
            WebUtils.putRegisteredService(context, registeredService);
            val request = new MockHttpServletRequest();
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("success", surrogateAuthorizationCheck.execute(context).getId());
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    @SneakyThrows
    public void verifyNotAuthorized() {
        val context = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        val attributes = new LinkedHashMap<String, Object>();
        attributes.put(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true);
        attributes.putAll(CoreAuthenticationTestUtils.getAttributeRepository().getBackingMap());
        val p = CoreAuthenticationTestUtils.getPrincipal("casuser", attributes);
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(p), context);
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService();
        val strategy = new SurrogateRegisteredServiceAccessStrategy();
        strategy.setSurrogateEnabled(true);
        strategy.setSurrogateRequiredAttributes(CollectionUtils.wrap("surrogateAttribute", CollectionUtils.wrapSet("someValue")));
        Mockito.when(registeredService.getAccessStrategy()).thenReturn(strategy);
        WebUtils.putRegisteredService(context, registeredService);
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        Assertions.assertThrows(PrincipalException.class, () -> surrogateAuthorizationCheck.execute(context));
    }
}

