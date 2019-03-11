package io.github.hidroh.materialistic;


import DateUtils.FORMAT_ABBREV_ALL;
import DateUtils.MINUTE_IN_MILLIS;
import Intent.ACTION_CHOOSER;
import Intent.ACTION_VIEW;
import R.id.menu_share;
import R.id.recycler_view;
import R.id.snackbar_action;
import R.id.toolbar;
import R.string.last_updated;
import R.string.pref_external;
import R.string.pref_launch_screen;
import R.string.pref_launch_screen_value_last;
import R.string.pref_story_display;
import R.string.pref_story_display_value_comments;
import RuntimeEnvironment.application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import io.github.hidroh.materialistic.test.TestListActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.TestWebItem;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerView;
import io.github.hidroh.materialistic.test.shadow.ShadowSnackbar;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowPackageManager;
import org.robolectric.shadows.ShadowResolveInfo;

import static Preferences.sReleaseNotesSeen;


@RunWith(TestRunner.class)
public class BaseListActivityTest {
    private ActivityController<TestListActivity> controller;

    private TestListActivity activity;

    @Test
    public void testCreate() {
        Assert.assertNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_share));
    }

    @Config(shadows = ShadowSnackbar.class)
    @Test
    public void testShowReleaseNotes() {
        controller.pause().stop().destroy();
        sReleaseNotesSeen = false;
        controller = Robolectric.buildActivity(TestListActivity.class);
        activity = controller.create().postCreate(null).start().resume().visible().get();
        View snackbarView = ShadowSnackbar.getLatestView();
        Assert.assertNotNull(snackbarView);
        snackbarView.findViewById(snackbar_action).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ReleaseNotesActivity.class);
        sReleaseNotesSeen = null;
    }

    @Test
    public void testRotate() {
        Bundle savedState = new Bundle();
        activity.onSaveInstanceState(savedState);
        RuntimeEnvironment.setQualifiers("w820dp-land");
        controller = Robolectric.buildActivity(TestListActivity.class);
        activity = controller.create(savedState).postCreate(null).start().resume().visible().get();
        Assert.assertNotNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_share));
    }

    @Test
    public void testSelectItemOpenWeb() {
        activity.onItemSelected(new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        Intent actual = Shadows.shadowOf(activity).getNextStartedActivity();
        Assert.assertEquals(ItemActivity.class.getName(), actual.getComponent().getClassName());
    }

    @Test
    public void testSelectItemOpenExternal() {
        ShadowPackageManager packageManager = Shadows.shadowOf(application.getPackageManager());
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_external), true).apply();
        controller.pause().resume();
        activity.onItemSelected(new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_VIEW);
    }

    @Test
    public void testSelectItemStartActionView() {
        ShadowPackageManager packageManager = Shadows.shadowOf(application.getPackageManager());
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")), ShadowResolveInfo.newResolveInfo("label", "com.android.browser", "DefaultActivity"));
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_external), true).apply();
        controller.pause().resume();
        activity.onItemSelected(new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_VIEW);
    }

    @Test
    public void testSelectItemOpenChooser() {
        ShadowPackageManager packageManager = Shadows.shadowOf(application.getPackageManager());
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://news.ycombinator.com/item?id=1")), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://news.ycombinator.com/item?id=1")), ShadowResolveInfo.newResolveInfo("label", "com.android.browser", "DefaultActivity"));
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_external), true).apply();
        controller.pause().resume();
        activity.onItemSelected(new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://news.ycombinator.com/item?id=1";
            }
        });
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_CHOOSER);
    }

    @Test
    public void testSelectItemOpenItem() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_comments)).apply();
        controller.pause().resume();
        activity.onItemSelected(new TestWebItem() {
            @Override
            public String getId() {
                return "1";
            }
        });
        Assert.assertEquals(ItemActivity.class.getName(), Shadows.shadowOf(activity).getNextStartedActivity().getComponent().getClassName());
    }

    @Test
    public void testGetSelectedItem() {
        onItemSelected(new TestWebItem() {
            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getId() {
                return "1";
            }
        });
        Assert.assertNotNull(getSelectedItem());
    }

    @Test
    public void testLastUpdated() {
        String expected = activity.getString(last_updated, DateUtils.getRelativeTimeSpanString(System.currentTimeMillis(), System.currentTimeMillis(), MINUTE_IN_MILLIS, FORMAT_ABBREV_ALL));
        onRefreshed();
        assertThat(getSupportActionBar()).hasSubtitle(expected);
        getSupportActionBar().setSubtitle(null);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertThat(getSupportActionBar()).hasSubtitle(expected);
        getSupportActionBar().setSubtitle(null);
        controller.pause().resume();
        assertThat(getSupportActionBar()).hasSubtitle(expected);
        Bundle savedState = new Bundle();
        activity.onSaveInstanceState(savedState);
        controller = Robolectric.buildActivity(TestListActivity.class);
        activity = controller.create(savedState).postCreate(null).start().resume().visible().get();
        assertThat(getSupportActionBar()).hasSubtitle(expected);
    }

    @Config(shadows = ShadowRecyclerView.class)
    @Test
    public void testScrollToTop() {
        RecyclerView recyclerView = activity.findViewById(recycler_view);
        recyclerView.smoothScrollToPosition(1);
        assertThat(CustomShadows.customShadowOf(recyclerView).getScrollPosition()).isEqualTo(1);
        activity.findViewById(toolbar).performClick();
        assertThat(CustomShadows.customShadowOf(recyclerView).getScrollPosition()).isEqualTo(0);
    }

    @Test
    public void testBackPressed() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_launch_screen), activity.getString(pref_launch_screen_value_last)).apply();
        onBackPressed();
        assertThat(activity).isNotFinishing();
    }

    @Test
    public void testBackPressedFinish() {
        onBackPressed();
        assertThat(activity).isFinishing();
    }
}

