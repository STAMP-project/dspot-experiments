package com.github.dockerjava.cmd;


import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SaveImageCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(SaveImageCmdIT.class);

    @Test
    public void saveImage() throws Exception {
        InputStream image = IOUtils.toBufferedInputStream(dockerRule.getClient().saveImageCmd("busybox").exec());
        MatcherAssert.assertThat(image.available(), Matchers.greaterThan(0));
        InputStream image2 = IOUtils.toBufferedInputStream(dockerRule.getClient().saveImageCmd("busybox").withTag("latest").exec());
        MatcherAssert.assertThat(image2.available(), Matchers.greaterThan(0));
    }
}

