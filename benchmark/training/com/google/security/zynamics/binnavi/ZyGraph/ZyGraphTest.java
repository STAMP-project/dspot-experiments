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
package com.google.security.zynamics.binnavi.ZyGraph;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.disassembly.CCodeNode;
import com.google.security.zynamics.binnavi.disassembly.CFunctionNode;
import com.google.security.zynamics.binnavi.disassembly.CNaviViewEdge;
import com.google.security.zynamics.binnavi.disassembly.CTextNode;
import com.google.security.zynamics.binnavi.disassembly.INaviInstruction;
import com.google.security.zynamics.binnavi.disassembly.INaviViewNode;
import com.google.security.zynamics.binnavi.disassembly.MockFunction;
import com.google.security.zynamics.binnavi.disassembly.MockInstruction;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.general.Pair;
import com.google.security.zynamics.zylib.gui.zygraph.functions.NodeFunctions;
import com.google.security.zynamics.zylib.gui.zygraph.helpers.GraphHelpers;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ZyGraphTest {
    private ZyGraph m_graph;

    private INaviView m_view;

    private SQLProvider m_provider;

    private MockModule m_module;

    private MockFunction m_function;

    @Test
    public void testAddedCodeNode() {
        Assert.assertEquals(7, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        final MockFunction function = new MockFunction(m_provider);
        final List<INaviInstruction> instructions = Lists.newArrayList(((INaviInstruction) (new MockInstruction(new CAddress(1193046), "mov", new ArrayList<com.google.security.zynamics.binnavi.disassembly.COperandTree>(), null, m_module))));
        final CCodeNode codeNode = m_graph.getRawView().getContent().createCodeNode(function, instructions);
        Assert.assertEquals(8, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(97, m_graph.getRawView().getNodeCount());
        final List<NaviNode> nodes = GraphHelpers.getNodes(m_graph);
        final NaviNode cnn = searchNode(nodes, codeNode);
        Assert.assertEquals(codeNode, cnn.getRawNode());
        Assert.assertTrue(codeNode.isVisible());
        Assert.assertEquals(codeNode.isVisible(), cnn.isVisible());
        codeNode.setVisible(false);
        Assert.assertFalse(codeNode.isVisible());
        Assert.assertEquals(codeNode.isVisible(), cnn.isVisible());
        codeNode.setVisible(true);
        Assert.assertTrue(codeNode.isVisible());
        Assert.assertEquals(codeNode.isVisible(), cnn.isVisible());
        Assert.assertFalse(codeNode.isSelected());
        Assert.assertEquals(codeNode.isSelected(), cnn.isSelected());
        codeNode.setSelected(false);
        Assert.assertFalse(codeNode.isSelected());
        Assert.assertEquals(codeNode.isSelected(), cnn.isSelected());
        codeNode.setSelected(true);
        Assert.assertTrue(codeNode.isSelected());
        Assert.assertEquals(codeNode.isSelected(), cnn.isSelected());
        Assert.assertEquals(codeNode.getColor(), cnn.getRealizer().getFillColor());
        codeNode.setColor(Color.GREEN);
        Assert.assertEquals(Color.GREEN, codeNode.getColor());
        Assert.assertEquals(codeNode.getColor(), cnn.getRealizer().getFillColor());
        codeNode.setX(100);
        Assert.assertEquals(100, codeNode.getX(), 0.1);
        Assert.assertEquals(codeNode.getX(), cnn.getX(), 0.1);
        codeNode.setY(200);
        Assert.assertEquals(200, codeNode.getY(), 0.1);
        Assert.assertEquals(codeNode.getY(), cnn.getY(), 0.1);
    }

    @Test
    public void testAddedEdgeCheckColor() {
        final Pair<CNaviViewEdge, NaviEdge> p = createEdgeForAddedEdgeTests();
        final CNaviViewEdge edge = p.first();
        final NaviEdge cnn = p.second();
        Assert.assertEquals(edge.getColor(), cnn.getRealizerLineColor());
        edge.setColor(Color.GREEN);
        Assert.assertEquals(Color.GREEN, edge.getColor());
        Assert.assertEquals(edge.getColor(), cnn.getRealizerLineColor());
    }

    @Test
    public void testAddedEdgeCheckSelection() {
        final Pair<CNaviViewEdge, NaviEdge> p = createEdgeForAddedEdgeTests();
        final CNaviViewEdge edge = p.first();
        final NaviEdge cnn = p.second();
        Assert.assertFalse(edge.isSelected());
        Assert.assertEquals(edge.isSelected(), cnn.isSelected());
        edge.setSelected(false);
        Assert.assertFalse(edge.isSelected());
        Assert.assertEquals(edge.isSelected(), cnn.isSelected());
        edge.setSelected(true);
        Assert.assertTrue(edge.isSelected());
        Assert.assertEquals(edge.isSelected(), cnn.isSelected());
    }

    @Test
    public void testAddedEdgeCheckVisibility() {
        final Pair<CNaviViewEdge, NaviEdge> p = createEdgeForAddedEdgeTests();
        final CNaviViewEdge edge = p.first();
        final NaviEdge cnn = p.second();
        Assert.assertTrue(edge.isVisible());
        Assert.assertEquals(edge.isVisible(), cnn.isVisible());
        edge.setVisible(false);
        Assert.assertFalse(edge.isVisible());
        Assert.assertEquals(edge.isVisible(), cnn.isVisible());
        edge.setVisible(true);
        Assert.assertTrue(edge.isVisible());
        Assert.assertEquals(edge.isVisible(), cnn.isVisible());
    }

    @Test
    public void testAddedFunctionNode() {
        Assert.assertEquals(7, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        final CFunctionNode functionNode = m_graph.getRawView().getContent().createFunctionNode(new MockFunction(m_provider));
        Assert.assertEquals(8, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(97, m_graph.getRawView().getNodeCount());
        final List<NaviNode> nodes = GraphHelpers.getNodes(m_graph);
        final NaviNode cnn = searchNode(nodes, functionNode);
        Assert.assertTrue(functionNode.isVisible());
        Assert.assertEquals(functionNode.isVisible(), cnn.isVisible());
        functionNode.setVisible(false);
        Assert.assertFalse(functionNode.isVisible());
        Assert.assertEquals(functionNode.isVisible(), cnn.isVisible());
        functionNode.setVisible(true);
        Assert.assertTrue(functionNode.isVisible());
        Assert.assertEquals(functionNode.isVisible(), cnn.isVisible());
        Assert.assertFalse(functionNode.isSelected());
        Assert.assertEquals(functionNode.isSelected(), cnn.isSelected());
        functionNode.setSelected(false);
        Assert.assertFalse(functionNode.isSelected());
        Assert.assertEquals(functionNode.isSelected(), cnn.isSelected());
        functionNode.setSelected(true);
        Assert.assertTrue(functionNode.isSelected());
        Assert.assertEquals(functionNode.isSelected(), cnn.isSelected());
        Assert.assertEquals(functionNode.getColor(), cnn.getRealizer().getFillColor());
        functionNode.setColor(Color.GREEN);
        Assert.assertEquals(Color.GREEN, functionNode.getColor());
        Assert.assertEquals(functionNode.getColor(), cnn.getRealizer().getFillColor());
        functionNode.setX(100);
        Assert.assertEquals(100, functionNode.getX(), 0.1);
        Assert.assertEquals(functionNode.getX(), cnn.getX(), 0.1);
        functionNode.setY(200);
        Assert.assertEquals(200, functionNode.getY(), 0.1);
        Assert.assertEquals(functionNode.getY(), cnn.getY(), 0.1);
    }

    @Test
    public void testAddedTextNode() {
        Assert.assertEquals(7, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        final CTextNode textNode = m_graph.getRawView().getContent().createTextNode(Lists.<IComment>newArrayList(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(null, CommonTestObjects.TEST_USER_1, null, "Hannes")));
        Assert.assertEquals(8, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(97, m_graph.getRawView().getNodeCount());
        final List<NaviNode> nodes = GraphHelpers.getNodes(m_graph);
        final NaviNode cnn = searchNode(nodes, textNode);
        Assert.assertTrue(textNode.isVisible());
        Assert.assertEquals(textNode.isVisible(), cnn.isVisible());
        textNode.setVisible(false);
        Assert.assertFalse(textNode.isVisible());
        Assert.assertEquals(textNode.isVisible(), cnn.isVisible());
        textNode.setVisible(true);
        Assert.assertTrue(textNode.isVisible());
        Assert.assertEquals(textNode.isVisible(), cnn.isVisible());
        Assert.assertFalse(textNode.isSelected());
        Assert.assertEquals(textNode.isSelected(), cnn.isSelected());
        textNode.setSelected(false);
        Assert.assertFalse(textNode.isSelected());
        Assert.assertEquals(textNode.isSelected(), cnn.isSelected());
        textNode.setSelected(true);
        Assert.assertTrue(textNode.isSelected());
        Assert.assertEquals(textNode.isSelected(), cnn.isSelected());
        Assert.assertEquals(textNode.getColor(), cnn.getRealizer().getFillColor());
        textNode.setColor(Color.GREEN);
        Assert.assertEquals(Color.GREEN, textNode.getColor());
        Assert.assertEquals(textNode.getColor(), cnn.getRealizer().getFillColor());
        textNode.setX(100);
        Assert.assertEquals(100, textNode.getX(), 0.1);
        Assert.assertEquals(textNode.getX(), cnn.getX(), 0.1);
        textNode.setY(200);
        Assert.assertEquals(200, textNode.getY(), 0.1);
        Assert.assertEquals(textNode.getY(), cnn.getY(), 0.1);
    }

    @Test
    public void testDeleteEdge() {
        m_view.getContent().deleteEdge(m_view.getGraph().getEdges().get(0));
    }

    @Test
    public void testDeleteNode() {
        Assert.assertEquals(7, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        final List<NaviNode> nodes = GraphHelpers.getNodes(m_graph);
        final INaviViewNode oldChild = m_view.getGraph().getNodes().get(1);
        Assert.assertEquals(1, searchNode(nodes, oldChild).getParents().size());
        m_view.getContent().deleteNode(m_view.getGraph().getNodes().get(0));
        Assert.assertEquals(6, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(95, m_graph.getRawView().getNodeCount());
        Assert.assertEquals(0, searchNode(nodes, oldChild).getParents().size());
        m_view.getContent().deleteNodes(Lists.newArrayList(m_view.getGraph().getNodes().get(0), m_view.getGraph().getNodes().get(1)));
        Assert.assertEquals(4, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(93, m_graph.getRawView().getNodeCount());
    }

    @Test
    public void testNode() {
        final List<NaviNode> nodes = GraphHelpers.getNodes(m_graph);
        final NaviNode cnn = nodes.get(0);
        final CCodeNode codeNode = ((CCodeNode) (cnn.getRawNode()));
        Assert.assertEquals(codeNode.isVisible(), cnn.isVisible());
        codeNode.setVisible(false);
        Assert.assertFalse(codeNode.isVisible());
        Assert.assertEquals(codeNode.isVisible(), cnn.isVisible());
        codeNode.setVisible(true);
        Assert.assertTrue(codeNode.isVisible());
        Assert.assertEquals(codeNode.isVisible(), cnn.isVisible());
        Assert.assertFalse(codeNode.isSelected());
        Assert.assertEquals(codeNode.isSelected(), cnn.isSelected());
        codeNode.setSelected(false);
        Assert.assertFalse(codeNode.isSelected());
        Assert.assertEquals(codeNode.isSelected(), cnn.isSelected());
        codeNode.setSelected(true);
        Assert.assertTrue(codeNode.isSelected());
        Assert.assertEquals(codeNode.isSelected(), cnn.isSelected());
        Assert.assertEquals(codeNode.getColor(), cnn.getRealizer().getFillColor());
        codeNode.setColor(Color.GREEN);
        Assert.assertEquals(Color.GREEN, codeNode.getColor());
        Assert.assertEquals(codeNode.getColor(), cnn.getRealizer().getFillColor());
        codeNode.setX(100);
        Assert.assertEquals(100, codeNode.getX(), 0.1);
        Assert.assertEquals(codeNode.getX(), cnn.getX(), 0.1);
        codeNode.setY(200);
        Assert.assertEquals(200, codeNode.getY(), 0.1);
        Assert.assertEquals(codeNode.getY(), cnn.getY(), 0.1);
    }

    @Test
    public void testVisibility() {
        Assert.assertEquals(7, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        int visibilityCounter = 0;
        int totalCounter = 0;
        for (final NaviNode node : GraphHelpers.getNodes(m_graph)) {
            Assert.assertEquals(node.isVisible(), node.getRawNode().isVisible());
            if (node.isVisible()) {
                visibilityCounter++;
            }
            totalCounter++;
        }
        Assert.assertEquals(7, visibilityCounter);
        Assert.assertEquals(96, totalCounter);
    }
}

