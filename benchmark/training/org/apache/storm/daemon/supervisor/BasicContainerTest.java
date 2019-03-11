/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.daemon.supervisor;


import Config.STORM_LOCAL_DIR;
import Config.STORM_WORKERS_ARTIFACTS_DIR;
import Config.WORKER_CHILDOPTS;
import ConfigUtils.STORM_HOME;
import DaemonConfig.STORM_LOG4J2_CONF_DIR;
import ProfileAction.JMAP_DUMP;
import ProfileAction.JPROFILE_DUMP;
import ProfileAction.JPROFILE_STOP;
import ProfileAction.JSTACK_DUMP;
import ProfileAction.JVM_RESTART;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.storm.container.ResourceIsolationInterface;
import org.apache.storm.daemon.supervisor.Container.ContainerType;
import org.apache.storm.generated.LocalAssignment;
import org.apache.storm.generated.ProfileRequest;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.metric.StormMetricsRegistry;
import org.apache.storm.utils.LocalState;
import org.apache.storm.utils.SimpleVersion;
import org.apache.storm.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class BasicContainerTest {
    @Test
    public void testCreateNewWorkerId() throws Exception {
        final String topoId = "test_topology";
        final int supervisorPort = 6628;
        final int port = 8080;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        Map<String, Object> superConf = new HashMap<>();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        LocalState ls = Mockito.mock(LocalState.class);
        BasicContainerTest.MockBasicContainer mc = new BasicContainerTest.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, null, new StormMetricsRegistry(), new HashMap(), ops, "profile");
        // null worker id means generate one...
        Assert.assertNotNull(mc._workerId);
        Mockito.verify(ls).getApprovedWorkers();
        Map<String, Integer> expectedNewState = new HashMap<String, Integer>();
        expectedNewState.put(mc._workerId, port);
        Mockito.verify(ls).setApprovedWorkers(expectedNewState);
    }

    @Test
    public void testRecovery() throws Exception {
        final String topoId = "test_topology";
        final String workerId = "myWorker";
        final int supervisorPort = 6628;
        final int port = 8080;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        Map<String, Integer> workerState = new HashMap<String, Integer>();
        workerState.put(workerId, port);
        LocalState ls = Mockito.mock(LocalState.class);
        Mockito.when(ls.getApprovedWorkers()).thenReturn(workerState);
        Map<String, Object> superConf = new HashMap<>();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        BasicContainerTest.MockBasicContainer mc = new BasicContainerTest.MockBasicContainer(ContainerType.RECOVER_FULL, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, null, new StormMetricsRegistry(), new HashMap(), ops, "profile");
        Assert.assertEquals(workerId, mc._workerId);
    }

    @Test
    public void testRecoveryMiss() throws Exception {
        final String topoId = "test_topology";
        final int supervisorPort = 6628;
        final int port = 8080;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        Map<String, Integer> workerState = new HashMap<String, Integer>();
        workerState.put("somethingelse", (port + 1));
        LocalState ls = Mockito.mock(LocalState.class);
        Mockito.when(ls.getApprovedWorkers()).thenReturn(workerState);
        try {
            new BasicContainerTest.MockBasicContainer(ContainerType.RECOVER_FULL, new HashMap<String, Object>(), "SUPERVISOR", supervisorPort, port, la, null, ls, null, new StormMetricsRegistry(), new HashMap(), null, "profile");
            Assert.fail("Container recovered worker incorrectly");
        } catch (ContainerRecoveryException e) {
            // Expected
        }
    }

    @Test
    public void testCleanUp() throws Exception {
        final String topoId = "test_topology";
        final int supervisorPort = 6628;
        final int port = 8080;
        final String workerId = "worker-id";
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        Map<String, Object> superConf = new HashMap<>();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Map<String, Integer> workerState = new HashMap<String, Integer>();
        workerState.put(workerId, port);
        LocalState ls = Mockito.mock(LocalState.class);
        Mockito.when(ls.getApprovedWorkers()).thenReturn(new HashMap(workerState));
        BasicContainerTest.MockBasicContainer mc = new BasicContainerTest.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, workerId, new StormMetricsRegistry(), new HashMap(), ops, "profile");
        cleanUp();
        Assert.assertNull(mc._workerId);
        Mockito.verify(ls).getApprovedWorkers();
        Map<String, Integer> expectedNewState = new HashMap<String, Integer>();
        Mockito.verify(ls).setApprovedWorkers(expectedNewState);
    }

    @Test
    public void testRunProfiling() throws Exception {
        final long pid = 100;
        final String topoId = "test_topology";
        final int supervisorPort = 6628;
        final int port = 8080;
        final String workerId = "worker-id";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "testing");
        final String topoRoot = ContainerTest.asAbsPath(stormLocal, topoId, String.valueOf(port));
        final File workerArtifactsPid = ContainerTest.asAbsFile(topoRoot, "worker.pid");
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(STORM_LOCAL_DIR, stormLocal);
        superConf.put(STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Mockito.when(ops.slurpString(workerArtifactsPid)).thenReturn(String.valueOf(pid));
        LocalState ls = Mockito.mock(LocalState.class);
        BasicContainerTest.MockBasicContainer mc = new BasicContainerTest.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, workerId, new StormMetricsRegistry(), new HashMap(), ops, "profile");
        // HEAP DUMP
        ProfileRequest req = new ProfileRequest();
        req.set_action(JMAP_DUMP);
        mc.runProfiling(req, false);
        Assert.assertEquals(1, mc.profileCmds.size());
        BasicContainerTest.CommandRun cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        Assert.assertEquals(Arrays.asList("profile", String.valueOf(pid), "jmap", topoRoot), cmd.cmd);
        Assert.assertEquals(new File(topoRoot), cmd.pwd);
        // JSTACK DUMP
        req.set_action(JSTACK_DUMP);
        mc.runProfiling(req, false);
        Assert.assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        Assert.assertEquals(Arrays.asList("profile", String.valueOf(pid), "jstack", topoRoot), cmd.cmd);
        Assert.assertEquals(new File(topoRoot), cmd.pwd);
        // RESTART
        req.set_action(JVM_RESTART);
        mc.runProfiling(req, false);
        Assert.assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        Assert.assertEquals(Arrays.asList("profile", String.valueOf(pid), "kill"), cmd.cmd);
        Assert.assertEquals(new File(topoRoot), cmd.pwd);
        // JPROFILE DUMP
        req.set_action(JPROFILE_DUMP);
        mc.runProfiling(req, false);
        Assert.assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        Assert.assertEquals(Arrays.asList("profile", String.valueOf(pid), "dump", topoRoot), cmd.cmd);
        Assert.assertEquals(new File(topoRoot), cmd.pwd);
        // JPROFILE START
        req.set_action(JPROFILE_STOP);
        mc.runProfiling(req, false);
        Assert.assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        Assert.assertEquals(Arrays.asList("profile", String.valueOf(pid), "start"), cmd.cmd);
        Assert.assertEquals(new File(topoRoot), cmd.pwd);
        // JPROFILE STOP
        req.set_action(JPROFILE_STOP);
        mc.runProfiling(req, true);
        Assert.assertEquals(1, mc.profileCmds.size());
        cmd = mc.profileCmds.get(0);
        mc.profileCmds.clear();
        Assert.assertEquals(Arrays.asList("profile", String.valueOf(pid), "stop", topoRoot), cmd.cmd);
        Assert.assertEquals(new File(topoRoot), cmd.pwd);
    }

    @Test
    public void testLaunch() throws Exception {
        final String topoId = "test_topology_current";
        final int supervisorPort = 6628;
        final int port = 8080;
        final String stormHome = ContainerTest.asAbsPath("tmp", "storm-home");
        final String stormLogDir = ContainerTest.asFile(".", "target").getCanonicalPath();
        final String workerId = "worker-id";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "storm-local");
        final String distRoot = ContainerTest.asAbsPath(stormLocal, "supervisor", "stormdist", topoId);
        final File stormcode = new File(distRoot, "stormcode.ser");
        final File stormjar = new File(distRoot, "stormjar.jar");
        final String log4jdir = ContainerTest.asAbsPath(stormHome, "conf");
        final String workerConf = ContainerTest.asAbsPath(log4jdir, "worker.xml");
        final String workerRoot = ContainerTest.asAbsPath(stormLocal, "workers", workerId);
        final String workerTmpDir = ContainerTest.asAbsPath(workerRoot, "tmp");
        final StormTopology st = new StormTopology();
        st.set_spouts(new HashMap());
        st.set_bolts(new HashMap());
        st.set_state_spouts(new HashMap());
        byte[] serializedState = Utils.gzip(Utils.thriftSerialize(st));
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(STORM_LOCAL_DIR, stormLocal);
        superConf.put(STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        superConf.put(STORM_LOG4J2_CONF_DIR, log4jdir);
        superConf.put(WORKER_CHILDOPTS, " -Dtesting=true");
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Mockito.when(ops.slurp(stormcode)).thenReturn(serializedState);
        LocalState ls = Mockito.mock(LocalState.class);
        BasicContainerTest.checkpoint(() -> {
            org.apache.storm.daemon.supervisor.MockBasicContainer mc = new org.apache.storm.daemon.supervisor.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, workerId, new StormMetricsRegistry(), new HashMap<>(), ops, "profile");
            mc.launch();
            assertEquals(1, mc.workerCmds.size());
            org.apache.storm.daemon.supervisor.CommandRun cmd = mc.workerCmds.get(0);
            mc.workerCmds.clear();
            assertListEquals(Arrays.asList("java", "-cp", ("FRAMEWORK_CP:" + (stormjar.getAbsolutePath())), "-Dlogging.sensitivity=S3", "-Dlogfile.name=worker.log", ("-Dstorm.home=" + stormHome), ("-Dworkers.artifacts=" + stormLocal), ("-Dstorm.id=" + topoId), ("-Dworker.id=" + workerId), ("-Dworker.port=" + port), ("-Dstorm.log.dir=" + stormLogDir), "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector", ("-Dstorm.local.dir=" + stormLocal), "-Dworker.memory_limit_mb=768", ("-Dlog4j.configurationFile=" + workerConf), "org.apache.storm.LogWriter", "java", "-server", "-Dlogging.sensitivity=S3", "-Dlogfile.name=worker.log", ("-Dstorm.home=" + stormHome), ("-Dworkers.artifacts=" + stormLocal), ("-Dstorm.id=" + topoId), ("-Dworker.id=" + workerId), ("-Dworker.port=" + port), ("-Dstorm.log.dir=" + stormLogDir), "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector", ("-Dstorm.local.dir=" + stormLocal), "-Dworker.memory_limit_mb=768", ("-Dlog4j.configurationFile=" + workerConf), "-Dtesting=true", "-Djava.library.path=JLP", "-Dstorm.conf.file=", "-Dstorm.options=", ("-Djava.io.tmpdir=" + workerTmpDir), "-cp", ("FRAMEWORK_CP:" + (stormjar.getAbsolutePath())), "org.apache.storm.daemon.worker.Worker", topoId, "SUPERVISOR", String.valueOf(supervisorPort), String.valueOf(port), workerId), cmd.cmd);
            assertEquals(new File(workerRoot), cmd.pwd);
        }, STORM_HOME, stormHome, "storm.log.dir", stormLogDir);
    }

    @Test
    public void testLaunchStorm1version() throws Exception {
        final String topoId = "test_topology_storm_1.x";
        final int supervisorPort = 6628;
        final int port = 8080;
        final String stormHome = ContainerTest.asAbsPath("tmp", "storm-home");
        final String stormLogDir = ContainerTest.asFile(".", "target").getCanonicalPath();
        final String workerId = "worker-id";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "storm-local");
        final String distRoot = ContainerTest.asAbsPath(stormLocal, "supervisor", "stormdist", topoId);
        final File stormcode = new File(distRoot, "stormcode.ser");
        final File stormjar = new File(distRoot, "stormjar.jar");
        final String log4jdir = ContainerTest.asAbsPath(stormHome, "conf");
        final String workerConf = ContainerTest.asAbsPath(log4jdir, "worker.xml");
        final String workerRoot = ContainerTest.asAbsPath(stormLocal, "workers", workerId);
        final String workerTmpDir = ContainerTest.asAbsPath(workerRoot, "tmp");
        final StormTopology st = new StormTopology();
        st.set_spouts(new HashMap());
        st.set_bolts(new HashMap());
        st.set_state_spouts(new HashMap());
        // minimum 1.x version of supporting STORM-2448 would be 1.0.4
        st.set_storm_version("1.0.4");
        byte[] serializedState = Utils.gzip(Utils.thriftSerialize(st));
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(STORM_LOCAL_DIR, stormLocal);
        superConf.put(STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        superConf.put(STORM_LOG4J2_CONF_DIR, log4jdir);
        superConf.put(WORKER_CHILDOPTS, " -Dtesting=true");
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Mockito.when(ops.slurp(stormcode)).thenReturn(serializedState);
        LocalState ls = Mockito.mock(LocalState.class);
        BasicContainerTest.checkpoint(() -> {
            org.apache.storm.daemon.supervisor.MockBasicContainer mc = new org.apache.storm.daemon.supervisor.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, workerId, new StormMetricsRegistry(), new HashMap<>(), ops, "profile");
            mc.launch();
            assertEquals(1, mc.workerCmds.size());
            org.apache.storm.daemon.supervisor.CommandRun cmd = mc.workerCmds.get(0);
            mc.workerCmds.clear();
            assertListEquals(Arrays.asList("java", "-cp", ("FRAMEWORK_CP:" + (stormjar.getAbsolutePath())), "-Dlogging.sensitivity=S3", "-Dlogfile.name=worker.log", ("-Dstorm.home=" + stormHome), ("-Dworkers.artifacts=" + stormLocal), ("-Dstorm.id=" + topoId), ("-Dworker.id=" + workerId), ("-Dworker.port=" + port), ("-Dstorm.log.dir=" + stormLogDir), "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector", ("-Dstorm.local.dir=" + stormLocal), "-Dworker.memory_limit_mb=768", ("-Dlog4j.configurationFile=" + workerConf), "org.apache.storm.LogWriter", "java", "-server", "-Dlogging.sensitivity=S3", "-Dlogfile.name=worker.log", ("-Dstorm.home=" + stormHome), ("-Dworkers.artifacts=" + stormLocal), ("-Dstorm.id=" + topoId), ("-Dworker.id=" + workerId), ("-Dworker.port=" + port), ("-Dstorm.log.dir=" + stormLogDir), "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector", ("-Dstorm.local.dir=" + stormLocal), "-Dworker.memory_limit_mb=768", ("-Dlog4j.configurationFile=" + workerConf), "-Dtesting=true", "-Djava.library.path=JLP", "-Dstorm.conf.file=", "-Dstorm.options=", ("-Djava.io.tmpdir=" + workerTmpDir), "-cp", ("FRAMEWORK_CP:" + (stormjar.getAbsolutePath())), "org.apache.storm.daemon.worker", topoId, "SUPERVISOR", String.valueOf(port), workerId), cmd.cmd);
            assertEquals(new File(workerRoot), cmd.pwd);
        }, STORM_HOME, stormHome, "storm.log.dir", stormLogDir);
    }

    @Test
    public void testLaunchStorm0version() throws Exception {
        final String topoId = "test_topology_storm_0.x";
        final int supervisorPort = 6628;
        final int port = 8080;
        final String stormHome = ContainerTest.asAbsPath("tmp", "storm-home");
        final String stormLogDir = ContainerTest.asFile(".", "target").getCanonicalPath();
        final String workerId = "worker-id";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "storm-local");
        final String distRoot = ContainerTest.asAbsPath(stormLocal, "supervisor", "stormdist", topoId);
        final File stormcode = new File(distRoot, "stormcode.ser");
        final File stormjar = new File(distRoot, "stormjar.jar");
        final String log4jdir = ContainerTest.asAbsPath(stormHome, "conf");
        final String workerConf = ContainerTest.asAbsPath(log4jdir, "worker.xml");
        final String workerRoot = ContainerTest.asAbsPath(stormLocal, "workers", workerId);
        final String workerTmpDir = ContainerTest.asAbsPath(workerRoot, "tmp");
        final StormTopology st = new StormTopology();
        st.set_spouts(new HashMap());
        st.set_bolts(new HashMap());
        st.set_state_spouts(new HashMap());
        // minimum 0.x version of supporting STORM-2448 would be 0.10.3
        st.set_storm_version("0.10.3");
        byte[] serializedState = Utils.gzip(Utils.thriftSerialize(st));
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(STORM_LOCAL_DIR, stormLocal);
        superConf.put(STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        superConf.put(STORM_LOG4J2_CONF_DIR, log4jdir);
        superConf.put(WORKER_CHILDOPTS, " -Dtesting=true");
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Mockito.when(ops.slurp(stormcode)).thenReturn(serializedState);
        LocalState ls = Mockito.mock(LocalState.class);
        BasicContainerTest.checkpoint(() -> {
            org.apache.storm.daemon.supervisor.MockBasicContainer mc = new org.apache.storm.daemon.supervisor.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, workerId, new StormMetricsRegistry(), new HashMap<>(), ops, "profile");
            mc.launch();
            assertEquals(1, mc.workerCmds.size());
            org.apache.storm.daemon.supervisor.CommandRun cmd = mc.workerCmds.get(0);
            mc.workerCmds.clear();
            assertListEquals(Arrays.asList("java", "-cp", ("FRAMEWORK_CP:" + (stormjar.getAbsolutePath())), "-Dlogging.sensitivity=S3", "-Dlogfile.name=worker.log", ("-Dstorm.home=" + stormHome), ("-Dworkers.artifacts=" + stormLocal), ("-Dstorm.id=" + topoId), ("-Dworker.id=" + workerId), ("-Dworker.port=" + port), ("-Dstorm.log.dir=" + stormLogDir), "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector", ("-Dstorm.local.dir=" + stormLocal), "-Dworker.memory_limit_mb=768", ("-Dlog4j.configurationFile=" + workerConf), "backtype.storm.LogWriter", "java", "-server", "-Dlogging.sensitivity=S3", "-Dlogfile.name=worker.log", ("-Dstorm.home=" + stormHome), ("-Dworkers.artifacts=" + stormLocal), ("-Dstorm.id=" + topoId), ("-Dworker.id=" + workerId), ("-Dworker.port=" + port), ("-Dstorm.log.dir=" + stormLogDir), "-DLog4jContextSelector=org.apache.logging.log4j.core.selector.BasicContextSelector", ("-Dstorm.local.dir=" + stormLocal), "-Dworker.memory_limit_mb=768", ("-Dlog4j.configurationFile=" + workerConf), "-Dtesting=true", "-Djava.library.path=JLP", "-Dstorm.conf.file=", "-Dstorm.options=", ("-Djava.io.tmpdir=" + workerTmpDir), "-cp", ("FRAMEWORK_CP:" + (stormjar.getAbsolutePath())), "backtype.storm.daemon.worker", topoId, "SUPERVISOR", String.valueOf(port), workerId), cmd.cmd);
            assertEquals(new File(workerRoot), cmd.pwd);
        }, STORM_HOME, stormHome, "storm.log.dir", stormLogDir);
    }

    @Test
    public void testSubstChildOpts() throws Exception {
        String workerId = "w-01";
        String topoId = "s-01";
        int supervisorPort = 6628;
        int port = 9999;
        int memOnheap = 512;
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        Map<String, Object> superConf = new HashMap<>();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        LocalState ls = Mockito.mock(LocalState.class);
        BasicContainerTest.MockBasicContainer mc = new BasicContainerTest.MockBasicContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, null, ls, workerId, new StormMetricsRegistry(), new HashMap(), ops, "profile");
        BasicContainerTest.assertListEquals(Arrays.asList("-Xloggc:/tmp/storm/logs/gc.worker-9999-s-01-w-01-9999.log", "-Xms256m", "-Xmx512m"), mc.substituteChildopts("-Xloggc:/tmp/storm/logs/gc.worker-%ID%-%TOPOLOGY-ID%-%WORKER-ID%-%WORKER-PORT%.log -Xms256m -Xmx%HEAP-MEM%m", memOnheap));
        BasicContainerTest.assertListEquals(Arrays.asList("-Xloggc:/tmp/storm/logs/gc.worker-9999-s-01-w-01-9999.log", "-Xms256m", "-Xmx512m"), mc.substituteChildopts(Arrays.asList("-Xloggc:/tmp/storm/logs/gc.worker-%ID%-%TOPOLOGY-ID%-%WORKER-ID%-%WORKER-PORT%.log", "-Xms256m", "-Xmx%HEAP-MEM%m"), memOnheap));
        BasicContainerTest.assertListEquals(Collections.emptyList(), mc.substituteChildopts(null));
    }

    private static interface Run {
        public void run() throws Exception;
    }

    public static class CommandRun {
        final List<String> cmd;

        final Map<String, String> env;

        final File pwd;

        public CommandRun(List<String> cmd, Map<String, String> env, File pwd) {
            this.cmd = cmd;
            this.env = env;
            this.pwd = pwd;
        }
    }

    public static class MockBasicContainer extends BasicContainer {
        public final List<BasicContainerTest.CommandRun> profileCmds = new ArrayList<>();

        public final List<BasicContainerTest.CommandRun> workerCmds = new ArrayList<>();

        public MockBasicContainer(ContainerType type, Map<String, Object> conf, String supervisorId, int supervisorPort, int port, LocalAssignment assignment, ResourceIsolationInterface resourceIsolationManager, LocalState localState, String workerId, StormMetricsRegistry metricsRegistry, Map<String, Object> topoConf, AdvancedFSOps ops, String profileCmd) throws IOException {
            super(type, conf, supervisorId, supervisorPort, port, assignment, resourceIsolationManager, localState, workerId, metricsRegistry, new ContainerMemoryTracker(metricsRegistry), topoConf, ops, profileCmd);
        }

        @Override
        protected Map<String, Object> readTopoConf() throws IOException {
            return new HashMap<>();
        }

        @Override
        public void createNewWorkerId() {
            super.createNewWorkerId();
        }

        @Override
        public List<String> substituteChildopts(Object value, int memOnheap) {
            return super.substituteChildopts(value, memOnheap);
        }

        @Override
        protected boolean runProfilingCommand(List<String> command, Map<String, String> env, String logPrefix, File targetDir) throws IOException, InterruptedException {
            profileCmds.add(new BasicContainerTest.CommandRun(command, env, targetDir));
            return true;
        }

        @Override
        protected void launchWorkerProcess(List<String> command, Map<String, String> env, String logPrefix, ExitCodeCallback processExitCallback, File targetDir) throws IOException {
            workerCmds.add(new BasicContainerTest.CommandRun(command, env, targetDir));
        }

        @Override
        protected String javaCmd(String cmd) {
            // avoid system dependent things
            return cmd;
        }

        @Override
        protected List<String> frameworkClasspath(SimpleVersion version) {
            // We are not really running anything so make this
            // simple to check for
            return Arrays.asList("FRAMEWORK_CP");
        }

        @Override
        protected String javaLibraryPath(String stormRoot, Map<String, Object> conf) {
            return "JLP";
        }
    }
}

