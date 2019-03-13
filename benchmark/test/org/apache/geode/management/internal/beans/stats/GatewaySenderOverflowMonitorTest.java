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


import GatewaySenderOverflowMonitor.GatewaySenderOverflowStatisticsListener;
import StatsKey.GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK;
import StatsKey.GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK;
import StatsKey.GATEWAYSENDER_LRU_EVICTIONS;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.geode.Statistics;
import org.apache.geode.internal.statistics.ValueMonitor;
import org.apache.geode.test.junit.categories.StatisticsTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;


@Category(StatisticsTest.class)
public class GatewaySenderOverflowMonitorTest {
    @Rule
    public TestName testName = new TestName();

    private GatewaySenderOverflowMonitor gatewaySenderOverflowMonitor;

    @Test
    public void computeDeltaShouldReturnZeroForUnknownStatistics() {
        assertThat(gatewaySenderOverflowMonitor.computeDelta(Collections.emptyMap(), "unknownStatistic", 6)).isEqualTo(0);
    }

    @Test
    public void computeDeltaShouldOperateForHandledStatistics() {
        Map<String, Number> statsMap = new HashMap<>();
        statsMap.put(GATEWAYSENDER_LRU_EVICTIONS, 50);
        statsMap.put(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK, 2048);
        statsMap.put(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK, 100);
        assertThat(gatewaySenderOverflowMonitor.computeDelta(statsMap, GATEWAYSENDER_LRU_EVICTIONS, 60)).isEqualTo(10L);
        assertThat(gatewaySenderOverflowMonitor.computeDelta(statsMap, GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK, 2100)).isEqualTo(52L);
        assertThat(gatewaySenderOverflowMonitor.computeDelta(statsMap, GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK, 150)).isEqualTo(50L);
    }

    @Test
    public void increaseStatsShouldIncrementStatisticsUsingTheSelectedValue() {
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_LRU_EVICTIONS, 5L);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK, 1024L);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK, 10000L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_LRU_EVICTIONS)).isEqualTo(5L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK)).isEqualTo(1024L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK)).isEqualTo(10000L);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_LRU_EVICTIONS, 5L);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK, 1024L);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK, 10000L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_LRU_EVICTIONS)).isEqualTo(10L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK)).isEqualTo(2048L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK)).isEqualTo(20000L);
    }

    @Test
    public void getStatisticShouldReturnZeroForUnknownStatistics() {
        assertThat(gatewaySenderOverflowMonitor.getStatistic("unhandledStatistic")).isEqualTo(0);
    }

    @Test
    public void getStatisticShouldReturnTheRecordedValueForHandledStatistics() {
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_LRU_EVICTIONS, 5);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK, 2048);
        gatewaySenderOverflowMonitor.increaseStats(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK, 10000);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_LRU_EVICTIONS)).isEqualTo(5L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_BYTES_OVERFLOWED_TO_DISK)).isEqualTo(2048L);
        assertThat(gatewaySenderOverflowMonitor.getStatistic(GATEWAYSENDER_ENTRIES_OVERFLOWED_TO_DISK)).isEqualTo(10000L);
    }

    @Test
    public void addStatisticsToMonitorShouldAddListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        gatewaySenderOverflowMonitor.addStatisticsToMonitor(statistics);
        assertThat(gatewaySenderOverflowMonitor.getMonitors().size()).isEqualTo(1);
        assertThat(gatewaySenderOverflowMonitor.getListeners().size()).isEqualTo(1);
    }

    @Test
    public void stopListenerShouldRemoveListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        GatewaySenderOverflowMonitor.GatewaySenderOverflowStatisticsListener listener = Mockito.mock(GatewaySenderOverflowStatisticsListener.class);
        gatewaySenderOverflowMonitor.getListeners().put(statistics, listener);
        gatewaySenderOverflowMonitor.getMonitors().put(statistics, regionMonitor);
        gatewaySenderOverflowMonitor.stopListener();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
        assertThat(gatewaySenderOverflowMonitor.getMonitors()).isEmpty();
        assertThat(gatewaySenderOverflowMonitor.getListeners()).isEmpty();
    }
}

