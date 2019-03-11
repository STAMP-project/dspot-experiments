package com.github.dockerjava.cmd;


import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.utils.TestUtils;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListImagesCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(ListImagesCmdIT.class);

    @Test
    public void listImages() throws DockerException {
        List<Image> images = dockerRule.getClient().listImagesCmd().withShowAll(true).exec();
        MatcherAssert.assertThat(images, Matchers.notNullValue());
        ListImagesCmdIT.LOG.info("Images List: {}", images);
        Info info = dockerRule.getClient().infoCmd().exec();
        if (TestUtils.isNotSwarm(dockerRule.getClient())) {
            MatcherAssert.assertThat(images.size(), Matchers.equalTo(info.getImages()));
        }
        Image img = images.get(0);
        MatcherAssert.assertThat(img.getCreated(), Matchers.is(Matchers.greaterThan(0L)));
        MatcherAssert.assertThat(img.getVirtualSize(), Matchers.is(Matchers.greaterThan(0L)));
        MatcherAssert.assertThat(img.getId(), Matchers.not(Matchers.isEmptyString()));
        MatcherAssert.assertThat(img.getRepoTags(), Matchers.not(Matchers.emptyArray()));
    }

    @Test
    public void listImagesWithDanglingFilter() throws DockerException {
        String imageId = createDanglingImage();
        List<Image> images = dockerRule.getClient().listImagesCmd().withDanglingFilter(true).withShowAll(true).exec();
        MatcherAssert.assertThat(images, Matchers.notNullValue());
        ListImagesCmdIT.LOG.info("Images List: {}", images);
        MatcherAssert.assertThat(images.size(), Matchers.is(Matchers.greaterThan(0)));
        Boolean imageInFilteredList = isImageInFilteredList(images, imageId);
        Assert.assertTrue(imageInFilteredList);
    }
}

