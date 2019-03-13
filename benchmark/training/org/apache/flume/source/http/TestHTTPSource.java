/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.http;


import HttpServletResponse.SC_BAD_REQUEST;
import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import junit.framework.Assert;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;


/**
 *
 */
public class TestHTTPSource {
    private static HTTPSource httpSource;

    private static HTTPSource httpsSource;

    private static HTTPSource httpsGlobalKeystoreSource;

    private static Channel httpChannel;

    private static Channel httpsChannel;

    private static Channel httpsGlobalKeystoreChannel;

    private static int httpPort;

    private static int httpsPort;

    private static int httpsGlobalKeystorePort;

    private HttpClient httpClient;

    private HttpPost postRequest;

    @Test
    public void testSimple() throws IOException, InterruptedException {
        StringEntity input = new StringEntity(("[{\"headers\":{\"a\": \"b\"},\"body\": \"random_body\"}," + "{\"headers\":{\"e\": \"f\"},\"body\": \"random_body2\"}]"));
        // if we do not set the content type to JSON, the client will use
        // ISO-8859-1 as the charset. JSON standard does not support this.
        input.setContentType("application/json");
        postRequest.setEntity(input);
        HttpResponse response = httpClient.execute(postRequest);
        Assert.assertEquals(SC_OK, response.getStatusLine().getStatusCode());
        Transaction tx = TestHTTPSource.httpChannel.getTransaction();
        tx.begin();
        Event e = TestHTTPSource.httpChannel.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("b", e.getHeaders().get("a"));
        Assert.assertEquals("random_body", new String(e.getBody(), "UTF-8"));
        e = TestHTTPSource.httpChannel.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("f", e.getHeaders().get("e"));
        Assert.assertEquals("random_body2", new String(e.getBody(), "UTF-8"));
        tx.commit();
        tx.close();
    }

    @Test
    public void testTrace() throws Exception {
        doTestForbidden(new HttpTrace(("http://0.0.0.0:" + (TestHTTPSource.httpPort))));
    }

    @Test
    public void testOptions() throws Exception {
        doTestForbidden(new HttpOptions(("http://0.0.0.0:" + (TestHTTPSource.httpPort))));
    }

    @Test
    public void testSimpleUTF16() throws IOException, InterruptedException {
        StringEntity input = new StringEntity(("[{\"headers\":{\"a\": \"b\"},\"body\": \"random_body\"}," + "{\"headers\":{\"e\": \"f\"},\"body\": \"random_body2\"}]"), "UTF-16");
        input.setContentType("application/json; charset=utf-16");
        postRequest.setEntity(input);
        HttpResponse response = httpClient.execute(postRequest);
        Assert.assertEquals(SC_OK, response.getStatusLine().getStatusCode());
        Transaction tx = TestHTTPSource.httpChannel.getTransaction();
        tx.begin();
        Event e = TestHTTPSource.httpChannel.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("b", e.getHeaders().get("a"));
        Assert.assertEquals("random_body", new String(e.getBody(), "UTF-16"));
        e = TestHTTPSource.httpChannel.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("f", e.getHeaders().get("e"));
        Assert.assertEquals("random_body2", new String(e.getBody(), "UTF-16"));
        tx.commit();
        tx.close();
    }

    @Test
    public void testInvalid() throws Exception {
        StringEntity input = new StringEntity(("[{\"a\": \"b\",[\"d\":\"e\"],\"body\": \"random_body\"}," + "{\"e\": \"f\",\"body\": \"random_body2\"}]"));
        input.setContentType("application/json");
        postRequest.setEntity(input);
        HttpResponse response = httpClient.execute(postRequest);
        Assert.assertEquals(SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(TestHTTPSource.httpSource, "sourceCounter")));
        Assert.assertEquals(1, sc.getEventReadFail());
    }

    @Test
    public void testBigBatchDeserializarionUTF8() throws Exception {
        testBatchWithVariousEncoding("UTF-8");
    }

    @Test
    public void testBigBatchDeserializarionUTF16() throws Exception {
        testBatchWithVariousEncoding("UTF-16");
    }

    @Test
    public void testBigBatchDeserializarionUTF32() throws Exception {
        testBatchWithVariousEncoding("UTF-32");
    }

    @Test
    public void testCounterGenericFail() throws Exception {
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new RuntimeException("dummy")).when(cp).processEventBatch(ArgumentMatchers.anyListOf(Event.class));
        ChannelProcessor oldCp = TestHTTPSource.httpSource.getChannelProcessor();
        TestHTTPSource.httpSource.setChannelProcessor(cp);
        testBatchWithVariousEncoding("UTF-8");
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(TestHTTPSource.httpSource, "sourceCounter")));
        Assert.assertEquals(1, sc.getGenericProcessingFail());
        TestHTTPSource.httpSource.setChannelProcessor(oldCp);
    }

    @Test
    public void testSingleEvent() throws Exception {
        StringEntity input = new StringEntity(("[{\"headers\" : {\"a\": \"b\"},\"body\":" + " \"random_body\"}]"));
        input.setContentType("application/json");
        postRequest.setEntity(input);
        httpClient.execute(postRequest);
        Transaction tx = TestHTTPSource.httpChannel.getTransaction();
        tx.begin();
        Event e = TestHTTPSource.httpChannel.take();
        Assert.assertNotNull(e);
        Assert.assertEquals("b", e.getHeaders().get("a"));
        Assert.assertEquals("random_body", new String(e.getBody(), "UTF-8"));
        tx.commit();
        tx.close();
    }

    /**
     * First test that the unconfigured behaviour is as-expected, then add configurations
     * to a new channel and observe the difference.
     * For some of the properties, the most convenient way to test is using the MBean interface
     * We test all of HttpConfiguration, ServerConnector, QueuedThreadPool and SslContextFactory
     * sub-configurations (but not all properties)
     */
    @Test
    public void testConfigurables() throws Exception {
        StringEntity input = new StringEntity(("[{\"headers\" : {\"a\": \"b\"},\"body\":" + " \"random_body\"}]"));
        input.setContentType("application/json");
        postRequest.setEntity(input);
        HttpResponse resp = httpClient.execute(postRequest);
        // Testing default behaviour (to not provided X-Powered-By, but to provide Server headers)
        Assert.assertTrue(((resp.getHeaders("X-Powered-By").length) == 0));
        Assert.assertTrue(((resp.getHeaders("Server").length) == 1));
        Transaction tx = TestHTTPSource.httpChannel.getTransaction();
        tx.begin();
        Event e = TestHTTPSource.httpChannel.take();
        Assert.assertNotNull(e);
        tx.commit();
        tx.close();
        Assert.assertTrue(((findMBeans("org.eclipse.jetty.util.thread:type=queuedthreadpool,*", "maxThreads", 123).size()) == 0));
        Assert.assertTrue(((findMBeans("org.eclipse.jetty.server:type=serverconnector,*", "acceptQueueSize", 22).size()) == 0));
        int newPort = TestHTTPSource.findFreePort();
        Context configuredSourceContext = TestHTTPSource.getDefaultNonSecureContext(newPort);
        configuredSourceContext.put("HttpConfiguration.sendServerVersion", "false");
        configuredSourceContext.put("HttpConfiguration.sendXPoweredBy", "true");
        configuredSourceContext.put("ServerConnector.acceptQueueSize", "22");
        configuredSourceContext.put("QueuedThreadPool.maxThreads", "123");
        HTTPSource newSource = new HTTPSource();
        Channel newChannel = new MemoryChannel();
        TestHTTPSource.configureSourceAndChannel(newSource, newChannel, configuredSourceContext);
        newChannel.start();
        newSource.start();
        HttpPost newPostRequest = new HttpPost(("http://0.0.0.0:" + newPort));
        resp = httpClient.execute(newPostRequest);
        Assert.assertTrue(((resp.getHeaders("X-Powered-By").length) > 0));
        Assert.assertTrue(((resp.getHeaders("Server").length) == 0));
        Assert.assertTrue(((findMBeans("org.eclipse.jetty.util.thread:type=queuedthreadpool,*", "maxThreads", 123).size()) == 1));
        Assert.assertTrue(((findMBeans("org.eclipse.jetty.server:type=serverconnector,*", "acceptQueueSize", 22).size()) == 1));
        newSource.stop();
        newChannel.stop();
        // Configure SslContextFactory with junk protocols (expect failure)
        newPort = TestHTTPSource.findFreePort();
        configuredSourceContext = TestHTTPSource.getDefaultSecureContext(newPort);
        configuredSourceContext.put("SslContextFactory.IncludeProtocols", "abc def");
        newSource = new HTTPSource();
        newChannel = new MemoryChannel();
        TestHTTPSource.configureSourceAndChannel(newSource, newChannel, configuredSourceContext);
        newChannel.start();
        newSource.start();
        newPostRequest = new HttpPost(("http://0.0.0.0:" + newPort));
        try {
            doTestHttps(null, newPort, TestHTTPSource.httpsChannel);
            // We are testing that this fails because we've deliberately configured the wrong protocols
            Assert.assertTrue(false);
        } catch (AssertionError ex) {
            // no-op
        }
        newSource.stop();
        newChannel.stop();
    }

    @Test
    public void testFullChannel() throws Exception {
        HttpResponse response = putWithEncoding("UTF-8", 150).response;
        Assert.assertEquals(SC_SERVICE_UNAVAILABLE, response.getStatusLine().getStatusCode());
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(TestHTTPSource.httpSource, "sourceCounter")));
        Assert.assertEquals(1, sc.getChannelWriteFail());
    }

    @Test
    public void testFail() throws Exception {
        HTTPSourceHandler handler = field("handler").ofType(HTTPSourceHandler.class).in(TestHTTPSource.httpSource).get();
        // Cause an exception in the source - this is equivalent to any exception
        // thrown by the handler since the handler is called inside a try-catch
        field("handler").ofType(HTTPSourceHandler.class).in(TestHTTPSource.httpSource).set(null);
        HttpResponse response = putWithEncoding("UTF-8", 1).response;
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        // Set the original handler back so tests don't fail after this runs.
        field("handler").ofType(HTTPSourceHandler.class).in(TestHTTPSource.httpSource).set(handler);
    }

    @Test
    public void testMBeans() throws Exception {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("org.eclipse.jetty.*:*");
        Set<ObjectInstance> queryMBeans = mbeanServer.queryMBeans(objectName, null);
        Assert.assertTrue(((queryMBeans.size()) > 0));
    }

    @Test
    public void testHandlerThrowingException() throws Exception {
        // This will cause the handler to throw an
        // UnsupportedCharsetException.
        HttpResponse response = putWithEncoding("ISO-8859-1", 150).response;
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testHttps() throws Exception {
        doTestHttps(null, TestHTTPSource.httpsPort, TestHTTPSource.httpsChannel);
    }

    @Test(expected = SSLHandshakeException.class)
    public void testHttpsSSLv3() throws Exception {
        doTestHttps("SSLv3", TestHTTPSource.httpsPort, TestHTTPSource.httpsChannel);
    }

    @Test
    public void testHttpsGlobalKeystore() throws Exception {
        doTestHttps(null, TestHTTPSource.httpsGlobalKeystorePort, TestHTTPSource.httpsGlobalKeystoreChannel);
    }

    @Test
    public void testHttpsSourceNonHttpsClient() throws Exception {
        Type listType = new TypeToken<List<JSONEvent>>() {}.getType();
        List<JSONEvent> events = new ArrayList<JSONEvent>();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            Map<String, String> input = Maps.newHashMap();
            for (int j = 0; j < 10; j++) {
                input.put(((String.valueOf(i)) + (String.valueOf(j))), String.valueOf(i));
            }
            input.put("MsgNum", String.valueOf(i));
            JSONEvent e = new JSONEvent();
            e.setHeaders(input);
            e.setBody(String.valueOf(rand.nextGaussian()).getBytes("UTF-8"));
            events.add(e);
        }
        Gson gson = new Gson();
        String json = gson.toJson(events, listType);
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(("http://0.0.0.0:" + (TestHTTPSource.httpsPort)));
            httpURLConnection = ((HttpURLConnection) (url.openConnection()));
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.getOutputStream().write(json.getBytes());
            httpURLConnection.getResponseCode();
            Assert.fail("HTTP Client cannot connect to HTTPS source");
        } catch (Exception exception) {
            Assert.assertTrue("Exception expected", true);
        } finally {
            httpURLConnection.disconnect();
        }
    }

    private class ResultWrapper {
        public final HttpResponse response;

        public final List<JSONEvent> events;

        public ResultWrapper(HttpResponse resp, List<JSONEvent> events) {
            this.response = resp;
            this.events = events;
        }
    }

    private class DisabledProtocolsSocketFactory extends SSLSocketFactory {
        private final SSLSocketFactory socketFactory;

        private final String[] protocols;

        DisabledProtocolsSocketFactory(SSLSocketFactory factory, String protocol) {
            this.socketFactory = factory;
            protocols = new String[1];
            protocols[0] = protocol;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return socketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return socketFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
            SSLSocket sc = ((SSLSocket) (socketFactory.createSocket(socket, s, i, b)));
            sc.setEnabledProtocols(protocols);
            return sc;
        }

        @Override
        public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
            SSLSocket sc = ((SSLSocket) (socketFactory.createSocket(s, i)));
            sc.setEnabledProtocols(protocols);
            return sc;
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
            SSLSocket sc = ((SSLSocket) (socketFactory.createSocket(s, i, inetAddress, i2)));
            sc.setEnabledProtocols(protocols);
            return sc;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            SSLSocket sc = ((SSLSocket) (socketFactory.createSocket(inetAddress, i)));
            sc.setEnabledProtocols(protocols);
            return sc;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
            SSLSocket sc = ((SSLSocket) (socketFactory.createSocket(inetAddress, i, inetAddress2, i2)));
            sc.setEnabledProtocols(protocols);
            return sc;
        }
    }
}

