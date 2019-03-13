package org.apereo.cas.web.flow.action;


import lombok.val;
import org.apereo.cas.authentication.SurrogateUsernamePasswordCredential;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SurrogateInitialAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SurrogateInitialAuthenticationActionTests extends BaseSurrogateInitialAuthenticationActionTests {
    @Autowired
    @Qualifier("authenticationViaFormAction")
    private Action authenticationViaFormAction;

    @Test
    public void verifyNoCredentialsFound() {
        try {
            val context = new MockRequestContext();
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
            Assertions.assertEquals("error", authenticationViaFormAction.execute(context).getId());
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void verifySurrogateCredentialsFound() {
        try {
            val context = new MockRequestContext();
            val c = new SurrogateUsernamePasswordCredential();
            c.setUsername("casuser");
            c.setPassword("Mellon");
            c.setSurrogateUsername("cassurrogate");
            WebUtils.putCredential(context, c);
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
            Assertions.assertEquals("success", authenticationViaFormAction.execute(context).getId());
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void verifyUsernamePasswordCredentialsFound() {
        try {
            val context = new MockRequestContext();
            val c = new UsernamePasswordCredential();
            c.setUsername("cassurrogate+casuser");
            c.setPassword("Mellon");
            WebUtils.putCredential(context, c);
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
            Assertions.assertEquals("success", authenticationViaFormAction.execute(context).getId());
            Assertions.assertTrue(((WebUtils.getCredential(context)) instanceof UsernamePasswordCredential));
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }
}

