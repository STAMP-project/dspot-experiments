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
package org.kaaproject.kaa.server.verifiers.facebook.verifier;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.verifier.UserVerifierCallback;
import org.kaaproject.kaa.server.verifiers.facebook.config.gen.FacebookAvroConfig;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;


public class FacebookUserVerifierTest extends FacebookUserVerifier {
    private static FacebookUserVerifier verifier;

    private static FacebookAvroConfig config;

    @Test
    public void invalidUserAccessCodeTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(400, (" {" + (((((((("       \"error\": {" + "         \"message\": \"Message describing the error\", ") + "         \"type\": \"OAuthException\", ") + "         \"code\": 190,") + "         \"error_subcode\": 467,") + "         \"error_user_title\": \"A title\",") + "         \"error_user_msg\": \"A message\"") + "       }") + "     }")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onTokenInvalid();
    }

    @Test
    public void expiredUserAccessTokenTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(400, (" {" + (((((((("       \"error\": {" + "         \"message\": \"Message describing the error\", ") + "         \"type\": \"OAuthException\", ") + "         \"code\": 190,") + "         \"error_subcode\": 463,") + "         \"error_user_title\": \"A title\",") + "         \"error_user_msg\": \"A message\"") + "       }") + "     }")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onTokenExpired();
    }

    @Test
    public void incompatibleUserIdsTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(200, ("{\"data\":{\"app_id\":\"1557997434440423\"," + (("\"application\":\"testApp\",\"expires_at\":1422990000," + "\"is_valid\":true,\"scopes\":[\"public_profile\"],\"user_id\"") + ":\"800033850084728\"}}")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.anyString());
        FacebookUserVerifierTest.verifier.stop();
    }

    @Test
    public void badRequestTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(400, "{}");
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        // no exception is thrown, if onVerificationFailure(String) was called
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.anyString());
        FacebookUserVerifierTest.verifier.stop();
    }

    @Test
    public void successfulVerificationTest() {
        String userId = "12456789123456";
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(200, ((("{\"data\":{\"app_id\":\"1557997434440423\"," + (("\"application\":\"testApp\",\"expires_at\":1422990000," + "\"is_valid\":true,\"scopes\":[\"public_profile\"],\"user_id\"") + ":")) + userId) + "}}"));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken(userId, "someToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onSuccess();
        FacebookUserVerifierTest.verifier.stop();
    }

    @Test
    public void connectionErrorTest() throws IOException {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifier();
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doThrow(new IOException()).when(httpClientMock).execute(ArgumentMatchers.any(HttpGet.class));
        ReflectionTestUtils.setField(FacebookUserVerifierTest.verifier, "httpClient", httpClientMock);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        Mockito.verify(callback, Mockito.timeout(1000)).onConnectionError(ArgumentMatchers.any(String.class));
        FacebookUserVerifierTest.verifier.stop();
    }

    @Test
    public void internalErrorTest() throws IOException {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifier();
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        // Throw any descendant of Exception, as the indicator of an internal error
        Mockito.doThrow(new NullPointerException()).when(httpClientMock).execute(ArgumentMatchers.any(HttpGet.class));
        ReflectionTestUtils.setField(FacebookUserVerifierTest.verifier, "httpClient", httpClientMock);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        Mockito.verify(callback, Mockito.timeout(1000)).onInternalError(ArgumentMatchers.any(String.class));
        FacebookUserVerifierTest.verifier.stop();
    }

    @Test
    public void unrecognizedResponseCodeTest() throws IOException {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(300, "");
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        Mockito.verify(callback, Mockito.timeout(1000)).onVerificationFailure(ArgumentMatchers.any(String.class));
        FacebookUserVerifierTest.verifier.stop();
    }

    @Test
    public void oauthErrorNoSubcodeTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(400, (" {" + (((((((("       \"error\": {" + "         \"message\": \"Message describing the error\", ") + "         \"type\": \"OAuthException\", ") + "         \"code\": 190,") + "         \"error_subcode\": null,") + "         \"error_user_title\": \"A title\",") + "         \"error_user_msg\": \"A message\"") + "       }") + "     }")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.any(String.class));
    }

    @Test
    public void unrecognizedOauthErrorSubcodeTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(400, (" {" + (((((((("       \"error\": {" + "         \"message\": \"Message describing the error\", ") + "         \"type\": \"OAuthException\", ") + "         \"code\": 190,") + "         \"error_subcode\": 111,") + "         \"error_user_title\": \"A title\",") + "         \"error_user_msg\": \"A message\"") + "       }") + "     }")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.any(String.class));
    }

    @Test
    public void unrecognizedResponseErrorCodeTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(400, (" {" + (((((((("       \"error\": {" + "         \"message\": \"Message describing the error\", ") + "         \"type\": \"OAuthException\", ") + "         \"code\": 111,") + "         \"error_subcode\": 111,") + "         \"error_user_title\": \"A title\",") + "         \"error_user_msg\": \"A message\"") + "       }") + "     }")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.any(String.class));
    }

    @Test
    public void unableToCloseHttpClientTest() throws Exception {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifier();
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        // Throw any descendant of Exception, as the indicator of an internal error
        Mockito.doThrow(new IOException()).when(httpClientMock).close();
        ReflectionTestUtils.setField(FacebookUserVerifierTest.verifier, "httpClient", httpClientMock);
        Logger LOG = Mockito.mock(Logger.class);
        Field logField = FacebookUserVerifier.class.getDeclaredField("LOG");
        // set final static field
        setFinalStatic(logField, LOG);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        FacebookUserVerifierTest.verifier.stop();
        Mockito.verify(callback, Mockito.timeout(1000)).onInternalError(ArgumentMatchers.any(String.class));
    }

    @Test
    public void getConfigurationClassTest() {
        FacebookUserVerifierTest.verifier = new FacebookUserVerifier();
        Assert.assertEquals(FacebookUserVerifierTest.verifier.getConfigurationClass(), FacebookAvroConfig.class);
    }

    @Test
    public void errorInDataResponseTest() {
        String userId = "12456789123456";
        FacebookUserVerifierTest.verifier = new FacebookUserVerifierTest.MyFacebookVerifier(200, ("{" + ((((((((((("\"data\":{" + "\"app_id\":\"1557997434440423\",") + "\"application\":\"testApp\",") + "\"expires_at\":1422990000,") + "\"is_valid\":true,") + "\"scopes\":[") + "\"public_profile\"") + "],") + "\"user_id\":134,") + "\"error\":{") + "\"message\":\"Message describing the error\",") + "\"code\":111}}}")));
        FacebookUserVerifierTest.verifier.init(null, FacebookUserVerifierTest.config);
        FacebookUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        FacebookUserVerifierTest.verifier.checkAccessToken(userId, "someToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000)).onTokenInvalid();
        FacebookUserVerifierTest.verifier.stop();
    }

    private static class MyFacebookVerifier extends FacebookUserVerifier {
        int responseCode;

        String inputStreamString = "";

        MyFacebookVerifier(int responseCode, String intputStreamString) {
            this.responseCode = responseCode;
            this.inputStreamString = intputStreamString;
        }

        @Override
        protected CloseableHttpResponse establishConnection(String userAccessToken, String accessToken) {
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

