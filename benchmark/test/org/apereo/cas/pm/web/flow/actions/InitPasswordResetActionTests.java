package org.apereo.cas.pm.web.flow.actions;


import lombok.val;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link InitPasswordResetActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@EnabledIfPortOpen(port = 25000)
@EnabledIfContinuousIntegration
@Tag("Mail")
public class InitPasswordResetActionTests extends BasePasswordManagementActionTests {
    @Test
    public void verifyAction() {
        try {
            val request = new MockHttpServletRequest();
            request.setRemoteAddr("1.2.3.4");
            request.setLocalAddr("1.2.3.4");
            ClientInfoHolder.setClientInfo(new org.apereo.inspektr.common.web.ClientInfo(request));
            val token = passwordManagementService.createToken("casuser");
            val context = new MockRequestContext();
            context.getFlowScope().put("token", token);
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("success", initPasswordResetAction.execute(context).getId());
            val c = WebUtils.getCredential(context, UsernamePasswordCredential.class);
            Assertions.assertNotNull(c);
            Assertions.assertEquals("casuser", c.getUsername());
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

