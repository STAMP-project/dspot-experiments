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
package org.apache.activemq;


import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.util.IdGenerator;
import org.apache.activemq.util.LongSequenceGenerator;
import org.junit.Assert;
import org.junit.Test;


public class ActiveMQMessageAuditNotSyncTest {
    private final IdGenerator connectionIdGenerator = new IdGenerator();

    private final LongSequenceGenerator sessionIdGenerator = new LongSequenceGenerator();

    private final LongSequenceGenerator producerIdGenerator = new LongSequenceGenerator();

    private final LongSequenceGenerator sequenceIdGenerator = new LongSequenceGenerator();

    @Test
    public void testAuditDepth() {
        int maxAuditDepth = Short.MAX_VALUE;
        ConnectionId connectionId = new ConnectionId(connectionIdGenerator.generateId());
        SessionId sessionId = new SessionId(connectionId, sessionIdGenerator.getNextSequenceId());
        ProducerId producerId = new ProducerId(sessionId, producerIdGenerator.getNextSequenceId());
        ActiveMQMessageAuditNoSync audit = new ActiveMQMessageAuditNoSync();
        audit.setAuditDepth(maxAuditDepth);
        MessageId msgId = new MessageId(producerId, 0);
        for (int i = 0; i < maxAuditDepth; i++) {
            msgId.setProducerSequenceId(sequenceIdGenerator.getNextSequenceId());
            Assert.assertFalse(audit.isDuplicate(msgId));
        }
        for (int i = 0; i < maxAuditDepth; i++) {
            Assert.assertTrue(audit.isDuplicate(msgId));
        }
    }
}

