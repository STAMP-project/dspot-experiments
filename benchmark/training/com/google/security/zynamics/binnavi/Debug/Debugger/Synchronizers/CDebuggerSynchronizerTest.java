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
package com.google.security.zynamics.binnavi.Debug.Debugger.Synchronizers;


import BreakpointStatus.BREAKPOINT_ACTIVE;
import BreakpointStatus.BREAKPOINT_DELETING;
import BreakpointStatus.BREAKPOINT_DISABLED;
import BreakpointStatus.BREAKPOINT_ENABLED;
import BreakpointStatus.BREAKPOINT_HIT;
import BreakpointStatus.BREAKPOINT_INACTIVE;
import BreakpointStatus.BREAKPOINT_INVALID;
import BreakpointType.ECHO;
import BreakpointType.REGULAR;
import BreakpointType.STEP;
import ThreadState.RUNNING;
import ThreadState.SUSPENDED;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.CUtilityFunctions;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Debugger.DebuggerMessageBuilder;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockEventListener;
import com.google.security.zynamics.binnavi.Exceptions.MaybeNullException;
import com.google.security.zynamics.binnavi.Log.NaviLogger;
import com.google.security.zynamics.binnavi.debug.connection.packets.parsers.MessageParserException;
import com.google.security.zynamics.binnavi.debug.connection.packets.parsers.RegisterValuesParser;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.AttachReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.AuthenticationFailedReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.DebuggerClosedUnexpectedlyReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.DetachReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ProcessClosedReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ReadMemoryReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ResumeReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ResumeThreadReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SetRegisterReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SingleStepReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SuspendThreadReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.TargetInformationReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.TerminateReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadClosedReply;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.synchronizers.DebuggerSynchronizer;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryModule;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection;
import com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.ThreadRegisters;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.types.lists.IFilledList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test class for functions related to debugger synchronization.
 */
@RunWith(JUnit4.class)
public final class CDebuggerSynchronizerTest {
    private MockDebugger mockDebugger;

    private DebuggerSynchronizer debuggerSynchronizer;

    private BreakpointManager breakpointManager;

    private final MockEventListener listener = new MockEventListener();

    @Test
    public void testAddBreakpointEcho() throws DebugExceptionWrapper {
        // It is not possible to set echo breakpoint in unconnected debuggers
        mockDebugger.connect();
        breakpointManager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_456_SET);
        // Immediately try to set the breakpoint in the target process if the debugger is active
        Assert.assertEquals(BREAKPOINT_INACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, ECHO));
    }

    @Test
    public void testAddBreakpointRegular() throws DebugExceptionWrapper {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        // Nothing happens to new breakpoints when the debugger is not connected
        Assert.assertEquals(BREAKPOINT_INACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_123, REGULAR));
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(CommonTestObjects.MEMORY_MODULE));
        // Immediately try to set the breakpoint in the target process if the debugger is active
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
    }

    @Test
    public void testAddBreakpointStep() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(CommonTestObjects.MEMORY_MODULE));
        // Immediately try to set the breakpoint in the target process if the debugger is active
        breakpointManager.addBreakpoints(STEP, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, STEP));
    }

    @Test
    public void testAttachError() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new AttachReply(0, 5));
        Assert.assertFalse(getProcessManager().isAttached());
        Assert.assertEquals("ERROR_ATTACH/5;", listener.events);
    }

    @Test
    public void testAttachSuccess() throws DebugExceptionWrapper {
        // Connect the debugger
        mockDebugger.connect();
        Assert.assertTrue(isConnected());
        debuggerSynchronizer.receivedEvent(new AttachReply(0, 0));
        Assert.assertTrue(getProcessManager().isAttached());
    }

    /**
     * This test makes sure that authentication failure replies are handled correctly.
     *
     *  This message is sent if the client can not authenticate itself as a BinNavi debug client. In
     * that case BinNavi has to close the connection to the debug client and reset the internal state
     * of the process.
     *
     * @throws DebugExceptionWrapper
     * 		Thrown if something goes wrong.
     */
    @Test
    public void testAuthenticationFailed() throws DebugExceptionWrapper {
        mockDebugger.connect();
        Assert.assertTrue(isConnected());
        debuggerSynchronizer.receivedEvent(new AuthenticationFailedReply());
        Assert.assertFalse(isConnected());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBreakpointRemoveErr() {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 5))));
        Assert.assertEquals(BREAKPOINT_INVALID, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_123, REGULAR));
        Assert.assertEquals("ERROR_REMOVE_BREAKPOINTS/00001123/5;", listener.events);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBreakpointRemoveSucc() throws DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_123_SET, REGULAR, BREAKPOINT_DELETING);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        Assert.assertEquals(0, breakpointManager.getNumberOfBreakpoints(REGULAR));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBreakpointSetErr() {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 5))));
        Assert.assertEquals(BREAKPOINT_INVALID, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_123, REGULAR));
        Assert.assertEquals("ERROR_SET_BREAKPOINTS/00001123/5;", listener.events);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBreakpointSetSucc() {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        Assert.assertEquals(1, breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_123, REGULAR));
    }

    @Test
    public void testDetachError() {
        debuggerSynchronizer.receivedEvent(new DetachReply(0, 5));
        Assert.assertFalse(isConnected());
        Assert.assertEquals("ERROR_DETACH/5;", listener.events);
    }

    @Test
    public void testDetachSucc() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new DetachReply(0, 0));
        Assert.assertFalse(isConnected());
    }

    @Test
    public void testDisableBreakpointRegularDisabledDebugger() {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_INACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        Assert.assertEquals("", mockDebugger.requests);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEchoBreakpointRemoveSucc() throws DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        Assert.assertEquals(0, breakpointManager.getNumberOfBreakpoints(ECHO));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEchoBreakpointSetErr() throws DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 5))));
        Assert.assertEquals(0, breakpointManager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals("ERROR_SET_ECHO_BREAKPOINT/00001123/5;", listener.events);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEchoBreakpointSetSucc() throws DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_123_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        Assert.assertEquals(1, breakpointManager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_123, ECHO));
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
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(CommonTestObjects.MEMORY_MODULE));
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        // If the debugger is active, the breakpoint is removed from the target process
        Assert.assertEquals(("CONNECT;READREGS;RESUME;SET_BREAKPOINTS/00000456/REGULAR;" + "REMOVE_BREAKPOINTS/00000456/REGULAR;"), mockDebugger.requests);
    }

    @Test
    public void testErrorConnectionClosed() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new DebuggerClosedUnexpectedlyReply());
        Assert.assertFalse(isConnected());
        Assert.assertEquals("DEBUGGER_CLOSED/0;", listener.events);
    }

    @Test
    public void testHandleExceptionOccured() throws MaybeNullException, DebugExceptionWrapper {
        mockDebugger.connect();
        final TargetProcessThread thread = new TargetProcessThread(18, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ExceptionOccurredReply(0, 0, 18, 5, CommonTestObjects.BP_ADDRESS_123_RELOC, "Test exception"));
        Assert.assertEquals(thread, getProcessManager().getActiveThread());
        Assert.assertEquals(RUNNING, getProcessManager().getThread(18).getState());
        Assert.assertEquals(CommonTestObjects.BP_ADDRESS_123_RELOC, getProcessManager().getThread(18).getCurrentAddress());
        Assert.assertEquals("CONNECT;READREGS;", mockDebugger.requests);
        Assert.assertEquals("EXCEPTION_OCCURRED/5;", listener.events);
    }

    @Test
    public void testHitBreakpoint_UnknownBreakpoint() throws MessageParserException {
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.SUSPENDED));
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointHit(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(0, listener.exception);
    }

    @Test
    public void testHitBreakpoint_UnknownThread() throws MessageParserException {
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointHit(CommonTestObjects.BP_ADDRESS_123_RELOC));
        Assert.assertEquals(0, listener.exception);
    }

    @Test
    public void testHitBreakpoint_Wellformed() throws MessageParserException {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_ACTIVE);
        getProcessManager().addThread(new TargetProcessThread(CommonTestObjects.THREAD_ID, ThreadState.SUSPENDED));
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildRegularBreakpointHit(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(BREAKPOINT_HIT, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
    }

    @Test
    public void testHitEchoBreakpoint() throws DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_456_SET);
        Assert.assertEquals(BREAKPOINT_INACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, ECHO));
    }

    @Test
    public void testHitEchoBreakpoint_Wellformed() throws MessageParserException, DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, ECHO, BREAKPOINT_ACTIVE);
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.SUSPENDED));
        Assert.assertEquals(1, breakpointManager.getNumberOfBreakpoints(ECHO));
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, ECHO));
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildEchoBreakpointHit(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(1, breakpointManager.getNumberOfBreakpoints(ECHO));
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildEchoBreakpointRemoveSucc(CommonTestObjects.BP_ADDRESS_456_RELOC));
        Assert.assertEquals(0, breakpointManager.getNumberOfBreakpoints(ECHO));
    }

    @Test
    public void testInfoString_Malformed() {
        debuggerSynchronizer.receivedEvent(new TargetInformationReply(0, 5, null));
        Assert.assertEquals(0, listener.exception);
    }

    @Test
    public void testInfoString_Wellformed() throws MaybeNullException, DebugExceptionWrapper {
        // Set breakpoints while the debugger is not connected
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        Assert.assertEquals(BREAKPOINT_INACTIVE, breakpointManager.getBreakpointStatus(REGULAR, 0));
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(CommonTestObjects.MEMORY_MODULE));
        Assert.assertEquals(0, listener.exception);
        Assert.assertNotNull(getProcessManager().getThread(CommonTestObjects.THREAD_ID));
        Assert.assertEquals(RUNNING, getProcessManager().getThread(CommonTestObjects.THREAD_ID).getState());
        debuggerSynchronizer.receivedEvent(new ThreadClosedReply(0, 0, CommonTestObjects.THREAD_ID));
        try {
            getProcessManager().getThread(CommonTestObjects.THREAD_ID);
            Assert.fail();
        } catch (final MaybeNullException exception) {
            CUtilityFunctions.logException(exception);
        }
        // On receiving an info string we request the memory map and the thread created reply triggers
        // read registers
        Assert.assertEquals((("CONNECT;READREGS;SET_BREAKPOINTS/" + (String.format("%08d", CommonTestObjects.THREAD_ID))) + "/REGULAR;RESUME;"), mockDebugger.requests);
        // Enabled breakpoints are activated
        Assert.assertEquals(BREAKPOINT_ENABLED, breakpointManager.getBreakpointStatus(REGULAR, 0));
        Assert.assertEquals(BREAKPOINT_DISABLED, breakpointManager.getBreakpointStatus(REGULAR, 1));
    }

    @Test
    public void testMemmap() {
        final IFilledList<MemorySection> sections = new com.google.security.zynamics.zylib.types.lists.FilledList<MemorySection>();
        sections.add(new MemorySection(new CAddress(100), new CAddress(200)));
        sections.add(new MemorySection(new CAddress(300), new CAddress(400)));
        debuggerSynchronizer.receivedEvent(new MemoryMapReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(sections)));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(2, getProcessManager().getMemoryMap().getNumberOfSections());
    }

    @Test
    public void testMemoryErr() {
        debuggerSynchronizer.receivedEvent(new ReadMemoryReply(0, 5, null, null));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals("ERROR_READING_MEMORY/5;", listener.events);
        Assert.assertFalse(getProcessManager().getMemory().hasData(CommonTestObjects.BP_ADDRESS_123.getAddress().getAddress().toLong(), 6));
    }

    @Test
    public void testMemorySucc() {
        debuggerSynchronizer.receivedEvent(new ReadMemoryReply(0, 0, CommonTestObjects.BP_ADDRESS_123.getAddress().getAddress(), "Hannes".getBytes()));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals("RECEIVED_MEMORY/00000123/6;", listener.events);
        Assert.assertTrue(getProcessManager().getMemory().hasData(291, 6));
    }

    /**
     * This test makes sure that the memory module lifecycle (Module Loaded -> Module Unloaded) is
     * working and that the process manager of the debugger is updated correctly.
     *
     * @throws DebugExceptionWrapper
     * 		
     */
    @Test
    public void testModuleLifecycle() throws DebugExceptionWrapper {
        Assert.assertTrue(getProcessManager().getModules().isEmpty());
        mockDebugger.connect();
        getProcessManager().getThreads().clear();
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadCreatedReply(0, 0, 1000, ThreadState.RUNNING));
        final MemoryModule module = new MemoryModule("hannes.dll", "C:\\hannes.dll", new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(16777216)), 1000);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ModuleLoadedReply(0, 0, module, new TargetProcessThread(1000, ThreadState.RUNNING)));
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, Lists.newArrayList(new RegisterDescription("eax", 4, true), new RegisterDescription("ebx", 4, false)), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        Assert.assertTrue(((getProcessManager().getModules().size()) == 1));
        Assert.assertTrue(((getProcessManager().getModules().get(0)) == module));
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ModuleUnloadedReply(0, 0, module));
        Assert.assertTrue(getProcessManager().getModules().isEmpty());
    }

    @Test
    public void testProcessClosed() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new ProcessClosedReply(0, 0));
        Assert.assertFalse(isConnected());
        Assert.assertEquals("PROCESS_CLOSED;", listener.events);
    }

    @Test
    public void testRegisterValues_Malformed() {
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.RUNNING));
        NaviLogger.setLevel(Level.OFF);
        try {
            debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.RegistersReply(0, 0, RegisterValuesParser.parse("Hannes".getBytes())));
            Assert.fail();
        } catch (final MessageParserException exception) {
            CUtilityFunctions.logException(exception);
        } finally {
            NaviLogger.setLevel(Level.SEVERE);
        }
    }

    @Test
    public void testRegisterValues_UnknownTID() throws MessageParserException {
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.SUSPENDED));
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.RegistersReply(0, 0, RegisterValuesParser.parse(("<Registers><Thread id=\"123\"><Register name=\"EAX\" " + (("value=\"123\" memory=\"\" /><Register name=\"EBX\" value=\"456\" memory=\"\" " + "/><Register name=\"EIP\" value=\"999\" memory=\"\" pc=\"true\" /></Thread>") + "</Registers>")).getBytes())));
        Assert.assertEquals(0, listener.exception);
    }

    @Test
    public void testRegisterValues_Wellformed() throws MaybeNullException, MessageParserException {
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.RUNNING));
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.RegistersReply(0, 0, RegisterValuesParser.parse(("<Registers><Thread id=\"123\"><Register name=\"EAX\" " + (("value=\"123\" memory=\"\" /><Register name=\"EBX\" value=\"456\" memory=\"\" " + "/><Register name=\"EIP\" value=\"999\" memory=\"\" pc=\"true\" /></Thread>") + "</Registers>")).getBytes())));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(1110, getProcessManager().getThread(123).getRegisterValues().get(1).getValue().longValue());
        Assert.assertEquals(2457, getProcessManager().getThread(123).getCurrentAddress().getAddress().toLong());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveBreakpoint_Active() throws DebugExceptionWrapper {
        mockDebugger.connect();
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_ACTIVE);
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.SUSPENDED));
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DELETING);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_456_RELOC, 0))));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(0, breakpointManager.getNumberOfBreakpoints(REGULAR));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveBreakpoint_Disabled() {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_DISABLED);
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.SUSPENDED));
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_456_RELOC, 0))));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(1, breakpointManager.getNumberOfBreakpoints(REGULAR));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveBreakpointError_Invalid() {
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 5, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_456_RELOC, 0))));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(0, breakpointManager.getNumberOfBreakpoints(REGULAR));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveBreakpointError_Valid() {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_ACTIVE);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_456_RELOC, 5))));
        Assert.assertEquals(0, listener.exception);
        Assert.assertEquals(1, breakpointManager.getNumberOfBreakpoints(REGULAR));
        Assert.assertEquals(BREAKPOINT_INVALID, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
    }

    @Test
    public void testResume_UnknownTID() {
        debuggerSynchronizer.receivedEvent(new ResumeReply(0, 0));
        Assert.assertEquals(0, listener.exception);
    }

    @Test
    public void testResume_Wellformed() throws MaybeNullException {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_456_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_456_SET, REGULAR, BREAKPOINT_HIT);
        final TargetProcessThread thread = new TargetProcessThread(123, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        getProcessManager().setActiveThread(thread);
        debuggerSynchronizer.receivedEvent(new ResumeReply(0, 0));
        Assert.assertEquals(0, listener.exception);
        Assert.assertNull(getProcessManager().getActiveThread());
        Assert.assertEquals(RUNNING, getProcessManager().getThread(123).getState());
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, REGULAR));
    }

    @Test
    public void testSetRegisterErr() {
        debuggerSynchronizer.receivedEvent(new SetRegisterReply(0, 5, 0, 0));
        Assert.assertEquals("ERROR_SET_REGISTERS/5;", listener.events);
    }

    @Test
    public void testSetRegisterSucc() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new SetRegisterReply(0, 0, 0, 0));
        Assert.assertEquals("CONNECT;READREGS;", mockDebugger.requests);
    }

    @Test
    public void testSingleStep_Err() {
        debuggerSynchronizer.receivedEvent(new SingleStepReply(0, 5, 0, new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(CommonTestObjects.BP_ADDRESS_123.getAddress().getAddress()), null));
        Assert.assertEquals("ERROR_SINGLE_STEP/5;", listener.events);
    }

    @Test
    public void testSingleStep_Valid() throws MaybeNullException, MessageParserException {
        breakpointManager.addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_333_SET);
        breakpointManager.setBreakpointStatus(CommonTestObjects.BP_ADDRESS_333_SET, REGULAR, BREAKPOINT_HIT);
        getProcessManager().addThread(new TargetProcessThread(123, ThreadState.SUSPENDED));
        debuggerSynchronizer.receivedEvent(new SingleStepReply(0, 0, 123, new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(2457)), RegisterValuesParser.parse(("<Registers><Thread id=\"123\">" + ((("<Register name=\"EAX\" value=\"123\" memory=\"\" />" + "<Register name=\"EBX\" value=\"456\" memory=\"\" />") + "<Register name=\"EIP\" value=\"999\" memory=\"\" pc=\"true\" />") + "</Thread></Registers>")).getBytes())));
        Assert.assertEquals(SUSPENDED, getProcessManager().getThread(123).getState());
        Assert.assertEquals(2457, getProcessManager().getThread(123).getCurrentAddress().getAddress().toLong());
        Assert.assertEquals(1110, getProcessManager().getThread(123).getRegisterValues().get(1).getValue().longValue());
        Assert.assertEquals(BREAKPOINT_ACTIVE, breakpointManager.getBreakpointStatus(CommonTestObjects.BP_ADDRESS_333, REGULAR));
    }

    /**
     * This test is used to determine whether the step breakpoint lifecycle (Set Step BP -> Hit Step
     * BP -> Remove Step BP) works correctly.
     *
     * @throws DebugExceptionWrapper
     * 		Thrown if something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStepBreakpointLifecycle() throws DebugExceptionWrapper {
        mockDebugger.connect();
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        getBreakpointManager().addBreakpoints(STEP, CommonTestObjects.BP_ADDRESS_123_SET);
        getBreakpointManager().addBreakpoints(STEP, CommonTestObjects.BP_ADDRESS_456_SET);
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0), new com.google.security.zynamics.zylib.general.Pair<com.google.security.zynamics.binnavi.disassembly.RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_456_RELOC, 0))));
        Assert.assertEquals(BREAKPOINT_ACTIVE, getBreakpointManager().getBreakpointStatus(CommonTestObjects.BP_ADDRESS_123, STEP));
        Assert.assertEquals(BREAKPOINT_ACTIVE, getBreakpointManager().getBreakpointStatus(CommonTestObjects.BP_ADDRESS_456, STEP));
        final RegisterValues registerValues = new RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(0, Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(291), new byte[0], true, false)))));
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointHitReply(0, 0, 0, registerValues));
        listener.toString();
        Assert.assertTrue(Iterables.isEmpty(getBreakpointManager().getBreakpoints(STEP)));
        Assert.assertEquals(thread, getProcessManager().getActiveThread());
        Assert.assertEquals(291, thread.getCurrentAddress().getAddress().toLong());
    }

    @Test
    public void testTerminate() throws DebugExceptionWrapper {
        mockDebugger.connect();
        Assert.assertTrue(isConnected());
        debuggerSynchronizer.receivedEvent(new TerminateReply(0, 0));
        Assert.assertFalse(isConnected());
    }

    @Test
    public void testThreadClosed() throws DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadCreatedReply(0, 0, 18, ThreadState.RUNNING));
        Assert.assertEquals(1, getProcessManager().getThreads().size());
        debuggerSynchronizer.receivedEvent(new ThreadClosedReply(0, 0, 18));
        Assert.assertEquals(0, getProcessManager().getThreads().size());
    }

    @Test
    public void testThreadCreated() throws MaybeNullException, DebugExceptionWrapper {
        mockDebugger.connect();
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadCreatedReply(0, 0, 18, ThreadState.RUNNING));
        debuggerSynchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadCreatedReply(0, 0, 19, ThreadState.SUSPENDED));
        Assert.assertEquals(RUNNING, getProcessManager().getThread(18).getState());
        Assert.assertEquals(SUSPENDED, getProcessManager().getThread(19).getState());
    }

    /**
     * This test is used to test the thread lifecycle of suspending and resuming threads.
     *
     * @throws DebugExceptionWrapper
     * 		Thrown if something goes wrong.
     */
    @Test
    public void testThreadLifecycle() throws DebugExceptionWrapper {
        mockDebugger.connect();
        final TargetProcessThread thread = new TargetProcessThread(123, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        debuggerSynchronizer.receivedEvent(new SuspendThreadReply(0, 0, 123));
        Assert.assertEquals(SUSPENDED, thread.getState());
        debuggerSynchronizer.receivedEvent(new ResumeThreadReply(0, 0, 123));
        Assert.assertEquals(RUNNING, thread.getState());
    }
}

