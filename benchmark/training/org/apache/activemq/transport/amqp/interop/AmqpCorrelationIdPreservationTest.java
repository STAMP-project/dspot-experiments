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
 * Tests that the AMQP CorrelationId value and type are preserved.
 */
@RunWith(Parameterized.class)
public class AmqpCorrelationIdPreservationTest extends AmqpClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSInteroperabilityTest.class);

    private final String transformer;

    public AmqpCorrelationIdPreservationTest(String transformer) {
        this.transformer = transformer;
    }

    @Test(timeout = 60000)
    public void testStringCorrelationIdIsPreserved() throws Exception {
        doTestCorrelationIdPreservation("msg-id-string:1");
    }

    @Test(timeout = 60000)
    public void testStringCorrelationIdIsPreservedAfterRestart() throws Exception {
        doTestCorrelationIdPreservationOnBrokerRestart("msg-id-string:1");
    }

    @Test(timeout = 60000)
    public void testUUIDCorrelationIdIsPreserved() throws Exception {
        doTestCorrelationIdPreservation(UUID.randomUUID());
    }

    @Test(timeout = 60000)
    public void testUUIDCorrelationIdIsPreservedAfterRestart() throws Exception {
        doTestCorrelationIdPreservationOnBrokerRestart(UUID.randomUUID());
    }

    @Test(timeout = 60000)
    public void testUnsignedLongCorrelationIdIsPreserved() throws Exception {
        doTestCorrelationIdPreservation(new UnsignedLong(255L));
    }

    @Test(timeout = 60000)
    public void testUnsignedLongCorrelationIdIsPreservedAfterRestart() throws Exception {
        doTestCorrelationIdPreservationOnBrokerRestart(new UnsignedLong(255L));
    }

    @Test(timeout = 60000)
    public void testBinaryLongCorrelationIdIsPreserved() throws Exception {
        byte[] payload = new byte[32];
        for (int i = 0; i < 32; ++i) {
            payload[i] = ((byte) ('a' + i));
        }
        doTestCorrelationIdPreservation(new Binary(payload));
    }

    @Test(timeout = 60000)
    public void testBinaryLongCorrelationIdIsPreservedAfterRestart() throws Exception {
        byte[] payload = new byte[32];
        for (int i = 0; i < 32; ++i) {
            payload[i] = ((byte) ('a' + i));
        }
        doTestCorrelationIdPreservationOnBrokerRestart(new Binary(payload));
    }

    @Test(timeout = 60000)
    public void testStringCorrelationIdPrefixIsPreserved() throws Exception {
        doTestCorrelationIdPreservation("ID:msg-id-string:1");
    }
}

