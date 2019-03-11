package org.apereo.cas.web;


import TokenConstants.PARAMETER_NAME_TOKEN;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * This is {@link DefaultTokenRequestExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class DefaultTokenRequestExtractorTests {
    @Test
    public void verifyTokenFromParameter() {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_NAME_TOKEN, "test");
        val e = new DefaultTokenRequestExtractor();
        val token = e.extract(request);
        Assertions.assertEquals("test", token);
    }

    @Test
    public void verifyTokenFromHeader() {
        val request = new MockHttpServletRequest();
        request.addHeader(PARAMETER_NAME_TOKEN, "test");
        val e = new DefaultTokenRequestExtractor();
        val token = e.extract(request);
        Assertions.assertEquals("test", token);
    }

    @Test
    public void verifyTokenNotFound() {
        val request = new MockHttpServletRequest();
        val e = new DefaultTokenRequestExtractor();
        val token = e.extract(request);
        Assertions.assertNull(token);
    }
}

