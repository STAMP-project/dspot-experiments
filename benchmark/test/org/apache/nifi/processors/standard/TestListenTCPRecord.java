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
package org.apache.nifi.processors.standard;


import ListenTCPRecord.CLIENT_AUTH;
import ListenTCPRecord.PORT;
import ListenTCPRecord.READ_TIMEOUT;
import ListenTCPRecord.RECORD_BATCH_SIZE;
import ListenTCPRecord.REL_SUCCESS;
import SSLContextService.ClientAuth.NONE;
import SslContextFactory.ClientAuth.REQUIRED;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.security.util.SslContextFactory;
import org.apache.nifi.security.util.SslContextFactory.ClientAuth;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestListenTCPRecord {
    static final Logger LOGGER = LoggerFactory.getLogger(TestListenTCPRecord.class);

    static final String SCHEMA_TEXT = "{\n" + (((((((("  \"name\": \"syslogRecord\",\n" + "  \"namespace\": \"nifi\",\n") + "  \"type\": \"record\",\n") + "  \"fields\": [\n") + "    { \"name\": \"timestamp\", \"type\": \"string\" },\n") + "    { \"name\": \"logsource\", \"type\": \"string\" },\n") + "    { \"name\": \"message\", \"type\": \"string\" }\n") + "  ]\n") + "}");

    static final List<String> DATA;

    static {
        final List<String> data = new ArrayList<>();
        data.add("[");
        data.add("{\"timestamp\" : \"123456789\", \"logsource\" : \"syslog\", \"message\" : \"This is a test 1\"},");
        data.add("{\"timestamp\" : \"123456789\", \"logsource\" : \"syslog\", \"message\" : \"This is a test 2\"},");
        data.add("{\"timestamp\" : \"123456789\", \"logsource\" : \"syslog\", \"message\" : \"This is a test 3\"}");
        data.add("]");
        DATA = Collections.unmodifiableList(data);
    }

    private ListenTCPRecord proc;

    private TestRunner runner;

    @Test
    public void testCustomValidate() throws InitializationException {
        runner.setProperty(PORT, "1");
        runner.assertValid();
        configureProcessorSslContextService();
        runner.setProperty(CLIENT_AUTH, "");
        runner.assertNotValid();
        runner.setProperty(CLIENT_AUTH, REQUIRED.name());
        runner.assertValid();
    }

    @Test
    public void testOneRecordPerFlowFile() throws IOException, InterruptedException {
        runner.setProperty(RECORD_BATCH_SIZE, "1");
        runTCP(TestListenTCPRecord.DATA, 3, null);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < (mockFlowFiles.size()); i++) {
            final MockFlowFile flowFile = mockFlowFiles.get(i);
            flowFile.assertAttributeEquals("record.count", "1");
            final String content = new String(flowFile.toByteArray(), StandardCharsets.UTF_8);
            Assert.assertNotNull(content);
            Assert.assertTrue(content.contains(("This is a test " + (i + 1))));
        }
    }

    @Test
    public void testMultipleRecordsPerFlowFileLessThanBatchSize() throws IOException, InterruptedException {
        runner.setProperty(RECORD_BATCH_SIZE, "5");
        runTCP(TestListenTCPRecord.DATA, 1, null);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, mockFlowFiles.size());
        final MockFlowFile flowFile = mockFlowFiles.get(0);
        flowFile.assertAttributeEquals("record.count", "3");
        final String content = new String(flowFile.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertNotNull(content);
        Assert.assertTrue(content.contains(("This is a test " + 1)));
        Assert.assertTrue(content.contains(("This is a test " + 2)));
        Assert.assertTrue(content.contains(("This is a test " + 3)));
    }

    @Test
    public void testTLSClientAuthRequiredAndClientCertProvided() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InitializationException {
        runner.setProperty(CLIENT_AUTH, SSLContextService.ClientAuth.REQUIRED.name());
        configureProcessorSslContextService();
        // Make an SSLContext with a key and trust store to send the test messages
        final SSLContext clientSslContext = SslContextFactory.createSslContext("src/test/resources/keystore.jks", "passwordpassword".toCharArray(), "jks", "src/test/resources/truststore.jks", "passwordpassword".toCharArray(), "jks", ClientAuth.valueOf("NONE"), "TLS");
        runTCP(TestListenTCPRecord.DATA, 1, clientSslContext);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, mockFlowFiles.size());
        final String content = new String(mockFlowFiles.get(0).toByteArray(), StandardCharsets.UTF_8);
        Assert.assertNotNull(content);
        Assert.assertTrue(content.contains(("This is a test " + 1)));
        Assert.assertTrue(content.contains(("This is a test " + 2)));
        Assert.assertTrue(content.contains(("This is a test " + 3)));
    }

    @Test
    public void testTLSClientAuthRequiredAndClientCertNotProvided() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InitializationException {
        runner.setProperty(CLIENT_AUTH, SSLContextService.ClientAuth.REQUIRED.name());
        runner.setProperty(READ_TIMEOUT, "5 seconds");
        configureProcessorSslContextService();
        // Make an SSLContext that only has the trust store, this should not work since the processor has client auth REQUIRED
        final SSLContext clientSslContext = SslContextFactory.createTrustSslContext("src/test/resources/truststore.jks", "passwordpassword".toCharArray(), "jks", "TLS");
        runTCP(TestListenTCPRecord.DATA, 0, clientSslContext);
    }

    @Test
    public void testTLSClientAuthNoneAndClientCertNotProvided() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InitializationException {
        runner.setProperty(CLIENT_AUTH, NONE.name());
        configureProcessorSslContextService();
        // Make an SSLContext that only has the trust store, this should work since the processor has client auth NONE
        final SSLContext clientSslContext = SslContextFactory.createTrustSslContext("src/test/resources/truststore.jks", "passwordpassword".toCharArray(), "jks", "TLS");
        runTCP(TestListenTCPRecord.DATA, 1, clientSslContext);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, mockFlowFiles.size());
        final String content = new String(mockFlowFiles.get(0).toByteArray(), StandardCharsets.UTF_8);
        Assert.assertNotNull(content);
        Assert.assertTrue(content.contains(("This is a test " + 1)));
        Assert.assertTrue(content.contains(("This is a test " + 2)));
        Assert.assertTrue(content.contains(("This is a test " + 3)));
    }

    private static class SocketSender implements Closeable , Runnable {
        private final int port;

        private final String host;

        private final SSLContext sslContext;

        private final List<String> data;

        private final long delay;

        private Socket socket;

        public SocketSender(final int port, final String host, final SSLContext sslContext, final List<String> data, final long delay) {
            this.port = port;
            this.host = host;
            this.sslContext = sslContext;
            this.data = data;
            this.delay = delay;
        }

        @Override
        public void run() {
            try {
                if ((sslContext) != null) {
                    socket = sslContext.getSocketFactory().createSocket(host, port);
                } else {
                    socket = new Socket(host, port);
                }
                for (final String message : data) {
                    socket.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
                    if ((delay) > 0) {
                        Thread.sleep(delay);
                    }
                }
                socket.getOutputStream().flush();
            } catch (final Exception e) {
                TestListenTCPRecord.LOGGER.error(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(socket);
            }
        }

        public void close() {
            IOUtils.closeQuietly(socket);
        }
    }
}

