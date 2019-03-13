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


import ProcessProperties.Property.SONAR_UPDATECENTER_ACTIVATE;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.UriReader;
import org.sonar.updatecenter.common.UpdateCenter;
import org.sonar.updatecenter.common.Version;


public class UpdateCenterClientTest {
    private static final String BASE_URL = "https://update.sonarsource.org";

    private UriReader reader = Mockito.mock(UriReader.class);

    private MapSettings settings = new MapSettings();

    private UpdateCenterClient underTest;

    @Test
    public void downloadUpdateCenter() throws URISyntaxException {
        Mockito.when(reader.readString(new URI(UpdateCenterClientTest.BASE_URL), StandardCharsets.UTF_8)).thenReturn("publicVersions=2.2,2.3");
        UpdateCenter plugins = underTest.getUpdateCenter().get();
        Mockito.verify(reader, Mockito.times(1)).readString(new URI(UpdateCenterClientTest.BASE_URL), StandardCharsets.UTF_8);
        assertThat(plugins.getSonar().getVersions()).containsOnly(Version.create("2.2"), Version.create("2.3"));
        assertThat(underTest.getLastRefreshDate()).isNotNull();
    }

    @Test
    public void not_available_before_initialization() {
        assertThat(underTest.getLastRefreshDate()).isNull();
    }

    @Test
    public void ignore_connection_errors() {
        Mockito.when(reader.readString(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(StandardCharsets.UTF_8))).thenThrow(new SonarException());
        assertThat(underTest.getUpdateCenter()).isAbsent();
    }

    @Test
    public void cache_data() throws Exception {
        Mockito.when(reader.readString(new URI(UpdateCenterClientTest.BASE_URL), StandardCharsets.UTF_8)).thenReturn("sonar.versions=2.2,2.3");
        underTest.getUpdateCenter();
        underTest.getUpdateCenter();
        Mockito.verify(reader, Mockito.times(1)).readString(new URI(UpdateCenterClientTest.BASE_URL), StandardCharsets.UTF_8);
    }

    @Test
    public void forceRefresh() throws Exception {
        Mockito.when(reader.readString(new URI(UpdateCenterClientTest.BASE_URL), StandardCharsets.UTF_8)).thenReturn("sonar.versions=2.2,2.3");
        underTest.getUpdateCenter();
        underTest.getUpdateCenter(true);
        Mockito.verify(reader, Mockito.times(2)).readString(new URI(UpdateCenterClientTest.BASE_URL), StandardCharsets.UTF_8);
    }

    @Test
    public void update_center_is_null_when_property_is_false() {
        settings.setProperty(SONAR_UPDATECENTER_ACTIVATE.getKey(), false);
        assertThat(underTest.getUpdateCenter()).isAbsent();
    }
}

