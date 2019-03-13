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


import V8Value.BYTE;
import V8Value.DOUBLE;
import V8Value.FLOAT_32_ARRAY;
import V8Value.FLOAT_64_ARRAY;
import V8Value.INTEGER;
import V8Value.INT_16_ARRAY;
import V8Value.INT_32_ARRAY;
import V8Value.INT_8_ARRAY;
import V8Value.UNSIGNED_INT_16_ARRAY;
import V8Value.UNSIGNED_INT_32_ARRAY;
import V8Value.UNSIGNED_INT_8_ARRAY;
import V8Value.UNSIGNED_INT_8_CLAMPED_ARRAY;
import V8Value.V8_TYPED_ARRAY;
import org.junit.Assert;
import org.junit.Test;

import static V8Value.BYTE;
import static V8Value.FLOAT_32_ARRAY;
import static V8Value.FLOAT_64_ARRAY;
import static V8Value.INTEGER;
import static V8Value.INT_16_ARRAY;
import static V8Value.INT_32_ARRAY;
import static V8Value.INT_8_ARRAY;
import static V8Value.UNSIGNED_INT_16_ARRAY;
import static V8Value.UNSIGNED_INT_32_ARRAY;
import static V8Value.UNSIGNED_INT_8_ARRAY;
import static V8Value.UNSIGNED_INT_8_CLAMPED_ARRAY;


public class V8TypedArraysTest {
    private V8 v8;

    @Test
    public void testArrayBuffer() {
        V8Value result = ((V8Value) (v8.executeScript("var buf = new ArrayBuffer(100); buf;")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testInt8Array() {
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Int8Array(); ints")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testInt16Array() {
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Int16Array(); ints")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testUint8Array() {
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Uint8Array(); ints")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testUint16Array() {
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Uint16Array(); ints")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testInt32Array() {
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Int32Array(); ints")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testUInt32Array() {
        V8Value result = ((V8Value) (v8.executeScript("var ints = new Uint32Array(); ints")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testFloat32Array() {
        V8Value result = ((V8Value) (v8.executeScript("var floats = new Float32Array(); floats")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testFloat64Array() {
        V8Value result = ((V8Value) (v8.executeScript("var floats = new Float64Array(); floats")));
        Assert.assertNotNull(result);
        result.close();
    }

    @Test
    public void testTypedArrayLength() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int32Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(1, result.length());
        result.close();
    }

    @Test
    public void testGetTypedArrayValue() {
        int result = v8.executeIntegerScript("var buf = new ArrayBuffer(4); var ints = new Int16Array(buf); ints[0] = 7; ints[0]");
        Assert.assertEquals(7, result);
    }

    @Test
    public void testGetTypedArrayIntValue() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int16Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(((short) (7)), result.get(0));
        result.close();
    }

    @Test
    public void testGetTypedArrayUsingKeys() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int16Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(7, result.getInteger("0"));
        result.close();
    }

    @Test
    public void testGetTypedArrayIntType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int16Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(INTEGER, result.getType(0));
        result.close();
    }

    @Test
    public void testGetTypedArrayFloatType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var floats = new Float32Array(buf); floats[0] = 7.7; floats")));
        Assert.assertEquals(DOUBLE, result.getType(0));
        result.close();
    }

    @Test
    public void testGetTypedArrayIntArrayType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int16Array(buf); ints[0] = 7; ints")));
        Assert.assertEquals(INT_16_ARRAY, result.getType());
        result.close();
    }

    @Test
    public void testGetTypedArrayUInt8Type() {
        v8.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(final V8Object receiver, final V8Array parameters) {
                Assert.assertEquals(V8_TYPED_ARRAY, parameters.getType(0));
            }
        }, "javaMethod");
        v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Uint8ClampedArray(buf); ints[0] = 7; javaMethod(ints);");
    }

    @Test
    public void testAccessSignedValueFromUnsignedByte_Greater128() {
        V8TypedArray array = ((V8TypedArray) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Uint8ClampedArray(buf); bytes[0] = 240; bytes[1] = 7; bytes")));
        V8ArrayBuffer arrayBuffer = array.getBuffer();
        short result = ((short) ((arrayBuffer.get()) & 255));
        Assert.assertEquals(240, result);
        array.close();
        arrayBuffer.close();
    }

    @Test
    public void testAccessSignedValueFromUnsignedByte_Less128() {
        V8TypedArray array = ((V8TypedArray) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Uint8Array(buf); bytes[0] = 20; bytes[1] = 7; bytes")));
        V8ArrayBuffer arrayBuffer = array.getBuffer();
        short result = ((short) ((arrayBuffer.get()) & 255));
        Assert.assertEquals(20, result);
        array.close();
        arrayBuffer.close();
    }

    @Test
    public void testInt8IsByteArray() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Int8Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(BYTE, result.getType());
        result.close();
    }

    @Test
    public void testInt8_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Int8Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(INT_8_ARRAY, result.getType());
        result.close();
    }

    @Test
    public void testUint8Clamped_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Uint8ClampedArray(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(UNSIGNED_INT_8_CLAMPED_ARRAY, result.getType());
        result.close();
    }

    @Test
    public void testUint8Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Uint8Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(UNSIGNED_INT_8_ARRAY, result.getType());
        result.close();
    }

    @Test
    public void testInt16Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Int16Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(INT_16_ARRAY, result.getType());
        Assert.assertEquals(4, result.length());
        result.close();
    }

    @Test
    public void testUnsignedInt16Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Uint16Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(UNSIGNED_INT_16_ARRAY, result.getType());
        Assert.assertEquals(4, result.length());
        result.close();
    }

    @Test
    public void testInt32Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Int32Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(INT_32_ARRAY, result.getType());
        Assert.assertEquals(2, result.length());
        result.close();
    }

    @Test
    public void testUInt32Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Uint32Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(UNSIGNED_INT_32_ARRAY, result.getType());
        Assert.assertEquals(2, result.length());
        result.close();
    }

    @Test
    public void testFloat32Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Float32Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(FLOAT_32_ARRAY, result.getType());
        Assert.assertEquals(2, result.length());
        result.close();
    }

    @Test
    public void testFloat64Array_GetType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Float64Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(FLOAT_64_ARRAY, result.getType());
        Assert.assertEquals(1, result.length());
        result.close();
    }

    @Test
    public void testGetBytesFromTypedArray() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var bytes = new Int8Array(buf); bytes[0] = 1; bytes[1] = 256; bytes")));
        Assert.assertEquals(BYTE, result.getType());
        Assert.assertEquals(1, result.getByte(0));
        Assert.assertEquals(0, result.getByte(1));
        result.close();
    }

    @Test
    public void testGetTypedArrayFloatArrayType() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var floats = new Float32Array(buf); floats[0] = 7.7; floats[1] = 7; floats")));
        Assert.assertEquals(FLOAT_32_ARRAY, result.getType());
        result.close();
    }

    @Test
    public void testGetTypedArrayType32BitValue() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int32Array(buf); ints[0] = 255; ints")));
        Assert.assertEquals(255, result.get(0));
        result.close();
    }

    @Test
    public void testGetTypedArrayType16BitValue() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var ints = new Int16Array(buf); ints[0] = 255; ints")));
        Assert.assertEquals(((short) (255)), result.get(0));
        result.close();
    }

    @Test
    public void testGetTypedArrayType32BitFloatValue() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(4); var floats = new Float32Array(buf); floats[0] = 255.5; floats")));
        Assert.assertEquals(255.5, result.getDouble(0), 1.0E-5);
        result.close();
    }

    @Test
    public void testGetTypedArrayType64BitFloatValue() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var floats = new Float64Array(buf); floats[0] = 255.5; floats")));
        Assert.assertEquals(255.5, result.getDouble(0), 1.0E-5);
        result.close();
    }

    @Test
    public void testGetTypedRangeArrayValue() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(100); var ints = new Int32Array(buf); for(var i = 0; i < 25; i++) {ints[i] = i;}; ints")));
        Assert.assertEquals(25, result.length());
        int[] ints = result.getIntegers(0, 25);
        for (int i = 0; i < (ints.length); i++) {
            Assert.assertEquals(i, ints[i]);
        }
        result.close();
    }

    @Test
    public void testGetTypedArrayGetKeys() {
        V8Array result = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints[0] = 255; ints[1] = 17; ints")));
        Assert.assertEquals(2, result.getKeys().length);
        Assert.assertEquals("0", result.getKeys()[0]);
        Assert.assertEquals("1", result.getKeys()[1]);
        result.close();
    }

    @Test
    public void testAddTypedArrayInteger() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        array.add("0", 7);
        array.add("1", 17);
        Assert.assertEquals(2, array.length());
        Assert.assertEquals(7, array.getInteger(0));
        Assert.assertEquals(17, array.getInteger(1));
        array.close();
    }

    @Test
    public void testAddTypedArrayFloat() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var floats = new Float32Array(buf); floats")));
        array.add("0", 7.7);
        array.add("1", 17.7);
        Assert.assertEquals(2, array.length());
        Assert.assertEquals(7.7, array.getDouble(0), 1.0E-6);
        Assert.assertEquals(17.7, array.getDouble(1), 1.0E-6);
        array.close();
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushIntToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(7);
        } finally {
            array.close();
        }
    }

    @Test
    public void testCannotPushIntToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(7);
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushFloatToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(7.7);
        } finally {
            array.close();
        }
    }

    @Test
    public void testCannotPushFloatToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(7.7);
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushBooleanToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(true);
        } finally {
            array.close();
        }
    }

    @Test
    public void testCannotPushBooleanToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(true);
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushStringToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push("foo");
        } finally {
            array.close();
        }
    }

    @Test
    public void testCannotPushStringToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push("foo");
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushUndefinedToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.pushUndefined();
        } finally {
            array.close();
        }
    }

    @Test
    public void testCannotPushUndefinedToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.pushUndefined();
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushNullToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(((V8Object) (null)));
        } finally {
            array.close();
        }
    }

    @Test
    public void testCannotPushNullToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        try {
            array.push(((V8Object) (null)));
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushV8ObjectToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        V8Object obj = new V8Object(v8);
        try {
            array.push(obj);
        } finally {
            array.close();
            obj.close();
        }
    }

    @Test
    public void testCannotPushV8ObjectToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        V8Object obj = new V8Object(v8);
        try {
            array.push(obj);
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
            obj.close();
        }
        Assert.fail("Expected failure");
    }

    @Test(expected = V8RuntimeException.class)
    public void testCannotPushV8ArrayToTypedArray() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        V8Array obj = new V8Array(v8);
        try {
            array.push(obj);
        } finally {
            array.close();
            obj.close();
        }
    }

    @Test
    public void testCannotPushV8ArrayToTypedArray_CheckMessage() {
        V8Array array = ((V8Array) (v8.executeScript("var buf = new ArrayBuffer(8); var ints = new Int32Array(buf); ints")));
        V8Array obj = new V8Array(v8);
        try {
            array.push(obj);
        } catch (Exception e) {
            Assert.assertEquals("Cannot push to a Typed Array.", e.getMessage());
            return;
        } finally {
            array.close();
            obj.close();
        }
        Assert.fail("Expected failure");
    }

    @Test
    public void testIntArrayLength() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + "var ints = new Int32Array(buf);\n"));
        int arrayLength = v8.executeIntegerScript("ints.length;");// 4 bytes for each element

        Assert.assertEquals(25, arrayLength);
    }

    @Test
    public void testIntArrayByteLength() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + "var ints = new Int32Array(buf);\n"));
        int arrayLength = v8.executeIntegerScript("ints.byteLength;");// 4 bytes for each element

        Assert.assertEquals(100, arrayLength);
    }

    @Test
    public void testGetTypedArray() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + "var intsArray = new Int32Array(buf);\n"));
        int type = v8.getType("intsArray");
        Assert.assertEquals(V8_TYPED_ARRAY, type);
    }

    @Test
    public void testGetTypedArray_IntegerType() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + "var intsArray = new Int32Array(buf);\n"));
        V8Array intsArray = ((V8Array) (v8.get("intsArray")));
        Assert.assertEquals(INTEGER, intsArray.getType());
        intsArray.close();
    }

    @Test
    public void testGetTypedArray_DoubleType() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(80);\n" + "var doublesArray = new Float64Array(buf);"));
        V8Array doublesArray = ((V8Array) (v8.get("doublesArray")));
        Assert.assertEquals(DOUBLE, doublesArray.getType());
        doublesArray.close();
    }

    @Test
    public void testGetTypedArray_IntegerTypeAfterNull() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + ("var intsArray = new Int32Array(buf);\n" + "intsArray[0] = null;\n")));
        V8Array intsArray = ((V8Array) (v8.get("intsArray")));
        Assert.assertEquals(INTEGER, intsArray.getType());
        intsArray.close();
    }

    @Test
    public void testGetTypedArray_IntegerTypeAfterUndefined() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + ("var intsArray = new Int32Array(buf);\n" + "intsArray[0] = undefined;\n")));
        V8Array intsArray = ((V8Array) (v8.get("intsArray")));
        Assert.assertEquals(INTEGER, intsArray.getType());
        intsArray.close();
    }

    @Test
    public void testGetTypedArray_IntegerTypeAfterFloat() {
        v8.executeVoidScript(("var buf = new ArrayBuffer(100);\n" + ("var intsArray = new Int32Array(buf);\n" + "intsArray[0] = 7.4;\n")));
        V8Array intsArray = ((V8Array) (v8.get("intsArray")));
        Assert.assertEquals(INTEGER, intsArray.getType());
        intsArray.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidOffset_Int32Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidLength_Int32Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidLengthNegative_Int32Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidLengthWithOffset_Int32Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 12);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test
    public void testGetArrayBuffer_Int32Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        V8ArrayBuffer result = v8Int32Array.getBuffer();
        Assert.assertEquals(result, buffer);
        result.close();
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testUseAccessedArrayBuffer_Int32Array() {
        V8TypedArray array = ((V8TypedArray) (v8.executeScript(("\n" + ((("var buffer = new ArrayBuffer(8);" + "var array = new Int32Array(buffer);") + "array[0] = 1; array[1] = 7;") + "array;")))));
        V8ArrayBuffer buffer = array.getBuffer();
        Assert.assertEquals(1, buffer.getInt(0));
        Assert.assertEquals(7, buffer.getInt(4));
        buffer.close();
        array.close();
    }

    @Test
    public void testCreateIntegerTypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        v8Int32Array.add("0", 7);
        v8Int32Array.add("1", 8);
        Assert.assertEquals(7, buffer.getInt());
        Assert.assertEquals(8, buffer.getInt());
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testUpdateInt32TypedArrayInJavaScript() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        v8.add("v8Int32Array", v8Int32Array);
        v8.executeVoidScript("v8Int32Array[0] = 7; v8Int32Array[1] = 9;");
        Assert.assertEquals(7, buffer.getInt());
        Assert.assertEquals(9, buffer.getInt());
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testAccessInt32TypedArrayInJavaScript() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        v8.add("v8Int32Array", v8Int32Array);
        buffer.putInt(4);
        buffer.putInt(8);
        Assert.assertEquals(4, v8.executeIntegerScript("v8Int32Array[0];"));
        Assert.assertEquals(8, v8.executeIntegerScript("v8Int32Array[1];"));
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testWriteInt32TypedArrayFromArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        v8.add("v8Int32Array", v8Int32Array);
        buffer.putInt(4);
        buffer.putInt(8);
        Assert.assertEquals(4, v8Int32Array.get(0));
        Assert.assertEquals(8, v8Int32Array.get(1));
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testInt32TypedArray_Length() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        v8.add("v8Int32Array", v8Int32Array);
        Assert.assertEquals(2, v8Int32Array.length());
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testInt32TypedArray_CustomLength() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 1);
        v8.add("v8Int32Array", v8Int32Array);
        Assert.assertEquals(1, v8Int32Array.length());
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testInt32TypedArray_CustomOffset() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 4, 1);
        v8.add("v8Int32Array", v8Int32Array);
        buffer.putInt(4);
        buffer.putInt(8);
        Assert.assertEquals(1, v8Int32Array.length());
        Assert.assertEquals(8, v8Int32Array.getInteger(0));
        buffer.close();
        v8Int32Array.close();
    }

    @Test
    public void testInt32TypedArray_Twin() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        V8Array twinArray = v8Int32Array.twin();
        Assert.assertTrue((twinArray instanceof V8TypedArray));
        Assert.assertEquals(v8Int32Array, twinArray);
        v8Int32Array.close();
        twinArray.close();
        buffer.close();
    }

    @Test
    public void testInt32TypedArray_TwinHasSameValues() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int32Array1 = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        V8TypedArray v8Int32Array2 = ((V8TypedArray) (v8Int32Array1.twin()));
        v8Int32Array1.add("0", 7);
        v8Int32Array1.add("1", 8);
        Assert.assertEquals(7, v8Int32Array2.get(0));
        Assert.assertEquals(8, v8Int32Array2.get(1));
        v8Int32Array1.close();
        v8Int32Array2.close();
        buffer.close();
    }

    @Test
    public void testGetInt32TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        V8Array array = ((V8Array) (v8.executeScript("new Int32Array(buf);")));
        Assert.assertTrue((array instanceof V8TypedArray));
        Assert.assertEquals(INTEGER, array.getType());
        array.close();
        buffer.close();
    }

    @Test
    public void testCreateV8Int32ArrayFromJSArrayBuffer() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript(("" + ((("var buffer = new ArrayBuffer(8)\n" + "var array = new Int32Array(buffer);\n") + "array[0] = 7; array[1] = 9;\n") + "buffer;")))));
        V8TypedArray array = new V8TypedArray(v8, buffer, INTEGER, 0, 2);
        Assert.assertEquals(7, array.get(0));
        Assert.assertEquals(9, array.get(1));
        array.close();
        buffer.close();
    }

    @Test
    public void testGetByteBuffer_Int8Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        V8ArrayBuffer arrayBuffer = v8Int8Array.getBuffer();
        Assert.assertEquals(arrayBuffer, buffer);
        buffer.close();
        v8Int8Array.close();
        arrayBuffer.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidLength_Int8Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidLengthNegative_Int8Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateArrayInvalidLengthWithOffset_Int8Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 12);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test
    public void testGetArrayBuffer_Int8Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        V8ArrayBuffer result = v8Int8Array.getBuffer();
        Assert.assertEquals(result, buffer);
        result.close();
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testUseAccessedArrayBuffer_Int8Array() {
        V8TypedArray array = ((V8TypedArray) (v8.executeScript(("\n" + ((("var buffer = new ArrayBuffer(8);" + "var array = new Int8Array(buffer);") + "array[0] = 1; array[1] = 7;") + "array;")))));
        V8ArrayBuffer buffer = array.getBuffer();
        Assert.assertEquals(1, buffer.get(0));
        Assert.assertEquals(7, buffer.get(1));
        buffer.close();
        array.close();
    }

    @Test
    public void testCreateByteTypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(7, buffer.get());
        Assert.assertEquals(8, buffer.get());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateInt8TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, INT_8_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(INT_8_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.get());
        Assert.assertEquals(8, buffer.get());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateUInt8TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_8_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(UNSIGNED_INT_8_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.get());
        Assert.assertEquals(8, buffer.get());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateUInt8ClampedTypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_8_CLAMPED_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 270);
        Assert.assertEquals(UNSIGNED_INT_8_CLAMPED_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.get());
        Assert.assertEquals(255, ((buffer.get()) & 255));// Java does not have Unsigned Bytes

        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateInt16TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, INT_16_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(INT_16_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.getShort());
        Assert.assertEquals(8, buffer.getShort());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateUInt16TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_16_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(UNSIGNED_INT_16_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.getShort());
        Assert.assertEquals(8, buffer.getShort());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateInt32TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, INT_32_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(INT_32_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.getInt());
        Assert.assertEquals(8, buffer.getInt());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateUInt32TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_32_ARRAY, 0, 2);
        typedArray.add("0", 7);
        typedArray.add("1", 8);
        Assert.assertEquals(UNSIGNED_INT_32_ARRAY, typedArray.getType());
        Assert.assertEquals(7, buffer.getInt());
        Assert.assertEquals(8, buffer.getInt());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateFloat32TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, FLOAT_32_ARRAY, 0, 2);
        Assert.assertEquals(FLOAT_32_ARRAY, typedArray.getType());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testAccess32BitFloatFromTypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, FLOAT_32_ARRAY, 0, 2);
        typedArray.add("0", 3.14);
        Assert.assertEquals(FLOAT_32_ARRAY, typedArray.getType());
        Assert.assertEquals(3.14, buffer.getFloat(), 1.0E-4);
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testCreateFloat64TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, FLOAT_64_ARRAY, 0, 1);
        Assert.assertEquals(FLOAT_64_ARRAY, typedArray.getType());
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testAccess64BitFloatFromTypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, FLOAT_64_ARRAY, 0, 1);
        typedArray.add("0", 3.14159265359);
        Assert.assertEquals(FLOAT_64_ARRAY, typedArray.getType());
        Assert.assertEquals(3.14159265359, buffer.getDouble(), 1.0E-12);
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testUpdateInt8TypedArrayInJavaScript() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        v8.add("v8Int8Array", v8Int8Array);
        v8.executeVoidScript("v8Int8Array[0] = 7; v8Int8Array[1] = 9;");
        Assert.assertEquals(7, buffer.get());
        Assert.assertEquals(9, buffer.get());
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testAccessInt8TypedArrayInJavaScript() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 2);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        v8.add("v8Int8Array", v8Int8Array);
        buffer.put(((byte) (4)));
        buffer.put(((byte) (8)));
        Assert.assertEquals(4, v8.executeIntegerScript("v8Int8Array[0];"));
        Assert.assertEquals(8, v8.executeIntegerScript("v8Int8Array[1];"));
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testWriteInt8TypedArrayFromArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        v8.add("v8Int8Array", v8Int8Array);
        buffer.put(((byte) (4)));
        buffer.put(((byte) (8)));
        Assert.assertEquals(((byte) (4)), v8Int8Array.get(0));
        Assert.assertEquals(((byte) (8)), v8Int8Array.get(1));
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testInt8TypedArray_Length() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        buffer.put(((byte) (4)));
        buffer.put(((byte) (8)));
        Assert.assertEquals(2, v8Int8Array.length());
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testInt8TypedArray_CustomLength() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 1);
        Assert.assertEquals(1, v8Int8Array.length());
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testInt8TypedArray_CustomOffset() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 1, 1);
        buffer.put(((byte) (4)));
        buffer.put(((byte) (8)));
        Assert.assertEquals(1, v8Int8Array.length());
        Assert.assertEquals(((byte) (8)), v8Int8Array.get(0));
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testFloat32TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, FLOAT_32_ARRAY, 0, 2);
        typedArray.add("0", 123.45);
        typedArray.add("1", (-3.14159));
        Assert.assertEquals(2, typedArray.length());
        Assert.assertEquals(123.45, ((Float) (typedArray.get(0))), 1.0E-5);
        Assert.assertEquals((-3.14159), ((Float) (typedArray.get(1))), 1.0E-4);
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testFloat64TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 16);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, FLOAT_64_ARRAY, 0, 2);
        typedArray.add("0", 123.45);
        typedArray.add("1", (-3.14159));
        Assert.assertEquals(2, typedArray.length());
        Assert.assertEquals(123.45, ((Double) (typedArray.get(0))), 1.0E-5);
        Assert.assertEquals((-3.14159), ((Double) (typedArray.get(1))), 1.0E-4);
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testUInt32TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_32_ARRAY, 0, 2);
        typedArray.add("0", ((long) ((Integer.MAX_VALUE) + 1)));
        typedArray.add("1", (-1));
        Assert.assertEquals(2, typedArray.length());
        Assert.assertEquals(2147483648L, typedArray.get(0));
        Assert.assertEquals(4294967295L, typedArray.get(1));
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testInt32TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 12);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, INT_32_ARRAY, 0, 3);
        typedArray.add("0", ((long) (Integer.MAX_VALUE)));
        typedArray.add("1", ((long) ((Integer.MAX_VALUE) + 1)));
        typedArray.add("2", ((long) (Integer.MIN_VALUE)));
        Assert.assertEquals(3, typedArray.length());
        Assert.assertEquals(2147483647, typedArray.get(0));
        Assert.assertEquals(-2147483648, typedArray.get(1));
        Assert.assertEquals(-2147483648, typedArray.get(2));
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testUInt16TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_16_ARRAY, 0, 2);
        typedArray.add("0", ((Short.MAX_VALUE) + 1));
        typedArray.add("1", (-1));
        Assert.assertEquals(2, typedArray.length());
        Assert.assertEquals(32768, typedArray.get(0));
        Assert.assertEquals(65535, typedArray.get(1));
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testInt16TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 6);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, INT_16_ARRAY, 0, 3);
        typedArray.add("0", Short.MAX_VALUE);
        typedArray.add("1", ((Short.MAX_VALUE) + 1));
        typedArray.add("2", Short.MIN_VALUE);
        Assert.assertEquals(3, typedArray.length());
        Assert.assertEquals(((short) (32767)), typedArray.get(0));
        Assert.assertEquals(((short) (-32768)), typedArray.get(1));
        Assert.assertEquals(((short) (-32768)), typedArray.get(2));
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testInt8TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 3);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, INT_8_ARRAY, 0, 3);
        typedArray.add("0", Byte.MAX_VALUE);
        typedArray.add("1", ((Byte.MAX_VALUE) + 1));
        typedArray.add("2", Byte.MIN_VALUE);
        Assert.assertEquals(3, typedArray.length());
        Assert.assertEquals(((byte) (127)), typedArray.get(0));
        Assert.assertEquals(((byte) (-128)), typedArray.get(1));
        Assert.assertEquals(((byte) (-128)), typedArray.get(2));
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testUInt8TypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray typedArray = new V8TypedArray(v8, buffer, UNSIGNED_INT_8_ARRAY, 0, 8);
        typedArray.add("0", ((short) (255)));
        typedArray.add("1", ((short) (-1)));
        Assert.assertEquals(8, typedArray.length());
        Assert.assertEquals(((short) (255)), typedArray.get(0));
        Assert.assertEquals(((short) (255)), typedArray.get(1));
        buffer.close();
        typedArray.close();
    }

    @Test
    public void testUInt8ClampedTypedArray_addInJava() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 8);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, UNSIGNED_INT_8_CLAMPED_ARRAY, 0, 8);
        v8Int8Array.add("0", ((short) (256)));
        v8Int8Array.add("1", (-1));
        Assert.assertEquals(8, v8Int8Array.length());
        Assert.assertEquals(((short) (255)), v8Int8Array.get(0));
        Assert.assertEquals(((short) (0)), v8Int8Array.get(1));
        buffer.close();
        v8Int8Array.close();
    }

    @Test
    public void testInt8TypedArray_Twin() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        V8TypedArray v8Int8Array = new V8TypedArray(v8, buffer, BYTE, 0, 4);
        V8Array twinArray = v8Int8Array.twin();
        Assert.assertTrue((twinArray instanceof V8TypedArray));
        Assert.assertEquals(BYTE, twinArray.getType());
        Assert.assertEquals(v8Int8Array, twinArray);
        v8Int8Array.close();
        twinArray.close();
        buffer.close();
    }

    @Test
    public void testInt8TypedArray_TwinHasSameValues() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        V8TypedArray v8Int8Array1 = new V8TypedArray(v8, buffer, BYTE, 0, 4);
        V8TypedArray v8Int8Array2 = ((V8TypedArray) (v8Int8Array1.twin()));
        v8Int8Array1.add("0", 7);
        v8Int8Array1.add("1", 8);
        Assert.assertEquals(((byte) (7)), v8Int8Array2.get(0));
        Assert.assertEquals(((byte) (8)), v8Int8Array2.get(1));
        v8Int8Array1.close();
        v8Int8Array2.close();
        buffer.close();
    }

    @Test
    public void testGetInt8TypedArray() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 4);
        v8.add("buf", buffer);
        V8Array array = ((V8Array) (v8.executeScript("new Int8Array(buf);")));
        Assert.assertTrue((array instanceof V8TypedArray));
        array.close();
        buffer.close();
    }

    @Test
    public void testCreateV8Int8ArrayFromJSArrayBuffer() {
        V8ArrayBuffer buffer = ((V8ArrayBuffer) (v8.executeScript(("" + ((("var buffer = new ArrayBuffer(8)\n" + "var array = new Int8Array(buffer);\n") + "array[0] = 7; array[1] = 9;\n") + "buffer;")))));
        V8TypedArray array = new V8TypedArray(v8, buffer, BYTE, 0, 2);
        Assert.assertEquals(((byte) (7)), array.get(0));
        Assert.assertEquals(((byte) (9)), array.get(1));
        array.close();
        buffer.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_Boolean() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_String() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_V8Array() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_V8Object() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_V8ArrayBuffer() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_V8Function() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_Null() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTypedArrayWithIllegalType_Undefined() {
        V8ArrayBuffer buffer = new V8ArrayBuffer(v8, 10);
        try {
            close();
        } finally {
            buffer.close();
        }
    }

    @Test
    public void testInt8ArrayIs1Byte() {
        Assert.assertEquals(1, V8TypedArray.getStructureSize(INT_8_ARRAY));
    }

    @Test
    public void testUInt8ArrayIs1Byte() {
        Assert.assertEquals(1, V8TypedArray.getStructureSize(UNSIGNED_INT_8_ARRAY));
    }

    @Test
    public void testUInt8ClampedArrayIs1Byte() {
        Assert.assertEquals(1, V8TypedArray.getStructureSize(UNSIGNED_INT_8_CLAMPED_ARRAY));
    }

    @Test
    public void testInt16ArrayIs2Bytes() {
        Assert.assertEquals(2, V8TypedArray.getStructureSize(INT_16_ARRAY));
    }

    @Test
    public void testUInt16ArrayIs2Bytes() {
        Assert.assertEquals(2, V8TypedArray.getStructureSize(UNSIGNED_INT_16_ARRAY));
    }

    @Test
    public void testInt32ArrayIs4Bytes() {
        Assert.assertEquals(4, V8TypedArray.getStructureSize(INT_32_ARRAY));
    }

    @Test
    public void testUInt32ArrayIs4Bytes() {
        Assert.assertEquals(4, V8TypedArray.getStructureSize(UNSIGNED_INT_32_ARRAY));
    }

    @Test
    public void testFloat32ArrayIs4Bytes() {
        Assert.assertEquals(4, V8TypedArray.getStructureSize(FLOAT_32_ARRAY));
    }

    @Test
    public void testFloat64ArrayIs8Bytes() {
        Assert.assertEquals(8, V8TypedArray.getStructureSize(FLOAT_64_ARRAY));
    }
}

