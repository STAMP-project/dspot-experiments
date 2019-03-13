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


import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class ExtensionPointHandlerTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static final String TEST_NAME = "testName";

    @Test
    public void callExtensionPointTest() throws Exception {
        PluginMockInterface pluginInterface = Mockito.mock(PluginMockInterface.class);
        Mockito.when(getName()).thenReturn(ExtensionPointHandlerTest.TEST_NAME);
        Mockito.when(getMainType()).thenReturn(((Class) (ExtensionPointInterface.class)));
        Mockito.when(getIds()).thenReturn(new String[]{ "testID" });
        ExtensionPointInterface extensionPoint = Mockito.mock(ExtensionPointInterface.class);
        Mockito.when(loadClass(ExtensionPointInterface.class)).thenReturn(extensionPoint);
        PluginRegistry.addPluginType(ExtensionPointPluginType.getInstance());
        PluginRegistry.getInstance().registerPlugin(ExtensionPointPluginType.class, pluginInterface);
        final LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        ExtensionPointHandler.callExtensionPoint(log, "noPoint", null);
        Mockito.verify(extensionPoint, Mockito.never()).callExtensionPoint(ArgumentMatchers.any(LogChannelInterface.class), ArgumentMatchers.any());
        ExtensionPointHandler.callExtensionPoint(log, ExtensionPointHandlerTest.TEST_NAME, null);
        Mockito.verify(extensionPoint, Mockito.times(1)).callExtensionPoint(ArgumentMatchers.eq(log), ArgumentMatchers.isNull());
    }
}

