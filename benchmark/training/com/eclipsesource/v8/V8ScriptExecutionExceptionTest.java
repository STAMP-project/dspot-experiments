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


import org.junit.Assert;
import org.junit.Test;


public class V8ScriptExecutionExceptionTest {
    private V8ScriptExecutionException exception;

    private String errorFunction = "function myFunction() {\n" + ("undefined.toString();\n" + "}\n");

    private String undefinedAccessScript = "x=undefined;\n" + (((("function called( y ) {\n" + " y.toString()\n") + "}\n") + "\n") + "called( x );\n");

    private V8 v8;

    @Test
    public void testV8ScriptExecutionExceptionGetFileName() {
        Assert.assertEquals("filename.js", exception.getFileName());
    }

    @Test
    public void testV8ScriptExecutionExceptionGetLineNumber() {
        Assert.assertEquals(4, exception.getLineNumber());
    }

    @Test
    public void testV8ScriptExecutionExceptionGetMessage() {
        Assert.assertEquals("the message", exception.getJSMessage());
    }

    @Test
    public void testV8ScriptExecutionExceptionGetSourceLine() {
        Assert.assertEquals("line of JS", exception.getSourceLine());
    }

    @Test
    public void testV8ScriptExecutionExceptionnGetStartColumn() {
        Assert.assertEquals(4, exception.getStartColumn());
    }

    @Test
    public void testV8ScriptExecutionExceptionGetEndColumn() {
        Assert.assertEquals(6, exception.getEndColumn());
    }

    @Test
    public void testV8ScriptExecutionExceptionGetStacktrace() {
        Assert.assertEquals("stack", exception.getJSStackTrace());
    }

    @Test
    public void testV8ScriptExecutionExceptionGetCause() {
        Assert.assertNotNull(exception.getCause());
        Assert.assertEquals("cause", exception.getCause().getMessage());
    }

    @Test
    public void testToString() {
        String result = "filename.js:4: the message\nline of JS\n    ^^\nstack\ncom.eclipsesource.v8.V8ScriptExecutionException";
        Assert.assertEquals(result, exception.toString());
    }

    @Test
    public void testToStringWithNull() {
        V8ScriptExecutionException exceptionWithNulls = new V8ScriptExecutionException(null, 4, null, null, 4, 6, null, null);
        Assert.assertNotNull(exceptionWithNulls.toString());
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testExceptionInVoidJavaCall() {
        try {
            v8.registerJavaMethod(this, "voidCallbackWithException", "voidCallback", new Class<?>[]{  });
            v8.executeVoidScript("voidCallback()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("Test Exception", e.getJSMessage());
            Assert.assertTrue(((e.getCause()) instanceof RuntimeException));
            throw e;
        }
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testExceptionInJavaCall() {
        try {
            v8.registerJavaMethod(this, "callbackWithException", "callback", new Class<?>[]{  });
            v8.executeVoidScript("callback()");
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("Test Exception", e.getJSMessage());
            Assert.assertTrue(((e.getCause()) instanceof RuntimeException));
            throw e;
        }
    }

    @Test
    public void testV8ScriptExecutionExceptionCreated() {
        try {
            v8.executeVoidScript(undefinedAccessScript, "file", 0);
        } catch (V8ScriptExecutionException e) {
            Assert.assertEquals("file", e.getFileName());
            Assert.assertEquals(3, e.getLineNumber());
            Assert.assertEquals(" y.toString()", e.getSourceLine());
            Assert.assertEquals(2, e.getStartColumn());
            Assert.assertEquals(3, e.getEndColumn());
            Assert.assertEquals("TypeError: Cannot read property 'toString' of undefined", e.getJSMessage());
            return;
        }
        Assert.fail("Exception should have been thrown.");
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExecutionExceptionIntScript() {
        v8.executeIntegerScript(((undefinedAccessScript) + "1;"), "file", 0);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExecutionExceptionBooleanScript() {
        v8.executeBooleanScript(((undefinedAccessScript) + "true;"), "file", 0);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExecutionExceptionStringScript() {
        v8.executeStringScript(((undefinedAccessScript) + "'string';"), "file", 0);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExecutionExceptionDoubleScript() {
        v8.executeDoubleScript(((undefinedAccessScript) + "1.1;"), "file", 0);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExecutionExceptionArrayScript() {
        v8.executeArrayScript(((undefinedAccessScript) + "[];"), "file", 0);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExecutionExceptionObjectScript() {
        v8.executeObjectScript(((undefinedAccessScript) + "{};"), "file", 0);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionVoidCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeVoidFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionIntCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeIntegerFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionBooleanCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeBooleanFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionDoubleCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeDoubleFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionStringCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeStringFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionObjectCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeObjectFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ScriptExceptionArrayCall() {
        v8.executeVoidScript(errorFunction);
        v8.executeArrayFunction("myFunction", null);
    }

    @Test(expected = V8ScriptExecutionException.class)
    public void testV8ThrowsException() {
        v8.executeVoidScript("throw 'problem';");
    }
}

