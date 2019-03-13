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
package com.google.cloud.spanner.admin.database.v1;


import StatusCode.Code.INVALID_ARGUMENT;
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
import com.google.longrunning.Operation;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import com.google.spanner.admin.database.v1.CreateDatabaseRequest;
import com.google.spanner.admin.database.v1.Database;
import com.google.spanner.admin.database.v1.DatabaseName;
import com.google.spanner.admin.database.v1.DropDatabaseRequest;
import com.google.spanner.admin.database.v1.GetDatabaseDdlRequest;
import com.google.spanner.admin.database.v1.GetDatabaseDdlResponse;
import com.google.spanner.admin.database.v1.GetDatabaseRequest;
import com.google.spanner.admin.database.v1.InstanceName;
import com.google.spanner.admin.database.v1.ListDatabasesRequest;
import com.google.spanner.admin.database.v1.ListDatabasesResponse;
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class DatabaseAdminClientTest {
    private static MockDatabaseAdmin mockDatabaseAdmin;

    private static MockServiceHelper serviceHelper;

    private DatabaseAdminClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listDatabasesTest() {
        String nextPageToken = "";
        Database databasesElement = Database.newBuilder().build();
        List<Database> databases = Arrays.asList(databasesElement);
        ListDatabasesResponse expectedResponse = ListDatabasesResponse.newBuilder().setNextPageToken(nextPageToken).addAllDatabases(databases).build();
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        InstanceName parent = InstanceName.of("[PROJECT]", "[INSTANCE]");
        DatabaseAdminClient.ListDatabasesPagedResponse pagedListResponse = client.listDatabases(parent);
        List<Database> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getDatabasesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListDatabasesRequest actualRequest = ((ListDatabasesRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, InstanceName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listDatabasesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            InstanceName parent = InstanceName.of("[PROJECT]", "[INSTANCE]");
            client.listDatabases(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createDatabaseTest() throws Exception {
        DatabaseName name = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        Database expectedResponse = Database.newBuilder().setName(name.toString()).build();
        Operation resultOperation = Operation.newBuilder().setName("createDatabaseTest").setDone(true).setResponse(Any.pack(expectedResponse)).build();
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(resultOperation);
        InstanceName parent = InstanceName.of("[PROJECT]", "[INSTANCE]");
        String createStatement = "createStatement552974828";
        Database actualResponse = client.createDatabaseAsync(parent, createStatement).get();
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateDatabaseRequest actualRequest = ((CreateDatabaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, InstanceName.parse(actualRequest.getParent()));
        Assert.assertEquals(createStatement, actualRequest.getCreateStatement());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createDatabaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            InstanceName parent = InstanceName.of("[PROJECT]", "[INSTANCE]");
            String createStatement = "createStatement552974828";
            client.createDatabaseAsync(parent, createStatement).get();
            Assert.fail("No exception raised");
        } catch (ExecutionException e) {
            Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getDatabaseTest() {
        DatabaseName name2 = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        Database expectedResponse = Database.newBuilder().setName(name2.toString()).build();
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        DatabaseName name = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        Database actualResponse = client.getDatabase(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetDatabaseRequest actualRequest = ((GetDatabaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, DatabaseName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getDatabaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            DatabaseName name = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
            client.getDatabase(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateDatabaseDdlTest() throws Exception {
        Empty expectedResponse = Empty.newBuilder().build();
        Operation resultOperation = Operation.newBuilder().setName("updateDatabaseDdlTest").setDone(true).setResponse(Any.pack(expectedResponse)).build();
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(resultOperation);
        DatabaseName database = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        List<String> statements = new ArrayList<>();
        Empty actualResponse = client.updateDatabaseDdlAsync(database, statements).get();
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateDatabaseDdlRequest actualRequest = ((UpdateDatabaseDdlRequest) (actualRequests.get(0)));
        Assert.assertEquals(database, DatabaseName.parse(actualRequest.getDatabase()));
        Assert.assertEquals(statements, actualRequest.getStatementsList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateDatabaseDdlExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            DatabaseName database = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
            List<String> statements = new ArrayList<>();
            client.updateDatabaseDdlAsync(database, statements).get();
            Assert.fail("No exception raised");
        } catch (ExecutionException e) {
            Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void dropDatabaseTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        DatabaseName database = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        client.dropDatabase(database);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DropDatabaseRequest actualRequest = ((DropDatabaseRequest) (actualRequests.get(0)));
        Assert.assertEquals(database, DatabaseName.parse(actualRequest.getDatabase()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void dropDatabaseExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            DatabaseName database = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
            client.dropDatabase(database);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getDatabaseDdlTest() {
        GetDatabaseDdlResponse expectedResponse = GetDatabaseDdlResponse.newBuilder().build();
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        DatabaseName database = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        GetDatabaseDdlResponse actualResponse = client.getDatabaseDdl(database);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetDatabaseDdlRequest actualRequest = ((GetDatabaseDdlRequest) (actualRequests.get(0)));
        Assert.assertEquals(database, DatabaseName.parse(actualRequest.getDatabase()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getDatabaseDdlExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            DatabaseName database = DatabaseName.of("[PROJECT]", "[INSTANCE]", "[DATABASE]");
            client.getDatabaseDdl(database);
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
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        String formattedResource = DatabaseName.format("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        Policy policy = Policy.newBuilder().build();
        Policy actualResponse = client.setIamPolicy(formattedResource, policy);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
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
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            String formattedResource = DatabaseName.format("[PROJECT]", "[INSTANCE]", "[DATABASE]");
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
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        String formattedResource = DatabaseName.format("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        Policy actualResponse = client.getIamPolicy(formattedResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetIamPolicyRequest actualRequest = ((GetIamPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedResource, actualRequest.getResource());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            String formattedResource = DatabaseName.format("[PROJECT]", "[INSTANCE]", "[DATABASE]");
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
        DatabaseAdminClientTest.mockDatabaseAdmin.addResponse(expectedResponse);
        String formattedResource = DatabaseName.format("[PROJECT]", "[INSTANCE]", "[DATABASE]");
        List<String> permissions = new ArrayList<>();
        TestIamPermissionsResponse actualResponse = client.testIamPermissions(formattedResource, permissions);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DatabaseAdminClientTest.mockDatabaseAdmin.getRequests();
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
        DatabaseAdminClientTest.mockDatabaseAdmin.addException(exception);
        try {
            String formattedResource = DatabaseName.format("[PROJECT]", "[INSTANCE]", "[DATABASE]");
            List<String> permissions = new ArrayList<>();
            client.testIamPermissions(formattedResource, permissions);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

