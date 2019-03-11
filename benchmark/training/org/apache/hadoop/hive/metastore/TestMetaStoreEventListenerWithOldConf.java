/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.events.ConfigChangeEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Mostly same tests as TestMetaStoreEventListener, but using old hive conf values instead of new
 * metastore conf values.
 */
@Category(MetastoreUnitTest.class)
public class TestMetaStoreEventListenerWithOldConf {
    private Configuration conf;

    private static final String metaConfKey = "hive.metastore.partition.name.whitelist.pattern";

    private static final String metaConfVal = "";

    @Test
    public void testMetaConfNotifyListenersClosingClient() throws Exception {
        HiveMetaStoreClient closingClient = new HiveMetaStoreClient(conf, null);
        closingClient.setMetaConf(TestMetaStoreEventListenerWithOldConf.metaConfKey, "[test pattern modified]");
        ConfigChangeEvent event = ((ConfigChangeEvent) (DummyListener.getLastEvent()));
        Assert.assertEquals(event.getOldValue(), TestMetaStoreEventListenerWithOldConf.metaConfVal);
        Assert.assertEquals(event.getNewValue(), "[test pattern modified]");
        closingClient.close();
        Thread.sleep((2 * 1000));
        event = ((ConfigChangeEvent) (DummyListener.getLastEvent()));
        Assert.assertEquals(event.getOldValue(), "[test pattern modified]");
        Assert.assertEquals(event.getNewValue(), TestMetaStoreEventListenerWithOldConf.metaConfVal);
    }

    @Test
    public void testMetaConfNotifyListenersNonClosingClient() throws Exception {
        HiveMetaStoreClient nonClosingClient = new HiveMetaStoreClient(conf, null);
        nonClosingClient.setMetaConf(TestMetaStoreEventListenerWithOldConf.metaConfKey, "[test pattern modified]");
        ConfigChangeEvent event = ((ConfigChangeEvent) (DummyListener.getLastEvent()));
        Assert.assertEquals(event.getOldValue(), TestMetaStoreEventListenerWithOldConf.metaConfVal);
        Assert.assertEquals(event.getNewValue(), "[test pattern modified]");
        // This should also trigger meta listener notification via TServerEventHandler#deleteContext
        nonClosingClient.getTTransport().close();
        Thread.sleep((2 * 1000));
        event = ((ConfigChangeEvent) (DummyListener.getLastEvent()));
        Assert.assertEquals(event.getOldValue(), "[test pattern modified]");
        Assert.assertEquals(event.getNewValue(), TestMetaStoreEventListenerWithOldConf.metaConfVal);
    }

    @Test
    public void testMetaConfDuplicateNotification() throws Exception {
        HiveMetaStoreClient closingClient = new HiveMetaStoreClient(conf, null);
        closingClient.setMetaConf(TestMetaStoreEventListenerWithOldConf.metaConfKey, TestMetaStoreEventListenerWithOldConf.metaConfVal);
        int beforeCloseNotificationEventCounts = DummyListener.notifyList.size();
        closingClient.close();
        Thread.sleep((2 * 1000));
        int afterCloseNotificationEventCounts = DummyListener.notifyList.size();
        // Setting key to same value, should not trigger configChange event during shutdown
        Assert.assertEquals(beforeCloseNotificationEventCounts, afterCloseNotificationEventCounts);
    }

    @Test
    public void testMetaConfSameHandler() throws Exception {
        HiveMetaStoreClient closingClient = new HiveMetaStoreClient(conf, null);
        closingClient.setMetaConf(TestMetaStoreEventListenerWithOldConf.metaConfKey, "[test pattern modified]");
        ConfigChangeEvent event = ((ConfigChangeEvent) (DummyListener.getLastEvent()));
        int beforeCloseNotificationEventCounts = DummyListener.notifyList.size();
        IHMSHandler beforeHandler = event.getHandler();
        closingClient.close();
        Thread.sleep((2 * 1000));
        event = ((ConfigChangeEvent) (DummyListener.getLastEvent()));
        int afterCloseNotificationEventCounts = DummyListener.notifyList.size();
        IHMSHandler afterHandler = event.getHandler();
        // Meta-conf cleanup should trigger an event to listener
        Assert.assertNotSame(beforeCloseNotificationEventCounts, afterCloseNotificationEventCounts);
        // Both the handlers should be same
        Assert.assertEquals(beforeHandler, afterHandler);
    }
}

