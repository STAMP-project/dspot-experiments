package com.baeldung.dockerapi;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ImageLiveTest {
    private static DockerClient dockerClient;

    @Test
    public void whenListingImages_thenReturnNonEmptyList() {
        // when
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().exec();
        // then
        MatcherAssert.assertThat(images.size(), Is.is(Matchers.not(0)));
    }

    @Test
    public void whenListingImagesWithIntermediateImages_thenReturnNonEmptyList() {
        // when
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().withShowAll(true).exec();
        // then
        MatcherAssert.assertThat(images.size(), Is.is(Matchers.not(0)));
    }

    @Test
    public void whenListingDanglingImages_thenReturnNonNullList() {
        // when
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().withDanglingFilter(true).exec();
        // then
        MatcherAssert.assertThat(images, Is.is(Matchers.not(null)));
    }

    @Test
    public void whenBuildingImage_thenMustReturnImageId() {
        // when
        String imageId = ImageLiveTest.dockerClient.buildImageCmd().withDockerfile(new File("src/test/resources/dockerapi/Dockerfile")).withPull(true).withNoCache(true).withTag("alpine:git").exec(new BuildImageResultCallback()).awaitImageId();
        // then
        MatcherAssert.assertThat(imageId, Is.is(Matchers.not(null)));
    }

    @Test
    public void givenListOfImages_whenInspectImage_thenMustReturnObject() {
        // given
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().exec();
        Image image = images.get(0);
        // when
        InspectImageResponse imageResponse = ImageLiveTest.dockerClient.inspectImageCmd(image.getId()).exec();
        // then
        MatcherAssert.assertThat(imageResponse.getId(), Is.is(image.getId()));
    }

    @Test
    public void givenListOfImages_whenTagImage_thenListMustIncrement() {
        // given
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().exec();
        Image image = images.get(0);
        // when
        ImageLiveTest.dockerClient.tagImageCmd(image.getId(), "baeldung/alpine", "3.6.v2").exec();
        // then
        List<Image> imagesNow = ImageLiveTest.dockerClient.listImagesCmd().exec();
        MatcherAssert.assertThat(imagesNow.size(), Is.is(Matchers.greaterThan(images.size())));
    }

    @Test
    public void whenPullingImage_thenImageListNotEmpty() throws InterruptedException {
        // when
        ImageLiveTest.dockerClient.pullImageCmd("alpine").withTag("latest").exec(new PullImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
        // then
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().exec();
        MatcherAssert.assertThat(images.size(), Is.is(Matchers.not(0)));
    }

    @Test
    public void whenRemovingImage_thenImageListDecrease() {
        // when
        List<Image> images = ImageLiveTest.dockerClient.listImagesCmd().exec();
        Image image = images.get(0);
        ImageLiveTest.dockerClient.removeImageCmd(image.getId()).exec();
        // then
        List<Image> imagesNow = ImageLiveTest.dockerClient.listImagesCmd().exec();
        MatcherAssert.assertThat(imagesNow.size(), Is.is(Matchers.lessThan(images.size())));
    }

    @Test
    public void whenSearchingImage_thenMustReturn25Items() {
        // when
        List<SearchItem> items = ImageLiveTest.dockerClient.searchImagesCmd("Java").exec();
        // then
        MatcherAssert.assertThat(items.size(), Is.is(25));
    }
}

