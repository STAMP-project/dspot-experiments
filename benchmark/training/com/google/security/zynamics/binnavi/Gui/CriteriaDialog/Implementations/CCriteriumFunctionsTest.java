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
package com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Implementations;


import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.CConditionBox;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.CCriteriaFactory;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.And.CAndCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.ICriteriumCreator;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.NodeColor.CColorCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.Or.COrCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.Selection.CSelectionCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.Tag.CTagCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.Text.CTextCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionModel.CCriteriumTree;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionModel.CCriteriumTreeNode;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionTree.JCriteriumTree;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import java.util.List;
import javax.swing.tree.TreePath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CCriteriumFunctionsTest {
    private ZyGraph m_graph;

    @Test
    public void findNode() {
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        final List<ICriteriumCreator> criteria = cCriteriaFactory.getConditions();
        final CConditionBox box = new CConditionBox(criteria);
        final JCriteriumTree jtree = new JCriteriumTree(cCriteriumTree, criteria);
        final CColorCriterium colorado = new CColorCriterium(m_graph);
        final CCriteriumTreeNode child = new CCriteriumTreeNode(colorado);
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final TreePath path = new TreePath(getFirstChild());
        jtree.setSelectionPath(path);
        box.setSelectedIndex(6);
        CCriteriumFunctions.appendCriterium(jtree, cCriteriumTree, box);
        Assert.assertEquals(null, CCriteriumFunctions.findNode(cCriteriumTree.getRoot(), new CTextCriterium()));
        Assert.assertEquals(child, CCriteriumFunctions.findNode(cCriteriumTree.getRoot(), colorado));
    }

    @Test
    public void testAppendOne() {
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        CCriteriumFunctions.appendCriterium(cCriteriumTree, cCriteriumTree.getRoot().getChildren().get(0), new CAndCriterium());
        Assert.assertTrue(((cCriteriumTree.getRoot().getChildren().get(0).getChildren().get(0).getCriterium()) instanceof CAndCriterium));
    }

    @Test
    public void testAppendTwoNoPath() {
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriaFactory cCriteriaFactory = new CCriteriaFactory(m_graph, null, null);
        final List<ICriteriumCreator> criteria = cCriteriaFactory.getConditions();
        final CConditionBox box = new CConditionBox(criteria);
        final JCriteriumTree jtree = new JCriteriumTree(cCriteriumTree, criteria);
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final TreePath path = new TreePath(getFirstChild());
        jtree.setSelectionPath(path);
        box.setSelectedIndex(6);
        CCriteriumFunctions.appendCriterium(jtree, cCriteriumTree, box);
        Assert.assertTrue(((cCriteriumTree.getRoot().getChildren().get(0).getChildren().get(0).getCriterium()) instanceof CSelectionCriterium));
        Assert.assertFalse(((cCriteriumTree.getRoot().getChildren().get(0).getChildren().get(0).getCriterium()) instanceof CTagCriterium));
    }

    @Test
    public void testInsertCriterium() {
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        CCriteriumFunctions.insertCriterium(cCriteriumTree, cCriteriumTree.getRoot(), new COrCriterium());
        Assert.assertTrue(((cCriteriumTree.getRoot().getChildren().get(0).getCriterium()) instanceof COrCriterium));
    }

    @Test
    public void testRemove() {
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final CCriteriumTreeNode child2 = new CCriteriumTreeNode(new COrCriterium());
        cCriteriumTree.appendNode(child, child2);
        CCriteriumFunctions.remove(cCriteriumTree, child);
        Assert.assertEquals(0, cCriteriumTree.getRoot().getChildren().size());
    }

    @Test
    public void testRemoveAll() {
        final CCriteriumTree cCriteriumTree = new CCriteriumTree();
        final CCriteriumTreeNode child = new CCriteriumTreeNode(new CColorCriterium(m_graph));
        cCriteriumTree.appendNode(cCriteriumTree.getRoot(), child);
        final CCriteriumTreeNode child2 = new CCriteriumTreeNode(new COrCriterium());
        cCriteriumTree.appendNode(child, child2);
        CCriteriumFunctions.removeAll(cCriteriumTree);
        Assert.assertEquals(0, cCriteriumTree.getRoot().getChildren().size());
    }
}

