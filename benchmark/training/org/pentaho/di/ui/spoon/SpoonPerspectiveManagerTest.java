/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon;


import SpoonPerspectiveManager.PerspectiveManager;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.swt.widgets.Composite;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.EngineMetaInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.ui.xul.XulOverlay;
import org.pentaho.ui.xul.impl.XulEventHandler;


public class SpoonPerspectiveManagerTest {
    private static final String PERSPECTIVE_ID = "perspective-id";

    private static final String PERSPECTIVE_NAME = "perspective-name";

    private Map<SpoonPerspective, SpoonPerspectiveManager.PerspectiveManager> perspectiveManagerMap;

    private static SpoonPerspectiveManager spoonPerspectiveManager;

    private SpoonPerspective perspective;

    @Test
    public void perspectiveIsInitializedOnlyOnce() throws KettleException {
        SpoonPerspectiveManager.PerspectiveManager perspectiveManager = perspectiveManagerMap.get(perspective);
        SpoonPerspectiveManagerTest.spoonPerspectiveManager.activatePerspective(perspective.getClass());
        // it's the first time this perspective gets active, so it should be initialized after this call
        Mockito.verify(perspectiveManager).performInit();
        SpoonPerspectiveManagerTest.spoonPerspectiveManager.activatePerspective(perspective.getClass());
        // make sure that perspective was inited only after first activation
        Mockito.verify(perspectiveManager).performInit();
    }

    @Test
    public void hidePerspective() {
        SpoonPerspectiveManager.PerspectiveManager perspectiveManager = perspectiveManagerMap.get(perspective);
        SpoonPerspectiveManagerTest.spoonPerspectiveManager.hidePerspective(perspective.getId());
        Mockito.verify(perspectiveManager).setPerspectiveHidden(SpoonPerspectiveManagerTest.PERSPECTIVE_NAME, true);
    }

    @Test
    public void showPerspective() {
        SpoonPerspectiveManager.PerspectiveManager perspectiveManager = perspectiveManagerMap.get(perspective);
        SpoonPerspectiveManagerTest.spoonPerspectiveManager.showPerspective(perspective.getId());
        Mockito.verify(perspectiveManager).setPerspectiveHidden(SpoonPerspectiveManagerTest.PERSPECTIVE_NAME, false);
    }

    private class DummyPerspective implements SpoonPerspective {
        @Override
        public String getId() {
            return SpoonPerspectiveManagerTest.PERSPECTIVE_ID;
        }

        @Override
        public Composite getUI() {
            return null;
        }

        @Override
        public String getDisplayName(Locale l) {
            return SpoonPerspectiveManagerTest.PERSPECTIVE_NAME;
        }

        @Override
        public InputStream getPerspectiveIcon() {
            return null;
        }

        @Override
        public void setActive(boolean active) {
        }

        @Override
        public List<XulOverlay> getOverlays() {
            return null;
        }

        @Override
        public List<XulEventHandler> getEventHandlers() {
            return null;
        }

        @Override
        public void addPerspectiveListener(SpoonPerspectiveListener listener) {
        }

        @Override
        public EngineMetaInterface getActiveMeta() {
            return null;
        }
    }
}

