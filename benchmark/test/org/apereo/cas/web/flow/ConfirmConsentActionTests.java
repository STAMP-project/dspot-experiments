package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import ConsentReminderOptions.ALWAYS;
import java.time.temporal.ChronoUnit;
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
 * This is {@link ConfirmConsentActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ConfirmConsentActionTests extends BaseConsentActionTests {
    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addParameter("reminder", "10");
        request.addParameter("reminderTimeUnit", ChronoUnit.DAYS.name());
        request.addParameter("option", String.valueOf(ALWAYS.getValue()));
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService("consentService"));
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, confirmConsentAction.execute(context).getId());
    }
}

