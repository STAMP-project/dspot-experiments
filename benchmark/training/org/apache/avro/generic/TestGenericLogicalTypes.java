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
package org.apache.avro.generic;


import GenericData.STRING_PROP;
import Schema.Type.BYTES;
import Schema.Type.NULL;
import Schema.Type.STRING;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.avro.GenericData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestGenericLogicalTypes {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    public static final GenericData GENERIC = new GenericData();

    @Test
    public void testReadUUID() throws IOException {
        Schema uuidSchema = Schema.create(STRING);
        LogicalTypes.uuid().addToSchema(uuidSchema);
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        List<UUID> expected = Arrays.asList(u1, u2);
        File test = write(Schema.create(STRING), u1.toString(), u2.toString());
        Assert.assertEquals("Should convert Strings to UUIDs", expected, read(TestGenericLogicalTypes.GENERIC.createDatumReader(uuidSchema), test));
    }

    @Test
    public void testWriteUUID() throws IOException {
        Schema stringSchema = Schema.create(STRING);
        stringSchema.addProp(STRING_PROP, "String");
        Schema uuidSchema = Schema.create(STRING);
        LogicalTypes.uuid().addToSchema(uuidSchema);
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        List<String> expected = Arrays.asList(u1.toString(), u2.toString());
        File test = write(TestGenericLogicalTypes.GENERIC, uuidSchema, u1, u2);
        Assert.assertEquals("Should read UUIDs as Strings", expected, read(GenericData.get().createDatumReader(stringSchema), test));
    }

    @Test
    public void testWriteNullableUUID() throws IOException {
        Schema stringSchema = Schema.create(STRING);
        stringSchema.addProp(STRING_PROP, "String");
        Schema nullableStringSchema = Schema.createUnion(Schema.create(NULL), stringSchema);
        Schema uuidSchema = Schema.create(STRING);
        LogicalTypes.uuid().addToSchema(uuidSchema);
        Schema nullableUuidSchema = Schema.createUnion(Schema.create(NULL), uuidSchema);
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        List<String> expected = Arrays.asList(u1.toString(), u2.toString());
        File test = write(TestGenericLogicalTypes.GENERIC, nullableUuidSchema, u1, u2);
        Assert.assertEquals("Should read UUIDs as Strings", expected, read(GenericData.get().createDatumReader(nullableStringSchema), test));
    }

    @Test
    public void testReadDecimalFixed() throws IOException {
        LogicalType decimal = LogicalTypes.decimal(9, 2);
        Schema fixedSchema = Schema.createFixed("aFixed", null, null, 4);
        Schema decimalSchema = decimal.addToSchema(Schema.createFixed("aFixed", null, null, 4));
        BigDecimal d1 = new BigDecimal("-34.34");
        BigDecimal d2 = new BigDecimal("117230.00");
        List<BigDecimal> expected = Arrays.asList(d1, d2);
        Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        // use the conversion directly instead of relying on the write side
        GenericFixed d1fixed = conversion.toFixed(d1, fixedSchema, decimal);
        GenericFixed d2fixed = conversion.toFixed(d2, fixedSchema, decimal);
        File test = write(fixedSchema, d1fixed, d2fixed);
        Assert.assertEquals("Should convert fixed to BigDecimals", expected, read(TestGenericLogicalTypes.GENERIC.createDatumReader(decimalSchema), test));
    }

    @Test
    public void testWriteDecimalFixed() throws IOException {
        LogicalType decimal = LogicalTypes.decimal(9, 2);
        Schema fixedSchema = Schema.createFixed("aFixed", null, null, 4);
        Schema decimalSchema = decimal.addToSchema(Schema.createFixed("aFixed", null, null, 4));
        BigDecimal d1 = new BigDecimal("-34.34");
        BigDecimal d2 = new BigDecimal("117230.00");
        Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        GenericFixed d1fixed = conversion.toFixed(d1, fixedSchema, decimal);
        GenericFixed d2fixed = conversion.toFixed(d2, fixedSchema, decimal);
        List<GenericFixed> expected = Arrays.asList(d1fixed, d2fixed);
        File test = write(TestGenericLogicalTypes.GENERIC, decimalSchema, d1, d2);
        Assert.assertEquals("Should read BigDecimals as fixed", expected, read(GenericData.get().createDatumReader(fixedSchema), test));
    }

    @Test
    public void testReadDecimalBytes() throws IOException {
        LogicalType decimal = LogicalTypes.decimal(9, 2);
        Schema bytesSchema = Schema.create(BYTES);
        Schema decimalSchema = decimal.addToSchema(Schema.create(BYTES));
        BigDecimal d1 = new BigDecimal("-34.34");
        BigDecimal d2 = new BigDecimal("117230.00");
        List<BigDecimal> expected = Arrays.asList(d1, d2);
        Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        // use the conversion directly instead of relying on the write side
        ByteBuffer d1bytes = conversion.toBytes(d1, bytesSchema, decimal);
        ByteBuffer d2bytes = conversion.toBytes(d2, bytesSchema, decimal);
        File test = write(bytesSchema, d1bytes, d2bytes);
        Assert.assertEquals("Should convert bytes to BigDecimals", expected, read(TestGenericLogicalTypes.GENERIC.createDatumReader(decimalSchema), test));
    }

    @Test
    public void testWriteDecimalBytes() throws IOException {
        LogicalType decimal = LogicalTypes.decimal(9, 2);
        Schema bytesSchema = Schema.create(BYTES);
        Schema decimalSchema = decimal.addToSchema(Schema.create(BYTES));
        BigDecimal d1 = new BigDecimal("-34.34");
        BigDecimal d2 = new BigDecimal("117230.00");
        Conversion<BigDecimal> conversion = new Conversions.DecimalConversion();
        // use the conversion directly instead of relying on the write side
        ByteBuffer d1bytes = conversion.toBytes(d1, bytesSchema, decimal);
        ByteBuffer d2bytes = conversion.toBytes(d2, bytesSchema, decimal);
        List<ByteBuffer> expected = Arrays.asList(d1bytes, d2bytes);
        File test = write(TestGenericLogicalTypes.GENERIC, decimalSchema, d1bytes, d2bytes);
        Assert.assertEquals("Should read BigDecimals as bytes", expected, read(GenericData.get().createDatumReader(bytesSchema), test));
    }

    @Test
    public void testCopyUuid() {
        testCopy(LogicalTypes.uuid().addToSchema(Schema.create(STRING)), UUID.randomUUID(), TestGenericLogicalTypes.GENERIC);
    }

    @Test
    public void testCopyUuidRaw() {
        // use raw type
        testCopy(LogicalTypes.uuid().addToSchema(Schema.create(STRING)), UUID.randomUUID().toString(), GenericData.get());// with no conversions

    }

    @Test
    public void testCopyDecimal() {
        testCopy(LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)), new BigDecimal("-34.34"), TestGenericLogicalTypes.GENERIC);
    }

    @Test
    public void testCopyDecimalRaw() {
        testCopy(LogicalTypes.decimal(9, 2).addToSchema(Schema.create(BYTES)), ByteBuffer.wrap(new BigDecimal("-34.34").unscaledValue().toByteArray()), GenericData.get());// no conversions

    }
}

