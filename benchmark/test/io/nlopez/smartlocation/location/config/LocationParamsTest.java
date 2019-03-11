package io.nlopez.smartlocation.location.config;


import io.nlopez.smartlocation.CustomTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static LocationAccuracy.HIGH;


/**
 * Created by mrm on 10/1/15.
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class LocationParamsTest {
    private static final LocationAccuracy ACCURACY = HIGH;

    private static final long INTERVAL = 1000;

    private static final float DISTANCE = 1000.0F;

    private static final double DELTA = 1.0E-7;

    @Test
    public void test_location_params_builder() {
        LocationParams locationParams = new LocationParams.Builder().setAccuracy(LocationParamsTest.ACCURACY).setInterval(LocationParamsTest.INTERVAL).setDistance(LocationParamsTest.DISTANCE).build();
        Assert.assertEquals(locationParams.getAccuracy(), LocationParamsTest.ACCURACY);
        Assert.assertEquals(locationParams.getDistance(), LocationParamsTest.DISTANCE, LocationParamsTest.DELTA);
        Assert.assertEquals(locationParams.getInterval(), LocationParamsTest.INTERVAL);
    }
}

