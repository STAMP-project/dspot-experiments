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


import java.nio.ByteBuffer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestReadingWritingDataInEvolvedSchemas {
    private static final String RECORD_A = "RecordA";

    private static final String FIELD_A = "fieldA";

    private static final char LATIN_SMALL_LETTER_O_WITH_DIARESIS = '\u00f6';

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Schema DOUBLE_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().doubleType().noDefault().endRecord();

    private static final Schema FLOAT_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().floatType().noDefault().endRecord();

    private static final Schema LONG_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().longType().noDefault().endRecord();

    private static final Schema INT_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().intType().noDefault().endRecord();

    private static final Schema UNION_INT_LONG_FLOAT_DOUBLE_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().doubleType().and().floatType().and().longType().and().intType().endUnion().noDefault().endRecord();

    private static final Schema STRING_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().stringType().noDefault().endRecord();

    private static final Schema BYTES_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().bytesType().noDefault().endRecord();

    private static final Schema UNION_STRING_BYTES_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().stringType().and().bytesType().endUnion().noDefault().endRecord();

    private static final Schema ENUM_AB_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().enumeration("Enum1").symbols("A", "B").noDefault().endRecord();

    private static final Schema ENUM_ABC_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().enumeration("Enum1").symbols("A", "B", "C").noDefault().endRecord();

    private static final Schema UNION_INT_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().intType().endUnion().noDefault().endRecord();

    private static final Schema UNION_LONG_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().longType().endUnion().noDefault().endRecord();

    private static final Schema UNION_FLOAT_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().floatType().endUnion().noDefault().endRecord();

    private static final Schema UNION_DOUBLE_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().doubleType().endUnion().noDefault().endRecord();

    private static final Schema UNION_LONG_FLOAT_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().floatType().and().longType().endUnion().noDefault().endRecord();

    private static final Schema UNION_FLOAT_DOUBLE_RECORD = // 
    // 
    // 
    SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().unionOf().floatType().and().doubleType().endUnion().noDefault().endRecord();

    public TestReadingWritingDataInEvolvedSchemas(TestReadingWritingDataInEvolvedSchemas.EncoderType encoderType) {
        this.encoderType = encoderType;
    }

    private final TestReadingWritingDataInEvolvedSchemas.EncoderType encoderType;

    enum EncoderType {

        BINARY,
        JSON;}

    @Test
    public void doubleWrittenWithUnionSchemaIsConvertedToDoubleSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_LONG_FLOAT_DOUBLE_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42.0);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.DOUBLE_RECORD, writer, encoded);
        Assert.assertEquals(42.0, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void longWrittenWithUnionSchemaIsConvertedToUnionLongFloatSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_LONG_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_LONG_FLOAT_RECORD, writer, encoded);
        Assert.assertEquals(42L, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void longWrittenWithUnionSchemaIsConvertedToDoubleSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_LONG_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_DOUBLE_RECORD, writer, encoded);
        Assert.assertEquals(42.0, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void intWrittenWithUnionSchemaIsConvertedToDoubleSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_DOUBLE_RECORD, writer, encoded);
        Assert.assertEquals(42.0, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void intWrittenWithUnionSchemaIsReadableByFloatSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.FLOAT_RECORD, writer, encoded);
        Assert.assertEquals(42.0F, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void intWrittenWithUnionSchemaIsReadableByFloatUnionSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_FLOAT_RECORD, writer, encoded);
        Assert.assertEquals(42.0F, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void longWrittenWithUnionSchemaIsReadableByFloatSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_LONG_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.FLOAT_RECORD, writer, encoded);
        Assert.assertEquals(42.0F, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void longWrittenWithUnionSchemaIsReadableByFloatUnionSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_LONG_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_FLOAT_RECORD, writer, encoded);
        Assert.assertEquals(42.0F, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void longWrittenWithUnionSchemaIsConvertedToLongFloatUnionSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_LONG_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_LONG_FLOAT_RECORD, writer, encoded);
        Assert.assertEquals(42L, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void longWrittenWithUnionSchemaIsConvertedToFloatDoubleUnionSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_LONG_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.UNION_FLOAT_DOUBLE_RECORD, writer, encoded);
        Assert.assertEquals(42.0F, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void doubleWrittenWithUnionSchemaIsNotConvertedToFloatSchema() throws Exception {
        expectedException.expect(AvroTypeException.class);
        expectedException.expectMessage("Found double, expecting float");
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_LONG_FLOAT_DOUBLE_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42.0);
        byte[] encoded = encodeGenericBlob(record);
        decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.FLOAT_RECORD, writer, encoded);
    }

    @Test
    public void floatWrittenWithUnionSchemaIsNotConvertedToLongSchema() throws Exception {
        expectedException.expect(AvroTypeException.class);
        expectedException.expectMessage("Found float, expecting long");
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_LONG_FLOAT_DOUBLE_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42.0F);
        byte[] encoded = encodeGenericBlob(record);
        decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.LONG_RECORD, writer, encoded);
    }

    @Test
    public void longWrittenWithUnionSchemaIsNotConvertedToIntSchema() throws Exception {
        expectedException.expect(AvroTypeException.class);
        expectedException.expectMessage("Found long, expecting int");
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_LONG_FLOAT_DOUBLE_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42L);
        byte[] encoded = encodeGenericBlob(record);
        decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.INT_RECORD, writer, encoded);
    }

    @Test
    public void intWrittenWithUnionSchemaIsConvertedToAllNumberSchemas() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_INT_LONG_FLOAT_DOUBLE_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        byte[] encoded = encodeGenericBlob(record);
        Assert.assertEquals(42.0, decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.DOUBLE_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
        Assert.assertEquals(42.0F, decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.FLOAT_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
        Assert.assertEquals(42L, decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.LONG_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
        Assert.assertEquals(42, decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.INT_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
    }

    @Test
    public void asciiStringWrittenWithUnionSchemaIsConvertedToBytesSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_STRING_BYTES_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, "42");
        byte[] encoded = encodeGenericBlob(record);
        ByteBuffer actual = ((ByteBuffer) (decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.BYTES_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A)));
        Assert.assertArrayEquals("42".getBytes("UTF-8"), actual.array());
    }

    @Test
    public void utf8StringWrittenWithUnionSchemaIsConvertedToBytesSchema() throws Exception {
        String goeran = String.format("G%sran", TestReadingWritingDataInEvolvedSchemas.LATIN_SMALL_LETTER_O_WITH_DIARESIS);
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_STRING_BYTES_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, goeran);
        byte[] encoded = encodeGenericBlob(record);
        ByteBuffer actual = ((ByteBuffer) (decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.BYTES_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A)));
        Assert.assertArrayEquals(goeran.getBytes("UTF-8"), actual.array());
    }

    @Test
    public void asciiBytesWrittenWithUnionSchemaIsConvertedToStringSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_STRING_BYTES_RECORD;
        ByteBuffer buf = ByteBuffer.wrap("42".getBytes("UTF-8"));
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, buf);
        byte[] encoded = encodeGenericBlob(record);
        CharSequence read = ((CharSequence) (decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.STRING_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A)));
        Assert.assertEquals("42", read.toString());
    }

    @Test
    public void utf8BytesWrittenWithUnionSchemaIsConvertedToStringSchema() throws Exception {
        String goeran = String.format("G%sran", TestReadingWritingDataInEvolvedSchemas.LATIN_SMALL_LETTER_O_WITH_DIARESIS);
        Schema writer = TestReadingWritingDataInEvolvedSchemas.UNION_STRING_BYTES_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, goeran);
        byte[] encoded = encodeGenericBlob(record);
        CharSequence read = ((CharSequence) (decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.STRING_RECORD, writer, encoded).get(TestReadingWritingDataInEvolvedSchemas.FIELD_A)));
        Assert.assertEquals(goeran, read.toString());
    }

    @Test
    public void enumRecordCanBeReadWithExtendedEnumSchema() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.ENUM_AB_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, new org.apache.avro.generic.GenericData.EnumSymbol(writer, "A"));
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.ENUM_ABC_RECORD, writer, encoded);
        Assert.assertEquals("A", decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A).toString());
    }

    @Test
    public void enumRecordWithExtendedSchemaCanBeReadWithOriginalEnumSchemaIfOnlyOldValues() throws Exception {
        Schema writer = TestReadingWritingDataInEvolvedSchemas.ENUM_ABC_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, new org.apache.avro.generic.GenericData.EnumSymbol(writer, "A"));
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.ENUM_AB_RECORD, writer, encoded);
        Assert.assertEquals("A", decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A).toString());
    }

    @Test
    public void enumRecordWithExtendedSchemaCanNotBeReadIfNewValuesAreUsed() throws Exception {
        expectedException.expect(AvroTypeException.class);
        expectedException.expectMessage("No match for C");
        Schema writer = TestReadingWritingDataInEvolvedSchemas.ENUM_ABC_RECORD;
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, new org.apache.avro.generic.GenericData.EnumSymbol(writer, "C"));
        byte[] encoded = encodeGenericBlob(record);
        decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.ENUM_AB_RECORD, writer, encoded);
    }

    @Test
    public void recordWrittenWithExtendedSchemaCanBeReadWithOriginalSchemaButLossOfData() throws Exception {
        Schema writer = // 
        // 
        // 
        // 
        SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name("newTopField").type().stringType().noDefault().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().intType().noDefault().endRecord();
        Record record = defaultRecordWithSchema(writer, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        record.put("newTopField", "not decoded");
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(TestReadingWritingDataInEvolvedSchemas.INT_RECORD, writer, encoded);
        Assert.assertEquals(42, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
        Assert.assertNull(decoded.get("newTopField"));
    }

    @Test
    public void readerWithoutDefaultValueThrowsException() throws Exception {
        expectedException.expect(AvroTypeException.class);
        expectedException.expectMessage("missing required field newField");
        Schema reader = // 
        // 
        // 
        // 
        SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name("newField").type().intType().noDefault().name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().intType().noDefault().endRecord();
        Record record = defaultRecordWithSchema(TestReadingWritingDataInEvolvedSchemas.INT_RECORD, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        byte[] encoded = encodeGenericBlob(record);
        decodeGenericBlob(reader, TestReadingWritingDataInEvolvedSchemas.INT_RECORD, encoded);
    }

    @Test
    public void readerWithDefaultValueIsApplied() throws Exception {
        Schema reader = // 
        // 
        // 
        // 
        SchemaBuilder.record(TestReadingWritingDataInEvolvedSchemas.RECORD_A).fields().name("newFieldWithDefault").type().intType().intDefault(314).name(TestReadingWritingDataInEvolvedSchemas.FIELD_A).type().intType().noDefault().endRecord();
        Record record = defaultRecordWithSchema(TestReadingWritingDataInEvolvedSchemas.INT_RECORD, TestReadingWritingDataInEvolvedSchemas.FIELD_A, 42);
        byte[] encoded = encodeGenericBlob(record);
        Record decoded = decodeGenericBlob(reader, TestReadingWritingDataInEvolvedSchemas.INT_RECORD, encoded);
        Assert.assertEquals(42, decoded.get(TestReadingWritingDataInEvolvedSchemas.FIELD_A));
        Assert.assertEquals(314, decoded.get("newFieldWithDefault"));
    }

    @Test
    public void aliasesInSchema() throws Exception {
        Schema writer = new Schema.Parser().parse(("{\"namespace\": \"example.avro\", \"type\": \"record\", \"name\": \"User\", \"fields\": [" + ("{\"name\": \"name\", \"type\": \"int\"}\n" + "]}\n")));
        Schema reader = new Schema.Parser().parse(("{\"namespace\": \"example.avro\", \"type\": \"record\", \"name\": \"User\", \"fields\": [" + ("{\"name\": \"fname\", \"type\": \"int\", \"aliases\" : [ \"name\" ]}\n" + "]}\n")));
        GenericData.Record record = defaultRecordWithSchema(writer, "name", 1);
        byte[] encoded = encodeGenericBlob(record);
        GenericData.Record decoded = decodeGenericBlob(reader, reader, encoded);
        Assert.assertEquals(1, decoded.get("fname"));
    }
}

