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
package com.google.security.zynamics.binnavi.Gui.Debug.History;


import BreakpointType.ECHO;
import BreakpointType.REGULAR;
import BreakpointType.STEP;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.Debug.Debugger.DebuggerMessageBuilder;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockEventListener;
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
import com.google.security.zynamics.binnavi.debug.debugger.synchronizers.DebuggerSynchronizer;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress;
import com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointManager;
import com.google.security.zynamics.binnavi.debug.models.processlist.ProcessList;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryModule;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.remotebrowser.RemoteFileSystem;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.ThreadRegisters;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.RelocatedAddress;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;


@RunWith(JUnit4.class)
public class CHistoryStringBuilderTest {
    private final MockModule mockModule = new MockModule();

    private final CAddress mockFileBase = new CAddress(0);

    private final RelocatedAddress mockImageBase = new RelocatedAddress(new CAddress(4096));

    private static final long mockImageSize = 65536;

    private final MemoryModule mockMemoryModule = new MemoryModule("Mock Module", "C:\\mockmodule.exe", mockImageBase, CHistoryStringBuilderTest.mockImageSize);

    private final BreakpointAddress BREAKPOINT_SINGLE_ADDRESS = new BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(1110)));

    private final Set<BreakpointAddress> BREAKPOINT_ADDRESS = Sets.newHashSet(BREAKPOINT_SINGLE_ADDRESS);

    private final RelocatedAddress BREAKPOINT_ADDRESS_RELOC = new RelocatedAddress(new CAddress(5206));

    private MockDebugger m_debugger;

    private BreakpointManager m_breakpointManager;

    private DebuggerSynchronizer m_synchronizer;

    private final MockEventListener m_listener = new MockEventListener();

    /**
     * This test tries to achieve complete code coverage to make sure all string formatters are
     * working as expected.
     *
     * @throws DebugExceptionWrapper
     * 		
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     * @throws ParserConfigurationException
     * 		
     * @throws MessageParserException
     * 		
     * @throws MaybeNullException
     * 		
     */
    @Test
    public void testComplete() throws MaybeNullException, MessageParserException, DebugExceptionWrapper, IOException, ParserConfigurationException, SAXException {
        final CHistoryStringBuilder builder = new CHistoryStringBuilder();
        builder.setDebugger(m_debugger);
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(1, ThreadState.RUNNING));
        m_synchronizer.receivedEvent(new AttachReply(0, 0));
        m_synchronizer.receivedEvent(new AttachReply(0, 1));
        m_synchronizer.receivedEvent(new AuthenticationFailedReply());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComplete2() throws DebugExceptionWrapper {
        final CHistoryStringBuilder builder = new CHistoryStringBuilder();
        builder.setDebugger(m_debugger);
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(1, ThreadState.RUNNING));
        m_synchronizer.receivedEvent(new BreakpointConditionSetReply(0, 0));
        m_synchronizer.receivedEvent(new BreakpointConditionSetReply(0, 1));
        try {
            m_synchronizer.receivedEvent(new BreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], false, false)))))));
            Assert.fail();
        } catch (final IllegalStateException e) {
        }
        m_synchronizer.receivedEvent(new BreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], true, false)))))));
        m_breakpointManager.addBreakpoints(REGULAR, BREAKPOINT_ADDRESS);
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 0))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 1))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 0))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.BreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 1))));
        m_breakpointManager.removeBreakpoints(REGULAR, BREAKPOINT_ADDRESS);
        m_synchronizer.receivedEvent(new DebuggerClosedUnexpectedlyReply());
        m_synchronizer.receivedEvent(new DetachReply(0, 0));
        m_synchronizer.receivedEvent(new DetachReply(0, 1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComplete3() throws MaybeNullException, MessageParserException, DebugExceptionWrapper, IOException, ParserConfigurationException, SAXException {
        final CHistoryStringBuilder builder = new CHistoryStringBuilder();
        builder.setDebugger(m_debugger);
        m_debugger.connect();
        m_synchronizer.receivedEvent(DebuggerMessageBuilder.buildProcessStartReply(mockMemoryModule));
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, Lists.newArrayList(new RegisterDescription("eax", 4, true), new RegisterDescription("ebx", 4, false)), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(1, ThreadState.RUNNING));
        m_synchronizer.receivedEvent(new BreakpointConditionSetReply(0, 0));
        m_synchronizer.receivedEvent(new BreakpointConditionSetReply(0, 1));
        m_synchronizer.receivedEvent(new EchoBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], false, false)))))));
        m_synchronizer.receivedEvent(new EchoBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], true, false)))))));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        getProcessManager().getThread(0).setCurrentAddress(new RelocatedAddress(new CAddress(0)));
        getBreakpointManager().addBreakpoints(ECHO, BREAKPOINT_ADDRESS);
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 0))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 1))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 0))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 1))));
        m_synchronizer.receivedEvent(new ExceptionOccurredReply(0, 0, 0, 0, new RelocatedAddress(new CAddress(0)), "Test exception"));
        m_synchronizer.receivedEvent(new HaltReply(0, 0, 0));
        m_synchronizer.receivedEvent(new HaltReply(0, 0, 1));
        m_synchronizer.receivedEvent(new ListFilesReply(0, 0, RemoteFileSystem.parse("<foo></foo>".getBytes())));
        m_synchronizer.receivedEvent(new ListFilesReply(0, 1, null));
        m_synchronizer.receivedEvent(new ListProcessesReply(0, 0, ProcessList.parse("<foo></foo>".getBytes())));
        m_synchronizer.receivedEvent(new ListProcessesReply(0, 1, null));
        m_synchronizer.receivedEvent(new MemoryMapReply(0, 0, new MemoryMap(new ArrayList<com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection>())));
        m_synchronizer.receivedEvent(new MemoryMapReply(0, 1, null));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ModuleLoadedReply(0, 0, new MemoryModule("XXX", "YYYXXX", new RelocatedAddress(new CAddress(0)), 0), new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(123, ThreadState.SUSPENDED)));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.ModuleUnloadedReply(0, 0, new MemoryModule("XXX", "YYYXXX", new RelocatedAddress(new CAddress(0)), 0)));
        m_synchronizer.receivedEvent(new ProcessClosedReply(0, 0));
        m_synchronizer.receivedEvent(new ReadMemoryReply(0, 0, new CAddress(0), new byte[8]));
        m_synchronizer.receivedEvent(new ReadMemoryReply(0, 1, null, null));
        m_synchronizer.receivedEvent(new RegistersReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(new com.google.security.zynamics.zylib.types.lists.FilledList<ThreadRegisters>())));
        m_synchronizer.receivedEvent(new RegistersReply(0, 1, null));
        m_synchronizer.receivedEvent(new RequestTargetReply(0, 0));
        m_synchronizer.receivedEvent(new RequestTargetReply(0, 1));
        m_synchronizer.receivedEvent(new ResumeReply(0, 0));
        m_synchronizer.receivedEvent(new ResumeReply(0, 1));
        m_debugger.connect();
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        getProcessManager().getThread(0).setCurrentAddress(new RelocatedAddress(new CAddress(0)));
        m_synchronizer.receivedEvent(new ResumeThreadReply(0, 0, 0));
        m_synchronizer.receivedEvent(new ResumeThreadReply(0, 1, 0));
        m_synchronizer.receivedEvent(new SearchReply(0, 0, new CAddress(0)));
        m_synchronizer.receivedEvent(new SearchReply(0, 1, null));
        m_synchronizer.receivedEvent(new SelectFileReply(0, 0));
        m_synchronizer.receivedEvent(new SelectFileReply(0, 1));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        getProcessManager().getThread(0).setCurrentAddress(new RelocatedAddress(new CAddress(0)));
        m_synchronizer.receivedEvent(new SetRegisterReply(0, 0, 0, 0));
        m_synchronizer.receivedEvent(new SetRegisterReply(0, 1, 0, 0));
        m_synchronizer.receivedEvent(new SingleStepReply(0, 0, 0, new RelocatedAddress(new CAddress(0)), new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(new com.google.security.zynamics.zylib.types.lists.FilledList<ThreadRegisters>())));
        m_synchronizer.receivedEvent(new SingleStepReply(0, 1, 0, new RelocatedAddress(new CAddress(0)), new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(new com.google.security.zynamics.zylib.types.lists.FilledList<ThreadRegisters>())));
        m_synchronizer.receivedEvent(new StepBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], false, false)))))));
        m_synchronizer.receivedEvent(new StepBreakpointHitReply(0, 0, 1, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(1, Lists.newArrayList(new RegisterValue("eip", BigInteger.ONE, new byte[0], true, false)))))));
        getBreakpointManager().addBreakpoints(STEP, BREAKPOINT_ADDRESS);
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointSetReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 0))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointSetReply(0, 1, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 1))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointsRemovedReply(0, 0, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 0))));
        m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.StepBreakpointsRemovedReply(0, 1, Lists.newArrayList(new com.google.security.zynamics.zylib.general.Pair<RelocatedAddress, Integer>(BREAKPOINT_ADDRESS_RELOC, 1))));
        m_synchronizer.receivedEvent(new SuspendThreadReply(0, 0, 0));
        m_synchronizer.receivedEvent(new SuspendThreadReply(0, 1, 0));
        m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, TargetInformationParser.parse("<foo><size>32</size><registers></registers><options></options></foo>".getBytes())));
        m_synchronizer.receivedEvent(new TargetInformationReply(0, 1, null));
        getProcessManager().addThread(new com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread(0, ThreadState.RUNNING));
        m_synchronizer.receivedEvent(new ThreadClosedReply(0, 0, 0));
        m_synchronizer.receivedEvent(new ThreadClosedReply(0, 1, 0));
        m_synchronizer.receivedEvent(new ThreadCreatedReply(0, 0, 0, ThreadState.RUNNING));
        m_synchronizer.receivedEvent(new ThreadCreatedReply(0, 1, 0, null));
        m_synchronizer.receivedEvent(new ValidateMemoryReply(0, 0, new CAddress(0), new CAddress(0)));
        m_synchronizer.receivedEvent(new ValidateMemoryReply(0, 1, null, null));
        m_synchronizer.receivedEvent(new WriteMemoryReply(0, 0));
        m_synchronizer.receivedEvent(new WriteMemoryReply(0, 1));
        m_synchronizer.receivedEvent(new TerminateReply(0, 0));
        m_synchronizer.receivedEvent(new TerminateReply(0, 1));
    }
}

