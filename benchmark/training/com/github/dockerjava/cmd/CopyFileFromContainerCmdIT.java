package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.junit.DockerMatchers;
import com.github.dockerjava.utils.TestUtils;
import java.io.InputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CopyFileFromContainerCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(CopyFileFromContainerCmdIT.class);

    @Test
    public void copyFromContainer() throws Exception {
        Assume.assumeThat("Doesn't work since 1.24", dockerRule, Matchers.not(DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_24)));
        DockerAssume.assumeNotSwarm("", dockerRule);
        String containerName = "copyFileFromContainer" + (dockerRule.getKind());
        dockerRule.ensureContainerRemoved(containerName);
        // TODO extract this into a shared method
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withName(containerName).withCmd("touch", "/copyFileFromContainer").exec();
        CopyFileFromContainerCmdIT.LOG.info("Created container: {}", container);
        MatcherAssert.assertThat(container.getId(), Matchers.not(Matchers.isEmptyOrNullString()));
        dockerRule.getClient().startContainerCmd(container.getId()).exec();
        InputStream response = dockerRule.getClient().copyFileFromContainerCmd(container.getId(), "/copyFileFromContainer").exec();
        Boolean bytesAvailable = (response.available()) > 0;
        Assert.assertTrue("The file was not copied from the container.", bytesAvailable);
        // read the stream fully. Otherwise, the underlying stream will not be closed.
        String responseAsString = TestUtils.asString(response);
        Assert.assertNotNull(responseAsString);
        Assert.assertTrue(((responseAsString.length()) > 0));
    }

    @Test(expected = NotFoundException.class)
    public void copyFromNonExistingContainer() throws Exception {
        dockerRule.getClient().copyFileFromContainerCmd("non-existing", "/test").exec();
    }
}

