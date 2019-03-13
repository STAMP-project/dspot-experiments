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
package org.sonar.server.platform.ws;


import java.io.StringWriter;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.server.health.HealthChecker;
import org.sonar.server.platform.monitoring.cluster.AppNodesInfoLoader;
import org.sonar.server.platform.monitoring.cluster.GlobalInfoLoader;
import org.sonar.server.platform.monitoring.cluster.SearchNodesInfoLoader;
import org.sonar.server.telemetry.TelemetryDataLoader;


public class ClusterSystemInfoWriterTest {
    private GlobalInfoLoader globalInfoLoader = Mockito.mock(GlobalInfoLoader.class);

    private AppNodesInfoLoader appNodesInfoLoader = Mockito.mock(AppNodesInfoLoader.class);

    private SearchNodesInfoLoader searchNodesInfoLoader = Mockito.mock(SearchNodesInfoLoader.class);

    private HealthChecker healthChecker = Mockito.mock(HealthChecker.class);

    private TelemetryDataLoader telemetry = Mockito.mock(TelemetryDataLoader.class, Mockito.RETURNS_MOCKS);

    private ClusterSystemInfoWriter underTest = new ClusterSystemInfoWriter(globalInfoLoader, appNodesInfoLoader, searchNodesInfoLoader, healthChecker, telemetry);

    @Test
    public void writeInfo() throws InterruptedException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = JsonWriter.of(writer);
        jsonWriter.beginObject();
        underTest.write(jsonWriter);
        jsonWriter.endObject();
        assertThat(writer.toString()).isEqualTo(("{\"Health\":\"GREEN\"," + (((("\"Health Causes\":[],\"\":{\"name\":\"globalInfo\"}," + "\"Application Nodes\":[{\"Name\":\"appNodes\",\"\":{\"name\":\"appNodes\"}}],") + "\"Search Nodes\":[{\"Name\":\"searchNodes\",\"\":{\"name\":\"searchNodes\"}}],") + "\"Statistics\":{\"id\":\"\",\"version\":\"\",\"database\":{\"name\":\"\",\"version\":\"\"},\"plugins\":[],") + "\"userCount\":0,\"projectCount\":0,\"usingBranches\":false,\"ncloc\":0,\"projectCountByLanguage\":[],\"nclocByLanguage\":[]}}")));
    }
}

