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
package org.sonar.server.plugins;


import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.server.platform.ServerFileSystem;


public class PluginUninstallerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private File uninstallDir;

    private PluginUninstaller underTest;

    private ServerPluginRepository serverPluginRepository;

    private ServerFileSystem fs;

    @Test
    public void uninstall() {
        Mockito.when(serverPluginRepository.hasPlugin("plugin")).thenReturn(true);
        underTest.uninstall("plugin");
        Mockito.verify(serverPluginRepository).uninstall("plugin", uninstallDir);
    }

    @Test
    public void fail_uninstall_if_plugin_not_installed() {
        Mockito.when(serverPluginRepository.hasPlugin("plugin")).thenReturn(false);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Plugin [plugin] is not installed");
        underTest.uninstall("plugin");
        Mockito.verifyZeroInteractions(serverPluginRepository);
    }

    @Test
    public void create_uninstall_dir() {
        File dir = new File(testFolder.getRoot(), "dir");
        Mockito.when(fs.getUninstalledPluginsDir()).thenReturn(dir);
        underTest = new PluginUninstaller(serverPluginRepository, fs);
        underTest.start();
        assertThat(dir).isDirectory();
    }

    @Test
    public void cancel() {
        underTest.cancelUninstalls();
        Mockito.verify(serverPluginRepository).cancelUninstalls(uninstallDir);
        Mockito.verifyNoMoreInteractions(serverPluginRepository);
    }

    @Test
    public void list_uninstalled_plugins() throws IOException {
        new File(uninstallDir, "file1").createNewFile();
        copyTestPluginTo("test-base-plugin", uninstallDir);
        assertThat(underTest.getUninstalledPlugins()).extracting("key").containsOnly("testbase");
    }
}

