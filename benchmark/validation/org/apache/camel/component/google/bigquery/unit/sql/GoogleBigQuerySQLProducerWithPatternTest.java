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
package org.apache.camel.component.google.bigquery.unit.sql;


import com.google.api.services.bigquery.model.QueryRequest;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GoogleBigQuerySQLProducerWithPatternTest extends GoogleBigQuerySQLProducerBaseTest {
    @Test
    public void sendExchangeWithProperties() throws Exception {
        Exchange exchange = createExchangeWithBody(null);
        exchange.getProperties().put("testDatasetId", "dataset");
        String expected = "insert into dataset.testTableId(id, data) values(1, 'test')";
        producer.process(exchange);
        ArgumentCaptor<QueryRequest> dataCaptor = ArgumentCaptor.forClass(QueryRequest.class);
        Mockito.verify(bigquery.jobs()).query(ArgumentMatchers.eq(projectId), dataCaptor.capture());
        List<QueryRequest> requests = dataCaptor.getAllValues();
        assertEquals(1, requests.size());
        assertEquals(expected, requests.get(0).getQuery());
    }

    @Test
    public void sendMessageWithHeaders() throws Exception {
        Exchange exchange = createExchangeWithBody(null);
        exchange.getMessage().getHeaders().put("testDatasetId", "dataset");
        String expected = "insert into dataset.testTableId(id, data) values(1, 'test')";
        producer.process(exchange);
        ArgumentCaptor<QueryRequest> dataCaptor = ArgumentCaptor.forClass(QueryRequest.class);
        Mockito.verify(bigquery.jobs()).query(ArgumentMatchers.eq(projectId), dataCaptor.capture());
        List<QueryRequest> requests = dataCaptor.getAllValues();
        assertEquals(1, requests.size());
        assertEquals(expected, requests.get(0).getQuery());
    }
}

