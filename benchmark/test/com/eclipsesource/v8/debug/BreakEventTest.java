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
import org.junit.Assert;
import org.junit.Test;


public class BreakEventTest {
    private static String script = "// 0 \n" + (((("function foo() {     // 1 \n" + "  var x = 7;         // 2 \n") + "  var y = x + 1;     // 3 \n") + "}                    // 4 \n") + "foo();               // 5 \n");

    private V8 v8;

    private Object result = false;

    @Test
    public void testGetSourceLine() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent type, final ExecutionState state, final EventData eventData, final V8Object data) {
                result = (getSourceLine()) == 3;
            }
        });
        v8.executeScript(BreakEventTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
        handler.close();
    }

    @Test
    public void testGetSourceColumn() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent type, final ExecutionState state, final EventData eventData, final V8Object data) {
                result = (getSourceColumn()) == 12;
                result = ((Boolean) (result)) && (getSourceLineText().equals("  var y = x + 1;     // 3 "));
            }
        });
        v8.executeScript(BreakEventTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
        handler.close();
    }

    @Test
    public void testGetSourceLineText() {
        DebugHandler handler = new DebugHandler(v8);
        handler.setScriptBreakpoint("script", 3);
        handler.addBreakHandler(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent type, final ExecutionState state, final EventData eventData, final V8Object data) {
                result = getSourceLineText().equals("  var y = x + 1;     // 3 ");
            }
        });
        v8.executeScript(BreakEventTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
        handler.close();
    }
}

