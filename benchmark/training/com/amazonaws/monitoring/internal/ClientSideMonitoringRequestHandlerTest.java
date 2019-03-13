/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.monitoring.internal;


import SignerConstants.X_AMZ_SECURITY_TOKEN;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.SdkClientException;
import com.amazonaws.handlers.HandlerAfterAttemptContext;
import com.amazonaws.http.timers.client.ClientExecutionTimeoutException;
import com.amazonaws.monitoring.ApiCallAttemptMonitoringEvent;
import com.amazonaws.monitoring.ApiCallMonitoringEvent;
import com.amazonaws.monitoring.ApiMonitoringEvent;
import com.amazonaws.monitoring.MonitoringListener;
import com.amazonaws.util.AWSRequestMetrics;
import com.amazonaws.util.AwsClientSideMonitoringMetrics;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClientSideMonitoringRequestHandlerTest {
    private static final String SERVICE_NAME = "WowService";

    private static final String SERVICE_ID = "Wow";

    private static final String API_NAME = "WowMe";

    private static final String REGION = "us-east-1";

    private static final String SECURITY_TOKENS = "test";

    private static final String CLIENT_ID = "unit-test";

    private ClientSideMonitoringRequestHandler requestHandler;

    @Mock
    private MonitoringListener agentMonitoringListener;

    @Mock
    private MonitoringListener customMonitoringListener;

    @Test
    public void afterAttempt_Succeed_shouldSendApiCallAttemptEvent() {
        HandlerAfterAttemptContext context = getContext(getMetrics(1));
        requestHandler.afterAttempt(context);
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallAttemptMonitoringEvent.class));
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyRequiredEntries(event);
        verifySuccessfulAttemptEvent(event);
    }

    @Test
    public void afterAttempt_AwsException_shouldSendApiCallAttemptEvent() {
        AmazonServiceException exception = new AmazonServiceException("test");
        exception.setStatusCode(400);
        exception.setErrorCode("BadRequest");
        Map<String, String> header = new HashMap<String, String>();
        header.put("x-amzn-RequestId", "test");
        exception.setHttpHeaders(header);
        AWSRequestMetrics metrics = getMetrics(1);
        requestHandler.afterAttempt(getContext(metrics, exception));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallAttemptMonitoringEvent.class));
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyRequiredEntries(event);
        verifyConditionalEntries(event, exception);
    }

    @Test
    public void afterAttempt_AwsExceptionWithOverSizeMessage_ShouldTrim() {
        AmazonServiceException exception = new AmazonServiceException(RandomStringUtils.randomAlphanumeric(1000));
        exception.setStatusCode(400);
        AWSRequestMetrics metrics = getMetrics(1);
        requestHandler.afterAttempt(getContext(metrics, exception));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyExceptionMessageSize(event.getAwsExceptionMessage());
    }

    @Test
    public void afterAttempt_ConnectionError_shouldSendApiCallAttemptEvent() {
        IOException ioException = new IOException("oops");
        AWSRequestMetrics metrics = getMetrics(1);
        requestHandler.afterAttempt(getContext(metrics, ioException));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallAttemptMonitoringEvent.class));
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyRequiredEntries(event);
        verifyConditionalEntries(event, ioException);
    }

    @Test
    public void afterAttempt_sdkExceptionWithOverSizeMessage_ShouldTrim() {
        SdkClientException ioException = new SdkClientException(new IOException(RandomStringUtils.randomAlphanumeric(1200)));
        AWSRequestMetrics metrics = getMetrics(1);
        requestHandler.afterAttempt(getContext(metrics, ioException));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyExceptionSize(event.getSdkException());
        verifyExceptionMessageSize(event.getSdkExceptionMessage());
    }

    @Test
    public void afterAttempt_OtherException_shouldSendApiCallAttemptEvent() {
        RuntimeException runtimeException = new RuntimeException("unexpected");
        AWSRequestMetrics metrics = getMetrics(1);
        requestHandler.afterAttempt(getContext(metrics, runtimeException));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallAttemptMonitoringEvent.class));
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyRequiredEntries(event);
        verifyConditionalEntries(event, runtimeException);
    }

    @Test
    public void afterAttempt_SessionTokenLowerCase_ShouldStillWork() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        request.setAWSRequestMetrics(getMetrics(1));
        request.getHeaders().clear();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-amz-security-token", ClientSideMonitoringRequestHandlerTest.SECURITY_TOKENS);
        request.setHeaders(headers);
        requestHandler.afterAttempt(getContext(request));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallAttemptMonitoringEvent.class));
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyRequiredEntries(event);
        verifySuccessfulAttemptEvent(event);
    }

    @Test
    public void afterAttempt_SessionTokenCapitalized_ShouldStillWork() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        request.setAWSRequestMetrics(getMetrics(1));
        request.getHeaders().clear();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(X_AMZ_SECURITY_TOKEN, ClientSideMonitoringRequestHandlerTest.SECURITY_TOKENS);
        request.setHeaders(headers);
        requestHandler.afterAttempt(getContext(request));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallAttemptMonitoringEvent.class));
        ApiCallAttemptMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyRequiredEntries(event);
        verifySuccessfulAttemptEvent(event);
    }

    @Test
    public void afterAttempt_agentMonitoringListenerThrowException_shouldNotAffectCustomListener() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        Response<?> response = ClientSideMonitoringRequestHandlerTest.getResponse(request);
        Mockito.doThrow(new RuntimeException("Oops")).when(agentMonitoringListener).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
        requestHandler.afterAttempt(getContext(response));
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
    }

    @Test
    public void afterAttempt_overSizeClientId_shouldTrim() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        request.setAWSRequestMetrics(getMetrics(1));
        requestHandler.afterAttempt(getContext(ClientSideMonitoringRequestHandlerTest.getResponse(request)));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        ApiCallAttemptMonitoringEvent monitoringEvent = monitoringEventArgumentCaptor.getValue();
        Assert.assertThat(monitoringEvent.getClientId().length(), Matchers.is(Matchers.lessThanOrEqualTo(255)));
    }

    @Test
    public void afterAttempt_overSizeUserAgent_shouldTrim() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        request.getOriginalRequest().getRequestClientOptions().appendUserAgent(RandomStringUtils.randomAlphanumeric(345));
        request.setAWSRequestMetrics(getMetrics(1));
        requestHandler.afterAttempt(getContext(ClientSideMonitoringRequestHandlerTest.getResponse(request)));
        ArgumentCaptor<ApiCallAttemptMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallAttemptMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        ApiCallAttemptMonitoringEvent monitoringEvent = monitoringEventArgumentCaptor.getValue();
        Assert.assertThat(monitoringEvent.getUserAgent().length(), Matchers.is(Matchers.lessThanOrEqualTo(256)));
    }

    @Test
    public void afterResponse_shouldSendApiCallMonitoringEvent() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        AWSRequestMetrics metrics = getMetrics(3);
        request.setAWSRequestMetrics(metrics);
        requestHandler.afterResponse(request, ClientSideMonitoringRequestHandlerTest.getResponse(request));
        ArgumentCaptor<ApiCallMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallMonitoringEvent.class));
        ApiCallMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyApiCallMonitoringEvent(3, event);
    }

    @Test
    public void afterError_shouldSendApiCallMonitoringEvent() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        AWSRequestMetrics metrics = getMetrics(1);
        request.setAWSRequestMetrics(metrics);
        requestHandler.afterError(request, ClientSideMonitoringRequestHandlerTest.getResponse(request), new IOException(""));
        ArgumentCaptor<ApiCallMonitoringEvent> monitoringEventArgumentCaptor = ArgumentCaptor.forClass(ApiCallMonitoringEvent.class);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(monitoringEventArgumentCaptor.capture());
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiCallMonitoringEvent.class));
        ApiCallMonitoringEvent event = monitoringEventArgumentCaptor.getValue();
        verifyApiCallMonitoringEvent(1, event);
    }

    @Test
    public void afterResponse_customMonitoringListenerThrowException_shouldNotAffectAgentListener() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        Response<?> response = ClientSideMonitoringRequestHandlerTest.getResponse(request);
        Mockito.doThrow(new RuntimeException("Oops")).when(customMonitoringListener).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
        requestHandler.afterResponse(request, response);
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
    }

    @Test
    public void afterError_customMonitoringListenerThrowException_shouldNotAffectAgentListener() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        Response<?> response = ClientSideMonitoringRequestHandlerTest.getResponse(request);
        Mockito.doThrow(new RuntimeException("Oops")).when(customMonitoringListener).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
        requestHandler.afterError(request, response, new AmazonServiceException("test"));
        Mockito.verify(agentMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
        Mockito.verify(customMonitoringListener, Mockito.times(1)).handleEvent(ArgumentMatchers.any(ApiMonitoringEvent.class));
    }

    @Test
    public void afterError_maxRetriesExceeded() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        AWSRequestMetrics metrics = getMetrics(3);
        metrics.addPropertyWith(AwsClientSideMonitoringMetrics.MaxRetriesExceeded, true);
        request.setAWSRequestMetrics(metrics);
        requestHandler.afterError(request, ClientSideMonitoringRequestHandlerTest.getResponse(request), new IOException(""));
        ApiCallMonitoringEvent event = getCapturedEvent(ApiCallMonitoringEvent.class);
        Assert.assertThat(event.getMaxRetriesExceeded(), Matchers.is(1));
    }

    @Test
    public void afterError_clientExecutionTimeoutException() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        AWSRequestMetrics metrics = getMetrics(1);
        request.setAWSRequestMetrics(metrics);
        requestHandler.afterError(request, ClientSideMonitoringRequestHandlerTest.getResponse(request), new ClientExecutionTimeoutException(""));
        ApiCallMonitoringEvent event = getCapturedEvent(ApiCallMonitoringEvent.class);
        Assert.assertThat(event.getApiCallTimeout(), Matchers.is(1));
    }

    @Test
    public void afterErrorWithoutApiCallAttemptExcludesFinalFields() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        AWSRequestMetrics metrics = getMetrics(1);
        request.setAWSRequestMetrics(metrics);
        requestHandler.afterError(request, ClientSideMonitoringRequestHandlerTest.getResponse(request), new ClientExecutionTimeoutException(""));
        verifyEmptyFinalFields(getCapturedEvent(ApiCallMonitoringEvent.class));
    }

    @Test
    public void afterResponseIncludesFinalFieldsOfLastAttempt() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        HandlerAfterAttemptContext failedAttempt = HandlerAfterAttemptContext.builder().withRequest(request).withResponse(ClientSideMonitoringRequestHandlerTest.getResponse(request)).withException(new AmazonServiceException("ErrorMessage")).build();
        HandlerAfterAttemptContext successfulAttempt = HandlerAfterAttemptContext.builder().withRequest(request).withResponse(ClientSideMonitoringRequestHandlerTest.getResponse(request)).build();
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterAttempt(successfulAttempt);
        requestHandler.afterResponse(successfulAttempt.getRequest(), successfulAttempt.getResponse());
        verifyFinalFields(getCapturedEvent(ApiCallMonitoringEvent.class), getCapturedEvents(ApiCallAttemptMonitoringEvent.class, 2).get(1));
    }

    @Test
    public void afterErrorIncludesFinalAwsExceptionFieldsOfLastAttempt() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        HandlerAfterAttemptContext failedAttempt = HandlerAfterAttemptContext.builder().withRequest(request).withResponse(ClientSideMonitoringRequestHandlerTest.getResponse(request)).withException(new AmazonServiceException("ErrorMessage")).build();
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterError(failedAttempt.getRequest(), failedAttempt.getResponse(), failedAttempt.getException());
        verifyFinalFields(getCapturedEvent(ApiCallMonitoringEvent.class), getCapturedEvents(ApiCallAttemptMonitoringEvent.class, 3).get(2));
    }

    @Test
    public void afterErrorIncludesFinalSdkExceptionFieldsOfLastAttempt() {
        Request<?> request = ClientSideMonitoringRequestHandlerTest.getRequest();
        HandlerAfterAttemptContext failedAttempt = HandlerAfterAttemptContext.builder().withRequest(request).withResponse(ClientSideMonitoringRequestHandlerTest.getResponse(request)).withException(new RuntimeException("ErrorMessage")).build();
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterAttempt(failedAttempt);
        requestHandler.afterError(failedAttempt.getRequest(), failedAttempt.getResponse(), failedAttempt.getException());
        verifyFinalFields(getCapturedEvent(ApiCallMonitoringEvent.class), getCapturedEvents(ApiCallAttemptMonitoringEvent.class, 3).get(2));
    }
}

