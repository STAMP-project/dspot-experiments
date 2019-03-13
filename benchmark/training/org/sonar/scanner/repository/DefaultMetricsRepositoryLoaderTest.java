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
package org.sonar.scanner.repository;


import java.io.IOException;
import java.io.Reader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.scanner.WsTestUtil;
import org.sonar.scanner.bootstrap.ScannerWsClient;


public class DefaultMetricsRepositoryLoaderTest {
    private static final String WS_URL = "/api/metrics/search?f=name,description,direction,qualitative,custom&ps=500&p=";

    private ScannerWsClient wsClient;

    private DefaultMetricsRepositoryLoader metricsRepositoryLoader;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test() {
        MetricsRepository metricsRepository = metricsRepositoryLoader.load();
        assertThat(metricsRepository.metrics()).hasSize(3);
        WsTestUtil.verifyCall(wsClient, ((DefaultMetricsRepositoryLoaderTest.WS_URL) + "1"));
        WsTestUtil.verifyCall(wsClient, ((DefaultMetricsRepositoryLoaderTest.WS_URL) + "2"));
        Mockito.verifyNoMoreInteractions(wsClient);
    }

    @Test
    public void testIOError() throws IOException {
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(reader.read(ArgumentMatchers.any(char[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(new IOException());
        WsTestUtil.mockReader(wsClient, reader);
        exception.expect(IllegalStateException.class);
        metricsRepositoryLoader.load();
    }

    @Test
    public void testCloseError() throws IOException {
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(reader.read(ArgumentMatchers.any(char[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn((-1));
        Mockito.doThrow(new IOException()).when(reader).close();
        WsTestUtil.mockReader(wsClient, reader);
        exception.expect(IllegalStateException.class);
        metricsRepositoryLoader.load();
    }
}

