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
package org.apache.camel.component.couchdb;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult.Row;
import org.lightcouch.CouchDbContext;
import org.lightcouch.CouchDbInfo;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CouchDbChangesetTrackerTest {
    @Mock
    private Changes changes;

    @Mock
    private CouchDbClientWrapper client;

    @Mock
    private CouchDbEndpoint endpoint;

    @Mock
    private CouchDbConsumer consumer;

    @Mock
    private CouchDbContext context;

    @Mock
    private CouchDbInfo info;

    @Mock
    private Row row3;

    @Mock
    private Row row2;

    @Mock
    private Row row1;

    @Mock
    private Exchange exchange1;

    @Mock
    private Exchange exchange2;

    @Mock
    private Exchange exchange3;

    @Mock
    private Processor processor;

    private CouchDbChangesetTracker tracker;

    @Test
    public void testExchangeCreatedWithCorrectProperties() throws Exception {
        Mockito.when(changes.hasNext()).thenReturn(true, true, true, false);
        Mockito.when(changes.next()).thenReturn(row1, row2, row3);
        Mockito.when(endpoint.createExchange("seq1", "id1", null, false)).thenReturn(exchange1);
        Mockito.when(endpoint.createExchange("seq2", "id2", null, false)).thenReturn(exchange2);
        Mockito.when(endpoint.createExchange("seq3", "id3", null, false)).thenReturn(exchange3);
        Mockito.when(consumer.getProcessor()).thenReturn(processor);
        tracker.run();
        Mockito.verify(endpoint).createExchange("seq1", "id1", null, false);
        Mockito.verify(processor).process(exchange1);
        Mockito.verify(endpoint).createExchange("seq2", "id2", null, false);
        Mockito.verify(processor).process(exchange2);
        Mockito.verify(endpoint).createExchange("seq3", "id3", null, false);
        Mockito.verify(processor).process(exchange3);
    }

    @Test
    public void testProcessorInvoked() throws Exception {
        Mockito.when(changes.hasNext()).thenReturn(true, false);
        Mockito.when(changes.next()).thenReturn(row1);
        Mockito.when(consumer.getProcessor()).thenReturn(processor);
        tracker.run();
        Mockito.verify(endpoint).createExchange("seq1", "id1", null, false);
        Mockito.verify(processor).process(ArgumentMatchers.isNull());
    }
}

