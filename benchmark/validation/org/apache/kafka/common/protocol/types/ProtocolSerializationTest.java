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
package org.apache.kafka.common.protocol.types;


import Type.BOOLEAN;
import Type.BYTES;
import Type.INT16;
import Type.INT32;
import Type.INT64;
import Type.INT8;
import Type.NULLABLE_BYTES;
import Type.NULLABLE_STRING;
import Type.STRING;
import Type.VARINT;
import Type.VARLONG;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import static Type.INT32;
import static Type.INT8;
import static Type.NULLABLE_STRING;
import static Type.STRING;


public class ProtocolSerializationTest {
    private Schema schema;

    private Struct struct;

    @Test
    public void testSimple() {
        check(BOOLEAN, false);
        check(BOOLEAN, true);
        check(INT8, ((byte) (-111)));
        check(INT16, ((short) (-11111)));
        check(INT32, (-11111111));
        check(INT64, (-11111111111L));
        check(STRING, "");
        check(STRING, "hello");
        check(STRING, "A\u00ea\u00f1\u00fcC");
        check(NULLABLE_STRING, null);
        check(NULLABLE_STRING, "");
        check(NULLABLE_STRING, "hello");
        check(BYTES, ByteBuffer.allocate(0));
        check(BYTES, ByteBuffer.wrap("abcd".getBytes()));
        check(NULLABLE_BYTES, null);
        check(NULLABLE_BYTES, ByteBuffer.allocate(0));
        check(NULLABLE_BYTES, ByteBuffer.wrap("abcd".getBytes()));
        check(VARINT, Integer.MAX_VALUE);
        check(VARINT, Integer.MIN_VALUE);
        check(VARLONG, Long.MAX_VALUE);
        check(VARLONG, Long.MIN_VALUE);
        check(new ArrayOf(INT32), new Object[]{ 1, 2, 3, 4 });
        check(new ArrayOf(STRING), new Object[]{  });
        check(new ArrayOf(STRING), new Object[]{ "hello", "there", "beautiful" });
        check(ArrayOf.nullable(STRING), null);
    }

    @Test
    public void testNulls() {
        for (BoundField f : this.schema.fields()) {
            Object o = this.struct.get(f);
            try {
                this.struct.set(f, null);
                this.struct.validate();
                if (!(f.def.type.isNullable()))
                    Assert.fail("Should not allow serialization of null value.");

            } catch (SchemaException e) {
                Assert.assertFalse(f.def.type.isNullable());
            } finally {
                this.struct.set(f, o);
            }
        }
    }

    @Test
    public void testDefault() {
        Schema schema = new Schema(new Field("field", INT32, "doc", 42));
        Struct struct = new Struct(schema);
        Assert.assertEquals("Should get the default value", 42, struct.get("field"));
        struct.validate();// should be valid even with missing value

    }

    @Test
    public void testNullableDefault() {
        checkNullableDefault(NULLABLE_BYTES, ByteBuffer.allocate(0));
        checkNullableDefault(NULLABLE_STRING, "default");
    }

    @Test
    public void testReadArraySizeTooLarge() {
        Type type = new ArrayOf(INT8);
        int size = 10;
        ByteBuffer invalidBuffer = ByteBuffer.allocate((4 + size));
        invalidBuffer.putInt(Integer.MAX_VALUE);
        for (int i = 0; i < size; i++)
            invalidBuffer.put(((byte) (i)));

        invalidBuffer.rewind();
        try {
            type.read(invalidBuffer);
            Assert.fail("Array size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
    }

    @Test
    public void testReadNegativeArraySize() {
        Type type = new ArrayOf(INT8);
        int size = 10;
        ByteBuffer invalidBuffer = ByteBuffer.allocate((4 + size));
        invalidBuffer.putInt((-1));
        for (int i = 0; i < size; i++)
            invalidBuffer.put(((byte) (i)));

        invalidBuffer.rewind();
        try {
            type.read(invalidBuffer);
            Assert.fail("Array size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
    }

    @Test
    public void testReadStringSizeTooLarge() {
        byte[] stringBytes = "foo".getBytes();
        ByteBuffer invalidBuffer = ByteBuffer.allocate((2 + (stringBytes.length)));
        invalidBuffer.putShort(((short) ((stringBytes.length) * 5)));
        invalidBuffer.put(stringBytes);
        invalidBuffer.rewind();
        try {
            STRING.read(invalidBuffer);
            Assert.fail("String size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
        invalidBuffer.rewind();
        try {
            NULLABLE_STRING.read(invalidBuffer);
            Assert.fail("String size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
    }

    @Test
    public void testReadNegativeStringSize() {
        byte[] stringBytes = "foo".getBytes();
        ByteBuffer invalidBuffer = ByteBuffer.allocate((2 + (stringBytes.length)));
        invalidBuffer.putShort(((short) (-1)));
        invalidBuffer.put(stringBytes);
        invalidBuffer.rewind();
        try {
            STRING.read(invalidBuffer);
            Assert.fail("String size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
    }

    @Test
    public void testReadBytesSizeTooLarge() {
        byte[] stringBytes = "foo".getBytes();
        ByteBuffer invalidBuffer = ByteBuffer.allocate((4 + (stringBytes.length)));
        invalidBuffer.putInt(((stringBytes.length) * 5));
        invalidBuffer.put(stringBytes);
        invalidBuffer.rewind();
        try {
            BYTES.read(invalidBuffer);
            Assert.fail("Bytes size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
        invalidBuffer.rewind();
        try {
            NULLABLE_BYTES.read(invalidBuffer);
            Assert.fail("Bytes size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
    }

    @Test
    public void testReadNegativeBytesSize() {
        byte[] stringBytes = "foo".getBytes();
        ByteBuffer invalidBuffer = ByteBuffer.allocate((4 + (stringBytes.length)));
        invalidBuffer.putInt((-20));
        invalidBuffer.put(stringBytes);
        invalidBuffer.rewind();
        try {
            BYTES.read(invalidBuffer);
            Assert.fail("Bytes size not validated");
        } catch (SchemaException e) {
            // Expected exception
        }
    }

    @Test
    public void testToString() {
        String structStr = this.struct.toString();
        Assert.assertNotNull("Struct string should not be null.", structStr);
        Assert.assertFalse("Struct string should not be empty.", structStr.isEmpty());
    }

    @Test
    public void testStructEquals() {
        Schema schema = new Schema(new Field("field1", NULLABLE_STRING), new Field("field2", NULLABLE_STRING));
        Struct emptyStruct1 = new Struct(schema);
        Struct emptyStruct2 = new Struct(schema);
        Assert.assertEquals(emptyStruct1, emptyStruct2);
        Struct mostlyEmptyStruct = set("field1", "foo");
        Assert.assertNotEquals(emptyStruct1, mostlyEmptyStruct);
        Assert.assertNotEquals(mostlyEmptyStruct, emptyStruct1);
    }
}

