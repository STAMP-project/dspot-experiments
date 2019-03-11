package com.ctrip.framework.apollo.openapi.client.service;


import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class ReleaseOpenApiServiceTest extends AbstractOpenApiServiceTest {
    private ReleaseOpenApiService releaseOpenApiService;

    private String someAppId;

    private String someEnv;

    private String someCluster;

    private String someNamespace;

    @Test
    public void testPublishNamespace() throws Exception {
        String someReleaseTitle = "someReleaseTitle";
        String someReleasedBy = "someReleasedBy";
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setReleaseTitle(someReleaseTitle);
        namespaceReleaseDTO.setReleasedBy(someReleasedBy);
        final ArgumentCaptor<HttpPost> request = ArgumentCaptor.forClass(HttpPost.class);
        releaseOpenApiService.publishNamespace(someAppId, someEnv, someCluster, someNamespace, namespaceReleaseDTO);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpPost post = request.getValue();
        Assert.assertEquals(String.format("%s/envs/%s/apps/%s/clusters/%s/namespaces/%s/releases", someBaseUrl, someEnv, someAppId, someCluster, someNamespace), post.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testPublishNamespaceWithError() throws Exception {
        String someReleaseTitle = "someReleaseTitle";
        String someReleasedBy = "someReleasedBy";
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setReleaseTitle(someReleaseTitle);
        namespaceReleaseDTO.setReleasedBy(someReleasedBy);
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        releaseOpenApiService.publishNamespace(someAppId, someEnv, someCluster, someNamespace, namespaceReleaseDTO);
    }

    @Test
    public void testGetLatestActiveRelease() throws Exception {
        final ArgumentCaptor<HttpGet> request = ArgumentCaptor.forClass(HttpGet.class);
        releaseOpenApiService.getLatestActiveRelease(someAppId, someEnv, someCluster, someNamespace);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpGet get = request.getValue();
        Assert.assertEquals(String.format("%s/envs/%s/apps/%s/clusters/%s/namespaces/%s/releases/latest", someBaseUrl, someEnv, someAppId, someCluster, someNamespace), get.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testGetLatestActiveReleaseWithError() throws Exception {
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        releaseOpenApiService.getLatestActiveRelease(someAppId, someEnv, someCluster, someNamespace);
    }
}

