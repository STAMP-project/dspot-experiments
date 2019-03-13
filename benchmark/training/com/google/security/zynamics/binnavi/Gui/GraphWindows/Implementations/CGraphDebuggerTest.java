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
package com.google.security.zynamics.binnavi.Gui.GraphWindows.Implementations;


import BreakpointStatus.BREAKPOINT_ACTIVE;
import BreakpointStatus.BREAKPOINT_DELETING;
import BreakpointStatus.BREAKPOINT_DISABLED;
import BreakpointStatus.BREAKPOINT_INACTIVE;
import BreakpointType.REGULAR;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.debug.debugger.DebugTargetSettings;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerProvider;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.CFunctionNode;
import com.google.security.zynamics.binnavi.disassembly.INaviCodeNode;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.FunctionType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CGraphDebuggerTest {
    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final CAddress m_fileBase = new CAddress(0);

    private final CAddress m_imageBase = new CAddress(4096);

    private final CModule m_module = new CModule(123, "Name", "Comment", new Date(), new Date(), CommonTestObjects.MD5, CommonTestObjects.SHA1, 55, 66, m_fileBase, m_imageBase, new DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, m_provider), null, Integer.MAX_VALUE, false, m_provider);

    private final MockDebugger m_debugger = new MockDebugger(new ModuleTargetSettings(m_module));

    @Test
    public void testGetBreakpointStatus() {
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)))));
        Assert.assertEquals(BREAKPOINT_INACTIVE, CGraphDebugger.getBreakpointStatus(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291))));
    }

    @Test
    public void testGetDebugger() {
        final MockModule module = new MockModule();
        module.getConfiguration().setDebugger(m_debugger);
        final DebugTargetSettings target = new ModuleTargetSettings(module);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        debuggerProvider.addDebugger(m_debugger);
        final CFunction function = new CFunction(module, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, m_provider);
        final CFunctionNode functionNode = new CFunctionNode(0, function, 0, 0, 0, 0, Color.RED, false, false, null, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        Assert.assertEquals(m_debugger, CGraphDebugger.getDebugger(debuggerProvider, functionNode));
    }

    @Test
    public void testGetDebugger2() {
        final MockModule module = new MockModule();
        module.getConfiguration().setDebugger(m_debugger);
        final DebugTargetSettings target = new ModuleTargetSettings(module);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        debuggerProvider.addDebugger(m_debugger);
        final CFunction function = new CFunction(module, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, m_provider);
        final ArrayList<IComment> comments = Lists.<IComment>newArrayList(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(null, CommonTestObjects.TEST_USER_1, null, "Mock Comment"));
        final INaviCodeNode codeNode = new com.google.security.zynamics.binnavi.disassembly.CCodeNode(0, 0, 0, 0, 0, Color.RED, Color.RED, false, false, comments, function, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new MockSqlProvider());
        codeNode.addInstruction(new com.google.security.zynamics.binnavi.disassembly.CInstruction(true, module, new CAddress(291), "nop", new ArrayList<com.google.security.zynamics.binnavi.disassembly.COperandTree>(), new byte[]{ ((byte) (144)) }, "x86-32", m_provider), null);
        Assert.assertEquals(m_debugger, CGraphDebugger.getDebugger(debuggerProvider, Iterables.getFirst(codeNode.getInstructions(), null)));
    }

    @Test
    public void testHasBreakpoint() {
        getBreakpointManager().addBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)))));
        Assert.assertTrue(CGraphDebugger.hasBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291))));
        Assert.assertFalse(CGraphDebugger.hasBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292))));
    }

    @Test
    public void testRemoveBreakpoint() {
        final BreakpointManager manager = new BreakpointManager();
        manager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        manager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        manager.setBreakpointStatus(REGULAR, BREAKPOINT_ACTIVE, 1);
        CGraphDebugger.removeBreakpoints(CommonTestObjects.BP_ADDRESS_123_SET, manager);
        CGraphDebugger.removeBreakpoints(CommonTestObjects.BP_ADDRESS_456_SET, manager);
        Assert.assertEquals(1, manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, manager.getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_DELETING, manager.getBreakpointStatus(REGULAR, 1));
    }

    @Test
    public void testRemoveBreakpoint3() {
        final Set<BreakpointAddress> breakpointAddresses = Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4387))), new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4388))));
        getBreakpointManager().addBreakpoints(REGULAR, breakpointAddresses);
        getBreakpointManager().setBreakpointStatus(REGULAR, BREAKPOINT_ACTIVE, 1);
        CGraphDebugger.removeBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4387)));
        CGraphDebugger.removeBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4388)));
        Assert.assertEquals(0, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
    }

    @Test
    public void testSetBreakpoint() {
        CGraphDebugger.setBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)));
        CGraphDebugger.setBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292)));
        Assert.assertEquals(2, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
        Assert.assertEquals(292, getBreakpointManager().getBreakpoint(REGULAR, 1).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 1));
    }

    @Test
    public void testToggleBreakpoint() {
        CGraphDebugger.toggleBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)));
        Assert.assertEquals(1, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
        CGraphDebugger.toggleBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)));
        Assert.assertEquals(0, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
    }

    @Test
    public void testToggleBreakpoint2() {
        final MockModule module = new MockModule();
        module.getConfiguration().setDebugger(m_debugger);
        m_debugger.setAddressTranslator(module, m_fileBase, m_imageBase);
        final DebugTargetSettings target = new ModuleTargetSettings(module);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        debuggerProvider.addDebugger(m_debugger);
        final CFunction function = new CFunction(module, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, m_provider);
        final CFunctionNode functionNode = new CFunctionNode(0, function, 0, 0, 0, 0, Color.RED, false, false, null, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        CGraphDebugger.toggleBreakpoint(debuggerProvider, functionNode);
        Assert.assertEquals(1, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
        CGraphDebugger.toggleBreakpoint(debuggerProvider, functionNode);
        Assert.assertEquals(0, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
    }

    @Test
    public void testToggleBreakpoint3() {
        final MockModule module = new MockModule();
        module.getConfiguration().setDebugger(m_debugger);
        m_debugger.setAddressTranslator(module, m_fileBase, m_imageBase);
        final DebugTargetSettings target = new ModuleTargetSettings(module);
        final DebuggerProvider debuggerProvider = new DebuggerProvider(target);
        debuggerProvider.addDebugger(m_debugger);
        final CFunction function = new CFunction(module, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, m_provider);
        final ArrayList<IComment> comments = Lists.<IComment>newArrayList(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(null, CommonTestObjects.TEST_USER_1, null, "Mock Comment"));
        final INaviCodeNode codeNode = new com.google.security.zynamics.binnavi.disassembly.CCodeNode(0, 0, 0, 0, 0, Color.RED, Color.RED, false, false, comments, function, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new MockSqlProvider());
        codeNode.addInstruction(new com.google.security.zynamics.binnavi.disassembly.CInstruction(true, module, new CAddress(291), "nop", new ArrayList<com.google.security.zynamics.binnavi.disassembly.COperandTree>(), new byte[]{ ((byte) (144)) }, "x86-32", m_provider), null);
        codeNode.addInstruction(new com.google.security.zynamics.binnavi.disassembly.CInstruction(true, module, new CAddress(292), "nop", new ArrayList<com.google.security.zynamics.binnavi.disassembly.COperandTree>(), new byte[]{ ((byte) (144)) }, "x86-32", m_provider), null);
        CGraphDebugger.toggleBreakpoint(debuggerProvider, codeNode, 2);
        Assert.assertEquals(1, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(292, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
        CGraphDebugger.toggleBreakpoint(debuggerProvider, codeNode, 2);
        Assert.assertEquals(0, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
    }

    @Test
    public void testToggleBreakpointStatus() {
        CGraphDebugger.toggleBreakpoint(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)));
        Assert.assertEquals(1, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
        CGraphDebugger.toggleBreakpointStatus(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)));
        Assert.assertEquals(1, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_DISABLED, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
        CGraphDebugger.toggleBreakpointStatus(getBreakpointManager(), m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)));
        Assert.assertEquals(1, getBreakpointManager().getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(291, getBreakpointManager().getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress().toLong());
        Assert.assertEquals(BREAKPOINT_INACTIVE, getBreakpointManager().getBreakpointStatus(REGULAR, 0));
    }
}

