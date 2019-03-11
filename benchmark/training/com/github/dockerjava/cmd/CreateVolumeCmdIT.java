package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.exception.DockerException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CreateVolumeCmdIT extends CmdIT {
    @Test
    public void createVolume() throws DockerException {
        String volumeName = "volume1";
        CreateVolumeResponse createVolumeResponse = dockerRule.getClient().createVolumeCmd().withName(volumeName).withDriver("local").exec();
        MatcherAssert.assertThat(createVolumeResponse.getName(), Matchers.equalTo(volumeName));
        MatcherAssert.assertThat(createVolumeResponse.getDriver(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(createVolumeResponse.getMountpoint(), Matchers.containsString("/volume1/"));
    }

    @Test
    public void createVolumeWithExistingName() throws DockerException {
        String volumeName = "volume1";
        CreateVolumeResponse createVolumeResponse1 = dockerRule.getClient().createVolumeCmd().withName(volumeName).withDriver("local").exec();
        MatcherAssert.assertThat(createVolumeResponse1.getName(), Matchers.equalTo(volumeName));
        MatcherAssert.assertThat(createVolumeResponse1.getDriver(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(createVolumeResponse1.getMountpoint(), Matchers.containsString("/volume1/"));
        CreateVolumeResponse createVolumeResponse2 = dockerRule.getClient().createVolumeCmd().withName(volumeName).withDriver("local").exec();
        MatcherAssert.assertThat(createVolumeResponse2.getName(), Matchers.equalTo(volumeName));
        MatcherAssert.assertThat(createVolumeResponse2.getDriver(), Matchers.equalTo("local"));
        MatcherAssert.assertThat(createVolumeResponse2.getMountpoint(), Matchers.equalTo(createVolumeResponse1.getMountpoint()));
    }
}

