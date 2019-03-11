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
import com.google.security.zynamics.binnavi.API.disassembly.Module;
import com.google.security.zynamics.binnavi.API.disassembly.ModuleFactory;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class BreakpointManagerTest {
    private final BreakpointManager internalManager = new BreakpointManager();

    private final MockBreakpointManagerListener mockListener = new MockBreakpointManagerListener();

    private final BreakpointManager apiManager = new com.google.security.zynamics.binnavi.API.debug.BreakpointManager(internalManager);

    final Module module = ModuleFactory.get(CommonTestObjects.MODULE);

    @Test
    public void testAddMultipleNative() {
        internalManager.addBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292)))));
        Assert.assertTrue(apiManager.hasEchoBreakpoint(module, new Address(292)));
        internalManager.clearBreakpointsPassive(ECHO);
    }

    @Test
    public void testGetBreakpoint() {
        try {
            apiManager.getBreakpoint(null, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            apiManager.getBreakpoint(null, new Address(291));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        apiManager.setBreakpoint(module, new Address(291));
        final Breakpoint breakpoint = apiManager.getBreakpoint(module, new Address(291));
        Assert.assertEquals(291, breakpoint.getAddress().toLong());
        final Module module = ModuleFactory.get();
        apiManager.setBreakpoint(module, new Address(291));
        Assert.assertTrue(apiManager.hasBreakpoint(module, new Address(291)));
        final Breakpoint breakpoint2 = apiManager.getBreakpoint(module, new Address(291));
        Assert.assertEquals(291, breakpoint2.getAddress().toLong());
        apiManager.removeBreakpoint(module, new Address(291));
    }

    @Test
    public void testGetBreakpoints() {
        apiManager.setBreakpoint(module, new Address(291));
        final List<Breakpoint> breakpoints = apiManager.getBreakpoints();
        Assert.assertEquals(291, breakpoints.get(0).getAddress().toLong());
    }

    @Test
    public void testGetEchoBreakpoint() {
        try {
            apiManager.getEchoBreakpoint(null, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            apiManager.getEchoBreakpoint(null, new Address(291));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        apiManager.setEchoBreakpoint(module, new Address(291));
        final Breakpoint breakpoint = apiManager.getEchoBreakpoint(module, new Address(291));
        Assert.assertEquals(291, breakpoint.getAddress().toLong());
        final Module module = ModuleFactory.get();
        apiManager.setEchoBreakpoint(module, new Address(291));
        Assert.assertTrue(apiManager.hasEchoBreakpoint(module, new Address(291)));
        final Breakpoint breakpoint2 = apiManager.getEchoBreakpoint(module, new Address(291));
        Assert.assertEquals(291, breakpoint2.getAddress().toLong());
        apiManager.removeEchoBreakpoint(module, new Address(291));
    }

    @Test
    public void testGetEchoBreakpoints() {
        apiManager.setEchoBreakpoint(module, new Address(291));
        final List<Breakpoint> breakpoints = apiManager.getEchoBreakpoints();
        Assert.assertEquals(291, breakpoints.get(0).getAddress().toLong());
    }

    @Test
    public void testHasBreakpoint() {
        try {
            apiManager.hasBreakpoint(null, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertFalse(apiManager.hasBreakpoint(module, new Address(291)));
        apiManager.setBreakpoint(module, new Address(291));
        Assert.assertTrue(apiManager.hasBreakpoint(module, new Address(291)));
    }

    @Test
    public void testHasEchoBreakpoint() {
        try {
            apiManager.hasEchoBreakpoint(null, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertFalse(apiManager.hasEchoBreakpoint(module, new Address(291)));
        apiManager.setEchoBreakpoint(module, new Address(291));
        Assert.assertTrue(apiManager.hasEchoBreakpoint(module, new Address(291)));
    }

    @Test
    public void testPreinitialized() {
        final BreakpointManager internalManager = new BreakpointManager();
        internalManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_0_SET);
        internalManager.addBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(1)))));
        final BreakpointManager apiManager = new BreakpointManager(internalManager);
        Assert.assertEquals(0, apiManager.getBreakpoints().get(0).getAddress().toLong());
        Assert.assertEquals(1, apiManager.getEchoBreakpoints().get(0).getAddress().toLong());
    }

    @Test
    public void testRemoveBreakpoint() {
        try {
            apiManager.removeBreakpoint(null, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        apiManager.setBreakpoint(module, new Address(291));
        apiManager.setBreakpoint(module, new Address(292));
        apiManager.addListener(mockListener);
        apiManager.removeBreakpoint(module, new Address(291));
        internalManager.removeBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292)))));
        Assert.assertEquals("removedBreakpoint/124;", mockListener.events);
        Assert.assertEquals(1, apiManager.getBreakpoints().size());
        apiManager.removeListener(mockListener);
    }

    @Test
    public void testRemoveEchoBreakpoint() {
        try {
            apiManager.removeEchoBreakpoint(null, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        apiManager.setEchoBreakpoint(module, new Address(291));
        apiManager.setEchoBreakpoint(module, new Address(292));
        apiManager.addListener(mockListener);
        apiManager.removeEchoBreakpoint(module, new Address(291));
        internalManager.removeBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292)))));
        Assert.assertEquals("removedEchoBreakpoint/123;removedEchoBreakpoint/124;", mockListener.events);
        Assert.assertEquals(0, apiManager.getBreakpoints().size());
        apiManager.removeListener(mockListener);
    }

    @Test
    public void testSetBreakpoint() {
        apiManager.addListener(mockListener);
        try {
            apiManager.setBreakpoint(null, null);
        } catch (final NullPointerException exception) {
        }
        apiManager.setBreakpoint(module, new Address(291));
        Assert.assertEquals("addedBreakpoint/123;", mockListener.events);
        try {
            apiManager.setBreakpoint(null, new Address(291));
        } catch (final NullPointerException exception) {
        }
        internalManager.addBreakpoints(REGULAR, Sets.newHashSet(new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292)))));
        Assert.assertEquals("addedBreakpoint/123;addedBreakpoint/124;", mockListener.events);
        Assert.assertTrue(apiManager.hasBreakpoint(module, new Address(292)));
        apiManager.removeListener(mockListener);
    }

    @Test
    public void testSetEchoBreakpoint() {
        apiManager.addListener(mockListener);
        try {
            apiManager.setEchoBreakpoint(null, null);
        } catch (final NullPointerException exception) {
        }
        apiManager.setEchoBreakpoint(module, new Address(291));
        Assert.assertEquals("addedEchoBreakpoint/123;", mockListener.events);
        try {
            apiManager.setEchoBreakpoint(null, new Address(291));
        } catch (final NullPointerException exception) {
        }
        internalManager.addBreakpoints(ECHO, Sets.newHashSet(new BreakpointAddress(CommonTestObjects.MODULE, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292)))));
        Assert.assertEquals("addedEchoBreakpoint/123;addedEchoBreakpoint/124;", mockListener.events);
        Assert.assertTrue(apiManager.hasEchoBreakpoint(module, new Address(292)));
        apiManager.removeListener(mockListener);
    }

    @Test
    public void testToString() {
        final String test = apiManager.toString();
        Assert.assertNotNull(test);
    }
}

