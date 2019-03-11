package io.nlopez.smartlocation.geofencing.model;


import com.google.android.gms.location.Geofence;
import io.nlopez.smartlocation.CustomTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Created by mrm on 10/1/15.
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class GeofenceModelTest {
    private static final double DELTA = 1.0E-7;

    private static final String GEOFENCE_ID = "id1";

    private static final int EXPIRATION = 1234;

    private static final double LATITUDE = 50.123456;

    private static final double LONGITUDE = -30.65312;

    private static final int LOITERING_DELAY = 100;

    private static final int RADIUS = 444;

    private static final int TRANSITION = Geofence.GEOFENCE_TRANSITION_EXIT;

    @Test
    public void test_geofence_model_creation() {
        final GeofenceModel model = new GeofenceModel.Builder(GeofenceModelTest.GEOFENCE_ID).setExpiration(GeofenceModelTest.EXPIRATION).setLatitude(GeofenceModelTest.LATITUDE).setLongitude(GeofenceModelTest.LONGITUDE).setRadius(GeofenceModelTest.RADIUS).setTransition(GeofenceModelTest.TRANSITION).setLoiteringDelay(GeofenceModelTest.LOITERING_DELAY).build();
        Assert.assertEquals(model.getRequestId(), GeofenceModelTest.GEOFENCE_ID);
        Assert.assertEquals(model.getExpiration(), GeofenceModelTest.EXPIRATION);
        Assert.assertEquals(model.getLatitude(), GeofenceModelTest.LATITUDE, GeofenceModelTest.DELTA);
        Assert.assertEquals(model.getLongitude(), GeofenceModelTest.LONGITUDE, GeofenceModelTest.DELTA);
        Assert.assertEquals(model.getRadius(), GeofenceModelTest.RADIUS, GeofenceModelTest.DELTA);
        Assert.assertEquals(model.getTransition(), GeofenceModelTest.TRANSITION);
        Assert.assertEquals(model.getLoiteringDelay(), GeofenceModelTest.LOITERING_DELAY);
    }

    @Test
    public void test_geofence_model_to_geofence() {
        final GeofenceModel model = new GeofenceModel.Builder(GeofenceModelTest.GEOFENCE_ID).setExpiration(GeofenceModelTest.EXPIRATION).setLatitude(GeofenceModelTest.LATITUDE).setLongitude(GeofenceModelTest.LONGITUDE).setRadius(GeofenceModelTest.RADIUS).setLoiteringDelay(GeofenceModelTest.LOITERING_DELAY).setTransition(GeofenceModelTest.TRANSITION).build();
        Geofence geofence = model.toGeofence();
        Assert.assertNotNull(geofence);
        Assert.assertEquals(geofence.getRequestId(), GeofenceModelTest.GEOFENCE_ID);
    }
}

