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
package com.google.cloud.oslogin.v1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.oslogin.common.OsLoginProto.SshPublicKey;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class OsLoginServiceClientTest {
    private static MockOsLoginService mockOsLoginService;

    private static MockServiceHelper serviceHelper;

    private OsLoginServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void deletePosixAccountTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[USER]", "[PROJECT]");
        client.deletePosixAccount(name);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeletePosixAccountRequest actualRequest = ((DeletePosixAccountRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deletePosixAccountExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[USER]", "[PROJECT]");
            client.deletePosixAccount(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteSshPublicKeyTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
        client.deleteSshPublicKey(name);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteSshPublicKeyRequest actualRequest = ((DeleteSshPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, FingerprintName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteSshPublicKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
            client.deleteSshPublicKey(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getLoginProfileTest() {
        String name2 = "name2-1052831874";
        boolean suspended = false;
        LoginProfile expectedResponse = LoginProfile.newBuilder().setName(name2).setSuspended(suspended).build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        UserName name = UserName.of("[USER]");
        LoginProfile actualResponse = client.getLoginProfile(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetLoginProfileRequest actualRequest = ((GetLoginProfileRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, UserName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getLoginProfileExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            UserName name = UserName.of("[USER]");
            client.getLoginProfile(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getSshPublicKeyTest() {
        String key = "key106079";
        long expirationTimeUsec = 2058878882L;
        String fingerprint = "fingerprint-1375934236";
        SshPublicKey expectedResponse = SshPublicKey.newBuilder().setKey(key).setExpirationTimeUsec(expirationTimeUsec).setFingerprint(fingerprint).build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
        SshPublicKey actualResponse = client.getSshPublicKey(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetSshPublicKeyRequest actualRequest = ((GetSshPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, FingerprintName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getSshPublicKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
            client.getSshPublicKey(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void importSshPublicKeyTest() {
        ImportSshPublicKeyResponse expectedResponse = ImportSshPublicKeyResponse.newBuilder().build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        UserName parent = UserName.of("[USER]");
        SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
        ImportSshPublicKeyResponse actualResponse = client.importSshPublicKey(parent, sshPublicKey);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ImportSshPublicKeyRequest actualRequest = ((ImportSshPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, UserName.parse(actualRequest.getParent()));
        Assert.assertEquals(sshPublicKey, actualRequest.getSshPublicKey());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void importSshPublicKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            UserName parent = UserName.of("[USER]");
            SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
            client.importSshPublicKey(parent, sshPublicKey);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void importSshPublicKeyTest2() {
        ImportSshPublicKeyResponse expectedResponse = ImportSshPublicKeyResponse.newBuilder().build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        UserName parent = UserName.of("[USER]");
        SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
        String projectId = "projectId-1969970175";
        ImportSshPublicKeyResponse actualResponse = client.importSshPublicKey(parent, sshPublicKey, projectId);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ImportSshPublicKeyRequest actualRequest = ((ImportSshPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, UserName.parse(actualRequest.getParent()));
        Assert.assertEquals(sshPublicKey, actualRequest.getSshPublicKey());
        Assert.assertEquals(projectId, actualRequest.getProjectId());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void importSshPublicKeyExceptionTest2() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            UserName parent = UserName.of("[USER]");
            SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
            String projectId = "projectId-1969970175";
            client.importSshPublicKey(parent, sshPublicKey, projectId);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateSshPublicKeyTest() {
        String key = "key106079";
        long expirationTimeUsec = 2058878882L;
        String fingerprint = "fingerprint-1375934236";
        SshPublicKey expectedResponse = SshPublicKey.newBuilder().setKey(key).setExpirationTimeUsec(expirationTimeUsec).setFingerprint(fingerprint).build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
        SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
        SshPublicKey actualResponse = client.updateSshPublicKey(name, sshPublicKey);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateSshPublicKeyRequest actualRequest = ((UpdateSshPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, FingerprintName.parse(actualRequest.getName()));
        Assert.assertEquals(sshPublicKey, actualRequest.getSshPublicKey());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateSshPublicKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
            SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
            client.updateSshPublicKey(name, sshPublicKey);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateSshPublicKeyTest2() {
        String key = "key106079";
        long expirationTimeUsec = 2058878882L;
        String fingerprint = "fingerprint-1375934236";
        SshPublicKey expectedResponse = SshPublicKey.newBuilder().setKey(key).setExpirationTimeUsec(expirationTimeUsec).setFingerprint(fingerprint).build();
        OsLoginServiceClientTest.mockOsLoginService.addResponse(expectedResponse);
        FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
        SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
        FieldMask updateMask = FieldMask.newBuilder().build();
        SshPublicKey actualResponse = client.updateSshPublicKey(name, sshPublicKey, updateMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = OsLoginServiceClientTest.mockOsLoginService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateSshPublicKeyRequest actualRequest = ((UpdateSshPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, FingerprintName.parse(actualRequest.getName()));
        Assert.assertEquals(sshPublicKey, actualRequest.getSshPublicKey());
        Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateSshPublicKeyExceptionTest2() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        OsLoginServiceClientTest.mockOsLoginService.addException(exception);
        try {
            FingerprintName name = FingerprintName.of("[USER]", "[FINGERPRINT]");
            SshPublicKey sshPublicKey = SshPublicKey.newBuilder().build();
            FieldMask updateMask = FieldMask.newBuilder().build();
            client.updateSshPublicKey(name, sshPublicKey, updateMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

