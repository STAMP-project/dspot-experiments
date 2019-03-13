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


import GenericData.Record;
import Kind.BAR;
import Kind.BAZ;
import java.nio.ByteBuffer;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.test.MD5;
import org.apache.avro.test.TestRecord;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;


public class TestCompare {
    @Test
    public void testNull() throws Exception {
        Schema schema = Schema.parse("\"null\"");
        byte[] b = TestCompare.render(null, schema, new org.apache.avro.generic.GenericDatumWriter());
        Assert.assertEquals(0, BinaryData.compare(b, 0, b, 0, schema));
    }

    @Test
    public void testBoolean() throws Exception {
        TestCompare.check("\"boolean\"", Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testString() throws Exception {
        TestCompare.check("\"string\"", new Utf8(""), new Utf8("a"));
        TestCompare.check("\"string\"", new Utf8("a"), new Utf8("b"));
        TestCompare.check("\"string\"", new Utf8("a"), new Utf8("ab"));
        TestCompare.check("\"string\"", new Utf8("ab"), new Utf8("b"));
    }

    @Test
    public void testBytes() throws Exception {
        TestCompare.check("\"bytes\"", ByteBuffer.wrap(new byte[]{  }), ByteBuffer.wrap(new byte[]{ 1 }));
        TestCompare.check("\"bytes\"", ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }));
        TestCompare.check("\"bytes\"", ByteBuffer.wrap(new byte[]{ 1, 2 }), ByteBuffer.wrap(new byte[]{ 2 }));
    }

    @Test
    public void testInt() throws Exception {
        TestCompare.check("\"int\"", new Integer((-1)), new Integer(0));
        TestCompare.check("\"int\"", new Integer(0), new Integer(1));
    }

    @Test
    public void testLong() throws Exception {
        TestCompare.check("\"long\"", new Long(11), new Long(12));
        TestCompare.check("\"long\"", new Long((-1)), new Long(1));
    }

    @Test
    public void testFloat() throws Exception {
        TestCompare.check("\"float\"", new Float(1.1), new Float(1.2));
        TestCompare.check("\"float\"", new Float((-1.1)), new Float(1.0));
    }

    @Test
    public void testDouble() throws Exception {
        TestCompare.check("\"double\"", new Double(1.2), new Double(1.3));
        TestCompare.check("\"double\"", new Double((-1.2)), new Double(1.3));
    }

    @Test
    public void testArray() throws Exception {
        String json = "{\"type\":\"array\", \"items\": \"long\"}";
        Schema schema = Schema.parse(json);
        GenericArray<Long> a1 = new GenericData.Array<>(1, schema);
        a1.add(1L);
        GenericArray<Long> a2 = new GenericData.Array<>(1, schema);
        a2.add(1L);
        a2.add(0L);
        TestCompare.check(json, a1, a2);
    }

    @Test
    public void testRecord() throws Exception {
        String fields = " \"fields\":[" + (("{\"name\":\"f\",\"type\":\"int\",\"order\":\"ignore\"}," + "{\"name\":\"g\",\"type\":\"int\",\"order\":\"descending\"},") + "{\"name\":\"h\",\"type\":\"int\"}]}");
        String recordJson = "{\"type\":\"record\", \"name\":\"Test\"," + fields;
        Schema schema = Schema.parse(recordJson);
        GenericData.Record r1 = new GenericData.Record(schema);
        r1.put("f", 1);
        r1.put("g", 13);
        r1.put("h", 41);
        GenericData.Record r2 = new GenericData.Record(schema);
        r2.put("f", 0);
        r2.put("g", 12);
        r2.put("h", 41);
        TestCompare.check(recordJson, r1, r2);
        r2.put("f", 0);
        r2.put("g", 13);
        r2.put("h", 42);
        TestCompare.check(recordJson, r1, r2);
        String record2Json = "{\"type\":\"record\", \"name\":\"Test2\"," + fields;
        Schema schema2 = Schema.parse(record2Json);
        GenericData.Record r3 = new GenericData.Record(schema2);
        r3.put("f", 1);
        r3.put("g", 13);
        r3.put("h", 41);
        assert !(r1.equals(r3));// same fields, diff name

    }

    @Test
    public void testEnum() throws Exception {
        String json = "{\"type\":\"enum\", \"name\":\"Test\",\"symbols\": [\"A\", \"B\"]}";
        Schema schema = Schema.parse(json);
        TestCompare.check(json, new GenericData.EnumSymbol(schema, "A"), new GenericData.EnumSymbol(schema, "B"));
    }

    @Test
    public void testFixed() throws Exception {
        String json = "{\"type\": \"fixed\", \"name\":\"Test\", \"size\": 1}";
        Schema schema = Schema.parse(json);
        TestCompare.check(json, new GenericData.Fixed(schema, new byte[]{ ((byte) ('a')) }), new GenericData.Fixed(schema, new byte[]{ ((byte) ('b')) }));
    }

    @Test
    public void testUnion() throws Exception {
        TestCompare.check("[\"string\", \"long\"]", new Utf8("a"), new Utf8("b"), false);
        TestCompare.check("[\"string\", \"long\"]", new Long(1), new Long(2), false);
        TestCompare.check("[\"string\", \"long\"]", new Utf8("a"), new Long(1), false);
    }

    @Test
    public void testSpecificRecord() throws Exception {
        TestRecord s1 = new TestRecord();
        TestRecord s2 = new TestRecord();
        s1.setName("foo");
        s1.setKind(BAZ);
        s1.setHash(new MD5(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5 }));
        s2.setName("bar");
        s2.setKind(BAR);
        s2.setHash(new MD5(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 6 }));
        Schema schema = SpecificData.get().getSchema(TestRecord.class);
        TestCompare.check(schema, s1, s2, true, new org.apache.avro.specific.SpecificDatumWriter(schema), SpecificData.get());
        s2.setKind(BAZ);
        TestCompare.check(schema, s1, s2, true, new org.apache.avro.specific.SpecificDatumWriter(schema), SpecificData.get());
    }
}

