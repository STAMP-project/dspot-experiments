package org.apereo.cas.pm.web.flow.actions;


import lombok.val;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SendPasswordResetInstructionsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@EnabledIfPortOpen(port = 25000)
@EnabledIfContinuousIntegration
@Tag("Mail")
public class SendPasswordResetInstructionsActionTests extends BasePasswordManagementActionTests {
    @Test
    public void verifyAction() {
        try {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            request.addParameter("username", "casuser");
            context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            Assertions.assertEquals("success", sendPasswordResetInstructionsAction.execute(context).getId());
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

