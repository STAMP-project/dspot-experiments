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
package org.apache.kafka.common.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class ByteUtilsTest {
    private final byte x00 = 0;

    private final byte x01 = 1;

    private final byte x02 = 2;

    private final byte x0F = 15;

    private final byte x7E = 126;

    private final byte x7F = 127;

    private final byte xFF = ((byte) (255));

    private final byte x80 = ((byte) (128));

    private final byte x81 = ((byte) (129));

    private final byte xFE = ((byte) (254));

    @Test
    public void testReadUnsignedIntLEFromArray() {
        byte[] array1 = new byte[]{ 1, 2, 3, 4, 5 };
        Assert.assertEquals(67305985, ByteUtils.readUnsignedIntLE(array1, 0));
        Assert.assertEquals(84148994, ByteUtils.readUnsignedIntLE(array1, 1));
        byte[] array2 = new byte[]{ ((byte) (241)), ((byte) (242)), ((byte) (243)), ((byte) (244)), ((byte) (245)), ((byte) (246)) };
        Assert.assertEquals(-185339151, ByteUtils.readUnsignedIntLE(array2, 0));
        Assert.assertEquals(-151653133, ByteUtils.readUnsignedIntLE(array2, 2));
    }

    @Test
    public void testReadUnsignedIntLEFromInputStream() throws IOException {
        byte[] array1 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        ByteArrayInputStream is1 = new ByteArrayInputStream(array1);
        Assert.assertEquals(67305985, ByteUtils.readUnsignedIntLE(is1));
        Assert.assertEquals(134678021, ByteUtils.readUnsignedIntLE(is1));
        byte[] array2 = new byte[]{ ((byte) (241)), ((byte) (242)), ((byte) (243)), ((byte) (244)), ((byte) (245)), ((byte) (246)), ((byte) (247)), ((byte) (248)) };
        ByteArrayInputStream is2 = new ByteArrayInputStream(array2);
        Assert.assertEquals(-185339151, ByteUtils.readUnsignedIntLE(is2));
        Assert.assertEquals(-117967115, ByteUtils.readUnsignedIntLE(is2));
    }

    @Test
    public void testReadUnsignedInt() {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        long writeValue = 133444;
        ByteUtils.writeUnsignedInt(buffer, writeValue);
        buffer.flip();
        long readValue = ByteUtils.readUnsignedInt(buffer);
        Assert.assertEquals(writeValue, readValue);
    }

    @Test
    public void testWriteUnsignedIntLEToArray() {
        int value1 = 67305985;
        byte[] array1 = new byte[4];
        ByteUtils.writeUnsignedIntLE(array1, 0, value1);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4 }, array1);
        array1 = new byte[8];
        ByteUtils.writeUnsignedIntLE(array1, 2, value1);
        Assert.assertArrayEquals(new byte[]{ 0, 0, 1, 2, 3, 4, 0, 0 }, array1);
        int value2 = -185339151;
        byte[] array2 = new byte[4];
        ByteUtils.writeUnsignedIntLE(array2, 0, value2);
        Assert.assertArrayEquals(new byte[]{ ((byte) (241)), ((byte) (242)), ((byte) (243)), ((byte) (244)) }, array2);
        array2 = new byte[8];
        ByteUtils.writeUnsignedIntLE(array2, 2, value2);
        Assert.assertArrayEquals(new byte[]{ 0, 0, ((byte) (241)), ((byte) (242)), ((byte) (243)), ((byte) (244)), 0, 0 }, array2);
    }

    @Test
    public void testWriteUnsignedIntLEToOutputStream() throws IOException {
        int value1 = 67305985;
        ByteArrayOutputStream os1 = new ByteArrayOutputStream();
        ByteUtils.writeUnsignedIntLE(os1, value1);
        ByteUtils.writeUnsignedIntLE(os1, value1);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 1, 2, 3, 4 }, os1.toByteArray());
        int value2 = -185339151;
        ByteArrayOutputStream os2 = new ByteArrayOutputStream();
        ByteUtils.writeUnsignedIntLE(os2, value2);
        Assert.assertArrayEquals(new byte[]{ ((byte) (241)), ((byte) (242)), ((byte) (243)), ((byte) (244)) }, os2.toByteArray());
    }

    @Test
    public void testVarintSerde() throws Exception {
        assertVarintSerde(0, new byte[]{ x00 });
        assertVarintSerde((-1), new byte[]{ x01 });
        assertVarintSerde(1, new byte[]{ x02 });
        assertVarintSerde(63, new byte[]{ x7E });
        assertVarintSerde((-64), new byte[]{ x7F });
        assertVarintSerde(64, new byte[]{ x80, x01 });
        assertVarintSerde((-65), new byte[]{ x81, x01 });
        assertVarintSerde(8191, new byte[]{ xFE, x7F });
        assertVarintSerde((-8192), new byte[]{ xFF, x7F });
        assertVarintSerde(8192, new byte[]{ x80, x80, x01 });
        assertVarintSerde((-8193), new byte[]{ x81, x80, x01 });
        assertVarintSerde(1048575, new byte[]{ xFE, xFF, x7F });
        assertVarintSerde((-1048576), new byte[]{ xFF, xFF, x7F });
        assertVarintSerde(1048576, new byte[]{ x80, x80, x80, x01 });
        assertVarintSerde((-1048577), new byte[]{ x81, x80, x80, x01 });
        assertVarintSerde(134217727, new byte[]{ xFE, xFF, xFF, x7F });
        assertVarintSerde((-134217728), new byte[]{ xFF, xFF, xFF, x7F });
        assertVarintSerde(134217728, new byte[]{ x80, x80, x80, x80, x01 });
        assertVarintSerde((-134217729), new byte[]{ x81, x80, x80, x80, x01 });
        assertVarintSerde(Integer.MAX_VALUE, new byte[]{ xFE, xFF, xFF, xFF, x0F });
        assertVarintSerde(Integer.MIN_VALUE, new byte[]{ xFF, xFF, xFF, xFF, x0F });
    }

    @Test
    public void testVarlongSerde() throws Exception {
        assertVarlongSerde(0, new byte[]{ x00 });
        assertVarlongSerde((-1), new byte[]{ x01 });
        assertVarlongSerde(1, new byte[]{ x02 });
        assertVarlongSerde(63, new byte[]{ x7E });
        assertVarlongSerde((-64), new byte[]{ x7F });
        assertVarlongSerde(64, new byte[]{ x80, x01 });
        assertVarlongSerde((-65), new byte[]{ x81, x01 });
        assertVarlongSerde(8191, new byte[]{ xFE, x7F });
        assertVarlongSerde((-8192), new byte[]{ xFF, x7F });
        assertVarlongSerde(8192, new byte[]{ x80, x80, x01 });
        assertVarlongSerde((-8193), new byte[]{ x81, x80, x01 });
        assertVarlongSerde(1048575, new byte[]{ xFE, xFF, x7F });
        assertVarlongSerde((-1048576), new byte[]{ xFF, xFF, x7F });
        assertVarlongSerde(1048576, new byte[]{ x80, x80, x80, x01 });
        assertVarlongSerde((-1048577), new byte[]{ x81, x80, x80, x01 });
        assertVarlongSerde(134217727, new byte[]{ xFE, xFF, xFF, x7F });
        assertVarlongSerde((-134217728), new byte[]{ xFF, xFF, xFF, x7F });
        assertVarlongSerde(134217728, new byte[]{ x80, x80, x80, x80, x01 });
        assertVarlongSerde((-134217729), new byte[]{ x81, x80, x80, x80, x01 });
        assertVarlongSerde(Integer.MAX_VALUE, new byte[]{ xFE, xFF, xFF, xFF, x0F });
        assertVarlongSerde(Integer.MIN_VALUE, new byte[]{ xFF, xFF, xFF, xFF, x0F });
        assertVarlongSerde(17179869183L, new byte[]{ xFE, xFF, xFF, xFF, x7F });
        assertVarlongSerde((-17179869184L), new byte[]{ xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde(17179869184L, new byte[]{ x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde((-17179869185L), new byte[]{ x81, x80, x80, x80, x80, x01 });
        assertVarlongSerde(2199023255551L, new byte[]{ xFE, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde((-2199023255552L), new byte[]{ xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde(2199023255552L, new byte[]{ x80, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde((-2199023255553L), new byte[]{ x81, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde(281474976710655L, new byte[]{ xFE, xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde((-281474976710656L), new byte[]{ xFF, xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde(281474976710656L, new byte[]{ x80, x80, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde((-281474976710657L), new byte[]{ x81, x80, x80, x80, x80, x80, x80, 1 });
        assertVarlongSerde(36028797018963967L, new byte[]{ xFE, xFF, xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde((-36028797018963968L), new byte[]{ xFF, xFF, xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde(36028797018963968L, new byte[]{ x80, x80, x80, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde((-36028797018963969L), new byte[]{ x81, x80, x80, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde(4611686018427387903L, new byte[]{ xFE, xFF, xFF, xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde((-4611686018427387904L), new byte[]{ xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, x7F });
        assertVarlongSerde(4611686018427387904L, new byte[]{ x80, x80, x80, x80, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde((-4611686018427387905L), new byte[]{ x81, x80, x80, x80, x80, x80, x80, x80, x80, x01 });
        assertVarlongSerde(Long.MAX_VALUE, new byte[]{ xFE, xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, x01 });
        assertVarlongSerde(Long.MIN_VALUE, new byte[]{ xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, x01 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVarint() {
        // varint encoding has one overflow byte
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ xFF, xFF, xFF, xFF, xFF, x01 });
        ByteUtils.readVarint(buf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVarlong() {
        // varlong encoding has one overflow byte
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, xFF, x01 });
        ByteUtils.readVarlong(buf);
    }
}

