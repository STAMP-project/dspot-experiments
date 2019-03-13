package org.robolectric.shadows;


import android.app.IntentService;
import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowIntentServiceTest {
    @Test
    public void shouldSetIntentRedelivery() {
        IntentService intentService = new ShadowIntentServiceTest.TestIntentService();
        ShadowIntentService shadowIntentService = Shadows.shadowOf(intentService);
        assertThat(shadowIntentService.getIntentRedelivery()).isFalse();
        intentService.setIntentRedelivery(true);
        assertThat(shadowIntentService.getIntentRedelivery()).isTrue();
        intentService.setIntentRedelivery(false);
        assertThat(shadowIntentService.getIntentRedelivery()).isFalse();
    }

    private static class TestIntentService extends IntentService {
        public TestIntentService() {
            super("TestIntentService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }
    }
}

