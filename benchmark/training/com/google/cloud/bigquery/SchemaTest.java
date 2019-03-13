/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import Field.Mode.NULLABLE;
import Field.Mode.REPEATED;
import Field.Mode.REQUIRED;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SchemaTest {
    private static final Field FIELD_SCHEMA1 = Field.newBuilder("StringField", STRING).setMode(NULLABLE).setDescription("FieldDescription1").build();

    private static final Field FIELD_SCHEMA2 = Field.newBuilder("IntegerField", INTEGER).setMode(REPEATED).setDescription("FieldDescription2").build();

    private static final Field FIELD_SCHEMA3 = Field.newBuilder("RecordField", RECORD, SchemaTest.FIELD_SCHEMA1, SchemaTest.FIELD_SCHEMA2).setMode(REQUIRED).setDescription("FieldDescription3").build();

    private static final List<Field> FIELDS = ImmutableList.of(SchemaTest.FIELD_SCHEMA1, SchemaTest.FIELD_SCHEMA2, SchemaTest.FIELD_SCHEMA3);

    private static final Schema TABLE_SCHEMA = Schema.of(SchemaTest.FIELDS);

    @Test
    public void testOf() {
        compareTableSchema(SchemaTest.TABLE_SCHEMA, Schema.of(SchemaTest.FIELDS));
    }

    @Test
    public void testToAndFromPb() {
        compareTableSchema(SchemaTest.TABLE_SCHEMA, Schema.fromPb(SchemaTest.TABLE_SCHEMA.toPb()));
    }

    @Test
    public void testEmptySchema() {
        TableSchema tableSchema = new TableSchema();
        Schema schema = Schema.fromPb(tableSchema);
        Assert.assertEquals(0, schema.getFields().size());
    }
}

