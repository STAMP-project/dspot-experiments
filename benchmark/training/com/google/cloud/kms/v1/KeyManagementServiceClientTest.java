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
package com.google.cloud.kms.v1;


import CryptoKey.CryptoKeyPurpose;
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
public class KeyManagementServiceClientTest {
    private static MockKeyManagementService mockKeyManagementService;

    private static MockIAMPolicy mockIAMPolicy;

    private static MockServiceHelper serviceHelper;

    private KeyManagementServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listKeyRingsTest() {
        String nextPageToken = "";
        int totalSize = 705419236;
        KeyRing keyRingsElement = KeyRing.newBuilder().build();
        List<KeyRing> keyRings = Arrays.asList(keyRingsElement);
        ListKeyRingsResponse expectedResponse = ListKeyRingsResponse.newBuilder().setNextPageToken(nextPageToken).setTotalSize(totalSize).addAllKeyRings(keyRings).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
        KeyManagementServiceClient.ListKeyRingsPagedResponse pagedListResponse = client.listKeyRings(parent);
        List<KeyRing> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getKeyRingsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListKeyRingsRequest actualRequest = ((ListKeyRingsRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listKeyRingsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
            client.listKeyRings(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listCryptoKeysTest() {
        String nextPageToken = "";
        int totalSize = 705419236;
        CryptoKey cryptoKeysElement = CryptoKey.newBuilder().build();
        List<CryptoKey> cryptoKeys = Arrays.asList(cryptoKeysElement);
        ListCryptoKeysResponse expectedResponse = ListCryptoKeysResponse.newBuilder().setNextPageToken(nextPageToken).setTotalSize(totalSize).addAllCryptoKeys(cryptoKeys).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        KeyRingName parent = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        KeyManagementServiceClient.ListCryptoKeysPagedResponse pagedListResponse = client.listCryptoKeys(parent);
        List<CryptoKey> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getCryptoKeysList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListCryptoKeysRequest actualRequest = ((ListCryptoKeysRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, KeyRingName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listCryptoKeysExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            KeyRingName parent = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
            client.listCryptoKeys(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listCryptoKeyVersionsTest() {
        String nextPageToken = "";
        int totalSize = 705419236;
        CryptoKeyVersion cryptoKeyVersionsElement = CryptoKeyVersion.newBuilder().build();
        List<CryptoKeyVersion> cryptoKeyVersions = Arrays.asList(cryptoKeyVersionsElement);
        ListCryptoKeyVersionsResponse expectedResponse = ListCryptoKeyVersionsResponse.newBuilder().setNextPageToken(nextPageToken).setTotalSize(totalSize).addAllCryptoKeyVersions(cryptoKeyVersions).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyName parent = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        KeyManagementServiceClient.ListCryptoKeyVersionsPagedResponse pagedListResponse = client.listCryptoKeyVersions(parent);
        List<CryptoKeyVersion> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getCryptoKeyVersionsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListCryptoKeyVersionsRequest actualRequest = ((ListCryptoKeyVersionsRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, CryptoKeyName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listCryptoKeyVersionsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyName parent = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
            client.listCryptoKeyVersions(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getKeyRingTest() {
        KeyRingName name2 = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        KeyRing expectedResponse = KeyRing.newBuilder().setName(name2.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        KeyRingName name = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        KeyRing actualResponse = client.getKeyRing(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetKeyRingRequest actualRequest = ((GetKeyRingRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, KeyRingName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getKeyRingExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            KeyRingName name = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
            client.getKeyRing(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getCryptoKeyTest() {
        CryptoKeyName name2 = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        CryptoKey expectedResponse = CryptoKey.newBuilder().setName(name2.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        CryptoKey actualResponse = client.getCryptoKey(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetCryptoKeyRequest actualRequest = ((GetCryptoKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getCryptoKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
            client.getCryptoKey(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getCryptoKeyVersionTest() {
        CryptoKeyVersionName name2 = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion expectedResponse = CryptoKeyVersion.newBuilder().setName(name2.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion actualResponse = client.getCryptoKeyVersion(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetCryptoKeyVersionRequest actualRequest = ((GetCryptoKeyVersionRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyVersionName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getCryptoKeyVersionExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
            client.getCryptoKeyVersion(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createKeyRingTest() {
        KeyRingName name = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        KeyRing expectedResponse = KeyRing.newBuilder().setName(name.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
        String keyRingId = "keyRingId-2056646742";
        KeyRing keyRing = KeyRing.newBuilder().build();
        KeyRing actualResponse = client.createKeyRing(parent, keyRingId, keyRing);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateKeyRingRequest actualRequest = ((CreateKeyRingRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
        Assert.assertEquals(keyRingId, actualRequest.getKeyRingId());
        Assert.assertEquals(keyRing, actualRequest.getKeyRing());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createKeyRingExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
            String keyRingId = "keyRingId-2056646742";
            KeyRing keyRing = KeyRing.newBuilder().build();
            client.createKeyRing(parent, keyRingId, keyRing);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createCryptoKeyTest() {
        CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        CryptoKey expectedResponse = CryptoKey.newBuilder().setName(name.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        KeyRingName parent = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        String cryptoKeyId = "my-app-key";
        CryptoKey.CryptoKeyPurpose purpose = CryptoKeyPurpose.ENCRYPT_DECRYPT;
        long seconds = 2147483647L;
        Timestamp nextRotationTime = Timestamp.newBuilder().setSeconds(seconds).build();
        long seconds2 = 604800L;
        Duration rotationPeriod = Duration.newBuilder().setSeconds(seconds2).build();
        CryptoKey cryptoKey = CryptoKey.newBuilder().setPurpose(purpose).setNextRotationTime(nextRotationTime).setRotationPeriod(rotationPeriod).build();
        CryptoKey actualResponse = client.createCryptoKey(parent, cryptoKeyId, cryptoKey);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateCryptoKeyRequest actualRequest = ((CreateCryptoKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, KeyRingName.parse(actualRequest.getParent()));
        Assert.assertEquals(cryptoKeyId, actualRequest.getCryptoKeyId());
        Assert.assertEquals(cryptoKey, actualRequest.getCryptoKey());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createCryptoKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            KeyRingName parent = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
            String cryptoKeyId = "my-app-key";
            CryptoKey.CryptoKeyPurpose purpose = CryptoKeyPurpose.ENCRYPT_DECRYPT;
            long seconds = 2147483647L;
            Timestamp nextRotationTime = Timestamp.newBuilder().setSeconds(seconds).build();
            long seconds2 = 604800L;
            Duration rotationPeriod = Duration.newBuilder().setSeconds(seconds2).build();
            CryptoKey cryptoKey = CryptoKey.newBuilder().setPurpose(purpose).setNextRotationTime(nextRotationTime).setRotationPeriod(rotationPeriod).build();
            client.createCryptoKey(parent, cryptoKeyId, cryptoKey);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createCryptoKeyVersionTest() {
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion expectedResponse = CryptoKeyVersion.newBuilder().setName(name.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyName parent = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        CryptoKeyVersion cryptoKeyVersion = CryptoKeyVersion.newBuilder().build();
        CryptoKeyVersion actualResponse = client.createCryptoKeyVersion(parent, cryptoKeyVersion);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateCryptoKeyVersionRequest actualRequest = ((CreateCryptoKeyVersionRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, CryptoKeyName.parse(actualRequest.getParent()));
        Assert.assertEquals(cryptoKeyVersion, actualRequest.getCryptoKeyVersion());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createCryptoKeyVersionExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyName parent = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
            CryptoKeyVersion cryptoKeyVersion = CryptoKeyVersion.newBuilder().build();
            client.createCryptoKeyVersion(parent, cryptoKeyVersion);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateCryptoKeyTest() {
        CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        CryptoKey expectedResponse = CryptoKey.newBuilder().setName(name.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKey cryptoKey = CryptoKey.newBuilder().build();
        FieldMask updateMask = FieldMask.newBuilder().build();
        CryptoKey actualResponse = client.updateCryptoKey(cryptoKey, updateMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateCryptoKeyRequest actualRequest = ((UpdateCryptoKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(cryptoKey, actualRequest.getCryptoKey());
        Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateCryptoKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKey cryptoKey = CryptoKey.newBuilder().build();
            FieldMask updateMask = FieldMask.newBuilder().build();
            client.updateCryptoKey(cryptoKey, updateMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateCryptoKeyVersionTest() {
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion expectedResponse = CryptoKeyVersion.newBuilder().setName(name.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersion cryptoKeyVersion = CryptoKeyVersion.newBuilder().build();
        FieldMask updateMask = FieldMask.newBuilder().build();
        CryptoKeyVersion actualResponse = client.updateCryptoKeyVersion(cryptoKeyVersion, updateMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateCryptoKeyVersionRequest actualRequest = ((UpdateCryptoKeyVersionRequest) (actualRequests.get(0)));
        Assert.assertEquals(cryptoKeyVersion, actualRequest.getCryptoKeyVersion());
        Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateCryptoKeyVersionExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersion cryptoKeyVersion = CryptoKeyVersion.newBuilder().build();
            FieldMask updateMask = FieldMask.newBuilder().build();
            client.updateCryptoKeyVersion(cryptoKeyVersion, updateMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void encryptTest() {
        String name2 = "name2-1052831874";
        ByteString ciphertext = ByteString.copyFromUtf8("-72");
        EncryptResponse expectedResponse = EncryptResponse.newBuilder().setName(name2).setCiphertext(ciphertext).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyPathName name = CryptoKeyPathName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY_PATH]");
        ByteString plaintext = ByteString.copyFromUtf8("-9");
        EncryptResponse actualResponse = client.encrypt(name, plaintext);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        EncryptRequest actualRequest = ((EncryptRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyPathName.parse(actualRequest.getName()));
        Assert.assertEquals(plaintext, actualRequest.getPlaintext());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void encryptExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyPathName name = CryptoKeyPathName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY_PATH]");
            ByteString plaintext = ByteString.copyFromUtf8("-9");
            client.encrypt(name, plaintext);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void decryptTest() {
        ByteString plaintext = ByteString.copyFromUtf8("-9");
        DecryptResponse expectedResponse = DecryptResponse.newBuilder().setPlaintext(plaintext).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        ByteString ciphertext = ByteString.copyFromUtf8("-72");
        DecryptResponse actualResponse = client.decrypt(name, ciphertext);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DecryptRequest actualRequest = ((DecryptRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyName.parse(actualRequest.getName()));
        Assert.assertEquals(ciphertext, actualRequest.getCiphertext());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void decryptExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
            ByteString ciphertext = ByteString.copyFromUtf8("-72");
            client.decrypt(name, ciphertext);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateCryptoKeyPrimaryVersionTest() {
        CryptoKeyName name2 = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        CryptoKey expectedResponse = CryptoKey.newBuilder().setName(name2.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
        String cryptoKeyVersionId = "cryptoKeyVersionId729489152";
        CryptoKey actualResponse = client.updateCryptoKeyPrimaryVersion(name, cryptoKeyVersionId);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateCryptoKeyPrimaryVersionRequest actualRequest = ((UpdateCryptoKeyPrimaryVersionRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyName.parse(actualRequest.getName()));
        Assert.assertEquals(cryptoKeyVersionId, actualRequest.getCryptoKeyVersionId());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateCryptoKeyPrimaryVersionExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyName name = CryptoKeyName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]");
            String cryptoKeyVersionId = "cryptoKeyVersionId729489152";
            client.updateCryptoKeyPrimaryVersion(name, cryptoKeyVersionId);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void destroyCryptoKeyVersionTest() {
        CryptoKeyVersionName name2 = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion expectedResponse = CryptoKeyVersion.newBuilder().setName(name2.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion actualResponse = client.destroyCryptoKeyVersion(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DestroyCryptoKeyVersionRequest actualRequest = ((DestroyCryptoKeyVersionRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyVersionName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void destroyCryptoKeyVersionExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
            client.destroyCryptoKeyVersion(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void restoreCryptoKeyVersionTest() {
        CryptoKeyVersionName name2 = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion expectedResponse = CryptoKeyVersion.newBuilder().setName(name2.toString()).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        CryptoKeyVersion actualResponse = client.restoreCryptoKeyVersion(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        RestoreCryptoKeyVersionRequest actualRequest = ((RestoreCryptoKeyVersionRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyVersionName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void restoreCryptoKeyVersionExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
            client.restoreCryptoKeyVersion(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getPublicKeyTest() {
        String pem = "pem110872";
        PublicKey expectedResponse = PublicKey.newBuilder().setPem(pem).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        PublicKey actualResponse = client.getPublicKey(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetPublicKeyRequest actualRequest = ((GetPublicKeyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyVersionName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getPublicKeyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
            client.getPublicKey(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void asymmetricDecryptTest() {
        ByteString plaintext = ByteString.copyFromUtf8("-9");
        AsymmetricDecryptResponse expectedResponse = AsymmetricDecryptResponse.newBuilder().setPlaintext(plaintext).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        ByteString ciphertext = ByteString.copyFromUtf8("-72");
        AsymmetricDecryptResponse actualResponse = client.asymmetricDecrypt(name, ciphertext);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AsymmetricDecryptRequest actualRequest = ((AsymmetricDecryptRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyVersionName.parse(actualRequest.getName()));
        Assert.assertEquals(ciphertext, actualRequest.getCiphertext());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void asymmetricDecryptExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
            ByteString ciphertext = ByteString.copyFromUtf8("-72");
            client.asymmetricDecrypt(name, ciphertext);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void asymmetricSignTest() {
        ByteString signature = ByteString.copyFromUtf8("106");
        AsymmetricSignResponse expectedResponse = AsymmetricSignResponse.newBuilder().setSignature(signature).build();
        KeyManagementServiceClientTest.mockKeyManagementService.addResponse(expectedResponse);
        CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
        Digest digest = Digest.newBuilder().build();
        AsymmetricSignResponse actualResponse = client.asymmetricSign(name, digest);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockKeyManagementService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AsymmetricSignRequest actualRequest = ((AsymmetricSignRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, CryptoKeyVersionName.parse(actualRequest.getName()));
        Assert.assertEquals(digest, actualRequest.getDigest());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void asymmetricSignExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockKeyManagementService.addException(exception);
        try {
            CryptoKeyVersionName name = CryptoKeyVersionName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]", "[CRYPTO_KEY]", "[CRYPTO_KEY_VERSION]");
            Digest digest = Digest.newBuilder().build();
            client.asymmetricSign(name, digest);
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
        KeyManagementServiceClientTest.mockIAMPolicy.addResponse(expectedResponse);
        KeyName resource = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        Policy policy = Policy.newBuilder().build();
        Policy actualResponse = client.setIamPolicy(resource, policy);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockIAMPolicy.getRequests();
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
        KeyManagementServiceClientTest.mockIAMPolicy.addException(exception);
        try {
            KeyName resource = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
            Policy policy = Policy.newBuilder().build();
            client.setIamPolicy(resource, policy);
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
        KeyManagementServiceClientTest.mockIAMPolicy.addResponse(expectedResponse);
        KeyName resource = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        Policy actualResponse = client.getIamPolicy(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockIAMPolicy.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetIamPolicyRequest actualRequest = ((GetIamPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(Objects.toString(resource), Objects.toString(actualRequest.getResource()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        KeyManagementServiceClientTest.mockIAMPolicy.addException(exception);
        try {
            KeyName resource = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
            client.getIamPolicy(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsTest() {
        TestIamPermissionsResponse expectedResponse = TestIamPermissionsResponse.newBuilder().build();
        KeyManagementServiceClientTest.mockIAMPolicy.addResponse(expectedResponse);
        KeyName resource = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
        List<String> permissions = new ArrayList<>();
        TestIamPermissionsResponse actualResponse = client.testIamPermissions(resource, permissions);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = KeyManagementServiceClientTest.mockIAMPolicy.getRequests();
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
        KeyManagementServiceClientTest.mockIAMPolicy.addException(exception);
        try {
            KeyName resource = KeyRingName.of("[PROJECT]", "[LOCATION]", "[KEY_RING]");
            List<String> permissions = new ArrayList<>();
            client.testIamPermissions(resource, permissions);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

