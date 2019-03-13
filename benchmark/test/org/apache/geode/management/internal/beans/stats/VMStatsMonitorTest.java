/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.beans.stats;


import StatsKey.VM_PROCESS_CPU_TIME;
import VMStatsMonitor.VALUE_NOT_AVAILABLE;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.geode.test.junit.categories.StatisticsTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;


@Category(StatisticsTest.class)
public class VMStatsMonitorTest {
    @Rule
    public TestName testName = new TestName();

    private VMStatsMonitor vmStatsMonitor;

    @Test
    public void statisticInitialValueShouldBeZeroWhenTheProcessCpuTimeJmxAttributeIsAvailable() {
        vmStatsMonitor = new VMStatsMonitor(testName.getMethodName(), true);
        assertThat(vmStatsMonitor).isNotNull();
        assertThat(vmStatsMonitor.getCpuUsage()).isEqualTo(0);
    }

    @Test
    public void statisticInitialValueShouldBeUndefinedWhenTheProcessCpuTimeJmxAttributeIsNotAvailable() {
        vmStatsMonitor = new VMStatsMonitor(testName.getMethodName(), false);
        assertThat(vmStatsMonitor).isNotNull();
        assertThat(vmStatsMonitor.getCpuUsage()).isEqualTo(VALUE_NOT_AVAILABLE);
    }

    @Test
    public void calculateCpuUsageShouldCorrectlyCalculateTheCpuUsed() {
        Instant now = Instant.now();
        long halfSecondAsNanoseconds = 500000000L;
        long quarterSecondAsNanoseconds = 250000000L;
        long threeQuarterSecondAsNanoseconds = 750000000L;
        vmStatsMonitor = Mockito.spy(new VMStatsMonitor(testName.getMethodName()));
        Mockito.when(vmStatsMonitor.getLastSystemTime()).thenReturn(now.toEpochMilli());
        // 50% used
        Mockito.when(vmStatsMonitor.getLastProcessCpuTime()).thenReturn(0L);
        float initialCpuUsage = vmStatsMonitor.calculateCpuUsage(now.plus(1, ChronoUnit.SECONDS).toEpochMilli(), halfSecondAsNanoseconds);
        assertThat(initialCpuUsage).isNotEqualTo(Float.NaN);
        assertThat(initialCpuUsage).isCloseTo(50.0F, within(1.0F));
        // 25% decrease
        Mockito.when(vmStatsMonitor.getLastProcessCpuTime()).thenReturn(50L);
        float decreasedCpuUsage = vmStatsMonitor.calculateCpuUsage(now.plus(1, ChronoUnit.SECONDS).toEpochMilli(), quarterSecondAsNanoseconds);
        assertThat(decreasedCpuUsage).isNotEqualTo(Float.NaN);
        assertThat(decreasedCpuUsage).isLessThan(initialCpuUsage);
        assertThat(decreasedCpuUsage).isCloseTo(25.0F, within(1.0F));
        // 50% increase
        Mockito.when(vmStatsMonitor.getLastProcessCpuTime()).thenReturn(25L);
        float increasedCpuUsage = vmStatsMonitor.calculateCpuUsage(now.plus(1, ChronoUnit.SECONDS).toEpochMilli(), threeQuarterSecondAsNanoseconds);
        assertThat(increasedCpuUsage).isNotEqualTo(Float.NaN);
        assertThat(increasedCpuUsage).isGreaterThan(decreasedCpuUsage);
        assertThat(increasedCpuUsage).isCloseTo(75.0F, within(1.0F));
    }

    @Test
    public void refreshStatsShouldUpdateCpuUsage() {
        ZonedDateTime now = ZonedDateTime.now();
        vmStatsMonitor = Mockito.spy(new VMStatsMonitor(testName.getMethodName(), true));
        assertThat(vmStatsMonitor).isNotNull();
        assertThat(vmStatsMonitor.getCpuUsage()).isEqualTo(0);
        Number processCpuTime = Mockito.spy(Number.class);
        vmStatsMonitor.statsMap.put(VM_PROCESS_CPU_TIME, processCpuTime);
        // First Run: updates lastSystemTime
        Mockito.when(vmStatsMonitor.currentTimeMillis()).thenReturn(now.toInstant().toEpochMilli());
        vmStatsMonitor.refreshStats();
        assertThat(vmStatsMonitor.getCpuUsage()).isEqualTo(0);
        Mockito.verify(processCpuTime, Mockito.times(0)).longValue();
        // Second Run: updates lastProcessCpuTime
        Mockito.when(processCpuTime.longValue()).thenReturn(500L);
        vmStatsMonitor.refreshStats();
        assertThat(vmStatsMonitor.getCpuUsage()).isEqualTo(0);
        Mockito.verify(processCpuTime, Mockito.times(1)).longValue();
        // Next runs will update the actual cpuUsage
        for (int i = 2; i < 6; i++) {
            long mockProcessCpuTime = i * 500;
            long mockSystemTime = now.plus(i, ChronoUnit.SECONDS).toInstant().toEpochMilli();
            Mockito.when(processCpuTime.longValue()).thenReturn(mockProcessCpuTime);
            Mockito.when(vmStatsMonitor.currentTimeMillis()).thenReturn(mockSystemTime);
            vmStatsMonitor.refreshStats();
            Mockito.verify(vmStatsMonitor, Mockito.times(1)).calculateCpuUsage(mockSystemTime, mockProcessCpuTime);
            assertThat(vmStatsMonitor.getCpuUsage()).isNotEqualTo(Float.NaN);
            assertThat(vmStatsMonitor.getCpuUsage()).isLessThan(1.0F);
        }
    }
}

