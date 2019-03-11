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


import DebugEvent.AfterCompile;
import DebugEvent.BeforeCompile;
import DebugEvent.Break;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.debug.DebugHandler.DebugEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DebugHandlerTest {
    private static String script = "// 1 \n" + (((("function foo() {     // 2 \n" + "  var x = 7;         // 3 \n") + "  var y = x + 1;     // 4 \n") + "}                    // 5 \n") + "foo();               // 6 \n");

    private V8 v8;

    private Object result = false;

    @Test
    public void testCreateDebugHandler() {
        DebugHandler handler = new DebugHandler(v8);
        Assert.assertNotNull(handler);
        handler.close();
    }

    @Test
    public void testDebugEvents() {
        DebugHandler handler = new DebugHandler(v8);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(breakHandler);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Mockito.verify(breakHandler).onBreak(ArgumentMatchers.eq(BeforeCompile), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(CompileEvent.class), ArgumentMatchers.any(V8Object.class));
        Mockito.verify(breakHandler).onBreak(ArgumentMatchers.eq(AfterCompile), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(CompileEvent.class), ArgumentMatchers.any(V8Object.class));
        Mockito.verify(breakHandler).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(BreakEvent.class), ArgumentMatchers.any(V8Object.class));
        handler.close();
    }

    @Test
    public void testBeforeCompileEvent() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent type, final ExecutionState state, final EventData eventData, final V8Object data) {
                if (type == (DebugEvent.BeforeCompile)) {
                    result = eventData instanceof CompileEvent;
                }
            }
        });
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
        handler.close();
    }

    @Test
    public void testAfterCompileEvent() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent type, final ExecutionState state, final EventData eventData, final V8Object data) {
                if (type == (DebugEvent.AfterCompile)) {
                    result = eventData instanceof CompileEvent;
                }
            }
        });
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
        handler.close();
    }

    @Test
    public void testBreakEvent() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent type, final ExecutionState state, final EventData eventData, final V8Object data) {
                if (type == (DebugEvent.Break)) {
                    result = eventData instanceof BreakEvent;
                }
            }
        });
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
        handler.close();
    }

    @Test
    public void testSetBreakpoint() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(1)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        handler.close();
    }

    @Test
    public void testClearBreakpoint() {
        DebugHandler handler = new DebugHandler(v8);
        int breakpointID = handler.setScriptBreakpoint("script", 3);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        handler.clearBreakPoint(breakpointID);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        int breakpointCount = handler.getScriptBreakPointCount();
        Mockito.verify(breakHandler, Mockito.times(0)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        Assert.assertEquals(0, breakpointCount);
        handler.close();
    }

    @Test
    public void testGetBreakpoints() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        int[] ids = handler.getScriptBreakPointIDs();
        Assert.assertEquals(1, ids.length);
        Assert.assertEquals(1, ids[0]);
        handler.close();
    }

    @Test
    public void testGetBreakpoint() {
        DebugHandler handler = new DebugHandler(v8);
        int breakpoint_0 = handler.setScriptBreakpoint("script", 3);
        int breakpoint_1 = handler.setScriptBreakpoint("script", 4);
        handler.clearBreakPoint(breakpoint_0);
        ScriptBreakPoint breakpoint = handler.getScriptBreakPoint(breakpoint_1);
        Assert.assertEquals(breakpoint_1, breakpoint.getBreakPointNumber());
        breakpoint.release();
        handler.close();
    }

    @Test
    public void testChangeBreakPointCondition() {
        DebugHandler handler = new DebugHandler(v8);
        int breakpointID = handler.setScriptBreakpoint("script", 3);
        handler.changeBreakPointCondition(breakpointID, "x=8;");
        ScriptBreakPoint breakPoint = handler.getScriptBreakPoint(breakpointID);
        Assert.assertEquals("x=8;", breakPoint.getCondition());
        breakPoint.release();
        handler.close();
    }

    @Test
    public void testDisableBreakpoint() {
        DebugHandler handler = new DebugHandler(v8);
        int breakpointID = handler.setScriptBreakpoint("script", 3);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        handler.disableScriptBreakPoint(breakpointID);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(0)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        handler.close();
    }

    @Test
    public void testDisableAllBreakpoints() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        handler.disableAllBreakPoints();
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(0)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        handler.close();
    }

    @Test
    public void testEnableBreakpoint() {
        DebugHandler handler = new DebugHandler(v8);
        int breakpointID = handler.setScriptBreakpoint("script", 3);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        handler.disableScriptBreakPoint(breakpointID);
        handler.enableScriptBreakPoint(breakpointID);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(1)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        handler.close();
    }

    @Test
    public void testSetBreakpointReturnsID() {
        DebugHandler handler = new DebugHandler(v8);
        int breakpointID = handler.setScriptBreakpoint("script", 3);
        Assert.assertEquals(1, breakpointID);
        handler.close();
    }

    @Test
    public void testSetBreakpointByFunction() {
        DebugHandler handler = new DebugHandler(v8);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        V8Function function = ((V8Function) (v8.get("foo")));
        handler.setBreakpoint(function);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        function.call(null, null);
        Mockito.verify(breakHandler, Mockito.times(1)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        handler.close();
        function.close();
    }

    @Test
    public void testSetBreakpointByFunctionReturnsID() {
        DebugHandler handler = new DebugHandler(v8);
        v8.executeScript(DebugHandlerTest.script, "script", 0);
        V8Function function = ((V8Function) (v8.get("foo")));
        int breakpointID = handler.setBreakpoint(function);
        Assert.assertEquals(1, breakpointID);
        handler.close();
        function.close();
    }

    @Test
    public void testRemoveBreakHandlerBeforeSet() {
        DebugHandler handler = new DebugHandler(v8);
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.removeBreakHandler(breakHandler);// Test should not throw NPE

        handler.close();
    }
}

