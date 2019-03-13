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


import MemberLevelDiskMonitor.MemberLevelDiskStatisticsListener;
import StatsKey.BACKUPS_COMPLETED;
import StatsKey.BACKUPS_IN_PROGRESS;
import StatsKey.DISK_QUEUE_SIZE;
import StatsKey.DISK_READ_BYTES;
import StatsKey.DISK_RECOVERED_BYTES;
import StatsKey.DISK_WRITEN_BYTES;
import StatsKey.FLUSHED_BYTES;
import StatsKey.NUM_FLUSHES;
import StatsKey.TOTAL_FLUSH_TIME;
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
public class MemberLevelDiskMonitorTest {
    @Rule
    public TestName testName = new TestName();

    private MemberLevelDiskMonitor memberLevelDiskMonitor;

    @Test
    public void computeDeltaShouldReturnZeroForUnknownStatistics() {
        assertThat(memberLevelDiskMonitor.computeDelta(Collections.emptyMap(), "unknownStatistic", 6)).isEqualTo(0);
    }

    @Test
    public void computeDeltaShouldOperateForHandledStatistics() {
        Map<String, Number> statsMap = new HashMap<>();
        statsMap.put(NUM_FLUSHES, 10);
        statsMap.put(DISK_QUEUE_SIZE, 148);
        statsMap.put(TOTAL_FLUSH_TIME, 10000);
        statsMap.put(FLUSHED_BYTES, 2048);
        statsMap.put(DISK_READ_BYTES, 1024);
        statsMap.put(DISK_RECOVERED_BYTES, 512);
        statsMap.put(BACKUPS_COMPLETED, 5);
        statsMap.put(DISK_WRITEN_BYTES, 8192);
        statsMap.put(BACKUPS_IN_PROGRESS, 2);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, NUM_FLUSHES, 16)).isEqualTo(6L);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, DISK_QUEUE_SIZE, 150)).isEqualTo(2);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, TOTAL_FLUSH_TIME, 10000)).isEqualTo(0L);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, FLUSHED_BYTES, 3000)).isEqualTo(952L);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, DISK_READ_BYTES, 2048)).isEqualTo(1024L);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, DISK_RECOVERED_BYTES, 1024)).isEqualTo(512L);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, BACKUPS_COMPLETED, 6)).isEqualTo(1);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, DISK_WRITEN_BYTES, 8193)).isEqualTo(1L);
        assertThat(memberLevelDiskMonitor.computeDelta(statsMap, BACKUPS_IN_PROGRESS, 1)).isEqualTo((-1));
    }

    @Test
    public void increaseStatsShouldIncrementStatisticsUsingTheSelectedValue() {
        memberLevelDiskMonitor.increaseStats(NUM_FLUSHES, 5);
        memberLevelDiskMonitor.increaseStats(DISK_QUEUE_SIZE, 13);
        memberLevelDiskMonitor.increaseStats(TOTAL_FLUSH_TIME, 1024);
        memberLevelDiskMonitor.increaseStats(FLUSHED_BYTES, 12);
        memberLevelDiskMonitor.increaseStats(DISK_READ_BYTES, 5);
        memberLevelDiskMonitor.increaseStats(DISK_RECOVERED_BYTES, 2048);
        memberLevelDiskMonitor.increaseStats(BACKUPS_COMPLETED, 20);
        memberLevelDiskMonitor.increaseStats(DISK_WRITEN_BYTES, 51);
        memberLevelDiskMonitor.increaseStats(BACKUPS_IN_PROGRESS, 60);
        assertThat(memberLevelDiskMonitor.getStatistic(NUM_FLUSHES)).isEqualTo(5L);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_QUEUE_SIZE)).isEqualTo(13);
        assertThat(memberLevelDiskMonitor.getStatistic(TOTAL_FLUSH_TIME)).isEqualTo(1024L);
        assertThat(memberLevelDiskMonitor.getStatistic(FLUSHED_BYTES)).isEqualTo(12L);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_READ_BYTES)).isEqualTo(2053L);
        assertThat(memberLevelDiskMonitor.getStatistic(BACKUPS_COMPLETED)).isEqualTo(20);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_WRITEN_BYTES)).isEqualTo(51L);
        assertThat(memberLevelDiskMonitor.getStatistic(BACKUPS_IN_PROGRESS)).isEqualTo(60);
        memberLevelDiskMonitor.increaseStats(NUM_FLUSHES, 2);
        memberLevelDiskMonitor.increaseStats(DISK_QUEUE_SIZE, 2);
        memberLevelDiskMonitor.increaseStats(TOTAL_FLUSH_TIME, 1);
        memberLevelDiskMonitor.increaseStats(FLUSHED_BYTES, 8);
        memberLevelDiskMonitor.increaseStats(DISK_READ_BYTES, 5);
        memberLevelDiskMonitor.increaseStats(DISK_RECOVERED_BYTES, 2);
        memberLevelDiskMonitor.increaseStats(BACKUPS_COMPLETED, 1);
        memberLevelDiskMonitor.increaseStats(DISK_WRITEN_BYTES, 11);
        memberLevelDiskMonitor.increaseStats(BACKUPS_IN_PROGRESS, 6);
        assertThat(memberLevelDiskMonitor.getStatistic(NUM_FLUSHES)).isEqualTo(7L);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_QUEUE_SIZE)).isEqualTo(15);
        assertThat(memberLevelDiskMonitor.getStatistic(TOTAL_FLUSH_TIME)).isEqualTo(1025L);
        assertThat(memberLevelDiskMonitor.getStatistic(FLUSHED_BYTES)).isEqualTo(20L);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_READ_BYTES)).isEqualTo(2060L);
        assertThat(memberLevelDiskMonitor.getStatistic(BACKUPS_COMPLETED)).isEqualTo(21);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_WRITEN_BYTES)).isEqualTo(62L);
        assertThat(memberLevelDiskMonitor.getStatistic(BACKUPS_IN_PROGRESS)).isEqualTo(66);
    }

    @Test
    public void addStatisticsToMonitorShouldAddListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        memberLevelDiskMonitor.addStatisticsToMonitor(statistics);
        assertThat(memberLevelDiskMonitor.getMonitors().size()).isEqualTo(1);
        assertThat(memberLevelDiskMonitor.getListeners().size()).isEqualTo(1);
    }

    @Test
    public void stopListenerShouldRemoveListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        MemberLevelDiskMonitor.MemberLevelDiskStatisticsListener listener = Mockito.mock(MemberLevelDiskStatisticsListener.class);
        memberLevelDiskMonitor.getListeners().put(statistics, listener);
        memberLevelDiskMonitor.getMonitors().put(statistics, regionMonitor);
        memberLevelDiskMonitor.stopListener();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
        assertThat(memberLevelDiskMonitor.getMonitors()).isEmpty();
        assertThat(memberLevelDiskMonitor.getListeners()).isEmpty();
    }

    @Test
    public void getStatisticShouldReturnZeroForUnknownStatistics() {
        assertThat(memberLevelDiskMonitor.getStatistic("unhandledStatistic")).isEqualTo(0);
    }

    @Test
    public void getStatisticShouldReturnTheRecordedValueForHandledStatistics() {
        memberLevelDiskMonitor.increaseStats(NUM_FLUSHES, 5);
        memberLevelDiskMonitor.increaseStats(DISK_QUEUE_SIZE, 13);
        memberLevelDiskMonitor.increaseStats(TOTAL_FLUSH_TIME, 1024);
        memberLevelDiskMonitor.increaseStats(FLUSHED_BYTES, 12);
        memberLevelDiskMonitor.increaseStats(DISK_READ_BYTES, 5);
        memberLevelDiskMonitor.increaseStats(DISK_RECOVERED_BYTES, 2048);
        memberLevelDiskMonitor.increaseStats(BACKUPS_COMPLETED, 20);
        memberLevelDiskMonitor.increaseStats(DISK_WRITEN_BYTES, 51);
        memberLevelDiskMonitor.increaseStats(BACKUPS_IN_PROGRESS, 60);
        assertThat(memberLevelDiskMonitor.getStatistic(NUM_FLUSHES)).isEqualTo(5L);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_QUEUE_SIZE)).isEqualTo(13);
        assertThat(memberLevelDiskMonitor.getStatistic(TOTAL_FLUSH_TIME)).isEqualTo(1024L);
        assertThat(memberLevelDiskMonitor.getStatistic(FLUSHED_BYTES)).isEqualTo(12L);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_READ_BYTES)).isEqualTo(2053L);
        assertThat(memberLevelDiskMonitor.getStatistic(BACKUPS_COMPLETED)).isEqualTo(20);
        assertThat(memberLevelDiskMonitor.getStatistic(DISK_WRITEN_BYTES)).isEqualTo(51L);
        assertThat(memberLevelDiskMonitor.getStatistic(BACKUPS_IN_PROGRESS)).isEqualTo(60);
    }

    @Test
    public void removeStatisticsFromMonitorShouldRemoveListenerAndMonitor() {
        Statistics statistics = Mockito.mock(Statistics.class);
        ValueMonitor regionMonitor = Mockito.mock(ValueMonitor.class);
        MemberLevelDiskMonitor.MemberLevelDiskStatisticsListener listener = Mockito.mock(MemberLevelDiskStatisticsListener.class);
        memberLevelDiskMonitor.getListeners().put(statistics, listener);
        memberLevelDiskMonitor.getMonitors().put(statistics, regionMonitor);
        memberLevelDiskMonitor.removeStatisticsFromMonitor(statistics);
        assertThat(memberLevelDiskMonitor.getListeners()).isEmpty();
        assertThat(memberLevelDiskMonitor.getMonitors()).isEmpty();
        Mockito.verify(listener, Mockito.times(1)).decreaseDiskStoreStats();
        Mockito.verify(regionMonitor, Mockito.times(1)).removeListener(listener);
        Mockito.verify(regionMonitor, Mockito.times(1)).removeStatistics(statistics);
    }

    @Test
    public void decreaseDiskStoreStatsShouldNotThrowNPE() {
        Statistics statistics = Mockito.mock(Statistics.class);
        memberLevelDiskMonitor.addStatisticsToMonitor(statistics);
        memberLevelDiskMonitor.getListeners().values().forEach(( l) -> l.decreaseDiskStoreStats());
    }
}

