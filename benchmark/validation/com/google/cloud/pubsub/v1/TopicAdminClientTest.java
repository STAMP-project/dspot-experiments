/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsub.v1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.common.collect.Lists;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import com.google.pubsub.v1.DeleteTopicRequest;
import com.google.pubsub.v1.GetTopicRequest;
import com.google.pubsub.v1.ListTopicSubscriptionsRequest;
import com.google.pubsub.v1.ListTopicSubscriptionsResponse;
import com.google.pubsub.v1.ListTopicsRequest;
import com.google.pubsub.v1.ListTopicsResponse;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PublishRequest;
import com.google.pubsub.v1.PublishResponse;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class TopicAdminClientTest {
    private static MockPublisher mockPublisher;

    private static MockIAMPolicy mockIAMPolicy;

    private static MockSubscriber mockSubscriber;

    private static MockServiceHelper serviceHelper;

    private TopicAdminClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void createTopicTest() {
        ProjectTopicName name2 = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        Topic expectedResponse = Topic.newBuilder().setName(name2.toString()).build();
        TopicAdminClientTest.mockPublisher.addResponse(expectedResponse);
        ProjectTopicName name = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        Topic actualResponse = client.createTopic(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockPublisher.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        Topic actualRequest = ((Topic) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectTopicName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createTopicExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockPublisher.addException(exception);
        try {
            ProjectTopicName name = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
            client.createTopic(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void publishTest() {
        String messageIdsElement = "messageIdsElement-744837059";
        List<String> messageIds = Arrays.asList(messageIdsElement);
        PublishResponse expectedResponse = PublishResponse.newBuilder().addAllMessageIds(messageIds).build();
        TopicAdminClientTest.mockPublisher.addResponse(expectedResponse);
        ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        ByteString data = ByteString.copyFromUtf8("-86");
        PubsubMessage messagesElement = PubsubMessage.newBuilder().setData(data).build();
        List<PubsubMessage> messages = Arrays.asList(messagesElement);
        PublishResponse actualResponse = client.publish(topic, messages);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockPublisher.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        PublishRequest actualRequest = ((PublishRequest) (actualRequests.get(0)));
        Assert.assertEquals(topic, ProjectTopicName.parse(actualRequest.getTopic()));
        Assert.assertEquals(messages, actualRequest.getMessagesList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void publishExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockPublisher.addException(exception);
        try {
            ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
            ByteString data = ByteString.copyFromUtf8("-86");
            PubsubMessage messagesElement = PubsubMessage.newBuilder().setData(data).build();
            List<PubsubMessage> messages = Arrays.asList(messagesElement);
            client.publish(topic, messages);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getTopicTest() {
        ProjectTopicName name = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        Topic expectedResponse = Topic.newBuilder().setName(name.toString()).build();
        TopicAdminClientTest.mockPublisher.addResponse(expectedResponse);
        ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        Topic actualResponse = client.getTopic(topic);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockPublisher.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetTopicRequest actualRequest = ((GetTopicRequest) (actualRequests.get(0)));
        Assert.assertEquals(topic, ProjectTopicName.parse(actualRequest.getTopic()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getTopicExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockPublisher.addException(exception);
        try {
            ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
            client.getTopic(topic);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listTopicsTest() {
        String nextPageToken = "";
        Topic topicsElement = Topic.newBuilder().build();
        List<Topic> topics = Arrays.asList(topicsElement);
        ListTopicsResponse expectedResponse = ListTopicsResponse.newBuilder().setNextPageToken(nextPageToken).addAllTopics(topics).build();
        TopicAdminClientTest.mockPublisher.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        TopicAdminClient.ListTopicsPagedResponse pagedListResponse = client.listTopics(project);
        List<Topic> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockPublisher.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListTopicsRequest actualRequest = ((ListTopicsRequest) (actualRequests.get(0)));
        Assert.assertEquals(project, ProjectName.parse(actualRequest.getProject()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listTopicsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockPublisher.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            client.listTopics(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listTopicSubscriptionsTest() {
        String nextPageToken = "";
        ProjectSubscriptionName subscriptionsElement = ProjectSubscriptionName.of("[PROJECT]", "[SUBSCRIPTION]");
        List<ProjectSubscriptionName> subscriptions = Arrays.asList(subscriptionsElement);
        ListTopicSubscriptionsResponse expectedResponse = ListTopicSubscriptionsResponse.newBuilder().setNextPageToken(nextPageToken).addAllSubscriptions(ProjectSubscriptionName.toStringList(subscriptions)).build();
        TopicAdminClientTest.mockPublisher.addResponse(expectedResponse);
        ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        TopicAdminClient.ListTopicSubscriptionsPagedResponse pagedListResponse = client.listTopicSubscriptions(topic);
        List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));
        List<ProjectSubscriptionName> resourceNames = Lists.newArrayList(pagedListResponse.iterateAllAsProjectSubscriptionName());
        Assert.assertEquals(1, resourceNames.size());
        Assert.assertEquals(ProjectSubscriptionName.parse(expectedResponse.getSubscriptionsList().get(0)), resourceNames.get(0));
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockPublisher.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListTopicSubscriptionsRequest actualRequest = ((ListTopicSubscriptionsRequest) (actualRequests.get(0)));
        Assert.assertEquals(topic, ProjectTopicName.parse(actualRequest.getTopic()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listTopicSubscriptionsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockPublisher.addException(exception);
        try {
            ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
            client.listTopicSubscriptions(topic);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteTopicTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        TopicAdminClientTest.mockPublisher.addResponse(expectedResponse);
        ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
        client.deleteTopic(topic);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockPublisher.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteTopicRequest actualRequest = ((DeleteTopicRequest) (actualRequests.get(0)));
        Assert.assertEquals(topic, ProjectTopicName.parse(actualRequest.getTopic()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteTopicExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockPublisher.addException(exception);
        try {
            ProjectTopicName topic = ProjectTopicName.of("[PROJECT]", "[TOPIC]");
            client.deleteTopic(topic);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyTest() {
        int version = 351608024;
        ByteString etag = ByteString.copyFromUtf8("21");
        Policy expectedResponse = Policy.newBuilder().setVersion(version).setEtag(etag).build();
        TopicAdminClientTest.mockIAMPolicy.addResponse(expectedResponse);
        String formattedResource = ProjectTopicName.format("[PROJECT]", "[TOPIC]");
        Policy policy = Policy.newBuilder().build();
        Policy actualResponse = client.setIamPolicy(formattedResource, policy);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockIAMPolicy.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        SetIamPolicyRequest actualRequest = ((SetIamPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedResource, actualRequest.getResource());
        Assert.assertEquals(policy, actualRequest.getPolicy());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockIAMPolicy.addException(exception);
        try {
            String formattedResource = ProjectTopicName.format("[PROJECT]", "[TOPIC]");
            Policy policy = Policy.newBuilder().build();
            client.setIamPolicy(formattedResource, policy);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyTest() {
        int version = 351608024;
        ByteString etag = ByteString.copyFromUtf8("21");
        Policy expectedResponse = Policy.newBuilder().setVersion(version).setEtag(etag).build();
        TopicAdminClientTest.mockIAMPolicy.addResponse(expectedResponse);
        String formattedResource = ProjectTopicName.format("[PROJECT]", "[TOPIC]");
        Policy actualResponse = client.getIamPolicy(formattedResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockIAMPolicy.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetIamPolicyRequest actualRequest = ((GetIamPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedResource, actualRequest.getResource());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockIAMPolicy.addException(exception);
        try {
            String formattedResource = ProjectTopicName.format("[PROJECT]", "[TOPIC]");
            client.getIamPolicy(formattedResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsTest() {
        TestIamPermissionsResponse expectedResponse = TestIamPermissionsResponse.newBuilder().build();
        TopicAdminClientTest.mockIAMPolicy.addResponse(expectedResponse);
        String formattedResource = ProjectTopicName.format("[PROJECT]", "[TOPIC]");
        List<String> permissions = new ArrayList<>();
        TestIamPermissionsResponse actualResponse = client.testIamPermissions(formattedResource, permissions);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = TopicAdminClientTest.mockIAMPolicy.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        TestIamPermissionsRequest actualRequest = ((TestIamPermissionsRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedResource, actualRequest.getResource());
        Assert.assertEquals(permissions, actualRequest.getPermissionsList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TopicAdminClientTest.mockIAMPolicy.addException(exception);
        try {
            String formattedResource = ProjectTopicName.format("[PROJECT]", "[TOPIC]");
            List<String> permissions = new ArrayList<>();
            client.testIamPermissions(formattedResource, permissions);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

