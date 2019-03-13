/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http;


import ExecutionContext.Builder;
import HandlerContextKey.AWS_CREDENTIALS;
import HttpMethodName.POST;
import HttpMethodName.PUT;
import com.amazonaws.AbortedException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.http.apache.request.impl.ApacheHttpRequestFactory;
import com.amazonaws.http.request.HttpRequestFactory;
import com.amazonaws.http.settings.HttpClientSettings;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AmazonHttpClientTest {
    private final String SERVER_NAME = "testsvc";

    private final String URI_NAME = "http://testsvc.region.amazonaws.com";

    private ConnectionManagerAwareHttpClient httpClient;

    private AmazonHttpClient client;

    private RequestHandler2 mockHandler;

    private List<RequestHandler2> requestHandlers = new ArrayList<RequestHandler2>();

    @Test
    public void testRetryIOExceptionFromExecute() throws IOException {
        IOException exception = new IOException("BOOM");
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.getConnectionManager()).andReturn(null).anyTimes();
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andThrow(exception).times(4);
        EasyMock.replay(httpClient);
        ExecutionContext context = new ExecutionContext();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>("testsvc");
        request.setEndpoint(URI.create("http://testsvc.region.amazonaws.com"));
        request.setContent(new ByteArrayInputStream(new byte[0]));
        try {
            client.requestExecutionBuilder().request(request).executionContext(context).execute();
            Assert.fail("No exception when request repeatedly fails!");
        } catch (AmazonClientException e) {
            Assert.assertSame(exception, e.getCause());
        }
        // Verify that we called execute 4 times.
        EasyMock.verify(httpClient);
    }

    @Test
    public void testRetryIOExceptionFromHandler() throws Exception {
        final IOException exception = new IOException("BOOM");
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = EasyMock.createMock(HttpResponseHandler.class);
        EasyMock.expect(handler.needsConnectionLeftOpen()).andReturn(false).anyTimes();
        EasyMock.expect(handler.handle(EasyMock.<HttpResponse>anyObject())).andThrow(exception).times(4);
        EasyMock.replay(handler);
        BasicHttpResponse response = createBasicHttpResponse();
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.getConnectionManager()).andReturn(null).anyTimes();
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andReturn(response).times(4);
        EasyMock.replay(httpClient);
        ExecutionContext context = new ExecutionContext();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>(null, "testsvc");
        request.setEndpoint(URI.create("http://testsvc.region.amazonaws.com"));
        request.setContent(new ByteArrayInputStream(new byte[0]));
        try {
            client.requestExecutionBuilder().request(request).executionContext(context).execute(handler);
            Assert.fail("No exception when request repeatedly fails!");
        } catch (AmazonClientException e) {
            Assert.assertSame(exception, e.getCause());
        }
        // Verify that we called execute 4 times.
        EasyMock.verify(httpClient);
    }

    @Test
    public void testUseExpectContinueTrue() throws IOException {
        Request<?> request = mockRequest(SERVER_NAME, PUT, URI_NAME, true);
        ClientConfiguration clientConfiguration = new ClientConfiguration().withUseExpectContinue(true);
        HttpRequestFactory<HttpRequestBase> httpRequestFactory = new ApacheHttpRequestFactory();
        HttpRequestBase httpRequest = httpRequestFactory.create(request, HttpClientSettings.adapt(clientConfiguration));
        Assert.assertNotNull(httpRequest);
        Assert.assertTrue(getConfig().isExpectContinueEnabled());
    }

    @Test
    public void testUseExpectContinueFalse() throws IOException {
        Request<?> request = mockRequest(SERVER_NAME, PUT, URI_NAME, true);
        ClientConfiguration clientConfiguration = new ClientConfiguration().withUseExpectContinue(false);
        HttpRequestFactory<HttpRequestBase> httpRequestFactory = new ApacheHttpRequestFactory();
        HttpRequestBase httpRequest = httpRequestFactory.create(request, HttpClientSettings.adapt(clientConfiguration));
        Assert.assertNotNull(httpRequest);
        Assert.assertFalse(getConfig().isExpectContinueEnabled());
    }

    @Test
    public void testPutRetryNoCL() throws Exception {
        Request<?> request = mockRequest(SERVER_NAME, PUT, URI_NAME, false);
        testRetries(request, 100);
    }

    @Test
    public void testPostRetryNoCL() throws Exception {
        Request<?> request = mockRequest(SERVER_NAME, POST, URI_NAME, false);
        testRetries(request, 100);
    }

    @Test
    public void testPutRetryCL() throws Exception {
        Request<?> request = mockRequest(SERVER_NAME, PUT, URI_NAME, true);
        testRetries(request, 100);
    }

    @Test
    public void testPostRetryCL() throws Exception {
        Request<?> request = mockRequest(SERVER_NAME, POST, URI_NAME, true);
        testRetries(request, 100);
    }

    @Test
    public void testUserAgentPrefixAndSuffixAreAdded() throws Exception {
        String prefix = "somePrefix";
        String suffix = "someSuffix";
        Request<?> request = mockRequest(SERVER_NAME, PUT, URI_NAME, true);
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();
        EasyMock.replay(handler);
        ClientConfiguration config = new ClientConfiguration().withUserAgentPrefix(prefix).withUserAgentSuffix(suffix);
        Capture<HttpRequestBase> capturedRequest = new Capture<HttpRequestBase>();
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.execute(EasyMock.capture(capturedRequest), EasyMock.<HttpContext>anyObject())).andReturn(createBasicHttpResponse()).once();
        EasyMock.replay(httpClient);
        AmazonHttpClient client = new AmazonHttpClient(config, httpClient, null);
        client.requestExecutionBuilder().request(request).execute(handler);
        String userAgent = capturedRequest.getValue().getFirstHeader("User-Agent").getValue();
        Assert.assertTrue(userAgent.startsWith(prefix));
        Assert.assertTrue(userAgent.endsWith(suffix));
    }

    @Test
    public void testCredentialsSetInRequestContext() throws Exception {
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.execute(EasyMock.<HttpRequestBase>anyObject(), EasyMock.<HttpContext>anyObject())).andReturn(createBasicHttpResponse()).once();
        EasyMock.replay(httpClient);
        AmazonHttpClient client = new AmazonHttpClient(new ClientConfiguration(), httpClient, null);
        final BasicAWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AWSCredentialsProvider credentialsProvider = EasyMock.createMock(AWSCredentialsProvider.class);
        EasyMock.expect(credentialsProvider.getCredentials()).andReturn(credentials).anyTimes();
        EasyMock.replay(credentialsProvider);
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setCredentialsProvider(credentialsProvider);
        Request<?> request = mockRequest(SERVER_NAME, PUT, URI_NAME, true);
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = createStubResponseHandler();
        EasyMock.replay(handler);
        client.execute(request, handler, null, executionContext);
        Assert.assertEquals(credentials, request.getHandlerContext(AWS_CREDENTIALS));
    }

    enum MockRequestOutcome {

        Success,
        FailureWithAwsClientException,
        Failure;}

    @Test
    public void testHandlerCallbacksOnFirstAttemptSuccess() throws IOException {
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andReturn(createBasicHttpResponse()).once();
        EasyMock.replay(httpClient);
        SetupMockRequestHandler2(mockHandler, 1, AmazonHttpClientTest.MockRequestOutcome.Success);
        ExecutionContext.Builder contextBuilder = ExecutionContext.builder();
        contextBuilder.withRequestHandler2s(requestHandlers);
        ExecutionContext context = contextBuilder.build();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>(SERVER_NAME);
        request.setEndpoint(URI.create(URI_NAME));
        request.setContent(new ByteArrayInputStream(new byte[0]));
        try {
            client.requestExecutionBuilder().request(request).executionContext(context).execute();
        } catch (Exception e) {
        }
        // Verify that we called handler callbacks in proper sequence
        EasyMock.verify(mockHandler);
    }

    @Test
    public void testHandlerCallbacksOnRepeatedIOExceptions() throws IOException {
        IOException exception = new IOException("BOOM");
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.getConnectionManager()).andReturn(null).anyTimes();
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andThrow(exception).times(4);
        EasyMock.replay(httpClient);
        SetupMockRequestHandler2(mockHandler, 4, AmazonHttpClientTest.MockRequestOutcome.FailureWithAwsClientException);
        ExecutionContext.Builder contextBuilder = ExecutionContext.builder();
        contextBuilder.withRequestHandler2s(requestHandlers);
        ExecutionContext context = contextBuilder.build();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>(SERVER_NAME);
        request.setEndpoint(URI.create(URI_NAME));
        request.setContent(new ByteArrayInputStream(new byte[0]));
        try {
            client.requestExecutionBuilder().request(request).executionContext(context).execute();
        } catch (Exception e) {
        }
        // Verify that we called handler callbacks in proper sequence
        EasyMock.verify(mockHandler);
    }

    @Test
    public void testHandlerCallbacksOnRuntimeException() throws IOException {
        Exception exception = new NullPointerException("BOOM");
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.getConnectionManager()).andReturn(null).anyTimes();
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andThrow(exception).times(1);
        EasyMock.replay(httpClient);
        SetupMockRequestHandler2(mockHandler, 1, AmazonHttpClientTest.MockRequestOutcome.Failure);
        ExecutionContext.Builder contextBuilder = ExecutionContext.builder();
        contextBuilder.withRequestHandler2s(requestHandlers);
        ExecutionContext context = contextBuilder.build();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>(SERVER_NAME);
        request.setEndpoint(URI.create(URI_NAME));
        request.setContent(new ByteArrayInputStream(new byte[0]));
        try {
            client.requestExecutionBuilder().request(request).executionContext(context).execute();
        } catch (Exception e) {
        }
        // Verify that we called handler callbacks in proper sequence
        EasyMock.verify(mockHandler);
    }

    @Test
    public void testHandlerCallbacksOnFailFailSuccess() throws IOException {
        Exception ioException = new IOException("SomethingBad");
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.getConnectionManager()).andReturn(null).anyTimes();
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andThrow(ioException).times(2);
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andReturn(createBasicHttpResponse()).once();
        EasyMock.replay(httpClient);
        SetupMockRequestHandler2(mockHandler, 3, AmazonHttpClientTest.MockRequestOutcome.Success);
        ExecutionContext.Builder contextBuilder = ExecutionContext.builder();
        contextBuilder.withRequestHandler2s(requestHandlers);
        ExecutionContext context = contextBuilder.build();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>(SERVER_NAME);
        request.setEndpoint(URI.create(URI_NAME));
        request.setContent(new ByteArrayInputStream(new byte[0]));
        try {
            client.requestExecutionBuilder().request(request).executionContext(context).execute();
        } catch (Exception e) {
        }
        // Verify that we called handler callbacks in proper sequence
        EasyMock.verify(mockHandler);
    }

    @Test
    public void testReturnsResponseWhenRequestAbortFails() throws Exception {
        final RuntimeException expectedThrown = new AbortedException("request was interrupted");
        HttpResponseHandler<AmazonWebServiceResponse<Object>> handler = EasyMock.createMock(HttpResponseHandler.class);
        EasyMock.expect(handler.needsConnectionLeftOpen()).andReturn(true).anyTimes();
        AmazonWebServiceResponse response = EasyMock.createMock(AmazonWebServiceResponse.class);
        EasyMock.expect(handler.handle(EasyMock.isA(HttpResponse.class))).andReturn(response);
        EasyMock.replay(handler);
        EasyMock.reset(httpClient);
        EasyMock.expect(httpClient.getConnectionManager()).andReturn(null).anyTimes();
        InputStream responseStream = EasyMock.createMock(InputStream.class);
        responseStream.close();
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(responseStream);
        BasicHttpResponse httpResponse = createBasicHttpResponse(responseStream);
        EasyMock.expect(httpClient.execute(EasyMock.<HttpUriRequest>anyObject(), EasyMock.<HttpContext>anyObject())).andReturn(httpResponse).times(1);
        EasyMock.replay(httpClient);
        InputStream requestInputStream = new ByteArrayInputStream("foo".getBytes()) {
            @Override
            public void close() throws IOException {
                throw expectedThrown;
            }
        };
        ExecutionContext context = new ExecutionContext();
        Request<?> request = new com.amazonaws.DefaultRequest<Object>(null, "testsvc");
        request.setEndpoint(URI.create("http://testsvc.region.amazonaws.com"));
        request.setContent(requestInputStream);
        Response<AmazonWebServiceResponse<Object>> awsResponse = client.requestExecutionBuilder().request(request).executionContext(context).execute(handler);
        awsResponse.getHttpResponse().getContent().close();
        EasyMock.verify(httpClient);
        // verify that the response stream was closed
        EasyMock.verify(responseStream);
    }
}

