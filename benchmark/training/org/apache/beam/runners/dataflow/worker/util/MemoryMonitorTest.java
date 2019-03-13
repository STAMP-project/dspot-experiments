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
package org.apache.beam.runners.dataflow.worker.util;


import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test the memory monitor will block threads when the server is in a (faked) GC thrashing state.
 */
@RunWith(JUnit4.class)
public class MemoryMonitorTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    static class FakeGCStatsProvider implements MemoryMonitor.GCStatsProvider {
        AtomicBoolean inGCThrashingState = new AtomicBoolean(false);

        long lastCallTimestamp = System.currentTimeMillis();

        long lastGCResult = 0;

        @Override
        public long totalGCTimeMilliseconds() {
            if (inGCThrashingState.get()) {
                long now = System.currentTimeMillis();
                lastGCResult += now - (lastCallTimestamp);
                lastCallTimestamp = now;
            }
            return lastGCResult;
        }
    }

    private MemoryMonitorTest.FakeGCStatsProvider provider;

    private File localDumpFolder;

    private MemoryMonitor monitor;

    private Thread thread;

    @Test(timeout = 1000)
    public void detectGCThrashing() throws InterruptedException {
        monitor.waitForRunning();
        monitor.waitForResources("Test1");
        provider.inGCThrashingState.set(true);
        monitor.waitForThrashingState(true);
        final Semaphore s = new Semaphore(0);
        new Thread(() -> {
            monitor.waitForResources("Test2");
            s.release();
        }).start();
        Assert.assertFalse(s.tryAcquire(100, TimeUnit.MILLISECONDS));
        provider.inGCThrashingState.set(false);
        monitor.waitForThrashingState(false);
        Assert.assertTrue(s.tryAcquire(100, TimeUnit.MILLISECONDS));
        monitor.waitForResources("Test3");
    }

    @Test
    public void heapDumpOnce() throws Exception {
        File folder = tempFolder.newFolder();
        File dump1 = MemoryMonitor.dumpHeap(folder);
        Assert.assertNotNull(dump1);
        Assert.assertTrue(dump1.exists());
        Assert.assertThat(dump1.getParentFile(), Matchers.equalTo(folder));
    }

    @Test
    public void heapDumpTwice() throws Exception {
        File folder = tempFolder.newFolder();
        File dump1 = MemoryMonitor.dumpHeap(folder);
        Assert.assertNotNull(dump1);
        Assert.assertTrue(dump1.exists());
        Assert.assertThat(dump1.getParentFile(), Matchers.equalTo(folder));
        File dump2 = MemoryMonitor.dumpHeap(folder);
        Assert.assertNotNull(dump2);
        Assert.assertTrue(dump2.exists());
        Assert.assertThat(dump2.getParentFile(), Matchers.equalTo(folder));
    }

    @Test
    public void uploadToGcs() throws Exception {
        File remoteFolder = tempFolder.newFolder();
        monitor = MemoryMonitor.forTest(provider, 10, 0, true, remoteFolder.getPath(), localDumpFolder);
        // Force the monitor to generate a local heap dump
        monitor.dumpHeap();
        // Try to upload the heap dump
        Assert.assertTrue(monitor.tryUploadHeapDumpIfItExists());
        File[] files = remoteFolder.listFiles();
        Assert.assertThat(files, Matchers.arrayWithSize(1));
        Assert.assertThat(files[0].getAbsolutePath(), Matchers.containsString("heap_dump"));
        Assert.assertThat(files[0].getAbsolutePath(), Matchers.containsString("hprof"));
    }

    @Test
    public void uploadToGcsDisabled() throws Exception {
        monitor = MemoryMonitor.forTest(provider, 10, 0, true, null, localDumpFolder);
        // Force the monitor to generate a local heap dump
        monitor.dumpHeap();
        // Try to upload the heap dump
        Assert.assertFalse(monitor.tryUploadHeapDumpIfItExists());
    }
}

