package org.robolectric.shadows;


import AccessibilityManager.TouchExplorationStateChangeListener;
import Context.ACCESSIBILITY_SERVICE;
import Context.ACTIVITY_SERVICE;
import Context.ALARM_SERVICE;
import Context.AUDIO_SERVICE;
import Context.BATTERY_SERVICE;
import Context.BIND_AUTO_CREATE;
import Context.CAPTIONING_SERVICE;
import Context.CONNECTIVITY_SERVICE;
import Context.DEVICE_POLICY_SERVICE;
import Context.DISPLAY_SERVICE;
import Context.DOWNLOAD_SERVICE;
import Context.DROPBOX_SERVICE;
import Context.FINGERPRINT_SERVICE;
import Context.INPUT_METHOD_SERVICE;
import Context.KEYGUARD_SERVICE;
import Context.LAYOUT_INFLATER_SERVICE;
import Context.LOCATION_SERVICE;
import Context.MEDIA_ROUTER_SERVICE;
import Context.MEDIA_SESSION_SERVICE;
import Context.NOTIFICATION_SERVICE;
import Context.NSD_SERVICE;
import Context.POWER_SERVICE;
import Context.PRINT_SERVICE;
import Context.RESTRICTIONS_SERVICE;
import Context.SEARCH_SERVICE;
import Context.SENSOR_SERVICE;
import Context.STORAGE_SERVICE;
import Context.TELEPHONY_SERVICE;
import Context.TELEPHONY_SUBSCRIPTION_SERVICE;
import Context.TEXT_CLASSIFICATION_SERVICE;
import Context.UI_MODE_SERVICE;
import Context.USER_SERVICE;
import Context.VIBRATOR_SERVICE;
import Context.WIFI_SERVICE;
import Gravity.CENTER;
import Intent.FLAG_ACTIVITY_NEW_TASK;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.content.ServiceConnection;
import android.hardware.SystemSensorManager;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.FingerprintManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.media.session.MediaSessionManager;
import android.net.ConnectivityManager;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.DropBoxManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.print.PrintManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassificationManager;
import android.widget.PopupWindow;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.testing.TestActivity;
import org.robolectric.util.Scheduler;


@RunWith(AndroidJUnit4.class)
public class ShadowApplicationTest {
    private Application context;

    @Test
    public void shouldBeAContext() throws Exception {
        assertThat(Robolectric.setupActivity(Activity.class).getApplication()).isSameAs(ApplicationProvider.getApplicationContext());
        assertThat(Robolectric.setupActivity(Activity.class).getApplication().getApplicationContext()).isSameAs(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void shouldProvideServices() throws Exception {
        assertThat(context.getSystemService(ACTIVITY_SERVICE)).isInstanceOf(ActivityManager.class);
        assertThat(context.getSystemService(POWER_SERVICE)).isInstanceOf(PowerManager.class);
        assertThat(context.getSystemService(ALARM_SERVICE)).isInstanceOf(AlarmManager.class);
        assertThat(context.getSystemService(NOTIFICATION_SERVICE)).isInstanceOf(NotificationManager.class);
        assertThat(context.getSystemService(KEYGUARD_SERVICE)).isInstanceOf(KeyguardManager.class);
        assertThat(context.getSystemService(LOCATION_SERVICE)).isInstanceOf(LocationManager.class);
        assertThat(context.getSystemService(SEARCH_SERVICE)).isInstanceOf(SearchManager.class);
        assertThat(context.getSystemService(SENSOR_SERVICE)).isInstanceOf(SystemSensorManager.class);
        assertThat(context.getSystemService(STORAGE_SERVICE)).isInstanceOf(StorageManager.class);
        assertThat(context.getSystemService(VIBRATOR_SERVICE)).isInstanceOf(Vibrator.class);
        assertThat(context.getSystemService(CONNECTIVITY_SERVICE)).isInstanceOf(ConnectivityManager.class);
        assertThat(context.getSystemService(WIFI_SERVICE)).isInstanceOf(WifiManager.class);
        assertThat(context.getSystemService(AUDIO_SERVICE)).isInstanceOf(AudioManager.class);
        assertThat(context.getSystemService(TELEPHONY_SERVICE)).isInstanceOf(TelephonyManager.class);
        assertThat(context.getSystemService(INPUT_METHOD_SERVICE)).isInstanceOf(InputMethodManager.class);
        assertThat(context.getSystemService(UI_MODE_SERVICE)).isInstanceOf(UiModeManager.class);
        assertThat(context.getSystemService(DOWNLOAD_SERVICE)).isInstanceOf(DownloadManager.class);
        assertThat(context.getSystemService(DEVICE_POLICY_SERVICE)).isInstanceOf(DevicePolicyManager.class);
        assertThat(context.getSystemService(DROPBOX_SERVICE)).isInstanceOf(DropBoxManager.class);
        assertThat(context.getSystemService(MEDIA_ROUTER_SERVICE)).isInstanceOf(MediaRouter.class);
        assertThat(context.getSystemService(ACCESSIBILITY_SERVICE)).isInstanceOf(AccessibilityManager.class);
        assertThat(context.getSystemService(NSD_SERVICE)).isInstanceOf(NsdManager.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void shouldProvideServicesIntroducedInJellyBeanMr1() throws Exception {
        assertThat(context.getSystemService(DISPLAY_SERVICE)).isInstanceOf(DisplayManager.class);
        assertThat(context.getSystemService(USER_SERVICE)).isInstanceOf(UserManager.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void shouldProvideServicesIntroducedInKitKat() throws Exception {
        assertThat(context.getSystemService(PRINT_SERVICE)).isInstanceOf(PrintManager.class);
        assertThat(context.getSystemService(CAPTIONING_SERVICE)).isInstanceOf(CaptioningManager.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldProvideServicesIntroducedInLollipop() throws Exception {
        assertThat(context.getSystemService(MEDIA_SESSION_SERVICE)).isInstanceOf(MediaSessionManager.class);
        assertThat(context.getSystemService(BATTERY_SERVICE)).isInstanceOf(BatteryManager.class);
        assertThat(context.getSystemService(RESTRICTIONS_SERVICE)).isInstanceOf(RestrictionsManager.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void shouldProvideServicesIntroducedInLollipopMr1() throws Exception {
        assertThat(context.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE)).isInstanceOf(SubscriptionManager.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldProvideServicesIntroducedMarshmallow() throws Exception {
        assertThat(context.getSystemService(FINGERPRINT_SERVICE)).isInstanceOf(FingerprintManager.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldProvideServicesIntroducedOreo() throws Exception {
        // Context.AUTOFILL_MANAGER_SERVICE is marked @hide and this is the documented way to obtain
        // this service.
        AutofillManager autofillManager = context.getSystemService(AutofillManager.class);
        assertThat(autofillManager).isNotNull();
        assertThat(context.getSystemService(TEXT_CLASSIFICATION_SERVICE)).isInstanceOf(TextClassificationManager.class);
    }

    @Test
    public void shouldProvideLayoutInflater() throws Exception {
        Object systemService = context.getSystemService(LAYOUT_INFLATER_SERVICE);
        assertThat(systemService).isInstanceOf(LayoutInflater.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void shouldCorrectlyInstantiatedAccessibilityService() throws Exception {
        AccessibilityManager accessibilityManager = ((AccessibilityManager) (context.getSystemService(ACCESSIBILITY_SERVICE)));
        AccessibilityManager.TouchExplorationStateChangeListener listener = ShadowApplicationTest.createTouchListener();
        assertThat(accessibilityManager.addTouchExplorationStateChangeListener(listener)).isTrue();
        assertThat(accessibilityManager.removeTouchExplorationStateChangeListener(listener)).isTrue();
    }

    @Test
    public void bindServiceShouldCallOnServiceConnectedWithDefaultValues() {
        TestService service = new TestService();
        ComponentName expectedComponentName = new ComponentName("", "");
        Binder expectedBinder = new Binder();
        Shadows.shadowOf(context).setComponentNameAndServiceForBindService(expectedComponentName, expectedBinder);
        context.bindService(new Intent(""), service, BIND_AUTO_CREATE);
        assertThat(service.name).isEqualTo(expectedComponentName);
        assertThat(service.service).isEqualTo(expectedBinder);
        assertThat(service.nameUnbound).isNull();
        context.unbindService(service);
        assertThat(service.nameUnbound).isEqualTo(expectedComponentName);
    }

    @Test
    public void bindServiceShouldCallOnServiceConnectedWithNullValues() {
        TestService service = new TestService();
        context.bindService(new Intent(""), service, BIND_AUTO_CREATE);
        assertThat(service.name).isNull();
        assertThat(service.service).isNull();
    }

    @Test
    public void bindServiceShouldCallOnServiceConnectedWhenNotPaused() {
        ShadowLooper.pauseMainLooper();
        ComponentName expectedComponentName = new ComponentName("", "");
        Binder expectedBinder = new Binder();
        Intent expectedIntent = new Intent("expected");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntent, expectedComponentName, expectedBinder);
        TestService service = new TestService();
        assertThat(context.bindService(expectedIntent, service, BIND_AUTO_CREATE)).isTrue();
        assertThat(service.name).isNull();
        assertThat(service.service).isNull();
        ShadowLooper.unPauseMainLooper();
        assertThat(service.name).isEqualTo(expectedComponentName);
        assertThat(service.service).isEqualTo(expectedBinder);
    }

    @Test
    public void unbindServiceShouldCallOnServiceDisconnectedWhenNotPaused() {
        TestService service = new TestService();
        ComponentName expectedComponentName = new ComponentName("", "");
        Binder expectedBinder = new Binder();
        Intent expectedIntent = new Intent("expected");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntent, expectedComponentName, expectedBinder);
        context.bindService(expectedIntent, service, BIND_AUTO_CREATE);
        ShadowLooper.pauseMainLooper();
        context.unbindService(service);
        assertThat(service.nameUnbound).isNull();
        ShadowLooper.unPauseMainLooper();
        assertThat(service.nameUnbound).isEqualTo(expectedComponentName);
    }

    @Test
    public void unbindServiceAddsEntryToUnboundServicesCollection() {
        TestService service = new TestService();
        ComponentName expectedComponentName = new ComponentName("", "");
        Binder expectedBinder = new Binder();
        Intent expectedIntent = new Intent("expected");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntent, expectedComponentName, expectedBinder);
        context.bindService(expectedIntent, service, BIND_AUTO_CREATE);
        context.unbindService(service);
        assertThat(Shadows.shadowOf(context).getUnboundServiceConnections()).hasSize(1);
        assertThat(Shadows.shadowOf(context).getUnboundServiceConnections().get(0)).isSameAs(service);
    }

    @Test
    public void declaringServiceUnbindableMakesBindServiceReturnFalse() {
        ShadowLooper.pauseMainLooper();
        TestService service = new TestService();
        ComponentName expectedComponentName = new ComponentName("", "");
        Binder expectedBinder = new Binder();
        Intent expectedIntent = new Intent("refuseToBind");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntent, expectedComponentName, expectedBinder);
        Shadows.shadowOf(context).declareActionUnbindable(expectedIntent.getAction());
        Assert.assertFalse(context.bindService(expectedIntent, service, BIND_AUTO_CREATE));
        ShadowLooper.unPauseMainLooper();
        assertThat(service.name).isNull();
        assertThat(service.service).isNull();
        assertThat(Shadows.shadowOf(context).peekNextStartedService()).isNull();
    }

    @Test
    public void bindServiceWithMultipleIntentsMapping() {
        TestService service = new TestService();
        ComponentName expectedComponentNameOne = new ComponentName("package", "one");
        Binder expectedBinderOne = new Binder();
        Intent expectedIntentOne = new Intent("expected_one");
        ComponentName expectedComponentNameTwo = new ComponentName("package", "two");
        Binder expectedBinderTwo = new Binder();
        Intent expectedIntentTwo = new Intent("expected_two");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntentOne, expectedComponentNameOne, expectedBinderOne);
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntentTwo, expectedComponentNameTwo, expectedBinderTwo);
        context.bindService(expectedIntentOne, service, BIND_AUTO_CREATE);
        assertThat(service.name).isEqualTo(expectedComponentNameOne);
        assertThat(service.service).isEqualTo(expectedBinderOne);
        context.bindService(expectedIntentTwo, service, BIND_AUTO_CREATE);
        assertThat(service.name).isEqualTo(expectedComponentNameTwo);
        assertThat(service.service).isEqualTo(expectedBinderTwo);
    }

    @Test
    public void bindServiceWithMultipleIntentsMappingWithDefault() {
        TestService service = new TestService();
        ComponentName expectedComponentNameOne = new ComponentName("package", "one");
        Binder expectedBinderOne = new Binder();
        Intent expectedIntentOne = new Intent("expected_one");
        ComponentName expectedComponentNameTwo = new ComponentName("package", "two");
        Binder expectedBinderTwo = new Binder();
        Intent expectedIntentTwo = new Intent("expected_two");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntentOne, expectedComponentNameOne, expectedBinderOne);
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntentTwo, expectedComponentNameTwo, expectedBinderTwo);
        context.bindService(expectedIntentOne, service, BIND_AUTO_CREATE);
        assertThat(service.name).isEqualTo(expectedComponentNameOne);
        assertThat(service.service).isEqualTo(expectedBinderOne);
        context.bindService(expectedIntentTwo, service, BIND_AUTO_CREATE);
        assertThat(service.name).isEqualTo(expectedComponentNameTwo);
        assertThat(service.service).isEqualTo(expectedBinderTwo);
        context.bindService(new Intent("unknown"), service, BIND_AUTO_CREATE);
        assertThat(service.name).isNull();
        assertThat(service.service).isNull();
    }

    @Test
    public void unbindServiceWithMultipleIntentsMapping() {
        TestService serviceOne = new TestService();
        ComponentName expectedComponentNameOne = new ComponentName("package", "one");
        Binder expectedBinderOne = new Binder();
        Intent expectedIntentOne = new Intent("expected_one");
        TestService serviceTwo = new TestService();
        ComponentName expectedComponentNameTwo = new ComponentName("package", "two");
        Binder expectedBinderTwo = new Binder();
        Intent expectedIntentTwo = new Intent("expected_two");
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntentOne, expectedComponentNameOne, expectedBinderOne);
        Shadows.shadowOf(context).setComponentNameAndServiceForBindServiceForIntent(expectedIntentTwo, expectedComponentNameTwo, expectedBinderTwo);
        context.bindService(expectedIntentOne, serviceOne, BIND_AUTO_CREATE);
        assertThat(serviceOne.nameUnbound).isNull();
        context.unbindService(serviceOne);
        assertThat(serviceOne.name).isEqualTo(expectedComponentNameOne);
        context.bindService(expectedIntentTwo, serviceTwo, BIND_AUTO_CREATE);
        assertThat(serviceTwo.nameUnbound).isNull();
        context.unbindService(serviceTwo);
        assertThat(serviceTwo.name).isEqualTo(expectedComponentNameTwo);
        TestService serviceDefault = new TestService();
        context.bindService(new Intent("default"), serviceDefault, BIND_AUTO_CREATE);
        assertThat(serviceDefault.nameUnbound).isNull();
        context.unbindService(serviceDefault);
        assertThat(serviceDefault.name).isNull();
    }

    @Test
    public void shouldHaveStoppedServiceIntentAndIndicateServiceWasntRunning() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        Intent intent = getSomeActionIntent("some.action");
        boolean wasRunning = activity.stopService(intent);
        Assert.assertFalse(wasRunning);
        assertThat(Shadows.shadowOf(context).getNextStoppedService()).isEqualTo(intent);
    }

    @Test
    public void shouldHaveStoppedServiceIntentAndIndicateServiceWasRunning() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        Intent intent = getSomeActionIntent("some.action");
        activity.startService(intent);
        boolean wasRunning = activity.stopService(intent);
        Assert.assertTrue(wasRunning);
        assertThat(Shadows.shadowOf(context).getNextStoppedService()).isEqualTo(intent);
    }

    @Test
    public void shouldHaveStoppedServiceByStartedComponent() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        ComponentName componentName = new ComponentName("package.test", "package.test.TestClass");
        Intent startServiceIntent = new Intent().setComponent(componentName);
        ComponentName startedComponent = activity.startService(startServiceIntent);
        assertThat(startedComponent.getPackageName()).isEqualTo("package.test");
        assertThat(startedComponent.getClassName()).isEqualTo("package.test.TestClass");
        Intent stopServiceIntent = new Intent().setComponent(startedComponent);
        stopServiceIntent.putExtra("someExtra", "someValue");
        boolean wasRunning = activity.stopService(stopServiceIntent);
        Assert.assertTrue(wasRunning);
        final Intent nextStoppedService = Shadows.shadowOf(context).getNextStoppedService();
        assertThat(nextStoppedService.filterEquals(startServiceIntent)).isTrue();
        assertThat(nextStoppedService.getStringExtra("someExtra")).isEqualTo("someValue");
    }

    @Test
    public void shouldClearStartedServiceIntents() {
        context.startService(getSomeActionIntent("some.action"));
        context.startService(getSomeActionIntent("another.action"));
        Shadows.shadowOf(context).clearStartedServices();
        Assert.assertNull(Shadows.shadowOf(context).getNextStartedService());
    }

    @Test
    public void shouldThrowIfContainsRegisteredReceiverOfAction() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        activity.registerReceiver(new ShadowApplicationTest.TestBroadcastReceiver(), new IntentFilter("Foo"));
        try {
            Shadows.shadowOf(context).assertNoBroadcastListenersOfActionRegistered(activity, "Foo");
            Assert.fail("should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void shouldNotThrowIfDoesNotContainsRegisteredReceiverOfAction() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        activity.registerReceiver(new ShadowApplicationTest.TestBroadcastReceiver(), new IntentFilter("Foo"));
        Shadows.shadowOf(context).assertNoBroadcastListenersOfActionRegistered(activity, "Bar");
    }

    @Test
    public void canAnswerIfReceiverIsRegisteredForIntent() throws Exception {
        BroadcastReceiver expectedReceiver = new ShadowApplicationTest.TestBroadcastReceiver();
        Assert.assertFalse(Shadows.shadowOf(context).hasReceiverForIntent(new Intent("Foo")));
        context.registerReceiver(expectedReceiver, new IntentFilter("Foo"));
        Assert.assertTrue(Shadows.shadowOf(context).hasReceiverForIntent(new Intent("Foo")));
    }

    @Test
    public void canFindAllReceiversForAnIntent() throws Exception {
        BroadcastReceiver expectedReceiver = new ShadowApplicationTest.TestBroadcastReceiver();
        Assert.assertFalse(Shadows.shadowOf(context).hasReceiverForIntent(new Intent("Foo")));
        context.registerReceiver(expectedReceiver, new IntentFilter("Foo"));
        context.registerReceiver(expectedReceiver, new IntentFilter("Foo"));
        assertThat(Shadows.shadowOf(context).getReceiversForIntent(new Intent("Foo"))).hasSize(2);
    }

    @Test
    public void broadcasts_shouldBeLogged() {
        Intent broadcastIntent = new Intent("foo");
        context.sendBroadcast(broadcastIntent);
        List<Intent> broadcastIntents = Shadows.shadowOf(context).getBroadcastIntents();
        assertThat(broadcastIntents).hasSize(1);
        assertThat(broadcastIntents.get(0)).isEqualTo(broadcastIntent);
    }

    @Test
    public void clearRegisteredReceivers_clearsReceivers() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        activity.registerReceiver(new ShadowApplicationTest.TestBroadcastReceiver(), new IntentFilter("Foo"));
        assertThat(Shadows.shadowOf(context).getRegisteredReceivers().size()).isAtLeast(1);
        Shadows.shadowOf(context).clearRegisteredReceivers();
        assertThat(Shadows.shadowOf(context).getRegisteredReceivers()).isEmpty();
    }

    @Test
    public void sendStickyBroadcast() {
        Intent broadcastIntent = new Intent("Foo");
        context.sendStickyBroadcast(broadcastIntent);
        // Register after the broadcast has fired. We should immediately get a sticky event.
        ShadowApplicationTest.TestBroadcastReceiver receiver = new ShadowApplicationTest.TestBroadcastReceiver();
        context.registerReceiver(receiver, new IntentFilter("Foo"));
        Assert.assertTrue(receiver.isSticky);
        // Fire the broadcast again, and we should get a non-sticky event.
        context.sendStickyBroadcast(broadcastIntent);
        Assert.assertFalse(receiver.isSticky);
    }

    @Test
    public void shouldRememberResourcesAfterLazilyLoading() throws Exception {
        Assert.assertSame(context.getResources(), context.getResources());
    }

    @Test
    public void startActivity_whenActivityCheckingEnabled_doesntFindResolveInfo() throws Exception {
        Shadows.shadowOf(context).checkActivities(true);
        String action = "com.does.not.exist.android.app.v2.mobile";
        try {
            context.startActivity(new Intent(action).addFlags(FLAG_ACTIVITY_NEW_TASK));
            Assert.fail("Expected startActivity to throw ActivityNotFoundException!");
        } catch (ActivityNotFoundException e) {
            assertThat(e.getMessage()).contains(action);
            assertThat(Shadows.shadowOf(context).getNextStartedActivity()).isNull();
        }
    }

    @Test
    public void startActivity_whenActivityCheckingEnabled_findsResolveInfo() throws Exception {
        Shadows.shadowOf(context).checkActivities(true);
        context.startActivity(new Intent().setClassName(context, TestActivity.class.getName()).addFlags(FLAG_ACTIVITY_NEW_TASK));
        assertThat(Shadows.shadowOf(context).getNextStartedActivity()).isNotNull();
    }

    @Test
    public void bindServiceShouldAddServiceConnectionToListOfBoundServiceConnections() {
        final ServiceConnection expectedServiceConnection = new ShadowApplicationTest.EmptyServiceConnection();
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(0);
        assertThat(context.bindService(new Intent("connect"), expectedServiceConnection, 0)).isTrue();
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(1);
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections().get(0)).isSameAs(expectedServiceConnection);
    }

    @Test
    public void bindServiceShouldAddServiceConnectionToListOfBoundServiceConnectionsEvenIfServiceUnboundable() {
        final ServiceConnection expectedServiceConnection = new ShadowApplicationTest.EmptyServiceConnection();
        final String unboundableAction = "refuse";
        final Intent serviceIntent = new Intent(unboundableAction);
        Shadows.shadowOf(context).declareActionUnbindable(unboundableAction);
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(0);
        assertThat(context.bindService(serviceIntent, expectedServiceConnection, 0)).isFalse();
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(1);
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections().get(0)).isSameAs(expectedServiceConnection);
    }

    @Test
    public void unbindServiceShouldRemoveServiceConnectionFromListOfBoundServiceConnections() {
        final ServiceConnection expectedServiceConnection = new ShadowApplicationTest.EmptyServiceConnection();
        assertThat(context.bindService(new Intent("connect"), expectedServiceConnection, 0)).isTrue();
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(1);
        assertThat(Shadows.shadowOf(context).getUnboundServiceConnections()).hasSize(0);
        context.unbindService(expectedServiceConnection);
        assertThat(Shadows.shadowOf(context).getBoundServiceConnections()).hasSize(0);
        assertThat(Shadows.shadowOf(context).getUnboundServiceConnections()).hasSize(1);
        assertThat(Shadows.shadowOf(context).getUnboundServiceConnections().get(0)).isSameAs(expectedServiceConnection);
    }

    @Test
    public void getThreadScheduler_shouldMatchRobolectricValue() {
        assertThat(Shadows.shadowOf(context).getForegroundThreadScheduler()).isSameAs(Robolectric.getForegroundThreadScheduler());
        assertThat(Shadows.shadowOf(context).getBackgroundThreadScheduler()).isSameAs(Robolectric.getBackgroundThreadScheduler());
    }

    @Test
    public void getForegroundThreadScheduler_shouldMatchRuntimeEnvironment() {
        Scheduler s = new Scheduler();
        RuntimeEnvironment.setMasterScheduler(s);
        assertThat(Shadows.shadowOf(context).getForegroundThreadScheduler()).isSameAs(s);
    }

    @Test
    public void getBackgroundThreadScheduler_shouldDifferFromRuntimeEnvironment_byDefault() {
        Scheduler s = new Scheduler();
        RuntimeEnvironment.setMasterScheduler(s);
        assertThat(Shadows.shadowOf(context).getBackgroundThreadScheduler()).isNotSameAs(RuntimeEnvironment.getMasterScheduler());
    }

    @Test
    public void getBackgroundThreadScheduler_shouldDifferFromRuntimeEnvironment_withAdvancedScheduling() {
        Scheduler s = new Scheduler();
        RuntimeEnvironment.setMasterScheduler(s);
        assertThat(Shadows.shadowOf(context).getBackgroundThreadScheduler()).isNotSameAs(s);
    }

    @Test
    public void getLatestPopupWindow() {
        PopupWindow pw = new PopupWindow(new android.widget.LinearLayout(context));
        pw.showAtLocation(new android.widget.LinearLayout(context), CENTER, 0, 0);
        PopupWindow latestPopupWindow = Shadows.shadowOf(RuntimeEnvironment.application).getLatestPopupWindow();
        assertThat(latestPopupWindow).isSameAs(pw);
    }

    // ///////////////////////////
    private static class EmptyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    public static class TestBroadcastReceiver extends BroadcastReceiver {
        public Context context;

        public Intent intent;

        public boolean isSticky;

        @Override
        public void onReceive(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
            this.isSticky = isInitialStickyBroadcast();
        }
    }
}

