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


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryModule;
import com.google.security.zynamics.binnavi.debug.models.processmanager.ProcessManager;
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
public final class ProcessTest {
    @Test
    public void testGetMemory() {
        final MockProcessListener listener = new MockProcessListener();
        final ProcessManager manager = new ProcessManager();
        final Process process = new Process(manager);
        addListener(listener);
        final Memory m1 = getMemory();
        final Memory m2 = getMemory();
        Assert.assertEquals(m1, m2);
        removeListener(listener);
    }

    @Test
    public void testGetMemoryMap() {
        final MockProcessListener listener = new MockProcessListener();
        final ProcessManager manager = new ProcessManager();
        final Process process = new Process(manager);
        addListener(listener);
        Assert.assertEquals(0, getMemoryMap().getSections().size());
        manager.setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(0), new CAddress(256)))));
        Assert.assertEquals("changedMemoryMap;", listener.events);
        Assert.assertEquals(1, getMemoryMap().getSections().size());
        final MemoryMap m1 = process.getMemoryMap();
        final MemoryMap m2 = process.getMemoryMap();
        Assert.assertEquals(m1, m2);
        removeListener(listener);
    }

    @Test
    public void testGetTargetInformation() {
        final MockProcessListener listener = new MockProcessListener();
        final ProcessManager manager = new ProcessManager();
        final Process process = new Process(manager);
        addListener(listener);
        Assert.assertNull(getTargetInformation());
        manager.setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        final TargetInformation t1 = process.getTargetInformation();
        final TargetInformation t2 = process.getTargetInformation();
        Assert.assertNotNull(t1);
        Assert.assertEquals(t1, t2);
        Assert.assertEquals("changedTargetInformation;", listener.events);
        Assert.assertEquals(5, getTargetInformation().getAddressSize());
        Assert.assertEquals(false, getTargetInformation().canTerminate());
        removeListener(listener);
    }

    @Test
    public void testLifeCycle() {
        final MockProcessListener listener = new MockProcessListener();
        final ProcessManager manager = new ProcessManager();
        final Process process = new Process(manager);
        addListener(listener);
        manager.setAttached(true);
        Assert.assertEquals("attached;", listener.events);
        removeListener(listener);
        manager.setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        manager.addThread(new TargetProcessThread(0, ThreadState.RUNNING));
        manager.addModule(new MemoryModule("Hannes", "C:\\Hannes.dll", new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(256)), 256));
        manager.setMemoryMap(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemoryMap(Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection(new CAddress(0), new CAddress(256)))));
        manager.getMemory().store(0, new byte[]{ 0, 1, 2, 3 });
        addListener(listener);
        manager.setTargetInformation(new com.google.security.zynamics.binnavi.debug.models.targetinformation.TargetInformation(5, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterDescription>(), new DebuggerOptions(false, false, false, false, false, false, false, false, false, false, 12, 0, new ArrayList<com.google.security.zynamics.binnavi.debug.models.targetinformation.DebuggerException>(), false, false, false)));
        Assert.assertEquals(0, getThreads().size());
        Assert.assertEquals(0, getModules().size());
        Assert.assertEquals(0, getMemoryMap().getSections().size());
        Assert.assertEquals(false, process.getMemory().hasData(0, 4));
        manager.setAttached(false);
        Assert.assertEquals("attached;changedTargetInformation;detached;", listener.events);
        Assert.assertEquals(0, getThreads().size());
        Assert.assertEquals(0, getModules().size());
        Assert.assertEquals(null, getTargetInformation());
        Assert.assertEquals(0, getMemoryMap().getSections().size());
        Assert.assertEquals(false, process.getMemory().hasData(0, 4));
        removeListener(listener);
    }

    @Test
    public void testModules() {
        final MockProcessListener listener = new MockProcessListener();
        final ProcessManager manager = new ProcessManager();
        manager.addModule(new MemoryModule("Hannes", "C:\\Hannes.dll", new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(256)), 256));
        final Process process = new Process(manager);
        Assert.assertEquals(1, getModules().size());
        addListener(listener);
        final MemoryModule dll = new MemoryModule("Foobert.dll", "C:\\Foobert.dll", new com.google.security.zynamics.binnavi.disassembly.RelocatedAddress(new CAddress(256)), 256);
        manager.addModule(dll);
        Assert.assertEquals("addedModule/Foobert.dll;", listener.events);
        Assert.assertEquals(2, getModules().size());
        manager.removeModule(dll);
        Assert.assertEquals("addedModule/Foobert.dll;removedModule/Foobert.dll;", listener.events);
        Assert.assertEquals(1, getModules().size());
        removeListener(listener);
    }

    @Test
    public void testThreads() {
        final MockProcessListener listener = new MockProcessListener();
        final ProcessManager manager = new ProcessManager();
        manager.addThread(new TargetProcessThread(0, ThreadState.RUNNING));
        final Process process = new Process(manager);
        addListener(listener);
        final Thread thread1 = getThreads().get(0);
        Assert.assertEquals(0, getThreadId());
        final TargetProcessThread thread = new TargetProcessThread(1, ThreadState.RUNNING);
        manager.addThread(thread);
        Assert.assertEquals("addedThread/1;", listener.events);
        Assert.assertEquals(2, getThreads().size());
        manager.removeThread(thread);
        Assert.assertEquals("addedThread/1;removedThread/1;", listener.events);
        Assert.assertEquals(1, getThreads().size());
        removeListener(listener);
    }
}

