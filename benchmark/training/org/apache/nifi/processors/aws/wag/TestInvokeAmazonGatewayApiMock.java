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
import InvokeAWSGatewayApi.PROP_ATTRIBUTES_TO_SEND;
import InvokeAWSGatewayApi.PROP_QUERY_PARAMS;
import InvokeAWSGatewayApi.REL_FAILURE;
import InvokeAWSGatewayApi.REL_NO_RETRY;
import InvokeAWSGatewayApi.REL_RESPONSE;
import InvokeAWSGatewayApi.REL_RETRY;
import InvokeAWSGatewayApi.REL_SUCCESS_REQ;
import InvokeAWSGatewayApi.RESOURCE_NAME_ATTR;
import InvokeAWSGatewayApi.STATUS_CODE;
import InvokeAWSGatewayApi.TRANSACTION_ID;
import com.amazonaws.http.apache.client.impl.SdkHttpClient;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestInvokeAmazonGatewayApiMock {
    private TestRunner runner = null;

    private InvokeAWSGatewayApi mockGetApi = null;

    private SdkHttpClient mockSdkClient = null;

    @Test
    public void testGetApiSimple() throws Exception {
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("test payload".getBytes()));
        resp.setEntity(entity);
        Mockito.doReturn(resp).when(mockSdkClient).execute(ArgumentMatchers.any(HttpUriRequest.class), ArgumentMatchers.any(HttpContext.class));
        // execute
        runner.assertValid();
        runner.run(1);
        // check
        Mockito.verify(mockSdkClient, Mockito.times(1)).execute(ArgumentMatchers.argThat(new RequestMatcher<HttpUriRequest>(( x) -> {
            return (((x.getMethod().equals("GET")) && (x.getFirstHeader("x-api-key").getValue().equals("abcd"))) && (x.getFirstHeader("Authorization").getValue().startsWith("AWS4"))) && (x.getURI().toString().equals("https://foobar.execute-api.us-east-1.amazonaws.com/TEST"));
        })), ArgumentMatchers.any(HttpContext.class));
        runner.assertTransferCount(REL_SUCCESS_REQ, 0);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
        final MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(STATUS_CODE, "200");
        ff0.assertContentEquals("test payload");
        ff0.assertAttributeExists(TRANSACTION_ID);
        ff0.assertAttributeEquals(RESOURCE_NAME_ATTR, "/TEST");
    }

    @Test
    public void testSendAttributes() throws Exception {
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("test payload".getBytes()));
        resp.setEntity(entity);
        Mockito.doReturn(resp).when(mockSdkClient).execute(ArgumentMatchers.any(HttpUriRequest.class), ArgumentMatchers.any(HttpContext.class));
        // add dynamic property
        runner.setProperty("dynamicHeader", "yes!");
        // set the regex
        runner.setProperty(PROP_ATTRIBUTES_TO_SEND, "F.*");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/plain-text");
        attributes.put("Foo", "Bar");
        runner.enqueue("Hello".getBytes("UTF-8"), attributes);
        // execute
        runner.assertValid();
        runner.run(1);
        Mockito.verify(mockSdkClient, Mockito.times(1)).execute(ArgumentMatchers.argThat(new RequestMatcher<HttpUriRequest>(( x) -> {
            return (((((x.getMethod().equals("GET")) && (x.getFirstHeader("x-api-key").getValue().equals("abcd"))) && (x.getFirstHeader("Authorization").getValue().startsWith("AWS4"))) && (x.getFirstHeader("dynamicHeader").getValue().equals("yes!"))) && (x.getFirstHeader("Foo").getValue().equals("Bar"))) && (x.getURI().toString().equals("https://foobar.execute-api.us-east-1.amazonaws.com/TEST"));
        })), ArgumentMatchers.any(HttpContext.class));
        // check
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
        final MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(STATUS_CODE, "200");
        ff0.assertContentEquals("test payload");
        ff0.assertAttributeExists(TRANSACTION_ID);
        ff0.assertAttributeEquals(RESOURCE_NAME_ATTR, "/TEST");
    }

    @Test
    public void testSendQueryParams() throws Exception {
        HttpResponse resp = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("test payload".getBytes()));
        resp.setEntity(entity);
        Mockito.doReturn(resp).when(mockSdkClient).execute(ArgumentMatchers.any(HttpUriRequest.class), ArgumentMatchers.any(HttpContext.class));
        // add dynamic property
        runner.setProperty("dynamicHeader", "yes!");
        runner.setProperty(PROP_QUERY_PARAMS, "apples=oranges&dogs=cats");
        // set the regex
        runner.setProperty(PROP_ATTRIBUTES_TO_SEND, "F.*");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/plain-text");
        attributes.put("Foo", "Bar");
        runner.enqueue("Hello".getBytes("UTF-8"), attributes);
        // execute
        runner.assertValid();
        runner.run(1);
        Mockito.verify(mockSdkClient, Mockito.times(1)).execute(ArgumentMatchers.argThat(new RequestMatcher<HttpUriRequest>(( x) -> {
            return (((((x.getMethod().equals("GET")) && (x.getFirstHeader("x-api-key").getValue().equals("abcd"))) && (x.getFirstHeader("Authorization").getValue().startsWith("AWS4"))) && (x.getFirstHeader("dynamicHeader").getValue().equals("yes!"))) && (x.getFirstHeader("Foo").getValue().equals("Bar"))) && (x.getURI().toString().equals("https://foobar.execute-api.us-east-1.amazonaws.com/TEST?dogs=cats&apples=oranges"));
        })), ArgumentMatchers.any(HttpContext.class));
        // check
        runner.assertTransferCount(REL_SUCCESS_REQ, 1);
        runner.assertTransferCount(REL_RESPONSE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_NO_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_RESPONSE);
        final MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(STATUS_CODE, "200");
        ff0.assertContentEquals("test payload");
        ff0.assertAttributeExists(TRANSACTION_ID);
        ff0.assertAttributeEquals(RESOURCE_NAME_ATTR, "/TEST");
    }
}

