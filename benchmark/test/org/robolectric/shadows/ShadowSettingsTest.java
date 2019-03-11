package org.robolectric.shadows;


import Secure.ADB_ENABLED;
import Secure.INSTALL_NON_MARKET_APPS;
import Settings.Global;
import Settings.Secure;
import Settings.SettingNotFoundException;
import Settings.System;
import android.app.Application;
import android.content.ContentResolver;
import android.os.Build.VERSION_CODES;
import android.text.format.DateFormat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowSettingsTest {
    private ContentResolver contentResolver;

    private Application context;

    @Test
    public void testSystemGetInt() throws Exception {
        assertThat(System.getInt(contentResolver, "property", 0)).isEqualTo(0);
        assertThat(System.getInt(contentResolver, "property", 2)).isEqualTo(2);
        System.putInt(contentResolver, "property", 1);
        assertThat(System.getInt(contentResolver, "property", 0)).isEqualTo(1);
    }

    @Test
    public void testSecureGetInt() throws Exception {
        assertThat(Secure.getInt(contentResolver, "property", 0)).isEqualTo(0);
        assertThat(Secure.getInt(contentResolver, "property", 2)).isEqualTo(2);
        Secure.putInt(contentResolver, "property", 1);
        assertThat(Secure.getInt(contentResolver, "property", 0)).isEqualTo(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testGlobalGetInt() throws Exception {
        assertThat(Global.getInt(contentResolver, "property", 0)).isEqualTo(0);
        assertThat(Global.getInt(contentResolver, "property", 2)).isEqualTo(2);
        Global.putInt(contentResolver, "property", 1);
        assertThat(Global.getInt(contentResolver, "property", 0)).isEqualTo(1);
    }

    @Test
    public void testSystemGetString() throws Exception {
        assertThat(System.getString(contentResolver, "property")).isNull();
        System.putString(contentResolver, "property", "value");
        assertThat(System.getString(contentResolver, "property")).isEqualTo("value");
    }

    @Test
    public void testSystemGetLong() throws Exception {
        assertThat(System.getLong(contentResolver, "property", 10L)).isEqualTo(10L);
        System.putLong(contentResolver, "property", 42L);
        assertThat(System.getLong(contentResolver, "property")).isEqualTo(42L);
        assertThat(System.getLong(contentResolver, "property", 10L)).isEqualTo(42L);
    }

    @Test
    public void testSystemGetFloat() throws Exception {
        assertThat(System.getFloat(contentResolver, "property", 23.23F)).isEqualTo(23.23F);
        System.putFloat(contentResolver, "property", 42.42F);
        assertThat(System.getFloat(contentResolver, "property", 10L)).isEqualTo(42.42F);
    }

    @Test(expected = SettingNotFoundException.class)
    public void testSystemGetLong_exception() throws Exception {
        System.getLong(contentResolver, "property");
    }

    @Test(expected = SettingNotFoundException.class)
    public void testSystemGetInt_exception() throws Exception {
        System.getInt(contentResolver, "property");
    }

    @Test(expected = SettingNotFoundException.class)
    public void testSystemGetFloat_exception() throws Exception {
        System.getFloat(contentResolver, "property");
    }

    @Test
    public void testSet24HourMode_24() {
        ShadowSettings.set24HourTimeFormat(true);
        assertThat(DateFormat.is24HourFormat(context.getBaseContext())).isTrue();
    }

    @Test
    public void testSet24HourMode_12() {
        ShadowSettings.set24HourTimeFormat(false);
        assertThat(DateFormat.is24HourFormat(context.getBaseContext())).isFalse();
    }

    @Test
    public void testSetAdbEnabled_settingsSecure_true() {
        ShadowSettings.setAdbEnabled(true);
        assertThat(android.provider.Settings.Secure.getInt(context.getContentResolver(), ADB_ENABLED, 0)).isEqualTo(1);
    }

    @Test
    public void testSetAdbEnabled_settingsSecure_false() {
        ShadowSettings.setAdbEnabled(false);
        assertThat(android.provider.Settings.Secure.getInt(context.getContentResolver(), ADB_ENABLED, 1)).isEqualTo(0);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testSetAdbEnabled_sinceJBMR1_settingsGlobal_true() {
        ShadowSettings.setAdbEnabled(true);
        assertThat(android.provider.Settings.Global.getInt(context.getContentResolver(), Global.ADB_ENABLED, 0)).isEqualTo(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testSetAdbEnabled_sinceJBMR1_settingsGlobal_false() {
        ShadowSettings.setAdbEnabled(false);
        assertThat(android.provider.Settings.Global.getInt(context.getContentResolver(), Global.ADB_ENABLED, 1)).isEqualTo(0);
    }

    @Test
    public void testSetInstallNonMarketApps_settingsSecure_true() {
        ShadowSettings.setInstallNonMarketApps(true);
        assertThat(android.provider.Settings.Secure.getInt(context.getContentResolver(), INSTALL_NON_MARKET_APPS, 0)).isEqualTo(1);
    }

    @Test
    public void testSetInstallNonMarketApps_settingsSecure_false() {
        ShadowSettings.setInstallNonMarketApps(false);
        assertThat(android.provider.Settings.Secure.getInt(context.getContentResolver(), INSTALL_NON_MARKET_APPS, 1)).isEqualTo(0);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testSetInstallNonMarketApps_sinceJBMR1_settingsGlobal_true() {
        ShadowSettings.setInstallNonMarketApps(true);
        assertThat(android.provider.Settings.Global.getInt(context.getContentResolver(), Global.INSTALL_NON_MARKET_APPS, 0)).isEqualTo(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testSetInstallNonMarketApps_sinceJBMR1_settingsGlobal_false() {
        ShadowSettings.setInstallNonMarketApps(false);
        assertThat(android.provider.Settings.Global.getInt(context.getContentResolver(), Global.INSTALL_NON_MARKET_APPS, 1)).isEqualTo(0);
    }
}

