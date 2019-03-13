/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.application;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.sonar.application.command.AbstractCommand;
import org.sonar.application.command.CommandFactory;
import org.sonar.application.command.EsScriptCommand;
import org.sonar.application.command.JavaCommand;
import org.sonar.application.config.TestAppSettings;
import org.sonar.application.process.ProcessLauncher;
import org.sonar.application.process.ProcessMonitor;
import org.sonar.process.ProcessId;
import org.sonar.process.cluster.hz.HazelcastMember;


public class SchedulerImplTest {
    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private EsScriptCommand esScriptCommand;

    private JavaCommand webLeaderCommand;

    private JavaCommand webFollowerCommand;

    private JavaCommand ceCommand;

    private AppReloader appReloader = Mockito.mock(AppReloader.class);

    private TestAppSettings settings = new TestAppSettings();

    private SchedulerImplTest.TestCommandFactory javaCommandFactory = new SchedulerImplTest.TestCommandFactory();

    private SchedulerImplTest.TestProcessLauncher processLauncher = new SchedulerImplTest.TestProcessLauncher();

    private TestAppState appState = new TestAppState();

    private HazelcastMember hazelcastMember = Mockito.mock(HazelcastMember.class);

    private TestClusterAppState clusterAppState = new TestClusterAppState(hazelcastMember);

    private List<ProcessId> orderedStops = Collections.synchronizedList(new ArrayList<>());

    @Test
    public void start_and_stop_sequence_of_ES_WEB_CE_in_order() throws Exception {
        SchedulerImpl underTest = newScheduler(false);
        underTest.schedule();
        // elasticsearch does not have preconditions to start
        SchedulerImplTest.TestProcess es = processLauncher.waitForProcess(ELASTICSEARCH);
        assertThat(es.isAlive()).isTrue();
        assertThat(processLauncher.processes).hasSize(1);
        // elasticsearch becomes operational -> web leader is starting
        es.operational = true;
        SchedulerImplTest.waitForAppStateOperational(appState, ELASTICSEARCH);
        SchedulerImplTest.TestProcess web = processLauncher.waitForProcess(WEB_SERVER);
        assertThat(web.isAlive()).isTrue();
        assertThat(processLauncher.processes).hasSize(2);
        assertThat(processLauncher.commands).containsExactly(esScriptCommand, webLeaderCommand);
        // web becomes operational -> CE is starting
        web.operational = true;
        SchedulerImplTest.waitForAppStateOperational(appState, WEB_SERVER);
        SchedulerImplTest.TestProcess ce = processLauncher.waitForProcess(COMPUTE_ENGINE);
        assertThat(ce.isAlive()).isTrue();
        assertThat(processLauncher.processes).hasSize(3);
        assertThat(processLauncher.commands).containsExactly(esScriptCommand, webLeaderCommand, ceCommand);
        // all processes are up
        processLauncher.processes.values().forEach(( p) -> assertThat(p.isAlive()).isTrue());
        // processes are stopped in reverse order of startup
        underTest.terminate();
        assertThat(orderedStops).containsExactly(COMPUTE_ENGINE, WEB_SERVER, ELASTICSEARCH);
        processLauncher.processes.values().forEach(( p) -> assertThat(p.isAlive()).isFalse());
        // does nothing because scheduler is already terminated
        underTest.awaitTermination();
    }

    @Test
    public void all_processes_are_stopped_if_one_process_goes_down() throws Exception {
        Scheduler underTest = startAll();
        processLauncher.waitForProcess(WEB_SERVER).destroyForcibly();
        underTest.awaitTermination();
        assertThat(orderedStops).containsExactly(WEB_SERVER, COMPUTE_ENGINE, ELASTICSEARCH);
        processLauncher.processes.values().forEach(( p) -> assertThat(p.isAlive()).isFalse());
        // following does nothing
        underTest.terminate();
        underTest.awaitTermination();
    }

    @Test
    public void all_processes_are_stopped_if_one_process_fails_to_start() throws Exception {
        SchedulerImpl underTest = newScheduler(false);
        processLauncher.makeStartupFail = COMPUTE_ENGINE;
        underTest.schedule();
        processLauncher.waitForProcess(ELASTICSEARCH).operational = true;
        processLauncher.waitForProcess(WEB_SERVER).operational = true;
        underTest.awaitTermination();
        assertThat(orderedStops).containsExactly(WEB_SERVER, ELASTICSEARCH);
        processLauncher.processes.values().forEach(( p) -> assertThat(p.isAlive()).isFalse());
    }

    @Test
    public void terminate_can_be_called_multiple_times() throws Exception {
        Scheduler underTest = startAll();
        underTest.terminate();
        processLauncher.processes.values().forEach(( p) -> assertThat(p.isAlive()).isFalse());
        // does nothing
        underTest.terminate();
    }

    @Test
    public void awaitTermination_blocks_until_all_processes_are_stopped() throws Exception {
        Scheduler underTest = startAll();
        Thread awaitingTermination = new Thread(() -> underTest.awaitTermination());
        awaitingTermination.start();
        assertThat(awaitingTermination.isAlive()).isTrue();
        underTest.terminate();
        // the thread is being stopped
        awaitingTermination.join();
        assertThat(awaitingTermination.isAlive()).isFalse();
    }

    @Test
    public void restart_stops_all_if_new_settings_are_not_allowed() throws Exception {
        Scheduler underTest = startAll();
        Mockito.doThrow(new IllegalStateException("reload error")).when(appReloader).reload(settings);
        processLauncher.waitForProcess(WEB_SERVER).askedForRestart = true;
        // waiting for all processes to be stopped
        processLauncher.waitForProcessDown(ELASTICSEARCH);
        processLauncher.waitForProcessDown(COMPUTE_ENGINE);
        processLauncher.waitForProcessDown(WEB_SERVER);
        // verify that awaitTermination() does not block
        underTest.awaitTermination();
    }

    @Test
    public void search_node_starts_only_elasticsearch() throws Exception {
        settings.set(CLUSTER_ENABLED.getKey(), "true");
        settings.set(CLUSTER_NODE_TYPE.getKey(), "search");
        addRequiredNodeProperties();
        SchedulerImpl underTest = newScheduler(true);
        underTest.schedule();
        processLauncher.waitForProcessAlive(ProcessId.ELASTICSEARCH);
        assertThat(processLauncher.processes).hasSize(1);
        underTest.terminate();
    }

    @Test
    public void application_node_starts_only_web_and_ce() throws Exception {
        clusterAppState.setOperational(ProcessId.ELASTICSEARCH);
        settings.set(CLUSTER_ENABLED.getKey(), "true");
        settings.set(CLUSTER_NODE_TYPE.getKey(), "application");
        SchedulerImpl underTest = newScheduler(true);
        underTest.schedule();
        SchedulerImplTest.TestProcess web = processLauncher.waitForProcessAlive(WEB_SERVER);
        web.operational = true;
        processLauncher.waitForProcessAlive(COMPUTE_ENGINE);
        assertThat(processLauncher.processes).hasSize(2);
        underTest.terminate();
    }

    @Test
    public void search_node_starts_even_if_web_leader_is_not_yet_operational() throws Exception {
        // leader takes the lock, so underTest won't get it
        assertThat(clusterAppState.tryToLockWebLeader()).isTrue();
        clusterAppState.setOperational(ProcessId.ELASTICSEARCH);
        settings.set(CLUSTER_ENABLED.getKey(), "true");
        settings.set(CLUSTER_NODE_TYPE.getKey(), "search");
        addRequiredNodeProperties();
        SchedulerImpl underTest = newScheduler(true);
        underTest.schedule();
        processLauncher.waitForProcessAlive(ProcessId.ELASTICSEARCH);
        assertThat(processLauncher.processes).hasSize(1);
        underTest.terminate();
    }

    @Test
    public void web_follower_starts_only_when_web_leader_is_operational() throws Exception {
        // leader takes the lock, so underTest won't get it
        assertThat(clusterAppState.tryToLockWebLeader()).isTrue();
        clusterAppState.setOperational(ProcessId.ELASTICSEARCH);
        settings.set(CLUSTER_ENABLED.getKey(), "true");
        settings.set(CLUSTER_NODE_TYPE.getKey(), "application");
        SchedulerImpl underTest = newScheduler(true);
        underTest.schedule();
        assertThat(processLauncher.processes).hasSize(0);
        // leader becomes operational -> follower can start
        clusterAppState.setOperational(WEB_SERVER);
        processLauncher.waitForProcessAlive(WEB_SERVER);
        processLauncher.waitForProcessAlive(COMPUTE_ENGINE);
        assertThat(processLauncher.processes).hasSize(2);
        underTest.terminate();
    }

    @Test
    public void web_server_waits_for_remote_elasticsearch_to_be_started_if_local_es_is_disabled() throws Exception {
        settings.set(CLUSTER_ENABLED.getKey(), "true");
        settings.set(CLUSTER_NODE_TYPE.getKey(), "application");
        SchedulerImpl underTest = newScheduler(true);
        underTest.schedule();
        // WEB and CE wait for ES to be up
        assertThat(processLauncher.processes).isEmpty();
        // ES becomes operational on another node -> web leader can start
        clusterAppState.setRemoteOperational(ProcessId.ELASTICSEARCH);
        processLauncher.waitForProcessAlive(WEB_SERVER);
        assertThat(processLauncher.processes).hasSize(1);
        underTest.terminate();
    }

    private class TestCommandFactory implements CommandFactory {
        @Override
        public EsScriptCommand createEsCommand() {
            return esScriptCommand;
        }

        @Override
        public JavaCommand createWebCommand(boolean leader) {
            return leader ? webLeaderCommand : webFollowerCommand;
        }

        @Override
        public JavaCommand createCeCommand() {
            return ceCommand;
        }
    }

    private class TestProcessLauncher implements ProcessLauncher {
        private final EnumMap<ProcessId, SchedulerImplTest.TestProcess> processes = new EnumMap(ProcessId.class);

        private final List<AbstractCommand<?>> commands = Collections.synchronizedList(new ArrayList<>());

        private ProcessId makeStartupFail = null;

        @Override
        public ProcessMonitor launch(AbstractCommand command) {
            return launchImpl(command);
        }

        private ProcessMonitor launchImpl(AbstractCommand<?> javaCommand) {
            commands.add(javaCommand);
            if ((makeStartupFail) == (javaCommand.getProcessId())) {
                throw new IllegalStateException(("cannot start " + (javaCommand.getProcessId())));
            }
            SchedulerImplTest.TestProcess process = new SchedulerImplTest.TestProcess(javaCommand.getProcessId());
            processes.put(javaCommand.getProcessId(), process);
            return process;
        }

        private SchedulerImplTest.TestProcess waitForProcess(ProcessId id) throws InterruptedException {
            while (true) {
                SchedulerImplTest.TestProcess p = processes.get(id);
                if (p != null) {
                    return p;
                }
                Thread.sleep(1L);
            } 
        }

        private SchedulerImplTest.TestProcess waitForProcessAlive(ProcessId id) throws InterruptedException {
            while (true) {
                SchedulerImplTest.TestProcess p = processes.get(id);
                if ((p != null) && (p.isAlive())) {
                    return p;
                }
                Thread.sleep(1L);
            } 
        }

        private SchedulerImplTest.TestProcess waitForProcessDown(ProcessId id) throws InterruptedException {
            while (true) {
                SchedulerImplTest.TestProcess p = processes.get(id);
                if ((p != null) && (!(p.isAlive()))) {
                    return p;
                }
                Thread.sleep(1L);
            } 
        }

        @Override
        public void close() {
            for (SchedulerImplTest.TestProcess process : processes.values()) {
                process.destroyForcibly();
            }
        }
    }

    private class TestProcess implements AutoCloseable , ProcessMonitor {
        private final ProcessId processId;

        private final CountDownLatch alive = new CountDownLatch(1);

        private boolean operational = false;

        private boolean askedForRestart = false;

        private TestProcess(ProcessId processId) {
            this.processId = processId;
        }

        @Override
        public InputStream getInputStream() {
            return Mockito.mock(InputStream.class, Mockito.RETURNS_MOCKS);
        }

        @Override
        public InputStream getErrorStream() {
            return Mockito.mock(InputStream.class, Mockito.RETURNS_MOCKS);
        }

        @Override
        public void closeStreams() {
        }

        @Override
        public boolean isAlive() {
            return (alive.getCount()) == 1;
        }

        @Override
        public void askForStop() {
            destroyForcibly();
        }

        @Override
        public void destroyForcibly() {
            if (isAlive()) {
                orderedStops.add(processId);
            }
            alive.countDown();
        }

        @Override
        public void waitFor() throws InterruptedException {
            alive.await();
        }

        @Override
        public void waitFor(long timeout, TimeUnit timeoutUnit) throws InterruptedException {
            alive.await(timeout, timeoutUnit);
        }

        @Override
        public boolean isOperational() {
            return operational;
        }

        @Override
        public boolean askedForRestart() {
            return askedForRestart;
        }

        @Override
        public void acknowledgeAskForRestart() {
            this.askedForRestart = false;
        }

        @Override
        public void close() {
            alive.countDown();
        }
    }
}

