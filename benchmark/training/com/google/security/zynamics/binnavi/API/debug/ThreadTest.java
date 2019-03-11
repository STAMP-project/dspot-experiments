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


import ThreadState.Running;
import ThreadState.Suspended;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.debug.models.processmanager.TargetProcessThread;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ThreadState.SUSPENDED;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ThreadTest {
    private final TargetProcessThread m_internalThread = new TargetProcessThread(0, ThreadState.RUNNING);

    private final Thread m_thread = new Thread(m_internalThread);

    @Test
    public void testConstructor() {
        Assert.assertEquals(0, getThreadId());
        Assert.assertEquals(null, getCurrentAddress());
        Assert.assertEquals(0, getRegisters().size());
        Assert.assertEquals(Running, m_thread.getState());
        Assert.assertEquals("Thread (TID: 0)", m_thread.toString());
    }

    @Test
    public void testGetCurrentAddress() {
        final MockThreadListener listener = new MockThreadListener();
        addListener(listener);
        m_internalThread.setCurrentAddress(new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(512)));
        Assert.assertEquals("changedProgramCounter;", listener.events);
        Assert.assertEquals(512, getCurrentAddress().toLong());
        removeListener(listener);
    }

    @Test
    public void testGetRegisters() {
        final MockThreadListener listener = new MockThreadListener();
        addListener(listener);
        m_internalThread.setRegisterValues(Lists.newArrayList(new RegisterValue("eax", BigInteger.TEN, new byte[0], false, false)));
        Assert.assertEquals("changedRegisters;", listener.events);
        Assert.assertEquals(1, getRegisters().size());
        removeListener(listener);
    }

    @Test
    public void testGetState() {
        final MockThreadListener listener = new MockThreadListener();
        addListener(listener);
        m_internalThread.setState(SUSPENDED);
        Assert.assertEquals("changedState;", listener.events);
        Assert.assertEquals(Suspended, m_thread.getState());
        removeListener(listener);
    }
}

