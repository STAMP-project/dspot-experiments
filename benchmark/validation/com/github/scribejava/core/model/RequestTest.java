package com.github.scribejava.core.model;


import org.junit.Assert;
import org.junit.Test;

import static Verb.GET;


public class RequestTest {
    private OAuthRequest getRequest;

    private OAuthRequest postRequest;

    @Test
    public void shouldGetQueryStringParameters() {
        Assert.assertEquals(2, getRequest.getQueryStringParams().size());
        Assert.assertEquals(0, postRequest.getQueryStringParams().size());
        Assert.assertTrue(getRequest.getQueryStringParams().contains(new Parameter("qsparam", "value")));
    }

    @Test
    public void shouldSetBodyParamsAndAddContentLength() {
        Assert.assertEquals("param=value&param%20with%20spaces=value%20with%20spaces", new String(postRequest.getByteArrayPayload()));
    }

    @Test
    public void shouldSetPayloadAndHeaders() {
        postRequest.setPayload("PAYLOAD");
        Assert.assertEquals("PAYLOAD", postRequest.getStringPayload());
    }

    @Test
    public void shouldAllowAddingQuerystringParametersAfterCreation() {
        final OAuthRequest request = new OAuthRequest(GET, "http://example.com?one=val");
        request.addQuerystringParameter("two", "other val");
        request.addQuerystringParameter("more", "params");
        Assert.assertEquals(3, request.getQueryStringParams().size());
    }

    @Test
    public void shouldReturnTheCompleteUrl() {
        final OAuthRequest request = new OAuthRequest(GET, "http://example.com?one=val");
        request.addQuerystringParameter("two", "other val");
        request.addQuerystringParameter("more", "params");
        Assert.assertEquals("http://example.com?one=val&two=other%20val&more=params", request.getCompleteUrl());
    }

    @Test
    public void shouldHandleQueryStringSpaceEncodingProperly() {
        Assert.assertTrue(getRequest.getQueryStringParams().contains(new Parameter("other param", "value with spaces")));
    }
}

