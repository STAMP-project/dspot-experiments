package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_SUCCESS;
import lombok.val;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link DisplayUserGraphicsBeforeAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DisplayUserGraphicsBeforeAuthenticationActionTests extends AbstractGraphicalAuthenticationActionTests {
    @Test
    public void verifyAction() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addParameter("username", "casuser");
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val event = displayUserGraphicsBeforeAuthenticationAction.execute(context);
        Assertions.assertEquals(TRANSITION_ID_SUCCESS, event.getId());
        Assertions.assertTrue(WebUtils.containsGraphicalUserAuthenticationImage(context));
        Assertions.assertTrue(WebUtils.containsGraphicalUserAuthenticationUsername(context));
    }
}

