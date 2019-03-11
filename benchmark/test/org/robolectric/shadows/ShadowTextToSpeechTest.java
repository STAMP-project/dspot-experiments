package org.robolectric.shadows;


import Engine.KEY_PARAM_UTTERANCE_ID;
import TextToSpeech.OnInitListener;
import TextToSpeech.QUEUE_ADD;
import TextToSpeech.QUEUE_FLUSH;
import android.app.Activity;
import android.os.Build.VERSION_CODES;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowTextToSpeechTest {
    private TextToSpeech textToSpeech;

    private Activity activity;

    private OnInitListener listener;

    private UtteranceProgressListener mockListener;

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(textToSpeech).isNotNull();
        assertThat(Shadows.shadowOf(textToSpeech)).isNotNull();
    }

    @Test
    public void getContext_shouldReturnContext() throws Exception {
        assertThat(Shadows.shadowOf(textToSpeech).getContext()).isEqualTo(activity);
    }

    @Test
    public void getOnInitListener_shouldReturnListener() throws Exception {
        assertThat(Shadows.shadowOf(textToSpeech).getOnInitListener()).isEqualTo(listener);
    }

    @Test
    public void getLastSpokenText_shouldReturnSpokenText() throws Exception {
        textToSpeech.speak("Hello", QUEUE_FLUSH, null);
        assertThat(Shadows.shadowOf(textToSpeech).getLastSpokenText()).isEqualTo("Hello");
    }

    @Test
    public void getLastSpokenText_shouldReturnMostRecentText() throws Exception {
        textToSpeech.speak("Hello", QUEUE_FLUSH, null);
        textToSpeech.speak("Hi", QUEUE_FLUSH, null);
        assertThat(Shadows.shadowOf(textToSpeech).getLastSpokenText()).isEqualTo("Hi");
    }

    @Test
    public void clearLastSpokenText_shouldSetLastSpokenTextToNull() throws Exception {
        textToSpeech.speak("Hello", QUEUE_FLUSH, null);
        Shadows.shadowOf(textToSpeech).clearLastSpokenText();
        assertThat(Shadows.shadowOf(textToSpeech).getLastSpokenText()).isNull();
    }

    @Test
    public void isShutdown_shouldReturnFalseBeforeShutdown() throws Exception {
        assertThat(Shadows.shadowOf(textToSpeech).isShutdown()).isFalse();
    }

    @Test
    public void isShutdown_shouldReturnTrueAfterShutdown() throws Exception {
        textToSpeech.shutdown();
        assertThat(Shadows.shadowOf(textToSpeech).isShutdown()).isTrue();
    }

    @Test
    public void isStopped_shouldReturnTrueBeforeSpeak() throws Exception {
        assertThat(Shadows.shadowOf(textToSpeech).isStopped()).isTrue();
    }

    @Test
    public void isStopped_shouldReturnTrueAfterStop() throws Exception {
        textToSpeech.stop();
        assertThat(Shadows.shadowOf(textToSpeech).isStopped()).isTrue();
    }

    @Test
    public void isStopped_shouldReturnFalseAfterSpeak() throws Exception {
        textToSpeech.speak("Hello", QUEUE_FLUSH, null);
        assertThat(Shadows.shadowOf(textToSpeech).isStopped()).isFalse();
    }

    @Test
    public void getQueueMode_shouldReturnMostRecentQueueMode() throws Exception {
        textToSpeech.speak("Hello", QUEUE_ADD, null);
        assertThat(Shadows.shadowOf(textToSpeech).getQueueMode()).isEqualTo(QUEUE_ADD);
    }

    @Test
    public void threeArgumentSpeak_withUtteranceId_shouldGetCallbackUtteranceId() throws Exception {
        textToSpeech.setOnUtteranceProgressListener(mockListener);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(KEY_PARAM_UTTERANCE_ID, "ThreeArgument");
        textToSpeech.speak("Hello", QUEUE_FLUSH, paramsMap);
        Robolectric.flushForegroundThreadScheduler();
        Mockito.verify(mockListener).onStart("ThreeArgument");
        Mockito.verify(mockListener).onDone("ThreeArgument");
    }

    @Test
    public void threeArgumentSpeak_withoutUtteranceId_shouldDoesNotGetCallback() throws Exception {
        textToSpeech.setOnUtteranceProgressListener(mockListener);
        textToSpeech.speak("Hello", QUEUE_FLUSH, null);
        Robolectric.flushForegroundThreadScheduler();
        Mockito.verify(mockListener, Mockito.never()).onStart(null);
        Mockito.verify(mockListener, Mockito.never()).onDone(null);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void speak_withUtteranceId_shouldReturnSpokenText() throws Exception {
        textToSpeech.speak("Hello", QUEUE_FLUSH, null, "TTSEnable");
        assertThat(Shadows.shadowOf(textToSpeech).getLastSpokenText()).isEqualTo("Hello");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void onUtteranceProgressListener_shouldGetCallbackUtteranceId() throws Exception {
        textToSpeech.setOnUtteranceProgressListener(mockListener);
        textToSpeech.speak("Hello", QUEUE_FLUSH, null, "TTSEnable");
        Robolectric.flushForegroundThreadScheduler();
        Mockito.verify(mockListener).onStart("TTSEnable");
        Mockito.verify(mockListener).onDone("TTSEnable");
    }
}

