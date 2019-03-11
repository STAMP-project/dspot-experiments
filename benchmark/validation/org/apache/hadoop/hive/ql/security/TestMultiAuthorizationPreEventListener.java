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
package org.apache.hadoop.hive.ql.security;


import DummyHiveMetastoreAuthorizationProvider.AuthCallContextType.DB;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.security.DummyHiveMetastoreAuthorizationProvider.AuthCallContext;
import org.junit.Assert;
import org.junit.Test;

import static DummyHiveMetastoreAuthorizationProvider.authCalls;


/**
 * Test case for verifying that multiple
 * {@link org.apache.hadoop.hive.metastore.AuthorizationPreEventListener}s can
 * be set and they get called.
 */
public class TestMultiAuthorizationPreEventListener {
    private static HiveConf clientHiveConf;

    private static HiveMetaStoreClient msc;

    private static IDriver driver;

    @Test
    public void testMultipleAuthorizationListners() throws Exception {
        String dbName = "hive" + (this.getClass().getSimpleName().toLowerCase());
        List<AuthCallContext> authCalls = authCalls;
        int listSize = 0;
        Assert.assertEquals(listSize, authCalls.size());
        TestMultiAuthorizationPreEventListener.driver.run(("create database " + dbName));
        // verify that there are two calls because of two instances of the authorization provider
        listSize = 2;
        Assert.assertEquals(listSize, authCalls.size());
        // verify that the actual action also went through
        Database db = TestMultiAuthorizationPreEventListener.msc.getDatabase(dbName);
        listSize += 2;// 1 read database auth calls for each authorization provider

        Database dbFromEvent = ((Database) (assertAndExtractSingleObjectFromEvent(listSize, authCalls, DB)));
        validateCreateDb(db, dbFromEvent);
    }
}

