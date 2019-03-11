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
package org.sonar.ce.notification;


import CeActivityDto.Status.FAILED;
import CeActivityDto.Status.SUCCESS;
import CeTaskTypes.REPORT;
import ReportAnalysisFailureNotification.Project;
import ReportAnalysisFailureNotification.TYPE;
import ReportAnalysisFailureNotification.Task;
import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.CeTaskResult;
import org.sonar.ce.task.projectanalysis.notification.ReportAnalysisFailureNotification;
import org.sonar.ce.task.projectanalysis.notification.ReportAnalysisFailureNotificationSerializer;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.RowNotFoundException;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.notification.NotificationService;


public class ReportAnalysisFailureNotificationExecutionListenerTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Random random = new Random();

    private DbClient dbClient = dbTester.getDbClient();

    private NotificationService notificationService = Mockito.mock(NotificationService.class);

    private ReportAnalysisFailureNotificationSerializer serializer = Mockito.mock(ReportAnalysisFailureNotificationSerializer.class);

    private System2 system2 = Mockito.mock(System2.class);

    private DbClient dbClientMock = Mockito.mock(DbClient.class);

    private CeTask ceTaskMock = Mockito.mock(CeTask.class);

    private Throwable throwableMock = Mockito.mock(Throwable.class);

    private CeTaskResult ceTaskResultMock = Mockito.mock(CeTaskResult.class);

    private ReportAnalysisFailureNotificationExecutionListener fullMockedUnderTest = new ReportAnalysisFailureNotificationExecutionListener(notificationService, dbClientMock, serializer, system2);

    private ReportAnalysisFailureNotificationExecutionListener underTest = new ReportAnalysisFailureNotificationExecutionListener(notificationService, dbClient, serializer, system2);

    @Test
    public void onStart_has_no_effect() {
        CeTask mockedCeTask = Mockito.mock(CeTask.class);
        fullMockedUnderTest.onStart(mockedCeTask);
        Mockito.verifyZeroInteractions(mockedCeTask, notificationService, dbClientMock, serializer, system2);
    }

    @Test
    public void onEnd_has_no_effect_if_status_is_SUCCESS() {
        fullMockedUnderTest.onEnd(ceTaskMock, SUCCESS, ceTaskResultMock, throwableMock);
        Mockito.verifyZeroInteractions(ceTaskMock, ceTaskResultMock, throwableMock, notificationService, dbClientMock, serializer, system2);
    }

    @Test
    public void onEnd_has_no_effect_if_CeTask_type_is_not_report() {
        Mockito.when(ceTaskMock.getType()).thenReturn(randomAlphanumeric(12));
        fullMockedUnderTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
        Mockito.verifyZeroInteractions(ceTaskResultMock, throwableMock, notificationService, dbClientMock, serializer, system2);
    }

    @Test
    public void onEnd_has_no_effect_if_CeTask_has_no_component_uuid() {
        Mockito.when(ceTaskMock.getType()).thenReturn(REPORT);
        fullMockedUnderTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
        Mockito.verifyZeroInteractions(ceTaskResultMock, throwableMock, notificationService, dbClientMock, serializer, system2);
    }

    @Test
    public void onEnd_has_no_effect_if_there_is_no_subscriber_for_ReportAnalysisFailureNotification_type() {
        String componentUuid = randomAlphanumeric(6);
        Mockito.when(ceTaskMock.getType()).thenReturn(REPORT);
        Mockito.when(ceTaskMock.getComponent()).thenReturn(Optional.of(new CeTask.Component(componentUuid, null, null)));
        Mockito.when(notificationService.hasProjectSubscribersForTypes(componentUuid, Collections.singleton(TYPE))).thenReturn(false);
        fullMockedUnderTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
        Mockito.verifyZeroInteractions(ceTaskResultMock, throwableMock, dbClientMock, serializer, system2);
    }

    @Test
    public void onEnd_fails_with_RowNotFoundException_if_component_does_not_exist_in_DB() {
        String componentUuid = randomAlphanumeric(6);
        Mockito.when(ceTaskMock.getType()).thenReturn(REPORT);
        Mockito.when(ceTaskMock.getComponent()).thenReturn(Optional.of(new CeTask.Component(componentUuid, null, null)));
        Mockito.when(notificationService.hasProjectSubscribersForTypes(componentUuid, Collections.singleton(TYPE))).thenReturn(true);
        expectedException.expect(RowNotFoundException.class);
        expectedException.expectMessage((("Component with uuid '" + componentUuid) + "' not found"));
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
    }

    @Test
    public void onEnd_fails_with_IAE_if_component_is_not_a_project() {
        Mockito.when(ceTaskMock.getType()).thenReturn(REPORT);
        OrganizationDto organization = OrganizationTesting.newOrganizationDto();
        ComponentDto project = dbTester.components().insertPrivateProject();
        ComponentDto module = dbTester.components().insertComponent(newModuleDto(project));
        ComponentDto directory = dbTester.components().insertComponent(newDirectory(module, randomAlphanumeric(12)));
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(project));
        ComponentDto view = dbTester.components().insertComponent(ComponentTesting.newView(organization));
        ComponentDto subView = dbTester.components().insertComponent(ComponentTesting.newSubView(view));
        ComponentDto projectCopy = dbTester.components().insertComponent(ComponentTesting.newProjectCopy(project, subView));
        ComponentDto application = dbTester.components().insertComponent(ComponentTesting.newApplication(organization));
        Arrays.asList(module, directory, file, view, subView, projectCopy, application).forEach(( component) -> {
            try {
                when(ceTaskMock.getComponent()).thenReturn(Optional.of(new CeTask.Component(component.uuid(), null, null)));
                when(notificationService.hasProjectSubscribersForTypes(component.uuid(), singleton(ReportAnalysisFailureNotification.TYPE))).thenReturn(true);
                underTest.onEnd(ceTaskMock, CeActivityDto.Status.FAILED, ceTaskResultMock, throwableMock);
                fail(("An IllegalArgumentException should have been thrown for component " + component));
            } catch ( e) {
                assertThat(e.getMessage()).isEqualTo(String.format("Component %s must be a project (scope=%s, qualifier=%s)", component.uuid(), component.scope(), component.qualifier()));
            }
        });
    }

    @Test
    public void onEnd_fails_with_RowNotFoundException_if_activity_for_task_does_not_exist_in_DB() {
        String componentUuid = randomAlphanumeric(6);
        String taskUuid = randomAlphanumeric(6);
        Mockito.when(ceTaskMock.getType()).thenReturn(REPORT);
        Mockito.when(ceTaskMock.getUuid()).thenReturn(taskUuid);
        Mockito.when(ceTaskMock.getComponent()).thenReturn(Optional.of(new CeTask.Component(componentUuid, null, null)));
        Mockito.when(notificationService.hasProjectSubscribersForTypes(componentUuid, Collections.singleton(TYPE))).thenReturn(true);
        dbTester.components().insertPrivateProject(( s) -> s.setUuid(componentUuid));
        expectedException.expect(RowNotFoundException.class);
        expectedException.expectMessage((("CeActivity with uuid '" + taskUuid) + "' not found"));
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
    }

    @Test
    public void onEnd_creates_notification_with_data_from_activity_and_project_and_deliver_it() {
        String taskUuid = randomAlphanumeric(12);
        int createdAt = random.nextInt(999999);
        long executedAt = random.nextInt(999999);
        ComponentDto project = initMocksToPassConditions(taskUuid, createdAt, executedAt);
        Notification notificationMock = mockSerializer();
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
        ArgumentCaptor<ReportAnalysisFailureNotification> notificationCaptor = verifyAndCaptureSerializedNotification();
        Mockito.verify(notificationService).deliver(ArgumentMatchers.same(notificationMock));
        ReportAnalysisFailureNotification reportAnalysisFailureNotification = notificationCaptor.getValue();
        ReportAnalysisFailureNotification.Project notificationProject = reportAnalysisFailureNotification.getProject();
        assertThat(notificationProject.getName()).isEqualTo(project.name());
        assertThat(notificationProject.getKey()).isEqualTo(project.getKey());
        assertThat(notificationProject.getUuid()).isEqualTo(project.uuid());
        assertThat(notificationProject.getBranchName()).isEqualTo(project.getBranch());
        ReportAnalysisFailureNotification.Task notificationTask = reportAnalysisFailureNotification.getTask();
        assertThat(notificationTask.getUuid()).isEqualTo(taskUuid);
        assertThat(notificationTask.getCreatedAt()).isEqualTo(createdAt);
        assertThat(notificationTask.getFailedAt()).isEqualTo(executedAt);
    }

    @Test
    public void onEnd_creates_notification_with_error_message_from_Throwable_argument_message() {
        initMocksToPassConditions(randomAlphanumeric(12), random.nextInt(999999), ((long) (random.nextInt(999999))));
        String message = randomAlphanumeric(66);
        Mockito.when(throwableMock.getMessage()).thenReturn(message);
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, throwableMock);
        ArgumentCaptor<ReportAnalysisFailureNotification> notificationCaptor = verifyAndCaptureSerializedNotification();
        ReportAnalysisFailureNotification reportAnalysisFailureNotification = notificationCaptor.getValue();
        assertThat(reportAnalysisFailureNotification.getErrorMessage()).isEqualTo(message);
    }

    @Test
    public void onEnd_creates_notification_with_null_error_message_if_Throwable_is_null() {
        String taskUuid = randomAlphanumeric(12);
        initMocksToPassConditions(taskUuid, random.nextInt(999999), ((long) (random.nextInt(999999))));
        Notification notificationMock = mockSerializer();
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, null);
        Mockito.verify(notificationService).deliver(ArgumentMatchers.same(notificationMock));
        ArgumentCaptor<ReportAnalysisFailureNotification> notificationCaptor = verifyAndCaptureSerializedNotification();
        ReportAnalysisFailureNotification reportAnalysisFailureNotification = notificationCaptor.getValue();
        assertThat(reportAnalysisFailureNotification.getErrorMessage()).isNull();
    }

    @Test
    public void onEnd_ignores_null_CeTaskResult_argument() {
        String taskUuid = randomAlphanumeric(12);
        initMocksToPassConditions(taskUuid, random.nextInt(999999), ((long) (random.nextInt(999999))));
        Notification notificationMock = mockSerializer();
        underTest.onEnd(ceTaskMock, FAILED, null, null);
        Mockito.verify(notificationService).deliver(ArgumentMatchers.same(notificationMock));
    }

    @Test
    public void onEnd_ignores_CeTaskResult_argument() {
        String taskUuid = randomAlphanumeric(12);
        initMocksToPassConditions(taskUuid, random.nextInt(999999), ((long) (random.nextInt(999999))));
        Notification notificationMock = mockSerializer();
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, null);
        Mockito.verify(notificationService).deliver(ArgumentMatchers.same(notificationMock));
        Mockito.verifyZeroInteractions(ceTaskResultMock);
    }

    @Test
    public void onEnd_uses_system_data_as_failedAt_if_task_has_no_executedAt() {
        String taskUuid = randomAlphanumeric(12);
        initMocksToPassConditions(taskUuid, random.nextInt(999999), null);
        long now = random.nextInt(999999);
        Mockito.when(system2.now()).thenReturn(now);
        Notification notificationMock = mockSerializer();
        underTest.onEnd(ceTaskMock, FAILED, ceTaskResultMock, null);
        Mockito.verify(notificationService).deliver(ArgumentMatchers.same(notificationMock));
        ArgumentCaptor<ReportAnalysisFailureNotification> notificationCaptor = verifyAndCaptureSerializedNotification();
        assertThat(notificationCaptor.getValue().getTask().getFailedAt()).isEqualTo(now);
    }
}

