package org.robolectric.shadows;


import AudioEffect.Descriptor;
import AudioEffect.EFFECT_TYPE_AEC;
import android.media.audiofx.AudioEffect;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowAudioEffectTest {
    @Test
    public void queryEffects() {
        AudioEffect.Descriptor descriptor = new AudioEffect.Descriptor();
        descriptor.type = AudioEffect.EFFECT_TYPE_AEC;
        ShadowAudioEffect.addEffect(descriptor);
        AudioEffect[] descriptors = AudioEffect.queryEffects();
        assertThat(descriptors).asList().hasSize(1);
        assertThat(descriptors[0].type).isEqualTo(EFFECT_TYPE_AEC);
    }
}

