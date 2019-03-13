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
package com.google.cloud.examples.pubsub.snippets;


import com.google.api.gax.rpc.ApiException;
import com.google.cloud.Identity;
import com.google.cloud.Role;
import com.google.cloud.pubsub.v1.TopicAdminClient.ListTopicSubscriptionsPagedResponse;
import com.google.cloud.pubsub.v1.TopicAdminClient.ListTopicsPagedResponse;
import com.google.common.collect.Iterables;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.Topic;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITTopicAdminClientSnippets {
    private static final String NAME_SUFFIX = UUID.randomUUID().toString();

    private static String projectId;

    private static TopicAdminClientSnippets topicAdminClientSnippets;

    private static String[] topics = new String[]{ ITTopicAdminClientSnippets.formatForTest("topic-1"), ITTopicAdminClientSnippets.formatForTest("topic-2") };

    private static String[] subscriptions = new String[]{ ITTopicAdminClientSnippets.formatForTest("subscription-1"), ITTopicAdminClientSnippets.formatForTest("subscription-2") };

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void topicAddedIsSameAsRetrieved() throws Exception {
        String topicName = ITTopicAdminClientSnippets.topics[0];
        Topic topicAdded = ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName);
        Assert.assertNotNull(topicAdded);
        Topic topicRetrieved = ITTopicAdminClientSnippets.topicAdminClientSnippets.getTopic(topicName);
        Assert.assertEquals(topicAdded, topicRetrieved);
    }

    @Test
    public void listTopicsRetreivesAddedTopics() throws Exception {
        List<Topic> addedTopics = new ArrayList<>();
        String topicName1 = ITTopicAdminClientSnippets.topics[0];
        addedTopics.add(ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName1));
        String topicName2 = ITTopicAdminClientSnippets.topics[1];
        addedTopics.add(ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName2));
        boolean[] topicFound = new boolean[]{ false, false };
        ListTopicsPagedResponse response = ITTopicAdminClientSnippets.topicAdminClientSnippets.listTopics();
        Assert.assertNotNull(response);
        Iterable<Topic> topics = response.iterateAll();
        for (int i = 0; i < 2; i++) {
            if (!(topicFound[i])) {
                topicFound[i] = Iterables.contains(topics, addedTopics.get(i));
            }
        }
        Assert.assertTrue(((topicFound[0]) && (topicFound[1])));
    }

    @Test
    public void listTopicSubscriptionsRetrievesAddedSubscriptions() throws Exception {
        List<String> addedSubscriptions = new ArrayList<>();
        String topicName1 = ITTopicAdminClientSnippets.topics[0];
        ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName1);
        String subscriptionName1 = ITTopicAdminClientSnippets.subscriptions[0];
        String subscriptionName2 = ITTopicAdminClientSnippets.subscriptions[1];
        addedSubscriptions.add(createSubscription(topicName1, subscriptionName1));
        addedSubscriptions.add(createSubscription(topicName1, subscriptionName2));
        boolean[] subFound = new boolean[]{ false, false };
        ListTopicSubscriptionsPagedResponse response = ITTopicAdminClientSnippets.topicAdminClientSnippets.listTopicSubscriptions(topicName1);
        Assert.assertNotNull(response);
        Iterable<String> subscriptions = response.iterateAll();
        for (int i = 0; i < 2; i++) {
            if (!(subFound[i])) {
                subFound[i] = Iterables.contains(subscriptions, addedSubscriptions.get(i));
            }
        }
        Assert.assertTrue(((subFound[0]) && (subFound[1])));
    }

    @Test(expected = ApiException.class)
    public void deletedTopicIsNotRetrievableAndThrowsException() throws Exception {
        String topicName = ITTopicAdminClientSnippets.topics[0];
        Topic topicAdded = ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName);
        Assert.assertNotNull(topicAdded);
        ProjectTopicName formattedName = ITTopicAdminClientSnippets.topicAdminClientSnippets.deleteTopic(topicName);
        Assert.assertNotNull(formattedName);
        ITTopicAdminClientSnippets.topicAdminClientSnippets.getTopic(topicName);
    }

    @Test
    public void topicPolicyIsCorrectlyRetrieved() throws Exception {
        String topicName = ITTopicAdminClientSnippets.topics[0];
        ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName);
        Policy policy = ITTopicAdminClientSnippets.topicAdminClientSnippets.getTopicPolicy(topicName);
        Assert.assertNotNull(policy);
    }

    @Test
    public void replaceTopicPolicyAndTestPermissionsIsSuccessful() throws Exception {
        String topicName = ITTopicAdminClientSnippets.topics[0];
        ITTopicAdminClientSnippets.topicAdminClientSnippets.createTopic(topicName);
        Policy policy = ITTopicAdminClientSnippets.topicAdminClientSnippets.replaceTopicPolicy(topicName);
        Assert.assertNotNull(policy.getBindingsCount());
        Assert.assertTrue(policy.getBindings(0).getRole().equalsIgnoreCase(Role.viewer().toString()));
        Assert.assertTrue(policy.getBindings(0).getMembers(0).equalsIgnoreCase(Identity.allAuthenticatedUsers().toString()));
        TestIamPermissionsResponse response = ITTopicAdminClientSnippets.topicAdminClientSnippets.testTopicPermissions(topicName);
        Assert.assertTrue(response.getPermissionsList().contains("pubsub.topics.get"));
    }
}

