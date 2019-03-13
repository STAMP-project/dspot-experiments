package com.github.ignition.location.tests;


import Context.MODE_PRIVATE;
import Criteria.ACCURACY_FINE;
import IgnitedLocationConstants.ACTIVE_LOCATION_UPDATE_ACTION;
import IgnitedLocationConstants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION;
import IgnitedLocationConstants.LOCATION_UPDATES_DISTANCE_DIFF_DEFAULT;
import IgnitedLocationConstants.LOCATION_UPDATES_INTERVAL_DEFAULT;
import IgnitedLocationConstants.MIN_BATTERY_LEVEL_FOR_GPS_DEFAULT;
import IgnitedLocationConstants.PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF_DEFAULT;
import IgnitedLocationConstants.PASSIVE_LOCATION_UPDATES_INTERVAL_DEFAULT;
import IgnitedLocationConstants.SHARED_PREFERENCE_FILE;
import IgnitedLocationConstants.SP_KEY_ENABLE_PASSIVE_LOCATION_UPDATES;
import IgnitedLocationConstants.SP_KEY_LOCATION_UPDATES_DISTANCE_DIFF;
import IgnitedLocationConstants.SP_KEY_LOCATION_UPDATES_INTERVAL;
import IgnitedLocationConstants.SP_KEY_LOCATION_UPDATES_USE_GPS;
import IgnitedLocationConstants.SP_KEY_MIN_BATTERY_LEVEL;
import IgnitedLocationConstants.SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF;
import IgnitedLocationConstants.SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL;
import IgnitedLocationConstants.SP_KEY_RUN_ONCE;
import IgnitedLocationConstants.SP_KEY_SHOW_WAIT_FOR_LOCATION_DIALOG;
import IgnitedLocationConstants.SP_KEY_WAIT_FOR_GPS_FIX_INTERVAL;
import IgnitedLocationConstants.WAIT_FOR_GPS_FIX_INTERVAL_DEFAULT;
import Intent.ACTION_BATTERY_LOW;
import LocationManager.GPS_PROVIDER;
import LocationManager.NETWORK_PROVIDER;
import R.id.ign_loc_dialog_no_providers_enabled;
import R.id.ign_loc_dialog_wait_for_fix;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.github.ignition.location.IgnitedLocationConstants;
import com.github.ignition.samples.location.ui.IgnitedLocationSampleActivity;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowApplication;
import com.xtremelabs.robolectric.shadows.ShadowApplication.Wrapper;
import com.xtremelabs.robolectric.shadows.ShadowLocationManager;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Test;


public abstract class AbstractIgnitedLocationManagerTest {
    private static final float DEFAULT_ACCURACY = 50.0F;

    protected ShadowApplication shadowApp;

    protected ShadowLocationManager shadowLocationManager;

    protected IgnitedLocationSampleActivity activity;

    private Location lastKnownLocation;

    @Test
    public void shouldShowWaitForFixDialogIfNoLocationAvailable() {
        shadowLocationManager.setLastKnownLocation(GPS_PROVIDER, null);
        resume();
        MatcherAssert.assertThat("Wait for fix dialog should be shown", Robolectric.shadowOf(activity).getLastShownDialogId(), IsEqual.equalTo(ign_loc_dialog_wait_for_fix));
    }

    @Test
    public void shouldShowNoEnabledProvidersDialogIfNoProviderAvailable() throws InterruptedException {
        shadowLocationManager.setProviderEnabled(GPS_PROVIDER, false);
        shadowLocationManager.setProviderEnabled(NETWORK_PROVIDER, false);
        resume();
        MatcherAssert.assertThat("No enabled providers dialog should be shown", Robolectric.shadowOf(activity).getLastShownDialogId(), IsEqual.equalTo(ign_loc_dialog_no_providers_enabled));
    }

    @Test
    public void ignitedLocationIsCurrentLocation() {
        resume();
        MatcherAssert.assertThat(lastKnownLocation, IsEqual.equalTo(activity.getCurrentLocation()));
        Location newLocation = sendMockLocationBroadcast(GPS_PROVIDER);
        MatcherAssert.assertThat(newLocation, IsEqual.equalTo(activity.getCurrentLocation()));
    }

    @Test
    public void shouldActivelyRequestLocationUpdatesOnResume() {
        resume();
        List<Wrapper> receivers = shadowApp.getRegisteredReceivers();
        MatcherAssert.assertThat(receivers, IsNull.notNullValue());
        boolean receiverRegistered = false;
        for (Wrapper receiver : receivers) {
            if (receiver.intentFilter.getAction(0).equals(ACTIVE_LOCATION_UPDATE_ACTION)) {
                receiverRegistered = true;
                break;
            }
        }
        MatcherAssert.assertThat(receiverRegistered, Is.is(true));
    }

    // TODO: find a better way to test this. Now the activity must be resumed twice or an Exception
    // will be thrown because one of the receivers is not registered.
    @Test
    public void shouldNotHaveRegisteredReceiverOnPause() throws Exception {
        resume();
        activity.onPause();
        ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
        shadowActivity.assertNoBroadcastListenersRegistered();
        resume();
    }

    // @Test
    // public void noTaskRunningOnFinish() {
    // // shadowApp.getBackgroundScheduler().pause();
    // ActivityManager activityManager = (ActivityManager) activity
    // .getSystemService(Context.ACTIVITY_SERVICE);
    // ShadowActivityManager shadowActivityManager = Robolectric.shadowOf(activityManager);
    // resume();
    // assertThat(shadowActivityManager.getRunningTasks(0).size(), equalTo(1));
    // }
    @Test
    public void shouldSaveSettingsToPreferences() {
        resume();
        SharedPreferences pref = activity.getSharedPreferences(SHARED_PREFERENCE_FILE, MODE_PRIVATE);
        boolean followLocationChanges = pref.getBoolean(SP_KEY_ENABLE_PASSIVE_LOCATION_UPDATES, false);
        boolean useGps = pref.getBoolean(SP_KEY_LOCATION_UPDATES_USE_GPS, (!(IgnitedLocationConstants.USE_GPS_DEFAULT)));
        boolean runOnce = pref.getBoolean(SP_KEY_RUN_ONCE, false);
        int locUpdatesDistDiff = pref.getInt(SP_KEY_LOCATION_UPDATES_DISTANCE_DIFF, ((IgnitedLocationConstants.LOCATION_UPDATES_DISTANCE_DIFF_DEFAULT) + 1));
        long locUpdatesInterval = pref.getLong(SP_KEY_LOCATION_UPDATES_INTERVAL, ((IgnitedLocationConstants.PASSIVE_LOCATION_UPDATES_INTERVAL_DEFAULT) + 1));
        int passiveLocUpdatesDistDiff = pref.getInt(SP_KEY_PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF, ((IgnitedLocationConstants.PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF_DEFAULT) + 1));
        long passiveLocUpdatesInterval = pref.getLong(SP_KEY_PASSIVE_LOCATION_UPDATES_INTERVAL, ((IgnitedLocationConstants.PASSIVE_LOCATION_UPDATES_INTERVAL_DEFAULT) + 1));
        long waitForGpsFixInterval = pref.getLong(SP_KEY_WAIT_FOR_GPS_FIX_INTERVAL, ((IgnitedLocationConstants.WAIT_FOR_GPS_FIX_INTERVAL_DEFAULT) + 1));
        int minBatteryLevelToUseGps = pref.getInt(SP_KEY_MIN_BATTERY_LEVEL, ((IgnitedLocationConstants.MIN_BATTERY_LEVEL_FOR_GPS_DEFAULT) + 1));
        boolean showWaitForLocationDialog = pref.getBoolean(SP_KEY_SHOW_WAIT_FOR_LOCATION_DIALOG, (!(IgnitedLocationConstants.SHOW_WAIT_FOR_LOCATION_DIALOG_DEFAULT)));
        MatcherAssert.assertThat(followLocationChanges, Is.is(true));
        MatcherAssert.assertThat(useGps, Is.is(true));
        MatcherAssert.assertThat(runOnce, Is.is(true));
        MatcherAssert.assertThat(showWaitForLocationDialog, Is.is(true));
        MatcherAssert.assertThat(minBatteryLevelToUseGps, IsEqual.equalTo(MIN_BATTERY_LEVEL_FOR_GPS_DEFAULT));
        MatcherAssert.assertThat(waitForGpsFixInterval, IsEqual.equalTo(WAIT_FOR_GPS_FIX_INTERVAL_DEFAULT));
        MatcherAssert.assertThat(LOCATION_UPDATES_DISTANCE_DIFF_DEFAULT, IsEqual.equalTo(locUpdatesDistDiff));
        MatcherAssert.assertThat(LOCATION_UPDATES_INTERVAL_DEFAULT, IsEqual.equalTo(locUpdatesInterval));
        MatcherAssert.assertThat(PASSIVE_LOCATION_UPDATES_DISTANCE_DIFF_DEFAULT, IsEqual.equalTo(passiveLocUpdatesDistDiff));
        MatcherAssert.assertThat(PASSIVE_LOCATION_UPDATES_INTERVAL_DEFAULT, IsEqual.equalTo(passiveLocUpdatesInterval));
    }

    @Test
    public void shouldRegisterListenerIfBestProviderDisabled() throws Exception {
        shadowLocationManager.setProviderEnabled(GPS_PROVIDER, false);
        shadowLocationManager.setBestProvider(GPS_PROVIDER, false);
        shadowLocationManager.setBestProvider(NETWORK_PROVIDER, true);
        resume();
        List<LocationListener> listeners = shadowLocationManager.getRequestLocationUpdateListeners();
        Assert.assertFalse(listeners.isEmpty());
    }

    @Test
    public void shouldNotRegisterListenerIfBestProviderEnabled() throws Exception {
        resume();
        List<LocationListener> listeners = shadowLocationManager.getRequestLocationUpdateListeners();
        MatcherAssert.assertThat("No listener must be registered, the best provider is enabled!", listeners.isEmpty());
    }

    @Test
    public void shouldRegisterLocationProviderDisabledReceiver() {
        resume();
        List<Wrapper> receivers = shadowApp.getRegisteredReceivers();
        MatcherAssert.assertThat(receivers, IsNull.notNullValue());
        boolean receiverRegistered = false;
        for (Wrapper receiver : receivers) {
            if (receiver.intentFilter.getAction(0).equals(ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED_ACTION)) {
                receiverRegistered = true;
                break;
            }
        }
        MatcherAssert.assertThat(receiverRegistered, Is.is(true));
    }

    @Test
    public void shouldNotRequestUpdatesFromGpsIfBatteryLow() {
        sendBatteryLevelChangedBroadcast(10);
        resume();
        Map<PendingIntent, Criteria> locationPendingIntents = shadowLocationManager.getRequestLocationUdpateCriteriaPendingIntents();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(ACCURACY_FINE);
        MatcherAssert.assertThat((("Updates from " + (LocationManager.GPS_PROVIDER)) + " provider shouldn't be requested when battery power is low!"), (!(locationPendingIntents.containsValue(criteria))));
    }

    @Test
    public void shouldSwitchToNetworkProviderIfBatteryLow() {
        resume();
        Map<PendingIntent, Criteria> locationPendingIntents = shadowLocationManager.getRequestLocationUdpateCriteriaPendingIntents();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(ACCURACY_FINE);
        sendBatteryLevelChangedBroadcast(10);
        Intent intent = new Intent();
        intent.setAction(ACTION_BATTERY_LOW);
        shadowApp.sendBroadcast(intent);
        sendBatteryLevelChangedBroadcast(100);
        MatcherAssert.assertThat((("Updates from " + (LocationManager.GPS_PROVIDER)) + " provider shouldn't be requested when battery power is low!"), (!(locationPendingIntents.containsValue(criteria))));
    }

    // @Test
    // public void shouldDisableLocationUpdatesIfOnIgnitedLocationChangedReturnsFalse() {
    // resume();
    // 
    // assertThat("Location updates shouldn't be disabled at this point", !IgnitedLocationManager
    // .aspectOf().isLocationUpdatesDisabled());
    // 
    // sendMockLocationBroadcast(LocationManager.GPS_PROVIDER, 10f);
    // 
    // assertThat("Location updates should be disabled at this point", IgnitedLocationManager
    // .aspectOf().isLocationUpdatesDisabled());
    // }
    // @Test
    // public void requestLocationUpdatesFromAnotherProviderIfCurrentOneIsDisabled() {
    // // TODO
    // }
    @Test
    public void shouldUpdateDataOnNewLocation() {
        resume();
        int countBefore = activity.getLocationCount();
        sendMockLocationBroadcast(GPS_PROVIDER);
        int countAfter = activity.getLocationCount();
        MatcherAssert.assertThat(countAfter, IsEqual.equalTo((++countBefore)));
    }
}

