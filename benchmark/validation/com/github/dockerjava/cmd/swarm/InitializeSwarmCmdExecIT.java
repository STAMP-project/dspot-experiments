package com.github.dockerjava.cmd.swarm;


import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotAcceptableException;
import com.github.dockerjava.api.model.Swarm;
import com.github.dockerjava.api.model.SwarmCAConfig;
import com.github.dockerjava.api.model.SwarmDispatcherConfig;
import com.github.dockerjava.api.model.SwarmOrchestration;
import com.github.dockerjava.api.model.SwarmRaftConfig;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.api.model.TaskDefaults;
import com.github.dockerjava.cmd.CmdIT;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InitializeSwarmCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(InitializeSwarmCmdExecIT.class);

    @Test
    public void initializeSwarm() throws DockerException {
        SwarmSpec swarmSpec = new SwarmSpec().withName("swarm").withDispatcher(new SwarmDispatcherConfig().withHeartbeatPeriod(10000000L)).withOrchestration(new SwarmOrchestration().withTaskHistoryRententionLimit(100)).withCaConfig(/* ns */
        new SwarmCAConfig().withNodeCertExpiry(((60 * 60) * 1000000000L))).withRaft(new SwarmRaftConfig().withElectionTick(8).withSnapshotInterval(20000).withHeartbeatTick(5).withLogEntriesForSlowFollowers(200)).withTaskDefaults(new TaskDefaults());
        dockerRule.getClient().initializeSwarmCmd(swarmSpec).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        InitializeSwarmCmdExecIT.LOG.info("Initialized swarm: {}", swarmSpec.toString());
        Swarm swarm = dockerRule.getClient().inspectSwarmCmd().exec();
        InitializeSwarmCmdExecIT.LOG.info("Inspected swarm: {}", swarm.toString());
        MatcherAssert.assertThat(swarm.getSpec(), Matchers.is(Matchers.equalTo(swarmSpec)));
    }

    @Test(expected = NotAcceptableException.class)
    public void initializingSwarmThrowsWhenAlreadyInSwarm() throws DockerException {
        SwarmSpec swarmSpec = new SwarmSpec().withName("swarm");
        dockerRule.getClient().initializeSwarmCmd(swarmSpec).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        InitializeSwarmCmdExecIT.LOG.info("Initialized swarm: {}", swarmSpec.toString());
        // Initializing a swarm if already in swarm mode should fail
        dockerRule.getClient().initializeSwarmCmd(swarmSpec).withListenAddr("127.0.0.1").exec();
    }
}

