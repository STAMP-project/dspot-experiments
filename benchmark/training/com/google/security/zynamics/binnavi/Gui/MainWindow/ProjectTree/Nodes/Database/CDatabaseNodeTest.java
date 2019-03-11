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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.Database;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.LinkedHashSet;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CDatabaseNodeTest {
    private MockDatabase m_database;

    private CProject m_project;

    @SuppressWarnings("unused")
    private CAddressSpace m_addressSpace;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final JTree m_tree = new JTree();

    @Test
    public void testChangingDescription() {
        final CDatabaseNode node = new CDatabaseNode(m_tree, new DefaultMutableTreeNode(), m_database);
        Assert.assertEquals("Mock Database", node.toString());
        m_database.getConfiguration().setDescription("Hannes");
        Assert.assertEquals("Hannes", node.toString());
    }

    @Test
    public void testClosed() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CDatabaseNode node = new CDatabaseNode(m_tree, new DefaultMutableTreeNode(), m_database);
        m_database.load();
        m_database.close();
        node.dispose();
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testConstructor() {
        final CDatabaseNode node = new CDatabaseNode(m_tree, new DefaultMutableTreeNode(), m_database);
        Assert.assertEquals(m_database, node.getObject());
        Assert.assertNotNull(node.getComponent());
    }

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CDatabaseNode node = new CDatabaseNode(m_tree, new DefaultMutableTreeNode(), m_database);
        node.dispose();
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testLoaded() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_database.load();
        final CDatabaseNode node = new CDatabaseNode(m_tree, new DefaultMutableTreeNode(), m_database);
        node.dispose();
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testUnloaded() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CDatabaseNode node = new CDatabaseNode(m_tree, new DefaultMutableTreeNode(), m_database);
        m_database.load();
        node.dispose();
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
    }
}

