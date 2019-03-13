/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.internal.processor;


import Action.Submit;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.SalesforceEndpoint;
import org.apache.camel.component.salesforce.SalesforceEndpointConfig;
import org.apache.camel.component.salesforce.api.SalesforceException;
import org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequest;
import org.apache.camel.component.salesforce.internal.client.RestClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AbstractRestProcessorApprovalTest {
    static class TestRestProcessor extends AbstractRestProcessor {
        RestClient client;

        TestRestProcessor() throws SalesforceException {
            this(AbstractRestProcessorApprovalTest.TestRestProcessor.endpoint(), Mockito.mock(RestClient.class));
        }

        TestRestProcessor(final SalesforceEndpoint endpoint, final RestClient client) {
            super(endpoint, client, Collections.emptyMap());
            this.client = client;
        }

        static SalesforceComponent component() {
            return new SalesforceComponent();
        }

        static SalesforceEndpointConfig configuration() {
            return new SalesforceEndpointConfig();
        }

        static SalesforceEndpoint endpoint() {
            return new SalesforceEndpoint(AbstractRestProcessorApprovalTest.notUsed(), AbstractRestProcessorApprovalTest.TestRestProcessor.component(), AbstractRestProcessorApprovalTest.TestRestProcessor.configuration(), AbstractRestProcessorApprovalTest.notUsed(), AbstractRestProcessorApprovalTest.notUsed());
        }

        @Override
        protected InputStream getRequestStream(final Exchange exchange) throws SalesforceException {
            return null;
        }

        @Override
        protected InputStream getRequestStream(final Message in, final Object object) throws SalesforceException {
            return null;
        }

        @Override
        protected void processRequest(final Exchange exchange) throws SalesforceException {
        }

        @Override
        protected void processResponse(final Exchange exchange, final InputStream responseEntity, final Map<String, String> headers, final SalesforceException ex, final AsyncCallback callback) {
        }
    }

    @Test
    public void shouldApplyTemplateToRequestFromBody() throws SalesforceException {
        final ApprovalRequest template = new ApprovalRequest();
        template.setActionType(Submit);
        final ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setComments("it should be me");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBodyAndHeader(approvalRequest, template);
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(approvalRequest.applyTemplate(template))));
    }

    @Test
    public void shouldApplyTemplateToRequestsFromBody() throws SalesforceException {
        final ApprovalRequest template = new ApprovalRequest();
        template.setActionType(Submit);
        final ApprovalRequest approvalRequest1 = new ApprovalRequest();
        approvalRequest1.setComments("it should be me first");
        final ApprovalRequest approvalRequest2 = new ApprovalRequest();
        approvalRequest2.setComments("it should be me second");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBodyAndHeader(Arrays.asList(approvalRequest1, approvalRequest2), template);
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(Arrays.asList(approvalRequest1.applyTemplate(template), approvalRequest2.applyTemplate(template)))));
    }

    @Test
    public void shouldComplainIfNoHeaderGivenOrBodyIsEmptyIterable() {
        try {
            sendBodyAndHeader(Collections.EMPTY_LIST, null);
            Assert.fail("SalesforceException should be thrown");
        } catch (final SalesforceException e) {
            Assert.assertEquals("Exception should be about not giving a body or a header", "Missing approval parameter in header or ApprovalRequest or List of ApprovalRequests body", e.getMessage());
        }
    }

    @Test
    public void shouldComplainIfNoHeaderOrBodyIsGiven() {
        try {
            sendBodyAndHeader(null, null);
            Assert.fail("SalesforceException should be thrown");
        } catch (final SalesforceException e) {
            Assert.assertEquals("Exception should be about not giving a body or a header", "Missing approval parameter in header or ApprovalRequest or List of ApprovalRequests body", e.getMessage());
        }
    }

    @Test
    public void shouldFetchApprovalRequestFromBody() throws SalesforceException {
        final ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setComments("it should be me");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBody(approvalRequest);
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(approvalRequest)));
    }

    @Test
    public void shouldFetchApprovalRequestFromHeader() throws SalesforceException {
        final ApprovalRequest request = new ApprovalRequest();
        request.setComments("hi there");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBodyAndHeader(null, request);
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(request)));
    }

    @Test
    public void shouldFetchApprovalRequestFromHeaderEvenIfBodyIsDefinedButNotConvertable() throws SalesforceException {
        final ApprovalRequest request = new ApprovalRequest();
        request.setComments("hi there");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBodyAndHeaders("Nothing to see here", request, Collections.singletonMap("approval.ContextId", "context-id"));
        final ApprovalRequest combined = new ApprovalRequest();
        combined.setComments("hi there");
        combined.setContextId("context-id");
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(combined)));
    }

    @Test
    public void shouldFetchApprovalRequestsFromBody() throws SalesforceException {
        final ApprovalRequest approvalRequest1 = new ApprovalRequest();
        approvalRequest1.setComments("it should be me first");
        final ApprovalRequest approvalRequest2 = new ApprovalRequest();
        approvalRequest2.setComments("it should be me second");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBody(Arrays.asList(approvalRequest1, approvalRequest2));
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(Arrays.asList(approvalRequest1, approvalRequest2))));
    }

    @Test
    public void shouldFetchApprovalRequestsFromMultiplePropertiesInMessageHeaders() throws SalesforceException {
        final Map<String, Object> headers = new HashMap<>();
        headers.put("approval.ContextId", "contextId");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor = sendBodyAndHeaders(AbstractRestProcessorApprovalTest.notUsed(), AbstractRestProcessorApprovalTest.notUsed(), headers);
        final ApprovalRequest request = new ApprovalRequest();
        request.setContextId("contextId");
        Mockito.verify(processor).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(request)));
    }

    @Test
    public void shouldHonorPriorities() throws SalesforceException {
        final ApprovalRequest template = new ApprovalRequest();
        template.setComments("third priority");
        final ApprovalRequest body = new ApprovalRequest();
        body.setComments("first priority");
        final Map<String, Object> headers = Collections.singletonMap("approval.Comments", "second priority");
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor1 = sendBodyAndHeaders(null, template, null);
        Mockito.verify(processor1).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(AbstractRestProcessorApprovalTest.requestWithComment("third priority"))));
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor2 = sendBodyAndHeaders(null, template, headers);
        Mockito.verify(processor2).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(AbstractRestProcessorApprovalTest.requestWithComment("second priority"))));
        final AbstractRestProcessorApprovalTest.TestRestProcessor processor3 = sendBodyAndHeaders(body, template, headers);
        Mockito.verify(processor3).getRequestStream(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(new org.apache.camel.component.salesforce.api.dto.approval.ApprovalRequests(AbstractRestProcessorApprovalTest.requestWithComment("first priority"))));
    }
}

