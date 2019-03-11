package org.apereo.cas.adaptors.x509.web.flow;


import CasWebflowConstants.TRANSITION_ID_ERROR;
import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import X509CertificateCredentialsNonInteractiveAction.REQUEST_ATTRIBUTE_X509_CERTIFICATE;
import java.security.cert.X509Certificate;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 *
 *
 * @author Marvin S. Addison
 * @since 3.0.0
 */
public class X509CertificateCredentialsNonInteractiveActionTests extends BaseCertificateCredentialActionTests {
    @Test
    public void verifyNoCredentialsResultsInError() throws Exception {
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        Assertions.assertEquals(TRANSITION_ID_ERROR, this.action.getObject().execute(context).getId());
    }

    @Test
    public void verifyCredentialsResultsInSuccess() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.setAttribute(REQUEST_ATTRIBUTE_X509_CERTIFICATE, new X509Certificate[]{ VALID_CERTIFICATE });
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, this.action.getObject().execute(context).getId());
    }
}

