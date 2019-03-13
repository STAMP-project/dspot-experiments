/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.diagnostics;


import Diagnostics.ENABLED;
import Diagnostics.FILENAME_PREFIX;
import DiagnosticsPlugin.STATIC;
import com.hazelcast.config.Config;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DiagnosticsTest extends HazelcastTestSupport {
    @Test
    public void testDisabledByDefault() {
        Config config = new Config();
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        Assert.assertFalse("Diagnostics should be disabled by default", hazelcastProperties.getBoolean(ENABLED));
    }

    @Test
    public void whenFileNamePrefixSet() {
        Config config = new Config().setProperty(FILENAME_PREFIX.getName(), "foobar");
        HazelcastProperties hzProperties = new HazelcastProperties(config);
        Diagnostics diagnostics = new Diagnostics("diagnostics", Logger.getLogger(Diagnostics.class), "hz", hzProperties);
        Assert.assertEquals("foobar-diagnostics", diagnostics.baseFileName);
    }

    @Test
    public void whenFileNamePrefixNotSet() {
        Config config = new Config();
        HazelcastProperties hzProperties = new HazelcastProperties(config);
        Diagnostics diagnostics = new Diagnostics("diagnostics", Logger.getLogger(Diagnostics.class), "hz", hzProperties);
        Assert.assertEquals("diagnostics", diagnostics.baseFileName);
    }

    @Test(expected = NullPointerException.class)
    public void register_whenNullPlugin() throws Exception {
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "true"));
        diagnostics.start();
        diagnostics.register(null);
    }

    @Test
    public void register_whenMonitorDisabled() throws Exception {
        DiagnosticsPlugin plugin = Mockito.mock(DiagnosticsPlugin.class);
        Mockito.when(plugin.getPeriodMillis()).thenReturn(1L);
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "false"));
        diagnostics.start();
        diagnostics.register(plugin);
        Assert.assertEquals(0, diagnostics.staticTasks.get().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void register_whenMonitorEnabled_andPluginReturnsValueSmallerThanMinesOne() throws Exception {
        DiagnosticsPlugin plugin = Mockito.mock(DiagnosticsPlugin.class);
        Mockito.when(plugin.getPeriodMillis()).thenReturn((-2L));
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "true"));
        diagnostics.start();
        diagnostics.register(plugin);
    }

    @Test
    public void register_whenMonitorEnabled_andPluginDisabled() throws Exception {
        DiagnosticsPlugin plugin = Mockito.mock(DiagnosticsPlugin.class);
        Mockito.when(plugin.getPeriodMillis()).thenReturn(0L);
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "true"));
        diagnostics.start();
        diagnostics.register(plugin);
        Assert.assertEquals(0, diagnostics.staticTasks.get().length);
    }

    @Test
    public void register_whenMonitorEnabled_andPluginStatic() throws Exception {
        DiagnosticsPlugin plugin = Mockito.mock(DiagnosticsPlugin.class);
        Mockito.when(plugin.getPeriodMillis()).thenReturn(STATIC);
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "true"));
        diagnostics.start();
        diagnostics.register(plugin);
        Assert.assertArrayEquals(new DiagnosticsPlugin[]{ plugin }, diagnostics.staticTasks.get());
    }

    @Test
    public void start_whenDisabled() throws Exception {
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "false"));
        diagnostics.start();
        Assert.assertNull("DiagnosticsLogFile should be null", diagnostics.diagnosticsLogFile);
    }

    @Test
    public void start_whenEnabled() throws Exception {
        Diagnostics diagnostics = newDiagnostics(new Config().setProperty(ENABLED.getName(), "true"));
        diagnostics.start();
        Assert.assertNotNull("DiagnosticsLogFile should not be null", diagnostics.diagnosticsLogFile);
    }
}

