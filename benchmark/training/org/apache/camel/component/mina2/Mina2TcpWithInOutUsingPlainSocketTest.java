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


import java.io.IOException;
import org.junit.Test;


/**
 * To test camel-mina2 component using a TCP client that communicates using TCP socket communication.
 */
public class Mina2TcpWithInOutUsingPlainSocketTest extends BaseMina2Test {
    @Test
    public void testSendAndReceiveOnce() throws Exception {
        String response = sendAndReceive("World");
        assertNotNull("Nothing received from Mina", response);
        assertEquals("Hello World", response);
    }

    @Test
    public void testSendAndReceiveTwice() throws Exception {
        String london = sendAndReceive("London");
        String paris = sendAndReceive("Paris");
        assertNotNull("Nothing received from Mina", london);
        assertNotNull("Nothing received from Mina", paris);
        assertEquals("Hello London", london);
        assertEquals("Hello Paris", paris);
    }

    @Test
    public void testReceiveNoResponseSinceOutBodyIsNull() throws Exception {
        String out = sendAndReceive("force-null-out-body");
        assertNull("no data should be recieved", out);
    }

    @Test
    public void testReceiveNoResponseSinceOutBodyIsNullTwice() throws Exception {
        String out = sendAndReceive("force-null-out-body");
        assertNull("no data should be recieved", out);
        out = sendAndReceive("force-null-out-body");
        assertNull("no data should be recieved", out);
    }

    @Test
    public void testExchangeFailedOutShouldBeNull() throws Exception {
        String out = sendAndReceive("force-exception");
        assertTrue("out should not be the same as in when the exchange has failed", (!("force-exception".equals(out))));
        assertEquals("should get the exception here", out, "java.lang.IllegalArgumentException: Forced exception");
    }

    @Test
    public void testExchangeWithInOnly() throws IOException {
        String out = sendAndReceive("force-set-in-body");
        assertEquals("Get a wrong response message", "Update the in message!", out);
    }
}

