package org.robolectric.shadows;


import Context.NOTIFICATION_SERVICE;
import Notification.Builder;
import RuntimeEnvironment.application;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaScannerConnection;
import android.os.IBinder;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowServiceTest {
    private ShadowServiceTest.MyService service;

    private Builder notBuilder;

    NotificationManager nm2 = ((NotificationManager) (ApplicationProvider.getApplicationContext().getSystemService(NOTIFICATION_SERVICE)));

    @Test
    public void shouldUnbindServiceAtShadowApplication() {
        Application application = ((Application) (ApplicationProvider.getApplicationContext()));
        ServiceConnection conn = Shadow.newInstanceOf(MediaScannerConnection.class);
        service.bindService(new Intent("dummy"), conn, 0);
        assertThat(Shadows.shadowOf(application).getUnboundServiceConnections()).isEmpty();
        service.unbindService(conn);
        assertThat(Shadows.shadowOf(application).getUnboundServiceConnections()).hasSize(1);
    }

    @Test
    public void shouldUnbindServiceSuccessfully() {
        ServiceConnection conn = Shadow.newInstanceOf(MediaScannerConnection.class);
        service.unbindService(conn);
    }

    @Test
    public void shouldUnbindServiceWithExceptionWhenRequested() {
        Shadows.shadowOf(application).setUnbindServiceShouldThrowIllegalArgument(true);
        ServiceConnection conn = Shadow.newInstanceOf(MediaScannerConnection.class);
        try {
            service.unbindService(conn);
            Assert.fail("Should throw");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void startForeground() {
        Notification n = notBuilder.build();
        service.startForeground(23, n);
        assertThat(Shadows.shadowOf(service).getLastForegroundNotification()).isSameAs(n);
        assertThat(Shadows.shadowOf(service).getLastForegroundNotificationId()).isEqualTo(23);
        assertThat(Shadows.shadowOf(nm2).getNotification(23)).isSameAs(n);
        assertThat(((n.flags) & (Notification.FLAG_FOREGROUND_SERVICE))).isNotEqualTo(0);
    }

    @Test
    public void stopForeground() {
        service.stopForeground(true);
        assertThat(Shadows.shadowOf(service).isForegroundStopped()).isTrue();
        assertThat(Shadows.shadowOf(service).getNotificationShouldRemoved()).isTrue();
    }

    @Test
    public void stopForegroundRemovesNotificationIfAsked() {
        service.startForeground(21, notBuilder.build());
        service.stopForeground(true);
        assertThat(Shadows.shadowOf(nm2).getNotification(21)).isNull();
    }

    /**
     * According to spec, if the foreground notification is not removed earlier,
     * then it will be removed when the service is destroyed.
     */
    @Test
    public void stopForegroundDoesntRemoveNotificationUnlessAsked() {
        Notification n = notBuilder.build();
        service.startForeground(21, n);
        service.stopForeground(false);
        assertThat(Shadows.shadowOf(nm2).getNotification(21)).isSameAs(n);
    }

    /**
     * According to spec, if the foreground notification is not removed earlier,
     * then it will be removed when the service is destroyed.
     */
    @Test
    public void onDestroyRemovesNotification() {
        Notification n = notBuilder.build();
        service.startForeground(21, n);
        service.onDestroy();
        assertThat(Shadows.shadowOf(nm2).getNotification(21)).isNull();
    }

    @Test
    public void shouldStopSelf() {
        service.stopSelf();
        assertThat(Shadows.shadowOf(service).isStoppedBySelf()).isTrue();
    }

    @Test
    public void shouldStopSelfWithId() {
        stopSelf(1);
        assertThat(Shadows.shadowOf(service).isStoppedBySelf()).isTrue();
        assertThat(Shadows.shadowOf(service).getStopSelfId()).isEqualTo(1);
    }

    @Test
    public void shouldStopSelfResultWithId() {
        stopSelfResult(1);
        assertThat(Shadows.shadowOf(service).isStoppedBySelf()).isTrue();
        assertThat(Shadows.shadowOf(service).getStopSelfResultId()).isEqualTo(1);
    }

    public static class MyService extends Service {
        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}

