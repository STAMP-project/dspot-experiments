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
import Config.TOPOLOGY_GROUPS;
import Config.TOPOLOGY_SUBMITTER_USER;
import Config.TOPOLOGY_USERS;
import DaemonConfig.LOGS_GROUPS;
import DaemonConfig.LOGS_USERS;
import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.storm.container.ResourceIsolationInterface;
import org.apache.storm.daemon.supervisor.Container.ContainerType;
import org.apache.storm.generated.LocalAssignment;
import org.apache.storm.generated.ProfileRequest;
import org.apache.storm.metric.StormMetricsRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.yaml.snakeyaml.Yaml;


public class ContainerTest {
    private static final Joiner PATH_JOIN = Joiner.on(File.separator).skipNulls();

    private static final String DOUBLE_SEP = (File.separator) + (File.separator);

    @Test
    public void testKill() throws Exception {
        final String topoId = "test_topology";
        final Map<String, Object> superConf = new HashMap<>();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        ContainerTest.MockContainer mc = new ContainerTest.MockContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", 6628, 8080, la, null, "worker", new HashMap(), ops, new StormMetricsRegistry());
        mc.kill();
        Assert.assertEquals(Collections.EMPTY_LIST, mc.killedPids);
        Assert.assertEquals(Collections.EMPTY_LIST, mc.forceKilledPids);
        mc.forceKill();
        Assert.assertEquals(Collections.EMPTY_LIST, mc.killedPids);
        Assert.assertEquals(Collections.EMPTY_LIST, mc.forceKilledPids);
        long pid = 987654321;
        mc.allPids.add(pid);
        mc.kill();
        Assert.assertEquals(mc.allPids, new HashSet<>(mc.killedPids));
        Assert.assertEquals(Collections.EMPTY_LIST, mc.forceKilledPids);
        mc.killedPids.clear();
        mc.forceKill();
        Assert.assertEquals(Collections.EMPTY_LIST, mc.killedPids);
        Assert.assertEquals(mc.allPids, new HashSet<>(mc.forceKilledPids));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetup() throws Exception {
        final int port = 8080;
        final String topoId = "test_topology";
        final String workerId = "worker_id";
        final String user = "me";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "testing");
        final File workerArtifacts = ContainerTest.asAbsFile(stormLocal, topoId, String.valueOf(port));
        final File logMetadataFile = new File(workerArtifacts, "worker.yaml");
        final File workerUserFile = ContainerTest.asAbsFile(stormLocal, "workers-users", workerId);
        final File workerRoot = ContainerTest.asAbsFile(stormLocal, "workers", workerId);
        final File distRoot = ContainerTest.asAbsFile(stormLocal, "supervisor", "stormdist", topoId);
        final Map<String, Object> topoConf = new HashMap<>();
        final List<String> topoUsers = Arrays.asList("t-user-a", "t-user-b");
        final List<String> logUsers = Arrays.asList("l-user-a", "l-user-b");
        final List<String> topoGroups = Arrays.asList("t-group-a", "t-group-b");
        final List<String> logGroups = Arrays.asList("l-group-a", "l-group-b");
        topoConf.put(LOGS_GROUPS, logGroups);
        topoConf.put(TOPOLOGY_GROUPS, topoGroups);
        topoConf.put(LOGS_USERS, logUsers);
        topoConf.put(TOPOLOGY_USERS, topoUsers);
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(STORM_LOCAL_DIR, stormLocal);
        superConf.put(STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        final StringWriter yamlDump = new StringWriter();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Mockito.when(ops.fileExists(workerArtifacts)).thenReturn(true);
        Mockito.when(ops.fileExists(workerRoot)).thenReturn(true);
        Mockito.when(ops.getWriter(logMetadataFile)).thenReturn(yamlDump);
        LocalAssignment la = new LocalAssignment();
        la.set_topology_id(topoId);
        la.set_owner(user);
        ContainerTest.MockContainer mc = new ContainerTest.MockContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", 6628, 8080, la, null, workerId, topoConf, ops, new StormMetricsRegistry());
        setup();
        // Initial Setup
        Mockito.verify(ops).forceMkdir(new File(workerRoot, "pids"));
        Mockito.verify(ops).forceMkdir(new File(workerRoot, "tmp"));
        Mockito.verify(ops).forceMkdir(new File(workerRoot, "heartbeats"));
        Mockito.verify(ops).fileExists(workerArtifacts);
        // Log file permissions
        Mockito.verify(ops).getWriter(logMetadataFile);
        String yamlResult = yamlDump.toString();
        Yaml yaml = new Yaml();
        Map<String, Object> result = ((Map<String, Object>) (yaml.load(yamlResult)));
        Assert.assertEquals(workerId, result.get("worker-id"));
        Assert.assertEquals(user, result.get(TOPOLOGY_SUBMITTER_USER));
        HashSet<String> allowedUsers = new HashSet<>(topoUsers);
        allowedUsers.addAll(logUsers);
        Assert.assertEquals(allowedUsers, new HashSet<String>(((List<String>) (result.get(LOGS_USERS)))));
        HashSet<String> allowedGroups = new HashSet<>(topoGroups);
        allowedGroups.addAll(logGroups);
        Assert.assertEquals(allowedGroups, new HashSet<String>(((List<String>) (result.get(LOGS_GROUPS)))));
        // Save the current user to help with recovery
        Mockito.verify(ops).dump(workerUserFile, user);
        // Create links to artifacts dir
        Mockito.verify(ops).createSymlink(new File(workerRoot, "artifacts"), workerArtifacts);
        // Create links to blobs
        Mockito.verify(ops, Mockito.never()).createSymlink(new File(workerRoot, "resources"), new File(distRoot, "resources"));
    }

    @Test
    public void testCleanup() throws Exception {
        final int supervisorPort = 6628;
        final int port = 8080;
        final long pid = 100;
        final String topoId = "test_topology";
        final String workerId = "worker_id";
        final String user = "me";
        final String stormLocal = ContainerTest.asAbsPath("tmp", "testing");
        final File workerArtifacts = ContainerTest.asAbsFile(stormLocal, topoId, String.valueOf(port));
        final File logMetadataFile = new File(workerArtifacts, "worker.yaml");
        final File workerUserFile = ContainerTest.asAbsFile(stormLocal, "workers-users", workerId);
        final File workerRoot = ContainerTest.asAbsFile(stormLocal, "workers", workerId);
        final File workerPidsRoot = new File(workerRoot, "pids");
        final Map<String, Object> topoConf = new HashMap<>();
        final Map<String, Object> superConf = new HashMap<>();
        superConf.put(STORM_LOCAL_DIR, stormLocal);
        superConf.put(STORM_WORKERS_ARTIFACTS_DIR, stormLocal);
        final StringWriter yamlDump = new StringWriter();
        AdvancedFSOps ops = Mockito.mock(AdvancedFSOps.class);
        Mockito.when(ops.doRequiredTopoFilesExist(superConf, topoId)).thenReturn(true);
        Mockito.when(ops.fileExists(workerArtifacts)).thenReturn(true);
        Mockito.when(ops.fileExists(workerRoot)).thenReturn(true);
        Mockito.when(ops.getWriter(logMetadataFile)).thenReturn(yamlDump);
        ResourceIsolationInterface iso = Mockito.mock(ResourceIsolationInterface.class);
        LocalAssignment la = new LocalAssignment();
        la.set_owner(user);
        la.set_topology_id(topoId);
        ContainerTest.MockContainer mc = new ContainerTest.MockContainer(ContainerType.LAUNCH, superConf, "SUPERVISOR", supervisorPort, port, la, iso, workerId, topoConf, ops, new StormMetricsRegistry());
        mc.allPids.add(pid);
        cleanUp();
        Mockito.verify(ops).deleteIfExists(ArgumentMatchers.eq(new File(workerPidsRoot, String.valueOf(pid))), ArgumentMatchers.eq(user), ArgumentMatchers.any(String.class));
        Mockito.verify(iso).releaseResourcesForWorker(workerId);
        Mockito.verify(ops).deleteIfExists(ArgumentMatchers.eq(new File(workerRoot, "pids")), ArgumentMatchers.eq(user), ArgumentMatchers.any(String.class));
        Mockito.verify(ops).deleteIfExists(ArgumentMatchers.eq(new File(workerRoot, "tmp")), ArgumentMatchers.eq(user), ArgumentMatchers.any(String.class));
        Mockito.verify(ops).deleteIfExists(ArgumentMatchers.eq(new File(workerRoot, "heartbeats")), ArgumentMatchers.eq(user), ArgumentMatchers.any(String.class));
        Mockito.verify(ops).deleteIfExists(ArgumentMatchers.eq(workerRoot), ArgumentMatchers.eq(user), ArgumentMatchers.any(String.class));
        Mockito.verify(ops).deleteIfExists(workerUserFile);
    }

    public static class MockContainer extends Container {
        public final List<Long> killedPids = new ArrayList<>();

        public final List<Long> forceKilledPids = new ArrayList<>();

        public final Set<Long> allPids = new HashSet<>();

        protected MockContainer(ContainerType type, Map<String, Object> conf, String supervisorId, int supervisorPort, int port, LocalAssignment assignment, ResourceIsolationInterface resourceIsolationManager, String workerId, Map<String, Object> topoConf, AdvancedFSOps ops, StormMetricsRegistry metricsRegistry) throws IOException {
            super(type, conf, supervisorId, supervisorPort, port, assignment, resourceIsolationManager, workerId, topoConf, ops, metricsRegistry, new ContainerMemoryTracker(new StormMetricsRegistry()));
        }

        @Override
        protected void kill(long pid) {
            killedPids.add(pid);
        }

        @Override
        protected void forceKill(long pid) {
            forceKilledPids.add(pid);
        }

        @Override
        protected Set<Long> getAllPids() throws IOException {
            return allPids;
        }

        @Override
        public void launch() throws IOException {
            Assert.fail("THIS IS NOT UNDER TEST");
        }

        @Override
        public void relaunch() throws IOException {
            Assert.fail("THIS IS NOT UNDER TEST");
        }

        @Override
        public boolean didMainProcessExit() {
            Assert.fail("THIS IS NOT UNDER TEST");
            return false;
        }

        @Override
        public boolean runProfiling(ProfileRequest request, boolean stop) throws IOException, InterruptedException {
            Assert.fail("THIS IS NOT UNDER TEST");
            return false;
        }
    }
}

