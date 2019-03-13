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
package org.apache.camel.opentracing.decorators;


import AbstractHttpSpanDecorator.GET_METHOD;
import AbstractHttpSpanDecorator.POST_METHOD;
import Exchange.HTTP_METHOD;
import Exchange.HTTP_QUERY;
import Exchange.HTTP_RESPONSE_CODE;
import Exchange.HTTP_URI;
import Tags.HTTP_STATUS;
import Tags.HTTP_URL;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.opentracing.SpanDecorator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AbstractHttpSpanDecoratorTest {
    private static final String TEST_URI = "http://localhost:8080/test";

    @Test
    public void testGetOperationName() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_METHOD)).thenReturn("PUT");
        AbstractHttpSpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        Assert.assertEquals("PUT", decorator.getOperationName(exchange, null));
    }

    @Test
    public void testGetMethodFromMethodHeader() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_METHOD)).thenReturn("PUT");
        Assert.assertEquals("PUT", AbstractHttpSpanDecorator.getHttpMethod(exchange, null));
    }

    @Test
    public void testGetMethodQueryStringHeader() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_QUERY)).thenReturn("MyQuery");
        Assert.assertEquals(GET_METHOD, AbstractHttpSpanDecorator.getHttpMethod(exchange, null));
    }

    @Test
    public void testGetMethodQueryStringInEndpoint() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn("http://localhost:8080/endpoint?query=hello");
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_URI)).thenReturn("http://localhost:8080/endpoint?query=hello");
        Assert.assertEquals(GET_METHOD, AbstractHttpSpanDecorator.getHttpMethod(exchange, endpoint));
    }

    @Test
    public void testGetMethodBodyNotNull() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_URI)).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(message.getBody()).thenReturn("Message Body");
        Assert.assertEquals(POST_METHOD, AbstractHttpSpanDecorator.getHttpMethod(exchange, endpoint));
    }

    @Test
    public void testGetMethodDefault() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_URI)).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Assert.assertEquals(GET_METHOD, AbstractHttpSpanDecorator.getHttpMethod(exchange, endpoint));
    }

    @Test
    public void testPreUri() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_URI)).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        SpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        MockTracer tracer = new MockTracer();
        MockSpan span = tracer.buildSpan("TestSpan").start();
        decorator.pre(span, exchange, endpoint);
        Assert.assertEquals(AbstractHttpSpanDecoratorTest.TEST_URI, span.tags().get(HTTP_URL.getKey()));
        Assert.assertTrue(span.tags().containsKey(Tags.HTTP_METHOD.getKey()));
    }

    @Test
    public void testGetHttpURLFromHeaderUrl() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_URI)).thenReturn("Another URL");
        Mockito.when(message.getHeader(Exchange.HTTP_URL)).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        AbstractHttpSpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        Assert.assertEquals(AbstractHttpSpanDecoratorTest.TEST_URI, decorator.getHttpURL(exchange, endpoint));
    }

    @Test
    public void testGetHttpURLFromHeaderUri() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_URI)).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        AbstractHttpSpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        Assert.assertEquals(AbstractHttpSpanDecoratorTest.TEST_URI, decorator.getHttpURL(exchange, endpoint));
    }

    @Test
    public void testGetHttpURLFromEndpointUri() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(AbstractHttpSpanDecoratorTest.TEST_URI);
        Mockito.when(exchange.getIn()).thenReturn(message);
        AbstractHttpSpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        Assert.assertEquals(AbstractHttpSpanDecoratorTest.TEST_URI, decorator.getHttpURL(exchange, endpoint));
    }

    @Test
    public void testGetHttpURLFromEndpointUriWithAdditionalScheme() {
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(("netty-http:" + (AbstractHttpSpanDecoratorTest.TEST_URI)));
        Mockito.when(exchange.getIn()).thenReturn(message);
        AbstractHttpSpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        Assert.assertEquals(AbstractHttpSpanDecoratorTest.TEST_URI, decorator.getHttpURL(exchange, endpoint));
    }

    @Test
    public void testPostResponseCode() {
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(exchange.hasOut()).thenReturn(true);
        Mockito.when(exchange.getOut()).thenReturn(message);
        Mockito.when(message.getHeader(HTTP_RESPONSE_CODE)).thenReturn(200);
        SpanDecorator decorator = new AbstractHttpSpanDecorator() {
            @Override
            public String getComponent() {
                return null;
            }
        };
        MockTracer tracer = new MockTracer();
        MockSpan span = tracer.buildSpan("TestSpan").start();
        decorator.post(span, exchange, null);
        Assert.assertEquals(200, span.tags().get(HTTP_STATUS.getKey()));
    }
}

