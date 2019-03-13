/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.serde.avro;


import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.Type.ARRAY;
import Schema.Type.MAP;
import Schema.Type.STRUCT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.util.KsqlConstants;
import java.util.Collections;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AvroDataTranslatorTest {
    @Test
    public void shoudRenameSourceDereference() {
        final Schema schema = SchemaBuilder.struct().field("STREAM_NAME.COLUMN_NAME", OPTIONAL_INT32_SCHEMA).optional().build();
        final AvroDataTranslator dataTranslator = new AvroDataTranslator(schema, KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME, true);
        final GenericRow ksqlRow = new GenericRow(ImmutableList.of(123));
        final Struct struct = dataTranslator.toConnectRow(ksqlRow);
        Assert.assertThat(struct.schema(), CoreMatchers.equalTo(SchemaBuilder.struct().name(struct.schema().name()).field("STREAM_NAME_COLUMN_NAME", OPTIONAL_INT32_SCHEMA).optional().build()));
        Assert.assertThat(struct.get("STREAM_NAME_COLUMN_NAME"), CoreMatchers.equalTo(123));
        final GenericRow translatedRow = dataTranslator.toKsqlRow(struct.schema(), struct);
        Assert.assertThat(translatedRow, CoreMatchers.equalTo(ksqlRow));
    }

    @Test
    public void shouldAddNamesToSchema() {
        final Schema arrayInner = SchemaBuilder.struct().field("ARRAY_INNER", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema mapInner = SchemaBuilder.struct().field("MAP_INNER", OPTIONAL_INT64_SCHEMA).optional().build();
        final Schema structInner = SchemaBuilder.struct().field("STRUCT_INNER", OPTIONAL_STRING_SCHEMA).optional().build();
        final Schema schema = SchemaBuilder.struct().field("ARRAY", SchemaBuilder.array(arrayInner).optional().build()).field("MAP", SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, mapInner).optional().build()).field("STRUCT", structInner).optional().build();
        final Struct arrayInnerStruct = new Struct(arrayInner).put("ARRAY_INNER", 123);
        final Struct mapInnerStruct = new Struct(mapInner).put("MAP_INNER", 456L);
        final Struct structInnerStruct = put("STRUCT_INNER", "foo");
        final AvroDataTranslator dataTranslator = new AvroDataTranslator(schema, KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME, true);
        final GenericRow ksqlRow = new GenericRow(ImmutableList.of(arrayInnerStruct), ImmutableMap.of("bar", mapInnerStruct), structInnerStruct);
        final Struct struct = dataTranslator.toConnectRow(ksqlRow);
        final Schema namedSchema = struct.schema();
        Assert.assertThat(namedSchema.type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(namedSchema.name(), CoreMatchers.notNullValue());
        final String baseName = namedSchema.name();
        Assert.assertThat(namedSchema.field("ARRAY").schema().type(), CoreMatchers.equalTo(ARRAY));
        Assert.assertThat(namedSchema.field("MAP").schema().type(), CoreMatchers.equalTo(MAP));
        Assert.assertThat(namedSchema.field("STRUCT").schema().type(), CoreMatchers.equalTo(STRUCT));
        final Schema namedArrayInner = namedSchema.field("ARRAY").schema().valueSchema();
        Assert.assertThat(namedArrayInner.type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(namedArrayInner.fields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(namedArrayInner.field("ARRAY_INNER").schema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
        Assert.assertThat(namedArrayInner.name(), CoreMatchers.equalTo((baseName + "_ARRAY")));
        final Schema namedMapInner = namedSchema.field("MAP").schema().valueSchema();
        Assert.assertThat(namedMapInner.type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(namedMapInner.fields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(namedMapInner.field("MAP_INNER").schema(), CoreMatchers.equalTo(OPTIONAL_INT64_SCHEMA));
        Assert.assertThat(namedMapInner.name(), CoreMatchers.equalTo((baseName + "_MAP_MapValue")));
        final Schema namedStructInner = namedSchema.field("STRUCT").schema();
        Assert.assertThat(namedStructInner.type(), CoreMatchers.equalTo(STRUCT));
        Assert.assertThat(namedStructInner.fields().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(namedStructInner.field("STRUCT_INNER").schema(), CoreMatchers.equalTo(OPTIONAL_STRING_SCHEMA));
        Assert.assertThat(namedStructInner.name(), CoreMatchers.equalTo((baseName + "_STRUCT")));
        Assert.assertThat(struct.schema(), CoreMatchers.equalTo(namedSchema));
        Assert.assertThat(getInt32("ARRAY_INNER"), CoreMatchers.equalTo(123));
        Assert.assertThat(getInt64("MAP_INNER"), CoreMatchers.equalTo(456L));
        Assert.assertThat(struct.getStruct("STRUCT").getString("STRUCT_INNER"), CoreMatchers.equalTo("foo"));
        final GenericRow translatedRow = dataTranslator.toKsqlRow(struct.schema(), struct);
        Assert.assertThat(translatedRow, CoreMatchers.equalTo(ksqlRow));
    }

    @Test
    public void shouldReplaceNullWithNull() {
        final Schema schema = SchemaBuilder.struct().field("COLUMN_NAME", SchemaBuilder.array(OPTIONAL_INT64_SCHEMA).optional().build()).optional().build();
        final AvroDataTranslator dataTranslator = new AvroDataTranslator(schema, KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME, true);
        final GenericRow ksqlRow = new GenericRow(Collections.singletonList(null));
        final Struct struct = dataTranslator.toConnectRow(ksqlRow);
        Assert.assertThat(struct.get("COLUMN_NAME"), CoreMatchers.nullValue());
        final GenericRow translatedRow = dataTranslator.toKsqlRow(struct.schema(), struct);
        Assert.assertThat(translatedRow, CoreMatchers.equalTo(ksqlRow));
    }

    @Test
    public void shoudlReplacePrimitivesCorrectly() {
        final Schema schema = SchemaBuilder.struct().field("COLUMN_NAME", OPTIONAL_INT64_SCHEMA).optional().build();
        final AvroDataTranslator dataTranslator = new AvroDataTranslator(schema, KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME, true);
        final GenericRow ksqlRow = new GenericRow(Collections.singletonList(123L));
        final Struct struct = dataTranslator.toConnectRow(ksqlRow);
        Assert.assertThat(struct.get("COLUMN_NAME"), CoreMatchers.equalTo(123L));
        final GenericRow translatedRow = dataTranslator.toKsqlRow(struct.schema(), struct);
        Assert.assertThat(translatedRow, CoreMatchers.equalTo(ksqlRow));
    }

    @Test
    public void shouldUseExplicitSchemaName() {
        final Schema schema = SchemaBuilder.struct().field("COLUMN_NAME", OPTIONAL_INT64_SCHEMA).optional().build();
        String schemaFullName = "com.custom.schema";
        final AvroDataTranslator dataTranslator = new AvroDataTranslator(schema, schemaFullName, true);
        final GenericRow ksqlRow = new GenericRow(Collections.singletonList(123L));
        final Struct struct = dataTranslator.toConnectRow(ksqlRow);
        Assert.assertThat(struct.schema().name(), CoreMatchers.equalTo(schemaFullName));
    }
}

