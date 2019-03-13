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
package org.apache.camel.component.hl7;


import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.QRY_A19;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test for HL7 routing where the mina endpoint passes on a byte array instead of a string
 * and leaves charset interpretation to the dataformat.
 */
public class HL7ByteArrayRouteTest extends HL7TestSupport {
    @Test
    public void testSendA19() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:a19");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(Message.class);
        String line1 = "MSH|^~\\&|MYSENDER|MYSENDERAPP|MYCLIENT|MYCLIENTAPP|200612211200||QRY^A19|1234|P|2.4";
        String line2 = "QRD|200612211200|R|I|GetPatient|||1^RD|0101701234|DEM||";
        StringBuilder in = new StringBuilder();
        in.append(line1);
        in.append("\r");
        in.append(line2);
        String out = template.requestBody((("mina2:tcp://127.0.0.1:" + (HL7TestSupport.getPort())) + "?sync=true&codec=#hl7codec"), in.toString(), String.class);
        String[] lines = out.split("\r");
        assertEquals("MSH|^~\\&|MYSENDER||||200701011539||ADR^A19||||123|||||UNICODE UTF-8", lines[0]);
        assertEquals("MSA|AA|123", lines[1]);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSendA01() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:a01");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(Message.class);
        String line1 = "MSH|^~\\&|MYSENDER|MYSENDERAPP|MYCLIENT|MYCLIENTAPP|200612211200||ADT^A01|123|P|2.4||||||UNICODE UTF-8";
        String line2 = "PID|||123456||D?e^John";
        StringBuilder in = new StringBuilder();
        in.append(line1);
        in.append("\r");
        in.append(line2);
        String out = template.requestBody((("mina2:tcp://127.0.0.1:" + (HL7TestSupport.getPort())) + "?sync=true&codec=#hl7codec"), in.toString(), String.class);
        String[] lines = out.split("\r");
        assertEquals("MSH|^~\\&|MYSENDER||||200701011539||ADT^A01||||123|||||UNICODE UTF-8", lines[0]);
        assertEquals("PID|||123||D?e^John", lines[1]);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSendUnknown() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:unknown");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(Message.class);
        String line1 = "MSH|^~\\&|MYSENDER|MYSENDERAPP|MYCLIENT|MYCLIENTAPP|200612211200||ADT^A02|1234|P|2.4";
        String line2 = "PID|||123456||D?e^John";
        StringBuilder in = new StringBuilder();
        in.append(line1);
        in.append("\r");
        in.append(line2);
        template.requestBody((("mina2:tcp://127.0.0.1:" + (HL7TestSupport.getPort())) + "?sync=true&codec=#hl7codec"), in.toString());
        assertMockEndpointsSatisfied();
    }

    public class MyHL7BusinessLogic {
        // This is a plain POJO that has NO imports whatsoever on Apache Camel.
        // its a plain POJO only importing the HAPI library so we can much easier work with the HL7 format.
        public Message handleA19(Message msg) throws Exception {
            // here you can have your business logic for A19 messages
            assertTrue((msg instanceof QRY_A19));
            // just return the same dummy response
            return HL7ByteArrayRouteTest.createADR19Message();
        }

        public Message handleA01(Message msg) throws Exception {
            // here you can have your business logic for A01 messages
            assertTrue((msg instanceof ADT_A01));
            // just return the same dummy response
            return HL7ByteArrayRouteTest.createADT01Message(getMSH().getMessageControlID().getValue());
        }
    }
}

