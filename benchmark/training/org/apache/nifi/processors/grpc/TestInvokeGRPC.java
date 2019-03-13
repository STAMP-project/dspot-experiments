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


import ClientAuth.NONE;
import FlowFileReply.Builder;
import FlowFileReply.ResponseCode.ERROR;
import FlowFileReply.ResponseCode.RETRY;
import FlowFileReply.ResponseCode.SUCCESS;
import FlowFileServiceGrpc.FlowFileServiceImplBase;
import InvokeGRPC.EXCEPTION_CLASS;
import InvokeGRPC.PROP_MAX_MESSAGE_SIZE;
import InvokeGRPC.PROP_OUTPUT_RESPONSE_REGARDLESS;
import InvokeGRPC.PROP_PENALIZE_NO_RETRY;
import InvokeGRPC.PROP_SERVICE_HOST;
import InvokeGRPC.PROP_SERVICE_PORT;
import InvokeGRPC.PROP_USE_SECURE;
import InvokeGRPC.REL_FAILURE;
import InvokeGRPC.REL_NO_RETRY;
import InvokeGRPC.REL_RESPONSE;
import InvokeGRPC.REL_RETRY;
import InvokeGRPC.REL_SUCCESS_REQ;
import InvokeGRPC.RESPONSE_BODY;
import InvokeGRPC.RESPONSE_CODE;
import InvokeGRPC.SERVICE_HOST;
import InvokeGRPC.SERVICE_PORT;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TestInvokeGRPC {
    // ids placed on flowfiles and used to dictate response codes in the DummyFlowFileService below
    private static final long ERROR = 500;

    private static final long SUCCESS = 501;

    private static final long RETRY = 502;

    @Test
    public void testSuccess() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.SUCCESS);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 1);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            response.assertAttributeEquals(RESPONSE_BODY, "success");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ);
            MatcherAssert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile successFile = successFiles.get(0);
            successFile.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            successFile.assertAttributeEquals(RESPONSE_BODY, "success");
            successFile.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            successFile.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSuccessWithFlowFileContent() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.enqueue("content");
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 1);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            response.assertAttributeEquals(RESPONSE_BODY, "content");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ);
            MatcherAssert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile successFile = successFiles.get(0);
            successFile.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            successFile.assertAttributeEquals(RESPONSE_BODY, "content");
            successFile.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            successFile.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSuccessAlwaysOutputResponse() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.SUCCESS);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 1);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            response.assertAttributeEquals(RESPONSE_BODY, "success");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ);
            MatcherAssert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile successFile = successFiles.get(0);
            successFile.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            successFile.assertAttributeEquals(RESPONSE_BODY, "success");
            successFile.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            successFile.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testExceedMaxMessageSize() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            // set max message size to 1B to force error
            runner.setProperty(PROP_MAX_MESSAGE_SIZE, "1B");
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.SUCCESS);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 0);
            runner.assertTransferCount(REL_SUCCESS_REQ, 0);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 1);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            // an exception should be thrown indicating that the max message size was exceeded.
            response.assertAttributeEquals(EXCEPTION_CLASS, "io.grpc.StatusRuntimeException");
        } finally {
            server.stop();
        }
    }

    @Test
    public void testRetry() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.RETRY);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 0);
            runner.assertTransferCount(REL_SUCCESS_REQ, 0);
            runner.assertTransferCount(REL_RETRY, 1);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertPenalizeCount(1);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RETRY);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.RETRY));
            response.assertAttributeEquals(RESPONSE_BODY, "retry");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testRetryAlwaysOutputResponse() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.RETRY);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 0);
            runner.assertTransferCount(REL_RETRY, 1);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertPenalizeCount(1);
            final List<MockFlowFile> retryFiles = runner.getFlowFilesForRelationship(REL_RETRY);
            MatcherAssert.assertThat(retryFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile retry = retryFiles.get(0);
            retry.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.RETRY));
            retry.assertAttributeEquals(RESPONSE_BODY, "retry");
            retry.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            retry.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.RETRY));
            response.assertAttributeEquals(RESPONSE_BODY, "retry");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNoRetryOnError() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.ERROR);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 0);
            runner.assertTransferCount(REL_SUCCESS_REQ, 0);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 1);
            runner.assertTransferCount(REL_FAILURE, 0);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_NO_RETRY);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.ERROR));
            response.assertAttributeEquals(RESPONSE_BODY, "error");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNoRetryOnErrorAlwaysOutputResponseAndPenalize() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
            runner.setProperty(PROP_PENALIZE_NO_RETRY, "true");
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.ERROR);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 0);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 1);
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertPenalizeCount(1);
            final List<MockFlowFile> noRetryFiles = runner.getFlowFilesForRelationship(REL_NO_RETRY);
            MatcherAssert.assertThat(noRetryFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile noRetry = noRetryFiles.get(0);
            noRetry.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.ERROR));
            noRetry.assertAttributeEquals(RESPONSE_BODY, "error");
            noRetry.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            noRetry.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.ERROR));
            response.assertAttributeEquals(RESPONSE_BODY, "error");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNoInput() throws Exception {
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class);
        try {
            final int port = server.start(0);
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 0);
            runner.assertTransferCount(REL_SUCCESS_REQ, 0);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertPenalizeCount(0);
        } finally {
            server.stop();
        }
    }

    @Test
    public void testServerConnectionFail() throws Exception {
        final int port = TestGRPCServer.randomPort();
        // should be no gRPC server running @ that port, so processor will fail
        final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
        runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
        runner.setProperty(PROP_SERVICE_PORT, Integer.toString(port));
        final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.SUCCESS);
        runner.enqueue(mockFlowFile);
        runner.run();
        runner.assertTransferCount(REL_RESPONSE, 0);
        runner.assertTransferCount(REL_SUCCESS_REQ, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
        final MockFlowFile response = responseFiles.get(0);
        response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
        response.assertAttributeEquals(SERVICE_PORT, Integer.toString(port));
        response.assertAttributeEquals(EXCEPTION_CLASS, "io.grpc.StatusRuntimeException");
    }

    @Test
    public void testSecureTwoWaySsl() throws Exception {
        final Map<String, String> sslProperties = TestInvokeGRPC.getKeystoreProperties();
        sslProperties.putAll(TestInvokeGRPC.getTruststoreProperties());
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class, sslProperties);
        try {
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            useSSLContextService(runner, sslProperties);
            final int port = server.start(0);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.setProperty(PROP_USE_SECURE, "true");
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.SUCCESS);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 1);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            response.assertAttributeEquals(RESPONSE_BODY, "success");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ);
            MatcherAssert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile successFile = successFiles.get(0);
            successFile.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            successFile.assertAttributeEquals(RESPONSE_BODY, "success");
            successFile.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            successFile.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSecureOneWaySsl() throws Exception {
        final Map<String, String> sslProperties = TestInvokeGRPC.getKeystoreProperties();
        sslProperties.put(TestGRPCServer.NEED_CLIENT_AUTH, NONE.name());
        final TestGRPCServer<TestInvokeGRPC.DummyFlowFileService> server = new TestGRPCServer(TestInvokeGRPC.DummyFlowFileService.class, sslProperties);
        try {
            final TestRunner runner = TestRunners.newTestRunner(InvokeGRPC.class);
            runner.setProperty(PROP_SERVICE_HOST, TestGRPCServer.HOST);
            useSSLContextService(runner, TestInvokeGRPC.getTruststoreProperties());
            final int port = server.start(0);
            runner.setProperty(PROP_SERVICE_PORT, String.valueOf(port));
            runner.setProperty(PROP_USE_SECURE, "true");
            final MockFlowFile mockFlowFile = new MockFlowFile(TestInvokeGRPC.SUCCESS);
            runner.enqueue(mockFlowFile);
            runner.run();
            runner.assertTransferCount(REL_RESPONSE, 1);
            runner.assertTransferCount(REL_SUCCESS_REQ, 1);
            runner.assertTransferCount(REL_RETRY, 0);
            runner.assertTransferCount(REL_NO_RETRY, 0);
            runner.assertTransferCount(REL_FAILURE, 0);
            final List<MockFlowFile> responseFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
            MatcherAssert.assertThat(responseFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile response = responseFiles.get(0);
            response.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            response.assertAttributeEquals(RESPONSE_BODY, "success");
            response.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            response.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
            final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ);
            MatcherAssert.assertThat(successFiles.size(), CoreMatchers.equalTo(1));
            final MockFlowFile successFile = successFiles.get(0);
            successFile.assertAttributeEquals(RESPONSE_CODE, String.valueOf(FlowFileReply.ResponseCode.SUCCESS));
            successFile.assertAttributeEquals(RESPONSE_BODY, "success");
            successFile.assertAttributeEquals(SERVICE_HOST, TestGRPCServer.HOST);
            successFile.assertAttributeEquals(SERVICE_PORT, String.valueOf(port));
        } finally {
            server.stop();
        }
    }

    /**
     * Dummy gRPC service whose responses are dictated by the IDs on the messages it receives
     */
    private static class DummyFlowFileService extends FlowFileServiceGrpc.FlowFileServiceImplBase {
        public DummyFlowFileService() {
        }

        @Override
        public void send(FlowFileRequest request, StreamObserver<FlowFileReply> responseObserver) {
            final FlowFileReply.Builder replyBuilder = FlowFileReply.newBuilder();
            // use the id to dictate response codes
            final long id = request.getId();
            if (id == (TestInvokeGRPC.ERROR)) {
                replyBuilder.setResponseCode(FlowFileReply.ResponseCode.ERROR).setBody("error");
            } else
                if (id == (TestInvokeGRPC.SUCCESS)) {
                    replyBuilder.setResponseCode(FlowFileReply.ResponseCode.SUCCESS).setBody("success");
                } else
                    if (id == (TestInvokeGRPC.RETRY)) {
                        replyBuilder.setResponseCode(FlowFileReply.ResponseCode.RETRY).setBody("retry");
                        // else, assume the request is to include the flowfile content in the response
                    } else {
                        replyBuilder.setResponseCode(FlowFileReply.ResponseCode.SUCCESS).setBody(request.getContent().toStringUtf8());
                    }


            responseObserver.onNext(replyBuilder.build());
            responseObserver.onCompleted();
        }
    }
}

