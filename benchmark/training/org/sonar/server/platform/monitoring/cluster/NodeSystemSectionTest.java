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
package org.sonar.server.platform.monitoring.cluster;


import ProtobufSystemInfo.Section;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.platform.Server;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;
import org.sonar.server.platform.OfficialDistribution;
import org.sonar.server.platform.monitoring.SystemInfoTesting;


public class NodeSystemSectionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MapSettings settings = new MapSettings();

    private Server server = Mockito.mock(Server.class, Mockito.RETURNS_DEEP_STUBS);

    private OfficialDistribution officialDistrib = Mockito.mock(OfficialDistribution.class);

    private NodeSystemSection underTest = new NodeSystemSection(settings.asConfig(), server, officialDistrib);

    @Test
    public void test_section_name() {
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        assertThat(section.getName()).isEqualTo("System");
    }

    @Test
    public void return_server_version() {
        Mockito.when(server.getVersion()).thenReturn("6.6");
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Version", "6.6");
    }

    @Test
    public void return_official_distribution_flag() {
        Mockito.when(officialDistrib.check()).thenReturn(true);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Official Distribution", true);
    }

    @Test
    public void return_nb_of_processors() {
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        assertThat(attribute(section, "Processors").getLongValue()).isGreaterThan(0);
    }

    @Test
    public void return_dir_paths() {
        settings.setProperty(PATH_HOME.getKey(), "/home");
        settings.setProperty(PATH_DATA.getKey(), "/data");
        settings.setProperty(PATH_TEMP.getKey(), "/temp");
        settings.setProperty(PATH_LOGS.getKey(), "/logs");
        settings.setProperty(PATH_WEB.getKey(), "/web");
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Home Dir", "/home");
        SystemInfoTesting.assertThatAttributeIs(section, "Data Dir", "/data");
        SystemInfoTesting.assertThatAttributeIs(section, "Temp Dir", "/temp");
        // logs dir is part of LoggingSection
        assertThat(attribute(section, "Logs Dir")).isNull();
        // for internal usage
        assertThat(attribute(section, "Web Dir")).isNull();
    }
}

