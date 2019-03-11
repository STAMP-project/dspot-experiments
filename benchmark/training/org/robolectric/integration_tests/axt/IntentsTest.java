package org.robolectric.integration_tests.axt;


import Intent.ACTION_VIEW;
import Intent.FLAG_ACTIVITY_NEW_TASK;
import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.truth.content.IntentCorrespondences;
import androidx.test.ext.truth.content.IntentSubject;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Integration tests for using ATSL's espresso intents API on Robolectric.
 */
@RunWith(AndroidJUnit4.class)
public class IntentsTest {
    @Test
    public void testNoIntents() {
        Intents.assertNoUnverifiedIntents();
    }

    @Test
    public void testIntendedFailEmpty() {
        try {
            Intents.intended(org.hamcrest.Matchers.any(Intent.class));
        } catch (AssertionError e) {
            // expected
            return;
        }
        Assert.fail("AssertionError not thrown");
    }

    @Test
    public void testIntendedSuccess() {
        Intent i = new Intent();
        i.setAction(ACTION_VIEW);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        Intents.intended(hasAction(ACTION_VIEW));
    }

    @Test
    public void testIntendedNotMatching() {
        Intent i = new Intent();
        i.setAction(ACTION_VIEW);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        try {
            Intents.intended(hasAction(Intent.ACTION_AIRPLANE_MODE_CHANGED));
        } catch (AssertionError e) {
            // expected
            return;
        }
        Assert.fail("intended did not throw");
    }

    /**
     * Variant of testIntendedSuccess that uses truth APIs.
     *
     * <p>In this form the test verifies that only a single intent was sent.
     */
    @Test
    public void testIntendedSuccess_truth() {
        Intent i = new Intent();
        i.setAction(ACTION_VIEW);
        i.putExtra("ignoreextra", "");
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        IntentSubject.assertThat(Iterables.getOnlyElement(getIntents())).hasAction(ACTION_VIEW);
    }

    /**
     * Variant of testIntendedSuccess that uses truth APIs.
     *
     * <p>This is a more flexible/lenient variant of {@link #testIntendedSuccess_truth} that handles
     * cases where other intents might have been sent.
     */
    @Test
    public void testIntendedSuccess_truthCorrespondence() {
        Intent i = new Intent();
        i.setAction(ACTION_VIEW);
        i.putExtra("ignoreextra", "");
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        Intent alsoSentIntentButDontCare = new Intent(Intent.ACTION_MAIN);
        alsoSentIntentButDontCare.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(alsoSentIntentButDontCare);
        IntentSubject.assertThat(getIntents()).comparingElementsUsing(IntentCorrespondences.action()).contains(new Intent(Intent.ACTION_VIEW));
    }

    /**
     * Variant of testIntendedSuccess_truthCorrespondence that uses chained Correspondences.
     */
    @Test
    public void testIntendedSuccess_truthChainedCorrespondence() {
        Intent i = new Intent();
        i.setAction(ACTION_VIEW);
        i.putExtra("ignoreextra", "");
        i.setData(Uri.parse("http://robolectric.org"));
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        Intent alsoSentIntentButNotMatching = new Intent(Intent.ACTION_VIEW);
        alsoSentIntentButNotMatching.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(alsoSentIntentButNotMatching);
        Intent expectedIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://robolectric.org"));
        IntentSubject.assertThat(getIntents()).comparingElementsUsing(IntentCorrespondences.all(IntentCorrespondences.action(), IntentCorrespondences.data())).contains(expectedIntent);
    }

    /**
     * Activity that captures calls to {#onActivityResult() }
     */
    public static class ResultCapturingActivity extends Activity {
        private ActivityResult activityResult;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            activityResult = new ActivityResult(resultCode, data);
        }
    }

    /**
     * Dummy activity whose calls we intent to we're stubbing out.
     */
    public static class DummyActivity extends Activity {}

    @Test
    public void intending_callsOnActivityResult() {
        intending(hasComponent(hasClassName(IntentsTest.DummyActivity.class.getName()))).respondWith(new ActivityResult(Activity.RESULT_OK, new Intent().putExtra("key", 123)));
        ActivityScenario<IntentsTest.ResultCapturingActivity> activityScenario = ActivityScenario.launch(IntentsTest.ResultCapturingActivity.class);
        activityScenario.onActivity(( activity) -> {
            activity.startActivityForResult(new Intent(activity, .class), 0);
        });
        activityScenario.onActivity(( activity) -> {
            assertThat(activity.activityResult.getResultCode()).isEqualTo(Activity.RESULT_OK);
            assertThat(activity.activityResult.getResultData()).extras().containsKey("key");
        });
    }
}

