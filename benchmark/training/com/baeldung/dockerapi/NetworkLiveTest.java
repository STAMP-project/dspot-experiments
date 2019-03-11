package com.baeldung.dockerapi;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Network.Ipam;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class NetworkLiveTest {
    private static DockerClient dockerClient;

    @Test
    public void whenCreatingNetwork_thenRetrieveResponse() {
        // when
        CreateNetworkResponse networkResponse = NetworkLiveTest.dockerClient.createNetworkCmd().withName("baeldungDefault").withDriver("bridge").exec();
        // then
        MatcherAssert.assertThat(networkResponse, Is.is(Matchers.not(null)));
    }

    @Test
    public void whenCreatingAdvanceNetwork_thenRetrieveResponse() {
        // when
        CreateNetworkResponse networkResponse = NetworkLiveTest.dockerClient.createNetworkCmd().withName("baeldungAdvanced").withIpam(new Ipam().withConfig(new Ipam.Config().withSubnet("172.36.0.0/16").withIpRange("172.36.5.0/24"))).withDriver("bridge").exec();
        // then
        MatcherAssert.assertThat(networkResponse, Is.is(Matchers.not(null)));
    }

    @Test
    public void whenInspectingNetwork_thenSizeMustBeGreaterThanZero() {
        // when
        String networkName = "bridge";
        Network network = NetworkLiveTest.dockerClient.inspectNetworkCmd().withNetworkId(networkName).exec();
        // then
        MatcherAssert.assertThat(network.getName(), Is.is(networkName));
    }

    @Test
    public void whenCreatingNetwork_thenRemove() throws InterruptedException {
        // when
        CreateNetworkResponse networkResponse = NetworkLiveTest.dockerClient.createNetworkCmd().withName("baeldungDefault").withDriver("bridge").exec();
        // then
        Thread.sleep(4000);
        NetworkLiveTest.dockerClient.removeNetworkCmd(networkResponse.getId()).exec();
    }
}

