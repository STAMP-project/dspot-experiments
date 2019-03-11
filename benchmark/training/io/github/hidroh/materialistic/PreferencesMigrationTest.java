package io.github.hidroh.materialistic;


import RuntimeEnvironment.application;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;


@RunWith(ParameterizedRobolectricTestRunner.class)
public class PreferencesMigrationTest {
    private final int oldKey;

    private final boolean oldValue;

    private final int newKey;

    private final int newValue;

    public PreferencesMigrationTest(@StringRes
    int oldKey, boolean oldValue, @StringRes
    int newKey, @StringRes
    int newValue) {
        this.oldKey = oldKey;
        this.oldValue = oldValue;
        this.newKey = newKey;
        this.newValue = newValue;
    }

    @Test
    public void testMigrate() {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean(application.getString(oldKey), oldValue).apply();
        Assert.assertTrue(PreferenceManager.getDefaultSharedPreferences(application).contains(application.getString(oldKey)));
        Preferences.migrate(application);
        Assert.assertFalse(PreferenceManager.getDefaultSharedPreferences(application).contains(application.getString(oldKey)));
        Assert.assertEquals(application.getString(newValue), PreferenceManager.getDefaultSharedPreferences(application).getString(application.getString(newKey), null));
    }

    @Test
    public void testNoMigrate() {
        Assert.assertFalse(PreferenceManager.getDefaultSharedPreferences(application).contains(application.getString(oldKey)));
        Preferences.migrate(application);
        Assert.assertNull(application.getString(newValue), PreferenceManager.getDefaultSharedPreferences(application).getString(application.getString(newKey), null));
    }

    @Test
    public void testNoMigrateDefault() {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean(application.getString(oldKey), (!(oldValue))).apply();
        Assert.assertTrue(PreferenceManager.getDefaultSharedPreferences(application).contains(application.getString(oldKey)));
        Preferences.migrate(application);
        Assert.assertFalse(PreferenceManager.getDefaultSharedPreferences(application).contains(application.getString(oldKey)));
        Assert.assertNull(application.getString(newValue), PreferenceManager.getDefaultSharedPreferences(application).getString(application.getString(newKey), null));
    }
}

