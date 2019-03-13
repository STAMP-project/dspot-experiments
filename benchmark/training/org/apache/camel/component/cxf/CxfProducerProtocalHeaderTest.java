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
package org.apache.camel.component.cxf;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CxfProducerProtocalHeaderTest extends CamelTestSupport {
    private static int port = AvailablePortFinder.getNextAvailable();

    private static final String RESPONSE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + (("<soap:Body><ns1:echoResponse xmlns:ns1=\"http://cxf.component.camel.apache.org/\">" + "<return xmlns=\"http://cxf.component.camel.apache.org/\">echo Hello World!</return>") + "</ns1:echoResponse></soap:Body></soap:Envelope>");

    @Test
    public void testSendMessage() {
        Exchange exchange = sendSimpleMessage(((("cxf://http://localhost:" + (CxfProducerProtocalHeaderTest.port)) + "/CxfProducerProtocalHeaderTest/user") + "?serviceClass=org.apache.camel.component.cxf.HelloService"));
        org.apache.camel.Message out = exchange.getOut();
        String result = out.getBody(String.class);
        assertEquals("reply body on Camel", ("echo " + "Hello World!"), result);
    }
}

