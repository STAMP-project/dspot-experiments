/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.plugins;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.creation.instance.Instantiator;
import org.mockito.plugins.AnnotationEngine;
import org.mockito.plugins.InstantiatorProvider;
import org.mockito.plugins.InstantiatorProvider2;
import org.mockito.plugins.MockMaker;
import org.mockito.plugins.MockitoLogger;
import org.mockito.plugins.MockitoPlugins;
import org.mockito.plugins.PluginSwitch;
import org.mockito.plugins.StackTraceCleanerProvider;
import org.mockitoutil.TestBase;


public class MockitoPluginsTest extends TestBase {
    private final MockitoPlugins plugins = Mockito.framework().getPlugins();

    @Test
    public void provides_built_in_plugins() {
        Assert.assertNotNull(plugins.getInlineMockMaker());
        Assert.assertNotNull(plugins.getDefaultPlugin(MockMaker.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(StackTraceCleanerProvider.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(PluginSwitch.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(InstantiatorProvider.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(InstantiatorProvider2.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(AnnotationEngine.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(MockitoLogger.class));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void instantiator_provider_backwards_compatibility() {
        InstantiatorProvider provider = plugins.getDefaultPlugin(InstantiatorProvider.class);
        Instantiator instantiator = provider.getInstantiator(Mockito.withSettings().build(MockitoPluginsTest.class));
        Assert.assertNotNull(instantiator.newInstance(MockitoPluginsTest.class));
    }
}

