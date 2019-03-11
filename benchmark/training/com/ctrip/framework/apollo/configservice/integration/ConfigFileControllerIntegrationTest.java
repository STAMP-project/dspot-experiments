package com.ctrip.framework.apollo.configservice.integration;


import ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR;
import ConfigConsts.CLUSTER_NAME_DEFAULT;
import ConfigConsts.NAMESPACE_APPLICATION;
import HttpStatus.OK;
import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.netflix.servo.util.Strings;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigFileControllerIntegrationTest extends AbstractBaseIntegrationTest {
    private String someAppId;

    private String somePublicAppId;

    private String someCluster;

    private String someNamespace;

    private String somePublicNamespace;

    private String someDC;

    private String someDefaultCluster;

    private String grayClientIp;

    private String nonGrayClientIp;

    private Gson gson = new Gson();

    private ExecutorService executorService;

    private Type mapResponseType = new TypeToken<Map<String, String>>() {}.getType();

    @Autowired
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigAsProperties() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}", String.class, getHostUrl(), someAppId, someCluster, someNamespace);
        String result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(result.contains("k2=v2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigAsPropertiesWithGrayRelease() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}?ip={clientIp}", String.class, getHostUrl(), someAppId, someDefaultCluster, NAMESPACE_APPLICATION, grayClientIp);
        ResponseEntity<String> anotherResponse = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}?ip={clientIp}", String.class, getHostUrl(), someAppId, someDefaultCluster, NAMESPACE_APPLICATION, nonGrayClientIp);
        String result = response.getBody();
        String anotherResult = anotherResponse.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(result.contains("k1=v1-gray"));
        Assert.assertEquals(OK, anotherResponse.getStatusCode());
        Assert.assertFalse(anotherResult.contains("k1=v1-gray"));
        Assert.assertTrue(anotherResult.contains("k1=v1"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-dc-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigAsProperties() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, someDC);
        String result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(result.contains("k1=override-someDC-v1"));
        Assert.assertTrue(result.contains("k2=someDC-v2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigAsJson() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}", String.class, getHostUrl(), someAppId, someCluster, someNamespace);
        Map<String, String> configs = gson.fromJson(response.getBody(), mapResponseType);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("v2", configs.get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryConfigAsJsonWithIncorrectCase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}", String.class, getHostUrl(), someAppId, someCluster, someNamespace.toUpperCase());
        Map<String, String> configs = gson.fromJson(response.getBody(), mapResponseType);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("v2", configs.get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-dc-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigAsJson() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, someDC);
        Map<String, String> configs = gson.fromJson(response.getBody(), mapResponseType);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("override-someDC-v1", configs.get("k1"));
        Assert.assertEquals("someDC-v2", configs.get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-dc-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigAsJsonWithIncorrectCase() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}?dataCenter={dateCenter}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace.toUpperCase(), someDC);
        Map<String, String> configs = gson.fromJson(response.getBody(), mapResponseType);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("override-someDC-v1", configs.get("k1"));
        Assert.assertEquals("someDC-v2", configs.get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-default-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigAsJsonWithGrayRelease() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(somePublicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}?ip={clientIp}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, grayClientIp);
        ResponseEntity<String> anotherResponse = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}?ip={clientIp}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace, nonGrayClientIp);
        Map<String, String> configs = gson.fromJson(response.getBody(), mapResponseType);
        Map<String, String> anotherConfigs = gson.fromJson(anotherResponse.getBody(), mapResponseType);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(OK, anotherResponse.getStatusCode());
        Assert.assertEquals("override-v1", configs.get("k1"));
        Assert.assertEquals("gray-v2", configs.get("k2"));
        Assert.assertEquals("override-v1", anotherConfigs.get("k1"));
        Assert.assertEquals("default-v2", anotherConfigs.get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-public-default-override.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-gray-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryPublicConfigAsJsonWithGrayReleaseAndIncorrectCase() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(somePublicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace), stop);
        TimeUnit.MILLISECONDS.sleep(500);
        stop.set(true);
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}?ip={clientIp}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace.toUpperCase(), grayClientIp);
        ResponseEntity<String> anotherResponse = restTemplate.getForEntity("http://{baseurl}/configfiles/json/{appId}/{clusterName}/{namespace}?ip={clientIp}", String.class, getHostUrl(), someAppId, someDefaultCluster, somePublicNamespace.toUpperCase(), nonGrayClientIp);
        Map<String, String> configs = gson.fromJson(response.getBody(), mapResponseType);
        Map<String, String> anotherConfigs = gson.fromJson(anotherResponse.getBody(), mapResponseType);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(OK, anotherResponse.getStatusCode());
        Assert.assertEquals("override-v1", configs.get("k1"));
        Assert.assertEquals("gray-v2", configs.get("k2"));
        Assert.assertEquals("override-v1", anotherConfigs.get("k1"));
        Assert.assertEquals("default-v2", anotherConfigs.get("k2"));
    }

    @Test
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testConfigChanged() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}", String.class, getHostUrl(), someAppId, someCluster, someNamespace);
        String result = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(result.contains("k2=v2"));
        String someReleaseName = "someReleaseName";
        String someReleaseComment = "someReleaseComment";
        Namespace namespace = new Namespace();
        namespace.setAppId(someAppId);
        namespace.setClusterName(someCluster);
        namespace.setNamespaceName(someNamespace);
        String someOwner = "someOwner";
        Map<String, String> newConfigurations = ImmutableMap.of("k1", "v1-changed", "k2", "v2-changed");
        buildRelease(someReleaseName, someReleaseComment, namespace, newConfigurations, someOwner);
        ResponseEntity<String> anotherResponse = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}", String.class, getHostUrl(), someAppId, someCluster, someNamespace);
        Assert.assertEquals(response.getBody(), anotherResponse.getBody());
        List<String> keys = Lists.newArrayList(someAppId, someCluster, someNamespace);
        String message = Strings.join(CLUSTER_NAMESPACE_SEPARATOR, keys.iterator());
        sendReleaseMessage(message);
        TimeUnit.MILLISECONDS.sleep(500);
        ResponseEntity<String> newResponse = restTemplate.getForEntity("http://{baseurl}/configfiles/{appId}/{clusterName}/{namespace}", String.class, getHostUrl(), someAppId, someCluster, someNamespace);
        result = newResponse.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(result.contains("k1=v1-changed"));
        Assert.assertTrue(result.contains("k2=v2-changed"));
    }
}

