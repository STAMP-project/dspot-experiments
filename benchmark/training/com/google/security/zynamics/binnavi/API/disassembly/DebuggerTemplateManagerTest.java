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
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplateManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class DebuggerTemplateManagerTest {
    @Test
    public void testConstructor() {
        final DebuggerTemplateManager internalManager = new DebuggerTemplateManager(new MockSqlProvider());
        final DebuggerTemplateManager manager = new DebuggerTemplateManager(internalManager);
        Assert.assertEquals("Debugger Template Manager (0 templates)", manager.toString());
    }

    @Test
    public void testDispose() throws CouldntDeleteException, CouldntSaveDataException {
        final MockDebuggerTemplateManagerListener listener = new MockDebuggerTemplateManagerListener();
        final DebuggerTemplateManager internalManager = new DebuggerTemplateManager(new MockSqlProvider());
        final DebuggerTemplateManager manager = new DebuggerTemplateManager(internalManager);
        manager.addListener(listener);
        final DebuggerTemplate template1 = manager.createDebuggerTemplate("Hannes", "Host", 123);
        @SuppressWarnings("unused")
        final DebuggerTemplate template2 = manager.createDebuggerTemplate("Hannes", "Host", 123);
        manager.deleteDebugger(template1);
        manager.dispose();
    }

    @Test
    public void testLifeCycle() throws CouldntDeleteException, CouldntSaveDataException {
        final MockDebuggerTemplateManagerListener listener = new MockDebuggerTemplateManagerListener();
        final DebuggerTemplateManager internalManager = new DebuggerTemplateManager(new MockSqlProvider());
        final DebuggerTemplateManager manager = new DebuggerTemplateManager(internalManager);
        manager.addListener(listener);
        final DebuggerTemplate template = manager.createDebuggerTemplate("Hannes", "Host", 123);
        Assert.assertEquals(1, internalManager.debuggerCount());
        Assert.assertEquals(1, manager.getDebuggerTemplateCount());
        Assert.assertEquals(1, manager.getDebuggerTemplates().size());
        Assert.assertEquals("Hannes", internalManager.getDebugger(0).getName());
        Assert.assertEquals("Host", internalManager.getDebugger(0).getHost());
        Assert.assertEquals(123, internalManager.getDebugger(0).getPort());
        Assert.assertEquals("Hannes", manager.getDebuggerTemplate(0).getName());
        Assert.assertEquals("Host", manager.getDebuggerTemplate(0).getHost());
        Assert.assertEquals(123, manager.getDebuggerTemplate(0).getPort());
        manager.deleteDebugger(template);
        Assert.assertEquals(0, internalManager.debuggerCount());
        Assert.assertEquals(0, manager.getDebuggerTemplateCount());
        Assert.assertEquals("addedDebuggerTemplate;deletedDebuggerTemplate;", listener.events);
        manager.removeListener(listener);
    }
}

