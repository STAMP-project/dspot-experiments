package org.robolectric.integration_tests.axt;


import State.CREATED;
import State.STARTED;
import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Integration tests for {@link ActivityScenario} that verify it behaves consistently on device and
 * Robolectric.
 */
@RunWith(AndroidJUnit4.class)
public class ActivityScenarioTest {
    private static final List<String> callbacks = new ArrayList<>();

    public static class TranscriptActivity extends Activity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ActivityScenarioTest.callbacks.add("onCreate");
        }

        @Override
        public void onStart() {
            super.onStart();
            ActivityScenarioTest.callbacks.add("onStart");
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            ActivityScenarioTest.callbacks.add("onPostCreate");
        }

        @Override
        public void onResume() {
            super.onResume();
            ActivityScenarioTest.callbacks.add("onResume");
        }

        @Override
        public void onPause() {
            super.onPause();
            ActivityScenarioTest.callbacks.add("onPause");
        }

        @Override
        public void onStop() {
            super.onStop();
            ActivityScenarioTest.callbacks.add("onStop");
        }

        @Override
        public void onRestart() {
            super.onRestart();
            ActivityScenarioTest.callbacks.add("onRestart");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ActivityScenarioTest.callbacks.add("onDestroy");
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            ActivityScenarioTest.callbacks.add(("onWindowFocusChanged " + hasFocus));
        }
    }

    public static class LifecycleOwnerActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setTheme(R.style.Theme_AppCompat);
        }
    }

    @Test
    public void launch_callbackSequence() {
        ActivityScenario<ActivityScenarioTest.TranscriptActivity> activityScenario = ActivityScenario.launch(ActivityScenarioTest.TranscriptActivity.class);
        assertThat(activityScenario).isNotNull();
        assertThat(ActivityScenarioTest.callbacks).containsExactly("onCreate", "onStart", "onPostCreate", "onResume", "onWindowFocusChanged true");
    }

    @Test
    public void launch_lifecycleOwnerActivity() {
        ActivityScenario<ActivityScenarioTest.LifecycleOwnerActivity> activityScenario = ActivityScenario.launch(ActivityScenarioTest.LifecycleOwnerActivity.class);
        assertThat(activityScenario).isNotNull();
        activityScenario.onActivity(( activity) -> {
            assertThat(activity.getLifecycle().getCurrentState()).isEqualTo(State.RESUMED);
        });
        activityScenario.moveToState(STARTED);
        activityScenario.onActivity(( activity) -> {
            assertThat(activity.getLifecycle().getCurrentState()).isEqualTo(State.STARTED);
        });
        activityScenario.moveToState(CREATED);
        activityScenario.onActivity(( activity) -> {
            assertThat(activity.getLifecycle().getCurrentState()).isEqualTo(State.CREATED);
        });
    }

    @Test
    public void recreate_retainFragmentHostingActivity() {
        Fragment fragment = new Fragment();
        fragment.setRetainInstance(true);
        ActivityScenario<ActivityScenarioTest.LifecycleOwnerActivity> activityScenario = ActivityScenario.launch(ActivityScenarioTest.LifecycleOwnerActivity.class);
        assertThat(activityScenario).isNotNull();
        activityScenario.onActivity(( activity) -> {
            activity.getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commitNow();
            assertThat(activity.getSupportFragmentManager().findFragmentById(android.R.id.content)).isSameAs(fragment);
        });
        activityScenario.recreate();
        activityScenario.onActivity(( activity) -> assertThat(activity.getSupportFragmentManager().findFragmentById(android.R.id.content)).isSameAs(fragment));
    }

    @Test
    public void recreate_nonRetainFragmentHostingActivity() {
        Fragment fragment = new Fragment();
        fragment.setRetainInstance(false);
        ActivityScenario<ActivityScenarioTest.LifecycleOwnerActivity> activityScenario = ActivityScenario.launch(ActivityScenarioTest.LifecycleOwnerActivity.class);
        assertThat(activityScenario).isNotNull();
        activityScenario.onActivity(( activity) -> {
            activity.getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commitNow();
            assertThat(activity.getSupportFragmentManager().findFragmentById(android.R.id.content)).isSameAs(fragment);
        });
        activityScenario.recreate();
        activityScenario.onActivity(( activity) -> assertThat(activity.getSupportFragmentManager().findFragmentById(android.R.id.content)).isNotSameAs(fragment));
    }
}

