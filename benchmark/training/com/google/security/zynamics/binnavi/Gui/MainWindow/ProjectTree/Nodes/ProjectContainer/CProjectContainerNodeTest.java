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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.ProjectContainer;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.ModuleContainer.CModuleContainerNode;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.swing.JTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CProjectContainerNodeTest {
    private MockDatabase m_database;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final JTree m_tree = new JTree();

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CModuleContainerNode node = new CModuleContainerNode(m_tree, m_database);
        node.dispose();
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database.getContent().getDebuggerTemplateManager(), "listeners"), "m_listeners"))) == null));
    }

    @Test
    public void testLoaded() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CProject project1 = new CProject(123, "Project 1", "Comment", new Date(), new Date(), 55, new ArrayList<com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate>(), m_provider);
        final CProject project2 = new CProject(123, "Project 2", "Comment", new Date(), new Date(), 55, new ArrayList<com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate>(), m_provider);
        m_database.addProject(project1);
        m_database.addProject(project2);
        final CProjectContainerNode node = new CProjectContainerNode(m_tree, m_database);
        Assert.assertEquals("Projects (2)", node.toString());
        Assert.assertEquals(2, node.getChildCount());
        Assert.assertEquals("Project 1 (55)", node.getChildAt(0).toString());
        Assert.assertEquals("Project 2 (55)", node.getChildAt(1).toString());
        node.dispose();
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database.getContent().getDebuggerTemplateManager(), "listeners"), "m_listeners"))) == null));
    }

    @Test
    public void testUnloaded() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CProject project1 = new CProject(123, "Project 1", "Comment", new Date(), new Date(), 55, new ArrayList<com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate>(), m_provider);
        final CProject project2 = new CProject(123, "Project 2", "Comment", new Date(), new Date(), 55, new ArrayList<com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate>(), m_provider);
        final CProjectContainerNode node = new CProjectContainerNode(m_tree, m_database);
        Assert.assertEquals("Projects (0)", node.toString());
        Assert.assertEquals(0, node.getChildCount());
        m_database.addProject(project1);
        m_database.addProject(project2);
        Assert.assertEquals("Projects (2)", node.toString());
        Assert.assertEquals(2, node.getChildCount());
        Assert.assertEquals("Project 1 (55)", node.getChildAt(0).toString());
        Assert.assertEquals("Project 2 (55)", node.getChildAt(1).toString());
        m_database.getContent().delete(project1);
        Assert.assertEquals("Projects (1)", node.toString());
        Assert.assertEquals(1, node.getChildCount());
        Assert.assertEquals("Project 2 (55)", node.getChildAt(0).toString());
        node.dispose();
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database.getContent().getDebuggerTemplateManager(), "listeners"), "m_listeners"))) == null));
    }
}

