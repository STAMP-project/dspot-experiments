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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.avro;


import ReflectData.AllowNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.avro.reflect.ReflectData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestSchemaValidation {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Collection of reader/writer schema pair that are compatible.
     */
    public static final List<TestSchemas.ReaderWriter> COMPATIBLE_READER_WRITER_TEST_CASES = // Avro spec says INT/LONG can be promoted to FLOAT/DOUBLE.
    // This is arguable as this causes a loss of precision.
    // String-to/from-bytes, introduced in Avro 1.7.7
    // Tests involving unions:
    // Readers capable of reading all branches of a union are compatible
    // Special case of singleton unions:
    // Tests involving records:
    // The SchemaValidator, unlike the SchemaCompatibility class, cannot cope with recursive schemas
    // See AVRO-2074
    // new ReaderWriter(INT_LIST_RECORD, INT_LIST_RECORD),
    // new ReaderWriter(LONG_LIST_RECORD, LONG_LIST_RECORD),
    // new ReaderWriter(LONG_LIST_RECORD, INT_LIST_RECORD),
    TestSchemas.list(new TestSchemas.ReaderWriter(TestSchemas.BOOLEAN_SCHEMA, TestSchemas.BOOLEAN_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.FLOAT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_SCHEMA, TestSchemas.STRING_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_SCHEMA, TestSchemas.BYTES_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_ARRAY_SCHEMA, TestSchemas.INT_ARRAY_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_ARRAY_SCHEMA, TestSchemas.INT_ARRAY_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_MAP_SCHEMA, TestSchemas.INT_MAP_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_MAP_SCHEMA, TestSchemas.INT_MAP_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_AB_SCHEMA, TestSchemas.ENUM1_AB_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_ABC_SCHEMA, TestSchemas.ENUM1_AB_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_SCHEMA, TestSchemas.BYTES_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_SCHEMA, TestSchemas.STRING_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_STRING_UNION_SCHEMA, TestSchemas.STRING_INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_UNION_SCHEMA, TestSchemas.BYTES_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_UNION_SCHEMA, TestSchemas.STRING_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.INT_LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.INT_LONG_FLOAT_DOUBLE_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_RECORD1, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_RECORD1, TestSchemas.A_DINT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.A_DINT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_LONG_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.A_INT_B_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_RECORD1, TestSchemas.A_INT_B_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_B_DINT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_B_DINT_RECORD1, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_B_DINT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_B_INT_RECORD1, TestSchemas.A_DINT_B_DINT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.NULL_SCHEMA, TestSchemas.NULL_SCHEMA));

    /**
     * Collection of reader/writer schema pair that are incompatible.
     */
    public static final List<TestSchemas.ReaderWriter> INCOMPATIBLE_READER_WRITER_TEST_CASES = // Tests involving unions:
    TestSchemas.list(new TestSchemas.ReaderWriter(TestSchemas.NULL_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.NULL_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BOOLEAN_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.NULL_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.BOOLEAN_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.FLOAT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.DOUBLE_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.FLOAT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.DOUBLE_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.DOUBLE_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_SCHEMA, TestSchemas.BOOLEAN_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_SCHEMA, TestSchemas.NULL_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_ARRAY_SCHEMA, TestSchemas.LONG_ARRAY_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_MAP_SCHEMA, TestSchemas.INT_ARRAY_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_ARRAY_SCHEMA, TestSchemas.INT_MAP_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_MAP_SCHEMA, TestSchemas.LONG_MAP_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_AB_SCHEMA, TestSchemas.ENUM1_ABC_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_BC_SCHEMA, TestSchemas.ENUM1_ABC_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_AB_SCHEMA, TestSchemas.ENUM2_AB_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.ENUM2_AB_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM2_AB_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.INT_STRING_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_UNION_SCHEMA, TestSchemas.INT_STRING_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.INT_LONG_FLOAT_DOUBLE_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_RECORD2, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_B_DINT_RECORD1, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.INT_LIST_RECORD, TestSchemas.LONG_LIST_RECORD), new TestSchemas.ReaderWriter(TestSchemas.NULL_SCHEMA, TestSchemas.INT_SCHEMA));

    SchemaValidatorBuilder builder = new SchemaValidatorBuilder();

    Schema rec = SchemaBuilder.record("test.Rec").fields().name("a").type().intType().intDefault(1).name("b").type().longType().noDefault().endRecord();

    Schema rec2 = SchemaBuilder.record("test.Rec").fields().name("a").type().intType().intDefault(1).name("b").type().longType().noDefault().name("c").type().intType().intDefault(0).endRecord();

    Schema rec3 = SchemaBuilder.record("test.Rec").fields().name("b").type().longType().noDefault().name("c").type().intType().intDefault(0).endRecord();

    Schema rec4 = SchemaBuilder.record("test.Rec").fields().name("b").type().longType().noDefault().name("c").type().intType().noDefault().endRecord();

    Schema rec5 = // different type from original
    SchemaBuilder.record("test.Rec").fields().name("a").type().stringType().stringDefault("").name("b").type().longType().noDefault().name("c").type().intType().intDefault(0).endRecord();

    @Test
    public void testAllTypes() throws SchemaValidationException {
        Schema s = SchemaBuilder.record("r").fields().requiredBoolean("boolF").requiredInt("intF").requiredLong("longF").requiredFloat("floatF").requiredDouble("doubleF").requiredString("stringF").requiredBytes("bytesF").name("fixedF1").type().fixed("F1").size(1).noDefault().name("enumF").type().enumeration("E1").symbols("S").noDefault().name("mapF").type().map().values().stringType().noDefault().name("arrayF").type().array().items().stringType().noDefault().name("recordF").type().record("inner").fields().name("f").type().intType().noDefault().endRecord().noDefault().optionalBoolean("boolO").endRecord();
        testValidatorPasses(builder.mutualReadStrategy().validateLatest(), s, s);
    }

    @Test
    public void testReadOnePrior() throws SchemaValidationException {
        testValidatorPasses(builder.canReadStrategy().validateLatest(), rec3, rec);
        testValidatorPasses(builder.canReadStrategy().validateLatest(), rec5, rec3);
        testValidatorFails(builder.canReadStrategy().validateLatest(), rec4, rec);
    }

    @Test
    public void testReadAllPrior() throws SchemaValidationException {
        testValidatorPasses(builder.canReadStrategy().validateAll(), rec3, rec, rec2);
        testValidatorFails(builder.canReadStrategy().validateAll(), rec4, rec, rec2, rec3);
        testValidatorFails(builder.canReadStrategy().validateAll(), rec5, rec, rec2, rec3);
    }

    @Test
    public void testOnePriorCanRead() throws SchemaValidationException {
        testValidatorPasses(builder.canBeReadStrategy().validateLatest(), rec, rec3);
        testValidatorFails(builder.canBeReadStrategy().validateLatest(), rec, rec4);
    }

    @Test
    public void testAllPriorCanRead() throws SchemaValidationException {
        testValidatorPasses(builder.canBeReadStrategy().validateAll(), rec, rec3, rec2);
        testValidatorFails(builder.canBeReadStrategy().validateAll(), rec, rec4, rec3, rec2);
    }

    @Test
    public void testOnePriorCompatible() throws SchemaValidationException {
        testValidatorPasses(builder.mutualReadStrategy().validateLatest(), rec, rec3);
        testValidatorFails(builder.mutualReadStrategy().validateLatest(), rec, rec4);
    }

    @Test
    public void testAllPriorCompatible() throws SchemaValidationException {
        testValidatorPasses(builder.mutualReadStrategy().validateAll(), rec, rec3, rec2);
        testValidatorFails(builder.mutualReadStrategy().validateAll(), rec, rec4, rec3, rec2);
    }

    @Test(expected = AvroRuntimeException.class)
    public void testInvalidBuild() {
        builder.strategy(null).validateAll();
    }

    public static class Point {
        double x;

        double y;
    }

    public static class Circle {
        TestSchemaValidation.Point center;

        double radius;
    }

    public static final Schema circleSchema = SchemaBuilder.record("Circle").fields().name("center").type().record("Point").fields().requiredDouble("x").requiredDouble("y").endRecord().noDefault().requiredDouble("radius").endRecord();

    public static final Schema circleSchemaDifferentNames = SchemaBuilder.record("crcl").fields().name("center").type().record("pt").fields().requiredDouble("x").requiredDouble("y").endRecord().noDefault().requiredDouble("radius").endRecord();

    @Test
    public void testReflectMatchStructure() throws SchemaValidationException {
        testValidatorPasses(builder.canBeReadStrategy().validateAll(), TestSchemaValidation.circleSchemaDifferentNames, ReflectData.get().getSchema(TestSchemaValidation.Circle.class));
    }

    @Test
    public void testReflectWithAllowNullMatchStructure() throws SchemaValidationException {
        testValidatorPasses(builder.canBeReadStrategy().validateAll(), TestSchemaValidation.circleSchemaDifferentNames, AllowNull.get().getSchema(TestSchemaValidation.Circle.class));
    }

    @Test
    public void testUnionWithIncompatibleElements() throws SchemaValidationException {
        Schema union1 = Schema.createUnion(Arrays.asList(rec));
        Schema union2 = Schema.createUnion(Arrays.asList(rec4));
        testValidatorFails(builder.canReadStrategy().validateAll(), union2, union1);
    }

    @Test
    public void testUnionWithCompatibleElements() throws SchemaValidationException {
        Schema union1 = Schema.createUnion(Arrays.asList(rec));
        Schema union2 = Schema.createUnion(Arrays.asList(rec3));
        testValidatorPasses(builder.canReadStrategy().validateAll(), union2, union1);
    }

    @Test
    public void testSchemaCompatibilitySuccesses() throws SchemaValidationException {
        // float-union-to-int/long-union does not work...
        // and neither does recursive types
        for (TestSchemas.ReaderWriter tc : TestSchemaValidation.COMPATIBLE_READER_WRITER_TEST_CASES) {
            testValidatorPasses(builder.canReadStrategy().validateAll(), tc.getReader(), tc.getWriter());
        }
    }

    @Test
    public void testSchemaCompatibilityFailures() throws SchemaValidationException {
        for (TestSchemas.ReaderWriter tc : TestSchemaValidation.INCOMPATIBLE_READER_WRITER_TEST_CASES) {
            Schema reader = tc.getReader();
            Schema writer = tc.getWriter();
            expectedException.expect(SchemaValidationException.class);
            expectedException.expectMessage(("Unable to read schema: \n" + (writer.toString())));
            SchemaValidator validator = builder.canReadStrategy().validateAll();
            validator.validate(reader, Collections.singleton(writer));
        }
    }

    public static final Schema recursiveSchema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Node\",\"namespace\":\"avro\",\"fields\":[{\"name\":\"value\",\"type\":[\"null\",\"Node\"],\"default\":null}]}");

    /**
     * Unit test to verify that recursive schemas can be validated.
     * See AVRO-2122.
     */
    @Test
    public void testRecursiveSchemaValidation() throws SchemaValidationException {
        // before AVRO-2122, this would cause a StackOverflowError
        final SchemaValidator backwardValidator = builder.canReadStrategy().validateLatest();
        backwardValidator.validate(TestSchemaValidation.recursiveSchema, Arrays.asList(TestSchemaValidation.recursiveSchema));
    }
}

