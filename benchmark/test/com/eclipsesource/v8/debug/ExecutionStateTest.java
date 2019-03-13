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
import StepAction.STEP_IN;
import StepAction.STEP_NEXT;
import StepAction.STEP_OUT;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.debug.mirror.Frame;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ExecutionStateTest {
    private static String script = "// 1  \n" + (((((((("function foo() {     // 2  \n" + "  bar();             // 3  \n") + "  var y = 2 + 1;     // 4  \n") + "}                    // 5  \n") + "function bar() {     // 6  \n") + "  var x = 8;         // 7  \n") + "  return x+1;        // 8  \n") + "}                    // 9  \n") + "foo();               // 10 \n");

    private Object result;

    private V8 v8;

    private DebugHandler debugHandler;

    private BreakHandler breakHandler;

    @Test
    public void testStepNext() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                ExecutionState state = ((ExecutionState) (invocation.getArguments()[1]));
                state.prepareStep(STEP_NEXT);
                return null;
            }
        }).when(breakHandler).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        v8.executeScript(ExecutionStateTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(4)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
    }

    @Test
    public void testStepOut() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                ExecutionState state = ((ExecutionState) (invocation.getArguments()[1]));
                state.prepareStep(STEP_OUT);
                return null;
            }
        }).when(breakHandler).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        v8.executeScript(ExecutionStateTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(2)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
    }

    @Test
    public void testStepIn() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                ExecutionState state = ((ExecutionState) (invocation.getArguments()[1]));
                state.prepareStep(STEP_IN);
                return null;
            }
        }).when(breakHandler).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        v8.executeScript(ExecutionStateTest.script, "script", 0);
        Mockito.verify(breakHandler, Mockito.times(7)).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
    }

    @Test
    public void testGetFrameCount() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                ExecutionState state = ((ExecutionState) (invocation.getArguments()[1]));
                result = state.getFrameCount();
                return null;
            }
        }).when(breakHandler).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        v8.executeScript(ExecutionStateTest.script, "script", 0);
        Assert.assertEquals(2, result);
    }

    @Test
    public void testGetFrame() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                ExecutionState state = ((ExecutionState) (invocation.getArguments()[1]));
                Frame frame0 = state.getFrame(0);
                Frame frame1 = state.getFrame(1);
                result = (frame0 != null) && (frame1 != null);
                frame0.close();
                frame1.close();
                return null;
            }
        }).when(breakHandler).onBreak(ArgumentMatchers.eq(Break), ArgumentMatchers.any(ExecutionState.class), ArgumentMatchers.any(EventData.class), ArgumentMatchers.any(V8Object.class));
        v8.executeScript(ExecutionStateTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }
}

