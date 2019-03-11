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


import DebugEvent.Break;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ScriptBreakPointTest {
    private static String script = "// 1 \n" + (((("function foo() {     // 2 \n" + "  var x = 7;         // 3 \n") + "  var y = x + 1;     // 4 \n") + "}                    // 5 \n") + "foo();               // 6 \n");

    private V8 v8;

    private DebugHandler handler;

    @Test
    public void testGetBreakpointNumber() {
        int breakpointID_0 = handler.setScriptBreakpoint("script", 3);
        int breakpointID_1 = handler.setScriptBreakpoint("script", 4);
        ScriptBreakPoint breakpoint_0 = handler.getScriptBreakPoint(breakpointID_0);
        ScriptBreakPoint breakpoint_1 = handler.getScriptBreakPoint(breakpointID_1);
        Assert.assertEquals(breakpointID_0, breakpoint_0.getBreakPointNumber());
        Assert.assertEquals(breakpointID_1, breakpoint_1.getBreakPointNumber());
        breakpoint_0.release();
        breakpoint_1.release();
    }

    @Test
    public void testGetLineNumber() {
        int breakpointID = handler.setScriptBreakpoint("script", 3);
        ScriptBreakPoint breakpoint = handler.getScriptBreakPoint(breakpointID);
        Assert.assertEquals(3, breakpoint.getLine());
        breakpoint.release();
    }

    @Test
    public void testFalseConditionDoesntTriggerBreak() {
        DebugHandler handler = new DebugHandler(v8);
        int breakPointID = handler.setScriptBreakpoint("script", 3);
        ScriptBreakPoint breakPoint = handler.getScriptBreakPoint(breakPointID);
        breakPoint.setCondition("false");
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        v8.executeScript(ScriptBreakPointTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(0)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        breakPoint.release();
        handler.close();
    }

    @Test
    public void testTrueConditionTriggersBreak() {
        DebugHandler handler = new DebugHandler(v8);
        int breakPointID = handler.setScriptBreakpoint("script", 3);
        ScriptBreakPoint breakPoint = handler.getScriptBreakPoint(breakPointID);
        breakPoint.setCondition("true;");
        BreakHandler breakHandler = Mockito.mock(BreakHandler.class);
        handler.addBreakHandler(breakHandler);
        v8.executeScript(ScriptBreakPointTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(1)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        breakPoint.release();
        handler.close();
    }

    @Test
    public void testGetCondition() {
        DebugHandler handler = new DebugHandler(v8);
        int breakPointID = handler.setScriptBreakpoint("script", 3);
        ScriptBreakPoint breakPoint = handler.getScriptBreakPoint(breakPointID);
        breakPoint.setCondition("x=7;");
        String result = breakPoint.getCondition();
        Assert.assertEquals("x=7;", result);
        breakPoint.release();
        handler.close();
    }

    @Test
    public void testGetNoConditionReturnsUndefined() {
        DebugHandler handler = new DebugHandler(v8);
        int breakPointID = handler.setScriptBreakpoint("script", 3);
        ScriptBreakPoint breakPoint = handler.getScriptBreakPoint(breakPointID);
        String result = breakPoint.getCondition();
        Assert.assertEquals("undefined", result);
        breakPoint.release();
        handler.close();
    }
}

