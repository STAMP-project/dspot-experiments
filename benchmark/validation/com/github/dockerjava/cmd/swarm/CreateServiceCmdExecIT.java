package com.github.dockerjava.cmd.swarm;


import EndpointResolutionMode.VIP;
import PortConfig.PublishMode.host;
import PortConfigProtocol.TCP;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ContainerSpec;
import com.github.dockerjava.api.model.EndpointSpec;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.NetworkAttachmentConfig;
import com.github.dockerjava.api.model.PortConfig;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.ServiceModeConfig;
import com.github.dockerjava.api.model.ServiceReplicatedModeOptions;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.api.model.TaskSpec;
import com.github.dockerjava.api.model.TmpfsOptions;
import com.github.dockerjava.cmd.CmdIT;
import com.github.dockerjava.junit.DockerRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CreateServiceCmdExecIT extends SwarmCmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(CreateServiceCmdExecIT.class);

    private static final String SERVICE_NAME = "theservice";

    @Test
    public void testCreateService() throws DockerException {
        dockerRule.getClient().initializeSwarmCmd(new SwarmSpec()).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        dockerRule.getClient().createServiceCmd(new ServiceSpec().withName(CreateServiceCmdExecIT.SERVICE_NAME).withTaskTemplate(new TaskSpec().withContainerSpec(new ContainerSpec().withImage(DockerRule.DEFAULT_IMAGE)))).exec();
        List<Service> services = dockerRule.getClient().listServicesCmd().withNameFilter(Lists.newArrayList(CreateServiceCmdExecIT.SERVICE_NAME)).exec();
        MatcherAssert.assertThat(services, Matchers.hasSize(1));
        dockerRule.getClient().removeServiceCmd(CreateServiceCmdExecIT.SERVICE_NAME).exec();
    }

    @Test
    public void testCreateServiceWithNetworks() {
        dockerRule.getClient().initializeSwarmCmd(new SwarmSpec()).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        String networkId = dockerRule.getClient().createNetworkCmd().withName("networkname").withDriver("overlay").withIpam(new Network.Ipam().withDriver("default")).exec().getId();
        ServiceSpec spec = new ServiceSpec().withName(CreateServiceCmdExecIT.SERVICE_NAME).withTaskTemplate(new TaskSpec().withForceUpdate(0).withContainerSpec(new ContainerSpec().withImage("busybox"))).withNetworks(Lists.newArrayList(new NetworkAttachmentConfig().withTarget(networkId).withAliases(Lists.<String>newArrayList("alias1", "alias2")))).withLabels(ImmutableMap.of("com.docker.java.usage", "SwarmServiceIT")).withMode(new ServiceModeConfig().withReplicated(new ServiceReplicatedModeOptions().withReplicas(1))).withEndpointSpec(new EndpointSpec().withMode(VIP).withPorts(Lists.<PortConfig>newArrayList(new PortConfig().withPublishMode(host).withPublishedPort(22).withProtocol(TCP))));
        dockerRule.getClient().createServiceCmd(spec).exec();
        List<Service> services = dockerRule.getClient().listServicesCmd().withNameFilter(Lists.newArrayList(CreateServiceCmdExecIT.SERVICE_NAME)).exec();
        MatcherAssert.assertThat(services, Matchers.hasSize(1));
        MatcherAssert.assertThat(services.get(0).getSpec(), Matchers.is(spec));
        dockerRule.getClient().removeServiceCmd(CreateServiceCmdExecIT.SERVICE_NAME).exec();
    }

    @Test
    public void testCreateServiceWithTmpfs() {
        dockerRule.getClient().initializeSwarmCmd(new SwarmSpec()).withListenAddr("127.0.0.1").withAdvertiseAddr("127.0.0.1").exec();
        Mount tmpMount = new Mount().withTmpfsOptions(new TmpfsOptions().withSizeBytes(600L)).withTarget("/tmp/foo");
        dockerRule.getClient().createServiceCmd(new ServiceSpec().withName(CreateServiceCmdExecIT.SERVICE_NAME).withTaskTemplate(new TaskSpec().withContainerSpec(new ContainerSpec().withImage(DockerRule.DEFAULT_IMAGE).withMounts(Collections.singletonList(tmpMount))))).exec();
        List<Service> services = dockerRule.getClient().listServicesCmd().withNameFilter(Lists.newArrayList(CreateServiceCmdExecIT.SERVICE_NAME)).exec();
        MatcherAssert.assertThat(services, Matchers.hasSize(1));
        List<Mount> mounts = dockerRule.getClient().inspectServiceCmd(CreateServiceCmdExecIT.SERVICE_NAME).exec().getSpec().getTaskTemplate().getContainerSpec().getMounts();
        MatcherAssert.assertThat(mounts, Matchers.hasSize(1));
        MatcherAssert.assertThat(mounts.get(0), Matchers.is(tmpMount));
        dockerRule.getClient().removeServiceCmd(CreateServiceCmdExecIT.SERVICE_NAME).exec();
    }
}

