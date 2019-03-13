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


import HttpServletResponse.SC_NO_CONTENT;
import HttpServletResponse.SC_OK;
import ListenHTTP.BASE_PATH;
import ListenHTTP.PORT;
import ListenHTTP.RELATIONSHIP_SUCCESS;
import ListenHTTP.RETURN_CODE;
import MultipartBody.FORM;
import StandardRestrictedSSLContextService.RESTRICTED_SSL_ALGORITHM;
import StandardSSLContextService.SSL_ALGORITHM;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestListenHTTP {
    private static final String SSL_CONTEXT_SERVICE_IDENTIFIER = "ssl-context";

    private static final String HTTP_POST_METHOD = "POST";

    private static final String HTTP_BASE_PATH = "basePath";

    private static final String PORT_VARIABLE = "HTTP_PORT";

    private static final String HTTP_SERVER_PORT_EL = ("${" + (TestListenHTTP.PORT_VARIABLE)) + "}";

    private static final String BASEPATH_VARIABLE = "HTTP_BASEPATH";

    private static final String HTTP_SERVER_BASEPATH_EL = ("${" + (TestListenHTTP.BASEPATH_VARIABLE)) + "}";

    private ListenHTTP proc;

    private TestRunner runner;

    private int availablePort;

    @Test
    public void testPOSTRequestsReceivedWithoutEL() throws Exception {
        runner.setProperty(PORT, Integer.toString(availablePort));
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_BASE_PATH);
        testPOSTRequestsReceived(SC_OK);
    }

    @Test
    public void testPOSTRequestsReceivedReturnCodeWithoutEL() throws Exception {
        runner.setProperty(PORT, Integer.toString(availablePort));
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_BASE_PATH);
        runner.setProperty(RETURN_CODE, Integer.toString(SC_NO_CONTENT));
        testPOSTRequestsReceived(SC_NO_CONTENT);
    }

    @Test
    public void testPOSTRequestsReceivedWithEL() throws Exception {
        runner.setProperty(PORT, TestListenHTTP.HTTP_SERVER_PORT_EL);
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_SERVER_BASEPATH_EL);
        runner.assertValid();
        testPOSTRequestsReceived(SC_OK);
    }

    @Test
    public void testPOSTRequestsReturnCodeReceivedWithEL() throws Exception {
        runner.setProperty(PORT, TestListenHTTP.HTTP_SERVER_PORT_EL);
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_SERVER_BASEPATH_EL);
        runner.setProperty(RETURN_CODE, Integer.toString(SC_NO_CONTENT));
        runner.assertValid();
        testPOSTRequestsReceived(SC_NO_CONTENT);
    }

    @Test
    public void testSecurePOSTRequestsReceivedWithoutEL() throws Exception {
        SSLContextService sslContextService = configureProcessorSslContextService();
        runner.setProperty(sslContextService, RESTRICTED_SSL_ALGORITHM, "TLSv1.2");
        runner.enableControllerService(sslContextService);
        runner.setProperty(PORT, Integer.toString(availablePort));
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_BASE_PATH);
        runner.assertValid();
        testPOSTRequestsReceived(SC_OK);
    }

    @Test
    public void testSecurePOSTRequestsReturnCodeReceivedWithoutEL() throws Exception {
        SSLContextService sslContextService = configureProcessorSslContextService();
        runner.setProperty(sslContextService, RESTRICTED_SSL_ALGORITHM, "TLSv1.2");
        runner.enableControllerService(sslContextService);
        runner.setProperty(PORT, Integer.toString(availablePort));
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_BASE_PATH);
        runner.setProperty(RETURN_CODE, Integer.toString(SC_NO_CONTENT));
        runner.assertValid();
        testPOSTRequestsReceived(SC_NO_CONTENT);
    }

    @Test
    public void testSecurePOSTRequestsReceivedWithEL() throws Exception {
        SSLContextService sslContextService = configureProcessorSslContextService();
        runner.setProperty(sslContextService, RESTRICTED_SSL_ALGORITHM, "TLSv1.2");
        runner.enableControllerService(sslContextService);
        runner.setProperty(PORT, TestListenHTTP.HTTP_SERVER_PORT_EL);
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_SERVER_BASEPATH_EL);
        runner.assertValid();
        testPOSTRequestsReceived(SC_OK);
    }

    @Test
    public void testSecurePOSTRequestsReturnCodeReceivedWithEL() throws Exception {
        SSLContextService sslContextService = configureProcessorSslContextService();
        runner.setProperty(sslContextService, RESTRICTED_SSL_ALGORITHM, "TLSv1.2");
        runner.enableControllerService(sslContextService);
        runner.setProperty(PORT, Integer.toString(availablePort));
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_BASE_PATH);
        runner.setProperty(RETURN_CODE, Integer.toString(SC_NO_CONTENT));
        runner.assertValid();
        testPOSTRequestsReceived(SC_NO_CONTENT);
    }

    @Test
    public void testSecureInvalidSSLConfiguration() throws Exception {
        SSLContextService sslContextService = configureInvalidProcessorSslContextService();
        runner.setProperty(sslContextService, SSL_ALGORITHM, "TLSv1.2");
        runner.enableControllerService(sslContextService);
        runner.setProperty(PORT, TestListenHTTP.HTTP_SERVER_PORT_EL);
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_SERVER_BASEPATH_EL);
        runner.assertNotValid();
    }

    @Test
    public void testMultipartFormDataRequest() throws Exception {
        runner.setProperty(PORT, Integer.toString(availablePort));
        runner.setProperty(BASE_PATH, TestListenHTTP.HTTP_BASE_PATH);
        runner.setProperty(RETURN_CODE, Integer.toString(SC_OK));
        final SSLContextService sslContextService = runner.getControllerService(TestListenHTTP.SSL_CONTEXT_SERVICE_IDENTIFIER, SSLContextService.class);
        final boolean isSecure = sslContextService != null;
        Runnable sendRequestToWebserver = () -> {
            try {
                MultipartBody multipartBody = new MultipartBody.Builder().setType(FORM).addFormDataPart("p1", "v1").addFormDataPart("p2", "v2").addFormDataPart("file1", "my-file-text.txt", RequestBody.create(MediaType.parse("text/plain"), createTextFile("my-file-text.txt", "Hello", "World"))).addFormDataPart("file2", "my-file-data.json", RequestBody.create(MediaType.parse("application/json"), createTextFile("my-file-text.txt", "{ \"name\":\"John\", \"age\":30 }"))).addFormDataPart("file3", "my-file-binary.bin", RequestBody.create(MediaType.parse("application/octet-stream"), generateRandomBinaryData(100))).build();
                Request request = new Request.Builder().url(buildUrl(isSecure)).post(multipartBody).build();
                int timeout = 3000;
                OkHttpClient client = new OkHttpClient.Builder().readTimeout(timeout, TimeUnit.MILLISECONDS).writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
                try (Response response = client.newCall(request).execute()) {
                    Assert.assertTrue(String.format("Unexpected code: %s, body: %s", response.code(), response.body().string()), response.isSuccessful());
                }
            } catch (final Throwable t) {
                t.printStackTrace();
                Assert.fail(t.toString());
            }
        };
        startWebServerAndSendRequests(sendRequestToWebserver, 5, 200);
        runner.assertAllFlowFilesTransferred(RELATIONSHIP_SUCCESS, 5);
        List<MockFlowFile> flowFilesForRelationship = runner.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
        // Part fragments are not processed in the order we submitted them.
        // We cannot rely on the order we sent them in.
        MockFlowFile mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "p1");
        mff.assertAttributeEquals("http.multipart.name", "p1");
        mff.assertAttributeExists("http.multipart.size");
        mff.assertAttributeEquals("http.multipart.fragments.sequence.number", "1");
        mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
        mff.assertAttributeExists("http.headers.multipart.content-disposition");
        mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "p2");
        mff.assertAttributeEquals("http.multipart.name", "p2");
        mff.assertAttributeExists("http.multipart.size");
        mff.assertAttributeExists("http.multipart.fragments.sequence.number");
        mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
        mff.assertAttributeExists("http.headers.multipart.content-disposition");
        mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "file1");
        mff.assertAttributeEquals("http.multipart.name", "file1");
        mff.assertAttributeEquals("http.multipart.filename", "my-file-text.txt");
        mff.assertAttributeEquals("http.headers.multipart.content-type", "text/plain");
        mff.assertAttributeExists("http.multipart.size");
        mff.assertAttributeExists("http.multipart.fragments.sequence.number");
        mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
        mff.assertAttributeExists("http.headers.multipart.content-disposition");
        mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "file2");
        mff.assertAttributeEquals("http.multipart.name", "file2");
        mff.assertAttributeEquals("http.multipart.filename", "my-file-data.json");
        mff.assertAttributeEquals("http.headers.multipart.content-type", "application/json");
        mff.assertAttributeExists("http.multipart.size");
        mff.assertAttributeExists("http.multipart.fragments.sequence.number");
        mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
        mff.assertAttributeExists("http.headers.multipart.content-disposition");
        mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "file3");
        mff.assertAttributeEquals("http.multipart.name", "file3");
        mff.assertAttributeEquals("http.multipart.filename", "my-file-binary.bin");
        mff.assertAttributeEquals("http.headers.multipart.content-type", "application/octet-stream");
        mff.assertAttributeExists("http.multipart.size");
        mff.assertAttributeExists("http.multipart.fragments.sequence.number");
        mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
        mff.assertAttributeExists("http.headers.multipart.content-disposition");
    }
}

