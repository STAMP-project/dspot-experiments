package com.github.dockerjava.api.command;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class InspectImageResponseTest {
    @Test
    public void serder1_22Json() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(InspectImageResponse.class);
        final InspectImageResponse inspectImage = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_22, "images/image1/inspect1.json", type);
        final ContainerConfig config = new ContainerConfig().withAttachStderr(false).withAttachStdin(false).withAttachStdout(false).withCmd(null).withDomainName("").withEntrypoint(null).withEnv(new String[]{ "HOME=/", "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" }).withExposedPorts(null).withHostName("aee9ba801acc").withImage("511136ea3c5a64f264b78b5433614aec563103b4d4702f3ba7d4d2698e22c158").withLabels(null).withMacAddress(null).withNetworkDisabled(null).withOnBuild(new String[]{  }).withStdinOpen(false).withPortSpecs(null).withStdInOnce(false).withTty(false).withUser("").withVolumes(null).withWorkingDir("");
        final ContainerConfig containerConfig = new ContainerConfig().withAttachStderr(false).withAttachStdin(false).withAttachStdout(false).withCmd(new String[]{ "/bin/sh", "-c", "#(nop) MAINTAINER hack@worldticket.net" }).withDomainName("").withEntrypoint(null).withEnv(new String[]{ "HOME=/", "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" }).withExposedPorts(null).withHostName("aee9ba801acc").withImage("511136ea3c5a64f264b78b5433614aec563103b4d4702f3ba7d4d2698e22c158").withLabels(null).withMacAddress(null).withNetworkDisabled(null).withOnBuild(new String[]{  }).withStdinOpen(false).withPortSpecs(null).withStdInOnce(false).withTty(false).withUser("").withVolumes(null).withWorkingDir("");
        MatcherAssert.assertThat(inspectImage, Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getArch(), Matchers.is("amd64"));
        MatcherAssert.assertThat(inspectImage.getAuthor(), Matchers.is("hack@worldticket.net"));
        MatcherAssert.assertThat(inspectImage.getComment(), Matchers.isEmptyString());
        MatcherAssert.assertThat(inspectImage.getConfig(), Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getConfig(), Matchers.equalTo(config));
        MatcherAssert.assertThat(inspectImage.getCreated(), Matchers.is("2014-04-29T19:59:10.84997669Z"));
        MatcherAssert.assertThat(inspectImage.getContainer(), Matchers.is("aee9ba801acca0e648ffd91df204ba82ae85d97608a4864a019e2004d7e1b133"));
        MatcherAssert.assertThat(inspectImage.getContainerConfig(), Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getContainerConfig(), Matchers.equalTo(containerConfig));
        MatcherAssert.assertThat(inspectImage.getDockerVersion(), Matchers.is("0.8.1"));
        MatcherAssert.assertThat(inspectImage.getId(), Matchers.is("sha256:ee45fe0d1fcdf1a0f9c2d1e36c6f4b3202bbb2032f14d7c9312b27bfcf6aee24"));
        MatcherAssert.assertThat(inspectImage.getOs(), Matchers.is("linux"));
        MatcherAssert.assertThat(inspectImage.getParent(), Matchers.isEmptyString());
        MatcherAssert.assertThat(inspectImage.getSize(), Matchers.is(0L));
        MatcherAssert.assertThat(inspectImage.getRepoTags(), Matchers.hasSize(1));
        MatcherAssert.assertThat(inspectImage.getRepoTags(), Matchers.hasItem("hackmann/empty:latest"));
        final GraphDriver aufsGraphDriver = new GraphDriver().withName("aufs");
        final GraphDriver graphDriver = inspectImage.getGraphDriver();
        MatcherAssert.assertThat(graphDriver, Matchers.notNullValue());
        MatcherAssert.assertThat(graphDriver, Matchers.equalTo(aufsGraphDriver));
        MatcherAssert.assertThat(graphDriver.getName(), Matchers.is("aufs"));
        MatcherAssert.assertThat(graphDriver.getData(), Matchers.nullValue());
        MatcherAssert.assertThat(inspectImage.getVirtualSize(), Matchers.is(0L));
        final InspectImageResponse inspectImageResponse = new InspectImageResponse().withArch("amd64").withAuthor("hack@worldticket.net").withComment("").withConfig(config).withContainer("aee9ba801acca0e648ffd91df204ba82ae85d97608a4864a019e2004d7e1b133").withContainerConfig(containerConfig).withCreated("2014-04-29T19:59:10.84997669Z").withDockerVersion("0.8.1").withId("sha256:ee45fe0d1fcdf1a0f9c2d1e36c6f4b3202bbb2032f14d7c9312b27bfcf6aee24").withOs("linux").withParent("").withSize(0L).withRepoTags(Collections.singletonList("hackmann/empty:latest")).withRepoDigests(Collections.<String>emptyList()).withVirtualSize(0L).withGraphDriver(aufsGraphDriver);
        MatcherAssert.assertThat(inspectImage, Matchers.equalTo(inspectImageResponse));
    }

    @Test
    public void serder1_22_doc() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(InspectImageResponse.class);
        final InspectImageResponse inspectImage = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_22, "images/docImage/doc.json", type);
        MatcherAssert.assertThat(inspectImage, Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getRepoDigests(), Matchers.hasSize(1));
        MatcherAssert.assertThat(inspectImage.getRepoDigests(), Matchers.contains(("localhost:5000/test/busybox/example@" + "sha256:cbbf2f9a99b47fc460d422812b6a5adff7dfee951d8fa2e4a98caa0382cfbdbf")));
        MatcherAssert.assertThat(inspectImage.getRepoTags(), Matchers.hasSize(3));
        MatcherAssert.assertThat(inspectImage.getRepoTags(), Matchers.containsInAnyOrder("example:1.0", "example:latest", "example:stable"));
    }

    @Test
    public void serder1_22_inspect_doc() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(InspectImageResponse.class);
        final InspectImageResponse inspectImage = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_22, "images/docImage/inspect_doc.json", type);
        GraphData newGraphData = new GraphData().withDeviceId("5").withDeviceName("docker-253:1-2763198-d2cc496561d6d520cbc0236b4ba88c362c446a7619992123f11c809cded25b47").withDeviceSize("171798691840");
        MatcherAssert.assertThat(inspectImage, Matchers.notNullValue());
        GraphDriver graphDriver = inspectImage.getGraphDriver();
        MatcherAssert.assertThat(graphDriver, Matchers.notNullValue());
        GraphData data = graphDriver.getData();
        MatcherAssert.assertThat(data, Matchers.is(newGraphData));
        MatcherAssert.assertThat(data.getDeviceId(), Matchers.is("5"));
        MatcherAssert.assertThat(data.getDeviceName(), Matchers.is("docker-253:1-2763198-d2cc496561d6d520cbc0236b4ba88c362c446a7619992123f11c809cded25b47"));
        MatcherAssert.assertThat(data.getDeviceSize(), Matchers.is("171798691840"));
    }

    @Test
    public void testOverlayNetworkRootDir() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(InspectImageResponse.class);
        final InspectImageResponse inspectImage = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_22, "images/overlay/inspectOverlay.json", type);
        final GraphData overlayGraphData = new GraphData().withRootDir("/var/lib/docker/overlay/7e8d362d6b78d47eafe4863fd129cbcada35dbd419d7188cc1dbf1233d505576/root");
        final GraphDriver overlayGraphDriver = new GraphDriver().withName("overlay").withData(overlayGraphData);
        final GraphDriver graphDriver = inspectImage.getGraphDriver();
        MatcherAssert.assertThat(graphDriver, Matchers.notNullValue());
        MatcherAssert.assertThat(graphDriver, Matchers.equalTo(overlayGraphDriver));
        MatcherAssert.assertThat(graphDriver.getName(), Matchers.is("overlay"));
        MatcherAssert.assertThat(graphDriver.getData(), Matchers.equalTo(overlayGraphData));
    }

    @Test
    public void inspectWindowsImage() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(InspectImageResponse.class);
        final InspectImageResponse inspectImage = JSONSamples.testRoundTrip(RemoteApiVersion.VERSION_1_25, "images/windowsImage/doc.json", type);
        MatcherAssert.assertThat(inspectImage, Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getRepoTags(), Matchers.hasSize(1));
        MatcherAssert.assertThat(inspectImage.getRepoTags(), Matchers.contains("microsoft/nanoserver:latest"));
        MatcherAssert.assertThat(inspectImage.getRepoDigests(), Matchers.hasSize(1));
        MatcherAssert.assertThat(inspectImage.getRepoDigests(), Matchers.contains(("microsoft/nanoserver@" + "sha256:aee7d4330fe3dc5987c808f647441c16ed2fa1c7d9c6ef49d6498e5c9860b50b")));
        MatcherAssert.assertThat(inspectImage.getConfig(), Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getConfig().getCmd(), Matchers.is(new String[]{ "c:\\windows\\system32\\cmd.exe" }));
        MatcherAssert.assertThat(inspectImage.getOs(), Matchers.is("windows"));
        MatcherAssert.assertThat(inspectImage.getOsVersion(), Matchers.is("10.0.14393"));
        MatcherAssert.assertThat(inspectImage.getSize(), Matchers.is(651862727L));
        MatcherAssert.assertThat(inspectImage.getVirtualSize(), Matchers.is(651862727L));
        MatcherAssert.assertThat(inspectImage.getGraphDriver(), Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getGraphDriver().getName(), Matchers.is("windowsfilter"));
        MatcherAssert.assertThat(inspectImage.getGraphDriver().getData(), Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getGraphDriver().getData().getDir(), Matchers.is(("C:\\control\\windowsfilter\\" + "6fe6a289b98276a6a5ca0345156ca61d7b38f3da6bb49ef95af1d0f1ac37e5bf")));
        MatcherAssert.assertThat(inspectImage.getRootFS(), Matchers.notNullValue());
        MatcherAssert.assertThat(inspectImage.getRootFS().getType(), Matchers.is("layers"));
        MatcherAssert.assertThat(inspectImage.getRootFS().getLayers(), Matchers.hasSize(1));
        MatcherAssert.assertThat(inspectImage.getRootFS().getLayers(), Matchers.contains("sha256:342d4e407550c52261edd20cd901b5ce438f0b1e940336de3978210612365063"));
    }
}

