/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.tools.schematool;


import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaException;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.FunctionBuilder;
import org.apache.hadoop.hive.metastore.client.builder.PartitionBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;


public class TestSchemaToolCatalogOps {
    private static MetastoreSchemaTool schemaTool;

    private static HiveConf conf;

    private IMetaStoreClient client;

    private static String testMetastoreDB;

    private static PrintStream errStream;

    private static PrintStream outStream;

    private static String argsBase;

    @Test
    public void createCatalog() throws HiveMetaException, TException {
        String catName = "my_test_catalog";
        String location = "file:///tmp/my_test_catalog";
        String description = "very descriptive";
        String argsCreate = String.format("-createCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\"", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskCreateCatalog(), argsCreate);
        Catalog cat = client.getCatalog(catName);
        Assert.assertEquals(location, cat.getLocationUri());
        Assert.assertEquals(description, cat.getDescription());
    }

    @Test(expected = HiveMetaException.class)
    public void createExistingCatalog() throws HiveMetaException {
        String catName = "hive";
        String location = "somewhere";
        String argsCreate = String.format("-createCatalog %s -catalogLocation \"%s\"", catName, location);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskCreateCatalog(), argsCreate);
    }

    @Test
    public void createExistingCatalogWithIfNotExists() throws HiveMetaException {
        String catName = "my_existing_test_catalog";
        String location = "file:///tmp/my_test_catalog";
        String description = "very descriptive";
        String argsCreate1 = String.format("-createCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\"", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskCreateCatalog(), argsCreate1);
        String argsCreate2 = String.format("-createCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\" -ifNotExists", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskCreateCatalog(), argsCreate2);
    }

    @Test
    public void alterCatalog() throws HiveMetaException, TException {
        String catName = "an_alterable_catalog";
        String location = "file:///tmp/an_alterable_catalog";
        String description = "description";
        String argsCreate = String.format("-createCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\"", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskCreateCatalog(), argsCreate);
        location = "file:///tmp/somewhere_else";
        String argsAlter1 = String.format("-alterCatalog %s -catalogLocation \"%s\"", catName, location);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskAlterCatalog(), argsAlter1);
        Catalog cat = client.getCatalog(catName);
        Assert.assertEquals(location, cat.getLocationUri());
        Assert.assertEquals(description, cat.getDescription());
        description = "a better description";
        String argsAlter2 = String.format("-alterCatalog %s -catalogDescription \"%s\"", catName, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskAlterCatalog(), argsAlter2);
        cat = client.getCatalog(catName);
        Assert.assertEquals(location, cat.getLocationUri());
        Assert.assertEquals(description, cat.getDescription());
        location = "file:///tmp/a_third_location";
        description = "best description yet";
        String argsAlter3 = String.format("-alterCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\"", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskAlterCatalog(), argsAlter3);
        cat = client.getCatalog(catName);
        Assert.assertEquals(location, cat.getLocationUri());
        Assert.assertEquals(description, cat.getDescription());
    }

    @Test(expected = HiveMetaException.class)
    public void alterBogusCatalog() throws HiveMetaException {
        String catName = "nosuch";
        String location = "file:///tmp/somewhere";
        String description = "whatever";
        String argsAlter = String.format("-alterCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\"", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskAlterCatalog(), argsAlter);
    }

    @Test(expected = HiveMetaException.class)
    public void alterCatalogNoChange() throws HiveMetaException {
        String catName = "alter_cat_no_change";
        String location = "file:///tmp/alter_cat_no_change";
        String description = "description";
        String argsCreate = String.format("-createCatalog %s -catalogLocation \"%s\" -catalogDescription \"%s\"", catName, location, description);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskCreateCatalog(), argsCreate);
        String argsAlter = String.format("-alterCatalog %s", catName);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskAlterCatalog(), argsAlter);
    }

    @Test
    public void moveDatabase() throws HiveMetaException, TException {
        String toCatName = "moveDbCat";
        String dbName = "moveDbDb";
        String tableName = "moveDbTable";
        String funcName = "movedbfunc";
        String partVal = "moveDbKey";
        new CatalogBuilder().setName(toCatName).setLocation("file:///tmp").create(client);
        Database db = new DatabaseBuilder().setCatalogName(Warehouse.DEFAULT_CATALOG_NAME).setName(dbName).create(client, TestSchemaToolCatalogOps.conf);
        new FunctionBuilder().inDb(db).setName(funcName).setClass("org.apache.hive.myudf").create(client, TestSchemaToolCatalogOps.conf);
        Table table = new TableBuilder().inDb(db).setTableName(tableName).addCol("a", "int").addPartCol("p", "string").create(client, TestSchemaToolCatalogOps.conf);
        new PartitionBuilder().inTable(table).addValue(partVal).addToTable(client, TestSchemaToolCatalogOps.conf);
        String argsMoveDB = String.format("-moveDatabase %s -fromCatalog %s -toCatalog %s", dbName, Warehouse.DEFAULT_CATALOG_NAME, toCatName);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveDatabase(), argsMoveDB);
        Database fetchedDb = client.getDatabase(toCatName, dbName);
        Assert.assertNotNull(fetchedDb);
        Assert.assertEquals(toCatName.toLowerCase(), fetchedDb.getCatalogName());
        Function fetchedFunction = client.getFunction(toCatName, dbName, funcName);
        Assert.assertNotNull(fetchedFunction);
        Assert.assertEquals(toCatName.toLowerCase(), fetchedFunction.getCatName());
        Assert.assertEquals(dbName.toLowerCase(), fetchedFunction.getDbName());
        Table fetchedTable = client.getTable(toCatName, dbName, tableName);
        Assert.assertNotNull(fetchedTable);
        Assert.assertEquals(toCatName.toLowerCase(), fetchedTable.getCatName());
        Assert.assertEquals(dbName.toLowerCase(), fetchedTable.getDbName());
        Partition fetchedPart = client.getPartition(toCatName, dbName, tableName, Collections.singletonList(partVal));
        Assert.assertNotNull(fetchedPart);
        Assert.assertEquals(toCatName.toLowerCase(), fetchedPart.getCatName());
        Assert.assertEquals(dbName.toLowerCase(), fetchedPart.getDbName());
        Assert.assertEquals(tableName.toLowerCase(), fetchedPart.getTableName());
    }

    @Test
    public void moveDatabaseWithExistingDbOfSameNameAlreadyInTargetCatalog() throws HiveMetaException, TException {
        String catName = "clobberCatalog";
        new CatalogBuilder().setName(catName).setLocation("file:///tmp").create(client);
        try {
            String argsMoveDB = String.format("-moveDatabase %s -fromCatalog %s -toCatalog %s", Warehouse.DEFAULT_DATABASE_NAME, catName, Warehouse.DEFAULT_CATALOG_NAME);
            TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveDatabase(), argsMoveDB);
            Assert.fail("Attempt to move default database should have failed.");
        } catch (HiveMetaException e) {
            // good
        }
        // Make sure nothing really moved
        Set<String> dbNames = new java.util.HashSet(client.getAllDatabases(Warehouse.DEFAULT_CATALOG_NAME));
        Assert.assertTrue(dbNames.contains(Warehouse.DEFAULT_DATABASE_NAME));
    }

    @Test(expected = HiveMetaException.class)
    public void moveNonExistentDatabase() throws HiveMetaException, TException {
        String catName = "moveNonExistentDb";
        new CatalogBuilder().setName(catName).setLocation("file:///tmp").create(client);
        String argsMoveDB = String.format("-moveDatabase nosuch -fromCatalog %s -toCatalog %s", catName, Warehouse.DEFAULT_CATALOG_NAME);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveDatabase(), argsMoveDB);
    }

    @Test
    public void moveDbToNonExistentCatalog() throws HiveMetaException, TException {
        String dbName = "doomedToHomelessness";
        new DatabaseBuilder().setName(dbName).create(client, TestSchemaToolCatalogOps.conf);
        try {
            String argsMoveDB = String.format("-moveDatabase %s -fromCatalog %s -toCatalog nosuch", dbName, Warehouse.DEFAULT_CATALOG_NAME);
            TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveDatabase(), argsMoveDB);
            Assert.fail("Attempt to move database to non-existent catalog should have failed.");
        } catch (HiveMetaException e) {
            // good
        }
        // Make sure nothing really moved
        Set<String> dbNames = new java.util.HashSet(client.getAllDatabases(Warehouse.DEFAULT_CATALOG_NAME));
        Assert.assertTrue(dbNames.contains(dbName.toLowerCase()));
    }

    @Test
    public void moveTable() throws HiveMetaException, TException {
        String toCatName = "moveTableCat";
        String toDbName = "moveTableDb";
        String tableName = "moveTableTable";
        String partVal = "moveTableKey";
        new CatalogBuilder().setName(toCatName).setLocation("file:///tmp").create(client);
        new DatabaseBuilder().setCatalogName(toCatName).setName(toDbName).create(client, TestSchemaToolCatalogOps.conf);
        Table table = new TableBuilder().setTableName(tableName).addCol("a", "int").addPartCol("p", "string").create(client, TestSchemaToolCatalogOps.conf);
        new PartitionBuilder().inTable(table).addValue(partVal).addToTable(client, TestSchemaToolCatalogOps.conf);
        String argsMoveTable = String.format("-moveTable %s -fromCatalog %s -toCatalog %s -fromDatabase %s -toDatabase %s", tableName, Warehouse.DEFAULT_CATALOG_NAME, toCatName, Warehouse.DEFAULT_DATABASE_NAME, toDbName);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveTable(), argsMoveTable);
        Table fetchedTable = client.getTable(toCatName, toDbName, tableName);
        Assert.assertNotNull(fetchedTable);
        Assert.assertEquals(toCatName.toLowerCase(), fetchedTable.getCatName());
        Assert.assertEquals(toDbName.toLowerCase(), fetchedTable.getDbName());
        Partition fetchedPart = client.getPartition(toCatName, toDbName, tableName, Collections.singletonList(partVal));
        Assert.assertNotNull(fetchedPart);
        Assert.assertEquals(toCatName.toLowerCase(), fetchedPart.getCatName());
        Assert.assertEquals(toDbName.toLowerCase(), fetchedPart.getDbName());
        Assert.assertEquals(tableName.toLowerCase(), fetchedPart.getTableName());
    }

    @Test
    public void moveTableWithinCatalog() throws HiveMetaException, TException {
        String toDbName = "moveTableWithinCatalogDb";
        String tableName = "moveTableWithinCatalogTable";
        String partVal = "moveTableWithinCatalogKey";
        new DatabaseBuilder().setName(toDbName).create(client, TestSchemaToolCatalogOps.conf);
        Table table = new TableBuilder().setTableName(tableName).addCol("a", "int").addPartCol("p", "string").create(client, TestSchemaToolCatalogOps.conf);
        new PartitionBuilder().inTable(table).addValue(partVal).addToTable(client, TestSchemaToolCatalogOps.conf);
        String argsMoveTable = String.format("-moveTable %s -fromCatalog %s -toCatalog %s -fromDatabase %s -toDatabase %s", tableName, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME, toDbName);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveTable(), argsMoveTable);
        Table fetchedTable = client.getTable(Warehouse.DEFAULT_CATALOG_NAME, toDbName, tableName);
        Assert.assertNotNull(fetchedTable);
        Assert.assertEquals(Warehouse.DEFAULT_CATALOG_NAME, fetchedTable.getCatName());
        Assert.assertEquals(toDbName.toLowerCase(), fetchedTable.getDbName());
        Partition fetchedPart = client.getPartition(Warehouse.DEFAULT_CATALOG_NAME, toDbName, tableName, Collections.singletonList(partVal));
        Assert.assertNotNull(fetchedPart);
        Assert.assertEquals(Warehouse.DEFAULT_CATALOG_NAME, fetchedPart.getCatName());
        Assert.assertEquals(toDbName.toLowerCase(), fetchedPart.getDbName());
        Assert.assertEquals(tableName.toLowerCase(), fetchedPart.getTableName());
    }

    @Test
    public void moveTableWithExistingTableOfSameNameAlreadyInTargetDatabase() throws HiveMetaException, TException {
        String toDbName = "clobberTableDb";
        String tableName = "clobberTableTable";
        Database toDb = new DatabaseBuilder().setName(toDbName).create(client, TestSchemaToolCatalogOps.conf);
        new TableBuilder().setTableName(tableName).addCol("a", "int").create(client, TestSchemaToolCatalogOps.conf);
        new TableBuilder().inDb(toDb).setTableName(tableName).addCol("b", "varchar(32)").create(client, TestSchemaToolCatalogOps.conf);
        try {
            String argsMoveTable = String.format("-moveTable %s -fromCatalog %s -toCatalog %s -fromDatabase %s -toDatabase %s", tableName, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME, toDbName);
            TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveTable(), argsMoveTable);
            Assert.fail("Attempt to move table should have failed.");
        } catch (HiveMetaException e) {
            // good
        }
        // Make sure nothing really moved
        Set<String> tableNames = new java.util.HashSet(client.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME));
        Assert.assertTrue(tableNames.contains(tableName.toLowerCase()));
        // Make sure the table in the target database didn't get clobbered
        Table fetchedTable = client.getTable(Warehouse.DEFAULT_CATALOG_NAME, toDbName, tableName);
        Assert.assertEquals("b", fetchedTable.getSd().getCols().get(0).getName());
    }

    @Test(expected = HiveMetaException.class)
    public void moveNonExistentTable() throws HiveMetaException, TException {
        String toDbName = "moveNonExistentTable";
        new DatabaseBuilder().setName(toDbName).create(client, TestSchemaToolCatalogOps.conf);
        String argsMoveTable = String.format("-moveTable nosuch -fromCatalog %s -toCatalog %s -fromDatabase %s -toDatabase %s", Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME, toDbName);
        TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveTable(), argsMoveTable);
    }

    @Test
    public void moveTableToNonExistentDb() throws HiveMetaException, TException {
        String tableName = "doomedToWander";
        new TableBuilder().setTableName(tableName).addCol("a", "int").create(client, TestSchemaToolCatalogOps.conf);
        try {
            String argsMoveTable = String.format("-moveTable %s -fromCatalog %s -toCatalog %s -fromDatabase %s -toDatabase nosuch", tableName, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME);
            TestSchemaToolCatalogOps.execute(new SchemaToolTaskMoveTable(), argsMoveTable);
            Assert.fail("Attempt to move table to non-existent table should have failed.");
        } catch (HiveMetaException e) {
            // good
        }
        // Make sure nothing really moved
        Set<String> tableNames = new java.util.HashSet(client.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME));
        Assert.assertTrue(tableNames.contains(tableName.toLowerCase()));
    }
}

