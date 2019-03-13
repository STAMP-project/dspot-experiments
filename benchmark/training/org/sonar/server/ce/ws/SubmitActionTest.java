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
package org.sonar.server.ce.ws;


import Ce.SubmitResponse;
import CeTask.Component;
import CeTaskTypes.REPORT;
import MediaTypes.JSON;
import MediaTypes.PROTOBUF;
import com.google.common.base.Strings;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.sonar.ce.task.CeTask;
import org.sonar.server.ce.queue.ReportSubmitter;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;
import org.sonar.test.JsonAssert;
import org.sonarqube.ws.Ce;


public class SubmitActionTest {
    private static final String PROJECT_UUID = "PROJECT_1";

    private static final Component COMPONENT = new CeTask.Component(SubmitActionTest.PROJECT_UUID, "KEY_1", "NAME_1");

    private static final CeTask A_CE_TASK = new CeTask.Builder().setOrganizationUuid("org1").setUuid("TASK_1").setType(REPORT).setComponent(SubmitActionTest.COMPONENT).setMainComponent(SubmitActionTest.COMPONENT).setSubmitter(new CeTask.User("UUID_1", "LOGIN_1")).build();

    @Captor
    ArgumentCaptor<Map<String, String>> map;

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.fromUuid("org1");

    private String organizationKey = defaultOrganizationProvider.get().getKey();

    private ReportSubmitter reportSubmitter = Mockito.mock(ReportSubmitter.class);

    private SubmitAction underTest = new SubmitAction(reportSubmitter, defaultOrganizationProvider);

    private WsActionTester tester = new WsActionTester(underTest);

    @Test
    public void submit_task_to_the_queue_and_ask_for_immediate_processing() {
        Mockito.when(reportSubmitter.submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("My Project"), ArgumentMatchers.anyMap(), ArgumentMatchers.any())).thenReturn(SubmitActionTest.A_CE_TASK);
        Ce.SubmitResponse submitResponse = tester.newRequest().setParam("projectKey", "my_project").setParam("projectName", "My Project").setPart("report", new ByteArrayInputStream("{binary}".getBytes()), "foo.bar").setMethod("POST").executeProtobuf(SubmitResponse.class);
        Mockito.verify(reportSubmitter).submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("My Project"), ArgumentMatchers.anyMap(), ArgumentMatchers.any());
        assertThat(submitResponse.getTaskId()).isEqualTo("TASK_1");
        assertThat(submitResponse.getProjectId()).isEqualTo(SubmitActionTest.PROJECT_UUID);
    }

    @Test
    public void submit_task_with_characteristics() {
        Mockito.when(reportSubmitter.submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("My Project"), ArgumentMatchers.anyMap(), ArgumentMatchers.any())).thenReturn(SubmitActionTest.A_CE_TASK);
        String[] characteristics = new String[]{ "branch=foo", "pullRequest=123", "unsupported=bar" };
        Ce.SubmitResponse submitResponse = tester.newRequest().setParam("projectKey", "my_project").setParam("projectName", "My Project").setMultiParam("characteristic", Arrays.asList(characteristics)).setPart("report", new ByteArrayInputStream("{binary}".getBytes()), "foo.bar").setMethod("POST").executeProtobuf(SubmitResponse.class);
        assertThat(submitResponse.getTaskId()).isEqualTo("TASK_1");
        Mockito.verify(reportSubmitter).submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("My Project"), map.capture(), ArgumentMatchers.any());
        // unsupported characteristics are ignored
        assertThat(map.getValue()).containsExactly(entry("branch", "foo"), entry("pullRequest", "123"));
    }

    @Test
    public void abbreviate_long_name() {
        String longName = Strings.repeat("a", 1000);
        String expectedName = (Strings.repeat("a", 497)) + "...";
        Mockito.when(reportSubmitter.submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq(expectedName), ArgumentMatchers.anyMap(), ArgumentMatchers.any())).thenReturn(SubmitActionTest.A_CE_TASK);
        Ce.SubmitResponse submitResponse = tester.newRequest().setParam("projectKey", "my_project").setParam("projectName", longName).setPart("report", new ByteArrayInputStream("{binary}".getBytes()), "foo.bar").setMethod("POST").executeProtobuf(SubmitResponse.class);
        Mockito.verify(reportSubmitter).submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq(expectedName), ArgumentMatchers.anyMap(), ArgumentMatchers.any());
        assertThat(submitResponse.getTaskId()).isEqualTo("TASK_1");
        assertThat(submitResponse.getProjectId()).isEqualTo(SubmitActionTest.PROJECT_UUID);
    }

    @Test
    public void test_example_json_response() {
        Mockito.when(reportSubmitter.submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("My Project"), ArgumentMatchers.anyMap(), ArgumentMatchers.any())).thenReturn(SubmitActionTest.A_CE_TASK);
        TestResponse wsResponse = tester.newRequest().setParam("projectKey", "my_project").setParam("projectName", "My Project").setPart("report", new ByteArrayInputStream("{binary}".getBytes()), "foo.bar").setMediaType(JSON).setMethod("POST").execute();
        JsonAssert.assertJson(tester.getDef().responseExampleAsString()).isSimilarTo(wsResponse.getInput());
    }

    /**
     * If project name is not specified, then name is the project key
     */
    @Test
    public void project_name_is_optional() {
        Mockito.when(reportSubmitter.submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("my_project"), ArgumentMatchers.anyMap(), ArgumentMatchers.any())).thenReturn(SubmitActionTest.A_CE_TASK);
        tester.newRequest().setParam("projectKey", "my_project").setPart("report", new ByteArrayInputStream("{binary}".getBytes()), "foo.bar").setMediaType(PROTOBUF).setMethod("POST").execute();
        Mockito.verify(reportSubmitter).submit(ArgumentMatchers.eq(organizationKey), ArgumentMatchers.eq("my_project"), ArgumentMatchers.isNull(), ArgumentMatchers.eq("my_project"), ArgumentMatchers.anyMap(), ArgumentMatchers.any());
    }
}

