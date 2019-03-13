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
package org.sonar.server.updatecenter.ws;


import java.io.File;
import java.io.InputStream;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class UploadActionTest {
    private static final String PLUGIN_NAME = "plugin.jar";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private ServerFileSystem fileSystem = Mockito.mock(ServerFileSystem.class);

    private File pluginDirectory;

    private File plugin = new File(getClass().getResource("UploadActionTest/plugin.jar").getFile());

    private WsActionTester wsTester;

    @Test
    public void upload_plugin() throws Exception {
        logInAsSystemAdministrator();
        TestResponse response = call(Files.newInputStream(plugin.toPath()), UploadActionTest.PLUGIN_NAME);
        assertThat(response.getStatus()).isEqualTo(204);
        assertPluginIsUploaded(UploadActionTest.PLUGIN_NAME);
    }

    @Test
    public void erase_existing_plugin_if_already_exists() throws Exception {
        logInAsSystemAdministrator();
        File plugin1 = new File(getClass().getResource("UploadActionTest/plugin.jar").getFile());
        call(Files.newInputStream(plugin1.toPath()), UploadActionTest.PLUGIN_NAME);
        File plugin2 = new File(getClass().getResource("UploadActionTest/anotherPlugin.jar").getFile());
        call(Files.newInputStream(plugin2.toPath()), UploadActionTest.PLUGIN_NAME);
        File result = new File(pluginDirectory, UploadActionTest.PLUGIN_NAME);
        assertThat(result.exists()).isTrue();
        assertThat(result.length()).isNotEqualTo(plugin1.length()).isEqualTo(plugin2.length());
    }

    @Test
    public void fail_when_plugin_extension_is_not_jar() throws Exception {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Only jar file is allowed");
        call(Files.newInputStream(plugin.toPath()), "plugin.zip");
    }

    @Test
    public void fail_when_no_files_param() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'file' parameter is missing");
        wsTester.newRequest().execute();
    }

    @Test
    public void input_stream_should_be_closed() throws Exception {
        logInAsSystemAdministrator();
        InputStream inputStream = Files.newInputStream(plugin.toPath());
        call(inputStream, UploadActionTest.PLUGIN_NAME);
        // As the same InputStream is used, it will fail as it should have been called during the first execution of the WS
        expectedException.expectCause(hasType(ClosedChannelException.class));
        call(inputStream, UploadActionTest.PLUGIN_NAME);
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() throws Exception {
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        call(Files.newInputStream(plugin.toPath()), UploadActionTest.PLUGIN_NAME);
    }
}

