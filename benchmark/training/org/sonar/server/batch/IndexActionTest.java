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
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.server.ws.WsActionTester;


public class IndexActionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ServerFileSystem serverFileSystem = Mockito.mock(ServerFileSystem.class);

    private BatchIndex batchIndex = new BatchIndex(serverFileSystem);

    private File batchDir;

    private WsActionTester tester = new WsActionTester(new IndexAction(batchIndex));

    @Test
    public void get_index() throws Exception {
        FileUtils.writeStringToFile(new File(batchDir, "sonar-batch.jar"), "something");
        batchIndex.start();
        String index = tester.newRequest().execute().getInput();
        assertThat(index).startsWith("sonar-batch.jar|");
    }

    @Test
    public void throw_ISE_when_no_file() {
        thrown.expect(IllegalStateException.class);
        tester.newRequest().execute();
    }

    @Test
    public void test_definition() {
        WebService.Action definition = tester.getDef();
        assertThat(definition.isInternal()).isTrue();
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.responseExampleAsString()).isNotEmpty();
        assertThat(definition.params()).isEmpty();
    }
}

