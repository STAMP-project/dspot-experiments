package com.github.dockerjava.cmd;


import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.utils.TestUtils;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ListNetworksCmdIT extends CmdIT {
    @Test
    public void listNetworks() throws DockerException {
        DockerAssume.assumeNotSwarm("Swarm has no network", dockerRule);
        List<Network> networks = dockerRule.getClient().listNetworksCmd().exec();
        Network network = TestUtils.findNetwork(networks, "bridge");
        MatcherAssert.assertThat(network.getName(), Matchers.equalTo("bridge"));
        MatcherAssert.assertThat(network.getScope(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(network.getDriver(), Matchers.equalTo("bridge"));
        MatcherAssert.assertThat(network.getIpam().getDriver(), Matchers.equalTo("default"));
    }
}

