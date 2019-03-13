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
package com.eclipsesource.v8.debug;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.debug.DebugHandler.DebugEvent;
import com.eclipsesource.v8.debug.mirror.Frame;
import com.eclipsesource.v8.debug.mirror.Scope;
import com.eclipsesource.v8.debug.mirror.ValueMirror;
import org.junit.Assert;
import org.junit.Test;


public class FrameTest {
    private static String script = "    // 1  \n" + ((((("function foo(a, b, c)  { // 2  \n" + "  var x = 7;             // 3  \n") + "  var y = x + 1;         // 4  \n") + "  var z = { \'foo\' : 3 }; // 5  \n") + "}                        // 6  \n") + "foo(1,2,\'yes\');          // 7 \n");

    private Object result;

    private V8 v8;

    private DebugHandler debugHandler;

    private BreakHandler breakHandler;

    @Test
    public void testGetFunctionMirror() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                result = frame.getFunction();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertEquals("foo", getName());
        close();
    }

    @Test
    public void testGetSourceLocation() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                result = frame.getSourceLocation();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertEquals(5, getLine());
        Assert.assertEquals("script", getScriptName());
        Assert.assertEquals(0, getColumn());
    }

    @Test
    public void testGetLocalCount() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                result = frame.getLocalCount();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testGetArgumentCount() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                result = frame.getArgumentCount();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testGetScopeCount() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                result = frame.getScopeCount();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testGetScope() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope0 = frame.getScope(0);
                Scope scope1 = frame.getScope(1);
                result = (scope0 != null) && (scope1 != null);
                scope0.close();
                scope1.close();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetLocalNames() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                int argumentCount = frame.getLocalCount();
                String local1 = frame.getLocalName(0);
                String local2 = frame.getLocalName(1);
                String local3 = frame.getLocalName(2);
                result = argumentCount == 3;
                result = ((Boolean) (result)) && (local1.equals("x"));
                result = ((Boolean) (result)) && (local2.equals("y"));
                result = ((Boolean) (result)) && (local3.equals("z"));
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetArgumentNames() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                int argumentCount = frame.getArgumentCount();
                String arg1 = frame.getArgumentName(0);
                String arg2 = frame.getArgumentName(1);
                String arg3 = frame.getArgumentName(2);
                result = argumentCount == 3;
                result = ((Boolean) (result)) && (arg1.equals("a"));
                result = ((Boolean) (result)) && (arg2.equals("b"));
                result = ((Boolean) (result)) && (arg3.equals("c"));
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetArgumentValues() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                int argumentCount = frame.getArgumentCount();
                ValueMirror arg1 = frame.getArgumentValue(0);
                ValueMirror arg2 = frame.getArgumentValue(1);
                ValueMirror arg3 = frame.getArgumentValue(2);
                result = argumentCount == 3;
                result = ((Boolean) (result)) && (arg1.getValue().equals(1));
                result = ((Boolean) (result)) && (arg2.getValue().equals(2));
                result = ((Boolean) (result)) && (arg3.getValue().equals("yes"));
                arg1.close();
                arg2.close();
                arg3.close();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetLocalValues() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                int argumentCount = frame.getLocalCount();
                ValueMirror local1 = frame.getLocalValue(0);
                ValueMirror local2 = frame.getLocalValue(1);
                ValueMirror local3 = frame.getLocalValue(2);
                result = argumentCount == 3;
                result = ((Boolean) (result)) && (local1.getValue().equals(7));
                result = ((Boolean) (result)) && (local2.getValue().equals(8));
                V8Object z = ((V8Object) (local3.getValue()));
                result = ((Boolean) (result)) && ((z.getInteger("foo")) == 3);
                local1.close();
                local2.close();
                local3.close();
                z.close();
                frame.close();
            }
        });
        v8.executeScript(FrameTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }
}

