package com.ctrip.framework.apollo.openapi.client.service;


import com.ctrip.framework.apollo.openapi.dto.OpenAppNamespaceDTO;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class NamespaceOpenApiServiceTest extends AbstractOpenApiServiceTest {
    private NamespaceOpenApiService namespaceOpenApiService;

    private String someAppId;

    private String someEnv;

    private String someCluster;

    private String someNamespace;

    @Test
    public void testGetNamespace() throws Exception {
        final ArgumentCaptor<HttpGet> request = ArgumentCaptor.forClass(HttpGet.class);
        namespaceOpenApiService.getNamespace(someAppId, someEnv, someCluster, someNamespace);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpGet get = request.getValue();
        Assert.assertEquals(String.format("%s/envs/%s/apps/%s/clusters/%s/namespaces/%s", someBaseUrl, someEnv, someAppId, someCluster, someNamespace), get.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testGetNamespaceWithError() throws Exception {
        Mockito.when(statusLine.getStatusCode()).thenReturn(404);
        namespaceOpenApiService.getNamespace(someAppId, someEnv, someCluster, someNamespace);
    }

    @Test
    public void testGetNamespaces() throws Exception {
        StringEntity responseEntity = new StringEntity("[]");
        Mockito.when(someHttpResponse.getEntity()).thenReturn(responseEntity);
        final ArgumentCaptor<HttpGet> request = ArgumentCaptor.forClass(HttpGet.class);
        namespaceOpenApiService.getNamespaces(someAppId, someEnv, someCluster);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpGet get = request.getValue();
        Assert.assertEquals(String.format("%s/envs/%s/apps/%s/clusters/%s/namespaces", someBaseUrl, someEnv, someAppId, someCluster), get.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testGetNamespacesWithError() throws Exception {
        Mockito.when(statusLine.getStatusCode()).thenReturn(404);
        namespaceOpenApiService.getNamespaces(someAppId, someEnv, someCluster);
    }

    @Test
    public void testCreateAppNamespace() throws Exception {
        String someName = "someName";
        String someCreatedBy = "someCreatedBy";
        OpenAppNamespaceDTO appNamespaceDTO = new OpenAppNamespaceDTO();
        appNamespaceDTO.setAppId(someAppId);
        appNamespaceDTO.setName(someName);
        appNamespaceDTO.setDataChangeCreatedBy(someCreatedBy);
        final ArgumentCaptor<HttpPost> request = ArgumentCaptor.forClass(HttpPost.class);
        namespaceOpenApiService.createAppNamespace(appNamespaceDTO);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpPost post = request.getValue();
        Assert.assertEquals(String.format("%s/apps/%s/appnamespaces", someBaseUrl, someAppId), post.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testCreateAppNamespaceWithError() throws Exception {
        String someName = "someName";
        String someCreatedBy = "someCreatedBy";
        OpenAppNamespaceDTO appNamespaceDTO = new OpenAppNamespaceDTO();
        appNamespaceDTO.setAppId(someAppId);
        appNamespaceDTO.setName(someName);
        appNamespaceDTO.setDataChangeCreatedBy(someCreatedBy);
        Mockito.when(statusLine.getStatusCode()).thenReturn(400);
        namespaceOpenApiService.createAppNamespace(appNamespaceDTO);
    }

    @Test
    public void testGetNamespaceLock() throws Exception {
        final ArgumentCaptor<HttpGet> request = ArgumentCaptor.forClass(HttpGet.class);
        namespaceOpenApiService.getNamespaceLock(someAppId, someEnv, someCluster, someNamespace);
        Mockito.verify(httpClient, Mockito.times(1)).execute(request.capture());
        HttpGet post = request.getValue();
        Assert.assertEquals(String.format("%s/envs/%s/apps/%s/clusters/%s/namespaces/%s/lock", someBaseUrl, someEnv, someAppId, someCluster, someNamespace), post.getURI().toString());
    }

    @Test(expected = RuntimeException.class)
    public void testGetNamespaceLockWithError() throws Exception {
        Mockito.when(statusLine.getStatusCode()).thenReturn(404);
        namespaceOpenApiService.getNamespaceLock(someAppId, someEnv, someCluster, someNamespace);
    }
}

