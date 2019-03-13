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


import ColumnType.INT_TYPE_NAME;
import ColumnType.STRING_TYPE_NAME;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test class for IMetaStoreClient API. Testing the Table related functions for metadata
 * querying like getting one, or multiple tables, and table name lists.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestTablesGetExists extends MetaStoreClientTest {
    private static final String DEFAULT_DATABASE = "default";

    private static final String OTHER_DATABASE = "dummy";

    private final AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private Table[] testTables = new Table[7];

    public TestTablesGetExists(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    @Test
    public void testGetTableCaseInsensitive() throws Exception {
        Table table = testTables[0];
        // Test in upper case
        Table resultUpper = client.getTable(table.getCatName().toUpperCase(), table.getDbName().toUpperCase(), table.getTableName().toUpperCase());
        Assert.assertEquals("Comparing tables", table, resultUpper);
        // Test in mixed case
        Table resultMix = client.getTable("hIvE", "DeFaUlt", "tEsT_TabLE");
        Assert.assertEquals("Comparing tables", table, resultMix);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetTableNoSuchDatabase() throws Exception {
        Table table = testTables[2];
        client.getTable("no_such_database", table.getTableName());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetTableNoSuchTable() throws Exception {
        Table table = testTables[2];
        client.getTable(table.getDbName(), "no_such_table");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetTableNoSuchTableInTheDatabase() throws Exception {
        Table table = testTables[2];
        client.getTable(TestTablesGetExists.OTHER_DATABASE, table.getTableName());
    }

    @Test
    public void testGetTableNullDatabase() throws Exception {
        try {
            client.getTable(null, TestTablesGetExists.OTHER_DATABASE);
            // TODO: Should be checked on server side. On Embedded metastore it throws MetaException,
            // on Remote metastore it throws TProtocolException
            Assert.fail("Expected an MetaException or TProtocolException to be thrown");
        } catch (MetaException exception) {
            // Expected exception - Embedded MetaStore
        } catch (TProtocolException exception) {
            // Expected exception - Remote MetaStore
        }
    }

    @Test
    public void testGetTableNullTableName() throws Exception {
        try {
            client.getTable(TestTablesGetExists.DEFAULT_DATABASE, null);
            // TODO: Should be checked on server side. On Embedded metastore it throws MetaException,
            // on Remote metastore it throws TProtocolException
            Assert.fail("Expected an MetaException or TProtocolException to be thrown");
        } catch (MetaException exception) {
            // Expected exception - Embedded MetaStore
        } catch (TProtocolException exception) {
            // Expected exception - Remote MetaStore
        }
    }

    @Test
    public void testGetAllTables() throws Exception {
        List<String> tables = client.getAllTables(TestTablesGetExists.DEFAULT_DATABASE);
        Assert.assertEquals("All tables size", 5, tables.size());
        for (Table table : testTables) {
            if (table.getDbName().equals(TestTablesGetExists.DEFAULT_DATABASE)) {
                Assert.assertTrue("Checking table names", tables.contains(table.getTableName()));
            }
        }
        // Drop one table, see what remains
        client.dropTable(testTables[1].getCatName(), testTables[1].getDbName(), testTables[1].getTableName());
        tables = client.getAllTables(TestTablesGetExists.DEFAULT_DATABASE);
        Assert.assertEquals("All tables size", 4, tables.size());
        for (Table table : testTables) {
            if ((table.getDbName().equals(TestTablesGetExists.DEFAULT_DATABASE)) && (!(table.getTableName().equals(testTables[1].getTableName())))) {
                Assert.assertTrue("Checking table names", tables.contains(table.getTableName()));
            }
        }
        // No such database
        tables = client.getAllTables("no_such_database");
        Assert.assertEquals("All tables size", 0, tables.size());
    }

    @Test(expected = MetaException.class)
    public void testGetAllTablesInvalidData() throws Exception {
        client.getAllTables(null);
    }

    @Test
    public void testGetAllTablesCaseInsensitive() throws Exception {
        // Check case insensitive search
        List<String> tables = client.getAllTables("dEFauLt");
        Assert.assertEquals("Found tables size", 5, tables.size());
    }

    @Test
    public void testGetTables() throws Exception {
        // Find tables which name contains _to_find_ in the default database
        List<String> tables = client.getTables(TestTablesGetExists.DEFAULT_DATABASE, "*_to_find_*");
        Assert.assertEquals("All tables size", 2, tables.size());
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[2].getTableName()));
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[3].getTableName()));
        // Find tables which name contains _to_find_ or _hidden_ in the default database
        tables = client.getTables(TestTablesGetExists.DEFAULT_DATABASE, "*_to_find_*|*_hidden_*");
        Assert.assertEquals("All tables size", 3, tables.size());
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[2].getTableName()));
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[3].getTableName()));
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[4].getTableName()));
        // Find table which name contains _to_find_ in the dummy database
        tables = client.getTables(TestTablesGetExists.OTHER_DATABASE, "*_to_find_*");
        Assert.assertEquals("Found functions size", 1, tables.size());
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[6].getTableName()));
        // Look for tables but do not find any
        tables = client.getTables(TestTablesGetExists.DEFAULT_DATABASE, "*_not_such_function_*");
        Assert.assertEquals("No such table size", 0, tables.size());
        // Look for tables without pattern
        tables = client.getTables(TestTablesGetExists.DEFAULT_DATABASE, ((String) (null)));
        Assert.assertEquals("No such functions size", 5, tables.size());
        // Look for tables with empty pattern
        tables = client.getTables(TestTablesGetExists.DEFAULT_DATABASE, "");
        Assert.assertEquals("No such functions size", 0, tables.size());
        // No such database
        tables = client.getTables("no_such_database", TestTablesGetExists.OTHER_DATABASE);
        Assert.assertEquals("No such table size", 0, tables.size());
    }

    @Test
    public void testGetTablesCaseInsensitive() throws Exception {
        // Check case insensitive search
        List<String> tables = client.getTables(TestTablesGetExists.DEFAULT_DATABASE, "*_tO_FiND*");
        Assert.assertEquals("Found tables size", 2, tables.size());
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[2].getTableName()));
        Assert.assertTrue("Comparing tablenames", tables.contains(testTables[3].getTableName()));
    }

    @Test(expected = MetaException.class)
    public void testGetTablesNullDatabase() throws Exception {
        client.getTables(null, "*_tO_FiND*");
    }

    @Test
    public void testTableExists() throws Exception {
        // Using the second table, since a table called "test_table" exists in both databases
        Table table = testTables[1];
        Assert.assertTrue("Table exists", client.tableExists(table.getCatName(), table.getDbName(), table.getTableName()));
        Assert.assertFalse("Table not exists", client.tableExists(table.getCatName(), table.getDbName(), "non_existing_table"));
        // No such database
        Assert.assertFalse("Table not exists", client.tableExists("no_such_database", table.getTableName()));
        // No such table in the given database
        Assert.assertFalse("Table not exists", client.tableExists(TestTablesGetExists.OTHER_DATABASE, table.getTableName()));
    }

    @Test
    public void testTableExistsCaseInsensitive() throws Exception {
        Table table = testTables[0];
        // Test in upper case
        Assert.assertTrue("Table exists", client.tableExists(table.getCatName().toUpperCase(), table.getDbName().toUpperCase(), table.getTableName().toUpperCase()));
        // Test in mixed case
        Assert.assertTrue("Table exists", client.tableExists("hIVe", "DeFaUlt", "tEsT_TabLE"));
    }

    @Test
    public void testTableExistsNullDatabase() throws Exception {
        try {
            client.tableExists(null, TestTablesGetExists.OTHER_DATABASE);
            // TODO: Should be checked on server side. On Embedded metastore it throws MetaException,
            // on Remote metastore it throws TProtocolException
            Assert.fail("Expected an MetaException or TProtocolException to be thrown");
        } catch (MetaException exception) {
            // Expected exception - Embedded MetaStore
        } catch (TProtocolException exception) {
            // Expected exception - Remote MetaStore
        }
    }

    @Test
    public void testTableExistsNullTableName() throws Exception {
        try {
            client.tableExists(TestTablesGetExists.DEFAULT_DATABASE, null);
            // TODO: Should be checked on server side. On Embedded metastore it throws MetaException,
            // on Remote metastore it throws TProtocolException
            Assert.fail("Expected an MetaException or TProtocolException to be thrown");
        } catch (MetaException exception) {
            // Expected exception - Embedded MetaStore
        } catch (TProtocolException exception) {
            // Expected exception - Remote MetaStore
        }
    }

    @Test
    public void testGetTableObjectsByName() throws Exception {
        List<String> tableNames = new ArrayList<>();
        tableNames.add(testTables[0].getTableName());
        tableNames.add(testTables[1].getTableName());
        List<Table> tables = client.getTableObjectsByName(TestTablesGetExists.DEFAULT_DATABASE, tableNames);
        Assert.assertEquals("Found tables", 2, tables.size());
        for (Table table : tables) {
            if (table.getTableName().equals(testTables[0].getTableName())) {
                Assert.assertEquals("Comparing tables", testTables[0], table);
            } else {
                Assert.assertEquals("Comparing tables", testTables[1], table);
            }
        }
        // Test with empty array
        tables = client.getTableObjectsByName(TestTablesGetExists.DEFAULT_DATABASE, new ArrayList());
        Assert.assertEquals("Found tables", 0, tables.size());
        // Test with table name which does not exists
        tableNames = new ArrayList<>();
        tableNames.add("no_such_table");
        client.getTableObjectsByName(testTables[0].getCatName(), testTables[0].getDbName(), tableNames);
        Assert.assertEquals("Found tables", 0, tables.size());
        // Test with table name which does not exists in the given database
        tableNames = new ArrayList<>();
        tableNames.add(testTables[0].getTableName());
        client.getTableObjectsByName(TestTablesGetExists.OTHER_DATABASE, tableNames);
        Assert.assertEquals("Found tables", 0, tables.size());
    }

    @Test
    public void testGetTableObjectsByNameCaseInsensitive() throws Exception {
        Table table = testTables[0];
        // Test in upper case
        List<String> tableNames = new ArrayList<>();
        tableNames.add(testTables[0].getTableName().toUpperCase());
        List<Table> tables = client.getTableObjectsByName(table.getCatName().toUpperCase(), table.getDbName().toUpperCase(), tableNames);
        Assert.assertEquals("Found tables", 1, tables.size());
        Assert.assertEquals("Comparing tables", table, tables.get(0));
        // Test in mixed case
        tableNames = new ArrayList<>();
        tableNames.add("tEsT_TabLE");
        tables = client.getTableObjectsByName("HiVe", "DeFaUlt", tableNames);
        Assert.assertEquals("Found tables", 1, tables.size());
        Assert.assertEquals("Comparing tables", table, tables.get(0));
    }

    @Test(expected = UnknownDBException.class)
    public void testGetTableObjectsByNameNoSuchDatabase() throws Exception {
        List<String> tableNames = new ArrayList<>();
        tableNames.add(testTables[0].getTableName());
        client.getTableObjectsByName("no_such_database", tableNames);
    }

    @Test
    public void testGetTableObjectsByNameNullDatabase() throws Exception {
        try {
            List<String> tableNames = new ArrayList<>();
            tableNames.add(TestTablesGetExists.OTHER_DATABASE);
            client.getTableObjectsByName(null, tableNames);
            // TODO: Should be checked on server side. On Embedded metastore it throws MetaException,
            // on Remote metastore it throws TProtocolException
            Assert.fail("Expected an UnknownDBException or TProtocolException to be thrown");
        } catch (UnknownDBException exception) {
            // Expected exception - Embedded MetaStore
        } catch (TProtocolException exception) {
            // Expected exception - Remote MetaStore
        }
    }

    @Test
    public void testGetTableObjectsByNameNullTableNameList() throws Exception {
        try {
            client.getTableObjectsByName(TestTablesGetExists.DEFAULT_DATABASE, null);
            // TODO: Should be checked on server side. On Embedded metastore it throws MetaException,
            // on Remote metastore it throws TTransportException
            Assert.fail("Expected an InvalidOperationException to be thrown");
        } catch (InvalidOperationException exception) {
            // Expected exception - Embedded MetaStore
        } catch (TTransportException exception) {
            // Expected exception - Remote MetaStore
        }
    }

    // Tests for getTable in other catalogs are covered in TestTablesCreateDropAlterTruncate.
    @Test
    public void otherCatalog() throws TException {
        String catName = "get_exists_tables_in_other_catalogs";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "db_in_other_catalog";
        // For this one don't specify a location to make sure it gets put in the catalog directory
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String[] tableNames = new String[4];
        for (int i = 0; i < (tableNames.length); i++) {
            tableNames[i] = "table_in_other_catalog_" + i;
            new TableBuilder().inDb(db).setTableName(tableNames[i]).addCol(("col1_" + i), STRING_TYPE_NAME).addCol(("col2_" + i), INT_TYPE_NAME).create(client, metaStore.getConf());
        }
        Set<String> tables = new java.util.HashSet(client.getTables(catName, dbName, "*e_in_other_*"));
        Assert.assertEquals(4, tables.size());
        for (String tableName : tableNames)
            Assert.assertTrue(tables.contains(tableName));

        List<String> fetchedNames = client.getTables(catName, dbName, "*_3");
        Assert.assertEquals(1, fetchedNames.size());
        Assert.assertEquals(tableNames[3], fetchedNames.get(0));
        Assert.assertTrue("Table exists", client.tableExists(catName, dbName, tableNames[0]));
        Assert.assertFalse("Table not exists", client.tableExists(catName, dbName, "non_existing_table"));
    }

    @Test
    public void getTablesBogusCatalog() throws TException {
        Assert.assertEquals(0, client.getTables("nosuch", Warehouse.DEFAULT_DATABASE_NAME, "*_to_find_*").size());
    }

    @Test
    public void tableExistsBogusCatalog() throws TException {
        Assert.assertFalse(client.tableExists("nosuch", testTables[0].getDbName(), testTables[0].getTableName()));
    }
}

