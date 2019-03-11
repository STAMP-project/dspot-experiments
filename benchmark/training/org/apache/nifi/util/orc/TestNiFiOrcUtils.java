/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.util.orc;


import RecordFieldType.ARRAY;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.hadoop.hive.ql.io.orc.NiFiOrcUtils;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObject;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the NiFiOrcUtils helper class
 */
public class TestNiFiOrcUtils {
    @Test
    public void test_getOrcField_primitive() {
        // Expected ORC types
        TypeInfo[] expectedTypes = new TypeInfo[]{ TypeInfoFactory.getPrimitiveTypeInfo("int"), TypeInfoFactory.getPrimitiveTypeInfo("bigint"), TypeInfoFactory.getPrimitiveTypeInfo("boolean"), TypeInfoFactory.getPrimitiveTypeInfo("float"), TypeInfoFactory.getPrimitiveTypeInfo("double"), TypeInfoFactory.getPrimitiveTypeInfo("binary"), TypeInfoFactory.getPrimitiveTypeInfo("string") };
        // Build a fake Avro record with all types
        RecordSchema testSchema = TestNiFiOrcUtils.buildPrimitiveRecordSchema();
        List<RecordField> fields = testSchema.getFields();
        for (int i = 0; i < (fields.size()); i++) {
            Assert.assertEquals(expectedTypes[i], NiFiOrcUtils.getOrcField(fields.get(i).getDataType(), false));
        }
    }

    @Test
    public void test_getOrcField_union_optional_type() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("union").type().unionOf().nullBuilder().endNull().and().booleanType().endUnion().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("union").get().getDataType(), false);
        Assert.assertEquals(TestNiFiOrcUtils.TypeInfoCreator.createBoolean(), orcType);
    }

    @Test
    public void test_getOrcField_union() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("union").type().unionOf().intType().and().booleanType().endUnion().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("union").get().getDataType(), false);
        Assert.assertEquals(TypeInfoFactory.getUnionTypeInfo(Arrays.asList(TestNiFiOrcUtils.TypeInfoCreator.createInt(), TestNiFiOrcUtils.TypeInfoCreator.createBoolean())), orcType);
    }

    @Test
    public void test_getOrcField_map() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("map").type().map().values().doubleType().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("map").get().getDataType(), true);
        Assert.assertEquals(TypeInfoFactory.getMapTypeInfo(TestNiFiOrcUtils.TypeInfoCreator.createString(), TestNiFiOrcUtils.TypeInfoCreator.createDouble()), orcType);
    }

    @Test
    public void test_getOrcField_nested_map() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("map").type().map().values().map().values().doubleType().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("map").get().getDataType(), false);
        Assert.assertEquals(TypeInfoFactory.getMapTypeInfo(TestNiFiOrcUtils.TypeInfoCreator.createString(), TypeInfoFactory.getMapTypeInfo(TestNiFiOrcUtils.TypeInfoCreator.createString(), TestNiFiOrcUtils.TypeInfoCreator.createDouble())), orcType);
    }

    @Test
    public void test_getOrcField_array() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("array").type().array().items().longType().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("array").get().getDataType(), false);
        Assert.assertEquals(TypeInfoFactory.getListTypeInfo(TestNiFiOrcUtils.TypeInfoCreator.createLong()), orcType);
    }

    @Test
    public void test_getOrcField_complex_array() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("Array").type().array().items().map().values().floatType().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("Array").get().getDataType(), true);
        Assert.assertEquals(TypeInfoFactory.getListTypeInfo(TypeInfoFactory.getMapTypeInfo(TestNiFiOrcUtils.TypeInfoCreator.createString(), TestNiFiOrcUtils.TypeInfoCreator.createFloat())), orcType);
    }

    @Test
    public void test_getOrcField_record() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("Int").type().intType().noDefault();
        builder.name("Long").type().longType().longDefault(1L);
        builder.name("Array").type().array().items().stringType().noDefault();
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        // Normalize field names for Hive, assert that their names are now lowercase
        TypeInfo orcType = NiFiOrcUtils.getOrcSchema(testSchema, true);
        Assert.assertEquals(TypeInfoFactory.getStructTypeInfo(Arrays.asList("int", "long", "array"), Arrays.asList(TestNiFiOrcUtils.TypeInfoCreator.createInt(), TestNiFiOrcUtils.TypeInfoCreator.createLong(), TypeInfoFactory.getListTypeInfo(TestNiFiOrcUtils.TypeInfoCreator.createString()))), orcType);
    }

    @Test
    public void test_getOrcField_enum() {
        final SchemaBuilder.FieldAssembler<Schema> builder = SchemaBuilder.record("testRecord").namespace("any.data").fields();
        builder.name("enumField").type().enumeration("enum").symbols("a", "b", "c").enumDefault("a");
        RecordSchema testSchema = AvroTypeUtil.createSchema(builder.endRecord());
        TypeInfo orcType = NiFiOrcUtils.getOrcField(testSchema.getField("enumField").get().getDataType(), true);
        Assert.assertEquals(TestNiFiOrcUtils.TypeInfoCreator.createString(), orcType);
    }

    @Test
    public void test_getPrimitiveOrcTypeFromPrimitiveFieldType() {
        // Expected ORC types
        TypeInfo[] expectedTypes = new TypeInfo[]{ TestNiFiOrcUtils.TypeInfoCreator.createInt(), TestNiFiOrcUtils.TypeInfoCreator.createLong(), TestNiFiOrcUtils.TypeInfoCreator.createBoolean(), TestNiFiOrcUtils.TypeInfoCreator.createFloat(), TestNiFiOrcUtils.TypeInfoCreator.createDouble(), TestNiFiOrcUtils.TypeInfoCreator.createBinary(), TestNiFiOrcUtils.TypeInfoCreator.createString() };
        RecordSchema testSchema = TestNiFiOrcUtils.buildPrimitiveRecordSchema();
        List<RecordField> fields = testSchema.getFields();
        for (int i = 0; i < (fields.size()); i++) {
            // Skip Binary as it is a primitive type in Avro but a complex type (array[byte]) in the NiFi Record API
            if (i == 5) {
                continue;
            }
            Assert.assertEquals(expectedTypes[i], NiFiOrcUtils.getPrimitiveOrcTypeFromPrimitiveFieldType(fields.get(i).getDataType()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getPrimitiveOrcTypeFromPrimitiveFieldType_badType() {
        NiFiOrcUtils.getPrimitiveOrcTypeFromPrimitiveFieldType(ARRAY.getDataType());
    }

    @Test
    public void test_getWritable() throws Exception {
        Assert.assertTrue(((NiFiOrcUtils.convertToORCObject(null, 1, true)) instanceof IntWritable));
        Assert.assertTrue(((NiFiOrcUtils.convertToORCObject(null, 1L, true)) instanceof LongWritable));
        Assert.assertTrue(((NiFiOrcUtils.convertToORCObject(null, 1.0F, true)) instanceof FloatWritable));
        Assert.assertTrue(((NiFiOrcUtils.convertToORCObject(null, 1.0, true)) instanceof DoubleWritable));
        Assert.assertTrue(((NiFiOrcUtils.convertToORCObject(null, new int[]{ 1, 2, 3 }, true)) instanceof List));
        Assert.assertTrue(((NiFiOrcUtils.convertToORCObject(null, Arrays.asList(1, 2, 3), true)) instanceof List));
        Map<String, Float> map = new HashMap<>();
        map.put("Hello", 1.0F);
        map.put("World", 2.0F);
        Object convMap = NiFiOrcUtils.convertToORCObject(TypeInfoUtils.getTypeInfoFromTypeString("map<string,float>"), map, true);
        Assert.assertTrue((convMap instanceof Map));
        ((Map) (convMap)).forEach(( key, value) -> {
            Assert.assertTrue((key instanceof Text));
            Assert.assertTrue((value instanceof FloatWritable));
        });
    }

    @Test
    public void test_getHiveTypeFromFieldType_primitive() {
        // Expected ORC types
        String[] expectedTypes = new String[]{ "INT", "BIGINT", "BOOLEAN", "FLOAT", "DOUBLE", "BINARY", "STRING" };
        RecordSchema testSchema = TestNiFiOrcUtils.buildPrimitiveRecordSchema();
        List<RecordField> fields = testSchema.getFields();
        for (int i = 0; i < (fields.size()); i++) {
            Assert.assertEquals(expectedTypes[i], NiFiOrcUtils.getHiveTypeFromFieldType(fields.get(i).getDataType(), false));
        }
    }

    @Test
    public void test_getHiveTypeFromFieldType_complex() {
        // Expected ORC types
        String[] expectedTypes = new String[]{ "INT", "MAP<STRING, DOUBLE>", "STRING", "UNIONTYPE<BIGINT, FLOAT>", "ARRAY<INT>" };
        RecordSchema testSchema = TestNiFiOrcUtils.buildComplexRecordSchema();
        List<RecordField> fields = testSchema.getFields();
        for (int i = 0; i < (fields.size()); i++) {
            Assert.assertEquals(expectedTypes[i], NiFiOrcUtils.getHiveTypeFromFieldType(fields.get(i).getDataType(), false));
        }
        Assert.assertEquals("STRUCT<myInt:INT, myMap:MAP<STRING, DOUBLE>, myEnum:STRING, myLongOrFloat:UNIONTYPE<BIGINT, FLOAT>, myIntList:ARRAY<INT>>", NiFiOrcUtils.getHiveSchema(testSchema, false));
    }

    @Test
    public void test_generateHiveDDL_primitive() {
        RecordSchema schema = TestNiFiOrcUtils.buildPrimitiveRecordSchema();
        String ddl = NiFiOrcUtils.generateHiveDDL(schema, "myHiveTable", false);
        Assert.assertEquals(("CREATE EXTERNAL TABLE IF NOT EXISTS `myHiveTable` (`int` INT, `long` BIGINT, `boolean` BOOLEAN, `float` FLOAT, `double` DOUBLE, `bytes` BINARY, `string` STRING)" + " STORED AS ORC"), ddl);
    }

    @Test
    public void test_generateHiveDDL_complex() {
        RecordSchema schema = TestNiFiOrcUtils.buildComplexRecordSchema();
        String ddl = NiFiOrcUtils.generateHiveDDL(schema, "myHiveTable", false);
        Assert.assertEquals(("CREATE EXTERNAL TABLE IF NOT EXISTS `myHiveTable` " + ("(`myInt` INT, `myMap` MAP<STRING, DOUBLE>, `myEnum` STRING, `myLongOrFloat` UNIONTYPE<BIGINT, FLOAT>, `myIntList` ARRAY<INT>)" + " STORED AS ORC")), ddl);
    }

    @Test
    public void test_generateHiveDDL_complex_normalize() {
        RecordSchema schema = TestNiFiOrcUtils.buildComplexRecordSchema();
        String ddl = NiFiOrcUtils.generateHiveDDL(schema, "myHiveTable", true);
        Assert.assertEquals(("CREATE EXTERNAL TABLE IF NOT EXISTS `myHiveTable` " + ("(`myint` INT, `mymap` MAP<STRING, DOUBLE>, `myenum` STRING, `mylongorfloat` UNIONTYPE<BIGINT, FLOAT>, `myintlist` ARRAY<INT>)" + " STORED AS ORC")), ddl);
    }

    @Test
    public void test_convertToORCObject() {
        List<Object> objects = Arrays.asList("Hello", "x");
        objects.forEach(( avroObject) -> {
            Object o = NiFiOrcUtils.convertToORCObject(TypeInfoUtils.getTypeInfoFromTypeString("uniontype<bigint,string>"), avroObject, true);
            Assert.assertTrue((o instanceof UnionObject));
            UnionObject uo = ((UnionObject) (o));
            Assert.assertTrue(((uo.getObject()) instanceof Text));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_convertToORCObjectBadUnion() {
        NiFiOrcUtils.convertToORCObject(TypeInfoUtils.getTypeInfoFromTypeString("uniontype<bigint,long>"), "Hello", true);
    }

    @Test
    public void test_getHiveTypeFromFieldType_complex_normalize() {
        // Expected ORC types
        String[] expectedTypes = new String[]{ "INT", "MAP<STRING, DOUBLE>", "STRING", "UNIONTYPE<BIGINT, FLOAT>", "ARRAY<INT>" };
        RecordSchema testSchema = TestNiFiOrcUtils.buildComplexRecordSchema();
        List<RecordField> fields = testSchema.getFields();
        for (int i = 0; i < (fields.size()); i++) {
            Assert.assertEquals(expectedTypes[i], NiFiOrcUtils.getHiveTypeFromFieldType(fields.get(i).getDataType(), true));
        }
        Assert.assertEquals("STRUCT<myint:INT, mymap:MAP<STRING, DOUBLE>, myenum:STRING, mylongorfloat:UNIONTYPE<BIGINT, FLOAT>, myintlist:ARRAY<INT>>", NiFiOrcUtils.getHiveSchema(testSchema, true));
    }

    private static class TypeInfoCreator {
        static TypeInfo createInt() {
            return TypeInfoFactory.getPrimitiveTypeInfo("int");
        }

        static TypeInfo createLong() {
            return TypeInfoFactory.getPrimitiveTypeInfo("bigint");
        }

        static TypeInfo createBoolean() {
            return TypeInfoFactory.getPrimitiveTypeInfo("boolean");
        }

        static TypeInfo createFloat() {
            return TypeInfoFactory.getPrimitiveTypeInfo("float");
        }

        static TypeInfo createDouble() {
            return TypeInfoFactory.getPrimitiveTypeInfo("double");
        }

        static TypeInfo createBinary() {
            return TypeInfoFactory.getPrimitiveTypeInfo("binary");
        }

        static TypeInfo createString() {
            return TypeInfoFactory.getPrimitiveTypeInfo("string");
        }
    }
}

