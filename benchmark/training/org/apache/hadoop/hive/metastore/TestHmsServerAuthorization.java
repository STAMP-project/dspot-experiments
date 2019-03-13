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


import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.events.PreEventContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the filtering behavior at HMS client and HMS server. The configuration at each test
 * changes, and therefore HMS client and server are created for each test case
 */
@Category(MetastoreUnitTest.class)
public class TestHmsServerAuthorization {
    /**
     * Implementation of MetaStorePreEventListener that throws MetaException when configured in
     * its function onEvent().
     */
    public static class DummyAuthorizationListenerImpl extends MetaStorePreEventListener {
        private static volatile boolean throwExceptionAtCall = false;

        public DummyAuthorizationListenerImpl(Configuration config) {
            super(config);
        }

        @Override
        public void onEvent(PreEventContext context) throws InvalidOperationException, MetaException, NoSuchObjectException {
            if (TestHmsServerAuthorization.DummyAuthorizationListenerImpl.throwExceptionAtCall) {
                throw new MetaException("Authorization fails");
            }
        }
    }

    private static HiveMetaStoreClient client;

    private static Configuration conf;

    private static final int DEFAULT_LIMIT_PARTITION_REQUEST = 100;

    private static String dbName1 = "testdb1";

    private static String dbName2 = "testdb2";

    private static final String TAB1 = "tab1";

    private static final String TAB2 = "tab2";

    /**
     * Test the pre-event listener is called in function get_fields at HMS server.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetFields() throws Exception {
        TestHmsServerAuthorization.dbName1 = "db_test_get_fields_1";
        TestHmsServerAuthorization.dbName2 = "db_test_get_fields_2";
        creatEnv(TestHmsServerAuthorization.conf);
        // enable throwing exception, so we can check pre-envent listener is called
        TestHmsServerAuthorization.DummyAuthorizationListenerImpl.throwExceptionAtCall = true;
        try {
            List<FieldSchema> tableSchema = TestHmsServerAuthorization.client.getFields(TestHmsServerAuthorization.dbName1, TestHmsServerAuthorization.TAB1);
            Assert.fail("getFields() should fail with throw exception mode at server side");
        } catch (MetaException ex) {
            boolean isMessageAuthorization = ex.getMessage().contains("Authorization fails");
            Assert.assertEquals(true, isMessageAuthorization);
        }
    }
}

