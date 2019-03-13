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


public class V8ScriptCompilationExceptionTest {
    private V8ScriptCompilationException exception;

    private V8 v8;

    String script = "x = [1,2,3];\n" + ((((("y = 0;\n" + "\n") + "//A JS Script that has a compile error, int should be var\n") + "for (int i = 0; i < x.length; i++) {\n") + "  y = y + x[i];\n") + "}");

    @Test
    public void testV8ScriptCompilationExceptionGetFileName() {
        Assert.assertEquals("filename.js", exception.getFileName());
    }

    @Test
    public void testV8ScriptCompilationExceptionGetLineNumber() {
        Assert.assertEquals(4, exception.getLineNumber());
    }

    @Test
    public void testV8ScriptCompilationExceptionGetMessage() {
        Assert.assertEquals("the message", exception.getJSMessage());
    }

    @Test
    public void testV8ScriptCompilationExceptionGetSourceLine() {
        Assert.assertEquals("line of JS", exception.getSourceLine());
    }

    @Test
    public void testV8ScriptCompilationExceptionnGetStartColumn() {
        Assert.assertEquals(4, exception.getStartColumn());
    }

    @Test
    public void testV8ScriptCompilationExceptionGetEndColumn() {
        Assert.assertEquals(6, exception.getEndColumn());
    }

    @Test
    public void testToString() {
        String result = "filename.js:4: the message\nline of JS\n    ^^\ncom.eclipsesource.v8.V8ScriptCompilationException";
        Assert.assertEquals(result, exception.toString());
    }

    @Test
    public void testToStringWithNull() {
        V8ScriptCompilationException exceptionWithNulls = new V8ScriptCompilationException(null, 4, null, null, 4, 6);
        Assert.assertNotNull(exceptionWithNulls.toString());
    }

    @Test
    public void testV8ScriptCompilationExceptionCreated() {
        try {
            v8.executeVoidScript(script, "file", 0);
        } catch (V8ScriptCompilationException e) {
            Assert.assertEquals("file", e.getFileName());
            Assert.assertEquals(5, e.getLineNumber());
            Assert.assertEquals("for (int i = 0; i < x.length; i++) {", e.getSourceLine());
            Assert.assertEquals(9, e.getStartColumn());
            Assert.assertEquals(10, e.getEndColumn());
            Assert.assertEquals("SyntaxError: Unexpected identifier", e.getJSMessage());
            return;
        }
        Assert.fail("Exception should have been thrown.");
    }

    @Test
    public void testV8ScriptCompilationExceptionCreatedUndefinedFile() {
        try {
            v8.executeVoidScript(script);
        } catch (V8ScriptCompilationException e) {
            Assert.assertEquals("undefined", e.getFileName());
            return;
        }
        Assert.fail("Exception should have been thrown.");
    }

    @Test
    public void testV8ScriptCompilationException() {
        try {
            v8.executeVoidScript("'a");
        } catch (V8ScriptCompilationException e) {
            Assert.assertEquals("SyntaxError: Invalid or unexpected token", e.getJSMessage());
            return;
        }
        Assert.fail("Exception should have been thrown.");
    }

    @Test
    public void testV8ScriptCompilationExceptionUnexpectedEnd() {
        try {
            v8.executeVoidScript("for (i");
        } catch (V8ScriptCompilationException e) {
            Assert.assertEquals("SyntaxError: Unexpected end of input", e.getJSMessage());
            return;
        }
        Assert.fail("Exception should have been thrown.");
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForVoidScript() {
        v8.executeVoidScript(script);
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForIntScript() {
        v8.executeIntegerScript(script);
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForDoubleScript() {
        v8.executeDoubleScript(script);
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForBooleanScript() {
        v8.executeBooleanScript(script);
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForStringScript() {
        v8.executeStringScript(script);
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForObjectScript() {
        v8.executeObjectScript(script);
    }

    @Test(expected = V8ScriptCompilationException.class)
    public void testV8ScriptCompilationExceptionForArrayScript() {
        v8.executeArrayScript(script);
    }
}

