package com.github.dockerjava.cmd.swarm;


import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotAcceptableException;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.cmd.CmdIT;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateSwarmCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(UpdateSwarmCmdExecIT.class);

    @Test(expected = NotAcceptableException.class)
    public void updatingSwarmThrowsWhenNotInSwarm() throws DockerException {
        SwarmSpec swarmSpec = new SwarmSpec().withName("swarm");
        dockerRule.getClient().updateSwarmCmd(swarmSpec).withVersion(1L).exec();
    }
}

