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
package com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions;


import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.CCriteriaFactory;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.And.CAndCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionModel.CCachedExpressionTree;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionModel.CCriteriumTree;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionModel.CCriteriumTreeNode;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionTree.JCriteriumTreeModel;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import javax.swing.JTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class JCriteriumTreeModelTest {
    private ZyGraph m_graph;

    @SuppressWarnings("unused")
    private CCachedExpressionTree m_tree;

    @Test
    public void testAppendNode() {
        final JTree tree = new JTree();
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        final JCriteriumTreeModel jCriterumTreeModel = new JCriteriumTreeModel(tree, cCriteriumTree, cCriteriaFactory.getConditions());
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        Assert.assertEquals(1, getChildCount());
        Assert.assertEquals(child.getCriterium(), getCriterium());
    }

    @Test
    public void testEmpty() {
        final JTree tree = new JTree();
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        @SuppressWarnings("unused")
        final JCriteriumTreeModel jCriterumTreeModel = new JCriteriumTreeModel(tree, cCriteriumTree, cCriteriaFactory.getConditions());
    }

    @Test
    public void testInsertNode() {
        final JTree tree = new JTree();
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        final JCriteriumTreeModel jCriterumTreeModel = new JCriteriumTreeModel(tree, cCriteriumTree, cCriteriaFactory.getConditions());
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final CCriteriumTreeNode child2 = new CCriteriumTreeNode(new CAndCriterium());
        cCriteriumTree.insertNode(cCriteriumTree.getRoot(), child2);
        Assert.assertEquals(1, getChildCount());
        Assert.assertEquals(child2.getCriterium(), getCriterium());
        Assert.assertEquals(child.getCriterium(), getCriterium());
    }

    @Test
    public void testRemoveAll() {
        final JTree tree = new JTree();
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        final JCriteriumTreeModel jCriterumTreeModel = new JCriteriumTreeModel(tree, cCriteriumTree, cCriteriaFactory.getConditions());
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final CCriteriumTreeNode child2 = new CCriteriumTreeNode(new CAndCriterium());
        cCriteriumTree.insertNode(cCriteriumTree.getRoot(), child2);
        final CCriteriumTreeNode child3 = new CCriteriumTreeNode(new com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium(m_graph));
        cCriteriumTree.appendNode(child2, child3);
        cCriteriumTree.clear();
        Assert.assertEquals(0, getChildCount());
    }

    @Test
    public void testRemoveNode() {
        final JTree tree = new JTree();
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        final JCriteriumTreeModel jCriterumTreeModel = new JCriteriumTreeModel(tree, cCriteriumTree, cCriteriaFactory.getConditions());
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final CCriteriumTreeNode child2 = new CCriteriumTreeNode(new CAndCriterium());
        cCriteriumTree.insertNode(cCriteriumTree.getRoot(), child2);
        final CCriteriumTreeNode child3 = new CCriteriumTreeNode(new com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium(m_graph));
        cCriteriumTree.appendNode(child2, child3);
        cCriteriumTree.remove(child);
        Assert.assertEquals(1, getChildCount());
        Assert.assertEquals(1, getChildCount());
        Assert.assertEquals(child2.getCriterium(), getCriterium());
    }
}

