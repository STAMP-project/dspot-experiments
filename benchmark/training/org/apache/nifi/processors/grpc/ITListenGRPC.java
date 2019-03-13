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
package org.apache.nifi.processors.grpc;


import CoreAttributes.UUID;
import FlowFileIngestServiceInterceptor.DEFAULT_FOUND_SUBJECT;
import FlowFileReply.ResponseCode.ERROR;
import FlowFileReply.ResponseCode.SUCCESS;
import FlowFileServiceGrpc.FlowFileServiceBlockingStub;
import ListenGRPC.PROP_AUTHORIZED_DN_PATTERN;
import ListenGRPC.PROP_MAX_MESSAGE_SIZE;
import ListenGRPC.PROP_SERVICE_PORT;
import ListenGRPC.PROP_USE_SECURE;
import ListenGRPC.REL_SUCCESS;
import ListenGRPC.REMOTE_HOST;
import ListenGRPC.REMOTE_USER_DN;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ITListenGRPC {
    private static final String HOST = "localhost";

    private static final String CERT_DN = "CN=localhost, OU=NIFI";

    private static final String SOURCE_SYSTEM_UUID = "FAKE_UUID";

    @Test
    public void testSuccessfulRoundTrip() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        final ProcessContext processContext = runner.getProcessContext();
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").putAttributes(UUID.key(), ITListenGRPC.SOURCE_SYSTEM_UUID).setContent(ByteString.copyFrom("content".getBytes())).build();
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(SUCCESS));
            Assert.assertThat(reply.getBody(), CoreMatchers.equalTo("FlowFile successfully received."));
            runner.assertTransferCount(REL_SUCCESS, 1);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile mockFlowFile = successFiles.get(0);
            Assert.assertThat(mockFlowFile.getAttribute("FOO"), CoreMatchers.equalTo("BAR"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_HOST), CoreMatchers.equalTo("127.0.0.1"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_USER_DN), CoreMatchers.equalTo(DEFAULT_FOUND_SUBJECT));
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }

    @Test
    public void testOutOfSpaceRoundTrip() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        final ProcessContext processContext = Mockito.spy(runner.getProcessContext());
        // force the context to return that space isn't available, prompting an error message to be returned.
        Mockito.when(processContext.getAvailableRelationships()).thenReturn(Sets.newHashSet());
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").setContent(ByteString.copyFrom("content".getBytes())).build();
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(ERROR));
            Assert.assertThat(reply.getBody(), StringContains.containsString("but no space available; Indicating Service Unavailable"));
            runner.assertTransferCount(REL_SUCCESS, 0);
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }

    @Test(expected = StatusRuntimeException.class)
    public void testExceedMaxMessageSize() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        // set max message size to 1 byte to force exception to be thrown.
        runner.setProperty(PROP_MAX_MESSAGE_SIZE, "1B");
        final ProcessContext processContext = runner.getProcessContext();
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").putAttributes(UUID.key(), ITListenGRPC.SOURCE_SYSTEM_UUID).setContent(ByteString.copyFrom("content".getBytes())).build();
            // this should throw a runtime exception
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(SUCCESS));
            Assert.assertThat(reply.getBody(), CoreMatchers.equalTo("FlowFile successfully received."));
            runner.assertTransferCount(REL_SUCCESS, 1);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile mockFlowFile = successFiles.get(0);
            Assert.assertThat(mockFlowFile.getAttribute("FOO"), CoreMatchers.equalTo("BAR"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_HOST), CoreMatchers.equalTo("127.0.0.1"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_USER_DN), CoreMatchers.equalTo(DEFAULT_FOUND_SUBJECT));
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }

    @Test
    public void testSecureTwoWaySSL() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final Map<String, String> sslProperties = ITListenGRPC.getKeystoreProperties();
        sslProperties.putAll(ITListenGRPC.getTruststoreProperties());
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort, sslProperties);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        runner.setProperty(PROP_USE_SECURE, "true");
        ITListenGRPC.useSSLContextService(runner, sslProperties);
        final ProcessContext processContext = runner.getProcessContext();
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").setContent(ByteString.copyFrom("content".getBytes())).build();
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(SUCCESS));
            Assert.assertThat(reply.getBody(), CoreMatchers.equalTo("FlowFile successfully received."));
            runner.assertTransferCount(REL_SUCCESS, 1);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile mockFlowFile = successFiles.get(0);
            Assert.assertThat(mockFlowFile.getAttribute("FOO"), CoreMatchers.equalTo("BAR"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_HOST), CoreMatchers.equalTo("127.0.0.1"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_USER_DN), CoreMatchers.equalTo(ITListenGRPC.CERT_DN));
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }

    @Test
    public void testSecureOneWaySSL() throws IOException, InterruptedException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final Map<String, String> sslProperties = ITListenGRPC.getTruststoreProperties();
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort, sslProperties);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        runner.setProperty(PROP_USE_SECURE, "true");
        ITListenGRPC.useSSLContextService(runner, ITListenGRPC.getKeystoreProperties());
        final ProcessContext processContext = runner.getProcessContext();
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").setContent(ByteString.copyFrom("content".getBytes())).build();
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(SUCCESS));
            Assert.assertThat(reply.getBody(), CoreMatchers.equalTo("FlowFile successfully received."));
            // known race condition spot: grpc reply vs flowfile transfer
            Thread.sleep(10);
            runner.assertTransferCount(REL_SUCCESS, 1);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile mockFlowFile = successFiles.get(0);
            Assert.assertThat(mockFlowFile.getAttribute("FOO"), CoreMatchers.equalTo("BAR"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_HOST), CoreMatchers.equalTo("127.0.0.1"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_USER_DN), CoreMatchers.equalTo(DEFAULT_FOUND_SUBJECT));
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }

    @Test(expected = StatusRuntimeException.class)
    public void testSecureTwoWaySSLFailAuthorizedDNCheck() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final Map<String, String> sslProperties = ITListenGRPC.getKeystoreProperties();
        sslProperties.putAll(ITListenGRPC.getTruststoreProperties());
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort, sslProperties);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        runner.setProperty(PROP_USE_SECURE, "true");
        runner.setProperty(PROP_AUTHORIZED_DN_PATTERN, "CN=FAKE.*");
        ITListenGRPC.useSSLContextService(runner, sslProperties);
        final ProcessContext processContext = runner.getProcessContext();
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").setContent(ByteString.copyFrom("content".getBytes())).build();
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(SUCCESS));
            Assert.assertThat(reply.getBody(), CoreMatchers.equalTo("FlowFile successfully received."));
            runner.assertTransferCount(REL_SUCCESS, 1);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile mockFlowFile = successFiles.get(0);
            Assert.assertThat(mockFlowFile.getAttribute("FOO"), CoreMatchers.equalTo("BAR"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_HOST), CoreMatchers.equalTo("127.0.0.1"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_USER_DN), CoreMatchers.equalTo(ITListenGRPC.CERT_DN));
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }

    @Test
    public void testSecureTwoWaySSLPassAuthorizedDNCheck() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        final int randPort = TestGRPCClient.randomPort();
        final Map<String, String> sslProperties = ITListenGRPC.getKeystoreProperties();
        sslProperties.putAll(ITListenGRPC.getTruststoreProperties());
        final ManagedChannel channel = TestGRPCClient.buildChannel(ITListenGRPC.HOST, randPort, sslProperties);
        final FlowFileServiceGrpc.FlowFileServiceBlockingStub stub = FlowFileServiceGrpc.newBlockingStub(channel);
        final ListenGRPC listenGRPC = new ListenGRPC();
        final TestRunner runner = TestRunners.newTestRunner(listenGRPC);
        runner.setProperty(PROP_SERVICE_PORT, String.valueOf(randPort));
        runner.setProperty(PROP_USE_SECURE, "true");
        runner.setProperty(PROP_AUTHORIZED_DN_PATTERN, "CN=localhost.*");
        ITListenGRPC.useSSLContextService(runner, sslProperties);
        final ProcessContext processContext = runner.getProcessContext();
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        try {
            // start the server. The order of the following statements shouldn't matter, because the
            // startServer() method waits for a processSessionFactory to be available to it.
            listenGRPC.startServer(processContext);
            listenGRPC.onTrigger(processContext, processSessionFactory);
            final FlowFileRequest ingestFile = FlowFileRequest.newBuilder().putAttributes("FOO", "BAR").setContent(ByteString.copyFrom("content".getBytes())).build();
            final FlowFileReply reply = stub.send(ingestFile);
            Assert.assertThat(reply.getResponseCode(), CoreMatchers.equalTo(SUCCESS));
            Assert.assertThat(reply.getBody(), CoreMatchers.equalTo("FlowFile successfully received."));
            runner.assertTransferCount(REL_SUCCESS, 1);
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
            Assert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile mockFlowFile = successFiles.get(0);
            Assert.assertThat(mockFlowFile.getAttribute("FOO"), CoreMatchers.equalTo("BAR"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_HOST), CoreMatchers.equalTo("127.0.0.1"));
            Assert.assertThat(mockFlowFile.getAttribute(REMOTE_USER_DN), CoreMatchers.equalTo(ITListenGRPC.CERT_DN));
        } finally {
            // stop the server
            listenGRPC.stopServer(processContext);
            channel.shutdown();
        }
    }
}

