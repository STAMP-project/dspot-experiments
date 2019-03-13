package org.apereo.cas.support.openid.web.support;


import lombok.val;
import org.apereo.cas.support.openid.AbstractOpenIdTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class OpenIdPostUrlHandlerMappingTests extends AbstractOpenIdTests {
    private static final String LOGIN_URL_PATH = "/login";

    @Autowired
    @Qualifier("openIdPostUrlHandlerMapping")
    private OpenIdPostUrlHandlerMapping handlerMapping;

    @Test
    public void verifyNoMatch() throws Exception {
        val request = new MockHttpServletRequest();
        request.setContextPath("/hello");
        Assertions.assertNull(this.handlerMapping.lookupHandler("/hello", request));
    }

    @Test
    public void verifyImproperMatch() throws Exception {
        val request = new MockHttpServletRequest();
        request.setContextPath("/hello");
        Assertions.assertNull(this.handlerMapping.lookupHandler(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH, request));
    }

    @Test
    public void verifyProperMatchWrongMethod() throws Exception {
        val request = new MockHttpServletRequest();
        request.setContextPath(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH);
        request.setMethod("GET");
        Assertions.assertNull(this.handlerMapping.lookupHandler(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH, request));
    }

    @Test
    public void verifyProperMatchCorrectMethodNoParam() throws Exception {
        val request = new MockHttpServletRequest();
        request.setContextPath(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH);
        request.setMethod("POST");
        Assertions.assertNull(this.handlerMapping.lookupHandler(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH, request));
    }

    @Test
    public void verifyProperMatchCorrectMethodWithParam() throws Exception {
        val request = new MockHttpServletRequest();
        request.setContextPath(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH);
        request.setMethod("POST");
        request.setParameter("openid.mode", "check_authentication");
        Assertions.assertNotNull(this.handlerMapping.lookupHandler(OpenIdPostUrlHandlerMappingTests.LOGIN_URL_PATH, request));
    }
}

