package org.apereo.cas.web.flow.action;


import SurrogateSelectionAction.PARAMETER_NAME_SURROGATE_TARGET;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationResultBuilder;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
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
 * This is {@link SurrogateSelectionActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SurrogateSelectionActionTests extends BaseSurrogateInitialAuthenticationActionTests {
    @Autowired
    @Qualifier("selectSurrogateAction")
    private Action selectSurrogateAction;

    @Test
    public void verifyNoCredentialFound() {
        try {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            request.addParameter(PARAMETER_NAME_SURROGATE_TARGET, "cassurrogate");
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("success", selectSurrogateAction.execute(context).getId());
            val c = WebUtils.getCredential(context);
            Assertions.assertNull(c);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void verifyCredentialFound() {
        try {
            val context = new MockRequestContext();
            WebUtils.putCredential(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
            val request = new MockHttpServletRequest();
            val builder = Mockito.mock(AuthenticationResultBuilder.class);
            Mockito.when(builder.getInitialAuthentication()).thenReturn(Optional.of(CoreAuthenticationTestUtils.getAuthentication()));
            Mockito.when(builder.collect(ArgumentMatchers.any(Authentication.class))).thenReturn(builder);
            WebUtils.putAuthenticationResultBuilder(builder, context);
            request.addParameter(PARAMETER_NAME_SURROGATE_TARGET, "cassurrogate");
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("success", selectSurrogateAction.execute(context).getId());
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }
}

