package org.robolectric.android.controller;


import Intent.ACTION_VIEW;
import android.app.IntentService;
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
public class IntentServiceControllerTest {
    private static final List<String> transcript = new ArrayList<>();

    private final ComponentName componentName = new ComponentName("org.robolectric", IntentServiceControllerTest.MyService.class.getName());

    private final IntentServiceController<IntentServiceControllerTest.MyService> controller = Robolectric.buildIntentService(IntentServiceControllerTest.MyService.class, new Intent());

    @Test
    public void onBindShouldSetIntent() throws Exception {
        IntentServiceControllerTest.MyService myService = controller.create().bind().get();
        assertThat(myService.boundIntent).isNotNull();
        assertThat(myService.boundIntent.getComponent()).isEqualTo(componentName);
    }

    @Test
    public void onStartCommandShouldSetIntent() throws Exception {
        IntentServiceControllerTest.MyService myService = controller.create().startCommand(3, 4).get();
        assertThat(myService.startIntent).isNotNull();
        assertThat(myService.startIntent.getComponent()).isEqualTo(componentName);
    }

    @Test
    public void onBindShouldSetIntentComponentWithCustomIntentWithoutComponentSet() throws Exception {
        IntentServiceControllerTest.MyService myService = Robolectric.buildIntentService(IntentServiceControllerTest.MyService.class, new Intent(Intent.ACTION_VIEW)).bind().get();
        assertThat(myService.boundIntent.getAction()).isEqualTo(ACTION_VIEW);
        assertThat(myService.boundIntent.getComponent()).isEqualTo(componentName);
    }

    @Test
    public void shouldSetIntentForGivenServiceInstance() throws Exception {
        IntentServiceController<IntentServiceControllerTest.MyService> intentServiceController = IntentServiceController.of(new IntentServiceControllerTest.MyService(), null).bind();
        assertThat(intentServiceController.get().boundIntent).isNotNull();
    }

    @Test
    public void whenLooperIsNotPaused_shouldCreateWithMainLooperPaused() throws Exception {
        ShadowLooper.unPauseMainLooper();
        controller.create();
        assertThat(Shadows.shadowOf(Looper.getMainLooper()).isPaused()).isFalse();
        assertThat(IntentServiceControllerTest.transcript).containsExactly("finishedOnCreate", "onCreate");
    }

    @Test
    public void whenLooperIsAlreadyPaused_shouldCreateWithMainLooperPaused() throws Exception {
        ShadowLooper.pauseMainLooper();
        controller.create();
        assertThat(Shadows.shadowOf(Looper.getMainLooper()).isPaused()).isTrue();
        assertThat(IntentServiceControllerTest.transcript).contains("finishedOnCreate");
        ShadowLooper.unPauseMainLooper();
        assertThat(IntentServiceControllerTest.transcript).contains("onCreate");
    }

    @Test
    public void unbind_callsUnbindWhilePaused() {
        controller.create().bind().unbind();
        assertThat(IntentServiceControllerTest.transcript).containsAllOf("finishedOnUnbind", "onUnbind");
    }

    @Test
    public void rebind_callsRebindWhilePaused() {
        controller.create().bind().unbind().bind().rebind();
        assertThat(IntentServiceControllerTest.transcript).containsAllOf("finishedOnRebind", "onRebind");
    }

    @Test
    public void destroy_callsOnDestroyWhilePaused() {
        controller.create().destroy();
        assertThat(IntentServiceControllerTest.transcript).containsAllOf("finishedOnDestroy", "onDestroy");
    }

    @Test
    public void bind_callsOnBindWhilePaused() {
        controller.create().bind();
        assertThat(IntentServiceControllerTest.transcript).containsAllOf("finishedOnBind", "onBind");
    }

    @Test
    public void startCommand_callsOnHandleIntentWhilePaused() {
        controller.create().startCommand(1, 2);
        assertThat(IntentServiceControllerTest.transcript).containsAllOf("finishedOnHandleIntent", "onHandleIntent");
    }

    public static class MyService extends IntentService {
        private Handler handler = new Handler(Looper.getMainLooper());

        public Intent boundIntent;

        public Intent reboundIntent;

        public Intent startIntent;

        public Intent unboundIntent;

        public MyService() {
            super("ThreadName");
        }

        @Override
        public IBinder onBind(Intent intent) {
            boundIntent = intent;
            transcribeWhilePaused("onBind");
            IntentServiceControllerTest.transcript.add("finishedOnBind");
            return null;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            startIntent = intent;
            transcribeWhilePaused("onHandleIntent");
            IntentServiceControllerTest.transcript.add("finishedOnHandleIntent");
        }

        @Override
        public void onCreate() {
            super.onCreate();
            transcribeWhilePaused("onCreate");
            IntentServiceControllerTest.transcript.add("finishedOnCreate");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            transcribeWhilePaused("onDestroy");
            IntentServiceControllerTest.transcript.add("finishedOnDestroy");
        }

        @Override
        public void onRebind(Intent intent) {
            reboundIntent = intent;
            transcribeWhilePaused("onRebind");
            IntentServiceControllerTest.transcript.add("finishedOnRebind");
        }

        @Override
        public boolean onUnbind(Intent intent) {
            unboundIntent = intent;
            transcribeWhilePaused("onUnbind");
            IntentServiceControllerTest.transcript.add("finishedOnUnbind");
            return false;
        }

        private void transcribeWhilePaused(final String event) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IntentServiceControllerTest.transcript.add(event);
                }
            });
        }

        private void runOnUiThread(Runnable action) {
            // This is meant to emulate the behavior of Activity.runOnUiThread();
            Shadows.shadowOf(handler.getLooper()).getScheduler().post(action);
        }
    }
}

