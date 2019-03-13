/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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


import R.string.listPreferenceKey;
import android.preference.Preference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PreferenceAnnotationsFragmentTest {
    private PreferenceAnnotationsFragment_ fragment;

    @Test
    public void testPreferenceChangeHandled() {
        assertThat(fragment.preferenceWithKeyChanged).isFalse();
        Preference preference = fragment.findPreference(fragment.getString(listPreferenceKey));
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, new Object());
        assertThat(fragment.preferenceWithKeyChanged).isTrue();
    }

    @Test
    public void testAfterPreferencesCalled() {
        assertThat(fragment.afterPreferencesCalled).isTrue();
    }
}

