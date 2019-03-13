package org.apereo.cas.web.flow;


import SpnegoConstants.HEADER_AUTHENTICATE;
import SpnegoConstants.HEADER_AUTHORIZATION;
import java.util.List;
import jcifs.spnego.Authentication;
import lombok.val;
import org.apereo.cas.support.spnego.MockJcifsAuthentication;
import org.apereo.cas.support.spnego.util.SpnegoConstants;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.EncodingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link SpnegoCredentialsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Import(SpnegoCredentialsActionTests.SpnegoAuthenticationTestConfiguration.class)
public class SpnegoCredentialsActionTests extends AbstractSpnegoTests {
    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addHeader(HEADER_AUTHORIZATION, (((SpnegoConstants.NEGOTIATE) + ' ') + (EncodingUtils.encodeBase64("credential"))));
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        spnegoAction.execute(context);
        Assertions.assertNotNull(response.getHeader(HEADER_AUTHENTICATE));
    }

    @TestConfiguration("SpnegoAuthenticationTestConfiguration")
    public static class SpnegoAuthenticationTestConfiguration {
        @Bean
        public List<Authentication> spnegoAuthentications() {
            return CollectionUtils.wrapList(new MockJcifsAuthentication());
        }
    }
}

