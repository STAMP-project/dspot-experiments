package org.apereo.cas.web.flow;


import SpnegoConstants.HEADER_AUTHENTICATE;
import javax.servlet.http.HttpServletResponse;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SpnegoNegotiateCredentialsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SpnegoNegotiateCredentialsActionTests extends AbstractSpnegoTests {
    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addHeader("User-Agent", "MSIE");
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        negociateSpnegoAction.execute(context);
        Assertions.assertNotNull(response.getHeader(HEADER_AUTHENTICATE));
        Assertions.assertTrue(((response.getStatus()) == (HttpServletResponse.SC_UNAUTHORIZED)));
    }
}

