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


import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.jaxrs.testbean.Customer;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;


public class CxfRsSpringConsumerTest extends CamelSpringTestSupport {
    private static int port1 = CXFTestSupport.getPort1();

    @Test
    public void testMappingException() throws Exception {
        String address = ("http://localhost:" + (CxfRsSpringConsumerTest.port1)) + "/CxfRsSpringConsumerTest/customerservice/customers/126";
        doTestMappingException(address);
    }

    @Test
    public void testMappingException2() throws Exception {
        String address = ("http://localhost:" + (CxfRsSpringConsumerTest.port1)) + "/CxfRsSpringConsumerTest2/customerservice/customers/126";
        doTestMappingException(address);
    }

    @Test
    public void testInvokeCxfRsConsumer() throws Exception {
        String address = ("http://localhost:" + (CxfRsSpringConsumerTest.port1)) + "/CxfRsSpringConsumerInvokeService/customerservice/customers/123";
        WebClient wc = WebClient.create(address);
        Customer c = wc.accept("application/json").get(Customer.class);
        assertEquals(246L, c.getId());
    }
}

