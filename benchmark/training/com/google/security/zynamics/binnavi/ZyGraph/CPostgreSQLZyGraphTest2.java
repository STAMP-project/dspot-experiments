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


import LayoutStyle.CIRCULAR;
import LayoutStyle.HIERARCHIC;
import com.google.common.collect.Iterables;
import com.google.security.zynamics.binnavi.Database.CDatabase;
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntConnectException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntInitializeDatabaseException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDriverException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.InvalidDatabaseException;
import com.google.security.zynamics.binnavi.Database.Exceptions.InvalidDatabaseVersionException;
import com.google.security.zynamics.binnavi.Database.Exceptions.InvalidExporterDatabaseFormatException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.config.ConfigManager;
import com.google.security.zynamics.binnavi.disassembly.ICallgraphView;
import com.google.security.zynamics.binnavi.disassembly.IFlowgraphView;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.views.CViewFilter;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.binnavi.yfileswrap.Gui.GraphWindows.Loader.CGraphBuilder;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CPostgreSQLZyGraphTest2 {
    private ZyGraph m_graph;

    private IFlowgraphView m_view;

    private INaviModule m_module;

    private CDatabase m_database;

    private CDatabase m_database2;

    @Test
    public void testLayouting() throws CPartialLoadException, CouldntLoadDataException, LoadCancelledException {
        final ICallgraphView cg = m_module.getContent().getViewContainer().getNativeCallgraphView();
        final ZyGraphViewSettings settings = ConfigManager.instance().getDefaultFlowGraphSettings();
        settings.getProximitySettings().setProximityBrowsingActivationThreshold(50);
        cg.load();
        final ZyGraph g = CGraphBuilder.buildGraph(cg);
        g.getSettings().getLayoutSettings().setDefaultGraphLayout(HIERARCHIC);
        g.doLayout();
        g.getSettings().getLayoutSettings().setDefaultGraphLayout(CIRCULAR);
        g.doLayout();
    }

    @Test
    public void testSave() throws CPartialLoadException, CouldntConnectException, CouldntInitializeDatabaseException, CouldntLoadDataException, CouldntLoadDriverException, CouldntSaveDataException, InvalidDatabaseException, InvalidDatabaseVersionException, InvalidExporterDatabaseFormatException, LoadCancelledException {
        m_view.getGraph().getNodes().get(0).setSelected(true);
        m_view.getGraph().getNodes().get(1).setColor(new Color(123));
        m_view.getGraph().getNodes().get(2).setX(456);
        m_view.getGraph().getNodes().get(3).setY(789);
        final INaviView newView = m_graph.saveAs(new com.google.security.zynamics.binnavi.disassembly.Modules.CModuleContainer(m_database, m_module), "New View", "New View Description");
        m_database2.connect();
        m_database2.load();
        final INaviModule module = m_database2.getContent().getModules().get(0);
        module.load();
        final Iterable<INaviView> views = CViewFilter.getFlowgraphViews(module.getContent().getViewContainer().getViews());
        final INaviView loadedNewView = Iterables.getLast(views);
        loadedNewView.load();
        Assert.assertEquals(loadedNewView.getNodeCount(), newView.getNodeCount());
        Assert.assertEquals(true, loadedNewView.getGraph().getNodes().get(0).isSelected());
        Assert.assertEquals(-16777093, loadedNewView.getGraph().getNodes().get(1).getColor().getRGB());
        Assert.assertEquals(456, loadedNewView.getGraph().getNodes().get(2).getX(), 0);
        Assert.assertEquals(789, loadedNewView.getGraph().getNodes().get(3).getY(), 0);
        m_database2.close();
    }
}

