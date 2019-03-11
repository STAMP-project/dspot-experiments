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
package org.apache.flink.formats.parquet.utils;


import BasicArrayTypeInfo.LONG_ARRAY_TYPE_INFO;
import BasicArrayTypeInfo.STRING_ARRAY_TYPE_INFO;
import BasicTypeInfo.LONG_TYPE_INFO;
import BasicTypeInfo.STRING_TYPE_INFO;
import OriginalType.INT_64;
import OriginalType.LIST;
import OriginalType.UTF8;
import PrimitiveType.PrimitiveTypeName.BINARY;
import PrimitiveType.PrimitiveTypeName.INT64;
import Type.Repetition.OPTIONAL;
import Type.Repetition.REQUIRED;
import java.util.Arrays;
import java.util.Map;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test case for conversion between Parquet schema and Flink date types.
 */
public class ParquetSchemaConverterTest extends TestUtil {
    private final TypeInformation<Row> simplyRowType = Types.ROW_NAMED(new String[]{ "foo", "bar", "arr" }, LONG_TYPE_INFO, STRING_TYPE_INFO, LONG_ARRAY_TYPE_INFO);

    private final TypeInformation<Row[]> nestedArray = Types.OBJECT_ARRAY(Types.ROW_NAMED(new String[]{ "type", "value" }, STRING_TYPE_INFO, STRING_TYPE_INFO));

    @SuppressWarnings("unchecked")
    private final TypeInformation<Map<String, Row>> nestedMap = Types.MAP(STRING_TYPE_INFO, Types.ROW_NAMED(new String[]{ "type", "value" }, STRING_TYPE_INFO, STRING_TYPE_INFO));

    @SuppressWarnings("unchecked")
    private final TypeInformation<Row> nestedRowType = Types.ROW_NAMED(new String[]{ "foo", "spamMap", "bar", "arr", "strArray", "nestedMap", "nestedArray" }, LONG_TYPE_INFO, Types.MAP(STRING_TYPE_INFO, STRING_TYPE_INFO), Types.ROW_NAMED(new String[]{ "spam" }, LONG_TYPE_INFO), LONG_ARRAY_TYPE_INFO, STRING_ARRAY_TYPE_INFO, nestedMap, nestedArray);

    private final Type[] simpleStandardTypes = new Type[]{ org.apache.parquet.schema.Types.primitive(INT64, OPTIONAL).as(INT_64).named("foo"), org.apache.parquet.schema.Types.primitive(BINARY, OPTIONAL).as(UTF8).named("bar"), org.apache.parquet.schema.Types.optionalGroup().addField(org.apache.parquet.schema.Types.repeatedGroup().addField(org.apache.parquet.schema.Types.primitive(INT64, REQUIRED).as(INT_64).named("element")).named("list")).as(LIST).named("arr") };

    private final Type[] nestedTypes = new Type[]{ org.apache.parquet.schema.Types.primitive(INT64, OPTIONAL).as(INT_64).named("foo"), org.apache.parquet.schema.Types.optionalMap().value(BINARY, OPTIONAL).as(UTF8).named("spamMap"), org.apache.parquet.schema.Types.optionalGroup().addField(org.apache.parquet.schema.Types.primitive(INT64, OPTIONAL).as(INT_64).named("spam")).named("bar"), org.apache.parquet.schema.Types.optionalGroup().addField(org.apache.parquet.schema.Types.repeatedGroup().addField(org.apache.parquet.schema.Types.primitive(INT64, REQUIRED).as(INT_64).named("element")).named("list")).as(LIST).named("arr"), org.apache.parquet.schema.Types.optionalGroup().addField(org.apache.parquet.schema.Types.repeatedGroup().addField(org.apache.parquet.schema.Types.primitive(BINARY, REQUIRED).as(UTF8).named("element")).named("list")).as(LIST).named("strArray"), org.apache.parquet.schema.Types.optionalMap().value(org.apache.parquet.schema.Types.optionalGroup().addField(org.apache.parquet.schema.Types.primitive(BINARY, OPTIONAL).as(UTF8).named("type")).addField(org.apache.parquet.schema.Types.primitive(BINARY, OPTIONAL).as(UTF8).named("value")).named("value")).named("nestedMap"), org.apache.parquet.schema.Types.optionalGroup().addField(org.apache.parquet.schema.Types.repeatedGroup().addField(org.apache.parquet.schema.Types.primitive(BINARY, REQUIRED).as(UTF8).named("type")).addField(org.apache.parquet.schema.Types.primitive(BINARY, REQUIRED).as(UTF8).named("value")).named("element")).as(LIST).named("nestedArray") };

    @Test
    public void testSimpleSchemaConversion() {
        MessageType simpleType = new MessageType("simple", simpleStandardTypes);
        RowTypeInfo rowTypeInfo = ((RowTypeInfo) (ParquetSchemaConverter.fromParquetType(simpleType)));
        Assert.assertEquals(simplyRowType, rowTypeInfo);
    }

    @Test
    public void testNestedSchemaConversion() {
        MessageType nestedTypes = new MessageType("nested", this.nestedTypes);
        RowTypeInfo rowTypeInfo = ((RowTypeInfo) (ParquetSchemaConverter.fromParquetType(nestedTypes)));
        Assert.assertEquals(nestedRowType, rowTypeInfo);
    }

    @Test
    public void testSimpleRowTypeConversion() {
        MessageType simpleSchema = ParquetSchemaConverter.toParquetType(simplyRowType, true);
        Assert.assertEquals(Arrays.asList(simpleStandardTypes), simpleSchema.getFields());
    }

    @Test
    public void testNestedRowTypeConversion() {
        MessageType nestedSchema = ParquetSchemaConverter.toParquetType(nestedRowType, true);
        Assert.assertEquals(Arrays.asList(nestedTypes), nestedSchema.getFields());
    }
}

