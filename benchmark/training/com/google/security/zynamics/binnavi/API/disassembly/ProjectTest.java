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


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.FunctionType;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ProjectTest {
    private Project m_project;

    private CProject m_internalProject;

    private final Date creationDate = new Date();

    private final Date modificationDate = new Date();

    private final MockSqlProvider provider = new MockSqlProvider();

    private final MockDatabase internalDatabase = new MockDatabase(provider);

    private final Database database = new Database(internalDatabase);

    @Test
    public void testConstructor() {
        Assert.assertEquals(database, m_project.getDatabase());
        Assert.assertEquals("Project Name", m_project.getName());
        Assert.assertEquals("Project Description", m_project.getDescription());
        Assert.assertEquals(creationDate, m_project.getCreationDate());
        Assert.assertEquals(modificationDate, m_project.getModificationDate());
        Assert.assertFalse(m_project.isLoaded());
        Assert.assertEquals("Project 'Project Name' [unloaded, 1 address spaces]", m_project.toString());
    }

    @Test
    public void testCreateAddressSpace() throws CouldntLoadDataException, CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.load();
        m_project.addListener(listener);
        final AddressSpace space = m_project.createAddressSpace("Hannes Space");
        space.load();
        final INaviModule nativeModule = new MockModule(provider);
        internalDatabase.getContent().addModule(nativeModule);
        final Module module = ModuleFactory.get(nativeModule, provider);
        module.load();
        space.addModule(module);
        Assert.assertEquals(space, m_project.getAddressSpaces().get(1));
        Assert.assertEquals("Hannes Space", m_internalProject.getContent().getAddressSpaces().get(1).getConfiguration().getName());
        Assert.assertEquals("addedAddressSpace;changedModificationDate;", listener.events);
        Assert.assertEquals(0, m_project.getFunctions().size());
        m_project.removeListener(listener);
    }

    @Test
    public void testCreateView() throws CouldntLoadDataException, CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.load();
        m_project.addListener(listener);
        final View view = m_project.createView("N A", "D A");
        Assert.assertEquals(view, m_project.getViews().get(0));
        Assert.assertEquals("N A", m_internalProject.getContent().getViews().get(0).getName());
        Assert.assertEquals("addedView;", listener.events);
        final View view2 = m_project.createView(view, "N B", "D B");
        Assert.assertEquals(view2, m_project.getViews().get(1));
        Assert.assertEquals("N B", m_internalProject.getContent().getViews().get(1).getName());
        Assert.assertEquals("addedView;addedView;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testDebuggerTemplates() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.load();
        final DebuggerTemplate template = database.getDebuggerTemplateManager().createDebuggerTemplate("Foo", "Bar", 123);
        m_project.addListener(listener);
        m_project.addDebuggerTemplate(template);
        Assert.assertEquals(1, m_project.getDebuggerTemplates().size());
        Assert.assertEquals(template, m_project.getDebuggerTemplates().get(0));
        Assert.assertEquals("addedDebuggerTemplate;changedModificationDate;", listener.events);
        m_project.removeDebuggerTemplate(template);
        Assert.assertEquals(0, m_project.getDebuggerTemplates().size());
        Assert.assertEquals("addedDebuggerTemplate;changedModificationDate;removedDebuggerTemplate;changedModificationDate;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testDeleteAddressSpace() throws CouldntDeleteException, CouldntLoadDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.load();
        m_project.addListener(listener);
        try {
            m_project.deleteAddressSpace(m_project.getAddressSpaces().get(0));
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        m_project.getAddressSpaces().get(0).close();
        m_project.deleteAddressSpace(m_project.getAddressSpaces().get(0));
        Assert.assertTrue(m_project.getAddressSpaces().isEmpty());
        Assert.assertEquals("deletedAddressSpace;changedModificationDate;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testDeleteView() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.load();
        final View view = m_project.createView("N A", "D A");
        final View view2 = m_project.createView(view, "N B", "D B");
        m_project.addListener(listener);
        m_project.deleteView(view);
        Assert.assertEquals(1, m_project.getViews().size());
        Assert.assertEquals(view2, m_project.getViews().get(0));
        Assert.assertEquals("deletedView;changedModificationDate;", listener.events);
        m_project.deleteView(view2);
        Assert.assertEquals(0, m_project.getViews().size());
        Assert.assertEquals("deletedView;changedModificationDate;deletedView;changedModificationDate;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testLifecycle() throws CouldntLoadDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.addListener(listener);
        m_project.load();
        Assert.assertTrue(m_project.isLoaded());
        Assert.assertTrue(m_internalProject.isLoaded());
        Assert.assertEquals("loadedProject;", listener.events);
        Assert.assertEquals("Project 'Project Name' ['']", m_project.toString());
        Assert.assertTrue(m_project.close());
        Assert.assertFalse(m_project.isLoaded());
        Assert.assertFalse(m_internalProject.isLoaded());
        Assert.assertEquals("loadedProject;closingProject;closedProject;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.addListener(listener);
        m_project.setDescription("New Description");
        Assert.assertEquals("New Description", m_project.getDescription());
        Assert.assertEquals("changedDescription;changedModificationDate;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.addListener(listener);
        m_project.setName("New Name");
        Assert.assertEquals("New Name", m_project.getName());
        Assert.assertEquals("changedName;changedModificationDate;", listener.events);
        m_project.removeListener(listener);
    }

    @Test
    public void testTraces() throws CouldntLoadDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockProjectListener listener = new MockProjectListener();
        m_project.load();
        m_project.addListener(listener);
        m_project.getNative().getContent().createTrace("foo", "bar");
        Assert.assertEquals("addedTrace;changedModificationDate;", listener.events);
        Assert.assertEquals(1, m_project.getTraces().size());
        Assert.assertEquals("foo", m_project.getTraces().get(0).getName());
        Assert.assertEquals("bar", m_project.getTraces().get(0).getDescription());
        m_project.getNative().getContent().removeTrace(m_project.getNative().getContent().getTraces().get(0));
        Assert.assertEquals("addedTrace;changedModificationDate;deletedTrace;changedModificationDate;", listener.events);
        Assert.assertTrue(m_project.getTraces().isEmpty());
    }

    @Test
    public void testUnloaded() throws CouldntLoadDataException, CouldntSaveDataException {
        try {
            m_project.createAddressSpace("Hannes");
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_project.createView("", "");
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            final MockSqlProvider provider = new MockSqlProvider();
            final TagManager nodeTagManager = new TagManager(new com.google.security.zynamics.binnavi.Tagging.CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.binnavi.Tagging.CTag(1, "", "", TagType.NODE_TAG, provider))), TagType.NODE_TAG, provider));
            final TagManager viewTagManager = new TagManager(new com.google.security.zynamics.binnavi.Tagging.CTagManager(new com.google.security.zynamics.zylib.types.trees.Tree<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.zylib.types.trees.TreeNode<com.google.security.zynamics.binnavi.Tagging.CTag>(new com.google.security.zynamics.binnavi.Tagging.CTag(1, "", "", TagType.VIEW_TAG, provider))), TagType.VIEW_TAG, provider));
            final View view = new View(m_project, new MockView(), nodeTagManager, viewTagManager);
            m_project.createView(view, "", "");
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_project.getAddressSpaces();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            final MockSqlProvider provider = new MockSqlProvider();
            final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
            final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
            m_project.getFunction(parentFunction);
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_project.getTraces();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
        try {
            m_project.getViews();
            Assert.fail();
        } catch (final IllegalStateException exception) {
        }
    }
}

