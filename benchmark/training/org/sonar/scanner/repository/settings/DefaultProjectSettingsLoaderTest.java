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
package org.sonar.scanner.repository.settings;


import Settings.Setting;
import Settings.ValuesWsResponse;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.scanner.bootstrap.ProcessedScannerProperties;
import org.sonar.scanner.bootstrap.ScannerWsClient;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.WsResponse;


public class DefaultProjectSettingsLoaderTest {
    private ScannerWsClient wsClient = Mockito.mock(ScannerWsClient.class);

    private ProcessedScannerProperties properties = Mockito.mock(ProcessedScannerProperties.class);

    private DefaultProjectSettingsLoader underTest = new DefaultProjectSettingsLoader(wsClient, properties);

    @Test
    public void loadProjectSettings() throws IOException {
        WsResponse response = Mockito.mock(WsResponse.class);
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        ValuesWsResponse.newBuilder().addSettings(Setting.newBuilder().setKey("abc").setValue("def").build()).addSettings(Setting.newBuilder().setKey("123").setValue("456").build()).build().writeTo(out);
        out.close();
        Mockito.when(response.contentStream()).thenReturn(in);
        Mockito.when(wsClient.call(ArgumentMatchers.any())).thenReturn(response);
        Mockito.when(properties.getKeyWithBranch()).thenReturn("project_key");
        Map<String, String> result = underTest.loadProjectSettings();
        ArgumentCaptor<GetRequest> argumentCaptor = ArgumentCaptor.forClass(GetRequest.class);
        Mockito.verify(wsClient, Mockito.times(1)).call(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getPath()).isEqualTo("api/settings/values.protobuf?component=project_key");
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get("abc")).isEqualTo("def");
        assertThat(result.get("123")).isEqualTo("456");
    }
}

