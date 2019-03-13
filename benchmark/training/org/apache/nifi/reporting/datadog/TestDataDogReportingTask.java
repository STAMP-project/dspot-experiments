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
package org.apache.nifi.reporting.datadog;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AtomicDouble;
import com.yammer.metrics.core.VirtualMachineMetrics;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.controller.status.ProcessorStatus;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.reporting.ReportingContext;
import org.apache.nifi.reporting.ReportingInitializationContext;
import org.apache.nifi.reporting.datadog.metrics.MetricsService;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDataDogReportingTask {
    private ProcessGroupStatus status;

    private ProcessorStatus procStatus;

    private ConcurrentHashMap<String, AtomicDouble> metricsMap;

    private MetricRegistry metricRegistry;

    private MetricsService metricsService;

    private String env = "dev";

    private String prefix = "nifi";

    private ReportingContext context;

    private ReportingInitializationContext initContext;

    private ConfigurationContext configurationContext;

    private volatile VirtualMachineMetrics virtualMachineMetrics;

    private Logger logger;

    // test onTrigger method
    @Test
    public void testOnTrigger() throws IOException, InitializationException {
        DataDogReportingTask dataDogReportingTask = new TestDataDogReportingTask.TestableDataDogReportingTask();
        dataDogReportingTask.initialize(initContext);
        dataDogReportingTask.setup(configurationContext);
        dataDogReportingTask.onTrigger(context);
        Mockito.verify(metricsService, Mockito.atLeast(1)).getProcessorMetrics(Mockito.<ProcessorStatus>any());
        Mockito.verify(metricsService, Mockito.atLeast(1)).getJVMMetrics(Mockito.<VirtualMachineMetrics>any());
    }

    // test updating metrics of processors
    @Test
    public void testUpdateMetricsProcessor() throws IOException, InitializationException {
        MetricsService ms = new MetricsService();
        Map<String, Double> processorMetrics = ms.getProcessorMetrics(procStatus);
        Map<String, String> tagsMap = ImmutableMap.of("env", "test");
        DataDogReportingTask dataDogReportingTask = new TestDataDogReportingTask.TestableDataDogReportingTask();
        dataDogReportingTask.initialize(initContext);
        dataDogReportingTask.setup(configurationContext);
        dataDogReportingTask.updateMetrics(processorMetrics, Optional.of("sampleProcessor"), tagsMap);
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.sampleProcessor.FlowFilesReceivedLast5Minutes"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.sampleProcessor.ActiveThreads"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.sampleProcessor.BytesWrittenLast5Minutes"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.sampleProcessor.BytesReadLast5Minutes"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.sampleProcessor.FlowFilesSentLast5Minutes"), Mockito.<Gauge>any());
    }

    // test updating JMV metrics
    @Test
    public void testUpdateMetricsJVM() throws IOException, InitializationException {
        MetricsService ms = new MetricsService();
        Map<String, Double> processorMetrics = ms.getJVMMetrics(virtualMachineMetrics);
        Map<String, String> tagsMap = ImmutableMap.of("env", "test");
        DataDogReportingTask dataDogReportingTask = new TestDataDogReportingTask.TestableDataDogReportingTask();
        dataDogReportingTask.initialize(initContext);
        dataDogReportingTask.setup(configurationContext);
        dataDogReportingTask.updateMetrics(processorMetrics, Optional.<String>absent(), tagsMap);
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.heap_usage"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.thread_count"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.thread_states.terminated"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.heap_used"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.thread_states.runnable"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.thread_states.timed_waiting"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.uptime"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.daemon_thread_count"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.file_descriptor_usage"), Mockito.<Gauge>any());
        Mockito.verify(metricRegistry).register(ArgumentMatchers.eq("nifi.flow.jvm.thread_states.blocked"), Mockito.<Gauge>any());
    }

    private class TestableDataDogReportingTask extends DataDogReportingTask {
        @Override
        protected MetricsService getMetricsService() {
            return metricsService;
        }

        @Override
        protected DDMetricRegistryBuilder getMetricRegistryBuilder() {
            return new DDMetricRegistryBuilder();
        }

        @Override
        protected MetricRegistry getMetricRegistry() {
            return metricRegistry;
        }

        @Override
        protected ConcurrentHashMap<String, AtomicDouble> getMetricsMap() {
            return metricsMap;
        }
    }
}

