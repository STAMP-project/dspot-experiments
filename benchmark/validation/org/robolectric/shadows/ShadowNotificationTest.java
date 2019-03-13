package org.robolectric.shadows;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;


@RunWith(AndroidJUnit4.class)
public class ShadowNotificationTest {
    @Test
    public void setLatestEventInfo__shouldCaptureContentIntent() throws Exception {
        PendingIntent pendingIntent = PendingIntent.getActivity(RuntimeEnvironment.application, 0, new Intent(), 0);
        Notification notification = new Notification();
        notification.setLatestEventInfo(RuntimeEnvironment.application, "title", "content", pendingIntent);
        assertThat(notification.contentIntent).isSameAs(pendingIntent);
    }
}

