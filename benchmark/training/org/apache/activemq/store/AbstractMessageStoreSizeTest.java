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
package org.apache.activemq.store;


import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.util.IdGenerator;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test is for AMQ-5748 to verify that {@link MessageStore} implements correctly
 * compute the size of the messages in the store.
 */
public abstract class AbstractMessageStoreSizeTest {
    protected static final IdGenerator id = new IdGenerator();

    protected ActiveMQQueue destination = new ActiveMQQueue("Test");

    protected ProducerId producerId = new ProducerId("1.1.1");

    protected static final int MESSAGE_COUNT = 20;

    protected static String dataDirectory = "target/test-amq-5748/datadb";

    protected static int testMessageSize = 1000;

    /**
     * This method tests that the message size exists after writing a bunch of messages to the store.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageSize() throws Exception {
        writeMessages();
        long messageSize = getMessageStore().getMessageSize();
        Assert.assertTrue(((getMessageStore().getMessageCount()) == 20));
        Assert.assertTrue((messageSize > (20 * (AbstractMessageStoreSizeTest.testMessageSize))));
    }
}

