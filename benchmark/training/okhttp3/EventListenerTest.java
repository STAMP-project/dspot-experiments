/**
 * Copyright (C) 2017 Square, Inc.
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
package okhttp3;


import HttpLoggingInterceptor.Level.BODY;
import Protocol.HTTP_1_1;
import Protocol.HTTP_2;
import SocketPolicy.DISCONNECT_DURING_REQUEST_BODY;
import SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY;
import SocketPolicy.EXPECT_CONTINUE;
import SocketPolicy.FAIL_HANDSHAKE;
import SocketPolicy.UPGRADE_TO_SSL_AT_END;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.DoubleInetAddressDns;
import okhttp3.internal.RecordingOkAuthenticator;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.tls.HandshakeCertificates;
import okio.BufferedSink;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public final class EventListenerTest {
    public static final Matcher<Response> anyResponse = CoreMatchers.any(Response.class);

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    private final RecordingEventListener listener = new RecordingEventListener();

    private final HandshakeCertificates handshakeCertificates = localhost();

    private OkHttpClient client = clientTestRule.client;

    private SocksProxy socksProxy;

    @Test
    public void successfulCallEventSequence() throws IOException {
        server.enqueue(new MockResponse().setBody("abc"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("abc", response.body().string());
        response.body().close();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void successfulCallEventSequenceForEnqueue() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        final CountDownLatch completionLatch = new CountDownLatch(1);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                completionLatch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) {
                response.close();
                completionLatch.countDown();
            }
        };
        call.enqueue(callback);
        completionLatch.await();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void failedCallEventSequence() {
        server.enqueue(new MockResponse().setHeadersDelay(2, TimeUnit.SECONDS));
        client = client.newBuilder().readTimeout(250, TimeUnit.MILLISECONDS).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertThat(expected.getMessage(), CoreMatchers.either(CoreMatchers.equalTo("timeout")).or(CoreMatchers.equalTo("Read timed out")));
        }
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseFailed", "ConnectionReleased", "CallFailed");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void failedDribbledCallEventSequence() throws IOException {
        server.enqueue(new MockResponse().setBody("0123456789").throttleBody(2, 100, TimeUnit.MILLISECONDS).setSocketPolicy(DISCONNECT_DURING_RESPONSE_BODY));
        client = client.newBuilder().protocols(Collections.singletonList(HTTP_1_1)).readTimeout(250, TimeUnit.MILLISECONDS).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        try {
            response.body.string();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertThat(expected.getMessage(), CoreMatchers.equalTo("unexpected end of stream"));
        }
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseFailed", "ConnectionReleased", "CallFailed");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
        RecordingEventListener.ResponseFailed responseFailed = listener.removeUpToEvent(RecordingEventListener.ResponseFailed.class);
        Assert.assertEquals("unexpected end of stream", responseFailed.ioe.getMessage());
    }

    @Test
    public void canceledCallEventSequence() {
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        call.cancel();
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("Canceled", expected.getMessage());
        }
        List<String> expectedEvents = Arrays.asList("CallStart", "CallFailed");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void secondCallEventSequence() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_2, HTTP_1_1));
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        client.newCall(new Request.Builder().url(server.url("/")).build()).execute().close();
        listener.removeUpToEvent(RecordingEventListener.CallEnd.class);
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        response.close();
        List<String> expectedEvents = Arrays.asList("CallStart", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void successfulEmptyH2CallEventSequence() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_2, HTTP_1_1));
        server.enqueue(new MockResponse());
        assertSuccessfulEventOrder(matchesProtocol(HTTP_2));
        assertBytesReadWritten(listener, CoreMatchers.any(Long.class), null, greaterThan(0L), CoreMatchers.equalTo(0L));
    }

    @Test
    public void successfulEmptyHttpsCallEventSequence() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_1_1));
        server.enqueue(new MockResponse().setBody("abc"));
        assertSuccessfulEventOrder(EventListenerTest.anyResponse);
        assertBytesReadWritten(listener, CoreMatchers.any(Long.class), null, greaterThan(0L), CoreMatchers.equalTo(3L));
    }

    @Test
    public void successfulChunkedHttpsCallEventSequence() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_1_1));
        server.enqueue(new MockResponse().setBodyDelay(100, TimeUnit.MILLISECONDS).setChunkedBody("Hello!", 2));
        assertSuccessfulEventOrder(EventListenerTest.anyResponse);
        assertBytesReadWritten(listener, CoreMatchers.any(Long.class), null, greaterThan(0L), CoreMatchers.equalTo(6L));
    }

    @Test
    public void successfulChunkedH2CallEventSequence() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_2, HTTP_1_1));
        server.enqueue(new MockResponse().setBodyDelay(100, TimeUnit.MILLISECONDS).setChunkedBody("Hello!", 2));
        assertSuccessfulEventOrder(matchesProtocol(HTTP_2));
        assertBytesReadWritten(listener, CoreMatchers.any(Long.class), null, CoreMatchers.equalTo(0L), greaterThan(6L));
    }

    @Test
    public void successfulDnsLookup() throws IOException {
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        RecordingEventListener.DnsStart dnsStart = listener.removeUpToEvent(RecordingEventListener.DnsStart.class);
        Assert.assertSame(call, dnsStart.call);
        Assert.assertEquals(server.getHostName(), dnsStart.domainName);
        RecordingEventListener.DnsEnd dnsEnd = listener.removeUpToEvent(RecordingEventListener.DnsEnd.class);
        Assert.assertSame(call, dnsEnd.call);
        Assert.assertEquals(server.getHostName(), dnsEnd.domainName);
        Assert.assertEquals(1, dnsEnd.inetAddressList.size());
    }

    @Test
    public void noDnsLookupOnPooledConnection() throws IOException {
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        // Seed the pool.
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals(200, response1.code());
        response1.body().close();
        listener.clearAllEvents();
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals(200, response2.code());
        response2.body().close();
        List<String> recordedEvents = listener.recordedEventTypes();
        Assert.assertFalse(recordedEvents.contains("DnsStart"));
        Assert.assertFalse(recordedEvents.contains("DnsEnd"));
    }

    @Test
    public void multipleDnsLookupsForSingleCall() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", ("http://www.fakeurl:" + (server.getPort()))));
        server.enqueue(new MockResponse());
        FakeDns dns = new FakeDns();
        dns.set("fakeurl", client.dns().lookup(server.getHostName()));
        dns.set("www.fakeurl", client.dns().lookup(server.getHostName()));
        client = client.newBuilder().dns(dns).build();
        Call call = client.newCall(new Request.Builder().url(("http://fakeurl:" + (server.getPort()))).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        listener.removeUpToEvent(RecordingEventListener.DnsStart.class);
        listener.removeUpToEvent(RecordingEventListener.DnsEnd.class);
        listener.removeUpToEvent(RecordingEventListener.DnsStart.class);
        listener.removeUpToEvent(RecordingEventListener.DnsEnd.class);
    }

    @Test
    public void failedDnsLookup() {
        client = client.newBuilder().dns(new FakeDns()).build();
        Call call = client.newCall(new Request.Builder().url("http://fakeurl/").build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        listener.removeUpToEvent(RecordingEventListener.DnsStart.class);
        RecordingEventListener.CallFailed callFailed = listener.removeUpToEvent(RecordingEventListener.CallFailed.class);
        Assert.assertSame(call, callFailed.call);
        Assert.assertTrue(((callFailed.ioe) instanceof UnknownHostException));
    }

    @Test
    public void emptyDnsLookup() {
        Dns emptyDns = ( hostname) -> Collections.emptyList();
        client = client.newBuilder().dns(emptyDns).build();
        Call call = client.newCall(new Request.Builder().url("http://fakeurl/").build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        listener.removeUpToEvent(RecordingEventListener.DnsStart.class);
        RecordingEventListener.CallFailed callFailed = listener.removeUpToEvent(RecordingEventListener.CallFailed.class);
        Assert.assertSame(call, callFailed.call);
        Assert.assertTrue(((callFailed.ioe) instanceof UnknownHostException));
    }

    @Test
    public void successfulConnect() throws IOException {
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        InetAddress address = client.dns().lookup(server.getHostName()).get(0);
        InetSocketAddress expectedAddress = new InetSocketAddress(address, server.getPort());
        RecordingEventListener.ConnectStart connectStart = listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        Assert.assertSame(call, connectStart.call);
        Assert.assertEquals(expectedAddress, connectStart.inetSocketAddress);
        Assert.assertEquals(Proxy.NO_PROXY, connectStart.proxy);
        RecordingEventListener.ConnectEnd connectEnd = listener.removeUpToEvent(RecordingEventListener.ConnectEnd.class);
        Assert.assertSame(call, connectEnd.call);
        Assert.assertEquals(expectedAddress, connectEnd.inetSocketAddress);
        Assert.assertEquals(HTTP_1_1, connectEnd.protocol);
    }

    @Test
    public void failedConnect() throws UnknownHostException {
        enableTlsWithTunnel(false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        InetAddress address = client.dns().lookup(server.getHostName()).get(0);
        InetSocketAddress expectedAddress = new InetSocketAddress(address, server.getPort());
        RecordingEventListener.ConnectStart connectStart = listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        Assert.assertSame(call, connectStart.call);
        Assert.assertEquals(expectedAddress, connectStart.inetSocketAddress);
        Assert.assertEquals(Proxy.NO_PROXY, connectStart.proxy);
        RecordingEventListener.ConnectFailed connectFailed = listener.removeUpToEvent(RecordingEventListener.ConnectFailed.class);
        Assert.assertSame(call, connectFailed.call);
        Assert.assertEquals(expectedAddress, connectFailed.inetSocketAddress);
        Assert.assertNull(connectFailed.protocol);
        Assert.assertNotNull(connectFailed.ioe);
    }

    @Test
    public void multipleConnectsForSingleCall() throws IOException {
        enableTlsWithTunnel(false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse());
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectFailed.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectEnd.class);
    }

    @Test
    public void successfulHttpProxyConnect() throws IOException {
        server.enqueue(new MockResponse());
        client = client.newBuilder().proxy(server.toProxyAddress()).build();
        Call call = client.newCall(new Request.Builder().url("http://www.fakeurl").build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        InetAddress address = client.dns().lookup(server.getHostName()).get(0);
        InetSocketAddress expectedAddress = new InetSocketAddress(address, server.getPort());
        RecordingEventListener.ConnectStart connectStart = listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        Assert.assertSame(call, connectStart.call);
        Assert.assertEquals(expectedAddress, connectStart.inetSocketAddress);
        Assert.assertEquals(server.toProxyAddress(), connectStart.proxy);
        RecordingEventListener.ConnectEnd connectEnd = listener.removeUpToEvent(RecordingEventListener.ConnectEnd.class);
        Assert.assertSame(call, connectEnd.call);
        Assert.assertEquals(expectedAddress, connectEnd.inetSocketAddress);
        Assert.assertEquals(HTTP_1_1, connectEnd.protocol);
    }

    @Test
    public void successfulSocksProxyConnect() throws Exception {
        server.enqueue(new MockResponse());
        socksProxy = new SocksProxy();
        socksProxy.play();
        Proxy proxy = socksProxy.proxy();
        client = client.newBuilder().proxy(proxy).build();
        Call call = client.newCall(new Request.Builder().url(((("http://" + (SocksProxy.HOSTNAME_THAT_ONLY_THE_PROXY_KNOWS)) + ":") + (server.getPort()))).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        InetSocketAddress expectedAddress = InetSocketAddress.createUnresolved(SocksProxy.HOSTNAME_THAT_ONLY_THE_PROXY_KNOWS, server.getPort());
        RecordingEventListener.ConnectStart connectStart = listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        Assert.assertSame(call, connectStart.call);
        Assert.assertEquals(expectedAddress, connectStart.inetSocketAddress);
        Assert.assertEquals(proxy, connectStart.proxy);
        RecordingEventListener.ConnectEnd connectEnd = listener.removeUpToEvent(RecordingEventListener.ConnectEnd.class);
        Assert.assertSame(call, connectEnd.call);
        Assert.assertEquals(expectedAddress, connectEnd.inetSocketAddress);
        Assert.assertEquals(HTTP_1_1, connectEnd.protocol);
    }

    @Test
    public void authenticatingTunnelProxyConnect() throws IOException {
        enableTlsWithTunnel(true);
        server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\"").addHeader("Connection: close"));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END));
        server.enqueue(new MockResponse());
        client = client.newBuilder().proxy(server.toProxyAddress()).proxyAuthenticator(new RecordingOkAuthenticator("password", "Basic")).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        RecordingEventListener.ConnectEnd connectEnd = listener.removeUpToEvent(RecordingEventListener.ConnectEnd.class);
        Assert.assertNull(connectEnd.protocol);
        listener.removeUpToEvent(RecordingEventListener.ConnectStart.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectEnd.class);
    }

    @Test
    public void successfulSecureConnect() throws IOException {
        enableTlsWithTunnel(false);
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        RecordingEventListener.SecureConnectStart secureStart = listener.removeUpToEvent(RecordingEventListener.SecureConnectStart.class);
        Assert.assertSame(call, secureStart.call);
        RecordingEventListener.SecureConnectEnd secureEnd = listener.removeUpToEvent(RecordingEventListener.SecureConnectEnd.class);
        Assert.assertSame(call, secureEnd.call);
        Assert.assertNotNull(secureEnd.handshake);
    }

    @Test
    public void failedSecureConnect() {
        enableTlsWithTunnel(false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        RecordingEventListener.SecureConnectStart secureStart = listener.removeUpToEvent(RecordingEventListener.SecureConnectStart.class);
        Assert.assertSame(call, secureStart.call);
        RecordingEventListener.CallFailed callFailed = listener.removeUpToEvent(RecordingEventListener.CallFailed.class);
        Assert.assertSame(call, callFailed.call);
        Assert.assertNotNull(callFailed.ioe);
    }

    @Test
    public void secureConnectWithTunnel() throws IOException {
        enableTlsWithTunnel(true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END));
        server.enqueue(new MockResponse());
        client = client.newBuilder().proxy(server.toProxyAddress()).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        RecordingEventListener.SecureConnectStart secureStart = listener.removeUpToEvent(RecordingEventListener.SecureConnectStart.class);
        Assert.assertSame(call, secureStart.call);
        RecordingEventListener.SecureConnectEnd secureEnd = listener.removeUpToEvent(RecordingEventListener.SecureConnectEnd.class);
        Assert.assertSame(call, secureEnd.call);
        Assert.assertNotNull(secureEnd.handshake);
    }

    @Test
    public void multipleSecureConnectsForSingleCall() throws IOException {
        enableTlsWithTunnel(false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse());
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        listener.removeUpToEvent(RecordingEventListener.SecureConnectStart.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectFailed.class);
        listener.removeUpToEvent(RecordingEventListener.SecureConnectStart.class);
        listener.removeUpToEvent(RecordingEventListener.SecureConnectEnd.class);
    }

    @Test
    public void noSecureConnectsOnPooledConnection() throws IOException {
        enableTlsWithTunnel(false);
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        // Seed the pool.
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals(200, response1.code());
        response1.body().close();
        listener.clearAllEvents();
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals(200, response2.code());
        response2.body().close();
        List<String> recordedEvents = listener.recordedEventTypes();
        Assert.assertFalse(recordedEvents.contains("SecureConnectStart"));
        Assert.assertFalse(recordedEvents.contains("SecureConnectEnd"));
    }

    @Test
    public void successfulConnectionFound() throws IOException {
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        response.body().close();
        RecordingEventListener.ConnectionAcquired connectionAcquired = listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        Assert.assertSame(call, connectionAcquired.call);
        Assert.assertNotNull(connectionAcquired.connection);
    }

    @Test
    public void noConnectionFoundOnFollowUp() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location", "/foo"));
        server.enqueue(new MockResponse().setBody("ABC"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("ABC", response.body().string());
        listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        List<String> remainingEvents = listener.recordedEventTypes();
        Assert.assertFalse(remainingEvents.contains("ConnectionAcquired"));
    }

    @Test
    public void pooledConnectionFound() throws IOException {
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        // Seed the pool.
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals(200, response1.code());
        response1.body().close();
        RecordingEventListener.ConnectionAcquired connectionAcquired1 = listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        listener.clearAllEvents();
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals(200, response2.code());
        response2.body().close();
        RecordingEventListener.ConnectionAcquired connectionAcquired2 = listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        Assert.assertSame(connectionAcquired1.connection, connectionAcquired2.connection);
    }

    @Test
    public void multipleConnectionsFoundForSingleCall() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location", "/foo").addHeader("Connection", "Close"));
        server.enqueue(new MockResponse().setBody("ABC"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("ABC", response.body().string());
        listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
    }

    @Test
    public void responseBodyFailHttp1OverHttps() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_1_1));
        responseBodyFail(HTTP_1_1);
    }

    @Test
    public void responseBodyFailHttp2OverHttps() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_2, HTTP_1_1));
        responseBodyFail(HTTP_2);
    }

    @Test
    public void responseBodyFailHttp() throws IOException {
        responseBodyFail(HTTP_1_1);
    }

    @Test
    public void emptyResponseBody() throws IOException {
        server.enqueue(new MockResponse().setBody("").setBodyDelay(1, TimeUnit.SECONDS).setSocketPolicy(DISCONNECT_DURING_RESPONSE_BODY));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        response.body().close();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void emptyResponseBodyConnectionClose() throws IOException {
        server.enqueue(new MockResponse().addHeader("Connection", "close").setBody(""));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        response.body().close();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void responseBodyClosedClosedWithoutReadingAllData() throws IOException {
        server.enqueue(new MockResponse().setBody("abc").setBodyDelay(1, TimeUnit.SECONDS).setSocketPolicy(DISCONNECT_DURING_RESPONSE_BODY));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        response.body().close();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void requestBodyFailHttp1OverHttps() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_1_1));
        requestBodyFail();
    }

    @Test
    public void requestBodyFailHttp2OverHttps() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_2, HTTP_1_1));
        requestBodyFail();
    }

    @Test
    public void requestBodyFailHttp() throws IOException {
        requestBodyFail();
    }

    @Test
    public void requestBodyMultipleFailuresReportedOnlyOnce() {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain");
            }

            @Override
            public long contentLength() {
                return (1024 * 1024) * 256;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                int failureCount = 0;
                for (int i = 0; i < 1024; i++) {
                    try {
                        sink.write(new byte[1024 * 256]);
                        sink.flush();
                    } catch (IOException e) {
                        failureCount++;
                        if (failureCount == 3)
                            throw e;

                    }
                }
            }
        };
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_DURING_REQUEST_BODY));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).post(requestBody).build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "RequestBodyStart", "RequestFailed", "ConnectionReleased", "CallFailed");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void requestBodySuccessHttp1OverHttps() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_1_1));
        requestBodySuccess(RequestBody.create(MediaType.get("text/plain"), "Hello"), CoreMatchers.equalTo(5L), CoreMatchers.equalTo(19L));
    }

    @Test
    public void requestBodySuccessHttp2OverHttps() throws IOException {
        enableTlsWithTunnel(false);
        server.setProtocols(Arrays.asList(HTTP_2, HTTP_1_1));
        requestBodySuccess(RequestBody.create(MediaType.get("text/plain"), "Hello"), CoreMatchers.equalTo(5L), CoreMatchers.equalTo(19L));
    }

    @Test
    public void requestBodySuccessHttp() throws IOException {
        requestBodySuccess(RequestBody.create(MediaType.get("text/plain"), "Hello"), CoreMatchers.equalTo(5L), CoreMatchers.equalTo(19L));
    }

    @Test
    public void requestBodySuccessStreaming() throws IOException {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(new byte[8192]);
                sink.flush();
            }
        };
        requestBodySuccess(requestBody, CoreMatchers.equalTo(8192L), CoreMatchers.equalTo(19L));
    }

    @Test
    public void requestBodySuccessEmpty() throws IOException {
        requestBodySuccess(RequestBody.create(MediaType.get("text/plain"), ""), CoreMatchers.equalTo(0L), CoreMatchers.equalTo(19L));
    }

    @Test
    public void successfulCallEventSequenceWithListener() throws IOException {
        server.enqueue(new MockResponse().setBody("abc"));
        client = client.newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(BODY)).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("abc", response.body().string());
        response.body().close();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void redirectUsingSameConnectionEventSequence() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo"));
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        call.execute();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void redirectUsingNewConnectionEventSequence() throws IOException {
        MockWebServer otherServer = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (otherServer.url("/foo")))));
        otherServer.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    @Test
    public void applicationInterceptorProceedsMultipleTimes() throws Exception {
        server.enqueue(new MockResponse().setBody("a"));
        server.enqueue(new MockResponse().setBody("b"));
        client = client.newBuilder().addInterceptor(( chain) -> {
            try (Response a = chain.proceed(chain.request())) {
                assertEquals("a", a.body().string());
            }
            return chain.proceed(chain.request());
        }).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("b", response.body().string());
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void applicationInterceptorShortCircuit() throws Exception {
        client = client.newBuilder().addInterceptor(( chain) -> new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200).message("OK").body(ResponseBody.create(null, "a")).build()).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("a", response.body().string());
        List<String> expectedEvents = Arrays.asList("CallStart", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }

    /**
     * Response headers start, then the entire request body, then response headers end.
     */
    @Test
    public void expectContinueStartsResponseHeadersEarly() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(EXPECT_CONTINUE));
        Request request = new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "abc")).build();
        Call call = client.newCall(request);
        call.execute();
        List<String> expectedEvents = Arrays.asList("CallStart", "DnsStart", "DnsEnd", "ConnectStart", "ConnectEnd", "ConnectionAcquired", "RequestHeadersStart", "RequestHeadersEnd", "ResponseHeadersStart", "RequestBodyStart", "RequestBodyEnd", "ResponseHeadersEnd", "ResponseBodyStart", "ResponseBodyEnd", "ConnectionReleased", "CallEnd");
        Assert.assertEquals(expectedEvents, listener.recordedEventTypes());
    }
}

