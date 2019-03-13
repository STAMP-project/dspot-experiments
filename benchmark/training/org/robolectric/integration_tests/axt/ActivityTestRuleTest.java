package org.robolectric.integration_tests.axt;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Integration tests for {@link ActivityTestRule} that verify it behaves consistently on device and
 * Robolectric.
 */
@RunWith(AndroidJUnit4.class)
public class ActivityTestRuleTest {
    private static final List<String> callbacks = new ArrayList<>();

    @Rule
    public ActivityTestRule<ActivityTestRuleTest.TranscriptActivity> rule = new ActivityTestRule<ActivityTestRuleTest.TranscriptActivity>(ActivityTestRuleTest.TranscriptActivity.class, false, false) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            ActivityTestRuleTest.callbacks.add("beforeActivityLaunched");
        }

        @Override
        protected void afterActivityLaunched() {
            ActivityTestRuleTest.callbacks.add("afterActivityLaunched");
            super.afterActivityLaunched();
        }

        @Override
        protected void afterActivityFinished() {
            ActivityTestRuleTest.callbacks.add("afterActivityFinished");
            super.afterActivityFinished();
        }
    };

    public static class TranscriptActivity extends Activity {
        Bundle receivedBundle;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            receivedBundle = savedInstanceState;
            ActivityTestRuleTest.callbacks.add("onCreate");
        }

        @Override
        public void onStart() {
            super.onStart();
            ActivityTestRuleTest.callbacks.add("onStart");
        }

        @Override
        public void onResume() {
            super.onResume();
            ActivityTestRuleTest.callbacks.add("onResume");
        }

        @Override
        public void onPause() {
            super.onPause();
            ActivityTestRuleTest.callbacks.add("onPause");
        }

        @Override
        public void onStop() {
            super.onStop();
            ActivityTestRuleTest.callbacks.add("onStop");
        }

        @Override
        public void onRestart() {
            super.onRestart();
            ActivityTestRuleTest.callbacks.add("onRestart");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ActivityTestRuleTest.callbacks.add("onDestroy");
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            ActivityTestRuleTest.callbacks.add(("onWindowFocusChanged " + hasFocus));
        }
    }

    @Test
    public void launchActivity_callbackSequence() {
        ActivityTestRuleTest.TranscriptActivity activity = rule.launchActivity(null);
        assertThat(activity).isNotNull();
        assertThat(ActivityTestRuleTest.callbacks).containsExactly("beforeActivityLaunched", "onCreate", "onStart", "onResume", "onWindowFocusChanged true", "afterActivityLaunched");
    }

    /**
     * Starting an activity with options is currently not supported, so check that received bundle is
     * always null in both modes.
     */
    @Test
    public void launchActivity_bundle() {
        ActivityTestRuleTest.TranscriptActivity activity = rule.launchActivity(null);
        assertThat(activity.receivedBundle).isNull();
    }

    @Test
    public void launchActivity_intentExtras() {
        Intent intent = new Intent();
        intent.putExtra("Key", "Value");
        ActivityTestRuleTest.TranscriptActivity activity = rule.launchActivity(intent);
        Intent activityIntent = getIntent();
        assertThat(activityIntent.getExtras()).isNotNull();
        assertThat(activityIntent.getStringExtra("Key")).isEqualTo("Value");
    }

    @Test
    public void finishActivity() {
        rule.launchActivity(null);
        ActivityTestRuleTest.callbacks.clear();
        rule.finishActivity();
        assertThat(ActivityTestRuleTest.callbacks).contains("afterActivityFinished");
        // TODO: On-device this will also invoke onPause windowFocusChanged false
        // need to track activity state and respond accordingly in robolectric
    }
}

