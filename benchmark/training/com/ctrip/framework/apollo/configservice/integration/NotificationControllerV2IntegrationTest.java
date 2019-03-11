package com.ctrip.framework.apollo.configservice.integration;


import ConfigConsts.CLUSTER_NAME_DEFAULT;
import ConfigConsts.NAMESPACE_APPLICATION;
import ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;
import HttpMethod.GET;
import HttpStatus.OK;
import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class NotificationControllerV2IntegrationTest extends AbstractBaseIntegrationTest {
    @Autowired
    private Gson gson;

    @Autowired
    private ReleaseMessageServiceWithCache releaseMessageServiceWithCache;

    private String someAppId;

    private String someCluster;

    private String defaultNamespace;

    private String somePublicNamespace;

    private ExecutorService executorService;

    private ParameterizedTypeReference<List<ApolloConfigNotification>> typeReference;

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespace() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(someAppId, someCluster, defaultNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(defaultNamespace, NOTIFICATION_ID_PLACEHOLDER));
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(defaultNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespaceAsFile() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(someAppId, someCluster, defaultNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(((defaultNamespace) + ".properties"), NOTIFICATION_ID_PLACEHOLDER));
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(defaultNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultipleNamespaces() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(someAppId, someCluster, somePublicNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(((defaultNamespace) + ".properties"), NOTIFICATION_ID_PLACEHOLDER, defaultNamespace, NOTIFICATION_ID_PLACEHOLDER, somePublicNamespace, NOTIFICATION_ID_PLACEHOLDER));
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultipleNamespacesAndIncorrectCase() throws Exception {
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(someAppId, someCluster, somePublicNamespace);
        periodicSendMessage(executorService, key, stop);
        String someDefaultNamespaceWithIncorrectCase = defaultNamespace.toUpperCase();
        String somePublicNamespaceWithIncorrectCase = somePublicNamespace.toUpperCase();
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(((defaultNamespace) + ".properties"), NOTIFICATION_ID_PLACEHOLDER, someDefaultNamespaceWithIncorrectCase, NOTIFICATION_ID_PLACEHOLDER, somePublicNamespaceWithIncorrectCase, NOTIFICATION_ID_PLACEHOLDER));
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespaceWithIncorrectCase, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithPrivateNamespaceAsFile() throws Exception {
        String namespace = "someNamespace.xml";
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(someAppId, CLUSTER_NAME_DEFAULT, namespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(namespace, NOTIFICATION_ID_PLACEHOLDER));
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(namespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithDefaultNamespaceWithNotificationIdOutDated() throws Exception {
        long someOutDatedNotificationId = 1;
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(defaultNamespace, someOutDatedNotificationId));
        long newNotificationId = 10;
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(defaultNamespace, notifications.get(0).getNamespaceName());
        Assert.assertEquals(newNotificationId, notifications.get(0).getNotificationId());
        String key = assembleKey(someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertEquals(newNotificationId, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthPublicNamespaceAndNoDataCenter() throws Exception {
        String publicAppId = "somePublicAppId";
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespace, NOTIFICATION_ID_PLACEHOLDER));
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthPublicNamespaceAndDataCenter() throws Exception {
        String publicAppId = "somePublicAppId";
        String someDC = "someDC";
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(publicAppId, someDC, somePublicNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}&dataCenter={dataCenter}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespace, NOTIFICATION_ID_PLACEHOLDER), someDC);
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthMultipleNamespacesAndMultipleNamespacesChanged() throws Exception {
        String publicAppId = "somePublicAppId";
        String someDC = "someDC";
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(publicAppId, someDC, somePublicNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}&dataCenter={dataCenter}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(defaultNamespace, NOTIFICATION_ID_PLACEHOLDER, somePublicNamespace, NOTIFICATION_ID_PLACEHOLDER), someDC);
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWthPublicNamespaceAsFile() throws Exception {
        String publicAppId = "somePublicAppId";
        String someDC = "someDC";
        AtomicBoolean stop = new AtomicBoolean();
        String key = assembleKey(publicAppId, someDC, somePublicNamespace);
        periodicSendMessage(executorService, key, stop);
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}&dataCenter={dataCenter}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(((somePublicNamespace) + ".properties"), NOTIFICATION_ID_PLACEHOLDER), someDC);
        stop.set(true);
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertNotEquals(0, notifications.get(0).getNotificationId());
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertNotEquals(NOTIFICATION_ID_PLACEHOLDER, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithPublicNamespaceWithNotificationIdOutDated() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespace, someOutDatedNotificationId));
        long newNotificationId = 20;
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertEquals(newNotificationId, notifications.get(0).getNotificationId());
        String key = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertEquals(newNotificationId, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultiplePublicNamespaceWithIncorrectCaseWithNotificationIdOutDated() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        long newNotificationId = 20;
        String somePublicNameWithIncorrectCase = somePublicNamespace.toUpperCase();
        // the same namespace with difference character case, and difference notification id
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespace, newNotificationId, somePublicNameWithIncorrectCase, someOutDatedNotificationId));
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNameWithIncorrectCase, notifications.get(0).getNamespaceName());
        Assert.assertEquals(newNotificationId, notifications.get(0).getNotificationId());
        String key = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertEquals(newNotificationId, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultiplePublicNamespaceWithIncorrectCase2WithNotificationIdOutDated() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        long newNotificationId = 20;
        String somePublicNameWithIncorrectCase = somePublicNamespace.toUpperCase();
        // the same namespace with difference character case, and difference notification id
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNameWithIncorrectCase, someOutDatedNotificationId, somePublicNamespace, newNotificationId));
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNameWithIncorrectCase, notifications.get(0).getNamespaceName());
        Assert.assertEquals(newNotificationId, notifications.get(0).getNotificationId());
        String key = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertEquals(newNotificationId, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultiplePublicNamespaceWithIncorrectCase3WithNotificationIdOutDated() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        long newNotificationId = 20;
        String somePublicNameWithIncorrectCase = somePublicNamespace.toUpperCase();
        // the same namespace with difference character case, and difference notification id
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNameWithIncorrectCase, newNotificationId, somePublicNamespace, someOutDatedNotificationId));
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertEquals(newNotificationId, notifications.get(0).getNotificationId());
        String key = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertEquals(newNotificationId, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultiplePublicNamespaceWithIncorrectCase4WithNotificationIdOutDated() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        long newNotificationId = 20;
        String somePublicNameWithIncorrectCase = somePublicNamespace.toUpperCase();
        // the same namespace with difference character case, and difference notification id
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespace, someOutDatedNotificationId, somePublicNameWithIncorrectCase, newNotificationId));
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, notifications.size());
        Assert.assertEquals(somePublicNamespace, notifications.get(0).getNamespaceName());
        Assert.assertEquals(newNotificationId, notifications.get(0).getNotificationId());
        String key = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages messages = result.getBody().get(0).getMessages();
        Assert.assertEquals(1, messages.getDetails().size());
        Assert.assertTrue(messages.has(key));
        Assert.assertEquals(newNotificationId, messages.get(key).longValue());
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultipleNamespacesAndNotificationIdsOutDated() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        long newDefaultNamespaceNotificationId = 10;
        long newPublicNamespaceNotification = 20;
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespace, someOutDatedNotificationId, defaultNamespace, someOutDatedNotificationId));
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(2, notifications.size());
        Set<String> outDatedNamespaces = Sets.newHashSet(notifications.get(0).getNamespaceName(), notifications.get(1).getNamespaceName());
        Assert.assertEquals(Sets.newHashSet(defaultNamespace, somePublicNamespace), outDatedNamespaces);
        Set<Long> newNotificationIds = Sets.newHashSet(notifications.get(0).getNotificationId(), notifications.get(1).getNotificationId());
        Assert.assertEquals(Sets.newHashSet(newDefaultNamespaceNotificationId, newPublicNamespaceNotification), newNotificationIds);
        String defaultNamespaceKey = assembleKey(someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        String publicNamespaceKey = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages firstMessages = notifications.get(0).getMessages();
        ApolloNotificationMessages secondMessages = notifications.get(1).getMessages();
        Assert.assertEquals(1, firstMessages.getDetails().size());
        Assert.assertEquals(1, secondMessages.getDetails().size());
        Assert.assertTrue((((firstMessages.has(defaultNamespaceKey)) && (firstMessages.get(defaultNamespaceKey).equals(newDefaultNamespaceNotificationId))) || ((firstMessages.has(publicNamespaceKey)) && (firstMessages.get(publicNamespaceKey).equals(newPublicNamespaceNotification)))));
        Assert.assertTrue((((secondMessages.has(defaultNamespaceKey)) && (secondMessages.get(defaultNamespaceKey).equals(newDefaultNamespaceNotificationId))) || ((secondMessages.has(publicNamespaceKey)) && (secondMessages.get(publicNamespaceKey).equals(newPublicNamespaceNotification)))));
    }

    @Test(timeout = 5000L)
    @Sql(scripts = "/integration-test/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/test-release-message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/integration-test/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testPollNotificationWithMultipleNamespacesAndNotificationIdsOutDatedAndIncorrectCase() throws Exception {
        String publicAppId = "somePublicAppId";
        long someOutDatedNotificationId = 1;
        long newDefaultNamespaceNotificationId = 10;
        long newPublicNamespaceNotification = 20;
        String someDefaultNamespaceWithIncorrectCase = defaultNamespace.toUpperCase();
        String somePublicNamespaceWithIncorrectCase = somePublicNamespace.toUpperCase();
        ResponseEntity<List<ApolloConfigNotification>> result = restTemplate.exchange("http://{baseurl}/notifications/v2?appId={appId}&cluster={clusterName}&notifications={notifications}", GET, null, typeReference, getHostUrl(), someAppId, someCluster, transformApolloConfigNotificationsToString(somePublicNamespaceWithIncorrectCase, someOutDatedNotificationId, someDefaultNamespaceWithIncorrectCase, someOutDatedNotificationId));
        List<ApolloConfigNotification> notifications = result.getBody();
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(2, notifications.size());
        Set<String> outDatedNamespaces = Sets.newHashSet(notifications.get(0).getNamespaceName(), notifications.get(1).getNamespaceName());
        Assert.assertEquals(Sets.newHashSet(someDefaultNamespaceWithIncorrectCase, somePublicNamespaceWithIncorrectCase), outDatedNamespaces);
        Set<Long> newNotificationIds = Sets.newHashSet(notifications.get(0).getNotificationId(), notifications.get(1).getNotificationId());
        Assert.assertEquals(Sets.newHashSet(newDefaultNamespaceNotificationId, newPublicNamespaceNotification), newNotificationIds);
        String defaultNamespaceKey = assembleKey(someAppId, CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        String publicNamespaceKey = assembleKey(publicAppId, CLUSTER_NAME_DEFAULT, somePublicNamespace);
        ApolloNotificationMessages firstMessages = notifications.get(0).getMessages();
        ApolloNotificationMessages secondMessages = notifications.get(1).getMessages();
        Assert.assertEquals(1, firstMessages.getDetails().size());
        Assert.assertEquals(1, secondMessages.getDetails().size());
        Assert.assertTrue((((firstMessages.has(defaultNamespaceKey)) && (firstMessages.get(defaultNamespaceKey).equals(newDefaultNamespaceNotificationId))) || ((firstMessages.has(publicNamespaceKey)) && (firstMessages.get(publicNamespaceKey).equals(newPublicNamespaceNotification)))));
        Assert.assertTrue((((secondMessages.has(defaultNamespaceKey)) && (secondMessages.get(defaultNamespaceKey).equals(newDefaultNamespaceNotificationId))) || ((secondMessages.has(publicNamespaceKey)) && (secondMessages.get(publicNamespaceKey).equals(newPublicNamespaceNotification)))));
    }
}

