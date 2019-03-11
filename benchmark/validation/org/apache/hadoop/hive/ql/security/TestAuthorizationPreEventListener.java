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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.security.DummyHiveMetastoreAuthorizationProvider.AuthCallContext;
import org.junit.Assert;

import static DummyHiveMetastoreAuthorizationProvider.authCalls;


/**
 * TestAuthorizationPreEventListener. Test case for
 * {@link org.apache.hadoop.hive.ql.security.authorization.AuthorizationPreEventListener} and
 * {@link org.apache.hadoop.hive.metastore.MetaStorePreEventListener}
 */
public class TestAuthorizationPreEventListener extends TestCase {
    private HiveConf clientHiveConf;

    private HiveMetaStoreClient msc;

    private IDriver driver;

    public void testListener() throws Exception {
        String dbName = "hive3705";
        String tblName = "tmptbl";
        String renamed = "tmptbl2";
        int listSize = 0;
        List<AuthCallContext> authCalls = authCalls;
        TestCase.assertEquals(authCalls.size(), listSize);
        driver.run(("create database " + dbName));
        listSize++;
        Database dbFromEvent = ((Database) (assertAndExtractSingleObjectFromEvent(listSize, authCalls, DB)));
        Database db = msc.getDatabase(dbName);
        validateCreateDb(db, dbFromEvent);
        driver.run(("use " + dbName));
        driver.run(String.format("create table %s (a string) partitioned by (b string)", tblName));
        listSize = authCalls.size();
        Table tblFromEvent = getTTable();
        Table tbl = msc.getTable(dbName, tblName);
        Assert.assertTrue(tbl.isSetId());
        tbl.unsetId();
        validateCreateTable(tbl, tblFromEvent);
        driver.run("alter table tmptbl add partition (b='2011')");
        listSize = authCalls.size();
        Partition ptnFromEvent = getTPartition();
        Partition part = msc.getPartition("hive3705", "tmptbl", "b=2011");
        validateAddPartition(part, ptnFromEvent);
        driver.run(String.format("alter table %s touch partition (%s)", tblName, "b='2011'"));
        listSize = authCalls.size();
        Partition ptnFromEventAfterAlter = getTPartition();
        // the partition did not change,
        // so the new partition should be similar to the original partition
        Partition modifiedP = msc.getPartition(dbName, tblName, "b=2011");
        validateAlterPartition(part, modifiedP, ptnFromEventAfterAlter.getDbName(), ptnFromEventAfterAlter.getTableName(), ptnFromEventAfterAlter.getValues(), ptnFromEventAfterAlter);
        List<String> part_vals = new ArrayList<String>();
        part_vals.add("c=2012");
        listSize = authCalls.size();
        Partition newPart = msc.appendPartition(dbName, tblName, part_vals);
        listSize++;
        Partition newPtnFromEvent = getTPartition();
        validateAddPartition(newPart, newPtnFromEvent);
        driver.run(String.format("alter table %s rename to %s", tblName, renamed));
        listSize = authCalls.size();
        Table renamedTableFromEvent = getTTable();
        Table renamedTable = msc.getTable(dbName, renamed);
        validateAlterTable(tbl, renamedTable, renamedTableFromEvent, renamedTable);
        TestCase.assertFalse(tbl.getTableName().equals(renamedTable.getTableName()));
        // change the table name back
        driver.run(String.format("alter table %s rename to %s", renamed, tblName));
        driver.run(String.format("alter table %s drop partition (b='2011')", tblName));
        listSize = authCalls.size();
        Partition ptnFromDropPartition = getTPartition();
        validateDropPartition(modifiedP, ptnFromDropPartition);
        driver.run(("drop table " + tblName));
        listSize = authCalls.size();
        Table tableFromDropTableEvent = getTTable();
        validateDropTable(tbl, tableFromDropTableEvent);
        // verify that we can create a table with IF/OF to some custom non-existent format
        Table tCustom = tbl.deepCopy();
        tCustom.getSd().setInputFormat("org.apache.hive.dummy.DoesNotExistInputFormat");
        tCustom.getSd().setOutputFormat("org.apache.hive.dummy.DoesNotExistOutputFormat");
        if ((tCustom.getSd().getSerdeInfo()) == null) {
            tCustom.getSd().setSerdeInfo(new SerDeInfo("dummy", "org.apache.hive.dummy.DoesNotExistSerDe", new HashMap<String, String>()));
        } else {
            tCustom.getSd().getSerdeInfo().setSerializationLib("org.apache.hive.dummy.DoesNotExistSerDe");
        }
        tCustom.setTableName(((tbl.getTableName()) + "_custom"));
        listSize = authCalls.size();
        msc.createTable(tCustom);
        listSize++;
        Table customCreatedTableFromEvent = getTTable();
        Table customCreatedTable = msc.getTable(tCustom.getDbName(), tCustom.getTableName());
        validateCreateTable(tCustom, customCreatedTable);
        validateCreateTable(tCustom, customCreatedTableFromEvent);
        TestCase.assertEquals(tCustom.getSd().getInputFormat(), customCreatedTable.getSd().getInputFormat());
        TestCase.assertEquals(tCustom.getSd().getOutputFormat(), customCreatedTable.getSd().getOutputFormat());
        TestCase.assertEquals(tCustom.getSd().getSerdeInfo().getSerializationLib(), customCreatedTable.getSd().getSerdeInfo().getSerializationLib());
        TestCase.assertEquals(tCustom.getSd().getInputFormat(), customCreatedTableFromEvent.getSd().getInputFormat());
        TestCase.assertEquals(tCustom.getSd().getOutputFormat(), customCreatedTableFromEvent.getSd().getOutputFormat());
        TestCase.assertEquals(tCustom.getSd().getSerdeInfo().getSerializationLib(), customCreatedTableFromEvent.getSd().getSerdeInfo().getSerializationLib());
        listSize = authCalls.size();
        msc.dropTable(tCustom.getDbName(), tCustom.getTableName());
        listSize += 2;
        Table table2FromDropTableEvent = getTTable();
        validateDropTable(tCustom, table2FromDropTableEvent);
        // Test ALTER DATABASE SET LOCATION.
        String oldDatabaseLocation = db.getLocationUri();
        String newDatabaseLocation = oldDatabaseLocation.replace(db.getName(), ("new." + (db.getName())));
        driver.run((((("ALTER DATABASE " + dbName) + " SET LOCATION \"") + newDatabaseLocation) + "\""));
        listSize = authCalls.size();
        Database dbFromAlterDatabaseEvent = ((Database) (assertAndExtractSingleObjectFromEvent(listSize, authCalls, DB)));
        validateAlterDb(db, dbFromAlterDatabaseEvent);
        // Reset database location.
        driver.run((((("ALTER DATABASE " + dbName) + " SET LOCATION \"") + oldDatabaseLocation) + "\""));
        // Test DROP DATABASE.
        driver.run(("drop database " + dbName));
        listSize = authCalls.size();
        Database dbFromDropDatabaseEvent = ((Database) (assertAndExtractSingleObjectFromEvent(listSize, authCalls, DB)));
        validateDropDb(db, dbFromDropDatabaseEvent);
    }
}

