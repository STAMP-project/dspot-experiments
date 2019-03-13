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
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.NotNullConstraintsRequest;
import org.apache.hadoop.hive.metastore.api.SQLNotNullConstraint;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.SQLNotNullConstraintBuilder;
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
public class TestNotNullConstraint extends MetaStoreClientTest {
    private static final String OTHER_DATABASE = "test_uc_other_database";

    private static final String OTHER_CATALOG = "test_uc_other_catalog";

    private static final String DATABASE_IN_OTHER_CATALOG = "test_uc_database_in_other_catalog";

    private final AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private Table[] testTables = new Table[3];

    private Database inOtherCatalog;

    public TestNotNullConstraint(String name, AbstractMetaStoreService metaStore) throws Exception {
        this.metaStore = metaStore;
    }

    @Test
    public void createGetDrop() throws TException {
        Table table = testTables[0];
        // Make sure get on a table with no key returns empty list
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLNotNullConstraint> fetched = client.getNotNullConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
        // Single column unnamed primary key in default catalog and database
        List<SQLNotNullConstraint> nn = new SQLNotNullConstraintBuilder().onTable(table).addColumn("col1").build(metaStore.getConf());
        client.addNotNullConstraint(nn);
        rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getNotNullConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(table.getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(table.getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals(((table.getTableName()) + "_not_null_constraint"), fetched.get(0).getNn_name());
        String table0PkName = fetched.get(0).getNn_name();
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(table.getCatName(), fetched.get(0).getCatName());
        // Drop a primary key
        client.dropConstraint(table.getCatName(), table.getDbName(), table.getTableName(), table0PkName);
        rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getNotNullConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
        // Make sure I can add it back
        client.addNotNullConstraint(nn);
    }

    @Test
    public void inOtherCatalog() throws TException {
        String constraintName = "ocuc";
        // Table in non 'hive' catalog
        List<SQLNotNullConstraint> nn = new SQLNotNullConstraintBuilder().onTable(testTables[2]).addColumn("col1").setConstraintName(constraintName).build(metaStore.getConf());
        client.addNotNullConstraint(nn);
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(testTables[2].getCatName(), testTables[2].getDbName(), testTables[2].getTableName());
        List<SQLNotNullConstraint> fetched = client.getNotNullConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(testTables[2].getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(testTables[2].getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals(constraintName, fetched.get(0).getNn_name());
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(testTables[2].getCatName(), fetched.get(0).getCatName());
        client.dropConstraint(testTables[2].getCatName(), testTables[2].getDbName(), testTables[2].getTableName(), constraintName);
        rqst = new NotNullConstraintsRequest(testTables[2].getCatName(), testTables[2].getDbName(), testTables[2].getTableName());
        fetched = client.getNotNullConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
    }

    @Test
    public void createTableWithConstraintsPk() throws TException {
        String constraintName = "ctwcuc";
        Table table = new TableBuilder().setTableName("table_with_constraints").addCol("col1", "int").addCol("col2", "varchar(32)").build(metaStore.getConf());
        List<SQLNotNullConstraint> nn = new SQLNotNullConstraintBuilder().onTable(table).addColumn("col1").setConstraintName(constraintName).build(metaStore.getConf());
        client.createTableWithConstraints(table, null, null, null, nn, null, null);
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLNotNullConstraint> fetched = client.getNotNullConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(table.getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(table.getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals(constraintName, fetched.get(0).getNn_name());
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(table.getCatName(), fetched.get(0).getCatName());
        client.dropConstraint(table.getCatName(), table.getDbName(), table.getTableName(), constraintName);
        rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getNotNullConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
    }

    @Test
    public void createTableWithConstraintsPkInOtherCatalog() throws TException {
        Table table = new TableBuilder().setTableName("table_in_other_catalog_with_constraints").inDb(inOtherCatalog).addCol("col1", "int").addCol("col2", "varchar(32)").build(metaStore.getConf());
        List<SQLNotNullConstraint> nn = new SQLNotNullConstraintBuilder().onTable(table).addColumn("col1").build(metaStore.getConf());
        client.createTableWithConstraints(table, null, null, null, nn, null, null);
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLNotNullConstraint> fetched = client.getNotNullConstraints(rqst);
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(table.getDbName(), fetched.get(0).getTable_db());
        Assert.assertEquals(table.getTableName(), fetched.get(0).getTable_name());
        Assert.assertEquals("col1", fetched.get(0).getColumn_name());
        Assert.assertEquals(((table.getTableName()) + "_not_null_constraint"), fetched.get(0).getNn_name());
        String tablePkName = fetched.get(0).getNn_name();
        Assert.assertTrue(fetched.get(0).isEnable_cstr());
        Assert.assertFalse(fetched.get(0).isValidate_cstr());
        Assert.assertFalse(fetched.get(0).isRely_cstr());
        Assert.assertEquals(table.getCatName(), fetched.get(0).getCatName());
        client.dropConstraint(table.getCatName(), table.getDbName(), table.getTableName(), tablePkName);
        rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        fetched = client.getNotNullConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
    }

    @Test
    public void doubleAddNotNullConstraint() throws TException {
        Table table = testTables[0];
        // Make sure get on a table with no key returns empty list
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(table.getCatName(), table.getDbName(), table.getTableName());
        List<SQLNotNullConstraint> fetched = client.getNotNullConstraints(rqst);
        Assert.assertTrue(fetched.isEmpty());
        // Single column unnamed primary key in default catalog and database
        List<SQLNotNullConstraint> nn = new SQLNotNullConstraintBuilder().onTable(table).addColumn("col1").build(metaStore.getConf());
        client.addNotNullConstraint(nn);
        try {
            nn = new SQLNotNullConstraintBuilder().onTable(table).addColumn("col2").build(metaStore.getConf());
            client.addNotNullConstraint(nn);
            Assert.fail();
        } catch (InvalidObjectException | TApplicationException e) {
            // NOP
        }
    }

    @Test
    public void addNoSuchTable() throws TException {
        try {
            List<SQLNotNullConstraint> nn = new SQLNotNullConstraintBuilder().setTableName("nosuch").addColumn("col2").build(metaStore.getConf());
            client.addNotNullConstraint(nn);
            Assert.fail();
        } catch (InvalidObjectException | TApplicationException e) {
            // NOP
        }
    }

    @Test
    public void getNoSuchTable() throws TException {
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, Warehouse.DEFAULT_DATABASE_NAME, "nosuch");
        List<SQLNotNullConstraint> nn = client.getNotNullConstraints(rqst);
        Assert.assertTrue(nn.isEmpty());
    }

    @Test
    public void getNoSuchDb() throws TException {
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, "nosuch", testTables[0].getTableName());
        List<SQLNotNullConstraint> nn = client.getNotNullConstraints(rqst);
        Assert.assertTrue(nn.isEmpty());
    }

    @Test
    public void getNoSuchCatalog() throws TException {
        NotNullConstraintsRequest rqst = new NotNullConstraintsRequest("nosuch", testTables[0].getDbName(), testTables[0].getTableName());
        List<SQLNotNullConstraint> nn = client.getNotNullConstraints(rqst);
        Assert.assertTrue(nn.isEmpty());
    }
}

