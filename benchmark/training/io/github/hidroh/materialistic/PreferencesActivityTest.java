/**
 * Copyright (c) 2016 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.hidroh.materialistic;


import R.id.alertTitle;
import R.string.pref_lazy_load_help;
import R.string.pref_lazy_load_title;
import R.string.pref_theme;
import R.string.pref_volume_help;
import R.string.pref_volume_title;
import android.app.Dialog;
import android.preference.PreferenceManager;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowPreferenceFragmentCompat;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;


@Config(shadows = { ShadowPreferenceFragmentCompat.class })
@RunWith(TestRunner.class)
public class PreferencesActivityTest {
    private PreferencesActivityTest.TestPreferencesActivity activity;

    private ActivityController<PreferencesActivityTest.TestPreferencesActivity> controller;

    @Test
    public void testPrefTheme() {
        String key = activity.getString(pref_theme);
        // trigger listener
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(key, "dark").apply();
        Assert.assertTrue(activity.recreated);
    }

    @Test
    public void testHelp() {
        getPreferenceScreen().findPreference(activity.getString(pref_volume_help)).performClick();
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(dialog);
        assertThat(((android.widget.TextView) (dialog.findViewById(alertTitle)))).hasText(pref_volume_title);
    }

    @Test
    public void testLazyLoadHelp() {
        getPreferenceScreen().findPreference(activity.getString(pref_lazy_load_help)).performClick();
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(dialog);
        assertThat(((android.widget.TextView) (dialog.findViewById(alertTitle)))).hasText(pref_lazy_load_title);
    }

    static class TestPreferencesActivity extends PreferencesActivity {
        boolean recreated;

        @Override
        public void recreate() {
            recreated = true;
        }
    }
}

