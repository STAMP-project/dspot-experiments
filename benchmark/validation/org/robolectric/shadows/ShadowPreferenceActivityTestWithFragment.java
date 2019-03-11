package org.robolectric.shadows;


import android.R.id.content;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;

import static org.robolectric.R.xml.preferences;


/**
 * Current Android examples show adding a PreferenceFragment as part of the hosting Activity
 * lifecycle. This resulted in a null pointer exception when trying to access a Context while
 * inflating the Preference objects defined in xml. This class tests that path.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowPreferenceActivityTestWithFragment {
    private ShadowPreferenceActivityTestWithFragment.TestPreferenceActivity activity = Robolectric.setupActivity(ShadowPreferenceActivityTestWithFragment.TestPreferenceActivity.class);

    private ShadowPreferenceActivityTestWithFragment.TestPreferenceFragment fragment;

    private static final String FRAGMENT_TAG = "fragmentPreferenceTag";

    @Test
    public void fragmentIsNotNull() {
        assertThat(this.fragment).isNotNull();
    }

    @Test
    public void preferenceAddedWithCorrectDetails() {
        Preference preference = findPreference("edit_text");
        assertThat(preference).isNotNull();
        assertThat(preference.getTitle()).isEqualTo("EditText Test");
        assertThat(preference.getSummary()).isEqualTo("");
    }

    private static class TestPreferenceActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FragmentManager fragmentManager = getFragmentManager();
            ShadowPreferenceActivityTestWithFragment.TestPreferenceFragment fragment = new ShadowPreferenceActivityTestWithFragment.TestPreferenceFragment();
            fragmentManager.beginTransaction().replace(content, fragment, ShadowPreferenceActivityTestWithFragment.FRAGMENT_TAG).commit();
        }
    }

    public static class TestPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(preferences);
        }
    }
}

