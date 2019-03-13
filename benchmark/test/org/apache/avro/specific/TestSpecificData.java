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
package org.apache.avro.specific;


import Type.INT;
import Type.STRING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.junit.Assert;
import org.junit.Test;


/* If integerClass is primitive, reflection to find method will
result in a NoSuchMethodException in the case of a UNION schema
 */
public class TestSpecificData {
    private Class<?> intClass;

    private Class<?> integerClass;

    @Test
    public void testClassTypes() {
        Assert.assertTrue(intClass.isPrimitive());
        Assert.assertFalse(integerClass.isPrimitive());
    }

    @Test
    public void testPrimitiveParam() throws Exception {
        Assert.assertNotNull(TestSpecificData.Reflection.class.getMethod("primitive", intClass));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testPrimitiveParamError() throws Exception {
        TestSpecificData.Reflection.class.getMethod("primitiveWrapper", intClass);
    }

    @Test
    public void testPrimitiveWrapperParam() throws Exception {
        Assert.assertNotNull(TestSpecificData.Reflection.class.getMethod("primitiveWrapper", integerClass));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testPrimitiveWrapperParamError() throws Exception {
        TestSpecificData.Reflection.class.getMethod("primitive", integerClass);
    }

    static class Reflection {
        public void primitive(int i) {
        }

        public void primitiveWrapper(Integer i) {
        }
    }

    public static class TestRecord extends SpecificRecordBase {
        private static final Schema SCHEMA = Schema.createRecord("TestRecord", null, null, false);

        static {
            List<Field> fields = new ArrayList<>();
            fields.add(new Field("x", Schema.create(INT), null, null));
            Schema stringSchema = Schema.create(STRING);
            GenericData.setStringType(stringSchema, GenericData.StringType.String);
            fields.add(new Field("y", stringSchema, null, null));
            TestSpecificData.TestRecord.SCHEMA.setFields(fields);
        }

        private int x;

        private String y;

        @Override
        public void put(int i, Object v) {
            switch (i) {
                case 0 :
                    x = ((Integer) (v));
                    break;
                case 1 :
                    y = ((String) (v));
                    break;
                default :
                    throw new RuntimeException();
            }
        }

        @Override
        public Object get(int i) {
            switch (i) {
                case 0 :
                    return x;
                case 1 :
                    return y;
            }
            throw new RuntimeException();
        }

        @Override
        public Schema getSchema() {
            return TestSpecificData.TestRecord.SCHEMA;
        }
    }

    @Test
    public void testSpecificRecordBase() {
        final TestSpecificData.TestRecord record = new TestSpecificData.TestRecord();
        record.put("x", 1);
        record.put("y", "str");
        Assert.assertEquals(1, record.get("x"));
        Assert.assertEquals("str", record.get("y"));
    }

    @Test
    public void testExternalizeable() throws Exception {
        final TestSpecificData.TestRecord before = new TestSpecificData.TestRecord();
        before.put("x", 1);
        before.put("y", "str");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(before);
        out.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        TestSpecificData.TestRecord after = ((TestSpecificData.TestRecord) (in.readObject()));
        Assert.assertEquals(before, after);
    }

    /**
     * Tests that non Stringable datum are rejected by specific writers.
     */
    @Test
    public void testNonStringable() throws Exception {
        final Schema string = Schema.create(STRING);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Encoder encoder = EncoderFactory.get().directBinaryEncoder(baos, null);
        final DatumWriter<Object> writer = new SpecificDatumWriter(string);
        try {
            writer.write(new Object(), encoder);
            Assert.fail("Non stringable object should be rejected.");
        } catch (ClassCastException cce) {
            // Expected error
        }
    }
}

