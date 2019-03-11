package org.apereo.cas.adaptors.trusted.web.flow;


import lombok.val;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link PrincipalFromRemoteRequestHeaderNonInteractiveCredentialsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = "cas.authn.trusted.remotePrincipalHeader=cas-header-name")
public class PrincipalFromRemoteRequestHeaderNonInteractiveCredentialsActionTests extends BaseNonInteractiveCredentialsActionTests {
    @Autowired
    @Qualifier("principalFromRemoteHeaderPrincipalAction")
    private Action action;

    @Test
    public void verifyRemoteUserExists() throws Exception {
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        request.addHeader("cas-header-name", "casuser");
        Assertions.assertEquals("success", this.action.execute(context).getId());
        val c = WebUtils.getCredential(context);
        Assertions.assertEquals("casuser", c.getId());
    }
}

