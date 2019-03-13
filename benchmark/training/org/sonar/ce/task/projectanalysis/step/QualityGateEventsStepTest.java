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
package org.sonar.ce.task.projectanalysis.step;


import BranchType.PULL_REQUEST;
import BranchType.SHORT;
import Component.Type.DIRECTORY;
import Component.Type.PROJECT;
import java.util.Optional;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.notifications.Notification;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.component.DefaultBranchImpl;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolder;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.event.Event;
import org.sonar.ce.task.projectanalysis.event.EventRepository;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepository;
import org.sonar.ce.task.projectanalysis.measure.QualityGateStatus;
import org.sonar.ce.task.projectanalysis.metric.Metric;
import org.sonar.ce.task.projectanalysis.metric.MetricRepository;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.server.notification.NotificationService;

import static Level.ERROR;
import static Level.OK;


public class QualityGateEventsStepTest {
    private static final String PROJECT_VERSION = (new Random().nextBoolean()) ? null : randomAlphabetic(19);

    private static final ReportComponent PROJECT_COMPONENT = ReportComponent.builder(PROJECT, 1).setUuid("uuid 1").setKey("key 1").setCodePeriodVersion("V1.9").setProjectVersion(QualityGateEventsStepTest.PROJECT_VERSION).addChildren(ReportComponent.builder(DIRECTORY, 2).build()).build();

    private static final String INVALID_ALERT_STATUS = "trololo";

    private static final String ALERT_TEXT = "alert text";

    private static final QualityGateStatus OK_QUALITY_GATE_STATUS = new QualityGateStatus(OK, QualityGateEventsStepTest.ALERT_TEXT);

    private static final QualityGateStatus ERROR_QUALITY_GATE_STATUS = new QualityGateStatus(ERROR, QualityGateEventsStepTest.ALERT_TEXT);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    private ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);

    private ArgumentCaptor<Notification> notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);

    private Metric alertStatusMetric = Mockito.mock(Metric.class);

    private MetricRepository metricRepository = Mockito.mock(MetricRepository.class);

    private MeasureRepository measureRepository = Mockito.mock(MeasureRepository.class);

    private EventRepository eventRepository = Mockito.mock(EventRepository.class);

    private NotificationService notificationService = Mockito.mock(NotificationService.class);

    private QualityGateEventsStep underTest = new QualityGateEventsStep(treeRootHolder, metricRepository, measureRepository, eventRepository, notificationService, analysisMetadataHolder);

    @Test
    public void no_event_if_no_raw_ALERT_STATUS_measure() {
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(Optional.empty());
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(measureRepository).getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verifyNoMoreInteractions(measureRepository, eventRepository);
    }

    @Test
    public void no_event_created_if_raw_ALERT_STATUS_measure_is_null() {
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().createNoValue()));
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(measureRepository).getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verifyNoMoreInteractions(measureRepository, eventRepository);
    }

    @Test
    public void no_event_created_if_raw_ALERT_STATUS_measure_is_unsupported_value() {
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().create(QualityGateEventsStepTest.INVALID_ALERT_STATUS)));
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(measureRepository).getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verifyNoMoreInteractions(measureRepository, eventRepository);
    }

    @Test
    public void no_event_created_if_no_base_ALERT_STATUS_and_raw_is_OK() {
        QualityGateStatus someQGStatus = new QualityGateStatus(Measure.Level.OK);
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(someQGStatus).createNoValue()));
        Mockito.when(measureRepository.getBaseMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().createNoValue()));
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(measureRepository).getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verify(measureRepository).getBaseMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verifyNoMoreInteractions(measureRepository, eventRepository);
    }

    @Test
    public void event_created_if_base_ALERT_STATUS_has_no_alertStatus_and_raw_is_ERROR() {
        verify_event_created_if_no_base_ALERT_STATUS_measure(Level.ERROR, "Red");
    }

    @Test
    public void event_created_if_base_ALERT_STATUS_has_invalid_alertStatus_and_raw_is_ERROR() {
        verify_event_created_if_no_base_ALERT_STATUS_measure(Level.ERROR, "Red");
    }

    @Test
    public void no_event_created_if_base_ALERT_STATUS_measure_but_status_is_the_same() {
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(QualityGateEventsStepTest.OK_QUALITY_GATE_STATUS).createNoValue()));
        Mockito.when(measureRepository.getBaseMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(QualityGateEventsStepTest.OK_QUALITY_GATE_STATUS).createNoValue()));
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(measureRepository).getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verify(measureRepository).getBaseMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric);
        Mockito.verifyNoMoreInteractions(measureRepository, eventRepository);
    }

    @Test
    public void event_created_if_base_ALERT_STATUS_measure_exists_and_status_has_changed() {
        verify_event_created_if_base_ALERT_STATUS_measure_exists_and_status_has_changed(Level.OK, QualityGateEventsStepTest.ERROR_QUALITY_GATE_STATUS, "Red (was Green)");
        verify_event_created_if_base_ALERT_STATUS_measure_exists_and_status_has_changed(Level.ERROR, QualityGateEventsStepTest.OK_QUALITY_GATE_STATUS, "Green (was Red)");
    }

    @Test
    public void verify_branch_name_is_set_in_notification_when_not_main() {
        String branchName = "feature1";
        analysisMetadataHolder.setBranch(new DefaultBranchImpl(branchName) {
            @Override
            public boolean isMain() {
                return false;
            }
        });
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(QualityGateEventsStepTest.OK_QUALITY_GATE_STATUS).createNoValue()));
        Mockito.when(measureRepository.getBaseMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(new QualityGateStatus(ERROR)).createNoValue()));
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(notificationService).deliver(notificationArgumentCaptor.capture());
        Notification notification = notificationArgumentCaptor.getValue();
        assertThat(notification.getType()).isEqualTo("alerts");
        assertThat(notification.getFieldValue("projectKey")).isEqualTo(QualityGateEventsStepTest.PROJECT_COMPONENT.getKey());
        assertThat(notification.getFieldValue("projectName")).isEqualTo(QualityGateEventsStepTest.PROJECT_COMPONENT.getName());
        assertThat(notification.getFieldValue("codePeriodVersion")).isEqualTo(QualityGateEventsStepTest.PROJECT_COMPONENT.getProjectAttributes().getCodePeriodVersion());
        assertThat(notification.getFieldValue("branch")).isEqualTo(branchName);
        Mockito.reset(measureRepository, eventRepository, notificationService);
    }

    @Test
    public void verify_branch_name_is_not_set_in_notification_when_main() {
        analysisMetadataHolder.setBranch(new DefaultBranchImpl());
        Mockito.when(measureRepository.getRawMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(QualityGateEventsStepTest.OK_QUALITY_GATE_STATUS).createNoValue()));
        Mockito.when(measureRepository.getBaseMeasure(QualityGateEventsStepTest.PROJECT_COMPONENT, alertStatusMetric)).thenReturn(QualityGateEventsStepTest.of(Measure.newMeasureBuilder().setQualityGateStatus(new QualityGateStatus(ERROR)).createNoValue()));
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(notificationService).deliver(notificationArgumentCaptor.capture());
        Notification notification = notificationArgumentCaptor.getValue();
        assertThat(notification.getType()).isEqualTo("alerts");
        assertThat(notification.getFieldValue("projectKey")).isEqualTo(QualityGateEventsStepTest.PROJECT_COMPONENT.getKey());
        assertThat(notification.getFieldValue("projectName")).isEqualTo(QualityGateEventsStepTest.PROJECT_COMPONENT.getName());
        assertThat(notification.getFieldValue("codePeriodVersion")).isEqualTo(QualityGateEventsStepTest.PROJECT_COMPONENT.getProjectAttributes().getCodePeriodVersion());
        assertThat(notification.getFieldValue("branch")).isEqualTo(null);
        Mockito.reset(measureRepository, eventRepository, notificationService);
    }

    @Test
    public void no_alert_on_short_living_branches() {
        Branch shortBranch = Mockito.mock(Branch.class);
        Mockito.when(shortBranch.getType()).thenReturn(SHORT);
        analysisMetadataHolder.setBranch(shortBranch);
        TreeRootHolder treeRootHolder = Mockito.mock(TreeRootHolder.class);
        MetricRepository metricRepository = Mockito.mock(MetricRepository.class);
        MeasureRepository measureRepository = Mockito.mock(MeasureRepository.class);
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        QualityGateEventsStep underTest = new QualityGateEventsStep(treeRootHolder, metricRepository, measureRepository, eventRepository, notificationService, analysisMetadataHolder);
        underTest.execute(new TestComputationStepContext());
        Mockito.verifyZeroInteractions(treeRootHolder, metricRepository, measureRepository, eventRepository, notificationService);
    }

    @Test
    public void no_alert_on_pull_request_branches() {
        Branch shortBranch = Mockito.mock(Branch.class);
        Mockito.when(shortBranch.getType()).thenReturn(PULL_REQUEST);
        analysisMetadataHolder.setBranch(shortBranch);
        TreeRootHolder treeRootHolder = Mockito.mock(TreeRootHolder.class);
        MetricRepository metricRepository = Mockito.mock(MetricRepository.class);
        MeasureRepository measureRepository = Mockito.mock(MeasureRepository.class);
        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        QualityGateEventsStep underTest = new QualityGateEventsStep(treeRootHolder, metricRepository, measureRepository, eventRepository, notificationService, analysisMetadataHolder);
        underTest.execute(new TestComputationStepContext());
        Mockito.verifyZeroInteractions(treeRootHolder, metricRepository, measureRepository, eventRepository, notificationService);
    }
}

