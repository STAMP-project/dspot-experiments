package org.robolectric.shadows;


import ShadowVideoView.PAUSE;
import ShadowVideoView.RESUME;
import ShadowVideoView.START;
import ShadowVideoView.STOP;
import ShadowVideoView.SUSPEND;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.VideoView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowVideoViewTest {
    private VideoView view;

    @Test
    public void shouldSetOnPreparedListener() throws Exception {
        ShadowVideoViewTest.TestPreparedListener l = new ShadowVideoViewTest.TestPreparedListener();
        view.setOnPreparedListener(l);
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getOnPreparedListener()).isSameAs(l);
    }

    @Test
    public void shouldSetOnErrorListener() throws Exception {
        ShadowVideoViewTest.TestErrorListener l = new ShadowVideoViewTest.TestErrorListener();
        view.setOnErrorListener(l);
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getOnErrorListener()).isSameAs(l);
    }

    @Test
    public void shouldSetOnCompletionListener() throws Exception {
        ShadowVideoViewTest.TestCompletionListener l = new ShadowVideoViewTest.TestCompletionListener();
        view.setOnCompletionListener(l);
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getOnCompletionListener()).isSameAs(l);
    }

    @Test
    public void shouldSetVideoPath() throws Exception {
        view.setVideoPath("video.mp4");
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getVideoPath()).isEqualTo("video.mp4");
        view.setVideoPath(null);
        assertThat(shadowVideoView.getVideoPath()).isNull();
    }

    @Test
    public void shouldSetVideoURI() throws Exception {
        view.setVideoURI(Uri.parse("video.mp4"));
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getVideoURIString()).isEqualTo("video.mp4");
        view.setVideoURI(null);
        assertThat(shadowVideoView.getVideoURIString()).isNull();
    }

    @Test
    public void shouldSetVideoDuration() throws Exception {
        assertThat(view.getDuration()).isEqualTo(0);
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        shadowVideoView.setDuration(10);
        assertThat(view.getDuration()).isEqualTo(10);
    }

    @Test
    public void shouldDetermineIsPlaying() throws Exception {
        assertThat(view.isPlaying()).isFalse();
        view.start();
        assertThat(view.isPlaying()).isTrue();
        view.stopPlayback();
        assertThat(view.isPlaying()).isFalse();
    }

    @Test
    public void shouldStartPlaying() throws Exception {
        view.start();
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getCurrentVideoState()).isEqualTo(START);
    }

    @Test
    public void shouldStopPlayback() throws Exception {
        view.stopPlayback();
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getCurrentVideoState()).isEqualTo(STOP);
    }

    @Test
    public void shouldSuspendPlaying() throws Exception {
        view.start();
        view.suspend();
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getPrevVideoState()).isEqualTo(START);
        assertThat(shadowVideoView.getCurrentVideoState()).isEqualTo(SUSPEND);
    }

    @Test
    public void shouldResumePlaying() throws Exception {
        view.start();
        view.suspend();
        view.resume();
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getPrevVideoState()).isEqualTo(SUSPEND);
        assertThat(shadowVideoView.getCurrentVideoState()).isEqualTo(RESUME);
    }

    @Test
    public void shouldPausePlaying() throws Exception {
        view.start();
        view.pause();
        ShadowVideoView shadowVideoView = Shadows.shadowOf(view);
        assertThat(shadowVideoView.getPrevVideoState()).isEqualTo(START);
        assertThat(shadowVideoView.getCurrentVideoState()).isEqualTo(PAUSE);
    }

    @Test
    public void shouldDetermineIfPausable() throws Exception {
        view.start();
        assertThat(view.canPause()).isTrue();
        view.pause();
        assertThat(view.canPause()).isFalse();
        view.resume();
        assertThat(view.canPause()).isTrue();
        view.suspend();
        assertThat(view.canPause()).isFalse();
    }

    @Test
    public void shouldSeekToSpecifiedPosition() throws Exception {
        assertThat(view.getCurrentPosition()).isEqualTo(0);
        view.seekTo(10000);
        assertThat(view.getCurrentPosition()).isEqualTo(10000);
    }

    private static class TestPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
        }
    }

    private static class TestErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    }

    private static class TestCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
        }
    }
}

