package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;
import com.github.dockerjava.api.exception.DockerException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ListVolumesCmdIT extends CmdIT {
    @Test
    public void listVolumes() throws DockerException {
        CreateVolumeResponse createVolumeResponse = dockerRule.getClient().createVolumeCmd().withName("volume1").withDriver("local").exec();
        MatcherAssert.assertThat(createVolumeResponse.getName(), Matchers.equalTo("volume1"));
        MatcherAssert.assertThat(createVolumeResponse.getDriver(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(createVolumeResponse.getMountpoint(), Matchers.containsString("/volume1/"));
        ListVolumesResponse listVolumesResponse = dockerRule.getClient().listVolumesCmd().exec();
        MatcherAssert.assertThat(listVolumesResponse.getVolumes().size(), Matchers.greaterThanOrEqualTo(1));
    }
}

