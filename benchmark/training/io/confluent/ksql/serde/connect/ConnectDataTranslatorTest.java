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
package io.confluent.ksql.serde.connect;


import Schema.STRING_SCHEMA;
import Schema.Type.INT32;
import Schema.Type.STRING;
import SchemaBuilder.OPTIONAL_INT32_SCHEMA;
import SchemaBuilder.OPTIONAL_INT64_SCHEMA;
import SchemaBuilder.OPTIONAL_STRING_SCHEMA;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class ConnectDataTranslatorTest {
    @Test
    public void shouldTranslateStructCorrectly() {
        final Schema structSchema = SchemaBuilder.struct().field("INT", OPTIONAL_INT32_SCHEMA).field("BIGINT", OPTIONAL_INT64_SCHEMA).optional().build();
        final Schema rowSchema = SchemaBuilder.struct().field("STRUCT", structSchema).optional().build();
        final Struct connectStruct = new Struct(rowSchema);
        final Struct structColumn = new Struct(structSchema);
        structColumn.put("INT", 123);
        structColumn.put("BIGINT", 456L);
        connectStruct.put("STRUCT", structColumn);
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        final GenericRow row = connectToKsqlTranslator.toKsqlRow(rowSchema, connectStruct);
        MatcherAssert.assertThat(row.getColumns().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(row.getColumnValue(0), CoreMatchers.instanceOf(Struct.class));
        final Struct connectStructColumn = row.getColumnValue(0);
        MatcherAssert.assertThat(connectStructColumn.schema(), CoreMatchers.equalTo(structSchema));
        MatcherAssert.assertThat(connectStructColumn.get("INT"), CoreMatchers.equalTo(123));
        MatcherAssert.assertThat(connectStructColumn.get("BIGINT"), CoreMatchers.equalTo(456L));
    }

    @Test
    public void shouldTranslateArrayOfStructs() {
        final Schema innerSchema = SchemaBuilder.struct().field("FIELD", Schema.OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema rowSchema = SchemaBuilder.struct().field("ARRAY", SchemaBuilder.array(innerSchema).optional().build()).build();
        final Struct connectStruct = new Struct(rowSchema);
        final Struct inner1 = new Struct(innerSchema);
        inner1.put("FIELD", 123);
        final Struct inner2 = new Struct(innerSchema);
        inner2.put("FIELD", 456);
        connectStruct.put("ARRAY", Arrays.asList(inner1, inner2));
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        final GenericRow row = connectToKsqlTranslator.toKsqlRow(rowSchema, connectStruct);
        MatcherAssert.assertThat(row.getColumns().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(row.getColumnValue(0), CoreMatchers.instanceOf(List.class));
        final List<Struct> array = row.getColumnValue(0);
        MatcherAssert.assertThat(array.get(0).get("FIELD"), CoreMatchers.equalTo(123));
        MatcherAssert.assertThat(array.get(1).get("FIELD"), CoreMatchers.equalTo(456));
    }

    @Test
    public void shouldTranslateMapWithStructValues() {
        final Schema innerSchema = SchemaBuilder.struct().field("FIELD", Schema.OPTIONAL_INT32_SCHEMA).build();
        final Schema rowSchema = SchemaBuilder.struct().field("MAP", SchemaBuilder.map(STRING_SCHEMA, innerSchema).optional().build()).build();
        final Struct connectStruct = new Struct(rowSchema);
        final Struct inner1 = new Struct(innerSchema);
        inner1.put("FIELD", 123);
        final Struct inner2 = new Struct(innerSchema);
        inner2.put("FIELD", 456);
        connectStruct.put("MAP", ImmutableMap.of("k1", inner1, "k2", inner2));
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        final GenericRow row = connectToKsqlTranslator.toKsqlRow(rowSchema, connectStruct);
        MatcherAssert.assertThat(row.getColumns().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(row.getColumnValue(0), CoreMatchers.instanceOf(Map.class));
        final Map<String, Struct> map = row.getColumnValue(0);
        MatcherAssert.assertThat(map.get("k1").get("FIELD"), CoreMatchers.equalTo(123));
        MatcherAssert.assertThat(map.get("k2").get("FIELD"), CoreMatchers.equalTo(456));
    }

    @Test
    public void shouldThrowOnTypeMismatch() {
        final Schema schema = SchemaBuilder.struct().field("FIELD", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema badSchema = SchemaBuilder.struct().field("FIELD", OPTIONAL_STRING_SCHEMA).optional().build();
        final Struct badData = new Struct(badSchema);
        badData.put("FIELD", "fubar");
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(schema);
        try {
            connectToKsqlTranslator.toKsqlRow(badSchema, badData);
            Assert.fail("Translation failed to detect bad connect type");
        } catch (final DataException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(STRING.getName()));
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(INT32.getName()));
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("FIELD"));
        }
    }

    @Test
    public void shouldTranslateStructFieldWithDifferentCase() {
        final Schema structSchema = SchemaBuilder.struct().field("INT", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema rowSchema = SchemaBuilder.struct().field("STRUCT", structSchema).build();
        final Schema dataStructSchema = SchemaBuilder.struct().field("iNt", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema dataRowSchema = SchemaBuilder.struct().field("STRUCT", dataStructSchema).optional().build();
        final Struct connectStruct = new Struct(dataRowSchema);
        final Struct structColumn = new Struct(dataStructSchema);
        structColumn.put("iNt", 123);
        connectStruct.put("STRUCT", structColumn);
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        final GenericRow row = connectToKsqlTranslator.toKsqlRow(dataRowSchema, connectStruct);
        MatcherAssert.assertThat(row.getColumns().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(row.getColumnValue(0), CoreMatchers.instanceOf(Struct.class));
        final Struct connectStructColumn = row.getColumnValue(0);
        MatcherAssert.assertThat(connectStructColumn.schema(), CoreMatchers.equalTo(structSchema));
        MatcherAssert.assertThat(connectStructColumn.get("INT"), CoreMatchers.equalTo(123));
    }

    @Test
    public void shouldThrowIfNestedFieldTypeDoesntMatch() {
        final Schema structSchema = SchemaBuilder.struct().field("INT", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema rowSchema = SchemaBuilder.struct().field("STRUCT", structSchema).optional().build();
        final Schema dataStructSchema = SchemaBuilder.struct().field("INT", OPTIONAL_STRING_SCHEMA).optional().build();
        final Schema dataRowSchema = SchemaBuilder.struct().field("STRUCT", dataStructSchema).optional().build();
        final Struct connectStruct = new Struct(dataRowSchema);
        final Struct structColumn = new Struct(dataStructSchema);
        structColumn.put("INT", "123");
        connectStruct.put("STRUCT", structColumn);
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        try {
            connectToKsqlTranslator.toKsqlRow(dataRowSchema, connectStruct);
            Assert.fail("Translation failed to check nested field");
        } catch (final DataException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(INT32.getName()));
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(STRING.getName()));
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("STRUCT->INT"));
        }
    }

    @Test
    public void shouldTranslateNullValueCorrectly() {
        final Schema rowSchema = SchemaBuilder.struct().field("INT", OPTIONAL_INT32_SCHEMA).optional().build();
        final Struct connectStruct = new Struct(rowSchema);
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        final GenericRow row = connectToKsqlTranslator.toKsqlRow(rowSchema, connectStruct);
        MatcherAssert.assertThat(row.getColumns().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(row.getColumnValue(0), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldTranslateMissingStructFieldToNull() {
        final Schema structSchema = SchemaBuilder.struct().field("INT", OPTIONAL_INT32_SCHEMA).optional().build();
        final Schema rowSchema = SchemaBuilder.struct().field("STRUCT", structSchema).optional().build();
        final Schema dataRowSchema = SchemaBuilder.struct().field("OTHER", OPTIONAL_INT32_SCHEMA).optional().build();
        final Struct connectStruct = new Struct(dataRowSchema);
        connectStruct.put("OTHER", 123);
        final ConnectDataTranslator connectToKsqlTranslator = new ConnectDataTranslator(rowSchema);
        final GenericRow row = connectToKsqlTranslator.toKsqlRow(dataRowSchema, connectStruct);
        MatcherAssert.assertThat(row.getColumns().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(row.getColumnValue(0), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

