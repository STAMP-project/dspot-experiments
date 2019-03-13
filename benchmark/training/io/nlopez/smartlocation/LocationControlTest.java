package io.nlopez.smartlocation;


import LocationParams.NAVIGATION;
import RuntimeEnvironment.application;
import SmartLocation.LocationControl;
import android.content.Context;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.util.MockLocationProvider;
import io.nlopez.smartlocation.utils.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class LocationControlTest {
    private static final LocationParams DEFAULT_PARAMS = LocationParams.BEST_EFFORT;

    private static final boolean DEFAULT_SINGLE_UPDATE = false;

    private MockLocationProvider mockProvider;

    private OnLocationUpdatedListener locationUpdatedListener;

    @Test
    public void test_location_control_init() {
        Context context = application.getApplicationContext();
        SmartLocation smartLocation = logging(false).preInitialize(false).build();
        SmartLocation.LocationControl locationControl = smartLocation.location(mockProvider);
        Mockito.verifyZeroInteractions(mockProvider);
        smartLocation = logging(false).build();
        locationControl = smartLocation.location(mockProvider);
        Mockito.verify(mockProvider).init(ArgumentMatchers.eq(context), ArgumentMatchers.any(Logger.class));
    }

    @Test
    public void test_location_control_start_defaults() {
        SmartLocation.LocationControl locationControl = createLocationControl();
        locationControl.start(locationUpdatedListener);
        Mockito.verify(mockProvider).start(locationUpdatedListener, LocationControlTest.DEFAULT_PARAMS, LocationControlTest.DEFAULT_SINGLE_UPDATE);
    }

    @Test
    public void test_location_control_start_only_once() {
        SmartLocation.LocationControl locationControl = createLocationControl();
        locationControl.oneFix();
        locationControl.start(locationUpdatedListener);
        Mockito.verify(mockProvider).start(locationUpdatedListener, LocationControlTest.DEFAULT_PARAMS, true);
    }

    @Test
    public void test_location_control_start_continuous() {
        SmartLocation.LocationControl locationControl = createLocationControl();
        locationControl.oneFix();
        locationControl.continuous();
        locationControl.start(locationUpdatedListener);
        Mockito.verify(mockProvider).start(locationUpdatedListener, LocationControlTest.DEFAULT_PARAMS, false);
    }

    @Test
    public void test_location_control_start_navigation() {
        SmartLocation.LocationControl locationControl = createLocationControl();
        locationControl.config(NAVIGATION);
        locationControl.start(locationUpdatedListener);
        Mockito.verify(mockProvider).start(ArgumentMatchers.eq(locationUpdatedListener), ArgumentMatchers.eq(NAVIGATION), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void test_location_control_get_last_location() {
        SmartLocation.LocationControl locationControl = createLocationControl();
        locationControl.getLastLocation();
        Mockito.verify(mockProvider).getLastLocation();
    }

    @Test
    public void test_location_control_stop() {
        SmartLocation.LocationControl locationControl = createLocationControl();
        locationControl.stop();
        Mockito.verify(mockProvider).stop();
    }
}

