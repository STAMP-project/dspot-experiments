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
package org.sonar.ce.logging;


import HttpAction.ActionRegistry;
import LoggerLevel.DEBUG;
import LoggerLevel.ERROR;
import LoggerLevel.INFO;
import LoggerLevel.TRACE;
import NanoHTTPD.Response;
import com.google.common.collect.ImmutableMap;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.httpd.CeHttpUtils;
import org.sonar.ce.httpd.HttpAction;
import org.sonar.server.log.ServerLogging;


public class ChangeLogLevelHttpActionTest {
    private ServerLogging serverLogging = Mockito.mock(ServerLogging.class);

    private ChangeLogLevelHttpAction underTest = new ChangeLogLevelHttpAction(serverLogging);

    @Test
    public void register_to_path_changeLogLevel() {
        HttpAction.ActionRegistry actionRegistry = Mockito.mock(ActionRegistry.class);
        underTest.register(actionRegistry);
        Mockito.verify(actionRegistry).register("changeLogLevel", underTest);
    }

    @Test
    public void serves_METHOD_NOT_ALLOWED_error_when_method_is_not_POST() {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.GET));
        assertThat(response.getStatus()).isEqualTo(Status.METHOD_NOT_ALLOWED);
    }

    @Test
    public void serves_BAD_REQUEST_error_when_parameter_level_is_missing() throws IOException {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST));
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST);
        assertThat(IOUtils.toString(response.getData())).isEqualTo("Parameter 'level' is missing");
    }

    @Test
    public void serves_BAD_REQUEST_error_when_value_of_parameter_level_is_not_LEVEL_in_uppercase() throws IOException {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST, ImmutableMap.of("level", "info")));
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST);
        assertThat(IOUtils.toString(response.getData())).isEqualTo("Value 'info' for parameter 'level' is invalid");
    }

    @Test
    public void changes_server_logging_if_level_is_ERROR() {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST, ImmutableMap.of("level", "ERROR")));
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        Mockito.verify(serverLogging).changeLevel(ERROR);
    }

    @Test
    public void changes_server_logging_if_level_is_INFO() {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST, ImmutableMap.of("level", "INFO")));
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        Mockito.verify(serverLogging).changeLevel(INFO);
    }

    @Test
    public void changes_server_logging_if_level_is_DEBUG() {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST, ImmutableMap.of("level", "DEBUG")));
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        Mockito.verify(serverLogging).changeLevel(DEBUG);
    }

    @Test
    public void changes_server_logging_if_level_is_TRACE() {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST, ImmutableMap.of("level", "TRACE")));
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        Mockito.verify(serverLogging).changeLevel(TRACE);
    }
}

