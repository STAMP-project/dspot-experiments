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
package org.sonar.server.platform;


import CoreProperties.SERVER_ID;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.SonarRuntime;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.Version;


public class ServerImplTest {
    private MapSettings settings = new MapSettings();

    private StartupMetadata state = Mockito.mock(StartupMetadata.class);

    private ServerFileSystem fs = Mockito.mock(ServerFileSystem.class);

    private UrlSettings urlSettings = Mockito.mock(UrlSettings.class);

    private SonarRuntime runtime = Mockito.mock(SonarRuntime.class);

    private ServerImpl underTest = new ServerImpl(settings.asConfig(), state, fs, urlSettings, runtime);

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void isDev_always_returns_false() {
        assertThat(underTest.isDev()).isFalse();
    }

    @Test
    public void test_url_information() {
        Mockito.when(urlSettings.getContextPath()).thenReturn("/foo");
        Mockito.when(urlSettings.getBaseUrl()).thenReturn("http://localhost:9000/foo");
        Mockito.when(urlSettings.isSecured()).thenReturn(false);
        assertThat(underTest.getContextPath()).isEqualTo("/foo");
        assertThat(underTest.getURL()).isEqualTo("http://localhost:9000/foo");
        assertThat(underTest.getPublicRootUrl()).isEqualTo("http://localhost:9000/foo");
        assertThat(underTest.isDev()).isFalse();
        assertThat(underTest.isSecured()).isFalse();
    }

    @Test
    public void test_file_system_information() throws IOException {
        File home = temp.newFolder();
        Mockito.when(fs.getHomeDir()).thenReturn(home);
        assertThat(underTest.getRootDir()).isEqualTo(home);
    }

    @Test
    public void test_startup_information() {
        long time = 123456789L;
        Mockito.when(state.getStartedAt()).thenReturn(time);
        assertThat(underTest.getStartedAt().getTime()).isEqualTo(time);
    }

    @Test
    public void test_id() {
        settings.setProperty(SERVER_ID, "foo");
        assertThat(underTest.getId()).isEqualTo("foo");
        assertThat(underTest.getPermanentServerId()).isEqualTo("foo");
    }

    @Test
    public void test_getVersion() {
        Version version = Version.create(6, 1);
        Mockito.when(runtime.getApiVersion()).thenReturn(version);
        assertThat(underTest.getVersion()).isEqualTo(version.toString());
    }
}

