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
package org.apache.activemq.transport.amqp.interop;


import java.util.UUID;
import org.apache.activemq.transport.amqp.JMSInteroperabilityTest;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.UnsignedLong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests that the AMQP MessageID value and type are preserved.
 */
@RunWith(Parameterized.class)
public class AmqpMessageIdPreservationTest extends AmqpClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSInteroperabilityTest.class);

    private final String transformer;

    public AmqpMessageIdPreservationTest(String transformer) {
        this.transformer = transformer;
    }

    @Test(timeout = 60000)
    public void testStringMessageIdIsPreserved() throws Exception {
        doTestMessageIdPreservation("msg-id-string:1");
    }

    @Test(timeout = 60000)
    public void testStringMessageIdIsPreservedAfterRestart() throws Exception {
        doTestMessageIdPreservationOnBrokerRestart("msg-id-string:1");
    }

    @Test(timeout = 60000)
    public void testUUIDMessageIdIsPreserved() throws Exception {
        doTestMessageIdPreservation(UUID.randomUUID());
    }

    @Test(timeout = 60000)
    public void testUUIDMessageIdIsPreservedAfterRestart() throws Exception {
        doTestMessageIdPreservationOnBrokerRestart(UUID.randomUUID());
    }

    @Test(timeout = 60000)
    public void testUnsignedLongMessageIdIsPreserved() throws Exception {
        doTestMessageIdPreservation(new UnsignedLong(255L));
    }

    @Test(timeout = 60000)
    public void testUnsignedLongMessageIdIsPreservedAfterRestart() throws Exception {
        doTestMessageIdPreservationOnBrokerRestart(new UnsignedLong(255L));
    }

    @Test(timeout = 60000)
    public void testBinaryLongMessageIdIsPreserved() throws Exception {
        byte[] payload = new byte[32];
        for (int i = 0; i < 32; ++i) {
            payload[i] = ((byte) ('a' + i));
        }
        doTestMessageIdPreservation(new Binary(payload));
    }

    @Test(timeout = 60000)
    public void testBinaryLongMessageIdIsPreservedAfterRestart() throws Exception {
        byte[] payload = new byte[32];
        for (int i = 0; i < 32; ++i) {
            payload[i] = ((byte) ('a' + i));
        }
        doTestMessageIdPreservationOnBrokerRestart(new Binary(payload));
    }

    @Test(timeout = 60000)
    public void testStringMessageIdPrefixIsPreserved() throws Exception {
        doTestMessageIdPreservation("ID:msg-id-string:1");
    }
}

