package com.github.dockerjava.cmd.swarm;


import LocalNodeState.ACTIVE;
import LocalNodeState.INACTIVE;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotAcceptableException;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.cmd.CmdIT;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LeaveSwarmCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(LeaveSwarmCmdExecIT.class);

    @Test
    public void leaveSwarmAsMaster() throws DockerException {
        SwarmSpec swarmSpec = new SwarmSpec().withName("firstSpec");
        dockerRule.getClient().initializeSwarmCmd(swarmSpec).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        LeaveSwarmCmdExecIT.LOG.info("Initialized swarm: {}", swarmSpec.toString());
        Info info = dockerRule.getClient().infoCmd().exec();
        LeaveSwarmCmdExecIT.LOG.info("Inspected docker: {}", info.toString());
        MatcherAssert.assertThat(info.getSwarm().getLocalNodeState(), Matchers.is(ACTIVE));
        dockerRule.getClient().leaveSwarmCmd().withForceEnabled(true).exec();
        LeaveSwarmCmdExecIT.LOG.info("Left swarm");
        info = dockerRule.getClient().infoCmd().exec();
        LeaveSwarmCmdExecIT.LOG.info("Inspected docker: {}", info.toString());
        MatcherAssert.assertThat(info.getSwarm().getLocalNodeState(), Matchers.is(INACTIVE));
    }

    @Test(expected = NotAcceptableException.class)
    public void leavingSwarmThrowsWhenNotInSwarm() throws DockerException {
        dockerRule.getClient().leaveSwarmCmd().exec();
    }
}

