package org.apereo.cas.web.flow;


import CheckConsentRequiredAction.EVENT_ID_CONSENT_REQUIRED;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link CheckConsentRequiredActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class CheckConsentRequiredActionTests extends BaseConsentActionTests {
    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService("consentService"));
        Assertions.assertEquals(EVENT_ID_CONSENT_REQUIRED, checkConsentRequiredAction.execute(context).getId());
        Assertions.assertTrue(context.getFlowScope().contains("attributes"));
        Assertions.assertTrue(context.getFlowScope().contains("principal"));
        Assertions.assertTrue(context.getFlowScope().contains("service"));
        Assertions.assertTrue(context.getFlowScope().contains("option"));
        Assertions.assertTrue(context.getFlowScope().contains("reminder"));
        Assertions.assertTrue(context.getFlowScope().contains("reminderTimeUnit"));
    }
}

