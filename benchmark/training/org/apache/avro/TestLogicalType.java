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
package org.apache.avro;


import LogicalTypes.Decimal;
import Schema.Type.BOOLEAN;
import Schema.Type.BYTES;
import Schema.Type.DOUBLE;
import Schema.Type.FLOAT;
import Schema.Type.INT;
import Schema.Type.LONG;
import Schema.Type.NULL;
import Schema.Type.STRING;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class TestLogicalType {
    @Test
    public void testDecimalFromSchema() {
        Schema schema = Schema.createFixed("aFixed", null, null, 4);
        schema.addProp("logicalType", "decimal");
        schema.addProp("precision", 9);
        schema.addProp("scale", 2);
        LogicalType logicalType = LogicalTypes.fromSchemaIgnoreInvalid(schema);
        Assert.assertTrue("Should be a Decimal", (logicalType instanceof LogicalTypes.Decimal));
        LogicalTypes.Decimal decimal = ((LogicalTypes.Decimal) (logicalType));
        Assert.assertEquals("Should have correct precision", 9, decimal.getPrecision());
        Assert.assertEquals("Should have correct scale", 2, decimal.getScale());
    }

    @Test
    public void testInvalidLogicalTypeIgnored() {
        final Schema schema = Schema.createFixed("aFixed", null, null, 2);
        schema.addProp("logicalType", "decimal");
        schema.addProp("precision", 9);
        schema.addProp("scale", 2);
        Assert.assertNull("Should ignore invalid logical type", LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testDecimalWithNonByteArrayTypes() {
        final LogicalType decimal = LogicalTypes.decimal(5, 2);
        // test simple types
        Schema[] nonBytes = new Schema[]{ Schema.createRecord("Record", null, null, false), Schema.createArray(Schema.create(BYTES)), Schema.createMap(Schema.create(BYTES)), Schema.createEnum("Enum", null, null, Arrays.asList("a", "b")), Schema.createUnion(Arrays.asList(Schema.create(BYTES), Schema.createFixed("fixed", null, null, 4))), Schema.create(BOOLEAN), Schema.create(INT), Schema.create(LONG), Schema.create(FLOAT), Schema.create(DOUBLE), Schema.create(NULL), Schema.create(STRING) };
        for (final Schema schema : nonBytes) {
            TestLogicalType.assertThrows(("Should reject type: " + (schema.getType())), IllegalArgumentException.class, "Logical type decimal must be backed by fixed or bytes", new Callable() {
                @Override
                public Object call() throws Exception {
                    decimal.addToSchema(schema);
                    return null;
                }
            });
        }
    }

    @Test
    public void testUnknownFromJsonNode() {
        Schema schema = Schema.create(STRING);
        schema.addProp("logicalType", "unknown");
        schema.addProp("someProperty", 34);
        LogicalType logicalType = LogicalTypes.fromSchemaIgnoreInvalid(schema);
        Assert.assertNull("Should not return a LogicalType instance", logicalType);
    }

    @Test
    public void testDecimalBytesHasNoPrecisionLimit() {
        Schema schema = Schema.create(BYTES);
        // precision is not limited for bytes
        LogicalTypes.decimal(Integer.MAX_VALUE).addToSchema(schema);
        Assert.assertEquals("Precision should be an Integer.MAX_VALUE", Integer.MAX_VALUE, getPrecision());
    }

    @Test
    public void testDecimalFixedPrecisionLimit() {
        // 4 bytes can hold up to 9 digits of precision
        final Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        TestLogicalType.assertThrows("Should reject precision", IllegalArgumentException.class, "fixed(4) cannot store 10 digits (max 9)", new Callable() {
            @Override
            public Object call() throws Exception {
                LogicalTypes.decimal(10).addToSchema(schema);
                return null;
            }
        });
        Assert.assertNull("Invalid logical type should not be set on schema", LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testDecimalFailsWithZeroPrecision() {
        final Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        TestLogicalType.assertThrows("Should reject precision", IllegalArgumentException.class, "Invalid decimal precision: 0 (must be positive)", new Callable() {
            @Override
            public Object call() throws Exception {
                LogicalTypes.decimal(0).addToSchema(schema);
                return null;
            }
        });
        Assert.assertNull("Invalid logical type should not be set on schema", LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testDecimalFailsWithNegativePrecision() {
        final Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        TestLogicalType.assertThrows("Should reject precision", IllegalArgumentException.class, "Invalid decimal precision: -9 (must be positive)", new Callable() {
            @Override
            public Object call() throws Exception {
                LogicalTypes.decimal((-9)).addToSchema(schema);
                return null;
            }
        });
        Assert.assertNull("Invalid logical type should not be set on schema", LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testDecimalScaleBoundedByPrecision() {
        final Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        TestLogicalType.assertThrows("Should reject precision", IllegalArgumentException.class, "Invalid decimal scale: 10 (greater than precision: 9)", new Callable() {
            @Override
            public Object call() throws Exception {
                LogicalTypes.decimal(9, 10).addToSchema(schema);
                return null;
            }
        });
        Assert.assertNull("Invalid logical type should not be set on schema", LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testDecimalFailsWithNegativeScale() {
        final Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        TestLogicalType.assertThrows("Should reject precision", IllegalArgumentException.class, "Invalid decimal scale: -2 (must be positive)", new Callable() {
            @Override
            public Object call() throws Exception {
                LogicalTypes.decimal(9, (-2)).addToSchema(schema);
                return null;
            }
        });
        Assert.assertNull("Invalid logical type should not be set on schema", LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testSchemaRejectsSecondLogicalType() {
        final Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        LogicalTypes.decimal(9).addToSchema(schema);
        TestLogicalType.assertThrows("Should reject second logical type", AvroRuntimeException.class, "Can't overwrite property: scale", new Callable() {
            @Override
            public Object call() throws Exception {
                LogicalTypes.decimal(9, 2).addToSchema(schema);
                return null;
            }
        });
        Assert.assertEquals("First logical type should still be set on schema", LogicalTypes.decimal(9), LogicalTypes.fromSchemaIgnoreInvalid(schema));
    }

    @Test
    public void testDecimalDefaultScale() {
        Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        // 4 bytes can hold up to 9 digits of precision
        LogicalTypes.decimal(9).addToSchema(schema);
        Assert.assertEquals("Scale should be a 0", 0, getScale());
    }

    @Test
    public void testFixedDecimalToFromJson() {
        Schema schema = Schema.createFixed("aDecimal", null, null, 4);
        LogicalTypes.decimal(9, 2).addToSchema(schema);
        Schema parsed = new Schema.Parser().parse(schema.toString(true));
        Assert.assertEquals("Constructed and parsed schemas should match", schema, parsed);
    }

    @Test
    public void testBytesDecimalToFromJson() {
        Schema schema = Schema.create(BYTES);
        LogicalTypes.decimal(9, 2).addToSchema(schema);
        Schema parsed = new Schema.Parser().parse(schema.toString(true));
        Assert.assertEquals("Constructed and parsed schemas should match", schema, parsed);
    }

    @Test
    public void testLogicalTypeEquals() {
        LogicalTypes.Decimal decimal90 = LogicalTypes.decimal(9);
        LogicalTypes.Decimal decimal80 = LogicalTypes.decimal(8);
        LogicalTypes.Decimal decimal92 = LogicalTypes.decimal(9, 2);
        TestLogicalType.assertEqualsTrue("Same decimal", LogicalTypes.decimal(9, 0), decimal90);
        TestLogicalType.assertEqualsTrue("Same decimal", LogicalTypes.decimal(8, 0), decimal80);
        TestLogicalType.assertEqualsTrue("Same decimal", LogicalTypes.decimal(9, 2), decimal92);
        TestLogicalType.assertEqualsFalse("Different logical type", LogicalTypes.uuid(), decimal90);
        TestLogicalType.assertEqualsFalse("Different precision", decimal90, decimal80);
        TestLogicalType.assertEqualsFalse("Different scale", decimal90, decimal92);
    }

    @Test
    public void testLogicalTypeInSchemaEquals() {
        Schema schema1 = Schema.createFixed("aDecimal", null, null, 4);
        Schema schema2 = Schema.createFixed("aDecimal", null, null, 4);
        Schema schema3 = Schema.createFixed("aDecimal", null, null, 4);
        Assert.assertNotSame(schema1, schema2);
        Assert.assertNotSame(schema1, schema3);
        TestLogicalType.assertEqualsTrue("No logical types", schema1, schema2);
        TestLogicalType.assertEqualsTrue("No logical types", schema1, schema3);
        LogicalTypes.decimal(9).addToSchema(schema1);
        TestLogicalType.assertEqualsFalse("Two has no logical type", schema1, schema2);
        LogicalTypes.decimal(9).addToSchema(schema2);
        TestLogicalType.assertEqualsTrue("Same logical types", schema1, schema2);
        LogicalTypes.decimal(9, 2).addToSchema(schema3);
        TestLogicalType.assertEqualsFalse("Different logical type", schema1, schema3);
    }
}

