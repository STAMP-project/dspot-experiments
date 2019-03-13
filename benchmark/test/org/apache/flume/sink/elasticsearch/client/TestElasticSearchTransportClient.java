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


import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.sink.elasticsearch.ElasticSearchEventSerializer;
import org.apache.flume.sink.elasticsearch.IndexNameBuilder;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TestElasticSearchTransportClient {
    private ElasticSearchTransportClient fixture;

    @Mock
    private ElasticSearchEventSerializer serializer;

    @Mock
    private IndexNameBuilder nameBuilder;

    @Mock
    private Client elasticSearchClient;

    @Mock
    private BulkRequestBuilder bulkRequestBuilder;

    @Mock
    private IndexRequestBuilder indexRequestBuilder;

    @Mock
    private Event event;

    @Test
    public void shouldAddNewEventWithoutTTL() throws Exception {
        fixture.addEvent(event, nameBuilder, "bar_type", (-1));
        Mockito.verify(indexRequestBuilder).setSource(serializer.getContentBuilder(event).bytes());
        Mockito.verify(bulkRequestBuilder).add(indexRequestBuilder);
    }

    @Test
    public void shouldAddNewEventWithTTL() throws Exception {
        fixture.addEvent(event, nameBuilder, "bar_type", 10);
        Mockito.verify(indexRequestBuilder).setTTL(10);
        Mockito.verify(indexRequestBuilder).setSource(serializer.getContentBuilder(event).bytes());
    }

    @Test
    public void shouldExecuteBulkRequestBuilder() throws Exception {
        ListenableActionFuture<BulkResponse> action = ((ListenableActionFuture<BulkResponse>) (Mockito.mock(ListenableActionFuture.class)));
        BulkResponse response = Mockito.mock(BulkResponse.class);
        Mockito.when(bulkRequestBuilder.execute()).thenReturn(action);
        Mockito.when(action.actionGet()).thenReturn(response);
        Mockito.when(response.hasFailures()).thenReturn(false);
        fixture.addEvent(event, nameBuilder, "bar_type", 10);
        fixture.execute();
        Mockito.verify(bulkRequestBuilder).execute();
    }

    @Test(expected = EventDeliveryException.class)
    public void shouldThrowExceptionOnExecuteFailed() throws Exception {
        ListenableActionFuture<BulkResponse> action = ((ListenableActionFuture<BulkResponse>) (Mockito.mock(ListenableActionFuture.class)));
        BulkResponse response = Mockito.mock(BulkResponse.class);
        Mockito.when(bulkRequestBuilder.execute()).thenReturn(action);
        Mockito.when(action.actionGet()).thenReturn(response);
        Mockito.when(response.hasFailures()).thenReturn(true);
        fixture.addEvent(event, nameBuilder, "bar_type", 10);
        fixture.execute();
    }
}

