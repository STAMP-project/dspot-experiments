package org.apereo.cas.interrupt;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.configuration.model.support.interrupt.InterruptProperties;
import org.apereo.cas.util.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link RestEndpointInterruptInquirerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
public class RestEndpointInterruptInquirerTests {
    private MockWebServer webServer;

    @Test
    public void verifyResponseCanBeFoundFromRest() {
        val restProps = new InterruptProperties.Rest();
        restProps.setUrl("http://localhost:8888");
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        val q = new RestEndpointInterruptInquirer(restProps);
        val response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"), CoreAuthenticationTestUtils.getRegisteredService(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), context);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isBlock());
        Assertions.assertTrue(response.isSsoEnabled());
        Assertions.assertEquals(2, response.getLinks().size());
        Assertions.assertEquals(getClass().getSimpleName(), response.getMessage());
    }
}

