package org.apereo.cas.web;


import CasProtocolConstants.PARAMETER_SERVICE;
import RegisteredServiceProperties.HTTP_HEADER_ENABLE_CACHE_CONTROL;
import RegisteredServiceProperties.HTTP_HEADER_ENABLE_CONTENT_SECURITY_POLICY;
import RegisteredServiceProperties.HTTP_HEADER_ENABLE_STRICT_TRANSPORT_SECURITY;
import RegisteredServiceProperties.HTTP_HEADER_ENABLE_XCONTENT_OPTIONS;
import RegisteredServiceProperties.HTTP_HEADER_ENABLE_XFRAME_OPTIONS;
import RegisteredServiceProperties.HTTP_HEADER_ENABLE_XSS_PROTECTION;
import RegisteredServiceProperties.HTTP_HEADER_XFRAME_OPTIONS;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link RegisteredServiceResponseHeadersEnforcementFilterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class RegisteredServiceResponseHeadersEnforcementFilterTests {
    @Test
    public void verifyCacheControl() throws Exception {
        val filter = RegisteredServiceResponseHeadersEnforcementFilterTests.getFilterForProperty(HTTP_HEADER_ENABLE_CACHE_CONTROL);
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, "service-0");
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertNotNull(response.getHeader("Cache-Control"));
    }

    @Test
    public void verifyContentSecurityPolicy() throws Exception {
        val filter = RegisteredServiceResponseHeadersEnforcementFilterTests.getFilterForProperty(HTTP_HEADER_ENABLE_CONTENT_SECURITY_POLICY);
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.setRequestURI("/cas/login");
        filter.setContentSecurityPolicy("sample-policy");
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertNotNull(response.getHeader("Content-Security-Policy"));
    }

    @Test
    public void verifyStrictTransport() throws Exception {
        val filter = RegisteredServiceResponseHeadersEnforcementFilterTests.getFilterForProperty(HTTP_HEADER_ENABLE_STRICT_TRANSPORT_SECURITY);
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, "service-0");
        request.setSecure(true);
        filter.doFilter(request, response, new MockFilterChain());
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertNotNull(response.getHeader("Strict-Transport-Security"));
    }

    @Test
    public void verifyXContentOptions() throws Exception {
        val filter = RegisteredServiceResponseHeadersEnforcementFilterTests.getFilterForProperty(HTTP_HEADER_ENABLE_XCONTENT_OPTIONS);
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, "service-0");
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertNotNull(response.getHeader("X-Content-Type-Options"));
    }

    @Test
    public void verifyXframeOptions() throws Exception {
        val filter = RegisteredServiceResponseHeadersEnforcementFilterTests.getFilterForProperty(Pair.of(HTTP_HEADER_ENABLE_XFRAME_OPTIONS, "true"), Pair.of(HTTP_HEADER_XFRAME_OPTIONS, "sameorigin"));
        filter.setXFrameOptions("some-other-value");
        filter.setEnableXFrameOptions(true);
        var response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.setParameter(PARAMETER_SERVICE, "service-0");
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertEquals("sameorigin", response.getHeader("X-Frame-Options"));
        response = new MockHttpServletResponse();
        request.setParameter(PARAMETER_SERVICE, "service-something-else");
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertEquals("some-other-value", response.getHeader("X-Frame-Options"));
    }

    @Test
    public void verifyXssProtection() throws Exception {
        val filter = RegisteredServiceResponseHeadersEnforcementFilterTests.getFilterForProperty(HTTP_HEADER_ENABLE_XSS_PROTECTION);
        val response = new MockHttpServletResponse();
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_SERVICE, "service-0");
        filter.doFilter(request, response, new MockFilterChain());
        Assertions.assertNotNull(response.getHeader("X-XSS-Protection"));
    }
}

