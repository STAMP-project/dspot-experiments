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
package org.apache.camel.component.google.bigquery.unit;


import GoogleBigQueryConstants.TABLE_ID;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GoogleBigQueryProducerTest extends BaseBigQueryTest {
    @Test
    public void sendMessage() throws Exception {
        producer.process(createExchangeWithBody(new HashMap()));
        ArgumentCaptor<TableDataInsertAllRequest> dataCaptor = ArgumentCaptor.forClass(TableDataInsertAllRequest.class);
        Mockito.verify(tabledataMock).insertAll(ArgumentMatchers.eq(projectId), ArgumentMatchers.eq(datasetId), ArgumentMatchers.eq(tableId), dataCaptor.capture());
        List<TableDataInsertAllRequest> requests = dataCaptor.getAllValues();
        assertEquals(1, requests.size());
        assertEquals(1, requests.get(0).getRows().size());
        assertNull(requests.get(0).getRows().get(0).getInsertId());
    }

    @Test
    public void sendMessageWithTableId() throws Exception {
        Exchange exchange = createExchangeWithBody(new HashMap());
        exchange.getIn().setHeader(TABLE_ID, "exchange_table_id");
        producer.process(exchange);
        ArgumentCaptor<TableDataInsertAllRequest> dataCaptor = ArgumentCaptor.forClass(TableDataInsertAllRequest.class);
        Mockito.verify(tabledataMock).insertAll(ArgumentMatchers.eq(projectId), ArgumentMatchers.eq(datasetId), ArgumentMatchers.eq("exchange_table_id"), dataCaptor.capture());
        List<TableDataInsertAllRequest> requests = dataCaptor.getAllValues();
        assertEquals(1, requests.size());
        assertEquals(1, requests.get(0).getRows().size());
        assertNull(requests.get(0).getRows().get(0).getInsertId());
    }

    @Test
    public void useAsInsertIdConfig() throws Exception {
        configuration.setUseAsInsertId("row1");
        Map<String, String> object = new HashMap<>();
        object.put("row1", "value1");
        producer.process(createExchangeWithBody(object));
        ArgumentCaptor<TableDataInsertAllRequest> dataCaptor = ArgumentCaptor.forClass(TableDataInsertAllRequest.class);
        Mockito.verify(tabledataMock).insertAll(ArgumentMatchers.eq(projectId), ArgumentMatchers.eq(datasetId), ArgumentMatchers.eq(tableId), dataCaptor.capture());
        List<TableDataInsertAllRequest> requests = dataCaptor.getAllValues();
        assertEquals(1, requests.size());
        assertEquals(1, requests.get(0).getRows().size());
        assertEquals("value1", requests.get(0).getRows().get(0).getInsertId());
    }

    @Test
    public void listOfMessages() throws Exception {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(new HashMap<>());
        messages.add(new HashMap<>());
        producer.process(createExchangeWithBody(messages));
        ArgumentCaptor<TableDataInsertAllRequest> dataCaptor = ArgumentCaptor.forClass(TableDataInsertAllRequest.class);
        Mockito.verify(tabledataMock).insertAll(ArgumentMatchers.eq(projectId), ArgumentMatchers.eq(datasetId), ArgumentMatchers.eq(tableId), dataCaptor.capture());
        List<TableDataInsertAllRequest> requests = dataCaptor.getAllValues();
        assertEquals(1, requests.size());
        assertEquals(2, requests.get(0).getRows().size());
    }
}

