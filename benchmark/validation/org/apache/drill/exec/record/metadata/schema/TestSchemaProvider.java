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
package org.apache.drill.exec.record.metadata.schema;


import SchemaContainer.Version;
import SchemaContainer.Version.CURRENT_DEFAULT_VERSION;
import StorageStrategy.DEFAULT;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class TestSchemaProvider {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInlineProviderExists() throws Exception {
        SchemaProvider provider = new InlineSchemaProvider("(i int)", null);
        Assert.assertTrue(provider.exists());
    }

    @Test
    public void testInlineProviderDelete() throws Exception {
        SchemaProvider provider = new InlineSchemaProvider("(i int)", null);
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Schema deletion is not supported");
        provider.delete();
    }

    @Test
    public void testInlineProviderStore() throws Exception {
        SchemaProvider provider = new InlineSchemaProvider("(i int)", null);
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Schema storage is not supported");
        provider.store("i int", null, DEFAULT);
    }

    @Test
    public void testInlineProviderRead() throws Exception {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("k1", "v1");
        SchemaProvider provider = new InlineSchemaProvider("(i int)", properties);
        SchemaContainer schemaContainer = provider.read();
        Assert.assertNotNull(schemaContainer);
        Assert.assertNull(schemaContainer.getTable());
        TupleMetadata metadata = schemaContainer.getSchema();
        Assert.assertNotNull(metadata);
        Assert.assertEquals(1, metadata.size());
        Assert.assertEquals(INT, metadata.metadata("i").type());
        Assert.assertEquals(properties, schemaContainer.getProperties());
        SchemaContainer.Version version = schemaContainer.getVersion();
        Assert.assertFalse(version.isUndefined());
        Assert.assertEquals(CURRENT_DEFAULT_VERSION, version.getValue());
    }

    @Test
    public void testPathProviderExists() throws Exception {
        File schema = new File(folder.getRoot(), "schema");
        SchemaProvider provider = new PathSchemaProvider(new Path(schema.getPath()));
        Assert.assertFalse(provider.exists());
        Assert.assertTrue(schema.createNewFile());
        Assert.assertTrue(provider.exists());
    }

    @Test
    public void testPathProviderDelete() throws Exception {
        File schema = folder.newFile("schema");
        Assert.assertTrue(schema.exists());
        SchemaProvider provider = new PathSchemaProvider(new Path(schema.getPath()));
        provider.delete();
        Assert.assertFalse(schema.exists());
    }

    @Test
    public void testPathProviderDeleteAbsentFile() throws Exception {
        File schema = new File(folder.getRoot(), "absent_file");
        SchemaProvider provider = new PathSchemaProvider(new Path(schema.getPath()));
        Assert.assertFalse(schema.exists());
        provider.delete();
        Assert.assertFalse(schema.exists());
    }

    @Test
    public void testPathProviderStore() throws Exception {
        File schema = new File(folder.getRoot(), "schema");
        SchemaProvider provider = new PathSchemaProvider(new Path(schema.getPath()));
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("k1", "v1");
        properties.put("k2", "v2");
        Assert.assertFalse(provider.exists());
        provider.store("i int, v varchar(10)", properties, DEFAULT);
        Assert.assertTrue(provider.exists());
        String expectedContent = "{\n" + ((((((((("  \"schema\" : [\n" + "    \"`i` INT\",\n") + "    \"`v` VARCHAR(10)\"\n") + "  ],\n") + "  \"properties\" : {\n") + "    \"k1\" : \"v1\",\n") + "    \"k2\" : \"v2\"\n") + "  },\n") + "  \"version\" : 1\n") + "}");
        List<String> lines = Files.readAllLines(schema.toPath());
        Assert.assertEquals(expectedContent, String.join("\n", lines));
    }

    @Test
    public void testPathProviderStoreInExistingFile() throws Exception {
        File schemaFile = folder.newFile("schema");
        Path schema = new Path(schemaFile.getPath());
        SchemaProvider provider = new PathSchemaProvider(schema);
        Assert.assertTrue(provider.exists());
        thrown.expect(IOException.class);
        thrown.expectMessage("File already exists");
        provider.store("i int", null, DEFAULT);
    }

    @Test
    public void testPathProviderRead() throws Exception {
        java.nio.file.Path schemaPath = folder.newFile("schema").toPath();
        Files.write(schemaPath, Collections.singletonList(("{  \n" + ((((((((("   \"table\":\"tbl\",\n" + "   \"schema\":[  \n") + "      \"`i` INT\",\n") + "      \"`v` VARCHAR\"\n") + "   ],\n") + "   \"properties\" : {\n") + "      \"k1\" : \"v1\",\n") + "      \"k2\" : \"v2\"\n") + "   }\n") + "}\n"))));
        SchemaProvider provider = new PathSchemaProvider(new Path(schemaPath.toUri().getPath()));
        Assert.assertTrue(provider.exists());
        SchemaContainer schemaContainer = provider.read();
        Assert.assertNotNull(schemaContainer);
        Assert.assertEquals("tbl", schemaContainer.getTable());
        TupleMetadata metadata = schemaContainer.getSchema();
        Assert.assertNotNull(metadata);
        Assert.assertEquals(2, metadata.size());
        Assert.assertEquals(INT, metadata.metadata("i").type());
        Assert.assertEquals(VARCHAR, metadata.metadata("v").type());
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("k1", "v1");
        properties.put("k2", "v2");
        Assert.assertEquals(properties, schemaContainer.getProperties());
        Assert.assertTrue(schemaContainer.getVersion().isUndefined());
    }

    @Test
    public void testPathProviderReadAbsentFile() throws Exception {
        Path schema = new Path(new File(folder.getRoot(), "absent_file").getPath());
        SchemaProvider provider = new PathSchemaProvider(schema);
        Assert.assertFalse(provider.exists());
        thrown.expect(FileNotFoundException.class);
        provider.read();
    }

    @Test
    public void testPathProviderReadSchemaWithComments() throws Exception {
        java.nio.file.Path schemaPath = folder.newFile("schema").toPath();
        Files.write(schemaPath, Collections.singletonList(("// my schema file start\n" + (((((("{  \n" + "   \"schema\":[  // start columns list\n") + "      \"`i` INT\"\n") + "   ]\n") + "}\n") + "// schema file end\n") + "/* multiline comment */"))));
        SchemaProvider provider = new PathSchemaProvider(new Path(schemaPath.toUri().getPath()));
        Assert.assertTrue(provider.exists());
        Assert.assertNotNull(provider.read());
    }
}

