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


import ColumnType.INT_TYPE_NAME;
import ColumnType.STRING_TYPE_NAME;
import hive_metastoreConstants.TABLE_IS_TRANSACTIONAL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.Type;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAcidTableSetup {
    private static final Logger LOG = LoggerFactory.getLogger(TestHiveMetaStore.class);

    protected static HiveMetaStoreClient client;

    protected static Configuration conf;

    @Test
    public void testTransactionalValidation() throws Throwable {
        String dbName = "acidDb";
        TestAcidTableSetup.silentDropDatabase(dbName);
        Database db = new Database();
        db.setName(dbName);
        TestAcidTableSetup.client.createDatabase(db);
        String tblName = "acidTable";
        Map<String, String> fields = new HashMap<>();
        fields.put("name", STRING_TYPE_NAME);
        fields.put("income", INT_TYPE_NAME);
        Type type = createType("Person1", fields);
        Map<String, String> params = new HashMap<>();
        params.put("transactional", "");
        // / CREATE TABLE scenarios
        // Fail - No "transactional" property is specified
        try {
            Table t = new TableBuilder().setDbName(dbName).setTableName(tblName).setTableParams(params).setCols(type.getFields()).build(TestAcidTableSetup.conf);
            TestAcidTableSetup.client.createTable(t);
            Assert.fail("Expected exception");
        } catch (MetaException e) {
            Assert.assertEquals("'transactional' property of TBLPROPERTIES may only have value 'true': acidDb.acidTable", e.getMessage());
        }
        // Fail - "transactional" property is set to an invalid value
        try {
            params.clear();
            params.put("transactional", "foobar");
            Table t = new TableBuilder().setDbName(dbName).setTableName(tblName).setTableParams(params).setCols(type.getFields()).build(TestAcidTableSetup.conf);
            TestAcidTableSetup.client.createTable(t);
            Assert.fail("Expected exception");
        } catch (MetaException e) {
            Assert.assertEquals("'transactional' property of TBLPROPERTIES may only have value 'true': acidDb.acidTable", e.getMessage());
        }
        // Fail - "transactional" is set to true, but the table is not bucketed
        try {
            params.clear();
            params.put("transactional", "true");
            Table t = new TableBuilder().setDbName(dbName).setTableName(tblName).setTableParams(params).setCols(type.getFields()).build(TestAcidTableSetup.conf);
            TestAcidTableSetup.client.createTable(t);
            Assert.fail("Expected exception");
        } catch (MetaException e) {
            Assert.assertEquals("The table must be stored using an ACID compliant format (such as ORC): acidDb.acidTable", e.getMessage());
        }
        List<String> bucketCols = new ArrayList<>();
        bucketCols.add("income");
        // Fail - "transactional" is set to true, and the table is bucketed, but doesn't use ORC
        try {
            params.clear();
            params.put("transactional", "true");
            Table t = new TableBuilder().setDbName(dbName).setTableName(tblName).setTableParams(params).setCols(type.getFields()).setBucketCols(bucketCols).build(TestAcidTableSetup.conf);
            TestAcidTableSetup.client.createTable(t);
            Assert.fail("Expected exception");
        } catch (MetaException e) {
            Assert.assertEquals("The table must be stored using an ACID compliant format (such as ORC): acidDb.acidTable", e.getMessage());
        }
        // Succeed - "transactional" is set to true, and the table is bucketed, and uses ORC
        params.clear();
        params.put("transactional", "true");
        Table t = new TableBuilder().setDbName(dbName).setTableName(tblName).setTableParams(params).setCols(type.getFields()).setBucketCols(bucketCols).setInputFormat("org.apache.hadoop.hive.ql.io.orc.OrcInputFormat").setOutputFormat("org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat").build(TestAcidTableSetup.conf);
        TestAcidTableSetup.client.createTable(t);
        Assert.assertTrue("CREATE TABLE should succeed", "true".equals(t.getParameters().get(TABLE_IS_TRANSACTIONAL)));
        // / ALTER TABLE scenarios
        // Fail - trying to set "transactional" to "false" is not allowed
        try {
            params.clear();
            params.put("transactional", "false");
            t = new Table();
            t.setParameters(params);
            t.setDbName(dbName);
            t.setTableName(tblName);
            TestAcidTableSetup.client.alter_table(dbName, tblName, t);
            Assert.fail("Expected exception");
        } catch (MetaException e) {
            Assert.assertEquals("TBLPROPERTIES with 'transactional'='true' cannot be unset: acidDb.acidTable", e.getMessage());
        }
        // Fail - trying to set "transactional" to "true" but doesn't satisfy bucketing and Input/OutputFormat requirement
        try {
            tblName += "1";
            params.clear();
            t = new TableBuilder().setDbName(dbName).setTableName(tblName).setCols(type.getFields()).setInputFormat("org.apache.hadoop.mapred.FileInputFormat").build(TestAcidTableSetup.conf);
            TestAcidTableSetup.client.createTable(t);
            params.put("transactional", "true");
            t.setParameters(params);
            TestAcidTableSetup.client.alter_table(dbName, tblName, t);
            Assert.fail("Expected exception");
        } catch (MetaException e) {
            Assert.assertEquals("The table must be stored using an ACID compliant format (such as ORC): acidDb.acidTable1", e.getMessage());
        }
        // Succeed - trying to set "transactional" to "true", and satisfies bucketing and Input/OutputFormat requirement
        tblName += "2";
        params.clear();
        t = new TableBuilder().setDbName(dbName).setTableName(tblName).setCols(type.getFields()).setNumBuckets(1).setBucketCols(bucketCols).setInputFormat("org.apache.hadoop.hive.ql.io.orc.OrcInputFormat").setOutputFormat("org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat").build(TestAcidTableSetup.conf);
        TestAcidTableSetup.client.createTable(t);
        params.put("transactional", "true");
        t.setParameters(params);
        TestAcidTableSetup.client.alter_table(dbName, tblName, t);
        Assert.assertTrue("ALTER TABLE should succeed", "true".equals(t.getParameters().get(TABLE_IS_TRANSACTIONAL)));
    }
}

