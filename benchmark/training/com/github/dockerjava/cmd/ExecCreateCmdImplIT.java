package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.junit.DockerRule;
import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecCreateCmdImplIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(ExecCreateCmdImplIT.class);

    @Test
    public void execCreateTest() {
        String containerName = "generated_" + (new SecureRandom().nextInt());
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd(DockerRule.DEFAULT_IMAGE).withUser("root").withCmd("top").withName(containerName).exec();
        ExecCreateCmdImplIT.LOG.info("Created container {}", container.toString());
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        ExecCreateCmdResponse execCreateCmdResponse = dockerRule.getClient().execCreateCmd(container.getId()).withCmd("touch", "file.log").exec();
        MatcherAssert.assertThat(execCreateCmdResponse.getId(), Matchers.not(Matchers.isEmptyString()));
    }
}

