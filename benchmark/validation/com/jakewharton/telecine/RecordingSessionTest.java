package com.jakewharton.telecine;


import org.junit.Test;


public final class RecordingSessionTest {
    @Test
    public void videoSizeNoCamera() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1080, 1920, 160, false, (-1), (-1), 30, 100);
        assertThat(size.width).isEqualTo(1080);
        assertThat(size.height).isEqualTo(1920);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeResize() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1080, 1920, 160, false, (-1), (-1), 30, 75);
        assertThat(size.width).isEqualTo(810);
        assertThat(size.height).isEqualTo(1440);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeFitsInCamera() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1080, 1920, 160, false, 1920, 1080, 30, 100);
        assertThat(size.width).isEqualTo(1080);
        assertThat(size.height).isEqualTo(1920);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeFitsInCameraLandscape() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1920, 1080, 160, true, 1920, 1080, 30, 100);
        assertThat(size.width).isEqualTo(1920);
        assertThat(size.height).isEqualTo(1080);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeLargerThanCamera() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(2160, 3840, 160, false, 1920, 1080, 30, 100);
        assertThat(size.width).isEqualTo(1080);
        assertThat(size.height).isEqualTo(1920);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeLargerThanCameraLandscape() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(3840, 2160, 160, true, 1920, 1080, 30, 100);
        assertThat(size.width).isEqualTo(1920);
        assertThat(size.height).isEqualTo(1080);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeLargerThanCameraScaling() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1200, 1920, 160, false, 1920, 1080, 30, 100);
        assertThat(size.width).isEqualTo(1080);
        assertThat(size.height).isEqualTo(1728);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeLargerThanCameraScalingResizesFirst() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1200, 1920, 160, false, 1920, 1080, 30, 75);
        assertThat(size.width).isEqualTo(900);
        assertThat(size.height).isEqualTo(1440);
        assertThat(size.density).isEqualTo(160);
    }

    @Test
    public void videoSizeLargerThanCameraScalingLandscape() {
        RecordingSession.RecordingInfo size = RecordingSession.calculateRecordingInfo(1920, 1200, 160, true, 1920, 1080, 30, 100);
        assertThat(size.width).isEqualTo(1728);
        assertThat(size.height).isEqualTo(1080);
        assertThat(size.density).isEqualTo(160);
    }
}

