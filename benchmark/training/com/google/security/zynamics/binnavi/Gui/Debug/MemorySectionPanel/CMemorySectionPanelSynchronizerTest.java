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
package com.google.security.zynamics.binnavi.Gui.Debug.MemorySectionPanel;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Connection.MockDebugConnection;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.IGraphModel;
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
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CMemorySectionPanelSynchronizerTest {
    private final CMemorySectionBox m_sectionBox = new CMemorySectionBox();

    private final IGraphModel m_graphModel = new MockGraphModel();

    private final CDebugPerspectiveModel m_model = new CDebugPerspectiveModel(m_graphModel);

    private final CMemorySectionPanelSynchronizer m_synchronizer = new CMemorySectionPanelSynchronizer(m_sectionBox, m_model);

    @Test
    public void testChangingAddress() throws DebugExceptionWrapper {
        // Makes sure to update the combo box when the active address changes
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        m_model.setActiveDebugger(debugger);
        final MemorySection section1 = new MemorySection(new CAddress(4096), new CAddress(8176));
        final MemorySection section2 = new MemorySection(new CAddress(12288), new CAddress(16368));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        Assert.assertEquals(2, m_sectionBox.getItemCount());
        Assert.assertEquals(section1, m_sectionBox.getItemAt(0).getObject());
        Assert.assertEquals(section2, m_sectionBox.getItemAt(1).getObject());
        Assert.assertEquals(section1, m_sectionBox.getSelectedItem().getObject());
        m_model.setActiveMemoryAddress(new CAddress(12288), false);
        Assert.assertEquals(section2, m_sectionBox.getSelectedItem().getObject());
        m_synchronizer.dispose();
        debugger.close();
    }

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
        Assert.assertEquals(2, m_sectionBox.getItemCount());
        Assert.assertEquals(section1, m_sectionBox.getItemAt(0).getObject());
        Assert.assertEquals(section2, m_sectionBox.getItemAt(1).getObject());
        Assert.assertTrue(m_sectionBox.isEnabled());
        debugger.connection.m_synchronizer.receivedEvent(new DetachReply(0, 0));
        Assert.assertEquals(0, m_sectionBox.getItemCount());
        Assert.assertFalse(m_sectionBox.isEnabled());
        m_synchronizer.dispose();
        debugger.close();
    }

    @Test
    public void testReceivedMemoryMap() throws DebugExceptionWrapper {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger.connect();
        m_model.setActiveDebugger(debugger);
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        Assert.assertEquals(2, m_sectionBox.getItemCount());
        Assert.assertEquals(section1, m_sectionBox.getItemAt(0).getObject());
        Assert.assertEquals(section2, m_sectionBox.getItemAt(1).getObject());
        m_synchronizer.dispose();
        debugger.close();
    }

    @Test
    public void testReceiveTargetInformation() throws DebugExceptionWrapper {
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.SUSPENDED);
        getProcessManager().addThread(thread);
        getProcessManager().setActiveThread(thread);
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        debugger.connection.m_synchronizer.receivedEvent(new com.google.security.zynamics.binnavi.debug.connection.packets.replies.MemoryMapReply(0, 0, memoryMap));
        debugger.connect();
        m_model.setActiveDebugger(debugger);
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, true, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        Assert.assertTrue(m_sectionBox.isEnabled());
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        Assert.assertFalse(m_sectionBox.isEnabled());
        m_synchronizer.dispose();
        debugger.close();
    }

    @Test
    public void testSwitchDebugger() throws DebugExceptionWrapper {
        final MemorySection section1 = new MemorySection(new CAddress(256), new CAddress(511));
        final MemorySection section2 = new MemorySection(new CAddress(768), new CAddress(1023));
        final MemoryMap memoryMap = new MemoryMap(Lists.newArrayList(section1, section2));
        final TargetProcessThread thread = new TargetProcessThread(1638, ThreadState.RUNNING);
        final MemoryModule module = new MemoryModule("narf.exe", "C:\\zort\\narf.exe", new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(4096)), 123345);
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
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
        Assert.assertTrue(m_sectionBox.isEnabled());
        m_model.setActiveDebugger(debugger2);
        Assert.assertFalse(m_sectionBox.isEnabled());
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
        Assert.assertFalse(m_sectionBox.isEnabled());
        getProcessManager().addThread(thread);
        Assert.assertFalse(m_sectionBox.isEnabled());
        getProcessManager().setActiveThread(thread);
        Assert.assertTrue(m_sectionBox.isEnabled());
        getProcessManager().setActiveThread(null);
        Assert.assertFalse(m_sectionBox.isEnabled());
        getProcessManager().removeThread(thread);
        Assert.assertFalse(m_sectionBox.isEnabled());
        getProcessManager().setAttached(false);
        m_synchronizer.dispose();
        debugger.close();
    }
}

