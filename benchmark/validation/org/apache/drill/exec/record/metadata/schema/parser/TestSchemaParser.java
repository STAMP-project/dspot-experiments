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
package org.apache.drill.exec.record.metadata.schema.parser;


import TypeProtos.DataMode.OPTIONAL;
import TypeProtos.DataMode.REQUIRED;
import TypeProtos.MinorType;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.BIT;
import TypeProtos.MinorType.DATE;
import TypeProtos.MinorType.FLOAT4;
import TypeProtos.MinorType.FLOAT8;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.INTERVAL;
import TypeProtos.MinorType.INTERVALDAY;
import TypeProtos.MinorType.INTERVALYEAR;
import TypeProtos.MinorType.TIME;
import TypeProtos.MinorType.TIMESTAMP;
import TypeProtos.MinorType.VARBINARY;
import TypeProtos.MinorType.VARCHAR;
import TypeProtos.MinorType.VARDECIMAL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.junit.Assert;
import org.junit.Test;


public class TestSchemaParser {
    @Test
    public void checkQuotedIdWithEscapes() {
        String schemaWithEscapes = "`a\\\\b\\`c` INT";
        Assert.assertEquals(schemaWithEscapes, SchemaExprParser.parseSchema(schemaWithEscapes).metadata(0).columnString());
        String schemaWithKeywords = "`INTEGER` INT";
        Assert.assertEquals(schemaWithKeywords, SchemaExprParser.parseSchema(schemaWithKeywords).metadata(0).columnString());
    }

    @Test
    public void testSchemaWithParen() {
        String schemaWithParen = "(`a` INT NOT NULL, `b` VARCHAR(10))";
        TupleMetadata schema = SchemaExprParser.parseSchema(schemaWithParen);
        Assert.assertEquals(2, schema.size());
        Assert.assertEquals("`a` INT NOT NULL", schema.metadata("a").columnString());
        Assert.assertEquals("`b` VARCHAR(10)", schema.metadata("b").columnString());
    }

    @Test
    public void testSkip() {
        String schemaString = "id\n/*comment*/int\r,//comment\r\nname\nvarchar\t\t\t";
        TupleMetadata schema = SchemaExprParser.parseSchema(schemaString);
        Assert.assertEquals(2, schema.size());
        Assert.assertEquals("`id` INT", schema.metadata("id").columnString());
        Assert.assertEquals("`name` VARCHAR", schema.metadata("name").columnString());
    }

    @Test
    public void testCaseInsensitivity() {
        String schema = "`Id` InTeGeR NoT NuLl";
        Assert.assertEquals("`Id` INT NOT NULL", SchemaExprParser.parseSchema(schema).metadata(0).columnString());
    }

    @Test
    public void testParseColumn() {
        ColumnMetadata column = SchemaExprParser.parseColumn("col int not null");
        Assert.assertEquals("`col` INT NOT NULL", column.columnString());
    }

    @Test
    public void testNumericTypes() {
        TupleMetadata schema = new SchemaBuilder().addNullable("int_col", INT).add("integer_col", INT).addNullable("bigint_col", BIGINT).add("float_col", FLOAT4).addNullable("double_col", FLOAT8).buildSchema();
        checkSchema(("int_col int, integer_col integer not null, bigint_col bigint, " + "float_col float not null, double_col double"), schema);
    }

    @Test
    public void testDecimalTypes() {
        TupleMetadata schema = new SchemaBuilder().addNullable("col", VARDECIMAL).add("col_p", VARDECIMAL, 5).addDecimal("col_ps", VARDECIMAL, OPTIONAL, 10, 2).buildSchema();
        List<String> schemas = Arrays.asList("col dec, col_p dec(5) not null, col_ps dec(10, 2)", "col decimal, col_p decimal(5) not null, col_ps decimal(10, 2)", "col numeric, col_p numeric(5) not null, col_ps numeric(10, 2)");
        schemas.forEach(( s) -> checkSchema(s, schema));
    }

    @Test
    public void testBooleanType() {
        TupleMetadata schema = new SchemaBuilder().addNullable("col", BIT).buildSchema();
        checkSchema("col boolean", schema);
    }

    @Test
    public void testCharacterTypes() {
        String schemaPattern = "col %1$s, col_p %1$s(50) not null";
        Map<String, TypeProtos.MinorType> properties = new HashMap<>();
        properties.put("char", VARCHAR);
        properties.put("character", VARCHAR);
        properties.put("character varying", VARCHAR);
        properties.put("varchar", VARCHAR);
        properties.put("binary", VARBINARY);
        properties.put("varbinary", VARBINARY);
        properties.forEach(( key, value) -> {
            TupleMetadata schema = new SchemaBuilder().addNullable("col", value).add("col_p", value, 50).buildSchema();
            checkSchema(String.format(schemaPattern, key), schema);
        });
    }

    @Test
    public void testTimeTypes() {
        TupleMetadata schema = new SchemaBuilder().addNullable("time_col", TIME).addNullable("time_prec_col", TIME, 3).add("date_col", DATE).addNullable("timestamp_col", TIMESTAMP).addNullable("timestamp_prec_col", TIMESTAMP, 3).buildSchema();
        checkSchema(("time_col time, time_prec_col time(3), date_col date not null, " + "timestamp_col timestamp, timestamp_prec_col timestamp(3)"), schema);
    }

    @Test
    public void testInterval() {
        TupleMetadata schema = new SchemaBuilder().addNullable("interval_year_col", INTERVALYEAR).addNullable("interval_month_col", INTERVALYEAR).addNullable("interval_day_col", INTERVALDAY).addNullable("interval_hour_col", INTERVALDAY).addNullable("interval_minute_col", INTERVALDAY).addNullable("interval_second_col", INTERVALDAY).addNullable("interval_col", INTERVAL).buildSchema();
        checkSchema(("interval_year_col interval year, interval_month_col interval month, " + ("interval_day_col interval day, interval_hour_col interval hour, interval_minute_col interval minute, " + "interval_second_col interval second, interval_col interval")), schema);
    }

    @Test
    public void testArray() {
        TupleMetadata schema = new SchemaBuilder().addArray("simple_array", INT).addRepeatedList("nested_array").addArray(INT).resumeSchema().addMapArray("map_array").addNullable("m1", INT).addNullable("m2", VARCHAR).resumeSchema().addRepeatedList("nested_array_map").addMapArray().addNullable("nm1", INT).addNullable("nm2", VARCHAR).resumeList().resumeSchema().buildSchema();
        checkSchema(("simple_array array<int>" + ((", nested_array array<array<int>>" + ", map_array array<map<m1 int, m2 varchar>>") + ", nested_array_map array<array<map<nm1 int, nm2 varchar>>>")), schema);
    }

    @Test
    public void testMap() {
        TupleMetadata schema = new SchemaBuilder().addMap("map_col").addNullable("int_col", INT).addArray("array_col", INT).addMap("nested_map").addNullable("m1", INT).addNullable("m2", VARCHAR).resumeMap().resumeSchema().buildSchema();
        checkSchema("map_col map<int_col int, array_col array<int>, nested_map map<m1 int, m2 varchar>>", schema);
    }

    @Test
    public void testModeForSimpleType() {
        TupleMetadata schema = SchemaExprParser.parseSchema("id int not null, name varchar");
        Assert.assertFalse(schema.metadata("id").isNullable());
        Assert.assertTrue(schema.metadata("name").isNullable());
    }

    @Test
    public void testModeForMapType() {
        TupleMetadata schema = SchemaExprParser.parseSchema("m map<m1 int not null, m2 varchar>");
        ColumnMetadata map = schema.metadata("m");
        Assert.assertTrue(map.isMap());
        Assert.assertEquals(REQUIRED, map.mode());
        TupleMetadata mapSchema = map.mapSchema();
        Assert.assertFalse(mapSchema.metadata("m1").isNullable());
        Assert.assertTrue(mapSchema.metadata("m2").isNullable());
    }

    @Test
    public void testModeForRepeatedType() {
        TupleMetadata schema = SchemaExprParser.parseSchema("a array<int>, aa array<array<int>>, ma array<map<m1 int not null, m2 varchar>>");
        Assert.assertTrue(schema.metadata("a").isArray());
        ColumnMetadata nestedArray = schema.metadata("aa");
        Assert.assertTrue(nestedArray.isArray());
        Assert.assertTrue(nestedArray.childSchema().isArray());
        ColumnMetadata mapArray = schema.metadata("ma");
        Assert.assertTrue(mapArray.isArray());
        Assert.assertTrue(mapArray.isMap());
        TupleMetadata mapSchema = mapArray.mapSchema();
        Assert.assertFalse(mapSchema.metadata("m1").isNullable());
        Assert.assertTrue(mapSchema.metadata("m2").isNullable());
    }

    @Test
    public void testFormat() {
        String value = "`a` DATE NOT NULL FORMAT 'yyyy-MM-dd'";
        TupleMetadata schema = SchemaExprParser.parseSchema(value);
        ColumnMetadata columnMetadata = schema.metadata("a");
        Assert.assertEquals("yyyy-MM-dd", columnMetadata.formatValue());
        Assert.assertEquals(value, columnMetadata.columnString());
    }

    @Test
    public void testDefault() {
        String value = "`a` INT NOT NULL DEFAULT '12'";
        TupleMetadata schema = SchemaExprParser.parseSchema(value);
        ColumnMetadata columnMetadata = schema.metadata("a");
        Assert.assertTrue(((columnMetadata.defaultValue()) instanceof Integer));
        Assert.assertEquals(12, columnMetadata.defaultValue());
        Assert.assertEquals("12", columnMetadata.defaultStringValue());
        Assert.assertEquals(value, columnMetadata.columnString());
    }

    @Test
    public void testFormatAndDefault() {
        String value = "`a` DATE NOT NULL FORMAT 'yyyy-MM-dd' DEFAULT '2018-12-31'";
        TupleMetadata schema = SchemaExprParser.parseSchema(value);
        ColumnMetadata columnMetadata = schema.metadata("a");
        Assert.assertTrue(((columnMetadata.defaultValue()) instanceof LocalDate));
        Assert.assertEquals(LocalDate.of(2018, 12, 31), columnMetadata.defaultValue());
        Assert.assertEquals("2018-12-31", columnMetadata.defaultStringValue());
        Assert.assertEquals(value, columnMetadata.columnString());
    }

    @Test
    public void testColumnProperties() {
        String value = "`a` INT NOT NULL PROPERTIES { 'k1' = 'v1', 'k2' = 'v2' }";
        TupleMetadata schema = SchemaExprParser.parseSchema(value);
        ColumnMetadata columnMetadata = schema.metadata("a");
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("k1", "v1");
        properties.put("k2", "v2");
        Assert.assertEquals(properties, columnMetadata.properties());
        Assert.assertEquals(value, columnMetadata.columnString());
    }
}

