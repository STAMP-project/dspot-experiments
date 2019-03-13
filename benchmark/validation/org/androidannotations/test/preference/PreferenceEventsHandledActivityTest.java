/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.preference;


import R.string.checkBoxPrefKey;
import R.string.checkBoxWithCastPrefKey;
import R.string.conventionKey;
import R.string.editTextPrefKey;
import R.string.listPreferenceKey;
import R.string.switchPrefKey;
import android.annotation.SuppressLint;
import android.preference.Preference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@SuppressWarnings("deprecation")
@RunWith(RobolectricTestRunner.class)
public class PreferenceEventsHandledActivityTest {
    private PreferenceEventsHandledActivity_ activity;

    @Test
    public void testConventionPreferenceChangeHandled() {
        assertThat(activity.conventionPrefChanged).isFalse();
        Preference preference = activity.findPreference(activity.getString(conventionKey));
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, new Object());
        assertThat(activity.conventionPrefChanged).isTrue();
    }

    @Test
    public void testPreferenceChangeHandled() {
        assertThat(activity.preferenceWithKeyChanged).isFalse();
        Preference preference = activity.findPreference(activity.getString(listPreferenceKey));
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, new Object());
        assertThat(activity.preferenceWithKeyChanged).isTrue();
    }

    @SuppressLint("UseValueOf")
    @Test
    public void testPreferenceChangeParameterPassed() {
        Preference preference = activity.findPreference(activity.getString(checkBoxPrefKey));
        Boolean newValue = new Boolean(true);
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, newValue);
        assertThat(activity.preference).isSameAs(preference);
        assertThat(activity.newValue).isSameAs(newValue);
    }

    @Test
    public void testPreferenceChangeCastedParameterPassed() {
        Preference preference = activity.findPreference(activity.getString(checkBoxWithCastPrefKey));
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, true);
        assertThat(activity.preference).isSameAs(preference);
    }

    @Test
    public void testPreferenceChangeParsedParameterPassed() {
        Preference preference = activity.findPreference(activity.getString(editTextPrefKey));
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, "2");
        assertThat(activity.editTextPreference).isSameAs(preference);
        assertThat(activity.preferenceChangedParsedValue).isEqualTo(2);
    }

    @Test
    public void testPreferenceChangeDefaultReturnValue() {
        Preference preference = activity.findPreference(activity.getString(listPreferenceKey));
        boolean result = preference.getOnPreferenceChangeListener().onPreferenceChange(preference, new Object());
        assertThat(result).isTrue();
    }

    @Test
    public void testPreferenceChangeCustomReturnValue() {
        Preference preference = activity.findPreference(activity.getString(switchPrefKey));
        boolean result = preference.getOnPreferenceChangeListener().onPreferenceChange(preference, new Object());
        assertThat(result).isFalse();
    }

    @Test
    public void testConventionPreferenceClickHandled() {
        assertThat(activity.conventionPrefClicked).isFalse();
        Preference preference = activity.findPreference(activity.getString(conventionKey));
        preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        assertThat(activity.conventionPrefClicked).isTrue();
    }

    @Test
    public void testPreferenceClickHandled() {
        assertThat(activity.preferenceWithKeyClicked).isFalse();
        Preference preference = activity.findPreference(activity.getString(listPreferenceKey));
        preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        assertThat(activity.preferenceWithKeyClicked).isTrue();
    }

    @Test
    public void testPreferenceClickParameterPassed() {
        Preference preference = activity.findPreference(activity.getString(checkBoxPrefKey));
        preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        assertThat(activity.preference).isSameAs(preference);
    }

    @Test
    public void testPreferenceClickCastedParameterPassed() {
        Preference preference = activity.findPreference(activity.getString(checkBoxWithCastPrefKey));
        preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        assertThat(activity.preference).isSameAs(preference);
    }

    @Test
    public void testPreferenceClickDefaultReturnValue() {
        Preference preference = activity.findPreference(activity.getString(listPreferenceKey));
        boolean result = preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        assertThat(result).isTrue();
    }

    @Test
    public void testPreferenceClickCustomReturnValue() {
        Preference preference = activity.findPreference(activity.getString(switchPrefKey));
        boolean result = preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        assertThat(result).isFalse();
    }
}

