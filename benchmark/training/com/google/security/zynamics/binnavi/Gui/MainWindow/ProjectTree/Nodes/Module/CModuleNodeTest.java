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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.Module;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModuleContainer;
import com.google.security.zynamics.binnavi.disassembly.views.CView;
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
public final class CModuleNodeTest {
    private MockDatabase m_database;

    private CModule m_module;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final JTree m_tree = new JTree();

    @Test
    public void testChangingName() throws CouldntSaveDataException {
        final CModuleNode node = new CModuleNode(m_tree, new DefaultMutableTreeNode(), m_database, m_module, new CModuleContainer(m_database, m_module));
        Assert.assertEquals("Name (55/66)", node.toString());
        m_module.getConfiguration().setName("Hannes");
        Assert.assertEquals("Hannes (55/66)", node.toString());
    }

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CModuleContainer container = new CModuleContainer(m_database, m_module);
        final CModuleNode node = new CModuleNode(m_tree, new DefaultMutableTreeNode(), m_database, m_module, container);
        node.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_module, "m_listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testLoaded() throws CouldntDeleteException, CouldntLoadDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_module.load();
        final CModuleNode node = new CModuleNode(m_tree, new DefaultMutableTreeNode(), m_database, m_module, new CModuleContainer(m_database, m_module));
        Assert.assertEquals("Name (1/0)", node.toString());
        final CView view = m_module.getContent().getViewContainer().createView("Foo", "Bar");
        Assert.assertEquals("Name (1/1)", node.toString());
        m_module.getContent().getViewContainer().deleteView(view);
        Assert.assertEquals("Name (1/0)", node.toString());
        node.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_module, "m_listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testUnloaded() throws CouldntDeleteException, CouldntLoadDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CModuleNode node = new CModuleNode(m_tree, new DefaultMutableTreeNode(), m_database, m_module, new CModuleContainer(m_database, m_module));
        Assert.assertEquals("Name (55/66)", node.toString());
        m_module.load();
        Assert.assertEquals("Name (1/0)", node.toString());
        final CView view = m_module.getContent().getViewContainer().createView("Foo", "Bar");
        Assert.assertEquals("Name (1/1)", node.toString());
        m_module.getContent().getViewContainer().deleteView(view);
        Assert.assertEquals("Name (1/0)", node.toString());
        node.dispose();
        final LinkedHashSet<?> viewListeners = ((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(view, "m_listeners"), "m_listeners")));
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_module, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(viewListeners.isEmpty());
    }

    @Test
    public void testUnloadedClosed() throws CouldntDeleteException, CouldntLoadDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CModuleNode node = new CModuleNode(m_tree, new DefaultMutableTreeNode(), m_database, m_module, new CModuleContainer(m_database, m_module));
        Assert.assertEquals("Name (55/66)", node.toString());
        m_module.load();
        Assert.assertEquals("Name (1/0)", node.toString());
        final CView view = m_module.getContent().getViewContainer().createView("Foo", "Bar");
        Assert.assertEquals("Name (1/1)", node.toString());
        m_module.getContent().getViewContainer().deleteView(view);
        Assert.assertEquals("Name (1/0)", node.toString());
        m_module.close();
        node.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_module, "m_listeners"), "m_listeners"))).isEmpty());
    }
}

