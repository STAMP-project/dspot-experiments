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
package com.google.security.zynamics.binnavi.Gui.Debug.BreakpointTable;


import BreakpointType.REGULAR;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.debug.debugger.DebugTargetSettings;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerProvider;
import com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.Breakpoint;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointCondition;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.conditions.BaseNode;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.enums.BreakpointStatus.BREAKPOINT_INACTIVE;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CBreakpointTableModelTest {
    private final BreakpointAddress BREAKPOINT_ADDRESS = new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0)));

    private final Set<BreakpointAddress> BREAKPOINT_ADDRESS_SET = Sets.newHashSet(BREAKPOINT_ADDRESS);

    @Test
    public void test1Simple() {
        final DebugTargetSettings target = new ModuleTargetSettings(CommonTestObjects.MODULE);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        new MockModule();
        getBreakpointManager().addBreakpoints(REGULAR, BREAKPOINT_ADDRESS_SET);
        debuggerProvider.addDebugger(debugger);
        @SuppressWarnings("unused")
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
    }

    @Test
    public void test2SimpleFail() {
        try {
            @SuppressWarnings("unused")
            final CBreakpointTableModel tableModel = new CBreakpointTableModel(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void test3IsCellEditable() {
        final DebugTargetSettings target = new ModuleTargetSettings(CommonTestObjects.MODULE);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        getBreakpointManager().addBreakpoints(REGULAR, BREAKPOINT_ADDRESS_SET);
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        Assert.assertFalse(tableModel.isCellEditable(0, 0));
        Assert.assertFalse(tableModel.isCellEditable(0, 1));
        Assert.assertFalse(tableModel.isCellEditable(0, 2));
        Assert.assertTrue(tableModel.isCellEditable(0, 5));
        Assert.assertTrue(tableModel.isCellEditable(0, 6));
    }

    @Test
    public void test4getValueAt() {
        final DebugTargetSettings target = new ModuleTargetSettings(CommonTestObjects.MODULE);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        getBreakpointManager().addBreakpoints(REGULAR, BREAKPOINT_ADDRESS_SET);
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, BREAKPOINT_ADDRESS);
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        Assert.assertEquals(BREAKPOINT_INACTIVE, tableModel.getValueAt(0, 0));
        Assert.assertEquals(debugger.getPrintableString(), tableModel.getValueAt(0, 1));
        Assert.assertEquals(address, tableModel.getValueAt(0, 2));
        Assert.assertEquals("foo", tableModel.getValueAt(0, 5));
        Assert.assertEquals("purzel", tableModel.getValueAt(0, 6));
        try {
            tableModel.getValueAt(0, 7);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void test5setValueAt() {
        final DebugTargetSettings target = new ModuleTargetSettings(CommonTestObjects.MODULE);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        getBreakpointManager().addBreakpoints(REGULAR, BREAKPOINT_ADDRESS_SET);
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, BREAKPOINT_ADDRESS);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        tableModel.setValueAt("piff==0", 0, 5);
        tableModel.setValueAt("puff", 0, 6);
        Assert.assertEquals("piff==0", tableModel.getValueAt(0, 5));
        Assert.assertEquals("puff", tableModel.getValueAt(0, 6));
    }

    @Test
    public void test6Utility() {
        final DebugTargetSettings target = new ModuleTargetSettings(CommonTestObjects.MODULE);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        getBreakpointManager().addBreakpoints(REGULAR, BREAKPOINT_ADDRESS_SET);
        final Breakpoint breakPoint = getBreakpointManager().getBreakpoint(REGULAR, BREAKPOINT_ADDRESS);
        @SuppressWarnings("unused")
        final CAddress address = new CAddress(0);
        final BaseNode root = new BaseNode();
        final BreakpointCondition bpCondition = new BreakpointCondition("foo", root);
        breakPoint.setCondition(bpCondition);
        breakPoint.setDescription("purzel");
        debuggerProvider.addDebugger(debugger);
        final CBreakpointTableModel tableModel = new CBreakpointTableModel(debuggerProvider);
        Assert.assertEquals(7, tableModel.getColumnCount());
        Assert.assertEquals("Status", tableModel.getColumnName(0));
        Assert.assertEquals("Debugger", tableModel.getColumnName(1));
        Assert.assertEquals("Unrelocated Address", tableModel.getColumnName(2));
        Assert.assertEquals("Relocated Address", tableModel.getColumnName(3));
        Assert.assertEquals("Module", tableModel.getColumnName(4));
        Assert.assertEquals("Condition", tableModel.getColumnName(5));
        Assert.assertEquals("Description", tableModel.getColumnName(6));
        Assert.assertEquals(1, tableModel.getRowCount());
    }
}

