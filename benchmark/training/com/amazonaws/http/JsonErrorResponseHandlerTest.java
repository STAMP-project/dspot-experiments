/**
 * Copyright 2015-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http;


import ErrorType.Client;
import ErrorType.Service;
import HttpResponseHandler.X_AMZN_REQUEST_ID_HEADER;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.internal.http.JsonErrorCodeParser;
import com.amazonaws.internal.http.JsonErrorMessageParser;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.util.StringInputStream;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JsonErrorResponseHandlerTest {
    private static final String SERVICE_NAME = "someService";

    private static final String ERROR_CODE = "someErrorCode";

    private JsonErrorResponseHandler responseHandler;

    private HttpResponse httpResponse;

    @Mock
    private JsonErrorUnmarshaller unmarshaller;

    @Mock
    private JsonErrorCodeParser errorCodeParser;

    @Test
    public void handle_NoUnmarshallersAdded_ReturnsGenericAmazonServiceException() throws Exception {
        responseHandler = new JsonErrorResponseHandler(new ArrayList<JsonErrorUnmarshaller>(), new JsonErrorCodeParser(), JsonErrorMessageParser.DEFAULT_ERROR_MESSAGE_PARSER, new JsonFactory());
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertNotNull(ase);
    }

    @Test
    public void handle_NoMatchingUnmarshallers_ReturnsGenericAmazonServiceException() throws Exception {
        expectUnmarshallerDoesNotMatch();
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertNotNull(ase);
    }

    @Test
    public void handle_NullContent_ReturnsGenericAmazonServiceException() throws Exception {
        httpResponse.setStatusCode(500);
        httpResponse.setContent(null);
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        // We assert these common properties are set again to make sure that code path is exercised
        // for unknown AmazonServiceExceptions as well
        Assert.assertEquals(JsonErrorResponseHandlerTest.ERROR_CODE, ase.getErrorCode());
        Assert.assertEquals(500, ase.getStatusCode());
        Assert.assertEquals(JsonErrorResponseHandlerTest.SERVICE_NAME, ase.getServiceName());
        Assert.assertEquals(Service, ase.getErrorType());
    }

    @Test
    public void handle_EmptyContent_ReturnsGenericAmazonServiceException() throws Exception {
        httpResponse.setStatusCode(500);
        httpResponse.setContent(new StringInputStream(""));
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertNotNull(ase);
    }

    @Test
    public void handle_UnmarshallerReturnsNull_ReturnsGenericAmazonServiceException() throws Exception {
        expectUnmarshallerMatches();
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertNotNull(ase);
        Assert.assertEquals(JsonErrorResponseHandlerTest.ERROR_CODE, ase.getErrorCode());
    }

    @Test
    public void handle_UnmarshallerThrowsException_ReturnsGenericAmazonServiceException() throws Exception {
        expectUnmarshallerMatches();
        Mockito.when(unmarshaller.unmarshall(((JsonNode) (ArgumentMatchers.anyObject())))).thenThrow(new RuntimeException());
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertNotNull(ase);
        Assert.assertEquals(JsonErrorResponseHandlerTest.ERROR_CODE, ase.getErrorCode());
    }

    @Test
    public void handle_UnmarshallerReturnsException_ClientErrorType() throws Exception {
        httpResponse.setStatusCode(400);
        expectUnmarshallerMatches();
        Mockito.when(unmarshaller.unmarshall(((JsonNode) (ArgumentMatchers.anyObject())))).thenReturn(new JsonErrorResponseHandlerTest.CustomException("error"));
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertEquals(JsonErrorResponseHandlerTest.ERROR_CODE, ase.getErrorCode());
        Assert.assertEquals(400, ase.getStatusCode());
        Assert.assertEquals(JsonErrorResponseHandlerTest.SERVICE_NAME, ase.getServiceName());
        Assert.assertEquals(Client, ase.getErrorType());
    }

    @Test
    public void handle_UnmarshallerReturnsException_ServiceErrorType() throws Exception {
        httpResponse.setStatusCode(500);
        expectUnmarshallerMatches();
        Mockito.when(unmarshaller.unmarshall(((JsonNode) (ArgumentMatchers.anyObject())))).thenReturn(new JsonErrorResponseHandlerTest.CustomException("error"));
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertEquals(Service, ase.getErrorType());
    }

    @Test
    public void handle_UnmarshallerReturnsException_WithRequestId() throws Exception {
        httpResponse.setStatusCode(500);
        httpResponse.addHeader(X_AMZN_REQUEST_ID_HEADER, "1234");
        expectUnmarshallerMatches();
        Mockito.when(unmarshaller.unmarshall(((JsonNode) (ArgumentMatchers.anyObject())))).thenReturn(new JsonErrorResponseHandlerTest.CustomException("error"));
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertEquals("1234", ase.getRequestId());
    }

    /**
     * Headers are case insensitive so the request id should still be parsed in this test
     */
    @Test
    public void handle_UnmarshallerReturnsException_WithCaseInsensitiveRequestId() throws Exception {
        httpResponse.setStatusCode(500);
        httpResponse.addHeader(StringUtils.upperCase(X_AMZN_REQUEST_ID_HEADER), "1234");
        expectUnmarshallerMatches();
        Mockito.when(unmarshaller.unmarshall(((JsonNode) (ArgumentMatchers.anyObject())))).thenReturn(new JsonErrorResponseHandlerTest.CustomException("error"));
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertEquals("1234", ase.getRequestId());
    }

    /**
     * All headers (Including ones that populate other fields like request id) should be dumped into
     * the header map.
     */
    @Test
    public void handle_AllHeaders_DumpedIntoHeaderMap() throws Exception {
        httpResponse.setStatusCode(500);
        httpResponse.addHeader("FooHeader", "FooValue");
        httpResponse.addHeader(X_AMZN_REQUEST_ID_HEADER, "1234");
        expectUnmarshallerMatches();
        Mockito.when(unmarshaller.unmarshall(((JsonNode) (ArgumentMatchers.anyObject())))).thenReturn(new JsonErrorResponseHandlerTest.CustomException("error"));
        AmazonServiceException ase = responseHandler.handle(httpResponse);
        Assert.assertThat(ase.getHttpHeaders(), Matchers.hasEntry("FooHeader", "FooValue"));
        Assert.assertThat(ase.getHttpHeaders(), Matchers.hasEntry(X_AMZN_REQUEST_ID_HEADER, "1234"));
    }

    private static class CustomException extends AmazonServiceException {
        private static final long serialVersionUID = 1305027296023640779L;

        public CustomException(String errorMessage) {
            super(errorMessage);
        }
    }
}

