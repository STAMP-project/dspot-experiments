/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.common;


import Schema.Field;
import Type.BOOLEAN;
import Type.BYTES;
import Type.DOUBLE;
import Type.FLOAT;
import Type.INT;
import Type.LONG;
import Type.STRING;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;


public class CommonToGenericTest {
    @Test
    public void testArrayOfFixed() {
        Schema fixedSchema = Schema.createFixed("someFixed", "", "", 2);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(fixedSchema, new DefaultCommonFixed(fixedSchema, new byte[]{ 1, 2 })));
        GenericFixed avroFixed = ((GenericFixed) (avroArray.get(0)));
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, avroFixed.bytes());
    }

    @Test
    public void testArrayOfBoolean() {
        Schema booleanSchema = Schema.create(BOOLEAN);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(booleanSchema, new Boolean(Boolean.TRUE)));
        Boolean avroBoolean = ((Boolean) (avroArray.get(0)));
        Assert.assertTrue(avroBoolean);
    }

    @Test
    public void testArrayOfInteger() {
        Schema integerSchema = Schema.create(INT);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(integerSchema, new Integer(5)));
        Integer avroInt = ((Integer) (avroArray.get(0)));
        Assert.assertEquals(5, avroInt.intValue());
    }

    @Test
    public void testArrayOfLong() {
        Schema longSchema = Schema.create(LONG);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(longSchema, new Long(5)));
        Long avroLong = ((Long) (avroArray.get(0)));
        Assert.assertEquals(5, avroLong.longValue());
    }

    @Test
    public void testArrayOfDouble() {
        Schema doubleSchema = Schema.create(DOUBLE);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(doubleSchema, new Double(5.5)));
        Double avroDouble = ((Double) (avroArray.get(0)));
        Assert.assertEquals(5.5, avroDouble.doubleValue(), 0.1);
    }

    @Test
    public void testArrayOfFloat() {
        Schema floatSchema = Schema.create(FLOAT);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(floatSchema, new Float(5.5)));
        Float avroFloat = ((Float) (avroArray.get(0)));
        Assert.assertEquals(5.5, avroFloat.floatValue(), 0.1);
    }

    @Test
    public void testArrayOfString() {
        Schema stringSchema = Schema.create(STRING);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(stringSchema, new String("abc")));
        String avroString = ((String) (avroArray.get(0)));
        Assert.assertEquals("abc", avroString);
    }

    @Test
    public void testArrayOfBytes() {
        Schema bytesSchema = Schema.create(BYTES);
        GenericArray avroArray = CommonToGeneric.createArray(CommonToGenericTest.createCommonArray(bytesSchema, ByteBuffer.wrap(new byte[]{ 1, 2 })));
        ByteBuffer avroBytes = ((ByteBuffer) (avroArray.get(0)));
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, avroBytes.array());
    }

    @Test
    public void testRecord() {
        List<String> symbols = new LinkedList<>();
        symbols.add("enum1");
        symbols.add("enum2");
        Schema recordSchema = Schema.createRecord("someRecord", "", "", false);
        List<Schema.Field> fields = new LinkedList<>();
        fields.add(new Schema.Field("field1", Schema.createEnum("someEnum", "", "", symbols), "", null));
        fields.add(new Schema.Field("field2", Schema.create(BOOLEAN), "", null));
        fields.add(new Schema.Field("field3", Schema.create(INT), "", null));
        fields.add(new Schema.Field("field4", Schema.create(LONG), "", null));
        fields.add(new Schema.Field("field5", Schema.create(DOUBLE), "", null));
        fields.add(new Schema.Field("field6", Schema.create(FLOAT), "", null));
        fields.add(new Schema.Field("field7", Schema.create(STRING), "", null));
        fields.add(new Schema.Field("field8", Schema.create(BYTES), "", null));
        recordSchema.setFields(fields);
        CommonRecord record = new DefaultCommonRecord(recordSchema);
        record.setField("field1", new DefaultCommonValue(new DefaultCommonEnum(Schema.createEnum("someEnum", "", "", symbols), "enum2")));
        record.setField("field2", new DefaultCommonValue(new Boolean(Boolean.TRUE)));
        record.setField("field3", new DefaultCommonValue(new Integer(1)));
        record.setField("field4", new DefaultCommonValue(new Long(2)));
        record.setField("field5", new DefaultCommonValue(new Double(5.5)));
        record.setField("field6", new DefaultCommonValue(new Float(5.6)));
        record.setField("field7", new DefaultCommonValue(new String("abc")));
        record.setField("field8", new DefaultCommonValue(ByteBuffer.wrap(new byte[]{ 1, 2 })));
        GenericRecord avroRecord = CommonToGeneric.createRecord(record);
        GenericEnumSymbol field1 = ((GenericEnumSymbol) (avroRecord.get("field1")));
        Assert.assertEquals("enum2", field1.toString());
        Assert.assertTrue(((Boolean) (avroRecord.get("field2"))));
        Assert.assertEquals(1, ((Integer) (avroRecord.get("field3"))).intValue());
        Assert.assertEquals(2, ((Long) (avroRecord.get("field4"))).longValue());
        Assert.assertEquals(5.5, ((Double) (avroRecord.get("field5"))).doubleValue(), 0.1);
        Assert.assertEquals(5.6, ((Float) (avroRecord.get("field6"))).floatValue(), 0.1);
        Assert.assertEquals("abc", ((String) (avroRecord.get("field7"))));
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, ((ByteBuffer) (avroRecord.get("field8"))).array());
    }
}

