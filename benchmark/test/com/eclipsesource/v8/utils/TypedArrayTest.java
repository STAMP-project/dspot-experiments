package com.eclipsesource.v8.utils;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8TypedArray;
import com.eclipsesource.v8.V8Value;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class TypedArrayTest {
    private V8 v8;

    @Test
    public void testGetV8TypedArray() {
        TypedArray typedArray = new TypedArray(v8, new ArrayBuffer(v8, ByteBuffer.allocateDirect(8)), V8Value.INT_8_ARRAY, 0, 8);
        V8TypedArray v8TypedArray = typedArray.getV8TypedArray();
        Assert.assertNotNull(v8TypedArray);
        v8TypedArray.close();
    }

    @Test
    public void testV8TypedArrayAvailable() {
        TypedArray typedArray = new TypedArray(v8, new ArrayBuffer(v8, ByteBuffer.allocateDirect(8)), V8Value.INT_8_ARRAY, 0, 8);
        Assert.assertTrue(typedArray.isAvailable());
    }
}

