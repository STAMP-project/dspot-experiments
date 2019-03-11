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
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabaseManager;
import com.google.security.zynamics.binnavi.Plugins.MockPluginInterface;
import com.google.security.zynamics.binnavi.api2.IPluginInterface;
import com.google.security.zynamics.binnavi.disassembly.views.CView;
import com.google.security.zynamics.binnavi.yfileswrap.API.disassembly.View2D;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class View2DTest {
    private View2D m_view2d;

    private final MockDatabaseManager manager = new MockDatabaseManager();

    private final IPluginInterface pluginInterface = new MockPluginInterface(manager);

    private CView m_view;

    @Test
    public void testConstructor() {
        Assert.assertEquals(pluginInterface.getDatabaseManager().getDatabases().get(0).getModules().get(0), m_view2d.getContainer());
        Assert.assertEquals("View2D 'name'", m_view2d.toString());
    }

    @Test
    public void testLayouts() {
        m_view2d.doCircularLayout();
        m_view2d.doHierarchicalLayout();
        m_view2d.doOrthogonalLayout();
        m_view2d.zoomToScreen();
        m_view2d.updateUI();
    }

    @Test
    public void testSave() throws CouldntSaveDataException {
        m_view2d.save();
    }

    @Test
    public void testSaveAs() throws CouldntSaveDataException {
        m_view2d.saveAs("Fark", "Fork");
    }
}

