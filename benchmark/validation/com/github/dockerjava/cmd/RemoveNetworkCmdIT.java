package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.junit.DockerAssume;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class RemoveNetworkCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(RemoveNetworkCmdIT.class);

    @Test
    public void removeNetwork() throws DockerException {
        DockerAssume.assumeNotSwarm("Swarm has no network", dockerRule);
        CreateNetworkResponse network = dockerRule.getClient().createNetworkCmd().withName("test-network").exec();
        RemoveNetworkCmdIT.LOG.info("Removing network: {}", network.getId());
        dockerRule.getClient().removeNetworkCmd(network.getId()).exec();
        List<Network> networks = dockerRule.getClient().listNetworksCmd().exec();
        Matcher matcher = Matchers.not(Matchers.hasItem(hasField("id", Matchers.startsWith(network.getId()))));
        MatcherAssert.assertThat(networks, matcher);
    }

    @Test(expected = NotFoundException.class)
    public void removeNonExistingContainer() throws DockerException {
        DockerAssume.assumeNotSwarm("Swarm has no network", dockerRule);
        dockerRule.getClient().removeNetworkCmd("non-existing").exec();
    }
}

