package com.ctrip.framework.apollo.openapi.client.service;


import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class AppOpenApiServiceTest extends AbstractOpenApiServiceTest {
    private AppOpenApiService appOpenApiService;

    private String someAppId;

    @Test
    public void testGetEnvClusterInfo() throws Exception {
        final ArgumentCaptor<HttpGet> request = ArgumentCaptor.forClass(HttpGet.class);
        appOpenApiService.getEnvClusterInfo(someAppId);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpGet get = request.getValue();
        Assert.assertEquals(String.format("%s/apps/%s/envclusters", someBaseUrl, someAppId), get.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testGetEnvClusterInfoWithError() throws Exception {
        Mockito.when(statusLine.getStatusCode()).thenReturn(500);
        appOpenApiService.getEnvClusterInfo(someAppId);
    }
}

