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
import java.io.InputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.utils.MessageException;
import org.sonar.scanner.WsTestUtil;
import org.sonar.scanner.bootstrap.ScannerWsClient;
import org.sonarqube.ws.client.HttpException;
import org.sonarqube.ws.client.WsRequest;


public class DefaultProjectRepositoriesLoaderTest {
    private static final String PROJECT_KEY = "foo?";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DefaultProjectRepositoriesLoader loader;

    private ScannerWsClient wsClient;

    @Test
    public void continueOnError() {
        Mockito.when(wsClient.call(ArgumentMatchers.any(WsRequest.class))).thenThrow(IllegalStateException.class);
        ProjectRepositories proj = loader.load(DefaultProjectRepositoriesLoaderTest.PROJECT_KEY, null);
        assertThat(proj.exists()).isEqualTo(false);
    }

    @Test
    public void parsingError() throws IOException {
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.when(is.read()).thenThrow(IOException.class);
        WsTestUtil.mockStream(wsClient, "/batch/project.protobuf?key=foo%3F", is);
        loader.load(DefaultProjectRepositoriesLoaderTest.PROJECT_KEY, null);
    }

    @Test(expected = IllegalStateException.class)
    public void failFastHttpError() {
        HttpException http = new HttpException("url", 403, null);
        IllegalStateException e = new IllegalStateException("http error", http);
        WsTestUtil.mockException(wsClient, e);
        loader.load(DefaultProjectRepositoriesLoaderTest.PROJECT_KEY, null);
    }

    @Test
    public void failFastHttpErrorMessageException() {
        thrown.expect(MessageException.class);
        thrown.expectMessage("http error");
        HttpException http = new HttpException("uri", 403, null);
        MessageException e = MessageException.of("http error", http);
        WsTestUtil.mockException(wsClient, e);
        loader.load(DefaultProjectRepositoriesLoaderTest.PROJECT_KEY, null);
    }

    @Test
    public void deserializeResponse() throws IOException {
        loader.load(DefaultProjectRepositoriesLoaderTest.PROJECT_KEY, null);
    }

    @Test
    public void passAndEncodeProjectKeyParameter() {
        loader.load(DefaultProjectRepositoriesLoaderTest.PROJECT_KEY, null);
        WsTestUtil.verifyCall(wsClient, "/batch/project.protobuf?key=foo%3F");
    }

    @Test
    public void readRealResponse() throws IOException {
        InputStream is = getTestResource("project.protobuf");
        WsTestUtil.mockStream(wsClient, "/batch/project.protobuf?key=org.sonarsource.github%3Asonar-github-plugin", is);
        DefaultInputFile file = Mockito.mock(DefaultInputFile.class);
        Mockito.when(file.getModuleRelativePath()).thenReturn("src/test/java/org/sonar/plugins/github/PullRequestIssuePostJobTest.java");
        ProjectRepositories proj = loader.load("org.sonarsource.github:sonar-github-plugin", null);
        FileData fd = proj.fileData("org.sonarsource.github:sonar-github-plugin", file);
        assertThat(fd.revision()).isEqualTo("27bf2c54633d05c5df402bbe09471fe43bd9e2e5");
        assertThat(fd.hash()).isEqualTo("edb6b3b9ab92d8dc53ba90ab86cd422e");
    }
}

