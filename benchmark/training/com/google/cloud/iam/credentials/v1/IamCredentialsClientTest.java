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
package com.google.cloud.iam.credentials.v1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class IamCredentialsClientTest {
    private static MockIAMCredentials mockIAMCredentials;

    private static MockServiceHelper serviceHelper;

    private IamCredentialsClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void generateAccessTokenTest() {
        String accessToken = "accessToken-1938933922";
        GenerateAccessTokenResponse expectedResponse = GenerateAccessTokenResponse.newBuilder().setAccessToken(accessToken).build();
        IamCredentialsClientTest.mockIAMCredentials.addResponse(expectedResponse);
        String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
        List<String> delegates = new ArrayList<>();
        List<String> scope = new ArrayList<>();
        Duration lifetime = Duration.newBuilder().build();
        GenerateAccessTokenResponse actualResponse = client.generateAccessToken(formattedName, delegates, scope, lifetime);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = IamCredentialsClientTest.mockIAMCredentials.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GenerateAccessTokenRequest actualRequest = ((GenerateAccessTokenRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedName, actualRequest.getName());
        Assert.assertEquals(delegates, actualRequest.getDelegatesList());
        Assert.assertEquals(scope, actualRequest.getScopeList());
        Assert.assertEquals(lifetime, actualRequest.getLifetime());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void generateAccessTokenExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        IamCredentialsClientTest.mockIAMCredentials.addException(exception);
        try {
            String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
            List<String> delegates = new ArrayList<>();
            List<String> scope = new ArrayList<>();
            Duration lifetime = Duration.newBuilder().build();
            client.generateAccessToken(formattedName, delegates, scope, lifetime);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void generateIdTokenTest() {
        String token = "token110541305";
        GenerateIdTokenResponse expectedResponse = GenerateIdTokenResponse.newBuilder().setToken(token).build();
        IamCredentialsClientTest.mockIAMCredentials.addResponse(expectedResponse);
        String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
        List<String> delegates = new ArrayList<>();
        String audience = "audience975628804";
        boolean includeEmail = false;
        GenerateIdTokenResponse actualResponse = client.generateIdToken(formattedName, delegates, audience, includeEmail);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = IamCredentialsClientTest.mockIAMCredentials.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GenerateIdTokenRequest actualRequest = ((GenerateIdTokenRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedName, actualRequest.getName());
        Assert.assertEquals(delegates, actualRequest.getDelegatesList());
        Assert.assertEquals(audience, actualRequest.getAudience());
        Assert.assertEquals(includeEmail, actualRequest.getIncludeEmail());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void generateIdTokenExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        IamCredentialsClientTest.mockIAMCredentials.addException(exception);
        try {
            String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
            List<String> delegates = new ArrayList<>();
            String audience = "audience975628804";
            boolean includeEmail = false;
            client.generateIdToken(formattedName, delegates, audience, includeEmail);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void signBlobTest() {
        String keyId = "keyId-1134673157";
        ByteString signedBlob = ByteString.copyFromUtf8("-32");
        SignBlobResponse expectedResponse = SignBlobResponse.newBuilder().setKeyId(keyId).setSignedBlob(signedBlob).build();
        IamCredentialsClientTest.mockIAMCredentials.addResponse(expectedResponse);
        String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
        List<String> delegates = new ArrayList<>();
        ByteString payload = ByteString.copyFromUtf8("-114");
        SignBlobResponse actualResponse = client.signBlob(formattedName, delegates, payload);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = IamCredentialsClientTest.mockIAMCredentials.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        SignBlobRequest actualRequest = ((SignBlobRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedName, actualRequest.getName());
        Assert.assertEquals(delegates, actualRequest.getDelegatesList());
        Assert.assertEquals(payload, actualRequest.getPayload());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void signBlobExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        IamCredentialsClientTest.mockIAMCredentials.addException(exception);
        try {
            String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
            List<String> delegates = new ArrayList<>();
            ByteString payload = ByteString.copyFromUtf8("-114");
            client.signBlob(formattedName, delegates, payload);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void signJwtTest() {
        String keyId = "keyId-1134673157";
        String signedJwt = "signedJwt-979546844";
        SignJwtResponse expectedResponse = SignJwtResponse.newBuilder().setKeyId(keyId).setSignedJwt(signedJwt).build();
        IamCredentialsClientTest.mockIAMCredentials.addResponse(expectedResponse);
        String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
        List<String> delegates = new ArrayList<>();
        String payload = "-114";
        SignJwtResponse actualResponse = client.signJwt(formattedName, delegates, payload);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = IamCredentialsClientTest.mockIAMCredentials.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        SignJwtRequest actualRequest = ((SignJwtRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedName, actualRequest.getName());
        Assert.assertEquals(delegates, actualRequest.getDelegatesList());
        Assert.assertEquals(payload, actualRequest.getPayload());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void signJwtExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        IamCredentialsClientTest.mockIAMCredentials.addException(exception);
        try {
            String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
            List<String> delegates = new ArrayList<>();
            String payload = "-114";
            client.signJwt(formattedName, delegates, payload);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void generateIdentityBindingAccessTokenTest() {
        String accessToken = "accessToken-1938933922";
        GenerateIdentityBindingAccessTokenResponse expectedResponse = GenerateIdentityBindingAccessTokenResponse.newBuilder().setAccessToken(accessToken).build();
        IamCredentialsClientTest.mockIAMCredentials.addResponse(expectedResponse);
        String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
        List<String> scope = new ArrayList<>();
        String jwt = "jwt105671";
        GenerateIdentityBindingAccessTokenResponse actualResponse = client.generateIdentityBindingAccessToken(formattedName, scope, jwt);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = IamCredentialsClientTest.mockIAMCredentials.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GenerateIdentityBindingAccessTokenRequest actualRequest = ((GenerateIdentityBindingAccessTokenRequest) (actualRequests.get(0)));
        Assert.assertEquals(formattedName, actualRequest.getName());
        Assert.assertEquals(scope, actualRequest.getScopeList());
        Assert.assertEquals(jwt, actualRequest.getJwt());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void generateIdentityBindingAccessTokenExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        IamCredentialsClientTest.mockIAMCredentials.addException(exception);
        try {
            String formattedName = IamCredentialsClient.formatServiceAccountName("[PROJECT]", "[SERVICE_ACCOUNT]");
            List<String> scope = new ArrayList<>();
            String jwt = "jwt105671";
            client.generateIdentityBindingAccessToken(formattedName, scope, jwt);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

