package org.robolectric.shadows;


import Activity.RESULT_OK;
import ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import AudioManager.STREAM_ALARM;
import Context.MODE_PRIVATE;
import Manifest.permission;
import ShadowActivity.IntentForResult;
import ShadowActivity.PermissionsRequest;
import Window.FEATURE_ACTION_BAR;
import android.R.id.content;
import android.R.style.Theme_NoDisplay;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewRootImpl;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.util.TestRunnable;

import static org.robolectric.R.anim.test_anim_1;
import static org.robolectric.R.id.action_search;
import static org.robolectric.R.id.burritos;
import static org.robolectric.R.id.button;
import static org.robolectric.R.id.title;
import static org.robolectric.R.layout.main;
import static org.robolectric.R.layout.toplevel_merge;
import static org.robolectric.R.menu.action_menu;
import static org.robolectric.R.string.activity_name;
import static org.robolectric.R.string.app_name;


/**
 * Test of ShadowActivity.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowActivityTest {
    private Activity activity;

    @Test
    public void shouldUseApplicationLabelFromManifestAsTitleForActivity() throws Exception {
        activity = Robolectric.setupActivity(ShadowActivityTest.LabelTestActivity1.class);
        assertThat(activity.getTitle()).isNotNull();
        assertThat(activity.getTitle().toString()).isEqualTo(activity.getString(app_name));
    }

    @Test
    public void shouldUseActivityLabelFromManifestAsTitleForActivity() throws Exception {
        activity = Robolectric.setupActivity(ShadowActivityTest.LabelTestActivity2.class);
        assertThat(activity.getTitle()).isNotNull();
        assertThat(activity.getTitle().toString()).isEqualTo(activity.getString(activity_name));
    }

    @Test
    public void shouldUseActivityLabelFromManifestAsTitleForActivityWithShortName() throws Exception {
        activity = Robolectric.setupActivity(ShadowActivityTest.LabelTestActivity3.class);
        assertThat(activity.getTitle()).isNotNull();
        assertThat(activity.getTitle().toString()).isEqualTo(activity.getString(activity_name));
    }

    @Test
    public void createActivity_noDisplayFinished_shouldFinishActivity() {
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        controller.get().setTheme(Theme_NoDisplay);
        controller.create();
        controller.get().finish();
        controller.start().visible().resume();
        activity = controller.get();
        assertThat(activity.isFinishing()).isTrue();
    }

    @Config(minSdk = VERSION_CODES.M)
    @Test
    public void createActivity_noDisplayNotFinished_shouldThrowIllegalStateException() {
        try {
            ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
            controller.get().setTheme(Theme_NoDisplay);
            controller.setup();
            // For apps targeting above Lollipop MR1, an exception "Activity <activity> did not call
            // finish() prior to onResume() completing" will be thrown
            Assert.fail("IllegalStateException should be thrown");
        } catch (IllegalStateException e) {
            // pass
        }
    }

    public static final class LabelTestActivity1 extends Activity {}

    public static final class LabelTestActivity2 extends Activity {}

    public static final class LabelTestActivity3 extends Activity {}

    @Test
    public void shouldNotComplainIfActivityIsDestroyedWhileAnotherActivityHasRegisteredBroadcastReceivers() throws Exception {
        ActivityController<ShadowActivityTest.DialogCreatingActivity> controller = Robolectric.buildActivity(ShadowActivityTest.DialogCreatingActivity.class);
        activity = controller.get();
        ShadowActivityTest.DialogLifeCycleActivity activity2 = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        activity2.registerReceiver(new AppWidgetProvider(), new IntentFilter());
        controller.destroy();
    }

    @Test
    public void shouldNotRegisterNullBroadcastReceiver() {
        ActivityController<ShadowActivityTest.DialogCreatingActivity> controller = Robolectric.buildActivity(ShadowActivityTest.DialogCreatingActivity.class);
        activity = controller.get();
        activity.registerReceiver(null, new IntentFilter());
        controller.destroy();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void shouldReportDestroyedStatus() {
        ActivityController<ShadowActivityTest.DialogCreatingActivity> controller = Robolectric.buildActivity(ShadowActivityTest.DialogCreatingActivity.class);
        activity = controller.get();
        controller.destroy();
        assertThat(activity.isDestroyed()).isTrue();
    }

    @Test
    public void startActivity_shouldDelegateToStartActivityForResult() {
        ShadowActivityTest.TranscriptActivity activity = Robolectric.setupActivity(ShadowActivityTest.TranscriptActivity.class);
        activity.startActivity(new Intent().setType("image/*"));
        Shadows.shadowOf(activity).receiveResult(new Intent().setType("image/*"), RESULT_OK, new Intent().setData(Uri.parse("content:foo")));
        assertThat(activity.transcript).containsExactly("onActivityResult called with requestCode -1, resultCode -1, intent data content:foo");
    }

    public static class TranscriptActivity extends Activity {
        final List<String> transcript = new ArrayList<>();

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            transcript.add(((((("onActivityResult called with requestCode " + requestCode) + ", resultCode ") + resultCode) + ", intent data ") + (data.getData())));
        }
    }

    @Test
    public void startActivities_shouldStartAllActivities() {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        final Intent view = new Intent(Intent.ACTION_VIEW);
        final Intent pick = new Intent(Intent.ACTION_PICK);
        activity.startActivities(new Intent[]{ view, pick });
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).isEqualTo(pick);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).isEqualTo(view);
    }

    @Test
    public void startActivities_withBundle_shouldStartAllActivities() {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        final Intent view = new Intent(Intent.ACTION_VIEW);
        final Intent pick = new Intent(Intent.ACTION_PICK);
        activity.startActivities(new Intent[]{ view, pick }, new Bundle());
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).isEqualTo(pick);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).isEqualTo(view);
    }

    @Test
    public void startActivityForResultAndReceiveResult_shouldSendResponsesBackToActivity() throws Exception {
        ShadowActivityTest.TranscriptActivity activity = Robolectric.setupActivity(ShadowActivityTest.TranscriptActivity.class);
        activity.startActivityForResult(new Intent().setType("audio/*"), 123);
        activity.startActivityForResult(new Intent().setType("image/*"), 456);
        Shadows.shadowOf(activity).receiveResult(new Intent().setType("image/*"), RESULT_OK, new Intent().setData(Uri.parse("content:foo")));
        assertThat(activity.transcript).containsExactly("onActivityResult called with requestCode 456, resultCode -1, intent data content:foo");
    }

    @Test
    public void startActivityForResultAndReceiveResult_whenNoIntentMatches_shouldThrowException() throws Exception {
        ShadowActivityTest.ThrowOnResultActivity activity = Robolectric.buildActivity(ShadowActivityTest.ThrowOnResultActivity.class).get();
        activity.startActivityForResult(new Intent().setType("audio/*"), 123);
        activity.startActivityForResult(new Intent().setType("image/*"), 456);
        Intent requestIntent = new Intent().setType("video/*");
        try {
            Shadows.shadowOf(activity).receiveResult(requestIntent, RESULT_OK, new Intent().setData(Uri.parse("content:foo")));
            Assert.fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).startsWith(("No intent matches " + requestIntent));
        }
    }

    public static class ThrowOnResultActivity extends Activity {
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            throw new IllegalStateException("should not be called");
        }
    }

    @Test
    public void shouldSupportStartActivityForResult() throws Exception {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        Intent intent = new Intent().setClass(activity, ShadowActivityTest.DialogLifeCycleActivity.class);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).isNull();
        activity.startActivityForResult(intent, 142);
        Intent startedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertThat(startedIntent).isNotNull();
        assertThat(startedIntent).isSameAs(intent);
    }

    @Test
    public void shouldSupportGetStartedActivitiesForResult() throws Exception {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        Intent intent = new Intent().setClass(activity, ShadowActivityTest.DialogLifeCycleActivity.class);
        activity.startActivityForResult(intent, 142);
        ShadowActivity.IntentForResult intentForResult = Shadows.shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult).isNotNull();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivityForResult()).isNull();
        assertThat(intentForResult.intent).isNotNull();
        assertThat(intentForResult.intent).isSameAs(intent);
        assertThat(intentForResult.requestCode).isEqualTo(142);
    }

    @Test
    public void shouldSupportPeekStartedActivitiesForResult() throws Exception {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        Intent intent = new Intent().setClass(activity, ShadowActivityTest.DialogLifeCycleActivity.class);
        activity.startActivityForResult(intent, 142);
        ShadowActivity.IntentForResult intentForResult = Shadows.shadowOf(activity).peekNextStartedActivityForResult();
        assertThat(intentForResult).isNotNull();
        assertThat(Shadows.shadowOf(activity).peekNextStartedActivityForResult()).isSameAs(intentForResult);
        assertThat(intentForResult.intent).isNotNull();
        assertThat(intentForResult.intent).isSameAs(intent);
        assertThat(intentForResult.requestCode).isEqualTo(142);
    }

    @Test
    public void onContentChangedShouldBeCalledAfterContentViewIsSet() throws RuntimeException {
        final List<String> transcript = new ArrayList<>();
        ShadowActivityTest.ActivityWithContentChangedTranscript customActivity = Robolectric.setupActivity(ShadowActivityTest.ActivityWithContentChangedTranscript.class);
        customActivity.setTranscript(transcript);
        customActivity.setContentView(main);
        assertThat(transcript).containsExactly("onContentChanged was called; title is \"Main Layout\"");
    }

    @Test
    public void shouldRetrievePackageNameFromTheManifest() throws Exception {
        assertThat(Robolectric.setupActivity(Activity.class).getPackageName()).isEqualTo(ApplicationProvider.getApplicationContext().getPackageName());
    }

    @Test
    public void shouldRunUiTasksImmediatelyByDefault() throws Exception {
        TestRunnable runnable = new TestRunnable();
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        activity.runOnUiThread(runnable);
        Assert.assertTrue(runnable.wasRun);
    }

    @Test
    public void shouldQueueUiTasksWhenUiThreadIsPaused() throws Exception {
        ShadowLooper.pauseMainLooper();
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        TestRunnable runnable = new TestRunnable();
        activity.runOnUiThread(runnable);
        Assert.assertFalse(runnable.wasRun);
        ShadowLooper.unPauseMainLooper();
        Assert.assertTrue(runnable.wasRun);
    }

    @Test
    public void showDialog_shouldCreatePrepareAndShowDialog() {
        final ShadowActivityTest.DialogLifeCycleActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        final AtomicBoolean dialogWasShown = new AtomicBoolean(false);
        new Dialog(activity) {
            {
                activity.dialog = this;
            }

            @Override
            public void show() {
                dialogWasShown.set(true);
            }
        };
        showDialog(1);
        Assert.assertTrue(activity.createdDialog);
        Assert.assertTrue(activity.preparedDialog);
        Assert.assertTrue(dialogWasShown.get());
    }

    @Test
    public void showDialog_shouldCreatePrepareAndShowDialogWithBundle() {
        final ShadowActivityTest.DialogLifeCycleActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        final AtomicBoolean dialogWasShown = new AtomicBoolean(false);
        new Dialog(activity) {
            {
                activity.dialog = this;
            }

            @Override
            public void show() {
                dialogWasShown.set(true);
            }
        };
        activity.showDialog(1, new Bundle());
        Assert.assertTrue(activity.createdDialog);
        Assert.assertTrue(activity.preparedDialogWithBundle);
        Assert.assertTrue(dialogWasShown.get());
    }

    @Test
    public void showDialog_shouldReturnFalseIfDialogDoesNotExist() {
        final ShadowActivityTest.DialogLifeCycleActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        boolean dialogCreated = activity.showDialog(97, new Bundle());
        assertThat(dialogCreated).isFalse();
        assertThat(activity.createdDialog).isTrue();
        assertThat(activity.preparedDialogWithBundle).isFalse();
    }

    @Test
    public void showDialog_shouldReuseDialogs() {
        final ShadowActivityTest.DialogCreatingActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogCreatingActivity.class);
        showDialog(1);
        Dialog firstDialog = ShadowDialog.getLatestDialog();
        showDialog(1);
        Dialog secondDialog = ShadowDialog.getLatestDialog();
        Assert.assertSame("dialogs should be the same instance", firstDialog, secondDialog);
    }

    @Test
    public void showDialog_shouldShowDialog() throws Exception {
        final ShadowActivityTest.DialogCreatingActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogCreatingActivity.class);
        showDialog(1);
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertTrue(dialog.isShowing());
    }

    @Test
    public void dismissDialog_shouldDismissPreviouslyShownDialog() throws Exception {
        final ShadowActivityTest.DialogCreatingActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogCreatingActivity.class);
        showDialog(1);
        dismissDialog(1);
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertFalse(dialog.isShowing());
    }

    @Test
    public void dismissDialog_shouldThrowExceptionIfDialogWasNotPreviouslyShown() throws Exception {
        final ShadowActivityTest.DialogCreatingActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogCreatingActivity.class);
        try {
            dismissDialog(1);
        } catch (Throwable expected) {
            assertThat(expected).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void removeDialog_shouldCreateDialogAgain() {
        final ShadowActivityTest.DialogCreatingActivity activity = Robolectric.setupActivity(ShadowActivityTest.DialogCreatingActivity.class);
        showDialog(1);
        Dialog firstDialog = ShadowDialog.getLatestDialog();
        removeDialog(1);
        Assert.assertNull(Shadows.shadowOf(activity).getDialogById(1));
        showDialog(1);
        Dialog secondDialog = ShadowDialog.getLatestDialog();
        Assert.assertNotSame("dialogs should not be the same instance", firstDialog, secondDialog);
    }

    @Test
    public void shouldCallOnCreateDialogFromShowDialog() {
        ShadowActivityTest.ActivityWithOnCreateDialog activity = Robolectric.setupActivity(ShadowActivityTest.ActivityWithOnCreateDialog.class);
        showDialog(123);
        Assert.assertTrue(activity.onCreateDialogWasCalled);
        assertThat(ShadowDialog.getLatestDialog()).isNotNull();
    }

    @Test
    public void shouldCallFinishInOnBackPressed() {
        Activity activity = new Activity();
        activity.onBackPressed();
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN)
    public void shouldCallFinishOnFinishAffinity() {
        Activity activity = new Activity();
        activity.finishAffinity();
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldCallFinishOnFinishAndRemoveTask() {
        Activity activity = new Activity();
        activity.finishAndRemoveTask();
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    public void shouldCallFinishOnFinish() {
        Activity activity = new Activity();
        activity.finish();
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    public void shouldSupportCurrentFocus() {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        Assert.assertNull(activity.getCurrentFocus());
        View view = new View(activity);
        Shadows.shadowOf(activity).setCurrentFocus(view);
        Assert.assertEquals(view, activity.getCurrentFocus());
    }

    @Test
    public void shouldSetOrientation() {
        activity = Robolectric.setupActivity(ShadowActivityTest.DialogLifeCycleActivity.class);
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        assertThat(activity.getRequestedOrientation()).isEqualTo(SCREEN_ORIENTATION_PORTRAIT);
    }

    @Test
    public void setDefaultKeyMode_shouldSetKeyMode() {
        int[] modes = new int[]{ Activity.DEFAULT_KEYS_DISABLE, Activity.DEFAULT_KEYS_SHORTCUT, Activity.DEFAULT_KEYS_DIALER, Activity.DEFAULT_KEYS_SEARCH_LOCAL, Activity.DEFAULT_KEYS_SEARCH_GLOBAL };
        Activity activity = new Activity();
        for (int mode : modes) {
            activity.setDefaultKeyMode(mode);
            assertThat(Shadows.shadowOf(activity).getDefaultKeymode()).named("Unexpected key mode").isEqualTo(mode);
        }
    }

    // unclear what the correct behavior should be here...
    @Test
    public void shouldPopulateWindowDecorViewWithMergeLayoutContents() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.setContentView(toplevel_merge);
        View contentView = activity.findViewById(content);
        assertThat(getChildCount()).isEqualTo(2);
    }

    @Test
    public void setContentView_shouldReplaceOldContentView() throws Exception {
        View view1 = new View(RuntimeEnvironment.application);
        view1.setId(burritos);
        View view2 = new View(RuntimeEnvironment.application);
        view2.setId(button);
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.setContentView(view1);
        Assert.assertSame(view1, activity.findViewById(burritos));
        activity.setContentView(view2);
        Assert.assertNull(activity.findViewById(burritos));
        Assert.assertSame(view2, activity.findViewById(button));
    }

    @Test
    public void onKeyUp_callsOnBackPressedWhichFinishesTheActivity() throws Exception {
        ShadowActivityTest.OnBackPressedActivity activity = Robolectric.buildActivity(ShadowActivityTest.OnBackPressedActivity.class).setup().get();
        boolean downConsumed = dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        boolean upConsumed = dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        Assert.assertTrue(downConsumed);
        Assert.assertTrue(upConsumed);
        Assert.assertTrue(activity.onBackPressedCalled);
        Assert.assertTrue(isFinishing());
    }

    @Test
    public void shouldGiveSharedPreferences() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        SharedPreferences preferences = activity.getPreferences(MODE_PRIVATE);
        Assert.assertNotNull(preferences);
        preferences.edit().putString("foo", "bar").commit();
        assertThat(activity.getPreferences(MODE_PRIVATE).getString("foo", null)).isEqualTo("bar");
    }

    @Test
    public void shouldFindContentViewContainerWithChild() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View contentView = new View(activity);
        activity.setContentView(contentView);
        FrameLayout contentViewContainer = ((FrameLayout) (activity.findViewById(content)));
        assertThat(contentViewContainer.getChildAt(0)).isSameAs(contentView);
    }

    @Test
    public void shouldFindContentViewContainerWithoutChild() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        FrameLayout contentViewContainer = ((FrameLayout) (activity.findViewById(content)));
        assertThat(contentViewContainer.getId()).isEqualTo(content);
    }

    @Test
    public void recreateGoesThroughFullLifeCycle() throws Exception {
        ActivityController<ShadowActivityTest.TestActivity> activityController = Robolectric.buildActivity(ShadowActivityTest.TestActivity.class);
        ShadowActivityTest.TestActivity oldActivity = activityController.get();
        // recreate should create new instance
        activityController.recreate();
        assertThat(activityController.get()).isNotSameAs(oldActivity);
        assertThat(oldActivity.transcript).containsExactly("onSaveInstanceState", "onPause", "onStop", "onRetainNonConfigurationInstance", "onDestroy");
        assertThat(activityController.get().transcript).containsExactly("onCreate", "onPostCreate", "onStart", "onRestoreInstanceState", "onResume");
    }

    @Test
    public void startAndStopManagingCursorTracksCursors() throws Exception {
        ShadowActivityTest.TestActivity activity = new ShadowActivityTest.TestActivity();
        assertThat(Shadows.shadowOf(activity).getManagedCursors()).isNotNull();
        assertThat(Shadows.shadowOf(activity).getManagedCursors()).isEmpty();
        Cursor c = Shadow.newInstanceOf(SQLiteCursor.class);
        activity.startManagingCursor(c);
        assertThat(Shadows.shadowOf(activity).getManagedCursors()).isNotNull();
        assertThat(Shadows.shadowOf(activity).getManagedCursors()).hasSize(1);
        assertThat(Shadows.shadowOf(activity).getManagedCursors().get(0)).isSameAs(c);
        activity.stopManagingCursor(c);
        assertThat(Shadows.shadowOf(activity).getManagedCursors()).isNotNull();
        assertThat(Shadows.shadowOf(activity).getManagedCursors()).isEmpty();
    }

    @Test
    public void setVolumeControlStream_setsTheSpecifiedStreamType() {
        ShadowActivityTest.TestActivity activity = new ShadowActivityTest.TestActivity();
        activity.setVolumeControlStream(STREAM_ALARM);
        assertThat(getVolumeControlStream()).isEqualTo(STREAM_ALARM);
    }

    @Test
    public void decorViewSizeEqualToDisplaySize() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().visible().get();
        View decorView = activity.getWindow().getDecorView();
        assertThat(decorView).isNotEqualTo(null);
        ViewRootImpl root = decorView.getViewRootImpl();
        assertThat(root).isNotEqualTo(null);
        assertThat(decorView.getWidth()).isNotEqualTo(0);
        assertThat(decorView.getHeight()).isNotEqualTo(0);
        Display display = ShadowDisplay.getDefaultDisplay();
        assertThat(decorView.getWidth()).isEqualTo(display.getWidth());
        assertThat(decorView.getHeight()).isEqualTo(display.getHeight());
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void requestsPermissions() {
        ShadowActivityTest.TestActivity activity = new ShadowActivityTest.TestActivity();
        requestPermissions(new String[0], (-1));
    }

    private static class TestActivity extends Activity {
        List<String> transcript = new ArrayList<>();

        private boolean isRecreating = false;

        @Override
        public void onSaveInstanceState(Bundle outState) {
            isRecreating = true;
            transcript.add("onSaveInstanceState");
            outState.putString("TestActivityKey", "TestActivityValue");
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
            transcript.add("onRestoreInstanceState");
            Assert.assertTrue(savedInstanceState.containsKey("TestActivityKey"));
            Assert.assertEquals("TestActivityValue", savedInstanceState.getString("TestActivityKey"));
            super.onRestoreInstanceState(savedInstanceState);
        }

        @Override
        public Object onRetainNonConfigurationInstance() {
            transcript.add("onRetainNonConfigurationInstance");
            return 5;
        }

        @Override
        public void onPause() {
            transcript.add("onPause");
            super.onPause();
        }

        @Override
        public void onDestroy() {
            transcript.add("onDestroy");
            super.onDestroy();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            transcript.add("onCreate");
            if (isRecreating) {
                Assert.assertTrue(savedInstanceState.containsKey("TestActivityKey"));
                Assert.assertEquals("TestActivityValue", savedInstanceState.getString("TestActivityKey"));
            }
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onStart() {
            transcript.add("onStart");
            super.onStart();
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            transcript.add("onPostCreate");
            super.onPostCreate(savedInstanceState);
        }

        @Override
        public void onStop() {
            transcript.add("onStop");
            super.onStop();
        }

        @Override
        public void onRestart() {
            transcript.add("onRestart");
            super.onRestart();
        }

        @Override
        public void onResume() {
            transcript.add("onResume");
            super.onResume();
        }
    }

    @Test
    public void getAndSetParentActivity_shouldWorkForTestingPurposes() throws Exception {
        Activity parentActivity = new Activity();
        Activity activity = new Activity();
        Shadows.shadowOf(activity).setParent(parentActivity);
        Assert.assertSame(parentActivity, activity.getParent());
    }

    @Test
    public void getAndSetRequestedOrientation_shouldRemember() throws Exception {
        Activity activity = new Activity();
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        Assert.assertEquals(SCREEN_ORIENTATION_PORTRAIT, activity.getRequestedOrientation());
    }

    @Test
    public void getAndSetRequestedOrientation_shouldDelegateToParentIfPresent() throws Exception {
        Activity parentActivity = new Activity();
        Activity activity = new Activity();
        Shadows.shadowOf(activity).setParent(parentActivity);
        parentActivity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        Assert.assertEquals(SCREEN_ORIENTATION_PORTRAIT, activity.getRequestedOrientation());
        activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        Assert.assertEquals(SCREEN_ORIENTATION_REVERSE_LANDSCAPE, parentActivity.getRequestedOrientation());
    }

    @Test
    public void shouldSupportIsTaskRoot() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        Assert.assertTrue(activity.isTaskRoot());// as implemented, Activities are considered task roots by default

        Shadows.shadowOf(activity).setIsTaskRoot(false);
        Assert.assertFalse(activity.isTaskRoot());
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void shouldSupportIsInMultiWindowMode() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        assertThat(activity.isInMultiWindowMode()).isFalse();// Activity is not in multi window mode by default.

        Shadows.shadowOf(activity).setInMultiWindowMode(true);
        assertThat(activity.isInMultiWindowMode()).isTrue();
    }

    @Test
    public void getPendingTransitionEnterAnimationResourceId_should() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        activity.overridePendingTransition(15, 2);
        assertThat(Shadows.shadowOf(activity).getPendingTransitionEnterAnimationResourceId()).isEqualTo(15);
    }

    @Test
    public void getPendingTransitionExitAnimationResourceId_should() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);
        activity.overridePendingTransition(15, 2);
        assertThat(Shadows.shadowOf(activity).getPendingTransitionExitAnimationResourceId()).isEqualTo(2);
    }

    @Test
    public void getActionBar_shouldWorkIfActivityHasAnAppropriateTheme() throws Exception {
        ShadowActivityTest.ActionBarThemedActivity myActivity = Robolectric.buildActivity(ShadowActivityTest.ActionBarThemedActivity.class).create().get();
        ActionBar actionBar = getActionBar();
        assertThat(actionBar).isNotNull();
    }

    public static class ActionBarThemedActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTheme(android.R.style.Theme_Holo_Light);
            setContentView(new LinearLayout(this));
        }
    }

    @Test
    public void canGetOptionsMenu() throws Exception {
        Activity activity = Robolectric.buildActivity(ShadowActivityTest.OptionsMenuActivity.class).create().visible().get();
        Menu optionsMenu = Shadows.shadowOf(activity).getOptionsMenu();
        assertThat(optionsMenu).isNotNull();
        assertThat(optionsMenu.getItem(0).getTitle().toString()).isEqualTo("Algebraic!");
    }

    @Test
    public void canGetOptionsMenuWithActionMenu() throws Exception {
        ShadowActivityTest.ActionMenuActivity activity = Robolectric.buildActivity(ShadowActivityTest.ActionMenuActivity.class).create().visible().get();
        SearchView searchView = activity.mSearchView;
        // This blows up when ShadowPopupMenu existed.
        searchView.setIconifiedByDefault(false);
    }

    @Test
    public void canStartActivityFromFragment() {
        final Activity activity = Robolectric.setupActivity(Activity.class);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        activity.startActivityFromFragment(new Fragment(), intent, 4);
        ShadowActivity.IntentForResult intentForResult = Shadows.shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult.intent).isSameAs(intent);
        assertThat(intentForResult.requestCode).isEqualTo(4);
    }

    @Test
    public void canStartActivityFromFragment_withBundle() {
        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        Bundle options = new Bundle();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        activity.startActivityFromFragment(new Fragment(), intent, 5, options);
        ShadowActivity.IntentForResult intentForResult = Shadows.shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult.intent).isSameAs(intent);
        assertThat(intentForResult.options).isSameAs(options);
        assertThat(intentForResult.requestCode).isEqualTo(5);
    }

    @Test
    public void shouldUseAnimationOverride() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        Intent intent = new Intent(activity, ShadowActivityTest.OptionsMenuActivity.class);
        Bundle animationBundle = ActivityOptions.makeCustomAnimation(activity, test_anim_1, test_anim_1).toBundle();
        activity.startActivity(intent, animationBundle);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivityForResult().options).isSameAs(animationBundle);
    }

    @Test
    public void shouldCallActivityLifecycleCallbacks() {
        final List<String> transcript = new ArrayList<>();
        final ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        Application applicationContext = ApplicationProvider.getApplicationContext();
        applicationContext.registerActivityLifecycleCallbacks(new ShadowActivityTest.ActivityLifecycleCallbacks(transcript));
        controller.create();
        assertThat(transcript).containsExactly("onActivityCreated");
        transcript.clear();
        controller.start();
        assertThat(transcript).containsExactly("onActivityStarted");
        transcript.clear();
        controller.resume();
        assertThat(transcript).containsExactly("onActivityResumed");
        transcript.clear();
        controller.saveInstanceState(new Bundle());
        assertThat(transcript).containsExactly("onActivitySaveInstanceState");
        transcript.clear();
        controller.pause();
        assertThat(transcript).containsExactly("onActivityPaused");
        transcript.clear();
        controller.stop();
        assertThat(transcript).containsExactly("onActivityStopped");
        transcript.clear();
        controller.destroy();
        assertThat(transcript).containsExactly("onActivityDestroyed");
    }

    public static class ChildActivity extends Activity {}

    public static class ParentActivity extends Activity {}

    @Test
    public void getParentActivityIntent() {
        Activity activity = Robolectric.setupActivity(ShadowActivityTest.ChildActivity.class);
        assertThat(activity.getParentActivityIntent().getComponent().getClassName()).isEqualTo(ShadowActivityTest.ParentActivity.class.getName());
    }

    @Test
    public void getCallingActivity_defaultsToNull() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        Assert.assertNull(activity.getCallingActivity());
    }

    @Test
    public void getCallingActivity_returnsSetValue() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        ComponentName componentName = new ComponentName("com.example.package", "SomeActivity");
        Shadows.shadowOf(activity).setCallingActivity(componentName);
        Assert.assertEquals(componentName, activity.getCallingActivity());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void lockTask() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        assertThat(Shadows.shadowOf(activity).isLockTask()).isFalse();
        activity.startLockTask();
        assertThat(Shadows.shadowOf(activity).isLockTask()).isTrue();
        activity.stopLockTask();
        assertThat(Shadows.shadowOf(activity).isLockTask()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getPermission_shouldReturnRequestedPermissions() {
        // GIVEN
        String[] permission = new String[]{ permission.CAMERA };
        int requestCode = 1007;
        Activity activity = Robolectric.setupActivity(Activity.class);
        // WHEN
        activity.requestPermissions(permission, requestCode);
        // THEN
        ShadowActivity.PermissionsRequest request = Shadows.shadowOf(activity).getLastRequestedPermission();
        assertThat(request.requestCode).isEqualTo(requestCode);
        assertThat(request.requestedPermissions).isEqualTo(permission);
    }

    // ///////////////////////////
    private static class DialogCreatingActivity extends Activity {
        @Override
        protected Dialog onCreateDialog(int id) {
            return new Dialog(this);
        }
    }

    private static class OptionsMenuActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Requesting the action bar causes it to be properly initialized when the Activity becomes visible
            getWindow().requestFeature(FEATURE_ACTION_BAR);
            setContentView(new FrameLayout(this));
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            menu.add("Algebraic!");
            return true;
        }
    }

    private static class ActionMenuActivity extends Activity {
        SearchView mSearchView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(FEATURE_ACTION_BAR);
            setContentView(new FrameLayout(this));
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(action_menu, menu);
            MenuItem searchMenuItem = menu.findItem(action_search);
            mSearchView = ((SearchView) (searchMenuItem.getActionView()));
            return true;
        }
    }

    private static class DialogLifeCycleActivity extends Activity {
        public boolean createdDialog = false;

        public boolean preparedDialog = false;

        public boolean preparedDialogWithBundle = false;

        public Dialog dialog = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new FrameLayout(this));
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

        @Override
        protected Dialog onCreateDialog(int id) {
            createdDialog = true;
            return dialog;
        }

        @Override
        protected void onPrepareDialog(int id, Dialog dialog) {
            preparedDialog = true;
        }

        @Override
        protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
            preparedDialogWithBundle = true;
        }
    }

    private static class ActivityWithOnCreateDialog extends Activity {
        boolean onCreateDialogWasCalled = false;

        @Override
        protected Dialog onCreateDialog(int id) {
            onCreateDialogWasCalled = true;
            return new Dialog(this);
        }
    }

    private static class ActivityWithContentChangedTranscript extends Activity {
        private List<String> transcript;

        @Override
        public void onContentChanged() {
            transcript.add((("onContentChanged was called; title is \"" + (Shadows.shadowOf(((View) (findViewById(title)))).innerText())) + "\""));
        }

        private void setTranscript(List<String> transcript) {
            this.transcript = transcript;
        }
    }

    private static class OnBackPressedActivity extends Activity {
        public boolean onBackPressedCalled = false;

        @Override
        public void onBackPressed() {
            onBackPressedCalled = true;
            super.onBackPressed();
        }
    }

    private static class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private final List<String> transcript;

        public ActivityLifecycleCallbacks(List<String> transcript) {
            this.transcript = transcript;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            transcript.add("onActivityCreated");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            transcript.add("onActivityStarted");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            transcript.add("onActivityResumed");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            transcript.add("onActivityPaused");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            transcript.add("onActivityStopped");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            transcript.add("onActivitySaveInstanceState");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            transcript.add("onActivityDestroyed");
        }
    }

    public static class TestActivityWithAnotherTheme extends org.robolectric.shadows.testing.TestActivity {}
}

