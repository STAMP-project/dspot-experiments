package com.baeldung.dockerapi;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class VolumeLiveTest {
    private static DockerClient dockerClient;

    @Test
    public void whenListingVolumes_thenSizeMustBeGreaterThanZero() {
        // when
        ListVolumesResponse volumesResponse = VolumeLiveTest.dockerClient.listVolumesCmd().exec();
        // then
        List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();
        MatcherAssert.assertThat(volumes.size(), Is.is(Matchers.greaterThan(0)));
    }

    @Test
    public void givenVolumes_whenInspectingVolume_thenReturnNonNullResponse() {
        // given
        ListVolumesResponse volumesResponse = VolumeLiveTest.dockerClient.listVolumesCmd().exec();
        List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();
        InspectVolumeResponse volume = volumes.get(0);
        // when
        InspectVolumeResponse volumeResponse = VolumeLiveTest.dockerClient.inspectVolumeCmd(volume.getName()).exec();
        // then
        MatcherAssert.assertThat(volumeResponse, Is.is(Matchers.not(null)));
    }

    @Test
    public void whenCreatingUnnamedVolume_thenGetVolumeId() {
        // when
        CreateVolumeResponse unnamedVolume = VolumeLiveTest.dockerClient.createVolumeCmd().exec();
        // then
        MatcherAssert.assertThat(unnamedVolume.getName(), Is.is(Matchers.not(null)));
    }

    @Test
    public void whenCreatingNamedVolume_thenGetVolumeId() {
        // when
        CreateVolumeResponse namedVolume = VolumeLiveTest.dockerClient.createVolumeCmd().withName("myNamedVolume").exec();
        // then
        MatcherAssert.assertThat(namedVolume.getName(), Is.is(Matchers.not(null)));
    }

    @Test
    public void whenGettingNamedVolume_thenRemove() throws InterruptedException {
        // when
        CreateVolumeResponse namedVolume = VolumeLiveTest.dockerClient.createVolumeCmd().withName("anotherNamedVolume").exec();
        // then
        Thread.sleep(4000);
        VolumeLiveTest.dockerClient.removeVolumeCmd(namedVolume.getName()).exec();
    }
}

