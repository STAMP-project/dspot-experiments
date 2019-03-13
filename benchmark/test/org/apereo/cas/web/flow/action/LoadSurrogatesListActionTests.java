package org.apereo.cas.web.flow.action;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED;
import SurrogateWebflowConfigurer.TRANSITION_ID_SKIP_SURROGATE;
import SurrogateWebflowConfigurer.TRANSITION_ID_SURROGATE_VIEW;
import java.util.LinkedHashMap;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationResultBuilder;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.SurrogateUsernamePasswordCredential;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link LoadSurrogatesListActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class LoadSurrogatesListActionTests extends BaseSurrogateInitialAuthenticationActionTests {
    @Autowired
    @Qualifier("loadSurrogatesListAction")
    private Action loadSurrogatesListAction;

    @Test
    public void verifyGetListView() {
        try {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            WebUtils.putRequestSurrogateAuthentication(context, true);
            WebUtils.putCredential(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
            Assertions.assertEquals(TRANSITION_ID_SURROGATE_VIEW, loadSurrogatesListAction.execute(context).getId());
            Assertions.assertNotNull(WebUtils.getSurrogateAuthenticationAccounts(context));
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void verifyAuthenticate() throws Exception {
        val context = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        val attributes = new LinkedHashMap<String, Object>();
        attributes.put(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true);
        attributes.putAll(CoreAuthenticationTestUtils.getAttributeRepository().getBackingMap());
        val p = CoreAuthenticationTestUtils.getPrincipal("casuser", attributes);
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(p), context);
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val creds = new SurrogateUsernamePasswordCredential();
        creds.setPassword("Mellon");
        creds.setUsername("casuser");
        creds.setSurrogateUsername("cassurrogate");
        WebUtils.putCredential(context, creds);
        val builder = Mockito.mock(AuthenticationResultBuilder.class);
        Mockito.when(builder.getInitialAuthentication()).thenReturn(Optional.of(CoreAuthenticationTestUtils.getAuthentication()));
        Mockito.when(builder.collect(ArgumentMatchers.any(Authentication.class))).thenReturn(builder);
        WebUtils.putAuthenticationResultBuilder(builder, context);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, loadSurrogatesListAction.execute(context).getId());
    }

    @Test
    public void verifySkipAuthenticate() throws Exception {
        val context = new MockRequestContext();
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        WebUtils.putRequestSurrogateAuthentication(context, Boolean.TRUE);
        val attributes = new LinkedHashMap<String, Object>();
        attributes.put(AUTHENTICATION_ATTR_SURROGATE_ENABLED, true);
        attributes.putAll(CoreAuthenticationTestUtils.getAttributeRepository().getBackingMap());
        val p = CoreAuthenticationTestUtils.getPrincipal("someuser", attributes);
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(p), context);
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val creds = new SurrogateUsernamePasswordCredential();
        creds.setPassword("Mellon");
        creds.setUsername("someuser");
        creds.setSurrogateUsername("others");
        WebUtils.putCredential(context, creds);
        val builder = Mockito.mock(AuthenticationResultBuilder.class);
        Mockito.when(builder.getInitialAuthentication()).thenReturn(Optional.of(CoreAuthenticationTestUtils.getAuthentication()));
        Mockito.when(builder.collect(ArgumentMatchers.any(Authentication.class))).thenReturn(builder);
        WebUtils.putAuthenticationResultBuilder(builder, context);
        Assertions.assertEquals(TRANSITION_ID_SKIP_SURROGATE, loadSurrogatesListAction.execute(context).getId());
    }
}

