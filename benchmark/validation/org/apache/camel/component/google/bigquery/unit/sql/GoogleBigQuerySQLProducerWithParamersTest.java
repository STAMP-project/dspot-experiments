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
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeExchangeException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GoogleBigQuerySQLProducerWithParamersTest extends GoogleBigQuerySQLProducerBaseTest {
    @Test
    public void sendMessageWithParametersInBody() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("id", "100");
        body.put("data", "some data");
        producer.process(createExchangeWithBody(body));
        ArgumentCaptor<QueryRequest> dataCaptor = ArgumentCaptor.forClass(QueryRequest.class);
        Mockito.verify(bigquery.jobs()).query(ArgumentMatchers.eq(projectId), dataCaptor.capture());
        QueryRequest request = dataCaptor.getValue();
        assertEquals(sql, request.getQuery());
        assertEquals(2, request.getQueryParameters().size());
        assertEquals("id", request.getQueryParameters().get(1).getName());
        assertEquals("100", request.getQueryParameters().get(1).getParameterValue().getValue());
        assertEquals("data", request.getQueryParameters().get(0).getName());
        assertEquals("some data", request.getQueryParameters().get(0).getParameterValue().getValue());
    }

    @Test
    public void sendMessageWithParametersInBodyAndHeaders() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("id", "100");
        Exchange exchange = createExchangeWithBody(body);
        exchange.getMessage().getHeaders().put("id", "200");
        exchange.getMessage().getHeaders().put("data", "some data");
        producer.process(exchange);
        ArgumentCaptor<QueryRequest> dataCaptor = ArgumentCaptor.forClass(QueryRequest.class);
        Mockito.verify(bigquery.jobs()).query(ArgumentMatchers.eq(projectId), dataCaptor.capture());
        QueryRequest request = dataCaptor.getValue();
        assertEquals(sql, request.getQuery());
        assertEquals(2, request.getQueryParameters().size());
        assertEquals("id", request.getQueryParameters().get(1).getName());
        assertEquals("Body data must have higher priority", "100", request.getQueryParameters().get(1).getParameterValue().getValue());
        assertEquals("data", request.getQueryParameters().get(0).getName());
        assertEquals("some data", request.getQueryParameters().get(0).getParameterValue().getValue());
    }

    @Test(expected = RuntimeExchangeException.class)
    public void sendMessageWithoutParameters() throws Exception {
        producer.process(createExchangeWithBody(new HashMap()));
    }
}

