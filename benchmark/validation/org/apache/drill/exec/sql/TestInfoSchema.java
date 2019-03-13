/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.sql;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.store.dfs.FileSystemConfig;
import org.apache.drill.exec.store.ischema.InfoSchemaConstants;
import org.apache.drill.exec.vector.NullableVarCharVector;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Contains tests for
 * -- InformationSchema
 * -- Queries on InformationSchema such as SHOW TABLES, SHOW SCHEMAS or DESCRIBE table
 * -- USE schema
 */
@Category(SqlTest.class)
public class TestInfoSchema extends BaseTestQuery {
    public static final String TEST_SUB_DIR = "testSubDir";

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    public void selectFromAllTables() throws Exception {
        BaseTestQuery.test("select * from INFORMATION_SCHEMA.SCHEMATA");
        BaseTestQuery.test("select * from INFORMATION_SCHEMA.CATALOGS");
        BaseTestQuery.test("select * from INFORMATION_SCHEMA.VIEWS");
        BaseTestQuery.test("select * from INFORMATION_SCHEMA.`TABLES`");
        BaseTestQuery.test("select * from INFORMATION_SCHEMA.COLUMNS");
        BaseTestQuery.test("select * from INFORMATION_SCHEMA.`FILES`");
    }

    @Test
    public void catalogs() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SELECT * FROM INFORMATION_SCHEMA.CATALOGS").unOrdered().baselineColumns(InfoSchemaConstants.CATS_COL_CATALOG_NAME, InfoSchemaConstants.CATS_COL_CATALOG_DESCRIPTION, InfoSchemaConstants.CATS_COL_CATALOG_CONNECT).baselineValues("DRILL", "The internal metadata used by Drill", "").go();
    }

    @Test
    public void showTablesFromDb() throws Exception {
        final List<String[]> expected = Arrays.asList(new String[]{ "information_schema", "VIEWS" }, new String[]{ "information_schema", "COLUMNS" }, new String[]{ "information_schema", "TABLES" }, new String[]{ "information_schema", "CATALOGS" }, new String[]{ "information_schema", "SCHEMATA" }, new String[]{ "information_schema", "FILES" });
        final TestBuilder t1 = BaseTestQuery.testBuilder().sqlQuery("SHOW TABLES FROM INFORMATION_SCHEMA").unOrdered().baselineColumns("TABLE_SCHEMA", "TABLE_NAME");
        for (String[] expectedRow : expected) {
            t1.baselineValues(expectedRow);
        }
        t1.go();
        final TestBuilder t2 = BaseTestQuery.testBuilder().sqlQuery("SHOW TABLES IN INFORMATION_SCHEMA").unOrdered().baselineColumns("TABLE_SCHEMA", "TABLE_NAME");
        for (String[] expectedRow : expected) {
            t2.baselineValues(expectedRow);
        }
        t2.go();
    }

    @Test
    public void showTablesFromDbWhere() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SHOW TABLES FROM INFORMATION_SCHEMA WHERE TABLE_NAME='VIEWS'").unOrdered().baselineColumns("TABLE_SCHEMA", "TABLE_NAME").baselineValues("information_schema", "VIEWS").go();
    }

    @Test
    public void showTablesLike() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SHOW TABLES LIKE '%CH%'").unOrdered().optionSettingQueriesForTestQuery("USE INFORMATION_SCHEMA").baselineColumns("TABLE_SCHEMA", "TABLE_NAME").baselineValues("information_schema", "SCHEMATA").go();
    }

    @Test
    public void showDatabases() throws Exception {
        List<String> expected = Arrays.asList("dfs.default", "dfs.root", "dfs.tmp", "cp.default", "sys", "information_schema");
        TestBuilder t1 = BaseTestQuery.testBuilder().sqlQuery("SHOW DATABASES").unOrdered().baselineColumns("SCHEMA_NAME");
        expected.forEach(t1::baselineValues);
        t1.go();
        TestBuilder t2 = BaseTestQuery.testBuilder().sqlQuery("SHOW SCHEMAS").unOrdered().baselineColumns("SCHEMA_NAME");
        expected.forEach(t2::baselineValues);
        t2.go();
    }

    @Test
    public void showDatabasesWhere() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SHOW DATABASES WHERE SCHEMA_NAME='dfs.tmp'").unOrdered().baselineColumns("SCHEMA_NAME").baselineValues("dfs.tmp").go();
    }

    @Test
    public void showDatabasesLike() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SHOW DATABASES LIKE '%y%'").unOrdered().baselineColumns("SCHEMA_NAME").baselineValues("sys").go();
    }

    @Test
    public void describeTable() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE CATALOGS").unOrdered().optionSettingQueriesForTestQuery("USE INFORMATION_SCHEMA").baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("CATALOG_NAME", "CHARACTER VARYING", "NO").baselineValues("CATALOG_DESCRIPTION", "CHARACTER VARYING", "NO").baselineValues("CATALOG_CONNECT", "CHARACTER VARYING", "NO").go();
    }

    @Test
    public void describeTableWithSchema() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE INFORMATION_SCHEMA.`TABLES`").unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("TABLE_CATALOG", "CHARACTER VARYING", "NO").baselineValues("TABLE_SCHEMA", "CHARACTER VARYING", "NO").baselineValues("TABLE_NAME", "CHARACTER VARYING", "NO").baselineValues("TABLE_TYPE", "CHARACTER VARYING", "NO").go();
    }

    @Test
    public void describeWhenSameTableNameExistsInMultipleSchemas() throws Exception {
        try {
            BaseTestQuery.test("USE dfs.tmp");
            BaseTestQuery.test("CREATE OR REPLACE VIEW `TABLES` AS SELECT full_name FROM cp.`employee.json`");
            BaseTestQuery.testBuilder().sqlQuery("DESCRIBE `TABLES`").unOrdered().optionSettingQueriesForTestQuery("USE dfs.tmp").baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("full_name", "ANY", "YES").go();
            BaseTestQuery.testBuilder().sqlQuery("DESCRIBE INFORMATION_SCHEMA.`TABLES`").unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("TABLE_CATALOG", "CHARACTER VARYING", "NO").baselineValues("TABLE_SCHEMA", "CHARACTER VARYING", "NO").baselineValues("TABLE_NAME", "CHARACTER VARYING", "NO").baselineValues("TABLE_TYPE", "CHARACTER VARYING", "NO").go();
        } finally {
            BaseTestQuery.test("DROP VIEW dfs.tmp.`TABLES`");
        }
    }

    @Test
    public void describeTableWithColumnName() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE `TABLES` TABLE_CATALOG").unOrdered().optionSettingQueriesForTestQuery("USE INFORMATION_SCHEMA").baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("TABLE_CATALOG", "CHARACTER VARYING", "NO").go();
    }

    @Test
    public void describeTableWithSchemaAndColumnName() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE INFORMATION_SCHEMA.`TABLES` TABLE_CATALOG").unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("TABLE_CATALOG", "CHARACTER VARYING", "NO").go();
    }

    @Test
    public void describeTableWithColQualifier() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE COLUMNS 'TABLE%'").unOrdered().optionSettingQueriesForTestQuery("USE INFORMATION_SCHEMA").baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("TABLE_CATALOG", "CHARACTER VARYING", "NO").baselineValues("TABLE_SCHEMA", "CHARACTER VARYING", "NO").baselineValues("TABLE_NAME", "CHARACTER VARYING", "NO").go();
    }

    @Test
    public void describeTableWithSchemaAndColQualifier() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE INFORMATION_SCHEMA.SCHEMATA 'SCHEMA%'").unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("SCHEMA_NAME", "CHARACTER VARYING", "NO").baselineValues("SCHEMA_OWNER", "CHARACTER VARYING", "NO").go();
    }

    @Test
    public void defaultSchemaDfs() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SELECT R_REGIONKEY FROM `sample-data/region.parquet` LIMIT 1").unOrdered().optionSettingQueriesForTestQuery("USE dfs").baselineColumns("R_REGIONKEY").baselineValues(0L).go();
    }

    @Test
    public void defaultSchemaClasspath() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SELECT full_name FROM `employee.json` LIMIT 1").unOrdered().optionSettingQueriesForTestQuery("USE cp").baselineColumns("full_name").baselineValues("Sheri Nowmer").go();
    }

    @Test
    public void queryFromNonDefaultSchema() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SELECT full_name FROM cp.`employee.json` LIMIT 1").unOrdered().optionSettingQueriesForTestQuery("USE dfs").baselineColumns("full_name").baselineValues("Sheri Nowmer").go();
    }

    @Test
    public void useSchema() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("USE dfs.`default`").unOrdered().baselineColumns("ok", "summary").baselineValues(true, "Default schema changed to [dfs.default]").go();
    }

    @Test
    public void useSubSchemaWithinSchema() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("USE dfs").unOrdered().baselineColumns("ok", "summary").baselineValues(true, "Default schema changed to [dfs]").go();
        BaseTestQuery.testBuilder().sqlQuery("USE tmp").unOrdered().baselineColumns("ok", "summary").baselineValues(true, "Default schema changed to [dfs.tmp]").go();
        BaseTestQuery.testBuilder().sqlQuery("USE dfs.`default`").unOrdered().baselineColumns("ok", "summary").baselineValues(true, "Default schema changed to [dfs.default]").go();
    }

    @Test
    public void useSchemaNegative() throws Exception {
        BaseTestQuery.errorMsgTestHelper("USE invalid.schema", "Schema [invalid.schema] is not valid with respect to either root schema or current default schema.");
    }

    // Tests using backticks around the complete schema path
    // select * from `dfs.tmp`.`/tmp/nation.parquet`;
    @Test
    public void completeSchemaRef1() throws Exception {
        BaseTestQuery.test("SELECT * FROM `cp.default`.`employee.json` limit 2");
    }

    @Test
    public void describeSchemaSyntax() throws Exception {
        BaseTestQuery.test("describe schema dfs");
        BaseTestQuery.test("describe schema dfs.`default`");
        BaseTestQuery.test("describe database dfs.`default`");
    }

    @Test
    public void describePartialSchema() throws Exception {
        BaseTestQuery.test("use dfs");
        BaseTestQuery.test("describe schema tmp");
    }

    @Test
    public void describeSchemaOutput() throws Exception {
        final List<QueryDataBatch> result = BaseTestQuery.testSqlWithResults("describe schema dfs.tmp");
        Assert.assertEquals(1, result.size());
        final QueryDataBatch batch = result.get(0);
        final RecordBatchLoader loader = new RecordBatchLoader(BaseTestQuery.getDrillbitContext().getAllocator());
        loader.load(batch.getHeader().getDef(), batch.getData());
        // check schema column value
        final VectorWrapper schemaValueVector = loader.getValueAccessorById(NullableVarCharVector.class, loader.getValueVectorId(SchemaPath.getCompoundPath("schema")).getFieldIds());
        String schema = schemaValueVector.getValueVector().getAccessor().getObject(0).toString();
        Assert.assertEquals("dfs.tmp", schema);
        // check properties column value
        final VectorWrapper propertiesValueVector = loader.getValueAccessorById(NullableVarCharVector.class, loader.getValueVectorId(SchemaPath.getCompoundPath("properties")).getFieldIds());
        String properties = propertiesValueVector.getValueVector().getAccessor().getObject(0).toString();
        final Map configMap = TestInfoSchema.mapper.readValue(properties, Map.class);
        // check some stable properties existence
        Assert.assertTrue(configMap.containsKey("connection"));
        Assert.assertTrue(configMap.containsKey("config"));
        Assert.assertTrue(configMap.containsKey("formats"));
        Assert.assertFalse(configMap.containsKey("workspaces"));
        // check some stable properties values
        Assert.assertEquals("file", configMap.get("type"));
        final FileSystemConfig testConfig = ((FileSystemConfig) (BaseTestQuery.bits[0].getContext().getStorage().getPlugin("dfs").getConfig()));
        final String tmpSchemaLocation = testConfig.getWorkspaces().get("tmp").getLocation();
        Assert.assertEquals(tmpSchemaLocation, configMap.get("location"));
        batch.release();
        loader.clear();
    }

    @Test
    public void describeSchemaInvalid() throws Exception {
        BaseTestQuery.errorMsgTestHelper("describe schema invalid.schema", "Invalid schema name [invalid.schema]");
    }
}

