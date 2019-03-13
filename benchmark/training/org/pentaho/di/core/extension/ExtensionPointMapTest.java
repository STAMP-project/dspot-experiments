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


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class ExtensionPointMapTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    public static final String TEST_NAME = "testName";

    private PluginMockInterface pluginInterface;

    private ExtensionPointInterface extensionPoint;

    @Test
    public void constructorTest() throws Exception {
        PluginRegistry.getInstance().registerPlugin(ExtensionPointPluginType.class, pluginInterface);
        Assert.assertEquals(1, ExtensionPointMap.getInstance().getNumberOfRows());
        PluginRegistry.getInstance().registerPlugin(ExtensionPointPluginType.class, pluginInterface);
        Assert.assertEquals(1, ExtensionPointMap.getInstance().getNumberOfRows());
        PluginRegistry.getInstance().removePlugin(ExtensionPointPluginType.class, pluginInterface);
        Assert.assertEquals(0, ExtensionPointMap.getInstance().getNumberOfRows());
        // Verify lazy loading
        loadClass(ArgumentMatchers.any(Class.class));
    }

    @Test
    public void addExtensionPointTest() throws KettlePluginException {
        ExtensionPointMap.getInstance().addExtensionPoint(pluginInterface);
        Assert.assertEquals(ExtensionPointMap.getInstance().getTableValue(ExtensionPointMapTest.TEST_NAME, "testID"), extensionPoint);
        // Verify cached instance
        Assert.assertEquals(ExtensionPointMap.getInstance().getTableValue(ExtensionPointMapTest.TEST_NAME, "testID"), extensionPoint);
        loadClass(ArgumentMatchers.any(Class.class));
    }
}

