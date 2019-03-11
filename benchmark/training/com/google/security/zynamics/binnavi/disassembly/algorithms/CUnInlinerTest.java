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
package com.google.security.zynamics.binnavi.disassembly.algorithms;


import EdgeType.ENTER_INLINED_FUNCTION;
import EdgeType.JUMP_CONDITIONAL_FALSE;
import EdgeType.JUMP_CONDITIONAL_TRUE;
import EdgeType.JUMP_UNCONDITIONAL;
import EdgeType.JUMP_UNCONDITIONAL_LOOP;
import EdgeType.LEAVE_INLINED_FUNCTION;
import com.google.common.collect.Iterables;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.disassembly.CCodeNode;
import com.google.security.zynamics.binnavi.disassembly.CNaviViewEdge;
import com.google.security.zynamics.binnavi.disassembly.CTextNode;
import com.google.security.zynamics.binnavi.disassembly.INaviCodeNode;
import com.google.security.zynamics.binnavi.disassembly.INaviEdge;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviViewNode;
import com.google.security.zynamics.binnavi.disassembly.MockFunction;
import com.google.security.zynamics.binnavi.disassembly.MockInstruction;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.zylib.gui.zygraph.edges.EdgeType;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CUnInlinerTest {
    private SQLProvider m_provider;

    @Test
    public void testOneBlock() {
        final List<INaviViewNode> nodes = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviViewNode>();
        final List<INaviEdge> edges = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviEdge>();
        final INaviFunction mockFunction = new MockFunction();
        final CCodeNode node = new CCodeNode(0, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        nodes.add(node);
        final MockView view = new MockView(nodes, edges, m_provider);
        CUnInliner.unInline(view, node);
    }

    @Test
    public void testUninlineSingleBlock() {
        final List<INaviViewNode> nodes = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviViewNode>();
        final List<INaviEdge> edges = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviEdge>();
        final INaviFunction mockFunction = new MockFunction(m_provider);
        final INaviFunction mockFunction2 = new MockFunction(m_provider);
        final MockInstruction mi1 = new MockInstruction(291);
        final MockInstruction mi2 = new MockInstruction(292);
        final MockInstruction mi3 = new MockInstruction(293);
        final CCodeNode upperNode = new CCodeNode(0, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode inlinedNode = new CCodeNode(0, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction2, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode lowerNode = new CCodeNode(0, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CTextNode textNode = new CTextNode(0, 0, 0, 0, 0, Color.RED, false, true, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), null, m_provider);
        upperNode.addInstruction(mi1, null);
        inlinedNode.addInstruction(mi2, null);
        lowerNode.addInstruction(mi3, null);
        nodes.add(upperNode);
        nodes.add(inlinedNode);
        nodes.add(textNode);
        nodes.add(lowerNode);
        final CNaviViewEdge enteringEdge = new CNaviViewEdge(0, upperNode, inlinedNode, EdgeType.ENTER_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge leavingEdge = new CNaviViewEdge(0, inlinedNode, lowerNode, EdgeType.LEAVE_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge textEdge = new CNaviViewEdge(0, textNode, inlinedNode, EdgeType.TEXTNODE_EDGE, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        edges.add(enteringEdge);
        edges.add(leavingEdge);
        edges.add(textEdge);
        connect(upperNode, inlinedNode, enteringEdge);
        connect(inlinedNode, lowerNode, leavingEdge);
        connect(textNode, inlinedNode, textEdge);
        final MockView view = new MockView(nodes, edges, m_provider);
        CUnInliner.unInline(view, inlinedNode);
        Assert.assertEquals(1, view.getNodeCount());
        Assert.assertEquals(0, view.getEdgeCount());
        final INaviCodeNode cnode = ((INaviCodeNode) (view.getGraph().getNodes().get(0)));
        Assert.assertEquals(2, Iterables.size(cnode.getInstructions()));
        Assert.assertEquals(mi1.toString(), Iterables.get(cnode.getInstructions(), 0).toString());
        Assert.assertEquals(mi3.toString(), Iterables.get(cnode.getInstructions(), 1).toString());
    }

    @Test
    public void testUninlineSingleBlockConnectNeighbors() {
        final List<INaviViewNode> nodes = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviViewNode>();
        final List<INaviEdge> edges = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviEdge>();
        final INaviFunction mockFunction = new MockFunction(m_provider);
        final INaviFunction mockFunction2 = new MockFunction(m_provider);
        final MockInstruction mi1 = new MockInstruction(291);
        final MockInstruction mi2 = new MockInstruction(292);
        final MockInstruction mi3 = new MockInstruction(293);
        final CCodeNode parentNode = new CCodeNode(1, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode upperNode = new CCodeNode(2, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode inlinedNode = new CCodeNode(3, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction2, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode lowerNode = new CCodeNode(4, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode childNode1 = new CCodeNode(5, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode childNode2 = new CCodeNode(6, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        upperNode.addInstruction(mi1, null);
        inlinedNode.addInstruction(mi2, null);
        lowerNode.addInstruction(mi3, null);
        nodes.add(parentNode);
        nodes.add(upperNode);
        nodes.add(inlinedNode);
        nodes.add(lowerNode);
        nodes.add(childNode1);
        nodes.add(childNode2);
        final CNaviViewEdge parentEdge = new CNaviViewEdge(0, parentNode, upperNode, EdgeType.JUMP_UNCONDITIONAL, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge enteringEdge = new CNaviViewEdge(1, upperNode, inlinedNode, EdgeType.ENTER_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge leavingEdge = new CNaviViewEdge(2, inlinedNode, lowerNode, EdgeType.LEAVE_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge childEdge1 = new CNaviViewEdge(3, lowerNode, childNode1, EdgeType.JUMP_CONDITIONAL_FALSE, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge childEdge2 = new CNaviViewEdge(4, lowerNode, childNode2, EdgeType.JUMP_CONDITIONAL_TRUE, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        edges.add(parentEdge);
        edges.add(enteringEdge);
        edges.add(leavingEdge);
        edges.add(childEdge1);
        edges.add(childEdge2);
        connect(parentNode, upperNode, parentEdge);
        connect(upperNode, inlinedNode, enteringEdge);
        connect(inlinedNode, lowerNode, leavingEdge);
        connect(lowerNode, childNode1, childEdge1);
        connect(lowerNode, childNode2, childEdge2);
        final MockView view = new MockView(nodes, edges, m_provider);
        CUnInliner.unInline(view, inlinedNode);
        Assert.assertEquals(4, view.getNodeCount());
        Assert.assertEquals(3, view.getEdgeCount());
        Assert.assertEquals(JUMP_UNCONDITIONAL, view.getGraph().getEdges().get(0).getType());
        Assert.assertEquals(JUMP_CONDITIONAL_FALSE, view.getGraph().getEdges().get(1).getType());
        Assert.assertEquals(JUMP_CONDITIONAL_TRUE, view.getGraph().getEdges().get(2).getType());
        final INaviCodeNode cnode = ((INaviCodeNode) (view.getGraph().getNodes().get(3)));
        Assert.assertEquals(2, Iterables.size(cnode.getInstructions()));
        Assert.assertEquals(mi1.toString(), Iterables.get(cnode.getInstructions(), 0).toString());
        Assert.assertEquals(mi3.toString(), Iterables.get(cnode.getInstructions(), 1).toString());
    }

    @Test
    public void testUninlineSingleBlockLoop() {
        final List<INaviViewNode> nodes = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviViewNode>();
        final List<INaviEdge> edges = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviEdge>();
        final INaviFunction mockFunction = new MockFunction(m_provider);
        final INaviFunction mockFunction2 = new MockFunction(m_provider);
        final MockInstruction mi1 = new MockInstruction(273);
        final MockInstruction mi2 = new MockInstruction(546);
        final MockInstruction mi3 = new MockInstruction(819);
        final MockInstruction mi4 = new MockInstruction(1092);
        final MockInstruction mi5 = new MockInstruction(1365);
        final MockInstruction mi6 = new MockInstruction(1638);
        final CCodeNode parentNode = new CCodeNode(1, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode upperNode = new CCodeNode(2, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode inlinedNode = new CCodeNode(3, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction2, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode lowerNode = new CCodeNode(4, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode childNode1 = new CCodeNode(5, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode childNode2 = new CCodeNode(6, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        parentNode.addInstruction(mi1, null);
        upperNode.addInstruction(mi2, null);
        inlinedNode.addInstruction(mi3, null);
        lowerNode.addInstruction(mi4, null);
        childNode1.addInstruction(mi5, null);
        childNode2.addInstruction(mi6, null);
        nodes.add(parentNode);
        nodes.add(upperNode);
        nodes.add(inlinedNode);
        nodes.add(lowerNode);
        nodes.add(childNode1);
        nodes.add(childNode2);
        final CNaviViewEdge parentUpperEdge = new CNaviViewEdge(1, parentNode, upperNode, EdgeType.JUMP_UNCONDITIONAL, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge upperInlinedEdge = new CNaviViewEdge(2, upperNode, inlinedNode, EdgeType.ENTER_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge inlingedLowerEdge = new CNaviViewEdge(3, inlinedNode, lowerNode, EdgeType.LEAVE_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge lowerChild1Edge = new CNaviViewEdge(4, lowerNode, childNode1, EdgeType.JUMP_CONDITIONAL_TRUE, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge lowerChild2Edge = new CNaviViewEdge(5, lowerNode, childNode2, EdgeType.JUMP_CONDITIONAL_FALSE, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge child1UpperEdge = new CNaviViewEdge(6, childNode1, upperNode, EdgeType.JUMP_UNCONDITIONAL_LOOP, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        edges.add(parentUpperEdge);
        edges.add(upperInlinedEdge);
        edges.add(inlingedLowerEdge);
        edges.add(lowerChild1Edge);
        edges.add(lowerChild2Edge);
        edges.add(child1UpperEdge);
        connect(parentNode, upperNode, parentUpperEdge);
        connect(upperNode, inlinedNode, upperInlinedEdge);
        connect(inlinedNode, lowerNode, inlingedLowerEdge);
        connect(lowerNode, childNode1, lowerChild1Edge);
        connect(lowerNode, childNode2, lowerChild2Edge);
        connect(childNode1, upperNode, child1UpperEdge);
        final MockView view = new MockView(nodes, edges, m_provider);
        CUnInliner.unInline(view, inlinedNode);
        Assert.assertEquals(4, view.getNodeCount());
        Assert.assertEquals(4, view.getEdgeCount());
        Assert.assertEquals(JUMP_UNCONDITIONAL, view.getGraph().getEdges().get(0).getType());
        Assert.assertEquals(JUMP_UNCONDITIONAL_LOOP, view.getGraph().getEdges().get(1).getType());
        Assert.assertEquals(JUMP_CONDITIONAL_TRUE, view.getGraph().getEdges().get(2).getType());
        Assert.assertEquals(JUMP_CONDITIONAL_FALSE, view.getGraph().getEdges().get(3).getType());
        final INaviCodeNode cnode = ((INaviCodeNode) (view.getGraph().getNodes().get(3)));
        Assert.assertEquals(2, Iterables.size(cnode.getInstructions()));
        Assert.assertEquals(mi2.toString(), Iterables.get(cnode.getInstructions(), 0).toString());
        Assert.assertEquals(mi4.toString(), Iterables.get(cnode.getInstructions(), 1).toString());
    }

    @Test
    public void testUninlineSingleBlockNested() {
        final List<INaviViewNode> nodes = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviViewNode>();
        final List<INaviEdge> edges = new com.google.security.zynamics.zylib.types.lists.FilledList<INaviEdge>();
        final INaviFunction mockFunction = new MockFunction(m_provider);
        final INaviFunction mockFunction2 = new MockFunction(m_provider);
        final MockInstruction mi1 = new MockInstruction(291);
        final MockInstruction mi2 = new MockInstruction(292);
        final MockInstruction mi3 = new MockInstruction(293);
        final CCodeNode parentNode = new CCodeNode(1, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode upperNode = new CCodeNode(2, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode inlinedNode = new CCodeNode(3, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction2, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode lowerNode = new CCodeNode(4, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        final CCodeNode childNode = new CCodeNode(5, 0, 0, 0, 0, Color.RED, Color.RED, false, true, null, mockFunction, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), m_provider);
        upperNode.addInstruction(mi1, null);
        inlinedNode.addInstruction(mi2, null);
        lowerNode.addInstruction(mi3, null);
        nodes.add(parentNode);
        nodes.add(upperNode);
        nodes.add(inlinedNode);
        nodes.add(lowerNode);
        nodes.add(childNode);
        final CNaviViewEdge parentEdge = new CNaviViewEdge(0, parentNode, upperNode, EdgeType.ENTER_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge enteringEdge = new CNaviViewEdge(1, upperNode, inlinedNode, EdgeType.ENTER_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge leavingEdge = new CNaviViewEdge(2, inlinedNode, lowerNode, EdgeType.LEAVE_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        final CNaviViewEdge childEdge1 = new CNaviViewEdge(3, lowerNode, childNode, EdgeType.LEAVE_INLINED_FUNCTION, 0, 0, 0, 0, Color.BLACK, false, true, null, new com.google.security.zynamics.zylib.types.lists.FilledList<com.google.security.zynamics.zylib.gui.zygraph.edges.CBend>(), m_provider);
        edges.add(parentEdge);
        edges.add(enteringEdge);
        edges.add(leavingEdge);
        edges.add(childEdge1);
        connect(parentNode, upperNode, parentEdge);
        connect(upperNode, inlinedNode, enteringEdge);
        connect(inlinedNode, lowerNode, leavingEdge);
        connect(lowerNode, childNode, childEdge1);
        final MockView view = new MockView(nodes, edges, m_provider);
        CUnInliner.unInline(view, inlinedNode);
        Assert.assertEquals(3, view.getNodeCount());
        Assert.assertEquals(2, view.getEdgeCount());
        Assert.assertEquals(ENTER_INLINED_FUNCTION, view.getGraph().getEdges().get(0).getType());
        Assert.assertEquals(LEAVE_INLINED_FUNCTION, view.getGraph().getEdges().get(1).getType());
        final INaviCodeNode cnode = ((INaviCodeNode) (view.getGraph().getNodes().get(2)));
        Assert.assertEquals(2, Iterables.size(cnode.getInstructions()));
        Assert.assertEquals(mi1.toString(), Iterables.get(cnode.getInstructions(), 0).toString());
        Assert.assertEquals(mi3.toString(), Iterables.get(cnode.getInstructions(), 1).toString());
    }
}

