package org.robolectric.shadows.support.v4;


import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class NotificationCompatBuilderTest {
    @Test
    public void addAction__shouldAddActionToNotification() {
        NotificationCompat.Action action = build();
        Notification notification = new NotificationCompat.Builder(org.robolectric.RuntimeEnvironment.application).addAction(action).build();
        assertThat(notification.actions).asList().hasSize(1);
    }
}

