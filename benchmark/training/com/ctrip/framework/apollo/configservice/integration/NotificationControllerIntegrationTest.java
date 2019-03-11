package com.ctrip.framework.apollo.configservice.integration;


import ConfigConsts.CLUSTER_NAME_DEFAULT;
import HttpStatus.OK;
import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.ctrip.framework.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import java.util.concurrent.ExecutorService;
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
public class NotificationControllerIntegrationTest extends AbstractBaseIntegrationTest {
    private String someAppId;

    private String someCluster;

    private String defaultNamespace;

    private String somePublicNamespace;

    private ExecutorService executorService;

    @Autowired
    private ReleaseMessageServiceWithCache releaseMessageServiceWithCache;

    @Autowired
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespace() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(someAppId, someCluster, defaultNamespace), stop);
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, defaultNamespace);
        stop.set(true);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(defaultNamespace, notification.getNamespaceName());
        Assert.assertNotEquals(0, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespaceAsFile() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(someAppId, someCluster, defaultNamespace), stop);
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, ((defaultNamespace) + ".properties"));
        stop.set(true);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(defaultNamespace, notification.getNamespaceName());
        Assert.assertNotEquals(0, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithPrivateNamespaceAsFile() throws Exception {
        String namespace = "someNamespace.xml";
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(someAppId, CLUSTER_NAME_DEFAULT, namespace), stop);
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, namespace);
        stop.set(true);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(namespace, notification.getNamespaceName());
        Assert.assertNotEquals(0, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespaceWithNotificationIdNull() throws Exception {
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, defaultNamespace);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(defaultNamespace, notification.getNamespaceName());
        Assert.assertEquals(10, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespaceWithNotificationIdOutDated() throws Exception {
        long someOutDatedNotificationId = 1;
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}&notificationId={notificationId}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, defaultNamespace, someOutDatedNotificationId);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(defaultNamespace, notification.getNamespaceName());
        Assert.assertEquals(10, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthPublicNamespaceAndNoDataCenter() throws Exception {
        String publicAppId = "somePublicAppId";
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace), stop);
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, somePublicNamespace);
        stop.set(true);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(somePublicNamespace, notification.getNamespaceName());
        Assert.assertNotEquals(0, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthPublicNamespaceAndDataCenter() throws Exception {
        String publicAppId = "somePublicAppId";
        String someDC = "someDC";
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(publicAppId, someDC, somePublicNamespace), stop);
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}&dataCenter={dataCenter}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, somePublicNamespace, someDC);
        stop.set(true);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(somePublicNamespace, notification.getNamespaceName());
        Assert.assertNotEquals(0, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthPublicNamespaceAsFile() throws Exception {
        String publicAppId = "somePublicAppId";
        String someDC = "someDC";
        AtomicBoolean stop = new AtomicBoolean();
        periodicSendMessage(executorService, assembleKey(publicAppId, someDC, somePublicNamespace), stop);
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}&dataCenter={dataCenter}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, ((somePublicNamespace) + ".properties"), someDC);
        stop.set(true);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(somePublicNamespace, notification.getNamespaceName());
        Assert.assertNotEquals(0, notification.getNotificationId());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithPublicNamespaceWithNotificationIdOutDated() throws Exception {
        long someOutDatedNotificationId = 1;
        ResponseEntity<ApolloConfigNotification> result = restTemplate.getForEntity("http://{baseurl}/notifications?appId={appId}&cluster={clusterName}&namespace={namespace}&notificationId={notificationId}", ApolloConfigNotification.class, getHostUrl(), someAppId, someCluster, somePublicNamespace, someOutDatedNotificationId);
        ApolloConfigNotification notification = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(somePublicNamespace, notification.getNamespaceName());
        Assert.assertEquals(20, notification.getNotificationId());
    }
}

