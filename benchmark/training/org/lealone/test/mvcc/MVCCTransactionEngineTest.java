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
package org.lealone.test.mvcc;


import LogSyncService.LOG_SYNC_TYPE_PERIODIC;
import Transaction.STATUS_CLOSED;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.lealone.storage.Storage;
import org.lealone.test.TestBase;
import org.lealone.transaction.Transaction;
import org.lealone.transaction.TransactionEngine;
import org.lealone.transaction.TransactionMap;


public class MVCCTransactionEngineTest extends TestBase {
    @Test
    public void testCheckpoint() {
        Map<String, String> config = MVCCTransactionEngineTest.getDefaultConfig();
        config.put("committed_data_cache_size_in_mb", "1");
        config.put("checkpoint_service_loop_interval", "100");// 100ms

        config.put("log_sync_type", LOG_SYNC_TYPE_PERIODIC);
        TransactionEngine te = MVCCTransactionEngineTest.getTransactionEngine(config);
        Storage storage = MVCCTransactionEngineTest.getStorage();
        Transaction t1 = te.beginTransaction(false, false);
        TransactionMap<String, String> map = t1.openMap("testCheckpoint", storage);
        map.remove();
        map = t1.openMap("testCheckpoint", storage);
        Assert.assertEquals(0, map.getDiskSpaceUsed());
        Assert.assertEquals(0, map.size());
        for (int i = 1; i <= 50000; i++) {
            map.put(("key" + i), ("value" + i));
        }
        t1.commit();
        Assert.assertEquals(50000, map.size());
        try {
            Thread.sleep(2000);// ???????????????

        } catch (InterruptedException e) {
        }
        Assert.assertTrue(((map.getDiskSpaceUsed()) > 0));
        map.remove();
        Transaction t2 = te.beginTransaction(false, false);
        map = t2.openMap("testCheckpoint", storage);
        Assert.assertEquals(0, map.getDiskSpaceUsed());
        map.put("abc", "value123");
        t2.commit();
        te.checkpoint();
        Assert.assertTrue(((map.getDiskSpaceUsed()) > 0));
    }

    @Test
    public void run() {
        TransactionEngine te = MVCCTransactionEngineTest.getTransactionEngine(false);
        Storage storage = MVCCTransactionEngineTest.getStorage();
        Transaction t = te.beginTransaction(false, false);
        TransactionMap<String, String> map = t.openMap("test", storage);
        map.clear();
        map.put("1", "a");
        map.put("2", "b");
        Assert.assertEquals("a", map.get("1"));
        Assert.assertEquals("b", map.get("2"));
        Assert.assertEquals(2, map.size());
        t.rollback();
        Assert.assertEquals(0, map.size());
        try {
            map.put("1", "a");// ??rollback?commit????????java.lang.IllegalStateException: Transaction is closed

            Assert.fail();
        } catch (IllegalStateException e) {
        }
        t = te.beginTransaction(false, false);
        map = map.getInstance(t);
        Assert.assertNull(map.get("1"));
        Assert.assertNull(map.get("2"));
        map.put("1", "a");
        map.put("2", "b");
        t.commit();
        map.get("1");// ????commit??????????????????

        try {
            map.put("1", "a");// ??rollback?commit????????java.lang.IllegalStateException: Transaction is closed

            Assert.fail();
        } catch (IllegalStateException e) {
        }
        Assert.assertEquals(2, map.size());
        Transaction t2 = te.beginTransaction(false, false);
        map = map.getInstance(t2);
        map.put("3", "c");
        map.put("4", "d");
        Assert.assertEquals(4, map.size());
        Transaction t3 = te.beginTransaction(false, false);
        map = map.getInstance(t3);
        map.put("5", "f");
        Assert.assertEquals(3, map.size());// t2??????????put???

        Transaction t4 = te.beginTransaction(false, false);
        map = map.getInstance(t4);
        map.remove("1");
        Assert.assertEquals(1, map.size());
        t4.commit();
        Transaction t5 = te.beginTransaction(false, false);
        map = map.getInstance(t5);
        map.put("6", "g");
        Assert.assertEquals(2, map.size());
        t5.prepareCommit();// ???????LogSyncService???sync????????????

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        Assert.assertEquals(STATUS_CLOSED, t5.getStatus());
        te.close();
    }
}

