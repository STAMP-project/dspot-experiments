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
package org.kaaproject.kaa.client.transport;


import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class DesktopHttpClientTest {
    private static final String URL = "http://some-url";

    private static final String HTTP_CLIENT_FIELD_NAME = "httpClient";

    private static final String HTTP_METHOD_FIELD_NAME = "method";

    private static final int OK = 200;

    private static final int FAILURE = 400;

    private static PrivateKey privateKey;

    private static PublicKey publicKey;

    private static PublicKey remotePublicKey;

    private static LinkedHashMap<String, byte[]> entities = new LinkedHashMap<>();

    private CloseableHttpResponse httpResponse;

    @Test(expected = TransportException.class)
    public void executeInvalidHttpRequestTest() throws Exception {
        DesktopHttpClient client = new DesktopHttpClient(DesktopHttpClientTest.URL, DesktopHttpClientTest.privateKey, DesktopHttpClientTest.publicKey, DesktopHttpClientTest.remotePublicKey);
        CloseableHttpClient httpClientMock = mockForHttpClient(DesktopHttpClientTest.FAILURE, true, null);
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_CLIENT_FIELD_NAME, httpClientMock);
        client.executeHttpRequest(DesktopHttpClientTest.URL, DesktopHttpClientTest.entities, false);
        Mockito.verify(httpResponse).close();
    }

    @Test
    public void executeValidHttpRequest() throws Exception {
        byte[] inputData = new byte[]{ 100, 101, 102 };
        DesktopHttpClient client = new DesktopHttpClient(DesktopHttpClientTest.URL, DesktopHttpClientTest.privateKey, DesktopHttpClientTest.publicKey, DesktopHttpClientTest.remotePublicKey);
        CloseableHttpClient httpClientMock = mockForHttpClient(DesktopHttpClientTest.OK, true, inputData);
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_CLIENT_FIELD_NAME, httpClientMock);
        byte[] body = client.executeHttpRequest(DesktopHttpClientTest.URL, DesktopHttpClientTest.entities, false);
        Assert.assertArrayEquals(inputData, body);
        Mockito.verify(httpResponse).close();
    }

    @Test
    public void canAbortTest() throws Throwable {
        DesktopHttpClient client = new DesktopHttpClient(DesktopHttpClientTest.URL, DesktopHttpClientTest.privateKey, DesktopHttpClientTest.publicKey, DesktopHttpClientTest.remotePublicKey);
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_METHOD_FIELD_NAME, null);
        Assert.assertFalse(client.canAbort());
        HttpPost method = new HttpPost();
        method.abort();
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_METHOD_FIELD_NAME, method);
        Assert.assertFalse(client.canAbort());
        method = new HttpPost();
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_METHOD_FIELD_NAME, method);
        Assert.assertTrue(client.canAbort());
    }

    @Test(expected = IOException.class)
    public void executeValidHttpRequestWithNoResponseEntityTest() throws Exception {
        DesktopHttpClient client = new DesktopHttpClient(DesktopHttpClientTest.URL, DesktopHttpClientTest.privateKey, DesktopHttpClientTest.publicKey, DesktopHttpClientTest.remotePublicKey);
        CloseableHttpClient httpClientMock = mockForHttpClient(DesktopHttpClientTest.OK, false, null);
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_CLIENT_FIELD_NAME, httpClientMock);
        client.executeHttpRequest(DesktopHttpClientTest.URL, DesktopHttpClientTest.entities, false);
        Mockito.verify(httpResponse).close();
    }

    @Test
    public void closeTest() throws IOException {
        DesktopHttpClient client = new DesktopHttpClient(DesktopHttpClientTest.URL, DesktopHttpClientTest.privateKey, DesktopHttpClientTest.publicKey, DesktopHttpClientTest.remotePublicKey);
        CloseableHttpClient httpClientMock = mockForHttpClient(DesktopHttpClientTest.OK, false, null);
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_CLIENT_FIELD_NAME, httpClientMock);
        client.close();
        Mockito.verify(httpClientMock).close();
    }

    @Test
    public void abortTest() throws IOException {
        DesktopHttpClient client = new DesktopHttpClient(DesktopHttpClientTest.URL, DesktopHttpClientTest.privateKey, DesktopHttpClientTest.publicKey, DesktopHttpClientTest.remotePublicKey);
        HttpPost method = Mockito.mock(HttpPost.class);
        Mockito.when(method.isAborted()).thenReturn(false);
        ReflectionTestUtils.setField(client, DesktopHttpClientTest.HTTP_METHOD_FIELD_NAME, method);
        client.abort();
        Mockito.verify(method).abort();
    }
}

