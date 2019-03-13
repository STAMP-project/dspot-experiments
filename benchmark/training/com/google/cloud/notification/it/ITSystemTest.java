/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.notification.it;


import PayloadFormat.JSON_API_V1;
import com.google.cloud.notification.Notification;
import com.google.cloud.notification.NotificationInfo;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.iam.v1.Binding;
import com.google.iam.v1.Policy;
import com.google.pubsub.v1.ProjectTopicName;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITSystemTest {
    private static RemoteStorageHelper remoteStorageHelper;

    private static TopicAdminClient topicAdminClient;

    private static Notification notificationService;

    private static Storage storageService;

    private static final Logger log = Logger.getLogger(ITSystemTest.class.getName());

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final String NAME_SUFFIX = UUID.randomUUID().toString();

    private static String projectId;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testNotifications() {
        // Use Pubsub to create a Topic.
        final ProjectTopicName topic = ProjectTopicName.of(ITSystemTest.projectId, formatForTest("testing-topic-foo"));
        ITSystemTest.topicAdminClient.createTopic(topic);
        Policy policy = ITSystemTest.topicAdminClient.getIamPolicy(topic.toString());
        Binding binding = Binding.newBuilder().setRole("roles/owner").addMembers("allAuthenticatedUsers").build();
        Policy newPolicy = ITSystemTest.topicAdminClient.setIamPolicy(topic.toString(), policy.toBuilder().addBindings(binding).build());
        Assert.assertTrue(newPolicy.getBindingsList().contains(binding));
        String permissionName = "pubsub.topics.get";
        List<String> permissions = ITSystemTest.topicAdminClient.testIamPermissions(topic.toString(), Collections.singletonList(permissionName)).getPermissionsList();
        Assert.assertTrue(permissions.contains(permissionName));
        // Use Storage API to create a Notification on that Topic.
        NotificationInfo notification = ITSystemTest.notificationService.createNotification(ITSystemTest.BUCKET, NotificationInfo.of(topic));
        Assert.assertNotNull(notification);
        List<NotificationInfo> notifications = ITSystemTest.notificationService.listNotifications(ITSystemTest.BUCKET);
        Assert.assertTrue(notifications.contains(notification));
        Assert.assertEquals(1, notifications.size());
        NotificationInfo notification2 = ITSystemTest.notificationService.createNotification(ITSystemTest.BUCKET, NotificationInfo.of(topic).toBuilder().setPayloadFormat(JSON_API_V1).build());
        Assert.assertEquals(topic, notification2.getTopic());
        notifications = ITSystemTest.notificationService.listNotifications(ITSystemTest.BUCKET);
        Assert.assertTrue(notifications.contains(notification));
        Assert.assertTrue(notifications.contains(notification2));
        Assert.assertEquals(2, notifications.size());
        Assert.assertTrue(ITSystemTest.notificationService.deleteNotification(ITSystemTest.BUCKET, notification.getGeneratedId()));
        Assert.assertTrue(ITSystemTest.notificationService.deleteNotification(ITSystemTest.BUCKET, notification2.getGeneratedId()));
        Assert.assertNull(ITSystemTest.notificationService.listNotifications(ITSystemTest.BUCKET));
        ITSystemTest.topicAdminClient.deleteTopic(topic);
    }
}

