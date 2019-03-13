package org.robolectric;


import Intent.FLAG_ACTIVITY_NEW_TASK;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowView;
import org.robolectric.util.ReflectionHelpers;


@RunWith(AndroidJUnit4.class)
public class RobolectricTest {
    private PrintStream originalSystemOut;

    private ByteArrayOutputStream buff;

    private String defaultLineSeparator;

    private Application context;

    @Test(expected = RuntimeException.class)
    public void clickOn_shouldThrowIfViewIsDisabled() throws Exception {
        View view = new View(context);
        view.setEnabled(false);
        ShadowView.clickOn(view);
    }

    @Test
    public void shouldResetBackgroundSchedulerBeforeTests() throws Exception {
        assertThat(Robolectric.getBackgroundThreadScheduler().isPaused()).isFalse();
        Robolectric.getBackgroundThreadScheduler().pause();
    }

    @Test
    public void shouldResetBackgroundSchedulerAfterTests() throws Exception {
        assertThat(Robolectric.getBackgroundThreadScheduler().isPaused()).isFalse();
        Robolectric.getBackgroundThreadScheduler().pause();
    }

    @Test
    public void idleMainLooper_executesScheduledTasks() {
        final boolean[] wasRun = new boolean[]{ false };
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wasRun[0] = true;
            }
        }, 2000);
        Assert.assertFalse(wasRun[0]);
        ShadowLooper.idleMainLooper(1999);
        Assert.assertFalse(wasRun[0]);
        ShadowLooper.idleMainLooper(1);
        Assert.assertTrue(wasRun[0]);
    }

    @Test
    public void clickOn_shouldCallClickListener() throws Exception {
        View view = new View(context);
        Shadows.shadowOf(view).setMyParent(ReflectionHelpers.createNullProxy(ViewParent.class));
        OnClickListener testOnClickListener = Mockito.mock(OnClickListener.class);
        view.setOnClickListener(testOnClickListener);
        ShadowView.clickOn(view);
        Mockito.verify(testOnClickListener).onClick(view);
    }

    @Test(expected = ActivityNotFoundException.class)
    public void checkActivities_shouldSetValueOnShadowApplication() throws Exception {
        ShadowApplication.getInstance().checkActivities(true);
        context.startActivity(new Intent("i.dont.exist.activity").addFlags(FLAG_ACTIVITY_NEW_TASK));
    }

    @Test
    @Config(sdk = 16)
    public void setupActivity_returnsAVisibleActivity() throws Exception {
        RobolectricTest.LifeCycleActivity activity = Robolectric.setupActivity(RobolectricTest.LifeCycleActivity.class);
        assertThat(activity.isCreated()).isTrue();
        assertThat(activity.isStarted()).isTrue();
        assertThat(isResumed()).isTrue();
        assertThat(activity.isVisible()).isTrue();
    }

    @Implements(View.class)
    public static class TestShadowView {
        @Implementation
        protected Context getContext() {
            return null;
        }
    }

    private static class LifeCycleActivity extends Activity {
        private boolean created;

        private boolean started;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            created = true;
        }

        @Override
        protected void onStart() {
            super.onStart();
            started = true;
        }

        public boolean isStarted() {
            return started;
        }

        public boolean isCreated() {
            return created;
        }

        public boolean isVisible() {
            return (getWindow().getDecorView().getWindowToken()) != null;
        }
    }
}

