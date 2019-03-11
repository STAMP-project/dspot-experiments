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
package org.apache.avro.reflect;


import GenericData.Record;
import JsonProperties.NULL_VALUE;
import Protocol.Message;
import ReflectData.ACCESSOR_CACHE;
import ReflectData.AllowNull;
import Schema.Type.INT;
import Schema.Type.NULL;
import Schema.Type.RECORD;
import Schema.Type.STRING;
import Schema.Type.UNION;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;


public class TestReflect {
    EncoderFactory factory = new EncoderFactory();

    // test primitive type inference
    @Test
    public void testVoid() {
        check(Void.TYPE, "\"null\"");
        check(Void.class, "\"null\"");
    }

    @Test
    public void testBoolean() {
        check(Boolean.TYPE, "\"boolean\"");
        check(Boolean.class, "\"boolean\"");
    }

    @Test
    public void testInt() {
        check(Integer.TYPE, "\"int\"");
        check(Integer.class, "\"int\"");
    }

    @Test
    public void testByte() {
        check(Byte.TYPE, "{\"type\":\"int\",\"java-class\":\"java.lang.Byte\"}");
        check(Byte.class, "{\"type\":\"int\",\"java-class\":\"java.lang.Byte\"}");
    }

    @Test
    public void testShort() {
        check(Short.TYPE, "{\"type\":\"int\",\"java-class\":\"java.lang.Short\"}");
        check(Short.class, "{\"type\":\"int\",\"java-class\":\"java.lang.Short\"}");
    }

    @Test
    public void testChar() {
        check(Character.TYPE, "{\"type\":\"int\",\"java-class\":\"java.lang.Character\"}");
        check(Character.class, "{\"type\":\"int\",\"java-class\":\"java.lang.Character\"}");
    }

    @Test
    public void testLong() {
        check(Long.TYPE, "\"long\"");
        check(Long.class, "\"long\"");
    }

    @Test
    public void testFloat() {
        check(Float.TYPE, "\"float\"");
        check(Float.class, "\"float\"");
    }

    @Test
    public void testDouble() {
        check(Double.TYPE, "\"double\"");
        check(Double.class, "\"double\"");
    }

    @Test
    public void testString() {
        check("Foo", "\"string\"");
    }

    @Test
    public void testBytes() {
        check(ByteBuffer.allocate(0), "\"bytes\"");
        check(new byte[0], "{\"type\":\"bytes\",\"java-class\":\"[B\"}");
    }

    @Test
    public void testUnionWithCollection() {
        Schema s = new Schema.Parser().parse("[\"null\", {\"type\":\"array\",\"items\":\"float\"}]");
        GenericData data = ReflectData.get();
        Assert.assertEquals(1, data.resolveUnion(s, new ArrayList<Float>()));
    }

    @Test
    public void testUnionWithMap() {
        Schema s = new Schema.Parser().parse("[\"null\", {\"type\":\"map\",\"values\":\"float\"}]");
        GenericData data = ReflectData.get();
        Assert.assertEquals(1, data.resolveUnion(s, new HashMap<String, Float>()));
    }

    @Test
    public void testUnionWithMapWithUtf8Keys() {
        Schema s = new Schema.Parser().parse("[\"null\", {\"type\":\"map\",\"values\":\"float\"}]");
        GenericData data = ReflectData.get();
        HashMap<Utf8, Float> map = new HashMap<Utf8, Float>();
        map.put(new Utf8("foo"), 1.0F);
        Assert.assertEquals(1, data.resolveUnion(s, map));
    }

    @Test
    public void testUnionWithFixed() {
        Schema s = new Schema.Parser().parse("[\"null\", {\"type\":\"fixed\",\"name\":\"f\",\"size\":1}]");
        Schema f = new Schema.Parser().parse("{\"type\":\"fixed\",\"name\":\"f\",\"size\":1}");
        GenericData data = ReflectData.get();
        Assert.assertEquals(1, data.resolveUnion(s, new GenericData.Fixed(f)));
    }

    @Test
    public void testUnionWithEnum() {
        Schema s = new Schema.Parser().parse(("[\"null\", {\"type\":\"enum\",\"name\":\"E\",\"namespace\":" + "\"org.apache.avro.reflect.TestReflect\",\"symbols\":[\"A\",\"B\"]}]"));
        GenericData data = ReflectData.get();
        Assert.assertEquals(1, data.resolveUnion(s, TestReflect.E.A));
    }

    @Test
    public void testUnionWithBytes() {
        Schema s = new Schema.Parser().parse("[\"null\", \"bytes\"]");
        GenericData data = ReflectData.get();
        Assert.assertEquals(1, data.resolveUnion(s, ByteBuffer.wrap(new byte[]{ 1 })));
    }

    // test map, array and list type inference
    public static class R1 {
        private Map<String, String> mapField = new HashMap<>();

        private String[] arrayField = new String[]{ "foo" };

        private List<String> listField = new ArrayList<>();

        {
            mapField.put("foo", "bar");
            listField.add("foo");
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R1))
                return false;

            TestReflect.R1 that = ((TestReflect.R1) (o));
            return ((mapField.equals(that.mapField)) && (Arrays.equals(this.arrayField, that.arrayField))) && (listField.equals(that.listField));
        }
    }

    @Test
    public void testMap() throws Exception {
        check(TestReflect.R1.class.getDeclaredField("mapField").getGenericType(), "{\"type\":\"map\",\"values\":\"string\"}");
    }

    @Test
    public void testArray() throws Exception {
        check(TestReflect.R1.class.getDeclaredField("arrayField").getGenericType(), "{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"[Ljava.lang.String;\"}");
    }

    @Test
    public void testList() throws Exception {
        check(TestReflect.R1.class.getDeclaredField("listField").getGenericType(), ("{\"type\":\"array\",\"items\":\"string\"" + ",\"java-class\":\"java.util.List\"}"));
    }

    @Test
    public void testR1() throws Exception {
        checkReadWrite(new TestReflect.R1());
    }

    // test record, array and list i/o
    public static class R2 {
        private String[] arrayField;

        private Collection<String> collectionField;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R2))
                return false;

            TestReflect.R2 that = ((TestReflect.R2) (o));
            return (Arrays.equals(this.arrayField, that.arrayField)) && (collectionField.equals(that.collectionField));
        }
    }

    @Test
    public void testR2() throws Exception {
        TestReflect.R2 r2 = new TestReflect.R2();
        r2.arrayField = new String[]{ "foo" };
        r2.collectionField = new ArrayList<>();
        r2.collectionField.add("foo");
        checkReadWrite(r2);
    }

    // test array i/o of unboxed type
    public static class R3 {
        private int[] intArray;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R3))
                return false;

            TestReflect.R3 that = ((TestReflect.R3) (o));
            return Arrays.equals(this.intArray, that.intArray);
        }
    }

    @Test
    public void testR3() throws Exception {
        TestReflect.R3 r3 = new TestReflect.R3();
        r3.intArray = new int[]{ 1 };
        checkReadWrite(r3);
    }

    // test inherited fields & short datatype
    public static class R4 {
        public short value;

        public short[] shorts;

        public byte b;

        public char c;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R4))
                return false;

            TestReflect.R4 that = ((TestReflect.R4) (o));
            return ((((this.value) == (that.value)) && (Arrays.equals(this.shorts, that.shorts))) && ((this.b) == (that.b))) && ((this.c) == (that.c));
        }
    }

    public static class R5 extends TestReflect.R4 {}

    @Test
    public void testR5() throws Exception {
        TestReflect.R5 r5 = new TestReflect.R5();
        r5.value = 1;
        r5.shorts = new short[]{ 3, 255, 256, Short.MAX_VALUE, Short.MIN_VALUE };
        r5.b = 99;
        r5.c = 'a';
        checkReadWrite(r5);
    }

    // test union annotation on a class
    @Union({ TestReflect.R7.class, TestReflect.R8.class })
    public static class R6 {}

    public static class R7 extends TestReflect.R6 {
        public int value;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R7))
                return false;

            return (this.value) == (((TestReflect.R7) (o)).value);
        }
    }

    public static class R8 extends TestReflect.R6 {
        public float value;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R8))
                return false;

            return (this.value) == (((TestReflect.R8) (o)).value);
        }
    }

    // test arrays of union annotated class
    public static class R9 {
        public TestReflect.R6[] r6s;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R9))
                return false;

            return Arrays.equals(this.r6s, ((TestReflect.R9) (o)).r6s);
        }
    }

    @Test
    public void testR6() throws Exception {
        TestReflect.R7 r7 = new TestReflect.R7();
        r7.value = 1;
        checkReadWrite(r7, ReflectData.get().getSchema(TestReflect.R6.class));
        TestReflect.R8 r8 = new TestReflect.R8();
        r8.value = 1;
        checkReadWrite(r8, ReflectData.get().getSchema(TestReflect.R6.class));
        TestReflect.R9 r9 = new TestReflect.R9();
        r9.r6s = new TestReflect.R6[]{ r7, r8 };
        checkReadWrite(r9, ReflectData.get().getSchema(TestReflect.R9.class));
    }

    // test union in fields
    public static class R9_1 {
        @Union({ Void.class, TestReflect.R7.class, TestReflect.R8.class })
        public Object value;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R9_1))
                return false;

            if ((this.value) == null)
                return (((TestReflect.R9_1) (o)).value) == null;

            return this.value.equals(((TestReflect.R9_1) (o)).value);
        }
    }

    @Test
    public void testR6_1() throws Exception {
        TestReflect.R7 r7 = new TestReflect.R7();
        r7.value = 1;
        checkReadWrite(r7, ReflectData.get().getSchema(TestReflect.R6.class));
        TestReflect.R8 r8 = new TestReflect.R8();
        r8.value = 1;
        checkReadWrite(r8, ReflectData.get().getSchema(TestReflect.R6.class));
        TestReflect.R9_1 r9_1 = new TestReflect.R9_1();
        r9_1.value = null;
        checkReadWrite(r9_1, ReflectData.get().getSchema(TestReflect.R9_1.class));
        r9_1.value = r7;
        checkReadWrite(r9_1, ReflectData.get().getSchema(TestReflect.R9_1.class));
        r9_1.value = r8;
        checkReadWrite(r9_1, ReflectData.get().getSchema(TestReflect.R9_1.class));
    }

    // test union annotation on methods and parameters
    public static interface P0 {
        @Union({ Void.class, String.class })
        String foo(@Union({ Void.class, String.class })
        String s);
    }

    @Test
    public void testP0() throws Exception {
        Protocol p0 = ReflectData.get().getProtocol(TestReflect.P0.class);
        Protocol.Message message = p0.getMessages().get("foo");
        // check response schema is union
        Schema response = message.getResponse();
        Assert.assertEquals(UNION, response.getType());
        Assert.assertEquals(NULL, response.getTypes().get(0).getType());
        Assert.assertEquals(STRING, response.getTypes().get(1).getType());
        // check request schema is union
        Schema request = message.getRequest();
        Field field = request.getField("s");
        Assert.assertNotNull("field 's' should not be null", field);
        Schema param = field.schema();
        Assert.assertEquals(UNION, param.getType());
        Assert.assertEquals(NULL, param.getTypes().get(0).getType());
        Assert.assertEquals(STRING, param.getTypes().get(1).getType());
        // check union erasure
        Assert.assertEquals(String.class, ReflectData.get().getClass(response));
        Assert.assertEquals(String.class, ReflectData.get().getClass(param));
    }

    // test Stringable annotation
    @Stringable
    public static class R10 {
        private String text;

        public R10(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R10))
                return false;

            return this.text.equals(((TestReflect.R10) (o)).text);
        }
    }

    @Test
    public void testR10() throws Exception {
        Schema r10Schema = ReflectData.get().getSchema(TestReflect.R10.class);
        Assert.assertEquals(STRING, r10Schema.getType());
        Assert.assertEquals(TestReflect.R10.class.getName(), r10Schema.getProp("java-class"));
        checkReadWrite(new TestReflect.R10("foo"), r10Schema);
    }

    // test Nullable annotation on field
    public static class R11 {
        @Nullable
        private String text;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.R11))
                return false;

            TestReflect.R11 that = ((TestReflect.R11) (o));
            if ((this.text) == null)
                return (that.text) == null;

            return this.text.equals(that.text);
        }
    }

    @Test
    public void testR11() throws Exception {
        Schema r11Record = ReflectData.get().getSchema(TestReflect.R11.class);
        Assert.assertEquals(RECORD, r11Record.getType());
        Field r11Field = r11Record.getField("text");
        Assert.assertEquals(NULL_VALUE, r11Field.defaultVal());
        Schema r11FieldSchema = r11Field.schema();
        Assert.assertEquals(UNION, r11FieldSchema.getType());
        Assert.assertEquals(NULL, r11FieldSchema.getTypes().get(0).getType());
        Schema r11String = r11FieldSchema.getTypes().get(1);
        Assert.assertEquals(STRING, r11String.getType());
        TestReflect.R11 r11 = new TestReflect.R11();
        checkReadWrite(r11, r11Record);
        r11.text = "foo";
        checkReadWrite(r11, r11Record);
    }

    // test nullable annotation on methods and parameters
    public static interface P1 {
        @Nullable
        String foo(@Nullable
        String s);
    }

    @Test
    public void testP1() throws Exception {
        Protocol p1 = ReflectData.get().getProtocol(TestReflect.P1.class);
        Protocol.Message message = p1.getMessages().get("foo");
        // check response schema is union
        Schema response = message.getResponse();
        Assert.assertEquals(UNION, response.getType());
        Assert.assertEquals(NULL, response.getTypes().get(0).getType());
        Assert.assertEquals(STRING, response.getTypes().get(1).getType());
        // check request schema is union
        Schema request = message.getRequest();
        Field field = request.getField("s");
        Assert.assertNotNull("field 's' should not be null", field);
        Schema param = field.schema();
        Assert.assertEquals(UNION, param.getType());
        Assert.assertEquals(NULL, param.getTypes().get(0).getType());
        Assert.assertEquals(STRING, param.getTypes().get(1).getType());
        // check union erasure
        Assert.assertEquals(String.class, ReflectData.get().getClass(response));
        Assert.assertEquals(String.class, ReflectData.get().getClass(param));
    }

    // test AvroSchema annotation
    public static class R12 {
        // fields
        @AvroSchema("\"int\"")
        Object x;

        @AvroSchema("{\"type\":\"array\",\"items\":[\"null\",\"string\"]}")
        List<String> strings;
    }

    @Test
    public void testR12() throws Exception {
        Schema s = ReflectData.get().getSchema(TestReflect.R12.class);
        Assert.assertEquals(INT, s.getField("x").schema().getType());
        Assert.assertEquals(Schema.parse("{\"type\":\"array\",\"items\":[\"null\",\"string\"]}"), s.getField("strings").schema());
    }

    // record
    @AvroSchema("\"null\"")
    public class R13 {}

    @Test
    public void testR13() throws Exception {
        Schema s = ReflectData.get().getSchema(TestReflect.R13.class);
        Assert.assertEquals(NULL, s.getType());
    }

    public interface P4 {
        // message value
        @AvroSchema("\"int\"")
        Object foo(@AvroSchema("\"int\"")
        Object x);// message param

    }

    @Test
    public void testP4() throws Exception {
        Protocol p = ReflectData.get().getProtocol(TestReflect.P4.class);
        Protocol.Message message = p.getMessages().get("foo");
        Assert.assertEquals(INT, message.getResponse().getType());
        Field field = message.getRequest().getField("x");
        Assert.assertEquals(INT, field.schema().getType());
    }

    // test error
    @SuppressWarnings("serial")
    public static class E1 extends Exception {}

    public static interface P2 {
        void error() throws TestReflect.E1;
    }

    @Test
    public void testP2() throws Exception {
        Schema e1 = ReflectData.get().getSchema(TestReflect.E1.class);
        Assert.assertEquals(RECORD, e1.getType());
        Assert.assertTrue(e1.isError());
        Field message = e1.getField("detailMessage");
        Assert.assertNotNull("field 'detailMessage' should not be null", message);
        Schema messageSchema = message.schema();
        Assert.assertEquals(UNION, messageSchema.getType());
        Assert.assertEquals(NULL, messageSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, messageSchema.getTypes().get(1).getType());
        Protocol p2 = ReflectData.get().getProtocol(TestReflect.P2.class);
        Protocol.Message m = p2.getMessages().get("error");
        // check error schema is union
        Schema response = m.getErrors();
        Assert.assertEquals(UNION, response.getType());
        Assert.assertEquals(STRING, response.getTypes().get(0).getType());
        Assert.assertEquals(e1, response.getTypes().get(1));
    }

    @Test
    public void testNoPackage() throws Exception {
        Class<?> noPackage = Class.forName("NoPackage");
        Schema s = ReflectData.get().getSchema(noPackage);
        Assert.assertEquals(noPackage.getName(), ReflectData.getClassName(s));
    }

    public static enum E {

        A,
        B;}

    @Test
    public void testEnum() throws Exception {
        check(TestReflect.E.class, ("{\"type\":\"enum\",\"name\":\"E\",\"namespace\":" + "\"org.apache.avro.reflect.TestReflect\",\"symbols\":[\"A\",\"B\"]}"));
    }

    public static class R {
        int a;

        long b;
    }

    @Test
    public void testRecord() throws Exception {
        check(TestReflect.R.class, ("{\"type\":\"record\",\"name\":\"R\",\"namespace\":" + (("\"org.apache.avro.reflect.TestReflect\",\"fields\":[" + "{\"name\":\"a\",\"type\":\"int\"},") + "{\"name\":\"b\",\"type\":\"long\"}]}")));
    }

    public static class RAvroIgnore {
        @AvroIgnore
        int a;
    }

    @Test
    public void testAnnotationAvroIgnore() throws Exception {
        check(TestReflect.RAvroIgnore.class, ("{\"type\":\"record\",\"name\":\"RAvroIgnore\",\"namespace\":" + "\"org.apache.avro.reflect.TestReflect\",\"fields\":[]}"));
    }

    public static class RAvroMeta {
        @AvroMeta(key = "K", value = "V")
        int a;
    }

    @Test
    public void testAnnotationAvroMeta() throws Exception {
        check(TestReflect.RAvroMeta.class, ("{\"type\":\"record\",\"name\":\"RAvroMeta\",\"namespace\":" + ("\"org.apache.avro.reflect.TestReflect\",\"fields\":[" + "{\"name\":\"a\",\"type\":\"int\",\"K\":\"V\"}]}")));
    }

    public static class RAvroName {
        @AvroName("b")
        int a;
    }

    @Test
    public void testAnnotationAvroName() throws Exception {
        check(TestReflect.RAvroName.class, ("{\"type\":\"record\",\"name\":\"RAvroName\",\"namespace\":" + ("\"org.apache.avro.reflect.TestReflect\",\"fields\":[" + "{\"name\":\"b\",\"type\":\"int\"}]}")));
    }

    public static class RAvroNameCollide {
        @AvroName("b")
        int a;

        int b;
    }

    @Test(expected = Exception.class)
    public void testAnnotationAvroNameCollide() throws Exception {
        check(TestReflect.RAvroNameCollide.class, ("{\"type\":\"record\",\"name\":\"RAvroNameCollide\",\"namespace\":" + (("\"org.apache.avro.reflect.TestReflect\",\"fields\":[" + "{\"name\":\"b\",\"type\":\"int\"},") + "{\"name\":\"b\",\"type\":\"int\"}]}")));
    }

    public static class RAvroStringableField {
        @Stringable
        int a;
    }

    @Test
    public void testRecordIO() throws IOException {
        Schema schm = ReflectData.get().getSchema(TestReflect.SampleRecord.class);
        ReflectDatumWriter<TestReflect.SampleRecord> writer = new ReflectDatumWriter(schm);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TestReflect.SampleRecord record = new TestReflect.SampleRecord();
        record.x = 5;
        record.y = 10;
        writer.write(record, factory.directBinaryEncoder(out, null));
        ReflectDatumReader<TestReflect.SampleRecord> reader = new ReflectDatumReader(schm);
        TestReflect.SampleRecord decoded = reader.read(null, DecoderFactory.get().binaryDecoder(out.toByteArray(), null));
        Assert.assertEquals(record, decoded);
    }

    public static class AvroEncRecord {
        @AvroEncode(using = DateAsLongEncoding.class)
        Date date;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestReflect.AvroEncRecord))
                return false;

            return date.equals(((TestReflect.AvroEncRecord) (o)).date);
        }
    }

    public static class multipleAnnotationRecord {
        @AvroIgnore
        @Stringable
        Integer i1;

        @AvroIgnore
        @Nullable
        Integer i2;

        @AvroIgnore
        @AvroName("j")
        Integer i3;

        @AvroIgnore
        @AvroEncode(using = DateAsLongEncoding.class)
        Date i4;

        @Stringable
        @Nullable
        Integer i5;

        @Stringable
        @AvroName("j6")
        Integer i6 = 6;

        @Stringable
        @AvroEncode(using = DateAsLongEncoding.class)
        Date i7 = new Date(7L);

        @Nullable
        @AvroName("j8")
        Integer i8;

        @Nullable
        @AvroEncode(using = DateAsLongEncoding.class)
        Date i9;

        @AvroName("j10")
        @AvroEncode(using = DateAsLongEncoding.class)
        Date i10 = new Date(10L);

        @Stringable
        @Nullable
        @AvroName("j11")
        @AvroEncode(using = DateAsLongEncoding.class)
        Date i11;
    }

    @Test
    public void testMultipleAnnotations() throws IOException {
        Schema schm = ReflectData.get().getSchema(TestReflect.multipleAnnotationRecord.class);
        ReflectDatumWriter<TestReflect.multipleAnnotationRecord> writer = new ReflectDatumWriter(schm);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TestReflect.multipleAnnotationRecord record = new TestReflect.multipleAnnotationRecord();
        record.i1 = 1;
        record.i2 = 2;
        record.i3 = 3;
        record.i4 = new Date(4L);
        record.i5 = 5;
        record.i6 = 6;
        record.i7 = new Date(7L);
        record.i8 = 8;
        record.i9 = new Date(9L);
        record.i10 = new Date(10L);
        record.i11 = new Date(11L);
        writer.write(record, factory.directBinaryEncoder(out, null));
        ReflectDatumReader<TestReflect.multipleAnnotationRecord> reader = new ReflectDatumReader(schm);
        TestReflect.multipleAnnotationRecord decoded = reader.read(new TestReflect.multipleAnnotationRecord(), DecoderFactory.get().binaryDecoder(out.toByteArray(), null));
        Assert.assertTrue(((decoded.i1) == null));
        Assert.assertTrue(((decoded.i2) == null));
        Assert.assertTrue(((decoded.i3) == null));
        Assert.assertTrue(((decoded.i4) == null));
        Assert.assertTrue(((decoded.i5) == 5));
        Assert.assertTrue(((decoded.i6) == 6));
        Assert.assertTrue(((decoded.i7.getTime()) == 7));
        Assert.assertTrue(((decoded.i8) == 8));
        Assert.assertTrue(((decoded.i9.getTime()) == 9));
        Assert.assertTrue(((decoded.i10.getTime()) == 10));
        Assert.assertTrue(((decoded.i11.getTime()) == 11));
    }

    @Test
    public void testAvroEncodeInducing() throws IOException {
        Schema schm = ReflectData.get().getSchema(TestReflect.AvroEncRecord.class);
        Assert.assertEquals(schm.toString(), ("{\"type\":\"record\",\"name\":\"AvroEncRecord\",\"namespace" + ("\":\"org.apache.avro.reflect.TestReflect\",\"fields\":[{\"name\":\"date\"," + "\"type\":{\"type\":\"long\",\"CustomEncoding\":\"DateAsLongEncoding\"}}]}")));
    }

    @Test
    public void testAvroEncodeIO() throws IOException {
        Schema schm = ReflectData.get().getSchema(TestReflect.AvroEncRecord.class);
        ReflectDatumWriter<TestReflect.AvroEncRecord> writer = new ReflectDatumWriter(schm);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TestReflect.AvroEncRecord record = new TestReflect.AvroEncRecord();
        record.date = new Date(948833323L);
        writer.write(record, factory.directBinaryEncoder(out, null));
        ReflectDatumReader<TestReflect.AvroEncRecord> reader = new ReflectDatumReader(schm);
        TestReflect.AvroEncRecord decoded = reader.read(new TestReflect.AvroEncRecord(), DecoderFactory.get().binaryDecoder(out.toByteArray(), null));
        Assert.assertEquals(record, decoded);
    }

    @Test
    public void testRecordWithNullIO() throws IOException {
        ReflectData reflectData = AllowNull.get();
        Schema schm = reflectData.getSchema(TestReflect.SampleRecord.AnotherSampleRecord.class);
        ReflectDatumWriter<TestReflect.SampleRecord.AnotherSampleRecord> writer = new ReflectDatumWriter(schm);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // keep record.a null and see if that works
        Encoder e = factory.directBinaryEncoder(out, null);
        TestReflect.SampleRecord.AnotherSampleRecord a = new TestReflect.SampleRecord.AnotherSampleRecord();
        writer.write(a, e);
        TestReflect.SampleRecord.AnotherSampleRecord b = new TestReflect.SampleRecord.AnotherSampleRecord(10);
        writer.write(b, e);
        e.flush();
        ReflectDatumReader<TestReflect.SampleRecord.AnotherSampleRecord> reader = new ReflectDatumReader(schm);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Decoder d = DecoderFactory.get().binaryDecoder(in, null);
        TestReflect.SampleRecord.AnotherSampleRecord decoded = reader.read(null, d);
        Assert.assertEquals(a, decoded);
        decoded = reader.read(null, d);
        Assert.assertEquals(b, decoded);
    }

    @Test
    public void testDisableUnsafe() throws Exception {
        String saved = System.getProperty("avro.disable.unsafe");
        try {
            System.setProperty("avro.disable.unsafe", "true");
            ACCESSOR_CACHE.remove(TestReflect.multipleAnnotationRecord.class);
            ACCESSOR_CACHE.remove(TestReflect.SampleRecord.AnotherSampleRecord.class);
            ReflectionUtil.resetFieldAccess();
            testMultipleAnnotations();
            testRecordWithNullIO();
        } finally {
            if (saved == null)
                System.clearProperty("avro.disable.unsafe");
            else
                System.setProperty("avro.disable.unsafe", saved);

            ACCESSOR_CACHE.remove(TestReflect.multipleAnnotationRecord.class);
            ACCESSOR_CACHE.remove(TestReflect.SampleRecord.AnotherSampleRecord.class);
            ReflectionUtil.resetFieldAccess();
        }
    }

    public static class SampleRecord {
        public int x = 1;

        private int y = 2;

        @Override
        public int hashCode() {
            return (x) + (y);
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            final TestReflect.SampleRecord other = ((TestReflect.SampleRecord) (obj));
            if ((x) != (other.x))
                return false;

            if ((y) != (other.y))
                return false;

            return true;
        }

        public static class AnotherSampleRecord {
            private Integer a = null;

            private TestReflect.SampleRecord s = null;

            public AnotherSampleRecord() {
            }

            AnotherSampleRecord(Integer a) {
                this.a = a;
                this.s = new TestReflect.SampleRecord();
            }

            @Override
            public int hashCode() {
                int hash = ((a) != null) ? a.hashCode() : 0;
                hash += ((s) != null) ? s.hashCode() : 0;
                return hash;
            }

            @Override
            public boolean equals(Object other) {
                if (other instanceof TestReflect.SampleRecord.AnotherSampleRecord) {
                    TestReflect.SampleRecord.AnotherSampleRecord o = ((TestReflect.SampleRecord.AnotherSampleRecord) (other));
                    if ((((((this.a) == null) && ((o.a) != null)) || (((this.a) != null) && (!(this.a.equals(o.a))))) || (((this.s) == null) && ((o.s) != null))) || (((this.s) != null) && (!(this.s.equals(o.s))))) {
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public static class X {
        int i;
    }

    public static class B1 {
        TestReflect.X x;
    }

    public static class B2 {
        TestReflect.X x;
    }

    public static class A {
        TestReflect.B1 b1;

        TestReflect.B2 b2;
    }

    public static interface C {
        void foo(TestReflect.A a);
    }

    @Test
    public void testForwardReference() {
        ReflectData data = ReflectData.get();
        Protocol reflected = data.getProtocol(TestReflect.C.class);
        Protocol reparsed = Protocol.parse(reflected.toString());
        Assert.assertEquals(reflected, reparsed);
        assert reparsed.getTypes().contains(data.getSchema(TestReflect.A.class));
        assert reparsed.getTypes().contains(data.getSchema(TestReflect.B1.class));
        assert reparsed.getTypes().contains(data.getSchema(TestReflect.B2.class));
        assert reparsed.getTypes().contains(data.getSchema(TestReflect.X.class));
    }

    public static interface P3 {
        void m1();

        void m1(int x);
    }

    @Test(expected = AvroTypeException.class)
    public void testOverloadedMethod() {
        ReflectData.get().getProtocol(TestReflect.P3.class);
    }

    @Test
    public void testNoPackageSchema() throws Exception {
        ReflectData.get().getSchema(Class.forName("NoPackage"));
    }

    @Test
    public void testNoPackageProtocol() throws Exception {
        ReflectData.get().getProtocol(Class.forName("NoPackage"));
    }

    public static class Y {
        int i;
    }

    /**
     * Test nesting of reflect data within generic.
     */
    @Test
    public void testReflectWithinGeneric() throws Exception {
        ReflectData data = ReflectData.get();
        // define a record with a field that's a specific Y
        Schema schema = Schema.createRecord("Foo", "", "x.y.z", false);
        List<Schema.Field> fields = new ArrayList<>();
        fields.add(new Schema.Field("f", data.getSchema(TestReflect.Y.class), "", null));
        schema.setFields(fields);
        // create a generic instance of this record
        TestReflect.Y y = new TestReflect.Y();
        y.i = 1;
        GenericData.Record record = new GenericData.Record(schema);
        record.put("f", y);
        // test that this instance can be written & re-read
        TestReflect.checkBinary(schema, record);
    }

    @Test
    public void testPrimitiveArray() throws Exception {
        testPrimitiveArrays(false);
    }

    @Test
    public void testPrimitiveArrayBlocking() throws Exception {
        testPrimitiveArrays(true);
    }

    /**
     * Test union of null and an array.
     */
    @Test
    public void testNullArray() throws Exception {
        String json = "[{\"type\":\"array\", \"items\": \"long\"}, \"null\"]";
        Schema schema = new Schema.Parser().parse(json);
        TestReflect.checkBinary(schema, null);
    }

    /**
     * Test stringable classes.
     */
    @Test
    public void testStringables() throws Exception {
        checkStringable(BigDecimal.class, "10");
        checkStringable(BigInteger.class, "20");
        checkStringable(URI.class, "foo://bar:9000/baz");
        checkStringable(URL.class, "http://bar:9000/baz");
        checkStringable(File.class, "foo.bar");
    }

    public static class M1 {
        Map<Integer, String> integerKeyMap;

        Map<BigInteger, String> bigIntegerKeyMap;

        Map<BigDecimal, String> bigDecimalKeyMap;

        Map<File, String> fileKeyMap;
    }

    /**
     * Test Map with stringable key classes.
     */
    @Test
    public void testStringableMapKeys() throws Exception {
        TestReflect.M1 record = new TestReflect.M1();
        record.integerKeyMap = new HashMap<>(1);
        record.integerKeyMap.put(10, "foo");
        record.bigIntegerKeyMap = new HashMap<>(1);
        record.bigIntegerKeyMap.put(BigInteger.TEN, "bar");
        record.bigDecimalKeyMap = new HashMap<>(1);
        record.bigDecimalKeyMap.put(BigDecimal.ONE, "bigDecimal");
        record.fileKeyMap = new HashMap<>(1);
        record.fileKeyMap.put(new File("foo.bar"), "file");
        ReflectData data = new ReflectData().addStringable(Integer.class);
        TestReflect.checkBinary(data, data.getSchema(TestReflect.M1.class), record, true);
    }

    public static class NullableStringable {
        BigDecimal number;
    }

    @Test
    public void testNullableStringableField() throws Exception {
        TestReflect.NullableStringable datum = new TestReflect.NullableStringable();
        datum.number = BigDecimal.TEN;
        Schema schema = AllowNull.get().getSchema(TestReflect.NullableStringable.class);
        TestReflect.checkBinary(schema, datum);
    }

    /**
     * Test that the error message contains the name of the class.
     */
    @Test
    public void testReflectFieldError() throws Exception {
        Object datum = "";
        try {
            ReflectData.get().getField(datum, "notAFieldOfString", 0);
        } catch (AvroRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains(datum.getClass().getName()));
        }
    }

    @AvroAlias(alias = "a", space = "b")
    private static class AliasA {}

    @AvroAlias(alias = "a", space = "")
    private static class AliasB {}

    @AvroAlias(alias = "a")
    private static class AliasC {}

    @Test
    public void testAvroAliasOnClass() {
        check(TestReflect.AliasA.class, "{\"type\":\"record\",\"name\":\"AliasA\",\"namespace\":\"org.apache.avro.reflect.TestReflect\",\"fields\":[],\"aliases\":[\"b.a\"]}");
        check(TestReflect.AliasB.class, "{\"type\":\"record\",\"name\":\"AliasB\",\"namespace\":\"org.apache.avro.reflect.TestReflect\",\"fields\":[],\"aliases\":[\"a\"]}");
        check(TestReflect.AliasC.class, "{\"type\":\"record\",\"name\":\"AliasC\",\"namespace\":\"org.apache.avro.reflect.TestReflect\",\"fields\":[],\"aliases\":[\"a\"]}");
    }

    private static class Z {}

    @Test
    public void testDollarTerminatedNamespaceCompatibility() {
        ReflectData data = ReflectData.get();
        Schema s = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Z\",\"namespace\":\"org.apache.avro.reflect.TestReflect$\",\"fields\":[]}");
        Assert.assertEquals(data.getSchema(data.getClass(s)).toString(), "{\"type\":\"record\",\"name\":\"Z\",\"namespace\":\"org.apache.avro.reflect.TestReflect\",\"fields\":[]}");
    }

    private static class ClassWithAliasOnField {
        @AvroAlias(alias = "aliasName")
        int primitiveField;
    }

    private static class ClassWithAliasAndNamespaceOnField {
        @AvroAlias(alias = "aliasName", space = "forbidden.space.entry")
        int primitiveField;
    }

    @Test
    public void testAvroAliasOnField() {
        Schema expectedSchema = SchemaBuilder.record(TestReflect.ClassWithAliasOnField.class.getSimpleName()).namespace("org.apache.avro.reflect.TestReflect").fields().name("primitiveField").aliases("aliasName").type(Schema.create(org.apache.avro.Schema.Type.INT)).noDefault().endRecord();
        check(TestReflect.ClassWithAliasOnField.class, expectedSchema.toString());
    }

    @Test(expected = org.apache.avro.AvroRuntimeException.class)
    public void namespaceDefinitionOnFieldAliasMustThrowException() {
        ReflectData.get().getSchema(TestReflect.ClassWithAliasAndNamespaceOnField.class);
    }

    private static class DefaultTest {
        @AvroDefault("1")
        int foo;
    }

    @Test
    public void testAvroDefault() {
        check(TestReflect.DefaultTest.class, ("{\"type\":\"record\",\"name\":\"DefaultTest\"," + ("\"namespace\":\"org.apache.avro.reflect.TestReflect\",\"fields\":[" + "{\"name\":\"foo\",\"type\":\"int\",\"default\":1}]}")));
    }

    public static class NullableBytesTest {
        @Nullable
        byte[] bytes;

        NullableBytesTest() {
        }

        NullableBytesTest(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof TestReflect.NullableBytesTest) && (Arrays.equals(((TestReflect.NullableBytesTest) (obj)).bytes, this.bytes));
        }
    }

    @Test
    public void testNullableByteArrayNotNullValue() throws Exception {
        checkReadWrite(new TestReflect.NullableBytesTest("foo".getBytes()));
    }

    @Test
    public void testNullableByteArrayNullValue() throws Exception {
        checkReadWrite(new TestReflect.NullableBytesTest());
    }

    private enum DocTestEnum {

        ENUM_1,
        ENUM_2;}

    @AvroDoc("DocTest class docs")
    private static class DocTest {
        @AvroDoc("Some Documentation")
        int foo;

        @AvroDoc("Some other Documentation")
        TestReflect.DocTestEnum enums;

        @AvroDoc("And again")
        TestReflect.DefaultTest defaultTest;
    }

    @Test
    public void testAvroDoc() {
        check(TestReflect.DocTest.class, ("{\"type\":\"record\",\"name\":\"DocTest\",\"namespace\":\"org.apache.avro.reflect.TestReflect\"," + ((((("\"doc\":\"DocTest class docs\"," + "\"fields\":[{\"name\":\"foo\",\"type\":\"int\",\"doc\":\"Some Documentation\"},") + "{\"name\":\"enums\",\"type\":{\"type\":\"enum\",\"name\":\"DocTestEnum\",") + "\"symbols\":[\"ENUM_1\",\"ENUM_2\"]},\"doc\":\"Some other Documentation\"},") + "{\"name\":\"defaultTest\",\"type\":{\"type\":\"record\",\"name\":\"DefaultTest\",") + "\"fields\":[{\"name\":\"foo\",\"type\":\"int\",\"default\":1}]},\"doc\":\"And again\"}]}")));
    }
}

