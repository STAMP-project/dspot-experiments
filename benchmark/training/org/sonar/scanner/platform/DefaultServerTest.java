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
package org.sonar.scanner.platform;


import CoreProperties.SERVER_BASE_URL;
import CoreProperties.SERVER_ID;
import CoreProperties.SERVER_STARTTIME;
import SonarQubeSide.SCANNER;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.Settings;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;
import org.sonar.scanner.bootstrap.ScannerWsClient;


public class DefaultServerTest {
    @Test
    public void shouldLoadServerProperties() {
        Settings settings = new MapSettings();
        settings.setProperty(SERVER_ID, "123");
        settings.setProperty(SERVER_STARTTIME, "2010-05-18T17:59:00+0000");
        ScannerWsClient client = Mockito.mock(ScannerWsClient.class);
        Mockito.when(client.baseUrl()).thenReturn("http://foo.com");
        DefaultServer metadata = new DefaultServer(asConfig(), client, SonarRuntimeImpl.forSonarQube(Version.parse("2.2"), SCANNER));
        assertThat(metadata.getId()).isEqualTo("123");
        assertThat(metadata.getVersion()).isEqualTo("2.2");
        assertThat(metadata.getStartedAt()).isNotNull();
        assertThat(metadata.getURL()).isEqualTo("http://foo.com");
        assertThat(metadata.getPermanentServerId()).isEqualTo("123");
        assertThat(metadata.getRootDir()).isNull();
        assertThat(metadata.getContextPath()).isNull();
        assertThat(metadata.isDev()).isFalse();
        assertThat(metadata.isSecured()).isFalse();
    }

    @Test
    public void publicRootUrl() {
        Settings settings = new MapSettings();
        ScannerWsClient client = Mockito.mock(ScannerWsClient.class);
        Mockito.when(client.baseUrl()).thenReturn("http://foo.com/");
        DefaultServer metadata = new DefaultServer(asConfig(), client, null);
        settings.setProperty(SERVER_BASE_URL, "http://server.com/");
        assertThat(metadata.getPublicRootUrl()).isEqualTo("http://server.com");
        settings.removeProperty(SERVER_BASE_URL);
        assertThat(metadata.getPublicRootUrl()).isEqualTo("http://foo.com");
    }

    @Test(expected = RuntimeException.class)
    public void invalid_startup_date_throws_exception() {
        Settings settings = new MapSettings();
        settings.setProperty(SERVER_STARTTIME, "invalid");
        ScannerWsClient client = Mockito.mock(ScannerWsClient.class);
        DefaultServer metadata = new DefaultServer(asConfig(), client, null);
        metadata.getStartedAt();
    }
}

