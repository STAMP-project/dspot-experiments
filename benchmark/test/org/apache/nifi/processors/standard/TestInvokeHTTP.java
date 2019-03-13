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


import InvokeHTTP.EXCEPTION_CLASS;
import InvokeHTTP.PROP_ATTRIBUTES_TO_SEND;
import InvokeHTTP.PROP_PROXY_HOST;
import InvokeHTTP.PROP_PROXY_PASSWORD;
import InvokeHTTP.PROP_PROXY_PORT;
import InvokeHTTP.PROP_PROXY_USER;
import InvokeHTTP.PROP_SSL_CONTEXT_SERVICE;
import InvokeHTTP.PROP_URL;
import InvokeHTTP.REL_FAILURE;
import InvokeHTTP.REL_NO_RETRY;
import InvokeHTTP.REL_RESPONSE;
import InvokeHTTP.REL_RETRY;
import InvokeHTTP.REL_SUCCESS_REQ;
import InvokeHTTP.STATUS_CODE;
import InvokeHTTP.STATUS_MESSAGE;
import StandardSSLContextService.KEYSTORE;
import StandardSSLContextService.KEYSTORE_PASSWORD;
import StandardSSLContextService.KEYSTORE_TYPE;
import StandardSSLContextService.TRUSTSTORE;
import StandardSSLContextService.TRUSTSTORE_PASSWORD;
import StandardSSLContextService.TRUSTSTORE_TYPE;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.nifi.processors.standard.util.TestInvokeHttpCommon;
import org.apache.nifi.ssl.StandardSSLContextService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunners;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;


public class TestInvokeHTTP extends TestInvokeHttpCommon {
    @Test
    public void testSslSetHttpRequest() throws Exception {
        final Map<String, String> sslProperties = new HashMap<>();
        sslProperties.put(KEYSTORE.getName(), "src/test/resources/keystore.jks");
        sslProperties.put(KEYSTORE_PASSWORD.getName(), "passwordpassword");
        sslProperties.put(KEYSTORE_TYPE.getName(), "JKS");
        sslProperties.put(TRUSTSTORE.getName(), "src/test/resources/truststore.jks");
        sslProperties.put(TRUSTSTORE_PASSWORD.getName(), "passwordpassword");
        sslProperties.put(TRUSTSTORE_TYPE.getName(), "JKS");
        runner = TestRunners.newTestRunner(InvokeHTTP.class);
        final StandardSSLContextService sslService = new StandardSSLContextService();
        runner.addControllerService("ssl-context", sslService, sslProperties);
        runner.enableControllerService(sslService);
        runner.setProperty(PROP_SSL_CONTEXT_SERVICE, "ssl-context");
        addHandler(new TestInvokeHttpCommon.GetOrHeadHandler());
        runner.setProperty(PROP_URL, ((TestInvokeHttpCommon.url) + "/status/200"));
        TestInvokeHttpCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE).get(0);
        bundle1.assertContentEquals("/status/200".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
    }

    // Currently InvokeHttp does not support Proxy via Https
    @Test
    public void testProxy() throws Exception {
        addHandler(new TestInvokeHTTP.MyProxyHandler());
        URL proxyURL = new URL(TestInvokeHttpCommon.url);
        runner.setVariable("proxy.host", proxyURL.getHost());
        runner.setVariable("proxy.port", String.valueOf(proxyURL.getPort()));
        runner.setVariable("proxy.username", "username");
        runner.setVariable("proxy.password", "password");
        runner.setProperty(PROP_URL, "http://nifi.apache.org/");// just a dummy URL no connection goes out

        runner.setProperty(PROP_PROXY_HOST, "${proxy.host}");
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            // Expect assertion error when proxy port isn't set but host is.
        }
        runner.setProperty(PROP_PROXY_PORT, "${proxy.port}");
        runner.setProperty(PROP_PROXY_USER, "${proxy.username}");
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            // Expect assertion error when proxy password isn't set but host is.
        }
        runner.setProperty(PROP_PROXY_PASSWORD, "${proxy.password}");
        TestInvokeHttpCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE).get(0);
        bundle1.assertContentEquals("http://nifi.apache.org/".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
    }

    @Test
    public void testFailingHttpRequest() throws Exception {
        runner = TestRunners.newTestRunner(InvokeHTTP.class);
        // Remember: we expect that connecting to the following URL should raise a Java exception
        runner.setProperty(PROP_URL, "http://127.0.0.1:0");
        TestInvokeHttpCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 0);
        runner.assertTransferCount(REL_RESPONSE, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertPenalizeCount(1);
        // expected in request java.exception
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        bundle.assertAttributeEquals(EXCEPTION_CLASS, "java.lang.IllegalArgumentException");
    }

    public static class MyProxyHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if ("Get".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(200);
                String proxyPath = baseRequest.getHttpURI().toString();
                response.setContentLength(proxyPath.length());
                response.setContentType("text/plain");
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(proxyPath);
                    writer.flush();
                }
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(0);
            }
        }
    }

    @Test
    public void testOnPropertyModified() throws Exception {
        final InvokeHTTP processor = new InvokeHTTP();
        final Field regexAttributesToSendField = InvokeHTTP.class.getDeclaredField("regexAttributesToSend");
        regexAttributesToSendField.setAccessible(true);
        Assert.assertNull(regexAttributesToSendField.get(processor));
        // Set Attributes to Send.
        processor.onPropertyModified(PROP_ATTRIBUTES_TO_SEND, null, "uuid");
        Assert.assertNotNull(regexAttributesToSendField.get(processor));
        // Null clear Attributes to Send. NIFI-1125: Throws NullPointerException.
        processor.onPropertyModified(PROP_ATTRIBUTES_TO_SEND, "uuid", null);
        Assert.assertNull(regexAttributesToSendField.get(processor));
        // Set Attributes to Send.
        processor.onPropertyModified(PROP_ATTRIBUTES_TO_SEND, null, "uuid");
        Assert.assertNotNull(regexAttributesToSendField.get(processor));
        // Clear Attributes to Send with empty string.
        processor.onPropertyModified(PROP_ATTRIBUTES_TO_SEND, "uuid", "");
        Assert.assertNull(regexAttributesToSendField.get(processor));
    }
}

