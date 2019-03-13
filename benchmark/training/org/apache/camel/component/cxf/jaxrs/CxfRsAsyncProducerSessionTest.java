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
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class CxfRsAsyncProducerSessionTest extends CamelSpringTestSupport {
    private static int port1 = CXFTestSupport.getPort1();

    private static int port2 = CXFTestSupport.getPort("CxfRsProducerSessionTest.jetty");

    @Test
    public void testNoSessionProxy() {
        String response = sendMessage("direct://proxy", "World", Boolean.FALSE).getOut().getBody(String.class);
        assertEquals("New New World", response);
        response = sendMessage("direct://proxy", "World", Boolean.FALSE).getOut().getBody(String.class);
        assertEquals("New New World", response);
    }

    @Test
    public void testExchangeSessionProxy() {
        String response = sendMessage("direct://proxyexchange", "World", Boolean.FALSE).getOut().getBody(String.class);
        assertEquals("Old New World", response);
        response = sendMessage("direct://proxyexchange", "World", Boolean.FALSE).getOut().getBody(String.class);
        assertEquals("Old New World", response);
    }

    @Test
    public void testInstanceSession() {
        String response = sendMessage("direct://proxyinstance", "World", Boolean.FALSE).getOut().getBody(String.class);
        assertEquals("Old New World", response);
        response = sendMessage("direct://proxyinstance", "World", Boolean.FALSE).getOut().getBody(String.class);
        assertEquals("Old Old World", response);
        // we do the instance tests for proxy and http in one test because order
        // matters here
        response = sendMessage("direct://httpinstance", "World", Boolean.TRUE).getOut().getBody(String.class);
        assertEquals("Old Old World", response);
    }

    @Test
    public void testNoSessionHttp() {
        String response = sendMessage("direct://http", "World", Boolean.TRUE).getOut().getBody(String.class);
        assertEquals("New New World", response);
        response = sendMessage("direct://http", "World", Boolean.TRUE).getOut().getBody(String.class);
        assertEquals("New New World", response);
    }

    @Test
    public void testExchangeSessionHttp() {
        String response = sendMessage("direct://httpexchange", "World", Boolean.TRUE).getOut().getBody(String.class);
        assertEquals("Old New World", response);
        response = sendMessage("direct://httpexchange", "World", Boolean.TRUE).getOut().getBody(String.class);
        assertEquals("Old New World", response);
    }
}

