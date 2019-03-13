package com.ctrip.framework.apollo.configservice.integration;


import ConfigConsts.CLUSTER_NAME_DEFAULT;
import ConfigConsts.NAMESPACE_APPLICATION;
import ConfigConsts.NO_APPID_PLACEHOLDER;
import HttpStatus.NOT_FOUND;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.OK;
import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpStatusCodeException;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigControllerIntegrationTest extends AbstractBaseIntegrationTest {
    private String someAppId;

    private String somePublicAppId;

    private String someCluster;

    private String someNamespace;

    private String somePublicNamespace;

    private String someDC;

    private String someDefaultCluster;

    private String someClientIp;

    private ExecutorService executorService;

    @Autowired
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigWithDefaultClusterAndDefaultNamespaceOK() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-RELEASE-KEY1", result.getReleaseKey());
        Assert.assertEquals("v1", result.getConfigurations().get("k1"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigWithDefaultClusterAndDefaultNamespaceAndIncorrectCase() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION.toUpperCase());
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-RELEASE-KEY1", result.getReleaseKey());
        Assert.assertEquals("v1", result.getConfigurations().get("k1"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryGrayConfigWithDefaultClusterAndDefaultNamespaceOK() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?ip={clientIp}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION, someClientIp);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-GRAY-RELEASE-KEY1", result.getReleaseKey());
        Assert.assertEquals("v1-gray", result.getConfigurations().get("k1"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryGrayConfigWithDefaultClusterAndDefaultNamespaceAndIncorrectCase() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?ip={clientIp}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION.toUpperCase(), someClientIp);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-GRAY-RELEASE-KEY1", result.getReleaseKey());
        Assert.assertEquals("v1-gray", result.getConfigurations().get("k1"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigFileWithDefaultClusterAndDefaultNamespaceOK() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, ((ConfigConsts.NAMESPACE_APPLICATION) + ".properties"));
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-RELEASE-KEY1", result.getReleaseKey());
        Assert.assertEquals("v1", result.getConfigurations().get("k1"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigWithNamespaceOK() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, someCluster, someNamespace);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-RELEASE-KEY2", result.getReleaseKey());
        Assert.assertEquals("v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigFileWithNamespaceOK() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, ((someNamespace) + ".xml"));
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-RELEASE-KEY5", result.getReleaseKey());
        Assert.assertEquals("v1-file", result.getConfigurations().get("k1"));
        Assert.assertEquals("v2-file", result.getConfigurations().get("k2"));
    }

    @Test
    public void testQueryConfigError() throws Exception {
        String someNamespaceNotExists = "someNamespaceNotExists";
        HttpStatusCodeException httpException = null;
        try {
            ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, someCluster, someNamespaceNotExists);
        } catch (HttpStatusCodeException ex) {
            httpException = ex;
        }
        Assert.assertEquals(NOT_FOUND, httpException.getStatusCode());
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigNotModified() throws Exception {
        String releaseKey = "TEST-RELEASE-KEY2";
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?releaseKey={releaseKey}", ApolloConfig.class, getHostUrl(), someAppId, someCluster, someNamespace, releaseKey);
        Assert.assertEquals(NOT_MODIFIED, response.getStatusCode());
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicGrayConfigWithNoOverride() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(somePublicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?ip={clientIp}", ApolloConfig.class, getHostUrl(), someAppId, someCluster, somePublicNamespace, someClientIp);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("TEST-GRAY-RELEASE-KEY2", result.getReleaseKey());
        Assert.assertEquals("gray-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("gray-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigWithDataCenterFoundAndNoOverride() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", ApolloConfig.class, getHostUrl(), someAppId, someCluster, somePublicNamespace, someDC);
        ApolloConfig result = response.getBody();
        Assert.assertEquals("TEST-RELEASE-KEY4", result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someCluster, result.getCluster());
        Assert.assertEquals(somePublicNamespace, result.getNamespaceName());
        Assert.assertEquals("someDC-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("someDC-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-dc-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigWithDataCenterFoundAndOverride() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", ApolloConfig.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, someDC);
        ApolloConfig result = response.getBody();
        Assert.assertEquals((("TEST-RELEASE-KEY6" + (ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)) + "TEST-RELEASE-KEY4"), result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someDC, result.getCluster());
        Assert.assertEquals(somePublicNamespace, result.getNamespaceName());
        Assert.assertEquals("override-someDC-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("someDC-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-dc-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigWithIncorrectCaseAndDataCenterFoundAndOverride() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", ApolloConfig.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace.toUpperCase(), someDC);
        ApolloConfig result = response.getBody();
        Assert.assertEquals((("TEST-RELEASE-KEY6" + (ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)) + "TEST-RELEASE-KEY4"), result.getReleaseKey());
        Assert.assertEquals("override-someDC-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("someDC-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigWithDataCenterNotFoundAndNoOverride() throws Exception {
        String someDCNotFound = "someDCNotFound";
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", ApolloConfig.class, getHostUrl(), someAppId, someCluster, somePublicNamespace, someDCNotFound);
        ApolloConfig result = response.getBody();
        Assert.assertEquals("TEST-RELEASE-KEY3", result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someCluster, result.getCluster());
        Assert.assertEquals(somePublicNamespace, result.getNamespaceName());
        Assert.assertEquals("default-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("default-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-default-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigWithDataCenterNotFoundAndOverride() throws Exception {
        String someDCNotFound = "someDCNotFound";
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", ApolloConfig.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, someDCNotFound);
        ApolloConfig result = response.getBody();
        Assert.assertEquals((("TEST-RELEASE-KEY5" + (ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)) + "TEST-RELEASE-KEY3"), result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someDefaultCluster, result.getCluster());
        Assert.assertEquals(somePublicNamespace, result.getNamespaceName());
        Assert.assertEquals("override-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("default-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-default-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicGrayConfigWithOverride() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(somePublicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?ip={clientIp}", ApolloConfig.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, someClientIp);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals((("TEST-RELEASE-KEY5" + (ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)) + "TEST-GRAY-RELEASE-KEY2"), result.getReleaseKey());
        Assert.assertEquals("override-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("gray-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-default-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicGrayConfigWithIncorrectCaseAndOverride() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(somePublicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?ip={clientIp}", ApolloConfig.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace.toUpperCase(), someClientIp);
        ApolloConfig result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals((("TEST-RELEASE-KEY5" + (ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)) + "TEST-GRAY-RELEASE-KEY2"), result.getReleaseKey());
        Assert.assertEquals("override-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("gray-v2", result.getConfigurations().get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPrivateConfigFileWithPublicNamespaceExists() throws Exception {
        String namespaceName = "anotherNamespace";
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), someAppId, CLUSTER_NAME_DEFAULT, namespaceName);
        ApolloConfig result = response.getBody();
        Assert.assertEquals("TEST-RELEASE-KEY6", result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(CLUSTER_NAME_DEFAULT, result.getCluster());
        Assert.assertEquals(namespaceName, result.getNamespaceName());
        Assert.assertEquals("v1-file", result.getConfigurations().get("k1"));
        Assert.assertEquals(null, result.getConfigurations().get("k2"));
    }

    @Test
    public void testQueryConfigForNoAppIdPlaceHolderWithPrivateNamespace() throws Exception {
        HttpStatusCodeException httpException = null;
        try {
            ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}", ApolloConfig.class, getHostUrl(), NO_APPID_PLACEHOLDER, someCluster, NAMESPACE_APPLICATION);
        } catch (HttpStatusCodeException ex) {
            httpException = ex;
        }
        Assert.assertEquals(NOT_FOUND, httpException.getStatusCode());
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigForNoAppIdPlaceHolder() throws Exception {
        ResponseEntity<ApolloConfig> response = restTemplate.getForEntity("http://{baseurl}/configs/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", ApolloConfig.class, getHostUrl(), NO_APPID_PLACEHOLDER, someCluster, somePublicNamespace, someDC);
        ApolloConfig result = response.getBody();
        Assert.assertEquals("TEST-RELEASE-KEY4", result.getReleaseKey());
        Assert.assertEquals(NO_APPID_PLACEHOLDER, result.getAppId());
        Assert.assertEquals(someCluster, result.getCluster());
        Assert.assertEquals(somePublicNamespace, result.getNamespaceName());
        Assert.assertEquals("someDC-v1", result.getConfigurations().get("k1"));
        Assert.assertEquals("someDC-v2", result.getConfigurations().get("k2"));
    }
}

