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
package org.apache.activemq.broker.region;


import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author gtully
 * @see https://issues.apache.org/activemq/browse/AMQ-2020
 */
public class QueueDuplicatesFromStoreTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(QueueDuplicatesFromStoreTest.class);

    ActiveMQQueue destination = new ActiveMQQueue(("queue-" + (QueueDuplicatesFromStoreTest.class.getSimpleName())));

    BrokerService brokerService;

    static final String mesageIdRoot = "11111:22222:";

    final int messageBytesSize = 256;

    final String text = new String(new byte[messageBytesSize]);

    final int ackStartIndex = 100;

    final int ackWindow = 50;

    final int ackBatchSize = 50;

    final int fullWindow = 200;

    protected int count = 5000;

    public void testNoDuplicateAfterCacheFullAndAckedWithLargeAuditDepth() throws Exception {
        doTestNoDuplicateAfterCacheFullAndAcked((1024 * 10));
    }

    public void testNoDuplicateAfterCacheFullAndAckedWithSmallAuditDepth() throws Exception {
        doTestNoDuplicateAfterCacheFullAndAcked(512);
    }
}

