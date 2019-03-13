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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.Project;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CProjectNodeTest {
    private MockDatabase m_database;

    private CProject m_project;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final JTree m_tree = new JTree();

    @Test
    public void testChangingName() throws CouldntSaveDataException {
        final CProjectNode node = new CProjectNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, new com.google.security.zynamics.binnavi.disassembly.CProjectContainer(m_database, m_project));
        Assert.assertEquals("Name (0)", node.toString());
        m_project.getConfiguration().setName("Hannes");
        Assert.assertEquals("Hannes (0)", node.toString());
    }

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CProjectNode node = new CProjectNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, new com.google.security.zynamics.binnavi.disassembly.CProjectContainer(m_database, m_project));
        node.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testLoaded() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_project.load();
        final CProjectNode node = new CProjectNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, new com.google.security.zynamics.binnavi.disassembly.CProjectContainer(m_database, m_project));
        Assert.assertEquals("Name (1)", node.toString());
        final CAddressSpace addressSpace = m_project.getContent().createAddressSpace("Fark");
        Assert.assertEquals("Name (2)", node.toString());
        m_project.getContent().removeAddressSpace(addressSpace);
        Assert.assertEquals("Name (1)", node.toString());
        node.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testUnloaded() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CProjectNode node = new CProjectNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, new com.google.security.zynamics.binnavi.disassembly.CProjectContainer(m_database, m_project));
        Assert.assertEquals("Name (0)", node.toString());
        m_project.load();
        Assert.assertEquals("Name (1)", node.toString());
        final CAddressSpace addressSpace = m_project.getContent().createAddressSpace("Fark");
        Assert.assertEquals("Name (2)", node.toString());
        m_project.getContent().removeAddressSpace(addressSpace);
        Assert.assertEquals("Name (1)", node.toString());
        node.dispose();
        final LinkedHashSet<?> viewListeners = ((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(addressSpace, "m_listeners"), "m_listeners")));
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(viewListeners.isEmpty());
    }

    @Test
    public void testUnloadedClosed() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CProjectNode node = new CProjectNode(m_tree, new DefaultMutableTreeNode(), m_database, m_project, new com.google.security.zynamics.binnavi.disassembly.CProjectContainer(m_database, m_project));
        Assert.assertEquals("Name (0)", node.toString());
        m_project.load();
        Assert.assertEquals("Name (1)", node.toString());
        final CAddressSpace addressSpace = m_project.getContent().createAddressSpace("Fark");
        Assert.assertEquals("Name (2)", node.toString());
        m_project.getContent().removeAddressSpace(addressSpace);
        Assert.assertEquals("Name (1)", node.toString());
        m_project.close();
        node.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
    }
}

