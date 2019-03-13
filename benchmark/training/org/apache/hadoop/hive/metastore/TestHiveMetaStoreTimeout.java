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


import ConfVars.CLIENT_SOCKET_TIMEOUT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static HiveMetaStore.TEST_TIMEOUT_VALUE;


/**
 * Test long running request timeout functionality in MetaStore Server
 * HiveMetaStore.HMSHandler.create_database() is used to simulate a long running method.
 */
@Category(MetastoreCheckinTest.class)
public class TestHiveMetaStoreTimeout {
    protected static HiveMetaStoreClient client;

    protected static Configuration conf;

    protected static Warehouse warehouse;

    @Test
    public void testNoTimeout() throws Exception {
        TEST_TIMEOUT_VALUE = 250;
        String dbName = "db";
        TestHiveMetaStoreTimeout.client.dropDatabase(dbName, true, true);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStoreTimeout.client, TestHiveMetaStoreTimeout.conf);
        TestHiveMetaStoreTimeout.client.dropDatabase(dbName, true, true);
    }

    @Test
    public void testTimeout() throws Exception {
        TEST_TIMEOUT_VALUE = 2 * 1000;
        String dbName = "db";
        TestHiveMetaStoreTimeout.client.dropDatabase(dbName, true, true);
        Database db = new DatabaseBuilder().setName(dbName).build(TestHiveMetaStoreTimeout.conf);
        try {
            TestHiveMetaStoreTimeout.client.createDatabase(db);
            Assert.fail("should throw timeout exception.");
        } catch (MetaException e) {
            Assert.assertTrue("unexpected MetaException", e.getMessage().contains(("Timeout when " + "executing method: create_database")));
        }
        // restore
        TEST_TIMEOUT_VALUE = 1;
    }

    @Test
    public void testResetTimeout() throws Exception {
        TEST_TIMEOUT_VALUE = 250;
        String dbName = "db";
        // no timeout before reset
        TestHiveMetaStoreTimeout.client.dropDatabase(dbName, true, true);
        Database db = new DatabaseBuilder().setName(dbName).build(TestHiveMetaStoreTimeout.conf);
        try {
            TestHiveMetaStoreTimeout.client.createDatabase(db);
        } catch (MetaException e) {
            Assert.fail(("should not throw timeout exception: " + (e.getMessage())));
        }
        TestHiveMetaStoreTimeout.client.dropDatabase(dbName, true, true);
        // reset
        TEST_TIMEOUT_VALUE = 2000;
        TestHiveMetaStoreTimeout.client.setMetaConf(CLIENT_SOCKET_TIMEOUT.getVarname(), "1s");
        // timeout after reset
        try {
            TestHiveMetaStoreTimeout.client.createDatabase(db);
            Assert.fail("should throw timeout exception.");
        } catch (MetaException e) {
            Assert.assertTrue("unexpected MetaException", e.getMessage().contains(("Timeout when " + "executing method: create_database")));
        }
        // restore
        TestHiveMetaStoreTimeout.client.dropDatabase(dbName, true, true);
        TestHiveMetaStoreTimeout.client.setMetaConf(CLIENT_SOCKET_TIMEOUT.getVarname(), "10s");
    }
}

