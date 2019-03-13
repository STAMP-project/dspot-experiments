package com.github.dockerjava.cmd;


import InspectContainerResponse.Mount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Capability;
import com.github.dockerjava.api.model.Device;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.api.model.VolumesFrom;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.junit.DockerMatchers;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.jcip.annotations.NotThreadSafe;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.dockerjava.cmd.CmdIT.FactoryType.JERSEY;


@NotThreadSafe
public class StartContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(StartContainerCmdIT.class);

    @Test
    public void startContainerWithVolumes() throws DockerException {
        // see http://docs.docker.io/use/working_with_volumes/
        Volume volume1 = new Volume("/opt/webapp1");
        Volume volume2 = new Volume("/opt/webapp2");
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withVolumes(volume1, volume2).withCmd("true").withHostConfig(HostConfig.newHostConfig().withBinds(new com.github.dockerjava.api.model.Bind("/src/webapp1", volume1, AccessMode.ro), new com.github.dockerjava.api.model.Bind("/src/webapp2", volume2))).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getVolumes().keySet(), Matchers.contains("/opt/webapp1", "/opt/webapp2"));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        dockerRule.getClient().waitContainerCmd(container.getId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse, DockerMatchers.mountedVolumes(Matchers.containsInAnyOrder(volume1, volume2)));
        final List<InspectContainerResponse.Mount> mounts = inspectContainerResponse.getMounts();
        MatcherAssert.assertThat(mounts, Matchers.hasSize(2));
        final InspectContainerResponse.Mount mount1 = new InspectContainerResponse.Mount().withRw(false).withMode("ro").withDestination(volume1).withSource("/src/webapp1");
        final InspectContainerResponse.Mount mount2 = new InspectContainerResponse.Mount().withRw(true).withMode("rw").withDestination(volume2).withSource("/src/webapp2");
        MatcherAssert.assertThat(mounts, Matchers.containsInAnyOrder(mount1, mount2));
    }

    @Test
    public void startContainerWithVolumesFrom() throws DockerException {
        Volume volume1 = new Volume("/opt/webapp1");
        Volume volume2 = new Volume("/opt/webapp2");
        String container1Name = UUID.randomUUID().toString();
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(container1Name).withHostConfig(HostConfig.newHostConfig().withBinds(new com.github.dockerjava.api.model.Bind("/src/webapp1", volume1), new com.github.dockerjava.api.model.Bind("/src/webapp2", volume2))).exec();
        StartContainerCmdIT.LOG.info("Created container1 {}", container1.toString());
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        StartContainerCmdIT.LOG.info("Started container1 {}", container1.toString());
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse1, DockerMatchers.mountedVolumes(Matchers.containsInAnyOrder(volume1, volume2)));
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withVolumesFrom(new VolumesFrom(container1Name))).exec();
        StartContainerCmdIT.LOG.info("Created container2 {}", container2.toString());
        dockerRule.getClient().startContainerCmd(container2.getId()).exec();
        StartContainerCmdIT.LOG.info("Started container2 {}", container2.toString());
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse2, DockerMatchers.mountedVolumes(Matchers.containsInAnyOrder(volume1, volume2)));
    }

    @Test
    public void startContainerWithDns() throws DockerException {
        String aDnsServer = "8.8.8.8";
        String anotherDnsServer = "8.8.4.4";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").withHostConfig(HostConfig.newHostConfig().withDns(aDnsServer, anotherDnsServer)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getDns()), Matchers.contains(aDnsServer, anotherDnsServer));
    }

    @Test
    public void startContainerWithDnsSearch() throws DockerException {
        String dnsSearch = "example.com";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").withHostConfig(HostConfig.newHostConfig().withDnsSearch(dnsSearch)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getDnsSearch()), Matchers.contains(dnsSearch));
    }

    @Test
    public void startContainerWithPortBindings() throws DockerException {
        int baseport = ((getFactoryType()) == (JERSEY)) ? 13000 : 14000;
        ExposedPort tcp22 = ExposedPort.tcp(22);
        ExposedPort tcp23 = ExposedPort.tcp(23);
        Ports portBindings = new Ports();
        portBindings.bind(tcp22, Binding.bindPort((baseport + 22)));
        portBindings.bind(tcp23, Binding.bindPort((baseport + 23)));
        portBindings.bind(tcp23, Binding.bindPort((baseport + 24)));
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").withExposedPorts(tcp22, tcp23).withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getConfig().getExposedPorts()), Matchers.contains(tcp22, tcp23));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPortBindings().getBindings().get(tcp22)[0], Matchers.is(Matchers.equalTo(Binding.bindPort((baseport + 22)))));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPortBindings().getBindings().get(tcp23)[0], Matchers.is(Matchers.equalTo(Binding.bindPort((baseport + 23)))));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPortBindings().getBindings().get(tcp23)[1], Matchers.is(Matchers.equalTo(Binding.bindPort((baseport + 24)))));
    }

    @Test
    public void startContainerWithRandomPortBindings() throws DockerException {
        ExposedPort tcp22 = ExposedPort.tcp(22);
        ExposedPort tcp23 = ExposedPort.tcp(23);
        Ports portBindings = new Ports();
        portBindings.bind(tcp22, Binding.empty());
        portBindings.bind(tcp23, Binding.empty());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withExposedPorts(tcp22, tcp23).withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings).withPublishAllPorts(true)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getConfig().getExposedPorts()), Matchers.contains(tcp22, tcp23));
        MatcherAssert.assertThat(inspectContainerResponse.getNetworkSettings().getPorts().getBindings().get(tcp22)[0].getHostPortSpec(), Matchers.is(Matchers.not(Matchers.equalTo(String.valueOf(tcp22.getPort())))));
        MatcherAssert.assertThat(inspectContainerResponse.getNetworkSettings().getPorts().getBindings().get(tcp23)[0].getHostPortSpec(), Matchers.is(Matchers.not(Matchers.equalTo(String.valueOf(tcp23.getPort())))));
    }

    @Test(expected = InternalServerErrorException.class)
    public void startContainerWithConflictingPortBindings() throws DockerException {
        ExposedPort tcp22 = ExposedPort.tcp(22);
        ExposedPort tcp23 = ExposedPort.tcp(23);
        Ports portBindings = new Ports();
        portBindings.bind(tcp22, Binding.bindPort(11022));
        portBindings.bind(tcp23, Binding.bindPort(11022));
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").withExposedPorts(tcp22, tcp23).withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
    }

    @Test
    public void startContainerWithLinkingDeprecated() throws DockerException {
        String container1Name = "containerWithLink1" + (dockerRule.getKind());
        String container2Name = "containerWithLink2" + (dockerRule.getKind());
        dockerRule.ensureContainerRemoved(container1Name);
        dockerRule.ensureContainerRemoved(container2Name);
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(container1Name).exec();
        StartContainerCmdIT.LOG.info("Created container1 {}", container1.toString());
        MatcherAssert.assertThat(container1.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        StartContainerCmdIT.LOG.info("Container1 Inspect: {}", inspectContainerResponse1.toString());
        MatcherAssert.assertThat(inspectContainerResponse1.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse1.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse1.getId(), Matchers.startsWith(container1.getId()));
        MatcherAssert.assertThat(inspectContainerResponse1.getName(), Matchers.equalTo(("/" + container1Name)));
        MatcherAssert.assertThat(inspectContainerResponse1.getImageId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse1.getState(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse1.getState().getRunning(), Matchers.is(true));
        if (!(inspectContainerResponse1.getState().getRunning())) {
            MatcherAssert.assertThat(inspectContainerResponse1.getState().getExitCode(), Matchers.is(Matchers.equalTo(0)));
        }
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(container2Name).withHostConfig(HostConfig.newHostConfig().withLinks(new Link(container1Name, (container1Name + "Link")))).exec();
        StartContainerCmdIT.LOG.info("Created container2 {}", container2.toString());
        MatcherAssert.assertThat(container2.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container2.getId()).exec();
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        StartContainerCmdIT.LOG.info("Container2 Inspect: {}", inspectContainerResponse2.toString());
        MatcherAssert.assertThat(inspectContainerResponse2.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.equalTo(new Link[]{ new Link(container1Name, (container1Name + "Link")) }));
        MatcherAssert.assertThat(inspectContainerResponse2.getId(), Matchers.startsWith(container2.getId()));
        MatcherAssert.assertThat(inspectContainerResponse2.getName(), Matchers.equalTo(("/" + container2Name)));
        MatcherAssert.assertThat(inspectContainerResponse2.getImageId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse2.getState(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getState().getRunning(), Matchers.is(true));
    }

    @Test
    public void startContainerWithLinking() throws DockerException {
        String container1Name = "containerWithLinking1" + (dockerRule.getKind());
        String container2Name = "containerWithLinking2" + (dockerRule.getKind());
        dockerRule.ensureContainerRemoved(container1Name);
        dockerRule.ensureContainerRemoved(container2Name);
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(container1Name).exec();
        StartContainerCmdIT.LOG.info("Created container1 {}", container1.toString());
        MatcherAssert.assertThat(container1.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        StartContainerCmdIT.LOG.info("Container1 Inspect: {}", inspectContainerResponse1.toString());
        MatcherAssert.assertThat(inspectContainerResponse1.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse1.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse1.getId(), Matchers.startsWith(container1.getId()));
        MatcherAssert.assertThat(inspectContainerResponse1.getName(), Matchers.equalTo(("/" + container1Name)));
        MatcherAssert.assertThat(inspectContainerResponse1.getImageId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse1.getState(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse1.getState().getRunning(), Matchers.is(true));
        if (!(inspectContainerResponse1.getState().getRunning())) {
            MatcherAssert.assertThat(inspectContainerResponse1.getState().getExitCode(), Matchers.is(Matchers.equalTo(0)));
        }
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withName(container2Name).withHostConfig(HostConfig.newHostConfig().withLinks(new Link(container1Name, (container1Name + "Link")))).exec();
        StartContainerCmdIT.LOG.info("Created container2 {}", container2.toString());
        MatcherAssert.assertThat(container2.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container2.getId()).exec();
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        StartContainerCmdIT.LOG.info("Container2 Inspect: {}", inspectContainerResponse2.toString());
        MatcherAssert.assertThat(inspectContainerResponse2.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.equalTo(new Link[]{ new Link(container1Name, (container1Name + "Link")) }));
        MatcherAssert.assertThat(inspectContainerResponse2.getId(), Matchers.startsWith(container2.getId()));
        MatcherAssert.assertThat(inspectContainerResponse2.getName(), Matchers.equalTo(("/" + container2Name)));
        MatcherAssert.assertThat(inspectContainerResponse2.getImageId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse2.getState(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getState().getRunning(), Matchers.is(true));
    }

    @Test
    public void startContainer() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd(new String[]{ "top" }).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        StartContainerCmdIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        MatcherAssert.assertThat(inspectContainerResponse.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse.getId(), Matchers.startsWith(container.getId()));
        MatcherAssert.assertThat(inspectContainerResponse.getImageId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse.getState(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(true));
        if (!(inspectContainerResponse.getState().getRunning())) {
            MatcherAssert.assertThat(inspectContainerResponse.getState().getExitCode(), Matchers.is(Matchers.equalTo(0)));
        }
    }

    @Test(expected = NotFoundException.class)
    public void testStartNonExistingContainer() throws DockerException {
        dockerRule.getClient().startContainerCmd("non-existing").exec();
    }

    /**
     * This tests support for --net option for the docker run command: --net="bridge" Set the Network mode for the container 'bridge':
     * creates a new network stack for the container on the docker bridge 'none': no networking for this container 'container:': reuses
     * another container network stack 'host': use the host network stack inside the container. Note: the host mode gives the container full
     * access to local system services such as D-bus and is therefore considered insecure.
     */
    @Test
    public void startContainerWithNetworkMode() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("true").withHostConfig(HostConfig.newHostConfig().withNetworkMode("host")).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getNetworkMode(), Matchers.is(Matchers.equalTo("host")));
    }

    @Test
    public void startContainerWithCapAddAndCapDrop() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withCapAdd(Capability.NET_ADMIN).withCapDrop(Capability.MKNOD)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(true));
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getCapAdd()), Matchers.contains(Capability.NET_ADMIN));
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getCapDrop()), Matchers.contains(Capability.MKNOD));
    }

    @Test
    public void startContainerWithDevices() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withDevices(new Device("rwm", "/dev/nulo", "/dev/zero"))).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(true));
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getDevices()), Matchers.contains(new Device("rwm", "/dev/nulo", "/dev/zero")));
    }

    @Test
    public void startContainerWithExtraHosts() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withExtraHosts("dockerhost:127.0.0.1")).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(true));
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getExtraHosts()), Matchers.contains("dockerhost:127.0.0.1"));
    }

    @Test
    public void startContainerWithRestartPolicy() throws DockerException {
        RestartPolicy restartPolicy = RestartPolicy.onFailureRestart(5);
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withRestartPolicy(restartPolicy)).exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getState().getRunning(), Matchers.is(true));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getRestartPolicy(), Matchers.is(Matchers.equalTo(restartPolicy)));
    }

    @Test
    public void existingHostConfigIsPreservedByBlankStartCmd() throws DockerException {
        String dnsServer = "8.8.8.8";
        // prepare a container with custom DNS
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withHostConfig(HostConfig.newHostConfig().withDns(dnsServer)).withCmd("true").exec();
        StartContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        // start container _without_any_customization_ (important!)
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        // The DNS setting survived.
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getDns(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getDns()), Matchers.contains(dnsServer));
    }

    @Test
    public void anUnconfiguredCommandSerializesToEmptyJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        StartContainerCmd command = dockerRule.getClient().startContainerCmd("");
        MatcherAssert.assertThat(objectMapper.writeValueAsString(command), Matchers.is("{}"));
    }
}

