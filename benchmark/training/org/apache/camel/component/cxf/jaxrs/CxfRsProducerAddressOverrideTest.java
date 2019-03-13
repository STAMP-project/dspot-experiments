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
package org.apache.camel.component.cxf.jaxrs;


import CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS;
import CxfConstants.CAMEL_CXF_RS_USING_HTTP_API;
import CxfConstants.OPERATION_NAME;
import Exchange.DESTINATION_OVERRIDE_URL;
import Exchange.HTTP_METHOD;
import Exchange.HTTP_PATH;
import ExchangePattern.InOut;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.jaxrs.testbean.Customer;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class CxfRsProducerAddressOverrideTest extends CamelSpringTestSupport {
    private static int port1 = CXFTestSupport.getPort1();

    private static int port2 = CXFTestSupport.getPort("CxfRsProducerAddressOverrideTest.jetty");

    @Test
    public void testGetCustomerWithSyncProxyAPIByOverrideDest() {
        Exchange exchange = template.send("direct://proxy", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setPattern(InOut);
                Message inMessage = exchange.getIn();
                // set the operation name
                inMessage.setHeader(OPERATION_NAME, "getCustomer");
                // using the proxy client API
                inMessage.setHeader(CAMEL_CXF_RS_USING_HTTP_API, Boolean.FALSE);
                // set the parameters , if you just have one parameter
                // camel will put this object into an Object[] itself
                inMessage.setBody("123");
                setupDestinationURL(inMessage);
            }
        });
        // get the response message
        Customer response = ((Customer) (exchange.getOut().getBody()));
        assertNotNull("The response should not be null ", response);
        assertEquals("Get a wrong customer id ", 123, response.getId());
        assertEquals("Get a wrong customer name", "John", response.getName());
    }

    @Test
    public void testGetCustomerWithSyncHttpAPIByOverrideDest() {
        Exchange exchange = template.send("direct://http", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setPattern(InOut);
                Message inMessage = exchange.getIn();
                // using the http central client API
                inMessage.setHeader(CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
                // set the Http method
                inMessage.setHeader(HTTP_METHOD, "GET");
                // set the relative path
                inMessage.setHeader(HTTP_PATH, "/customerservice/customers/123");
                // Specify the response class , cxfrs will use InputStream as the response object type
                inMessage.setHeader(CAMEL_CXF_RS_RESPONSE_CLASS, Customer.class);
                setupDestinationURL(inMessage);
            }
        });
        // get the response message
        Customer response = ((Customer) (exchange.getOut().getBody()));
        assertNotNull("The response should not be null ", response);
        assertEquals("Get a wrong customer id ", 123, response.getId());
        assertEquals("Get a wrong customer name", "John", response.getName());
    }

    @Test
    public void testGetCustomerWithAsyncProxyAPIByOverrideDest() {
        Exchange exchange = template.send("cxfrs:bean:rsClientProxy", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setPattern(InOut);
                Message inMessage = exchange.getIn();
                // set the operation name
                inMessage.setHeader(OPERATION_NAME, "getCustomer");
                // using the proxy client API
                inMessage.setHeader(CAMEL_CXF_RS_USING_HTTP_API, Boolean.FALSE);
                // set the parameters , if you just have one parameter
                // camel will put this object into an Object[] itself
                inMessage.setBody("123");
                setupDestinationURL(inMessage);
            }
        });
        // get the response message
        Customer response = ((Customer) (exchange.getOut().getBody()));
        assertNotNull("The response should not be null ", response);
        assertEquals("Get a wrong customer id ", 123, response.getId());
        assertEquals("Get a wrong customer name", "John", response.getName());
    }

    @Test
    public void testGetCustomerWithAsyncHttpAPIByOverrideDest() {
        Exchange exchange = template.send("cxfrs:bean:rsClientHttp", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setPattern(InOut);
                Message inMessage = exchange.getIn();
                // using the http central client API
                inMessage.setHeader(CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
                // set the Http method
                inMessage.setHeader(HTTP_METHOD, "GET");
                // set the relative path
                inMessage.setHeader(HTTP_PATH, "/customerservice/customers/123");
                // Specify the response class , cxfrs will use InputStream as
                // the response object type
                inMessage.setHeader(CAMEL_CXF_RS_RESPONSE_CLASS, Customer.class);
                setupDestinationURL(inMessage);
            }
        });
        // get the response message
        Customer response = ((Customer) (exchange.getOut().getBody()));
        assertNotNull("The response should not be null", response);
        assertEquals("Get a wrong customer id ", 123, response.getId());
        assertEquals("Get a wrong customer name", "John", response.getName());
    }

    @Test
    public void testAddressMultiOverride() {
        // First call with override url
        Exchange exchange = template.send("direct://http", new CxfRsProducerAddressOverrideTest.SendProcessor((("http://localhost:" + (getPort1())) + "/CxfRsProducerAddressOverrideTest")));
        // get the response message
        Customer response = exchange.getOut().getBody(Customer.class);
        assertNotNull("The response should not be null ", response);
        // Second call with override url
        exchange = template.send("direct://http", new CxfRsProducerAddressOverrideTest.SendProcessor((("http://localhost:" + (getPort1())) + "/CxfRsProducerNonExistingAddressOverrideTest")));
        // Third call with override url ( we reuse the first url there )
        exchange = template.send("direct://http", new CxfRsProducerAddressOverrideTest.SendProcessor((("http://localhost:" + (getPort1())) + "/CxfRsProducerAddressOverrideTest")));
        // get the response message
        response = exchange.getOut().getBody(Customer.class);
        assertNotNull("The response should not be null ", response);
    }

    class SendProcessor implements Processor {
        private String address;

        public SendProcessor(String address) {
            this.address = address;
        }

        public void process(Exchange exchange) throws Exception {
            exchange.setPattern(InOut);
            Message inMessage = exchange.getIn();
            // using the http central client API
            inMessage.setHeader(CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
            // set the Http method
            inMessage.setHeader(HTTP_METHOD, "GET");
            // set the relative path
            inMessage.setHeader(HTTP_PATH, "/customerservice/customers/123");
            // Specify the response class , cxfrs will use InputStream as the
            // response object type
            inMessage.setHeader(CAMEL_CXF_RS_RESPONSE_CLASS, Customer.class);
            inMessage.setHeader(DESTINATION_OVERRIDE_URL, address);
        }
    }
}

