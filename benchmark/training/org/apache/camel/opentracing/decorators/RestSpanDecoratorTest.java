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


import java.util.Arrays;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.opentracing.SpanDecorator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RestSpanDecoratorTest {
    @Test
    public void testGetOperation() {
        String path = "/persons/{personId}";
        String uri = "rest://put:/persons:/%7BpersonId%7D?routeId=route4";
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Mockito.when(endpoint.getEndpointUri()).thenReturn(uri);
        Mockito.when(exchange.getFromEndpoint()).thenReturn(endpoint);
        SpanDecorator decorator = new RestSpanDecorator();
        Assert.assertEquals(path, decorator.getOperationName(exchange, endpoint));
    }

    @Test
    public void testGetParameters() {
        Assert.assertEquals(Arrays.asList("id1", "id2"), RestSpanDecorator.getParameters("/context/{id1}/{id2}"));
    }

    @Test
    public void testGetParametersNone() {
        Assert.assertTrue(RestSpanDecorator.getParameters("rest://put:/persons/hello/world?routeId=route4").isEmpty());
    }

    @Test
    public void testPreStringParameter() {
        testParameter("strParam", "strValue");
    }

    @Test
    public void testPreNumberParameter() {
        testParameter("numParam", 5.6);
    }

    @Test
    public void testPreBooleanParameter() {
        testParameter("boolParam", Boolean.TRUE);
    }

    @Test
    public void testGetPath() {
        Assert.assertEquals("/persons/{personId}", RestSpanDecorator.getPath("rest://put:/persons:/%7BpersonId%7D?routeId=route4"));
    }
}

