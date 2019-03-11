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
package org.sonar.server.batch;


import WebService.Action;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.server.ws.WsActionTester;


public class FileActionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ServerFileSystem serverFileSystem = Mockito.mock(ServerFileSystem.class);

    private BatchIndex batchIndex = new BatchIndex(serverFileSystem);

    private File batchDir;

    private WsActionTester tester = new WsActionTester(new FileAction(batchIndex));

    @Test
    public void download_file() throws Exception {
        FileUtils.writeStringToFile(new File(batchDir, "sonar-batch.jar"), "foo");
        FileUtils.writeStringToFile(new File(batchDir, "other.jar"), "bar");
        batchIndex.start();
        String jar = tester.newRequest().setParam("name", "sonar-batch.jar").execute().getInput();
        assertThat(jar).isEqualTo("foo");
    }

    @Test
    public void throw_NotFoundException_when_file_does_not_exist() throws Exception {
        FileUtils.writeStringToFile(new File(batchDir, "sonar-batch.jar"), "foo");
        batchIndex.start();
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Bad filename: unknown");
        tester.newRequest().setParam("name", "unknown").execute();
    }

    @Test
    public void throw_IAE_when_no_name_parameter() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The 'name' parameter is missing");
        tester.newRequest().execute();
    }

    @Test
    public void test_definition() {
        WebService.Action definition = tester.getDef();
        assertThat(definition.isInternal()).isTrue();
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.responseExampleAsString()).isNotEmpty();
        assertThat(definition.params()).extracting(WebService.Param::key).containsOnly("name");
    }
}

