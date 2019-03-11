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
package com.google.security.zynamics.binnavi.Debug.Debugger;


import BreakpointStatus.BREAKPOINT_ACTIVE;
import BreakpointStatus.BREAKPOINT_DELETING;
import BreakpointStatus.BREAKPOINT_DISABLED;
import BreakpointStatus.BREAKPOINT_ENABLED;
import BreakpointStatus.BREAKPOINT_HIT;
import BreakpointStatus.BREAKPOINT_INACTIVE;
import BreakpointStatus.BREAKPOINT_INVALID;
import BreakpointType.REGULAR;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.debug.connection.packets.parsers.MessageParserException;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.synchronizers.DebuggerSynchronizer;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryModule;
import com.google.security.zynamics.binnavi.disassembly.RelocatedAddress;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class BreakpointLifecycleTest {
    private final CAddress mockFileBase = new CAddress(0);

    private final RelocatedAddress mockImageBase = new RelocatedAddress(new CAddress(4096));

    private static final long mockImageSize = 65536;

    private final MemoryModule mockMemoryModule = new MemoryModule("Mock Module", "C:\\mockmodule.exe", mockImageBase, BreakpointLifecycleTest.mockImageSize);

    private MockDebugger m_debugger;

    private BreakpointManager m_breakpointManager;

    private DebuggerSynchronizer m_synchronizer;

    private final MockEventListener m_listener = new MockEventListener();

    @Test
    public void testActiveToDeleting() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint
        // 2. Debug clients sets breakpoint in the target process
        // 3. User deletes breakpoint
        // 4. Debug client removes breakpoint
        // 
        // Expected result:
        // - Breakpoint is removed from the target process
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testActiveToDisabled() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint
        // 2. Debug clients sets breakpoint in the target process
        // 3. User disables breakpoint
        // 4. Debug client removes breakpoint
        // 
        // Expected result:
        // - Breakpoint is removed from the target process
        // - Final breakpoint status DISABLED
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(1, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(BREAKPOINT_DISABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testActiveToHit() throws MessageParserException, DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint
        // 2. Debug clients sets breakpoint in the target process
        // 3. Breakpoint is hit
        // 
        // Expected result:
        // - Final breakpoint status HIT
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointHit(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_HIT, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testDeletingToDisabled() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint
        // 2. Debug client sets breakpoint
        // 3. User deletes breakpoint
        // 4. User disables breakpoints
        // 
        // Expected result:
        // - Not possible; exception
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        // 1. User sets breakpoint
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
        // 2. Debug client sets breakpoint
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        // 3. User deletes breakpoint
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        // assertEquals(BreakpointStatus.BREAKPOINT_DELETING,
        // m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456,
        // BreakpointType.REGULAR));
        // try
        // {
        // // 4. User disables breakpoints
        // m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET,
        // BreakpointType.REGULAR, BreakpointStatus.BREAKPOINT_DISABLED);
        // fail();
        // }
        // catch (final IllegalStateException exception)
        // {
        // }
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
        // assertEquals(BreakpointStatus.BREAKPOINT_DISABLED,
        // m_breakpointManager.getBreakpoint(BreakpointType.REGULAR, CommonTestObjects.BP_ADDRESS_456));
        // 
        // // 5. Debug client deletes breakpoint
        // m_synchronizer.handleBreakpointRemoveSucc(DebuggerMessageBuilder.buildEchoBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        // 
        // assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(BreakpointType.REGULAR));
    }

    @Test
    public void testDisabledToDeleting() throws DebugExceptionWrapper {
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        Assert.assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        Assert.assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testEnabledToActive() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint
        // 2. SET reply arrives from the Debug Client
        // 
        // Expected result:
        // - Final breakpoint status ACTIVE
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testEnabledToDisabled() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint (SET Message is sent to the Debug Client)
        // 2. User disables breakpoint (REMOVE Message is sent to the Debug Client)
        // 
        // At this point there are two messages on the way to the debug client which
        // are guaranteed to come back in the order they were sent.
        // 
        // Expected result:
        // - Final breakpoint status DISABLED
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_DISABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_DISABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testEnabledToInvalid() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint
        // 2. Debug client fails to set breakpoint
        // 
        // Expected result:
        // 
        // - Final breakpoint status INVALID
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointSetError(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_INVALID, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testHitToDeleting() throws MessageParserException, DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint (SET Message is sent to the Debug Client)
        // 2. Debug client sets breakpoint
        // 3. Debug client hits breakpoint
        // 4. User deletes breakpoint (REMOVE Message is sent to the Debug Client)
        // 
        // Expected result:
        // - Breakpoint is removed from the breakpoint manager
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        // 1. User sets breakpoint (SET Message is sent to the Debug Client)
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        // 2. Debug client sets breakpoint
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        // 3. Debug client hits breakpoint
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointHit(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_HIT, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        // 4. User deletes breakpoint (REMOVE Message is sent to the Debug Client)
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testHitToDisabled() throws MessageParserException, DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets breakpoint (SET Message is sent to the Debug Client)
        // 2. Debug client sets breakpoint
        // 3. Debug client hits breakpoint
        // 4. User disables breakpoint (REMOVE Message is sent to the Debug Client)
        // 
        // Expected result:
        // - Final breakpoint status DISABLED
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        // 1. User sets breakpoint (SET Message is sent to the Debug Client)
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        // 2. Debug client sets breakpoint
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildBreakpointSuccess(CommonTestObjects.BP_ADDRESS_456_RELOC));
        // 3. Debug client hits breakpoint
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointHit(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_HIT, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        // 4. User disables breakpoint (REMOVE Message is sent to the Debug Client)
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_DISABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;REMOVE_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testInactiveToDeleting() {
        // Scenario:
        // 
        // 1. Debugger is inactive
        // 2. User sets a breakpoint
        // 3. User removes breakpoint
        // 
        // Expected result:
        // - Breakpoint is removed from the breakpoint manager
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        Assert.assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals("", m_debugger.requests);
    }

    @Test
    public void testInactiveToDisabled() {
        // Scenario:
        // 
        // 1. Debugger is inactive
        // 2. User sets a breakpoint
        // 3. User disables breakpoint
        // 
        // Expected result:
        // - Breakpoint is disabled
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        Assert.assertEquals(BREAKPOINT_DISABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("", m_debugger.requests);
    }

    @Test
    public void testInactiveToEnabled() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. Debugger is inactive
        // 2. User sets a breakpoint
        // 3. Debugger is started
        // 4. Process start event
        // 
        // Expected result:
        // - Final breakpoint status ENABLED
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_INACTIVE, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        Assert.assertEquals(BREAKPOINT_ENABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;SET_BREAKPOINTS/00000456/REGULAR;RESUME;", m_debugger.requests);
    }

    @Test
    public void testInvalidToDeleting() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets a breakpoint
        // 2. Debug client fails to set breakpoint
        // 3. User removes breakpoint
        // 
        // Expected result:
        // - Breakpoint is removed from the breakpoint manager
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointSetError(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_INVALID, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        Assert.assertEquals(0, m_breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }

    @Test
    public void testInvalidToDisabled() throws DebugExceptionWrapper {
        // Scenario:
        // 
        // 1. User sets a breakpoint
        // 2. Debug client fails to set breakpoint
        // 3. User disables breakpoint
        // 
        // Expected result:
        // - Breakpoint is removed from the breakpoint manager
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        m_breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointSetError(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(BREAKPOINT_INVALID, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        m_breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        Assert.assertEquals(BREAKPOINT_DISABLED, m_breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        Assert.assertEquals("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;", m_debugger.requests);
    }
}

