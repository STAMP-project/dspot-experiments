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


import AggregateRegionStatsMonitor.MemberLevelRegionStatisticsListener;
import StatsKey.BUCKET_COUNT;
import StatsKey.DISK_SPACE;
import StatsKey.LRU_DESTROYS;
import StatsKey.LRU_EVICTIONS;
import StatsKey.PRIMARY_BUCKET_COUNT;
import StatsKey.TOTAL_BUCKET_SIZE;
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
public class AggregateRegionStatsMonitorTest {
    @Rule
    public TestName testName = new TestName();

    private AggregateRegionStatsMonitor aggregateRegionStatsMonitor;

    @Test
    public void computeDeltaShouldReturnZeroForUnknownStatistics() {
        assertThat(aggregateRegionStatsMonitor.computeDelta(Collections.emptyMap(), "unknownStatistic", 6)).isEqualTo(0);
    }

    @Test
    public void computeDeltaShouldOperateForHandledStatistics() {
        Map<String, Number> statsMap = new HashMap<>();
        statsMap.put(PRIMARY_BUCKET_COUNT, 5);
        statsMap.put(BUCKET_COUNT, 13);
        statsMap.put(TOTAL_BUCKET_SIZE, 1024);
        statsMap.put(LRU_EVICTIONS, 12);
        statsMap.put(LRU_DESTROYS, 5);
        statsMap.put(DISK_SPACE, 2048);
        assertThat(aggregateRegionStatsMonitor.computeDelta(statsMap, PRIMARY_BUCKET_COUNT, 6)).isEqualTo(1);
        assertThat(aggregateRegionStatsMonitor.computeDelta(statsMap, BUCKET_COUNT, 15)).isEqualTo(2);
        assertThat(aggregateRegionStatsMonitor.computeDelta(statsMap, TOTAL_BUCKET_SIZE, 1030)).isEqualTo(6);
        assertThat(aggregateRegionStatsMonitor.computeDelta(statsMap, LRU_EVICTIONS, 20)).isEqualTo(8L);
        assertThat(aggregateRegionStatsMonitor.computeDelta(statsMap, LRU_DESTROYS, 6)).isEqualTo(1L);
        assertThat(aggregateRegionStatsMonitor.computeDelta(statsMap, DISK_SPACE, 2050)).isEqualTo(2L);
    }

    @Test
    public void increaseStatsShouldIncrementStatisticsUsingTheSelectedValue() {
        aggregateRegionStatsMonitor.increaseStats(PRIMARY_BUCKET_COUNT, 5);
        aggregateRegionStatsMonitor.increaseStats(BUCKET_COUNT, 13);
        aggregateRegionStatsMonitor.increaseStats(TOTAL_BUCKET_SIZE, 1024);
        aggregateRegionStatsMonitor.increaseStats(LRU_EVICTIONS, 12);
        aggregateRegionStatsMonitor.increaseStats(LRU_DESTROYS, 5);
        aggregateRegionStatsMonitor.increaseStats(DISK_SPACE, 2048);
        assertThat(aggregateRegionStatsMonitor.getStatistic(PRIMARY_BUCKET_COUNT)).isEqualTo(5);
        assertThat(aggregateRegionStatsMonitor.getStatistic(BUCKET_COUNT)).isEqualTo(13);
        assertThat(aggregateRegionStatsMonitor.getStatistic(TOTAL_BUCKET_SIZE)).isEqualTo(1024);
        assertThat(aggregateRegionStatsMonitor.getStatistic(LRU_EVICTIONS)).isEqualTo(12L);
        assertThat(aggregateRegionStatsMonitor.getStatistic(LRU_DESTROYS)).isEqualTo(5L);
        assertThat(aggregateRegionStatsMonitor.getStatistic(DISK_SPACE)).isEqualTo(2048L);
        aggregateRegionStatsMonitor.increaseStats(PRIMARY_BUCKET_COUNT, 2);
        aggregateRegionStatsMonitor.increaseStats(BUCKET_COUNT, 2);
        aggregateRegionStatsMonitor.increaseStats(TOTAL_BUCKET_SIZE, 1);
        aggregateRegionStatsMonitor.increaseStats(LRU_EVICTIONS, 8);
        aggregateRegionStatsMonitor.increaseStats(LRU_DESTROYS, 5);
        aggregateRegionStatsMonitor.increaseStats(DISK_SPACE, 2);
        assertThat(aggregateRegionStatsMonitor.getStatistic(PRIMARY_BUCKET_COUNT)).isEqualTo(7);
        assertThat(aggregateRegionStatsMonitor.getStatistic(BUCKET_COUNT)).isEqualTo(15);
        assertThat(aggregateRegionStatsMonitor.getStatistic(TOTAL_BUCKET_SIZE)).isEqualTo(1025);
        assertThat(aggregateRegionStatsMonitor.getStatistic(LRU_EVICTIONS)).isEqualTo(20L);
        assertThat(aggregateRegionStatsMonitor.getStatistic(LRU_DESTROYS)).isEqualTo(10L);
        assertThat(aggregateRegionStatsMonitor.getStatistic(DISK_SPACE)).isEqualTo(2050L);
    }

    @Test
    public void removeLRUStatisticsShouldRemoveListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        AggregateRegionStatsMonitor.MemberLevelRegionStatisticsListener listener = Mockito.mock(MemberLevelRegionStatisticsListener.class);
        aggregateRegionStatsMonitor.getListeners().put(statistics, listener);
        aggregateRegionStatsMonitor.getMonitors().put(statistics, regionMonitor);
        aggregateRegionStatsMonitor.removeLRUStatistics(statistics);
        assertThat(aggregateRegionStatsMonitor.getListeners()).isEmpty();
        assertThat(aggregateRegionStatsMonitor.getMonitors()).isEmpty();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
    }

    @Test
    public void removeDirectoryStatisticsShouldRemoveListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        AggregateRegionStatsMonitor.MemberLevelRegionStatisticsListener listener = Mockito.mock(MemberLevelRegionStatisticsListener.class);
        aggregateRegionStatsMonitor.getListeners().put(statistics, listener);
        aggregateRegionStatsMonitor.getMonitors().put(statistics, regionMonitor);
        aggregateRegionStatsMonitor.removeDirectoryStatistics(statistics);
        assertThat(aggregateRegionStatsMonitor.getListeners()).isEmpty();
        assertThat(aggregateRegionStatsMonitor.getMonitors()).isEmpty();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
    }

    @Test
    public void removePartitionStatisticsShouldDecreaseStatsAndRemoveBothListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        AggregateRegionStatsMonitor.MemberLevelRegionStatisticsListener listener = Mockito.mock(MemberLevelRegionStatisticsListener.class);
        aggregateRegionStatsMonitor.getListeners().put(statistics, listener);
        aggregateRegionStatsMonitor.getMonitors().put(statistics, regionMonitor);
        aggregateRegionStatsMonitor.removePartitionStatistics(statistics);
        assertThat(aggregateRegionStatsMonitor.getListeners()).isEmpty();
        assertThat(aggregateRegionStatsMonitor.getMonitors()).isEmpty();
        Mockito.verify(listener, Mockito.times(1)).decreaseParStats();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
    }

    @Test
    public void getStatisticShouldReturnZeroForUnknownStatistics() {
        assertThat(aggregateRegionStatsMonitor.getStatistic("unhandledStatistic")).isEqualTo(0);
    }

    @Test
    public void getStatisticShouldReturnTheRecordedValueForHandledStatistics() {
        aggregateRegionStatsMonitor.increaseStats(PRIMARY_BUCKET_COUNT, 5);
        aggregateRegionStatsMonitor.increaseStats(BUCKET_COUNT, 13);
        aggregateRegionStatsMonitor.increaseStats(TOTAL_BUCKET_SIZE, 1024);
        aggregateRegionStatsMonitor.increaseStats(LRU_EVICTIONS, 12);
        aggregateRegionStatsMonitor.increaseStats(LRU_DESTROYS, 5);
        aggregateRegionStatsMonitor.increaseStats(DISK_SPACE, 2048);
        assertThat(aggregateRegionStatsMonitor.getStatistic(PRIMARY_BUCKET_COUNT)).isEqualTo(5);
        assertThat(aggregateRegionStatsMonitor.getStatistic(BUCKET_COUNT)).isEqualTo(13);
        assertThat(aggregateRegionStatsMonitor.getStatistic(TOTAL_BUCKET_SIZE)).isEqualTo(1024);
        assertThat(aggregateRegionStatsMonitor.getStatistic(LRU_EVICTIONS)).isEqualTo(12L);
        assertThat(aggregateRegionStatsMonitor.getStatistic(LRU_DESTROYS)).isEqualTo(5L);
        assertThat(aggregateRegionStatsMonitor.getStatistic(DISK_SPACE)).isEqualTo(2048L);
    }

    @Test
    public void addStatisticsToMonitorShouldAddListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        aggregateRegionStatsMonitor.addStatisticsToMonitor(statistics);
        assertThat(aggregateRegionStatsMonitor.getMonitors().size()).isEqualTo(1);
        assertThat(aggregateRegionStatsMonitor.getListeners().size()).isEqualTo(1);
    }

    @Test
    public void stopListenerShouldRemoveListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        AggregateRegionStatsMonitor.MemberLevelRegionStatisticsListener listener = Mockito.mock(MemberLevelRegionStatisticsListener.class);
        aggregateRegionStatsMonitor.getListeners().put(statistics, listener);
        aggregateRegionStatsMonitor.getMonitors().put(statistics, regionMonitor);
        aggregateRegionStatsMonitor.stopListener();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
        assertThat(aggregateRegionStatsMonitor.getMonitors()).isEmpty();
        assertThat(aggregateRegionStatsMonitor.getListeners()).isEmpty();
    }

    @Test
    public void decreaseDiskStoreStatsShouldNotThrowNPE() {
        Statistics statistics = Mockito.mock(Statistics.class);
        aggregateRegionStatsMonitor.addStatisticsToMonitor(statistics);
        aggregateRegionStatsMonitor.getListeners().values().forEach(( l) -> l.decreaseParStats());
    }
}

