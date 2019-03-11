package org.robolectric.shadows;


import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowPreferenceGroupTest {
    private ShadowPreferenceGroupTest.TestPreferenceGroup group;

    private ShadowPreference shadow;

    private Activity activity;

    private AttributeSet attrs;

    private Preference pref1;

    private Preference pref2;

    @Test
    public void shouldInheritFromPreference() {
        assertThat(shadow).isInstanceOf(ShadowPreference.class);
    }

    @Test
    public void shouldAddPreferences() {
        assertThat(getPreferenceCount()).isEqualTo(0);
        // First add succeeds
        assertThat(group.addPreference(pref1)).isTrue();
        assertThat(getPreferenceCount()).isEqualTo(1);
        // Dupe add fails silently
        assertThat(group.addPreference(pref1)).isTrue();
        assertThat(getPreferenceCount()).isEqualTo(1);
        // Second add succeeds
        assertThat(group.addPreference(pref2)).isTrue();
        assertThat(getPreferenceCount()).isEqualTo(2);
    }

    @Test
    public void shouldAddItemFromInflater() {
        assertThat(getPreferenceCount()).isEqualTo(0);
        // First add succeeds
        group.addItemFromInflater(pref1);
        assertThat(getPreferenceCount()).isEqualTo(1);
        // Dupe add fails silently
        group.addItemFromInflater(pref1);
        assertThat(getPreferenceCount()).isEqualTo(1);
        // Second add succeeds
        group.addItemFromInflater(pref2);
        assertThat(getPreferenceCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetPreference() {
        group.addPreference(pref1);
        group.addPreference(pref2);
        assertThat(getPreference(0)).isSameAs(pref1);
        assertThat(getPreference(1)).isSameAs(pref2);
    }

    @Test
    public void shouldGetPreferenceCount() {
        assertThat(getPreferenceCount()).isEqualTo(0);
        group.addPreference(pref1);
        assertThat(getPreferenceCount()).isEqualTo(1);
        group.addPreference(pref2);
        assertThat(getPreferenceCount()).isEqualTo(2);
    }

    @Test
    public void shouldRemovePreference() {
        group.addPreference(pref1);
        group.addPreference(pref2);
        assertThat(getPreferenceCount()).isEqualTo(2);
        // First remove succeeds
        assertThat(group.removePreference(pref1)).isTrue();
        assertThat(getPreferenceCount()).isEqualTo(1);
        // Dupe remove fails
        assertThat(group.removePreference(pref1)).isFalse();
        assertThat(getPreferenceCount()).isEqualTo(1);
        // Second remove succeeds
        assertThat(group.removePreference(pref2)).isTrue();
        assertThat(getPreferenceCount()).isEqualTo(0);
    }

    @Test
    public void shouldRemoveAll() {
        group.addPreference(pref1);
        group.addPreference(pref2);
        assertThat(getPreferenceCount()).isEqualTo(2);
        removeAll();
        assertThat(getPreferenceCount()).isEqualTo(0);
    }

    @Test
    public void shouldFindPreference() {
        group.addPreference(pref1);
        group.addPreference(pref2);
        assertThat(group.findPreference(pref1.getKey())).isSameAs(pref1);
        assertThat(group.findPreference(pref2.getKey())).isSameAs(pref2);
    }

    @Test
    public void shouldFindPreferenceRecursively() {
        ShadowPreferenceGroupTest.TestPreferenceGroup group2 = new ShadowPreferenceGroupTest.TestPreferenceGroup(activity, attrs);
        Shadows.shadowOf(group2).callOnAttachedToHierarchy(new android.preference.PreferenceManager(activity, 0));
        group2.addPreference(pref2);
        group.addPreference(pref1);
        addPreference(group2);
        assertThat(group.findPreference(pref2.getKey())).isSameAs(pref2);
    }

    @Test
    public void shouldSetEnabledRecursively() {
        boolean[] values = new boolean[]{ false, true };
        ShadowPreferenceGroupTest.TestPreferenceGroup group2 = new ShadowPreferenceGroupTest.TestPreferenceGroup(activity, attrs);
        Shadows.shadowOf(group2).callOnAttachedToHierarchy(new android.preference.PreferenceManager(activity, 0));
        group2.addPreference(pref2);
        group.addPreference(pref1);
        addPreference(group2);
        for (boolean enabled : values) {
            setEnabled(enabled);
            assertThat(isEnabled()).isEqualTo(enabled);
            assertThat(isEnabled()).isEqualTo(enabled);
            assertThat(pref1.isEnabled()).isEqualTo(enabled);
            assertThat(pref2.isEnabled()).isEqualTo(enabled);
        }
    }

    private static class TestPreferenceGroup extends PreferenceGroup {
        public TestPreferenceGroup(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }
}

