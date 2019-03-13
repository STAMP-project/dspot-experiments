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


import CouchDbConstants.HEADER_DOC_ID;
import CouchDbConstants.HEADER_DOC_REV;
import CouchDbConstants.HEADER_METHOD;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.UUID;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightcouch.Response;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class CouchDbProducerTest {
    @Mock
    private CouchDbClientWrapper client;

    @Mock
    private CouchDbEndpoint endpoint;

    @Mock
    private Exchange exchange;

    @Mock
    private Message msg;

    @Mock
    private Response response;

    private CouchDbProducer producer;

    @Test(expected = InvalidPayloadException.class)
    public void testBodyMandatory() throws Exception {
        Mockito.when(msg.getMandatoryBody()).thenThrow(InvalidPayloadException.class);
        producer.process(exchange);
    }

    @Test
    public void testDocumentHeadersAreSet() throws Exception {
        String id = UUID.randomUUID().toString();
        String rev = UUID.randomUUID().toString();
        JsonObject doc = new JsonObject();
        doc.addProperty("_id", id);
        doc.addProperty("_rev", rev);
        Mockito.when(msg.getMandatoryBody()).thenReturn(doc);
        Mockito.when(client.update(doc)).thenReturn(response);
        Mockito.when(response.getId()).thenReturn(id);
        Mockito.when(response.getRev()).thenReturn(rev);
        producer.process(exchange);
        Mockito.verify(msg).setHeader(HEADER_DOC_ID, id);
        Mockito.verify(msg).setHeader(HEADER_DOC_REV, rev);
    }

    @Test(expected = InvalidPayloadException.class)
    public void testNullSaveResponseThrowsError() throws Exception {
        Mockito.when(exchange.getIn().getMandatoryBody()).thenThrow(InvalidPayloadException.class);
        Mockito.when(producer.getBodyAsJsonElement(exchange)).thenThrow(InvalidPayloadException.class);
        producer.process(exchange);
    }

    @Test
    public void testDeleteResponse() throws Exception {
        String id = UUID.randomUUID().toString();
        String rev = UUID.randomUUID().toString();
        JsonObject doc = new JsonObject();
        doc.addProperty("_id", id);
        doc.addProperty("_rev", rev);
        Mockito.when(msg.getHeader(HEADER_METHOD, String.class)).thenReturn("DELETE");
        Mockito.when(msg.getMandatoryBody()).thenReturn(doc);
        Mockito.when(client.remove(doc)).thenReturn(response);
        Mockito.when(response.getId()).thenReturn(id);
        Mockito.when(response.getRev()).thenReturn(rev);
        producer.process(exchange);
        Mockito.verify(msg).setHeader(HEADER_DOC_ID, id);
        Mockito.verify(msg).setHeader(HEADER_DOC_REV, rev);
    }

    @Test
    public void testGetResponse() throws Exception {
        String id = UUID.randomUUID().toString();
        JsonObject doc = new JsonObject();
        doc.addProperty("_id", id);
        Mockito.when(msg.getHeader(HEADER_METHOD, String.class)).thenReturn("GET");
        Mockito.when(msg.getHeader(HEADER_DOC_ID, String.class)).thenReturn(id);
        Mockito.when(msg.getMandatoryBody()).thenReturn(doc);
        Mockito.when(client.get(id)).thenReturn(response);
        producer.process(exchange);
        Mockito.verify(msg).getHeader(HEADER_DOC_ID, String.class);
    }

    @Test
    public void testStringBodyIsConvertedToJsonTree() throws Exception {
        Mockito.when(msg.getMandatoryBody()).thenReturn("{ \"name\" : \"coldplay\" }");
        Mockito.when(client.save(ArgumentMatchers.any())).thenAnswer(new Answer<Response>() {
            @Override
            public Response answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertTrue((((invocation.getArguments()[0].getClass()) + " but wanted ") + (JsonElement.class)), ((invocation.getArguments()[0]) instanceof JsonElement));
                return new Response();
            }
        });
        producer.process(exchange);
        Mockito.verify(client).save(ArgumentMatchers.any(JsonObject.class));
    }
}

