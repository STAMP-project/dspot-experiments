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
package org.apache.beam.sdk.extensions.sql.impl.utils;


import Schema.FieldType.BOOLEAN;
import Schema.FieldType.BYTE;
import Schema.FieldType.BYTES;
import Schema.FieldType.DECIMAL;
import Schema.FieldType.DOUBLE;
import Schema.FieldType.FLOAT;
import Schema.FieldType.INT16;
import Schema.FieldType.INT32;
import Schema.FieldType.INT64;
import Schema.FieldType.STRING;
import SqlTypeName.BIGINT;
import SqlTypeName.INTEGER;
import SqlTypeName.SMALLINT;
import SqlTypeName.TINYINT;
import SqlTypeName.VARBINARY;
import SqlTypeName.VARCHAR;
import java.util.Map;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for conversion from Beam schema to Calcite data type.
 */
public class CalciteUtilsTest {
    RelDataTypeFactory dataTypeFactory;

    @Test
    public void testToCalciteRowType() {
        final Schema schema = Schema.builder().addField("f1", BYTE).addField("f2", INT16).addField("f3", INT32).addField("f4", INT64).addField("f5", FLOAT).addField("f6", DOUBLE).addField("f7", DECIMAL).addField("f8", BOOLEAN).addField("f9", BYTES).addField("f10", STRING).build();
        final Map<String, RelDataType> fields = calciteRowTypeFields(schema);
        Assert.assertEquals(10, fields.size());
        fields.values().forEach(( x) -> assertFalse(x.isNullable()));
        Assert.assertEquals(TINYINT, fields.get("f1").getSqlTypeName());
        Assert.assertEquals(SMALLINT, fields.get("f2").getSqlTypeName());
        Assert.assertEquals(INTEGER, fields.get("f3").getSqlTypeName());
        Assert.assertEquals(BIGINT, fields.get("f4").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.FLOAT, fields.get("f5").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.DOUBLE, fields.get("f6").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.DECIMAL, fields.get("f7").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.BOOLEAN, fields.get("f8").getSqlTypeName());
        Assert.assertEquals(VARBINARY, fields.get("f9").getSqlTypeName());
        Assert.assertEquals(VARCHAR, fields.get("f10").getSqlTypeName());
    }

    @Test
    public void testToCalciteRowTypeNullable() {
        final Schema schema = Schema.builder().addNullableField("f1", BYTE).addNullableField("f2", INT16).addNullableField("f3", INT32).addNullableField("f4", INT64).addNullableField("f5", FLOAT).addNullableField("f6", DOUBLE).addNullableField("f7", DECIMAL).addNullableField("f8", BOOLEAN).addNullableField("f9", BYTES).addNullableField("f10", STRING).build();
        final Map<String, RelDataType> fields = calciteRowTypeFields(schema);
        Assert.assertEquals(10, fields.size());
        fields.values().forEach(( x) -> assertTrue(x.isNullable()));
        Assert.assertEquals(TINYINT, fields.get("f1").getSqlTypeName());
        Assert.assertEquals(SMALLINT, fields.get("f2").getSqlTypeName());
        Assert.assertEquals(INTEGER, fields.get("f3").getSqlTypeName());
        Assert.assertEquals(BIGINT, fields.get("f4").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.FLOAT, fields.get("f5").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.DOUBLE, fields.get("f6").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.DECIMAL, fields.get("f7").getSqlTypeName());
        Assert.assertEquals(SqlTypeName.BOOLEAN, fields.get("f8").getSqlTypeName());
        Assert.assertEquals(VARBINARY, fields.get("f9").getSqlTypeName());
        Assert.assertEquals(VARCHAR, fields.get("f10").getSqlTypeName());
    }

    @Test
    public void testRoundTripBeamSchema() {
        final Schema schema = Schema.builder().addField("f1", BYTE).addField("f2", INT16).addField("f3", INT32).addField("f4", INT64).addField("f5", FLOAT).addField("f6", DOUBLE).addField("f7", DECIMAL).addField("f8", BOOLEAN).addField("f9", BYTES).addField("f10", STRING).build();
        final Schema out = CalciteUtils.toSchema(CalciteUtils.toCalciteRowType(schema, dataTypeFactory));
        Assert.assertEquals(schema, out);
    }

    @Test
    public void testRoundTripBeamNullableSchema() {
        final Schema schema = Schema.builder().addNullableField("f1", BYTE).addNullableField("f2", INT16).addNullableField("f3", INT32).addNullableField("f4", INT64).addNullableField("f5", FLOAT).addNullableField("f6", DOUBLE).addNullableField("f7", DECIMAL).addNullableField("f8", BOOLEAN).addNullableField("f9", BYTES).addNullableField("f10", STRING).build();
        final Schema out = CalciteUtils.toSchema(CalciteUtils.toCalciteRowType(schema, dataTypeFactory));
        Assert.assertEquals(schema, out);
    }
}

