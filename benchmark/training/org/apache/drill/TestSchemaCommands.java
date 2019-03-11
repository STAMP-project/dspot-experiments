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
package org.apache.drill;


import SchemaProvider.DEFAULT_SCHEMA_NAME;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.record.metadata.schema.SchemaContainer;
import org.apache.drill.exec.record.metadata.schema.SchemaProvider;
import org.apache.drill.test.ClusterTest;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category(SqlTest.class)
public class TestSchemaCommands extends ClusterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateWithoutSchema() throws Exception {
        thrown.expect(UserException.class);
        thrown.expectMessage("PARSE ERROR: Lexical error");
        ClusterTest.run("create schema for");
    }

    @Test
    public void testCreateWithForAndPath() throws Exception {
        thrown.expect(UserException.class);
        thrown.expectMessage("PARSE ERROR: Encountered \"path\"");
        ClusterTest.run("create schema ( col1 int, col2 int) for table tbl path '/tmp/schema.file'");
    }

    @Test
    public void testCreateWithPathAndOrReplace() throws Exception {
        thrown.expect(UserException.class);
        thrown.expectMessage("PARSE ERROR: <OR REPLACE> cannot be used with <PATH> property");
        ClusterTest.run("create or replace schema (col1 int, col2 int) path '/tmp/schema.file'");
    }

    @Test
    public void testCreateForMissingTable() throws Exception {
        String table = "dfs.tmp.tbl";
        thrown.expect(UserException.class);
        thrown.expectMessage("VALIDATION ERROR: Table [tbl] was not found");
        ClusterTest.run("create schema (col1 int, col2 int) for table %s", table);
    }

    @Test
    public void testCreateForTemporaryTable() throws Exception {
        String table = "temp_create";
        try {
            ClusterTest.run("create temporary table %s as select 'a' as c from (values(1))", table);
            thrown.expect(UserException.class);
            thrown.expectMessage(String.format("VALIDATION ERROR: Indicated table [%s] is temporary table", table));
            ClusterTest.run("create schema (col1 int, col2 int) for table %s", table);
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }

    @Test
    public void testCreateForImmutableSchema() throws Exception {
        String table = "sys.version";
        thrown.expect(UserException.class);
        thrown.expectMessage("VALIDATION ERROR: Unable to create or drop objects. Schema [sys] is immutable");
        ClusterTest.run("create schema (col1 int, col2 int) for table %s", table);
    }

    @Test
    public void testMissingDirectory() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getTmpDir();
        Path schema = new Path(Paths.get(tmpDir.getPath(), "missing_parent_directory", "file.schema").toFile().getPath());
        thrown.expect(UserException.class);
        thrown.expectMessage(String.format("RESOURCE ERROR: Parent path for schema file [%s] does not exist", schema.toUri().getPath()));
        ClusterTest.run("create schema (col1 int, col2 int) path '%s'", schema.toUri().getPath());
    }

    @Test
    public void testTableAsFile() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getDfsTestTmpDir();
        String table = "test_table_as_file.json";
        File tablePath = new File(tmpDir, table);
        Assert.assertTrue(tablePath.createNewFile());
        thrown.expect(UserException.class);
        thrown.expectMessage(String.format("RESOURCE ERROR: Indicated table [%s] must be a directory", String.format("dfs.tmp.%s", table)));
        try {
            ClusterTest.run("create schema (col1 int, col2 int) for table %s.`%s`", "dfs.tmp", table);
        } finally {
            Assert.assertTrue(tablePath.delete());
        }
    }

    @Test
    public void testCreateSimpleForPathWithExistingSchema() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getTmpDir();
        File schema = new File(tmpDir, "simple_for_path.schema");
        Assert.assertTrue(schema.createNewFile());
        thrown.expect(UserException.class);
        thrown.expectMessage(String.format("VALIDATION ERROR: Schema already exists for [%s]", schema.getPath()));
        try {
            ClusterTest.run("create schema (col1 int, col2 int) path '%s'", schema.getPath());
        } finally {
            Assert.assertTrue(schema.delete());
        }
    }

    @Test
    public void testCreateSimpleForTableWithExistingSchema() throws Exception {
        String table = "dfs.tmp.table_for_simple_existing_schema";
        try {
            ClusterTest.run("create table %s as select 'a' as c from (values(1))", table);
            testBuilder().sqlQuery("create schema (c varchar not null) for table %s", table).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", table)).go();
            thrown.expect(UserRemoteException.class);
            thrown.expectMessage(String.format("VALIDATION ERROR: Schema already exists for [%s]", table));
            ClusterTest.run("create schema (c varchar not null) for table %s", table);
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }

    @Test
    public void testSuccessfulCreateForPath() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getTmpDir();
        File schemaFile = new File(tmpDir, "schema_for_successful_create_for_path.schema");
        Assert.assertFalse(schemaFile.exists());
        try {
            testBuilder().sqlQuery("create schema (i int not null, v varchar) path '%s'", schemaFile.getPath()).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", schemaFile.getPath())).go();
            SchemaProvider schemaProvider = new org.apache.drill.exec.record.metadata.schema.PathSchemaProvider(new Path(schemaFile.getPath()));
            Assert.assertTrue(schemaProvider.exists());
            SchemaContainer schemaContainer = schemaProvider.read();
            Assert.assertNull(schemaContainer.getTable());
            Assert.assertNotNull(schemaContainer.getSchema());
            TupleMetadata schema = schemaContainer.getSchema();
            ColumnMetadata intColumn = schema.metadata("i");
            Assert.assertFalse(intColumn.isNullable());
            Assert.assertEquals(INT, intColumn.type());
            ColumnMetadata varcharColumn = schema.metadata("v");
            Assert.assertTrue(varcharColumn.isNullable());
            Assert.assertEquals(VARCHAR, varcharColumn.type());
        } finally {
            if (schemaFile.exists()) {
                Assert.assertTrue(schemaFile.delete());
            }
        }
    }

    @Test
    public void testSuccessfulCreateOrReplaceForTable() throws Exception {
        String tableName = "table_for_successful_create_or_replace_for_table";
        String table = String.format("dfs.tmp.%s", tableName);
        try {
            ClusterTest.run("create table %s as select 'a' as c from (values(1))", table);
            File schemaPath = Paths.get(ClusterTest.dirTestWatcher.getDfsTestTmpDir().getPath(), tableName, DEFAULT_SCHEMA_NAME).toFile();
            Assert.assertFalse(schemaPath.exists());
            testBuilder().sqlQuery("create schema (c varchar not null) for table %s", table).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", table)).go();
            SchemaProvider schemaProvider = new org.apache.drill.exec.record.metadata.schema.PathSchemaProvider(new Path(schemaPath.getPath()));
            Assert.assertTrue(schemaProvider.exists());
            SchemaContainer schemaContainer = schemaProvider.read();
            Assert.assertNotNull(schemaContainer.getTable());
            Assert.assertEquals(String.format("dfs.tmp.`%s`", tableName), schemaContainer.getTable());
            Assert.assertNotNull(schemaContainer.getSchema());
            ColumnMetadata column = schemaContainer.getSchema().metadata("c");
            Assert.assertFalse(column.isNullable());
            Assert.assertEquals(VARCHAR, column.type());
            testBuilder().sqlQuery("create or replace schema (c varchar) for table %s", table).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", table)).go();
            Assert.assertTrue(schemaProvider.exists());
            SchemaContainer updatedSchemaContainer = schemaProvider.read();
            Assert.assertNotNull(updatedSchemaContainer.getTable());
            Assert.assertEquals(String.format("dfs.tmp.`%s`", tableName), updatedSchemaContainer.getTable());
            Assert.assertNotNull(updatedSchemaContainer.getSchema());
            ColumnMetadata updatedColumn = updatedSchemaContainer.getSchema().metadata("c");
            Assert.assertTrue(updatedColumn.isNullable());
            Assert.assertEquals(VARCHAR, updatedColumn.type());
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }

    @Test
    public void testCreateWithProperties() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getTmpDir();
        File schemaFile = new File(tmpDir, "schema_for_create_with_properties.schema");
        Assert.assertFalse(schemaFile.exists());
        try {
            testBuilder().sqlQuery(("create schema (i int not null) path '%s' " + "properties ('k1' = 'v1', 'k2' = 'v2', 'k3' = 'v3')"), schemaFile.getPath()).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", schemaFile.getPath())).go();
            SchemaProvider schemaProvider = new org.apache.drill.exec.record.metadata.schema.PathSchemaProvider(new Path(schemaFile.getPath()));
            Assert.assertTrue(schemaProvider.exists());
            SchemaContainer schemaContainer = schemaProvider.read();
            Assert.assertNull(schemaContainer.getTable());
            Assert.assertNotNull(schemaContainer.getSchema());
            Assert.assertNotNull(schemaContainer.getProperties());
            Map<String, String> properties = new LinkedHashMap<>();
            properties.put("k1", "v1");
            properties.put("k2", "v2");
            properties.put("k3", "v3");
            Assert.assertEquals(properties.size(), schemaContainer.getProperties().size());
            Assert.assertEquals(properties, schemaContainer.getProperties());
        } finally {
            if (schemaFile.exists()) {
                Assert.assertTrue(schemaFile.delete());
            }
        }
    }

    @Test
    public void testCreateWithoutProperties() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getTmpDir();
        File schemaFile = new File(tmpDir, "schema_for_create_without_properties.schema");
        Assert.assertFalse(schemaFile.exists());
        try {
            testBuilder().sqlQuery("create schema (i int not null) path '%s'", schemaFile.getPath()).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", schemaFile.getPath())).go();
            SchemaProvider schemaProvider = new org.apache.drill.exec.record.metadata.schema.PathSchemaProvider(new Path(schemaFile.getPath()));
            Assert.assertTrue(schemaProvider.exists());
            SchemaContainer schemaContainer = schemaProvider.read();
            Assert.assertNull(schemaContainer.getTable());
            Assert.assertNotNull(schemaContainer.getSchema());
            Assert.assertNotNull(schemaContainer.getProperties());
            Assert.assertEquals(0, schemaContainer.getProperties().size());
        } finally {
            if (schemaFile.exists()) {
                Assert.assertTrue(schemaFile.delete());
            }
        }
    }

    @Test
    public void testCreateUsingLoadFromMissingFile() throws Exception {
        thrown.expect(UserException.class);
        thrown.expectMessage("RESOURCE ERROR: File with raw schema [path/to/file] does not exist");
        ClusterTest.run("create schema load 'path/to/file' for table dfs.tmp.t");
    }

    @Test
    public void testCreateUsingLoad() throws Exception {
        File tmpDir = ClusterTest.dirTestWatcher.getTmpDir();
        File rawSchema = new File(tmpDir, "raw.schema");
        File schemaFile = new File(tmpDir, "schema_for_create_using_load.schema");
        try {
            Files.write(rawSchema.toPath(), Arrays.asList("i int,", "v varchar"));
            Assert.assertTrue(rawSchema.exists());
            testBuilder().sqlQuery("create schema load '%s' path '%s' properties ('k1'='v1', 'k2' = 'v2')", rawSchema.getPath(), schemaFile.getPath()).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", schemaFile.getPath())).go();
            SchemaProvider schemaProvider = new org.apache.drill.exec.record.metadata.schema.PathSchemaProvider(new Path(schemaFile.getPath()));
            Assert.assertTrue(schemaFile.exists());
            SchemaContainer schemaContainer = schemaProvider.read();
            Assert.assertNull(schemaContainer.getTable());
            TupleMetadata schema = schemaContainer.getSchema();
            Assert.assertNotNull(schema);
            Assert.assertEquals(2, schema.size());
            Assert.assertEquals(INT, schema.metadata("i").type());
            Assert.assertEquals(VARCHAR, schema.metadata("v").type());
            Assert.assertNotNull(schemaContainer.getProperties());
            Assert.assertEquals(2, schemaContainer.getProperties().size());
        } finally {
            if (rawSchema.exists()) {
                Assert.assertTrue(rawSchema.delete());
            }
        }
    }

    @Test
    public void testDropWithoutTable() throws Exception {
        thrown.expect(UserException.class);
        thrown.expectMessage("PARSE ERROR: Encountered \"<EOF>\"");
        ClusterTest.run("drop schema");
    }

    @Test
    public void testDropForMissingTable() throws Exception {
        thrown.expect(UserException.class);
        thrown.expectMessage("VALIDATION ERROR: Table [t] was not found");
        ClusterTest.run("drop schema for table dfs.t");
    }

    @Test
    public void testDropForTemporaryTable() throws Exception {
        String table = "temp_drop";
        try {
            ClusterTest.run("create temporary table %s as select 'a' as c from (values(1))", table);
            thrown.expect(UserException.class);
            thrown.expectMessage(String.format("VALIDATION ERROR: Indicated table [%s] is temporary table", table));
            ClusterTest.run("drop schema for table %s", table);
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }

    @Test
    public void testDropForImmutableSchema() throws Exception {
        String table = "sys.version";
        thrown.expect(UserException.class);
        thrown.expectMessage("VALIDATION ERROR: Unable to create or drop objects. Schema [sys] is immutable");
        ClusterTest.run("drop schema for table %s", table);
    }

    @Test
    public void testDropForMissingSchema() throws Exception {
        String table = "dfs.tmp.table_with_missing_schema";
        try {
            ClusterTest.run("create table %s as select 'a' as c from (values(1))", table);
            thrown.expect(UserException.class);
            thrown.expectMessage(String.format(("VALIDATION ERROR: Schema [%s] " + "does not exist in table [%s] root directory"), DEFAULT_SCHEMA_NAME, table));
            ClusterTest.run("drop schema for table %s", table);
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }

    @Test
    public void testDropForMissingSchemaIfExists() throws Exception {
        String table = "dfs.tmp.table_with_missing_schema_if_exists";
        try {
            ClusterTest.run("create table %s as select 'a' as c from (values(1))", table);
            testBuilder().sqlQuery("drop schema if exists for table %s", table).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("Schema [%s] does not exist in table [%s] root directory", DEFAULT_SCHEMA_NAME, table)).go();
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }

    @Test
    public void testSuccessfulDrop() throws Exception {
        String tableName = "table_for_successful_drop";
        String table = String.format("dfs.tmp.%s", tableName);
        try {
            ClusterTest.run("create table %s as select 'a' as c from (values(1))", table);
            File schemaPath = Paths.get(ClusterTest.dirTestWatcher.getDfsTestTmpDir().getPath(), tableName, DEFAULT_SCHEMA_NAME).toFile();
            Assert.assertFalse(schemaPath.exists());
            testBuilder().sqlQuery("create schema (c varchar not null) for table %s", table).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Created schema for [%s]", table)).go();
            Assert.assertTrue(schemaPath.exists());
            testBuilder().sqlQuery("drop schema for table %s", table).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Dropped schema for table [%s]", table)).go();
            Assert.assertFalse(schemaPath.exists());
        } finally {
            ClusterTest.run("drop table if exists %s", table);
        }
    }
}

