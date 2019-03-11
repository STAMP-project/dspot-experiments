/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.plugins;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.creation.bytebuddy.ByteBuddyMockMaker;
import org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker;
import org.mockito.internal.util.ConsoleMockitoLogger;
import org.mockito.plugins.InstantiatorProvider;
import org.mockito.plugins.InstantiatorProvider2;
import org.mockito.plugins.MockMaker;
import org.mockito.plugins.MockitoLogger;
import org.mockitoutil.TestBase;


public class DefaultMockitoPluginsTest extends TestBase {
    private DefaultMockitoPlugins plugins = new DefaultMockitoPlugins();

    @Test
    public void provides_plugins() throws Exception {
        Assert.assertEquals("org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker", plugins.getDefaultPluginClass(DefaultMockitoPlugins.INLINE_ALIAS));
        Assert.assertEquals(InlineByteBuddyMockMaker.class, plugins.getInlineMockMaker().getClass());
        Assert.assertEquals(ByteBuddyMockMaker.class, plugins.getDefaultPlugin(MockMaker.class).getClass());
        Assert.assertNotNull(plugins.getDefaultPlugin(InstantiatorProvider.class));
        Assert.assertNotNull(plugins.getDefaultPlugin(InstantiatorProvider2.class));
        Assert.assertEquals(ConsoleMockitoLogger.class, plugins.getDefaultPlugin(MockitoLogger.class).getClass());
    }
}

