package org.robolectric.shadows;


import Event.MOVE_TO_BACKGROUND;
import Event.MOVE_TO_FOREGROUND;
import ShadowUsageStatsManager.EventBuilder;
import UsageStatsManager.EXTRA_OBSERVER_ID;
import UsageStatsManager.EXTRA_TIME_LIMIT;
import UsageStatsManager.EXTRA_TIME_USED;
import UsageStatsManager.STANDBY_BUCKET_ACTIVE;
import UsageStatsManager.STANDBY_BUCKET_RARE;
import android.app.Application;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowUsageStatsManager.UsageStatsBuilder;

import static Build.VERSION_CODES.P;


/**
 * Test for {@link ShadowUsageStatsManager}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowUsageStatsManagerTest {
    private static final String TEST_PACKAGE_NAME1 = "com.company1.pkg1";

    private static final String TEST_PACKAGE_NAME2 = "com.company2.pkg2";

    private static final String TEST_ACTIVITY_NAME = "com.company2.pkg2.Activity";

    private UsageStatsManager usageStatsManager;

    private Application context;

    @Test
    public void testQueryEvents_emptyEvents() throws Exception {
        UsageEvents events = usageStatsManager.queryEvents(1000L, 2000L);
        Event event = new Event();
        assertThat(events.hasNextEvent()).isFalse();
        assertThat(events.getNextEvent(event)).isFalse();
    }

    @Test
    public void testQueryEvents_appendEventData_shouldCombineWithPreviousData() throws Exception {
        Shadows.shadowOf(usageStatsManager).addEvent(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 500L, MOVE_TO_FOREGROUND);
        Shadows.shadowOf(usageStatsManager).addEvent(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 1000L, MOVE_TO_BACKGROUND);
        Shadows.shadowOf(usageStatsManager).addEvent(EventBuilder.buildEvent().setTimeStamp(1500L).setPackage(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME2).setClass(ShadowUsageStatsManagerTest.TEST_ACTIVITY_NAME).setEventType(MOVE_TO_FOREGROUND).build());
        Shadows.shadowOf(usageStatsManager).addEvent(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME2, 2000L, MOVE_TO_BACKGROUND);
        Shadows.shadowOf(usageStatsManager).addEvent(EventBuilder.buildEvent().setTimeStamp(2500L).setPackage(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1).setEventType(MOVE_TO_FOREGROUND).setClass(ShadowUsageStatsManagerTest.TEST_ACTIVITY_NAME).build());
        UsageEvents events = usageStatsManager.queryEvents(1000L, 2000L);
        Event event = new Event();
        assertThat(events.hasNextEvent()).isTrue();
        assertThat(events.getNextEvent(event)).isTrue();
        assertThat(event.getPackageName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1);
        assertThat(event.getTimeStamp()).isEqualTo(1000L);
        assertThat(event.getEventType()).isEqualTo(MOVE_TO_BACKGROUND);
        assertThat(events.hasNextEvent()).isTrue();
        assertThat(events.getNextEvent(event)).isTrue();
        assertThat(event.getPackageName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME2);
        assertThat(event.getTimeStamp()).isEqualTo(1500L);
        assertThat(event.getEventType()).isEqualTo(MOVE_TO_FOREGROUND);
        assertThat(event.getClassName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_ACTIVITY_NAME);
        assertThat(events.hasNextEvent()).isFalse();
        assertThat(events.getNextEvent(event)).isFalse();
    }

    @Test
    public void testQueryEvents_appendEventData_simulateTimeChange_shouldAddOffsetToPreviousData() throws Exception {
        Shadows.shadowOf(usageStatsManager).addEvent(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 500L, MOVE_TO_FOREGROUND);
        Shadows.shadowOf(usageStatsManager).addEvent(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 1000L, MOVE_TO_BACKGROUND);
        Shadows.shadowOf(usageStatsManager).addEvent(EventBuilder.buildEvent().setTimeStamp(1500L).setPackage(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME2).setClass(ShadowUsageStatsManagerTest.TEST_ACTIVITY_NAME).setEventType(MOVE_TO_FOREGROUND).build());
        Shadows.shadowOf(usageStatsManager).addEvent(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME2, 2000L, MOVE_TO_BACKGROUND);
        Shadows.shadowOf(usageStatsManager).addEvent(EventBuilder.buildEvent().setTimeStamp(2500L).setPackage(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1).setEventType(MOVE_TO_FOREGROUND).setClass(ShadowUsageStatsManagerTest.TEST_ACTIVITY_NAME).build());
        Shadows.shadowOf(usageStatsManager).simulateTimeChange(10000L);
        UsageEvents events = usageStatsManager.queryEvents(11000L, 12000L);
        Event event = new Event();
        assertThat(events.hasNextEvent()).isTrue();
        assertThat(events.getNextEvent(event)).isTrue();
        assertThat(event.getPackageName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1);
        assertThat(event.getTimeStamp()).isEqualTo(11000L);
        assertThat(event.getEventType()).isEqualTo(MOVE_TO_BACKGROUND);
        assertThat(events.hasNextEvent()).isTrue();
        assertThat(events.getNextEvent(event)).isTrue();
        assertThat(event.getPackageName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME2);
        assertThat(event.getTimeStamp()).isEqualTo(11500L);
        assertThat(event.getEventType()).isEqualTo(MOVE_TO_FOREGROUND);
        assertThat(event.getClassName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_ACTIVITY_NAME);
        assertThat(events.hasNextEvent()).isFalse();
        assertThat(events.getNextEvent(event)).isFalse();
    }

    @Test
    @Config(minSdk = P)
    public void testGetAppStandbyBucket_withPackageName() throws Exception {
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBuckets()).isEmpty();
        Shadows.shadowOf(usageStatsManager).setAppStandbyBucket("app1", STANDBY_BUCKET_RARE);
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBucket("app1")).isEqualTo(STANDBY_BUCKET_RARE);
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBuckets().keySet()).containsExactly("app1");
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBuckets().get("app1")).isEqualTo(STANDBY_BUCKET_RARE);
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBucket("app_unset")).isEqualTo(STANDBY_BUCKET_ACTIVE);
    }

    @Test
    @Config(minSdk = P)
    public void testSetAppStandbyBuckets() throws Exception {
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBuckets()).isEmpty();
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBucket("app1")).isEqualTo(STANDBY_BUCKET_ACTIVE);
        Map<String, Integer> appBuckets = Collections.singletonMap("app1", STANDBY_BUCKET_RARE);
        Shadows.shadowOf(usageStatsManager).setAppStandbyBuckets(appBuckets);
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBuckets()).isEqualTo(appBuckets);
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBucket("app1")).isEqualTo(STANDBY_BUCKET_RARE);
    }

    @Test
    @Config(minSdk = P)
    public void testGetAppStandbyBucket_currentApp() throws Exception {
        Shadows.shadowOf(usageStatsManager).setCurrentAppStandbyBucket(STANDBY_BUCKET_RARE);
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBucket()).isEqualTo(STANDBY_BUCKET_RARE);
        ShadowUsageStatsManager.reset();
        assertThat(Shadows.shadowOf(usageStatsManager).getAppStandbyBucket()).isEqualTo(STANDBY_BUCKET_ACTIVE);
    }

    @Test
    @Config(minSdk = P)
    public void testRegisterAppUsageObserver_uniqueObserverIds_shouldAddBothObservers() {
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION1"), 0);
        usageStatsManager.registerAppUsageObserver(12, new String[]{ "com.package1", "com.package2" }, 123L, TimeUnit.MINUTES, pendingIntent1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION2"), 0);
        usageStatsManager.registerAppUsageObserver(24, new String[]{ "com.package3" }, 456L, TimeUnit.SECONDS, pendingIntent2);
        assertThat(Shadows.shadowOf(usageStatsManager).getRegisteredAppUsageObservers()).containsExactly(new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(12, ImmutableList.of("com.package1", "com.package2"), 123L, TimeUnit.MINUTES, pendingIntent1), new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(24, ImmutableList.of("com.package3"), 456L, TimeUnit.SECONDS, pendingIntent2));
    }

    @Test
    @Config(minSdk = P)
    public void testRegisterAppUsageObserver_duplicateObserverIds_shouldOverrideExistingObserver() {
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION1"), 0);
        usageStatsManager.registerAppUsageObserver(12, new String[]{ "com.package1", "com.package2" }, 123L, TimeUnit.MINUTES, pendingIntent1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION2"), 0);
        usageStatsManager.registerAppUsageObserver(12, new String[]{ "com.package3" }, 456L, TimeUnit.SECONDS, pendingIntent2);
        assertThat(Shadows.shadowOf(usageStatsManager).getRegisteredAppUsageObservers()).containsExactly(new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(12, ImmutableList.of("com.package3"), 456L, TimeUnit.SECONDS, pendingIntent2));
    }

    @Test
    @Config(minSdk = P)
    public void testUnregisterAppUsageObserver_existingObserverId_shouldRemoveObserver() {
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION1"), 0);
        usageStatsManager.registerAppUsageObserver(12, new String[]{ "com.package1", "com.package2" }, 123L, TimeUnit.MINUTES, pendingIntent1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION2"), 0);
        usageStatsManager.registerAppUsageObserver(24, new String[]{ "com.package3" }, 456L, TimeUnit.SECONDS, pendingIntent2);
        usageStatsManager.unregisterAppUsageObserver(12);
        assertThat(Shadows.shadowOf(usageStatsManager).getRegisteredAppUsageObservers()).containsExactly(new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(24, ImmutableList.of("com.package3"), 456L, TimeUnit.SECONDS, pendingIntent2));
    }

    @Test
    @Config(minSdk = P)
    public void testUnregisterAppUsageObserver_nonExistentObserverId_shouldBeNoOp() {
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION1"), 0);
        usageStatsManager.registerAppUsageObserver(12, new String[]{ "com.package1", "com.package2" }, 123L, TimeUnit.MINUTES, pendingIntent1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION2"), 0);
        usageStatsManager.registerAppUsageObserver(24, new String[]{ "com.package3" }, 456L, TimeUnit.SECONDS, pendingIntent2);
        usageStatsManager.unregisterAppUsageObserver(36);
        assertThat(Shadows.shadowOf(usageStatsManager).getRegisteredAppUsageObservers()).containsExactly(new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(12, ImmutableList.of("com.package1", "com.package2"), 123L, TimeUnit.MINUTES, pendingIntent1), new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(24, ImmutableList.of("com.package3"), 456L, TimeUnit.SECONDS, pendingIntent2));
    }

    @Test
    @Config(minSdk = P)
    public void testTriggerRegisteredAppUsageObserver_shouldSendIntentAndRemoveObserver() {
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION1"), 0);
        usageStatsManager.registerAppUsageObserver(12, new String[]{ "com.package1", "com.package2" }, 123L, TimeUnit.MINUTES, pendingIntent1);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, new Intent("ACTION2"), 0);
        usageStatsManager.registerAppUsageObserver(24, new String[]{ "com.package3" }, 456L, TimeUnit.SECONDS, pendingIntent2);
        Shadows.shadowOf(usageStatsManager).triggerRegisteredAppUsageObserver(24, 500000L);
        List<Intent> broadcastIntents = Shadows.shadowOf(context).getBroadcastIntents();
        assertThat(broadcastIntents).hasSize(1);
        Intent broadcastIntent = broadcastIntents.get(0);
        assertThat(broadcastIntent.getAction()).isEqualTo("ACTION2");
        assertThat(broadcastIntent.getIntExtra(EXTRA_OBSERVER_ID, 0)).isEqualTo(24);
        assertThat(broadcastIntent.getLongExtra(EXTRA_TIME_LIMIT, 0)).isEqualTo(456000L);
        assertThat(broadcastIntent.getLongExtra(EXTRA_TIME_USED, 0)).isEqualTo(500000L);
        assertThat(Shadows.shadowOf(usageStatsManager).getRegisteredAppUsageObservers()).containsExactly(new org.robolectric.shadows.ShadowUsageStatsManager.AppUsageObserver(12, ImmutableList.of("com.package1", "com.package2"), 123L, TimeUnit.MINUTES, pendingIntent1));
    }

    @Test
    public void queryUsageStats_noStatsAdded() {
        List<UsageStats> results = usageStatsManager.queryUsageStats(INTERVAL_WEEKLY, 0, 3000);
        assertThat(results).isEmpty();
    }

    @Test
    public void queryUsageStats() {
        UsageStats usageStats1 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 0, 1000);
        UsageStats usageStats2 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 1001, 2000);
        UsageStats usageStats3 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 2001, 3000);
        UsageStats usageStats4 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 3001, 4000);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_WEEKLY, usageStats1);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_WEEKLY, usageStats2);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_WEEKLY, usageStats3);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_WEEKLY, usageStats4);
        // Query fully covers usageStats 2 and 3, and partially overlaps with 4.
        List<UsageStats> results = usageStatsManager.queryUsageStats(INTERVAL_WEEKLY, 1001, 3500);
        assertThat(results).containsExactly(usageStats2, usageStats3, usageStats4);
    }

    @Test
    public void queryUsageStats_multipleIntervalTypes() {
        // Weekly data.
        UsageStats usageStats1 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 1000, 2000);
        UsageStats usageStats2 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 2001, 3000);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_WEEKLY, usageStats1);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_WEEKLY, usageStats2);
        // Daily data.
        UsageStats usageStats3 = newUsageStats(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1, 2001, 3000);
        Shadows.shadowOf(usageStatsManager).addUsageStats(INTERVAL_DAILY, usageStats3);
        List<UsageStats> results = usageStatsManager.queryUsageStats(INTERVAL_WEEKLY, 0, 3000);
        assertThat(results).containsExactly(usageStats1, usageStats2);
        results = usageStatsManager.queryUsageStats(INTERVAL_DAILY, 0, 3000);
        assertThat(results).containsExactly(usageStats3);
    }

    @Test
    public void usageStatsBuilder_noFieldsSet() {
        UsageStats usage = // Don't set any fields; the object should still build.
        UsageStatsBuilder.newBuilder().build();
        assertThat(usage.getPackageName()).isNull();
        assertThat(usage.getFirstTimeStamp()).isEqualTo(0);
        assertThat(usage.getLastTimeStamp()).isEqualTo(0);
        assertThat(usage.getLastTimeUsed()).isEqualTo(0);
        assertThat(usage.getTotalTimeInForeground()).isEqualTo(0);
    }

    @Test
    public void usageStatsBuilder() {
        long firstTimestamp = 1500000000000L;
        long lastTimestamp = firstTimestamp + 10000;
        long lastTimeUsed = firstTimestamp + 100;
        long totalTimeInForeground = TimeUnit.HOURS.toMillis(10);
        UsageStats usage = // Set all fields
        UsageStatsBuilder.newBuilder().setPackageName(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1).setFirstTimeStamp(firstTimestamp).setLastTimeStamp(lastTimestamp).setLastTimeUsed(lastTimeUsed).setTotalTimeInForeground(totalTimeInForeground).build();
        assertThat(usage.getPackageName()).isEqualTo(ShadowUsageStatsManagerTest.TEST_PACKAGE_NAME1);
        assertThat(usage.getFirstTimeStamp()).isEqualTo(firstTimestamp);
        assertThat(usage.getLastTimeStamp()).isEqualTo(lastTimestamp);
        assertThat(usage.getLastTimeUsed()).isEqualTo(lastTimeUsed);
        assertThat(usage.getTotalTimeInForeground()).isEqualTo(totalTimeInForeground);
    }
}

