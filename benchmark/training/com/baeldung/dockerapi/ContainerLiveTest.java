package com.baeldung.dockerapi;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PortBinding;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ContainerLiveTest {
    private static DockerClient dockerClient;

    @Test
    public void whenListingRunningContainers_thenReturnNonEmptyList() {
        // when
        List<Container> containers = ContainerLiveTest.dockerClient.listContainersCmd().exec();
        // then
        MatcherAssert.assertThat(containers.size(), Is.is(Matchers.not(0)));
    }

    @Test
    public void whenListingExitedContainers_thenReturnNonEmptyList() {
        // when
        List<Container> containers = ContainerLiveTest.dockerClient.listContainersCmd().withShowSize(true).withShowAll(true).withStatusFilter("exited").exec();
        // then
        MatcherAssert.assertThat(containers.size(), Is.is(Matchers.not(0)));
    }

    @Test
    public void whenCreatingContainer_thenMustReturnContainerId() {
        // when
        CreateContainerResponse container = ContainerLiveTest.dockerClient.createContainerCmd("mongo:3.6").withCmd("--bind_ip_all").withName("mongo").withHostName("baeldung").withEnv("MONGO_LATEST_VERSION=3.6").withPortBindings(PortBinding.parse("9999:27017")).exec();
        // then
        MatcherAssert.assertThat(container.getId(), Is.is(Matchers.not(null)));
    }

    @Test
    public void whenHavingContainer_thenRunContainer() throws InterruptedException {
        // when
        CreateContainerResponse container = ContainerLiveTest.dockerClient.createContainerCmd("alpine:3.6").withCmd("sleep", "10000").exec();
        Thread.sleep(3000);
        // then
        ContainerLiveTest.dockerClient.startContainerCmd(container.getId()).exec();
        ContainerLiveTest.dockerClient.stopContainerCmd(container.getId()).exec();
    }

    @Test
    public void whenRunningContainer_thenStopContainer() throws InterruptedException {
        // when
        CreateContainerResponse container = ContainerLiveTest.dockerClient.createContainerCmd("alpine:3.6").withCmd("sleep", "10000").exec();
        Thread.sleep(3000);
        ContainerLiveTest.dockerClient.startContainerCmd(container.getId()).exec();
        // then
        ContainerLiveTest.dockerClient.stopContainerCmd(container.getId()).exec();
    }

    @Test
    public void whenRunningContainer_thenKillContainer() throws InterruptedException {
        // when
        CreateContainerResponse container = ContainerLiveTest.dockerClient.createContainerCmd("alpine:3.6").withCmd("sleep", "10000").exec();
        ContainerLiveTest.dockerClient.startContainerCmd(container.getId()).exec();
        Thread.sleep(3000);
        ContainerLiveTest.dockerClient.stopContainerCmd(container.getId()).exec();
        // then
        ContainerLiveTest.dockerClient.killContainerCmd(container.getId()).exec();
    }

    @Test
    public void whenHavingContainer_thenInspectContainer() {
        // when
        CreateContainerResponse container = ContainerLiveTest.dockerClient.createContainerCmd("alpine:3.6").withCmd("sleep", "10000").exec();
        // then
        InspectContainerResponse containerResponse = ContainerLiveTest.dockerClient.inspectContainerCmd(container.getId()).exec();
        MatcherAssert.assertThat(containerResponse.getId(), Is.is(container.getId()));
    }

    @Test
    public void givenContainer_whenCommittingContainer_thenMustReturnImageId() {
        // given
        CreateContainerResponse container = ContainerLiveTest.dockerClient.createContainerCmd("alpine:3.6").withCmd("sleep", "10000").exec();
        // when
        String imageId = ContainerLiveTest.dockerClient.commitCmd(container.getId()).withEnv("SNAPSHOT_YEAR=2018").withMessage("add git support").withCmd("sleep", "10000").withRepository("alpine").withTag("3.6.v2").exec();
        // then
        MatcherAssert.assertThat(imageId, Is.is(Matchers.not(null)));
    }
}

