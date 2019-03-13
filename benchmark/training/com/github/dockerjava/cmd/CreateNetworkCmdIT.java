package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.junit.DockerMatchers;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static com.github.dockerjava.cmd.CmdIT.FactoryType.JERSEY;


@NotThreadSafe
public class CreateNetworkCmdIT extends CmdIT {
    @Test
    public void createNetwork() throws DockerException {
        DockerAssume.assumeNotSwarm("no network in swarm", dockerRule);
        String networkName = "createNetwork" + (dockerRule.getKind());
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        Network network = dockerRule.getClient().inspectNetworkCmd().withNetworkId(createNetworkResponse.getId()).exec();
        Assert.assertThat(network.getName(), CoreMatchers.is(networkName));
        Assert.assertThat(network.getDriver(), CoreMatchers.is("bridge"));
    }

    @Test
    public void createNetworkWithIpamConfig() throws DockerException {
        DockerAssume.assumeNotSwarm("no network in swarm", dockerRule);
        String networkName = "networkIpam" + (dockerRule.getKind());
        String subnet = ((getFactoryType()) == (JERSEY)) ? "10.67.79.0/24" : "10.67.90.0/24";
        Network.Ipam ipam = new Network.Ipam().withConfig(new Network.Ipam.Config().withSubnet(subnet));
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).withIpam(ipam).exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        Network network = dockerRule.getClient().inspectNetworkCmd().withNetworkId(createNetworkResponse.getId()).exec();
        Assert.assertEquals(network.getName(), networkName);
        Assert.assertEquals("bridge", network.getDriver());
        Assert.assertEquals(subnet, network.getIpam().getConfig().iterator().next().getSubnet());
    }

    @Test
    public void createAttachableNetwork() throws DockerException {
        Assume.assumeThat("API version should be > 1.24", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_25));
        String networkName = "createAttachableNetwork" + (dockerRule.getKind());
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).withAttachable(true).exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        Network network = dockerRule.getClient().inspectNetworkCmd().withNetworkId(createNetworkResponse.getId()).exec();
        Assert.assertThat(network, CoreMatchers.notNullValue());
        Assert.assertTrue(network.isAttachable());
    }

    @Test
    public void createNetworkWithLabel() throws DockerException {
        DockerAssume.assumeNotSwarm("no network in swarm?", dockerRule);
        Assume.assumeThat("API version should be >= 1.21", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_21));
        String networkName = "createNetworkWithLabel" + (dockerRule.getKind());
        Map<String, String> labels = new HashMap<>();
        labels.put(("com.example.usage" + (dockerRule.getKind())), "test");
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).withLabels(labels).exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        Network network = dockerRule.getClient().inspectNetworkCmd().withNetworkId(createNetworkResponse.getId()).exec();
        Assert.assertEquals(network.getLabels(), labels);
    }
}

