package io.nlopez.smartlocation.location;


import RuntimeEnvironment.application;
import android.location.Location;
import io.nlopez.smartlocation.CustomTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Created by nacho on 1/9/15.
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class LocationStoreTest {
    private static final double DELTA = 1.0E-7;

    private static final String TEST_LOCATION_ID = "test_location_1";

    private static final float ACCURACY = 1.234F;

    private static final double ALTITUDE = 12.34;

    private static final float BEARING = 123.0F;

    private static final float SPEED = 321.0F;

    private static final double LATITUDE = -50.123456;

    private static final double LONGITUDE = 9.8765432;

    private static final int TIME = 987654321;

    private final Location testLocation = new Location("test");

    @Test
    public void test_location_store_full_cycle() {
        LocationStore store = new LocationStore(application.getApplicationContext());
        store.setPreferences(getSharedPreferences());
        Assert.assertNull(store.get(LocationStoreTest.TEST_LOCATION_ID));
        store.put(LocationStoreTest.TEST_LOCATION_ID, testLocation);
        Location storedLocation = store.get(LocationStoreTest.TEST_LOCATION_ID);
        Assert.assertEquals(storedLocation.getAccuracy(), testLocation.getAccuracy(), LocationStoreTest.DELTA);
        Assert.assertEquals(storedLocation.getAltitude(), testLocation.getAltitude(), LocationStoreTest.DELTA);
        Assert.assertEquals(storedLocation.getBearing(), testLocation.getBearing(), LocationStoreTest.DELTA);
        Assert.assertEquals(storedLocation.getLatitude(), testLocation.getLatitude(), LocationStoreTest.DELTA);
        Assert.assertEquals(storedLocation.getLongitude(), testLocation.getLongitude(), LocationStoreTest.DELTA);
        Assert.assertEquals(storedLocation.getSpeed(), testLocation.getSpeed(), LocationStoreTest.DELTA);
        Assert.assertEquals(storedLocation.getTime(), testLocation.getTime());
        store.remove(LocationStoreTest.TEST_LOCATION_ID);
        Assert.assertNull(store.get(LocationStoreTest.TEST_LOCATION_ID));
    }
}

