/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.plugins;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;


public class PluginLoaderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    PluginInitializer initializer;

    @Mock
    DefaultMockitoPlugins plugins;

    @InjectMocks
    PluginLoader loader;

    @Test
    public void loads_plugin() {
        Mockito.when(initializer.loadImpl(PluginLoaderTest.FooPlugin.class)).thenReturn(new PluginLoaderTest.FooPlugin());
        // when
        PluginLoaderTest.FooPlugin plugin = loader.loadPlugin(PluginLoaderTest.FooPlugin.class);
        // then
        Assert.assertNotNull(plugin);
    }

    @Test
    public void loads_alternative_plugin() {
        BDDMockito.willReturn(null).given(initializer).loadImpl(PluginLoaderTest.FooPlugin.class);
        PluginLoaderTest.BarPlugin expected = new PluginLoaderTest.BarPlugin();
        BDDMockito.willReturn(expected).given(initializer).loadImpl(PluginLoaderTest.BarPlugin.class);
        // when
        Object plugin = loader.loadPlugin(PluginLoaderTest.FooPlugin.class, PluginLoaderTest.BarPlugin.class);
        // then
        Assert.assertSame(plugin, expected);
    }

    @Test
    public void loads_default_plugin() {
        BDDMockito.willReturn(null).given(initializer).loadImpl(PluginLoaderTest.FooPlugin.class);
        BDDMockito.willReturn(null).given(initializer).loadImpl(PluginLoaderTest.BarPlugin.class);
        PluginLoaderTest.FooPlugin expected = new PluginLoaderTest.FooPlugin();
        BDDMockito.willReturn(expected).given(plugins).getDefaultPlugin(PluginLoaderTest.FooPlugin.class);
        // when
        Object plugin = loader.loadPlugin(PluginLoaderTest.FooPlugin.class, PluginLoaderTest.BarPlugin.class);
        // then
        Assert.assertSame(plugin, expected);
    }

    @Test
    public void fails_to_load_plugin() {
        RuntimeException cause = new RuntimeException("Boo!");
        Mockito.when(initializer.loadImpl(PluginLoaderTest.Foo.class)).thenThrow(cause);
        // when
        final PluginLoaderTest.Foo plugin = loader.loadPlugin(PluginLoaderTest.Foo.class);
        // then
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                plugin.toString();// call any method on the plugin

            }
        }).isInstanceOf(IllegalStateException.class).hasMessage("Could not initialize plugin: interface org.mockito.internal.configuration.plugins.PluginLoaderTest$Foo (alternate: null)").hasCause(cause);
    }

    static class FooPlugin {}

    static class BarPlugin {}

    static interface Foo {}
}

