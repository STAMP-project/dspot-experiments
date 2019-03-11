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
package org.kaaproject.kaa.server.verifiers.gplus.verifier;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.verifier.UserVerifierCallback;
import org.kaaproject.kaa.server.verifiers.gplus.config.gen.GplusAvroConfig;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;


public class GplusUserVerifierTest extends GplusUserVerifier {
    private static GplusUserVerifier verifier;

    private static String userId = "1557997434440423";

    private static GplusAvroConfig config;

    @Test
    public void invalidUserAccessCodeTest() {
        GplusUserVerifierTest.verifier = new GplusUserVerifierTest.MyGplusVerifier(200, ("{\n" + ((" \"error\": \"invalid_token\",\n" + " \"error_description\": \"Invalid Value\"\n") + "}\n")));
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.anyString());
    }

    @Test
    public void incompatibleUserIdsTest() {
        GplusUserVerifierTest.verifier = new GplusUserVerifierTest.MyGplusVerifier(200, (((((("{\n" + ("  \"audience\":\"8819981768.apps.googleusercontent.com\",\n" + "  \"user_id\":\"")) + (GplusUserVerifierTest.userId)) + "\",\n") + "  \"scope\":\"profile email\",\n") + "  \"expires_in\":436\n") + "}"));
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onVerificationFailure(ArgumentMatchers.anyString());
    }

    @Test
    public void badRequestTest() {
        GplusUserVerifierTest.verifier = new GplusUserVerifierTest.MyGplusVerifier(400);
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onTokenInvalid();
    }

    @Test
    public void successfulVerificationTest() {
        String userId = "12456789123456";
        GplusUserVerifierTest.verifier = new GplusUserVerifierTest.MyGplusVerifier(200, (((((("{\n" + ("  \"audience\":\"8819981768.apps.googleusercontent.com\",\n" + "  \"user_id\":\"")) + userId) + "\",\n") + "  \"scope\":\"profile email\",\n") + "  \"expires_in\":436\n") + "}"));
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken(userId, "someToken", callback);
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onSuccess();
    }

    @Test
    public void internalErrorIOExceptionTest() throws IOException {
        GplusUserVerifierTest.verifier = new GplusUserVerifier();
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doThrow(new IOException()).when(httpClientMock).execute(ArgumentMatchers.any(HttpGet.class));
        ReflectionTestUtils.setField(GplusUserVerifierTest.verifier, "httpClient", httpClientMock);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        GplusUserVerifierTest.verifier.stop();
        Mockito.verify(callback, Mockito.timeout(1000)).onInternalError();
    }

    @Test
    public void internalErrorExceptionTest() throws Exception {
        GplusUserVerifierTest.verifier = new GplusUserVerifier();
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doThrow(new NullPointerException()).when(httpClientMock).execute(ArgumentMatchers.any(HttpGet.class));
        ReflectionTestUtils.setField(GplusUserVerifierTest.verifier, "httpClient", httpClientMock);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        GplusUserVerifierTest.verifier.stop();
        Mockito.verify(callback, Mockito.timeout(2000)).onInternalError();
    }

    @Test
    public void unableToCloseHttpClientTest() throws Exception {
        GplusUserVerifierTest.verifier = new GplusUserVerifier();
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        // Throw any descendant of Exception, as the indicator of an internal error
        Mockito.doThrow(new IOException()).when(httpClientMock).close();
        ReflectionTestUtils.setField(GplusUserVerifierTest.verifier, "httpClient", httpClientMock);
        Logger LOG = Mockito.mock(Logger.class);
        Field logField = GplusUserVerifier.class.getDeclaredField("LOG");
        setFinalStatic(logField, LOG);
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        GplusUserVerifierTest.verifier.stop();
        Mockito.verify(callback, Mockito.timeout(1000)).onInternalError();
    }

    @Test
    public void getConfigurationClassTest() {
        GplusUserVerifierTest.verifier = new GplusUserVerifier();
        Assert.assertEquals(GplusUserVerifierTest.verifier.getConfigurationClass(), GplusAvroConfig.class);
    }

    @Test
    public void internalErrorBadResponseCodeTest() throws IOException {
        GplusUserVerifierTest.verifier = new GplusUserVerifierTest.MyGplusVerifier(503, (((((("{\n" + ("  \"audience\":\"8819981768.apps.googleusercontent.com\",\n" + "  \"user_id\":\"")) + (GplusUserVerifierTest.userId)) + "\",\n") + "  \"scope\":\"profile email\",\n") + "  \"expires_in\":436\n") + "}"));
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("invalidUserId", "falseUserAccessToken", callback);
        GplusUserVerifierTest.verifier.stop();
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onInternalError();
    }

    @Test
    public void checkMalformedUri() throws Exception {
        GplusUserVerifierTest.verifier = new GplusUserVerifier();
        GplusUserVerifierTest.verifier.init(null, GplusUserVerifierTest.config);
        GplusUserVerifierTest.verifier.start();
        Field uriPart = GplusUserVerifier.class.getDeclaredField("GOOGLE_OAUTH");
        setFinalStatic(uriPart, "\\\\\\\\\\");
        UserVerifierCallback callback = Mockito.mock(UserVerifierCallback.class);
        GplusUserVerifierTest.verifier.checkAccessToken("id", "token", callback);
        GplusUserVerifierTest.verifier.stop();
        Mockito.verify(callback, Mockito.timeout(1000).atLeastOnce()).onInternalError();
        setFinalStatic(uriPart, "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=");
    }

    private static class MyGplusVerifier extends GplusUserVerifier {
        int responseCode;

        String inputStreamString = "";

        MyGplusVerifier(int responseCode) {
            this.responseCode = responseCode;
        }

        MyGplusVerifier(int responseCode, String intputStreamString) {
            this.responseCode = responseCode;
            this.inputStreamString = intputStreamString;
        }

        @Override
        protected CloseableHttpResponse establishConnection(URI uri) {
            CloseableHttpResponse closeableHttpResponse = Mockito.mock(CloseableHttpResponse.class);
            try {
                StatusLine statusLine = Mockito.mock(StatusLine.class);
                Mockito.when(statusLine.getStatusCode()).thenReturn(responseCode);
                HttpEntity httpEntity = Mockito.mock(HttpEntity.class);
                Mockito.when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(inputStreamString.getBytes(StandardCharsets.UTF_8)));
                Mockito.when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
                Mockito.when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return closeableHttpResponse;
        }
    }
}

