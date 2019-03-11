package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class InspectVolumeCmdIT extends CmdIT {
    @Test
    public void inspectVolume() throws DockerException {
        String volumeName = "volume1";
        dockerRule.getClient().createVolumeCmd().withName(volumeName).withDriver("local").exec();
        InspectVolumeResponse inspectVolumeResponse = dockerRule.getClient().inspectVolumeCmd(volumeName).exec();
        MatcherAssert.assertThat(inspectVolumeResponse.getName(), Matchers.equalTo("volume1"));
        MatcherAssert.assertThat(inspectVolumeResponse.getDriver(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(inspectVolumeResponse.getMountpoint(), Matchers.containsString("/volume1/"));
    }

    @Test(expected = NotFoundException.class)
    public void inspectNonExistentVolume() throws DockerException {
        String volumeName = "non-existing";
        dockerRule.getClient().inspectVolumeCmd(volumeName).exec();
    }
}

