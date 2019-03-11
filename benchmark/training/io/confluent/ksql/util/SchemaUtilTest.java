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
package io.confluent.ksql.util;


import Schema.BOOLEAN_SCHEMA;
import Schema.BYTES_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.Type.ARRAY;
import Schema.Type.BOOLEAN;
import Schema.Type.BYTES;
import Schema.Type.FLOAT32;
import Schema.Type.FLOAT64;
import Schema.Type.INT32;
import Schema.Type.INT64;
import Schema.Type.MAP;
import Schema.Type.STRING;
import SchemaUtil.ROWKEY_NAME;
import SchemaUtil.ROWTIME_NAME;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type.NULL;
import org.apache.avro.Schema.Type.UNION;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SchemaUtilTest {
    private Schema schema;

    @Test
    public void shouldGetCorrectJavaClassForBoolean() {
        final Class booleanClazz = SchemaUtil.getJavaType(OPTIONAL_BOOLEAN_SCHEMA);
        MatcherAssert.assertThat(booleanClazz, CoreMatchers.equalTo(Boolean.class));
    }

    @Test
    public void shouldGetCorrectJavaClassForInt() {
        final Class intClazz = SchemaUtil.getJavaType(OPTIONAL_INT32_SCHEMA);
        MatcherAssert.assertThat(intClazz, CoreMatchers.equalTo(Integer.class));
    }

    @Test
    public void shouldGetCorrectJavaClassForBigInt() {
        final Class longClazz = SchemaUtil.getJavaType(OPTIONAL_INT64_SCHEMA);
        MatcherAssert.assertThat(longClazz, CoreMatchers.equalTo(Long.class));
    }

    @Test
    public void shouldGetCorrectJavaClassForDouble() {
        final Class doubleClazz = SchemaUtil.getJavaType(OPTIONAL_FLOAT64_SCHEMA);
        MatcherAssert.assertThat(doubleClazz, CoreMatchers.equalTo(Double.class));
    }

    @Test
    public void shouldGetCorrectJavaClassForString() {
        final Class StringClazz = SchemaUtil.getJavaType(OPTIONAL_STRING_SCHEMA);
        MatcherAssert.assertThat(StringClazz, CoreMatchers.equalTo(String.class));
    }

    @Test
    public void shouldGetCorrectJavaClassForArray() {
        final Class arrayClazz = SchemaUtil.getJavaType(SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build());
        MatcherAssert.assertThat(arrayClazz, CoreMatchers.equalTo(List.class));
    }

    @Test
    public void shouldGetCorrectJavaClassForMap() {
        final Class mapClazz = SchemaUtil.getJavaType(SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build());
        MatcherAssert.assertThat(mapClazz, CoreMatchers.equalTo(Map.class));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForBoolean() {
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(OPTIONAL_BOOLEAN_SCHEMA), CoreMatchers.equalTo("BOOLEAN"));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForInt() {
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(OPTIONAL_INT32_SCHEMA), CoreMatchers.equalTo("INT"));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForBigint() {
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(OPTIONAL_INT64_SCHEMA), CoreMatchers.equalTo("BIGINT"));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForDouble() {
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(OPTIONAL_FLOAT64_SCHEMA), CoreMatchers.equalTo("DOUBLE"));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForArray() {
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build()), CoreMatchers.equalTo("ARRAY<DOUBLE>"));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForMap() {
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build()), CoreMatchers.equalTo("MAP<VARCHAR,DOUBLE>"));
    }

    @Test
    public void shouldGetCorrectSqlTypeNameForStruct() {
        final Schema structSchema = SchemaBuilder.struct().field("COL1", OPTIONAL_STRING_SCHEMA).field("COL2", OPTIONAL_INT32_SCHEMA).field("COL3", OPTIONAL_FLOAT64_SCHEMA).field("COL4", SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build()).field("COL5", SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build()).build();
        MatcherAssert.assertThat(SchemaUtil.getSqlTypeName(structSchema), CoreMatchers.equalTo("STRUCT<COL1 VARCHAR, COL2 INT, COL3 DOUBLE, COL4 ARRAY<DOUBLE>, COL5 MAP<VARCHAR,DOUBLE>>"));
    }

    @Test
    public void shouldCreateCorrectAvroSchemaWithNullableFields() {
        final SchemaBuilder schemaBuilder = SchemaBuilder.struct();
        schemaBuilder.field("ordertime", OPTIONAL_INT64_SCHEMA).field("orderid", OPTIONAL_STRING_SCHEMA).field("itemid", OPTIONAL_STRING_SCHEMA).field("orderunits", OPTIONAL_FLOAT64_SCHEMA).field("arraycol", SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build()).field("mapcol", SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA)).optional().build();
        final String avroSchemaString = SchemaUtil.buildAvroSchema(schemaBuilder.build(), "orders").toString();
        MatcherAssert.assertThat(avroSchemaString, CoreMatchers.equalTo(("{\"type\":\"record\",\"name\":\"orders\",\"namespace\":\"ksql\",\"fields\":" + (((((("[{\"name\":\"ordertime\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":" + "\"orderid\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"itemid\",") + "\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"orderunits\",\"type\":") + "[\"null\",\"double\"],\"default\":null},{\"name\":\"arraycol\",\"type\":[\"null\",") + "{\"type\":\"array\",\"items\":[\"null\",\"double\"]}],\"default\":null},{\"name\":") + "\"mapcol\",\"type\":[\"null\",{\"type\":\"map\",\"values\":[\"null\",\"double\"]}]") + ",\"default\":null}]}"))));
    }

    @Test
    public void shouldSupportAvroStructs() {
        // When:
        final org.apache.avro.Schema avroSchema = SchemaUtil.buildAvroSchema(schema, "bob");
        // Then:
        final Field rawStruct = avroSchema.getField("RAW_STRUCT");
        MatcherAssert.assertThat(rawStruct, CoreMatchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(rawStruct.schema().getType(), CoreMatchers.is(UNION));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(0).getType(), CoreMatchers.is(NULL));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(1).toString(), CoreMatchers.is(("{" + (((((("\"type\":\"record\"," + "\"name\":\"RAW_STRUCT\",") + "\"namespace\":\"ksql.bob\",") + "\"fields\":[") + "{\"name\":\"f0\",\"type\":[\"null\",\"long\"],\"default\":null},") + "{\"name\":\"f1\",\"type\":[\"null\",\"boolean\"],\"default\":null}") + "]}"))));
    }

    @Test
    public void shouldSupportAvroArrayOfStructs() {
        // When:
        final org.apache.avro.Schema avroSchema = SchemaUtil.buildAvroSchema(schema, "bob");
        // Then:
        final Field rawStruct = avroSchema.getField("ARRAY_OF_STRUCTS");
        MatcherAssert.assertThat(rawStruct, CoreMatchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(rawStruct.schema().getType(), CoreMatchers.is(UNION));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(0).getType(), CoreMatchers.is(NULL));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(1).toString(), CoreMatchers.is(("{" + ((((((((("\"type\":\"array\"," + "\"items\":[") + "\"null\",") + "{\"type\":\"record\",") + "\"name\":\"ARRAY_OF_STRUCTS\",") + "\"namespace\":\"ksql.bob\",") + "\"fields\":[") + "{\"name\":\"f0\",\"type\":[\"null\",\"long\"],\"default\":null},") + "{\"name\":\"f1\",\"type\":[\"null\",\"boolean\"],\"default\":null}") + "]}]}"))));
    }

    @Test
    public void shouldSupportAvroMapOfStructs() {
        // When:
        final org.apache.avro.Schema avroSchema = SchemaUtil.buildAvroSchema(schema, "bob");
        // Then:
        final Field rawStruct = avroSchema.getField("MAP_OF_STRUCTS");
        MatcherAssert.assertThat(rawStruct, CoreMatchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(rawStruct.schema().getType(), CoreMatchers.is(UNION));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(0).getType(), CoreMatchers.is(NULL));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(1).toString(), CoreMatchers.is(("{" + ((((((((("\"type\":\"map\"," + "\"values\":[") + "\"null\",") + "{\"type\":\"record\",") + "\"name\":\"MAP_OF_STRUCTS\",") + "\"namespace\":\"ksql.bob\",") + "\"fields\":[") + "{\"name\":\"f0\",\"type\":[\"null\",\"long\"],\"default\":null},") + "{\"name\":\"f1\",\"type\":[\"null\",\"boolean\"],\"default\":null}") + "]}]}"))));
    }

    @Test
    public void shouldSupportAvroNestedStructs() {
        // When:
        final org.apache.avro.Schema avroSchema = SchemaUtil.buildAvroSchema(schema, "bob");
        // Then:
        final Field rawStruct = avroSchema.getField("NESTED_STRUCTS");
        MatcherAssert.assertThat(rawStruct, CoreMatchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(rawStruct.schema().getType(), CoreMatchers.is(UNION));
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(0).getType(), CoreMatchers.is(NULL));
        final String s0Schema = "{" + (((((("\"type\":\"record\"," + "\"name\":\"s0\",") + "\"namespace\":\"ksql.bob.NESTED_STRUCTS\",") + "\"fields\":[") + "{\"name\":\"f0\",\"type\":[\"null\",\"long\"],\"default\":null},") + "{\"name\":\"f1\",\"type\":[\"null\",\"boolean\"],\"default\":null}") + "]}");
        final String ss0Schema = "{" + (((((("\"type\":\"record\"," + "\"name\":\"ss0\",") + "\"namespace\":\"ksql.bob.NESTED_STRUCTS.s1\",") + "\"fields\":[") + "{\"name\":\"f0\",\"type\":[\"null\",\"long\"],\"default\":null},") + "{\"name\":\"f1\",\"type\":[\"null\",\"boolean\"],\"default\":null}") + "]}");
        final String s1Schema = ((("{" + (((("\"type\":\"record\"," + "\"name\":\"s1\",") + "\"namespace\":\"ksql.bob.NESTED_STRUCTS\",") + "\"fields\":[") + "{\"name\":\"ss0\",\"type\":[\"null\",")) + ss0Schema) + "],\"default\":null}") + "]}";
        MatcherAssert.assertThat(rawStruct.schema().getTypes().get(1).toString(), CoreMatchers.is(((((((("{" + (((("\"type\":\"record\"," + "\"name\":\"NESTED_STRUCTS\",") + "\"namespace\":\"ksql.bob\",") + "\"fields\":[") + "{\"name\":\"s0\",\"type\":[\"null\",")) + s0Schema) + "],\"default\":null},") + "{\"name\":\"s1\",\"type\":[\"null\",") + s1Schema) + "],\"default\":null}") + "]}")));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForBoolean() {
        final Schema schema = Schema.OPTIONAL_BOOLEAN_SCHEMA;
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(Boolean.class));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForInt() {
        final Schema schema = Schema.OPTIONAL_INT32_SCHEMA;
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(Integer.class));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForLong() {
        final Schema schema = Schema.OPTIONAL_INT64_SCHEMA;
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(Long.class));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForDouble() {
        final Schema schema = Schema.OPTIONAL_FLOAT64_SCHEMA;
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(Double.class));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForString() {
        final Schema schema = Schema.OPTIONAL_STRING_SCHEMA;
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(String.class));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForArray() {
        final Schema schema = SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build();
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(List.class));
    }

    @Test
    public void shouldGetTheCorrectJavaTypeForMap() {
        final Schema schema = SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build();
        final Class javaClass = SchemaUtil.getJavaType(schema);
        MatcherAssert.assertThat(javaClass, CoreMatchers.equalTo(Map.class));
    }

    @Test
    public void shouldFailForCorrectJavaType() {
        try {
            SchemaUtil.getJavaType(BYTES_SCHEMA);
            Assert.fail();
        } catch (final KsqlException ksqlException) {
            MatcherAssert.assertThat("Invalid type retured.", ksqlException.getMessage(), CoreMatchers.equalTo(("Type is not " + "supported: BYTES")));
        }
    }

    @Test
    public void shouldMatchName() {
        final org.apache.kafka.connect.data.Field field = new org.apache.kafka.connect.data.Field("foo", 0, Schema.INT32_SCHEMA);
        MatcherAssert.assertThat(SchemaUtil.matchFieldName(field, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void shouldNotMatchDifferentName() {
        final org.apache.kafka.connect.data.Field field = new org.apache.kafka.connect.data.Field("foo", 0, Schema.INT32_SCHEMA);
        MatcherAssert.assertThat(SchemaUtil.matchFieldName(field, "bar"), CoreMatchers.is(false));
    }

    @Test
    public void shouldMatchNameWithAlias() {
        final org.apache.kafka.connect.data.Field field = new org.apache.kafka.connect.data.Field("foo", 0, Schema.INT32_SCHEMA);
        MatcherAssert.assertThat(SchemaUtil.matchFieldName(field, "bar.foo"), CoreMatchers.is(true));
    }

    @Test
    public void shouldGetTheCorrectFieldName() {
        final Optional<org.apache.kafka.connect.data.Field> field = SchemaUtil.getFieldByName(schema, "orderid".toUpperCase());
        Assert.assertTrue(field.isPresent());
        MatcherAssert.assertThat(field.get().schema(), CoreMatchers.sameInstance(OPTIONAL_INT64_SCHEMA));
        MatcherAssert.assertThat("", field.get().name().toLowerCase(), CoreMatchers.equalTo("orderid"));
        final Optional<org.apache.kafka.connect.data.Field> field1 = SchemaUtil.getFieldByName(schema, "orderid");
        Assert.assertFalse(field1.isPresent());
    }

    @Test
    public void shouldGetTheCorrectFieldIndex() {
        final int index1 = SchemaUtil.getFieldIndexByName(schema, "orderid".toUpperCase());
        final int index2 = SchemaUtil.getFieldIndexByName(schema, "itemid".toUpperCase());
        final int index3 = SchemaUtil.getFieldIndexByName(schema, "mapcol".toUpperCase());
        MatcherAssert.assertThat("Incorrect index.", index1, CoreMatchers.equalTo(1));
        MatcherAssert.assertThat("Incorrect index.", index2, CoreMatchers.equalTo(2));
        MatcherAssert.assertThat("Incorrect index.", index3, CoreMatchers.equalTo(5));
    }

    @Test
    public void shouldHandleInvalidFieldIndexCorrectly() {
        final int index = SchemaUtil.getFieldIndexByName(schema, "mapcol1".toUpperCase());
        MatcherAssert.assertThat("Incorrect index.", index, CoreMatchers.equalTo((-1)));
    }

    @Test
    public void shouldBuildTheCorrectSchemaWithAndWithoutAlias() {
        final String alias = "Hello";
        final Schema schemaWithAlias = SchemaUtil.buildSchemaWithAlias(schema, alias);
        MatcherAssert.assertThat("Incorrect schema field count.", ((schemaWithAlias.fields().size()) == (schema.fields().size())));
        for (int i = 0; i < (schemaWithAlias.fields().size()); i++) {
            final org.apache.kafka.connect.data.Field fieldWithAlias = schemaWithAlias.fields().get(i);
            final org.apache.kafka.connect.data.Field field = schema.fields().get(i);
            MatcherAssert.assertThat(fieldWithAlias.name(), CoreMatchers.equalTo(((alias + ".") + (field.name()))));
        }
        final Schema schemaWithoutAlias = SchemaUtil.getSchemaWithNoAlias(schemaWithAlias);
        MatcherAssert.assertThat("Incorrect schema field count.", ((schemaWithAlias.fields().size()) == (schema.fields().size())));
        for (int i = 0; i < (schemaWithoutAlias.fields().size()); i++) {
            final org.apache.kafka.connect.data.Field fieldWithAlias = schemaWithoutAlias.fields().get(i);
            final org.apache.kafka.connect.data.Field field = schema.fields().get(i);
            MatcherAssert.assertThat("Incorrect field name.", fieldWithAlias.name().equals(field.name()));
        }
    }

    @Test
    public void shouldGetTheCorrectJavaCastClass() {
        MatcherAssert.assertThat("Incorrect class.", SchemaUtil.getJavaCastString(OPTIONAL_BOOLEAN_SCHEMA), CoreMatchers.equalTo("(Boolean)"));
        MatcherAssert.assertThat("Incorrect class.", SchemaUtil.getJavaCastString(OPTIONAL_INT32_SCHEMA), CoreMatchers.equalTo("(Integer)"));
        MatcherAssert.assertThat("Incorrect class.", SchemaUtil.getJavaCastString(OPTIONAL_INT64_SCHEMA), CoreMatchers.equalTo("(Long)"));
        MatcherAssert.assertThat("Incorrect class.", SchemaUtil.getJavaCastString(OPTIONAL_FLOAT64_SCHEMA), CoreMatchers.equalTo("(Double)"));
        MatcherAssert.assertThat("Incorrect class.", SchemaUtil.getJavaCastString(OPTIONAL_STRING_SCHEMA), CoreMatchers.equalTo("(String)"));
    }

    @Test
    public void shouldAddAndRemoveImplicitColumns() {
        // Given:
        final int initialFieldCount = schema.fields().size();
        // When:
        final Schema withImplicit = SchemaUtil.addImplicitRowTimeRowKeyToSchema(schema);
        // Then:
        MatcherAssert.assertThat("Invalid field count.", withImplicit.fields(), Matchers.hasSize((initialFieldCount + 2)));
        MatcherAssert.assertThat("Field name should be ROWTIME.", withImplicit.fields().get(0).name(), CoreMatchers.equalTo(ROWTIME_NAME));
        MatcherAssert.assertThat("Field name should ne ROWKEY.", withImplicit.fields().get(1).name(), CoreMatchers.equalTo(ROWKEY_NAME));
        // When:
        final Schema withoutImplicit = SchemaUtil.removeImplicitRowTimeRowKeyFromSchema(withImplicit);
        // Then:
        MatcherAssert.assertThat("Invalid field count.", withoutImplicit.fields(), Matchers.hasSize(initialFieldCount));
        MatcherAssert.assertThat("Invalid field name.", withoutImplicit.fields().get(0).name(), CoreMatchers.equalTo("ORDERTIME"));
        MatcherAssert.assertThat("Invalid field name.", withoutImplicit.fields().get(1).name(), CoreMatchers.equalTo("ORDERID"));
    }

    @Test
    public void shouldGetTheSchemaDefString() {
        final String schemaDef = SchemaUtil.getSchemaDefinitionString(schema);
        MatcherAssert.assertThat("Invalid schema def.", schemaDef, CoreMatchers.equalTo(("[ORDERTIME : BIGINT, " + (((((((("ORDERID : BIGINT, " + "ITEMID : VARCHAR, ") + "ORDERUNITS : DOUBLE, ") + "ARRAYCOL : ARRAY<DOUBLE>, ") + "MAPCOL : MAP<VARCHAR,DOUBLE>, ") + "RAW_STRUCT : STRUCT<f0 BIGINT, f1 BOOLEAN>, ") + "ARRAY_OF_STRUCTS : ARRAY<STRUCT<f0 BIGINT, f1 BOOLEAN>>, ") + "MAP-OF-STRUCTS : MAP<VARCHAR,STRUCT<f0 BIGINT, f1 BOOLEAN>>, ") + "NESTED.STRUCTS : STRUCT<s0 STRUCT<f0 BIGINT, f1 BOOLEAN>, s1 STRUCT<ss0 STRUCT<f0 BIGINT, f1 BOOLEAN>>>]"))));
    }

    @Test
    public void shouldGetCorrectSqlType() {
        final String sqlType1 = SchemaUtil.getSqlTypeName(OPTIONAL_BOOLEAN_SCHEMA);
        final String sqlType2 = SchemaUtil.getSqlTypeName(OPTIONAL_INT32_SCHEMA);
        final String sqlType3 = SchemaUtil.getSqlTypeName(OPTIONAL_INT64_SCHEMA);
        final String sqlType4 = SchemaUtil.getSqlTypeName(OPTIONAL_FLOAT64_SCHEMA);
        final String sqlType5 = SchemaUtil.getSqlTypeName(OPTIONAL_STRING_SCHEMA);
        final String sqlType6 = SchemaUtil.getSqlTypeName(SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build());
        final String sqlType7 = SchemaUtil.getSqlTypeName(SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build());
        MatcherAssert.assertThat("Invalid SQL type.", sqlType1, CoreMatchers.equalTo("BOOLEAN"));
        MatcherAssert.assertThat("Invalid SQL type.", sqlType2, CoreMatchers.equalTo("INT"));
        MatcherAssert.assertThat("Invalid SQL type.", sqlType3, CoreMatchers.equalTo("BIGINT"));
        MatcherAssert.assertThat("Invalid SQL type.", sqlType4, CoreMatchers.equalTo("DOUBLE"));
        MatcherAssert.assertThat("Invalid SQL type.", sqlType5, CoreMatchers.equalTo("VARCHAR"));
        MatcherAssert.assertThat("Invalid SQL type.", sqlType6, CoreMatchers.equalTo("ARRAY<DOUBLE>"));
        MatcherAssert.assertThat("Invalid SQL type.", sqlType7, CoreMatchers.equalTo("MAP<VARCHAR,DOUBLE>"));
    }

    @Test
    public void shouldGetCorrectSqlTypeFromSchemaType() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaTypeAsSqlType(STRING), CoreMatchers.is("VARCHAR(STRING)"));
        MatcherAssert.assertThat(SchemaUtil.getSchemaTypeAsSqlType(INT64), CoreMatchers.is("BIGINT"));
        MatcherAssert.assertThat(SchemaUtil.getSchemaTypeAsSqlType(INT32), CoreMatchers.is("INTEGER"));
        MatcherAssert.assertThat(SchemaUtil.getSchemaTypeAsSqlType(FLOAT64), CoreMatchers.is("DOUBLE"));
        MatcherAssert.assertThat(SchemaUtil.getSchemaTypeAsSqlType(BOOLEAN), CoreMatchers.is("BOOLEAN"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnUnknownSchemaType() {
        SchemaUtil.getSchemaTypeAsSqlType(BYTES);
    }

    @Test
    public void shouldStripAliasFromFieldName() {
        final Schema schemaWithAlias = SchemaUtil.buildSchemaWithAlias(schema, "alias");
        MatcherAssert.assertThat("Invalid field name", SchemaUtil.getFieldNameWithNoAlias(schemaWithAlias.fields().get(0)), CoreMatchers.equalTo(schema.fields().get(0).name()));
    }

    @Test
    public void shouldReturnFieldNameWithoutAliasAsIs() {
        MatcherAssert.assertThat("Invalid field name", SchemaUtil.getFieldNameWithNoAlias(schema.fields().get(0)), CoreMatchers.equalTo(schema.fields().get(0).name()));
    }

    @Test
    public void shouldResolveIntAndLongSchemaToLong() {
        MatcherAssert.assertThat(SchemaUtil.resolveBinaryOperatorResultType(INT64, INT32).type(), CoreMatchers.equalTo(INT64));
    }

    @Test
    public void shouldResolveIntAndIntSchemaToInt() {
        MatcherAssert.assertThat(SchemaUtil.resolveBinaryOperatorResultType(INT32, INT32).type(), CoreMatchers.equalTo(INT32));
    }

    @Test
    public void shouldResolveFloat64AndAnyNumberTypeToFloat() {
        MatcherAssert.assertThat(SchemaUtil.resolveBinaryOperatorResultType(INT32, FLOAT64).type(), CoreMatchers.equalTo(FLOAT64));
        MatcherAssert.assertThat(SchemaUtil.resolveBinaryOperatorResultType(FLOAT64, INT64).type(), CoreMatchers.equalTo(FLOAT64));
        MatcherAssert.assertThat(SchemaUtil.resolveBinaryOperatorResultType(FLOAT32, FLOAT64).type(), CoreMatchers.equalTo(FLOAT64));
    }

    @Test
    public void shouldResolveStringAndStringToString() {
        MatcherAssert.assertThat(SchemaUtil.resolveBinaryOperatorResultType(STRING, STRING).type(), CoreMatchers.equalTo(STRING));
    }

    @Test(expected = KsqlException.class)
    public void shouldThrowExceptionWhenResolvingStringWithAnythingElse() {
        SchemaUtil.resolveBinaryOperatorResultType(STRING, FLOAT64);
    }

    @Test(expected = KsqlException.class)
    public void shouldThrowExceptionWhenResolvingUnkonwnType() {
        SchemaUtil.resolveBinaryOperatorResultType(BOOLEAN, FLOAT64);
    }

    @Test
    public void shouldGetBooleanSchemaForBooleanClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Boolean.class), CoreMatchers.equalTo(OPTIONAL_BOOLEAN_SCHEMA));
    }

    @Test
    public void shouldGetBooleanSchemaForBooleanPrimitiveClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(boolean.class), CoreMatchers.equalTo(BOOLEAN_SCHEMA));
    }

    @Test
    public void shouldGetIntSchemaForIntegerClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Integer.class), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldGetIntegerSchemaForIntPrimitiveClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(int.class), CoreMatchers.equalTo(INT32_SCHEMA));
    }

    @Test
    public void shouldGetLongSchemaForLongClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Long.class), CoreMatchers.equalTo(OPTIONAL_INT64_SCHEMA));
    }

    @Test
    public void shouldGetLongSchemaForLongPrimitiveClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(long.class), CoreMatchers.equalTo(INT64_SCHEMA));
    }

    @Test
    public void shouldGetFloatSchemaForDoubleClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Double.class), CoreMatchers.equalTo(OPTIONAL_FLOAT64_SCHEMA));
    }

    @Test
    public void shouldGetFloatSchemaForDoublePrimitiveClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(double.class), CoreMatchers.equalTo(FLOAT64_SCHEMA));
    }

    @Test
    public void shouldGetMapSchemaFromMapClass() throws NoSuchMethodException {
        final Type type = getClass().getDeclaredMethod("mapType", Map.class).getGenericParameterTypes()[0];
        final Schema schema = SchemaUtil.getSchemaFromType(type);
        MatcherAssert.assertThat(schema.type(), CoreMatchers.equalTo(MAP));
        MatcherAssert.assertThat(schema.keySchema(), CoreMatchers.equalTo(OPTIONAL_STRING_SCHEMA));
        MatcherAssert.assertThat(schema.valueSchema(), CoreMatchers.equalTo(OPTIONAL_INT32_SCHEMA));
    }

    @Test
    public void shouldGetArraySchemaFromListClass() throws NoSuchMethodException {
        final Type type = getClass().getDeclaredMethod("listType", List.class).getGenericParameterTypes()[0];
        final Schema schema = SchemaUtil.getSchemaFromType(type);
        MatcherAssert.assertThat(schema.type(), CoreMatchers.equalTo(ARRAY));
        MatcherAssert.assertThat(schema.valueSchema(), CoreMatchers.equalTo(OPTIONAL_FLOAT64_SCHEMA));
    }

    @Test
    public void shouldGetStringSchemaFromStringClass() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(String.class), CoreMatchers.equalTo(OPTIONAL_STRING_SCHEMA));
    }

    @Test(expected = KsqlException.class)
    public void shouldThrowExceptionIfClassDoesntMapToSchema() {
        SchemaUtil.getSchemaFromType(System.class);
    }

    @Test
    public void shouldDefaultToNoNameOnGetSchemaFromType() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Double.class).name(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldDefaultToNoDocOnGetSchemaFromType() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Double.class).doc(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldSetNameOnGetSchemaFromType() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Double.class, "name", "").name(), CoreMatchers.is("name"));
    }

    @Test
    public void shouldSetDocOnGetSchemaFromType() {
        MatcherAssert.assertThat(SchemaUtil.getSchemaFromType(Double.class, "", "doc").doc(), CoreMatchers.is("doc"));
    }

    @Test
    public void shouldPassIsNumberForInt() {
        MatcherAssert.assertThat(SchemaUtil.isNumber(INT32), CoreMatchers.is(true));
    }

    @Test
    public void shouldPassIsNumberForBigint() {
        MatcherAssert.assertThat(SchemaUtil.isNumber(INT64), CoreMatchers.is(true));
    }

    @Test
    public void shouldPassIsNumberForDouble() {
        MatcherAssert.assertThat(SchemaUtil.isNumber(FLOAT64), CoreMatchers.is(true));
    }

    @Test
    public void shouldFailIsNumberForBoolean() {
        MatcherAssert.assertThat(SchemaUtil.isNumber(BOOLEAN), CoreMatchers.is(false));
    }

    @Test
    public void shouldFailIsNumberForString() {
        MatcherAssert.assertThat(SchemaUtil.isNumber(STRING), CoreMatchers.is(false));
    }
}

