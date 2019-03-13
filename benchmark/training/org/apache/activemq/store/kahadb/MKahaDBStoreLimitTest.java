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


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.BaseDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.usage.StoreUsage;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MKahaDBStoreLimitTest {
    private static final Logger LOG = LoggerFactory.getLogger(MKahaDBStoreLimitTest.class);

    final ActiveMQQueue queueA = new ActiveMQQueue("Q.A");

    final ActiveMQQueue queueB = new ActiveMQQueue("Q.B");

    private BrokerService broker;

    @Test
    public void testPerDestUsage() throws Exception {
        // setup multi-kaha adapter
        MultiKahaDBPersistenceAdapter persistenceAdapter = new MultiKahaDBPersistenceAdapter();
        KahaDBPersistenceAdapter kahaStore = new KahaDBPersistenceAdapter();
        kahaStore.setJournalMaxFileLength((1024 * 5));
        kahaStore.setCleanupInterval(1000);
        // set up a store per destination
        FilteredKahaDBPersistenceAdapter filtered = new FilteredKahaDBPersistenceAdapter();
        StoreUsage storeUsage = new StoreUsage();
        storeUsage.setPercentLimit(10);
        storeUsage.setTotal(((1024 * 1024) * 10));
        filtered.setUsage(storeUsage);
        filtered.setPersistenceAdapter(kahaStore);
        filtered.setPerDestination(true);
        List<FilteredKahaDBPersistenceAdapter> stores = new ArrayList<>();
        stores.add(filtered);
        persistenceAdapter.setFilteredPersistenceAdapters(stores);
        createBroker(persistenceAdapter).start();
        produceMessages(queueA, 20);
        produceMessages(queueB, 0);
        MKahaDBStoreLimitTest.LOG.info(((("Store global u: " + (broker.getSystemUsage().getStoreUsage().getUsage())) + ", %:") + (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
        Assert.assertTrue("some usage", ((broker.getSystemUsage().getStoreUsage().getUsage()) > 0));
        BaseDestination baseDestinationA = ((BaseDestination) (broker.getRegionBroker().getDestinationMap().get(queueA)));
        BaseDestination baseDestinationB = ((BaseDestination) (broker.getRegionBroker().getDestinationMap().get(queueB)));
        MKahaDBStoreLimitTest.LOG.info(((("Store A u: " + (baseDestinationA.getSystemUsage().getStoreUsage().getUsage())) + ", %: ") + (baseDestinationA.getSystemUsage().getStoreUsage().getPercentUsage())));
        Assert.assertTrue(((baseDestinationA.getSystemUsage().getStoreUsage().getUsage()) > 0));
        produceMessages(queueB, 40);
        Assert.assertTrue(((baseDestinationB.getSystemUsage().getStoreUsage().getUsage()) > 0));
        Assert.assertTrue(((baseDestinationB.getSystemUsage().getStoreUsage().getUsage()) > (baseDestinationA.getSystemUsage().getStoreUsage().getUsage())));
        MKahaDBStoreLimitTest.LOG.info(((("Store B u: " + (baseDestinationB.getSystemUsage().getStoreUsage().getUsage())) + ", %: ") + (baseDestinationB.getSystemUsage().getStoreUsage().getPercentUsage())));
        MKahaDBStoreLimitTest.LOG.info(((("Store global u: " + (broker.getSystemUsage().getStoreUsage().getUsage())) + ", %:") + (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
        consume(queueA);
        consume(queueB);
        MKahaDBStoreLimitTest.LOG.info(((("Store global u: " + (broker.getSystemUsage().getStoreUsage().getUsage())) + ", %:") + (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
        MKahaDBStoreLimitTest.LOG.info(((("Store A u: " + (baseDestinationA.getSystemUsage().getStoreUsage().getUsage())) + ", %: ") + (baseDestinationA.getSystemUsage().getStoreUsage().getPercentUsage())));
        MKahaDBStoreLimitTest.LOG.info(((("Store B u: " + (baseDestinationB.getSystemUsage().getStoreUsage().getUsage())) + ", %: ") + (baseDestinationB.getSystemUsage().getStoreUsage().getPercentUsage())));
    }

    @Test
    public void testExplicitAdapter() throws Exception {
        MultiKahaDBPersistenceAdapter persistenceAdapter = new MultiKahaDBPersistenceAdapter();
        KahaDBPersistenceAdapter kahaStore = new KahaDBPersistenceAdapter();
        kahaStore.setJournalMaxFileLength((1024 * 25));
        FilteredKahaDBPersistenceAdapter filtered = new FilteredKahaDBPersistenceAdapter();
        StoreUsage storeUsage = new StoreUsage();
        storeUsage.setPercentLimit(50);
        storeUsage.setTotal((512 * 1024));
        filtered.setUsage(storeUsage);
        filtered.setDestination(queueA);
        filtered.setPersistenceAdapter(kahaStore);
        List<FilteredKahaDBPersistenceAdapter> stores = new ArrayList<>();
        stores.add(filtered);
        persistenceAdapter.setFilteredPersistenceAdapters(stores);
        BrokerService brokerService = createBroker(persistenceAdapter);
        brokerService.getSystemUsage().getStoreUsage().setTotal((1024 * 1024));
        brokerService.start();
        produceMessages(queueA, 20);
        MKahaDBStoreLimitTest.LOG.info(((("Store global u: " + (broker.getSystemUsage().getStoreUsage().getUsage())) + ", %:") + (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
        Assert.assertTrue("some usage", ((broker.getSystemUsage().getStoreUsage().getUsage()) > 0));
        BaseDestination baseDestinationA = ((BaseDestination) (broker.getRegionBroker().getDestinationMap().get(queueA)));
        MKahaDBStoreLimitTest.LOG.info(((("Store A u: " + (baseDestinationA.getSystemUsage().getStoreUsage().getUsage())) + ", %: ") + (baseDestinationA.getSystemUsage().getStoreUsage().getPercentUsage())));
        Assert.assertTrue("limited store has more % usage than parent", ((baseDestinationA.getSystemUsage().getStoreUsage().getPercentUsage()) > (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
    }

    @Test
    public void testExplicitAdapterBlockingProducer() throws Exception {
        MultiKahaDBPersistenceAdapter persistenceAdapter = new MultiKahaDBPersistenceAdapter();
        KahaDBPersistenceAdapter kahaStore = new KahaDBPersistenceAdapter();
        kahaStore.setJournalMaxFileLength((1024 * 8));
        kahaStore.setIndexDirectory(new File(IOHelper.getDefaultDataDirectory()));
        FilteredKahaDBPersistenceAdapter filtered = new FilteredKahaDBPersistenceAdapter();
        StoreUsage storeUsage = new StoreUsage();
        storeUsage.setLimit((44 * 1024));
        filtered.setUsage(storeUsage);
        filtered.setDestination(queueA);
        filtered.setPersistenceAdapter(kahaStore);
        List<FilteredKahaDBPersistenceAdapter> stores = new ArrayList<>();
        stores.add(filtered);
        persistenceAdapter.setFilteredPersistenceAdapters(stores);
        BrokerService brokerService = createBroker(persistenceAdapter);
        brokerService.start();
        final AtomicBoolean done = new AtomicBoolean();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    produceMessages(queueA, 20);
                    done.set(true);
                } catch (Exception ignored) {
                }
            }
        });
        Assert.assertTrue("some messages got to dest", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                BaseDestination baseDestinationA = ((BaseDestination) (broker.getRegionBroker().getDestinationMap().get(queueA)));
                return (baseDestinationA != null) && ((baseDestinationA.getDestinationStatistics().getMessages().getCount()) > 4L);
            }
        }));
        BaseDestination baseDestinationA = ((BaseDestination) (broker.getRegionBroker().getDestinationMap().get(queueA)));
        // loop till producer stalled
        long enqueues = 0L;
        do {
            enqueues = baseDestinationA.getDestinationStatistics().getEnqueues().getCount();
            MKahaDBStoreLimitTest.LOG.info(("Dest Enqueues: " + enqueues));
            TimeUnit.MILLISECONDS.sleep(500);
        } while (enqueues != (baseDestinationA.getDestinationStatistics().getEnqueues().getCount()) );
        Assert.assertFalse("expect producer to block", done.get());
        MKahaDBStoreLimitTest.LOG.info(((("Store global u: " + (broker.getSystemUsage().getStoreUsage().getUsage())) + ", %:") + (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
        Assert.assertTrue("some usage", ((broker.getSystemUsage().getStoreUsage().getUsage()) > 0));
        MKahaDBStoreLimitTest.LOG.info(((("Store A u: " + (baseDestinationA.getSystemUsage().getStoreUsage().getUsage())) + ", %: ") + (baseDestinationA.getSystemUsage().getStoreUsage().getPercentUsage())));
        Assert.assertTrue("limited store has more % usage than parent", ((baseDestinationA.getSystemUsage().getStoreUsage().getPercentUsage()) > (broker.getSystemUsage().getStoreUsage().getPercentUsage())));
        executor.shutdownNow();
    }
}

