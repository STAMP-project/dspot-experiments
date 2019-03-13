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
package com.google.security.zynamics.binnavi.Gui.Debug.StackPanel;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Connection.MockDebugConnection;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.MockGraphModel;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.Panels.CDebugPerspectiveModel;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.DetachReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ProcessStartReply;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.TargetInformationReply;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryModule;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection;
import com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.gui.JStackView.JStackView;
import java.math.BigInteger;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CStackViewSynchronizerTest {
    private final CDebugPerspectiveModel m_model = new CDebugPerspectiveModel(new MockGraphModel());

    private final CStackMemoryProvider m_stackModel = new CStackMemoryProvider();

    private final JStackView m_stackView = new JStackView(m_stackModel);

    private final CStackViewSynchronizer m_synchronizer = new CStackViewSynchronizer(m_stackView, m_stackModel, m_model);

    @Test
    public void testDetach() throws DebugExceptionWrapper {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.SUSPENDED);
        getProcessManager().addThread(thread);
        getProcessManager().setActiveThread(thread);
        debugger.connect();
        m_model.setActiveDebugger(debugger);
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, true, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        Assert.assertTrue(m_stackView.isEnabled());
        debugger.connection.m_synchronizer.receivedEvent(new DetachReply(0, 0));
        Assert.assertFalse(m_stackView.isEnabled());
        m_synchronizer.dispose();
        debugger.close();
    }

    /**
     * This test makes sure that freshly created threads have listeners attached.
     *
     * Test for 2035: Listener issue in the stack view synchronizer
     *
     * @throws DebugExceptionWrapper
     * 		
     */
    @Test
    public void testNewThread() throws DebugExceptionWrapper {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        getProcessManager().setAttached(true);
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, true, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        m_model.setActiveDebugger(debugger);
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        m_synchronizer.dispose();
        debugger.close();
    }

    /**
     * This test makes sure that the stack view is filled properly when switching from no debugger to
     * a debugger with an active thread.
     *
     *  Fix for Case 2034: Stack view is not initialized correctly when opening graph windows with
     * connected debuggers
     *
     * @throws DebugExceptionWrapper
     * 		
     */
    @Test
    public void testNewThread2() throws DebugExceptionWrapper {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        getProcessManager().setAttached(true);
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, true, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.RUNNING);
        thread.setRegisterValues(Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(272), new byte[0], false, true)));
        getProcessManager().addThread(thread);
        getProcessManager().setActiveThread(thread);
        m_model.setActiveDebugger(debugger);
        Assert.assertEquals(272, m_stackModel.getStackPointer());
        m_synchronizer.dispose();
        debugger.close();
    }

    @Test
    public void testSwitchDebugger() throws DebugExceptionWrapper {
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        final TargetProcessThread thread = new TargetProcessThread(1638, ThreadState.RUNNING);
        final MemoryModule module = new MemoryModule("narf.exe", "C:\\zort\\narf.exe", new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(4096)), 123345);
        debugger.connect();
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, true, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        debugger.connection.m_synchronizer.receivedEvent(new ProcessStartReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.processmanager.ProcessStart(thread, module)));
        final MockDebugger debugger2 = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger2.connect();
        debugger2.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        debugger2.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        debugger2.connection.m_synchronizer.receivedEvent(new ProcessStartReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.processmanager.ProcessStart(thread, module)));
        m_model.setActiveDebugger(debugger);
        Assert.assertTrue(m_stackView.isEnabled());
        m_model.setActiveDebugger(debugger2);
        Assert.assertTrue(m_stackView.isEnabled());
        m_synchronizer.dispose();
        debugger.close();
        debugger2.close();
    }

    @Test
    public void testThreads() throws DebugExceptionWrapper {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        getProcessManager().setAttached(true);
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, true, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.RUNNING);
        m_model.setActiveDebugger(debugger);
        getProcessManager().setActiveThread(null);
        Assert.assertFalse(m_stackView.isEnabled());
        getProcessManager().addThread(thread);
        Assert.assertFalse(m_stackView.isEnabled());
        getProcessManager().setActiveThread(thread);
        Assert.assertTrue(m_stackView.isEnabled());
        getProcessManager().setActiveThread(null);
        Assert.assertFalse(m_stackView.isEnabled());
        getProcessManager().removeThread(thread);
        Assert.assertFalse(m_stackView.isEnabled());
        getProcessManager().setAttached(false);
        m_synchronizer.dispose();
        debugger.close();
    }
}

