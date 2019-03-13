package org.apereo.cas.web.flow;


import lombok.val;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SetServiceUnauthorizedRedirectUrlActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class SetServiceUnauthorizedRedirectUrlActionTests extends AbstractWebflowActionsTests {
    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val action = new org.apereo.cas.web.flow.login.SetServiceUnauthorizedRedirectUrlAction(servicesManager);
        WebUtils.putRegisteredService(context, servicesManager.findServiceBy("https://github.com/apereo/cas"));
        action.execute(context);
        Assertions.assertNotNull(WebUtils.getUnauthorizedRedirectUrlFromFlowScope(context));
    }
}

