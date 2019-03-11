package org.robolectric.shadows;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowMessengerTest {
    @Test
    public void testMessengerSend() throws Exception {
        Handler handler = new Handler();
        Messenger messenger = new Messenger(handler);
        ShadowLooper.pauseMainLooper();
        Message msg = Message.obtain(null, 123);
        messenger.send(msg);
        assertThat(handler.hasMessages(123)).isTrue();
        Looper looper = Looper.myLooper();
        Shadows.shadowOf(looper).runOneTask();
        assertThat(handler.hasMessages(123)).isFalse();
    }

    @Test
    public void getLastMessageSentShouldWork() throws Exception {
        Handler handler = new Handler();
        Messenger messenger = new Messenger(handler);
        Message msg = Message.obtain(null, 123);
        Message originalMessage = Message.obtain(msg);
        messenger.send(msg);
        assertThat(ShadowMessenger.getLastMessageSent().what).isEqualTo(originalMessage.what);
    }

    @Test
    public void createMessengerWithBinder_getLastMessageSentShouldWork() throws Exception {
        Handler handler = new Handler();
        Messenger messenger = new Messenger(getBinder());
        Message msg = Message.obtain(null, 123);
        Message originalMessage = Message.obtain(msg);
        messenger.send(msg);
        assertThat(ShadowMessenger.getLastMessageSent().what).isEqualTo(originalMessage.what);
    }
}

