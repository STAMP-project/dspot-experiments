package com.github.dockerjava.cmd.swarm;


import LocalNodeState.ACTIVE;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotAcceptableException;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.SwarmJoinTokens;
import com.google.common.collect.Lists;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JoinSwarmCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(JoinSwarmCmdExecIT.class);

    @Test
    public void joinSwarmAsWorker() throws DockerException {
        DockerClient docker1 = startDockerInDocker();
        DockerClient docker2 = startDockerInDocker();
        SwarmJoinTokens tokens = initSwarmOnDocker(docker1);
        docker2.joinSwarmCmd().withRemoteAddrs(Lists.newArrayList("docker1")).withJoinToken(tokens.getWorker()).exec();
        JoinSwarmCmdExecIT.LOG.info("docker2 joined docker1's swarm");
        Info info = docker2.infoCmd().exec();
        JoinSwarmCmdExecIT.LOG.info("Inspected docker2: {}", info.toString());
        MatcherAssert.assertThat(info.getSwarm().getLocalNodeState(), Matchers.is(Matchers.equalTo(ACTIVE)));
    }

    @Test
    public void joinSwarmAsManager() throws DockerException, InterruptedException {
        DockerClient docker1 = startDockerInDocker();
        DockerClient docker2 = startDockerInDocker();
        SwarmJoinTokens tokens = initSwarmOnDocker(docker1);
        docker2.joinSwarmCmd().withRemoteAddrs(Lists.newArrayList("docker1")).withJoinToken(tokens.getManager()).exec();
        JoinSwarmCmdExecIT.LOG.info("docker2 joined docker1's swarm");
        Info info = docker2.infoCmd().exec();
        JoinSwarmCmdExecIT.LOG.info("Inspected docker2: {}", info.toString());
        MatcherAssert.assertThat(info.getSwarm().getLocalNodeState(), Matchers.is(Matchers.equalTo(ACTIVE)));
    }

    @Test(expected = NotAcceptableException.class)
    public void joinSwarmIfAlreadyInSwarm() {
        DockerClient docker1 = startDockerInDocker();
        DockerClient docker2 = startDockerInDocker();
        SwarmJoinTokens tokens = initSwarmOnDocker(docker1);
        initSwarmOnDocker(docker2);
        docker2.joinSwarmCmd().withRemoteAddrs(Lists.newArrayList("docker1")).withJoinToken(tokens.getWorker()).exec();
    }
}

