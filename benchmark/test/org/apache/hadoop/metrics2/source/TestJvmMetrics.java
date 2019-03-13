/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.metrics2.source;


import GcTimeMonitor.GcData;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.service.ServiceStateException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.util.GcTimeMonitor;
import org.apache.hadoop.util.JvmPauseMonitor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestJvmMetrics {
    @Rule
    public Timeout timeout = new Timeout(30000);

    private JvmPauseMonitor pauseMonitor;

    private GcTimeMonitor gcTimeMonitor;

    @Test
    public void testJvmPauseMonitorPresence() {
        pauseMonitor = new JvmPauseMonitor();
        pauseMonitor.init(new Configuration());
        pauseMonitor.start();
        JvmMetrics jvmMetrics = new JvmMetrics("test", "test");
        jvmMetrics.setPauseMonitor(pauseMonitor);
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(jvmMetrics);
        MetricsCollector mc = rb.parent();
        Mockito.verify(mc).addRecord(JvmMetrics);
        Mockito.verify(rb).tag(ProcessName, "test");
        Mockito.verify(rb).tag(SessionId, "test");
        for (JvmMetricsInfo.JvmMetricsInfo info : JvmMetricsInfo.JvmMetricsInfo.values()) {
            if (info.name().startsWith("Mem")) {
                Mockito.verify(rb).addGauge(ArgumentMatchers.eq(info), ArgumentMatchers.anyFloat());
            } else
                if ((info.name().startsWith("Gc")) && (!(info.name().equals("GcTimePercentage")))) {
                    Mockito.verify(rb).addCounter(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
                } else
                    if (info.name().startsWith("Threads")) {
                        Mockito.verify(rb).addGauge(ArgumentMatchers.eq(info), ArgumentMatchers.anyInt());
                    } else
                        if (info.name().startsWith("Log")) {
                            Mockito.verify(rb).addCounter(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
                        }



        }
    }

    @Test
    public void testGcTimeMonitorPresence() {
        gcTimeMonitor = new GcTimeMonitor(60000, 1000, 70, null);
        gcTimeMonitor.start();
        JvmMetrics jvmMetrics = new JvmMetrics("test", "test");
        jvmMetrics.setGcTimeMonitor(gcTimeMonitor);
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(jvmMetrics);
        MetricsCollector mc = rb.parent();
        Mockito.verify(mc).addRecord(JvmMetrics);
        Mockito.verify(rb).tag(ProcessName, "test");
        Mockito.verify(rb).tag(SessionId, "test");
        for (JvmMetricsInfo.JvmMetricsInfo info : JvmMetricsInfo.JvmMetricsInfo.values()) {
            if (info.name().equals("GcTimePercentage")) {
                Mockito.verify(rb).addGauge(ArgumentMatchers.eq(info), ArgumentMatchers.anyInt());
            }
        }
    }

    @Test
    public void testDoubleStop() throws Throwable {
        pauseMonitor = new JvmPauseMonitor();
        pauseMonitor.init(new Configuration());
        pauseMonitor.start();
        pauseMonitor.stop();
        pauseMonitor.stop();
    }

    @Test
    public void testDoubleStart() throws Throwable {
        pauseMonitor = new JvmPauseMonitor();
        pauseMonitor.init(new Configuration());
        pauseMonitor.start();
        pauseMonitor.start();
        pauseMonitor.stop();
    }

    @Test
    public void testStopBeforeStart() throws Throwable {
        pauseMonitor = new JvmPauseMonitor();
        try {
            pauseMonitor.init(new Configuration());
            pauseMonitor.stop();
            pauseMonitor.start();
            Assert.fail(("Expected an exception, got " + (pauseMonitor)));
        } catch (ServiceStateException e) {
            GenericTestUtils.assertExceptionContains("cannot enter state", e);
        }
    }

    @Test
    public void testStopBeforeInit() throws Throwable {
        pauseMonitor = new JvmPauseMonitor();
        try {
            pauseMonitor.stop();
            pauseMonitor.init(new Configuration());
            Assert.fail(("Expected an exception, got " + (pauseMonitor)));
        } catch (ServiceStateException e) {
            GenericTestUtils.assertExceptionContains("cannot enter state", e);
        }
    }

    @Test
    public void testGcTimeMonitor() {
        class Alerter implements GcTimeMonitor.GcTimeAlertHandler {
            private volatile int numAlerts;

            private volatile int maxGcTimePercentage;

            @Override
            public void alert(GcTimeMonitor.GcData gcData) {
                (numAlerts)++;
                if ((gcData.getGcTimePercentage()) > (maxGcTimePercentage)) {
                    maxGcTimePercentage = gcData.getGcTimePercentage();
                }
            }
        }
        Alerter alerter = new Alerter();
        int alertGcPerc = 10;// Alerter should be called if GC takes >= 10%

        gcTimeMonitor = new GcTimeMonitor((60 * 1000), 100, alertGcPerc, alerter);
        gcTimeMonitor.start();
        int maxGcTimePercentage = 0;
        long gcCount = 0;
        // Generate a lot of garbage for some time and verify that the monitor
        // reports at least some percentage of time in GC pauses, and that the
        // alerter is invoked at least once.
        List<String> garbageStrings = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        // Run this for at least 1 sec for our monitor to collect enough data
        while (((System.currentTimeMillis()) - startTime) < 1000) {
            for (int j = 0; j < 100000; j++) {
                garbageStrings.add(("Long string prefix just to fill memory with garbage " + j));
            }
            garbageStrings.clear();
            System.gc();
            GcTimeMonitor.GcData gcData = gcTimeMonitor.getLatestGcData();
            int gcTimePercentage = gcData.getGcTimePercentage();
            if (gcTimePercentage > maxGcTimePercentage) {
                maxGcTimePercentage = gcTimePercentage;
            }
            gcCount = gcData.getAccumulatedGcCount();
        } 
        Assert.assertTrue((maxGcTimePercentage > 0));
        Assert.assertTrue((gcCount > 0));
        Assert.assertTrue(((alerter.numAlerts) > 0));
        Assert.assertTrue(((alerter.maxGcTimePercentage) >= alertGcPerc));
    }

    @Test
    public void testJvmMetricsSingletonWithSameProcessName() {
        JvmMetrics jvmMetrics1 = org.apache.hadoop.metrics2.source.JvmMetrics.initSingleton("test", null);
        JvmMetrics jvmMetrics2 = org.apache.hadoop.metrics2.source.JvmMetrics.initSingleton("test", null);
        Assert.assertEquals("initSingleton should return the singleton instance", jvmMetrics1, jvmMetrics2);
    }

    @Test
    public void testJvmMetricsSingletonWithDifferentProcessNames() {
        final String process1Name = "process1";
        JvmMetrics jvmMetrics1 = org.apache.hadoop.metrics2.source.JvmMetrics.initSingleton(process1Name, null);
        final String process2Name = "process2";
        JvmMetrics jvmMetrics2 = org.apache.hadoop.metrics2.source.JvmMetrics.initSingleton(process2Name, null);
        Assert.assertEquals("initSingleton should return the singleton instance", jvmMetrics1, jvmMetrics2);
        Assert.assertEquals("unexpected process name of the singleton instance", process1Name, jvmMetrics1.processName);
        Assert.assertEquals("unexpected process name of the singleton instance", process1Name, jvmMetrics2.processName);
    }
}

