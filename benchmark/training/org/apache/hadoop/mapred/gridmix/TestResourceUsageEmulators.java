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


import CumulativeCpuUsageEmulatorPlugin.CPU_EMULATION_PROGRESS_INTERVAL;
import ResourceUsageMatcher.RESOURCE_USAGE_EMULATION_PLUGINS;
import TTConfig.TT_RESOURCE_CALCULATOR_PLUGIN;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.gridmix.LoadJob.ResourceUsageMatcherRunner;
import org.apache.hadoop.mapred.gridmix.emulators.resourceusage.CumulativeCpuUsageEmulatorPlugin;
import org.apache.hadoop.mapred.gridmix.emulators.resourceusage.CumulativeCpuUsageEmulatorPlugin.DefaultCpuUsageEmulator;
import org.apache.hadoop.mapred.gridmix.emulators.resourceusage.ResourceUsageEmulatorPlugin;
import org.apache.hadoop.mapred.gridmix.emulators.resourceusage.ResourceUsageMatcher;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.StatusReporter;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.tools.rumen.ResourceUsageMetrics;
import org.apache.hadoop.yarn.util.ResourceCalculatorPlugin;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Gridmix's resource emulator framework and supported plugins.
 */
public class TestResourceUsageEmulators {
    /**
     * A {@link ResourceUsageEmulatorPlugin} implementation for testing purpose.
     * It essentially creates a file named 'test' in the test directory.
     */
    static class TestResourceUsageEmulatorPlugin implements ResourceUsageEmulatorPlugin {
        static final Path rootTempDir = new Path(System.getProperty("test.build.data", "/tmp"));

        static final Path tempDir = new Path(TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.rootTempDir, "TestResourceUsageEmulatorPlugin");

        static final String DEFAULT_IDENTIFIER = "test";

        private Path touchPath = null;

        private FileSystem fs = null;

        @Override
        public void emulate() throws IOException, InterruptedException {
            // add some time between 2 calls to emulate()
            try {
                Thread.sleep(1000);// sleep for 1s

            } catch (Exception e) {
            }
            try {
                fs.delete(touchPath, false);// delete the touch file

                // TODO Search for a better touch utility
                fs.create(touchPath).close();// recreate it

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected String getIdentifier() {
            return TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.DEFAULT_IDENTIFIER;
        }

        private static Path getFilePath(String id) {
            return new Path(TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.tempDir, id);
        }

        private static Path getInitFilePath(String id) {
            return new Path(TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.tempDir, (id + ".init"));
        }

        @Override
        public void initialize(Configuration conf, ResourceUsageMetrics metrics, ResourceCalculatorPlugin monitor, Progressive progress) {
            // add some time between 2 calls to initialize()
            try {
                Thread.sleep(1000);// sleep for 1s

            } catch (Exception e) {
            }
            try {
                fs = FileSystem.getLocal(conf);
                Path initPath = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.getInitFilePath(getIdentifier());
                fs.delete(initPath, false);// delete the old file

                fs.create(initPath).close();// create a new one

                touchPath = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.getFilePath(getIdentifier());
                fs.delete(touchPath, false);
            } catch (Exception e) {
            } finally {
                if ((fs) != null) {
                    try {
                        fs.deleteOnExit(TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.tempDir);
                    } catch (IOException ioe) {
                    }
                }
            }
        }

        // test if the emulation framework successfully loaded this plugin
        static long testInitialization(String id, Configuration conf) throws IOException {
            Path testPath = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.getInitFilePath(id);
            FileSystem fs = FileSystem.getLocal(conf);
            return fs.exists(testPath) ? fs.getFileStatus(testPath).getModificationTime() : 0;
        }

        // test if the emulation framework successfully loaded this plugin
        static long testEmulation(String id, Configuration conf) throws IOException {
            Path testPath = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.getFilePath(id);
            FileSystem fs = FileSystem.getLocal(conf);
            return fs.exists(testPath) ? fs.getFileStatus(testPath).getModificationTime() : 0;
        }

        @Override
        public float getProgress() {
            try {
                return fs.exists(touchPath) ? 1.0F : 0.0F;
            } catch (IOException ioe) {
            }
            return 0.0F;
        }
    }

    /**
     * Test implementation of {@link ResourceUsageEmulatorPlugin} which creates
     * a file named 'others' in the test directory.
     */
    static class TestOthers extends TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin {
        static final String ID = "others";

        @Override
        protected String getIdentifier() {
            return TestResourceUsageEmulators.TestOthers.ID;
        }
    }

    /**
     * Test implementation of {@link ResourceUsageEmulatorPlugin} which creates
     * a file named 'cpu' in the test directory.
     */
    static class TestCpu extends TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin {
        static final String ID = "cpu";

        @Override
        protected String getIdentifier() {
            return TestResourceUsageEmulators.TestCpu.ID;
        }
    }

    /**
     * Test {@link ResourceUsageMatcher}.
     */
    @Test
    public void testResourceUsageMatcher() throws Exception {
        ResourceUsageMatcher matcher = new ResourceUsageMatcher();
        Configuration conf = new Configuration();
        conf.setClass(RESOURCE_USAGE_EMULATION_PLUGINS, TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.class, ResourceUsageEmulatorPlugin.class);
        long currentTime = System.currentTimeMillis();
        matcher.configure(conf, null, null, null);
        matcher.matchResourceUsage();
        String id = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.DEFAULT_IDENTIFIER;
        long result = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testInitialization(id, conf);
        Assert.assertTrue(("Resource usage matcher failed to initialize the configured" + " plugin"), (result > currentTime));
        result = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testEmulation(id, conf);
        Assert.assertTrue(("Resource usage matcher failed to load and emulate the" + " configured plugin"), (result > currentTime));
        // test plugin order to first emulate cpu and then others
        conf.setStrings(RESOURCE_USAGE_EMULATION_PLUGINS, (((TestResourceUsageEmulators.TestCpu.class.getName()) + ",") + (TestResourceUsageEmulators.TestOthers.class.getName())));
        matcher.configure(conf, null, null, null);
        // test the initialization order
        long time1 = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testInitialization(TestResourceUsageEmulators.TestCpu.ID, conf);
        long time2 = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testInitialization(TestResourceUsageEmulators.TestOthers.ID, conf);
        Assert.assertTrue(("Resource usage matcher failed to initialize the configured" + " plugins in order"), (time1 < time2));
        matcher.matchResourceUsage();
        // Note that the cpu usage emulator plugin is configured 1st and then the
        // others plugin.
        time1 = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testInitialization(TestResourceUsageEmulators.TestCpu.ID, conf);
        time2 = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testInitialization(TestResourceUsageEmulators.TestOthers.ID, conf);
        Assert.assertTrue("Resource usage matcher failed to load the configured plugins", (time1 < time2));
    }

    /**
     * Fakes the cumulative usage using {@link FakeCpuUsageEmulatorCore}.
     */
    static class FakeResourceUsageMonitor extends DummyResourceCalculatorPlugin {
        private TestResourceUsageEmulators.FakeCpuUsageEmulatorCore core;

        public FakeResourceUsageMonitor(TestResourceUsageEmulators.FakeCpuUsageEmulatorCore core) {
            this.core = core;
        }

        /**
         * A dummy CPU usage monitor. Every call to
         * {@link ResourceCalculatorPlugin#getCumulativeCpuTime()} will return the
         * value of {@link FakeCpuUsageEmulatorCore#getNumCalls()}.
         */
        @Override
        public long getCumulativeCpuTime() {
            return core.getCpuUsage();
        }
    }

    /**
     * A dummy {@link Progressive} implementation that allows users to set the
     * progress for testing. The {@link Progressive#getProgress()} call will
     * return the last progress value set using
     * {@link FakeProgressive#setProgress(float)}.
     */
    static class FakeProgressive implements Progressive {
        private float progress = 0.0F;

        @Override
        public float getProgress() {
            return progress;
        }

        void setProgress(float progress) {
            this.progress = progress;
        }
    }

    /**
     * A dummy reporter for {@link LoadJob.ResourceUsageMatcherRunner}.
     */
    private static class DummyReporter extends StatusReporter {
        private Progressive progress;

        DummyReporter(Progressive progress) {
            this.progress = progress;
        }

        @Override
        public Counter getCounter(Enum<?> name) {
            return null;
        }

        @Override
        public Counter getCounter(String group, String name) {
            return null;
        }

        @Override
        public void progress() {
        }

        @Override
        public float getProgress() {
            return progress.getProgress();
        }

        @Override
        public void setStatus(String status) {
        }
    }

    // Extends ResourceUsageMatcherRunner for testing.
    @SuppressWarnings("unchecked")
    private static class FakeResourceUsageMatcherRunner extends ResourceUsageMatcherRunner {
        FakeResourceUsageMatcherRunner(TaskInputOutputContext context, ResourceUsageMetrics metrics) {
            super(context, metrics);
        }

        // test ResourceUsageMatcherRunner
        void test() throws Exception {
            match();
        }
    }

    /**
     * Test {@link LoadJob.ResourceUsageMatcherRunner}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testResourceUsageMatcherRunner() throws Exception {
        Configuration conf = new Configuration();
        TestResourceUsageEmulators.FakeProgressive progress = new TestResourceUsageEmulators.FakeProgressive();
        // set the resource calculator plugin
        conf.setClass(TT_RESOURCE_CALCULATOR_PLUGIN, DummyResourceCalculatorPlugin.class, ResourceCalculatorPlugin.class);
        // set the resources
        // set the resource implementation class
        conf.setClass(RESOURCE_USAGE_EMULATION_PLUGINS, TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.class, ResourceUsageEmulatorPlugin.class);
        long currentTime = System.currentTimeMillis();
        // initialize the matcher class
        TaskAttemptID id = new TaskAttemptID("test", 1, TaskType.MAP, 1, 1);
        StatusReporter reporter = new TestResourceUsageEmulators.DummyReporter(progress);
        TaskInputOutputContext context = new org.apache.hadoop.mapreduce.task.MapContextImpl(conf, id, null, null, null, reporter, null);
        TestResourceUsageEmulators.FakeResourceUsageMatcherRunner matcher = new TestResourceUsageEmulators.FakeResourceUsageMatcherRunner(context, null);
        // check if the matcher initialized the plugin
        String identifier = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.DEFAULT_IDENTIFIER;
        long initTime = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testInitialization(identifier, conf);
        Assert.assertTrue(("ResourceUsageMatcherRunner failed to initialize the" + " configured plugin"), (initTime > currentTime));
        // check the progress
        Assert.assertEquals("Progress mismatch in ResourceUsageMatcherRunner", 0, progress.getProgress(), 0.0);
        // call match() and check progress
        progress.setProgress(0.01F);
        currentTime = System.currentTimeMillis();
        matcher.test();
        long emulateTime = TestResourceUsageEmulators.TestResourceUsageEmulatorPlugin.testEmulation(identifier, conf);
        Assert.assertTrue(("ProgressBasedResourceUsageMatcher failed to load and emulate" + " the configured plugin"), (emulateTime > currentTime));
    }

    /**
     * Test {@link CumulativeCpuUsageEmulatorPlugin}'s core CPU usage emulation
     * engine.
     */
    @Test
    public void testCpuUsageEmulator() throws IOException {
        // test CpuUsageEmulator calibration with fake resource calculator plugin
        long target = 100000L;// 100 secs

        int unitUsage = 50;
        TestResourceUsageEmulators.FakeCpuUsageEmulatorCore fakeCpuEmulator = new TestResourceUsageEmulators.FakeCpuUsageEmulatorCore();
        fakeCpuEmulator.setUnitUsage(unitUsage);
        TestResourceUsageEmulators.FakeResourceUsageMonitor fakeMonitor = new TestResourceUsageEmulators.FakeResourceUsageMonitor(fakeCpuEmulator);
        // calibrate for 100ms
        calibrate(fakeMonitor, target);
        // by default, CpuUsageEmulator.calibrate() will consume 100ms of CPU usage
        Assert.assertEquals("Fake calibration failed", 100, fakeMonitor.getCumulativeCpuTime());
        Assert.assertEquals("Fake calibration failed", 100, fakeCpuEmulator.getCpuUsage());
        // by default, CpuUsageEmulator.performUnitComputation() will be called
        // twice
        Assert.assertEquals("Fake calibration failed", 2, fakeCpuEmulator.getNumCalls());
    }

    /**
     * This is a dummy class that fakes CPU usage.
     */
    private static class FakeCpuUsageEmulatorCore extends DefaultCpuUsageEmulator {
        private int numCalls = 0;

        private int unitUsage = 1;

        private int cpuUsage = 0;

        @Override
        protected void performUnitComputation() {
            ++(numCalls);
            cpuUsage += unitUsage;
        }

        int getNumCalls() {
            return numCalls;
        }

        int getCpuUsage() {
            return cpuUsage;
        }

        void reset() {
            numCalls = 0;
            cpuUsage = 0;
        }

        void setUnitUsage(int unitUsage) {
            this.unitUsage = unitUsage;
        }
    }

    /**
     * Test {@link CumulativeCpuUsageEmulatorPlugin}.
     */
    @Test
    public void testCumulativeCpuUsageEmulatorPlugin() throws Exception {
        Configuration conf = new Configuration();
        long targetCpuUsage = 1000L;
        int unitCpuUsage = 50;
        // fake progress indicator
        TestResourceUsageEmulators.FakeProgressive fakeProgress = new TestResourceUsageEmulators.FakeProgressive();
        // fake cpu usage generator
        TestResourceUsageEmulators.FakeCpuUsageEmulatorCore fakeCore = new TestResourceUsageEmulators.FakeCpuUsageEmulatorCore();
        fakeCore.setUnitUsage(unitCpuUsage);
        // a cumulative cpu usage emulator with fake core
        CumulativeCpuUsageEmulatorPlugin cpuPlugin = new CumulativeCpuUsageEmulatorPlugin(fakeCore);
        // test with invalid or missing resource usage value
        ResourceUsageMetrics invalidUsage = TestResourceUsageEmulators.createMetrics(0);
        cpuPlugin.initialize(conf, invalidUsage, null, null);
        // test if disabled cpu emulation plugin's emulate() call is a no-operation
        // this will test if the emulation plugin is disabled or not
        int numCallsPre = fakeCore.getNumCalls();
        long cpuUsagePre = fakeCore.getCpuUsage();
        cpuPlugin.emulate();
        int numCallsPost = fakeCore.getNumCalls();
        long cpuUsagePost = fakeCore.getCpuUsage();
        // test if no calls are made cpu usage emulator core
        Assert.assertEquals("Disabled cumulative CPU usage emulation plugin works!", numCallsPre, numCallsPost);
        // test if no calls are made cpu usage emulator core
        Assert.assertEquals("Disabled cumulative CPU usage emulation plugin works!", cpuUsagePre, cpuUsagePost);
        // test with get progress
        float progress = cpuPlugin.getProgress();
        Assert.assertEquals(("Invalid progress of disabled cumulative CPU usage emulation " + "plugin!"), 1.0F, progress, 0.0F);
        // test with valid resource usage value
        ResourceUsageMetrics metrics = TestResourceUsageEmulators.createMetrics(targetCpuUsage);
        // fake monitor
        ResourceCalculatorPlugin monitor = new TestResourceUsageEmulators.FakeResourceUsageMonitor(fakeCore);
        // test with default emulation interval
        TestResourceUsageEmulators.testEmulationAccuracy(conf, fakeCore, monitor, metrics, cpuPlugin, targetCpuUsage, (targetCpuUsage / unitCpuUsage));
        // test with custom value for emulation interval of 20%
        conf.setFloat(CPU_EMULATION_PROGRESS_INTERVAL, 0.2F);
        TestResourceUsageEmulators.testEmulationAccuracy(conf, fakeCore, monitor, metrics, cpuPlugin, targetCpuUsage, (targetCpuUsage / unitCpuUsage));
        // test if emulation interval boundary is respected (unit usage = 1)
        // test the case where the current progress is less than threshold
        fakeProgress = new TestResourceUsageEmulators.FakeProgressive();// initialize

        fakeCore.reset();
        fakeCore.setUnitUsage(1);
        conf.setFloat(CPU_EMULATION_PROGRESS_INTERVAL, 0.25F);
        cpuPlugin.initialize(conf, metrics, monitor, fakeProgress);
        // take a snapshot after the initialization
        long initCpuUsage = monitor.getCumulativeCpuTime();
        long initNumCalls = fakeCore.getNumCalls();
        // test with 0 progress
        TestResourceUsageEmulators.testEmulationBoundary(0.0F, fakeCore, fakeProgress, cpuPlugin, initCpuUsage, initNumCalls, "[no-op, 0 progress]");
        // test with 24% progress
        TestResourceUsageEmulators.testEmulationBoundary(0.24F, fakeCore, fakeProgress, cpuPlugin, initCpuUsage, initNumCalls, "[no-op, 24% progress]");
        // test with 25% progress
        // target = 1000ms, target emulation at 25% = 250ms,
        // weighed target = 1000 * 0.25^4 (we are using progress^4 as the weight)
        // ~ 4
        // but current usage = init-usage = 100, hence expected = 100
        TestResourceUsageEmulators.testEmulationBoundary(0.25F, fakeCore, fakeProgress, cpuPlugin, initCpuUsage, initNumCalls, "[op, 25% progress]");
        // test with 80% progress
        // target = 1000ms, target emulation at 80% = 800ms,
        // weighed target = 1000 * 0.25^4 (we are using progress^4 as the weight)
        // ~ 410
        // current-usage = init-usage = 100, hence expected-usage = 410
        TestResourceUsageEmulators.testEmulationBoundary(0.8F, fakeCore, fakeProgress, cpuPlugin, 410, 410, "[op, 80% progress]");
        // now test if the final call with 100% progress ramps up the CPU usage
        TestResourceUsageEmulators.testEmulationBoundary(1.0F, fakeCore, fakeProgress, cpuPlugin, targetCpuUsage, targetCpuUsage, "[op, 100% progress]");
        // test if emulation interval boundary is respected (unit usage = 50)
        // test the case where the current progress is less than threshold
        fakeProgress = new TestResourceUsageEmulators.FakeProgressive();// initialize

        fakeCore.reset();
        fakeCore.setUnitUsage(unitCpuUsage);
        conf.setFloat(CPU_EMULATION_PROGRESS_INTERVAL, 0.4F);
        cpuPlugin.initialize(conf, metrics, monitor, fakeProgress);
        // take a snapshot after the initialization
        initCpuUsage = monitor.getCumulativeCpuTime();
        initNumCalls = fakeCore.getNumCalls();
        // test with 0 progress
        TestResourceUsageEmulators.testEmulationBoundary(0.0F, fakeCore, fakeProgress, cpuPlugin, initCpuUsage, initNumCalls, "[no-op, 0 progress]");
        // test with 39% progress
        TestResourceUsageEmulators.testEmulationBoundary(0.39F, fakeCore, fakeProgress, cpuPlugin, initCpuUsage, initNumCalls, "[no-op, 39% progress]");
        // test with 40% progress
        // target = 1000ms, target emulation at 40% = 4000ms,
        // weighed target = 1000 * 0.40^4 (we are using progress^4 as the weight)
        // ~ 26
        // current-usage = init-usage = 100, hence expected-usage = 100
        TestResourceUsageEmulators.testEmulationBoundary(0.4F, fakeCore, fakeProgress, cpuPlugin, initCpuUsage, initNumCalls, "[op, 40% progress]");
        // test with 90% progress
        // target = 1000ms, target emulation at 90% = 900ms,
        // weighed target = 1000 * 0.90^4 (we are using progress^4 as the weight)
        // ~ 657
        // current-usage = init-usage = 100, hence expected-usage = 657 but
        // the fake-core increases in steps of 50, hence final target = 700
        TestResourceUsageEmulators.testEmulationBoundary(0.9F, fakeCore, fakeProgress, cpuPlugin, 700, (700 / unitCpuUsage), "[op, 90% progress]");
        // now test if the final call with 100% progress ramps up the CPU usage
        TestResourceUsageEmulators.testEmulationBoundary(1.0F, fakeCore, fakeProgress, cpuPlugin, targetCpuUsage, (targetCpuUsage / unitCpuUsage), "[op, 100% progress]");
    }
}

