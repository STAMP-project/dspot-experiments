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
package org.apache.activemq.broker.region.cursors;


import QueueMessageReference.NULL_MESSAGE;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PList;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.util.ByteSequence;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilePendingMessageCursorTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(FilePendingMessageCursorTestSupport.class);

    protected BrokerService brokerService;

    protected FilePendingMessageCursor underTest;

    @Test
    public void testAddToEmptyCursorWhenTempStoreIsFull() throws Exception {
        createBrokerWithTempStoreLimit();
        SystemUsage usage = brokerService.getSystemUsage();
        PList dud = brokerService.getTempDataStore().getPList("dud");
        // fill the temp store
        int id = 0;
        ByteSequence payload = new ByteSequence(new byte[1024]);
        while (!(usage.getTempUsage().isFull())) {
            dud.addFirst(("A-" + (++id)), payload);
        } 
        Assert.assertTrue(("temp store is full: %" + (usage.getTempUsage().getPercentUsage())), usage.getTempUsage().isFull());
        underTest = new FilePendingMessageCursor(brokerService.getBroker(), "test", false);
        underTest.setSystemUsage(usage);
        // ok to add
        underTest.addMessageLast(NULL_MESSAGE);
        Assert.assertFalse("cursor is not full", underTest.isFull());
    }

    @Test
    public void testResetClearsIterator() throws Exception {
        createBrokerWithTempStoreLimit();
        underTest = new FilePendingMessageCursor(brokerService.getBroker(), "test", false);
        // ok to add
        underTest.addMessageLast(NULL_MESSAGE);
        underTest.reset();
        underTest.release();
        try {
            underTest.hasNext();
            Assert.fail("expect npe on use of iterator after release");
        } catch (NullPointerException expected) {
        }
    }
}

