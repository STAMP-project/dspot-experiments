package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.UpdateContainerResponse;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.junit.DockerMatchers;
import com.github.dockerjava.junit.DockerRule;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class UpdateContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(UpdateContainerCmdIT.class);

    @Test
    public void updateContainer() throws DockerException, IOException {
        Assume.assumeThat("API version should be >= 1.22", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_22));
        CreateContainerResponse response = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withCmd("sleep", "9999").exec();
        String containerId = response.getId();
        dockerRule.getClient().startContainerCmd(containerId).exec();
        InspectContainerResponse inspectBefore = dockerRule.getClient().inspectContainerCmd(containerId).exec();
        UpdateContainerCmdIT.LOG.debug("Inspect: {}", inspectBefore);
        final Long memory = inspectBefore.getHostConfig().getMemory();
        final UpdateContainerResponse updateResponse = // .withMemory(209715200L + 2L)
        // .withMemorySwap(514288000L) Your kernel does not support swap limit capabilities, memory limited without swap.
        // .withMemoryReservation(209715200L)
        // .withKernelMemory(52428800) Can not update kernel memory to a running container, please stop it first.
        // .withCpusetCpus("0") // depends on env
        dockerRule.getClient().updateContainerCmd(containerId).withBlkioWeight(300).withCpuShares(512).withCpuPeriod(100000).withCpuQuota(50000).withCpusetMems("0").exec();
        // true only on docker toolbox (1.10.1)
        // assertThat(updateResponse.getWarnings(), hasSize(1));
        // assertThat(updateResponse.getWarnings().get(0),
        // is("Your kernel does not support Block I/O weight. Weight discarded."));
        InspectContainerResponse inspectAfter = dockerRule.getClient().inspectContainerCmd(containerId).exec();
        final HostConfig afterHostConfig = inspectAfter.getHostConfig();
        // assertThat(afterHostConfig.getMemory(), is(209715200L + 2L));
        // assertThat(afterHostConfig.getBlkioWeight(), is(300));
        MatcherAssert.assertThat(afterHostConfig.getCpuShares(), CoreMatchers.is(512));
        MatcherAssert.assertThat(afterHostConfig.getCpuPeriod(), CoreMatchers.is(100000L));
        MatcherAssert.assertThat(afterHostConfig.getCpuQuota(), CoreMatchers.is(50000L));
        MatcherAssert.assertThat(afterHostConfig.getCpusetMems(), CoreMatchers.is("0"));
        // assertThat(afterHostConfig.getMemoryReservation(), is(209715200L));
        // assertThat(afterHostConfig.getMemorySwap(), is(514288000L));
    }
}

