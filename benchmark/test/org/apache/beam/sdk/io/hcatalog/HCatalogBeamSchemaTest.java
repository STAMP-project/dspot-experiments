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
package org.apache.beam.sdk.io.hcatalog;


import Schema.FieldType.INT32;
import Schema.FieldType.STRING;
import java.io.Serializable;
import org.apache.beam.sdk.io.hcatalog.test.EmbeddedMetastoreService;
import org.apache.beam.sdk.schemas.Schema;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link HCatalogBeamSchema}.
 */
public class HCatalogBeamSchemaTest implements Serializable {
    @ClassRule
    public static final TemporaryFolder TMP_FOLDER = new TemporaryFolder();

    private static EmbeddedMetastoreService service;

    @Test
    public void testHasDB() throws Exception {
        HCatalogBeamSchema hcatSchema = HCatalogBeamSchema.create(HCatalogBeamSchemaTest.service.getHiveConfAsMap());
        Assert.assertTrue(hcatSchema.hasDatabase(TEST_DATABASE));
    }

    @Test
    public void testDoesntHaveDB() throws Exception {
        HCatalogBeamSchema hcatSchema = HCatalogBeamSchema.create(HCatalogBeamSchemaTest.service.getHiveConfAsMap());
        Assert.assertFalse(hcatSchema.hasDatabase("non-existent-db"));
    }

    @Test
    public void testGetTableSchema() throws Exception {
        HCatalogBeamSchema hcatSchema = HCatalogBeamSchema.create(HCatalogBeamSchemaTest.service.getHiveConfAsMap());
        Schema schema = hcatSchema.getTableSchema(TEST_DATABASE, TEST_TABLE).get();
        Schema expectedSchema = Schema.builder().addNullableField("mycol1", STRING).addNullableField("mycol2", INT32).build();
        Assert.assertEquals(expectedSchema, schema);
    }

    @Test
    public void testDoesntHaveTable() throws Exception {
        HCatalogBeamSchema hcatSchema = HCatalogBeamSchema.create(HCatalogBeamSchemaTest.service.getHiveConfAsMap());
        Assert.assertFalse(hcatSchema.getTableSchema(TEST_DATABASE, "non-existent-table").isPresent());
    }
}

