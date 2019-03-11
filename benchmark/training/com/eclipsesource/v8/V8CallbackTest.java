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


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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


public class V8CallbackTest {
    private V8 v8;

    public interface ICallback {
        public void voidMethodNoParameters();

        public void voidMethodWithParameters(final int a, final double b, final boolean c, final String d, V8Object e);

        public void voidMethodWithObjectParameters(final Integer a);

        public void voidMethodWithArrayParameter(final V8Array array);

        public void voidMethodWithObjectParameter(final V8Object object);

        public void voidMethodWithFunctionParameter(final V8Function object);

        public void voidMethodWithStringParameter(final String string);

        public void voidMethodWithIntParameter(final int i);

        public int intMethodNoParameters();

        public Integer integerMethod();

        public int intMethodWithParameters(final int x, final int b);

        public int intMethodWithArrayParameter(final V8Array array);

        public double doubleMethodNoParameters();

        public float floatMethodNoParameters();

        public double doubleMethodWithParameters(final double x, final double y);

        public boolean booleanMethodNoParameters();

        public boolean booleanMethodWithArrayParameter(final V8Array array);

        public String stringMethodNoParameters();

        public String stringMethodWithArrayParameter(final V8Array array);

        public V8Object v8ObjectMethodNoParameters();

        public V8Object v8ObjectMethodWithObjectParameter(final V8Object object);

        public V8Array v8ArrayMethodNoParameters();

        public V8Array v8ArrayMethodWithStringParameter(final String string);

        public Object objectMethodNoParameter();

        public void voidMethodVarArgs(final Object... args);

        public void voidMethodStringVarArgs(final String... args);

        public void voidMethodV8ObjectVarArgs(final V8Object... args);

        public void voidMethodVarArgsAndOthers(int x, int y, final Object... args);

        public void voidMethodVarArgsReceiverAndOthers(V8Object recier, int x, int y, final Object... args);
    }

    @Test
    public void testVoidMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testIntMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).intMethodNoParameters();
    }

    @Test
    public void testIntMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(7).when(callback).intMethodNoParameters();
        v8.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);
        int result = v8.executeIntegerScript("foo();");
        Assert.assertEquals(7, result);
    }

    @Test
    public void testIntegerMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(8).when(callback).integerMethod();
        v8.registerJavaMethod(callback, "integerMethod", "foo", new Class<?>[0]);
        int result = v8.executeIntegerScript("foo();");
        Assert.assertEquals(8, result);
    }

    @Test
    public void testDoubleMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).doubleMethodNoParameters();
    }

    @Test
    public void testDoubleMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(3.14159).when(callback).doubleMethodNoParameters();
        v8.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);
        double result = v8.executeDoubleScript("foo();");
        Assert.assertEquals(3.14159, result, 1.0E-7);
    }

    @Test
    public void testFloatMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(3.14159F).when(callback).floatMethodNoParameters();
        v8.registerJavaMethod(callback, "floatMethodNoParameters", "foo", new Class<?>[0]);
        double result = v8.executeDoubleScript("foo();");
        Assert.assertEquals(3.14159F, result, 1.0E-7);
    }

    @Test
    public void testBooleanMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).booleanMethodNoParameters();
    }

    @Test
    public void testBooleanMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(true).when(callback).booleanMethodNoParameters();
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8.executeBooleanScript("foo();");
        Assert.assertTrue(result);
    }

    @Test
    public void testStringMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).stringMethodNoParameters();
    }

    @Test
    public void testCallbackWithFunctionInParameterList() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        v8.executeVoidScript("var bar = function() {}; foo(bar);");
        Mockito.verify(callback).voidMethodWithObjectParameter(((V8Object) (ArgumentMatchers.isNotNull())));
    }

    @Test
    public void testCallbackWithExplicitFunctionInParameterList() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithFunctionParameter", "foo", new Class<?>[]{ V8Function.class });
        v8.executeVoidScript("var bar = function() {}; foo(bar);");
        Mockito.verify(callback).voidMethodWithFunctionParameter(((V8Function) (ArgumentMatchers.isNotNull())));
    }

    @Test
    public void testCallbackWithUndefinedInParameterList() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        v8.executeVoidScript("foo(undefined);");
        Mockito.verify(callback).voidMethodWithObjectParameter(new V8Object.Undefined());
    }

    @Test
    public void testCallbackWithNullInStringParameterList() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithStringParameter", "foo", new Class<?>[]{ String.class });
        v8.executeVoidScript("foo(null);");
        Mockito.verify(callback).voidMethodWithStringParameter(null);
    }

    @Test
    public void testCallbackVarArgsWithUndefined() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgs", "foo", new Class<?>[]{ Object[].class });
        v8.executeVoidScript("foo(undefined);");
        Mockito.verify(callback).voidMethodVarArgs(new V8Object.Undefined());
    }

    @Test
    public void testCallbackStringVarArgs() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodStringVarArgs", "foo", new Class<?>[]{ String[].class });
        v8.executeVoidScript("foo('bar');");
        Mockito.verify(callback).voidMethodStringVarArgs(ArgumentMatchers.eq("bar"));
    }

    @Test
    public void testCallbackV8ObjectVarArgs() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodV8ObjectVarArgs", "foo", new Class<?>[]{ V8Object[].class });
        v8.executeVoidScript("foo({});");
        Mockito.verify(callback).voidMethodV8ObjectVarArgs(ArgumentMatchers.any(V8Object.class));
        Mockito.verify(callback).voidMethodV8ObjectVarArgs(((V8Object[]) (ArgumentMatchers.notNull())));
    }

    @Test
    public void testCallbackV8ArrayVarArgs() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodV8ObjectVarArgs", "foo", new Class<?>[]{ V8Object[].class });
        v8.executeVoidScript("foo([]);");
        Mockito.verify(callback).voidMethodV8ObjectVarArgs(ArgumentMatchers.any(V8Array.class));
        Mockito.verify(callback).voidMethodV8ObjectVarArgs(((V8Object[]) (ArgumentMatchers.notNull())));
    }

    @Test
    public void testCallbackWithNullInParameterList() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        v8.executeVoidScript("foo(null);");
        Mockito.verify(callback).voidMethodWithObjectParameter(null);
    }

    @Test
    public void testCallbackWithEmptyParameterList() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).voidMethodWithObjectParameter(new V8Object.Undefined());
    }

    @Test
    public void testStringMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn("bar").when(callback).stringMethodNoParameters();
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);
        String result = v8.executeStringScript("foo();");
        Assert.assertEquals("bar", result);
    }

    @Test
    public void testStringMethodCalledFromScriptWithNull() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(null).when(callback).stringMethodNoParameters();
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8.executeBooleanScript("foo() === null");
        Assert.assertTrue(result);
    }

    @Test
    public void testV8ObjectMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).v8ObjectMethodNoParameters();
    }

    @Test(expected = V8RuntimeException.class)
    public void testReturnReleasedV8ObjectThrowsException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<V8Object>() {
            @Override
            public V8Object answer(final InvocationOnMock invocation) throws Throwable {
                V8Object result = new V8Object(v8);
                result.close();
                return result;
            }
        }).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeObjectScript("foo();");
    }

    @Test
    public void testV8ObjectMethodReturnsUndefined() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(V8.getUndefined()).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8.executeBooleanScript("typeof foo() === 'undefined'");
        Assert.assertTrue(result);
    }

    @Test
    public void testV8ObjectMethodReturnsNull() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(null).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8.executeBooleanScript("foo() === null");
        Assert.assertTrue(result);
    }

    @Test
    public void testV8ObjectMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8Object object = new V8Object(v8);
        object.add("name", "john");
        Mockito.doReturn(object).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        V8Object result = v8.executeObjectScript("foo();");
        Assert.assertEquals("john", result.getString("name"));
        result.close();
    }

    @Test
    public void testV8ObjectMethodReleasesResults() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8Object object = new V8Object(v8);
        Mockito.doReturn(object).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Assert.assertTrue(object.isReleased());
    }

    @Test
    public void testV8ArrayMethodCalledFromVoidScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Mockito.verify(callback).v8ArrayMethodNoParameters();
    }

    @Test
    public void testV8ArrayMethodReturnsUndefined() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(new V8Array.Undefined()).when(callback).v8ArrayMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8.executeBooleanScript("typeof foo() === 'undefined'");
        Assert.assertTrue(result);
    }

    @Test
    public void testV8ArrayMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8Array array = new V8Array(v8);
        array.push("john");
        Mockito.doReturn(array).when(callback).v8ArrayMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        V8Array result = v8.executeArrayScript("foo();");
        Assert.assertEquals("john", result.getString(0));
        result.close();
    }

    @Test
    public void testV8TypedArrayMethodCalledFromScriptWithResult() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8ArrayBuffer arrayBuffer = new V8ArrayBuffer(v8, 100);
        V8TypedArray array = new V8TypedArray(v8, arrayBuffer, INTEGER, 0, 25);
        for (int i = 0; i < 25; i++) {
            array.add(("" + i), i);
        }
        Mockito.doReturn(array).when(callback).v8ArrayMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        V8Array result = v8.executeArrayScript("foo();");
        Assert.assertTrue((result instanceof V8TypedArray));
        for (int i = 0; i < 25; i++) {
            Assert.assertEquals(i, result.getInteger(i));
        }
        arrayBuffer.close();
        result.close();
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Int32Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (INT_32_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Int32Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Int8Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (INT_8_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Int8Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Int16Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (INT_16_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Int16Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Float32Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (FLOAT_32_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Float32Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Float64Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (FLOAT_64_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Float64Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Uint8Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (UNSIGNED_INT_8_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Uint8Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Uint8ClampledArray() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (UNSIGNED_INT_8_CLAMPED_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Uint8ClampedArray(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Uint16Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (UNSIGNED_INT_16_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Uint16Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackWithTypedArray_Uint32Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Boolean invoke(final V8Object receiver, final V8Array parameters) {
                V8TypedArray result = getV8TypedArray();
                try {
                    return (result.getType()) == (UNSIGNED_INT_32_ARRAY);
                } finally {
                    result.close();
                }
            }
        };
        v8.registerJavaMethod(callback, "callback");
        Object result = v8.executeScript("callback(new Uint32Array(24));");
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testInvokeCallbackReturnsArrayBuffer() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                V8ArrayBuffer arrayBuffer = new V8ArrayBuffer(v8, 8);
                arrayBuffer.put(((byte) (8)));
                return arrayBuffer;
            }
        };
        v8.registerJavaMethod(callback, "callback");
        int result = v8.executeIntegerScript(("\n" + ("var buffer = callback();\n" + "new Int8Array(buffer)[0]")));
        Assert.assertEquals(8, result);
    }

    @Test
    public void testInvokeCallbackWithArrayAsParameter_PassedTypedArray() {
        class MyCallback {
            @SuppressWarnings("unused")
            public int testArray(final V8Array array) {
                return array.length();
            }
        }
        MyCallback callback = new MyCallback();
        v8.registerJavaMethod(callback, "testArray", "testArray", new Class[]{ V8Array.class });
        int result = v8.executeIntegerScript(("\n" + ((("var array = new Float32Array(5);\n" + "for (var i = 0; i < 5; i++) \n") + "  array[i] = i / 1000; ") + "testArray(array);")));
        Assert.assertEquals(5, result);
    }

    @Test
    public void testInvokeCallbackWithArrayBufferAsParameter() {
        class MyCallback {
            @SuppressWarnings("unused")
            public int testArray(final V8ArrayBuffer arrayBuffer) {
                return arrayBuffer.limit();
            }
        }
        MyCallback callback = new MyCallback();
        v8.registerJavaMethod(callback, "testArray", "testArray", new Class[]{ V8ArrayBuffer.class });
        int result = v8.executeIntegerScript(("\n" + ("var arrayBuffer = new ArrayBuffer(8);\n" + "testArray(arrayBuffer);")));
        Assert.assertEquals(8, result);
    }

    @Test
    public void testInvokeCallbackWithTypedArrayAsParameter() {
        class MyCallback {
            @SuppressWarnings("unused")
            public int testArray(final V8Array array) {
                Assert.fail("Test should have invoked the other method.");
                return 0;
            }

            @SuppressWarnings("unused")
            public int testArray(final V8TypedArray array) {
                return array.length();
            }
        }
        MyCallback callback = new MyCallback();
        v8.registerJavaMethod(callback, "testArray", "testArray", new Class[]{ V8TypedArray.class });
        int result = v8.executeIntegerScript(("\n" + ((("var array = new Float32Array(5);\n" + "for (var i = 0; i < 5; i++) \n") + "  array[i] = i / 1000; ") + "testArray(array);")));
        Assert.assertEquals(5, result);
    }

    @Test
    public void testInvokeCallbackReturnsTypedArray_Int8Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                V8ArrayBuffer arrayBuffer = new V8ArrayBuffer(v8, 8);
                V8TypedArray array = new V8TypedArray(v8, arrayBuffer, V8Value.INT_8_ARRAY, 0, 8);
                array.add("0", 8);
                arrayBuffer.close();
                return array;
            }
        };
        v8.registerJavaMethod(callback, "callback");
        int result = v8.executeIntegerScript(("\n" + ("var array = callback();\n" + "array[0]")));
        Assert.assertEquals(8, result);
    }

    @Test
    public void testInvokeCallbackReturnsTypedArray_Int16Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                V8ArrayBuffer arrayBuffer = new V8ArrayBuffer(v8, 8);
                V8TypedArray array = new V8TypedArray(v8, arrayBuffer, V8Value.INT_16_ARRAY, 0, 4);
                array.add("0", 8000);
                arrayBuffer.close();
                return array;
            }
        };
        v8.registerJavaMethod(callback, "callback");
        int result = v8.executeIntegerScript(("\n" + ("var array = callback();\n" + "array[0]")));
        Assert.assertEquals(8000, result);
    }

    @Test
    public void testInvokeCallbackReturnsTypedArray_Int32Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                V8ArrayBuffer arrayBuffer = new V8ArrayBuffer(v8, 8);
                V8TypedArray array = new V8TypedArray(v8, arrayBuffer, V8Value.INT_32_ARRAY, 0, 2);
                array.add("0", 800000000);
                arrayBuffer.close();
                return array;
            }
        };
        v8.registerJavaMethod(callback, "callback");
        int result = v8.executeIntegerScript(("\n" + ("var array = callback();\n" + "array[0]")));
        Assert.assertEquals(800000000, result);
    }

    @Test
    public void testInvokeCallbackReturnsTypedArray_Float32Array() {
        JavaCallback callback = new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                V8ArrayBuffer arrayBuffer = new V8ArrayBuffer(v8, 8);
                V8TypedArray array = new V8TypedArray(v8, arrayBuffer, V8Value.FLOAT_32_ARRAY, 0, 2);
                array.add("0", 3.14);
                arrayBuffer.close();
                return array;
            }
        };
        v8.registerJavaMethod(callback, "callback");
        float result = ((float) (v8.executeDoubleScript(("\n" + ("var array = callback();\n" + "array[0]")))));
        Assert.assertEquals(3.14, result, 0.1);
    }

    @Test
    public void testV8ArrayMethodReleasesResults() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8Array object = new V8Array(v8);
        Mockito.doReturn(object).when(callback).v8ArrayMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeVoidScript("foo();");
        Assert.assertTrue(object.isReleased());
    }

    @Test
    public void testVoidFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8Object.executeVoidFunction("foo", null);
        Mockito.verify(callback).voidMethodNoParameters();
        v8Object.close();
    }

    @Test
    public void testVoidFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8Array.executeVoidFunction("foo", null);
        Mockito.verify(callback).voidMethodNoParameters();
        v8Array.close();
    }

    @Test
    public void testIntFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(99).when(callback).intMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);
        int result = v8Object.executeIntegerFunction("foo", null);
        Mockito.verify(callback).intMethodNoParameters();
        Assert.assertEquals(99, result);
        v8Object.close();
    }

    @Test
    public void testIntFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(99).when(callback).intMethodNoParameters();
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);
        int result = v8Array.executeIntegerFunction("foo", null);
        Mockito.verify(callback).intMethodNoParameters();
        Assert.assertEquals(99, result);
        v8Array.close();
    }

    @Test
    public void testDoubleFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(99.9).when(callback).doubleMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);
        double result = v8Object.executeDoubleFunction("foo", null);
        Mockito.verify(callback).doubleMethodNoParameters();
        Assert.assertEquals(99.9, result, 1.0E-6);
        v8Object.close();
    }

    @Test
    public void testDoubleFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(99.9).when(callback).doubleMethodNoParameters();
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);
        double result = v8Array.executeDoubleFunction("foo", null);
        Mockito.verify(callback).doubleMethodNoParameters();
        Assert.assertEquals(99.9, result, 1.0E-6);
        v8Array.close();
    }

    @Test
    public void testBooleanFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(false).when(callback).booleanMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8Object.executeBooleanFunction("foo", null);
        Mockito.verify(callback).booleanMethodNoParameters();
        Assert.assertFalse(result);
        v8Object.close();
    }

    @Test
    public void testBooleanFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(false).when(callback).booleanMethodNoParameters();
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);
        boolean result = v8Array.executeBooleanFunction("foo", null);
        Mockito.verify(callback).booleanMethodNoParameters();
        Assert.assertFalse(result);
        v8Array.close();
    }

    @Test
    public void testStringFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn("mystring").when(callback).stringMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);
        String result = v8Object.executeStringFunction("foo", null);
        Mockito.verify(callback).stringMethodNoParameters();
        Assert.assertEquals("mystring", result);
        v8Object.close();
    }

    @Test
    public void testStringFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn("mystring").when(callback).stringMethodNoParameters();
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);
        String result = v8Array.executeStringFunction("foo", null);
        Mockito.verify(callback).stringMethodNoParameters();
        Assert.assertEquals("mystring", result);
        v8Array.close();
    }

    @Test
    public void testV8ObjectFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(v8.executeObjectScript("x = {first:'bob'}; x")).when(callback).v8ObjectMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        V8Object result = v8Object.executeObjectFunction("foo", null);
        Mockito.verify(callback).v8ObjectMethodNoParameters();
        Assert.assertEquals("bob", result.getString("first"));
        v8Object.close();
        result.close();
    }

    @Test
    public void testV8ObjectFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(v8.executeObjectScript("x = {first:'bob'}; x")).when(callback).v8ObjectMethodNoParameters();
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);
        V8Object result = v8Array.executeObjectFunction("foo", null);
        Mockito.verify(callback).v8ObjectMethodNoParameters();
        Assert.assertEquals("bob", result.getString("first"));
        v8Array.close();
        result.close();
    }

    @Test
    public void testV8ArrayFunctionCallOnJSObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(v8.executeArrayScript("x = ['a','b','c']; x")).when(callback).v8ArrayMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        V8Array result = v8Object.executeArrayFunction("foo", null);
        Mockito.verify(callback).v8ArrayMethodNoParameters();
        Assert.assertEquals(3, result.length());
        Assert.assertEquals("a", result.getString(0));
        Assert.assertEquals("b", result.getString(1));
        Assert.assertEquals("c", result.getString(2));
        v8Object.close();
        result.close();
    }

    @Test
    public void testV8ArrayFunctionCallOnJSArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(v8.executeArrayScript("x = ['a','b','c']; x")).when(callback).v8ArrayMethodNoParameters();
        V8Array v8Array = new V8Array(v8);
        v8Array.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[0]);
        V8Array result = v8Array.executeArrayFunction("foo", null);
        Mockito.verify(callback).v8ArrayMethodNoParameters();
        Assert.assertEquals(3, result.length());
        Assert.assertEquals("a", result.getString(0));
        Assert.assertEquals("b", result.getString(1));
        Assert.assertEquals("c", result.getString(2));
        v8Array.close();
        result.close();
    }

    @Test
    public void testVoidMethodCalledFromIntScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeIntegerScript("foo();1");
        Mockito.verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromDoubleScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeDoubleScript("foo();1.1");
        Mockito.verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromStringScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeStringScript("foo();'test'");
        Mockito.verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromArrayScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeArrayScript("foo();[]").close();
        Mockito.verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromObjectScript() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);
        v8.executeObjectScript("foo(); bar={}; bar;").close();
        Mockito.verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledWithParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithParameters", "foo", new Class<?>[]{ Integer.TYPE, Double.TYPE, Boolean.TYPE, String.class, V8Object.class });
        v8.executeVoidScript("foo(1,1.1, false, 'string', undefined);");
        Mockito.verify(callback).voidMethodWithParameters(1, 1.1, false, "string", new V8Object.Undefined());
    }

    @Test
    public void testIntMethodCalledWithParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(final InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                int x = ((Integer) (args[0]));
                int y = ((Integer) (args[1]));
                return x + y;
            }
        }).when(callback).intMethodWithParameters(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        v8.registerJavaMethod(callback, "intMethodWithParameters", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE });
        int result = v8.executeIntegerScript("foo(8,7);");
        Mockito.verify(callback).intMethodWithParameters(8, 7);
        Assert.assertEquals(15, result);
    }

    @Test
    public void testDoubleMethodCalledWithParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Double>() {
            @Override
            public Double answer(final InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                double x = ((Double) (args[0]));
                double y = ((Double) (args[1]));
                return x + y;
            }
        }).when(callback).doubleMethodWithParameters(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble());
        v8.registerJavaMethod(callback, "doubleMethodWithParameters", "foo", new Class<?>[]{ Double.TYPE, Double.TYPE });
        double result = v8.executeDoubleScript("foo(8.3,7.1);");
        Mockito.verify(callback).doubleMethodWithParameters(8.3, 7.1);
        Assert.assertEquals(15.4, result, 1.0E-6);
    }

    @Test
    public void testVoidMethodCalledWithArrayParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Assert.assertEquals(1, args.length);
                Assert.assertEquals(1, ((V8Array) (args[0])).getInteger(0));
                Assert.assertEquals(2, ((V8Array) (args[0])).getInteger(1));
                Assert.assertEquals(3, ((V8Array) (args[0])).getInteger(2));
                Assert.assertEquals(4, ((V8Array) (args[0])).getInteger(3));
                Assert.assertEquals(5, ((V8Array) (args[0])).getInteger(4));
                return null;
            }
        }).when(callback).voidMethodWithArrayParameter(ArgumentMatchers.any(V8Array.class));
        v8.registerJavaMethod(callback, "voidMethodWithArrayParameter", "foo", new Class<?>[]{ V8Array.class });
        v8.executeVoidScript("foo([1,2,3,4,5]);");
    }

    @Test
    public void testIntMethodCalledWithArrayParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int arrayLength = length();
                int result = 0;
                for (int i = 0; i < arrayLength; i++) {
                    result += ((V8Array) (args[0])).getInteger(i);
                }
                return result;
            }
        }).when(callback).intMethodWithArrayParameter(ArgumentMatchers.any(V8Array.class));
        v8.registerJavaMethod(callback, "intMethodWithArrayParameter", "foo", new Class<?>[]{ V8Array.class });
        int result = v8.executeIntegerScript("foo([1,2,3,4,5]);");
        Assert.assertEquals(15, result);
    }

    @Test
    public void testBooleanMethodCalledWithArrayParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int arrayLength = length();
                int result = 0;
                for (int i = 0; i < arrayLength; i++) {
                    result += ((V8Array) (args[0])).getInteger(i);
                }
                return result > 10;
            }
        }).when(callback).booleanMethodWithArrayParameter(ArgumentMatchers.any(V8Array.class));
        v8.registerJavaMethod(callback, "booleanMethodWithArrayParameter", "foo", new Class<?>[]{ V8Array.class });
        boolean result = v8.executeBooleanScript("foo([1,2,3,4,5]);");
        Assert.assertTrue(result);
    }

    @Test
    public void testStringMethodCalledWithArrayParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int arrayLength = length();
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < arrayLength; i++) {
                    result.append(((V8Array) (args[0])).getString(i));
                }
                return result.toString();
            }
        }).when(callback).stringMethodWithArrayParameter(ArgumentMatchers.any(V8Array.class));
        v8.registerJavaMethod(callback, "stringMethodWithArrayParameter", "foo", new Class<?>[]{ V8Array.class });
        String result = v8.executeStringScript("foo(['a', 'b', 'c', 'd', 'e']);");
        Assert.assertEquals("abcde", result);
    }

    @Test
    public void testArrayMethodCalledWithParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<V8Array>() {
            @Override
            public V8Array answer(final InvocationOnMock invocation) {
                V8Array result = new V8Array(v8);
                String arg = ((String) (invocation.getArguments()[0]));
                String[] split = arg.split(" ");
                for (String string : split) {
                    result.push(string);
                }
                return result;
            }
        }).when(callback).v8ArrayMethodWithStringParameter(ArgumentMatchers.any(String.class));
        v8.registerJavaMethod(callback, "v8ArrayMethodWithStringParameter", "foo", new Class<?>[]{ String.class });
        V8Array result = v8.executeArrayScript("foo('hello world how are you');");
        Assert.assertEquals(5, result.length());
        Assert.assertEquals("hello", result.getString(0));
        Assert.assertEquals("world", result.getString(1));
        Assert.assertEquals("how", result.getString(2));
        Assert.assertEquals("are", result.getString(3));
        Assert.assertEquals("you", result.getString(4));
        result.close();
    }

    @Test
    public void testVoidMethodCalledWithObjectParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Assert.assertEquals(1, args.length);
                Assert.assertEquals("john", getString("first"));
                Assert.assertEquals("smith", getString("last"));
                Assert.assertEquals(7, getInteger("age"));
                return null;
            }
        }).when(callback).voidMethodWithObjectParameter(ArgumentMatchers.any(V8Object.class));
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        v8.executeVoidScript("foo({first:'john', last:'smith', age:7});");
        Mockito.verify(callback).voidMethodWithObjectParameter(ArgumentMatchers.any(V8Object.class));
    }

    @Test
    public void testObjectMethodCalledWithObjectParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                V8Object parameter = ((V8Object) (args[0]));
                V8Object result = new V8Object(v8);
                result.add("first", parameter.getString("last"));
                result.add("last", parameter.getString("first"));
                return result;
            }
        }).when(callback).v8ObjectMethodWithObjectParameter(ArgumentMatchers.any(V8Object.class));
        v8.registerJavaMethod(callback, "v8ObjectMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        V8Object result = v8.executeObjectScript("foo({first:'john', last:'smith'});");
        Assert.assertEquals("smith", result.getString("first"));
        Assert.assertEquals("john", result.getString("last"));
        result.close();
    }

    @Test(expected = RuntimeException.class)
    public void testVoidMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).voidMethodNoParameters();
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test
    public void testCatchJavaExceptionInJS() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).voidMethodNoParameters();
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[]{  });
        v8.executeVoidScript("var caught = false; try {foo();} catch (e) {if ( e === 'My Runtime Exception' ) caught=true;}");
        Assert.assertTrue(v8.getBoolean("caught"));
    }

    @Test
    public void testCatchJavaExceptionInJSWithoutMessage() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException()).when(callback).voidMethodNoParameters();
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[]{  });
        v8.executeVoidScript("var caught = false; try {foo();} catch (e) {if ( e === 'Unhandled Java Exception' ) caught=true;}");
        Assert.assertTrue(v8.getBoolean("caught"));
    }

    @Test
    public void testNonVoidCallbackCatchJavaExceptionInJS() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).booleanMethodNoParameters();
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[]{  });
        v8.executeVoidScript("var caught = false; try {foo();} catch (e) {if ( e === 'My Runtime Exception' ) caught=true;}");
        Assert.assertTrue(v8.getBoolean("caught"));
    }

    @Test
    public void testNonVoidCallbackCatchJavaExceptionInJSWithoutMessage() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException()).when(callback).booleanMethodNoParameters();
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[]{  });
        v8.executeVoidScript("var caught = false; try {foo();} catch (e) {if ( e === 'Unhandled Java Exception' ) caught=true;}");
        Assert.assertTrue(v8.getBoolean("caught"));
    }

    @Test
    public void testJSCatchWillCatchJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).voidMethodNoParameters();
        Mockito.doNothing().when(callback).voidMethodWithStringParameter(ArgumentMatchers.anyString());
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[]{  });
        v8.registerJavaMethod(callback, "voidMethodWithStringParameter", "bar", new Class<?>[]{ String.class });
        v8.executeVoidScript("try {foo();} catch (e) {bar('string');}");
        // Runtime exception should not be thrown
    }

    @Test
    public void testJSCatchWillCatchJavaException2() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).voidMethodNoParameters();
        Mockito.doNothing().when(callback).voidMethodWithStringParameter(ArgumentMatchers.anyString());
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[]{  });
        v8.registerJavaMethod(callback, "voidMethodWithStringParameter", "bar", new Class<?>[]{ String.class });
        v8.executeVoidScript("try {foo();} catch (e) {}");
        // Runtime exception should not be thrown
    }

    @Test(expected = RuntimeException.class)
    public void testIntMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).intMethodNoParameters();
        v8.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testDoubleMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).doubleMethodNoParameters();
        v8.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testBooleanMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).booleanMethodNoParameters();
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testStringMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).stringMethodNoParameters();
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testV8ObjectMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testV8ArrayMethodThrowsJavaException() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doThrow(new RuntimeException("My Runtime Exception")).when(callback).v8ArrayMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ArrayMethodNoParameters", "foo", new Class<?>[]{  });
        try {
            v8.executeVoidScript("foo()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("My Runtime Exception", e.getCause().getMessage());
            throw e;
        }
    }

    @Test
    public void testVoidMethodCallWithMissingObjectArgs() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[]{ V8Object.class });
        v8.executeVoidScript("foo()");
        Mockito.verify(callback).voidMethodWithObjectParameter(new V8Array.Undefined());
    }

    @Test
    public void testObjectMethodReturnsInteger() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(7).when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        int result = v8.executeIntegerFunction("foo", null);
        Assert.assertEquals(7, result);
    }

    @Test
    public void testObjectMethodReturnsBoolean() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(true).when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        boolean result = v8.executeBooleanFunction("foo", null);
        Assert.assertTrue(result);
    }

    @Test
    public void testObjectMethodReturnsDouble() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(7.7).when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        double result = v8.executeDoubleFunction("foo", null);
        Assert.assertEquals(7.7, result, 1.0E-6);
    }

    @Test
    public void testObjectMethodReturnsString() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn("foobar").when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        String result = v8.executeStringFunction("foo", null);
        Assert.assertEquals("foobar", result);
    }

    @SuppressWarnings("resource")
    @Test
    public void testObjectMethodReturnsV8Object() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(add("foo", "bar")).when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        V8Object result = v8.executeObjectFunction("foo", null);
        Assert.assertEquals("bar", result.getString("foo"));
        result.close();
    }

    @SuppressWarnings("resource")
    @Test
    public void testObjectMethodReturnsV8Array() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(push(1).push("a")).when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        V8Array result = v8.executeArrayFunction("foo", null);
        Assert.assertEquals(2, result.length());
        Assert.assertEquals(1, result.getInteger(0));
        Assert.assertEquals("a", result.getString(1));
        result.close();
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testObjectMethodReturnsIncompatibleType() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        Mockito.doReturn(new Date()).when(callback).objectMethodNoParameter();
        v8.registerJavaMethod(callback, "objectMethodNoParameter", "foo", new Class<?>[]{  });
        v8.executeVoidScript("foo()");
    }

    @Test
    public void testVarArgParametersString() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsAndOthers", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE, Object[].class });
        v8.executeVoidScript("foo(1, 2, 'foo', 'bar')");
        Mockito.verify(callback).voidMethodVarArgsAndOthers(1, 2, "foo", "bar");
    }

    @Test
    public void testVarArgParametersObject() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsAndOthers", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE, Object[].class });
        v8.executeVoidScript("foo(1, 2, {}, {foo:'bar'})");
        Mockito.verify(callback).voidMethodVarArgsAndOthers(ArgumentMatchers.eq(1), ArgumentMatchers.eq(2), ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Object.class));
    }

    @Test
    public void testVarArgParametersArray() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsAndOthers", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE, Object[].class });
        v8.executeVoidScript("foo(1, 2, [], [1,2,3])");
        Mockito.verify(callback).voidMethodVarArgsAndOthers(ArgumentMatchers.eq(1), ArgumentMatchers.eq(2), ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Object.class));
    }

    @Test
    public void testVarArgParametersInts() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsAndOthers", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE, Object[].class });
        v8.executeVoidScript("foo(1, 2, 3, 4)");
        Mockito.verify(callback).voidMethodVarArgsAndOthers(1, 2, 3, 4);
    }

    @Test
    public void testVarArgParametersMissing() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsAndOthers", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE, Object[].class });
        v8.executeVoidScript("foo(1, 2)");
        Mockito.verify(callback).voidMethodVarArgsAndOthers(1, 2);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testMissingParamters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithParameters", "foo", new Class<?>[]{ Integer.TYPE, Double.TYPE, Boolean.TYPE, String.class, V8Object.class });
        v8.executeVoidScript("foo()");
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testSomeMissingParamters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithParameters", "foo", new Class<?>[]{ Integer.TYPE, Double.TYPE, Boolean.TYPE, String.class, V8Object.class });
        v8.executeVoidScript("foo(1,2)");
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testMissingIntParamtersWithVarArgs() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsAndOthers", "foo", new Class<?>[]{ Integer.TYPE, Integer.TYPE, Object[].class });
        v8.executeVoidScript("foo(1)");
    }

    @Test
    public void testVarArgsNoReciver() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsReceiverAndOthers", "foo", new Class<?>[]{ V8Object.class, Integer.TYPE, Integer.TYPE, Object[].class }, false);
        v8.executeVoidScript("foo(undefined, 1, 2);");
        Mockito.verify(callback).voidMethodVarArgsReceiverAndOthers(new V8Object.Undefined(), 1, 2);
    }

    @Test
    public void testVarArgsWithReciver() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsReceiverAndOthers", "foo", new Class<?>[]{ V8Object.class, Integer.TYPE, Integer.TYPE, Object[].class }, true);
        V8Array parameters = new V8Array(v8);
        push(2);
        Mockito.doAnswer(constructReflectiveAnswer(v8, parameters, null)).when(callback).voidMethodVarArgsReceiverAndOthers(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        v8.executeVoidScript("foo(1, 2);");
        parameters.close();
    }

    @Test
    public void testAvailableVarArgsWithReciver() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsReceiverAndOthers", "foo", new Class<?>[]{ V8Object.class, Integer.TYPE, Integer.TYPE, Object[].class }, true);
        V8Array parameters = new V8Array(v8);
        push(4);
        Mockito.doAnswer(constructReflectiveAnswer(v8, parameters, null)).when(callback).voidMethodVarArgsReceiverAndOthers(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        v8.executeVoidScript("foo(1, 2, 3, 4);");
        parameters.close();
    }

    @Test
    public void testAvailableVarArgsWithReciver2() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsReceiverAndOthers", "foo", new Class<?>[]{ V8Object.class, Integer.TYPE, Integer.TYPE, Object[].class }, true);
        V8Array parameters = new V8Array(v8);
        push(3).push(false);
        Mockito.doAnswer(constructReflectiveAnswer(v8, parameters, null)).when(callback).voidMethodVarArgsReceiverAndOthers(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        v8.executeVoidScript("foo(1, 2, 3, false);");
        parameters.close();
    }

    @Test
    public void testAvailableVarArgsWithNoReciver() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodVarArgsReceiverAndOthers", "foo", new Class<?>[]{ V8Object.class, Integer.TYPE, Integer.TYPE, Object[].class }, false);
        V8Array parameters = new V8Array(v8);
        push(3).push(false);
        Mockito.doAnswer(constructReflectiveAnswer(null, parameters, null)).when(callback).voidMethodVarArgsReceiverAndOthers(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        v8.executeVoidScript("foo(this, 1, 2, 3, false);");
        parameters.close();
    }

    @Test
    public void testMissingParamtersWithObjectParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameters", "foo", new Class<?>[]{ Integer.class });
        v8.executeVoidScript("foo(1)");
        Mockito.verify(callback).voidMethodWithObjectParameters(1);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testMissingParamtersWithMissingObjectParameters() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameters", "foo", new Class<?>[]{ Integer.class });
        v8.executeVoidScript("foo()");
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testCallJavaMethodMissingInt() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithIntParameter", "foo", new Class<?>[]{ Integer.TYPE });
        v8.executeVoidScript("foo()");
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testCallJavaMethodNullInt() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithIntParameter", "foo", new Class<?>[]{ Integer.TYPE });
        v8.executeVoidScript("foo(null)");
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testCallJavaMethodMissingString() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithStringParameter", "foo", new Class<?>[]{ String.class });
        v8.executeVoidScript("foo()");
    }

    @Test
    public void testCallJavaMethodNullString() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithStringParameter", "foo", new Class<?>[]{ String.class });
        v8.executeVoidScript("foo(null)");
        Mockito.verify(callback).voidMethodWithStringParameter(null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testCallJavaMethodNotInt() {
        V8CallbackTest.ICallback callback = Mockito.mock(V8CallbackTest.ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithIntParameter", "foo", new Class<?>[]{ Integer.TYPE });
        v8.executeVoidScript("foo('bar')");
    }

    @Test
    public void testRegisterJavaCallback() {
        JavaCallback callback = Mockito.mock(JavaCallback.class);
        v8.registerJavaMethod(callback, "foo");
        v8.executeVoidScript("foo()");
        Mockito.verify(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
    }

    @Test
    public void testRegisterJavaCallbackExecuteFunction() {
        JavaCallback callback = Mockito.mock(JavaCallback.class);
        v8.registerJavaMethod(callback, "foo");
        v8.executeVoidFunction("foo", null);
        Mockito.verify(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
    }

    @Test
    public void testInvokeCallbackWithParameters() {
        JavaCallback callback = Mockito.mock(JavaCallback.class);
        v8.registerJavaMethod(callback, "foo");
        V8Object object = new V8Object(v8);
        object.add("foo", "bar");
        V8Array array = new V8Array(v8);
        push(3);
        V8Array parameters = new V8Array(v8);
        parameters.push(7);
        parameters.push("test");
        parameters.push(3.14159);
        parameters.push(true);
        parameters.push(object);
        parameters.push(array);
        Mockito.doAnswer(constructAnswer(null, parameters, null)).when(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
        v8.executeVoidFunction("foo", parameters);
        parameters.close();
        object.close();
        array.close();
    }

    @Test
    public void testRegisterJavaVoidCallback() {
        JavaVoidCallback callback = Mockito.mock(JavaVoidCallback.class);
        v8.registerJavaMethod(callback, "foo");
        v8.executeVoidScript("foo()");
        Mockito.verify(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
    }

    @Test
    public void testRegisterJavaVoidCallbackExecuteFunction() {
        JavaVoidCallback callback = Mockito.mock(JavaVoidCallback.class);
        v8.registerJavaMethod(callback, "foo");
        v8.executeVoidFunction("foo", null);
        Mockito.verify(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
    }

    @Test
    public void testInvokeVoidCallbackWithParameters() {
        JavaVoidCallback callback = Mockito.mock(JavaVoidCallback.class);
        v8.registerJavaMethod(callback, "foo");
        V8Object object = new V8Object(v8);
        object.add("foo", "bar");
        V8Array array = new V8Array(v8);
        push(3);
        V8Array parameters = new V8Array(v8);
        parameters.push(7);
        parameters.push("test");
        parameters.push(3.14159);
        parameters.push(true);
        parameters.push(object);
        parameters.push(array);
        Mockito.doAnswer(constructAnswer(null, parameters, null)).when(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
        v8.executeVoidFunction("foo", parameters);
        parameters.close();
        object.close();
        array.close();
    }

    @Test
    public void testInvokeCallbackWithReturnValue() {
        JavaCallback callback = Mockito.mock(JavaCallback.class);
        v8.registerJavaMethod(callback, "foo");
        Mockito.doAnswer(constructAnswer(null, null, 77)).when(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
        int result = v8.executeIntegerFunction("foo", null);
        Assert.assertEquals(77, result);
    }

    @Test
    public void testInvokeCallbackFunctionUsesReciver() {
        V8Object bar = v8.executeObjectScript("var bar = {}; bar;");
        JavaVoidCallback callback = Mockito.mock(JavaVoidCallback.class);
        bar.registerJavaMethod(callback, "foo");
        Mockito.doAnswer(constructAnswer(bar, null, 77)).when(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
        bar.executeVoidFunction("foo", null);
        bar.close();
    }

    @Test
    public void testInvokeCallbackOnGlobalFunctionUsesGlobalScopeAsReciver() {
        JavaVoidCallback callback = Mockito.mock(JavaVoidCallback.class);
        v8.registerJavaMethod(callback, "foo");
        Mockito.doAnswer(constructAnswer(v8, null, null)).when(callback).invoke(ArgumentMatchers.any(V8Object.class), ArgumentMatchers.any(V8Array.class));
        v8.executeVoidFunction("foo", null);
    }

    @Test
    public void testInvokeCallbackOnGlobalFunctionUsesGlobalScopeAsReciver2() {
        JavaCallback javaCallback = new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                return receiver.executeFunction("testGlobal", null);
            }
        };
        v8.registerJavaMethod(javaCallback, "foo");
        boolean result = ((Boolean) (v8.executeScript(("var global = this;\n" + ("var testGlobal = function() {return this === global;}; \n" + "foo();")))));
        Assert.assertTrue(result);
    }
}

