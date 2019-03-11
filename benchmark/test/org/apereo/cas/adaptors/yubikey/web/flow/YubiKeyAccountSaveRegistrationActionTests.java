package org.apereo.cas.adaptors.yubikey.web.flow;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import lombok.val;
import org.apereo.cas.adaptors.yubikey.AcceptAllYubiKeyAccountValidator;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link YubiKeyAccountSaveRegistrationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class YubiKeyAccountSaveRegistrationActionTests {
    @Test
    public void verifyActionSuccess() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addParameter("token", "yubikeyToken");
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        val action = new YubiKeyAccountSaveRegistrationAction(new org.apereo.cas.adaptors.yubikey.registry.OpenYubiKeyAccountRegistry(new AcceptAllYubiKeyAccountValidator()));
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, action.execute(context).getId());
    }
}

