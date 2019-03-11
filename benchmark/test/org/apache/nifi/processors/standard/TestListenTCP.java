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


import ListenTCP.CLIENT_AUTH;
import ListenTCP.MAX_BATCH_SIZE;
import ListenTCP.PORT;
import ListenTCP.REL_SUCCESS;
import SSLContextService.ClientAuth.NONE;
import SslContextFactory.ClientAuth.REQUIRED;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.security.util.SslContextFactory;
import org.apache.nifi.security.util.SslContextFactory.ClientAuth;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestListenTCP {
    private ListenTCP proc;

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
    public void testListenTCP() throws IOException, InterruptedException {
        final List<String> messages = new ArrayList<>();
        messages.add("This is message 1\n");
        messages.add("This is message 2\n");
        messages.add("This is message 3\n");
        messages.add("This is message 4\n");
        messages.add("This is message 5\n");
        runTCP(messages, messages.size(), null);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < (mockFlowFiles.size()); i++) {
            mockFlowFiles.get(i).assertContentEquals(("This is message " + (i + 1)));
        }
    }

    @Test
    public void testListenTCPBatching() throws IOException, InterruptedException {
        runner.setProperty(MAX_BATCH_SIZE, "3");
        final List<String> messages = new ArrayList<>();
        messages.add("This is message 1\n");
        messages.add("This is message 2\n");
        messages.add("This is message 3\n");
        messages.add("This is message 4\n");
        messages.add("This is message 5\n");
        runTCP(messages, 2, null);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile mockFlowFile1 = mockFlowFiles.get(0);
        mockFlowFile1.assertContentEquals("This is message 1\nThis is message 2\nThis is message 3");
        MockFlowFile mockFlowFile2 = mockFlowFiles.get(1);
        mockFlowFile2.assertContentEquals("This is message 4\nThis is message 5");
    }

    @Test
    public void testTLSClientAuthRequiredAndClientCertProvided() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InitializationException {
        runner.setProperty(CLIENT_AUTH, SSLContextService.ClientAuth.REQUIRED.name());
        configureProcessorSslContextService();
        final List<String> messages = new ArrayList<>();
        messages.add("This is message 1\n");
        messages.add("This is message 2\n");
        messages.add("This is message 3\n");
        messages.add("This is message 4\n");
        messages.add("This is message 5\n");
        // Make an SSLContext with a key and trust store to send the test messages
        final SSLContext clientSslContext = SslContextFactory.createSslContext("src/test/resources/keystore.jks", "passwordpassword".toCharArray(), "jks", "src/test/resources/truststore.jks", "passwordpassword".toCharArray(), "jks", ClientAuth.valueOf("NONE"), "TLS");
        runTCP(messages, messages.size(), clientSslContext);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < (mockFlowFiles.size()); i++) {
            mockFlowFiles.get(i).assertContentEquals(("This is message " + (i + 1)));
        }
    }

    @Test
    public void testTLSClientAuthRequiredAndClientCertNotProvided() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InitializationException {
        runner.setProperty(CLIENT_AUTH, SSLContextService.ClientAuth.REQUIRED.name());
        configureProcessorSslContextService();
        final List<String> messages = new ArrayList<>();
        messages.add("This is message 1\n");
        messages.add("This is message 2\n");
        messages.add("This is message 3\n");
        messages.add("This is message 4\n");
        messages.add("This is message 5\n");
        // Make an SSLContext that only has the trust store, this should not work since the processor has client auth REQUIRED
        final SSLContext clientSslContext = SslContextFactory.createTrustSslContext("src/test/resources/truststore.jks", "passwordpassword".toCharArray(), "jks", "TLS");
        try {
            runTCP(messages, messages.size(), clientSslContext);
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void testTLSClientAuthNoneAndClientCertNotProvided() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InitializationException {
        runner.setProperty(CLIENT_AUTH, NONE.name());
        configureProcessorSslContextService();
        final List<String> messages = new ArrayList<>();
        messages.add("This is message 1\n");
        messages.add("This is message 2\n");
        messages.add("This is message 3\n");
        messages.add("This is message 4\n");
        messages.add("This is message 5\n");
        // Make an SSLContext that only has the trust store, this should not work since the processor has client auth REQUIRED
        final SSLContext clientSslContext = SslContextFactory.createTrustSslContext("src/test/resources/truststore.jks", "passwordpassword".toCharArray(), "jks", "TLS");
        runTCP(messages, messages.size(), clientSslContext);
        List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < (mockFlowFiles.size()); i++) {
            mockFlowFiles.get(i).assertContentEquals(("This is message " + (i + 1)));
        }
    }
}

