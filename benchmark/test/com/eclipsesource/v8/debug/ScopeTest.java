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


import PropertyKind.Named;
import ScopeType.Closure;
import ScopeType.Global;
import ScopeType.Local;
import ScopeType.Script;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.debug.DebugHandler.DebugEvent;
import com.eclipsesource.v8.debug.mirror.Frame;
import com.eclipsesource.v8.debug.mirror.ObjectMirror;
import com.eclipsesource.v8.debug.mirror.Scope;
import org.junit.Assert;
import org.junit.Test;


public class ScopeTest {
    private static String script = "    // 1  \n" + ((((((((("function foo(a, b, c)  { // 2  \n" + "  var x = 7;             // 3  \n") + "  var y = x + 1;         // 4  \n") + "  return function() {    // 5  \n") + "    var z = x;           // 6  \n") + "    var k = 8;           // 7  \n") + "    return z;            // 8  \n") + "  }                      // 9  \n") + "}                        // 10 \n") + "foo(1,2,3)();            // 11 \n");

    private Object result = false;

    private V8 v8;

    private DebugHandler debugHandler;

    private BreakHandler breakHandler;

    @Test
    public void testGetLocalScopeType() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                result = scope.getType();
                scope.close();
                frame.close();
            }
        });
        v8.executeScript(ScopeTest.script, "script", 0);
        Assert.assertEquals(Local, result);
    }

    @Test
    public void testGetScriptScopeType() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(2);
                result = scope.getType();
                scope.close();
                frame.close();
            }
        });
        v8.executeScript(ScopeTest.script, "script", 0);
        Assert.assertEquals(Script, result);
    }

    @Test
    public void testGetGlobalScopeType() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(3);
                result = scope.getType();
                scope.close();
                frame.close();
            }
        });
        v8.executeScript(ScopeTest.script, "script", 0);
        Assert.assertEquals(Global, result);
    }

    @Test
    public void testGetClosureScope() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(1);
                result = scope.getType();
                scope.close();
                frame.close();
            }
        });
        v8.executeScript(ScopeTest.script, "script", 0);
        Assert.assertEquals(Closure, result);
    }

    @Test
    public void testSetVariableValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                scope.setVariableValue("z", 0);
                scope.close();
                frame.close();
            }
        });
        int result = v8.executeIntegerScript(ScopeTest.script, "script", 0);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testChangeVariableTypeV8Object() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                V8Object newValue = new V8Object(v8);
                newValue.add("foo", "bar");
                scope.setVariableValue("z", newValue);
                newValue.close();
                scope.close();
                frame.close();
            }
        });
        V8Object result = v8.executeObjectScript(ScopeTest.script, "script", 0);
        Assert.assertEquals("bar", result.getString("foo"));
        result.close();
    }

    @Test
    public void testChangeVariableTypeBoolean() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                scope.setVariableValue("z", false);
                scope.close();
                frame.close();
            }
        });
        boolean result = ((Boolean) (v8.executeScript(ScopeTest.script, "script", 0)));
        Assert.assertFalse(result);
    }

    @Test
    public void testChangeVariableTypeDouble() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                scope.setVariableValue("z", 3.14);
                scope.close();
                frame.close();
            }
        });
        double result = ((Double) (v8.executeScript(ScopeTest.script, "script", 0)));
        Assert.assertEquals(3.14, result, 1.0E-4);
    }

    @Test
    public void testChangeVariableTypeString() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                scope.setVariableValue("z", "someString");
                scope.close();
                frame.close();
            }
        });
        String result = ((String) (v8.executeScript(ScopeTest.script, "script", 0)));
        Assert.assertEquals("someString", result);
    }

    @Test
    public void testGetScopeObject() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                Scope scope = frame.getScope(0);
                ObjectMirror scopeObject = scope.getScopeObject();
                String[] propertyNames = scopeObject.getPropertyNames(Named, 0);
                result = (propertyNames.length) == 2;
                result = ((Boolean) (result)) && (propertyNames[0].equals("z"));
                result = ((Boolean) (result)) && (propertyNames[1].equals("k"));
                scopeObject.close();
                scope.close();
                frame.close();
            }
        });
        v8.executeScript(ScopeTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }
}

