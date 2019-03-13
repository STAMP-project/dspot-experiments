package com.github.dockerjava.cmd;


import LogConfig.LoggingType;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Capability;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Device;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.LogConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.api.model.Ulimit;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.api.model.VolumesFrom;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.junit.DockerMatchers;
import com.github.dockerjava.junit.DockerRule;
import com.github.dockerjava.utils.RegistryUtils;
import com.github.dockerjava.utils.TestUtils;
import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.dockerjava.cmd.CmdIT.FactoryType.JERSEY;


@NotThreadSafe
public class CreateContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(CreateContainerCmdIT.class);

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder(new File("target/"));

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = ConflictException.class)
    public void createContainerWithExistingName() throws DockerException {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("env").withName(containerName).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("env").withName(containerName).exec();
    }

    @Test
    public void createContainerWithVolume() throws DockerException {
        Volume volume = new Volume("/var/log");
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withVolumes(volume).withCmd("true").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        CreateContainerCmdIT.LOG.info("Inspect container {}", inspectContainerResponse.getConfig().getVolumes());
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getVolumes().keySet(), Matchers.contains("/var/log"));
        MatcherAssert.assertThat(inspectContainerResponse.getMounts().get(0).getDestination(), Matchers.equalTo(volume));
        MatcherAssert.assertThat(inspectContainerResponse.getMounts().get(0).getMode(), Matchers.equalTo(""));
        MatcherAssert.assertThat(inspectContainerResponse.getMounts().get(0).getRW(), Matchers.equalTo(true));
    }

    @Test
    public void createContainerWithReadOnlyVolume() throws DockerException {
        Volume volume = new Volume("/srv/test");
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withVolumes(volume).withCmd("true").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        CreateContainerCmdIT.LOG.info("Inspect container {}", inspectContainerResponse.getConfig().getVolumes());
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getVolumes().keySet(), Matchers.contains("/srv/test"));
        MatcherAssert.assertThat(inspectContainerResponse.getMounts().get(0).getDestination(), Matchers.equalTo(volume));
        // TODO: Create a read-only volume and test like this
        // assertFalse(inspectContainerResponse.getMounts().get(0).getRW());
    }

    @Test
    public void createContainerWithVolumesFrom() throws DockerException {
        String container1Name = UUID.randomUUID().toString();
        CreateVolumeResponse volume1Info = dockerRule.getClient().createVolumeCmd().exec();
        CreateVolumeResponse volume2Info = dockerRule.getClient().createVolumeCmd().exec();
        Volume volume1 = new Volume("/src/webapp1");
        Volume volume2 = new Volume("/src/webapp2");
        Bind bind1 = new Bind(volume1Info.getName(), volume1);
        Bind bind2 = new Bind(volume2Info.getName(), volume2);
        // create a running container with bind mounts
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withName(container1Name).withHostConfig(HostConfig.newHostConfig().withBinds(bind1, bind2)).exec();
        CreateContainerCmdIT.LOG.info("Created container1 {}", container1.toString());
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse1.getHostConfig().getBinds()), Matchers.containsInAnyOrder(bind1, bind2));
        MatcherAssert.assertThat(inspectContainerResponse1, DockerMatchers.mountedVolumes(Matchers.containsInAnyOrder(volume1, volume2)));
        // create a second container with volumes from first container
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withVolumesFrom(new VolumesFrom(container1Name))).exec();
        CreateContainerCmdIT.LOG.info("Created container2 {}", container2.toString());
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        // No volumes are created, the information is just stored in .HostConfig.VolumesFrom
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getVolumesFrom(), Matchers.hasItemInArray(new VolumesFrom(container1Name)));
        MatcherAssert.assertThat(inspectContainerResponse1, DockerMatchers.mountedVolumes(Matchers.containsInAnyOrder(volume1, volume2)));
        // To ensure that the information stored in VolumesFrom really is considered
        // when starting the container, we start it and verify that it has the same
        // bind mounts as the first container.
        // This is somehow out of scope here, but it helped me to understand how the
        // VolumesFrom feature really works.
        dockerRule.getClient().startContainerCmd(container2.getId()).exec();
        CreateContainerCmdIT.LOG.info("Started container2 {}", container2.toString());
        inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getVolumesFrom(), Matchers.hasItemInArray(new VolumesFrom(container1Name)));
        MatcherAssert.assertThat(inspectContainerResponse2, DockerMatchers.mountedVolumes(Matchers.containsInAnyOrder(volume1, volume2)));
    }

    @Test
    public void createContainerWithEnv() throws Exception {
        final String testVariable = "VARIABLE=success";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withEnv(testVariable).withCmd("env").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getConfig().getEnv()), Matchers.hasItem(testVariable));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(dockerRule.containerLog(container.getId()), Matchers.containsString(testVariable));
    }

    @Test
    public void createContainerWithHostname() throws Exception {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostName("docker-java").withCmd("env").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getHostName(), Matchers.equalTo("docker-java"));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(dockerRule.containerLog(container.getId()), Matchers.containsString("HOSTNAME=docker-java"));
    }

    @Test(expected = ConflictException.class)
    public void createContainerWithName() throws DockerException {
        String containerName = "container_" + (dockerRule.getKind());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withName(containerName).withCmd("env").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getName(), Matchers.equalTo(("/" + containerName)));
        dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withName(containerName).withCmd("env").exec();
    }

    @Test
    public void createContainerWithLink() throws DockerException {
        String containerName1 = "containerWithlink_" + (dockerRule.getKind());
        String containerName2 = "container2Withlink_" + (dockerRule.getKind());
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withName(containerName1).exec();
        CreateContainerCmdIT.LOG.info("Created container1 {}", container1.toString());
        MatcherAssert.assertThat(container1.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        CreateContainerCmdIT.LOG.info("Container1 Inspect: {}", inspectContainerResponse1.toString());
        MatcherAssert.assertThat(inspectContainerResponse1.getState().getRunning(), Matchers.is(true));
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withName(containerName2).withCmd("env").withHostConfig(HostConfig.newHostConfig().withLinks(new Link(containerName1, "container1Link"))).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container2.toString());
        MatcherAssert.assertThat(container2.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.equalTo(new Link[]{ new Link(containerName1, "container1Link") }));
    }

    @Test
    public void createContainerWithMemorySwappiness() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withMemorySwappiness(42L)).exec();
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        CreateContainerCmdIT.LOG.info("Started container {}", container.toString());
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        CreateContainerCmdIT.LOG.info("Container Inspect: {}", inspectContainerResponse.toString());
        Assert.assertSame(42L, inspectContainerResponse.getHostConfig().getMemorySwappiness());
    }

    @Test
    public void createContainerWithLinkInCustomNetwork() throws DockerException {
        String containerName1 = "containerCustomlink_" + (dockerRule.getKind());
        String containerName2 = "containerCustom2link_" + (dockerRule.getKind());
        String networkName = "linkNetcustom" + (dockerRule.getKind());
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).withDriver("bridge").exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(HostConfig.newHostConfig().withNetworkMode(networkName)).withCmd("sleep", "9999").withName(containerName1).exec();
        MatcherAssert.assertThat(container1.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        CreateContainerCmdIT.LOG.info("Container1 Inspect: {}", inspectContainerResponse1.toString());
        MatcherAssert.assertThat(inspectContainerResponse1.getState().getRunning(), Matchers.is(true));
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(HostConfig.newHostConfig().withLinks(new Link(containerName1, (containerName1 + "Link"))).withNetworkMode(networkName)).withName(containerName2).withCmd("env").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container2.toString());
        MatcherAssert.assertThat(container2.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        ContainerNetwork linkNet = inspectContainerResponse2.getNetworkSettings().getNetworks().get(networkName);
        Assert.assertNotNull(linkNet);
        MatcherAssert.assertThat(linkNet.getLinks(), Matchers.equalTo(new Link[]{ new Link(containerName1, (containerName1 + "Link")) }));
    }

    @Test
    public void createContainerWithCustomIp() throws DockerException {
        String containerName1 = "containerCustomIplink_" + (dockerRule.getKind());
        String networkName = "customIpNet" + (dockerRule.getKind());
        String subnetPrefix = ((getFactoryType()) == (JERSEY)) ? "10.100.104" : "10.100.105";
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withIpam(new Network.Ipam().withConfig(new Network.Ipam.Config().withSubnet((subnetPrefix + ".0/24")))).withDriver("bridge").withName(networkName).exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(HostConfig.newHostConfig().withNetworkMode(networkName)).withCmd("sleep", "9999").withName(containerName1).withIpv4Address((subnetPrefix + ".100")).exec();
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        ContainerNetwork customIpNet = inspectContainerResponse.getNetworkSettings().getNetworks().get(networkName);
        Assert.assertNotNull(customIpNet);
        MatcherAssert.assertThat(customIpNet.getGateway(), Matchers.is((subnetPrefix + ".1")));
        MatcherAssert.assertThat(customIpNet.getIpAddress(), Matchers.is((subnetPrefix + ".100")));
    }

    @Test
    public void createContainerWithAlias() throws DockerException {
        String containerName1 = "containerAlias_" + (dockerRule.getKind());
        String networkName = "aliasNet" + (dockerRule.getKind());
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).withDriver("bridge").exec();
        Assert.assertNotNull(createNetworkResponse.getId());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(HostConfig.newHostConfig().withNetworkMode(networkName)).withCmd("sleep", "9999").withName(containerName1).withAliases(("server" + (dockerRule.getKind()))).exec();
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        ContainerNetwork aliasNet = inspectContainerResponse.getNetworkSettings().getNetworks().get(networkName);
        MatcherAssert.assertThat(aliasNet.getAliases(), Matchers.hasItem(("server" + (dockerRule.getKind()))));
    }

    @Test
    public void createContainerWithCapAddAndCapDrop() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(HostConfig.newHostConfig().withCapAdd(Capability.NET_ADMIN).withCapDrop(Capability.MKNOD)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getCapAdd()), Matchers.contains(Capability.NET_ADMIN));
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getCapDrop()), Matchers.contains(Capability.MKNOD));
    }

    @Test
    public void createContainerWithDns() throws DockerException {
        String aDnsServer = "8.8.8.8";
        String anotherDnsServer = "8.8.4.4";
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("true").withHostConfig(HostConfig.newHostConfig().withDns(aDnsServer, anotherDnsServer)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getDns()), Matchers.contains(aDnsServer, anotherDnsServer));
    }

    @Test
    public void createContainerWithEntrypoint() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withName(("containerEntrypoint" + (dockerRule.getKind()))).withEntrypoint("sleep", "9999").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getConfig().getEntrypoint()), Matchers.contains("sleep", "9999"));
    }

    @Test
    public void createContainerWithExtraHosts() throws DockerException {
        String[] extraHosts = new String[]{ "dockerhost:127.0.0.1", "otherhost:10.0.0.1" };
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withName(("containerextrahosts" + (dockerRule.getKind()))).withHostConfig(HostConfig.newHostConfig().withExtraHosts(extraHosts)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getExtraHosts()), Matchers.containsInAnyOrder("dockerhost:127.0.0.1", "otherhost:10.0.0.1"));
    }

    @Test
    public void createContainerWithDevices() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withDevices(new Device("rwm", "/dev/nulo", "/dev/zero"))).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getDevices()), Matchers.contains(new Device("rwm", "/dev/nulo", "/dev/zero")));
    }

    @Test
    public void createContainerWithPortBindings() throws DockerException {
        int baseport = ((getFactoryType()) == (JERSEY)) ? 11000 : 12000;
        ExposedPort tcp22 = ExposedPort.tcp(22);
        ExposedPort tcp23 = ExposedPort.tcp(23);
        Ports portBindings = new Ports();
        portBindings.bind(tcp22, Binding.bindPort((baseport + 22)));
        portBindings.bind(tcp23, Binding.bindPort((baseport + 23)));
        portBindings.bind(tcp23, Binding.bindPort((baseport + 24)));
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("true").withExposedPorts(tcp22, tcp23).withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getConfig().getExposedPorts()), Matchers.contains(tcp22, tcp23));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPortBindings().getBindings().get(tcp22)[0], Matchers.is(Matchers.equalTo(Binding.bindPort((baseport + 22)))));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPortBindings().getBindings().get(tcp23)[0], Matchers.is(Matchers.equalTo(Binding.bindPort((baseport + 23)))));
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPortBindings().getBindings().get(tcp23)[1], Matchers.is(Matchers.equalTo(Binding.bindPort((baseport + 24)))));
    }

    @Test
    public void createContainerWithLinking() throws DockerException {
        String containerName1 = "containerWithlinking_" + (dockerRule.getKind());
        String containerName2 = "container2Withlinking_" + (dockerRule.getKind());
        CreateContainerResponse container1 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withName(containerName1).exec();
        CreateContainerCmdIT.LOG.info("Created container1 {}", container1.toString());
        MatcherAssert.assertThat(container1.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container1.getId()).exec();
        InspectContainerResponse inspectContainerResponse1 = dockerRule.getClient().inspectContainerCmd(container1.getId()).exec();
        CreateContainerCmdIT.LOG.info("Container1 Inspect: {}", inspectContainerResponse1.toString());
        MatcherAssert.assertThat(inspectContainerResponse1.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse1.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse1.getId(), Matchers.startsWith(container1.getId()));
        MatcherAssert.assertThat(inspectContainerResponse1.getName(), Matchers.equalTo(("/" + containerName1)));
        MatcherAssert.assertThat(inspectContainerResponse1.getImageId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse1.getState(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse1.getState().getRunning(), Matchers.is(true));
        if (!(inspectContainerResponse1.getState().getRunning())) {
            MatcherAssert.assertThat(inspectContainerResponse1.getState().getExitCode(), Matchers.is(Matchers.equalTo(0)));
        }
        CreateContainerResponse container2 = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withName(containerName2).withHostConfig(HostConfig.newHostConfig().withLinks(new Link(containerName1, (containerName1 + "Link")))).exec();
        CreateContainerCmdIT.LOG.info("Created container2 {}", container2.toString());
        MatcherAssert.assertThat(container2.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse2 = dockerRule.getClient().inspectContainerCmd(container2.getId()).exec();
        CreateContainerCmdIT.LOG.info("Container2 Inspect: {}", inspectContainerResponse2.toString());
        MatcherAssert.assertThat(inspectContainerResponse2.getConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(inspectContainerResponse2.getHostConfig().getLinks(), Matchers.equalTo(new Link[]{ new Link(containerName1, (containerName1 + "Link")) }));
        MatcherAssert.assertThat(inspectContainerResponse2.getId(), Matchers.startsWith(container2.getId()));
        MatcherAssert.assertThat(inspectContainerResponse2.getName(), Matchers.equalTo(("/" + containerName2)));
        MatcherAssert.assertThat(inspectContainerResponse2.getImageId(), Matchers.not(Matchers.isEmptyString()));
    }

    @Test
    public void createContainerWithRestartPolicy() throws DockerException {
        RestartPolicy restartPolicy = RestartPolicy.onFailureRestart(5);
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withHostConfig(HostConfig.newHostConfig().withRestartPolicy(restartPolicy)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getRestartPolicy(), Matchers.is(Matchers.equalTo(restartPolicy)));
    }

    @Test
    public void createContainerWithPidMode() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("true").withHostConfig(HostConfig.newHostConfig().withPidMode("host")).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPidMode(), Matchers.is(Matchers.equalTo("host")));
    }

    /**
     * This tests support for --net option for the docker run command: --net="bridge" Set the Network mode for the container 'bridge':
     * creates a new network stack for the container on the docker bridge 'none': no networking for this container 'container:': reuses
     * another container network stack 'host': use the host network stack inside the container. Note: the host mode gives the container full
     * access to local system services such as D-bus and is therefore considered insecure.
     */
    @Test
    public void createContainerWithNetworkMode() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("true").withHostConfig(HostConfig.newHostConfig().withNetworkMode("host")).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getNetworkMode(), Matchers.is(Matchers.equalTo("host")));
    }

    @Test
    public void createContainerWithMacAddress() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withMacAddress("00:80:41:ae:fd:7e").withCmd("true").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getMacAddress(), Matchers.is("00:80:41:ae:fd:7e"));
    }

    @Test
    public void createContainerWithULimits() throws DockerException {
        String containerName = "containerulimit" + (dockerRule.getKind());
        Ulimit[] ulimits = new Ulimit[]{ new Ulimit("nproc", 709, 1026), new Ulimit("nofile", 1024, 4096) };
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withName(containerName).withHostConfig(HostConfig.newHostConfig().withUlimits(ulimits)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(Arrays.asList(inspectContainerResponse.getHostConfig().getUlimits()), Matchers.containsInAnyOrder(new Ulimit("nproc", 709, 1026), new Ulimit("nofile", 1024, 4096)));
    }

    @Test
    public void createContainerWithLabels() throws DockerException {
        Map<String, String> labels = new HashMap<String, String>();
        labels.put("com.github.dockerjava.null", null);
        labels.put("com.github.dockerjava.Boolean", "true");
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withLabels(labels).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        // null becomes empty string
        labels.put("com.github.dockerjava.null", "");
        // swarm adds 3d label
        MatcherAssert.assertThat(inspectContainerResponse.getConfig().getLabels(), Matchers.allOf(Matchers.hasEntry("com.github.dockerjava.null", ""), Matchers.hasEntry("com.github.dockerjava.Boolean", "true")));
    }

    @Test
    public void createContainerWithLogConfig() throws DockerException {
        LogConfig logConfig = new LogConfig(LoggingType.NONE, null);
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(HostConfig.newHostConfig().withLogConfig(logConfig)).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        // null becomes empty string
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getLogConfig().type, Matchers.is(logConfig.type));
    }

    /**
     * https://github.com/calavera/docker/blob/3781cde61ff10b1d9114ae5b4c5c1d1b2c20a1ee/integration-cli/docker_cli_run_unix_test.go#L319-L333
     */
    @Test
    public void testWithStopSignal() throws Exception {
        Integer signal = 10;// SIGUSR1 in busybox

        CreateContainerResponse resp = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("/bin/sh", "-c", "trap \'echo \"exit trapped 10\"; exit 10\' USR1; while true; do sleep 1; done").withAttachStdin(true).withTty(true).withStopSignal(signal.toString()).exec();
        final String containerId = resp.getId();
        MatcherAssert.assertThat(containerId, Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(containerId).exec();
        InspectContainerResponse inspect = dockerRule.getClient().inspectContainerCmd(containerId).exec();
        MatcherAssert.assertThat(inspect.getState().getRunning(), Matchers.is(true));
        dockerRule.getClient().stopContainerCmd(containerId).exec();
        Thread.sleep(TimeUnit.SECONDS.toMillis(3));
        inspect = dockerRule.getClient().inspectContainerCmd(containerId).exec();
        MatcherAssert.assertThat(inspect.getState().getRunning(), Matchers.is(false));
        MatcherAssert.assertThat(inspect.getState().getExitCode(), Matchers.is(signal));
        StringBuilder stringBuilder = new StringBuilder();
        final CreateContainerCmdIT.StringBuilderLogReader callback = new CreateContainerCmdIT.StringBuilderLogReader(stringBuilder);
        dockerRule.getClient().logContainerCmd(containerId).withStdErr(true).withStdOut(true).withTailAll().exec(callback).awaitCompletion();
        String log = callback.builder.toString();
        MatcherAssert.assertThat(log, Matchers.is("exit trapped 10"));
    }

    private static class StringBuilderLogReader extends LogContainerResultCallback {
        public StringBuilder builder;

        public StringBuilderLogReader(StringBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void onNext(Frame item) {
            builder.append(new String(item.getPayload()).trim());
            super.onNext(item);
        }
    }

    @Test
    public void createContainerWithCgroupParent() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withHostConfig(HostConfig.newHostConfig().withCgroupParent("/parent")).exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainer = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainer.getHostConfig().getCgroupParent(), Matchers.is("/parent"));
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void createContainerWithShmSize() throws DockerException {
        HostConfig hostConfig = new HostConfig().withShmSize((96 * (FileUtils.ONE_MB)));
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(hostConfig).withCmd("true").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getShmSize(), Matchers.is(hostConfig.getShmSize()));
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void createContainerWithShmPidsLimit() throws DockerException {
        Assume.assumeThat("API version should be >= 1.23", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_23));
        HostConfig hostConfig = new HostConfig().withPidsLimit(2L);
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withHostConfig(hostConfig).withCmd("true").exec();
        CreateContainerCmdIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getPidsLimit(), Matchers.is(hostConfig.getPidsLimit()));
    }

    @Test
    public void createContainerWithNetworkID() {
        Assume.assumeThat("API version should be >= 1.23", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_24));
        String networkName = "net-" + (UUID.randomUUID().toString());
        Map<String, String> labels = new HashMap<>();
        labels.put("com.example.label", "test");
        CreateNetworkResponse createNetworkResponse = dockerRule.getClient().createNetworkCmd().withName(networkName).withLabels(labels).withAttachable(true).exec();
        String networkId = createNetworkResponse.getId();
        CreateContainerResponse createContainerResponse = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withLabels(labels).withCmd("true").exec();
        String containerId = createContainerResponse.getId();
        dockerRule.getClient().connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(containerId).exec();
        ContainerNetwork containerNetwork = inspectContainerResponse.getNetworkSettings().getNetworks().get(networkName);
        if (containerNetwork == null) {
            // swarm node used network id
            containerNetwork = inspectContainerResponse.getNetworkSettings().getNetworks().get(networkId);
        }
        MatcherAssert.assertThat(containerNetwork, Matchers.notNullValue());
    }

    @Test
    public void createContainerFromPrivateRegistryWithValidAuth() throws Exception {
        DockerAssume.assumeSwarm(dockerRule.getClient());
        AuthConfig authConfig = RegistryUtils.runPrivateRegistry(dockerRule.getClient());
        String imgName = RegistryUtils.createPrivateImage(dockerRule, "create-container-with-valid-auth");
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(imgName).withAuthConfig(authConfig).exec();
        MatcherAssert.assertThat(container.getId(), Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void createContainerFromPrivateRegistryWithNoAuth() throws Exception {
        AuthConfig authConfig = RegistryUtils.runPrivateRegistry(dockerRule.getClient());
        String imgName = RegistryUtils.createPrivateImage(dockerRule, "create-container-with-no-auth");
        if (TestUtils.isSwarm(dockerRule.getClient())) {
            exception.expect(Matchers.instanceOf(InternalServerErrorException.class));
        } else {
            exception.expect(Matchers.instanceOf(NotFoundException.class));
        }
        dockerRule.getClient().createContainerCmd(imgName).exec();
    }

    @Test
    public void createContainerWithTmpFs() throws DockerException {
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").withHostConfig(new HostConfig().withTmpFs(Collections.singletonMap("/tmp", "rw,noexec,nosuid,size=50m"))).exec();
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        InspectContainerResponse inspectContainerResponse = dockerRule.getClient().inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(inspectContainerResponse.getHostConfig().getTmpFs().get("/tmp"), Matchers.equalTo("rw,noexec,nosuid,size=50m"));
    }
}

