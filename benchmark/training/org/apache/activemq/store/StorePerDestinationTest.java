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


import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.kahadb.FilteredKahaDBPersistenceAdapter;
import org.apache.activemq.store.kahadb.MultiKahaDBPersistenceAdapter;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StorePerDestinationTest {
    static final Logger LOG = LoggerFactory.getLogger(StorePerDestinationTest.class);

    static final int maxFileLength = 1024 * 100;

    static final int numToSend = 5000;

    final Vector<Throwable> exceptions = new Vector<Throwable>();

    BrokerService brokerService;

    @Test
    public void testTransactedSendReceive() throws Exception {
        brokerService.start();
        sendMessages(true, "SlowQ", 1, 0);
        Assert.assertEquals("got one", 1, receiveMessages(true, "SlowQ", 1));
    }

    @Test
    public void testTransactedSendReceiveAcrossStores() throws Exception {
        brokerService.start();
        sendMessages(true, "SlowQ,FastQ", 1, 0);
        Assert.assertEquals("got one", 2, receiveMessages(true, "SlowQ,FastQ", 2));
    }

    @Test
    public void testCommitRecovery() throws Exception {
        doTestRecovery(true);
    }

    @Test
    public void testRollbackRecovery() throws Exception {
        doTestRecovery(false);
    }

    @Test
    public void testDirectoryDefault() throws Exception {
        MultiKahaDBPersistenceAdapter multiKahaDBPersistenceAdapter = new MultiKahaDBPersistenceAdapter();
        ArrayList<FilteredKahaDBPersistenceAdapter> adapters = new ArrayList<FilteredKahaDBPersistenceAdapter>();
        FilteredKahaDBPersistenceAdapter otherFilteredKahaDBPersistenceAdapter = new FilteredKahaDBPersistenceAdapter();
        PersistenceAdapter otherStore = createStore(false);
        File someOtherDisk = new File((("target" + (File.separator)) + "someOtherDisk"));
        otherStore.setDirectory(someOtherDisk);
        otherFilteredKahaDBPersistenceAdapter.setPersistenceAdapter(otherStore);
        otherFilteredKahaDBPersistenceAdapter.setDestination(new ActiveMQQueue("Other"));
        adapters.add(otherFilteredKahaDBPersistenceAdapter);
        FilteredKahaDBPersistenceAdapter filteredKahaDBPersistenceAdapterDefault = new FilteredKahaDBPersistenceAdapter();
        PersistenceAdapter storeDefault = createStore(false);
        filteredKahaDBPersistenceAdapterDefault.setPersistenceAdapter(storeDefault);
        adapters.add(filteredKahaDBPersistenceAdapterDefault);
        multiKahaDBPersistenceAdapter.setFilteredPersistenceAdapters(adapters);
        Assert.assertEquals(multiKahaDBPersistenceAdapter.getDirectory(), storeDefault.getDirectory().getParentFile());
        Assert.assertEquals(someOtherDisk, otherStore.getDirectory().getParentFile());
    }

    @Test
    public void testSlowFastDestinationsStoreUsage() throws Exception {
        brokerService.start();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessages(false, "SlowQ", 50, 500);
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessages(false, "FastQ", StorePerDestinationTest.numToSend, 0);
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Assert.assertEquals("Got all sent", StorePerDestinationTest.numToSend, receiveMessages(false, "FastQ", StorePerDestinationTest.numToSend));
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }
        });
        executorService.shutdown();
        Assert.assertTrue("consumers executor finished on time", executorService.awaitTermination((5 * 60), TimeUnit.SECONDS));
        final SystemUsage usage = brokerService.getSystemUsage();
        Assert.assertTrue("Store is not hogged", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                long storeUsage = usage.getStoreUsage().getUsage();
                StorePerDestinationTest.LOG.info(("Store Usage: " + storeUsage));
                return storeUsage < (5 * (StorePerDestinationTest.maxFileLength));
            }
        }));
        Assert.assertTrue("no exceptions", exceptions.isEmpty());
    }
}

