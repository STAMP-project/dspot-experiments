package org.robolectric.shadows;


import SoundPool.OnLoadCompleteListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.R;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSoundPool.Playback;

import static org.robolectric.R.raw.sound;


@RunWith(AndroidJUnit4.class)
public class ShadowSoundPoolTest {
    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldCreateSoundPool_Lollipop() {
        SoundPool soundPool = new SoundPool.Builder().build();
        assertThat(soundPool).isNotNull();
        SoundPool.OnLoadCompleteListener listener = Mockito.mock(OnLoadCompleteListener.class);
        soundPool.setOnLoadCompleteListener(listener);
    }

    @Test
    @Config(maxSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void shouldCreateSoundPool_JellyBean() {
        SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        assertThat(soundPool).isNotNull();
    }

    @Test
    public void playedSoundsFromResourcesAreRecorded() {
        SoundPool soundPool = createSoundPool();
        int soundId = soundPool.load(ApplicationProvider.getApplicationContext(), sound, 1);
        soundPool.play(soundId, 1.0F, 1.0F, 1, 0, 1);
        assertThat(Shadows.shadowOf(soundPool).wasResourcePlayed(sound)).isTrue();
    }

    @Test
    public void playedSoundsFromResourcesAreCollected() {
        SoundPool soundPool = createSoundPool();
        int soundId = soundPool.load(ApplicationProvider.getApplicationContext(), sound, 1);
        soundPool.play(soundId, 1.0F, 0.0F, 0, 0, 0.5F);
        soundPool.play(soundId, 0.0F, 1.0F, 1, 0, 2.0F);
        assertThat(Shadows.shadowOf(soundPool).getResourcePlaybacks(sound)).containsExactly(new Playback(soundId, 1.0F, 0.0F, 0, 0, 0.5F), new Playback(soundId, 0.0F, 1.0F, 1, 0, 2.0F)).inOrder();
    }

    @Test
    public void playedSoundsFromPathAreRecorded() {
        SoundPool soundPool = createSoundPool();
        int soundId = soundPool.load("/mnt/sdcard/sound.wav", 1);
        soundPool.play(soundId, 1.0F, 1.0F, 1, 0, 1);
        assertThat(Shadows.shadowOf(soundPool).wasPathPlayed("/mnt/sdcard/sound.wav")).isTrue();
    }

    @Test
    public void playedSoundsFromPathAreCollected() {
        SoundPool soundPool = createSoundPool();
        int soundId = soundPool.load("/mnt/sdcard/sound.wav", 1);
        soundPool.play(soundId, 0.0F, 1.0F, 1, 0, 2.0F);
        soundPool.play(soundId, 1.0F, 0.0F, 0, 0, 0.5F);
        assertThat(Shadows.shadowOf(soundPool).getPathPlaybacks("/mnt/sdcard/sound.wav")).containsExactly(new Playback(soundId, 0.0F, 1.0F, 1, 0, 2.0F), new Playback(soundId, 1.0F, 0.0F, 0, 0, 0.5F)).inOrder();
    }

    @Test
    public void notifyPathLoaded_notifiesListener() {
        SoundPool soundPool = createSoundPool();
        SoundPool.OnLoadCompleteListener listener = Mockito.mock(OnLoadCompleteListener.class);
        soundPool.setOnLoadCompleteListener(listener);
        int soundId = soundPool.load("/mnt/sdcard/sound.wav", 1);
        Shadows.shadowOf(soundPool).notifyPathLoaded("/mnt/sdcard/sound.wav", true);
        Mockito.verify(listener).onLoadComplete(soundPool, soundId, 0);
    }

    @Test
    public void notifyResourceLoaded_notifiesListener() {
        SoundPool soundPool = createSoundPool();
        SoundPool.OnLoadCompleteListener listener = Mockito.mock(OnLoadCompleteListener.class);
        soundPool.setOnLoadCompleteListener(listener);
        int soundId = soundPool.load(ApplicationProvider.getApplicationContext(), sound, 1);
        Shadows.shadowOf(soundPool).notifyResourceLoaded(sound, true);
        Mockito.verify(listener).onLoadComplete(soundPool, soundId, 0);
    }

    @Test
    public void notifyPathLoaded_notifiesFailure() {
        SoundPool soundPool = createSoundPool();
        SoundPool.OnLoadCompleteListener listener = Mockito.mock(OnLoadCompleteListener.class);
        soundPool.setOnLoadCompleteListener(listener);
        int soundId = soundPool.load("/mnt/sdcard/sound.wav", 1);
        Shadows.shadowOf(soundPool).notifyPathLoaded("/mnt/sdcard/sound.wav", false);
        Mockito.verify(listener).onLoadComplete(soundPool, soundId, 1);
    }

    @Test
    public void notifyResourceLoaded_doNotFailWithoutListener() {
        SoundPool soundPool = createSoundPool();
        soundPool.load("/mnt/sdcard/sound.wav", 1);
        Shadows.shadowOf(soundPool).notifyPathLoaded("/mnt/sdcard/sound.wav", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notifyPathLoaded_failIfLoadWasntCalled() {
        SoundPool soundPool = createSoundPool();
        Shadows.shadowOf(soundPool).notifyPathLoaded("no.mp3", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notifyResourceLoaded_failIfLoadWasntCalled() {
        SoundPool soundPool = createSoundPool();
        Shadows.shadowOf(soundPool).notifyResourceLoaded(123, true);
    }

    @Test
    public void playedSoundsAreCleared() {
        SoundPool soundPool = createSoundPool();
        int soundId = soundPool.load(ApplicationProvider.getApplicationContext(), sound, 1);
        soundPool.play(soundId, 1.0F, 1.0F, 1, 0, 1);
        assertThat(Shadows.shadowOf(soundPool).wasResourcePlayed(sound)).isTrue();
        Shadows.shadowOf(soundPool).clearPlayed();
        assertThat(Shadows.shadowOf(soundPool).wasResourcePlayed(sound)).isFalse();
    }
}

