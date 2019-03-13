/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoinline;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker;


public class PluginTest {
    @Test
    public void plugin_type_should_be_inline() throws Exception {
        Assert.assertTrue(((Plugins.getMockMaker()) instanceof InlineByteBuddyMockMaker));
    }
}

