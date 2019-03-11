package io.nlopez.smartlocation.activity;


import RuntimeEnvironment.application;
import com.google.android.gms.location.DetectedActivity;
import io.nlopez.smartlocation.CustomTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Tests {@link ActivityStore}
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class ActivityStoreTest {
    private static final DetectedActivity TEST_ACTIVITY = new DetectedActivity(DetectedActivity.UNKNOWN, 100);

    private static final String TEST_ACTIVITY_ID = "test_activity_1";

    @Test
    public void test_activity_store_full_cycle() {
        ActivityStore store = new ActivityStore(application.getApplicationContext());
        store.setPreferences(getSharedPreferences());
        Assert.assertNull(store.get(ActivityStoreTest.TEST_ACTIVITY_ID));
        store.put(ActivityStoreTest.TEST_ACTIVITY_ID, ActivityStoreTest.TEST_ACTIVITY);
        DetectedActivity storedActivity = store.get(ActivityStoreTest.TEST_ACTIVITY_ID);
        Assert.assertEquals(storedActivity.getConfidence(), ActivityStoreTest.TEST_ACTIVITY.getConfidence());
        Assert.assertEquals(storedActivity.getType(), ActivityStoreTest.TEST_ACTIVITY.getType());
        store.remove(ActivityStoreTest.TEST_ACTIVITY_ID);
        Assert.assertNull(store.get(ActivityStoreTest.TEST_ACTIVITY_ID));
    }
}

