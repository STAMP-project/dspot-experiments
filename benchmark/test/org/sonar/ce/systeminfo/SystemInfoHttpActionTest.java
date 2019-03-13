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
package org.sonar.ce.systeminfo;


import HttpAction.ActionRegistry;
import NanoHTTPD.Response;
import ProtobufSystemInfo.SystemInfo;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.ce.httpd.CeHttpUtils;
import org.sonar.ce.httpd.HttpAction;
import org.sonar.process.systeminfo.JvmStateSection;
import org.sonar.process.systeminfo.SystemInfoSection;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;


public class SystemInfoHttpActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SystemInfoSection stateProvider1 = new JvmStateSection("state1");

    private SystemInfoSection stateProvider2 = new JvmStateSection("state2");

    private SystemInfoHttpAction underTest;

    @Test
    public void register_to_path_systemInfo() {
        HttpAction.ActionRegistry actionRegistry = Mockito.mock(ActionRegistry.class);
        underTest.register(actionRegistry);
        Mockito.verify(actionRegistry).register("systemInfo", underTest);
    }

    @Test
    public void serves_METHOD_NOT_ALLOWED_error_when_method_is_not_GET() {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.POST));
        assertThat(response.getStatus()).isEqualTo(Status.METHOD_NOT_ALLOWED);
    }

    @Test
    public void serves_data_from_SystemInfoSections() throws Exception {
        NanoHTTPD.Response response = underTest.serve(CeHttpUtils.createHttpSession(Method.GET));
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        ProtobufSystemInfo.SystemInfo systemInfo = SystemInfo.parseFrom(response.getData());
        assertThat(systemInfo.getSectionsCount()).isEqualTo(2);
        assertThat(systemInfo.getSections(0).getName()).isEqualTo("state1");
        assertThat(systemInfo.getSections(1).getName()).isEqualTo("state2");
    }
}

