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
package org.apache.camel.component.salesforce.internal.client;


import SalesforceSecurityHandler.AUTHENTICATION_REQUEST_ATTRIBUTE;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.salesforce.SalesforceHttpClient;
import org.apache.camel.component.salesforce.api.SalesforceException;
import org.apache.camel.component.salesforce.internal.SalesforceSession;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Response.CompleteListener;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpFields;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class AbstractClientBaseTest {
    static class Client extends AbstractClientBase {
        Client(final SalesforceSession session) throws SalesforceException {
            /* 1 second termination timeout */
            super(null, session, Mockito.mock(SalesforceHttpClient.class), 1);
        }

        @Override
        protected SalesforceException createRestException(final Response response, final InputStream responseContent) {
            return null;
        }

        @Override
        protected void setAccessToken(final Request request) {
        }
    }

    SalesforceSession session = Mockito.mock(SalesforceSession.class);

    // having client as a field also tests that the same client instance can be
    // stopped and started again
    final AbstractClientBaseTest.Client client;

    public AbstractClientBaseTest() throws SalesforceException {
        client = new AbstractClientBaseTest.Client(session);
        Mockito.when(session.getAccessToken()).thenReturn("token");
    }

    @Test
    public void shouldDetermineHeadersForRequest() {
        final CamelContext context = new DefaultCamelContext();
        final Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        final Message in = new org.apache.camel.support.DefaultMessage(context);
        in.setHeader("sforce-auto-assign", "TRUE");
        in.setHeader("SFORCE-CALL-OPTIONS", new String[]{ "client=SampleCaseSensitiveToken/100", "defaultNamespace=battle" });
        in.setHeader("Sforce-Limit-Info", Collections.singletonList("per-app-api-usage"));
        in.setHeader("x-sfdc-packageversion-clientPackage", "1.0");
        in.setHeader("Sforce-Query-Options", "batchSize=1000");
        in.setHeader("Non-Related", "Header");
        exchange.setIn(in);
        final Map<String, List<String>> headers = AbstractClientBase.determineHeaders(exchange);
        assertThat(headers).containsOnly(entry("sforce-auto-assign", Collections.singletonList("TRUE")), entry("SFORCE-CALL-OPTIONS", Arrays.asList("client=SampleCaseSensitiveToken/100", "defaultNamespace=battle")), entry("Sforce-Limit-Info", Collections.singletonList("per-app-api-usage")), entry("x-sfdc-packageversion-clientPackage", Collections.singletonList("1.0")), entry("Sforce-Query-Options", Collections.singletonList("batchSize=1000")));
    }

    @Test
    public void shouldDetermineHeadersFromResponse() {
        final Response response = Mockito.mock(Response.class);
        final HttpFields httpHeaders = new HttpFields();
        httpHeaders.add("Date", "Mon, 20 May 2013 22:21:46 GMT");
        httpHeaders.add("Sforce-Limit-Info", "api-usage=18/5000");
        httpHeaders.add("Last-Modified", "Mon, 20 May 2013 20:49:32 GMT");
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        httpHeaders.add("Transfer-Encoding", "chunked");
        Mockito.when(response.getHeaders()).thenReturn(httpHeaders);
        final Map<String, String> headers = AbstractClientBase.determineHeadersFrom(response);
        assertThat(headers).containsEntry("Sforce-Limit-Info", "api-usage=18/5000");
    }

    @Test
    public void shouldNotHangIfRequestsHaveFinished() throws Exception {
        final Request request = Mockito.mock(Request.class);
        final ArgumentCaptor<CompleteListener> listener = ArgumentCaptor.forClass(CompleteListener.class);
        Mockito.doNothing().when(request).send(listener.capture());
        client.doHttpRequest(request, ( response, headers, exception) -> {
        });
        final Result result = Mockito.mock(Result.class);
        final Response response = Mockito.mock(Response.class);
        Mockito.when(result.getResponse()).thenReturn(response);
        Mockito.when(response.getHeaders()).thenReturn(new HttpFields());
        final SalesforceHttpRequest salesforceRequest = Mockito.mock(SalesforceHttpRequest.class);
        Mockito.when(result.getRequest()).thenReturn(salesforceRequest);
        final HttpConversation conversation = Mockito.mock(HttpConversation.class);
        Mockito.when(salesforceRequest.getConversation()).thenReturn(conversation);
        Mockito.when(conversation.getAttribute(AUTHENTICATION_REQUEST_ATTRIBUTE)).thenReturn(salesforceRequest);
        // completes the request
        listener.getValue().onComplete(result);
        final long stopStartTime = System.currentTimeMillis();
        // should not wait
        stop();
        final long elapsed = (System.currentTimeMillis()) - stopStartTime;
        Assert.assertTrue((elapsed < 10));
    }

    @Test
    public void shouldTimeoutWhenRequestsAreStillOngoing() throws Exception {
        doHttpRequest(Mockito.mock(Request.class), ( response, headers, exception) -> {
        });
        // the request never completes
        final long stopStartTime = System.currentTimeMillis();
        // will wait for 1 second
        stop();
        final long elapsed = (System.currentTimeMillis()) - stopStartTime;
        Assert.assertTrue(((elapsed > 900) && (elapsed < 1100)));
    }
}

