package com.github.dockerjava.cmd;


import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.utils.TestResources;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import net.jcip.annotations.NotThreadSafe;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Test;


@NotThreadSafe
public class LoadImageCmdIT extends CmdIT {
    private String expectedImageId;

    @Test
    public void loadImageFromTar() throws Exception {
        try (InputStream uploadStream = Files.newInputStream(TestResources.getApiImagesLoadTestTarball())) {
            dockerRule.getClient().loadImageCmd(uploadStream).exec();
        }
        // swarm needs some time to refelct new images
        synchronized(this) {
            wait(5000);
        }
        final Image image = findImageWithId(expectedImageId, dockerRule.getClient().listImagesCmd().exec());
        MatcherAssert.assertThat("Can't find expected image after loading from a tar archive!", image, IsNull.notNullValue());
        MatcherAssert.assertThat("Image after loading from a tar archive has wrong tags!", Arrays.asList(image.getRepoTags()), IsEqual.equalTo(Collections.singletonList("docker-java/load:1.0")));
    }
}

