package io.github.hidroh.materialistic;


import DialogInterface.BUTTON_POSITIVE;
import R.id.drawer_about;
import R.id.drawer_display;
import R.id.drawer_offline;
import R.id.drawer_release;
import R.id.menu_clear_drafts;
import R.id.menu_clear_recent;
import R.id.menu_comments;
import R.id.menu_list;
import R.id.menu_readability;
import R.id.menu_reset;
import R.string.pref_color_code;
import android.app.AlertDialog;
import android.preference.PreferenceManager;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowSearchRecentSuggestions;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;


@Config(shadows = { ShadowSearchRecentSuggestions.class })
@RunWith(TestRunner.class)
public class SettingsActivityTest {
    private SettingsActivity activity;

    private ActivityController<SettingsActivity> controller;

    @Test
    public void testClearRecentSearches() {
        ShadowSearchRecentSuggestions.historyClearCount = 0;
        Assert.assertNotNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_clear_recent));
        Shadows.shadowOf(activity).clickMenuItem(menu_clear_recent);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        Assert.assertEquals(1, ShadowSearchRecentSuggestions.historyClearCount);
    }

    @Test
    public void testReset() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_color_code), false).apply();
        Assert.assertFalse(Preferences.colorCodeEnabled(activity));
        Assert.assertNotNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_reset));
        Shadows.shadowOf(activity).clickMenuItem(menu_reset);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        Assert.assertTrue(Preferences.colorCodeEnabled(activity));
    }

    @Test
    public void testClearDrafts() {
        Preferences.saveDraft(activity, "1", "draft");
        Shadows.shadowOf(activity).clickMenuItem(menu_clear_drafts);
        assertThat(Preferences.getDraft(activity, "1")).isNullOrEmpty();
    }

    @Test
    public void testAbout() {
        activity.findViewById(drawer_about).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, AboutActivity.class);
    }

    @Test
    public void testReleaseNotes() {
        activity.findViewById(drawer_release).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ReleaseNotesActivity.class);
    }

    @Test
    public void testDisplay() {
        activity.findViewById(drawer_display).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, PreferencesActivity.class);
    }

    @Test
    public void testOffline() {
        activity.findViewById(drawer_offline).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, PreferencesActivity.class);
    }

    @Test
    public void testList() {
        activity.findViewById(menu_list).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, PreferencesActivity.class);
    }

    @Test
    public void testComments() {
        activity.findViewById(menu_comments).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, PreferencesActivity.class);
    }

    @Test
    public void testReadability() {
        activity.findViewById(menu_readability).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, PreferencesActivity.class);
    }
}

