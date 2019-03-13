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
public class UnsharableCodecsConflicts2Test extends BaseNettyTest {
    static final byte[] LENGTH_HEADER = new byte[]{ 0, 0, 64, 0 };// 16384 bytes


    private Processor processor = new UnsharableCodecsConflicts2Test.P();

    private int port;

    @Test
    public void unsharableCodecsConflictsTest() throws Exception {
        byte[] data1 = new byte[8192];
        byte[] data2 = new byte[16383];
        Arrays.fill(data1, ((byte) (56)));
        Arrays.fill(data2, ((byte) (57)));
        byte[] body1 = ((new String(UnsharableCodecsConflicts2Test.LENGTH_HEADER)) + (new String(data1))).getBytes();
        byte[] body2 = ((new String(UnsharableCodecsConflicts2Test.LENGTH_HEADER)) + (new String(data2))).getBytes();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(((new String(data2)) + "9"));
        Socket client1 = UnsharableCodecsConflicts2Test.getSocket("localhost", port);
        Socket client2 = UnsharableCodecsConflicts2Test.getSocket("localhost", port);
        // use two clients to send to the same server at the same time
        try {
            UnsharableCodecsConflicts2Test.sendBuffer(body2, client2);
            UnsharableCodecsConflicts2Test.sendBuffer(body1, client1);
            UnsharableCodecsConflicts2Test.sendBuffer(new String("9").getBytes(), client2);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            client1.close();
            client2.close();
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

