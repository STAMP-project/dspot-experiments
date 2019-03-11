/**
 * *****************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 * ****************************************************************************
 */
package com.eclipsesource.v8;


import V8Value.V8_ARRAY_BUFFER;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import static V8Value.INT_32_ARRAY;


public class V8ArrayBufferTest {
    private V8 v8;

    @Test
    public void testEmptyArrayBufferReturned() {
        V8ArrayBuffer arrayBuffer = ((V8ArrayBuffer) (v8.executeScript("new ArrayBuffer(0);")));
        Assert.assertEquals(arrayBuffer.capacity(), 0);
        arrayBuffer.close();
    }

    @Test
    public void testCreateV8ArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 100);
        v8.add("buffer", buffer);
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Int8Array(buffer); ints")));
        Assert.assertNotNull(result);
        result.close();
        buffer.close();
    }

    @Test
    public void testArrayBufferType() {
        V8Array container = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(100); var container = [buf]; container")));
        Assert.assertEquals(V8_ARRAY_BUFFER, container.getType(0));
        container.close();
    }

    @Test
    public void testGetArrayBufferIsV8ArrayBuffer() {
        V8Value result = ((V8Value) (v8.executeScript("var buf = new ArrayBuffer(100);  buf;")));
        Assert.assertTrue((result instanceof V8ArrayBuffer));
        result.close();
    }

    @Test
    public void testTypedArrayLength_WithArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        V8Array result = ((V8Array) (v8.executeScript("var ints = new Int32Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(1, result.length());
        result.close();
        buffer.close();
    }

    @Test
    public void testAccessArrayBuffer_Int8ArrayView() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int8Array(buf); ints[0] = 7; buf")));
        Assert.assertEquals(4, buffer.limit());
        Assert.assertEquals(7, buffer.get(0));
        buffer.close();
    }

    @Test
    public void testAccessArrayBuffer_Int32ArrayView() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int32Array(buf); ints[0] = 7; buf")));
        Assert.assertEquals(1, buffer.intLimit());
        Assert.assertEquals(7, buffer.getInt(0));
        buffer.close();
    }

    @Test
    public void testAccessArrayBuffer_Int16ArrayView() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript("var buf = new ArrayBuffer(4); var shorts = new Int16Array(buf); shorts[0] = 7; buf")));
        Assert.assertEquals(2, buffer.shortLimit());
        Assert.assertEquals(7, buffer.getShort(0));
        buffer.close();
    }

    @Test
    public void testAccessArrayBuffer_Float32rrayView() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript("var buf = new ArrayBuffer(4); var floats = new Float32Array(buf); floats[0] = 7.7; buf")));
        Assert.assertEquals(1, buffer.floatLimit());
        Assert.assertEquals(7.7, buffer.getFloat(0), 1.0E-5);
        buffer.close();
    }

    @Test
    public void testAccessArrayBuffer_Float64ArrayView() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript("var buf = new ArrayBuffer(8); var floats = new Float64Array(buf); floats[0] = 7.7; buf")));
        Assert.assertEquals(1, buffer.doubleLimit());
        Assert.assertEquals(7.7, buffer.getDouble(0), 1.0E-5);
        buffer.close();
    }

    @Test
    public void testGetTypedArrayValue_WithArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        int result = v8.executeIntegerScript("var ints = new Int16Array(buf); ints[0] = 7; ints[0]");
        Assert.assertEquals(7, result);
        buffer.close();
    }

    @Test
    public void testGetTypedArrayIntValue_WithArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        V8Array result = ((V8Array) (v8.executeScript("var ints = new Int16Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(((short) (7)), result.get(0));
        result.close();
        buffer.close();
    }

    @Test
    public void testGetTypedArrayUsingKeys_WithArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        V8Array result = ((V8Array) (v8.executeScript("var ints = new Int16Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(7, result.getInteger("0"));
        result.close();
        buffer.close();
    }

    @Test
    public void testGetTypedArrayType32BitValue_FromBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        v8.executeVoidScript("var ints = new Int32Array(buf); ints[0] = 255;");
        Assert.assertEquals(255, buffer.getInt(0));
        buffer.close();
    }

    @Test
    public void testGetTypedArrayType16BitValue_FromBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        v8.executeVoidScript("var ints = new Int16Array(buf); ints[0] = 255;");
        Assert.assertEquals(255, buffer.getShort(0));
        buffer.close();
    }

    @Test
    public void testGetTypedArrayType32BitFloatValue_FromBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        v8.executeVoidScript("var floats = new Float32Array(buf); floats[0] = 255.5;");
        Assert.assertEquals(255.5, buffer.getFloat(0), 1.0E-5);
        buffer.close();
    }

    @Test
    public void testGetTypedArrayType64BitFloatValue_FromBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        v8.add("buf", buffer);
        v8.executeVoidScript("var floats = new Float64Array(buf); floats[0] = 255.5;");
        Assert.assertEquals(255.5, buffer.getDouble(0), 1.0E-5);
        buffer.close();
    }

    @Test
    public void testGetTypedRangeArrayValue_FromBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 100);
        v8.add("buf", buffer);
        v8.executeVoidScript("var ints = new Int32Array(buf); for(var i = 0; i < 25; i++) {ints[i] = i;};");
        Assert.assertEquals(25, buffer.intLimit());
        for (int i = 0; i < (buffer.intLimit()); i++) {
            Assert.assertEquals(i, buffer.getInt((i * 4)));
        }
        buffer.close();
    }

    @Test
    public void testAddTypedArrayIntegerToBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        v8.add("buf", buffer);
        V8Array array = ((V8Array) (v8.executeScript("var ints = new Int32Array(buf); ints")));
        buffer.putInt(0, 7);
        buffer.putInt(1, 17);
        Assert.assertEquals(2, array.length());
        Assert.assertEquals(7, array.getInteger(0));
        Assert.assertEquals(17, array.getInteger(1));
        array.close();
        buffer.close();
    }

    @Test
    public void testAddTypedArrayFloatToBackingStore() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        v8.add("buf", buffer);
        V8Array array = ((V8Array) (v8.executeScript("var floats = new Float32Array(buf); floats")));
        buffer.putFloat(0, 7.7F);
        buffer.putFloat(4, 17.7F);
        Assert.assertEquals(2, array.length());
        Assert.assertEquals(7.7, array.getDouble(0), 1.0E-6);
        Assert.assertEquals(17.7, array.getDouble(1), 1.0E-6);
        array.close();
        buffer.close();
    }

    @Test
    public void testUseCustomByteBuffer_Int32Array() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, byteBuffer);
        v8.add("buf", buffer);
        V8Array array = ((V8Array) (v8.executeScript("var ints = new Int32Array(buf); ints")));
        buffer.putInt(0, 7);
        buffer.putInt(1, 17);
        Assert.assertEquals(2, array.length());
        Assert.assertEquals(7, array.getInteger(0));
        Assert.assertEquals(17, array.getInteger(1));
        array.close();
        buffer.close();
    }

    @Test
    public void testUseCustomByteBuffer_Float32Array() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, byteBuffer);
        v8.add("buf", buffer);
        V8Array array = ((V8Array) (v8.executeScript("var floats = new Float32Array(buf); floats")));
        buffer.putFloat(0, 7.7F);
        buffer.putFloat(4, 17.7F);
        Assert.assertEquals(2, array.length());
        Assert.assertEquals(7.7, array.getDouble(0), 1.0E-6);
        Assert.assertEquals(17.7, array.getDouble(1), 1.0E-6);
        array.close();
        buffer.close();
    }

    @Test
    public void shareDirectBufferBetweenArrayBuffers() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);
        V8ArrayBuffer buffer1 = new V8ArrayBuffer(v8, byteBuffer);
        V8ArrayBuffer buffer2 = new V8ArrayBuffer(v8, byteBuffer);
        V8TypedArray array1 = new V8TypedArray(v8, buffer1, INT_32_ARRAY, 0, 2);
        V8TypedArray array2 = new V8TypedArray(v8, buffer2, INT_32_ARRAY, 0, 2);
        array1.add("0", 7).add("1", 9);
        Assert.assertEquals(7, array2.get(0));
        Assert.assertEquals(9, array2.get(1));
        array1.close();
        array2.close();
        buffer1.close();
        buffer2.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testByteBufferMustBeDirectBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        close();
    }

    @Test
    public void getArrayBuffer() {
        v8.executeVoidScript("var buffer = new ArrayBuffer(8);");
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.get("buffer")));
        Assert.assertNotNull(buffer);
        buffer.close();
    }
}

