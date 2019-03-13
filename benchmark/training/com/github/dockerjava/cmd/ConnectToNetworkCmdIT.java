package com.github.dockerjava.cmd;


import Network.ContainerNetworkConfig;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.junit.DockerRule;
import net.jcip.annotations.ThreadSafe;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static com.github.dockerjava.cmd.CmdIT.FactoryType.JERSEY;


/**
 * TODO fix parallel.
 */
@ThreadSafe
public class ConnectToNetworkCmdIT extends CmdIT {
    @Test
    public void connectToNetwork() throws InterruptedException {
        DockerAssume.assumeNotSwarm("no network in swarm", dockerRule);
        String networkName = "connectToNetwork" + (dockerRule.getKind());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        CreateNetworkResponse network = dockerRule.getClient().createNetworkCmd().withName(networkName).exec();
        dockerRule.getClient().connectToNetworkCmd().withNetworkId(network.getId()).withContainerId(container.getId()).exec();
        Network updatedNetwork = dockerRule.getClient().inspectNetworkCmd().withNetworkId(network.getId()).exec();
        Assert.assertTrue(updatedNetwork.getContainers().containsKey(container.getId()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        Assert.assertNotNull(inspectContainerResponse.getNetworkSettings().getNetworks().get(networkName));
    }

    @Test
    public void connectToNetworkWithContainerNetwork() throws InterruptedException {
        DockerAssume.assumeNotSwarm("no network in swarm", dockerRule);
        final String subnetPrefix = ((getFactoryType()) == (JERSEY)) ? "10.100.102" : "10.100.103";
        final String networkName = "ContainerWithNetwork" + (dockerRule.getKind());
        final String containerIp = subnetPrefix + ".100";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        try {
            dockerRule.getClient().removeNetworkCmd(networkName).exec();
        } catch (DockerException ignore) {
        }
        CreateNetworkResponse network = dockerRule.getClient().createNetworkCmd().withName(networkName).withIpam(new Network.Ipam().withConfig(new Network.Ipam.Config().withSubnet((subnetPrefix + ".0/24")))).exec();
        dockerRule.getClient().connectToNetworkCmd().withNetworkId(network.getId()).withContainerId(container.getId()).withContainerNetwork(new ContainerNetwork().withAliases(("aliasName" + (dockerRule.getKind()))).withIpamConfig(new ContainerNetwork.Ipam().withIpv4Address(containerIp))).exec();
        Network updatedNetwork = dockerRule.getClient().inspectNetworkCmd().withNetworkId(network.getId()).exec();
        Network.ContainerNetworkConfig containerNetworkConfig = updatedNetwork.getContainers().get(container.getId());
        Assert.assertNotNull(containerNetworkConfig);
        Assert.assertThat(containerNetworkConfig.getIpv4Address(), Is.is((containerIp + "/24")));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        ContainerNetwork testNetwork = inspectContainerResponse.getNetworkSettings().getNetworks().get(networkName);
        Assert.assertNotNull(testNetwork);
        Assert.assertThat(testNetwork.getAliases(), Matchers.hasItem(("aliasName" + (dockerRule.getKind()))));
        Assert.assertThat(testNetwork.getGateway(), Is.is((subnetPrefix + ".1")));
        Assert.assertThat(testNetwork.getIpAddress(), Is.is(containerIp));
    }
}

