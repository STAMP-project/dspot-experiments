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


import Warehouse.DEFAULT_CATALOG_NAME;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * TestMetaStoreEventListener. Test case for
 * {@link org.apache.hadoop.hive.metastore.MetaStoreEndFunctionListener}
 */
@Category(MetastoreUnitTest.class)
public class TestMetaStoreEndFunctionListener {
    private Configuration conf;

    private HiveMetaStoreClient msc;

    @Test
    public void testEndFunctionListener() throws Exception {
        /* Objective here is to ensure that when exceptions are thrown in HiveMetaStore in API methods
        they bubble up and are stored in the MetaStoreEndFunctionContext objects
         */
        String dbName = "hive3524";
        String tblName = "tmptbl";
        int listSize;
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(DEFAULT_CATALOG_NAME).create(msc, conf);
        try {
            msc.getDatabase("UnknownDB");
        } catch (Exception e) {
            // All good
        }
        listSize = DummyEndFunctionListener.funcNameList.size();
        String func_name = DummyEndFunctionListener.funcNameList.get((listSize - 1));
        MetaStoreEndFunctionContext context = DummyEndFunctionListener.contextList.get((listSize - 1));
        Assert.assertEquals(func_name, "get_database");
        Assert.assertFalse(context.isSuccess());
        Exception e = context.getException();
        Assert.assertTrue((e != null));
        Assert.assertTrue((e instanceof NoSuchObjectException));
        Assert.assertEquals(context.getInputTableName(), null);
        String unknownTable = "UnknownTable";
        new TableBuilder().inDb(db).setTableName(tblName).addCol("a", "string").addPartCol("b", "string").create(msc, conf);
        try {
            msc.getTable(dbName, unknownTable);
        } catch (Exception e1) {
            // All good
        }
        listSize = DummyEndFunctionListener.funcNameList.size();
        func_name = DummyEndFunctionListener.funcNameList.get((listSize - 1));
        context = DummyEndFunctionListener.contextList.get((listSize - 1));
        Assert.assertEquals(func_name, "get_table");
        Assert.assertFalse(context.isSuccess());
        e = context.getException();
        Assert.assertTrue((e != null));
        Assert.assertTrue((e instanceof NoSuchObjectException));
        Assert.assertEquals(context.getInputTableName(), unknownTable);
        try {
            msc.getPartition("hive3524", tblName, "b=2012");
        } catch (Exception e2) {
            // All good
        }
        listSize = DummyEndFunctionListener.funcNameList.size();
        func_name = DummyEndFunctionListener.funcNameList.get((listSize - 1));
        context = DummyEndFunctionListener.contextList.get((listSize - 1));
        Assert.assertEquals(func_name, "get_partition_by_name");
        Assert.assertFalse(context.isSuccess());
        e = context.getException();
        Assert.assertTrue((e != null));
        Assert.assertTrue((e instanceof NoSuchObjectException));
        Assert.assertEquals(context.getInputTableName(), tblName);
        try {
            msc.dropTable(dbName, unknownTable);
        } catch (Exception e4) {
            // All good
        }
        listSize = DummyEndFunctionListener.funcNameList.size();
        func_name = DummyEndFunctionListener.funcNameList.get((listSize - 1));
        context = DummyEndFunctionListener.contextList.get((listSize - 1));
        Assert.assertEquals(func_name, "get_table");
        Assert.assertFalse(context.isSuccess());
        e = context.getException();
        Assert.assertTrue((e != null));
        Assert.assertTrue((e instanceof NoSuchObjectException));
        Assert.assertEquals(context.getInputTableName(), "UnknownTable");
    }
}

