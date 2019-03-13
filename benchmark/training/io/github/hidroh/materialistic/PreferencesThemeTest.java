package io.github.hidroh.materialistic;


import Preferences.Theme;
import R.string.pref_theme;
import R.style.AppTheme_Dark;
import R.style.AppTheme_DayNight;
import android.app.Activity;
import android.preference.PreferenceManager;
import io.github.hidroh.materialistic.test.TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(TestRunner.class)
public class PreferencesThemeTest {
    private Activity activity;

    @Test
    public void testDefaultTheme() {
        Theme.apply(activity, false, false);
        assertThat(Shadows.shadowOf(activity).callGetThemeResId()).isEqualTo(AppTheme_DayNight);
    }

    @Test
    public void testDarkTheme() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_theme), "dark").apply();
        Theme.apply(activity, false, false);
        assertThat(Shadows.shadowOf(activity).callGetThemeResId()).isEqualTo(AppTheme_Dark);
    }
}

