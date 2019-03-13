package com.github.dockerjava.api.model;


import RemoteApiVersion.VERSION_1_38;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.InfoRegistryConfig.IndexConfig;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class InfoTest {
    @Test
    public void serder1Json() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Info.class);
        final Info info = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_22, "info/1.json", type);
        final List<List<String>> driverStatus = Arrays.asList(Arrays.asList("Root Dir", "/mnt/sda1/var/lib/docker/aufs"), Arrays.asList("Backing Filesystem", "extfs"), Arrays.asList("Dirs", "31"), Arrays.asList("Dirperm1 Supported", "true"));
        final Map<String, List<String>> plugins = new LinkedHashMap<>();
        plugins.put("Volume", Collections.singletonList("local"));
        plugins.put("Network", Arrays.asList("bridge", "null", "host"));
        plugins.put("Authorization", null);
        MatcherAssert.assertThat(info, Matchers.notNullValue());
        MatcherAssert.assertThat(info.getArchitecture(), Matchers.equalTo("x86_64"));
        MatcherAssert.assertThat(info.getContainersStopped(), CoreMatchers.is(3));
        MatcherAssert.assertThat(info.getContainersPaused(), CoreMatchers.is(10));
        MatcherAssert.assertThat(info.getContainersRunning(), CoreMatchers.is(2));
        MatcherAssert.assertThat(info.getCpuCfsPeriod(), CoreMatchers.is(true));
        // not available in this dump
        MatcherAssert.assertThat(info.getCpuCfsQuota(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getDiscoveryBackend(), Matchers.nullValue());
        MatcherAssert.assertThat(info.getOomScoreAdj(), Matchers.nullValue());
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.notNullValue());
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.hasSize(4));
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.equalTo(driverStatus));
        MatcherAssert.assertThat(info.getNGoroutines(), CoreMatchers.is(40));
        MatcherAssert.assertThat(info.getSystemStatus(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(info.getPlugins(), Matchers.equalTo(plugins));
        MatcherAssert.assertThat(info.getPlugins(), Matchers.hasEntry("Volume", Collections.singletonList("local")));
        MatcherAssert.assertThat(info.getPlugins(), Matchers.hasEntry("Authorization", null));
        MatcherAssert.assertThat(info.getExperimentalBuild(), CoreMatchers.is(false));
        MatcherAssert.assertThat(info.getHttpProxy(), Matchers.isEmptyString());
        MatcherAssert.assertThat(info.getHttpsProxy(), Matchers.isEmptyString());
        MatcherAssert.assertThat(info.getNoProxy(), Matchers.isEmptyString());
        MatcherAssert.assertThat(info.getOomKillDisable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getOsType(), Matchers.equalTo("linux"));
        final InfoRegistryConfig registryConfig = info.getRegistryConfig();
        MatcherAssert.assertThat(registryConfig, Matchers.notNullValue());
        final List<String> cidRs = registryConfig.getInsecureRegistryCIDRs();
        MatcherAssert.assertThat(cidRs, Matchers.notNullValue());
        MatcherAssert.assertThat(cidRs, Matchers.contains("127.0.0.0/8"));
        final Map<String, IndexConfig> indexConfigs = registryConfig.getIndexConfigs();
        MatcherAssert.assertThat(indexConfigs, Matchers.notNullValue());
        final IndexConfig indexConfig = new IndexConfig().withMirrors(null).withName("docker.io").withSecure(true).withOfficial(true);
        MatcherAssert.assertThat(indexConfigs, Matchers.hasEntry("docker.io", indexConfig));
        MatcherAssert.assertThat(registryConfig.getMirrors(), Matchers.nullValue());
        MatcherAssert.assertThat(info.getSystemTime(), CoreMatchers.is("2016-02-17T14:56:35.212841831Z"));
        MatcherAssert.assertThat(info.getServerVersion(), CoreMatchers.is("1.10.1"));
        MatcherAssert.assertThat(info.getCpuSet(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getCpuShares(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getIPv4Forwarding(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getBridgeNfIptables(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getBridgeNfIp6tables(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getDebug(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getNFd(), CoreMatchers.is(24));
        MatcherAssert.assertThat(info.getOomKillDisable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getLoggingDriver(), CoreMatchers.is("json-file"));
        MatcherAssert.assertThat(info.getOperatingSystem(), CoreMatchers.is("Boot2Docker 1.10.1 (TCL 6.4.1); master : b03e158 - Thu Feb 11 22:34:01 UTC 2016"));
        MatcherAssert.assertThat(info.getClusterStore(), CoreMatchers.is(""));
        final Info withInfo = // shredinger-fields
        new Info().withArchitecture("x86_64").withContainers(2).withContainersRunning(2).withContainersPaused(10).withContainersStopped(3).withImages(13).withId("HLN2:5SBU:SRQR:CQI6:AB52:LZZ2:DED5:REDM:BU73:JFHE:R37A:5HMX").withDriver("aufs").withDriverStatuses(driverStatus).withSystemStatus(null).withPlugins(plugins).withMemoryLimit(true).withSwapLimit(true).withCpuCfsPeriod(true).withCpuCfsQuota(true).withCpuShares(true).withCpuSet(true).withIPv4Forwarding(true).withBridgeNfIptables(true).withBridgeNfIp6tables(true).withDebug(true).withNFd(24).withOomKillDisable(true).withNGoroutines(40).withSystemTime("2016-02-17T14:56:35.212841831Z").withExecutionDriver("native-0.2").withLoggingDriver("json-file").withNEventsListener(0).withKernelVersion("4.1.17-boot2docker").withOperatingSystem("Boot2Docker 1.10.1 (TCL 6.4.1); master : b03e158 - Thu Feb 11 22:34:01 UTC 2016").withOsType("linux").withIndexServerAddress("https://index.docker.io/v1/").withRegistryConfig(registryConfig).withInitSha1("").withInitPath("/usr/local/bin/docker").withNCPU(1).withMemTotal(1044574208L).withDockerRootDir("/mnt/sda1/var/lib/docker").withHttpProxy("").withHttpsProxy("").withNoProxy("").withName("docker-java").withLabels(new String[]{ "provider=virtualbox" }).withExperimentalBuild(false).withServerVersion("1.10.1").withClusterStore("").withClusterAdvertise("").withDiscoveryBackend(null).withOomScoreAdj(null).withSockets(null);
        MatcherAssert.assertThat(info, CoreMatchers.is(withInfo));
    }

    @Test
    public void serder2Json() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Info.class);
        final Info info = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_22, "info/2.json", type);
        final List<List<String>> driverStatus = Arrays.asList(Arrays.asList("Pool Name", "docker-253:2-17567992-pool"), Arrays.asList("Pool Blocksize", "65.54 kB"), Arrays.asList("Base Device Size", "107.4 GB"), Arrays.asList("Backing Filesystem", "ext4"), Arrays.asList("Data file", "/dev/loop0"), Arrays.asList("Metadata file", "/dev/loop1"), Arrays.asList("Data Space Used", "3.89 GB"), Arrays.asList("Data Space Total", "107.4 GB"), Arrays.asList("Data Space Available", "103.5 GB"), Arrays.asList("Metadata Space Used", "5.46 MB"), Arrays.asList("Metadata Space Total", "2.147 GB"), Arrays.asList("Metadata Space Available", "2.142 GB"), Arrays.asList("Udev Sync Supported", "true"), Arrays.asList("Deferred Removal Enabled", "false"), Arrays.asList("Deferred Deletion Enabled", "false"), Arrays.asList("Deferred Deleted Device Count", "0"), Arrays.asList("Data loop file", "/var/lib/docker/devicemapper/devicemapper/data"), Arrays.asList("Metadata loop file", "/var/lib/docker/devicemapper/devicemapper/metadata"), Arrays.asList("Library Version", "1.02.107-RHEL7 (2015-12-01)"));
        final Map<String, List<String>> plugins = new LinkedHashMap<>();
        plugins.put("Volume", Collections.singletonList("local"));
        plugins.put("Network", Arrays.asList("null", "host", "bridge"));
        plugins.put("Authorization", null);
        MatcherAssert.assertThat(info, Matchers.notNullValue());
        MatcherAssert.assertThat(info.getArchitecture(), Matchers.equalTo("x86_64"));
        MatcherAssert.assertThat(info.getContainersStopped(), CoreMatchers.is(2));
        MatcherAssert.assertThat(info.getContainersPaused(), CoreMatchers.is(0));
        MatcherAssert.assertThat(info.getContainersRunning(), CoreMatchers.is(0));
        MatcherAssert.assertThat(info.getCpuCfsPeriod(), CoreMatchers.is(true));
        // not available in this dump
        MatcherAssert.assertThat(info.getCpuCfsQuota(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getDiscoveryBackend(), Matchers.nullValue());
        MatcherAssert.assertThat(info.getOomScoreAdj(), Matchers.nullValue());
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.notNullValue());
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.hasSize(19));
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.equalTo(driverStatus));
        MatcherAssert.assertThat(info.getNGoroutines(), CoreMatchers.is(30));
        MatcherAssert.assertThat(info.getSystemStatus(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(info.getPlugins(), Matchers.equalTo(plugins));
        MatcherAssert.assertThat(info.getPlugins(), Matchers.hasEntry("Volume", Collections.singletonList("local")));
        MatcherAssert.assertThat(info.getPlugins(), Matchers.hasEntry("Authorization", null));
        MatcherAssert.assertThat(info.getExperimentalBuild(), CoreMatchers.is(false));
        MatcherAssert.assertThat(info.getHttpProxy(), Matchers.isEmptyString());
        MatcherAssert.assertThat(info.getHttpsProxy(), Matchers.isEmptyString());
        MatcherAssert.assertThat(info.getNoProxy(), Matchers.isEmptyString());
        MatcherAssert.assertThat(info.getOomKillDisable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getOsType(), Matchers.equalTo("linux"));
        final InfoRegistryConfig registryConfig = info.getRegistryConfig();
        MatcherAssert.assertThat(registryConfig, Matchers.notNullValue());
        final List<String> cidRs = registryConfig.getInsecureRegistryCIDRs();
        MatcherAssert.assertThat(cidRs, Matchers.notNullValue());
        MatcherAssert.assertThat(cidRs, Matchers.contains("127.0.0.0/8"));
        final Map<String, IndexConfig> indexConfigs = registryConfig.getIndexConfigs();
        MatcherAssert.assertThat(indexConfigs, Matchers.notNullValue());
        final IndexConfig indexConfig = new IndexConfig().withMirrors(null).withName("docker.io").withSecure(true).withOfficial(true);
        MatcherAssert.assertThat(indexConfigs, Matchers.hasEntry("docker.io", indexConfig));
        final IndexConfig indexConfig2 = new IndexConfig().withMirrors(Collections.<String>emptyList()).withName("somehost:80").withSecure(false).withOfficial(false);
        MatcherAssert.assertThat(indexConfigs, Matchers.hasEntry("somehost:80", indexConfig2));
        MatcherAssert.assertThat(registryConfig.getMirrors(), Matchers.nullValue());
        MatcherAssert.assertThat(info.getSystemTime(), CoreMatchers.is("2016-03-20T17:32:06.598846244+01:00"));
        MatcherAssert.assertThat(info.getServerVersion(), CoreMatchers.is("1.10.2"));
        MatcherAssert.assertThat(info.getCpuSet(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getCpuShares(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getIPv4Forwarding(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getBridgeNfIptables(), CoreMatchers.is(false));
        MatcherAssert.assertThat(info.getBridgeNfIp6tables(), CoreMatchers.is(false));
        MatcherAssert.assertThat(info.getDebug(), CoreMatchers.is(false));
        MatcherAssert.assertThat(info.getNFd(), CoreMatchers.is(13));
        MatcherAssert.assertThat(info.getOomKillDisable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(info.getLoggingDriver(), CoreMatchers.is("json-file"));
        MatcherAssert.assertThat(info.getOperatingSystem(), CoreMatchers.is("Red Hat Enterprise Linux Workstation 7.2 (Maipo)"));
        MatcherAssert.assertThat(info.getClusterStore(), CoreMatchers.is(""));
        final Info withInfo = // shredinger-fields
        new Info().withArchitecture("x86_64").withContainers(2).withContainersRunning(0).withContainersPaused(0).withContainersStopped(2).withImages(55).withId("H52J:52LG:YP4W:EHKY:SRK5:RYG6:ETWR:7AR3:MTFJ:PC6C:4YF2:NTN2").withDriver("devicemapper").withDriverStatuses(driverStatus).withSystemStatus(null).withPlugins(plugins).withMemoryLimit(true).withSwapLimit(true).withCpuCfsPeriod(true).withCpuCfsQuota(true).withCpuShares(true).withCpuSet(true).withIPv4Forwarding(true).withBridgeNfIptables(false).withBridgeNfIp6tables(false).withDebug(false).withNFd(13).withOomKillDisable(true).withNGoroutines(30).withSystemTime("2016-03-20T17:32:06.598846244+01:00").withExecutionDriver("native-0.2").withLoggingDriver("json-file").withNEventsListener(0).withKernelVersion("3.10.0-327.10.1.el7.x86_64").withOperatingSystem("Red Hat Enterprise Linux Workstation 7.2 (Maipo)").withOsType("linux").withIndexServerAddress("https://index.docker.io/v1/").withRegistryConfig(registryConfig).withInitSha1("672d65f3cf8816fbda421afeed7e52c0ca17d5e7").withInitPath("/usr/libexec/docker/dockerinit").withNCPU(8).withMemTotal(33350918144L).withDockerRootDir("/var/lib/docker").withHttpProxy("").withHttpsProxy("").withNoProxy("").withName("somename").withLabels(null).withExperimentalBuild(false).withServerVersion("1.10.2").withClusterStore("").withClusterAdvertise("").withDiscoveryBackend(null).withOomScoreAdj(null).withSockets(null);
        MatcherAssert.assertThat(info, CoreMatchers.is(withInfo));
    }

    @Test
    public void info_1_38() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Info.class);
        final Info info = JSONSamples.testRoundTrip(VERSION_1_38, "info/lcow.json", type);
        MatcherAssert.assertThat(info, Matchers.notNullValue());
        MatcherAssert.assertThat(info.getArchitecture(), CoreMatchers.is("x86_64"));
        MatcherAssert.assertThat(info.getDockerRootDir(), CoreMatchers.is("C:\\ProgramData\\Docker"));
        MatcherAssert.assertThat(info.getDriver(), CoreMatchers.is("windowsfilter (windows) lcow (linux)"));
        MatcherAssert.assertThat(info.getDriverStatuses(), Matchers.equalTo(Arrays.asList(Arrays.asList("Windows", ""), Arrays.asList("LCOW", ""))));
        MatcherAssert.assertThat(info.getIsolation(), CoreMatchers.is("hyperv"));
        MatcherAssert.assertThat(info.getKernelVersion(), CoreMatchers.is("10.0 17134 (17134.1.amd64fre.rs4_release.180410-1804)"));
        MatcherAssert.assertThat(info.getOsType(), CoreMatchers.is("windows"));
        MatcherAssert.assertThat(info.getOperatingSystem(), CoreMatchers.is("Windows 10 Pro Version 1803 (OS Build 17134.228)"));
        final Map<String, List<String>> plugins = new LinkedHashMap<>();
        plugins.put("Authorization", null);
        plugins.put("Log", Arrays.asList("awslogs", "etwlogs", "fluentd", "gelf", "json-file", "logentries", "splunk", "syslog"));
        plugins.put("Network", Arrays.asList("ics", "l2bridge", "l2tunnel", "nat", "null", "overlay", "transparent"));
        plugins.put("Volume", Collections.singletonList("local"));
        MatcherAssert.assertThat(info.getPlugins(), Matchers.equalTo(plugins));
        MatcherAssert.assertThat(info.getServerVersion(), CoreMatchers.is("18.06.1-ce"));
    }
}

