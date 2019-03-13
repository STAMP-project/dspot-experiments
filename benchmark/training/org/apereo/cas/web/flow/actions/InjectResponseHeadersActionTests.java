package org.apereo.cas.web.flow.actions;


import CasProtocolConstants.PARAMETER_SERVICE;
import CasWebflowConstants.STATE_ID_SUCCESS;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.ResponseBuilderLocator;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link InjectResponseHeadersActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreServicesConfiguration.class, CasCoreUtilConfiguration.class })
public class InjectResponseHeadersActionTests {
    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Test
    public void verifyAction() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService());
        val locator = Mockito.mock(ResponseBuilderLocator.class);
        Mockito.when(locator.locate(ArgumentMatchers.any(WebApplicationService.class))).thenReturn(new org.apereo.cas.authentication.principal.WebApplicationServiceResponseBuilder(this.servicesManager));
        val redirectToServiceAction = new InjectResponseHeadersAction(locator);
        val event = redirectToServiceAction.execute(context);
        Assertions.assertEquals(STATE_ID_SUCCESS, event.getId());
        Assertions.assertNotNull(response.getHeader(PARAMETER_SERVICE));
    }
}

