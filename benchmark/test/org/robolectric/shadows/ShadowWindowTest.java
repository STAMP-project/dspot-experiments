package org.robolectric.shadows;


import R.drawable.btn_star;
import R.drawable.ic_lock_power_off;
import View.GONE;
import View.INVISIBLE;
import View.VISIBLE;
import WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
import WindowManager.LayoutParams.FLAG_FULLSCREEN;
import android.app.Activity;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowWindowTest {
    @Test
    public void getFlag_shouldReturnWindowFlags() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        Window window = activity.getWindow();
        assertThat(Shadows.shadowOf(window).getFlag(FLAG_FULLSCREEN)).isFalse();
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        assertThat(Shadows.shadowOf(window).getFlag(FLAG_FULLSCREEN)).isTrue();
        window.setFlags(FLAG_ALLOW_LOCK_WHILE_SCREEN_ON, FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        assertThat(Shadows.shadowOf(window).getFlag(FLAG_FULLSCREEN)).isTrue();
    }

    @Test
    public void getTitle_shouldReturnWindowTitle() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        Window window = activity.getWindow();
        window.setTitle("My Window Title");
        assertThat(Shadows.shadowOf(window).getTitle()).isEqualTo("My Window Title");
    }

    @Test
    public void getBackgroundDrawable_returnsSetDrawable() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        Window window = activity.getWindow();
        ShadowWindow shadowWindow = Shadows.shadowOf(window);
        assertThat(shadowWindow.getBackgroundDrawable()).isNull();
        window.setBackgroundDrawableResource(btn_star);
        assertThat(Shadows.shadowOf(shadowWindow.getBackgroundDrawable()).createdFromResId).isEqualTo(btn_star);
    }

    @Test
    public void getSoftInputMode_returnsSoftInputMode() throws Exception {
        ShadowWindowTest.TestActivity activity = Robolectric.buildActivity(ShadowWindowTest.TestActivity.class).create().get();
        Window window = getWindow();
        ShadowWindow shadowWindow = Shadows.shadowOf(window);
        window.setSoftInputMode(7);
        assertThat(shadowWindow.getSoftInputMode()).isEqualTo(7);
    }

    @Test
    public void getProgressBar_returnsTheProgressBar() {
        Activity activity = Robolectric.buildActivity(ShadowWindowTest.TestActivity.class).create().get();
        ProgressBar progress = Shadows.shadowOf(activity.getWindow()).getProgressBar();
        assertThat(progress.getVisibility()).isEqualTo(INVISIBLE);
        activity.setProgressBarVisibility(true);
        assertThat(progress.getVisibility()).isEqualTo(VISIBLE);
        activity.setProgressBarVisibility(false);
        assertThat(progress.getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void getIndeterminateProgressBar_returnsTheIndeterminateProgressBar() {
        ActivityController<ShadowWindowTest.TestActivity> testActivityActivityController = Robolectric.buildActivity(ShadowWindowTest.TestActivity.class);
        ShadowWindowTest.TestActivity activity = testActivityActivityController.get();
        activity.requestFeature = Window.FEATURE_INDETERMINATE_PROGRESS;
        testActivityActivityController.create();
        ProgressBar indeterminate = Shadows.shadowOf(activity.getWindow()).getIndeterminateProgressBar();
        assertThat(indeterminate.getVisibility()).isEqualTo(INVISIBLE);
        setProgressBarIndeterminateVisibility(true);
        assertThat(indeterminate.getVisibility()).isEqualTo(VISIBLE);
        setProgressBarIndeterminateVisibility(false);
        assertThat(indeterminate.getVisibility()).isEqualTo(GONE);
    }

    @Test
    @Config(maxSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void forPreM_create_shouldCreateImplPhoneWindow() throws Exception {
        assertThat(ShadowWindow.create(ApplicationProvider.getApplicationContext()).getClass().getName()).isEqualTo("com.android.internal.policy.impl.PhoneWindow");
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void forM_create_shouldCreatePhoneWindow() throws Exception {
        assertThat(ShadowWindow.create(ApplicationProvider.getApplicationContext()).getClass().getName()).isEqualTo("com.android.internal.policy.PhoneWindow");
    }

    public static class TestActivity extends Activity {
        public int requestFeature = Window.FEATURE_PROGRESS;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTheme(R.style.Theme_Holo_Light);
            getWindow().requestFeature(requestFeature);
            setContentView(new LinearLayout(this));
            getActionBar().setIcon(ic_lock_power_off);
        }
    }
}

