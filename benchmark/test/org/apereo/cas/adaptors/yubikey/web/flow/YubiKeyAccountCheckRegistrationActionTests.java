package org.apereo.cas.adaptors.yubikey.web.flow;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import lombok.val;
import org.apereo.cas.adaptors.yubikey.AcceptAllYubiKeyAccountValidator;
import org.apereo.cas.adaptors.yubikey.DenyAllYubiKeyAccountValidator;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link YubiKeyAccountCheckRegistrationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class YubiKeyAccountCheckRegistrationActionTests {
    @Test
    public void verifyActionSuccess() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        val action = new YubiKeyAccountCheckRegistrationAction(new org.apereo.cas.adaptors.yubikey.registry.OpenYubiKeyAccountRegistry(new AcceptAllYubiKeyAccountValidator()));
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, action.execute(context).getId());
    }

    @Test
    public void verifyActionRegister() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val authentication = CoreAuthenticationTestUtils.getAuthentication();
        WebUtils.putAuthentication(authentication, context);
        val registry = new org.apereo.cas.adaptors.yubikey.registry.ClosedYubiKeyAccountRegistry(new DenyAllYubiKeyAccountValidator());
        val action = new YubiKeyAccountCheckRegistrationAction(registry);
        Assertions.assertEquals("register", action.execute(context).getId());
    }
}

