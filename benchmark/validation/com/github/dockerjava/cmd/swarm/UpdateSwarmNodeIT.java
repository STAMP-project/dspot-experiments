package com.github.dockerjava.cmd.swarm;


import SwarmNodeAvailability.PAUSE;
import SwarmNodeState.READY;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.SwarmNodeSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class UpdateSwarmNodeIT extends SwarmCmdIT {
    @Test
    public void testUpdateSwarmNode() {
        DockerClient docker1 = startDockerInDocker();
        docker1.initializeSwarmCmd(new SwarmSpec()).exec();
        List<SwarmNode> nodes = docker1.listSwarmNodesCmd().exec();
        MatcherAssert.assertThat(1, Matchers.is(nodes.size()));
        SwarmNode node = nodes.get(0);
        MatcherAssert.assertThat(READY, Matchers.is(node.getStatus().getState()));
        // update the node availability
        SwarmNodeSpec nodeSpec = node.getSpec().withAvailability(PAUSE);
        docker1.updateSwarmNodeCmd().withSwarmNodeId(node.getId()).withVersion(node.getVersion().getIndex()).withSwarmNodeSpec(nodeSpec).exec();
        nodes = docker1.listSwarmNodesCmd().exec();
        MatcherAssert.assertThat(nodes.size(), Matchers.is(1));
        MatcherAssert.assertThat(nodes.get(0).getSpec().getAvailability(), Matchers.is(PAUSE));
    }
}

