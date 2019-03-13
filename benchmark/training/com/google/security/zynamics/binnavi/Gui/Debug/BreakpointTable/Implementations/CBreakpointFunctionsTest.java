/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.Gui.Debug.BreakpointTable.Implementations;


import BreakpointType.REGULAR;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Gui.Debug.BreakpointTable.CBreakpointTableModel;
import com.google.security.zynamics.binnavi.debug.debugger.DebugTargetSettings;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerProvider;
import com.google.security.zynamics.binnavi.debug.debugger.interfaces.IDebugger;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.Breakpoint;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointCondition;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.conditions.BaseNode;
import com.google.security.zynamics.binnavi.disassembly.CFunctionContainerHelper;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.MockFunction;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.general.Pair;
import com.google.security.zynamics.zylib.types.lists.IFilledList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CBreakpointFunctionsTest {
    @Test
    public void test1Simple() {
        final INaviModule mockModule = new MockModule();
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0)))));
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0))));
        @SuppressWarnings("unused")
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        @SuppressWarnings("unused")
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        final int[] rows = new int[]{ 0 };
        Assert.assertFalse(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        Assert.assertTrue(CBreakpointFunctions.allNotDisabled(debuggerProvider, rows));
    }

    @Test
    public void test2DeleteBreakpoints() {
        final INaviModule mockModule = new MockModule();
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0)))));
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0))));
        @SuppressWarnings("unused")
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        final int[] rows = new int[]{ 0 };
        Assert.assertEquals(1, tableModel.getRowCount());
        CBreakpointRemoveFunctions.deleteBreakpoints(debuggerProvider, rows);
        Assert.assertEquals(0, tableModel.getRowCount());
    }

    @Test
    public void test3disableAll() {
        final INaviModule mockModule = new MockModule();
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0)))));
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0))));
        @SuppressWarnings("unused")
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        @SuppressWarnings("unused")
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        final int[] rows = new int[]{ 0 };
        Assert.assertFalse(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        CBreakpointRemoveFunctions.disableAll(debuggerProvider);
        Assert.assertTrue(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
    }

    @Test
    public void test4DisableBreakpoints() {
        final INaviModule mockModule = new MockModule();
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0)))));
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0))));
        @SuppressWarnings("unused")
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        @SuppressWarnings("unused")
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        final int[] rows = new int[]{ 0 };
        final int[] rows2 = new int[]{ 1 };
        Assert.assertFalse(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        try {
            CBreakpointRemoveFunctions.disableBreakpoints(debuggerProvider, rows2);
        } catch (final IllegalArgumentException e) {
        }
        CBreakpointRemoveFunctions.disableBreakpoints(debuggerProvider, rows);
        Assert.assertTrue(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
    }

    @Test
    public void test5enableFunctions() {
        final INaviModule mockModule = new MockModule();
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0)))));
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0))));
        @SuppressWarnings("unused")
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        @SuppressWarnings("unused")
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        final int[] rows = new int[]{ 0 };
        Assert.assertFalse(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        CBreakpointRemoveFunctions.disableAll(debuggerProvider);
        Assert.assertTrue(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        CBreakpointSetFunctions.enableAll(debuggerProvider);
        Assert.assertFalse(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        CBreakpointRemoveFunctions.disableAll(debuggerProvider);
        Assert.assertTrue(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
        CBreakpointSetFunctions.enableBreakpoints(debuggerProvider, rows);
        Assert.assertFalse(CBreakpointFunctions.allDisabled(debuggerProvider, rows));
    }

    @Test
    public void test6removeFunctions() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final MockFunction mockFunction = new MockFunction();
        final INaviModule mockModule = mockFunction.getModule();
        CFunctionContainerHelper.addFunction(mockModule.getContent().getFunctionContainer(), mockFunction);
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660)))));
        @SuppressWarnings("unused")
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660))));
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        Assert.assertEquals(1, tableModel.getRowCount());
        final IFilledList<Pair<IDebugger, INaviFunction>> targets = new com.google.security.zynamics.zylib.types.lists.FilledList<Pair<IDebugger, INaviFunction>>();
        final Pair<IDebugger, INaviFunction> targetPair = new Pair<IDebugger, INaviFunction>(debugger, mockFunction);
        targets.add(targetPair);
        Assert.assertEquals(1, targets.size());
        CBreakpointRemoveFunctions.removeBreakpoints(targets);
        @SuppressWarnings("unused")
        final BreakpointManager manager = debugger.getBreakpointManager();
        Assert.assertEquals(0, tableModel.getRowCount());
    }

    @Test
    public void test7setBreakpoints() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final MockFunction mockFunction = new MockFunction();
        final INaviModule mockModule = mockFunction.getModule();
        CFunctionContainerHelper.addFunction(mockModule.getContent().getFunctionContainer(), mockFunction);
        final DebugTargetSettings target = new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings(mockModule));
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660)))));
        @SuppressWarnings("unused")
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660))));
        // CBreakpointAddress address = new CBreakpointAddress(mockModule, new CUnrelocatedAddress(new
        // CAddress(0x2c9)));
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        Assert.assertEquals(1, tableModel.getRowCount());
        final IFilledList<Pair<IDebugger, INaviFunction>> targets = new com.google.security.zynamics.zylib.types.lists.FilledList<Pair<IDebugger, INaviFunction>>();
        final Pair<IDebugger, INaviFunction> targetPair = new Pair<IDebugger, INaviFunction>(debugger, mockFunction);
        targets.add(targetPair);
        CBreakpointRemoveFunctions.removeBreakpoints(targets);
        Assert.assertEquals(0, tableModel.getRowCount());
        CBreakpointSetFunctions.setBreakpoints(targets);
        Assert.assertEquals(1, tableModel.getRowCount());
    }
}

