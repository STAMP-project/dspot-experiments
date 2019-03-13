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
package org.kaaproject.kaa.server.verifiers.twitter.verifier;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.kaaproject.kaa.server.common.verifier.UserVerifierCallback;
import org.kaaproject.kaa.server.verifiers.twitter.config.gen.TwitterAvroConfig;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class TwitterUserVerifierTest extends TwitterUserVerifier {
    private static final String INVALID_TOKEN_CODE = "89";

    private static TwitterUserVerifier verifier;

    private static TwitterAvroConfig config;

    @Test
    public void successfulVerificationTest() {
        String userId = "12456789123456";
        TwitterUserVerifierTest.verifier = new TwitterUserVerifierTest.MyTwitterVerifier(200, (((((("{" + ((("    \"contributors_enabled\": true," + "    \"geo_enabled\": true,") + "    \"id\": 38895958,") + "    \"id_str\": \"")) + userId) + "\",") + "    \"is_translator\": false,") + "    \"lang\": \"en\",") + "    \"name\": \"Sean Cook\"}"));
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken(userId, "someToken someSecret", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onSuccess();
        TwitterUserVerifierTest.verifier.stop();
    }

    @Test
    public void incompatibleUserIdsTest() {
        String invalidUserId = "12456789123456";
        TwitterUserVerifierTest.verifier = new TwitterUserVerifierTest.MyTwitterVerifier(200, ("{" + (((((("    \"contributors_enabled\": true," + "    \"geo_enabled\": true,") + "    \"id\": 38895958,") + "    \"id_str\": \"123456\",") + "    \"is_translator\": false,") + "    \"lang\": \"en\",") + "    \"name\": \"Sean Cook\"}")));
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken(invalidUserId, "someToken someSecret", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.anyString());
        TwitterUserVerifierTest.verifier.stop();
    }

    @Test
    public void invalidUserAccessTokenTest() {
        TwitterUserVerifierTest.verifier = new TwitterUserVerifierTest.MyTwitterVerifier(400, ((("{\"errors\":[{\"message\":\"Sorry," + " that page does not exist\",\"code\": ") + (TwitterUserVerifierTest.INVALID_TOKEN_CODE)) + "}]}"));
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken("invalidUserId", "someToken someSecret", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onTokenInvalid();
    }

    @Test
    public void otherResponseCodeTest() {
        TwitterUserVerifierTest.verifier = new TwitterUserVerifierTest.MyTwitterVerifier(406, "{}");
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken("invalidUserId", "someToken someSecret", callback);
        // no exception is thrown, if onVerificationFailure(String) was called
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.anyString());
        TwitterUserVerifierTest.verifier.stop();
    }

    @Test
    public void badResponseWithOtherErrorCodeTest() {
        String otherErrorCode = "215";
        TwitterUserVerifierTest.verifier = new TwitterUserVerifierTest.MyTwitterVerifier(400, ((("{\"errors\":[{\"message\":\"Sorry," + " that page does not exist\",\"code\": ") + otherErrorCode) + "}]}"));
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken("invalidUserId", "someToken someSecret", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.any(String.class));
    }

    @Test
    public void connectionErrorTest() throws IOException {
        TwitterUserVerifierTest.verifier = new TwitterUserVerifier();
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doThrow(new IOException()).when(httpClientMock).execute(ArgumentMatchers.any(HttpHost.class), ArgumentMatchers.any(HttpRequest.class));
        ReflectionTestUtils.setField(TwitterUserVerifierTest.verifier, "httpClient", httpClientMock);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken("id", "token secret", callback);
        Mockito.verify(callback, Mockito.timeout(1000)).onConnectionError(ArgumentMatchers.any(String.class));
    }

    @Test
    public void internalErrorTest() throws IOException {
        TwitterUserVerifierTest.verifier = new TwitterUserVerifier();
        TwitterUserVerifierTest.verifier.init(null, TwitterUserVerifierTest.config);
        TwitterUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        // Throw any descendant of Exception, as the indicator of an internal error
        Mockito.doThrow(new NullPointerException()).when(httpClientMock).execute(ArgumentMatchers.any(HttpGet.class));
        ReflectionTestUtils.setField(TwitterUserVerifierTest.verifier, "httpClient", httpClientMock);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        TwitterUserVerifierTest.verifier.checkAccessToken("id", "token secret", callback);
        Mockito.verify(callback, Mockito.timeout(1000)).onInternalError(ArgumentMatchers.any(String.class));
    }

    private static class MyTwitterVerifier extends TwitterUserVerifier {
        int responseCode;

        String inputStreamString = "";

        MyTwitterVerifier(int responseCode, String intputStreamString) {
            this.responseCode = responseCode;
            this.inputStreamString = intputStreamString;
        }

        @Override
        protected CloseableHttpResponse establishConnection(String accessToken) {
            CloseableHttpResponse connection = Mockito.mock(CloseableHttpResponse.class);
            try {
                StatusLine statusLine = Mockito.mock(StatusLine.class);
                Mockito.when(statusLine.getStatusCode()).thenReturn(responseCode);
                HttpEntity httpEntity = Mockito.mock(HttpEntity.class);
                Mockito.when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(inputStreamString.getBytes(StandardCharsets.UTF_8)));
                Mockito.when(connection.getStatusLine()).thenReturn(statusLine);
                Mockito.when(connection.getEntity()).thenReturn(httpEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return connection;
        }
    }
}

