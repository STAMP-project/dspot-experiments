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
package com.google.security.zynamics.binnavi.Debug.Models.Breakpoints;


import BreakpointStatus.BREAKPOINT_INACTIVE;
import BreakpointType.ECHO;
import BreakpointType.REGULAR;
import BreakpointType.STEP;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test class for the class CBreakpointManager
 */
@RunWith(JUnit4.class)
public final class CBreakpointManagerTest {
    private BreakpointManager m_manager;

    private final MockModule m_module = CommonTestObjects.MODULE;

    @Test
    public void testBreakpointOverwriting() {
        final MockBreakpointManagerListener listener = new MockBreakpointManagerListener();
        m_manager.addListener(listener);
        // Echo breakpoints can not overwrite regular breakpoints.
        m_manager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_123_SET);
        Assert.assertEquals(0, listener.size());
        Assert.assertEquals(3, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
        // Echo breakpoints can not overwrite stepping breakpoints
        m_manager.addBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4096)))));
        Assert.assertEquals(0, listener.size());
        Assert.assertEquals(3, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
        // Stepping breakpoints can not overwrite regular breakpoints
        m_manager.addBreakpoints(STEP, CommonTestObjects.BP_ADDRESS_123_SET);
        Assert.assertEquals(0, listener.size());
        Assert.assertEquals(3, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
        // Stepping breakpoints can overwrite echo breakpoints
        m_manager.addBreakpoints(STEP, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(273)))));
        Assert.assertEquals(2, listener.size());
        Assert.assertEquals("Remove: 00000111", listener.getEvent(0));
        Assert.assertEquals("Add: 00000111", listener.getEvent(1));
        Assert.assertEquals(3, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(STEP));
        // Regular breakpoints can overwrite stepping breakpoints
        m_manager.addBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4096)))));
        Assert.assertEquals(4, listener.size());
        Assert.assertEquals("Remove: 00001000", listener.getEvent(2));
        Assert.assertEquals("Add: 00001000", listener.getEvent(3));
        Assert.assertEquals(4, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
        // Regular breakpoints can overwrite echo breakpoints
        m_manager.addBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(546)))));
        Assert.assertEquals(6, listener.size());
        Assert.assertEquals("Remove: 00000222", listener.getEvent(4));
        Assert.assertEquals("Add: 00000222", listener.getEvent(5));
        Assert.assertEquals(5, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(0, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
    }

    @Test
    public void testClearEchoBreakpointsPassive() {
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(ECHO));
        final MockBreakpointManagerListener listener = new MockBreakpointManagerListener();
        m_manager.addListener(listener);
        m_manager.clearBreakpointsPassive(ECHO);
        Assert.assertEquals(0, m_manager.getNumberOfBreakpoints(ECHO));
        // Make sure that no message was sent
        Assert.assertEquals(0, listener.size());
    }

    @Test
    public void testGetBreakpoint() {
        Assert.assertEquals(new CAddress(new CAddress(291)), m_manager.getBreakpoint(REGULAR, 0).getAddress().getAddress().getAddress());
        Assert.assertEquals(new CAddress(new CAddress(1110)), m_manager.getBreakpoint(REGULAR, 1).getAddress().getAddress().getAddress());
        // Error condition: Invalid index
        try {
            m_manager.getBreakpoint(REGULAR, (-1)).getAddress();
            Assert.fail("Exception not raised");
        } catch (final IllegalArgumentException ex) {
        }
    }

    @Test
    public void testGetBreakpoint2() {
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_manager.getBreakpointStatus(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291))), REGULAR));
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_manager.getBreakpointStatus(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(1110))), REGULAR));
        Assert.assertEquals(null, m_manager.getBreakpointStatus(null, REGULAR));
        Assert.assertEquals(null, m_manager.getBreakpointStatus(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(0))), REGULAR));
    }

    @Test
    public void testGetBreakpointDescription() {
        try {
            m_manager.getBreakpoint(REGULAR, (-1)).setDescription("Argl");
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        m_manager.getBreakpoint(REGULAR, 0).setDescription(null);
        m_manager.getBreakpoint(REGULAR, 0).setDescription("Hannes");
        Assert.assertEquals("Hannes", m_manager.getBreakpoint(REGULAR, 0).getDescription());
    }

    @Test
    public void testGetBreakpointStatus() {
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_manager.getBreakpointStatus(REGULAR, 0));
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_manager.getBreakpointStatus(REGULAR, 1));
        // Error condition: Invalid index
        try {
            m_manager.getBreakpointStatus(REGULAR, (-1));
            Assert.fail("Exception not raised");
        } catch (final IllegalArgumentException ex) {
        }
    }

    @Test
    public void testRemoveEchoBreakpoint() {
        // Error condition: Null argument
        try {
            m_manager.removeBreakpoints(ECHO, null);
            Assert.fail("Exception not raised");
        } catch (final NullPointerException ex) {
        }
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(ECHO));
        m_manager.removeBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(273)))));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(ECHO));
        m_manager.removeBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(546)))));
        Assert.assertEquals(0, m_manager.getNumberOfBreakpoints(ECHO));
    }

    @Test
    public void testRemoveRegularBreakpoint() {
        // Error condition: Null argument
        try {
            m_manager.removeBreakpoints(REGULAR, null);
            Assert.fail("Exception not raised");
        } catch (final NullPointerException ex) {
        }
        Assert.assertEquals(3, m_manager.getNumberOfBreakpoints(REGULAR));
        m_manager.removeBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291)))));
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(REGULAR));
        m_manager.removeBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(1110)))));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(REGULAR));
        m_manager.removeBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(1929)))));
        Assert.assertEquals(0, m_manager.getNumberOfBreakpoints(REGULAR));
    }

    @Test
    public void testRemoveSteppingBreakpoint() {
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
        final MockBreakpointManagerListener listener = new MockBreakpointManagerListener();
        m_manager.addListener(listener);
        m_manager.removeBreakpoints(STEP, Sets.newHashSet(new BreakpointAddress(m_module, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(4096)))));
        // The breakpoint was removed
        Assert.assertEquals(0, m_manager.getNumberOfBreakpoints(STEP));
        // Make sure that the message was sent
        Assert.assertEquals(1, listener.size());
    }

    @Test
    public void testSetup() {
        Assert.assertEquals(3, m_manager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(2, m_manager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
    }

    @Test
    public void testStepEchoBreakpointsPassive() {
        Assert.assertEquals(1, m_manager.getNumberOfBreakpoints(STEP));
        final MockBreakpointManagerListener listener = new MockBreakpointManagerListener();
        m_manager.addListener(listener);
        m_manager.clearBreakpointsPassive(STEP);
        Assert.assertEquals(0, m_manager.getNumberOfBreakpoints(STEP));
        // Make sure that no message was sent
        Assert.assertEquals(0, listener.size());
    }
}

