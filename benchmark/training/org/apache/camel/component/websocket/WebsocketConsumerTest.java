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
package org.apache.camel.component.websocket;


import WebsocketConstants.CONNECTION_KEY;
import java.net.InetSocketAddress;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spi.ExceptionHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WebsocketConsumerTest {
    private static final String CONNECTION_KEY = "random-connection-key";

    private static final String MESSAGE = "message";

    private static final InetSocketAddress ADDRESS = InetSocketAddress.createUnresolved("127.0.0.1", 12345);

    @Mock
    private WebsocketEndpoint endpoint;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private Processor processor;

    @Mock
    private Exchange exchange;

    @Mock
    private Message outMessage;

    private Exception exception = new Exception("BAD NEWS EVERYONE!");

    private WebsocketConsumer websocketConsumer;

    @Test
    public void testSendExchange() throws Exception {
        Mockito.when(endpoint.createExchange()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(outMessage);
        websocketConsumer.sendMessage(WebsocketConsumerTest.CONNECTION_KEY, WebsocketConsumerTest.MESSAGE, WebsocketConsumerTest.ADDRESS);
        InOrder inOrder = Mockito.inOrder(endpoint, exceptionHandler, processor, exchange, outMessage);
        inOrder.verify(endpoint, Mockito.times(1)).createExchange();
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(outMessage, Mockito.times(1)).setHeader(WebsocketConstants.CONNECTION_KEY, WebsocketConsumerTest.CONNECTION_KEY);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(outMessage, Mockito.times(1)).setBody(WebsocketConsumerTest.MESSAGE);
        inOrder.verify(processor, Mockito.times(1)).process(exchange);
        inOrder.verify(exchange, Mockito.times(1)).getException();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendExchangeWithException() throws Exception {
        Mockito.when(endpoint.createExchange()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(outMessage);
        Mockito.doThrow(exception).when(processor).process(exchange);
        Mockito.when(exchange.getException()).thenReturn(exception);
        websocketConsumer.sendMessage(WebsocketConsumerTest.CONNECTION_KEY, WebsocketConsumerTest.MESSAGE, WebsocketConsumerTest.ADDRESS);
        InOrder inOrder = Mockito.inOrder(endpoint, exceptionHandler, processor, exchange, outMessage);
        inOrder.verify(endpoint, Mockito.times(1)).createExchange();
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(outMessage, Mockito.times(1)).setHeader(WebsocketConstants.CONNECTION_KEY, WebsocketConsumerTest.CONNECTION_KEY);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(outMessage, Mockito.times(1)).setBody(WebsocketConsumerTest.MESSAGE);
        inOrder.verify(processor, Mockito.times(1)).process(exchange);
        inOrder.verify(exchange, Mockito.times(2)).getException();
        inOrder.verify(exceptionHandler, Mockito.times(1)).handleException(ArgumentMatchers.any(), ArgumentMatchers.eq(exchange), ArgumentMatchers.eq(exception));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendExchangeWithExchangeExceptionIsNull() throws Exception {
        Mockito.when(endpoint.createExchange()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(outMessage);
        Mockito.doThrow(exception).when(processor).process(exchange);
        Mockito.when(exchange.getException()).thenReturn(null);
        websocketConsumer.sendMessage(WebsocketConsumerTest.CONNECTION_KEY, WebsocketConsumerTest.MESSAGE, WebsocketConsumerTest.ADDRESS);
        InOrder inOrder = Mockito.inOrder(endpoint, exceptionHandler, processor, exchange, outMessage);
        inOrder.verify(endpoint, Mockito.times(1)).createExchange();
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(outMessage, Mockito.times(1)).setHeader(WebsocketConstants.CONNECTION_KEY, WebsocketConsumerTest.CONNECTION_KEY);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(outMessage, Mockito.times(1)).setBody(WebsocketConsumerTest.MESSAGE);
        inOrder.verify(processor, Mockito.times(1)).process(exchange);
        inOrder.verify(exchange, Mockito.times(1)).getException();
        inOrder.verifyNoMoreInteractions();
    }
}

