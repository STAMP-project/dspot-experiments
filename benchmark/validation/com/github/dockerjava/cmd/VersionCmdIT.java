package com.github.dockerjava.cmd;


import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Version;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VersionCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(VersionCmdIT.class);

    @Test
    public void version() throws DockerException {
        Version version = dockerRule.getClient().versionCmd().exec();
        VersionCmdIT.LOG.info(version.toString());
        Assert.assertTrue(((version.getGoVersion().length()) > 0));
        Assert.assertTrue(((version.getVersion().length()) > 0));
        Assert.assertEquals(StringUtils.split(version.getVersion(), ".").length, 3);
    }
}

