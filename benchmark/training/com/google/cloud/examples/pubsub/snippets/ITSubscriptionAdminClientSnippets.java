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
import com.google.cloud.pubsub.v1.SubscriptionAdminClient.ListSubscriptionsPagedResponse;
import com.google.common.collect.Iterables;
import com.google.iam.v1.Policy;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.pubsub.v1.Subscription;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITSubscriptionAdminClientSnippets {
    private static final String NAME_SUFFIX = UUID.randomUUID().toString();

    private static String projectId;

    private static SubscriptionAdminClientSnippets subscriptionAdminClientSnippets;

    private static String[] topics = new String[]{ ITSubscriptionAdminClientSnippets.formatForTest("topic-1"), ITSubscriptionAdminClientSnippets.formatForTest("topic-2") };

    private static String[] subscriptions = new String[]{ ITSubscriptionAdminClientSnippets.formatForTest("subscription-1"), ITSubscriptionAdminClientSnippets.formatForTest("subscription-2") };

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void createSubscriptionWithPushIsSuccessful() throws Exception {
        String topicName = ITSubscriptionAdminClientSnippets.topics[0];
        String subscriptionName = ITSubscriptionAdminClientSnippets.subscriptions[0];
        createTopic(topicName);
        String endpoint = ("https://" + (ITSubscriptionAdminClientSnippets.projectId)) + ".appspot.com/push";
        Subscription subscription = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.createSubscriptionWithPushEndpoint(topicName, subscriptionName, endpoint);
        Assert.assertNotNull(subscription);
        Subscription retrievedSubscription = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.getSubscription(subscriptionName);
        Assert.assertNotNull(retrievedSubscription);
        Assert.assertEquals(subscription.getName(), retrievedSubscription.getName());
        Assert.assertEquals(subscription.getPushConfig().getPushEndpoint(), endpoint);
    }

    @Test
    public void replacePushConfigIsSuccessful() throws Exception {
        String topicName = ITSubscriptionAdminClientSnippets.topics[0];
        String subscriptionName = ITSubscriptionAdminClientSnippets.subscriptions[0];
        createSubscription(topicName, subscriptionName);
        String endpoint = ("https://" + (ITSubscriptionAdminClientSnippets.projectId)) + ".appspot.com/push";
        ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.replacePushConfig(subscriptionName, endpoint);
        Subscription subscription = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.getSubscription(subscriptionName);
        Assert.assertNotNull(subscription.getPushConfig());
        Assert.assertEquals(subscription.getPushConfig().getPushEndpoint(), endpoint);
    }

    @Test
    public void listSubscriptionsRetrievesAllAddedSubscriptions() throws Exception {
        List<Subscription> addedSubscriptions = new ArrayList<>();
        String topicName1 = ITSubscriptionAdminClientSnippets.topics[0];
        String subscriptionName1 = ITSubscriptionAdminClientSnippets.subscriptions[0];
        String topicName2 = ITSubscriptionAdminClientSnippets.topics[1];
        String subscriptionName2 = ITSubscriptionAdminClientSnippets.subscriptions[1];
        addedSubscriptions.add(createSubscription(topicName1, subscriptionName1));
        addedSubscriptions.add(createSubscription(topicName2, subscriptionName2));
        boolean[] subFound = new boolean[]{ false, false };
        ListSubscriptionsPagedResponse response = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.listSubscriptions();
        Assert.assertNotNull(response);
        Iterable<Subscription> subscriptions = response.iterateAll();
        for (int i = 0; i < 2; i++) {
            if (!(subFound[i])) {
                subFound[i] = Iterables.contains(subscriptions, addedSubscriptions.get(i));
            }
        }
        Assert.assertTrue(((subFound[0]) && (subFound[1])));
    }

    @Test(expected = ApiException.class)
    public void deleteSubscriptionThrowsExceptionWhenRetrieved() throws Exception {
        String topicName = ITSubscriptionAdminClientSnippets.topics[0];
        String subscriptionName = ITSubscriptionAdminClientSnippets.subscriptions[0];
        createSubscription(topicName, subscriptionName);
        ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.deleteSubscription(subscriptionName);
        // expected to throw exception on retrieval
        ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.getSubscription(subscriptionName);
    }

    @Test
    public void subscriptionHasValidIamPolicy() throws Exception {
        String topicName = ITSubscriptionAdminClientSnippets.topics[0];
        String subscriptionName = ITSubscriptionAdminClientSnippets.subscriptions[0];
        createSubscription(topicName, subscriptionName);
        Policy policy = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.getSubscriptionPolicy(subscriptionName);
        Assert.assertNotNull(policy);
    }

    @Test
    public void replaceSubscriptionPolicyAndTestPermissionsIsSuccessful() throws Exception {
        String topicName = ITSubscriptionAdminClientSnippets.topics[0];
        String subscriptionName = ITSubscriptionAdminClientSnippets.subscriptions[0];
        createSubscription(topicName, subscriptionName);
        Policy policy = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.replaceSubscriptionPolicy(subscriptionName);
        Assert.assertNotNull(policy.getBindingsCount());
        Assert.assertTrue(policy.getBindings(0).getRole().equalsIgnoreCase(Role.viewer().toString()));
        Assert.assertTrue(policy.getBindings(0).getMembers(0).equalsIgnoreCase(Identity.allAuthenticatedUsers().toString()));
        TestIamPermissionsResponse response = ITSubscriptionAdminClientSnippets.subscriptionAdminClientSnippets.testSubscriptionPermissions(subscriptionName);
        Assert.assertTrue(response.getPermissionsList().contains("pubsub.subscriptions.get"));
    }
}

