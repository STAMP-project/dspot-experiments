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
package org.apache.activemq.store.kahadb;


import Transaction.CallableClosure;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test is for AMQ-5748 to verify that {@link MessageStore} implements correctly
 * compute the size of the messages in the KahaDB Store.
 */
public class KahaDBMessageStoreSizeTest extends AbstractKahaDBMessageStoreSizeTest {
    /**
     * Make sure that the sizes stored in the KahaDB location index are the same as in
     * the message order index.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLocationIndexMatchesOrderIndex() throws Exception {
        final KahaDBStore kahaDbStore = ((KahaDBStore) (store));
        writeMessages();
        // Iterate over the order index and add up the size of the messages to compare
        // to the location index
        kahaDbStore.indexLock.readLock().lock();
        try {
            long size = kahaDbStore.pageFile.tx().execute(new CallableClosure<Long, IOException>() {
                @Override
                public Long execute(org.apache.activemq.store.kahadb.disk.page.Transaction tx) throws IOException {
                    long size = 0;
                    // Iterate through all index entries to get the size of each message
                    org.apache.activemq.store.kahadb.MessageDatabase.StoredDestination sd = kahaDbStore.getStoredDestination(kahaDbStore.convert(destination), tx);
                    for (Iterator<Entry<Long, org.apache.activemq.store.kahadb.MessageDatabase.MessageKeys>> iterator = sd.orderIndex.iterator(tx); iterator.hasNext();) {
                        size += iterator.next().getValue().location.getSize();
                    }
                    return size;
                }
            });
            Assert.assertEquals("Order index size values don't match message size", size, messageStore.getMessageSize());
        } finally {
            kahaDbStore.indexLock.readLock().unlock();
        }
    }
}

