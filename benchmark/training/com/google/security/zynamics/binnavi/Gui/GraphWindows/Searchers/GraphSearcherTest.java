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
package com.google.security.zynamics.binnavi.Gui.GraphWindows.Searchers;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.CCodeNode;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.CInstruction;
import com.google.security.zynamics.binnavi.disassembly.MockCreator;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.views.CView;
import com.google.security.zynamics.binnavi.yfileswrap.Gui.GraphWindows.Searchers.Text.Model.GraphSearcher;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode;
import com.google.security.zynamics.zylib.gui.zygraph.realizers.ZyLabelContent;
import com.google.security.zynamics.zylib.gui.zygraph.realizers.ZyLineContent;
import com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNodeRealizer;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import y.base.Node;
import y.view.Graph2D;


@RunWith(JUnit4.class)
public final class GraphSearcherTest {
    private CCodeNode m_codeNode1;

    private ZyLabelContent m_content;

    private Node m_ynode;

    @Test
    public void testAfterLast() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "");
        Assert.assertNull(searcher.getCursor().current());
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertFalse(searcher.getCursor().isAfterLast());
        searcher.getCursor().next();
        Assert.assertTrue(searcher.getCursor().isAfterLast());
        searcher.getCursor().next();
        Assert.assertTrue(searcher.getCursor().isAfterLast());
    }

    @Test
    public void testBeforeFirst() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final NaviNode m_node2 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "");
        Assert.assertNull(searcher.getCursor().current());
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1, m_node2), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertFalse(searcher.getCursor().isAfterLast());
        searcher.getCursor().previous();
        Assert.assertTrue(searcher.getCursor().isBeforeFirst());
        searcher.getCursor().previous();
        Assert.assertFalse(searcher.getCursor().isBeforeFirst());
    }

    @Test
    public void testSearchRegex() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        searcher.getSettings().setRegEx(true);
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my[^s]*");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(5, searcher.getCursor().current().getLength());
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my.*");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(7, searcher.getCursor().current().getLength());
        searcher.getSettings().setCaseSensitive(true);
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my[^t]*");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(6, searcher.getCursor().current().getLength());
    }

    @Test
    public void testSearchSelected() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        searcher.getSettings().setOnlySelected(true);
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertNull(searcher.getCursor().current());
        m_node1.getRawNode().setSelected(true);
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
        // TEST: Do not move beyond the last result
        searcher.getCursor().next();
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
    }

    @Test
    public void testSearchVisible() throws CPartialLoadException, CouldntLoadDataException, LoadCancelledException {
        final MockSqlProvider sql = new MockSqlProvider();
        final CModule module = MockCreator.createModule(sql);
        module.load();
        final CFunction function = MockCreator.createFunction(module, sql);
        final CView m_view = MockCreator.createView(sql, module);
        final CInstruction instruction1 = MockCreator.createInstruction(module, sql);
        final CInstruction instruction2 = MockCreator.createInstruction(module, sql);
        final CInstruction instruction3 = MockCreator.createInstruction(module, sql);
        m_view.load();
        final CCodeNode node1 = m_view.getContent().createCodeNode(function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final CCodeNode node2 = m_view.getContent().createCodeNode(function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final CCodeNode node3 = m_view.getContent().createCodeNode(function, Lists.newArrayList(instruction1, instruction2, instruction3));
        final ZyNodeRealizer<NaviNode> r1 = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final ZyNodeRealizer<NaviNode> r2 = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final ZyNodeRealizer<NaviNode> r3 = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final Graph2D g = new Graph2D();
        g.setHierarchyManager(new y.view.hierarchy.HierarchyManager(g));
        final NaviNode m_node1 = new NaviNode(g.createNode(), r1, node1);
        final NaviNode m_node2 = new NaviNode(g.createNode(), r2, node2);
        final NaviNode m_node3 = new NaviNode(g.createNode(), r3, node3);
        Assert.assertTrue(m_node1.isVisible());
        final GraphSearcher searcher = new GraphSearcher();
        searcher.getSettings().setOnlyVisible(true);
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1, m_node2, m_node3), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
        searcher.getCursor().next();
        Assert.assertEquals(m_node2, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
        searcher.getCursor().next();
        Assert.assertEquals(m_node3, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
        m_node2.getRawNode().setVisible(false);
        searcher.search(Lists.newArrayList(m_node1, m_node2, m_node3), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
        searcher.getCursor().next();
        Assert.assertEquals(m_node3, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
    }

    @Test
    public void testSimple() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "");
        Assert.assertNull(searcher.getCursor().current());
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
        // TEST: Do not move beyond the last result
        searcher.getCursor().next();
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
    }

    @Test
    public void testSimpleMultiple() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        m_content.addLineContent(new ZyLineContent("Hello my my Test", null));
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "my");
        Assert.assertEquals(m_node1, searcher.getCursor().current().getObject());
        Assert.assertEquals(0, searcher.getCursor().current().getLine());
        Assert.assertEquals(6, searcher.getCursor().current().getPosition());
        Assert.assertEquals(2, searcher.getCursor().current().getLength());
    }

    @Test
    public void testSimpleNoResult() {
        final ZyNodeRealizer<NaviNode> r = new com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.realizers.ZyNormalNodeRealizer<NaviNode>(m_content);
        final NaviNode m_node1 = new NaviNode(m_ynode, r, m_codeNode1);
        final GraphSearcher searcher = new GraphSearcher();
        m_content.addLineContent(new ZyLineContent("Hello my Test", null));
        searcher.search(Lists.newArrayList(m_node1), new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviEdge>(), "mx");
        searcher.getCursor().next();
        Assert.assertNull(searcher.getCursor().current());
    }
}

