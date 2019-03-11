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
package org.apache.nifi.processors.aws.wag;


import CoreAttributes.MIME_TYPE;
import InvokeAWSGatewayApi.PROP_ADD_HEADERS_TO_REQUEST;
import InvokeAWSGatewayApi.PROP_ATTRIBUTES_TO_SEND;
import InvokeAWSGatewayApi.PROP_AWS_GATEWAY_API_ENDPOINT;
import InvokeAWSGatewayApi.PROP_CONTENT_TYPE;
import InvokeAWSGatewayApi.PROP_METHOD;
import InvokeAWSGatewayApi.PROP_OUTPUT_RESPONSE_REGARDLESS;
import InvokeAWSGatewayApi.PROP_PENALIZE_NO_RETRY;
import InvokeAWSGatewayApi.PROP_PUT_ATTRIBUTE_MAX_LENGTH;
import InvokeAWSGatewayApi.PROP_PUT_OUTPUT_IN_ATTRIBUTE;
import InvokeAWSGatewayApi.PROP_RESOURCE_NAME;
import InvokeAWSGatewayApi.PROP_SEND_BODY;
import InvokeAWSGatewayApi.PROXY_HOST;
import InvokeAWSGatewayApi.PROXY_HOST_PORT;
import InvokeAWSGatewayApi.PROXY_PASSWORD;
import InvokeAWSGatewayApi.PROXY_USERNAME;
import InvokeAWSGatewayApi.REL_FAILURE;
import InvokeAWSGatewayApi.REL_FAILURE_NAME;
import InvokeAWSGatewayApi.REL_NO_RETRY;
import InvokeAWSGatewayApi.REL_NO_RETRY_NAME;
import InvokeAWSGatewayApi.REL_RESPONSE;
import InvokeAWSGatewayApi.REL_RESPONSE_NAME;
import InvokeAWSGatewayApi.REL_RETRY;
import InvokeAWSGatewayApi.REL_RETRY_NAME;
import InvokeAWSGatewayApi.REL_SUCCESS_REQ;
import InvokeAWSGatewayApi.REL_SUCCESS_REQ_NAME;
import InvokeAWSGatewayApi.RESPONSE_BODY;
import InvokeAWSGatewayApi.STATUS_CODE;
import InvokeAWSGatewayApi.STATUS_MESSAGE;
import InvokeAWSGatewayApi.TIMEOUT;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceEventType;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.web.util.TestServer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;

import static InvokeAWSGatewayApi.DEFAULT_CONTENT_TYPE;


public abstract class TestInvokeAWSGatewayApiCommon {
    public static TestServer server;

    public static String url;

    public TestRunner runner;

    @Test
    public void test200() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        // verify that call works with or without flowfile being sent for GET
        // there should only be 1 REQ, but 2 RESPONSE
        runner.run();
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 2);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response of each message
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("{\"status\":\"200\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
        final MockFlowFile bundle2 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(1);
        bundle1.assertContentEquals("{\"status\":\"200\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
        final List<ProvenanceEventRecord> provEvents = runner.getProvenanceEvents();
        Assert.assertEquals(3, provEvents.size());
        boolean forkEvent = false;
        boolean fetchEvent = false;
        boolean recieveEvent = false;
        for (final ProvenanceEventRecord event : provEvents) {
            if ((event.getEventType()) == (ProvenanceEventType.FORK)) {
                forkEvent = true;
            } else
                if ((event.getEventType()) == (ProvenanceEventType.FETCH)) {
                    fetchEvent = true;
                } else
                    if ((event.getEventType()) == (ProvenanceEventType.RECEIVE)) {
                        recieveEvent = true;
                    }


        }
        Assert.assertTrue(forkEvent);
        Assert.assertTrue(fetchEvent);
        Assert.assertTrue(recieveEvent);
    }

    @Test
    public void testOutputResponseRegardless() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler(true));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "404");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Not Found");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("{ \"error\": \"oops\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "404");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "Not Found");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
    }

    @Test
    public void testOutputResponseRegardlessWithOutputInAttribute() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler(true));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
        runner.setProperty(PROP_PUT_OUTPUT_IN_ATTRIBUTE, "outputBody");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "404");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Not Found");
        bundle.assertAttributeEquals("outputBody", "{ \"error\": \"oops\"}");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("{ \"error\": \"oops\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "404");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "Not Found");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
    }

    @Test
    public void testOutputResponseSetMimeTypeToResponseContentType() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        String statusUrl = "/status/200";
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
        runner.setProperty(PROP_PUT_OUTPUT_IN_ATTRIBUTE, "outputBody");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("outputBody", "{\"status\":\"200\"}");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("{\"status\":\"200\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
        bundle1.assertAttributeEquals("mime.type", "application/json");
    }

    @Test
    public void testOutputResponseRegardlessWithOutputInAttributeLarge() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetLargeHandler(true));
        String statusUrl = "/status/200";
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.setProperty(PROP_OUTPUT_RESPONSE_REGARDLESS, "true");
        runner.setProperty(PROP_PUT_OUTPUT_IN_ATTRIBUTE, "outputBody");
        runner.setProperty(PROP_PUT_ATTRIBUTE_MAX_LENGTH, "11");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "404");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Not Found");
        bundle.assertAttributeEquals("outputBody", "{\"name\":\"Lo");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals(("{\"name\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " + (("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor " + "in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, ") + "sunt in culpa qui officia deserunt mollit anim id est laborum.\"}")));
        bundle1.assertAttributeEquals(STATUS_CODE, "404");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "Not Found");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
    }

    // NOTE : Amazon does not support multiple headers with the same name!!!
    @Test
    public void testMultipleSameHeaders() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetMultipleHeaderHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("/status/200".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        // amazon does not support headers with the same name, we'll only get 2 here
        // this is in the amazon layer, we only get Map<String,String> for headers so the 1 has been stripped
        // already
        bundle1.assertAttributeEquals("double", "2");
        bundle1.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
    }

    @Test
    public void testPutResponseHeadersInRequest() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetMultipleHeaderHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_ADD_HEADERS_TO_REQUEST, "true");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+all attributes from response)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        bundle.assertAttributeEquals("double", "2");
        bundle.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("/status/200".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("double", "2");
        bundle1.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
    }

    @Test
    public void testToRequestAttribute() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_PUT_OUTPUT_IN_ATTRIBUTE, "outputBody");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code, status.message and body of response in attribute
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals("outputBody", "{\"status\":\"200\"}");
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void testNoInput() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setIncomingConnection(false);
        runner.setNonLoopConnection(false);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("{\"status\":\"200\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
    }

    @Test
    public void testNoInputWithAttributes() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_ATTRIBUTES_TO_SEND, "myAttribute");
        runner.setIncomingConnection(false);
        runner.setNonLoopConnection(false);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("{\"status\":\"200\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Content-Type", "application/json");
    }

    @Test
    public void testNoInputSendToAttribute() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_PUT_OUTPUT_IN_ATTRIBUTE, "outputBody");
        runner.setIncomingConnection(false);
        runner.setNonLoopConnection(false);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        // expected in request
        // status code, status message, no ff content
        // server response message body into attribute of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle1.assertContentEquals("".getBytes("UTF-8"));
        bundle1.assertAttributeEquals("outputBody", "{\"status\":\"200\"}");
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
    }

    @Test
    public void test500() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/500");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 1);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(1);
        // expected in response
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_RETRY_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        bundle.assertAttributeEquals(STATUS_CODE, "500");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Internal Server Error");
        bundle.assertAttributeEquals(RESPONSE_BODY, "{\"status\":\"500\"}");
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void test300() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/302");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in response
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        bundle.assertAttributeEquals(STATUS_CODE, "302");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Moved Temporarily");
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void test304() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/304");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        // assertTrue(server.jetty.isRunning());
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in response
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        bundle.assertAttributeEquals(STATUS_CODE, "304");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Not Modified");
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void test400() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/400");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in response
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        bundle.assertAttributeEquals(STATUS_CODE, "400");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Bad Request");
        bundle.assertAttributeEquals(RESPONSE_BODY, "{\"status\":\"400\"}");
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void test400WithPenalizeNoRetry() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/400");
        runner.setProperty(PROP_PENALIZE_NO_RETRY, "true");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(1);
        // expected in response
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        bundle.assertAttributeEquals(STATUS_CODE, "400");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Bad Request");
        bundle.assertAttributeEquals(RESPONSE_BODY, "{\"status\":\"400\"}");
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void test412() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/412");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 1);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in response
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_NO_RETRY_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        bundle.assertAttributeEquals(STATUS_CODE, "412");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "Precondition Failed");
        bundle.assertAttributeEquals(RESPONSE_BODY, "{\"status\":\"412\"}");
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void testHead() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_METHOD, "HEAD");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        final String actual1 = new String(bundle1.toByteArray(), StandardCharsets.UTF_8);
        final String expected1 = "";
        Assert.assertEquals(expected1, actual1);
    }

    @Test
    public void testPost() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.POST));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "POST");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeNotExists("Content-Type");
        final String actual1 = new String(bundle1.toByteArray(), StandardCharsets.UTF_8);
        final String expected1 = "";
        Assert.assertEquals(expected1, actual1);
    }

    @Test
    public void testPostWithMimeType() throws Exception {
        final String suppliedMimeType = "text/plain";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.POST, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "POST");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), suppliedMimeType);
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
    }

    @Test
    public void testPostWithEmptyELExpression() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.POST, DEFAULT_CONTENT_TYPE));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "POST");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "");
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
    }

    @Test
    public void testPostWithContentTypeProperty() throws Exception {
        final String suppliedMimeType = "text/plain";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.POST, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "POST");
        runner.setProperty(PROP_CONTENT_TYPE, suppliedMimeType);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
    }

    @Test
    public void testPostWithEmptyBodySet() throws Exception {
        final String suppliedMimeType = "";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.POST, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "POST");
        runner.setProperty(PROP_CONTENT_TYPE, suppliedMimeType);
        runner.setProperty(PROP_SEND_BODY, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), suppliedMimeType);
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
    }

    @Test
    public void testPutWithMimeType() throws Exception {
        final String suppliedMimeType = "text/plain";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PUT, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "PUT");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), suppliedMimeType);
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
    }

    @Test
    public void testPutWithEmptyELExpression() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PUT, DEFAULT_CONTENT_TYPE));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "PUT");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "");
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
    }

    @Test
    public void testPutWithContentTypeProperty() throws Exception {
        final String suppliedMimeType = "text/plain";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PUT, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "PUT");
        runner.setProperty(PROP_CONTENT_TYPE, suppliedMimeType);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
    }

    @Test
    public void testPut() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PUT));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/post");
        runner.setProperty(PROP_METHOD, "PUT");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE).get(0);
        bundle1.assertContentEquals("".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeNotExists("Content-Type");
        final String actual1 = new String(bundle1.toByteArray(), StandardCharsets.UTF_8);
        final String expected1 = "";
        Assert.assertEquals(expected1, actual1);
    }

    @Test
    public void testPatch() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PATCH));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/patch");
        runner.setProperty(PROP_METHOD, "PATCH");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE).get(0);
        bundle1.assertContentEquals("".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeNotExists("Content-Type");
        final String actual1 = new String(bundle1.toByteArray(), StandardCharsets.UTF_8);
        final String expected1 = "";
        Assert.assertEquals(expected1, actual1);
    }

    @Test
    public void testPatchWithMimeType() throws Exception {
        final String suppliedMimeType = "text/plain";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PATCH, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/patch");
        runner.setProperty(PROP_METHOD, "PATCH");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), suppliedMimeType);
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
    }

    @Test
    public void testPatchWithEmptyELExpression() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PATCH, DEFAULT_CONTENT_TYPE));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/patch");
        runner.setProperty(PROP_METHOD, "PATCH");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "");
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
    }

    @Test
    public void testPatchWithContentTypeProperty() throws Exception {
        final String suppliedMimeType = "text/plain";
        addHandler(new TestInvokeAWSGatewayApiCommon.MutativeMethodHandler(TestInvokeAWSGatewayApiCommon.MutativeMethod.PATCH, suppliedMimeType));
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/patch");
        runner.setProperty(PROP_METHOD, "PATCH");
        runner.setProperty(PROP_CONTENT_TYPE, suppliedMimeType);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue("Hello".getBytes(), attrs);
        runner.run(1);
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
    }

    @Test
    public void testDelete() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.DeleteHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_METHOD, "DELETE");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE).get(0);
        bundle1.assertContentEquals("".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        final String actual1 = new String(bundle1.toByteArray(), StandardCharsets.UTF_8);
        final String expected1 = "";
        Assert.assertEquals(expected1, actual1);
    }

    @Test
    public void testOptions() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.OptionsHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_METHOD, "OPTIONS");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertPenalizeCount(0);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE).get(0);
        bundle1.assertContentEquals("{\"status\":\"200\"}".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void testSendAttributes() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.AttributesSentHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_ATTRIBUTES_TO_SEND, "F.*");
        runner.setProperty("dynamicHeader", "yes!");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 1);
        runner.assertTransferCount(REL_RESPONSE_NAME, 1);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 0);
        runner.assertPenalizeCount(0);
        // expected in request status.code and status.message
        // original flow file (+attributes)
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_SUCCESS_REQ_NAME).get(0);
        bundle.assertContentEquals("Hello".getBytes("UTF-8"));
        bundle.assertAttributeEquals(STATUS_CODE, "200");
        bundle.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle.assertAttributeEquals("Foo", "Bar");
        // expected in response
        // status code, status message, all headers from server response --> ff attributes
        // server response message body into payload of ff
        final MockFlowFile bundle1 = runner.getFlowFilesForRelationship(REL_RESPONSE_NAME).get(0);
        bundle1.assertContentEquals("Bar".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("dynamicHeader", "yes!");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
    }

    @Test
    public void testReadTimeout() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.ReadTimeoutHandler());
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(TIMEOUT, "5 secs");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 1);
        runner.assertPenalizeCount(1);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_FAILURE_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void testConnectFailBadPort() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        // this is the bad urls
        final String badurlport = "http://localhost:" + 445;
        runner.setProperty(PROP_AWS_GATEWAY_API_ENDPOINT, badurlport);
        runner.setProperty(PROP_RESOURCE_NAME, "/doesnotExist");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 1);
        runner.assertPenalizeCount(1);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_FAILURE_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test
    public void testConnectFailBadHost() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.GetOrHeadHandler());
        setupEndpointAndRegion();
        final String badurlhost = "http://localhOOst:" + 445;
        runner.setProperty(PROP_AWS_GATEWAY_API_ENDPOINT, badurlhost);
        runner.setProperty(PROP_RESOURCE_NAME, "/doesnotExist");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS_REQ_NAME, 0);
        runner.assertTransferCount(REL_RESPONSE_NAME, 0);
        runner.assertTransferCount(REL_RETRY_NAME, 0);
        runner.assertTransferCount(REL_NO_RETRY_NAME, 0);
        runner.assertTransferCount(REL_FAILURE_NAME, 1);
        runner.assertPenalizeCount(1);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_FAILURE_NAME).get(0);
        final String actual = new String(bundle.toByteArray(), StandardCharsets.UTF_8);
        final String expected = "Hello";
        Assert.assertEquals(expected, actual);
        bundle.assertAttributeEquals("Foo", "Bar");
    }

    @Test(expected = AssertionError.class)
    public void testArbitraryRequestFailsValidation() throws Exception {
        setupEndpointAndRegion();
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROP_METHOD, "FETCH");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
        runner.run();
    }

    @Test
    public void testProxy() throws Exception {
        addHandler(new TestInvokeAWSGatewayApiCommon.MyProxyHandler());
        setupEndpointAndRegion();
        URL proxyURL = new URL(TestInvokeAWSGatewayApiCommon.url);
        runner.setVariable("proxy.host", proxyURL.getHost());
        runner.setVariable("proxy.port", String.valueOf(proxyURL.getPort()));
        runner.setVariable("proxy.username", "username");
        runner.setVariable("proxy.password", "password");
        runner.setProperty(PROP_AWS_GATEWAY_API_ENDPOINT, "http://nifi.apache.org/");
        runner.setProperty(PROP_RESOURCE_NAME, "/status/200");
        runner.setProperty(PROXY_HOST, "${proxy.host}");
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            // Expect assertion error when proxy port isn't set but host is.
        }
        runner.setProperty(PROXY_HOST_PORT, "${proxy.port}");
        runner.setProperty(PROXY_USERNAME, "${proxy.username}");
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            // Expect assertion error when proxy password isn't set but host is.
        }
        runner.setProperty(PROXY_PASSWORD, "${proxy.password}");
        TestInvokeAWSGatewayApiCommon.createFlowFiles(runner);
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
        bundle1.assertContentEquals("http://nifi.apache.org/status/200".getBytes("UTF-8"));
        bundle1.assertAttributeEquals(STATUS_CODE, "200");
        bundle1.assertAttributeEquals(STATUS_MESSAGE, "OK");
        bundle1.assertAttributeEquals("Foo", "Bar");
        bundle1.assertAttributeEquals("Content-Type", "text/plain;charset=iso-8859-1");
    }

    protected static class DateHandler extends AbstractHandler {
        private String dateString;

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            dateString = request.getHeader("Date");
            response.setStatus(200);
            response.setContentType("text/plain");
            response.getWriter().println("Way to go!");
        }
    }

    private enum MutativeMethod {

        POST,
        PUT,
        PATCH;}

    public static class MutativeMethodHandler extends AbstractHandler {
        private final TestInvokeAWSGatewayApiCommon.MutativeMethod method;

        private final String expectedContentType;

        private String headerToTrack;

        private String trackedHeaderValue;

        public MutativeMethodHandler(final TestInvokeAWSGatewayApiCommon.MutativeMethod method) {
            this(method, "application/plain-text");
        }

        public MutativeMethodHandler(final TestInvokeAWSGatewayApiCommon.MutativeMethod method, final String expectedContentType) {
            this.method = method;
            this.expectedContentType = expectedContentType;
        }

        private void setHeaderToTrack(String headerToTrack) {
            this.headerToTrack = headerToTrack;
        }

        public String getTrackedHeaderValue() {
            return trackedHeaderValue;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if (method.name().equals(request.getMethod())) {
                if (this.expectedContentType.isEmpty()) {
                    // with nothing set, aws defaults to form encoded
                    Assert.assertEquals(request.getHeader("Content-Type"), "application/x-www-form-urlencoded; charset=UTF-8");
                } else {
                    Assert.assertEquals(this.expectedContentType, request.getHeader("Content-Type"));
                }
                final String body = request.getReader().readLine();
                this.trackedHeaderValue = baseRequest.getHttpFields().get(headerToTrack);
                if (this.expectedContentType.isEmpty()) {
                    Assert.assertNull(body);
                } else {
                    Assert.assertEquals("Hello", body);
                }
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(0);
            }
        }
    }

    public static class GetOrHeadHandler extends AbstractHandler {
        boolean force404 = false;

        public GetOrHeadHandler() {
        }

        public GetOrHeadHandler(boolean force404) {
            this.force404 = force404;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
            response.setStatus(status);
            if ((!(force404)) && ("GET".equalsIgnoreCase(request.getMethod()))) {
                if (status == 304) {
                    // Status code 304 ("Not Modified") must not contain a message body
                    response.getWriter().write("{\"name\":\"test\"}");
                    return;
                } else
                    if (status == 302) {
                        // this will be treated as an error
                        // the target content must be json
                        target = "{\"status\":\"moved\"}";
                    }

                response.setContentType("application/json");
                target = String.format("{\"status\":\"%d\"}", status);
                response.setContentLength(target.length());
                response.setHeader("Cache-Control", "public,max-age=1");
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(target);
                    writer.flush();
                }
            } else
                if ((force404) || (!("HEAD".equalsIgnoreCase(request.getMethod())))) {
                    response.setStatus(404);
                    response.setContentType("application/json");
                    String body = "{ \"error\": \"oops\"}";
                    response.setContentLength(body.length());
                    try (PrintWriter writer = response.getWriter()) {
                        writer.print(body);
                        writer.flush();
                    }
                }

        }
    }

    public static class GetLargeHandler extends AbstractHandler {
        private boolean force404 = false;

        public GetLargeHandler() {
        }

        public GetLargeHandler(boolean force404) {
            this.force404 = force404;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
            response.setStatus(status);
            response.setContentType("text/plain");
            response.setContentLength(target.length());
            if ((!(force404)) && ("GET".equalsIgnoreCase(request.getMethod()))) {
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(target);
                    writer.flush();
                }
            } else {
                response.setStatus(404);
                response.setContentType("application/json");
                // Lorem Ipsum
                String body = "{\"name\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " + (("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor " + "in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, ") + "sunt in culpa qui officia deserunt mollit anim id est laborum.\"}");
                response.setContentLength(body.length());
                response.setContentType("application/json");
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(body);
                    writer.flush();
                }
            }
        }
    }

    public static class GetMultipleHeaderHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
            response.setStatus(status);
            response.setContentType("text/plain");
            response.setContentLength(target.length());
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                response.addHeader("double", "1");
                response.addHeader("double", "2");
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(target);
                    writer.flush();
                }
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(0);
            }
        }
    }

    public static class DeleteHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if ("DELETE".equalsIgnoreCase(request.getMethod())) {
                final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
                response.setStatus(status);
                response.setContentLength(0);
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(0);
            }
        }
    }

    public static class OptionsHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
                response.setStatus(status);
                response.setContentType("application/json");
                target = String.format("{\"status\":\"%d\"}", status);
                response.setContentLength(target.length());
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(target);
                    writer.flush();
                }
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(target.length());
            }
        }
    }

    public static class AttributesSentHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if ("Get".equalsIgnoreCase(request.getMethod())) {
                String headerValue = request.getHeader("Foo");
                response.setHeader("dynamicHeader", request.getHeader("dynamicHeader"));
                final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
                response.setStatus(status);
                response.setContentLength(headerValue.length());
                response.setContentType("text/plain");
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(headerValue);
                    writer.flush();
                }
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(0);
            }
        }
    }

    public static class ReadTimeoutHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if ("Get".equalsIgnoreCase(request.getMethod())) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    return;
                }
                String headerValue = request.getHeader("Foo");
                headerValue = (headerValue == null) ? "" : headerValue;
                final int status = Integer.valueOf(target.substring((("/status".length()) + 1)));
                response.setStatus(status);
                response.setContentLength(headerValue.length());
                response.setContentType("text/plain");
                try (PrintWriter writer = response.getWriter()) {
                    writer.print(headerValue);
                    writer.flush();
                }
            } else {
                response.setStatus(404);
                response.setContentType("text/plain");
                response.setContentLength(0);
            }
        }
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
}

