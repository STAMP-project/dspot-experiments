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
package com.google.cloud.tasks.v2beta2;


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
import com.google.protobuf.Duration;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class CloudTasksClientTest {
    private static MockCloudTasks mockCloudTasks;

    private static MockServiceHelper serviceHelper;

    private CloudTasksClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listQueuesTest() {
        String nextPageToken = "";
        Queue queuesElement = Queue.newBuilder().build();
        List<Queue> queues = Arrays.asList(queuesElement);
        ListQueuesResponse expectedResponse = ListQueuesResponse.newBuilder().setNextPageToken(nextPageToken).addAllQueues(queues).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
        CloudTasksClient.ListQueuesPagedResponse pagedListResponse = client.listQueues(parent);
        List<Queue> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getQueuesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListQueuesRequest actualRequest = ((ListQueuesRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listQueuesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
            client.listQueues(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getQueueTest() {
        QueueName name2 = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue expectedResponse = Queue.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue actualResponse = client.getQueue(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetQueueRequest actualRequest = ((GetQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, QueueName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.getQueue(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createQueueTest() {
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue expectedResponse = Queue.newBuilder().setName(name.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
        Queue queue = Queue.newBuilder().build();
        Queue actualResponse = client.createQueue(parent, queue);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateQueueRequest actualRequest = ((CreateQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
        Assert.assertEquals(queue, actualRequest.getQueue());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
            Queue queue = Queue.newBuilder().build();
            client.createQueue(parent, queue);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateQueueTest() {
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue expectedResponse = Queue.newBuilder().setName(name.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        Queue queue = Queue.newBuilder().build();
        FieldMask updateMask = FieldMask.newBuilder().build();
        Queue actualResponse = client.updateQueue(queue, updateMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateQueueRequest actualRequest = ((UpdateQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(queue, actualRequest.getQueue());
        Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            Queue queue = Queue.newBuilder().build();
            FieldMask updateMask = FieldMask.newBuilder().build();
            client.updateQueue(queue, updateMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteQueueTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        client.deleteQueue(name);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteQueueRequest actualRequest = ((DeleteQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, QueueName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.deleteQueue(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void purgeQueueTest() {
        QueueName name2 = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue expectedResponse = Queue.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue actualResponse = client.purgeQueue(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        PurgeQueueRequest actualRequest = ((PurgeQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, QueueName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void purgeQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.purgeQueue(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void pauseQueueTest() {
        QueueName name2 = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue expectedResponse = Queue.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue actualResponse = client.pauseQueue(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        PauseQueueRequest actualRequest = ((PauseQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, QueueName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void pauseQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.pauseQueue(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void resumeQueueTest() {
        QueueName name2 = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue expectedResponse = Queue.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Queue actualResponse = client.resumeQueue(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ResumeQueueRequest actualRequest = ((ResumeQueueRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, QueueName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void resumeQueueExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName name = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.resumeQueue(name);
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
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName resource = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Policy actualResponse = client.getIamPolicy(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetIamPolicyRequest actualRequest = ((GetIamPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(Objects.toString(resource), Objects.toString(actualRequest.getResource()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName resource = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.getIamPolicy(resource);
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
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName resource = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Policy policy = Policy.newBuilder().build();
        Policy actualResponse = client.setIamPolicy(resource, policy);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        SetIamPolicyRequest actualRequest = ((SetIamPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(Objects.toString(resource), Objects.toString(actualRequest.getResource()));
        Assert.assertEquals(policy, actualRequest.getPolicy());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName resource = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            Policy policy = Policy.newBuilder().build();
            client.setIamPolicy(resource, policy);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsTest() {
        TestIamPermissionsResponse expectedResponse = TestIamPermissionsResponse.newBuilder().build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName resource = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        List<String> permissions = new ArrayList<>();
        TestIamPermissionsResponse actualResponse = client.testIamPermissions(resource, permissions);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        TestIamPermissionsRequest actualRequest = ((TestIamPermissionsRequest) (actualRequests.get(0)));
        Assert.assertEquals(Objects.toString(resource), Objects.toString(actualRequest.getResource()));
        Assert.assertEquals(permissions, actualRequest.getPermissionsList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName resource = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            List<String> permissions = new ArrayList<>();
            client.testIamPermissions(resource, permissions);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listTasksTest() {
        String nextPageToken = "";
        Task tasksElement = Task.newBuilder().build();
        List<Task> tasks = Arrays.asList(tasksElement);
        ListTasksResponse expectedResponse = ListTasksResponse.newBuilder().setNextPageToken(nextPageToken).addAllTasks(tasks).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName parent = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        CloudTasksClient.ListTasksPagedResponse pagedListResponse = client.listTasks(parent);
        List<Task> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getTasksList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListTasksRequest actualRequest = ((ListTasksRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, QueueName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listTasksExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName parent = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            client.listTasks(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getTaskTest() {
        TaskName name2 = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task expectedResponse = Task.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task actualResponse = client.getTask(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetTaskRequest actualRequest = ((GetTaskRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, TaskName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getTaskExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
            client.getTask(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createTaskTest() {
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task expectedResponse = Task.newBuilder().setName(name.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        QueueName parent = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
        Task task = Task.newBuilder().build();
        Task actualResponse = client.createTask(parent, task);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateTaskRequest actualRequest = ((CreateTaskRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, QueueName.parse(actualRequest.getParent()));
        Assert.assertEquals(task, actualRequest.getTask());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createTaskExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            QueueName parent = QueueName.of("[PROJECT]", "[LOCATION]", "[QUEUE]");
            Task task = Task.newBuilder().build();
            client.createTask(parent, task);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteTaskTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        client.deleteTask(name);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteTaskRequest actualRequest = ((DeleteTaskRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, TaskName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteTaskExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
            client.deleteTask(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void acknowledgeTaskTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Timestamp scheduleTime = Timestamp.newBuilder().build();
        client.acknowledgeTask(name, scheduleTime);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AcknowledgeTaskRequest actualRequest = ((AcknowledgeTaskRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, TaskName.parse(actualRequest.getName()));
        Assert.assertEquals(scheduleTime, actualRequest.getScheduleTime());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void acknowledgeTaskExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
            Timestamp scheduleTime = Timestamp.newBuilder().build();
            client.acknowledgeTask(name, scheduleTime);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void renewLeaseTest() {
        TaskName name2 = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task expectedResponse = Task.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Timestamp scheduleTime = Timestamp.newBuilder().build();
        Duration leaseDuration = Duration.newBuilder().build();
        Task actualResponse = client.renewLease(name, scheduleTime, leaseDuration);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        RenewLeaseRequest actualRequest = ((RenewLeaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, TaskName.parse(actualRequest.getName()));
        Assert.assertEquals(scheduleTime, actualRequest.getScheduleTime());
        Assert.assertEquals(leaseDuration, actualRequest.getLeaseDuration());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void renewLeaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
            Timestamp scheduleTime = Timestamp.newBuilder().build();
            Duration leaseDuration = Duration.newBuilder().build();
            client.renewLease(name, scheduleTime, leaseDuration);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void cancelLeaseTest() {
        TaskName name2 = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task expectedResponse = Task.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Timestamp scheduleTime = Timestamp.newBuilder().build();
        Task actualResponse = client.cancelLease(name, scheduleTime);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CancelLeaseRequest actualRequest = ((CancelLeaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, TaskName.parse(actualRequest.getName()));
        Assert.assertEquals(scheduleTime, actualRequest.getScheduleTime());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void cancelLeaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
            Timestamp scheduleTime = Timestamp.newBuilder().build();
            client.cancelLease(name, scheduleTime);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void runTaskTest() {
        TaskName name2 = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task expectedResponse = Task.newBuilder().setName(name2.toString()).build();
        CloudTasksClientTest.mockCloudTasks.addResponse(expectedResponse);
        TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
        Task actualResponse = client.runTask(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = CloudTasksClientTest.mockCloudTasks.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        RunTaskRequest actualRequest = ((RunTaskRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, TaskName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void runTaskExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        CloudTasksClientTest.mockCloudTasks.addException(exception);
        try {
            TaskName name = TaskName.of("[PROJECT]", "[LOCATION]", "[QUEUE]", "[TASK]");
            client.runTask(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

