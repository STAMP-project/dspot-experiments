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
package org.apache.hadoop.hive.metastore.client;


import java.util.List;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.DefaultConstraintsRequest;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.SQLDefaultConstraint;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.SQLDefaultConstraintBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestDefaultConstraint extends MetaStoreClientTest {
    private static final String OTHER_DATABASE = "test_uc_other_database";

    private static final String OTHER_CATALOG = "test_uc_other_catalog";

    private static final String DATABASE_IN_OTHER_CATALOG = "test_uc_database_in_other_catalog";

    private final AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private Table[] testTables = new Table[3];

    private Database inOtherCatalog;

    public TestDefaultConstraint(String name, AbstractMetaStoreService metaStore) throws Exception {
        this.metaStore = metaStore;
    }

    @Test
    public void createGetDrop() throws TException {
        Table table = testTables[0];
        // Make sure get on a table with no key returns empty list
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLDefaultConstraint> fetched = client.getDefaultConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
        // Single column unnamed primary key in default catalog and database
        List<SQLDefaultConstraint> dv = new SQLDefaultConstraintBuilder().onTable(table).addColumn("col1").setDefaultVal(0).build(metaStore.getConf());
        client.addDefaultConstraint(dv);
        rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getDefaultConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(table.getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(table.getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals("0", fetched.get(0).getDefault_value());
        Assert.assertEquals(((table.getTableName()) + "_default_value"), fetched.get(0).getDc_name());
        String table0PkName = fetched.get(0).getDc_name();
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(table.getCatName(), fetched.get(0).getCatName());
        // Drop a primary key
        client.dropConstraint(table.getCatName(), table.getDbName(), table.getTableName(), table0PkName);
        rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getDefaultConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
        // Make sure I can add it back
        client.addDefaultConstraint(dv);
    }

    @Test
    public void inOtherCatalog() throws TException {
        String constraintName = "ocdv";
        // Table in non 'hive' catalog
        List<SQLDefaultConstraint> dv = new SQLDefaultConstraintBuilder().onTable(testTables[2]).addColumn("col1").setConstraintName(constraintName).setDefaultVal("empty").build(metaStore.getConf());
        client.addDefaultConstraint(dv);
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(testTables[2].getCatName(), testTables[2].getDbName(), testTables[2].getTableName());
        List<SQLDefaultConstraint> fetched = client.getDefaultConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(testTables[2].getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(testTables[2].getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals("empty", fetched.get(0).getDefault_value());
        Assert.assertEquals(constraintName, fetched.get(0).getDc_name());
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(testTables[2].getCatName(), fetched.get(0).getCatName());
        client.dropConstraint(testTables[2].getCatName(), testTables[2].getDbName(), testTables[2].getTableName(), constraintName);
        rqst = new DefaultConstraintsRequest(testTables[2].getCatName(), testTables[2].getDbName(), testTables[2].getTableName());
        fetched = client.getDefaultConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
    }

    @Test
    public void createTableWithConstraintsPk() throws TException {
        String constraintName = "ctwcdv";
        Table table = new TableBuilder().setTableName("table_with_constraints").addCol("col1", "int").addCol("col2", "varchar(32)").build(metaStore.getConf());
        List<SQLDefaultConstraint> dv = new SQLDefaultConstraintBuilder().onTable(table).addColumn("col1").setConstraintName(constraintName).setDefaultVal(0).build(metaStore.getConf());
        client.createTableWithConstraints(table, null, null, null, null, dv, null);
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLDefaultConstraint> fetched = client.getDefaultConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(table.getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(table.getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals("0", fetched.get(0).getDefault_value());
        Assert.assertEquals(constraintName, fetched.get(0).getDc_name());
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(table.getCatName(), fetched.get(0).getCatName());
        client.dropConstraint(table.getCatName(), table.getDbName(), table.getTableName(), constraintName);
        rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getDefaultConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
    }

    @Test
    public void createTableWithConstraintsPkInOtherCatalog() throws TException {
        Table table = new TableBuilder().setTableName("table_in_other_catalog_with_constraints").inDb(inOtherCatalog).addCol("col1", "int").addCol("col2", "varchar(32)").build(metaStore.getConf());
        List<SQLDefaultConstraint> dv = new SQLDefaultConstraintBuilder().onTable(table).addColumn("col1").setDefaultVal(0).build(metaStore.getConf());
        client.createTableWithConstraints(table, null, null, null, null, dv, null);
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLDefaultConstraint> fetched = client.getDefaultConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(table.getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(table.getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals("0", fetched.get(0).getDefault_value());
        Assert.assertEquals(((table.getTableName()) + "_default_value"), fetched.get(0).getDc_name());
        String tablePkName = fetched.get(0).getDc_name();
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(table.getCatName(), fetched.get(0).getCatName());
        client.dropConstraint(table.getCatName(), table.getDbName(), table.getTableName(), tablePkName);
        rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getDefaultConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
    }

    @Test
    public void doubleAddUniqueConstraint() throws TException {
        Table table = testTables[0];
        // Make sure get on a table with no key returns empty list
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLDefaultConstraint> fetched = client.getDefaultConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
        // Single column unnamed primary key in default catalog and database
        List<SQLDefaultConstraint> dv = new SQLDefaultConstraintBuilder().onTable(table).addColumn("col1").setDefaultVal(0).build(metaStore.getConf());
        client.addDefaultConstraint(dv);
        try {
            dv = new SQLDefaultConstraintBuilder().onTable(table).addColumn("col2").setDefaultVal("this string intentionally left empty").build(metaStore.getConf());
            client.addDefaultConstraint(dv);
            Assert.fail();
        } catch (InvalidObjectException | TApplicationException e) {
            // NOP
        }
    }

    @Test
    public void addNoSuchTable() throws TException {
        try {
            List<SQLDefaultConstraint> dv = new SQLDefaultConstraintBuilder().setTableName("nosuch").addColumn("col2").setDefaultVal("this string intentionally left empty").build(metaStore.getConf());
            client.addDefaultConstraint(dv);
            Assert.fail();
        } catch (InvalidObjectException | TApplicationException e) {
            // NOP
        }
    }

    @Test
    public void getNoSuchTable() throws TException {
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME, "nosuch");
        List<SQLDefaultConstraint> dv = client.getDefaultConstraints(rqst);
        Assert.assertTrue(dv.isEmpty());
    }

    @Test
    public void getNoSuchDb() throws TException {
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, "nosuch", testTables[0].getTableName());
        List<SQLDefaultConstraint> dv = client.getDefaultConstraints(rqst);
        Assert.assertTrue(dv.isEmpty());
    }

    @Test
    public void getNoSuchCatalog() throws TException {
        DefaultConstraintsRequest rqst = new DefaultConstraintsRequest("nosuch", testTables[0].getDbName(), testTables[0].getTableName());
        List<SQLDefaultConstraint> dv = client.getDefaultConstraints(rqst);
        Assert.assertTrue(dv.isEmpty());
    }
}

