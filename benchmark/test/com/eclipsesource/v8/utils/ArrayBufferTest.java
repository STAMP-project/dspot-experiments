package com.eclipsesource.v8.utils;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8ArrayBuffer;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class ArrayBufferTest {
    private V8 v8;

    @Test
    public void testGetV8ArrayBuffer() {
        ArrayBuffer arrayBuffer = new ArrayBuffer(v8, ByteBuffer.allocateDirect(8));
        V8ArrayBuffer v8ArrayBuffer = arrayBuffer.getV8ArrayBuffer();
        Assert.assertNotNull(v8ArrayBuffer);
        v8ArrayBuffer.close();
    }

    @Test
    public void testV8ArrayBufferAvailable() {
        ArrayBuffer arrayBuffer = new ArrayBuffer(v8, ByteBuffer.allocateDirect(8));
        Assert.assertTrue(arrayBuffer.isAvailable());
    }
}

