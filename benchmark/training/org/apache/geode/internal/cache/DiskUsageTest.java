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
package org.apache.geode.internal.cache;


import DiskState.CRITICAL;
import DiskState.NORMAL;
import DiskState.WARN;
import java.io.File;
import org.apache.geode.internal.cache.DiskStoreMonitor.DiskState;
import org.apache.geode.internal.cache.DiskStoreMonitor.DiskUsage;
import org.junit.Test;
import org.mockito.Mockito;


public class DiskUsageTest {
    @Test
    public void defaultStateIsNormal() {
        DiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage();
        assertThat(diskUsage.getState()).isEqualTo(NORMAL);
    }

    @Test
    public void updateReturnsCurrentStateIfThresholdsDisabled() {
        DiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage();
        assertThat(diskUsage.update(0.0F, 0.0F)).isEqualTo(NORMAL);
    }

    @Test
    public void updateReturnsCurrentStateIfDirectoryDoesNotExist() {
        File dir = Mockito.mock(File.class);
        DiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        assertThat(diskUsage.update(1.0F, 0.0F)).isEqualTo(NORMAL);
        Mockito.verify(dir, Mockito.times(1)).exists();
    }

    @Test
    public void updateSendsTotalToRecordsStats() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        final long expectedTotal = 123;
        Mockito.when(dir.getTotalSpace()).thenReturn(expectedTotal);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        update(0.0F, 1.0F);
        assertThat(diskUsage.getTotal()).isEqualTo(expectedTotal);
    }

    @Test
    public void updateSendsUsableSpaceToRecordsStats() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        final long expectedFree = 456;
        Mockito.when(dir.getUsableSpace()).thenReturn(expectedFree);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        update(0.0F, 1.0F);
        assertThat(diskUsage.getFree()).isEqualTo(expectedFree);
    }

    @Test
    public void updateChangesStateToWarn() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 2% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(100L);
        Mockito.when(dir.getUsableSpace()).thenReturn(98L);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        // warn if over 1.9% used
        assertThat(update(1.9F, 0.0F)).isEqualTo(WARN);
        assertThat(diskUsage.getNext()).isEqualTo(WARN);
        assertThat(diskUsage.getPct()).isEqualTo("2%");
        assertThat(diskUsage.getCriticalMessage()).isNull();
    }

    @Test
    public void updateStaysNormalIfBelowWarnThreshold() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 2% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(100L);
        Mockito.when(dir.getUsableSpace()).thenReturn(98L);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        // warn if over 2.1% used
        assertThat(update(2.1F, 0.0F)).isEqualTo(NORMAL);
        assertThat(diskUsage.getNext()).isNull();
        assertThat(diskUsage.getPct()).isNull();
        assertThat(diskUsage.getCriticalMessage()).isNull();
    }

    @Test
    public void updateWarnThresholdIgnoresMinimum() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 2% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(100L);
        Mockito.when(dir.getUsableSpace()).thenReturn(98L);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir, 1);
        // warn if over 2.1% used
        assertThat(update(2.1F, 0.0F)).isEqualTo(NORMAL);
        assertThat(diskUsage.getNext()).isNull();
        assertThat(diskUsage.getPct()).isNull();
        assertThat(diskUsage.getCriticalMessage()).isNull();
    }

    @Test
    public void updateChangesStateToCritical() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 2% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(100L);
        Mockito.when(dir.getUsableSpace()).thenReturn(98L);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        // critical if over 1.9% used
        assertThat(update(0.0F, 1.9F)).isEqualTo(CRITICAL);
        assertThat(diskUsage.getNext()).isEqualTo(CRITICAL);
        assertThat(diskUsage.getPct()).isEqualTo("2%");
        assertThat(diskUsage.getCriticalMessage()).isEqualTo("the file system is 2% full, which reached the critical threshold of 1.9%.");
    }

    @Test
    public void updateStaysNormalIfBelowCriticalThreshold() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 2% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(100L);
        Mockito.when(dir.getUsableSpace()).thenReturn(98L);
        DiskUsageTest.TestableDiskUsage diskUsage = new DiskUsageTest.TestableDiskUsage(dir);
        // critical if over 2.1% used
        assertThat(update(0.0F, 2.1F)).isEqualTo(NORMAL);
        assertThat(diskUsage.getNext()).isNull();
        assertThat(diskUsage.getPct()).isNull();
        assertThat(diskUsage.getCriticalMessage()).isNull();
    }

    @Test
    public void updateCriticalThresholdGoesCriticalIfBelowMinimum() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 2% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(100L);
        Mockito.when(dir.getUsableSpace()).thenReturn(98L);
        DiskUsageTest.TestableDiskUsage diskUsage = /* one megabyte */
        new DiskUsageTest.TestableDiskUsage(dir, 1);
        // critical if over 2.1% used
        assertThat(update(0.0F, 2.1F)).isEqualTo(CRITICAL);
        assertThat(diskUsage.getNext()).isEqualTo(CRITICAL);
        assertThat(diskUsage.getPct()).isEqualTo("2%");
        assertThat(diskUsage.getCriticalMessage()).isEqualTo("the file system only has 98 bytes free which is below the minimum of 1048576.");
    }

    @Test
    public void criticalMessageStatesUsageExceedsCritical() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        Mockito.when(dir.getTotalSpace()).thenReturn(((1024L * 1024L) * 3L));
        Mockito.when(dir.getUsableSpace()).thenReturn(((1024L * 1024L) * 2L));
        DiskUsageTest.TestableDiskUsage diskUsage = /* one megabyte */
        new DiskUsageTest.TestableDiskUsage(dir, 1);
        assertThat(update(0.0F, 33.2F)).isEqualTo(CRITICAL);
        assertThat(diskUsage.getNext()).isEqualTo(CRITICAL);
        assertThat(diskUsage.getPct()).isEqualTo("33.3%");
        assertThat(diskUsage.getCriticalMessage()).isEqualTo("the file system is 33.3% full, which reached the critical threshold of 33.2%.");
    }

    @Test
    public void criticalMessageStatesUsageExceedsCriticalWithManyDigits() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        Mockito.when(dir.getTotalSpace()).thenReturn(((1024L * 1024L) * 3L));
        Mockito.when(dir.getUsableSpace()).thenReturn(((1024L * 1024L) * 2L));
        DiskUsageTest.TestableDiskUsage diskUsage = /* one megabyte */
        new DiskUsageTest.TestableDiskUsage(dir, 1);
        assertThat(update(0.0F, 33.257835F)).isEqualTo(CRITICAL);
        assertThat(diskUsage.getNext()).isEqualTo(CRITICAL);
        assertThat(diskUsage.getPct()).isEqualTo("33.3%");
        assertThat(diskUsage.getCriticalMessage()).isEqualTo("the file system is 33.3% full, which reached the critical threshold of 33.3%.");
    }

    @Test
    public void updateCriticalThresholdStaysNormalIfFreeSpaceAboveMinimum() {
        File dir = Mockito.mock(File.class);
        Mockito.when(dir.exists()).thenReturn(true);
        // File indicates 98% of disk used
        Mockito.when(dir.getTotalSpace()).thenReturn(((100L * 1024) * 1024));
        Mockito.when(dir.getUsableSpace()).thenReturn(((2L * 1024) * 1024));
        DiskUsageTest.TestableDiskUsage diskUsage = /* one megabyte */
        new DiskUsageTest.TestableDiskUsage(dir, 1);
        // critical if over 98.1% used
        assertThat(update(0.0F, 98.1F)).isEqualTo(NORMAL);
        assertThat(diskUsage.getNext()).isNull();
        assertThat(diskUsage.getPct()).isNull();
        assertThat(diskUsage.getCriticalMessage()).isNull();
    }

    private static class TestableDiskUsage extends DiskUsage {
        private final File dir;

        private final long minimum;

        private long total;

        private long free;

        private long elapsed;

        private DiskState next;

        private String pct;

        private String criticalMessage;

        public TestableDiskUsage(File dir, long min) {
            this.dir = dir;
            this.minimum = min;
        }

        public TestableDiskUsage(File dir) {
            this.dir = dir;
            this.minimum = 0L;
        }

        public TestableDiskUsage() {
            this.dir = null;
            this.minimum = 0L;
        }

        @Override
        protected File dir() {
            return this.dir;
        }

        @Override
        protected long getMinimumSpace() {
            return this.minimum;
        }

        @Override
        protected void recordStats(long total, long free, long elapsed) {
            this.total = total;
            this.free = free;
            this.elapsed = elapsed;
        }

        @Override
        protected void handleStateChange(DiskState next, String pct, String criticalMessage) {
            this.next = next;
            this.pct = pct;
            this.criticalMessage = criticalMessage;
        }

        public long getTotal() {
            return this.total;
        }

        public long getFree() {
            return this.free;
        }

        public DiskState getNext() {
            return this.next;
        }

        public String getPct() {
            return this.pct;
        }

        public String getCriticalMessage() {
            return this.criticalMessage;
        }
    }
}

