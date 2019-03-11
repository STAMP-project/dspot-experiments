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
package org.sonar.server.platform.monitoring;


import ProtobufSystemInfo.Section;
import SonarQubeSide.COMPUTE_ENGINE;
import SonarQubeSide.SERVER;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.SonarRuntime;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;
import org.sonar.server.log.ServerLogging;


public class LoggingSectionTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private SonarRuntime runtime = Mockito.mock(SonarRuntime.class);

    private ServerLogging logging = Mockito.mock(ServerLogging.class);

    private File logDir;

    private LoggingSection underTest = new LoggingSection(runtime, logging);

    @Test
    public void return_logging_attributes_of_compute_engine() {
        Mockito.when(runtime.getSonarQubeSide()).thenReturn(COMPUTE_ENGINE);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        assertThat(section.getName()).isEqualTo("Compute Engine Logging");
        SystemInfoTesting.assertThatAttributeIs(section, "Logs Dir", logDir.getAbsolutePath());
        SystemInfoTesting.assertThatAttributeIs(section, "Logs Level", "DEBUG");
    }

    @Test
    public void return_logging_attributes_of_web_server() {
        Mockito.when(runtime.getSonarQubeSide()).thenReturn(SERVER);
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        assertThat(section.getName()).isEqualTo("Web Logging");
        SystemInfoTesting.assertThatAttributeIs(section, "Logs Dir", logDir.getAbsolutePath());
        SystemInfoTesting.assertThatAttributeIs(section, "Logs Level", "DEBUG");
    }
}

