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
package org.sonar.scanner.report;


import Ce.SubmitResponse;
import LoggerLevel.DEBUG;
import LoggerLevel.INFO;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.platform.Server;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.log.LogTester;
import org.sonar.scanner.bootstrap.GlobalAnalysisMode;
import org.sonar.scanner.bootstrap.ScannerWsClient;
import org.sonar.scanner.scan.ScanProperties;
import org.sonar.scanner.scan.branch.BranchConfiguration;
import org.sonar.scanner.scan.branch.BranchType;
import org.sonarqube.ws.client.HttpException;
import org.sonarqube.ws.client.WsRequest;
import org.sonarqube.ws.client.WsResponse;


public class ReportPublisherTest {
    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    GlobalAnalysisMode mode = Mockito.mock(GlobalAnalysisMode.class);

    ScanProperties properties = Mockito.mock(ScanProperties.class);

    ScannerWsClient wsClient = Mockito.mock(ScannerWsClient.class, Mockito.RETURNS_DEEP_STUBS);

    Server server = Mockito.mock(Server.class);

    InputModuleHierarchy moduleHierarchy = Mockito.mock(InputModuleHierarchy.class);

    DefaultInputModule root;

    AnalysisContextReportPublisher contextPublisher = Mockito.mock(AnalysisContextReportPublisher.class);

    BranchConfiguration branchConfiguration = Mockito.mock(BranchConfiguration.class);

    ReportPublisher underTest = new ReportPublisher(properties, wsClient, server, contextPublisher, moduleHierarchy, mode, Mockito.mock(TempFolder.class), new ReportPublisherStep[0], branchConfiguration);

    @Test
    public void log_and_dump_information_about_report_uploading() throws IOException {
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of("MyOrg"));
        underTest.logSuccess("TASK-123");
        assertThat(logTester.logs(INFO)).contains("ANALYSIS SUCCESSFUL, you can browse https://localhost/dashboard?id=org.sonarsource.sonarqube%3Asonarqube").contains("Note that you will be able to access the updated dashboard once the server has processed the submitted analysis report").contains("More about the report processing at https://localhost/api/ce/task?id=TASK-123");
        assertThat(FileUtils.readFileToString(properties.metadataFilePath().toFile(), StandardCharsets.UTF_8)).isEqualTo(("organization=MyOrg\n" + ((((("projectKey=org.sonarsource.sonarqube:sonarqube\n" + "serverUrl=https://localhost\n") + "serverVersion=6.4\n") + "dashboardUrl=https://localhost/dashboard?id=org.sonarsource.sonarqube%3Asonarqube\n") + "ceTaskId=TASK-123\n") + "ceTaskUrl=https://localhost/api/ce/task?id=TASK-123\n")));
    }

    @Test
    public void parse_upload_error_message() throws IOException {
        HttpException ex = new HttpException("url", 404, "{\"errors\":[{\"msg\":\"Organization with key \'MyOrg\' does not exist\"}]}");
        WsResponse response = Mockito.mock(WsResponse.class);
        Mockito.when(response.failIfNotSuccessful()).thenThrow(ex);
        Mockito.when(wsClient.call(ArgumentMatchers.any(WsRequest.class))).thenReturn(response);
        exception.expect(MessageException.class);
        exception.expectMessage("Failed to upload report - Organization with key 'MyOrg' does not exist");
        underTest.upload(temp.newFile());
    }

    @Test
    public void log_public_url_if_defined_for_main_branch() throws IOException {
        Mockito.when(server.getPublicRootUrl()).thenReturn("https://publicserver/sonarqube");
        underTest.logSuccess("TASK-123");
        assertThat(logTester.logs(INFO)).contains("ANALYSIS SUCCESSFUL, you can browse https://publicserver/sonarqube/dashboard?id=org.sonarsource.sonarqube%3Asonarqube").contains("More about the report processing at https://publicserver/sonarqube/api/ce/task?id=TASK-123");
        assertThat(FileUtils.readFileToString(properties.metadataFilePath().toFile(), StandardCharsets.UTF_8)).isEqualTo(("projectKey=org.sonarsource.sonarqube:sonarqube\n" + (((("serverUrl=https://publicserver/sonarqube\n" + "serverVersion=6.4\n") + "dashboardUrl=https://publicserver/sonarqube/dashboard?id=org.sonarsource.sonarqube%3Asonarqube\n") + "ceTaskId=TASK-123\n") + "ceTaskUrl=https://publicserver/sonarqube/api/ce/task?id=TASK-123\n")));
    }

    @Test
    public void log_public_url_if_defined_for_long_living_branches() throws IOException {
        Mockito.when(server.getPublicRootUrl()).thenReturn("https://publicserver/sonarqube");
        Mockito.when(branchConfiguration.branchType()).thenReturn(BranchType.LONG);
        Mockito.when(branchConfiguration.branchName()).thenReturn("branch-6.7");
        ReportPublisher underTest = new ReportPublisher(properties, wsClient, server, contextPublisher, moduleHierarchy, mode, Mockito.mock(TempFolder.class), new ReportPublisherStep[0], branchConfiguration);
        underTest.logSuccess("TASK-123");
        assertThat(logTester.logs(INFO)).contains("ANALYSIS SUCCESSFUL, you can browse https://publicserver/sonarqube/dashboard?id=org.sonarsource.sonarqube%3Asonarqube&branch=branch-6.7").contains("More about the report processing at https://publicserver/sonarqube/api/ce/task?id=TASK-123");
        assertThat(FileUtils.readFileToString(properties.metadataFilePath().toFile(), StandardCharsets.UTF_8)).isEqualTo(("projectKey=org.sonarsource.sonarqube:sonarqube\n" + (((("serverUrl=https://publicserver/sonarqube\n" + "serverVersion=6.4\n") + "dashboardUrl=https://publicserver/sonarqube/dashboard?id=org.sonarsource.sonarqube%3Asonarqube&branch=branch-6.7\n") + "ceTaskId=TASK-123\n") + "ceTaskUrl=https://publicserver/sonarqube/api/ce/task?id=TASK-123\n")));
    }

    @Test
    public void log_public_url_if_defined_for_short_living_branches() throws IOException {
        Mockito.when(server.getPublicRootUrl()).thenReturn("https://publicserver/sonarqube");
        Mockito.when(branchConfiguration.branchType()).thenReturn(BranchType.SHORT);
        Mockito.when(branchConfiguration.branchName()).thenReturn("branch-6.7");
        ReportPublisher underTest = new ReportPublisher(properties, wsClient, server, contextPublisher, moduleHierarchy, mode, Mockito.mock(TempFolder.class), new ReportPublisherStep[0], branchConfiguration);
        underTest.logSuccess("TASK-123");
        assertThat(logTester.logs(INFO)).contains("ANALYSIS SUCCESSFUL, you can browse https://publicserver/sonarqube/dashboard?id=org.sonarsource.sonarqube%3Asonarqube&branch=branch-6.7&resolved=false").contains("More about the report processing at https://publicserver/sonarqube/api/ce/task?id=TASK-123");
        assertThat(FileUtils.readFileToString(properties.metadataFilePath().toFile(), StandardCharsets.UTF_8)).isEqualTo(("projectKey=org.sonarsource.sonarqube:sonarqube\n" + (((("serverUrl=https://publicserver/sonarqube\n" + "serverVersion=6.4\n") + "dashboardUrl=https://publicserver/sonarqube/dashboard?id=org.sonarsource.sonarqube%3Asonarqube&branch=branch-6.7&resolved=false\n") + "ceTaskId=TASK-123\n") + "ceTaskUrl=https://publicserver/sonarqube/api/ce/task?id=TASK-123\n")));
    }

    @Test
    public void log_public_url_if_defined_for_pull_request() throws IOException {
        Mockito.when(server.getPublicRootUrl()).thenReturn("https://publicserver/sonarqube");
        Mockito.when(branchConfiguration.branchName()).thenReturn("Bitbucket cloud Widget");
        Mockito.when(branchConfiguration.branchType()).thenReturn(BranchType.PULL_REQUEST);
        Mockito.when(branchConfiguration.pullRequestKey()).thenReturn("105");
        ReportPublisher underTest = new ReportPublisher(properties, wsClient, server, contextPublisher, moduleHierarchy, mode, Mockito.mock(TempFolder.class), new ReportPublisherStep[0], branchConfiguration);
        underTest.logSuccess("TASK-123");
        assertThat(logTester.logs(INFO)).contains("ANALYSIS SUCCESSFUL, you can browse https://publicserver/sonarqube/project/issues?id=org.sonarsource.sonarqube%3Asonarqube&pullRequest=105&resolved=false").contains("More about the report processing at https://publicserver/sonarqube/api/ce/task?id=TASK-123");
        assertThat(FileUtils.readFileToString(properties.metadataFilePath().toFile(), StandardCharsets.UTF_8)).isEqualTo(("projectKey=org.sonarsource.sonarqube:sonarqube\n" + (((("serverUrl=https://publicserver/sonarqube\n" + "serverVersion=6.4\n") + "dashboardUrl=https://publicserver/sonarqube/project/issues?id=org.sonarsource.sonarqube%3Asonarqube&pullRequest=105&resolved=false\n") + "ceTaskId=TASK-123\n") + "ceTaskUrl=https://publicserver/sonarqube/api/ce/task?id=TASK-123\n")));
    }

    @Test
    public void fail_if_public_url_malformed() {
        Mockito.when(server.getPublicRootUrl()).thenReturn("invalid");
        exception.expect(MessageException.class);
        exception.expectMessage("Failed to parse public URL set in SonarQube server: invalid");
        underTest.start();
    }

    @Test
    public void log_but_not_dump_information_when_report_is_not_uploaded() throws IOException {
        /* report not uploaded, no server task */
        underTest.logSuccess(null);
        assertThat(logTester.logs(INFO)).contains("ANALYSIS SUCCESSFUL").doesNotContain("dashboard/index");
        assertThat(properties.metadataFilePath()).doesNotExist();
    }

    @Test
    public void log_and_dump_information_to_custom_path() throws IOException {
        underTest.logSuccess("TASK-123");
        assertThat(properties.metadataFilePath()).exists();
        assertThat(logTester.logs(DEBUG)).contains(("Report metadata written to " + (properties.metadataFilePath())));
    }

    @Test
    public void should_not_delete_report_if_property_is_set() throws IOException {
        Mockito.when(properties.shouldKeepReport()).thenReturn(true);
        Path reportDir = temp.getRoot().toPath().resolve("scanner-report");
        Files.createDirectory(reportDir);
        underTest.start();
        underTest.stop();
        assertThat(reportDir).isDirectory();
    }

    @Test
    public void should_delete_report_by_default() throws IOException {
        Path reportDir = temp.getRoot().toPath().resolve("scanner-report");
        Files.createDirectory(reportDir);
        underTest.start();
        underTest.stop();
        assertThat(reportDir).doesNotExist();
    }

    @Test
    public void test_ws_parameters() throws Exception {
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of("MyOrg"));
        WsResponse response = Mockito.mock(WsResponse.class);
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        SubmitResponse.newBuilder().build().writeTo(out);
        out.close();
        Mockito.when(response.failIfNotSuccessful()).thenReturn(response);
        Mockito.when(response.contentStream()).thenReturn(in);
        Mockito.when(wsClient.call(ArgumentMatchers.any(WsRequest.class))).thenReturn(response);
        underTest.upload(temp.newFile());
        ArgumentCaptor<WsRequest> capture = ArgumentCaptor.forClass(WsRequest.class);
        Mockito.verify(wsClient).call(capture.capture());
        WsRequest wsRequest = capture.getValue();
        assertThat(wsRequest.getParameters().getKeys()).containsOnly("organization", "projectKey");
        assertThat(wsRequest.getParameters().getValue("organization")).isEqualTo("MyOrg");
        assertThat(wsRequest.getParameters().getValue("projectKey")).isEqualTo("org.sonarsource.sonarqube:sonarqube");
    }

    @Test
    public void test_send_branches_characteristics() throws Exception {
        String orgName = "MyOrg";
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of(orgName));
        String branchName = "feature";
        Mockito.when(branchConfiguration.branchName()).thenReturn(branchName);
        Mockito.when(branchConfiguration.branchType()).thenReturn(BranchType.SHORT);
        WsResponse response = Mockito.mock(WsResponse.class);
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        SubmitResponse.newBuilder().build().writeTo(out);
        out.close();
        Mockito.when(response.failIfNotSuccessful()).thenReturn(response);
        Mockito.when(response.contentStream()).thenReturn(in);
        Mockito.when(wsClient.call(ArgumentMatchers.any(WsRequest.class))).thenReturn(response);
        underTest.upload(temp.newFile());
        ArgumentCaptor<WsRequest> capture = ArgumentCaptor.forClass(WsRequest.class);
        Mockito.verify(wsClient).call(capture.capture());
        WsRequest wsRequest = capture.getValue();
        assertThat(wsRequest.getParameters().getKeys()).hasSize(3);
        assertThat(wsRequest.getParameters().getValues("organization")).containsExactly(orgName);
        assertThat(wsRequest.getParameters().getValues("projectKey")).containsExactly("org.sonarsource.sonarqube:sonarqube");
        assertThat(wsRequest.getParameters().getValues("characteristic")).containsExactlyInAnyOrder(("branch=" + branchName), ("branchType=" + (BranchType.SHORT.name())));
    }

    @Test
    public void send_pull_request_characteristic() throws Exception {
        String orgName = "MyOrg";
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of(orgName));
        String branchName = "feature";
        String pullRequestId = "pr-123";
        Mockito.when(branchConfiguration.branchName()).thenReturn(branchName);
        Mockito.when(branchConfiguration.branchType()).thenReturn(BranchType.PULL_REQUEST);
        Mockito.when(branchConfiguration.pullRequestKey()).thenReturn(pullRequestId);
        WsResponse response = Mockito.mock(WsResponse.class);
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        SubmitResponse.newBuilder().build().writeTo(out);
        out.close();
        Mockito.when(response.failIfNotSuccessful()).thenReturn(response);
        Mockito.when(response.contentStream()).thenReturn(in);
        Mockito.when(wsClient.call(ArgumentMatchers.any(WsRequest.class))).thenReturn(response);
        underTest.upload(temp.newFile());
        ArgumentCaptor<WsRequest> capture = ArgumentCaptor.forClass(WsRequest.class);
        Mockito.verify(wsClient).call(capture.capture());
        WsRequest wsRequest = capture.getValue();
        assertThat(wsRequest.getParameters().getKeys()).hasSize(3);
        assertThat(wsRequest.getParameters().getValues("organization")).containsExactly(orgName);
        assertThat(wsRequest.getParameters().getValues("projectKey")).containsExactly("org.sonarsource.sonarqube:sonarqube");
        assertThat(wsRequest.getParameters().getValues("characteristic")).containsExactlyInAnyOrder(("pullRequest=" + pullRequestId));
    }
}

