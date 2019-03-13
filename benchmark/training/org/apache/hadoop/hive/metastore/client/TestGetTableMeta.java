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
package org.apache.hadoop.hive.metastore.client;


import TableType.EXTERNAL_TABLE;
import TableType.MANAGED_TABLE;
import TableType.MATERIALIZED_VIEW;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.TableMeta;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * API tests for HMS client's getTableMeta method.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestGetTableMeta extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "testpartdb";

    private static final String TABLE_NAME = "testparttable";

    private List<TableMeta> expectedMetas = null;

    public TestGetTableMeta(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    /**
     * Testing getTableMeta(String,String,List(String)) ->
     *         get_table_meta(String,String,List(String)).
     */
    @Test
    public void testGetTableMeta() throws Exception {
        List<TableMeta> tableMetas = client.getTableMeta("asdf", "qwerty", Lists.newArrayList("zxcv"));
        assertTableMetas(new int[]{  }, tableMetas);
        tableMetas = client.getTableMeta("testpartdb_two", "vtestparttable", Lists.newArrayList());
        assertTableMetas(new int[]{ 4 }, tableMetas);
        tableMetas = client.getTableMeta("*", "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2, 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("***", "**", Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2, 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("*one", "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2 }, tableMetas);
        tableMetas = client.getTableMeta("*one*", "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2 }, tableMetas);
        tableMetas = client.getTableMeta("testpartdb_two", "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("testpartdb_two*", "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("testpartdb*", "*", Lists.newArrayList(EXTERNAL_TABLE.name()));
        assertTableMetas(new int[]{ 0 }, tableMetas);
        tableMetas = client.getTableMeta("testpartdb*", "*", Lists.newArrayList(EXTERNAL_TABLE.name(), MATERIALIZED_VIEW.name()));
        assertTableMetas(new int[]{ 0, 4 }, tableMetas);
        tableMetas = client.getTableMeta("*one", "*", Lists.newArrayList("*TABLE"));
        assertTableMetas(new int[]{  }, tableMetas);
        tableMetas = client.getTableMeta("*one", "*", Lists.newArrayList("*"));
        assertTableMetas(new int[]{  }, tableMetas);
    }

    @Test
    public void testGetTableMetaCaseSensitive() throws Exception {
        List<TableMeta> tableMetas = client.getTableMeta("*tWo", "tEsT*", Lists.newArrayList());
        assertTableMetas(new int[]{ 3 }, tableMetas);
        tableMetas = client.getTableMeta("*", "*", Lists.newArrayList("mAnAGeD_tABlE"));
        assertTableMetas(new int[]{  }, tableMetas);
    }

    @Test
    public void testGetTableMetaNullOrEmptyDb() throws Exception {
        List<TableMeta> tableMetas = client.getTableMeta(null, "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2, 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("", "*", Lists.newArrayList());
        assertTableMetas(new int[]{  }, tableMetas);
    }

    @Test
    public void testGetTableMetaNullOrEmptyTbl() throws Exception {
        List<TableMeta> tableMetas = client.getTableMeta("*", null, Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2, 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("*", "", Lists.newArrayList());
        assertTableMetas(new int[]{  }, tableMetas);
    }

    @Test
    public void testGetTableMetaNullOrEmptyTypes() throws Exception {
        List<TableMeta> tableMetas = client.getTableMeta("*", "*", Lists.newArrayList());
        assertTableMetas(new int[]{ 0, 1, 2, 3, 4 }, tableMetas);
        tableMetas = client.getTableMeta("*", "*", Lists.newArrayList(""));
        assertTableMetas(new int[]{  }, tableMetas);
        tableMetas = client.getTableMeta("*", "*", null);
        assertTableMetas(new int[]{ 0, 1, 2, 3, 4 }, tableMetas);
    }

    @Test
    public void testGetTableMetaNullNoDbNoTbl() throws Exception {
        client.dropDatabase(((TestGetTableMeta.DB_NAME) + "_one"), true, true, true);
        client.dropDatabase(((TestGetTableMeta.DB_NAME) + "_two"), true, true, true);
        List<TableMeta> tableMetas = client.getTableMeta("*", "*", Lists.newArrayList());
        assertTableMetas(new int[]{  }, tableMetas);
    }

    @Test
    public void tablesInDifferentCatalog() throws TException {
        String catName = "get_table_meta_catalog";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "db9";
        // For this one don't specify a location to make sure it gets put in the catalog directory
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String[] tableNames = new String[]{ "table_in_other_catalog_1", "table_in_other_catalog_2", "random_name" };
        List<TableMeta> expected = new ArrayList<>(tableNames.length);
        for (int i = 0; i < (tableNames.length); i++) {
            client.createTable(new TableBuilder().inDb(db).setTableName(tableNames[i]).addCol("id", "int").addCol("name", "string").build(metaStore.getConf()));
            TableMeta tableMeta = new TableMeta(dbName, tableNames[i], MANAGED_TABLE.name());
            tableMeta.setCatName(catName);
            expected.add(tableMeta);
        }
        List<String> types = Collections.singletonList(MANAGED_TABLE.name());
        List<TableMeta> actual = client.getTableMeta(catName, dbName, "*", types);
        assertTableMetas(expected, actual, 0, 1, 2);
        actual = client.getTableMeta(catName, "*", "table_*", types);
        assertTableMetas(expected, actual, 0, 1);
        actual = client.getTableMeta(dbName, "table_in_other_catalog_*", types);
        assertTableMetas(expected, actual);
    }

    @Test
    public void noSuchCatalog() throws TException {
        List<TableMeta> tableMetas = client.getTableMeta("nosuchcatalog", "*", "*", Lists.newArrayList());
        Assert.assertEquals(0, tableMetas.size());
    }

    @Test
    public void catalogPatternsDontWork() throws TException {
        List<TableMeta> tableMetas = client.getTableMeta("h*", "*", "*", Lists.newArrayList());
        Assert.assertEquals(0, tableMetas.size());
    }
}

