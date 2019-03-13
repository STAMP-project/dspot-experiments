/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.core.plugins;


import ValueMetaInterface.TYPE_STRING;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.exception.KettlePluginClassMapException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.extension.PluginMockInterface;
import org.pentaho.di.core.logging.LoggingPluginType;
import org.pentaho.di.core.row.RowBuffer;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class PluginRegistryUnitTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void getGetPluginInformation() throws KettlePluginException {
        PluginRegistry.getInstance().reset();
        RowBuffer result = PluginRegistry.getInstance().getPluginInformation(BasePluginType.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(8, result.getRowMeta().size());
        for (ValueMetaInterface vmi : result.getRowMeta().getValueMetaList()) {
            Assert.assertEquals(TYPE_STRING, vmi.getType());
        }
    }

    /**
     * Test that additional plugin mappings can be added via the PluginRegistry.
     */
    @Test
    public void testSupplementalPluginMappings() throws Exception {
        PluginRegistry registry = PluginRegistry.getInstance();
        PluginInterface mockPlugin = Mockito.mock(PluginInterface.class);
        Mockito.when(mockPlugin.getIds()).thenReturn(new String[]{ "mockPlugin" });
        Mockito.when(mockPlugin.matches("mockPlugin")).thenReturn(true);
        Mockito.when(mockPlugin.getName()).thenReturn("mockPlugin");
        Mockito.doReturn(LoggingPluginType.class).when(mockPlugin).getPluginType();
        registry.registerPlugin(LoggingPluginType.class, mockPlugin);
        registry.addClassFactory(LoggingPluginType.class, String.class, "mockPlugin", () -> "Foo");
        String result = registry.loadClass(LoggingPluginType.class, "mockPlugin", String.class);
        Assert.assertEquals("Foo", result);
        Assert.assertEquals(2, registry.getPlugins(LoggingPluginType.class).size());
        // Now add another mapping and verify that it works and the existing supplementalPlugin was reused.
        UUID uuid = UUID.randomUUID();
        registry.addClassFactory(LoggingPluginType.class, UUID.class, "mockPlugin", () -> uuid);
        UUID out = registry.loadClass(LoggingPluginType.class, "mockPlugin", UUID.class);
        Assert.assertEquals(uuid, out);
        Assert.assertEquals(2, registry.getPlugins(LoggingPluginType.class).size());
    }

    /**
     * Test that several plugin jar can share the same classloader.
     */
    @Test
    public void testPluginClassloaderGroup() throws Exception {
        PluginRegistry registry = PluginRegistry.getInstance();
        PluginInterface mockPlugin1 = Mockito.mock(PluginInterface.class);
        Mockito.when(mockPlugin1.getIds()).thenReturn(new String[]{ "mockPlugin" });
        Mockito.when(mockPlugin1.matches("mockPlugin")).thenReturn(true);
        Mockito.when(mockPlugin1.getName()).thenReturn("mockPlugin");
        Mockito.when(mockPlugin1.getClassMap()).thenReturn(new HashMap<Class<?>, String>() {
            {
                put(PluginTypeInterface.class, String.class.getName());
            }
        });
        Mockito.when(mockPlugin1.getClassLoaderGroup()).thenReturn("groupPlugin");
        Mockito.doReturn(BasePluginType.class).when(mockPlugin1).getPluginType();
        PluginInterface mockPlugin2 = Mockito.mock(PluginInterface.class);
        Mockito.when(mockPlugin2.getIds()).thenReturn(new String[]{ "mockPlugin2" });
        Mockito.when(mockPlugin2.matches("mockPlugin2")).thenReturn(true);
        Mockito.when(mockPlugin2.getName()).thenReturn("mockPlugin2");
        Mockito.when(mockPlugin2.getClassMap()).thenReturn(new HashMap<Class<?>, String>() {
            {
                put(PluginTypeInterface.class, Integer.class.getName());
            }
        });
        Mockito.when(mockPlugin2.getClassLoaderGroup()).thenReturn("groupPlugin");
        Mockito.doReturn(BasePluginType.class).when(mockPlugin2).getPluginType();
        registry.registerPlugin(BasePluginType.class, mockPlugin1);
        registry.registerPlugin(BasePluginType.class, mockPlugin2);
        // test they share the same classloader
        ClassLoader ucl = registry.getClassLoader(mockPlugin1);
        Assert.assertEquals(ucl, registry.getClassLoader(mockPlugin2));
        // test removing a shared plugin creates a new classloader
        registry.removePlugin(BasePluginType.class, mockPlugin2);
        Assert.assertNotEquals(ucl, registry.getClassLoader(mockPlugin1));
    }

    @Test(expected = KettlePluginClassMapException.class)
    public void testClassloadingPluginNoClassRegistered() throws KettlePluginException {
        PluginRegistry registry = PluginRegistry.getInstance();
        PluginMockInterface plugin = Mockito.mock(PluginMockInterface.class);
        Mockito.when(loadClass(ArgumentMatchers.any())).thenReturn(null);
        registry.loadClass(plugin, Class.class);
    }

    @Test
    public void testMergingPluginFragment() throws KettlePluginException {
        // setup
        // initialize Fragment Type
        PluginRegistry registry = PluginRegistry.getInstance();
        BaseFragmentType fragmentType = new BaseFragmentType(Annotation.class, "", "", ValueMetaPluginType.class) {
            @Override
            protected void initListeners(Class<? extends PluginTypeInterface> aClass, Class<? extends PluginTypeInterface> typeToTrack) {
                super.initListeners(BaseFragmentType.class, typeToTrack);
            }

            @Override
            protected String extractID(Annotation annotation) {
                return null;
            }

            @Override
            protected String extractImageFile(Annotation annotation) {
                return null;
            }

            @Override
            protected String extractDocumentationUrl(Annotation annotation) {
                return null;
            }

            @Override
            protected String extractCasesUrl(Annotation annotation) {
                return null;
            }

            @Override
            protected String extractForumUrl(Annotation annotation) {
                return null;
            }

            @Override
            protected String extractSuggestion(Annotation annotation) {
                return null;
            }
        };
        Assert.assertTrue(fragmentType.isFragment());
        PluginInterface plugin = Mockito.mock(PluginInterface.class);
        Mockito.when(plugin.getIds()).thenReturn(new String[]{ "mock" });
        Mockito.when(plugin.matches(ArgumentMatchers.any())).thenReturn(true);
        Mockito.doReturn(ValueMetaPluginType.class).when(plugin).getPluginType();
        Mockito.doAnswer(( invocationOnMock) -> null).when(plugin).merge(ArgumentMatchers.any(PluginInterface.class));
        PluginInterface fragment = Mockito.mock(PluginInterface.class);
        Mockito.when(fragment.getIds()).thenReturn(new String[]{ "mock" });
        Mockito.when(fragment.matches(ArgumentMatchers.any())).thenReturn(true);
        Mockito.doReturn(BaseFragmentType.class).when(fragment).getPluginType();
        Mockito.doAnswer(( invocationOnMock) -> null).when(fragment).merge(ArgumentMatchers.any(PluginInterface.class));
        // test
        registry.registerPlugin(ValueMetaPluginType.class, plugin);
        Mockito.verify(plugin, Mockito.atLeastOnce()).merge(ArgumentMatchers.any());
        registry.registerPlugin(BaseFragmentType.class, fragment);
        Mockito.verify(fragment, Mockito.never()).merge(ArgumentMatchers.any());
        Mockito.verify(plugin, Mockito.atLeast(2)).merge(ArgumentMatchers.any());
        // verify that the order doesn't influence
        registry.removePlugin(ValueMetaPluginType.class, plugin);
        registry.registerPlugin(ValueMetaPluginType.class, plugin);
        Mockito.verify(plugin, Mockito.atLeast(3)).merge(ArgumentMatchers.any());
        // verify plugin changes
        registry.registerPlugin(ValueMetaPluginType.class, plugin);
        Mockito.verify(plugin, Mockito.atLeast(4)).merge(ArgumentMatchers.any());
    }
}

