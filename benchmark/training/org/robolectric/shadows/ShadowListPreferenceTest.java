package org.robolectric.shadows;


import android.preference.ListPreference;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;

import static org.robolectric.R.array.greetings;


@RunWith(AndroidJUnit4.class)
public class ShadowListPreferenceTest {
    private ListPreference listPreference;

    @Test
    public void shouldHaveEntries() {
        CharSequence[] entries = new CharSequence[]{ "this", "is", "only", "a", "test" };
        assertThat(listPreference.getEntries()).isNull();
        listPreference.setEntries(entries);
        assertThat(listPreference.getEntries()).isSameAs(entries);
    }

    @Test
    public void shouldSetEntriesByResourceId() {
        assertThat(listPreference.getEntries()).isNull();
        listPreference.setEntries(greetings);
        assertThat(listPreference.getEntries()).isNotNull();
    }

    @Test
    public void shouldHaveEntryValues() {
        CharSequence[] entryValues = new CharSequence[]{ "this", "is", "only", "a", "test" };
        assertThat(listPreference.getEntryValues()).isNull();
        listPreference.setEntryValues(entryValues);
        assertThat(listPreference.getEntryValues()).isSameAs(entryValues);
    }

    @Test
    public void shouldSetEntryValuesByResourceId() {
        assertThat(listPreference.getEntryValues()).isNull();
        listPreference.setEntryValues(greetings);
        assertThat(listPreference.getEntryValues()).isNotNull();
    }

    @Test
    public void shouldSetValue() {
        assertThat(listPreference.getValue()).isNull();
        listPreference.setValue("testing");
        assertThat(listPreference.getValue()).isEqualTo("testing");
    }
}

