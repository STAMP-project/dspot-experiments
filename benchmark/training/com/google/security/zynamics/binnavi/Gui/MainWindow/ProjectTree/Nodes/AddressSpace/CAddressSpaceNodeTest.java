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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.AddressSpace;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.ZyGraph.Builders.ZyGraphBuilderManager;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.views.IViewContainer;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.LinkedHashSet;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CAddressSpaceNodeTest {
    private MockDatabase m_database;

    private CProject m_project;

    private CAddressSpace m_addressSpace;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final JTree m_tree = new JTree();

    private IViewContainer m_container;

    @Test
    public void testChangingName() throws CouldntSaveDataException {
        final CAddressSpaceNode node = new CAddressSpaceNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, m_addressSpace, m_container);
        Assert.assertEquals("Address Space (?)", node.toString());
        m_addressSpace.getConfiguration().setName("Hannes");
        Assert.assertEquals("Hannes (?)", node.toString());
    }

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CAddressSpaceNode node = new CAddressSpaceNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, m_addressSpace, m_container);
        node.dispose();
        m_container.dispose();
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_addressSpace, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(ZyGraphBuilderManager.instance(), "m_listeners"), "m_listeners"))) == null));
    }

    @Test
    public void testLoaded() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_addressSpace.load();
        final CAddressSpaceNode node = new CAddressSpaceNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, m_addressSpace, m_container);
        Assert.assertEquals("Address Space (0)", node.toString());
        final MockModule unloadedModule = new MockModule(m_provider, false);
        m_addressSpace.getContent().addModule(new MockModule(m_provider, true));
        m_addressSpace.getContent().addModule(unloadedModule);
        Assert.assertEquals("Address Space (2)", node.toString());
        Assert.assertEquals(2, node.getChildCount());
        m_addressSpace.getContent().removeModule(unloadedModule);
        Assert.assertEquals("Address Space (1)", node.toString());
        Assert.assertEquals(1, node.getChildCount());
        node.dispose();
        m_container.dispose();
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_addressSpace, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(ZyGraphBuilderManager.instance(), "m_listeners"), "m_listeners"))) == null));
    }

    @Test
    public void testUnloaded() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CAddressSpaceNode node = new CAddressSpaceNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, m_addressSpace, m_container);
        Assert.assertEquals("Address Space (?)", node.toString());
        m_addressSpace.load();
        Assert.assertEquals("Address Space (0)", node.toString());
        final MockModule unloadedModule = new MockModule(m_provider, false);
        m_addressSpace.getContent().addModule(new MockModule(m_provider, true));
        m_addressSpace.getContent().addModule(unloadedModule);
        Assert.assertEquals("Address Space (2)", node.toString());
        Assert.assertEquals(2, node.getChildCount());
        m_addressSpace.getContent().removeModule(unloadedModule);
        Assert.assertEquals("Address Space (1)", node.toString());
        Assert.assertEquals(1, node.getChildCount());
        node.dispose();
        m_container.dispose();
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_addressSpace, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(ZyGraphBuilderManager.instance(), "m_listeners"), "m_listeners"))) == null));
    }

    @Test
    public void testUnloadedClosed() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CAddressSpaceNode node = new CAddressSpaceNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, m_addressSpace, m_container);
        Assert.assertEquals("Address Space (?)", node.toString());
        m_addressSpace.load();
        Assert.assertEquals("Address Space (0)", node.toString());
        final MockModule unloadedModule = new MockModule(m_provider, false);
        m_addressSpace.getContent().addModule(new MockModule(m_provider, true));
        m_addressSpace.getContent().addModule(unloadedModule);
        Assert.assertEquals("Address Space (2)", node.toString());
        Assert.assertEquals(2, node.getChildCount());
        m_addressSpace.getContent().removeModule(unloadedModule);
        Assert.assertEquals("Address Space (1)", node.toString());
        Assert.assertEquals(1, node.getChildCount());
        m_addressSpace.close();
        node.dispose();
        m_container.dispose();
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_addressSpace, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(ZyGraphBuilderManager.instance(), "m_listeners"), "m_listeners"))) == null));
    }
}

