package org.apereo.cas.web.view;


import java.util.Map;
import lombok.val;
import org.apereo.cas.authentication.DefaultAuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.support.NoOpProtocolAttributeEncoder;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.validation.AuthenticationAttributeReleasePolicy;
import org.apereo.cas.web.view.attributes.NoOpProtocolAttributesRenderer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Unit test for {@link Cas10ResponseView} class.
 *
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @since 3.0.0
 */
public class Cas10ResponseViewTests {
    private Map<String, Object> model;

    @Test
    public void verifySuccessView() throws Exception {
        val response = new MockHttpServletResponse();
        val view = new Cas10ResponseView(true, new NoOpProtocolAttributeEncoder(), Mockito.mock(ServicesManager.class), Mockito.mock(AuthenticationAttributeReleasePolicy.class), new DefaultAuthenticationServiceSelectionPlan(), new NoOpProtocolAttributesRenderer());
        view.render(this.model, new MockHttpServletRequest(), response);
        Assertions.assertEquals("yes\ntest\n", response.getContentAsString());
    }

    @Test
    public void verifyFailureView() throws Exception {
        val response = new MockHttpServletResponse();
        val view = new Cas10ResponseView(false, new NoOpProtocolAttributeEncoder(), Mockito.mock(ServicesManager.class), Mockito.mock(AuthenticationAttributeReleasePolicy.class), new DefaultAuthenticationServiceSelectionPlan(), new NoOpProtocolAttributesRenderer());
        view.render(this.model, new MockHttpServletRequest(), response);
        Assertions.assertEquals("no\n\n", response.getContentAsString());
    }
}

