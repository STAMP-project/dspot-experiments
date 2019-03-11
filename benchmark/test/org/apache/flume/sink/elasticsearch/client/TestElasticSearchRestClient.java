/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.elasticsearch.client;


import java.util.List;
import junit.framework.Assert;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.sink.elasticsearch.ElasticSearchEventSerializer;
import org.apache.flume.sink.elasticsearch.IndexNameBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TestElasticSearchRestClient {
    private ElasticSearchRestClient fixture;

    @Mock
    private ElasticSearchEventSerializer serializer;

    @Mock
    private IndexNameBuilder nameBuilder;

    @Mock
    private Event event;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse httpResponse;

    @Mock
    private StatusLine httpStatus;

    @Mock
    private HttpEntity httpEntity;

    private static final String INDEX_NAME = "foo_index";

    private static final String MESSAGE_CONTENT = "{\"body\":\"test\"}";

    private static final String[] HOSTS = new String[]{ "host1", "host2" };

    @Test
    public void shouldAddNewEventWithoutTTL() throws Exception {
        ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
        Mockito.when(httpStatus.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(httpStatus);
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpUriRequest.class))).thenReturn(httpResponse);
        fixture.addEvent(event, nameBuilder, "bar_type", (-1));
        fixture.execute();
        Mockito.verify(httpClient).execute(ArgumentMatchers.isA(HttpUriRequest.class));
        Mockito.verify(httpClient).execute(argument.capture());
        Assert.assertEquals("http://host1/_bulk", argument.getValue().getURI().toString());
        Assert.assertTrue(verifyJsonEvents("{\"index\":{\"_type\":\"bar_type\", \"_index\":\"foo_index\"}}\n", TestElasticSearchRestClient.MESSAGE_CONTENT, EntityUtils.toString(argument.getValue().getEntity())));
    }

    @Test
    public void shouldAddNewEventWithTTL() throws Exception {
        ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
        Mockito.when(httpStatus.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(httpStatus);
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpUriRequest.class))).thenReturn(httpResponse);
        fixture.addEvent(event, nameBuilder, "bar_type", 123);
        fixture.execute();
        Mockito.verify(httpClient).execute(ArgumentMatchers.isA(HttpUriRequest.class));
        Mockito.verify(httpClient).execute(argument.capture());
        Assert.assertEquals("http://host1/_bulk", argument.getValue().getURI().toString());
        Assert.assertTrue(verifyJsonEvents("{\"index\":{\"_type\":\"bar_type\",\"_index\":\"foo_index\",\"_ttl\":\"123\"}}\n", TestElasticSearchRestClient.MESSAGE_CONTENT, EntityUtils.toString(argument.getValue().getEntity())));
    }

    @Test(expected = EventDeliveryException.class)
    public void shouldThrowEventDeliveryException() throws Exception {
        ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
        Mockito.when(httpStatus.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(httpStatus);
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpUriRequest.class))).thenReturn(httpResponse);
        fixture.addEvent(event, nameBuilder, "bar_type", 123);
        fixture.execute();
    }

    @Test
    public void shouldRetryBulkOperation() throws Exception {
        ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
        Mockito.when(httpStatus.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.SC_OK);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(httpStatus);
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpUriRequest.class))).thenReturn(httpResponse);
        fixture.addEvent(event, nameBuilder, "bar_type", 123);
        fixture.execute();
        Mockito.verify(httpClient, Mockito.times(2)).execute(ArgumentMatchers.isA(HttpUriRequest.class));
        Mockito.verify(httpClient, Mockito.times(2)).execute(argument.capture());
        List<HttpPost> allValues = argument.getAllValues();
        Assert.assertEquals("http://host1/_bulk", allValues.get(0).getURI().toString());
        Assert.assertEquals("http://host2/_bulk", allValues.get(1).getURI().toString());
    }
}

