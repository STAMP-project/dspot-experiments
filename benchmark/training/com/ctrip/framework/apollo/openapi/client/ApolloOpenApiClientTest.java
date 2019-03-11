package com.ctrip.framework.apollo.openapi.client;


import org.junit.Assert;
import org.junit.Test;


public class ApolloOpenApiClientTest {
    @Test
    public void testCreate() {
        String someUrl = "http://someUrl";
        String someToken = "someToken";
        ApolloOpenApiClient client = ApolloOpenApiClient.newBuilder().withPortalUrl(someUrl).withToken(someToken).build();
        Assert.assertEquals(someUrl, client.getPortalUrl());
        Assert.assertEquals(someToken, client.getToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithInvalidUrl() {
        String someInvalidUrl = "someInvalidUrl";
        String someToken = "someToken";
        ApolloOpenApiClient.newBuilder().withPortalUrl(someInvalidUrl).withToken(someToken).build();
    }
}

