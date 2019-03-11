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
package com.google.security.zynamics.binnavi.Gui.Debug.MemoryPanel;


import AddressMode.BIT32;
import AddressMode.BIT64;
import ThreadState.RUNNING;
import ThreadState.SUSPENDED;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Debug.Connection.MockDebugConnection;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.IGraphModel;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.MockGraphModel;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.Panels.CDebugPerspectiveModel;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.TargetInformationReply;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings;
import com.google.security.zynamics.binnavi.debug.debugger.interfaces.IDebugger;
import com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.gui.JHexPanel.JHexView;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CMemoryViewerSynchronizerTest {
    private CMemoryViewerSynchronizer m_synchronizer;

    private MockDebugger m_debugger;

    private CDebugPerspectiveModel m_model;

    private final JHexView m_hexView = new JHexView();

    @Test
    public void testActiveDebugger() {
        final IGraphModel graphModel = new MockGraphModel();
        final CDebugPerspectiveModel model = new CDebugPerspectiveModel(graphModel);
        final JHexView hexView = new JHexView();
        final CMemoryProvider provider = new CMemoryProvider();
        final MockDebugger debugger = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        model.setActiveDebugger(debugger);
        final CMemoryViewerSynchronizer synchronizer = new CMemoryViewerSynchronizer(hexView, provider, model);
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        Assert.assertEquals(BIT32, hexView.getAddressMode());
        debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(64, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        Assert.assertEquals(BIT64, hexView.getAddressMode());
        synchronizer.dispose();
    }

    @Test
    public void testLifeCycle() throws DebugExceptionWrapper {
        m_debugger.connect();
        getProcessManager().setAttached(true);
        getProcessManager().setAttached(true);
        final MockMemoryViewerSynchronizerListener listener = new MockMemoryViewerSynchronizerListener();
        m_synchronizer.addListener(listener);
        m_model.setActiveMemoryAddress(new CAddress(256), false);
        Assert.assertEquals("requestedUnsectionedAddress;", listener.events);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(511)))));
        Assert.assertEquals("requestedUnsectionedAddress;", listener.events);
        m_model.setActiveMemoryAddress(new CAddress(256), false);
        Assert.assertEquals(256, m_hexView.getCurrentOffset());
        Assert.assertEquals("requestedUnsectionedAddress;", listener.events);
        m_synchronizer.removeListener(listener);
        getProcessManager().setAttached(false);
        m_synchronizer.dispose();
    }

    /**
     * This test makes sure that the synchronizer can handle new threads.
     *
     * Fix for Case 2036: Listener issue in the memory viewer
     */
    @Test
    public void testNewThread() {
        final IGraphModel graphModel = new MockGraphModel();
        final CDebugPerspectiveModel model = new CDebugPerspectiveModel(graphModel);
        final JHexView hexView = new JHexView();
        final CMemoryProvider provider = new CMemoryProvider();
        model.setActiveDebugger(m_debugger);
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        getProcessManager().setActiveThread(thread);
        final CMemoryViewerSynchronizer synchronizer = new CMemoryViewerSynchronizer(hexView, provider, model);
        synchronizer.dispose();
    }

    @Test
    public void testNoDebugger() {
        m_model.setActiveDebugger(m_debugger);
        m_debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(32, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        Assert.assertEquals(BIT32, m_hexView.getAddressMode());
        m_debugger.connection.m_synchronizer.receivedEvent(new TargetInformationReply(0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(64, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 1, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false))));
        Assert.assertEquals(BIT64, m_hexView.getAddressMode());
        m_synchronizer.dispose();
    }

    @Test
    public void testResizeSection() throws DebugExceptionWrapper {
        m_debugger.connect();
        getProcessManager().setAttached(true);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(511)))));
        m_model.setActiveMemoryAddress(new CAddress(256), false);
        Assert.assertEquals(256, m_hexView.getCurrentOffset());
        Assert.assertEquals(256, m_hexView.getData().getDataLength());
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(511)), new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(1280), new CAddress(1535)))));
        Assert.assertEquals(256, m_hexView.getCurrentOffset());
        Assert.assertEquals(256, m_hexView.getData().getDataLength());
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(0), new CAddress(767)), new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(1280), new CAddress(1535)))));
        Assert.assertEquals(256, m_hexView.getCurrentOffset());
        Assert.assertEquals(768, m_hexView.getData().getDataLength());
        final MockMemoryViewerSynchronizerListener listener = new MockMemoryViewerSynchronizerListener();
        m_synchronizer.addListener(listener);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(0), new CAddress(255)), new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(1280), new CAddress(1535)))));
        Assert.assertEquals(0, m_hexView.getCurrentOffset());
        Assert.assertEquals(256, m_hexView.getData().getDataLength());
        Assert.assertEquals("addressTurnedInvalid;", listener.events);
        m_synchronizer.removeListener(listener);
        getProcessManager().setAttached(false);
        m_synchronizer.dispose();
    }

    @Test
    public void testSwitchDebuggers() throws DebugExceptionWrapper {
        m_debugger.connect();
        getProcessManager().setAttached(true);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(511)))));
        m_model.setActiveMemoryAddress(new CAddress(256), false);
        Assert.assertEquals(256, m_hexView.getCurrentOffset());
        Assert.assertEquals(256, m_hexView.getData().getDataLength());
        final IDebugger debugger2 = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        debugger2.connect();
        debugger2.getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(16), new CAddress(80)))));
        m_model.setActiveDebugger(debugger2);
        Assert.assertEquals(16, m_hexView.getCurrentOffset());
        Assert.assertEquals(65, m_hexView.getData().getDataLength());
        getProcessManager().setAttached(false);
        m_synchronizer.dispose();
        debugger2.close();
    }

    @Test
    public void testSwitchSection() throws DebugExceptionWrapper {
        m_debugger.connect();
        getProcessManager().setAttached(true);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(511)), new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(512), new CAddress(1279)))));
        m_model.setActiveMemoryAddress(new CAddress(1024), false);
        Assert.assertEquals(1024, m_hexView.getCurrentOffset());
        Assert.assertEquals(768, m_hexView.getData().getDataLength());
        m_model.setActiveMemoryAddress(new CAddress(256), false);
        Assert.assertEquals(256, m_hexView.getCurrentOffset());
        Assert.assertEquals(256, m_hexView.getData().getDataLength());
        getProcessManager().setAttached(false);
        m_synchronizer.dispose();
    }

    @Test
    public void testThreads() throws DebugExceptionWrapper {
        m_debugger.connect();
        getProcessManager().setAttached(true);
        final MockMemoryViewerSynchronizerListener listener = new MockMemoryViewerSynchronizerListener();
        m_synchronizer.addListener(listener);
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.RUNNING);
        getProcessManager().addThread(thread);
        getProcessManager().setActiveThread(thread);
        thread.setState(SUSPENDED);
        thread.setState(RUNNING);
        final MockDebugger debugger2 = new MockDebugger(new ModuleTargetSettings(CommonTestObjects.MODULE));
        m_model.setActiveDebugger(debugger2);
        m_model.setActiveDebugger(m_debugger);
        getProcessManager().removeThread(thread);
        getProcessManager().setAttached(false);
        m_synchronizer.removeListener(listener);
        m_synchronizer.dispose();
    }
}

