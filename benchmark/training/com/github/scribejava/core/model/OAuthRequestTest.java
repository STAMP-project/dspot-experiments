package com.github.scribejava.core.model;


import OAuthConstants.NONCE;
import OAuthConstants.REALM;
import OAuthConstants.SCOPE;
import OAuthConstants.TIMESTAMP;
import OAuthConstants.TOKEN;
import org.junit.Assert;
import org.junit.Test;


public class OAuthRequestTest {
    private OAuthRequest request;

    @Test
    public void shouldAddOAuthParamters() {
        request.addOAuthParameter(TOKEN, "token");
        request.addOAuthParameter(NONCE, "nonce");
        request.addOAuthParameter(TIMESTAMP, "ts");
        request.addOAuthParameter(SCOPE, "feeds");
        request.addOAuthParameter(REALM, "some-realm");
        Assert.assertEquals(5, request.getOauthParameters().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfParameterIsNotOAuth() {
        request.addOAuthParameter("otherParam", "value");
    }

    @Test
    public void shouldNotSentHeaderTwice() {
        Assert.assertTrue(request.getHeaders().isEmpty());
        request.addHeader("HEADER-NAME", "first");
        request.addHeader("header-name", "middle");
        request.addHeader("Header-Name", "last");
        Assert.assertEquals(1, request.getHeaders().size());
        Assert.assertTrue(request.getHeaders().containsKey("HEADER-NAME"));
        Assert.assertTrue(request.getHeaders().containsKey("header-name"));
        Assert.assertTrue(request.getHeaders().containsKey("Header-Name"));
        Assert.assertEquals("last", request.getHeaders().get("HEADER-NAME"));
        Assert.assertEquals("last", request.getHeaders().get("header-name"));
        Assert.assertEquals("last", request.getHeaders().get("Header-Name"));
    }
}

