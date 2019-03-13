/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.notification;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.KaaClientProperties;
import org.kaaproject.kaa.client.channel.NotificationTransport;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.kaaproject.kaa.client.persistance.KaaClientPropertiesStateTest;
import org.kaaproject.kaa.client.persistence.FilePersistentStorage;
import org.kaaproject.kaa.client.persistence.KaaClientPropertiesState;
import org.kaaproject.kaa.client.util.CommonsBase64;
import org.kaaproject.kaa.common.endpoint.gen.Notification;
import org.kaaproject.kaa.common.endpoint.gen.NotificationType;
import org.kaaproject.kaa.common.endpoint.gen.SubscriptionType;
import org.kaaproject.kaa.common.endpoint.gen.Topic;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultNotificationManagerTest {
    private static final String workDir = "work_dir" + (System.getProperty("file.separator"));

    private static final Long UNKNOWN_TOPIC_ID = 100500L;

    private static ExecutorContext executorContext;

    private static ExecutorService executor;

    @Test
    public void testEmptyTopicList() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        for (Topic t : notificationManager.getTopics()) {
            System.out.println(t);
        }
        Assert.assertTrue(notificationManager.getTopics().isEmpty());
    }

    @Test
    public void testTopicsAfterUpdate() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topics = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topics);
        Assert.assertTrue(((notificationManager.getTopics().size()) == (topics.size())));
    }

    @Test
    public void testTopicPersistence() throws IOException {
        KaaClientProperties props = KaaClientPropertiesStateTest.getProperties();
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), props);
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topics = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topics);
        state.persist();
        KaaClientPropertiesState newState = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        DefaultNotificationManager newNotificationManager = new DefaultNotificationManager(newState, DefaultNotificationManagerTest.executorContext, transport);
        Assert.assertTrue(((newNotificationManager.getTopics().size()) == (topics.size())));
        boolean deleted = new File(((DefaultNotificationManagerTest.workDir) + (props.getProperty("state.file_name")))).delete();
        Assert.assertTrue(deleted);
    }

    @Test
    public void testTwiceTopicUpdate() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        Topic topic1 = new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION);
        Topic topic2 = new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION);
        Topic topic3 = new Topic(3L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION);
        List<Topic> topicUpdates = new LinkedList<>();
        topicUpdates.add(topic1);
        topicUpdates.add(topic2);
        notificationManager.topicsListUpdated(topicUpdates);
        topicUpdates.remove(topic2);
        topicUpdates.add(topic3);
        notificationManager.topicsListUpdated(topicUpdates);
        List<Topic> newTopics = notificationManager.getTopics();
        Assert.assertTrue(((newTopics.size()) == (topicUpdates.size())));
        Assert.assertTrue(newTopics.contains(topic1));
        Assert.assertTrue(newTopics.contains(topic3));
    }

    @Test
    public void testAddTopicUpdateListener() throws Exception {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        final List<Topic> topicUpdates = new LinkedList<>();
        topicUpdates.add(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        topicUpdates.add(new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        topicUpdates.add(new Topic(3L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.addTopicListListener(new NotificationTopicListListener() {
            @Override
            public void onListUpdated(List<Topic> list) {
                Assert.assertArrayEquals(topicUpdates.toArray(), list.toArray());
                topicUpdates.clear();
            }
        });
        notificationManager.topicsListUpdated(topicUpdates);
        Thread.sleep(500);
        Assert.assertTrue(topicUpdates.isEmpty());
    }

    @Test
    public void testRemoveTopicUpdateListener() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        NotificationTopicListListener listener1 = Mockito.mock(NotificationTopicListListener.class);
        NotificationTopicListListener listener2 = Mockito.mock(NotificationTopicListListener.class);
        notificationManager.addTopicListListener(listener1);
        notificationManager.addTopicListListener(listener2);
        List<Topic> topicUpdate = Arrays.asList(new Topic());
        notificationManager.topicsListUpdated(topicUpdate);
        notificationManager.removeTopicListListener(listener2);
        notificationManager.topicsListUpdated(topicUpdate);
        Mockito.verify(listener1, Mockito.timeout(1000).times(2)).onListUpdated(topicUpdate);
        Mockito.verify(listener2, Mockito.timeout(1000).times(1)).onListUpdated(topicUpdate);
    }

    @Test
    public void testGlobalNotificationListeners() throws Exception {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        ByteBuffer notificationBody = ByteBuffer.wrap(new org.kaaproject.kaa.common.avro.AvroByteArrayConverter(Notification.class).toByteArray(new org.kaaproject.kaa.schema.base.Notification()));
        notificationManager.topicsListUpdated(topicsUpdate);
        List<Notification> notificationUpdate = Arrays.asList(new Notification(1L, NotificationType.CUSTOM, null, 1, notificationBody), new Notification(2L, NotificationType.CUSTOM, null, 1, notificationBody));
        NotificationListener mandatoryListener = Mockito.mock(NotificationListener.class);
        NotificationListener globalListener = Mockito.mock(NotificationListener.class);
        notificationManager.addNotificationListener(mandatoryListener);
        notificationManager.notificationReceived(notificationUpdate);
        Thread.sleep(500);
        notificationManager.removeNotificationListener(mandatoryListener);
        notificationManager.addNotificationListener(globalListener);
        notificationManager.notificationReceived(notificationUpdate);
        notificationManager.notificationReceived(notificationUpdate);
        Mockito.verify(mandatoryListener, Mockito.timeout(1000).times(notificationUpdate.size())).onNotification(Mockito.anyLong(), Mockito.any(Notification.class));
        Mockito.verify(globalListener, Mockito.timeout(1000).times(((notificationUpdate.size()) * 2))).onNotification(Mockito.anyLong(), Mockito.any(Notification.class));
    }

    @Test
    public void testNotificationListenerOnTopic() throws Exception {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        ByteBuffer notificationBody = ByteBuffer.wrap(new org.kaaproject.kaa.common.avro.AvroByteArrayConverter(Topic.class).toByteArray(new Topic(3L, "name", SubscriptionType.MANDATORY_SUBSCRIPTION)));
        notificationManager.topicsListUpdated(topicsUpdate);
        List<Notification> notificationUpdate = Arrays.asList(new Notification(1L, NotificationType.CUSTOM, null, 1, notificationBody), new Notification(2L, NotificationType.CUSTOM, null, 1, notificationBody));
        NotificationListener globalListener = Mockito.mock(NotificationListener.class);
        NotificationListener topicListener = Mockito.mock(NotificationListener.class);
        notificationManager.addNotificationListener(globalListener);
        notificationManager.addNotificationListener(2L, topicListener);
        notificationManager.notificationReceived(notificationUpdate);
        notificationManager.removeNotificationListener(2L, topicListener);
        notificationManager.notificationReceived(notificationUpdate);
        Mockito.verify(globalListener, Mockito.timeout(1000).times((((notificationUpdate.size()) * 2) - 1))).onNotification(Mockito.anyLong(), Mockito.any(Notification.class));
        Mockito.verify(topicListener, Mockito.timeout(1000).times(1)).onNotification(Mockito.anyLong(), Mockito.any(Notification.class));
    }

    @Test(expected = UnavailableTopicException.class)
    public void testAddListenerForUnknownTopic() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        NotificationListener listener = Mockito.mock(NotificationListener.class);
        notificationManager.addNotificationListener(DefaultNotificationManagerTest.UNKNOWN_TOPIC_ID, listener);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testRemoveListenerForUnknownTopic() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        NotificationListener listener = Mockito.mock(NotificationListener.class);
        notificationManager.removeNotificationListener(DefaultNotificationManagerTest.UNKNOWN_TOPIC_ID, listener);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testSubscribeOnUnknownTopic1() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.subscribeToTopic(DefaultNotificationManagerTest.UNKNOWN_TOPIC_ID, true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testSubscribeOnUnknownTopic2() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.subscribeToTopics(Arrays.asList(1L, 2L, DefaultNotificationManagerTest.UNKNOWN_TOPIC_ID), true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testUnsubscribeFromUnknownTopic1() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.unsubscribeFromTopic(DefaultNotificationManagerTest.UNKNOWN_TOPIC_ID, true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testUnsubscribeFromUnknownTopic2() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.unsubscribeFromTopics(Arrays.asList(1L, 2L, DefaultNotificationManagerTest.UNKNOWN_TOPIC_ID), true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testSubscribeOnMandatoryTopic1() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.subscribeToTopic(2L, true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testSubscribeOnMandatoryTopic2() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.subscribeToTopics(Arrays.asList(1L, 2L), true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testUnsubscribeFromMandatoryTopic1() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.unsubscribeFromTopic(2L, true);
    }

    @Test(expected = UnavailableTopicException.class)
    public void testUnsubscribeFromMandatoryTopic2() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.MANDATORY_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.unsubscribeFromTopics(Arrays.asList(1L, 2L), true);
    }

    @Test
    public void testSuccessSubscriptionToTopic() throws IOException, UnavailableTopicException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        NotificationTransport transport = Mockito.mock(NotificationTransport.class);
        DefaultNotificationManager notificationManager = new DefaultNotificationManager(state, DefaultNotificationManagerTest.executorContext, transport);
        List<Topic> topicsUpdate = Arrays.asList(new Topic(1L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(2L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION), new Topic(3L, "topic_name1", SubscriptionType.OPTIONAL_SUBSCRIPTION));
        notificationManager.topicsListUpdated(topicsUpdate);
        notificationManager.subscribeToTopic(1L, true);
        Mockito.verify(transport, Mockito.times(1)).sync();
        notificationManager.subscribeToTopics(Arrays.asList(1L, 2L), false);
        notificationManager.unsubscribeFromTopic(1L, false);
        Mockito.verify(transport, Mockito.times(1)).sync();
        notificationManager.sync();
        Mockito.verify(transport, Mockito.times(2)).sync();
        notificationManager.unsubscribeFromTopics(Arrays.asList(1L, 2L), true);
        Mockito.verify(transport, Mockito.times(3)).sync();
    }
}

