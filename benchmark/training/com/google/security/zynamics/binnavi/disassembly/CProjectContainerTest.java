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
package com.google.security.zynamics.binnavi.disassembly;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.MockAddressSpaceListener;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.views.CView;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.zylib.disassembly.MockAddress;
import com.google.security.zynamics.zylib.types.lists.IFilledList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CProjectContainerTest {
    private MockSqlProvider m_provider;

    private MockModule m_module;

    private MockDatabase m_database;

    private CProject m_project;

    private CAddressSpace m_space;

    private MockFunction m_function;

    private DebuggerTemplate m_debugger;

    @SuppressWarnings("unused")
    private CView m_view1;

    @SuppressWarnings("unused")
    private CView m_view2;

    private MockAddressSpaceListener m_listener;

    @Test
    public void testContainsModule() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertFalse(container.containsModule(new MockModule()));
        Assert.assertTrue(container.containsModule(m_module));
        try {
            container.containsModule(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void testCreateView() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        try {
            container.createView(null, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            container.createView("seppel", null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        final INaviView view = container.createView("foo", "berT");
        Assert.assertNotNull(view);
    }

    @Test
    public void testDeleteView() throws CouldntDeleteException {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        final INaviView view = container.createView("foo", "berT");
        Assert.assertNotNull(view);
        try {
            container.deleteView(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        container.deleteView(view);
        Assert.assertEquals(2, container.getViews().size());
    }

    @Test
    public void testDispose() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project, m_space);
        container.dispose();
    }

    @Test
    public void testGetAddressSpaces() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project, m_space);
        Assert.assertEquals(2, container.getAddressSpaces().size());
    }

    @Test
    public void testGetDatabase() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertEquals(m_database, container.getDatabase());
    }

    @Test
    public void testGetDebuggerProvider() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertNotNull(container.getDebuggerProvider());
    }

    @Test
    public void testGetFunction() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        final INaviView view = container.createView("foo", "berT");
        try {
            container.getFunction(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        Assert.assertNull(container.getFunction(view));
    }

    @Test
    public void testGetFunctions() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertEquals(0, container.getFunctions().size());
    }

    @Test
    public void testGetModules() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertEquals(1, container.getModules().size());
    }

    @Test
    public void testGetName() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertEquals("Name", container.getName());
    }

    @Test
    public void testGetTaggedViews() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertEquals(0, container.getTaggedViews().size());
    }

    // @Test
    // public void testGetTaggedViews2()
    // {
    // final CProjectContainer container = new CProjectContainer(m_database, m_project);
    // assertEquals(0, container.getTaggedViews()));
    // }
    @Test
    public void testGetTraceProvider() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertNotNull(container.getTraceProvider());
    }

    @Test
    public void testGetView() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        @SuppressWarnings("unused")
        final INaviView view = container.createView("foo", "berT");
        Assert.assertNull(container.getView(m_function));
    }

    @Test
    public void testGetViews() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        final INaviView view = container.createView("view1", "information");
        Assert.assertEquals(3, container.getViews().size());
        Assert.assertTrue(container.getViews().contains(view));
    }

    @Test
    public void testGetViewsWithAddresses() throws CouldntLoadDataException {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        @SuppressWarnings("unused")
        final INaviView view = container.createView("view1", "information");
        final UnrelocatedAddress address = new UnrelocatedAddress(new MockAddress());
        final IFilledList<UnrelocatedAddress> addresses = new com.google.security.zynamics.zylib.types.lists.FilledList<UnrelocatedAddress>();
        addresses.add(address);
        try {
            container.getViewsWithAddresses(null, true);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        // assertNull(container.getViewsWithAddresses(addresses, true));
    }

    @Test
    public void testIsLoaded() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        Assert.assertTrue(container.isLoaded());
    }

    @Test
    public void testListenersSomehow() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        @SuppressWarnings("unused")
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        final DebuggerTemplate template = new DebuggerTemplate(2, "bla", "horst", 2222, m_provider);
        m_space.getConfiguration().setDebuggerTemplate(template);
        m_space.getConfiguration().setDebuggerTemplate(null);
        Assert.assertEquals(1, m_space.getContent().getModules().size());
        m_space.getContent().removeModule(m_module);
        m_space.getContent().addModule(m_module);
        m_module.load();
        m_project.load();
        m_space.close();
        m_space.load();
    }

    @Test
    public void testRemoveListener() {
        final CProjectContainer container = new CProjectContainer(m_database, m_project);
        try {
            container.removeListener(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }
}

