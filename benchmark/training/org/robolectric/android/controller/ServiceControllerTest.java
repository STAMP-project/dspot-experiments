package org.robolectric.android.controller;


import Intent.ACTION_VIEW;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowLooper;


@RunWith(AndroidJUnit4.class)
public class ServiceControllerTest {
    private static final List<String> transcript = new ArrayList<>();

    private final ComponentName componentName = new ComponentName("org.robolectric", ServiceControllerTest.MyService.class.getName());

    private final ServiceController<ServiceControllerTest.MyService> controller = Robolectric.buildService(ServiceControllerTest.MyService.class);

    @Test
    public void onBindShouldSetIntent() throws Exception {
        ServiceControllerTest.MyService myService = controller.create().bind().get();
        assertThat(myService.boundIntent).isNotNull();
        assertThat(myService.boundIntent.getComponent()).isEqualTo(componentName);
    }

    @Test
    public void onStartCommandShouldSetIntentAndFlags() throws Exception {
        ServiceControllerTest.MyService myService = controller.create().startCommand(3, 4).get();
        assertThat(myService.startIntent).isNotNull();
        assertThat(myService.startIntent.getComponent()).isEqualTo(componentName);
        assertThat(myService.startFlags).isEqualTo(3);
        assertThat(myService.startId).isEqualTo(4);
    }

    @Test
    public void onBindShouldSetIntentComponentWithCustomIntentWithoutComponentSet() throws Exception {
        ServiceControllerTest.MyService myService = Robolectric.buildService(ServiceControllerTest.MyService.class, new Intent(Intent.ACTION_VIEW)).bind().get();
        assertThat(myService.boundIntent.getAction()).isEqualTo(ACTION_VIEW);
        assertThat(myService.boundIntent.getComponent()).isEqualTo(componentName);
    }

    @Test
    public void shouldSetIntentForGivenServiceInstance() throws Exception {
        ServiceController<ServiceControllerTest.MyService> serviceController = ServiceController.of(new ServiceControllerTest.MyService(), null).bind();
        assertThat(serviceController.get().boundIntent).isNotNull();
    }

    @Test
    public void whenLooperIsNotPaused_shouldCreateWithMainLooperPaused() throws Exception {
        ShadowLooper.unPauseMainLooper();
        controller.create();
        assertThat(Shadows.shadowOf(Looper.getMainLooper()).isPaused()).isFalse();
        assertThat(ServiceControllerTest.transcript).containsExactly("finishedOnCreate", "onCreate");
    }

    @Test
    public void whenLooperIsAlreadyPaused_shouldCreateWithMainLooperPaused() throws Exception {
        ShadowLooper.pauseMainLooper();
        controller.create();
        assertThat(Shadows.shadowOf(Looper.getMainLooper()).isPaused()).isTrue();
        assertThat(ServiceControllerTest.transcript).contains("finishedOnCreate");
        ShadowLooper.unPauseMainLooper();
        assertThat(ServiceControllerTest.transcript).contains("onCreate");
    }

    @Test
    public void unbind_callsUnbindWhilePaused() {
        controller.create().bind().unbind();
        assertThat(ServiceControllerTest.transcript).containsAllOf("finishedOnUnbind", "onUnbind");
    }

    @Test
    public void rebind_callsRebindWhilePaused() {
        controller.create().bind().unbind().bind().rebind();
        assertThat(ServiceControllerTest.transcript).containsAllOf("finishedOnRebind", "onRebind");
    }

    @Test
    public void destroy_callsOnDestroyWhilePaused() {
        controller.create().destroy();
        assertThat(ServiceControllerTest.transcript).containsAllOf("finishedOnDestroy", "onDestroy");
    }

    @Test
    public void bind_callsOnBindWhilePaused() {
        controller.create().bind();
        assertThat(ServiceControllerTest.transcript).containsAllOf("finishedOnBind", "onBind");
    }

    @Test
    public void startCommand_callsOnStartCommandWhilePaused() {
        controller.create().startCommand(1, 2);
        assertThat(ServiceControllerTest.transcript).containsAllOf("finishedOnStartCommand", "onStartCommand");
    }

    public static class MyService extends Service {
        private Handler handler = new Handler(Looper.getMainLooper());

        public Intent boundIntent;

        public Intent reboundIntent;

        public Intent startIntent;

        public int startFlags;

        public int startId;

        public Intent unboundIntent;

        @Override
        public IBinder onBind(Intent intent) {
            boundIntent = intent;
            transcribeWhilePaused("onBind");
            ServiceControllerTest.transcript.add("finishedOnBind");
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            transcribeWhilePaused("onCreate");
            ServiceControllerTest.transcript.add("finishedOnCreate");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            transcribeWhilePaused("onDestroy");
            ServiceControllerTest.transcript.add("finishedOnDestroy");
        }

        @Override
        public void onRebind(Intent intent) {
            reboundIntent = intent;
            transcribeWhilePaused("onRebind");
            ServiceControllerTest.transcript.add("finishedOnRebind");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startIntent = intent;
            startFlags = flags;
            this.startId = startId;
            transcribeWhilePaused("onStartCommand");
            ServiceControllerTest.transcript.add("finishedOnStartCommand");
            return START_STICKY;
        }

        @Override
        public boolean onUnbind(Intent intent) {
            unboundIntent = intent;
            transcribeWhilePaused("onUnbind");
            ServiceControllerTest.transcript.add("finishedOnUnbind");
            return false;
        }

        private void transcribeWhilePaused(final String event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ServiceControllerTest.transcript.add(event);
                }
            });
        }

        private void runOnUiThread(Runnable action) {
            // This is meant to emulate the behavior of Activity.runOnUiThread();
            Shadows.shadowOf(handler.getLooper()).getScheduler().post(action);
        }
    }
}

