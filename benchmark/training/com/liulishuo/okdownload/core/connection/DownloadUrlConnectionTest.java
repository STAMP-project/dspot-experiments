/**
 * Copyright (c) 2017 LingoChamp Inc.
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


import DownloadConnection.Connected;
import DownloadConnection.NO_RESPONSE_CODE;
import DownloadUrlConnection.Factory;
import DownloadUrlConnection.RedirectHandler;
import com.liulishuo.okdownload.RedirectUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DownloadUrlConnectionTest {
    @Mock
    private Proxy proxy;

    @Mock
    private URLConnection urlConnection;

    @Mock
    private URL url;

    @Mock
    private Map<String, List<String>> headerFields;

    @Mock
    private InputStream inputStream;

    private RedirectHandler redirectHandler;

    private DownloadUrlConnection downloadUrlConnection;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void construct_noConfiguration_noAssigned() throws IOException {
        DownloadUrlConnection.Factory factory = new DownloadUrlConnection.Factory();
        factory.create("https://jacksgong.com");
        Mockito.verify(urlConnection, Mockito.never()).setConnectTimeout(ArgumentMatchers.anyInt());
        Mockito.verify(urlConnection, Mockito.never()).setReadTimeout(ArgumentMatchers.anyInt());
    }

    @Test
    public void construct_validConfiguration_Assigned() throws IOException {
        DownloadUrlConnection.Factory factory = new DownloadUrlConnection.Factory(new DownloadUrlConnection.Configuration().proxy(proxy).connectTimeout(123).readTimeout(456));
        factory.create(url);
        Mockito.verify(url).openConnection(proxy);
        Mockito.verify(urlConnection).setConnectTimeout(123);
        Mockito.verify(urlConnection).setReadTimeout(456);
    }

    @Test
    public void addHeader() throws Exception {
        downloadUrlConnection.addHeader("name1", "value1");
        Mockito.verify(urlConnection).addRequestProperty(ArgumentMatchers.eq("name1"), ArgumentMatchers.eq("value1"));
    }

    @Test
    public void execute() throws Exception {
        Mockito.doNothing().when(redirectHandler).handleRedirect(ArgumentMatchers.any(DownloadConnection.class), ArgumentMatchers.any(Connected.class), ArgumentMatchers.<String, List<String>>anyMap());
        downloadUrlConnection.execute();
        Mockito.verify(urlConnection).connect();
    }

    @Test
    public void handleRedirect() throws IOException {
        final DownloadUrlConnection.RedirectHandler handler = new DownloadUrlConnection.RedirectHandler();
        final Map<String, List<String>> headers = new HashMap<>();
        final String redirectLocation = "http://13.png";
        Mockito.when(downloadUrlConnection.getResponseCode()).thenReturn(302).thenReturn(206);
        Mockito.when(downloadUrlConnection.getResponseHeaderField("Location")).thenReturn(redirectLocation);
        Mockito.doNothing().when(downloadUrlConnection).configUrlConnection();
        Mockito.doNothing().when(urlConnection).connect();
        handler.handleRedirect(downloadUrlConnection, downloadUrlConnection, headers);
        Mockito.verify(downloadUrlConnection).release();
        Mockito.verify(downloadUrlConnection).configUrlConnection();
        Mockito.verify(urlConnection).connect();
        assertThat(handler.getRedirectLocation()).isEqualTo(redirectLocation);
    }

    @Test
    public void handleRedirect_error() throws IOException {
        final DownloadUrlConnection.RedirectHandler handler = new DownloadUrlConnection.RedirectHandler();
        final Map<String, List<String>> headers = new HashMap<>();
        final String redirectLocation = "http://13.png";
        Mockito.when(downloadUrlConnection.getResponseCode()).thenReturn(302);
        Mockito.when(downloadUrlConnection.getResponseHeaderField("Location")).thenReturn(redirectLocation);
        Mockito.doNothing().when(downloadUrlConnection).configUrlConnection();
        Mockito.doNothing().when(urlConnection).connect();
        thrown.expect(ProtocolException.class);
        thrown.expectMessage(("Too many redirect requests: " + ((RedirectUtil.MAX_REDIRECT_TIMES) + 1)));
        handler.handleRedirect(downloadUrlConnection, downloadUrlConnection, headers);
    }

    @Test
    public void getResponseCode() throws Exception {
        assertThat(downloadUrlConnection.getResponseCode()).isEqualTo(NO_RESPONSE_CODE);
    }

    @Test
    public void getInputStream() throws Exception {
        Mockito.when(urlConnection.getInputStream()).thenReturn(inputStream);
        assertThat(downloadUrlConnection.getInputStream()).isEqualTo(inputStream);
    }

    @Test
    public void getResponseHeaderFields() throws Exception {
        Mockito.when(urlConnection.getHeaderFields()).thenReturn(headerFields);
        assertThat(downloadUrlConnection.getResponseHeaderFields()).isEqualTo(headerFields);
    }

    @Test
    public void getResponseHeaderField() throws Exception {
        Mockito.when(urlConnection.getHeaderField("key1")).thenReturn("value1");
        assertThat(downloadUrlConnection.getResponseHeaderField("key1")).isEqualTo("value1");
    }

    @Test
    public void release() throws Exception {
        Mockito.when(urlConnection.getInputStream()).thenReturn(inputStream);
        downloadUrlConnection.release();
        Mockito.verify(inputStream).close();
    }

    @Test
    public void getRequestProperties() throws Exception {
        Mockito.when(urlConnection.getRequestProperties()).thenReturn(headerFields);
        assertThat(downloadUrlConnection.getRequestProperties()).isEqualTo(headerFields);
    }

    @Test
    public void getRequestProperty() throws Exception {
        Mockito.when(urlConnection.getRequestProperty("key1")).thenReturn("value1");
        assertThat(downloadUrlConnection.getRequestProperty("key1")).isEqualTo("value1");
    }

    @Test
    public void setRequestMethod() throws ProtocolException {
        final HttpURLConnection httpURLConnection = Mockito.mock(HttpURLConnection.class);
        final DownloadUrlConnection connection = new DownloadUrlConnection(httpURLConnection);
        assertThat(connection.setRequestMethod("HEAD")).isTrue();
        Mockito.verify(httpURLConnection).setRequestMethod(ArgumentMatchers.eq("HEAD"));
        assertThat(downloadUrlConnection.setRequestMethod("GET")).isFalse();
    }

    @Test
    public void getRedirectLocation() {
        final String redirectLocation = "http://13.png";
        redirectHandler.redirectLocation = redirectLocation;
        assertThat(downloadUrlConnection.getRedirectLocation()).isEqualTo(redirectLocation);
    }
}

