package io.nlopez.smartlocation.geofencing;


import RuntimeEnvironment.application;
import io.nlopez.smartlocation.CustomTestRunner;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Created by nacho on 1/9/15.
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class GeofencingStoreTest {
    private static final double DELTA = 1.0E-7;

    private static final String TEST_GEOFENCE_ID = "test_geofence_1";

    private GeofenceModel testGeofence;

    @Test
    public void test_geofencing_store_full_cycle() {
        GeofencingStore store = new GeofencingStore(application.getApplicationContext());
        store.setPreferences(getSharedPreferences());
        Assert.assertNull(store.get(GeofencingStoreTest.TEST_GEOFENCE_ID));
        store.put(GeofencingStoreTest.TEST_GEOFENCE_ID, testGeofence);
        GeofenceModel geofenceModel = store.get(GeofencingStoreTest.TEST_GEOFENCE_ID);
        Assert.assertEquals(geofenceModel.getLatitude(), testGeofence.getLatitude(), GeofencingStoreTest.DELTA);
        Assert.assertEquals(geofenceModel.getLongitude(), testGeofence.getLongitude(), GeofencingStoreTest.DELTA);
        Assert.assertEquals(geofenceModel.getExpiration(), testGeofence.getExpiration());
        Assert.assertEquals(geofenceModel.getRadius(), testGeofence.getRadius(), GeofencingStoreTest.DELTA);
        Assert.assertEquals(geofenceModel.getTransition(), testGeofence.getTransition());
        Assert.assertEquals(geofenceModel.getLoiteringDelay(), testGeofence.getLoiteringDelay());
        store.remove(GeofencingStoreTest.TEST_GEOFENCE_ID);
        Assert.assertNull(store.get(GeofencingStoreTest.TEST_GEOFENCE_ID));
    }
}

