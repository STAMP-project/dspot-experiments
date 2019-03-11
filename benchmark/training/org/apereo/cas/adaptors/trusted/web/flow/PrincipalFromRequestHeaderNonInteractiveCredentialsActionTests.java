package org.apereo.cas.adaptors.trusted.web.flow;


import java.security.Principal;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link PrincipalFromRequestHeaderNonInteractiveCredentialsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class PrincipalFromRequestHeaderNonInteractiveCredentialsActionTests extends BaseNonInteractiveCredentialsActionTests {
    @Autowired
    @Qualifier("principalFromRemoteHeaderPrincipalAction")
    private Action action;

    @Test
    public void verifyRemoteUserExists() throws Exception {
        val request = new MockHttpServletRequest();
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("casuser");
        request.setUserPrincipal(principal);
        Assertions.assertEquals("success", this.action.execute(context).getId());
        request.setRemoteUser("test");
        Assertions.assertEquals("success", this.action.execute(context).getId());
        request.addHeader("principal", "casuser");
        Assertions.assertEquals("success", this.action.execute(context).getId());
    }
}

