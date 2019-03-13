/**
 * *****************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
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


import V8Value.BOOLEAN;
import V8Value.DOUBLE;
import V8Value.INTEGER;
import V8Value.STRING;
import V8Value.UNDEFINED;
import V8Value.V8_ARRAY;
import V8Value.V8_OBJECT;
import com.eclipsesource.v8.V8Array.Undefined;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class V8ArrayTest {
    private V8 v8;

    @SuppressWarnings("resource")
    @Test(expected = IllegalStateException.class)
    public void testDoNotReleaseArrayReference() {
        V8 _v8 = V8.createV8Runtime();
        new V8Array(_v8);
        _v8.close();
    }

    @Test
    public void testGetArrayElementFromProperties() {
        V8Array v8Array = new V8Array(v8);
        v8Array.push("1").push(2).push(3.3);
        String result1 = v8Array.getString("0");
        int result2 = v8Array.getInteger("1");
        double result3 = v8Array.getDouble("2");
        Assert.assertEquals("1", result1);
        Assert.assertEquals(2, result2);
        Assert.assertEquals(3.3, result3, 1.0E-6);
        v8Array.close();
    }

    @Test
    public void testSetArrayElementsWithProperties() {
        V8Array v8Array = new V8Array(v8);
        v8Array.add("0", 1);
        v8Array.add("10", 2);
        v8Array.add("19", 3);
        v8Array.add("bob", 4);
        Assert.assertEquals(20, v8Array.length());
        Assert.assertEquals(1, v8Array.getInteger(0));
        Assert.assertEquals(2, v8Array.getInteger(10));
        Assert.assertEquals(3, v8Array.getInteger(19));
        Assert.assertEquals(4, v8Array.getInteger("bob"));
        v8Array.close();
    }

    @Test
    public void testCreateAndReleaseArray() {
        for (int i = 0; i < 10000; i++) {
            V8Array v8Array = new V8Array(v8);
            v8Array.close();
        }
    }

    @Test
    public void testArraySize() {
        V8Array array = v8.executeArrayScript("[1,2,3];");
        Assert.assertEquals(3, array.length());
        array.close();
    }

    @Test
    public void testArraySizeZero() {
        V8Array array = v8.executeArrayScript("[];");
        Assert.assertEquals(0, array.length());
        array.close();
    }

    /**
     * * Undefined **
     */
    @SuppressWarnings("resource")
    @Test
    public void testUndefinedObjectProperty() {
        V8Array result = v8.getArray("array");
        Assert.assertTrue(result.isUndefined());
    }

    @SuppressWarnings("resource")
    @Test
    public void testObjectUndefinedEqualsArrayUndefined() {
        Assert.assertEquals(new V8Object.Undefined(), new V8Array.Undefined());
    }

    @SuppressWarnings("resource")
    @Test
    public void testObjectUndefinedHashCodeEqualsArrayUndefinedHashCode() {
        Assert.assertEquals(new V8Object.Undefined().hashCode(), new V8Array.Undefined().hashCode());
    }

    @SuppressWarnings("resource")
    @Test
    public void testUndefinedEqual() {
        V8Array undefined1 = v8.getArray("foo");
        V8Array undefined2 = v8.getArray("bar");
        Assert.assertEquals(undefined1, undefined2);
    }

    @Test
    public void testStaticUndefined() {
        V8Array undefined = v8.getArray("foo");
        Assert.assertEquals(undefined, V8.getUndefined());
    }

    @Test
    public void testUndefinedHashCodeEquals() {
        V8Array undefined1 = v8.getArray("foo");
        V8Array undefined2 = v8.getArray("bar");
        Assert.assertEquals(undefined1.hashCode(), undefined2.hashCode());
    }

    @Test
    public void testUndefinedToString() {
        V8Array undefined = v8.getArray("object");
        Assert.assertEquals("undefined", undefined.toString());
    }

    @Test
    public void testUndefinedRelease() {
        V8Array undefined = v8.getArray("object");
        undefined.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetByteUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getByte(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBytesUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getBytes(0, 10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBytesUndefined2() {
        V8Array undefined = v8.getArray("array");
        undefined.getBytes(0, 10, new byte[10]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddIntUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.add("foo", 7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddBooleanUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.add("foo", false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddStringUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.add("foo", "bar");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddDoubleUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.add("foo", 7.7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddObjectUndefined() {
        V8Array undefined = v8.getArray("array");
        V8Object object = new V8Object(v8);
        try {
            undefined.add("foo", object);
        } finally {
            object.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddArrayUndefined() {
        V8Array undefined = v8.getArray("array");
        V8Array array = new V8Array(v8);
        try {
            undefined.add("foo", array);
        } finally {
            array.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddUndefinedUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.addUndefined("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testContainsUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.contains("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteIntFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeIntegerFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteBooleanFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeBooleanFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteDoubleFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeDoubleFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteStringFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeStringFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteObjectFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeObjectFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteArrayFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeArrayFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteVoidFunctionUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.executeVoidFunction("foo", null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetIntegerUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getInteger("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBooleanUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getBoolean("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDoubleUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getDouble("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetStringUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getString("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetObjectUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getObject("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetArrayUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getArray("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetKeysUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getKeys();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTypeKeyUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getType("bar");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTypeIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getType(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetPrototype() {
        V8Array undefined = v8.getArray("array");
        V8Object prototype = new V8Object(v8);
        try {
            undefined.setPrototype(prototype);
        } finally {
            prototype.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRegisterJavaMethod() {
        V8Array undefined = v8.getArray("array");
        undefined.registerJavaMethod(Mockito.mock(JavaCallback.class), "name");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRegisterVoidJavaMethod() {
        V8Array undefined = v8.getArray("array");
        undefined.registerJavaMethod(Mockito.mock(JavaVoidCallback.class), "name");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRegisterAnyJavaMethod() {
        V8Array undefined = v8.getArray("array");
        undefined.registerJavaMethod(new Object(), "toString", "toString", new Class<?>[0], false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetIntegerIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getInteger(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBooleanIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getBoolean(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDoubleIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getDouble(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetStringIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getString(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetObjectIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getObject(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetArrayIndexUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getArray(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushIntUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.push(7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushBooleanUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.push(false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushDoubleUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.push(7.7);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushStringUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.push("bar");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushV8ObjectUndefined() {
        V8Array undefined = v8.getArray("array");
        V8Object object = new V8Object(v8);
        try {
            undefined.push(object);
        } finally {
            object.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushV8ArrayUndefined() {
        V8Array undefined = v8.getArray("array");
        V8Array array = new V8Array(v8);
        try {
            undefined.push(array);
        } finally {
            array.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushUndefinedUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.pushUndefined();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetIntsUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getIntegers(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetInts2Undefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getIntegers(0, 1, new int[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDoublesUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getDoubles(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDoubles2Undefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getDoubles(0, 1, new double[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBooleansUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getBooleans(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBooleans2Undefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getBooleans(0, 1, new boolean[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetStringsUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getStrings(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetStrings2Undefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getStrings(0, 1, new String[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLengthUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.length();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTypeUndefined() {
        V8Array undefined = v8.getArray("array");
        undefined.getType();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetIndex() {
        V8Array undefined = v8.getArray("array");
        undefined.get(7);
    }

    @Test
    public void testGetIsInteger() {
        V8Array array = v8.executeArrayScript("foo = [7]");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof Integer));
        Assert.assertEquals(7, result);
        array.close();
    }

    @Test
    public void testGetIsDouble() {
        V8Array array = v8.executeArrayScript("foo = [7.7]");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof Double));
        Assert.assertEquals(7.7, result);
        array.close();
    }

    @Test
    public void testGetIsString() {
        V8Array array = v8.executeArrayScript("foo = ['bar']");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof String));
        Assert.assertEquals("bar", result);
        array.close();
    }

    @Test
    public void testGetIsBoolean() {
        V8Array array = v8.executeArrayScript("foo = [true]");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof Boolean));
        Assert.assertEquals(true, result);
        array.close();
    }

    @Test
    public void testGetIsObject() {
        V8Array array = v8.executeArrayScript("foo = [{}]");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof V8Object));
        array.close();
        release();
    }

    @Test
    public void testGetIsArray() {
        V8Array array = v8.executeArrayScript("foo = [[]]");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof V8Array));
        array.close();
        release();
    }

    @Test
    public void testGetIsNull() {
        V8Array array = v8.executeArrayScript("foo = [null]");
        Object result = array.get(0);
        Assert.assertNull(result);
        array.close();
    }

    @Test
    public void testGetIsUndefined() {
        V8Array array = v8.executeArrayScript("foo = []");
        Object result = array.get(0);
        Assert.assertEquals(V8.getUndefined(), result);
        array.close();
    }

    @Test
    public void testGetIsFunction() {
        V8Array array = v8.executeArrayScript("foo = [function(){}]");
        Object result = array.get(0);
        Assert.assertTrue((result instanceof V8Function));
        array.close();
        release();
    }

    /**
     * * Null **
     */
    @Test
    public void testNullStrinsgInArray() {
        V8Array array = v8.executeArrayScript("x = [null]; x;");
        Assert.assertNull(array.getString(0));
        array.close();
    }

    @Test
    public void testIsNull() {
        V8Array array = v8.executeArrayScript("x = [null]; x;");
        Assert.assertEquals(V8Value.NULL, array.getType(0));
        array.close();
    }

    @Test
    public void testGetNullInArray() {
        V8Array array = v8.executeArrayScript("x = [null]; x;");
        Assert.assertNull(array.getObject(0));
        array.close();
    }

    @Test
    public void testGenericPushUndefined() {
        V8Array array = new V8Array(v8);
        array.push(((Object) (V8.getUndefined())));
        Assert.assertEquals(V8.getUndefined(), array.get(0));
        array.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericPush_IllegalArgument() {
        V8Array array = new V8Array(v8);
        try {
            array.push(new Object());
        } finally {
            array.close();
        }
    }

    @Test
    public void testGenericPushNull() {
        V8Array array = new V8Array(v8);
        array.push(((Object) (null)));
        Assert.assertNull(array.get(0));
        array.close();
    }

    @Test
    public void testGenericPushInteger() {
        V8Array array = new V8Array(v8);
        array.push(new Integer(7));
        Assert.assertEquals(7, array.get(0));
        array.close();
    }

    @Test
    public void testGenericPushDouble() {
        V8Array array = new V8Array(v8);
        array.push(new Double(7.7777));
        Assert.assertEquals(7.7777, ((Double) (array.get(0))), 1.0E-4);
        array.close();
    }

    @Test
    public void testGenericPushBoolean() {
        V8Array array = new V8Array(v8);
        array.push(new Boolean(true));
        Assert.assertTrue(((Boolean) (array.get(0))));
        array.close();
    }

    @Test
    public void testGenericPushObject() {
        V8Object object = new V8Object(v8);
        V8Array array = new V8Array(v8);
        array.push(object);
        V8Value result = ((V8Value) (array.get(0)));
        Assert.assertEquals(object, result);
        array.close();
        object.close();
        result.close();
    }

    @Test(expected = Error.class)
    public void testGenericPushObject_WrongRuntime() {
        V8 newV8 = V8.createV8Runtime();
        V8Object object = new V8Object(newV8);
        V8Array array = new V8Array(v8);
        try {
            array.push(object);
        } finally {
            array.close();
            object.close();
            newV8.close();
        }
    }

    @Test
    public void testGenericPushFloat() {
        V8Array array = new V8Array(v8);
        array.push(new Float(3.14));
        Assert.assertEquals(3.14, ((Double) (array.get(0))), 1.0E-4);
        array.close();
    }

    @Test
    public void testGenericPushString() {
        V8Array array = new V8Array(v8);
        array.push(((Object) ("foo")));
        Assert.assertEquals("foo", array.get(0));
        array.close();
    }

    @Test
    public void testAddNullAsObject() {
        V8Array array = new V8Array(v8);
        array.push(((V8Object) (null)));
        Assert.assertNull(array.getObject(0));
        array.close();
    }

    @Test
    public void testAddNullAsString() {
        V8Array array = new V8Array(v8);
        array.push(((String) (null)));
        Assert.assertNull(array.getObject(0));
        array.close();
    }

    @Test
    public void testAddNullAsArray() {
        V8Array array = new V8Array(v8);
        array.push(((V8Array) (null)));
        Assert.assertNull(array.getArray(0));
        array.close();
    }

    /**
     * * Get Byte **
     */
    @Test
    public void testGetIntegerAsByte() {
        V8Array array = v8.executeArrayScript("foo = [3]");
        byte result = array.getByte(0);
        Assert.assertEquals(3, result);
        array.close();
    }

    @Test
    public void testGetIntegerAsByte_Overflow() {
        V8Array array = v8.executeArrayScript("foo = [256]");
        byte result = array.getByte(0);
        Assert.assertEquals(0, result);
        array.close();
    }

    /**
     * * Get Int **
     */
    @Test
    public void testArrayGetInt() {
        V8Array array = v8.executeArrayScript("[1,2,8];");
        Assert.assertEquals(1, array.getInteger(0));
        Assert.assertEquals(2, array.getInteger(1));
        Assert.assertEquals(8, array.getInteger(2));
        array.close();
    }

    @Test
    public void testArrayGetIntFromDouble() {
        V8Array array = v8.executeArrayScript("[1.1, 2.2];");
        Assert.assertEquals(1, array.getInteger(0));
        Assert.assertEquals(2, array.getInteger(1));
        array.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetIntWrongType() {
        V8Array array = v8.executeArrayScript("['string'];");
        try {
            array.getInteger(0);
        } finally {
            array.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetIntIndexOutOfBounds() {
        V8Array array = v8.executeArrayScript("[];");
        try {
            array.getInteger(0);
        } finally {
            array.close();
        }
    }

    @Test
    public void testArrayGetIntChangeValue() {
        V8Array array = v8.executeArrayScript("foo = []; foo;");
        v8.executeVoidScript("foo[0] = 1");
        Assert.assertEquals(1, array.getInteger(0));
        array.close();
    }

    @Test
    public void testLargeArrayGetInt() {
        V8Array array = v8.executeArrayScript("foo = []; for ( var i = 0; i < 10000; i++) {foo[i] = i;}; foo");
        Assert.assertEquals(10000, array.length());
        for (int i = 0; i < 10000; i++) {
            Assert.assertEquals(i, array.getInteger(i));
        }
        array.close();
    }

    /**
     * * Get Boolean **
     */
    @Test
    public void testArrayGetBoolean() {
        V8Array array = v8.executeArrayScript("[true,false,false];");
        Assert.assertTrue(array.getBoolean(0));
        Assert.assertFalse(array.getBoolean(1));
        Assert.assertFalse(array.getBoolean(2));
        array.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetBooleanWrongType() {
        V8Array array = v8.executeArrayScript("['string'];");
        try {
            array.getBoolean(0);
        } finally {
            array.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetBooleanIndexOutOfBounds() {
        V8Array array = v8.executeArrayScript("[];");
        try {
            array.getBoolean(0);
        } finally {
            array.close();
        }
    }

    @Test
    public void testArrayGetBooleanChangeValue() {
        V8Array array = v8.executeArrayScript("foo = []; foo;");
        v8.executeVoidScript("foo[0] = true");
        Assert.assertTrue(array.getBoolean(0));
        array.close();
    }

    /**
     * * Get Double **
     */
    @Test
    public void testArrayGetDouble() {
        V8Array array = v8.executeArrayScript("[3.1,4.2,5.3];");
        Assert.assertEquals(3.1, array.getDouble(0), 1.0E-5);
        Assert.assertEquals(4.2, array.getDouble(1), 1.0E-5);
        Assert.assertEquals(5.3, array.getDouble(2), 1.0E-5);
        array.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetDoubleWrongType() {
        V8Array array = v8.executeArrayScript("['string'];");
        try {
            array.getDouble(0);
        } finally {
            array.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetDoubleIndexOutOfBounds() {
        V8Array array = v8.executeArrayScript("[];");
        try {
            array.getDouble(0);
        } finally {
            array.close();
        }
    }

    @Test
    public void testArrayGetDoubleChangeValue() {
        V8Array array = v8.executeArrayScript("foo = []; foo;");
        v8.executeVoidScript("foo[0] = 3.14159");
        Assert.assertEquals(3.14159, array.getDouble(0), 1.0E-6);
        array.close();
    }

    /**
     * * Get String **
     */
    @Test
    public void testArrayGetString() {
        V8Array array = v8.executeArrayScript("['first','second','third'];");
        Assert.assertEquals("first", array.getString(0));
        Assert.assertEquals("second", array.getString(1));
        Assert.assertEquals("third", array.getString(2));
        array.close();
    }

    @Test
    public void testArrayGetString_Unicode() {
        V8Array array = v8.executeArrayScript("['?','?','?'];");
        Assert.assertEquals("?", array.getString(0));
        Assert.assertEquals("?", array.getString(1));
        Assert.assertEquals("?", array.getString(2));
        array.close();
    }

    @Test
    public void testArrayGetStrings_Unicode() {
        V8Array array = v8.executeArrayScript("['?','?','?'];");
        String[] result = array.getStrings(0, 3);
        Assert.assertEquals("?", result[0]);
        Assert.assertEquals("?", result[1]);
        Assert.assertEquals("?", result[2]);
        array.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetStringWrongType() {
        V8Array array = v8.executeArrayScript("[42];");
        try {
            array.getString(0);
        } finally {
            array.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetStringIndexOutOfBounds() {
        V8Array array = v8.executeArrayScript("[];");
        try {
            array.getString(0);
        } finally {
            array.close();
        }
    }

    @Test
    public void testArrayGetStringChangeValue() {
        V8Array array = v8.executeArrayScript("foo = []; foo");
        v8.executeVoidScript("foo[0] = 'test'");
        Assert.assertEquals("test", array.getString(0));
        array.close();
    }

    /**
     * ** Get Object ***
     */
    @Test
    public void testArrayGetObject() {
        V8Array array = v8.executeArrayScript("[{name : 'joe', age : 38 }];");
        V8Object object = array.getObject(0);
        Assert.assertEquals("joe", object.getString("name"));
        Assert.assertEquals(38, object.getInteger("age"));
        array.close();
        object.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetObjectWrongType() {
        V8Array array = v8.executeArrayScript("[42];");
        try {
            array.getObject(0);
        } finally {
            array.close();
        }
    }

    @Test
    public void testArrayGetObjectIndexOutOfBounds() {
        V8Array array = v8.executeArrayScript("[];");
        V8Object result = array.getObject(0);
        Assert.assertTrue(result.isUndefined());
        array.close();
    }

    @Test
    public void testArrayGetObjectChangeValue() {
        V8Array array = v8.executeArrayScript("foo = []; foo");
        v8.executeVoidScript("foo[0] = {foo:'bar'}");
        V8Object obj = array.getObject(0);
        Assert.assertEquals("bar", obj.getString("foo"));
        array.close();
        obj.close();
    }

    /**
     * * Get Array **
     */
    @Test
    public void testArrayGetArray() {
        V8Array array = v8.executeArrayScript("[[1,2,3],['first','second'],[true]];");
        V8Array array1 = array.getArray(0);
        V8Array array2 = array.getArray(1);
        V8Array array3 = array.getArray(2);
        Assert.assertEquals(3, array1.length());
        Assert.assertEquals(2, array2.length());
        Assert.assertEquals(1, array3.length());
        array.close();
        array1.close();
        array2.close();
        array3.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testArrayGetArrayWrongType() {
        V8Array array = v8.executeArrayScript("[42];");
        try {
            array.getArray(0);
        } finally {
            array.close();
        }
    }

    @Test
    public void testArrayGetArrayIndexOutOfBounds() {
        V8Array array = v8.executeArrayScript("[];");
        V8Array result = array.getArray(0);
        Assert.assertTrue(result.isUndefined());
        array.close();
    }

    @Test
    public void testArrayGetArrayChangeValue() {
        V8Array array = v8.executeArrayScript("foo = []; foo");
        v8.executeVoidScript("foo[0] = [1,2,3]");
        V8Array array1 = array.getArray(0);
        Assert.assertEquals(3, array1.length());
        array.close();
        array1.close();
    }

    /**
     * ** Mixed Array ***
     */
    @Test
    public void testMixedArray() {
        V8Array array = v8.executeArrayScript("['a', 3, 3.1, true];");
        Assert.assertEquals(4, array.length());
        Assert.assertEquals("a", array.getString(0));
        Assert.assertEquals(3, array.getInteger(1));
        Assert.assertEquals(3.1, array.getDouble(2), 1.0E-5);
        Assert.assertTrue(array.getBoolean(3));
        array.close();
    }

    /**
     * * Add Primitives **
     */
    @Test
    public void testAddInt() {
        V8Array array = new V8Array(v8);
        array.push(7);
        array.push(8);
        array.push(9);
        Assert.assertEquals(3, array.length());
        Assert.assertEquals(7, array.getInteger(0));
        Assert.assertEquals(8, array.getInteger(1));
        Assert.assertEquals(9, array.getInteger(2));
        array.close();
    }

    @Test
    public void testAddString() {
        V8Array array = new V8Array(v8);
        array.push("first");
        array.push("second");
        array.push("third");
        array.push("forth");
        Assert.assertEquals(4, array.length());
        Assert.assertEquals("first", array.getString(0));
        Assert.assertEquals("second", array.getString(1));
        Assert.assertEquals("third", array.getString(2));
        Assert.assertEquals("forth", array.getString(3));
        array.close();
    }

    @Test
    public void testAddDouble() {
        V8Array array = new V8Array(v8);
        array.push(1.1);
        array.push(2.2);
        array.push(3.3);
        array.push(4.9);
        Assert.assertEquals(4, array.length());
        Assert.assertEquals(1.1, array.getDouble(0), 1.0E-6);
        Assert.assertEquals(2.2, array.getDouble(1), 1.0E-6);
        Assert.assertEquals(3.3, array.getDouble(2), 1.0E-6);
        Assert.assertEquals(4.9, array.getDouble(3), 1.0E-6);
        array.close();
    }

    @Test
    public void testAddBoolean() {
        V8Array array = new V8Array(v8);
        array.push(true);
        array.push(false);
        Assert.assertEquals(2, array.length());
        Assert.assertTrue(array.getBoolean(0));
        Assert.assertFalse(array.getBoolean(1));
        array.close();
    }

    @Test
    public void testAddMixedValues() {
        V8Array array = new V8Array(v8);
        array.push(true);
        array.push(false);
        array.push(1);
        array.push("string");
        array.push(false);
        array.pushUndefined();
        Assert.assertEquals(6, array.length());
        Assert.assertTrue(array.getBoolean(0));
        Assert.assertFalse(array.getBoolean(1));
        Assert.assertEquals(1, array.getInteger(2));
        Assert.assertEquals("string", array.getString(3));
        Assert.assertFalse(array.getBoolean(4));
        Assert.assertEquals(V8Value.UNDEFINED, array.getType(5));
        array.close();
    }

    @Test
    public void testAddToExistingArray() {
        V8Array array = v8.executeArrayScript("[1,2,3,,5];");
        array.push(false);
        Assert.assertEquals(6, array.length());
        Assert.assertFalse(array.getBoolean(5));
        array.close();
    }

    @Test
    public void testSparseArrayLength() {
        V8Array array = v8.executeArrayScript("x = []; x[0] = 'foo'; x[100] = 'bar'; x['boo'] = 'baz'; x");
        Assert.assertEquals(101, array.length());
        array.close();
    }

    @Test
    public void testAddUndefined() {
        V8Array v8Array = new V8Array(v8);
        v8Array.pushUndefined();
        Assert.assertEquals(1, v8Array.length());
        Assert.assertEquals(V8Value.UNDEFINED, v8Array.getType(0));
        v8Array.close();
    }

    @Test
    public void testAddNull() {
        V8Array v8Array = new V8Array(v8);
        v8Array.pushNull();
        Assert.assertEquals(1, v8Array.length());
        Assert.assertEquals(V8Value.NULL, v8Array.getType(0));
        Assert.assertNull(v8Array.getObject(0));
        v8Array.close();
    }

    @Test
    public void testGetNull() {
        V8Array v8Array = v8.executeArrayScript("[null];");
        Assert.assertEquals(V8Value.NULL, v8Array.getType(0));
        v8Array.close();
    }

    @Test
    public void testCreateMatrix() {
        V8Array a1 = new V8Array(v8);
        V8Array a2 = new V8Array(v8);
        V8Array a3 = new V8Array(v8);
        a1.push(1);
        a1.push(2);
        a1.push(3);
        a2.push(4);
        a2.push(5);
        a2.push(6);
        a3.push(7);
        a3.push(8);
        a3.push(9);
        V8Array array = new V8Array(v8);
        array.push(a1);
        array.push(a2);
        array.push(a3);
        V8Array parameters = new V8Array(v8);
        parameters.push(array);
        v8.executeVoidScript("var total = 0; function add(matrix) { for(var i = 0; i < 3; i++) { for (var j = 0; j < 3; j++) { total = total + matrix[i][j]; }}};");
        v8.executeVoidFunction("add", parameters);
        int result = v8.getInteger("total");
        Assert.assertEquals(45, result);
        a1.close();
        a2.close();
        a3.close();
        array.close();
        parameters.close();
    }

    @Test
    public void testCreateArrayOfObjects() {
        V8Object obj1 = new V8Object(v8);
        V8Object obj2 = new V8Object(v8);
        obj1.add("first", "John");
        obj1.add("last", "Smith");
        obj1.add("age", 7);
        obj2.add("first", "Tim");
        obj2.add("last", "Jones");
        obj2.add("age", 8);
        V8Array array = new V8Array(v8);
        array.push(obj1);
        array.push(obj2);
        V8Object result1 = array.getObject(0);
        V8Object result2 = array.getObject(1);
        Assert.assertEquals("John", result1.getString("first"));
        Assert.assertEquals("Smith", result1.getString("last"));
        Assert.assertEquals(7, result1.getInteger("age"));
        Assert.assertEquals("Tim", result2.getString("first"));
        Assert.assertEquals("Jones", result2.getString("last"));
        Assert.assertEquals(8, result2.getInteger("age"));
        obj1.close();
        obj2.close();
        array.close();
        result1.close();
        result2.close();
    }

    /**
     * * Test Types **
     */
    @Test
    public void testGetTypeInt() {
        V8Array v8Array = new V8Array(v8);
        v8Array.push(1);
        Assert.assertEquals(V8Value.INTEGER, v8Array.getType(0));
        v8Array.close();
    }

    @Test
    public void testGetTypeDouble() {
        V8Array v8Array = new V8Array(v8);
        v8Array.push(1.1);
        Assert.assertEquals(V8Value.DOUBLE, v8Array.getType(0));
        v8Array.close();
    }

    @Test
    public void testGetTypeString() {
        V8Array v8Array = new V8Array(v8);
        v8Array.push("String");
        Assert.assertEquals(V8Value.STRING, v8Array.getType(0));
        v8Array.close();
    }

    @Test
    public void testGetTypeBoolean() {
        V8Array v8Array = new V8Array(v8);
        v8Array.push(false);
        Assert.assertEquals(V8Value.BOOLEAN, v8Array.getType(0));
        v8Array.close();
    }

    @Test
    public void testGetTypeArray() {
        V8Array v8Array = new V8Array(v8);
        V8Array value = new V8Array(v8);
        v8Array.push(value);
        Assert.assertEquals(V8Value.V8_ARRAY, v8Array.getType(0));
        v8Array.close();
        value.close();
    }

    @Test
    public void testGetTypeFunction() {
        v8.executeVoidScript("var foo = function() {};");
        V8Object function = v8.getObject("foo");
        V8Array v8Array = new V8Array(v8);
        v8Array.push(function);
        Assert.assertEquals(V8Value.V8_FUNCTION, v8Array.getType(0));
        v8Array.close();
        function.close();
    }

    @Test
    public void testGetTypeObject() {
        V8Array v8Array = new V8Array(v8);
        V8Object value = new V8Object(v8);
        v8Array.push(value);
        Assert.assertEquals(V8Value.V8_OBJECT, v8Array.getType(0));
        v8Array.close();
        value.close();
    }

    @Test
    public void testGetTypeIndexOutOfBounds() {
        V8Array v8Array = new V8Array(v8);
        int result = v8Array.getType(0);
        Assert.assertEquals(V8Value.UNDEFINED, result);
        v8Array.close();
    }

    /**
     * * Equals **
     */
    @Test
    public void testEqualsArray() {
        v8.executeVoidScript("a = [];");
        V8Array o1 = v8.executeArrayScript("a");
        V8Array o2 = v8.executeArrayScript("a");
        Assert.assertEquals(o1, o2);
        o1.close();
        o2.close();
    }

    @Test
    public void testEqualsArrayAndObject() {
        v8.executeVoidScript("a = [];");
        V8Array o1 = v8.executeArrayScript("a");
        V8Object o2 = v8.executeObjectScript("a");
        Assert.assertEquals(o1, o2);
        o1.close();
        o2.close();
    }

    @Test
    public void testNotEqualsArray() {
        V8Array a = v8.executeArrayScript("a = []; a");
        V8Array b = v8.executeArrayScript("b = []; b");
        Assert.assertNotEquals(a, b);
        a.close();
        b.close();
    }

    @Test
    public void testHashEqualsArray() {
        V8Array a = v8.executeArrayScript("a = []; a");
        V8Array b = v8.executeArrayScript("a");
        Assert.assertEquals(a.hashCode(), b.hashCode());
        a.close();
        b.close();
    }

    @Test
    public void testNotEqualsNull() {
        V8Array a = v8.executeArrayScript("a = []; a");
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(null, a);
        a.close();
    }

    @Test
    public void testHashStable() {
        V8Array a = v8.executeArrayScript("a = []; a");
        int hash1 = a.hashCode();
        int hash2 = a.push(true).push(false).push(123).hashCode();
        Assert.assertEquals(hash1, hash2);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfInts() {
        V8Array a = v8.executeArrayScript("[1,2,3,4,5];");
        int result = a.getType(0, 5);
        Assert.assertEquals(V8Value.INTEGER, result);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfDoubles() {
        V8Array a = v8.executeArrayScript("[1.1,2.2,3.3,4.4,5.5];");
        int result = a.getType(1, 3);
        Assert.assertEquals(V8Value.DOUBLE, result);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfStrings() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 1, 2];");
        int result = a.getType(0, 3);
        Assert.assertEquals(V8Value.STRING, result);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfBooleans() {
        V8Array a = v8.executeArrayScript("[1, false, true, false, 2];");
        int result = a.getType(1, 3);
        Assert.assertEquals(V8Value.BOOLEAN, result);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfUndefined() {
        V8Array a = v8.executeArrayScript("[1, undefined, undefined, undefined, 2];");
        int result = a.getType(1, 3);
        Assert.assertEquals(V8Value.UNDEFINED, result);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfArrays() {
        V8Array a = v8.executeArrayScript("[1, [1], [false], ['string'], 2];");
        int result = a.getType(1, 3);
        Assert.assertEquals(V8Value.V8_ARRAY, result);
        a.close();
    }

    @Test
    public void testGetTypeRangeOfObjects() {
        V8Array a = v8.executeArrayScript("[1, {foo:1}, {foo:false}, {foo:'string'}, 2];");
        int result = a.getType(1, 3);
        Assert.assertEquals(V8Value.V8_OBJECT, result);
        a.close();
    }

    @Test
    public void testGetTypeSubRangeOfInts() {
        V8Array a = v8.executeArrayScript("[1,2,3,4,5];");
        int result = a.getType(4, 1);
        Assert.assertEquals(V8Value.INTEGER, result);
        a.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetMixedTypeRangeThrowsUndefinedException() {
        V8Array a = v8.executeArrayScript("[1, false, true, false, 2];");
        try {
            a.getType(0, 5);
        } finally {
            a.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetTypeRangeSizeZeroThrowsUndefinedException() {
        V8Array a = v8.executeArrayScript("[1, false, true, false, 2];");
        try {
            a.getType(0, 0);
        } finally {
            a.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetTypeOutOfBoundsThrowsUndefinedException() {
        V8Array a = v8.executeArrayScript("[1, false, true, false, 2];");
        try {
            a.getType(5, 0);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetArrayOfInts() {
        V8Array a = v8.executeArrayScript("[1,2,3,4,5];");
        int[] result = a.getIntegers(0, 5);
        Assert.assertEquals(5, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);
        Assert.assertEquals(4, result[3]);
        Assert.assertEquals(5, result[4]);
        a.close();
    }

    @Test
    public void testGetSubArrayOfInts() {
        V8Array a = v8.executeArrayScript("[1,2,3,4,5];");
        int[] result = a.getIntegers(4, 1);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(5, result[0]);
        a.close();
    }

    @Test
    public void testGetSubArrayOfInts2() {
        V8Array a = v8.executeArrayScript("[1,2,3,4,5];");
        int[] result = a.getIntegers(3, 2);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(4, result[0]);
        Assert.assertEquals(5, result[1]);
        a.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetIntsWithoutInts() {
        V8Array a = v8.executeArrayScript("[1,'a',3,4,5];");
        try {
            a.getIntegers(0, 5);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetIntsWithDoubles() {
        V8Array a = v8.executeArrayScript("[1,1.1,3,4,5];");
        int[] ints = a.getIntegers(0, 5);
        Assert.assertEquals(5, ints.length);
        Assert.assertEquals(1, ints[0]);
        Assert.assertEquals(1, ints[1]);
        Assert.assertEquals(3, ints[2]);
        Assert.assertEquals(4, ints[3]);
        Assert.assertEquals(5, ints[4]);
        a.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetSubArrayOfIntsOutOfBounds() {
        V8Array a = v8.executeArrayScript("[1,2,3,4,5];");
        try {
            a.getIntegers(3, 3);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetArrayOfDoubles() {
        V8Array a = v8.executeArrayScript("[1.1,2.1,3.1,4.1,5.1];");
        double[] result = a.getDoubles(0, 5);
        Assert.assertEquals(5, result.length);
        Assert.assertEquals(1.1, result[0], 1);
        Assert.assertEquals(2.1, result[1], 1);
        Assert.assertEquals(3.1, result[2], 1);
        Assert.assertEquals(4.1, result[3], 1);
        Assert.assertEquals(5.1, result[4], 1);
        a.close();
    }

    @Test
    public void testGetSubArrayOfDoubles() {
        V8Array a = v8.executeArrayScript("[1.1,2.1,3.1,4.1,5.1];");
        double[] result = a.getDoubles(4, 1);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(5.1, result[0], 1.0E-6);
        a.close();
    }

    @Test
    public void testGetSubArrayOfDoubles2() {
        V8Array a = v8.executeArrayScript("[1.1,2.1,3.1,4.1,5.1];");
        double[] result = a.getDoubles(3, 2);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(4.1, result[0], 1.0E-6);
        Assert.assertEquals(5.1, result[1], 1.0E-6);
        a.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetDoublesWithoutDoubles() {
        V8Array a = v8.executeArrayScript("[1.1,'a',3.1,4.1,5.1];");
        try {
            a.getIntegers(0, 5);
        } finally {
            a.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetSubArrayOfDoublesOutOfBounds() {
        V8Array a = v8.executeArrayScript("[1.1,2.1,3.1,4.1,5.1];");
        try {
            a.getIntegers(3, 3);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetArrayOfBooleans() {
        V8Array a = v8.executeArrayScript("[true, false, true, true, false];");
        boolean[] result = a.getBooleans(0, 5);
        Assert.assertEquals(5, result.length);
        Assert.assertTrue(result[0]);
        Assert.assertFalse(result[1]);
        Assert.assertTrue(result[2]);
        Assert.assertTrue(result[3]);
        Assert.assertFalse(result[4]);
        a.close();
    }

    @Test
    public void testGetArrayOfBytes() {
        V8Array a = v8.executeArrayScript("[0, 1, 2, 3, 256];");
        byte[] result = a.getBytes(0, 5);
        Assert.assertEquals(5, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(1, result[1]);
        Assert.assertEquals(2, result[2]);
        Assert.assertEquals(3, result[3]);
        Assert.assertEquals(0, result[4]);
        a.close();
    }

    @Test
    public void testGetSubArrayOfBooleans() {
        V8Array a = v8.executeArrayScript("[true, false, true, true, false];");
        boolean[] result = a.getBooleans(4, 1);
        Assert.assertEquals(1, result.length);
        Assert.assertFalse(result[0]);
        a.close();
    }

    @Test
    public void testGetSubArrayOfBooleans2() {
        V8Array a = v8.executeArrayScript("[true, false, true, true, false];");
        boolean[] result = a.getBooleans(3, 2);
        Assert.assertEquals(2, result.length);
        Assert.assertTrue(result[0]);
        Assert.assertFalse(result[1]);
        a.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetBooleansWithoutBooleans() {
        V8Array a = v8.executeArrayScript("[true, 'a', false, false, true];");
        try {
            a.getBooleans(0, 5);
        } finally {
            a.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetSubArrayOfBooleansOutOfBounds() {
        V8Array a = v8.executeArrayScript("[true, true, true, true, false];");
        try {
            a.getBooleans(3, 3);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetArrayOfStrings() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd', 'e'];");
        String[] result = a.getStrings(0, 5);
        Assert.assertEquals(5, result.length);
        Assert.assertEquals("a", result[0]);
        Assert.assertEquals("b", result[1]);
        Assert.assertEquals("c", result[2]);
        Assert.assertEquals("d", result[3]);
        Assert.assertEquals("e", result[4]);
        a.close();
    }

    @Test
    public void testGetSubArrayOfStrings() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd', 'e'];");
        String[] result = a.getStrings(4, 1);
        Assert.assertEquals(1, result.length);
        Assert.assertEquals("e", result[0]);
        a.close();
    }

    @Test
    public void testGetSubArrayOfStrings2() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd', 'e'];");
        String[] result = a.getStrings(3, 2);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals("d", result[0]);
        Assert.assertEquals("e", result[1]);
        a.close();
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetStringsWithoutStrings() {
        V8Array a = v8.executeArrayScript("['a', 7, 'c', 'd', 'e'];");
        try {
            a.getStrings(0, 5);
        } finally {
            a.close();
        }
    }

    @Test(expected = V8ResultUndefined.class)
    public void testGetSubArrayOfStringsOutOfBounds() {
        V8Array a = v8.executeArrayScript("['a', 7, 'c', 'd', 'e']");
        try {
            a.getStrings(3, 3);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetArrayTypeInt() {
        V8Array a = v8.executeArrayScript("[1,2,3,4]");
        int type = a.getType();
        Assert.assertEquals(INTEGER, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeDouble() {
        V8Array a = v8.executeArrayScript("[1.1,2.2,3.3,4.4]");
        int type = a.getType();
        Assert.assertEquals(DOUBLE, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeDoubleSingleValue() {
        V8Array a = v8.executeArrayScript("[0.1]");
        int type = a.getType();
        Assert.assertEquals(DOUBLE, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeDoubleMixed() {
        V8Array a = v8.executeArrayScript("[0, 0.1]");
        int type = a.getType();
        Assert.assertEquals(DOUBLE, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeDoubleWithInts2() {
        V8Array a = v8.executeArrayScript("[1,2,3.3,4.4]");
        int type = a.getType();
        Assert.assertEquals(DOUBLE, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeString() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd']");
        int type = a.getType();
        Assert.assertEquals(STRING, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeBoolean() {
        V8Array a = v8.executeArrayScript("[true, false, false, true]");
        int type = a.getType();
        Assert.assertEquals(BOOLEAN, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeObject() {
        V8Array a = v8.executeArrayScript("[{}, {}, {foo:'bar'}]");
        int type = a.getType();
        Assert.assertEquals(V8_OBJECT, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeArray() {
        V8Array a = v8.executeArrayScript("[[], [1,2,3], []]");
        int type = a.getType();
        Assert.assertEquals(V8_ARRAY, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeObjectWithArray1() {
        V8Array a = v8.executeArrayScript("[{}, []]");
        int type = a.getType();
        Assert.assertEquals(V8_OBJECT, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeObjectWithArray2() {
        V8Array a = v8.executeArrayScript("[[], {}]");
        int type = a.getType();
        Assert.assertEquals(V8_OBJECT, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeUndefined() {
        V8Array a = v8.executeArrayScript("[false, 1, true, 0]");
        int type = a.getType();
        Assert.assertEquals(UNDEFINED, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeUndefined2() {
        V8Array a = v8.executeArrayScript("['false', false]");
        int type = a.getType();
        Assert.assertEquals(UNDEFINED, type);
        a.close();
    }

    @Test
    public void testGetArrayTypeEmpty() {
        V8Array a = v8.executeArrayScript("['false', false]");
        int type = a.getType();
        Assert.assertEquals(UNDEFINED, type);
        a.close();
    }

    @Test
    public void testGetIntsSameSizeArray() {
        V8Array a = v8.executeArrayScript("[1,2,3,4]");
        int[] result = new int[4];
        int size = a.getIntegers(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test
    public void testGetIntsBiggerArray() {
        V8Array a = v8.executeArrayScript("[1,2,3,4]");
        int[] result = new int[40];
        int size = a.getIntegers(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIntsSmallerArray() {
        V8Array a = v8.executeArrayScript("[1,2,3,4]");
        int[] result = new int[3];
        try {
            a.getIntegers(0, 4, result);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetIntsPopulatesArray() {
        V8Array a = v8.executeArrayScript("[1,2,3,4]");
        int[] result = new int[4];
        a.getIntegers(0, 4, result);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);
        Assert.assertEquals(4, result[3]);
        a.close();
    }

    @Test
    public void testGetDoubleSameSizeArray() {
        V8Array a = v8.executeArrayScript("[1,2.2,3.3,4]");
        double[] result = new double[4];
        int size = a.getDoubles(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test
    public void testGetDoublesBiggerArray() {
        V8Array a = v8.executeArrayScript("[1.1,2.2,3,4]");
        double[] result = new double[40];
        int size = a.getDoubles(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetDoublesSmallerArray() {
        V8Array a = v8.executeArrayScript("[1,2,3.3,4.4]");
        double[] result = new double[3];
        try {
            a.getDoubles(0, 4, result);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetDoublesPopulatesArray() {
        V8Array a = v8.executeArrayScript("[1.1,2.2,3.3,4]");
        double[] result = new double[4];
        a.getDoubles(0, 4, result);
        Assert.assertEquals(1.1, result[0], 1.0E-6);
        Assert.assertEquals(2.2, result[1], 1.0E-6);
        Assert.assertEquals(3.3, result[2], 1.0E-6);
        Assert.assertEquals(4, result[3], 1.0E-6);
        a.close();
    }

    @Test
    public void testGetBooleanSameSizeArray() {
        V8Array a = v8.executeArrayScript("[true, false, false, true]");
        boolean[] result = new boolean[4];
        int size = a.getBooleans(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test
    public void testGetBytesSameSizeArray() {
        V8Array a = v8.executeArrayScript("[0, 1, 2, 3]");
        byte[] result = new byte[4];
        int size = a.getBytes(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test
    public void testGetBooleanBiggerArray() {
        V8Array a = v8.executeArrayScript("[false, false, false, true]");
        boolean[] result = new boolean[40];
        int size = a.getBooleans(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test
    public void testGetBytesBiggerArray() {
        V8Array a = v8.executeArrayScript("[0, 1, 2, 3]");
        byte[] result = new byte[40];
        int size = a.getBytes(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBooleanSmallerArray() {
        V8Array a = v8.executeArrayScript("[true, true, false, false]");
        boolean[] result = new boolean[3];
        try {
            a.getBooleans(0, 4, result);
        } finally {
            a.close();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesSmallerArray() {
        V8Array a = v8.executeArrayScript("[0, 1, 2, 3]");
        byte[] result = new byte[3];
        try {
            a.getBytes(0, 4, result);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetBooleanPopulatesArray() {
        V8Array a = v8.executeArrayScript("[true, false, false, true]");
        boolean[] result = new boolean[4];
        a.getBooleans(0, 4, result);
        Assert.assertTrue(result[0]);
        Assert.assertFalse(result[1]);
        Assert.assertFalse(result[2]);
        Assert.assertTrue(result[3]);
        a.close();
    }

    @Test
    public void testGetBytesPopulatesArray() {
        V8Array a = v8.executeArrayScript("[0, 1, 2, 256]");
        byte[] result = new byte[4];
        a.getBytes(0, 4, result);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(1, result[1]);
        Assert.assertEquals(2, result[2]);
        Assert.assertEquals(0, result[3]);
        a.close();
    }

    @Test
    public void testGetStringSameSizeArray() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd']");
        String[] result = new String[4];
        int size = a.getStrings(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test
    public void testGetStringBiggerArray() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd']");
        String[] result = new String[40];
        int size = a.getStrings(0, 4, result);
        Assert.assertEquals(4, size);
        a.close();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetStringSmallerArray() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd']");
        String[] result = new String[3];
        try {
            a.getStrings(0, 4, result);
        } finally {
            a.close();
        }
    }

    @Test
    public void testGetStringPopulatesArray() {
        V8Array a = v8.executeArrayScript("['a', 'b', 'c', 'd']");
        String[] result = new String[4];
        a.getStrings(0, 4, result);
        Assert.assertEquals("a", result[0]);
        Assert.assertEquals("b", result[1]);
        Assert.assertEquals("c", result[2]);
        Assert.assertEquals("d", result[3]);
        a.close();
    }

    @Test
    public void testUndefinedNotReleased() {
        V8Array.Undefined undefined = new V8Array.Undefined();
        undefined.close();
        Assert.assertFalse(undefined.isReleased());
    }
}

