/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarqube.ws.client;


import HttpConnector.DEFAULT_CONNECT_TIMEOUT_MILLISECONDS;
import HttpConnector.DEFAULT_READ_TIMEOUT_MILLISECONDS;
import MediaTypes.PROTOBUF;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Credentials;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonarqube.ws.MediaTypes;

import static java.net.Proxy.Type.HTTP;


public class HttpConnectorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MockWebServer server;

    private String serverUrl;

    private HttpConnector underTest;

    @Test
    public void follow_redirects_post() throws IOException, InterruptedException {
        MockWebServer server2 = new MockWebServer();
        server2.start();
        server2.url("").url().toString();
        server.enqueue(new MockResponse().setResponseCode(302).setHeader("Location", server2.url("").url().toString()));
        server2.enqueue(new MockResponse().setResponseCode(200));
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        PostRequest request = new PostRequest("api/ce/submit").setParam("projectKey", "project");
        WsResponse response = underTest.call(request);
        RecordedRequest recordedRequest = server2.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getBody().readUtf8()).isEqualTo("projectKey=project");
        assertThat(response.requestUrl()).isEqualTo(server2.url("").url().toString());
        assertThat(response.code()).isEqualTo(200);
    }

    @Test
    public void test_default_settings() throws Exception {
        answerHelloWorld();
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        assertThat(underTest.baseUrl()).isEqualTo(serverUrl);
        GetRequest request = new GetRequest("api/issues/search").setMediaType(PROTOBUF);
        WsResponse response = underTest.call(request);
        // verify default timeouts on client
        assertThat(underTest.okHttpClient().connectTimeoutMillis()).isEqualTo(DEFAULT_CONNECT_TIMEOUT_MILLISECONDS);
        assertThat(underTest.okHttpClient().readTimeoutMillis()).isEqualTo(DEFAULT_READ_TIMEOUT_MILLISECONDS);
        // verify response
        assertThat(response.hasContent()).isTrue();
        assertThat(response.content()).isEqualTo("hello, world!");
        // verify the request received by server
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/issues/search");
        assertThat(recordedRequest.getHeader("Accept")).isEqualTo(PROTOBUF);
        assertThat(recordedRequest.getHeader("Accept-Charset")).isEqualTo("UTF-8");
        assertThat(recordedRequest.getHeader("User-Agent")).startsWith("okhttp/");
        // compression is handled by OkHttp
        assertThat(recordedRequest.getHeader("Accept-Encoding")).isEqualTo("gzip");
    }

    @Test
    public void add_headers_to_GET_request() throws Exception {
        answerHelloWorld();
        GetRequest request = new GetRequest("api/issues/search").setHeader("X-Foo", "fooz").setHeader("X-Bar", "barz");
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("X-Foo")).isEqualTo("fooz");
        assertThat(recordedRequest.getHeader("X-Bar")).isEqualTo("barz");
    }

    @Test
    public void use_basic_authentication() throws Exception {
        answerHelloWorld();
        underTest = HttpConnector.newBuilder().url(serverUrl).credentials("theLogin", "thePassword").build();
        GetRequest request = new GetRequest("api/issues/search");
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(Credentials.basic("theLogin", "thePassword"));
    }

    @Test
    public void use_basic_authentication_with_null_password() throws Exception {
        answerHelloWorld();
        underTest = HttpConnector.newBuilder().url(serverUrl).credentials("theLogin", null).build();
        GetRequest request = new GetRequest("api/issues/search");
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(Credentials.basic("theLogin", ""));
    }

    @Test
    public void use_basic_authentication_with_utf8_login_and_password() throws Exception {
        answerHelloWorld();
        String login = "??";
        String password = "??";
        underTest = HttpConnector.newBuilder().url(serverUrl).credentials(login, password).build();
        GetRequest request = new GetRequest("api/issues/search");
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        // do not use OkHttp Credentials.basic() in order to not use the same code as the code under test
        String expectedHeader = "Basic " + (Base64.getEncoder().encodeToString(((login + ":") + password).getBytes(StandardCharsets.UTF_8)));
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(expectedHeader);
    }

    /**
     * Access token replaces the couple {login,password} and is sent through
     * the login field
     */
    @Test
    public void use_access_token() throws Exception {
        answerHelloWorld();
        underTest = HttpConnector.newBuilder().url(serverUrl).token("theToken").build();
        GetRequest request = new GetRequest("api/issues/search");
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo(Credentials.basic("theToken", ""));
    }

    @Test
    public void systemPassCode_sets_header_when_value_is_not_null() throws InterruptedException {
        answerHelloWorld();
        String systemPassCode = (new Random().nextBoolean()) ? "" : RandomStringUtils.randomAlphanumeric(21);
        underTest = HttpConnector.newBuilder().url(serverUrl).systemPassCode(systemPassCode).build();
        GetRequest request = new GetRequest("api/issues/search");
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("X-sonar-passcode")).isEqualTo(systemPassCode);
    }

    @Test
    public void use_proxy_authentication() throws Exception {
        try (MockWebServer proxy = new MockWebServer()) {
            proxy.start();
            underTest = HttpConnector.newBuilder().url(serverUrl).proxy(new java.net.Proxy(HTTP, new java.net.InetSocketAddress(proxy.getHostName(), proxy.getPort()))).proxyCredentials("theProxyLogin", "theProxyPassword").build();
            GetRequest request = new GetRequest("api/issues/search");
            proxy.enqueue(new MockResponse().setResponseCode(407));
            proxy.enqueue(new MockResponse().setBody("OK!"));
            underTest.call(request);
            RecordedRequest recordedRequest = proxy.takeRequest();
            assertThat(recordedRequest.getHeader("Proxy-Authorization")).isNull();
            recordedRequest = proxy.takeRequest();
            assertThat(recordedRequest.getHeader("Proxy-Authorization")).isEqualTo(Credentials.basic("theProxyLogin", "theProxyPassword"));
        }
    }

    @Test
    public void use_proxy_authentication_wrong_crendentials() throws Exception {
        try (MockWebServer proxy = new MockWebServer()) {
            proxy.start();
            underTest = HttpConnector.newBuilder().url(serverUrl).proxy(new java.net.Proxy(HTTP, new java.net.InetSocketAddress(proxy.getHostName(), proxy.getPort()))).proxyCredentials("theProxyLogin", "wrongPassword").build();
            GetRequest request = new GetRequest("api/issues/search");
            proxy.enqueue(new MockResponse().setResponseCode(407));
            proxy.enqueue(new MockResponse().setResponseCode(407));
            proxy.enqueue(new MockResponse().setResponseCode(407));
            underTest.call(request);
            RecordedRequest recordedRequest = proxy.takeRequest();
            assertThat(recordedRequest.getHeader("Proxy-Authorization")).isNull();
            recordedRequest = proxy.takeRequest();
            assertThat(recordedRequest.getHeader("Proxy-Authorization")).isEqualTo(Credentials.basic("theProxyLogin", "wrongPassword"));
            assertThat(proxy.getRequestCount()).isEqualTo(2);
        }
    }

    @Test
    public void override_timeouts() {
        underTest = HttpConnector.newBuilder().url(serverUrl).readTimeoutMilliseconds(42).connectTimeoutMilliseconds(74).build();
        assertThat(underTest.okHttpClient().readTimeoutMillis()).isEqualTo(42);
        assertThat(underTest.okHttpClient().connectTimeoutMillis()).isEqualTo(74);
    }

    @Test
    public void send_user_agent() throws Exception {
        answerHelloWorld();
        underTest = HttpConnector.newBuilder().url(serverUrl).userAgent("Maven Plugin/2.3").build();
        underTest.call(new GetRequest("api/issues/search"));
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("User-Agent")).isEqualTo("Maven Plugin/2.3");
    }

    @Test
    public void fail_if_unknown_implementation_of_request() {
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        try {
            underTest.call(Mockito.mock(WsRequest.class));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageContaining("Unsupported implementation: ");
        }
    }

    @Test
    public void fail_if_malformed_URL() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Malformed URL: 'wrong URL'");
        underTest = HttpConnector.newBuilder().url("wrong URL").build();
    }

    @Test
    public void send_post_request() throws Exception {
        answerHelloWorld();
        PostRequest request = new PostRequest("api/issues/search").setParam("severity", "MAJOR").setMediaType(PROTOBUF);
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        WsResponse response = underTest.call(request);
        // verify response
        assertThat(response.hasContent()).isTrue();
        assertThat(response.content()).isEqualTo("hello, world!");
        // verify the request received by server
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/issues/search");
        assertThat(recordedRequest.getBody().readUtf8()).isEqualTo("severity=MAJOR");
        assertThat(recordedRequest.getHeader("Accept")).isEqualTo("application/x-protobuf");
    }

    @Test
    public void add_header_to_POST_request() throws Exception {
        answerHelloWorld();
        PostRequest request = new PostRequest("api/issues/search").setHeader("X-Foo", "fooz").setHeader("X-Bar", "barz");
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        underTest.call(request);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getHeader("X-Foo")).isEqualTo("fooz");
        assertThat(recordedRequest.getHeader("X-Bar")).isEqualTo("barz");
    }

    @Test
    public void upload_file() throws Exception {
        answerHelloWorld();
        File reportFile = temp.newFile();
        FileUtils.write(reportFile, "the report content");
        PostRequest request = new PostRequest("api/report/upload").setParam("project", "theKey").setPart("report", new PostRequest.Part(MediaTypes.TXT, reportFile)).setMediaType(PROTOBUF);
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        WsResponse response = underTest.call(request);
        assertThat(response.hasContent()).isTrue();
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/report/upload?project=theKey");
        String body = IOUtils.toString(recordedRequest.getBody().inputStream());
        assertThat(body).contains("Content-Disposition: form-data; name=\"report\"").contains("Content-Type: text/plain").contains("the report content");
    }

    @Test
    public void http_error() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));
        PostRequest request = new PostRequest("api/issues/search");
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        WsResponse wsResponse = underTest.call(request);
        assertThat(wsResponse.code()).isEqualTo(404);
    }

    @Test
    public void support_base_url_ending_with_slash() throws Exception {
        assertThat(serverUrl).endsWith("/");
        underTest = HttpConnector.newBuilder().url(StringUtils.removeEnd(serverUrl, "/")).build();
        GetRequest request = new GetRequest("api/issues/search");
        answerHelloWorld();
        WsResponse response = underTest.call(request);
        assertThat(response.hasContent()).isTrue();
    }

    @Test
    public void support_base_url_with_context() {
        // just to be sure
        assertThat(serverUrl).endsWith("/");
        underTest = HttpConnector.newBuilder().url(((serverUrl) + "sonar")).build();
        GetRequest request = new GetRequest("api/issues/search");
        answerHelloWorld();
        assertThat(underTest.call(request).requestUrl()).isEqualTo(((serverUrl) + "sonar/api/issues/search"));
        request = new GetRequest("/api/issues/search");
        answerHelloWorld();
        assertThat(underTest.call(request).requestUrl()).isEqualTo(((serverUrl) + "sonar/api/issues/search"));
    }

    @Test
    public void support_tls_versions_of_java8() {
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        assertTlsAndClearTextSpecifications(underTest);
        assertThat(underTest.okHttpClient().sslSocketFactory()).isInstanceOf(SSLSocketFactory.getDefault().getClass());
    }

    @Test
    public void override_timeout_on_get() {
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        server.enqueue(new MockResponse().setBodyDelay(100, TimeUnit.MILLISECONDS).setBody("Hello delayed"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(IsInstanceOf.instanceOf(SocketTimeoutException.class));
        WsResponse call = underTest.call(new GetRequest("/").setTimeOutInMs(5));
        assertThat(call.content()).equals("Hello delayed");
    }

    @Test
    public void override_timeout_on_post() {
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        // Headers are not affected by setBodyDelay, let's throttle the answer
        server.enqueue(new MockResponse().throttleBody(1, 100, TimeUnit.MILLISECONDS).setBody("Hello delayed"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(IsInstanceOf.instanceOf(SocketTimeoutException.class));
        WsResponse call = underTest.call(new PostRequest("/").setTimeOutInMs(5));
        assertThat(call.content()).equals("Hello delayed");
    }

    @Test
    public void override_timeout_on_post_with_redirect() {
        underTest = HttpConnector.newBuilder().url(serverUrl).build();
        server.enqueue(new MockResponse().setResponseCode(301).setHeader("Location:", "/redirect"));
        // Headers are not affected by setBodyDelay, let's throttle the answer
        server.enqueue(new MockResponse().throttleBody(1, 100, TimeUnit.MILLISECONDS).setBody("Hello delayed"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectCause(IsInstanceOf.instanceOf(SocketTimeoutException.class));
        WsResponse call = underTest.call(new PostRequest("/").setTimeOutInMs(5));
        assertThat(call.content()).equals("Hello delayed");
    }
}

