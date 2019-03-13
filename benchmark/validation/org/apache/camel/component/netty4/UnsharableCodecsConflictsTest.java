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
package org.apache.camel.component.netty4;


import java.net.Socket;
import java.util.Arrays;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 *
 */
public class UnsharableCodecsConflictsTest extends BaseNettyTest {
    static final byte[] LENGTH_HEADER = new byte[]{ 0, 0, 64, 0 };// 4096 bytes


    private Processor processor = new UnsharableCodecsConflictsTest.P();

    private int port1;

    private int port2;

    @Test
    public void canSupplyMultipleCodecsToEndpointPipeline() throws Exception {
        byte[] sPort1 = new byte[8192];
        byte[] sPort2 = new byte[16383];
        Arrays.fill(sPort1, ((byte) (56)));
        Arrays.fill(sPort2, ((byte) (57)));
        byte[] bodyPort1 = ((new String(UnsharableCodecsConflictsTest.LENGTH_HEADER)) + (new String(sPort1))).getBytes();
        byte[] bodyPort2 = ((new String(UnsharableCodecsConflictsTest.LENGTH_HEADER)) + (new String(sPort2))).getBytes();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(((new String(sPort2)) + "9"));
        Socket server1 = UnsharableCodecsConflictsTest.getSocket("localhost", port1);
        Socket server2 = UnsharableCodecsConflictsTest.getSocket("localhost", port2);
        try {
            UnsharableCodecsConflictsTest.sendSopBuffer(bodyPort2, server2);
            UnsharableCodecsConflictsTest.sendSopBuffer(bodyPort1, server1);
            UnsharableCodecsConflictsTest.sendSopBuffer(new String("9").getBytes(), server2);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            server1.close();
            server2.close();
        }
        mock.assertIsSatisfied();
    }

    class P implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            exchange.getOut().setBody(exchange.getIn().getBody(String.class));
        }
    }
}

