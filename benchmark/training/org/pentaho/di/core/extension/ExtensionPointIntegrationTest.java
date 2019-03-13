/**
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
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
 */
package org.pentaho.di.core.extension;


import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;

import static KettleExtensionPoint.JobAfterOpen;


public class ExtensionPointIntegrationTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    public static final String EXECUTED_FIELD_NAME = "executed";

    private static final int TOTAL_THREADS_TO_RUN = 2000;

    private static final int MAX_TIMEOUT_SECONDS = 60;

    private static ClassPool pool;

    @Test
    public void test() throws Exception {
        // check that all extension points are added to the map
        Assert.assertEquals(KettleExtensionPoint.values().length, ExtensionPointMap.getInstance().getNumberOfRows());
        // check that all extension points are executed
        final LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        for (KettleExtensionPoint ep : KettleExtensionPoint.values()) {
            final ExtensionPointInterface currentEP = ExtensionPointMap.getInstance().getTableValue(ep.id, ("id" + (ep.id)));
            Assert.assertFalse(currentEP.getClass().getField(ExtensionPointIntegrationTest.EXECUTED_FIELD_NAME).getBoolean(currentEP));
            ExtensionPointHandler.callExtensionPoint(log, ep.id, null);
            Assert.assertTrue(currentEP.getClass().getField(ExtensionPointIntegrationTest.EXECUTED_FIELD_NAME).getBoolean(currentEP));
        }
        // check modification of extension point
        final KettleExtensionPoint jobAfterOpen = JobAfterOpen;
        final ExtensionPointInterface int1 = ExtensionPointMap.getInstance().getTableValue(jobAfterOpen.id, ("id" + (jobAfterOpen.id)));
        ExtensionPointPluginType.getInstance().registerCustom(ExtensionPointIntegrationTest.createClassRuntime(jobAfterOpen, "Edited"), "custom", ("id" + (jobAfterOpen.id)), jobAfterOpen.id, "no description", null);
        Assert.assertNotSame(int1, ExtensionPointMap.getInstance().getTableValue(jobAfterOpen.id, ("id" + (jobAfterOpen.id))));
        Assert.assertEquals(KettleExtensionPoint.values().length, ExtensionPointMap.getInstance().getNumberOfRows());
        // check removal of extension point
        PluginRegistry.getInstance().removePlugin(ExtensionPointPluginType.class, PluginRegistry.getInstance().getPlugin(ExtensionPointPluginType.class, ("id" + (jobAfterOpen.id))));
        Assert.assertTrue(((ExtensionPointMap.getInstance().getTableValue(jobAfterOpen.id, ("id" + (jobAfterOpen.id)))) == null));
        Assert.assertEquals(((KettleExtensionPoint.values().length) - 1), ExtensionPointMap.getInstance().getNumberOfRows());
    }

    @Test
    public void testExtensionPointMapConcurrency() throws InterruptedException {
        final LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        List<Runnable> parallelTasksList = new ArrayList<>(ExtensionPointIntegrationTest.TOTAL_THREADS_TO_RUN);
        for (int i = 0; i < (ExtensionPointIntegrationTest.TOTAL_THREADS_TO_RUN); i++) {
            parallelTasksList.add(() -> {
                KettleExtensionPoint kettleExtensionPoint = ExtensionPointIntegrationTest.getRandomKettleExtensionPoint();
                PluginInterface pluginInterface = PluginRegistry.getInstance().getPlugin(ExtensionPointPluginType.class, ("id" + (kettleExtensionPoint.id)));
                try {
                    PluginRegistry.getInstance().removePlugin(ExtensionPointPluginType.class, pluginInterface);
                    PluginRegistry.getInstance().registerPlugin(ExtensionPointPluginType.class, pluginInterface);
                } catch (KettlePluginException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    // NullPointerException can be thrown if trying to remove a plugin that doesn't exit, discarding occurence
                }
                ExtensionPointMap.getInstance().reInitialize();
                try {
                    ExtensionPointMap.getInstance().callExtensionPoint(log, kettleExtensionPoint.id, null);
                } catch (KettleException e) {
                    e.printStackTrace();
                }
            });
        }
        ExtensionPointIntegrationTest.assertConcurrent(parallelTasksList);
    }
}

