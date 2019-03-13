/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static BufferUtil.TEMP_BUFFER_SIZE;


public class BufferUtilTest {
    @Test
    public void testToInt() throws Exception {
        ByteBuffer[] buf = new ByteBuffer[]{ BufferUtil.toBuffer("0"), BufferUtil.toBuffer(" 42 "), BufferUtil.toBuffer("   43abc"), BufferUtil.toBuffer("-44"), BufferUtil.toBuffer(" - 45;"), BufferUtil.toBuffer("-2147483648"), BufferUtil.toBuffer("2147483647") };
        int[] val = new int[]{ 0, 42, 43, -44, -45, -2147483648, 2147483647 };
        for (int i = 0; i < (buf.length); i++)
            Assertions.assertEquals(val[i], BufferUtil.toInt(buf[i]), ("t" + i));

    }

    @Test
    public void testPutInt() throws Exception {
        int[] val = new int[]{ 0, 42, 43, -44, -45, Integer.MIN_VALUE, Integer.MAX_VALUE };
        String[] str = new String[]{ "0", "42", "43", "-44", "-45", "" + (Integer.MIN_VALUE), "" + (Integer.MAX_VALUE) };
        ByteBuffer buffer = ByteBuffer.allocate(24);
        for (int i = 0; i < (val.length); i++) {
            BufferUtil.clearToFill(buffer);
            BufferUtil.putDecInt(buffer, val[i]);
            BufferUtil.flipToFlush(buffer, 0);
            Assertions.assertEquals(str[i], BufferUtil.toString(buffer), ("t" + i));
        }
    }

    @Test
    public void testPutLong() throws Exception {
        long[] val = new long[]{ 0L, 42L, 43L, -44L, -45L, Long.MIN_VALUE, Long.MAX_VALUE };
        String[] str = new String[]{ "0", "42", "43", "-44", "-45", "" + (Long.MIN_VALUE), "" + (Long.MAX_VALUE) };
        ByteBuffer buffer = ByteBuffer.allocate(50);
        for (int i = 0; i < (val.length); i++) {
            BufferUtil.clearToFill(buffer);
            BufferUtil.putDecLong(buffer, val[i]);
            BufferUtil.flipToFlush(buffer, 0);
            Assertions.assertEquals(str[i], BufferUtil.toString(buffer), ("t" + i));
        }
    }

    @Test
    public void testPutHexInt() throws Exception {
        int[] val = new int[]{ 0, 42, 43, -44, -45, -2147483648, 2147483647 };
        String[] str = new String[]{ "0", "2A", "2B", "-2C", "-2D", "-80000000", "7FFFFFFF" };
        ByteBuffer buffer = ByteBuffer.allocate(50);
        for (int i = 0; i < (val.length); i++) {
            BufferUtil.clearToFill(buffer);
            BufferUtil.putHexInt(buffer, val[i]);
            BufferUtil.flipToFlush(buffer, 0);
            Assertions.assertEquals(str[i], BufferUtil.toString(buffer), ("t" + i));
        }
    }

    @Test
    public void testPut() throws Exception {
        ByteBuffer to = BufferUtil.allocate(10);
        ByteBuffer from = BufferUtil.toBuffer("12345");
        BufferUtil.clear(to);
        Assertions.assertEquals(5, BufferUtil.append(to, from));
        Assertions.assertTrue(BufferUtil.isEmpty(from));
        Assertions.assertEquals("12345", BufferUtil.toString(to));
        from = BufferUtil.toBuffer("XX67890ZZ");
        from.position(2);
        Assertions.assertEquals(5, BufferUtil.append(to, from));
        Assertions.assertEquals(2, from.remaining());
        Assertions.assertEquals("1234567890", BufferUtil.toString(to));
    }

    @Test
    public void testAppend() throws Exception {
        ByteBuffer to = BufferUtil.allocate(8);
        ByteBuffer from = BufferUtil.toBuffer("12345");
        BufferUtil.append(to, from.array(), 0, 3);
        Assertions.assertEquals("123", BufferUtil.toString(to));
        BufferUtil.append(to, from.array(), 3, 2);
        Assertions.assertEquals("12345", BufferUtil.toString(to));
        Assertions.assertThrows(BufferOverflowException.class, () -> {
            BufferUtil.append(to, from.array(), 0, 5);
        });
    }

    @Test
    public void testPutDirect() throws Exception {
        ByteBuffer to = BufferUtil.allocateDirect(10);
        ByteBuffer from = BufferUtil.toBuffer("12345");
        BufferUtil.clear(to);
        Assertions.assertEquals(5, BufferUtil.append(to, from));
        Assertions.assertTrue(BufferUtil.isEmpty(from));
        Assertions.assertEquals("12345", BufferUtil.toString(to));
        from = BufferUtil.toBuffer("XX67890ZZ");
        from.position(2);
        Assertions.assertEquals(5, BufferUtil.append(to, from));
        Assertions.assertEquals(2, from.remaining());
        Assertions.assertEquals("1234567890", BufferUtil.toString(to));
    }

    @Test
    public void testToBuffer_Array() {
        byte[] arr = new byte[128];
        Arrays.fill(arr, ((byte) (68)));
        ByteBuffer buf = BufferUtil.toBuffer(arr);
        int count = 0;
        while ((buf.remaining()) > 0) {
            byte b = buf.get();
            Assertions.assertEquals(b, 68);
            count++;
        } 
        Assertions.assertEquals(arr.length, count, "Count of bytes");
    }

    @Test
    public void testToBuffer_ArrayOffsetLength() {
        byte[] arr = new byte[128];
        Arrays.fill(arr, ((byte) (255)));// fill whole thing with FF

        int offset = 10;
        int length = 100;
        Arrays.fill(arr, offset, (offset + length), ((byte) (119)));// fill partial with 0x77

        ByteBuffer buf = BufferUtil.toBuffer(arr, offset, length);
        int count = 0;
        while ((buf.remaining()) > 0) {
            byte b = buf.get();
            Assertions.assertEquals(b, 119);
            count++;
        } 
        Assertions.assertEquals(length, count, "Count of bytes");
    }

    private static final Logger LOG = Log.getLogger(BufferUtilTest.class);

    @Test
    public void testWriteToWithBufferThatDoesNotExposeArrayAndSmallContent() throws IOException {
        int capacity = (TEMP_BUFFER_SIZE) / 4;
        testWriteToWithBufferThatDoesNotExposeArray(capacity);
    }

    @Test
    public void testWriteToWithBufferThatDoesNotExposeArrayAndContentLengthMatchingTempBufferSize() throws IOException {
        int capacity = TEMP_BUFFER_SIZE;
        testWriteToWithBufferThatDoesNotExposeArray(capacity);
    }

    @Test
    public void testWriteToWithBufferThatDoesNotExposeArrayAndContentSlightlyBiggerThanTwoTimesTempBufferSize() throws IOException {
        int capacity = ((TEMP_BUFFER_SIZE) * 2) + 1024;
        testWriteToWithBufferThatDoesNotExposeArray(capacity);
    }

    @Test
    @SuppressWarnings("ReferenceEquality")
    public void testEnsureCapacity() throws Exception {
        ByteBuffer b = BufferUtil.toBuffer("Goodbye Cruel World");
        Assertions.assertTrue((b == (BufferUtil.ensureCapacity(b, 0))));
        Assertions.assertTrue((b == (BufferUtil.ensureCapacity(b, 10))));
        Assertions.assertTrue((b == (BufferUtil.ensureCapacity(b, b.capacity()))));
        ByteBuffer b1 = BufferUtil.ensureCapacity(b, 64);
        Assertions.assertTrue((b != b1));
        Assertions.assertEquals(64, b1.capacity());
        Assertions.assertEquals("Goodbye Cruel World", BufferUtil.toString(b1));
        b1.position(8);
        b1.limit(13);
        Assertions.assertEquals("Cruel", BufferUtil.toString(b1));
        ByteBuffer b2 = b1.slice();
        Assertions.assertEquals("Cruel", BufferUtil.toString(b2));
        System.err.println(BufferUtil.toDetailString(b2));
        Assertions.assertEquals(8, b2.arrayOffset());
        Assertions.assertEquals(5, b2.capacity());
        Assertions.assertTrue((b2 == (BufferUtil.ensureCapacity(b2, 5))));
        ByteBuffer b3 = BufferUtil.ensureCapacity(b2, 64);
        Assertions.assertTrue((b2 != b3));
        Assertions.assertEquals(64, b3.capacity());
        Assertions.assertEquals("Cruel", BufferUtil.toString(b3));
        Assertions.assertEquals(0, b3.arrayOffset());
    }

    @Test
    public void testToDetail_WithDEL() {
        ByteBuffer b = ByteBuffer.allocate(40);
        b.putChar('a').putChar('b').putChar('c');
        b.put(((byte) (127)));
        b.putChar('x').putChar('y').putChar('z');
        b.flip();
        String result = BufferUtil.toDetailString(b);
        MatcherAssert.assertThat("result", result, CoreMatchers.containsString("\\x7f"));
    }
}

