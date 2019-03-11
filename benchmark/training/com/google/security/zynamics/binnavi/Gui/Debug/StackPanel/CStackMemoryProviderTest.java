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


import AddressMode.BIT64;
import StackDataLayout.Bytes;
import StackDataLayout.Dwords;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerOptions;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.math.BigInteger;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CStackMemoryProviderTest {
    private MockDebugger m_debugger;

    private final CStackMemoryProvider m_provider = new CStackMemoryProvider();

    @Test
    public void testAddressMode() throws DebugExceptionWrapper {
        m_provider.setDebugger(m_debugger);
        m_debugger.connect();
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.SUSPENDED);
        thread.setRegisterValues(Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(291), new byte[0], false, true)));
        m_provider.setActiveThread(thread);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(384)))));
        getProcessManager().getMemory().store(288, new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0 });
        // Bytes + 32
        Assert.assertEquals("01000000", m_provider.getElement(288));
        m_provider.setDataLayout(Dwords);
        // Dwords + 32
        Assert.assertEquals("00000001", m_provider.getElement(288));
        m_provider.setAddressMode(BIT64);
        // Dwords + 64
        Assert.assertEquals("0000000000000001", m_provider.getElement(288));
        m_provider.setDataLayout(Bytes);
        // Bytes + 64
        Assert.assertEquals("0100000000000000", m_provider.getElement(288));
    }

    @Test
    public void testElement() throws DebugExceptionWrapper {
        m_provider.setDebugger(m_debugger);
        m_debugger.connect();
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.SUSPENDED);
        thread.setRegisterValues(Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(291), new byte[0], false, true)));
        m_provider.setActiveThread(thread);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(384)))));
        getProcessManager().getMemory().store(256, new byte[]{ 18, 52, 86, 120, ((byte) (154)), ((byte) (188)), ((byte) (222)), ((byte) (240)) });
        Assert.assertEquals("12345678", m_provider.getElement(256));
        m_provider.setDataLayout(Dwords);
        Assert.assertEquals("78563412", m_provider.getElement(256));
    }

    @Test
    public void testInitial() throws DebugExceptionWrapper {
        m_provider.setDebugger(m_debugger);
        Assert.assertEquals(0, m_provider.getNumberOfEntries());
        Assert.assertFalse(m_provider.keepTrying());
        Assert.assertEquals((-1), m_provider.getStartAddress());
        Assert.assertEquals((-1), m_provider.getStackPointer());
        m_debugger.connect();
        Assert.assertEquals(0, m_provider.getNumberOfEntries());
        Assert.assertTrue(m_provider.keepTrying());
    }

    @Test
    public void testNumberOfEntries() throws DebugExceptionWrapper {
        m_provider.setDebugger(m_debugger);
        getProcessManager().setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        Assert.assertEquals(0, m_provider.getNumberOfEntries());
        m_debugger.connect();
        Assert.assertEquals(0, m_provider.getNumberOfEntries());
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.SUSPENDED);
        thread.setRegisterValues(Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(291), new byte[0], false, true)));
        m_provider.setActiveThread(thread);
        Assert.assertEquals(0, m_provider.getNumberOfEntries());
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(384)))));
        Assert.assertEquals((128 / 4), m_provider.getNumberOfEntries());
    }

    @Test
    public void testStackPointer() throws DebugExceptionWrapper {
        m_provider.setDebugger(m_debugger);
        Assert.assertEquals((-1), m_provider.getStackPointer());
        m_debugger.connect();
        Assert.assertEquals((-1), m_provider.getStackPointer());
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.SUSPENDED);
        thread.setRegisterValues(Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(291), new byte[0], false, true)));
        m_provider.setActiveThread(thread);
        Assert.assertEquals(291, m_provider.getStackPointer());
    }

    @Test
    public void testStartAddress() throws DebugExceptionWrapper {
        m_provider.setDebugger(m_debugger);
        getProcessManager().setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(256), new CAddress(512)))));
        Assert.assertEquals((-1), m_provider.getStartAddress());
        m_debugger.connect();
        Assert.assertEquals((-1), m_provider.getStartAddress());
        final TargetProcessThread thread = new TargetProcessThread(0, ThreadState.SUSPENDED);
        thread.setRegisterValues(Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(291), new byte[0], false, true)));
        Assert.assertEquals((-1), m_provider.getStartAddress());
        m_provider.setActiveThread(thread);
        Assert.assertEquals(256, m_provider.getStartAddress());
    }
}

