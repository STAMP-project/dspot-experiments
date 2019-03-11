/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.bootstrap;


import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.Plugin;
import org.sonar.core.platform.PluginInfo;
import org.sonar.core.platform.PluginLoader;


public class ScannerPluginRepositoryTest {
    PluginInstaller installer = Mockito.mock(PluginInstaller.class);

    PluginLoader loader = Mockito.mock(PluginLoader.class);

    ScannerPluginRepository underTest = new ScannerPluginRepository(installer, loader);

    @Test
    public void install_and_load_plugins() {
        PluginInfo info = new PluginInfo("squid");
        ImmutableMap<String, ScannerPlugin> plugins = ImmutableMap.of("squid", new ScannerPlugin("squid", 1L, info));
        Plugin instance = Mockito.mock(Plugin.class);
        Mockito.when(loader.load(ArgumentMatchers.anyMap())).thenReturn(ImmutableMap.of("squid", instance));
        Mockito.when(installer.installRemotes()).thenReturn(plugins);
        underTest.start();
        assertThat(underTest.getPluginInfos()).containsOnly(info);
        assertThat(underTest.getPluginsByKey()).isEqualTo(plugins);
        assertThat(underTest.getPluginInfo("squid")).isSameAs(info);
        assertThat(underTest.getPluginInstance("squid")).isSameAs(instance);
        underTest.stop();
        Mockito.verify(loader).unload(ArgumentMatchers.anyCollection());
    }

    @Test
    public void fail_if_requesting_missing_plugin() {
        underTest.start();
        try {
            underTest.getPluginInfo("unknown");
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Plugin [unknown] does not exist");
        }
        try {
            underTest.getPluginInstance("unknown");
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Plugin [unknown] does not exist");
        }
    }
}

