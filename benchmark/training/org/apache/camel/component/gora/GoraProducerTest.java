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
package org.apache.camel.component.gora;


import GoraAttribute.GORA_OPERATION.value;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.store.DataStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * GORA Producer Tests
 */
@RunWith(MockitoJUnitRunner.class)
public class GoraProducerTest extends GoraTestSupport {
    // TODO: Query methods does not yet has tests
    /**
     * Mock CamelExchange
     */
    @Mock
    private Exchange mockCamelExchange;

    /**
     * Mock Gora Endpoint
     */
    @Mock
    private GoraEndpoint mockGoraEndpoint;

    /**
     * Mock Gora Configuration
     */
    @Mock
    private GoraConfiguration mockGoraConfiguration;

    /**
     * Mock Camel Message
     */
    @Mock
    private Message mockCamelMessage;

    /**
     * Mock Gora DataStore
     */
    @Mock
    private DataStore<Object, Persistent> mockDatastore;

    @Test(expected = RuntimeException.class)
    public void processShouldThrowExceptionIfOperationIsNull() throws Exception {
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfOperationIsUnknown() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("dah");
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atMost(1)).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atMost(1)).getHeader(value);
    }

    @Test
    public void shouldInvokeDastorePut() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("PUT");
        final Long sampleKey = new Long(2);
        Mockito.when(mockCamelMessage.getHeader(GoraAttribute.GORA_KEY.value)).thenReturn(sampleKey);
        final Persistent sampleValue = Mockito.mock(Persistent.class);
        Mockito.when(mockCamelMessage.getBody(Persistent.class)).thenReturn(sampleValue);
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(GoraAttribute.GORA_KEY.value);
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getBody(Persistent.class);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).put(sampleKey, sampleValue);
    }

    @Test
    public void shouldInvokeDastoreGet() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("get");
        final Long sampleKey = new Long(2);
        Mockito.when(mockCamelMessage.getHeader(GoraAttribute.GORA_KEY.value)).thenReturn(sampleKey);
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(GoraAttribute.GORA_KEY.value);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).get(sampleKey);
    }

    @Test
    public void shouldInvokeDatastoreDelete() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("dEletE");
        final Long sampleKey = new Long(2);
        Mockito.when(mockCamelMessage.getHeader(GoraAttribute.GORA_KEY.value)).thenReturn(sampleKey);
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(GoraAttribute.GORA_KEY.value);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).delete(sampleKey);
    }

    @Test
    public void shouldInvokeDastoreSchemaExists() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("schemaExists");
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).schemaExists();
    }

    @Test
    public void shouldInvokeDastoreCreateSchema() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("createSchema");
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).createSchema();
    }

    @Test
    public void shouldInvokeDastoreGetSchemaName() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("GetSchemANamE");
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).getSchemaName();
    }

    @Test
    public void shouldInvokeDatastoreDeleteSchema() throws Exception {
        Mockito.when(mockCamelExchange.getIn()).thenReturn(mockCamelMessage);
        Mockito.when(mockCamelMessage.getHeader(value)).thenReturn("DeleteSChEmA");
        final Message outMessage = Mockito.mock(Message.class);
        Mockito.when(mockCamelExchange.getOut()).thenReturn(outMessage);
        final GoraProducer producer = new GoraProducer(mockGoraEndpoint, mockGoraConfiguration, mockDatastore);
        producer.process(mockCamelExchange);
        Mockito.verify(mockCamelExchange, Mockito.atLeastOnce()).getIn();
        Mockito.verify(mockCamelMessage, Mockito.atLeastOnce()).getHeader(value);
        Mockito.verify(mockDatastore, Mockito.atMost(1)).deleteSchema();
    }
}

