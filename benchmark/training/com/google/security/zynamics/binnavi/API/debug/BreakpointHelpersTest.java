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
package com.google.security.zynamics.binnavi.API.debug;


import BreakpointType.ECHO;
import BreakpointType.REGULAR;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.API.disassembly.Address;
import com.google.security.zynamics.binnavi.API.disassembly.CodeNode;
import com.google.security.zynamics.binnavi.API.disassembly.View;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.debug.debugger.DebugTargetSettings;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class BreakpointHelpersTest {
    private Debugger m_debugger;

    private CodeNode m_node;

    private View m_view;

    private Object m_functionNode;

    private CModule m_module;

    private DebugTargetSettings m_moduleDebugSettings;

    private MockDebugger m_mockDebugger;

    @Test
    public void testGetBreakpointsNode() {
        Assert.assertTrue(BreakpointHelpers.getBreakpoints(m_debugger, m_node).isEmpty());
        m_debugger.getBreakpointManager().getNative().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660)))));
        final List<Address> breakpoints = BreakpointHelpers.getBreakpoints(m_debugger, m_node);
        Assert.assertEquals(1, breakpoints.size());
        Assert.assertEquals(4660, breakpoints.get(0).toLong());
        try {
            BreakpointHelpers.getBreakpoints(null, m_node);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            BreakpointHelpers.getBreakpoints(m_debugger, ((CodeNode) (null)));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
    }

    @Test
    public void testGetBreakpointsView() {
        Assert.assertTrue(BreakpointHelpers.getBreakpoints(m_debugger, m_view).isEmpty());
        m_debugger.getBreakpointManager().getNative().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660)))));
        m_debugger.getBreakpointManager().getNative().addBreakpoints(REGULAR, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)))));
        final List<Address> breakpoints = BreakpointHelpers.getBreakpoints(m_debugger, m_view);
        Assert.assertEquals(2, breakpoints.size());
        Assert.assertEquals(4660, breakpoints.get(0).toLong());
        Assert.assertEquals(291, breakpoints.get(1).toLong());
        try {
            BreakpointHelpers.getBreakpoints(null, m_view);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            BreakpointHelpers.getBreakpoints(m_debugger, ((View) (null)));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
    }

    @Test
    public void testGetEchoBreakpointsNode() {
        Assert.assertTrue(BreakpointHelpers.getEchoBreakpoints(m_debugger, m_node).isEmpty());
        m_debugger.getBreakpointManager().getNative().addBreakpoints(ECHO, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660)))));
        final List<Address> breakpoints = BreakpointHelpers.getEchoBreakpoints(m_debugger, m_node);
        Assert.assertEquals(1, breakpoints.size());
        Assert.assertEquals(4660, breakpoints.get(0).toLong());
        try {
            BreakpointHelpers.getEchoBreakpoints(null, m_node);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            BreakpointHelpers.getEchoBreakpoints(m_debugger, ((CodeNode) (null)));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
    }

    @Test
    public void testGetEchoBreakpointsView() {
        Assert.assertTrue(BreakpointHelpers.getEchoBreakpoints(m_debugger, m_view).isEmpty());
        m_debugger.getBreakpointManager().getNative().addBreakpoints(ECHO, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4660)))));
        m_debugger.getBreakpointManager().getNative().addBreakpoints(ECHO, Sets.newHashSet(new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)))));
        final List<Address> breakpoints = BreakpointHelpers.getEchoBreakpoints(m_debugger, m_view);
        Assert.assertEquals(2, breakpoints.size());
        Assert.assertEquals(4660, breakpoints.get(0).toLong());
        Assert.assertEquals(291, breakpoints.get(1).toLong());
        try {
            BreakpointHelpers.getEchoBreakpoints(null, m_view);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            BreakpointHelpers.getEchoBreakpoints(m_debugger, ((View) (null)));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
    }
}

