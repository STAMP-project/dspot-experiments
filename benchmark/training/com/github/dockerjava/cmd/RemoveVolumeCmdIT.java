package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RemoveVolumeCmdIT extends CmdIT {
    @Test(expected = NotFoundException.class)
    public void removeVolume() throws DockerException {
        String volumeName = "volume1" + (dockerRule.getKind());
        CreateVolumeResponse createVolumeResponse = dockerRule.getClient().createVolumeCmd().withName(volumeName).withDriver("local").exec();
        MatcherAssert.assertThat(createVolumeResponse.getName(), Matchers.equalTo(volumeName));
        MatcherAssert.assertThat(createVolumeResponse.getDriver(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(createVolumeResponse.getMountpoint(), Matchers.containsString(volumeName));
        dockerRule.getClient().removeVolumeCmd(volumeName).exec();
        dockerRule.getClient().inspectVolumeCmd(volumeName).exec();
    }
}

