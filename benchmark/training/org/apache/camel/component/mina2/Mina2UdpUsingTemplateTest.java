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
package org.apache.camel.component.mina2;


import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class Mina2UdpUsingTemplateTest extends BaseMina2Test {
    private int messageCount = 3;

    @Test
    public void testMinaRoute() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedMessageCount(3);
        endpoint.expectedBodiesReceived("Hello Message: 0", "Hello Message: 1", "Hello Message: 2");
        sendUdpMessages();
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSendingByteMessages() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedMessageCount(1);
        byte[] in = "Hello from bytes".getBytes();
        template.sendBody(String.format("mina2:udp://127.0.0.1:%1$s?sync=false", getPort()), in);
        assertMockEndpointsSatisfied();
        List<Exchange> list = endpoint.getReceivedExchanges();
        byte[] out = list.get(0).getIn().getBody(byte[].class);
        for (int i = 0; i < (in.length); i++) {
            assertEquals("Thew bytes should be the same", in[i], out[i]);
        }
    }

    @Test
    public void testSendingRawByteMessage() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedMessageCount(1);
        String toSend = "ef3e00559f5faf0262f5ff0962d9008daa91001cd46b0fa9330ef0f3030fff250e46f72444d1cc501678c351e04b8004c" + "4000002080000fe850bbe011030000008031b031bfe9251305441593830354720020800050440ff";
        byte[] in = fromHexString(toSend);
        template.sendBody(String.format("mina2:udp://127.0.0.1:%1$s?sync=false", getPort()), in);
        assertMockEndpointsSatisfied();
        List<Exchange> list = endpoint.getReceivedExchanges();
        byte[] out = list.get(0).getIn().getBody(byte[].class);
        for (int i = 0; i < (in.length); i++) {
            assertEquals("The bytes should be the same", in[i], out[i]);
        }
        assertEquals("The strings should be the same", toSend, byteArrayToHex(out));
    }
}

