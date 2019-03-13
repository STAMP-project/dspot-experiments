package com.github.dockerjava.cmd;


import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.utils.TestUtils;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class InspectNetworkCmdIT extends CmdIT {
    @Test
    public void inspectNetwork() throws DockerException {
        DockerAssume.assumeNotSwarm("no network in swarm", dockerRule);
        List<Network> networks = dockerRule.getClient().listNetworksCmd().exec();
        Network expected = TestUtils.findNetwork(networks, "bridge");
        Network network = dockerRule.getClient().inspectNetworkCmd().withNetworkId(expected.getId()).exec();
        MatcherAssert.assertThat(network.getName(), Matchers.equalTo(expected.getName()));
        MatcherAssert.assertThat(network.getScope(), Matchers.equalTo(expected.getScope()));
        MatcherAssert.assertThat(network.getDriver(), Matchers.equalTo(expected.getDriver()));
        MatcherAssert.assertThat(network.getIpam().getConfig().get(0).getSubnet(), Matchers.equalTo(expected.getIpam().getConfig().get(0).getSubnet()));
        MatcherAssert.assertThat(network.getIpam().getDriver(), Matchers.equalTo(expected.getIpam().getDriver()));
    }
}

