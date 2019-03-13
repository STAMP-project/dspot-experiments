package org.robolectric.shadows;


import Context.BLUETOOTH_SERVICE;
import Context.LAYOUT_INFLATER_SERVICE;
import Context.MODE_PRIVATE;
import Context.WALLPAPER_SERVICE;
import Intent.FLAG_ACTIVITY_NEW_TASK;
import IntentSender.SendIntentException;
import PendingIntent.FLAG_UPDATE_CURRENT;
import android.app.Application;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.widget.RemoteViews;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

import static org.robolectric.R.layout.remote_views;


@RunWith(AndroidJUnit4.class)
public class ShadowContextImplTest {
    private final Application context = ApplicationProvider.getApplicationContext();

    private final ShadowContextImpl shadowContext = Shadow.extract(context.getBaseContext());

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void deviceProtectedContext() {
        // Regular context should be credential protected, not device protected.
        assertThat(context.isDeviceProtectedStorage()).isFalse();
        assertThat(context.isCredentialProtectedStorage()).isFalse();
        // Device protected storage context should have device protected rather than credential
        // protected storage.
        Context deviceProtectedStorageContext = context.createDeviceProtectedStorageContext();
        assertThat(deviceProtectedStorageContext.isDeviceProtectedStorage()).isTrue();
        assertThat(deviceProtectedStorageContext.isCredentialProtectedStorage()).isFalse();
        // Data dirs of these two contexts must be different locations.
        assertThat(context.getDataDir()).isNotEqualTo(deviceProtectedStorageContext.getDataDir());
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void testMoveSharedPreferencesFrom() throws Exception {
        String PREFS = "PREFS";
        String PREF_NAME = "TOKEN_PREF";
        context.getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(PREF_NAME, "token").commit();
        Context dpContext = context.createDeviceProtectedStorageContext();
        assertThat(dpContext.getSharedPreferences(PREFS, MODE_PRIVATE).contains(PREF_NAME)).isFalse();
        assertThat(context.getSharedPreferences(PREFS, MODE_PRIVATE).contains(PREF_NAME)).isTrue();
        assertThat(dpContext.moveSharedPreferencesFrom(context, PREFS)).isTrue();
        assertThat(dpContext.getSharedPreferences(PREFS, MODE_PRIVATE).contains(PREF_NAME)).isTrue();
        assertThat(context.getSharedPreferences(PREFS, MODE_PRIVATE).contains(PREF_NAME)).isFalse();
    }

    @Config(minSdk = VERSION_CODES.KITKAT)
    @Test
    public void getExternalFilesDirs() {
        File[] dirs = context.getExternalFilesDirs("something");
        assertThat(dirs).asList().hasSize(1);
        assertThat(dirs[0].isDirectory()).isTrue();
        assertThat(dirs[0].canWrite()).isTrue();
        assertThat(dirs[0].getName()).isEqualTo("something");
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void getSystemService_shouldReturnBluetoothAdapter() {
        assertThat(context.getSystemService(BLUETOOTH_SERVICE)).isInstanceOf(BluetoothManager.class);
    }

    @Test
    public void getSystemService_shouldReturnWallpaperManager() {
        assertThat(context.getSystemService(WALLPAPER_SERVICE)).isInstanceOf(WallpaperManager.class);
    }

    @Test
    public void removeSystemService_getSystemServiceReturnsNull() {
        shadowContext.removeSystemService(WALLPAPER_SERVICE);
        assertThat(context.getSystemService(WALLPAPER_SERVICE)).isNull();
    }

    @Test
    public void startIntentSender_activityIntent() throws SendIntentException {
        PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent().setClassName(context, "ActivityIntent").addFlags(FLAG_ACTIVITY_NEW_TASK), FLAG_UPDATE_CURRENT);
        context.startIntentSender(intent.getIntentSender(), null, 0, 0, 0);
        assertThat(Shadows.shadowOf(context).getNextStartedActivity().getComponent().getClassName()).isEqualTo("ActivityIntent");
    }

    @Test
    public void startIntentSender_broadcastIntent() throws SendIntentException {
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent().setClassName(context, "BroadcastIntent"), FLAG_UPDATE_CURRENT);
        context.startIntentSender(intent.getIntentSender(), null, 0, 0, 0);
        assertThat(Shadows.shadowOf(context).getBroadcastIntents().get(0).getComponent().getClassName()).isEqualTo("BroadcastIntent");
    }

    @Test
    public void startIntentSender_serviceIntent() throws SendIntentException {
        PendingIntent intent = PendingIntent.getService(context, 0, new Intent().setClassName(context, "ServiceIntent"), FLAG_UPDATE_CURRENT);
        context.startIntentSender(intent.getIntentSender(), null, 0, 0, 0);
        assertThat(Shadows.shadowOf(context).getNextStartedService().getComponent().getClassName()).isEqualTo("ServiceIntent");
    }

    @Test
    public void createPackageContext() throws Exception {
        Context packageContext = context.createPackageContext(context.getPackageName(), 0);
        LayoutInflater inflater = ((LayoutInflater) (context.getSystemService(LAYOUT_INFLATER_SERVICE)));
        inflater.cloneInContext(packageContext);
        inflater.inflate(remote_views, new android.widget.FrameLayout(context), false);
    }

    @Test
    public void createPackageContextRemoteViews() throws Exception {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), remote_views);
        remoteViews.apply(context, new android.widget.FrameLayout(context));
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void bindServiceAsUser() throws Exception {
        Intent serviceIntent = new Intent();
        ServiceConnection serviceConnection = buildServiceConnection();
        int flags = 0;
        assertThat(context.bindServiceAsUser(serviceIntent, serviceConnection, flags, myUserHandle())).isTrue();
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(1);
    }

    @Test
    public void bindService() throws Exception {
        Intent serviceIntent = new Intent();
        ServiceConnection serviceConnection = buildServiceConnection();
        int flags = 0;
        assertThat(context.bindService(serviceIntent, serviceConnection, flags)).isTrue();
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(1);
    }

    @Test
    public void bindService_unbindable() throws Exception {
        String action = "foo-action";
        Intent serviceIntent = new Intent(action);
        ServiceConnection serviceConnection = buildServiceConnection();
        int flags = 0;
        Shadows.shadowOf(context).declareActionUnbindable(action);
        assertThat(context.bindService(serviceIntent, serviceConnection, flags)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void sendBroadcastAsUser_sendBroadcast() throws SendIntentException {
        String action = "foo-action";
        Intent intent = new Intent(action);
        context.sendBroadcastAsUser(intent, myUserHandle());
        assertThat(Shadows.shadowOf(context).getBroadcastIntents().get(0).getAction()).isEqualTo(action);
    }

    @Test
    public void createPackageContext_absent() {
        try {
            context.createPackageContext("doesnt.exist", 0);
            Assert.fail("Should throw NameNotFoundException");
        } catch (NameNotFoundException e) {
            // expected
        }
    }
}

