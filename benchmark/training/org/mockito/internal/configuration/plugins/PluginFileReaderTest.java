/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.plugins;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class PluginFileReaderTest extends TestBase {
    PluginFileReader reader = new PluginFileReader();

    @Test
    public void no_class_in_resource() throws IOException {
        // no class
        Assert.assertNull(reader.readPluginClass(impl("")));
        Assert.assertNull(reader.readPluginClass(impl("  ")));
        Assert.assertNull(reader.readPluginClass(impl(" \n ")));
        // commented out
        Assert.assertNull(reader.readPluginClass(impl("#foo")));
        Assert.assertNull(reader.readPluginClass(impl("  # foo  ")));
        Assert.assertNull(reader.readPluginClass(impl("  # # # java.langString # ")));
        Assert.assertNull(reader.readPluginClass(impl("  \n # foo \n # foo \n ")));
    }

    @Test
    public void reads_class_name() throws IOException {
        Assert.assertEquals("java.lang.String", reader.readPluginClass(impl("java.lang.String")));
        Assert.assertEquals("x", reader.readPluginClass(impl("x")));
        Assert.assertEquals("x y z", reader.readPluginClass(impl(" x y z ")));
        Assert.assertEquals("foo.Foo", reader.readPluginClass(impl(" #my class\n  foo.Foo \n #other class ")));
        Assert.assertEquals("foo.Foo", reader.readPluginClass(impl("foo.Foo  # cool class")));
    }
}

