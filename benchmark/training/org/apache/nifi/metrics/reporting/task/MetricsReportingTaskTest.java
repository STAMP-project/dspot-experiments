/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.metrics.reporting.task;


import MetricsReportingTask.PROCESS_GROUP_ID;
import MetricsReportingTask.REPORTER_SERVICE;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.metrics.reporting.reporter.service.MetricReporterService;
import org.apache.nifi.util.MockConfigurationContext;
import org.apache.nifi.util.MockReportingContext;
import org.apache.nifi.util.MockReportingInitializationContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Test class for {@link MetricsReportingTask}.
 *
 * @author Omer Hadari
 */
@RunWith(MockitoJUnitRunner.class)
public class MetricsReportingTaskTest {
    /**
     * Identifier for {@link #reporterServiceStub}.
     */
    private static final String REPORTER_SERVICE_IDENTIFIER = "reporter-service";

    /**
     * Id for the group with status {@link #innerGroupStatus}.
     */
    private static final String TEST_GROUP_ID = "test-process-group-id";

    /**
     * Id for the {@link #reportingInitContextStub}.
     */
    private static final String TEST_INIT_CONTEXT_ID = "test-init-context-id";

    /**
     * Name for {@link #reportingInitContextStub}.
     */
    private static final String TEST_INIT_CONTEXT_NAME = "test-init-context-name";

    /**
     * Id for the tested tested reporting task.
     */
    private static final String TEST_TASK_ID = "test-task-id";

    /**
     * Stub context, used by {@link MetricsReportingTask#onTrigger(ReportingContext)} for reaching the status.
     */
    private MockReportingContext reportingContextStub;

    /**
     * Stub context, used by {@link MetricsReportingTask#connect(ConfigurationContext)} for reaching the service.
     */
    private MockConfigurationContext configurationContextStub;

    /**
     * Stub service for providing {@link #reporterMock}, used for actual reporting
     */
    @Mock
    private MetricReporterService reporterServiceStub;

    /**
     * Mock reporter, used for verifying actual reporting.
     */
    @Mock
    private ScheduledReporter reporterMock;

    /**
     * A status for the "root" process group.
     */
    private ProcessGroupStatus rootGroupStatus;

    /**
     * Same as {@link #rootGroupStatus}, used when {@link MetricsReportingTask#PROCESS_GROUP_ID} is set.
     */
    private ProcessGroupStatus innerGroupStatus;

    /**
     * Stub initialization context for calling {@link MetricsReportingTask#initialize(ReportingInitializationContext)}.
     */
    private MockReportingInitializationContext reportingInitContextStub;

    /**
     * The test subject.
     */
    private MetricsReportingTask testedReportingTask;

    /**
     * Make sure that in a single life cycle the correct metrics are registered, the correct {@link ProcessGroupStatus}
     * is used and that metrics are actually reported.
     */
    @Test
    public void testValidLifeCycleReportsCorrectly() throws Exception {
        reportingContextStub.getEventAccess().setProcessGroupStatus(rootGroupStatus);
        testedReportingTask.initialize(reportingInitContextStub);
        testedReportingTask.connect(configurationContextStub);
        testedReportingTask.onTrigger(reportingContextStub);
        Mockito.verify(reporterMock).report();
        // Verify correct metrics are registered
        ArgumentCaptor<MetricRegistry> registryCaptor = ArgumentCaptor.forClass(MetricRegistry.class);
        Mockito.verify(reporterServiceStub).createReporter(registryCaptor.capture());
        MetricRegistry usedRegistry = registryCaptor.getValue();
        Map<String, Metric> usedMetrics = usedRegistry.getMetrics();
        Assert.assertTrue(usedMetrics.keySet().containsAll(new MemoryUsageGaugeSet().getMetrics().keySet()));
        Assert.assertTrue(usedMetrics.keySet().containsAll(getMetrics().keySet()));
        // Verify the most current ProcessGroupStatus is updated
        Assert.assertEquals(testedReportingTask.currentStatusReference.get(), rootGroupStatus);
    }

    /**
     * Make sure that in a single life cycle the correct metrics are registered, the correct {@link ProcessGroupStatus}
     * is used and that metrics are actually reported.
     */
    @Test
    public void testValidLifeCycleReportsCorrectlyProcessGroupSpecified() throws Exception {
        reportingContextStub.setProperty(PROCESS_GROUP_ID.getName(), MetricsReportingTaskTest.TEST_GROUP_ID);
        reportingContextStub.getEventAccess().setProcessGroupStatus(MetricsReportingTaskTest.TEST_GROUP_ID, innerGroupStatus);
        testedReportingTask.initialize(reportingInitContextStub);
        testedReportingTask.connect(configurationContextStub);
        testedReportingTask.onTrigger(reportingContextStub);
        Mockito.verify(reporterMock).report();
        // Verify correct metrics are registered
        ArgumentCaptor<MetricRegistry> registryCaptor = ArgumentCaptor.forClass(MetricRegistry.class);
        Mockito.verify(reporterServiceStub).createReporter(registryCaptor.capture());
        MetricRegistry usedRegistry = registryCaptor.getValue();
        Map<String, Metric> usedMetrics = usedRegistry.getMetrics();
        Assert.assertTrue(usedMetrics.keySet().containsAll(new MemoryUsageGaugeSet().getMetrics().keySet()));
        Assert.assertTrue(usedMetrics.keySet().containsAll(getMetrics().keySet()));
        // Verify the most current ProcessGroupStatus is updated
        Assert.assertEquals(testedReportingTask.currentStatusReference.get(), innerGroupStatus);
    }

    /**
     * Make sure that in a single life cycle the correct metrics are registered, the correct {@link ProcessGroupStatus}
     * is used and that metrics are actually reported.
     */
    @Test
    public void testInvalidProcessGroupId() throws Exception {
        reportingContextStub.setProperty(PROCESS_GROUP_ID.getName(), ((MetricsReportingTaskTest.TEST_GROUP_ID) + "-invalid"));
        reportingContextStub.getEventAccess().setProcessGroupStatus(MetricsReportingTaskTest.TEST_GROUP_ID, innerGroupStatus);
        testedReportingTask.initialize(reportingInitContextStub);
        testedReportingTask.connect(configurationContextStub);
        testedReportingTask.onTrigger(reportingContextStub);
        Mockito.verify(reporterMock, Mockito.never()).report();
        Assert.assertNull(testedReportingTask.currentStatusReference.get());
    }

    /**
     * Make sure that {@link MetricsReportingTask#connect(ConfigurationContext)} does not create a new reporter
     * if there is already an active reporter.
     */
    @Test
    public void testConnectCreatesSingleReporter() throws Exception {
        testedReportingTask.initialize(reportingInitContextStub);
        testedReportingTask.connect(configurationContextStub);
        testedReportingTask.connect(configurationContextStub);
        Mockito.verify(reporterServiceStub, Mockito.times(1)).createReporter(ArgumentMatchers.any());
    }

    /**
     * Sanity check for registered properties.
     */
    @Test
    public void testGetSupportedPropertyDescriptorsSanity() throws Exception {
        List<PropertyDescriptor> expected = Arrays.asList(REPORTER_SERVICE, PROCESS_GROUP_ID);
        Assert.assertEquals(expected, testedReportingTask.getSupportedPropertyDescriptors());
    }
}

