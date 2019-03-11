/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.source;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSyslogTcpSource {
    private static final Logger logger = LoggerFactory.getLogger(TestSyslogTcpSource.class);

    private SyslogTcpSource source;

    private Channel channel;

    private static final int TEST_SYSLOG_PORT = 0;

    private final DateTime time = new DateTime();

    private final String stamp1 = time.toString();

    private final String host1 = "localhost.localdomain";

    private final String data1 = "test syslog data";

    private final String bodyWithHostname = ((host1) + " ") + (data1);

    private final String bodyWithTimestamp = ((stamp1) + " ") + (data1);

    private final String bodyWithTandH = ((((("<10>" + (stamp1)) + " ") + (host1)) + " ") + (data1)) + "\n";

    @Test
    public void testKeepFields() throws IOException {
        runKeepFieldsTest("all");
        // Backwards compatibility
        runKeepFieldsTest("true");
    }

    @Test
    public void testRemoveFields() throws IOException {
        runKeepFieldsTest("none");
        // Backwards compatibility
        runKeepFieldsTest("false");
    }

    @Test
    public void testKeepHostname() throws IOException {
        runKeepFieldsTest("hostname");
    }

    @Test
    public void testKeepTimestamp() throws IOException {
        runKeepFieldsTest("timestamp");
    }

    @Test
    public void testSourceCounter() throws IOException {
        runKeepFieldsTest("all");
        Assert.assertEquals(10, source.getSourceCounter().getEventAcceptedCount());
        Assert.assertEquals(10, source.getSourceCounter().getEventReceivedCount());
    }

    @Test
    public void testSourceCounterChannelFail() throws Exception {
        init("true");
        errorCounterCommon(new ChannelException("dummy"));
        for (int i = 0; (i < 10) && ((source.getSourceCounter().getChannelWriteFail()) == 0); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(1, source.getSourceCounter().getChannelWriteFail());
    }

    @Test
    public void testSourceCounterEventFail() throws Exception {
        init("true");
        errorCounterCommon(new RuntimeException("dummy"));
        for (int i = 0; (i < 10) && ((source.getSourceCounter().getEventReadFail()) == 0); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(1, source.getSourceCounter().getEventReadFail());
    }

    @Test
    public void testSSLMessages() throws Exception {
        initSsl();
        source.start();
        InetSocketAddress address = source.getBoundAddress();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String s) {
                // nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String s) {
                // nothing
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, null);
        SocketFactory socketFactory = sslContext.getSocketFactory();
        Socket socket = socketFactory.createSocket();
        socket.connect(address);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bodyWithTandH.getBytes());
        socket.close();
        // Thread.sleep(100);
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        Event event = channel.take();
        Assert.assertEquals(new String(event.getBody()), data1);
        transaction.commit();
        transaction.close();
    }

    @Test
    public void testClientHeaders() throws IOException {
        String testClientIPHeader = "testClientIPHeader";
        String testClientHostnameHeader = "testClientHostnameHeader";
        Context context = new Context();
        context.put("clientIPHeader", testClientIPHeader);
        context.put("clientHostnameHeader", testClientHostnameHeader);
        init("none", context);
        source.start();
        // Write some message to the syslog port
        InetSocketAddress addr = source.getBoundAddress();
        Socket syslogSocket = new Socket(addr.getAddress(), addr.getPort());
        syslogSocket.getOutputStream().write(bodyWithTandH.getBytes());
        Transaction txn = channel.getTransaction();
        txn.begin();
        Event e = channel.take();
        try {
            txn.commit();
        } catch (Throwable t) {
            txn.rollback();
        } finally {
            txn.close();
        }
        source.stop();
        Map<String, String> headers = e.getHeaders();
        TestSyslogTcpSource.checkHeader(headers, testClientIPHeader, InetAddress.getLoopbackAddress().getHostAddress());
        TestSyslogTcpSource.checkHeader(headers, testClientHostnameHeader, InetAddress.getLoopbackAddress().getHostName());
    }
}

