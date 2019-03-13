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


import ConfVars.METASTORE_CLIENT_FILTER_ENABLED;
import ConfVars.METASTORE_SERVER_FILTER_ENABLED;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.TableMeta;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the filtering behavior at HMS client and HMS server. The configuration at each test
 * changes, and therefore HMS client and server are created for each test case
 */
@Category(MetastoreUnitTest.class)
public class TestFilterHooks {
    public static class DummyMetaStoreFilterHookImpl implements MetaStoreFilterHook {
        private static boolean blockResults = false;

        public DummyMetaStoreFilterHookImpl(Configuration conf) {
        }

        @Override
        public List<String> filterDatabases(List<String> dbList) throws MetaException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                return new ArrayList<>();
            }
            return dbList;
        }

        @Override
        public Database filterDatabase(Database dataBase) throws NoSuchObjectException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                throw new NoSuchObjectException("Blocked access");
            }
            return dataBase;
        }

        @Override
        public List<String> filterTableNames(String catName, String dbName, List<String> tableList) throws MetaException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                return new ArrayList<>();
            }
            return tableList;
        }

        @Override
        public Table filterTable(Table table) throws NoSuchObjectException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                throw new NoSuchObjectException("Blocked access");
            }
            return table;
        }

        @Override
        public List<Table> filterTables(List<Table> tableList) throws MetaException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                return new ArrayList<>();
            }
            return tableList;
        }

        @Override
        public List<TableMeta> filterTableMetas(String catName, String dbName, List<TableMeta> tableMetas) throws MetaException {
            return tableMetas;
        }

        @Override
        public List<Partition> filterPartitions(List<Partition> partitionList) throws MetaException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                return new ArrayList<>();
            }
            return partitionList;
        }

        @Override
        public List<PartitionSpec> filterPartitionSpecs(List<PartitionSpec> partitionSpecList) throws MetaException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                return new ArrayList<>();
            }
            return partitionSpecList;
        }

        @Override
        public Partition filterPartition(Partition partition) throws NoSuchObjectException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                throw new NoSuchObjectException("Blocked access");
            }
            return partition;
        }

        @Override
        public List<String> filterPartitionNames(String catName, String dbName, String tblName, List<String> partitionNames) throws MetaException {
            if (TestFilterHooks.DummyMetaStoreFilterHookImpl.blockResults) {
                return new ArrayList<>();
            }
            return partitionNames;
        }
    }

    protected static HiveMetaStoreClient client;

    protected static Configuration conf;

    protected static Warehouse warehouse;

    private static final int DEFAULT_LIMIT_PARTITION_REQUEST = 100;

    private static String DBNAME1 = "testdb1";

    private static String DBNAME2 = "testdb2";

    private static final String TAB1 = "tab1";

    private static final String TAB2 = "tab2";

    /**
     * The default configuration should be disable filtering at HMS server
     * Disable the HMS client side filtering in order to see HMS server filtering behavior
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHMSServerWithoutFilter() throws Exception {
        MetastoreConf.setBoolVar(TestFilterHooks.conf, METASTORE_CLIENT_FILTER_ENABLED, false);
        TestFilterHooks.DBNAME1 = "db_testHMSServerWithoutFilter_1";
        TestFilterHooks.DBNAME2 = "db_testHMSServerWithoutFilter_2";
        creatEnv(TestFilterHooks.conf);
        Assert.assertNotNull(TestFilterHooks.client.getTable(TestFilterHooks.DBNAME1, TestFilterHooks.TAB1));
        Assert.assertEquals(2, TestFilterHooks.client.getTables(TestFilterHooks.DBNAME1, "*").size());
        Assert.assertEquals(2, TestFilterHooks.client.getAllTables(TestFilterHooks.DBNAME1).size());
        Assert.assertEquals(1, TestFilterHooks.client.getTables(TestFilterHooks.DBNAME1, TestFilterHooks.TAB2).size());
        Assert.assertEquals(0, TestFilterHooks.client.getAllTables(TestFilterHooks.DBNAME2).size());
        Assert.assertNotNull(TestFilterHooks.client.getDatabase(TestFilterHooks.DBNAME1));
        Assert.assertEquals(2, TestFilterHooks.client.getDatabases("*testHMSServerWithoutFilter*").size());
        Assert.assertEquals(1, TestFilterHooks.client.getDatabases(TestFilterHooks.DBNAME1).size());
        Assert.assertNotNull(TestFilterHooks.client.getPartition(TestFilterHooks.DBNAME1, TestFilterHooks.TAB2, "name=value1"));
        Assert.assertEquals(1, TestFilterHooks.client.getPartitionsByNames(TestFilterHooks.DBNAME1, TestFilterHooks.TAB2, Lists.newArrayList("name=value1")).size());
    }

    /**
     * Enable the HMS server side filtering
     * Disable the HMS client side filtering in order to see HMS server filtering behavior
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHMSServerWithFilter() throws Exception {
        MetastoreConf.setBoolVar(TestFilterHooks.conf, METASTORE_CLIENT_FILTER_ENABLED, false);
        MetastoreConf.setBoolVar(TestFilterHooks.conf, METASTORE_SERVER_FILTER_ENABLED, true);
        TestFilterHooks.DBNAME1 = "db_testHMSServerWithFilter_1";
        TestFilterHooks.DBNAME2 = "db_testHMSServerWithFilter_2";
        creatEnv(TestFilterHooks.conf);
        testFilterForDb(true);
        testFilterForTables(true);
        testFilterForPartition(true);
    }

    /**
     * Disable filtering at HMS client
     * By default, the HMS server side filtering is diabled, so we can see HMS client filtering behavior
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHMSClientWithoutFilter() throws Exception {
        MetastoreConf.setBoolVar(TestFilterHooks.conf, METASTORE_CLIENT_FILTER_ENABLED, false);
        TestFilterHooks.DBNAME1 = "db_testHMSClientWithoutFilter_1";
        TestFilterHooks.DBNAME2 = "db_testHMSClientWithoutFilter_2";
        creatEnv(TestFilterHooks.conf);
        Assert.assertNotNull(TestFilterHooks.client.getTable(TestFilterHooks.DBNAME1, TestFilterHooks.TAB1));
        Assert.assertEquals(2, TestFilterHooks.client.getTables(TestFilterHooks.DBNAME1, "*").size());
        Assert.assertEquals(2, TestFilterHooks.client.getAllTables(TestFilterHooks.DBNAME1).size());
        Assert.assertEquals(1, TestFilterHooks.client.getTables(TestFilterHooks.DBNAME1, TestFilterHooks.TAB2).size());
        Assert.assertEquals(0, TestFilterHooks.client.getAllTables(TestFilterHooks.DBNAME2).size());
        Assert.assertNotNull(TestFilterHooks.client.getDatabase(TestFilterHooks.DBNAME1));
        Assert.assertEquals(2, TestFilterHooks.client.getDatabases("*testHMSClientWithoutFilter*").size());
        Assert.assertEquals(1, TestFilterHooks.client.getDatabases(TestFilterHooks.DBNAME1).size());
        Assert.assertNotNull(TestFilterHooks.client.getPartition(TestFilterHooks.DBNAME1, TestFilterHooks.TAB2, "name=value1"));
        Assert.assertEquals(1, TestFilterHooks.client.getPartitionsByNames(TestFilterHooks.DBNAME1, TestFilterHooks.TAB2, Lists.newArrayList("name=value1")).size());
    }

    /**
     * By default, the HMS Client side filtering is enabled
     * Disable the HMS server side filtering in order to see HMS client filtering behavior
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHMSClientWithFilter() throws Exception {
        MetastoreConf.setBoolVar(TestFilterHooks.conf, METASTORE_SERVER_FILTER_ENABLED, false);
        TestFilterHooks.DBNAME1 = "db_testHMSClientWithFilter_1";
        TestFilterHooks.DBNAME2 = "db_testHMSClientWithFilter_2";
        creatEnv(TestFilterHooks.conf);
        testFilterForDb(false);
        testFilterForTables(false);
        testFilterForPartition(false);
    }
}

