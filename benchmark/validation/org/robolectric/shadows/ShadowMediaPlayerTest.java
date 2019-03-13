package org.robolectric.shadows;


import MediaPlayer.MEDIA_INFO_BUFFERING_END;
import MediaPlayer.MEDIA_INFO_BUFFERING_START;
import MediaPlayer.OnCompletionListener;
import MediaPlayer.OnErrorListener;
import MediaPlayer.OnInfoListener;
import MediaPlayer.OnPreparedListener;
import MediaPlayer.OnSeekCompleteListener;
import MediaPlayer.SEEK_CLOSEST;
import ShadowMediaPlayer.CreateListener;
import android.app.Application;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Looper;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowMediaPlayer.MediaEvent;
import org.robolectric.shadows.ShadowMediaPlayer.MediaInfo;
import org.robolectric.shadows.util.DataSource;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.Scheduler;

import static State.PAUSED;
import static State.PLAYBACK_COMPLETED;
import static State.PREPARED;
import static State.STARTED;


@RunWith(AndroidJUnit4.class)
public class ShadowMediaPlayerTest {
    private static final String DUMMY_SOURCE = "dummy-source";

    private MediaPlayer mediaPlayer;

    private ShadowMediaPlayer shadowMediaPlayer;

    private OnCompletionListener completionListener;

    private OnErrorListener errorListener;

    private OnInfoListener infoListener;

    private OnPreparedListener preparedListener;

    private OnSeekCompleteListener seekListener;

    private Scheduler scheduler;

    private MediaInfo info;

    private DataSource defaultSource;

    @Test
    public void create_withResourceId_shouldSetDataSource() {
        Application context = ApplicationProvider.getApplicationContext();
        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource((("android.resource://" + (context.getPackageName())) + "/123")), new ShadowMediaPlayer.MediaInfo(100, 10));
        MediaPlayer mp = MediaPlayer.create(context, 123);
        ShadowMediaPlayer shadow = Shadows.shadowOf(mp);
        assertThat(shadow.getDataSource()).isEqualTo(DataSource.toDataSource((("android.resource://" + (context.getPackageName())) + "/123")));
    }

    @Test
    public void testInitialState() {
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.IDLE);
    }

    @Test
    public void testCreateListener() {
        ShadowMediaPlayer.CreateListener createListener = Mockito.mock(CreateListener.class);
        ShadowMediaPlayer.setCreateListener(createListener);
        MediaPlayer newPlayer = new MediaPlayer();
        ShadowMediaPlayer shadow = Shadows.shadowOf(newPlayer);
        Mockito.verify(createListener).onCreate(newPlayer, shadow);
    }

    @Test
    public void testResetResetsPosition() {
        shadowMediaPlayer.setCurrentPosition(300);
        mediaPlayer.reset();
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(0);
    }

    @Test
    public void testPrepare() throws IOException {
        int[] testDelays = new int[]{ 0, 10, 100, 1500 };
        for (int delay : testDelays) {
            final long startTime = scheduler.getCurrentTime();
            info.setPreparationDelay(delay);
            shadowMediaPlayer.setState(State.INITIALIZED);
            mediaPlayer.prepare();
            assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PREPARED);
            assertThat(scheduler.getCurrentTime()).isEqualTo((startTime + delay));
        }
    }

    @Test
    public void testSetDataSourceString() throws IOException {
        DataSource ds = toDataSource("dummy");
        ShadowMediaPlayer.addMediaInfo(ds, info);
        mediaPlayer.setDataSource("dummy");
        assertThat(shadowMediaPlayer.getDataSource()).named("dataSource").isEqualTo(ds);
    }

    @Test
    public void testSetDataSourceUri() throws IOException {
        Map<String, String> headers = new HashMap<>();
        Uri uri = Uri.parse("file:/test");
        DataSource ds = toDataSource(ApplicationProvider.getApplicationContext(), uri, headers);
        ShadowMediaPlayer.addMediaInfo(ds, info);
        mediaPlayer.setDataSource(ApplicationProvider.getApplicationContext(), uri, headers);
        assertThat(shadowMediaPlayer.getSourceUri()).named("sourceUri").isSameAs(uri);
        assertThat(shadowMediaPlayer.getDataSource()).named("dataSource").isEqualTo(ds);
    }

    @Test
    public void testSetDataSourceFD() throws IOException {
        File tmpFile = File.createTempFile("MediaPlayerTest", null);
        try {
            tmpFile.deleteOnExit();
            FileInputStream is = new FileInputStream(tmpFile);
            try {
                FileDescriptor fd = is.getFD();
                DataSource ds = toDataSource(fd, 23, 524);
                ShadowMediaPlayer.addMediaInfo(ds, info);
                mediaPlayer.setDataSource(fd, 23, 524);
                assertThat(shadowMediaPlayer.getSourceUri()).named("sourceUri").isNull();
                assertThat(shadowMediaPlayer.getDataSource()).named("dataSource").isEqualTo(ds);
            } finally {
                is.close();
            }
        } finally {
            tmpFile.delete();
        }
    }

    @Config(minSdk = VERSION_CODES.M)
    @Test
    public void testSetDataSourceMediaDataSource() throws IOException {
        MediaDataSource mediaDataSource = new MediaDataSource() {
            @Override
            public void close() {
            }

            @Override
            public int readAt(long position, byte[] buffer, int offset, int size) {
                return 0;
            }

            @Override
            public long getSize() {
                return 0;
            }
        };
        DataSource ds = toDataSource(mediaDataSource);
        ShadowMediaPlayer.addMediaInfo(ds, info);
        mediaPlayer.setDataSource(mediaDataSource);
        assertThat(shadowMediaPlayer.getDataSource()).named("dataSource").isEqualTo(ds);
    }

    @Test
    public void testPrepareAsyncAutoCallback() {
        mediaPlayer.setOnPreparedListener(preparedListener);
        int[] testDelays = new int[]{ 0, 10, 100, 1500 };
        for (int delay : testDelays) {
            info.setPreparationDelay(delay);
            shadowMediaPlayer.setState(State.INITIALIZED);
            final long startTime = scheduler.getCurrentTime();
            mediaPlayer.prepareAsync();
            assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PREPARING);
            Mockito.verifyZeroInteractions(preparedListener);
            scheduler.advanceToLastPostedRunnable();
            assertThat(scheduler.getCurrentTime()).named("currentTime").isEqualTo((startTime + delay));
            assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PREPARED);
            Mockito.verify(preparedListener).onPrepared(mediaPlayer);
            Mockito.verifyNoMoreInteractions(preparedListener);
            Mockito.reset(preparedListener);
        }
    }

    @Test
    public void testPrepareAsyncManualCallback() {
        mediaPlayer.setOnPreparedListener(preparedListener);
        info.setPreparationDelay((-1));
        shadowMediaPlayer.setState(State.INITIALIZED);
        final long startTime = scheduler.getCurrentTime();
        mediaPlayer.prepareAsync();
        assertThat(scheduler.getCurrentTime()).named("currentTime").isEqualTo(startTime);
        assertThat(shadowMediaPlayer.getState()).isSameAs(State.PREPARING);
        Mockito.verifyZeroInteractions(preparedListener);
        shadowMediaPlayer.invokePreparedListener();
        assertThat(shadowMediaPlayer.getState()).isSameAs(State.PREPARED);
        Mockito.verify(preparedListener).onPrepared(mediaPlayer);
        Mockito.verifyNoMoreInteractions(preparedListener);
    }

    @Test
    public void testDefaultPreparationDelay() {
        assertThat(info.getPreparationDelay()).named("preparationDelay").isEqualTo(0);
    }

    @Test
    public void testIsPlaying() {
        EnumSet<org.robolectric.shadows.ShadowMediaPlayer.State> nonPlayingStates = EnumSet.of(State.IDLE, State.INITIALIZED, State.PREPARED, State.PAUSED, State.STOPPED, State.PLAYBACK_COMPLETED);
        for (org.robolectric.shadows.ShadowMediaPlayer.State state : nonPlayingStates) {
            shadowMediaPlayer.setState(state);
            assertThat(mediaPlayer.isPlaying()).isFalse();
        }
        shadowMediaPlayer.setState(State.STARTED);
        assertThat(mediaPlayer.isPlaying()).isTrue();
    }

    @Test
    public void testIsPrepared() {
        EnumSet<org.robolectric.shadows.ShadowMediaPlayer.State> prepStates = EnumSet.of(State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETED);
        for (org.robolectric.shadows.ShadowMediaPlayer.State state : org.robolectric.shadows.ShadowMediaPlayer.State.values()) {
            shadowMediaPlayer.setState(state);
            if (prepStates.contains(state)) {
                assertThat(shadowMediaPlayer.isPrepared()).isTrue();
            } else {
                assertThat(shadowMediaPlayer.isPrepared()).isFalse();
            }
        }
    }

    @Test
    public void testPlaybackProgress() {
        shadowMediaPlayer.setState(State.PREPARED);
        // This time offset is just to make sure that it doesn't work by
        // accident because the offsets are calculated relative to 0.
        scheduler.advanceBy(100);
        mediaPlayer.start();
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(0);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.STARTED);
        scheduler.advanceBy(500);
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(500);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.STARTED);
        scheduler.advanceBy(499);
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(999);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.STARTED);
        Mockito.verifyZeroInteractions(completionListener);
        scheduler.advanceBy(1);
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(1000);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PLAYBACK_COMPLETED);
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
        Mockito.verifyNoMoreInteractions(completionListener);
        scheduler.advanceBy(1);
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(1000);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PLAYBACK_COMPLETED);
        Mockito.verifyZeroInteractions(completionListener);
    }

    @Test
    public void testStop() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(300);
        mediaPlayer.stop();
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(300);
        scheduler.advanceBy(400);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(300);
    }

    @Test
    public void testPauseReschedulesCompletionCallback() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        mediaPlayer.pause();
        scheduler.advanceBy(800);
        Mockito.verifyZeroInteractions(completionListener);
        mediaPlayer.start();
        scheduler.advanceBy(799);
        Mockito.verifyZeroInteractions(completionListener);
        scheduler.advanceBy(1);
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
        Mockito.verifyNoMoreInteractions(completionListener);
        assertThat(scheduler.advanceToLastPostedRunnable()).isFalse();
        Mockito.verifyZeroInteractions(completionListener);
    }

    @Test
    public void testPauseUpdatesPosition() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        mediaPlayer.pause();
        scheduler.advanceBy(200);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PAUSED);
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(200);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.STARTED);
        assertThat(shadowMediaPlayer.getCurrentPosition()).isEqualTo(400);
    }

    @Test
    public void testSeekDuringPlaybackReschedulesCompletionCallback() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(300);
        mediaPlayer.seekTo(400);
        scheduler.advanceBy(599);
        Mockito.verifyZeroInteractions(completionListener);
        scheduler.advanceBy(1);
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
        Mockito.verifyNoMoreInteractions(completionListener);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PLAYBACK_COMPLETED);
        assertThat(scheduler.advanceToLastPostedRunnable()).isFalse();
        Mockito.verifyZeroInteractions(completionListener);
    }

    @Test
    public void testSeekDuringPlaybackUpdatesPosition() {
        shadowMediaPlayer.setState(State.PREPARED);
        // This time offset is just to make sure that it doesn't work by
        // accident because the offsets are calculated relative to 0.
        scheduler.advanceBy(100);
        mediaPlayer.start();
        scheduler.advanceBy(400);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(400);
        mediaPlayer.seekTo(600);
        scheduler.advanceBy(0);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(600);
        scheduler.advanceBy(300);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(900);
        mediaPlayer.seekTo(100);
        scheduler.advanceBy(0);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(100);
        scheduler.advanceBy(900);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(1000);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PLAYBACK_COMPLETED);
        scheduler.advanceBy(100);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(1000);
    }

    @Config(minSdk = VERSION_CODES.O)
    @Test
    public void testSeekToMode() {
        shadowMediaPlayer.setState(State.PREPARED);
        // This time offset is just to make sure that it doesn't work by
        // accident because the offsets are calculated relative to 0.
        scheduler.advanceBy(100);
        mediaPlayer.start();
        scheduler.advanceBy(400);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(400);
        mediaPlayer.seekTo(600, SEEK_CLOSEST);
        scheduler.advanceBy(0);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(600);
    }

    @Test
    public void testPendingEventsRemovedOnError() {
        Mockito.when(errorListener.onError(mediaPlayer, 2, 3)).thenReturn(true);
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        // We should have a pending completion callback.
        assertThat(scheduler.size()).isEqualTo(1);
        shadowMediaPlayer.invokeErrorListener(2, 3);
        assertThat(scheduler.advanceToLastPostedRunnable()).isFalse();
        Mockito.verifyZeroInteractions(completionListener);
    }

    @Test
    public void testAttachAuxEffectStates() {
        testStates(new ShadowMediaPlayerTest.MethodSpec("attachAuxEffect", 37), EnumSet.of(State.IDLE, State.ERROR), onErrorTester, null);
    }

    private static final EnumSet<org.robolectric.shadows.ShadowMediaPlayer.State> emptyStateSet = EnumSet.noneOf(org.robolectric.shadows.ShadowMediaPlayer.State.class);

    @Test
    public void testGetAudioSessionIdStates() {
        testStates("getAudioSessionId", ShadowMediaPlayerTest.emptyStateSet, onErrorTester, null);
    }

    @Test
    public void testGetCurrentPositionStates() {
        testStates("getCurrentPosition", EnumSet.of(State.IDLE, State.ERROR), onErrorTester, null);
    }

    @Test
    public void testGetDurationStates() {
        testStates("getDuration", EnumSet.of(State.IDLE, State.INITIALIZED, State.ERROR), onErrorTester, null);
    }

    @Test
    public void testGetVideoHeightAndWidthStates() {
        testStates("getVideoHeight", EnumSet.of(State.IDLE, State.ERROR), logTester, null);
        testStates("getVideoWidth", EnumSet.of(State.IDLE, State.ERROR), logTester, null);
    }

    @Test
    public void testIsLoopingStates() {
        // isLooping is quite unique as it throws ISE when in END state,
        // even though every other state is legal.
        testStates("isLooping", EnumSet.of(State.END), iseTester, null);
    }

    @Test
    public void testIsPlayingStates() {
        testStates("isPlaying", EnumSet.of(State.ERROR), onErrorTester, null);
    }

    @Test
    public void testPauseStates() {
        testStates("pause", EnumSet.of(State.IDLE, State.INITIALIZED, State.PREPARED, State.STOPPED, State.ERROR), onErrorTester, State.PAUSED);
    }

    @Test
    public void testPrepareStates() {
        testStates("prepare", EnumSet.of(State.IDLE, State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETED, State.ERROR), State.PREPARED);
    }

    @Test
    public void testPrepareAsyncStates() {
        testStates("prepareAsync", EnumSet.of(State.IDLE, State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETED, State.ERROR), State.PREPARING);
    }

    @Test
    public void testReleaseStates() {
        testStates("release", ShadowMediaPlayerTest.emptyStateSet, State.END);
    }

    @Test
    public void testResetStates() {
        testStates("reset", EnumSet.of(State.END), State.IDLE);
    }

    @Test
    public void testSeekToStates() {
        testStates(new ShadowMediaPlayerTest.MethodSpec("seekTo", 38), EnumSet.of(State.IDLE, State.INITIALIZED, State.STOPPED, State.ERROR), onErrorTester, null);
    }

    @Test
    public void testSetAudioSessionIdStates() {
        testStates(new ShadowMediaPlayerTest.MethodSpec("setAudioSessionId", 40), EnumSet.of(State.INITIALIZED, State.PREPARED, State.STARTED, State.PAUSED, State.STOPPED, State.PLAYBACK_COMPLETED, State.ERROR), onErrorTester, null);
    }

    // NOTE: This test diverges from the spec in the MediaPlayer
    // doc, which says that setAudioStreamType() is valid to call
    // from any state other than ERROR. It mentions that
    // unless you call it before prepare it won't be effective.
    // However, by inspection I found that it actually calls onError
    // and moves into the ERROR state unless invoked from IDLE state,
    // so that is what I have emulated.
    @Test
    public void testSetAudioStreamTypeStates() {
        testStates(new ShadowMediaPlayerTest.MethodSpec("setAudioStreamType", AudioManager.STREAM_MUSIC), EnumSet.of(State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETED, State.ERROR), onErrorTester, null);
    }

    @Test
    public void testSetLoopingStates() {
        testStates(new ShadowMediaPlayerTest.MethodSpec("setLooping", true), EnumSet.of(State.ERROR), onErrorTester, null);
    }

    @Test
    public void testSetVolumeStates() {
        testStates(new ShadowMediaPlayerTest.MethodSpec("setVolume", new Class<?>[]{ float.class, float.class }, new Object[]{ 1.0F, 1.0F }), EnumSet.of(State.ERROR), onErrorTester, null);
    }

    @Test
    public void testSetDataSourceStates() {
        final EnumSet<org.robolectric.shadows.ShadowMediaPlayer.State> invalidStates = EnumSet.of(State.INITIALIZED, State.PREPARED, State.STARTED, State.PAUSED, State.PLAYBACK_COMPLETED, State.STOPPED, State.ERROR);
        testStates(new ShadowMediaPlayerTest.MethodSpec("setDataSource", ShadowMediaPlayerTest.DUMMY_SOURCE), invalidStates, iseTester, State.INITIALIZED);
    }

    @Test
    public void testStartStates() {
        testStates("start", EnumSet.of(State.IDLE, State.INITIALIZED, State.PREPARING, State.STOPPED, State.ERROR), onErrorTester, State.STARTED);
    }

    @Test
    public void testStopStates() {
        testStates("stop", EnumSet.of(State.IDLE, State.INITIALIZED, State.ERROR), onErrorTester, State.STOPPED);
    }

    @Test
    public void testCurrentPosition() {
        int[] positions = new int[]{ 0, 1, 2, 1024 };
        for (int position : positions) {
            shadowMediaPlayer.setCurrentPosition(position);
            assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(position);
        }
    }

    @Test
    public void testInitialAudioSessionIdIsNotZero() {
        assertThat(mediaPlayer.getAudioSessionId()).named("initial audioSessionId").isNotEqualTo(0);
    }

    private ShadowMediaPlayerTest.Tester onErrorTester = new ShadowMediaPlayerTest.OnErrorTester((-38), 0);

    private ShadowMediaPlayerTest.Tester iseTester = new ShadowMediaPlayerTest.ExceptionTester(IllegalStateException.class);

    private ShadowMediaPlayerTest.Tester logTester = new ShadowMediaPlayerTest.LogTester(null);

    private ShadowMediaPlayerTest.Tester assertTester = new ShadowMediaPlayerTest.ExceptionTester(AssertionError.class);

    public class MethodSpec {
        public Method method;

        // public String method;
        public Class<?>[] argTypes;

        public Object[] args;

        public MethodSpec(String method) {
            this(method, ((Class<?>[]) (null)), ((Object[]) (null)));
        }

        public MethodSpec(String method, Class<?>[] argTypes, Object[] args) {
            try {
                this.method = MediaPlayer.class.getDeclaredMethod(method, argTypes);
                this.args = args;
            } catch (NoSuchMethodException e) {
                throw new AssertionError(("Method lookup failed: " + method), e);
            }
        }

        public MethodSpec(String method, int arg) {
            this(method, new Class<?>[]{ int.class }, new Object[]{ arg });
        }

        public MethodSpec(String method, boolean arg) {
            this(method, new Class<?>[]{ boolean.class }, new Object[]{ arg });
        }

        public MethodSpec(String method, Class<?> c) {
            this(method, new Class<?>[]{ c }, new Object[]{ null });
        }

        public MethodSpec(String method, Object o) {
            this(method, new Class<?>[]{ o.getClass() }, new Object[]{ o });
        }

        public <T> MethodSpec(String method, T o, Class<T> c) {
            this(method, new Class<?>[]{ c }, new Object[]{ o });
        }

        public void invoke() throws InvocationTargetException {
            try {
                method.invoke(mediaPlayer, args);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public String toString() {
            return method.toString();
        }
    }

    private interface Tester {
        void test(ShadowMediaPlayerTest.MethodSpec method);
    }

    private class OnErrorTester implements ShadowMediaPlayerTest.Tester {
        private int what;

        private int extra;

        public OnErrorTester(int what, int extra) {
            this.what = what;
            this.extra = extra;
        }

        @Override
        public void test(ShadowMediaPlayerTest.MethodSpec method) {
            final org.robolectric.shadows.ShadowMediaPlayer.State state = shadowMediaPlayer.getState();
            final boolean wasPaused = scheduler.isPaused();
            scheduler.pause();
            try {
                method.invoke();
            } catch (InvocationTargetException e) {
                throw new RuntimeException((((((("Expected <" + method) + "> to call onError rather than throw <") + (e.getTargetException())) + "> when called from <") + state) + ">"), e);
            }
            Mockito.verifyZeroInteractions(errorListener);
            final org.robolectric.shadows.ShadowMediaPlayer.State finalState = shadowMediaPlayer.getState();
            assertThat(finalState).isSameAs(State.ERROR);
            scheduler.unPause();
            Mockito.verify(errorListener).onError(mediaPlayer, what, extra);
            Mockito.reset(errorListener);
            if (wasPaused) {
                scheduler.pause();
            }
        }
    }

    private class ExceptionTester implements ShadowMediaPlayerTest.Tester {
        private Class<? extends Throwable> eClass;

        public ExceptionTester(Class<? extends Throwable> eClass) {
            this.eClass = eClass;
        }

        @Override
        @SuppressWarnings("MissingFail")
        public void test(ShadowMediaPlayerTest.MethodSpec method) {
            final org.robolectric.shadows.ShadowMediaPlayer.State state = shadowMediaPlayer.getState();
            boolean success = false;
            try {
                method.invoke();
                success = true;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getTargetException();
                assertThat(cause).isInstanceOf(eClass);
                final org.robolectric.shadows.ShadowMediaPlayer.State finalState = shadowMediaPlayer.getState();
                assertThat(finalState).isSameAs(state);
            }
            assertThat(success).isFalse();
        }
    }

    private class LogTester implements ShadowMediaPlayerTest.Tester {
        private org.robolectric.shadows.ShadowMediaPlayer.State next;

        public LogTester(org.robolectric.shadows.ShadowMediaPlayer.State next) {
            this.next = next;
        }

        @Override
        public void test(ShadowMediaPlayerTest.MethodSpec method) {
            testMethodSuccess(method, next);
        }
    }

    private static final org.robolectric.shadows.ShadowMediaPlayer.State[] seekableStates = new org.robolectric.shadows.ShadowMediaPlayer.State[]{ PREPARED, PAUSED, PLAYBACK_COMPLETED, STARTED };

    // It is not 100% clear from the docs if seeking to < 0 should
    // invoke an error. I have assumed from the documentation
    // which says "Successful invoke of this method in a valid
    // state does not change the state" that it doesn't invoke an
    // error. Rounding the seek up to 0 seems to be the sensible
    // alternative behavior.
    @Test
    public void testSeekBeforeStart() {
        shadowMediaPlayer.setSeekDelay((-1));
        for (org.robolectric.shadows.ShadowMediaPlayer.State state : ShadowMediaPlayerTest.seekableStates) {
            shadowMediaPlayer.setState(state);
            shadowMediaPlayer.setCurrentPosition(500);
            mediaPlayer.seekTo((-1));
            shadowMediaPlayer.invokeSeekCompleteListener();
            assertThat(mediaPlayer.getCurrentPosition()).named(("Current postion while " + state)).isEqualTo(0);
            assertThat(shadowMediaPlayer.getState()).named(("Final state " + state)).isEqualTo(state);
        }
    }

    // Similar comments apply to this test as to
    // testSeekBeforeStart().
    @Test
    public void testSeekPastEnd() {
        shadowMediaPlayer.setSeekDelay((-1));
        for (org.robolectric.shadows.ShadowMediaPlayer.State state : ShadowMediaPlayerTest.seekableStates) {
            shadowMediaPlayer.setState(state);
            shadowMediaPlayer.setCurrentPosition(500);
            mediaPlayer.seekTo(1001);
            shadowMediaPlayer.invokeSeekCompleteListener();
            assertThat(mediaPlayer.getCurrentPosition()).named(("Current postion while " + state)).isEqualTo(1000);
            assertThat(shadowMediaPlayer.getState()).named(("Final state " + state)).isEqualTo(state);
        }
    }

    @Test
    public void testCompletionListener() {
        shadowMediaPlayer.invokeCompletionListener();
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
    }

    @Test
    public void testCompletionWithoutListenerDoesNotThrowException() {
        mediaPlayer.setOnCompletionListener(null);
        shadowMediaPlayer.invokeCompletionListener();
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PLAYBACK_COMPLETED);
        Mockito.verifyZeroInteractions(completionListener);
    }

    @Test
    public void testSeekListener() {
        shadowMediaPlayer.invokeSeekCompleteListener();
        Mockito.verify(seekListener).onSeekComplete(mediaPlayer);
    }

    @Test
    public void testSeekWithoutListenerDoesNotThrowException() {
        mediaPlayer.setOnSeekCompleteListener(null);
        shadowMediaPlayer.invokeSeekCompleteListener();
        Mockito.verifyZeroInteractions(seekListener);
    }

    @Test
    public void testSeekDuringPlaybackDelayedCallback() {
        shadowMediaPlayer.setState(State.PREPARED);
        shadowMediaPlayer.setSeekDelay(100);
        assertThat(shadowMediaPlayer.getSeekDelay()).isEqualTo(100);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(200);
        mediaPlayer.seekTo(450);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(200);
        scheduler.advanceBy(99);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(200);
        Mockito.verifyZeroInteractions(seekListener);
        scheduler.advanceBy(1);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(450);
        Mockito.verify(seekListener).onSeekComplete(mediaPlayer);
        assertThat(scheduler.advanceToLastPostedRunnable()).isTrue();
        Mockito.verifyNoMoreInteractions(seekListener);
    }

    @Test
    public void testSeekWhilePausedDelayedCallback() {
        shadowMediaPlayer.setState(State.PAUSED);
        shadowMediaPlayer.setSeekDelay(100);
        scheduler.advanceBy(200);
        mediaPlayer.seekTo(450);
        scheduler.advanceBy(99);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(0);
        Mockito.verifyZeroInteractions(seekListener);
        scheduler.advanceBy(1);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(450);
        Mockito.verify(seekListener).onSeekComplete(mediaPlayer);
        // Check that no completion callback or alternative
        // seek callbacks have been scheduled.
        assertThat(scheduler.advanceToLastPostedRunnable()).isFalse();
    }

    @Test
    public void testSeekWhileSeekingWhilePaused() {
        shadowMediaPlayer.setState(State.PAUSED);
        shadowMediaPlayer.setSeekDelay(100);
        scheduler.advanceBy(200);
        mediaPlayer.seekTo(450);
        scheduler.advanceBy(50);
        mediaPlayer.seekTo(600);
        scheduler.advanceBy(99);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(0);
        Mockito.verifyZeroInteractions(seekListener);
        scheduler.advanceBy(1);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(600);
        Mockito.verify(seekListener).onSeekComplete(mediaPlayer);
        // Check that no completion callback or alternative
        // seek callbacks have been scheduled.
        assertThat(scheduler.advanceToLastPostedRunnable()).isFalse();
    }

    @Test
    public void testSeekWhileSeekingWhilePlaying() {
        shadowMediaPlayer.setState(State.PREPARED);
        shadowMediaPlayer.setSeekDelay(100);
        final long startTime = scheduler.getCurrentTime();
        mediaPlayer.start();
        scheduler.advanceBy(200);
        mediaPlayer.seekTo(450);
        scheduler.advanceBy(50);
        mediaPlayer.seekTo(600);
        scheduler.advanceBy(99);
        // Not sure of the correct behavior to emulate here, as the MediaPlayer
        // documentation is not detailed enough. There are three possibilities:
        // 1. Playback is paused for the entire time that a seek is in progress.
        // 2. Playback continues normally until the seek is complete.
        // 3. Somewhere between these two extremes - playback continues for
        // a while and then pauses until the seek is complete.
        // I have decided to emulate the first. I don't think that
        // implementations should depend on any of these particular behaviors
        // and consider the behavior indeterminate.
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(200);
        Mockito.verifyZeroInteractions(seekListener);
        scheduler.advanceBy(1);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(600);
        Mockito.verify(seekListener).onSeekComplete(mediaPlayer);
        // Check that the completion callback is scheduled properly
        // but no alternative seek callbacks.
        assertThat(scheduler.advanceToLastPostedRunnable()).isTrue();
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
        Mockito.verifyNoMoreInteractions(seekListener);
        assertThat(scheduler.getCurrentTime()).isEqualTo((startTime + 750));
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(1000);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PLAYBACK_COMPLETED);
    }

    @Test
    public void testSimulatenousEventsAllRun() {
        // Simultaneous events should all run even if
        // one of them stops playback.
        MediaEvent e1 = new MediaEvent() {
            @Override
            public void run(MediaPlayer mp, ShadowMediaPlayer smp) {
                smp.doStop();
            }
        };
        MediaEvent e2 = Mockito.mock(MediaEvent.class);
        info.scheduleEventAtOffset(100, e1);
        info.scheduleEventAtOffset(100, e2);
        shadowMediaPlayer.setState(State.INITIALIZED);
        shadowMediaPlayer.doStart();
        scheduler.advanceBy(100);
        // Verify that the first event ran
        assertThat(shadowMediaPlayer.isReallyPlaying()).isFalse();
        Mockito.verify(e2).run(mediaPlayer, shadowMediaPlayer);
    }

    @Test
    public void testResetCancelsCallbacks() {
        shadowMediaPlayer.setState(State.STARTED);
        mediaPlayer.seekTo(100);
        MediaEvent e = Mockito.mock(MediaEvent.class);
        shadowMediaPlayer.postEventDelayed(e, 200);
        mediaPlayer.reset();
        assertThat(scheduler.size()).isEqualTo(0);
    }

    @Test
    public void testReleaseCancelsSeekCallback() {
        shadowMediaPlayer.setState(State.STARTED);
        mediaPlayer.seekTo(100);
        MediaEvent e = Mockito.mock(MediaEvent.class);
        shadowMediaPlayer.postEventDelayed(e, 200);
        mediaPlayer.release();
        assertThat(scheduler.size()).isEqualTo(0);
    }

    @Test
    public void testSeekManualCallback() {
        // Need to put the player into a state where seeking is allowed
        shadowMediaPlayer.setState(State.STARTED);
        // seekDelay of -1 signifies that OnSeekComplete won't be
        // invoked automatically by the shadow player itself.
        shadowMediaPlayer.setSeekDelay((-1));
        assertThat(shadowMediaPlayer.getPendingSeek()).named("pendingSeek before").isEqualTo((-1));
        int[] positions = new int[]{ 0, 5, 2, 999 };
        int prevPos = 0;
        for (int position : positions) {
            mediaPlayer.seekTo(position);
            assertThat(shadowMediaPlayer.getPendingSeek()).named("pendingSeek").isEqualTo(position);
            assertThat(mediaPlayer.getCurrentPosition()).named("pendingSeekCurrentPos").isEqualTo(prevPos);
            shadowMediaPlayer.invokeSeekCompleteListener();
            assertThat(shadowMediaPlayer.getPendingSeek()).isEqualTo((-1));
            assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(position);
            prevPos = position;
        }
    }

    @Test
    public void testPreparedListenerCalled() {
        shadowMediaPlayer.invokePreparedListener();
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PREPARED);
        Mockito.verify(preparedListener).onPrepared(mediaPlayer);
    }

    @Test
    public void testPreparedWithoutListenerDoesNotThrowException() {
        mediaPlayer.setOnPreparedListener(null);
        shadowMediaPlayer.invokePreparedListener();
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.PREPARED);
        Mockito.verifyZeroInteractions(preparedListener);
    }

    @Test
    public void testInfoListenerCalled() {
        shadowMediaPlayer.invokeInfoListener(21, 32);
        Mockito.verify(infoListener).onInfo(mediaPlayer, 21, 32);
    }

    @Test
    public void testInfoWithoutListenerDoesNotThrowException() {
        mediaPlayer.setOnInfoListener(null);
        shadowMediaPlayer.invokeInfoListener(3, 44);
        Mockito.verifyZeroInteractions(infoListener);
    }

    @Test
    public void testErrorListenerCalledNoOnCompleteCalledWhenReturnTrue() {
        Mockito.when(errorListener.onError(mediaPlayer, 112, 221)).thenReturn(true);
        shadowMediaPlayer.invokeErrorListener(112, 221);
        assertThat(shadowMediaPlayer.getState()).isEqualTo(State.ERROR);
        Mockito.verify(errorListener).onError(mediaPlayer, 112, 221);
        Mockito.verifyZeroInteractions(completionListener);
    }

    @Test
    public void testErrorListenerCalledOnCompleteCalledWhenReturnFalse() {
        Mockito.when(errorListener.onError(mediaPlayer, 0, 0)).thenReturn(false);
        shadowMediaPlayer.invokeErrorListener(321, 11);
        Mockito.verify(errorListener).onError(mediaPlayer, 321, 11);
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
    }

    @Test
    public void testErrorCausesOnCompleteCalledWhenNoErrorListener() {
        mediaPlayer.setOnErrorListener(null);
        shadowMediaPlayer.invokeErrorListener(321, 21);
        Mockito.verifyZeroInteractions(errorListener);
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
    }

    @Test
    public void testReleaseStopsScheduler() {
        shadowMediaPlayer.doStart();
        mediaPlayer.release();
        assertThat(scheduler.size()).isEqualTo(0);
    }

    @Test
    public void testResetStopsScheduler() {
        shadowMediaPlayer.doStart();
        mediaPlayer.reset();
        assertThat(scheduler.size()).isEqualTo(0);
    }

    @Test
    public void testDoStartStop() {
        assertThat(shadowMediaPlayer.isReallyPlaying()).isFalse();
        scheduler.advanceBy(100);
        shadowMediaPlayer.doStart();
        assertThat(shadowMediaPlayer.isReallyPlaying()).isTrue();
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(0);
        assertThat(shadowMediaPlayer.getState()).isSameAs(State.IDLE);
        scheduler.advanceBy(100);
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(100);
        shadowMediaPlayer.doStop();
        assertThat(shadowMediaPlayer.isReallyPlaying()).isFalse();
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(100);
        assertThat(shadowMediaPlayer.getState()).isSameAs(State.IDLE);
        scheduler.advanceBy(50);
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(100);
    }

    @Test
    public void testScheduleErrorAtOffsetWhileNotPlaying() {
        info.scheduleErrorAtOffset(500, 1, 3);
        shadowMediaPlayer.setState(State.INITIALIZED);
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(499);
        Mockito.verifyZeroInteractions(errorListener);
        scheduler.advanceBy(1);
        Mockito.verify(errorListener).onError(mediaPlayer, 1, 3);
        assertThat(shadowMediaPlayer.getState()).isSameAs(State.ERROR);
        assertThat(scheduler.advanceToLastPostedRunnable()).isFalse();
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(500);
    }

    @Test
    public void testScheduleErrorAtOffsetInPast() {
        info.scheduleErrorAtOffset(200, 1, 2);
        shadowMediaPlayer.setState(State.INITIALIZED);
        shadowMediaPlayer.setCurrentPosition(400);
        shadowMediaPlayer.setState(State.PAUSED);
        mediaPlayer.start();
        scheduler.unPause();
        Mockito.verifyZeroInteractions(errorListener);
    }

    @Test
    public void testScheduleBufferUnderrunAtOffset() {
        info.scheduleBufferUnderrunAtOffset(100, 50);
        shadowMediaPlayer.setState(State.INITIALIZED);
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(99);
        Mockito.verifyZeroInteractions(infoListener);
        scheduler.advanceBy(1);
        Mockito.verify(infoListener).onInfo(mediaPlayer, MEDIA_INFO_BUFFERING_START, 0);
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(100);
        assertThat(shadowMediaPlayer.isReallyPlaying()).isFalse();
        scheduler.advanceBy(49);
        Mockito.verifyZeroInteractions(infoListener);
        scheduler.advanceBy(1);
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(100);
        Mockito.verify(infoListener).onInfo(mediaPlayer, MEDIA_INFO_BUFFERING_END, 0);
        scheduler.advanceBy(100);
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(200);
    }

    @Test
    public void testRemoveEventAtOffset() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        MediaEvent e = info.scheduleInfoAtOffset(500, 1, 3);
        scheduler.advanceBy(299);
        info.removeEventAtOffset(500, e);
        scheduler.advanceToLastPostedRunnable();
        Mockito.verifyZeroInteractions(infoListener);
    }

    @Test
    public void testRemoveEvent() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        MediaEvent e = info.scheduleInfoAtOffset(500, 1, 3);
        scheduler.advanceBy(299);
        shadowMediaPlayer.doStop();
        info.removeEvent(e);
        shadowMediaPlayer.doStart();
        scheduler.advanceToLastPostedRunnable();
        Mockito.verifyZeroInteractions(infoListener);
    }

    @Test
    public void testScheduleMultipleRunnables() {
        shadowMediaPlayer.setState(State.PREPARED);
        scheduler.advanceBy(25);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        assertThat(scheduler.size()).isEqualTo(1);
        shadowMediaPlayer.doStop();
        info.scheduleInfoAtOffset(250, 2, 4);
        shadowMediaPlayer.doStart();
        assertThat(scheduler.size()).isEqualTo(1);
        MediaEvent e1 = Mockito.mock(MediaEvent.class);
        shadowMediaPlayer.doStop();
        info.scheduleEventAtOffset(400, e1);
        shadowMediaPlayer.doStart();
        scheduler.advanceBy(49);
        Mockito.verifyZeroInteractions(infoListener);
        scheduler.advanceBy(1);
        Mockito.verify(infoListener).onInfo(mediaPlayer, 2, 4);
        scheduler.advanceBy(149);
        shadowMediaPlayer.doStop();
        info.scheduleErrorAtOffset(675, 32, 22);
        shadowMediaPlayer.doStart();
        Mockito.verifyZeroInteractions(e1);
        scheduler.advanceBy(1);
        Mockito.verify(e1).run(mediaPlayer, shadowMediaPlayer);
        mediaPlayer.pause();
        assertThat(scheduler.size()).isEqualTo(0);
        scheduler.advanceBy(324);
        MediaEvent e2 = Mockito.mock(MediaEvent.class);
        info.scheduleEventAtOffset(680, e2);
        mediaPlayer.start();
        scheduler.advanceBy(274);
        Mockito.verifyZeroInteractions(errorListener);
        scheduler.advanceBy(1);
        Mockito.verify(errorListener).onError(mediaPlayer, 32, 22);
        assertThat(scheduler.size()).isEqualTo(0);
        assertThat(shadowMediaPlayer.getCurrentPositionRaw()).isEqualTo(675);
        assertThat(shadowMediaPlayer.getState()).isSameAs(State.ERROR);
        Mockito.verifyZeroInteractions(e2);
    }

    @Test
    public void testSetDataSourceExceptionWithWrongExceptionTypeAsserts() {
        boolean fail = false;
        Map<DataSource, Exception> exceptions = ReflectionHelpers.getStaticField(ShadowMediaPlayer.class, "exceptions");
        DataSource ds = toDataSource("dummy");
        Exception e = new CloneNotSupportedException();// just a convenient, non-RuntimeException in java.lang

        exceptions.put(ds, e);
        try {
            shadowMediaPlayer.setDataSource(ds);
            fail = true;
        } catch (AssertionError a) {
        } catch (IOException ioe) {
            Assert.fail((("Got exception <" + ioe) + ">; expecting assertion"));
        }
        if (fail) {
            Assert.fail("setDataSource() should assert with non-IOException,non-RuntimeException");
        }
    }

    @Test
    public void testSetDataSourceCustomExceptionOverridesIllegalState() {
        shadowMediaPlayer.setState(State.PREPARED);
        ShadowMediaPlayer.addException(toDataSource("dummy"), new IOException());
        try {
            mediaPlayer.setDataSource("dummy");
            Assert.fail("Expecting IOException to be thrown");
        } catch (IOException eThrown) {
        } catch (Exception eThrown) {
            Assert.fail((eThrown + " was thrown, expecting IOException"));
        }
    }

    @Test
    public void testGetSetLooping() {
        assertThat(mediaPlayer.isLooping()).isFalse();
        mediaPlayer.setLooping(true);
        assertThat(mediaPlayer.isLooping()).isTrue();
        mediaPlayer.setLooping(false);
        assertThat(mediaPlayer.isLooping()).isFalse();
    }

    /**
     * If the looping mode was being set to {@code true}
     * {@link MediaPlayer#setLooping(boolean)}, the MediaPlayer object shall
     * remain in the Started state.
     */
    @Test
    public void testSetLoopingCalledWhilePlaying() {
        shadowMediaPlayer.setState(State.PREPARED);
        mediaPlayer.start();
        scheduler.advanceBy(200);
        mediaPlayer.setLooping(true);
        scheduler.advanceBy(1100);
        Mockito.verifyZeroInteractions(completionListener);
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(300);
        mediaPlayer.setLooping(false);
        scheduler.advanceBy(699);
        Mockito.verifyZeroInteractions(completionListener);
        scheduler.advanceBy(1);
        Mockito.verify(completionListener).onCompletion(mediaPlayer);
    }

    @Test
    public void testSetLoopingCalledWhileStartable() {
        final org.robolectric.shadows.ShadowMediaPlayer.State[] startableStates = new org.robolectric.shadows.ShadowMediaPlayer.State[]{ PREPARED, PAUSED };
        for (org.robolectric.shadows.ShadowMediaPlayer.State state : startableStates) {
            shadowMediaPlayer.setCurrentPosition(500);
            shadowMediaPlayer.setState(state);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            scheduler.advanceBy(700);
            Mockito.verifyZeroInteractions(completionListener);
            assertThat(mediaPlayer.getCurrentPosition()).named(state.toString()).isEqualTo(200);
        }
    }

    /**
     * While in the PlaybackCompleted state, calling start() can restart the
     * playback from the beginning of the audio/video source.
     */
    @Test
    public void testStartAfterPlaybackCompleted() {
        shadowMediaPlayer.setState(State.PLAYBACK_COMPLETED);
        shadowMediaPlayer.setCurrentPosition(1000);
        mediaPlayer.start();
        assertThat(mediaPlayer.getCurrentPosition()).isEqualTo(0);
    }

    @Test
    public void testResetStaticState() {
        ShadowMediaPlayer.CreateListener createListener = Mockito.mock(CreateListener.class);
        ShadowMediaPlayer.setCreateListener(createListener);
        assertThat(ShadowMediaPlayer.createListener).named("createListener").isSameAs(createListener);
        DataSource dummy = toDataSource("stuff");
        IOException e = new IOException();
        ShadowMediaPlayer.addException(dummy, e);
        try {
            shadowMediaPlayer.setState(State.IDLE);
            shadowMediaPlayer.setDataSource(dummy);
            Assert.fail("Expected exception thrown");
        } catch (IOException e2) {
            assertThat(e2).named("thrown exception").isSameAs(e);
        }
        // Check that the mediaInfo was cleared
        shadowMediaPlayer.doSetDataSource(defaultSource);
        assertThat(shadowMediaPlayer.getMediaInfo()).named("mediaInfo:before").isNotNull();
        ShadowMediaPlayer.resetStaticState();
        // Check that the listener was cleared.
        assertThat(ShadowMediaPlayer.createListener).named("createListener").isNull();
        // Check that the mediaInfo was cleared.
        try {
            shadowMediaPlayer.doSetDataSource(defaultSource);
            Assert.fail("Expected exception thrown");
        } catch (IllegalArgumentException ie) {
            // We expect this if the static state has been cleared.
        }
        // Check that the exception was cleared.
        try {
            shadowMediaPlayer.setState(State.IDLE);
            ShadowMediaPlayer.addMediaInfo(dummy, info);
            shadowMediaPlayer.setDataSource(dummy);
        } catch (IOException e2) {
            Assert.fail(((("Exception was not cleared by resetStaticState() for <" + dummy) + ">") + e2));
        }
    }

    @Test
    public void setDataSourceException_withRuntimeException() {
        RuntimeException e = new RuntimeException("some dummy message");
        ShadowMediaPlayer.addException(toDataSource("dummy"), e);
        try {
            mediaPlayer.setDataSource("dummy");
            Assert.fail("Expected exception thrown");
        } catch (Exception caught) {
            assertThat(caught).isSameAs(e);
            assertThat(e.getStackTrace()[0].getClassName()).named("Stack trace should originate in Shadow").isEqualTo(ShadowMediaPlayer.class.getName());
        }
    }

    @Test
    public void setDataSourceException_withIOException() {
        IOException e = new IOException("some dummy message");
        ShadowMediaPlayer.addException(toDataSource("dummy"), e);
        shadowMediaPlayer.setState(State.IDLE);
        try {
            mediaPlayer.setDataSource("dummy");
            Assert.fail("Expected exception thrown");
        } catch (Exception caught) {
            assertThat(caught).isSameAs(e);
            assertThat(e.getStackTrace()[0].getClassName()).named("Stack trace should originate in Shadow").isEqualTo(ShadowMediaPlayer.class.getName());
            assertThat(shadowMediaPlayer.getState()).named((("State after " + e) + " thrown should be unchanged")).isSameAs(State.IDLE);
        }
    }

    @Test
    public void setDataSource_forNoDataSource_asserts() {
        try {
            mediaPlayer.setDataSource("some unspecified data source");
            Assert.fail("Expected exception thrown");
        } catch (IllegalArgumentException a) {
            assertThat(a.getMessage()).named("assertionMessage").contains("addException");
            assertThat(a.getMessage()).named("assertionMessage").contains("addMediaInfo");
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    @Test
    public void instantiateOnBackgroundThread() throws InterruptedException, ExecutionException {
        ShadowMediaPlayer shadowMediaPlayer = Executors.newSingleThreadExecutor().submit(() -> {
            // This thread does not have a prepared looper, so the main looper is used
            MediaPlayer mediaPlayer = Shadow.newInstanceOf(MediaPlayer.class);
            return Shadows.shadowOf(mediaPlayer);
        }).get();
        AtomicBoolean ran = new AtomicBoolean(false);
        shadowMediaPlayer.postEvent(new MediaEvent() {
            @Override
            public void run(MediaPlayer mp, ShadowMediaPlayer smp) {
                assertThat(Looper.myLooper()).isSameAs(Looper.getMainLooper());
                ran.set(true);
            }
        });
        scheduler.advanceToLastPostedRunnable();
        assertThat(ran.get()).isTrue();
    }
}

