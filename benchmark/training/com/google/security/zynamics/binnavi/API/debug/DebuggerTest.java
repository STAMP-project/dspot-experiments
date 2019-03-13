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
import BreakpointType.STEP;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.API.disassembly.Address;
import com.google.security.zynamics.binnavi.API.disassembly.Module;
import com.google.security.zynamics.binnavi.API.disassembly.ModuleFactory;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Connection.MockDebugConnection;
import com.google.security.zynamics.binnavi.Debug.Debugger.DebuggerMessageBuilder;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Exceptions.MaybeNullException;
import com.google.security.zynamics.binnavi.debug.connection.packets.parsers.MessageParserException;
import com.google.security.zynamics.binnavi.debug.connection.packets.parsers.TargetInformationParser;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.AttachReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.AuthenticationFailedReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointConditionSetReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointHitReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.DebuggerClosedUnexpectedlyReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.DetachReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointHitReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ExceptionOccurredReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.HaltReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ListFilesReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ListProcessesReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ProcessClosedReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ReadMemoryReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.RegistersReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.RequestTargetReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ResumeReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ResumeThreadReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SearchReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SelectFileReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SetRegisterReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SingleStepReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointHitReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.SuspendThreadReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.TargetInformationReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.TerminateReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadClosedReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ThreadCreatedReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ValidateMemoryReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.WriteMemoryReply;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.DebugTargetSettings;
import com.google.security.zynamics.binnavi.debug.models.processlist.ProcessList;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryModule;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.remotebrowser.RemoteFileSystem;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.ThreadRegisters;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.RelocatedAddress;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.general.Pair;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;


/**
 * Tests various debugger functionality.
 */
@RunWith(JUnit4.class)
public final class DebuggerTest {
    private MockDebugger mockDebugger;

    private DebugTargetSettings debugSettings;

    private Debugger debugger;

    @Test
    public void testConnect1() throws DebugException {
        debugger.connect();
        Assert.assertEquals("CONNECT;", mockDebugger.requests);
        Assert.assertEquals("Debugger 'Mock'", debugger.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testConnect2() throws DebugException {
        debugger.connect();
        Assert.assertEquals("CONNECT;", mockDebugger.requests);
        debugger.connect();
        Assert.assertEquals("CONNECT;", mockDebugger.requests);
    }

    @Test(expected = DebugException.class)
    public void testDetach1() throws DebugException {
        debugger.detach();
    }

    @Test
    public void testDetach2() throws DebugException {
        debugger.connect();
        debugger.detach();
        Assert.assertEquals("CONNECT;DETACH;", mockDebugger.requests);
        debugger.detach();
        Assert.assertEquals("CONNECT;DETACH;DETACH;", mockDebugger.requests);
    }

    @Test
    public void testGetBookmarkManager() {
        final BookmarkManager bm1 = debugger.getBookmarkManager();
        final BookmarkManager bm2 = debugger.getBookmarkManager();
        Assert.assertNotNull(bm1);
        Assert.assertEquals(bm1, bm2);
    }

    @Test
    public void testGetBreakpointManager() {
        final BreakpointManager bm1 = debugger.getBreakpointManager();
        final BreakpointManager bm2 = debugger.getBreakpointManager();
        Assert.assertNotNull(bm1);
        Assert.assertEquals(bm1, bm2);
    }

    @Test
    public void testGetProcess() {
        final Process p1 = debugger.getProcess();
        final Process p2 = debugger.getProcess();
        Assert.assertNotNull(p1);
        Assert.assertEquals(p1, p2);
    }

    @Test
    public void testIsConnected() throws DebugException {
        Assert.assertFalse(debugger.isConnected());
        debugger.connect();
        Assert.assertTrue(debugger.isConnected());
    }

    @Test
    public void testlisteners0() throws MaybeNullException, DebugExceptionWrapper, IOException, ParserConfigurationException, SAXException {
        mockDebugger.connect();
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, Lists.newArrayList(new RegisterDescription("eax", 4, true), new RegisterDescription("ebx", 4, false)), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        mockDebugger.connection.m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(CommonTestObjects.MEMORY_MODULE));
        getProcessManager().getThread(0).setCurrentAddress(new RelocatedAddress(new CAddress(0)));
        getBreakpointManager().addBreakpoints(ECHO, CommonTestObjects.BP_ADDRESS_123_SET);
        final ArrayList<Pair<RelocatedAddress, Integer>> list = new ArrayList<Pair<RelocatedAddress, Integer>>();
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointSetReply(0, 0, list));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointSetReply(0, 0, list));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointsRemovedReply(0, 0, list));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointsRemovedReply(0, 0, list));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ExceptionOccurredReply(0, 0, 0, 0, new RelocatedAddress(new CAddress(0)), "Test exception"));
        mockDebugger.connection.m_synchronizer.receivedEvent(new HaltReply(0, 0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new HaltReply(0, 0, 1));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ListFilesReply(0, 0, RemoteFileSystem.parse("<foo></foo>".getBytes())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ListFilesReply(0, 1, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ListProcessesReply(0, 0, ProcessList.parse("<foo></foo>".getBytes())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ListProcessesReply(0, 1, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new MemoryMapReply(0, 0, new MemoryMap(new ArrayList<com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection>())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new MemoryMapReply(0, 1, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ModuleLoadedReply(0, 0, new MemoryModule("XXX", "YYYXXX", new RelocatedAddress(new CAddress(0)), 0), new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(123, ThreadState.SUSPENDED)));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ModuleUnloadedReply(0, 0, new MemoryModule("XXX", "YYYXXX", new RelocatedAddress(new CAddress(0)), 0)));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ProcessClosedReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ReadMemoryReply(0, 0, new CAddress(0), new byte[8]));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ReadMemoryReply(0, 1, null, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new RegistersReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(new com.google.security.zynamics.zylib.types.lists.FilledList<ThreadRegisters>())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new RegistersReply(0, 1, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new RequestTargetReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new RequestTargetReply(0, 1));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ResumeReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ResumeReply(0, 1));
    }

    @Test
    public void testListeners1() throws DebugExceptionWrapper {
        final DebuggerTest.DebuggerListener listener = new DebuggerTest.DebuggerListener();
        debugger.addListener(listener);
        mockDebugger.connect();
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(1, ThreadState.RUNNING));
        mockDebugger.connection.m_synchronizer.receivedEvent(new AttachReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new AttachReply(0, 1));
        mockDebugger.connection.m_synchronizer.receivedEvent(new AuthenticationFailedReply());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListeners2() throws DebugExceptionWrapper {
        final DebuggerTest.DebuggerListener listener = new DebuggerTest.DebuggerListener();
        debugger.addListener(listener);
        mockDebugger.connect();
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(1, ThreadState.RUNNING));
        mockDebugger.connection.m_synchronizer.receivedEvent(new BreakpointConditionSetReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new BreakpointConditionSetReply(0, 1));
        mockDebugger.connection.m_synchronizer.receivedEvent(new BreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], true, false)))))));
        getBreakpointManager().addBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 1))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 1))));
        getBreakpointManager().removeBreakpoints(REGULAR, CommonTestObjects.BP_ADDRESS_123_SET);
        mockDebugger.connection.m_synchronizer.receivedEvent(new DebuggerClosedUnexpectedlyReply());
        mockDebugger.connection.m_synchronizer.receivedEvent(new DetachReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new DetachReply(0, 1));
        mockDebugger.connection.m_synchronizer.receivedEvent(new EchoBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], false, false)))))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new EchoBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], true, false)))))));
        debugger.removeListener(listener);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListeners3() throws MaybeNullException, MessageParserException, DebugExceptionWrapper {
        mockDebugger.connect();
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        getProcessManager().getThread(0).setCurrentAddress(new RelocatedAddress(new CAddress(0)));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ResumeThreadReply(0, 0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ResumeThreadReply(0, 1, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SearchReply(0, 0, new CAddress(0)));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SearchReply(0, 1, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SelectFileReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SelectFileReply(0, 1));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        getProcessManager().getThread(0).setCurrentAddress(new RelocatedAddress(new CAddress(0)));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SetRegisterReply(0, 0, 0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SetRegisterReply(0, 1, 0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SingleStepReply(0, 0, 0, new RelocatedAddress(new CAddress(0)), new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(new com.google.security.zynamics.zylib.types.lists.FilledList<ThreadRegisters>())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SingleStepReply(0, 1, 0, new RelocatedAddress(new CAddress(0)), new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(new com.google.security.zynamics.zylib.types.lists.FilledList<ThreadRegisters>())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new StepBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], false, false)))))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new StepBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], true, false)))))));
        getBreakpointManager().addBreakpoints(STEP, CommonTestObjects.BP_ADDRESS_123_SET);
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointSetReply(0, 0, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointSetReply(0, 1, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 1))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointsRemovedReply(0, 0, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 0))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointsRemovedReply(0, 1, Lists.newArrayList(new Pair<RelocatedAddress, Integer>(CommonTestObjects.BP_ADDRESS_123_RELOC, 1))));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SuspendThreadReply(0, 0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new SuspendThreadReply(0, 1, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, TargetInformationParser.parse("<foo><size>32</size><registers></registers><options></options></foo>".getBytes())));
        mockDebugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 1, null));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ThreadClosedReply(0, 0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ThreadClosedReply(0, 1, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ThreadCreatedReply(0, 0, 0, ThreadState.RUNNING));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ThreadCreatedReply(0, 1, 0, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ValidateMemoryReply(0, 0, new CAddress(0), new CAddress(0)));
        mockDebugger.connection.m_synchronizer.receivedEvent(new ValidateMemoryReply(0, 1, null, null));
        mockDebugger.connection.m_synchronizer.receivedEvent(new WriteMemoryReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new WriteMemoryReply(0, 1));
        mockDebugger.connection.m_synchronizer.receivedEvent(new TerminateReply(0, 0));
        mockDebugger.connection.m_synchronizer.receivedEvent(new TerminateReply(0, 1));
    }

    @Test(expected = DebugException.class)
    public void testReadMemory1() throws DebugException {
        debugger.readMemory(new Address(0), 256);
    }

    @Test
    public void testReadMemory2() throws DebugException {
        debugger.connect();
        debugger.readMemory(new Address(0), 256);
        Assert.assertEquals("CONNECT;READMEM/00000000/256;", mockDebugger.requests);
    }

    @Test(expected = DebugException.class)
    public void testReadRegisters1() throws DebugException {
        debugger.readRegisters();
    }

    @Test
    public void testReadRegisters() throws DebugException {
        debugger.connect();
        debugger.readRegisters();
        Assert.assertEquals("CONNECT;READREGS;", mockDebugger.requests);
    }

    @Test
    public void testRelocation1() {
        final INaviModule nativeModule = new MockModule();
        mockDebugger.setAddressTranslator(nativeModule, new CAddress(100), new CAddress(200));
        final Module module = ModuleFactory.get(nativeModule);
        Assert.assertEquals(250, debugger.toImagebase(module, new Address(150)).toLong());
        Assert.assertEquals(150, debugger.toFilebase(module, new Address(250)).toLong());
    }

    @Test(expected = NullPointerException.class)
    public void testRelocation2() {
        debugger.toImagebase(null, new Address(250));
    }

    @Test(expected = NullPointerException.class)
    public void testRelocation3() {
        final INaviModule nativeModule = new MockModule();
        final Module module = ModuleFactory.get(nativeModule);
        debugger.toImagebase(module, null);
    }

    @Test(expected = NullPointerException.class)
    public void testRelocation4() {
        debugger.toFilebase(null, new Address(250));
    }

    @Test(expected = NullPointerException.class)
    public void testRelocation5() {
        final INaviModule nativeModule = new MockModule();
        final Module module = ModuleFactory.get(nativeModule);
        debugger.toFilebase(module, null);
        Assert.fail();
    }

    @Test(expected = DebugException.class)
    public void testResume1() throws DebugException {
        debugger.resume();
    }

    @Test
    public void testResume2() throws DebugException {
        debugger.connect();
        debugger.resume();
        Assert.assertEquals("CONNECT;RESUME;", mockDebugger.requests);
    }

    @Test(expected = DebugException.class)
    public void testSingleStep1() throws DebugException {
        debugger.singleStep();
    }

    @Test
    public void testSingleStep2() throws DebugException {
        debugger.connect();
        debugger.singleStep();
        Assert.assertEquals("CONNECT;STEP;", mockDebugger.requests);
    }

    @Test(expected = DebugException.class)
    public void testTerminate1() throws DebugException {
        debugger.terminate();
    }

    @Test
    public void testTerminate() throws DebugException {
        debugger.connect();
        debugger.terminate();
        Assert.assertEquals("CONNECT;TERMINATE;", mockDebugger.requests);
    }

    @Test(expected = NullPointerException.class)
    public void writeMemory1() throws DebugException {
        debugger.writeMemory(null, new byte[10]);
    }

    @Test(expected = NullPointerException.class)
    public void writeMemory2() throws DebugException {
        debugger.writeMemory(new Address(0), null);
    }

    @Test(expected = DebugException.class)
    public void writeMemory3() throws DebugException {
        debugger.writeMemory(new Address(0), new byte[10]);
    }

    @Test
    public void writeMemory4() throws DebugException {
        debugger.connect();
        debugger.writeMemory(new Address(0), new byte[10]);
    }

    @Test(expected = NullPointerException.class)
    public void writeRegister1() throws DebugException {
        debugger.writeRegister(0, "eax", 0);
    }

    @Test(expected = NullPointerException.class)
    public void writeRegister2() throws DebugException {
        debugger.writeRegister(0, null, 0);
    }

    @Test
    public void writeRegister3() throws DebugException {
        debugger.connect();
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, Lists.newArrayList(new RegisterDescription("eax", 4, true), new RegisterDescription("ebx", 4, false)), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        debugger.writeRegister(0, "eax", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeRegister4() throws DebugException {
        debugger.connect();
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, Lists.newArrayList(new RegisterDescription("eax", 4, true), new RegisterDescription("ebx", 4, false)), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        debugger.writeRegister(0, "ebx", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeRegister5() throws DebugException {
        debugger.connect();
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, Lists.newArrayList(new RegisterDescription("eax", 4, true), new RegisterDescription("ebx", 4, false)), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        debugger.writeRegister(0, "ecx", 0);
    }

    /**
     * Mock debugger listener
     */
    private class DebuggerListener extends DebuggerListenerAdapter {}
}

