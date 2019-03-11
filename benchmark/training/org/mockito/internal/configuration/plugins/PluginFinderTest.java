/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration.plugins;


import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.io.IOUtil;
import org.mockito.plugins.PluginSwitch;
import org.mockitoutil.TestBase;


public class PluginFinderTest extends TestBase {
    @Mock
    PluginSwitch switcher;

    @InjectMocks
    PluginFinder finder;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void empty_resources() {
        Assert.assertNull(finder.findPluginClass(Collections.<URL>emptyList()));
    }

    @Test
    public void no_valid_impl() throws Exception {
        File f = tmp.newFile();
        // when
        IOUtil.writeText("  \n  ", f);
        // then
        Assert.assertNull(finder.findPluginClass(Arrays.asList(f.toURI().toURL())));
    }

    @Test
    public void single_implementation() throws Exception {
        File f = tmp.newFile();
        Mockito.when(switcher.isEnabled("foo.Foo")).thenReturn(true);
        // when
        IOUtil.writeText("  foo.Foo  ", f);
        // then
        Assert.assertEquals("foo.Foo", finder.findPluginClass(Arrays.asList(f.toURI().toURL())));
    }

    @Test
    public void single_implementation_disabled() throws Exception {
        File f = tmp.newFile();
        Mockito.when(switcher.isEnabled("foo.Foo")).thenReturn(false);
        // when
        IOUtil.writeText("  foo.Foo  ", f);
        // then
        Assert.assertEquals(null, finder.findPluginClass(Arrays.asList(f.toURI().toURL())));
    }

    @Test
    public void multiple_implementations_only_one_enabled() throws Exception {
        File f1 = tmp.newFile();
        File f2 = tmp.newFile();
        Mockito.when(switcher.isEnabled("Bar")).thenReturn(true);
        // when
        IOUtil.writeText("Foo", f1);
        IOUtil.writeText("Bar", f2);
        // then
        Assert.assertEquals("Bar", finder.findPluginClass(Arrays.asList(f1.toURI().toURL(), f2.toURI().toURL())));
    }

    @Test
    public void multiple_implementations_only_one_useful() throws Exception {
        File f1 = tmp.newFile();
        File f2 = tmp.newFile();
        Mockito.when(switcher.isEnabled(ArgumentMatchers.anyString())).thenReturn(true);
        // when
        IOUtil.writeText("   ", f1);
        IOUtil.writeText("X", f2);
        // then
        Assert.assertEquals("X", finder.findPluginClass(Arrays.asList(f1.toURI().toURL(), f2.toURI().toURL())));
    }

    @Test
    public void multiple_empty_implementations() throws Exception {
        File f1 = tmp.newFile();
        File f2 = tmp.newFile();
        Mockito.when(switcher.isEnabled(ArgumentMatchers.anyString())).thenReturn(true);
        // when
        IOUtil.writeText("   ", f1);
        IOUtil.writeText("\n", f2);
        // then
        Assert.assertEquals(null, finder.findPluginClass(Arrays.asList(f1.toURI().toURL(), f2.toURI().toURL())));
    }

    @Test
    public void problems_loading_impl() throws Exception {
        Mockito.when(switcher.isEnabled(ArgumentMatchers.anyString())).thenThrow(new RuntimeException("Boo!"));
        try {
            // when
            finder.findPluginClass(Arrays.asList(new File("xxx").toURI().toURL()));
            // then
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e).hasMessageContaining("xxx");
            e.getCause().getMessage().equals("Boo!");
        }
    }
}

