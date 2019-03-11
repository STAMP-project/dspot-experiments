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


import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.ZyGraph.Implementations.CGraphFunctions;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import com.google.security.zynamics.zylib.gui.zygraph.functions.NodeFunctions;
import com.google.security.zynamics.zylib.gui.zygraph.proximity.ProximityUpdater;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ZyProximityBrowserTest {
    private ZyGraph m_graph;

    private MockSqlProvider m_provider;

    private MockView m_view;

    @Test
    public void testConstructor() {
        final ProximityUpdater<NaviNode> updater = new ProximityUpdater<NaviNode>(m_graph) {
            @Override
            protected void showNodes(final Collection<NaviNode> selectedNodes, final Collection<NaviNode> allNodes) {
                CGraphFunctions.showNodes(null, m_graph, selectedNodes, allNodes);
            }
        };
        m_graph.addListener(updater);
        Assert.assertEquals(7, m_graph.visibleNodeCount());
        Assert.assertEquals(89, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        m_graph.getRawView().getGraph().getNodes().get(2).setSelected(true);
        Assert.assertEquals(9, m_graph.visibleNodeCount());
        Assert.assertEquals(87, NodeFunctions.getInvisibleNodes(m_graph).size());
        Assert.assertEquals(96, m_graph.getRawView().getNodeCount());
        m_graph.removeListener(updater);
    }
}

