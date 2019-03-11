/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.remote.client.http;


import HttpServletResponse.SC_ACCEPTED;
import HttpServletResponse.SC_CREATED;
import HttpServletResponse.SC_OK;
import ResponseCode.CONFIRM_TRANSACTION;
import ResponseCode.CONTINUE_TRANSACTION;
import ResponseCode.PROPERTIES_OK;
import ResponseCode.TRANSACTION_FINISHED;
import TransferDirection.RECEIVE;
import TransferDirection.SEND;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.exception.HandshakeException;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.http.HttpHeaders;
import org.apache.nifi.remote.util.StandardDataPacket;
import org.apache.nifi.web.api.dto.ControllerDTO;
import org.apache.nifi.web.api.dto.PortDTO;
import org.apache.nifi.web.api.dto.remote.PeerDTO;
import org.apache.nifi.web.api.entity.ControllerEntity;
import org.apache.nifi.web.api.entity.PeersEntity;
import org.apache.nifi.web.api.entity.TransactionResultEntity;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


public class TestHttpClient {
    private static Logger logger = LoggerFactory.getLogger(TestHttpClient.class);

    private static Server server;

    private static ServerConnector httpConnector;

    private static ServerConnector sslConnector;

    private static CountDownLatch testCaseFinished;

    private static HttpProxyServer proxyServer;

    private static HttpProxyServer proxyServerWithAuth;

    private static Set<PortDTO> inputPorts;

    private static Set<PortDTO> outputPorts;

    private static Set<PeerDTO> peers;

    private static Set<PeerDTO> peersSecure;

    private static String serverChecksum;

    public static class SiteInfoServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final ControllerDTO controller = new ControllerDTO();
            if ((req.getLocalPort()) == (TestHttpClient.httpConnector.getLocalPort())) {
                controller.setRemoteSiteHttpListeningPort(TestHttpClient.httpConnector.getLocalPort());
                controller.setSiteToSiteSecure(false);
            } else {
                controller.setRemoteSiteHttpListeningPort(TestHttpClient.sslConnector.getLocalPort());
                controller.setSiteToSiteSecure(true);
            }
            controller.setId("remote-controller-id");
            controller.setInstanceId("remote-instance-id");
            controller.setName("Remote NiFi Flow");
            Assert.assertNotNull("Test case should set <inputPorts> depending on the test scenario.", TestHttpClient.inputPorts);
            controller.setInputPorts(TestHttpClient.inputPorts);
            controller.setInputPortCount(TestHttpClient.inputPorts.size());
            Assert.assertNotNull("Test case should set <outputPorts> depending on the test scenario.", TestHttpClient.outputPorts);
            controller.setOutputPorts(TestHttpClient.outputPorts);
            controller.setOutputPortCount(TestHttpClient.outputPorts.size());
            final ControllerEntity controllerEntity = new ControllerEntity();
            controllerEntity.setController(controller);
            TestHttpClient.respondWithJson(resp, controllerEntity);
        }
    }

    public static class WrongSiteInfoServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            // This response simulates when a Site-to-Site is given an URL which has wrong path.
            TestHttpClient.respondWithText(resp, "<p class=\"message-pane-content\">You may have mistyped...</p>", 200);
        }
    }

    public static class PeersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final PeersEntity peersEntity = new PeersEntity();
            if ((req.getLocalPort()) == (TestHttpClient.httpConnector.getLocalPort())) {
                Assert.assertNotNull("Test case should set <peers> depending on the test scenario.", TestHttpClient.peers);
                peersEntity.setPeers(TestHttpClient.peers);
            } else {
                Assert.assertNotNull("Test case should set <peersSecure> depending on the test scenario.", TestHttpClient.peersSecure);
                peersEntity.setPeers(TestHttpClient.peersSecure);
            }
            TestHttpClient.respondWithJson(resp, peersEntity);
        }
    }

    public static class PortTransactionsServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            TransactionResultEntity entity = new TransactionResultEntity();
            entity.setResponseCode(PROPERTIES_OK.getCode());
            entity.setMessage("A transaction is created.");
            resp.setHeader(HttpHeaders.LOCATION_URI_INTENT_NAME, HttpHeaders.LOCATION_URI_INTENT_VALUE);
            resp.setHeader(HttpHeaders.LOCATION_HEADER_NAME, ((req.getRequestURL()) + "/transaction-id"));
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.respondWithJson(resp, entity, SC_CREATED);
        }
    }

    public static class PortTransactionsAccessDeniedServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            TestHttpClient.respondWithText(resp, ("Unable to perform the desired action" + " due to insufficient permissions. Contact the system administrator."), 403);
        }
    }

    public static class InputPortTransactionServlet extends HttpServlet {
        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            final TransactionResultEntity entity = new TransactionResultEntity();
            entity.setResponseCode(CONTINUE_TRANSACTION.getCode());
            entity.setMessage("Extended TTL.");
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.respondWithJson(resp, entity, SC_OK);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            TransactionResultEntity entity = new TransactionResultEntity();
            entity.setResponseCode(TRANSACTION_FINISHED.getCode());
            entity.setMessage("The transaction is finished.");
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.respondWithJson(resp, entity, SC_OK);
        }
    }

    public static class OutputPortTransactionServlet extends HttpServlet {
        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            final TransactionResultEntity entity = new TransactionResultEntity();
            entity.setResponseCode(CONTINUE_TRANSACTION.getCode());
            entity.setMessage("Extended TTL.");
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.respondWithJson(resp, entity, SC_OK);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            TransactionResultEntity entity = new TransactionResultEntity();
            entity.setResponseCode(CONFIRM_TRANSACTION.getCode());
            entity.setMessage("The transaction is confirmed.");
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.respondWithJson(resp, entity, SC_OK);
        }
    }

    public static class FlowFilesServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            DataPacket dataPacket;
            while ((dataPacket = TestHttpClient.readIncomingPacket(req)) != null) {
                TestHttpClient.logger.info("received {}", dataPacket);
                TestHttpClient.consumeDataPacket(dataPacket);
            } 
            TestHttpClient.logger.info("finish receiving data packets.");
            Assert.assertNotNull("Test case should set <serverChecksum> depending on the test scenario.", TestHttpClient.serverChecksum);
            TestHttpClient.respondWithText(resp, TestHttpClient.serverChecksum, SC_ACCEPTED);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            resp.setStatus(SC_ACCEPTED);
            resp.setContentType("application/octet-stream");
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            final OutputStream outputStream = TestHttpClient.getOutputStream(req, resp);
            TestHttpClient.writeOutgoingPacket(outputStream);
            TestHttpClient.writeOutgoingPacket(outputStream);
            TestHttpClient.writeOutgoingPacket(outputStream);
            resp.flushBuffer();
        }
    }

    public static class FlowFilesTimeoutServlet extends TestHttpClient.FlowFilesServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            TestHttpClient.sleepUntilTestCaseFinish();
            super.doPost(req, resp);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            TestHttpClient.sleepUntilTestCaseFinish();
            super.doGet(req, resp);
        }
    }

    public static class FlowFilesTimeoutAfterDataExchangeServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.consumeDataPacket(TestHttpClient.readIncomingPacket(req));
            TestHttpClient.sleepUntilTestCaseFinish();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            final int reqProtocolVersion = TestHttpClient.getReqProtocolVersion(req);
            resp.setStatus(SC_ACCEPTED);
            resp.setContentType("application/octet-stream");
            TestHttpClient.setCommonResponseHeaders(resp, reqProtocolVersion);
            TestHttpClient.writeOutgoingPacket(TestHttpClient.getOutputStream(req, resp));
            TestHttpClient.sleepUntilTestCaseFinish();
        }
    }

    private static final String PROXY_USER = "proxy user";

    private static final String PROXY_PASSWORD = "proxy password";

    private static class DataPacketBuilder {
        private final Map<String, String> attributes = new HashMap<>();

        private String contents;

        private TestHttpClient.DataPacketBuilder attr(final String k, final String v) {
            attributes.put(k, v);
            return this;
        }

        private TestHttpClient.DataPacketBuilder contents(final String contents) {
            this.contents = contents;
            return this;
        }

        private DataPacket build() {
            byte[] bytes = contents.getBytes();
            return new StandardDataPacket(attributes, new ByteArrayInputStream(bytes), bytes.length);
        }
    }

    @Test
    public void testUnknownClusterUrl() throws Exception {
        final URI uri = TestHttpClient.server.getURI();
        try (SiteToSiteClient client = getDefaultBuilder().url((((("http://" + (uri.getHost())) + ":") + (uri.getPort())) + "/unknown")).portName("input-running").build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNull(transaction);
        }
    }

    @Test
    public void testWrongPath() throws Exception {
        final URI uri = TestHttpClient.server.getURI();
        try (SiteToSiteClient client = getDefaultBuilder().url((((("http://" + (uri.getHost())) + ":") + (uri.getPort())) + "/wrong")).portName("input-running").build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNull(transaction);
        }
    }

    @Test
    public void testNoAvailablePeer() throws Exception {
        TestHttpClient.peers = new HashSet();
        try (SiteToSiteClient client = getDefaultBuilder().portName("input-running").build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNull(transaction);
        }
    }

    @Test
    public void testSendUnknownPort() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("input-unknown").build()) {
            try {
                client.createTransaction(SEND);
                Assert.fail();
            } catch (IOException e) {
                TestHttpClient.logger.info("Exception message: {}", e.getMessage());
                Assert.assertTrue(e.getMessage().contains("Failed to determine the identifier of port"));
            }
        }
    }

    @Test
    public void testSendSuccess() throws Exception {
        try (final SiteToSiteClient client = getDefaultBuilder().portName("input-running").build()) {
            testSend(client);
        }
    }

    @Test
    public void testSendSuccessMultipleUrls() throws Exception {
        final Set<String> urls = new LinkedHashSet<>();
        urls.add("http://localhost:9999");
        urls.add((("http://localhost:" + (TestHttpClient.httpConnector.getLocalPort())) + "/nifi"));
        try (final SiteToSiteClient client = getDefaultBuilder().urls(urls).portName("input-running").build()) {
            testSend(client);
        }
    }

    @Test
    public void testSendSuccessWithProxy() throws Exception {
        try (final SiteToSiteClient client = getDefaultBuilder().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServer.getListenAddress().getPort(), null, null)).build()) {
            testSend(client);
        }
    }

    @Test
    public void testSendProxyAuthFailed() throws Exception {
        try (final SiteToSiteClient client = getDefaultBuilder().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServerWithAuth.getListenAddress().getPort(), null, null)).build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNull("createTransaction should fail at peer selection and return null.", transaction);
        }
    }

    @Test
    public void testSendSuccessWithProxyAuth() throws Exception {
        try (final SiteToSiteClient client = getDefaultBuilder().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServerWithAuth.getListenAddress().getPort(), TestHttpClient.PROXY_USER, TestHttpClient.PROXY_PASSWORD)).build()) {
            testSend(client);
        }
    }

    @Test
    public void testSendAccessDeniedHTTPS() throws Exception {
        try (final SiteToSiteClient client = getDefaultBuilderHTTPS().portName("input-access-denied").build()) {
            try {
                client.createTransaction(SEND);
                Assert.fail("Handshake exception should be thrown.");
            } catch (HandshakeException e) {
            }
        }
    }

    @Test
    public void testSendSuccessHTTPS() throws Exception {
        try (final SiteToSiteClient client = getDefaultBuilderHTTPS().portName("input-running").build()) {
            testSend(client);
        }
    }

    private interface SendData {
        void apply(final Transaction transaction) throws IOException;
    }

    @Test
    public void testSendLargeFileHTTP() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("input-running").build()) {
            TestHttpClient.testSendLargeFile(client);
        }
    }

    @Test
    public void testSendLargeFileHTTPWithProxy() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServer.getListenAddress().getPort(), null, null)).build()) {
            TestHttpClient.testSendLargeFile(client);
        }
    }

    @Test
    public void testSendLargeFileHTTPWithProxyAuth() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServerWithAuth.getListenAddress().getPort(), TestHttpClient.PROXY_USER, TestHttpClient.PROXY_PASSWORD)).build()) {
            TestHttpClient.testSendLargeFile(client);
        }
    }

    @Test
    public void testSendLargeFileHTTPS() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilderHTTPS().portName("input-running").build()) {
            TestHttpClient.testSendLargeFile(client);
        }
    }

    @Test
    public void testSendLargeFileHTTPSWithProxy() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilderHTTPS().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServer.getListenAddress().getPort(), null, null)).build()) {
            TestHttpClient.testSendLargeFile(client);
        }
    }

    @Test
    public void testSendLargeFileHTTPSWithProxyAuth() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilderHTTPS().portName("input-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServerWithAuth.getListenAddress().getPort(), TestHttpClient.PROXY_USER, TestHttpClient.PROXY_PASSWORD)).build()) {
            TestHttpClient.testSendLargeFile(client);
        }
    }

    @Test
    public void testSendSuccessCompressed() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("input-running").useCompression(true).build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNotNull(transaction);
            TestHttpClient.serverChecksum = "1071206772";
            for (int i = 0; i < 20; i++) {
                DataPacket packet = new TestHttpClient.DataPacketBuilder().contents("Example contents from client.").attr("Client attr 1", "Client attr 1 value").attr("Client attr 2", "Client attr 2 value").build();
                transaction.send(packet);
                long written = getCommunicationsSession().getBytesWritten();
                TestHttpClient.logger.info("{}: {} bytes have been written.", i, written);
            }
            transaction.confirm();
            transaction.complete();
        }
    }

    @Test
    public void testSendSlowClientSuccess() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().idleExpiration(1000, MILLISECONDS).portName("input-running").build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNotNull(transaction);
            TestHttpClient.serverChecksum = "3882825556";
            for (int i = 0; i < 3; i++) {
                DataPacket packet = new TestHttpClient.DataPacketBuilder().contents("Example contents from client.").attr("Client attr 1", "Client attr 1 value").attr("Client attr 2", "Client attr 2 value").build();
                transaction.send(packet);
                long written = getCommunicationsSession().getBytesWritten();
                TestHttpClient.logger.info("{} bytes have been written.", written);
                Thread.sleep(50);
            }
            transaction.confirm();
            transaction.complete();
        }
    }

    @Test
    public void testSendTimeout() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());// skip on windows

        try (SiteToSiteClient client = getDefaultBuilder().timeout(1, SECONDS).portName("input-timeout").build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNotNull(transaction);
            DataPacket packet = new TestHttpClient.DataPacketBuilder().contents("Example contents from client.").attr("Client attr 1", "Client attr 1 value").attr("Client attr 2", "Client attr 2 value").build();
            TestHttpClient.serverChecksum = "1345413116";
            transaction.send(packet);
            try {
                transaction.confirm();
                Assert.fail();
            } catch (IOException e) {
                TestHttpClient.logger.info("An exception was thrown as expected.", e);
                Assert.assertTrue(e.getMessage().contains("TimeoutException"));
            }
            completeShouldFail(transaction);
        }
    }

    @Test
    public void testSendTimeoutAfterDataExchange() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());// skip on windows

        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi.remote.protocol.http.HttpClientTransaction", "INFO");
        try (SiteToSiteClient client = getDefaultBuilder().idleExpiration(500, MILLISECONDS).timeout(500, MILLISECONDS).portName("input-timeout-data-ex").build()) {
            final Transaction transaction = client.createTransaction(SEND);
            Assert.assertNotNull(transaction);
            DataPacket packet = new TestHttpClient.DataPacketBuilder().contents("Example contents from client.").attr("Client attr 1", "Client attr 1 value").attr("Client attr 2", "Client attr 2 value").build();
            for (int i = 0; i < 100; i++) {
                transaction.send(packet);
                if ((i % 10) == 0) {
                    TestHttpClient.logger.info("Sent {} packets...", i);
                }
            }
            try {
                confirmShouldFail(transaction);
                Assert.fail("Should be timeout.");
            } catch (IOException e) {
                TestHttpClient.logger.info("Exception message: {}", e.getMessage());
                Assert.assertTrue(e.getMessage().contains("TimeoutException"));
            }
            completeShouldFail(transaction);
        }
    }

    @Test
    public void testReceiveUnknownPort() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("output-unknown").build()) {
            try {
                client.createTransaction(RECEIVE);
                Assert.fail();
            } catch (IOException e) {
                TestHttpClient.logger.info("Exception message: {}", e.getMessage());
                Assert.assertTrue(e.getMessage().contains("Failed to determine the identifier of port"));
            }
        }
    }

    @Test
    public void testReceiveSuccess() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("output-running").build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSuccessWithProxy() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("output-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServer.getListenAddress().getPort(), null, null)).build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSuccessWithProxyAuth() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("output-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServerWithAuth.getListenAddress().getPort(), TestHttpClient.PROXY_USER, TestHttpClient.PROXY_PASSWORD)).build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSuccessHTTPS() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilderHTTPS().portName("output-running").build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSuccessHTTPSWithProxy() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilderHTTPS().portName("output-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServer.getListenAddress().getPort(), null, null)).build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSuccessHTTPSWithProxyAuth() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilderHTTPS().portName("output-running").httpProxy(new org.apache.nifi.remote.protocol.http.HttpProxy("localhost", TestHttpClient.proxyServerWithAuth.getListenAddress().getPort(), TestHttpClient.PROXY_USER, TestHttpClient.PROXY_PASSWORD)).build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSuccessCompressed() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("output-running").useCompression(true).build()) {
            testReceive(client);
        }
    }

    @Test
    public void testReceiveSlowClientSuccess() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().portName("output-running").build()) {
            final Transaction transaction = client.createTransaction(RECEIVE);
            Assert.assertNotNull(transaction);
            DataPacket packet;
            while ((packet = transaction.receive()) != null) {
                TestHttpClient.consumeDataPacket(packet);
                Thread.sleep(500);
            } 
            transaction.confirm();
            transaction.complete();
        }
    }

    @Test
    public void testReceiveTimeout() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().timeout(1, SECONDS).portName("output-timeout").build()) {
            try {
                client.createTransaction(RECEIVE);
                Assert.fail();
            } catch (IOException e) {
                TestHttpClient.logger.info("An exception was thrown as expected.", e);
                Assert.assertTrue((e instanceof SocketTimeoutException));
            }
        }
    }

    @Test
    public void testReceiveTimeoutAfterDataExchange() throws Exception {
        try (SiteToSiteClient client = getDefaultBuilder().timeout(1, SECONDS).portName("output-timeout-data-ex").build()) {
            final Transaction transaction = client.createTransaction(RECEIVE);
            Assert.assertNotNull(transaction);
            DataPacket packet = transaction.receive();
            Assert.assertNotNull(packet);
            TestHttpClient.consumeDataPacket(packet);
            try {
                transaction.receive();
                Assert.fail();
            } catch (IOException e) {
                TestHttpClient.logger.info("An exception was thrown as expected.", e);
                Assert.assertTrue(((e.getCause()) instanceof SocketTimeoutException));
            }
            confirmShouldFail(transaction);
            completeShouldFail(transaction);
        }
    }
}

