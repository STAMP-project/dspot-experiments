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
import java.util.Arrays;
import java.util.HashMap;
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
    public void checkQuotedId() {
        String schemaWithEscapes = "`a\\\\b\\`c` INT";
        Assert.assertEquals(schemaWithEscapes, SchemaExprParser.parseSchema(schemaWithEscapes).schemaString());
        String schemaWithKeywords = "`INTEGER` INT";
        Assert.assertEquals(schemaWithKeywords, SchemaExprParser.parseSchema(schemaWithKeywords).schemaString());
    }

    @Test
    public void testSchemaWithParen() {
        String schema = "`a` INT NOT NULL, `b` VARCHAR(10)";
        Assert.assertEquals(schema, SchemaExprParser.parseSchema(String.format("(%s)", schema)).schemaString());
    }

    @Test
    public void testSkip() {
        String schemaString = "id\n/*comment*/int\r,//comment\r\nname\nvarchar\t\t\t";
        TupleMetadata schema = SchemaExprParser.parseSchema(schemaString);
        Assert.assertEquals(2, schema.size());
        Assert.assertEquals("`id` INT, `name` VARCHAR", schema.schemaString());
    }

    @Test
    public void testCaseInsensitivity() {
        String schema = "`Id` InTeGeR NoT NuLl";
        Assert.assertEquals("`Id` INT NOT NULL", SchemaExprParser.parseSchema(schema).schemaString());
    }

    @Test
    public void testParseColumn() {
        ColumnMetadata column = SchemaExprParser.parseColumn("col int not null");
        Assert.assertEquals("`col` INT NOT NULL", column.columnString());
    }

    @Test
    public void testNumericTypes() {
        TupleMetadata schema = new SchemaBuilder().addNullable("int_col", INT).add("integer_col", INT).addNullable("bigint_col", BIGINT).add("float_col", FLOAT4).addNullable("double_col", FLOAT8).buildSchema();
        checkSchema(("int_col int, integer_col integer not null, bigint_col bigint, " + "float_col float not null, double_col double"), schema, ("`int_col` INT, `integer_col` INT NOT NULL, `bigint_col` BIGINT, " + "`float_col` FLOAT NOT NULL, `double_col` DOUBLE"));
    }

    @Test
    public void testDecimalTypes() {
        TupleMetadata schema = new SchemaBuilder().addNullable("col", VARDECIMAL).add("col_p", VARDECIMAL, 5).addDecimal("col_ps", VARDECIMAL, OPTIONAL, 10, 2).buildSchema();
        List<String> schemas = Arrays.asList("col dec, col_p dec(5) not null, col_ps dec(10, 2)", "col decimal, col_p decimal(5) not null, col_ps decimal(10, 2)", "col numeric, col_p numeric(5) not null, col_ps numeric(10, 2)");
        String expectedSchema = "`col` DECIMAL, `col_p` DECIMAL(5) NOT NULL, `col_ps` DECIMAL(10, 2)";
        schemas.forEach(( s) -> checkSchema(s, schema, expectedSchema));
    }

    @Test
    public void testBooleanType() {
        TupleMetadata schema = new SchemaBuilder().addNullable("col", BIT).buildSchema();
        checkSchema("col boolean", schema, "`col` BOOLEAN");
    }

    @Test
    public void testCharacterTypes() {
        String schemaPattern = "col %1$s, col_p %1$s(50) not null";
        String expectedSchema = "`col` %1$s, `col_p` %1$s(50) NOT NULL";
        Map<String, TypeProtos.MinorType> properties = new HashMap<>();
        properties.put("char", VARCHAR);
        properties.put("character", VARCHAR);
        properties.put("character varying", VARCHAR);
        properties.put("varchar", VARCHAR);
        properties.put("binary", VARBINARY);
        properties.put("varbinary", VARBINARY);
        properties.forEach(( key, value) -> {
            TupleMetadata schema = new SchemaBuilder().addNullable("col", value).add("col_p", value, 50).buildSchema();
            checkSchema(String.format(schemaPattern, key), schema, String.format(expectedSchema, value.name()));
        });
    }

    @Test
    public void testTimeTypes() {
        TupleMetadata schema = new SchemaBuilder().addNullable("time_col", TIME).addNullable("time_prec_col", TIME, 3).add("date_col", DATE).addNullable("timestamp_col", TIMESTAMP).addNullable("timestamp_prec_col", TIMESTAMP, 3).buildSchema();
        checkSchema(("time_col time, time_prec_col time(3), date_col date not null, " + "timestamp_col timestamp, timestamp_prec_col timestamp(3)"), schema, ("`time_col` TIME, `time_prec_col` TIME(3), `date_col` DATE NOT NULL, " + "`timestamp_col` TIMESTAMP, `timestamp_prec_col` TIMESTAMP(3)"));
    }

    @Test
    public void testInterval() {
        TupleMetadata schema = new SchemaBuilder().addNullable("interval_year_col", INTERVALYEAR).addNullable("interval_month_col", INTERVALYEAR).addNullable("interval_day_col", INTERVALDAY).addNullable("interval_hour_col", INTERVALDAY).addNullable("interval_minute_col", INTERVALDAY).addNullable("interval_second_col", INTERVALDAY).addNullable("interval_col", INTERVAL).buildSchema();
        checkSchema(("interval_year_col interval year, interval_month_col interval month, " + ("interval_day_col interval day, interval_hour_col interval hour, interval_minute_col interval minute, " + "interval_second_col interval second, interval_col interval")), schema, ("`interval_year_col` INTERVAL YEAR, `interval_month_col` INTERVAL YEAR, " + ("`interval_day_col` INTERVAL DAY, `interval_hour_col` INTERVAL DAY, `interval_minute_col` INTERVAL DAY, " + "`interval_second_col` INTERVAL DAY, `interval_col` INTERVAL")));
    }

    @Test
    public void testArray() {
        TupleMetadata schema = new SchemaBuilder().addArray("simple_array", INT).addRepeatedList("nested_array").addArray(INT).resumeSchema().addMapArray("map_array").addNullable("m1", INT).addNullable("m2", VARCHAR).resumeSchema().addRepeatedList("nested_array_map").addMapArray().addNullable("nm1", INT).addNullable("nm2", VARCHAR).resumeList().resumeSchema().buildSchema();
        checkSchema(("simple_array array<int>" + ((", nested_array array<array<int>>" + ", map_array array<map<m1 int, m2 varchar>>") + ", nested_array_map array<array<map<nm1 int, nm2 varchar>>>")), schema, ("`simple_array` ARRAY<INT>" + ((", `nested_array` ARRAY<ARRAY<INT>>" + ", `map_array` ARRAY<MAP<`m1` INT, `m2` VARCHAR>>") + ", `nested_array_map` ARRAY<ARRAY<MAP<`nm1` INT, `nm2` VARCHAR>>>")));
    }

    @Test
    public void testMap() {
        TupleMetadata schema = new SchemaBuilder().addMap("map_col").addNullable("int_col", INT).addArray("array_col", INT).addMap("nested_map").addNullable("m1", INT).addNullable("m2", VARCHAR).resumeMap().resumeSchema().buildSchema();
        checkSchema("map_col map<int_col int, array_col array<int>, nested_map map<m1 int, m2 varchar>>", schema, "`map_col` MAP<`int_col` INT, `array_col` ARRAY<INT>, `nested_map` MAP<`m1` INT, `m2` VARCHAR>>");
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
}

