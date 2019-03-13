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
package org.apache.camel.component.bean;


import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.TypeConverter;
import org.apache.camel.spi.Registry;
import org.junit.Assert;
import org.junit.Test;


public class DefaultParameterMappingStrategyTest extends ContextTestSupport {
    @Test
    public void testExchange() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Exchange");
        template.sendBody("direct:a", "Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMessage() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Message");
        template.sendBody("direct:b", "Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testException() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Exception");
        template.send("direct:c", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello");
                exchange.setException(new IllegalArgumentException("Forced by unit test"));
            }
        });
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTypeConverter() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("TypeConverter");
        template.sendBody("direct:d", "Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRegistry() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Registry");
        template.sendBody("direct:e", "Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testCamelContext() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("CamelContext");
        template.sendBody("direct:f", "Hello");
        assertMockEndpointsSatisfied();
    }

    public static class MyFooBean {
        public String withExchange(Exchange exchange) {
            Assert.assertNotNull(exchange);
            Assert.assertEquals("Hello", exchange.getIn().getBody(String.class));
            return "Exchange";
        }

        public String withMessage(Message message) {
            Assert.assertNotNull(message);
            Assert.assertEquals("Hello", message.getBody(String.class));
            return "Message";
        }

        public String withException(Message in, Exception cause) {
            Assert.assertNotNull(in);
            Assert.assertNotNull(cause);
            Assert.assertEquals("Hello", in.getBody(String.class));
            return "Exception";
        }

        public String withTypeConverter(String body, TypeConverter converter) {
            Assert.assertNotNull(body);
            Assert.assertNotNull(converter);
            Assert.assertEquals("Hello", body);
            Assert.assertEquals(new Integer(123), converter.convertTo(Integer.class, "123"));
            return "TypeConverter";
        }

        public String withRegistry(String body, Registry registry) {
            Assert.assertNotNull(body);
            Assert.assertNotNull(registry);
            Assert.assertNotNull(registry.lookupByName("foo"));
            Assert.assertEquals("Hello", body);
            return "Registry";
        }

        public String withCamelContext(String body, CamelContext camel) {
            Assert.assertNotNull(body);
            Assert.assertNotNull(camel);
            Assert.assertNotNull(camel.getRegistry().lookupByName("foo"));
            Assert.assertEquals("Hello", body);
            return "CamelContext";
        }
    }
}

