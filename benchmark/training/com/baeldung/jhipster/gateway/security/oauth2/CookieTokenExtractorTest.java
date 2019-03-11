package com.baeldung.jhipster.gateway.security.oauth2;


import HttpMethod.GET;
import OAuth2AccessToken.ACCESS_TOKEN;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


/**
 * Test whether the CookieTokenExtractor can properly extract access tokens from
 * Cookies and Headers.
 */
public class CookieTokenExtractorTest {
    private CookieTokenExtractor cookieTokenExtractor;

    @Test
    public void testExtractTokenCookie() {
        MockHttpServletRequest request = OAuth2AuthenticationServiceTest.createMockHttpServletRequest();
        Authentication authentication = cookieTokenExtractor.extract(request);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE, authentication.getPrincipal().toString());
    }

    @Test
    public void testExtractTokenHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest(GET.name(), "http://www.test.com");
        request.addHeader("Authorization", (((OAuth2AccessToken.BEARER_TYPE) + " ") + (OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE)));
        Authentication authentication = cookieTokenExtractor.extract(request);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE, authentication.getPrincipal().toString());
    }

    @Test
    public void testExtractTokenParam() {
        MockHttpServletRequest request = new MockHttpServletRequest(GET.name(), "http://www.test.com");
        request.addParameter(ACCESS_TOKEN, OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE);
        Authentication authentication = cookieTokenExtractor.extract(request);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE, authentication.getPrincipal().toString());
    }
}

