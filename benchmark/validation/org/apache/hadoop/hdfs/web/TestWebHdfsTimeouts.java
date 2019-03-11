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
package org.apache.hadoop.hdfs.web;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test suite checks that WebHdfsFileSystem sets connection timeouts and
 * read timeouts on its sockets, thus preventing threads from hanging
 * indefinitely on an undefined/infinite timeout.  The tests work by starting a
 * bogus server on the namenode HTTP port, which is rigged to not accept new
 * connections or to accept connections but not send responses.
 */
@RunWith(Parameterized.class)
public class TestWebHdfsTimeouts {
    private static final Logger LOG = LoggerFactory.getLogger(TestWebHdfsTimeouts.class);

    private static final int CLIENTS_TO_CONSUME_BACKLOG = 129;

    private static final int CONNECTION_BACKLOG = 1;

    private static final int SHORT_SOCKET_TIMEOUT = 200;

    private static final int TEST_TIMEOUT = 100000;

    private List<SocketChannel> clients;

    private WebHdfsFileSystem fs;

    private InetSocketAddress nnHttpAddress;

    private ServerSocket serverSocket;

    private Thread serverThread;

    private final URLConnectionFactory connectionFactory = new URLConnectionFactory(new ConnectionConfigurator() {
        @Override
        public HttpURLConnection configure(HttpURLConnection conn) throws IOException {
            conn.setReadTimeout(TestWebHdfsTimeouts.SHORT_SOCKET_TIMEOUT);
            conn.setConnectTimeout(TestWebHdfsTimeouts.SHORT_SOCKET_TIMEOUT);
            return conn;
        }
    });

    public enum TimeoutSource {

        ConnectionFactory,
        Configuration;}

    @Parameterized.Parameter
    public TestWebHdfsTimeouts.TimeoutSource timeoutSource;

    /**
     * Expect connect timeout, because the connection backlog is consumed.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testConnectTimeout() throws Exception {
        consumeConnectionBacklog();
        try {
            fs.listFiles(new Path("/"), false);
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": connect timed out"), e);
        }
    }

    /**
     * Expect read timeout, because the bogus server never sends a reply.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testReadTimeout() throws Exception {
        try {
            fs.listFiles(new Path("/"), false);
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": Read timed out"), e);
        }
    }

    /**
     * Expect connect timeout on a URL that requires auth, because the connection
     * backlog is consumed.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testAuthUrlConnectTimeout() throws Exception {
        consumeConnectionBacklog();
        try {
            fs.getDelegationToken("renewer");
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": connect timed out"), e);
        }
    }

    /**
     * Expect read timeout on a URL that requires auth, because the bogus server
     * never sends a reply.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testAuthUrlReadTimeout() throws Exception {
        try {
            fs.getDelegationToken("renewer");
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": Read timed out"), e);
        }
    }

    /**
     * After a redirect, expect connect timeout accessing the redirect location,
     * because the connection backlog is consumed.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testRedirectConnectTimeout() throws Exception {
        startSingleTemporaryRedirectResponseThread(true);
        try {
            fs.getFileChecksum(new Path("/file"));
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": connect timed out"), e);
        }
    }

    /**
     * After a redirect, expect read timeout accessing the redirect location,
     * because the bogus server never sends a reply.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testRedirectReadTimeout() throws Exception {
        startSingleTemporaryRedirectResponseThread(false);
        try {
            fs.getFileChecksum(new Path("/file"));
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": Read timed out"), e);
        }
    }

    /**
     * On the second step of two-step write, expect connect timeout accessing the
     * redirect location, because the connection backlog is consumed.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testTwoStepWriteConnectTimeout() throws Exception {
        startSingleTemporaryRedirectResponseThread(true);
        OutputStream os = null;
        try {
            os = fs.create(new Path("/file"));
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains(((fs.getUri().getAuthority()) + ": connect timed out"), e);
        } finally {
            IOUtils.cleanupWithLogger(TestWebHdfsTimeouts.LOG, os);
        }
    }

    /**
     * On the second step of two-step write, expect read timeout accessing the
     * redirect location, because the bogus server never sends a reply.
     */
    @Test(timeout = TestWebHdfsTimeouts.TEST_TIMEOUT)
    public void testTwoStepWriteReadTimeout() throws Exception {
        startSingleTemporaryRedirectResponseThread(false);
        OutputStream os = null;
        try {
            os = fs.create(new Path("/file"));
            os.close();// must close stream to force reading the HTTP response

            os = null;
            Assert.fail("expected timeout");
        } catch (SocketTimeoutException e) {
            GenericTestUtils.assertExceptionContains("Read timed out", e);
        } finally {
            IOUtils.cleanupWithLogger(TestWebHdfsTimeouts.LOG, os);
        }
    }
}

