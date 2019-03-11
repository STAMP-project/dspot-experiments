package com.github.sarxos.webcam;


import WebcamResolution.VGA;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class WebcamResolutionTest {
    @Test
    public void test_getSize() {
        Assertions.assertThat(VGA.getSize()).isNotNull();
        Assertions.assertThat(VGA.getSize().getWidth()).isEqualTo(640);
        Assertions.assertThat(VGA.getSize().getHeight()).isEqualTo(480);
    }

    @Test
    public void test_getPixelCount() {
        Assertions.assertThat(VGA.getPixelsCount()).isEqualTo((640 * 480));
    }

    @Test
    public void test_getAspectRatio() {
        Assertions.assertThat(VGA.getAspectRatio()).isNotNull();
        Assertions.assertThat(VGA.getAspectRatio().getWidth()).isEqualTo(4);
        Assertions.assertThat(VGA.getAspectRatio().getHeight()).isEqualTo(3);
    }

    @Test
    public void test_getWidth() {
        Assertions.assertThat(VGA.getWidth()).isEqualTo(640);
    }

    @Test
    public void test_getHeight() {
        Assertions.assertThat(VGA.getHeight()).isEqualTo(480);
    }

    @Test
    public void test_toString() {
        Assertions.assertThat(VGA.toString()).isEqualTo("VGA 640x480 (4:3)");
    }
}

