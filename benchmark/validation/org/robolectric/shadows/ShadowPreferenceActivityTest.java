package org.robolectric.shadows;


import android.preference.PreferenceActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;

import static org.robolectric.R.xml.preferences;


@RunWith(AndroidJUnit4.class)
public class ShadowPreferenceActivityTest {
    private ShadowPreferenceActivityTest.TestPreferenceActivity activity;

    @Test
    public void shouldInitializeListViewInOnCreate() {
        assertThat(getListView()).isNotNull();
    }

    @Test
    public void shouldNotInitializePreferenceScreen() {
        ShadowPreferenceActivityTest.TestPreferenceActivity activity = Robolectric.buildActivity(ShadowPreferenceActivityTest.TestPreferenceActivity.class).get();
        assertThat(getPreferenceScreen()).isNull();
    }

    @Test
    public void shouldFindPreferences() {
        addPreferencesFromResource(preferences);
        Assert.assertNotNull(findPreference("category"));
        Assert.assertNotNull(findPreference("inside_category"));
        Assert.assertNotNull(findPreference("screen"));
        Assert.assertNotNull(findPreference("inside_screen"));
        Assert.assertNotNull(findPreference("checkbox"));
        Assert.assertNotNull(findPreference("edit_text"));
        Assert.assertNotNull(findPreference("list"));
        Assert.assertNotNull(findPreference("preference"));
        Assert.assertNotNull(findPreference("ringtone"));
    }

    @Test
    public void shouldFindPreferencesWithStringResourceKeyValue() {
        addPreferencesFromResource(preferences);
        Assert.assertNotNull(findPreference("preference_resource_key_value"));
    }

    @SuppressWarnings("FragmentInjection")
    private static class TestPreferenceActivity extends PreferenceActivity {}
}

