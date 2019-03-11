/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.data;


import Date.SCHEMA;
import Schema.BOOLEAN_SCHEMA;
import Schema.BYTES_SCHEMA;
import Schema.FLOAT32_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_BYTES_SCHEMA;
import Schema.OPTIONAL_FLOAT32_SCHEMA;
import Schema.OPTIONAL_INT16_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_INT8_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import Type.BOOLEAN;
import Type.BYTES;
import Type.FLOAT32;
import Type.FLOAT64;
import Type.INT16;
import Type.INT32;
import Type.INT64;
import Type.INT8;
import Type.STRING;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.errors.SchemaProjectorException;
import org.junit.Assert;
import org.junit.Test;

import static Date.SCHEMA;
import static Schema.BOOLEAN_SCHEMA;
import static Schema.BYTES_SCHEMA;
import static Schema.FLOAT32_SCHEMA;
import static Schema.FLOAT64_SCHEMA;
import static Schema.INT16_SCHEMA;
import static Schema.INT32_SCHEMA;
import static Schema.INT64_SCHEMA;
import static Schema.INT8_SCHEMA;
import static Schema.OPTIONAL_FLOAT32_SCHEMA;
import static Schema.OPTIONAL_FLOAT64_SCHEMA;
import static Schema.OPTIONAL_INT16_SCHEMA;
import static Schema.OPTIONAL_INT32_SCHEMA;
import static Schema.OPTIONAL_INT64_SCHEMA;
import static Schema.OPTIONAL_INT8_SCHEMA;
import static Schema.STRING_SCHEMA;


public class SchemaProjectorTest {
    @Test
    public void testPrimitiveTypeProjection() {
        Object projected;
        projected = SchemaProjector.project(BOOLEAN_SCHEMA, false, BOOLEAN_SCHEMA);
        Assert.assertEquals(false, projected);
        byte[] bytes = new byte[]{ ((byte) (1)), ((byte) (2)) };
        projected = SchemaProjector.project(BYTES_SCHEMA, bytes, BYTES_SCHEMA);
        Assert.assertEquals(bytes, projected);
        projected = SchemaProjector.project(STRING_SCHEMA, "abc", STRING_SCHEMA);
        Assert.assertEquals("abc", projected);
        projected = SchemaProjector.project(BOOLEAN_SCHEMA, false, OPTIONAL_BOOLEAN_SCHEMA);
        Assert.assertEquals(false, projected);
        projected = SchemaProjector.project(BYTES_SCHEMA, bytes, OPTIONAL_BYTES_SCHEMA);
        Assert.assertEquals(bytes, projected);
        projected = SchemaProjector.project(STRING_SCHEMA, "abc", OPTIONAL_STRING_SCHEMA);
        Assert.assertEquals("abc", projected);
        try {
            SchemaProjector.project(OPTIONAL_BOOLEAN_SCHEMA, false, BOOLEAN_SCHEMA);
            Assert.fail("Cannot project optional schema to schema with no default value.");
        } catch (DataException e) {
            // expected
        }
        try {
            SchemaProjector.project(OPTIONAL_BYTES_SCHEMA, bytes, BYTES_SCHEMA);
            Assert.fail("Cannot project optional schema to schema with no default value.");
        } catch (DataException e) {
            // expected
        }
        try {
            SchemaProjector.project(OPTIONAL_STRING_SCHEMA, "abc", STRING_SCHEMA);
            Assert.fail("Cannot project optional schema to schema with no default value.");
        } catch (DataException e) {
            // expected
        }
    }

    @Test
    public void testNumericTypeProjection() {
        Schema[] promotableSchemas = new Schema[]{ INT8_SCHEMA, INT16_SCHEMA, INT32_SCHEMA, INT64_SCHEMA, FLOAT32_SCHEMA, FLOAT64_SCHEMA };
        Schema[] promotableOptionalSchemas = new Schema[]{ OPTIONAL_INT8_SCHEMA, OPTIONAL_INT16_SCHEMA, OPTIONAL_INT32_SCHEMA, OPTIONAL_INT64_SCHEMA, OPTIONAL_FLOAT32_SCHEMA, OPTIONAL_FLOAT64_SCHEMA };
        Object[] values = new Object[]{ ((byte) (127)), ((short) (255)), 32767, 327890L, 1.2F, 1.2345 };
        Map<Object, List<?>> expectedProjected = new HashMap<>();
        expectedProjected.put(values[0], Arrays.asList(((byte) (127)), ((short) (127)), 127, 127L, 127.0F, 127.0));
        expectedProjected.put(values[1], Arrays.asList(((short) (255)), 255, 255L, 255.0F, 255.0));
        expectedProjected.put(values[2], Arrays.asList(32767, 32767L, 32767.0F, 32767.0));
        expectedProjected.put(values[3], Arrays.asList(327890L, 327890.0F, 327890.0));
        expectedProjected.put(values[4], Arrays.asList(1.2F, 1.2));
        expectedProjected.put(values[5], Arrays.asList(1.2345));
        Object promoted;
        for (int i = 0; i < (promotableSchemas.length); ++i) {
            Schema source = promotableSchemas[i];
            List<?> expected = expectedProjected.get(values[i]);
            for (int j = i; j < (promotableSchemas.length); ++j) {
                Schema target = promotableSchemas[j];
                promoted = SchemaProjector.project(source, values[i], target);
                if ((target.type()) == (Type.FLOAT64)) {
                    Assert.assertEquals(((Double) (expected.get((j - i)))), ((double) (promoted)), 1.0E-6);
                } else {
                    Assert.assertEquals(expected.get((j - i)), promoted);
                }
            }
            for (int j = i; j < (promotableOptionalSchemas.length); ++j) {
                Schema target = promotableOptionalSchemas[j];
                promoted = SchemaProjector.project(source, values[i], target);
                if ((target.type()) == (Type.FLOAT64)) {
                    Assert.assertEquals(((Double) (expected.get((j - i)))), ((double) (promoted)), 1.0E-6);
                } else {
                    Assert.assertEquals(expected.get((j - i)), promoted);
                }
            }
        }
        for (int i = 0; i < (promotableOptionalSchemas.length); ++i) {
            Schema source = promotableSchemas[i];
            List<?> expected = expectedProjected.get(values[i]);
            for (int j = i; j < (promotableOptionalSchemas.length); ++j) {
                Schema target = promotableOptionalSchemas[j];
                promoted = SchemaProjector.project(source, values[i], target);
                if ((target.type()) == (Type.FLOAT64)) {
                    Assert.assertEquals(((Double) (expected.get((j - i)))), ((double) (promoted)), 1.0E-6);
                } else {
                    Assert.assertEquals(expected.get((j - i)), promoted);
                }
            }
        }
        Schema[] nonPromotableSchemas = new Schema[]{ BOOLEAN_SCHEMA, BYTES_SCHEMA, STRING_SCHEMA };
        for (Schema promotableSchema : promotableSchemas) {
            for (Schema nonPromotableSchema : nonPromotableSchemas) {
                Object dummy = new Object();
                try {
                    SchemaProjector.project(promotableSchema, dummy, nonPromotableSchema);
                    Assert.fail(((("Cannot promote " + (promotableSchema.type())) + " to ") + (nonPromotableSchema.type())));
                } catch (DataException e) {
                    // expected
                }
            }
        }
    }

    @Test
    public void testPrimitiveOptionalProjection() {
        verifyOptionalProjection(OPTIONAL_BOOLEAN_SCHEMA, BOOLEAN, false, true, false, true);
        verifyOptionalProjection(OPTIONAL_BOOLEAN_SCHEMA, BOOLEAN, false, true, false, false);
        byte[] bytes = new byte[]{ ((byte) (1)), ((byte) (2)) };
        byte[] defaultBytes = new byte[]{ ((byte) (3)), ((byte) (4)) };
        verifyOptionalProjection(OPTIONAL_BYTES_SCHEMA, BYTES, bytes, defaultBytes, bytes, true);
        verifyOptionalProjection(OPTIONAL_BYTES_SCHEMA, BYTES, bytes, defaultBytes, bytes, false);
        verifyOptionalProjection(OPTIONAL_STRING_SCHEMA, STRING, "abc", "def", "abc", true);
        verifyOptionalProjection(OPTIONAL_STRING_SCHEMA, STRING, "abc", "def", "abc", false);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT8, ((byte) (12)), ((byte) (127)), ((byte) (12)), true);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT8, ((byte) (12)), ((byte) (127)), ((byte) (12)), false);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT16, ((byte) (12)), ((short) (127)), ((short) (12)), true);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT16, ((byte) (12)), ((short) (127)), ((short) (12)), false);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT32, ((byte) (12)), 12789, 12, true);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT32, ((byte) (12)), 12789, 12, false);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT64, ((byte) (12)), 127890L, 12L, true);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, INT64, ((byte) (12)), 127890L, 12L, false);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, FLOAT32, ((byte) (12)), 3.45F, 12.0F, true);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, FLOAT32, ((byte) (12)), 3.45F, 12.0F, false);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, FLOAT64, ((byte) (12)), 3.4567, 12.0, true);
        verifyOptionalProjection(OPTIONAL_INT8_SCHEMA, FLOAT64, ((byte) (12)), 3.4567, 12.0, false);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, INT16, ((short) (12)), ((short) (127)), ((short) (12)), true);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, INT16, ((short) (12)), ((short) (127)), ((short) (12)), false);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, INT32, ((short) (12)), 12789, 12, true);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, INT32, ((short) (12)), 12789, 12, false);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, INT64, ((short) (12)), 127890L, 12L, true);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, INT64, ((short) (12)), 127890L, 12L, false);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, FLOAT32, ((short) (12)), 3.45F, 12.0F, true);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, FLOAT32, ((short) (12)), 3.45F, 12.0F, false);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, FLOAT64, ((short) (12)), 3.4567, 12.0, true);
        verifyOptionalProjection(OPTIONAL_INT16_SCHEMA, FLOAT64, ((short) (12)), 3.4567, 12.0, false);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, INT32, 12, 12789, 12, true);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, INT32, 12, 12789, 12, false);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, INT64, 12, 127890L, 12L, true);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, INT64, 12, 127890L, 12L, false);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, FLOAT32, 12, 3.45F, 12.0F, true);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, FLOAT32, 12, 3.45F, 12.0F, false);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, FLOAT64, 12, 3.4567, 12.0, true);
        verifyOptionalProjection(OPTIONAL_INT32_SCHEMA, FLOAT64, 12, 3.4567, 12.0, false);
        verifyOptionalProjection(OPTIONAL_INT64_SCHEMA, INT64, 12L, 127890L, 12L, true);
        verifyOptionalProjection(OPTIONAL_INT64_SCHEMA, INT64, 12L, 127890L, 12L, false);
        verifyOptionalProjection(OPTIONAL_INT64_SCHEMA, FLOAT32, 12L, 3.45F, 12.0F, true);
        verifyOptionalProjection(OPTIONAL_INT64_SCHEMA, FLOAT32, 12L, 3.45F, 12.0F, false);
        verifyOptionalProjection(OPTIONAL_INT64_SCHEMA, FLOAT64, 12L, 3.4567, 12.0, true);
        verifyOptionalProjection(OPTIONAL_INT64_SCHEMA, FLOAT64, 12L, 3.4567, 12.0, false);
        verifyOptionalProjection(OPTIONAL_FLOAT32_SCHEMA, FLOAT32, 12.345F, 3.45F, 12.345F, true);
        verifyOptionalProjection(OPTIONAL_FLOAT32_SCHEMA, FLOAT32, 12.345F, 3.45F, 12.345F, false);
        verifyOptionalProjection(OPTIONAL_FLOAT32_SCHEMA, FLOAT64, 12.345F, 3.4567, 12.345, true);
        verifyOptionalProjection(OPTIONAL_FLOAT32_SCHEMA, FLOAT64, 12.345F, 3.4567, 12.345, false);
        verifyOptionalProjection(OPTIONAL_FLOAT32_SCHEMA, FLOAT64, 12.345, 3.4567, 12.345, true);
        verifyOptionalProjection(OPTIONAL_FLOAT32_SCHEMA, FLOAT64, 12.345, 3.4567, 12.345, false);
    }

    @Test
    public void testStructAddField() {
        Schema source = SchemaBuilder.struct().field("field", INT32_SCHEMA).build();
        Struct sourceStruct = new Struct(source);
        sourceStruct.put("field", 1);
        Schema target = SchemaBuilder.struct().field("field", INT32_SCHEMA).field("field2", SchemaBuilder.int32().defaultValue(123).build()).build();
        Struct targetStruct = ((Struct) (SchemaProjector.project(source, sourceStruct, target)));
        Assert.assertEquals(1, ((int) (targetStruct.getInt32("field"))));
        Assert.assertEquals(123, ((int) (targetStruct.getInt32("field2"))));
        Schema incompatibleTargetSchema = SchemaBuilder.struct().field("field", INT32_SCHEMA).field("field2", INT32_SCHEMA).build();
        try {
            SchemaProjector.project(source, sourceStruct, incompatibleTargetSchema);
            Assert.fail("Incompatible schema.");
        } catch (DataException e) {
            // expected
        }
    }

    @Test
    public void testStructRemoveField() {
        Schema source = SchemaBuilder.struct().field("field", INT32_SCHEMA).field("field2", INT32_SCHEMA).build();
        Struct sourceStruct = new Struct(source);
        sourceStruct.put("field", 1);
        sourceStruct.put("field2", 234);
        Schema target = SchemaBuilder.struct().field("field", INT32_SCHEMA).build();
        Struct targetStruct = ((Struct) (SchemaProjector.project(source, sourceStruct, target)));
        Assert.assertEquals(1, targetStruct.get("field"));
        try {
            targetStruct.get("field2");
            Assert.fail("field2 is not part of the projected struct");
        } catch (DataException e) {
            // expected
        }
    }

    @Test
    public void testStructDefaultValue() {
        Schema source = SchemaBuilder.struct().optional().field("field", INT32_SCHEMA).field("field2", INT32_SCHEMA).build();
        SchemaBuilder builder = SchemaBuilder.struct().field("field", INT32_SCHEMA).field("field2", INT32_SCHEMA);
        Struct defaultStruct = put("field", 12).put("field2", 345);
        builder.defaultValue(defaultStruct);
        Schema target = builder.build();
        Object projected = SchemaProjector.project(source, null, target);
        Assert.assertEquals(defaultStruct, projected);
        Struct sourceStruct = put("field", 45).put("field2", 678);
        Struct targetStruct = ((Struct) (SchemaProjector.project(source, sourceStruct, target)));
        Assert.assertEquals(sourceStruct.get("field"), targetStruct.get("field"));
        Assert.assertEquals(sourceStruct.get("field2"), targetStruct.get("field2"));
    }

    @Test
    public void testNestedSchemaProjection() {
        Schema sourceFlatSchema = SchemaBuilder.struct().field("field", INT32_SCHEMA).build();
        Schema targetFlatSchema = SchemaBuilder.struct().field("field", INT32_SCHEMA).field("field2", SchemaBuilder.int32().defaultValue(123).build()).build();
        Schema sourceNestedSchema = SchemaBuilder.struct().field("first", INT32_SCHEMA).field("second", STRING_SCHEMA).field("array", SchemaBuilder.array(INT32_SCHEMA).build()).field("map", SchemaBuilder.map(INT32_SCHEMA, STRING_SCHEMA).build()).field("nested", sourceFlatSchema).build();
        Schema targetNestedSchema = SchemaBuilder.struct().field("first", INT32_SCHEMA).field("second", STRING_SCHEMA).field("array", SchemaBuilder.array(INT32_SCHEMA).build()).field("map", SchemaBuilder.map(INT32_SCHEMA, STRING_SCHEMA).build()).field("nested", targetFlatSchema).build();
        Struct sourceFlatStruct = new Struct(sourceFlatSchema);
        sourceFlatStruct.put("field", 113);
        Struct sourceNestedStruct = new Struct(sourceNestedSchema);
        sourceNestedStruct.put("first", 1);
        sourceNestedStruct.put("second", "abc");
        sourceNestedStruct.put("array", Arrays.asList(1, 2));
        sourceNestedStruct.put("map", Collections.singletonMap(5, "def"));
        sourceNestedStruct.put("nested", sourceFlatStruct);
        Struct targetNestedStruct = ((Struct) (SchemaProjector.project(sourceNestedSchema, sourceNestedStruct, targetNestedSchema)));
        Assert.assertEquals(1, targetNestedStruct.get("first"));
        Assert.assertEquals("abc", targetNestedStruct.get("second"));
        Assert.assertEquals(Arrays.asList(1, 2), targetNestedStruct.get("array"));
        Assert.assertEquals(Collections.singletonMap(5, "def"), targetNestedStruct.get("map"));
        Struct projectedStruct = ((Struct) (targetNestedStruct.get("nested")));
        Assert.assertEquals(113, projectedStruct.get("field"));
        Assert.assertEquals(123, projectedStruct.get("field2"));
    }

    @Test
    public void testLogicalTypeProjection() {
        Schema[] logicalTypeSchemas = new Schema[]{ Decimal.schema(2), SCHEMA, Time.SCHEMA, Timestamp.SCHEMA };
        Object projected;
        BigDecimal testDecimal = new BigDecimal(new BigInteger("156"), 2);
        projected = SchemaProjector.project(Decimal.schema(2), testDecimal, Decimal.schema(2));
        Assert.assertEquals(testDecimal, projected);
        projected = SchemaProjector.project(SCHEMA, 1000, SCHEMA);
        Assert.assertEquals(1000, projected);
        projected = SchemaProjector.project(Time.SCHEMA, 231, Time.SCHEMA);
        Assert.assertEquals(231, projected);
        projected = SchemaProjector.project(Timestamp.SCHEMA, 34567L, Timestamp.SCHEMA);
        Assert.assertEquals(34567L, projected);
        java.util.Date date = new java.util.Date();
        projected = SchemaProjector.project(SCHEMA, date, SCHEMA);
        Assert.assertEquals(date, projected);
        projected = SchemaProjector.project(Time.SCHEMA, date, Time.SCHEMA);
        Assert.assertEquals(date, projected);
        projected = SchemaProjector.project(Timestamp.SCHEMA, date, Timestamp.SCHEMA);
        Assert.assertEquals(date, projected);
        Schema namedSchema = SchemaBuilder.int32().name("invalidLogicalTypeName").build();
        for (Schema logicalTypeSchema : logicalTypeSchemas) {
            try {
                SchemaProjector.project(logicalTypeSchema, null, BOOLEAN_SCHEMA);
                Assert.fail("Cannot project logical types to non-logical types.");
            } catch (SchemaProjectorException e) {
                // expected
            }
            try {
                SchemaProjector.project(logicalTypeSchema, null, namedSchema);
                Assert.fail("Reader name is not a valid logical type name.");
            } catch (SchemaProjectorException e) {
                // expected
            }
            try {
                SchemaProjector.project(BOOLEAN_SCHEMA, null, logicalTypeSchema);
                Assert.fail("Cannot project non-logical types to logical types.");
            } catch (SchemaProjectorException e) {
                // expected
            }
        }
    }

    @Test
    public void testArrayProjection() {
        Schema source = SchemaBuilder.array(INT32_SCHEMA).build();
        Object projected = SchemaProjector.project(source, Arrays.asList(1, 2, 3), source);
        Assert.assertEquals(Arrays.asList(1, 2, 3), projected);
        Schema optionalSource = SchemaBuilder.array(INT32_SCHEMA).optional().build();
        Schema target = SchemaBuilder.array(INT32_SCHEMA).defaultValue(Arrays.asList(1, 2, 3)).build();
        projected = SchemaProjector.project(optionalSource, Arrays.asList(4, 5), target);
        Assert.assertEquals(Arrays.asList(4, 5), projected);
        projected = SchemaProjector.project(optionalSource, null, target);
        Assert.assertEquals(Arrays.asList(1, 2, 3), projected);
        Schema promotedTarget = SchemaBuilder.array(INT64_SCHEMA).defaultValue(Arrays.asList(1L, 2L, 3L)).build();
        projected = SchemaProjector.project(optionalSource, Arrays.asList(4, 5), promotedTarget);
        List<Long> expectedProjected = Arrays.asList(4L, 5L);
        Assert.assertEquals(expectedProjected, projected);
        projected = SchemaProjector.project(optionalSource, null, promotedTarget);
        Assert.assertEquals(Arrays.asList(1L, 2L, 3L), projected);
        Schema noDefaultValueTarget = SchemaBuilder.array(INT32_SCHEMA).build();
        try {
            SchemaProjector.project(optionalSource, null, noDefaultValueTarget);
            Assert.fail("Target schema does not provide a default value.");
        } catch (SchemaProjectorException e) {
            // expected
        }
        Schema nonPromotableTarget = SchemaBuilder.array(BOOLEAN_SCHEMA).build();
        try {
            SchemaProjector.project(optionalSource, null, nonPromotableTarget);
            Assert.fail("Neither source type matches target type nor source type can be promoted to target type");
        } catch (SchemaProjectorException e) {
            // expected
        }
    }

    @Test
    public void testMapProjection() {
        Schema source = SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA).optional().build();
        Schema target = SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA).defaultValue(Collections.singletonMap(1, 2)).build();
        Object projected = SchemaProjector.project(source, Collections.singletonMap(3, 4), target);
        Assert.assertEquals(Collections.singletonMap(3, 4), projected);
        projected = SchemaProjector.project(source, null, target);
        Assert.assertEquals(Collections.singletonMap(1, 2), projected);
        Schema promotedTarget = SchemaBuilder.map(INT64_SCHEMA, FLOAT32_SCHEMA).defaultValue(Collections.singletonMap(3L, 4.5F)).build();
        projected = SchemaProjector.project(source, Collections.singletonMap(3, 4), promotedTarget);
        Assert.assertEquals(Collections.singletonMap(3L, 4.0F), projected);
        projected = SchemaProjector.project(source, null, promotedTarget);
        Assert.assertEquals(Collections.singletonMap(3L, 4.5F), projected);
        Schema noDefaultValueTarget = SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA).build();
        try {
            SchemaProjector.project(source, null, noDefaultValueTarget);
            Assert.fail("Reader does not provide a default value.");
        } catch (SchemaProjectorException e) {
            // expected
        }
        Schema nonPromotableTarget = SchemaBuilder.map(BOOLEAN_SCHEMA, STRING_SCHEMA).build();
        try {
            SchemaProjector.project(source, null, nonPromotableTarget);
            Assert.fail("Neither source type matches target type nor source type can be promoted to target type");
        } catch (SchemaProjectorException e) {
            // expected
        }
    }

    @Test
    public void testMaybeCompatible() {
        Schema source = SchemaBuilder.int32().name("source").build();
        Schema target = SchemaBuilder.int32().name("target").build();
        try {
            SchemaProjector.project(source, 12, target);
            Assert.fail("Source name and target name mismatch.");
        } catch (SchemaProjectorException e) {
            // expected
        }
        Schema targetWithParameters = SchemaBuilder.int32().parameters(Collections.singletonMap("key", "value"));
        try {
            SchemaProjector.project(source, 34, targetWithParameters);
            Assert.fail("Source parameters and target parameters mismatch.");
        } catch (SchemaProjectorException e) {
            // expected
        }
    }

    @Test
    public void testProjectMissingDefaultValuedStructField() {
        final Schema source = SchemaBuilder.struct().build();
        final Schema target = SchemaBuilder.struct().field("id", SchemaBuilder.int64().defaultValue(42L).build()).build();
        Assert.assertEquals(42L, ((long) (getInt64("id"))));
    }

    @Test
    public void testProjectMissingOptionalStructField() {
        final Schema source = SchemaBuilder.struct().build();
        final Schema target = SchemaBuilder.struct().field("id", SchemaBuilder.OPTIONAL_INT64_SCHEMA).build();
        Assert.assertEquals(null, getInt64("id"));
    }

    @Test(expected = SchemaProjectorException.class)
    public void testProjectMissingRequiredField() {
        final Schema source = SchemaBuilder.struct().build();
        final Schema target = SchemaBuilder.struct().field("id", SchemaBuilder.INT64_SCHEMA).build();
        SchemaProjector.project(source, new Struct(source), target);
    }
}

