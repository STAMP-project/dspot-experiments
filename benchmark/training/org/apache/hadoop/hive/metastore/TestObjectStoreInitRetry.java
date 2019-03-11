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


import ConfVars.CONNECTION_DRIVER;
import ConfVars.CONNECT_URL_KEY;
import ConfVars.HMS_HANDLER_ATTEMPTS;
import ConfVars.HMS_HANDLER_INTERVAL;
import ConfVars.TRY_DIRECT_SQL;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MetastoreCheckinTest.class)
public class TestObjectStoreInitRetry {
    private static final Logger LOG = LoggerFactory.getLogger(TestObjectStoreInitRetry.class);

    private static int injectConnectFailure = 0;

    protected static Configuration conf;

    @Test
    public void testObjStoreRetry() throws Exception {
        TestObjectStoreInitRetry.conf = MetastoreConf.newMetastoreConf();
        MetastoreConf.setLongVar(TestObjectStoreInitRetry.conf, HMS_HANDLER_ATTEMPTS, 4);
        MetastoreConf.setTimeVar(TestObjectStoreInitRetry.conf, HMS_HANDLER_INTERVAL, 1, TimeUnit.SECONDS);
        MetastoreConf.setVar(TestObjectStoreInitRetry.conf, CONNECTION_DRIVER, FakeDerby.class.getName());
        MetastoreConf.setBoolVar(TestObjectStoreInitRetry.conf, TRY_DIRECT_SQL, true);
        String jdbcUrl = MetastoreConf.getVar(TestObjectStoreInitRetry.conf, CONNECT_URL_KEY);
        jdbcUrl = jdbcUrl.replace("derby", "fderby");
        MetastoreConf.setVar(TestObjectStoreInitRetry.conf, CONNECT_URL_KEY, jdbcUrl);
        MetaStoreTestUtils.setConfForStandloneMode(TestObjectStoreInitRetry.conf);
        FakeDerby fd = new FakeDerby();
        ObjectStore objStore = new ObjectStore();
        Exception savE = null;
        try {
            TestObjectStoreInitRetry.setInjectConnectFailure(5);
            objStore.setConf(TestObjectStoreInitRetry.conf);
            Assert.fail();
        } catch (Exception e) {
            TestObjectStoreInitRetry.LOG.info("Caught exception ", e);
            savE = e;
        }
        /* A note on retries.

        We've configured a total of 4 attempts.
        5 - 4 == 1 connect failure simulation count left after this.
         */
        Assert.assertEquals(1, TestObjectStoreInitRetry.getInjectConnectFailure());
        Assert.assertNotNull(savE);
        TestObjectStoreInitRetry.setInjectConnectFailure(0);
        objStore.setConf(TestObjectStoreInitRetry.conf);
        Assert.assertEquals(0, TestObjectStoreInitRetry.getInjectConnectFailure());
    }
}

