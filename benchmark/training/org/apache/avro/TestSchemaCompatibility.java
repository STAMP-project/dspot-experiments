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


import Schema.Field;
import Schema.Type.STRING;
import SchemaCompatibility.SchemaCompatibilityResult;
import SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
import SchemaCompatibility.SchemaPairCompatibility;
import SchemaCompatibilityType.COMPATIBLE;
import SchemaIncompatibilityType.READER_FIELD_MISSING_DEFAULT_VALUE;
import SchemaIncompatibilityType.TYPE_MISMATCH;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.avro.SchemaCompatibility.SchemaCompatibility.READER_WRITER_COMPATIBLE_MESSAGE;


/**
 * Unit-tests for SchemaCompatibility.
 */
public class TestSchemaCompatibility {
    private static final Logger LOG = LoggerFactory.getLogger(TestSchemaCompatibility.class);

    // -----------------------------------------------------------------------------------------------
    private static final Schema WRITER_SCHEMA = Schema.createRecord(TestSchemas.list(new Schema.Field("oldfield1", TestSchemas.INT_SCHEMA, null, null), new Schema.Field("oldfield2", TestSchemas.STRING_SCHEMA, null, null)));

    @Test
    public void testValidateSchemaPairMissingField() throws Exception {
        final List<Schema.Field> readerFields = TestSchemas.list(new Schema.Field("oldfield1", TestSchemas.INT_SCHEMA, null, null));
        final Schema reader = Schema.createRecord(readerFields);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility expectedResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.compatible(), reader, TestSchemaCompatibility.WRITER_SCHEMA, READER_WRITER_COMPATIBLE_MESSAGE);
        // Test omitting a field.
        Assert.assertEquals(expectedResult, checkReaderWriterCompatibility(reader, TestSchemaCompatibility.WRITER_SCHEMA));
    }

    @Test
    public void testValidateSchemaPairMissingSecondField() throws Exception {
        final List<Schema.Field> readerFields = TestSchemas.list(new Schema.Field("oldfield2", TestSchemas.STRING_SCHEMA, null, null));
        final Schema reader = Schema.createRecord(readerFields);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility expectedResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.compatible(), reader, TestSchemaCompatibility.WRITER_SCHEMA, READER_WRITER_COMPATIBLE_MESSAGE);
        // Test omitting other field.
        Assert.assertEquals(expectedResult, checkReaderWriterCompatibility(reader, TestSchemaCompatibility.WRITER_SCHEMA));
    }

    @Test
    public void testValidateSchemaPairAllFields() throws Exception {
        final List<Schema.Field> readerFields = TestSchemas.list(new Schema.Field("oldfield1", TestSchemas.INT_SCHEMA, null, null), new Schema.Field("oldfield2", TestSchemas.STRING_SCHEMA, null, null));
        final Schema reader = Schema.createRecord(readerFields);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility expectedResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.compatible(), reader, TestSchemaCompatibility.WRITER_SCHEMA, READER_WRITER_COMPATIBLE_MESSAGE);
        // Test with all fields.
        Assert.assertEquals(expectedResult, checkReaderWriterCompatibility(reader, TestSchemaCompatibility.WRITER_SCHEMA));
    }

    @Test
    public void testValidateSchemaNewFieldWithDefault() throws Exception {
        final List<Schema.Field> readerFields = TestSchemas.list(new Schema.Field("oldfield1", TestSchemas.INT_SCHEMA, null, null), new Schema.Field("newfield1", TestSchemas.INT_SCHEMA, null, 42));
        final Schema reader = Schema.createRecord(readerFields);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility expectedResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.compatible(), reader, TestSchemaCompatibility.WRITER_SCHEMA, READER_WRITER_COMPATIBLE_MESSAGE);
        // Test new field with default value.
        Assert.assertEquals(expectedResult, checkReaderWriterCompatibility(reader, TestSchemaCompatibility.WRITER_SCHEMA));
    }

    @Test
    public void testValidateSchemaNewField() throws Exception {
        final List<Schema.Field> readerFields = TestSchemas.list(new Schema.Field("oldfield1", TestSchemas.INT_SCHEMA, null, null), new Schema.Field("newfield1", TestSchemas.INT_SCHEMA, null, null));
        final Schema reader = Schema.createRecord(readerFields);
        SchemaPairCompatibility compatibility = checkReaderWriterCompatibility(reader, TestSchemaCompatibility.WRITER_SCHEMA);
        // Test new field without default value.
        Assert.assertEquals(INCOMPATIBLE, compatibility.getType());
        Assert.assertEquals(SchemaCompatibilityResult.incompatible(READER_FIELD_MISSING_DEFAULT_VALUE, reader, TestSchemaCompatibility.WRITER_SCHEMA, "newfield1", Arrays.asList("", "fields", "1")), compatibility.getResult());
        Assert.assertEquals(String.format(("Data encoded using writer schema:%n%s%n" + "will or may fail to decode using reader schema:%n%s%n"), TestSchemaCompatibility.WRITER_SCHEMA.toString(true), reader.toString(true)), compatibility.getDescription());
        Assert.assertEquals(reader, compatibility.getReader());
        Assert.assertEquals(TestSchemaCompatibility.WRITER_SCHEMA, compatibility.getWriter());
    }

    @Test
    public void testValidateArrayWriterSchema() throws Exception {
        final Schema validReader = Schema.createArray(TestSchemas.STRING_SCHEMA);
        final Schema invalidReader = Schema.createMap(TestSchemas.STRING_SCHEMA);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility validResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.compatible(), validReader, TestSchemas.STRING_ARRAY_SCHEMA, READER_WRITER_COMPATIBLE_MESSAGE);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility invalidResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.incompatible(TYPE_MISMATCH, invalidReader, TestSchemas.STRING_ARRAY_SCHEMA, "reader type: MAP not compatible with writer type: ARRAY", Arrays.asList("")), invalidReader, TestSchemas.STRING_ARRAY_SCHEMA, String.format(("Data encoded using writer schema:%n%s%n" + "will or may fail to decode using reader schema:%n%s%n"), TestSchemas.STRING_ARRAY_SCHEMA.toString(true), invalidReader.toString(true)));
        Assert.assertEquals(validResult, checkReaderWriterCompatibility(validReader, TestSchemas.STRING_ARRAY_SCHEMA));
        Assert.assertEquals(invalidResult, checkReaderWriterCompatibility(invalidReader, TestSchemas.STRING_ARRAY_SCHEMA));
    }

    @Test
    public void testValidatePrimitiveWriterSchema() throws Exception {
        final Schema validReader = Schema.create(STRING);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility validResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.compatible(), validReader, TestSchemas.STRING_SCHEMA, READER_WRITER_COMPATIBLE_MESSAGE);
        final SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility invalidResult = new SchemaCompatibility.SchemaCompatibility.SchemaPairCompatibility(SchemaCompatibilityResult.incompatible(TYPE_MISMATCH, TestSchemas.INT_SCHEMA, TestSchemas.STRING_SCHEMA, "reader type: INT not compatible with writer type: STRING", Arrays.asList("")), TestSchemas.INT_SCHEMA, TestSchemas.STRING_SCHEMA, String.format(("Data encoded using writer schema:%n%s%n" + "will or may fail to decode using reader schema:%n%s%n"), TestSchemas.STRING_SCHEMA.toString(true), TestSchemas.INT_SCHEMA.toString(true)));
        Assert.assertEquals(validResult, checkReaderWriterCompatibility(validReader, TestSchemas.STRING_SCHEMA));
        Assert.assertEquals(invalidResult, checkReaderWriterCompatibility(TestSchemas.INT_SCHEMA, TestSchemas.STRING_SCHEMA));
    }

    /**
     * Reader union schema must contain all writer union branches.
     */
    @Test
    public void testUnionReaderWriterSubsetIncompatibility() {
        final Schema unionWriter = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.STRING_SCHEMA, TestSchemas.LONG_SCHEMA));
        final Schema unionReader = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.STRING_SCHEMA));
        final SchemaPairCompatibility result = checkReaderWriterCompatibility(unionReader, unionWriter);
        Assert.assertEquals(SchemaCompatibilityType.INCOMPATIBLE, result.getType());
    }

    // -----------------------------------------------------------------------------------------------
    /**
     * Collection of reader/writer schema pair that are compatible.
     */
    public static final List<TestSchemas.ReaderWriter> COMPATIBLE_READER_WRITER_TEST_CASES = // Avro spec says INT/LONG can be promoted to FLOAT/DOUBLE.
    // This is arguable as this causes a loss of precision.
    // String-to/from-bytes, introduced in Avro 1.7.7
    // Tests involving unions:
    // Readers capable of reading all branches of a union are compatible
    // Special case of singleton unions:
    // Fixed types
    // Tests involving records:
    TestSchemas.list(new TestSchemas.ReaderWriter(TestSchemas.BOOLEAN_SCHEMA, TestSchemas.BOOLEAN_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.LONG_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.FLOAT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_SCHEMA, TestSchemas.STRING_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_SCHEMA, TestSchemas.BYTES_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_ARRAY_SCHEMA, TestSchemas.INT_ARRAY_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_ARRAY_SCHEMA, TestSchemas.INT_ARRAY_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_MAP_SCHEMA, TestSchemas.INT_MAP_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_MAP_SCHEMA, TestSchemas.INT_MAP_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_AB_SCHEMA, TestSchemas.ENUM1_AB_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM1_ABC_SCHEMA, TestSchemas.ENUM1_AB_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_SCHEMA, TestSchemas.BYTES_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_SCHEMA, TestSchemas.STRING_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.INT_LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_STRING_UNION_SCHEMA, TestSchemas.STRING_INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.STRING_UNION_SCHEMA, TestSchemas.BYTES_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_UNION_SCHEMA, TestSchemas.EMPTY_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.BYTES_UNION_SCHEMA, TestSchemas.STRING_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_UNION_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.LONG_SCHEMA, TestSchemas.INT_LONG_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.INT_FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.DOUBLE_SCHEMA, TestSchemas.INT_LONG_FLOAT_DOUBLE_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FLOAT_SCHEMA, TestSchemas.FLOAT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_UNION_SCHEMA, TestSchemas.INT_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.INT_SCHEMA, TestSchemas.INT_UNION_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.FIXED_4_BYTES, TestSchemas.FIXED_4_BYTES), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_RECORD1, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.EMPTY_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_RECORD1, TestSchemas.A_DINT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.A_DINT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_LONG_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_RECORD1, TestSchemas.A_INT_B_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_RECORD1, TestSchemas.A_INT_B_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_B_DINT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_B_DINT_RECORD1, TestSchemas.EMPTY_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_DINT_B_DINT_RECORD1, TestSchemas.A_INT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.A_INT_B_INT_RECORD1, TestSchemas.A_DINT_B_DINT_RECORD1), new TestSchemas.ReaderWriter(TestSchemas.INT_LIST_RECORD, TestSchemas.INT_LIST_RECORD), new TestSchemas.ReaderWriter(TestSchemas.LONG_LIST_RECORD, TestSchemas.LONG_LIST_RECORD), new TestSchemas.ReaderWriter(TestSchemas.LONG_LIST_RECORD, TestSchemas.INT_LIST_RECORD), new TestSchemas.ReaderWriter(TestSchemas.NULL_SCHEMA, TestSchemas.NULL_SCHEMA), new TestSchemas.ReaderWriter(TestSchemas.ENUM_AB_ENUM_DEFAULT_A_RECORD, TestSchemas.ENUM_ABC_ENUM_DEFAULT_A_RECORD), new TestSchemas.ReaderWriter(TestSchemas.ENUM_AB_FIELD_DEFAULT_A_ENUM_DEFAULT_B_RECORD, TestSchemas.ENUM_ABC_FIELD_DEFAULT_B_ENUM_DEFAULT_A_RECORD));

    // -----------------------------------------------------------------------------------------------
    /**
     * Tests reader/writer compatibility validation.
     */
    @Test
    public void testReaderWriterCompatibility() {
        for (TestSchemas.ReaderWriter readerWriter : TestSchemaCompatibility.COMPATIBLE_READER_WRITER_TEST_CASES) {
            final Schema reader = readerWriter.getReader();
            final Schema writer = readerWriter.getWriter();
            TestSchemaCompatibility.LOG.debug("Testing compatibility of reader {} with writer {}.", reader, writer);
            final SchemaPairCompatibility result = checkReaderWriterCompatibility(reader, writer);
            Assert.assertEquals(String.format("Expecting reader %s to be compatible with writer %s, but tested incompatible.", reader, writer), COMPATIBLE, result.getType());
        }
    }

    // -----------------------------------------------------------------------------------------------
    /**
     * Descriptor for a test case that encodes a datum according to a given writer schema,
     * then decodes it according to reader schema and validates the decoded value.
     */
    private static final class DecodingTestCase {
        /**
         * Writer schema used to encode the datum.
         */
        private final Schema mWriterSchema;

        /**
         * Datum to encode according to the specified writer schema.
         */
        private final Object mDatum;

        /**
         * Reader schema used to decode the datum encoded using the writer schema.
         */
        private final Schema mReaderSchema;

        /**
         * Expected datum value when using the reader schema to decode from the writer schema.
         */
        private final Object mDecodedDatum;

        public DecodingTestCase(final Schema writerSchema, final Object datum, final Schema readerSchema, final Object decoded) {
            mWriterSchema = writerSchema;
            mDatum = datum;
            mReaderSchema = readerSchema;
            mDecodedDatum = decoded;
        }

        public Schema getReaderSchema() {
            return mReaderSchema;
        }

        public Schema getWriterSchema() {
            return mWriterSchema;
        }

        public Object getDatum() {
            return mDatum;
        }

        public Object getDecodedDatum() {
            return mDecodedDatum;
        }
    }

    // -----------------------------------------------------------------------------------------------
    public static final List<TestSchemaCompatibility.DecodingTestCase> DECODING_COMPATIBILITY_TEST_CASES = // This is currently accepted but causes a precision loss:
    // IEEE 754 floats have 24 bits signed mantissa
    // new DecodingTestCase(LONG_SCHEMA, 1L, INT_SCHEMA, 1),  // should work in best-effort!
    TestSchemas.list(new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_SCHEMA, 1, TestSchemas.INT_SCHEMA, 1), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_SCHEMA, 1, TestSchemas.LONG_SCHEMA, 1L), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_SCHEMA, 1, TestSchemas.FLOAT_SCHEMA, 1.0F), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_SCHEMA, 1, TestSchemas.DOUBLE_SCHEMA, 1.0), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_SCHEMA, ((1 << 24) + 1), TestSchemas.FLOAT_SCHEMA, ((float) ((1 << 24) + 1))), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.ENUM1_AB_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM1_AB_SCHEMA, "A"), TestSchemas.ENUM1_ABC_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM1_ABC_SCHEMA, "A")), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.ENUM1_ABC_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM1_ABC_SCHEMA, "A"), TestSchemas.ENUM1_AB_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM1_AB_SCHEMA, "A")), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.ENUM1_ABC_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM1_ABC_SCHEMA, "B"), TestSchemas.ENUM1_BC_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM1_BC_SCHEMA, "B")), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.ENUM_ABC_ENUM_DEFAULT_A_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM_ABC_ENUM_DEFAULT_A_SCHEMA, "C"), TestSchemas.ENUM_AB_ENUM_DEFAULT_A_SCHEMA, new org.apache.avro.generic.GenericData.EnumSymbol(TestSchemas.ENUM_AB_ENUM_DEFAULT_A_SCHEMA, "A")), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_STRING_UNION_SCHEMA, "the string", TestSchemas.STRING_SCHEMA, new Utf8("the string")), new TestSchemaCompatibility.DecodingTestCase(TestSchemas.INT_STRING_UNION_SCHEMA, "the string", TestSchemas.STRING_UNION_SCHEMA, new Utf8("the string")));

    /**
     * Tests the reader/writer compatibility at decoding time.
     */
    @Test
    public void testReaderWriterDecodingCompatibility() throws Exception {
        for (TestSchemaCompatibility.DecodingTestCase testCase : TestSchemaCompatibility.DECODING_COMPATIBILITY_TEST_CASES) {
            final Schema readerSchema = testCase.getReaderSchema();
            final Schema writerSchema = testCase.getWriterSchema();
            final Object datum = testCase.getDatum();
            final Object expectedDecodedDatum = testCase.getDecodedDatum();
            TestSchemaCompatibility.LOG.debug("Testing incompatibility of reader {} with writer {}.", readerSchema, writerSchema);
            TestSchemaCompatibility.LOG.debug("Encode datum {} with writer {}.", datum, writerSchema);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final Encoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
            final DatumWriter<Object> datumWriter = new org.apache.avro.generic.GenericDatumWriter(writerSchema);
            datumWriter.write(datum, encoder);
            encoder.flush();
            TestSchemaCompatibility.LOG.debug("Decode datum {} whose writer is {} with reader {}.", new Object[]{ datum, writerSchema, readerSchema });
            final byte[] bytes = baos.toByteArray();
            final Decoder decoder = DecoderFactory.get().resolvingDecoder(writerSchema, readerSchema, DecoderFactory.get().binaryDecoder(bytes, null));
            final DatumReader<Object> datumReader = new org.apache.avro.generic.GenericDatumReader(readerSchema);
            final Object decodedDatum = datumReader.read(null, decoder);
            Assert.assertEquals(String.format(("Expecting decoded value %s when decoding value %s whose writer schema is %s " + "using reader schema %s, but value was %s."), expectedDecodedDatum, datum, writerSchema, readerSchema, decodedDatum), expectedDecodedDatum, decodedDatum);
        }
    }
}

