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
package org.sonar.server.plugins.ws;


import WebService.Action;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.plugins.InstalledPlugin;
import org.sonar.server.plugins.PluginFileSystem;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsAction;
import org.sonar.server.ws.WsActionTester;


public class DownloadActionTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PluginFileSystem pluginFileSystem = Mockito.mock(PluginFileSystem.class);

    private WsAction underTest = new DownloadAction(pluginFileSystem);

    private WsActionTester tester = new WsActionTester(underTest);

    @Test
    public void test_definition() {
        WebService.Action def = tester.getDef();
        assertThat(def.isInternal()).isTrue();
        assertThat(def.since()).isEqualTo("7.2");
        assertThat(def.params()).extracting(WebService.Param::key).containsExactlyInAnyOrder("plugin", "acceptCompressions");
    }

    @Test
    public void return_404_if_plugin_not_found() {
        Mockito.when(pluginFileSystem.getInstalledPlugin("foo")).thenReturn(Optional.empty());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Plugin foo not found");
        tester.newRequest().setParam("plugin", "foo").execute();
    }

    @Test
    public void return_jar_if_plugin_exists() throws Exception {
        InstalledPlugin plugin = newPlugin();
        Mockito.when(pluginFileSystem.getInstalledPlugin(plugin.getPluginInfo().getKey())).thenReturn(Optional.of(plugin));
        TestResponse response = tester.newRequest().setParam("plugin", plugin.getPluginInfo().getKey()).execute();
        assertThat(response.getHeader("Sonar-MD5")).isEqualTo(plugin.getLoadedJar().getMd5());
        assertThat(response.getHeader("Sonar-Compression")).isNull();
        assertThat(response.getMediaType()).isEqualTo("application/java-archive");
        DownloadActionTest.verifySameContent(response, plugin.getLoadedJar().getFile());
    }

    @Test
    public void return_uncompressed_jar_if_client_does_not_accept_compression() throws Exception {
        InstalledPlugin plugin = newCompressedPlugin();
        Mockito.when(pluginFileSystem.getInstalledPlugin(plugin.getPluginInfo().getKey())).thenReturn(Optional.of(plugin));
        TestResponse response = tester.newRequest().setParam("plugin", plugin.getPluginInfo().getKey()).execute();
        assertThat(response.getHeader("Sonar-MD5")).isEqualTo(plugin.getLoadedJar().getMd5());
        assertThat(response.getHeader("Sonar-Compression")).isNull();
        assertThat(response.getHeader("Sonar-UncompressedMD5")).isNull();
        assertThat(response.getMediaType()).isEqualTo("application/java-archive");
        DownloadActionTest.verifySameContent(response, plugin.getLoadedJar().getFile());
    }

    @Test
    public void return_uncompressed_jar_if_client_requests_unsupported_compression() throws Exception {
        InstalledPlugin plugin = newCompressedPlugin();
        Mockito.when(pluginFileSystem.getInstalledPlugin(plugin.getPluginInfo().getKey())).thenReturn(Optional.of(plugin));
        TestResponse response = tester.newRequest().setParam("plugin", plugin.getPluginInfo().getKey()).setParam("acceptCompressions", "zip").execute();
        assertThat(response.getHeader("Sonar-MD5")).isEqualTo(plugin.getLoadedJar().getMd5());
        assertThat(response.getHeader("Sonar-Compression")).isNull();
        assertThat(response.getHeader("Sonar-UncompressedMD5")).isNull();
        assertThat(response.getMediaType()).isEqualTo("application/java-archive");
        DownloadActionTest.verifySameContent(response, plugin.getLoadedJar().getFile());
    }

    @Test
    public void return_compressed_jar_if_client_accepts_pack200() throws Exception {
        InstalledPlugin plugin = newCompressedPlugin();
        Mockito.when(pluginFileSystem.getInstalledPlugin(plugin.getPluginInfo().getKey())).thenReturn(Optional.of(plugin));
        TestResponse response = tester.newRequest().setParam("plugin", plugin.getPluginInfo().getKey()).setParam("acceptCompressions", "pack200").execute();
        assertThat(response.getHeader("Sonar-MD5")).isEqualTo(plugin.getCompressedJar().getMd5());
        assertThat(response.getHeader("Sonar-UncompressedMD5")).isEqualTo(plugin.getLoadedJar().getMd5());
        assertThat(response.getHeader("Sonar-Compression")).isEqualTo("pack200");
        assertThat(response.getMediaType()).isEqualTo("application/octet-stream");
        DownloadActionTest.verifySameContent(response, plugin.getCompressedJar().getFile());
    }
}

