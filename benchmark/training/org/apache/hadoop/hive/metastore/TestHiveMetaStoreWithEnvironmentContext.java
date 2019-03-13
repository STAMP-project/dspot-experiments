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


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.EnvironmentContext;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.events.AddPartitionEvent;
import org.apache.hadoop.hive.metastore.events.AlterTableEvent;
import org.apache.hadoop.hive.metastore.events.CreateDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.CreateTableEvent;
import org.apache.hadoop.hive.metastore.events.DropDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.DropPartitionEvent;
import org.apache.hadoop.hive.metastore.events.DropTableEvent;
import org.apache.hadoop.hive.metastore.events.ListenerEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * TestHiveMetaStoreWithEnvironmentContext. Test case for _with_environment_context
 * calls in {@link org.apache.hadoop.hive.metastore.HiveMetaStore}
 */
@Category(MetastoreUnitTest.class)
public class TestHiveMetaStoreWithEnvironmentContext {
    private Configuration conf;

    private HiveMetaStoreClient msc;

    private EnvironmentContext envContext;

    private final Database db = new Database();

    private Table table;

    private Partition partition;

    private static final String dbName = "hive3252";

    private static final String tblName = "tmptbl";

    private static final String renamed = "tmptbl2";

    @Test
    public void testEnvironmentContext() throws Exception {
        int listSize = 0;
        List<ListenerEvent> notifyList = DummyListener.notifyList;
        Assert.assertEquals(notifyList.size(), listSize);
        msc.createDatabase(db);
        listSize++;
        Assert.assertEquals(listSize, notifyList.size());
        CreateDatabaseEvent dbEvent = ((CreateDatabaseEvent) (notifyList.get((listSize - 1))));
        assert dbEvent.getStatus();
        msc.createTable(table, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        CreateTableEvent tblEvent = ((CreateTableEvent) (notifyList.get((listSize - 1))));
        assert tblEvent.getStatus();
        Assert.assertEquals(envContext, tblEvent.getEnvironmentContext());
        table = msc.getTable(TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.tblName);
        partition.getSd().setLocation(((table.getSd().getLocation()) + "/part1"));
        msc.add_partition(partition, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        AddPartitionEvent partEvent = ((AddPartitionEvent) (notifyList.get((listSize - 1))));
        assert partEvent.getStatus();
        Assert.assertEquals(envContext, partEvent.getEnvironmentContext());
        List<String> partVals = new ArrayList<>();
        partVals.add("2012");
        msc.appendPartition(TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.tblName, partVals, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        AddPartitionEvent appendPartEvent = ((AddPartitionEvent) (notifyList.get((listSize - 1))));
        assert appendPartEvent.getStatus();
        Assert.assertEquals(envContext, appendPartEvent.getEnvironmentContext());
        table.setTableName(TestHiveMetaStoreWithEnvironmentContext.renamed);
        msc.alter_table_with_environmentContext(TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.tblName, table, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        AlterTableEvent alterTableEvent = ((AlterTableEvent) (notifyList.get((listSize - 1))));
        assert alterTableEvent.getStatus();
        Assert.assertEquals(envContext, alterTableEvent.getEnvironmentContext());
        table.setTableName(TestHiveMetaStoreWithEnvironmentContext.tblName);
        msc.alter_table_with_environmentContext(TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.renamed, table, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        List<String> dropPartVals = new ArrayList<>();
        dropPartVals.add("2011");
        msc.dropPartition(TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.tblName, dropPartVals, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        DropPartitionEvent dropPartEvent = ((DropPartitionEvent) (notifyList.get((listSize - 1))));
        assert dropPartEvent.getStatus();
        Assert.assertEquals(envContext, dropPartEvent.getEnvironmentContext());
        msc.dropPartition(TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.tblName, "b=2012", true, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        DropPartitionEvent dropPartByNameEvent = ((DropPartitionEvent) (notifyList.get((listSize - 1))));
        assert dropPartByNameEvent.getStatus();
        Assert.assertEquals(envContext, dropPartByNameEvent.getEnvironmentContext());
        msc.dropTable(Warehouse.DEFAULT_CATALOG_NAME, TestHiveMetaStoreWithEnvironmentContext.dbName, TestHiveMetaStoreWithEnvironmentContext.tblName, true, false, envContext);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        DropTableEvent dropTblEvent = ((DropTableEvent) (notifyList.get((listSize - 1))));
        assert dropTblEvent.getStatus();
        Assert.assertEquals(envContext, dropTblEvent.getEnvironmentContext());
        msc.dropDatabase(TestHiveMetaStoreWithEnvironmentContext.dbName);
        listSize++;
        Assert.assertEquals(notifyList.size(), listSize);
        DropDatabaseEvent dropDB = ((DropDatabaseEvent) (notifyList.get((listSize - 1))));
        assert dropDB.getStatus();
    }
}

