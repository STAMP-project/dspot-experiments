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
package org.apache.hadoop.mapred.gridmix;


import GridmixJob.GRIDMIX_TASK_JVM_OPTIONS_ENABLE;
import JobConf.MAPRED_TASK_JAVA_OPTS;
import MRJobConfig.MAP_JAVA_OPTS;
import MRJobConfig.REDUCE_JAVA_OPTS;
import TotalHeapUsageEmulatorPlugin.HEAP_EMULATION_PROGRESS_INTERVAL;
import TotalHeapUsageEmulatorPlugin.HEAP_LOAD_RATIO;
import TotalHeapUsageEmulatorPlugin.MIN_HEAP_FREE_RATIO;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.gridmix.emulators.resourceusage.TotalHeapUsageEmulatorPlugin;
import org.apache.hadoop.mapred.gridmix.emulators.resourceusage.TotalHeapUsageEmulatorPlugin.DefaultHeapUsageEmulator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.tools.rumen.ResourceUsageMetrics;
import org.apache.hadoop.yarn.util.ResourceCalculatorPlugin;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Gridmix memory emulation.
 */
public class TestGridmixMemoryEmulation {
    /**
     * This is a dummy class that fakes heap usage.
     */
    private static class FakeHeapUsageEmulatorCore extends DefaultHeapUsageEmulator {
        private int numCalls = 0;

        @Override
        public void load(long sizeInMB) {
            ++(numCalls);
            super.load(sizeInMB);
        }

        // Get the total number of times load() was invoked
        int getNumCalls() {
            return numCalls;
        }

        // Get the total number of 1mb objects stored within
        long getHeapUsageInMB() {
            return getHeapSpaceSize();
        }

        @Override
        public void reset() {
            // no op to stop emulate() from resetting
        }

        /**
         * For re-testing purpose.
         */
        void resetFake() {
            numCalls = 0;
            super.reset();
        }
    }

    /**
     * This is a dummy class that fakes the heap usage emulator plugin.
     */
    private static class FakeHeapUsageEmulatorPlugin extends TotalHeapUsageEmulatorPlugin {
        private TestGridmixMemoryEmulation.FakeHeapUsageEmulatorCore core;

        public FakeHeapUsageEmulatorPlugin(TestGridmixMemoryEmulation.FakeHeapUsageEmulatorCore core) {
            super(core);
            this.core = core;
        }

        @Override
        protected long getMaxHeapUsageInMB() {
            return (Long.MAX_VALUE) / (ONE_MB);
        }

        @Override
        protected long getTotalHeapUsageInMB() {
            return core.getHeapUsageInMB();
        }
    }

    /**
     * Test {@link TotalHeapUsageEmulatorPlugin}'s core heap usage emulation
     * engine.
     */
    @Test
    public void testHeapUsageEmulator() throws IOException {
        TestGridmixMemoryEmulation.FakeHeapUsageEmulatorCore heapEmulator = new TestGridmixMemoryEmulation.FakeHeapUsageEmulatorCore();
        long testSizeInMB = 10;// 10 mb

        long previousHeap = heapEmulator.getHeapUsageInMB();
        heapEmulator.load(testSizeInMB);
        long currentHeap = heapEmulator.getHeapUsageInMB();
        // check if the heap has increased by expected value
        Assert.assertEquals("Default heap emulator failed to load 10mb", (previousHeap + testSizeInMB), currentHeap);
        // test reset
        heapEmulator.resetFake();
        Assert.assertEquals("Default heap emulator failed to reset", 0, heapEmulator.getHeapUsageInMB());
    }

    /**
     * Test {@link TotalHeapUsageEmulatorPlugin}.
     */
    @Test
    public void testTotalHeapUsageEmulatorPlugin() throws Exception {
        Configuration conf = new Configuration();
        // set the dummy resource calculator for testing
        ResourceCalculatorPlugin monitor = new DummyResourceCalculatorPlugin();
        long maxHeapUsage = 1024 * (TotalHeapUsageEmulatorPlugin.ONE_MB);// 1GB

        conf.setLong(DummyResourceCalculatorPlugin.MAXPMEM_TESTING_PROPERTY, maxHeapUsage);
        monitor.setConf(conf);
        // no buffer to be reserved
        conf.setFloat(MIN_HEAP_FREE_RATIO, 0.0F);
        // only 1 call to be made per cycle
        conf.setFloat(HEAP_LOAD_RATIO, 1.0F);
        long targetHeapUsageInMB = 200;// 200mb

        // fake progress indicator
        TestResourceUsageEmulators.FakeProgressive fakeProgress = new TestResourceUsageEmulators.FakeProgressive();
        // fake heap usage generator
        TestGridmixMemoryEmulation.FakeHeapUsageEmulatorCore fakeCore = new TestGridmixMemoryEmulation.FakeHeapUsageEmulatorCore();
        // a heap usage emulator with fake core
        TestGridmixMemoryEmulation.FakeHeapUsageEmulatorPlugin heapPlugin = new TestGridmixMemoryEmulation.FakeHeapUsageEmulatorPlugin(fakeCore);
        // test with invalid or missing resource usage value
        ResourceUsageMetrics invalidUsage = TestResourceUsageEmulators.createMetrics(0);
        heapPlugin.initialize(conf, invalidUsage, null, null);
        // test if disabled heap emulation plugin's emulate() call is a no-operation
        // this will test if the emulation plugin is disabled or not
        int numCallsPre = fakeCore.getNumCalls();
        long heapUsagePre = fakeCore.getHeapUsageInMB();
        emulate();
        int numCallsPost = fakeCore.getNumCalls();
        long heapUsagePost = fakeCore.getHeapUsageInMB();
        // test if no calls are made heap usage emulator core
        Assert.assertEquals("Disabled heap usage emulation plugin works!", numCallsPre, numCallsPost);
        // test if no calls are made heap usage emulator core
        Assert.assertEquals("Disabled heap usage emulation plugin works!", heapUsagePre, heapUsagePost);
        // test with get progress
        float progress = getProgress();
        Assert.assertEquals(("Invalid progress of disabled cumulative heap usage emulation " + "plugin!"), 1.0F, progress, 0.0F);
        // test with wrong/invalid configuration
        Boolean failed = null;
        invalidUsage = TestResourceUsageEmulators.createMetrics((maxHeapUsage + (TotalHeapUsageEmulatorPlugin.ONE_MB)));
        try {
            heapPlugin.initialize(conf, invalidUsage, monitor, null);
            failed = false;
        } catch (Exception e) {
            failed = true;
        }
        Assert.assertNotNull("Fail case failure!", failed);
        Assert.assertTrue("Expected failure!", failed);
        // test with valid resource usage value
        ResourceUsageMetrics metrics = TestResourceUsageEmulators.createMetrics((targetHeapUsageInMB * (TotalHeapUsageEmulatorPlugin.ONE_MB)));
        // test with default emulation interval
        // in every interval, the emulator will add 100% of the expected usage
        // (since gridmix.emulators.resource-usage.heap.load-ratio=1)
        // so at 10%, emulator will add 10% (difference), at 20% it will add 10% ...
        // So to emulate 200MB, it will add
        // 20mb + 20mb + 20mb + 20mb + .. = 200mb
        TestGridmixMemoryEmulation.testEmulationAccuracy(conf, fakeCore, monitor, metrics, heapPlugin, 200, 10);
        // test with custom value for emulation interval of 20%
        conf.setFloat(HEAP_EMULATION_PROGRESS_INTERVAL, 0.2F);
        // 40mb + 40mb + 40mb + 40mb + 40mb = 200mb
        TestGridmixMemoryEmulation.testEmulationAccuracy(conf, fakeCore, monitor, metrics, heapPlugin, 200, 5);
        // test with custom value of free heap ratio and load ratio = 1
        conf.setFloat(HEAP_LOAD_RATIO, 1.0F);
        conf.setFloat(MIN_HEAP_FREE_RATIO, 0.5F);
        // 40mb + 0mb + 80mb + 0mb + 0mb = 120mb
        TestGridmixMemoryEmulation.testEmulationAccuracy(conf, fakeCore, monitor, metrics, heapPlugin, 120, 2);
        // test with custom value of heap load ratio and min free heap ratio = 0
        conf.setFloat(HEAP_LOAD_RATIO, 0.5F);
        conf.setFloat(MIN_HEAP_FREE_RATIO, 0.0F);
        // 20mb (call#1) + 20mb (call#1) + 20mb (call#2) + 20mb (call#2) +.. = 200mb
        TestGridmixMemoryEmulation.testEmulationAccuracy(conf, fakeCore, monitor, metrics, heapPlugin, 200, 10);
        // test with custom value of free heap ratio = 0.3 and load ratio = 0.5
        conf.setFloat(MIN_HEAP_FREE_RATIO, 0.25F);
        conf.setFloat(HEAP_LOAD_RATIO, 0.5F);
        // 20mb (call#1) + 20mb (call#1) + 30mb (call#2) + 0mb (call#2)
        // + 30mb (call#3) + 0mb (call#3) + 35mb (call#4) + 0mb (call#4)
        // + 37mb (call#5) + 0mb (call#5) = 162mb
        TestGridmixMemoryEmulation.testEmulationAccuracy(conf, fakeCore, monitor, metrics, heapPlugin, 162, 6);
        // test if emulation interval boundary is respected
        fakeProgress = new TestResourceUsageEmulators.FakeProgressive();// initialize

        conf.setFloat(MIN_HEAP_FREE_RATIO, 0.0F);
        conf.setFloat(HEAP_LOAD_RATIO, 1.0F);
        conf.setFloat(HEAP_EMULATION_PROGRESS_INTERVAL, 0.25F);
        heapPlugin.initialize(conf, metrics, monitor, fakeProgress);
        fakeCore.resetFake();
        // take a snapshot after the initialization
        long initHeapUsage = fakeCore.getHeapUsageInMB();
        long initNumCallsUsage = fakeCore.getNumCalls();
        // test with 0 progress
        TestGridmixMemoryEmulation.testEmulationBoundary(0.0F, fakeCore, fakeProgress, heapPlugin, initHeapUsage, initNumCallsUsage, "[no-op, 0 progress]");
        // test with 24% progress
        TestGridmixMemoryEmulation.testEmulationBoundary(0.24F, fakeCore, fakeProgress, heapPlugin, initHeapUsage, initNumCallsUsage, "[no-op, 24% progress]");
        // test with 25% progress
        TestGridmixMemoryEmulation.testEmulationBoundary(0.25F, fakeCore, fakeProgress, heapPlugin, (targetHeapUsageInMB / 4), 1, "[op, 25% progress]");
        // test with 80% progress
        TestGridmixMemoryEmulation.testEmulationBoundary(0.8F, fakeCore, fakeProgress, heapPlugin, ((targetHeapUsageInMB * 4) / 5), 2, "[op, 80% progress]");
        // now test if the final call with 100% progress ramps up the heap usage
        TestGridmixMemoryEmulation.testEmulationBoundary(1.0F, fakeCore, fakeProgress, heapPlugin, targetHeapUsageInMB, 3, "[op, 100% progress]");
    }

    /**
     * Test task-level java heap options configuration in {@link GridmixJob}.
     */
    @Test
    public void testJavaHeapOptions() throws Exception {
        // test missing opts
        testJavaHeapOptions(null, null, null, null, null, null, null, null, null);
        // test original heap opts and missing default opts
        testJavaHeapOptions("-Xms10m", "-Xms20m", "-Xms30m", null, null, null, null, null, null);
        // test missing opts with default opts
        testJavaHeapOptions(null, null, null, "-Xms10m", "-Xms20m", "-Xms30m", "-Xms10m", "-Xms20m", "-Xms30m");
        // test empty option
        testJavaHeapOptions("", "", "", null, null, null, null, null, null);
        // test empty default option and no original heap options
        testJavaHeapOptions(null, null, null, "", "", "", "", "", "");
        // test empty opts and default opts
        testJavaHeapOptions("", "", "", "-Xmx10m -Xms1m", "-Xmx50m -Xms2m", "-Xms2m -Xmx100m", "-Xmx10m -Xms1m", "-Xmx50m -Xms2m", "-Xms2m -Xmx100m");
        // test custom heap opts with no default opts
        testJavaHeapOptions("-Xmx10m", "-Xmx20m", "-Xmx30m", null, null, null, "-Xmx10m", "-Xmx20m", "-Xmx30m");
        // test heap opts with default opts (multiple value)
        testJavaHeapOptions("-Xms5m -Xmx200m", "-Xms15m -Xmx300m", "-Xms25m -Xmx50m", "-XXabc", "-XXxyz", "-XXdef", "-XXabc -Xmx200m", "-XXxyz -Xmx300m", "-XXdef -Xmx50m");
        // test heap opts with default opts (duplication of -Xmx)
        testJavaHeapOptions("-Xms5m -Xmx200m", "-Xms15m -Xmx300m", "-Xms25m -Xmx50m", "-XXabc -Xmx500m", "-XXxyz -Xmx600m", "-XXdef -Xmx700m", "-XXabc -Xmx200m", "-XXxyz -Xmx300m", "-XXdef -Xmx50m");
        // test heap opts with default opts (single value)
        testJavaHeapOptions("-Xmx10m", "-Xmx20m", "-Xmx50m", "-Xms2m", "-Xms3m", "-Xms5m", "-Xms2m -Xmx10m", "-Xms3m -Xmx20m", "-Xms5m -Xmx50m");
        // test heap opts with default opts (duplication of -Xmx)
        testJavaHeapOptions("-Xmx10m", "-Xmx20m", "-Xmx50m", "-Xmx2m", "-Xmx3m", "-Xmx5m", "-Xmx10m", "-Xmx20m", "-Xmx50m");
    }

    /**
     * Test disabled task heap options configuration in {@link GridmixJob}.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testJavaHeapOptionsDisabled() throws Exception {
        Configuration gridmixConf = new Configuration();
        gridmixConf.setBoolean(GRIDMIX_TASK_JVM_OPTIONS_ENABLE, false);
        // set the default values of simulated job
        gridmixConf.set(MAP_JAVA_OPTS, "-Xmx1m");
        gridmixConf.set(REDUCE_JAVA_OPTS, "-Xmx2m");
        gridmixConf.set(MAPRED_TASK_JAVA_OPTS, "-Xmx3m");
        // set the default map and reduce task options for original job
        final JobConf originalConf = new JobConf();
        originalConf.set(MAP_JAVA_OPTS, "-Xmx10m");
        originalConf.set(REDUCE_JAVA_OPTS, "-Xmx20m");
        originalConf.set(MAPRED_TASK_JAVA_OPTS, "-Xmx30m");
        // define a mock job
        DebugJobProducer.MockJob story = new DebugJobProducer.MockJob(originalConf) {
            public JobConf getJobConf() {
                return originalConf;
            }
        };
        GridmixJob job = new TestHighRamJob.DummyGridmixJob(gridmixConf, story);
        Job simulatedJob = job.getJob();
        Configuration simulatedConf = simulatedJob.getConfiguration();
        Assert.assertEquals("Map heap options works when disabled!", "-Xmx1m", simulatedConf.get(MAP_JAVA_OPTS));
        Assert.assertEquals("Reduce heap options works when disabled!", "-Xmx2m", simulatedConf.get(REDUCE_JAVA_OPTS));
        Assert.assertEquals("Task heap options works when disabled!", "-Xmx3m", simulatedConf.get(MAPRED_TASK_JAVA_OPTS));
    }
}

