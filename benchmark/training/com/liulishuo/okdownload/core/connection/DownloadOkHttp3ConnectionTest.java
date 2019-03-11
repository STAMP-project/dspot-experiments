/**
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload.core.connection;


import DownloadOkHttp3Connection.Factory;
import OkHttpClient.Builder;
import Protocol.HTTP_1_1;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DownloadOkHttp3ConnectionTest {
    private DownloadOkHttp3Connection connection;

    private static final String URL = "https://jacksgong.com";

    @Mock
    private OkHttpClient client;

    @Test
    public void release() throws Exception {
        final Call call = Mockito.mock(Call.class);
        Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        final ResponseBody body = Mockito.mock(ResponseBody.class);
        connection.response = createResponseBuilder().body(body).build();
        connection.release();
        Mockito.verify(body).close();
        assertThat(connection.response).isNull();
    }

    @Test
    public void createCreator_withBuilder_Assigned() throws IOException {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        final Proxy proxy = Mockito.mock(Proxy.class);
        builder.proxy(proxy);
        DownloadOkHttp3Connection.Factory factory = new DownloadOkHttp3Connection.Factory().setBuilder(builder);
        assertThat(factory.builder()).isEqualTo(builder);
        DownloadOkHttp3Connection connection = ((DownloadOkHttp3Connection) (factory.create(DownloadOkHttp3ConnectionTest.URL)));
        assertThat(connection.client.proxy()).isEqualTo(proxy);
    }

    @Test
    public void createCreator_customizeWithoutBuilder_newOne() throws IOException {
        DownloadOkHttp3Connection.Factory creator = new DownloadOkHttp3Connection.Factory();
        assertThat(creator.builder()).isNotNull();
    }

    @Test
    public void createCreator_withoutBuilder_newOne() throws IOException {
        DownloadOkHttp3Connection.Factory factory = new DownloadOkHttp3Connection.Factory();
        DownloadOkHttp3Connection connection = ((DownloadOkHttp3Connection) (factory.create(DownloadOkHttp3ConnectionTest.URL)));
        assertThat(connection.client).isNotNull();
    }

    @Test(expected = IOException.class)
    public void getInputStream_responseIsNull_throwException() throws IOException {
        connection.getInputStream();
    }

    @Test
    public void getInputStream_executed_getRightInputStream() throws IOException {
        final Call call = Mockito.mock(Call.class);
        Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        final ResponseBody body = Mockito.mock(ResponseBody.class);
        final Response response = createResponseBuilder().body(body).build();
        Mockito.when(call.execute()).thenReturn(response);
        final BufferedSource source = Mockito.mock(BufferedSource.class);
        Mockito.when(body.source()).thenReturn(source);
        final InputStream expectedInputStream = Mockito.mock(InputStream.class);
        Mockito.when(source.inputStream()).thenReturn(expectedInputStream);
        connection.execute();
        final InputStream resultInputStream = connection.getInputStream();
        assertThat(resultInputStream).isEqualTo(expectedInputStream);
    }

    @Test
    public void addHeader_getRequestHeaderFiles_meet() throws IOException {
        assertThat(connection.getRequestProperty("no-exist-key")).isNull();
        DownloadOkHttp3Connection.Factory creator = new DownloadOkHttp3Connection.Factory();
        DownloadOkHttp3Connection connection = ((DownloadOkHttp3Connection) (creator.create(DownloadOkHttp3ConnectionTest.URL)));
        connection.addHeader("mock", "mock");
        connection.addHeader("mock1", "mock2");
        connection.addHeader("mock1", "mock3");
        assertThat(connection.getRequestProperty("mock")).isEqualTo("mock");
        Map<String, List<String>> headers = connection.getRequestProperties();
        assertThat(headers.keySet()).hasSize(2).contains("mock", "mock1");
        List<String> allValues = new ArrayList<>();
        Collection<List<String>> valueList = headers.values();
        for (List<String> values : valueList) {
            allValues.addAll(values);
        }
        assertThat(allValues).hasSize(3).contains("mock", "mock2", "mock3");
    }

    @Test
    public void getResponseHeaderFields_noResponse_null() {
        final Map<String, List<String>> nullFields = connection.getResponseHeaderFields();
        assertThat(nullFields).isNull();
        final String nullField = connection.getResponseHeaderField("no-exist-key");
        assertThat(nullField).isNull();
    }

    @Test
    public void getResponseHeaderFields_responseNotNull_getHeaders() throws IOException {
        final Call call = Mockito.mock(Call.class);
        Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        final Response response = createResponseBuilder().addHeader("test-key", "test-value").build();
        Mockito.when(call.execute()).thenReturn(response);
        connection.execute();
        final Map<String, List<String>> fieldsResult = connection.getResponseHeaderFields();
        assertThat(fieldsResult.get("test-key")).contains("test-value");
        assertThat(connection.getResponseHeaderField("test-key")).contains("test-value");
    }

    @Test
    public void execute() throws IOException {
        final Call call = Mockito.mock(Call.class);
        Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        final Request request = new Request.Builder().url("http://jacksgong.com").build();
        final Response response = createResponseBuilder().build();
        Mockito.when(call.execute()).thenReturn(response);
        final Request.Builder builder = Mockito.mock(Request.Builder.class);
        Mockito.when(builder.build()).thenReturn(request);
        DownloadOkHttp3Connection connection = new DownloadOkHttp3Connection(client, builder);
        connection.execute();
        Mockito.verify(client).newCall(ArgumentMatchers.eq(request));
        Mockito.verify(call).execute();
    }

    @Test(expected = IOException.class)
    public void getResponseCode_responseIsNull_throwException() throws IOException {
        connection.getResponseCode();
    }

    @Test
    public void getResponseCode_responseNotNull_getCode() throws IOException {
        final Call call = Mockito.mock(Call.class);
        Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenReturn(call);
        final int expectedCode = 201;
        final Response response = createResponseBuilder().code(expectedCode).build();
        Mockito.when(call.execute()).thenReturn(response);
        connection.execute();
        final Integer result = connection.getResponseCode();
        assertThat(result).isEqualTo(expectedCode);
    }

    @Test
    public void setRequestMethod() throws ProtocolException {
        final Request.Builder builder = Mockito.mock(Request.Builder.class);
        final DownloadOkHttp3Connection connection = new DownloadOkHttp3Connection(client, builder);
        assertThat(connection.setRequestMethod("HEAD")).isTrue();
        Mockito.verify(builder).method(ArgumentMatchers.eq("HEAD"), ArgumentMatchers.nullable(RequestBody.class));
    }

    @Test
    public void getRedirectLocation() {
        final Response.Builder responseBuilder = createResponseBuilder();
        connection.response = responseBuilder.build();
        assertThat(connection.getRedirectLocation()).isEqualTo(null);
        final Response priorRes = new Response.Builder().protocol(HTTP_1_1).code(302).request(new Request.Builder().url("http://fake.com").build()).message("message").build();
        connection.response = responseBuilder.priorResponse(priorRes).build();
        assertThat(connection.getRedirectLocation()).isEqualTo("http://jacksgong.com/");
    }
}

