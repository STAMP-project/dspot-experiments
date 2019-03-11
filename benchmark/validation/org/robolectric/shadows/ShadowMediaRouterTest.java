package org.robolectric.shadows;


import RouteInfo.DEVICE_TYPE_BLUETOOTH;
import ShadowMediaRouter.BLUETOOTH_DEVICE_NAME;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ShadowMediaRouter}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowMediaRouterTest {
    private MediaRouter mediaRouter;

    @Test
    public void testAddBluetoothRoute_additionalRouteAvailable() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        assertThat(mediaRouter.getRouteCount()).isEqualTo(2);
    }

    @Test
    public void testAddBluetoothRoute_bluetoothRouteSelected() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        assertThat(mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isEqualTo(bluetoothRoute);
    }

    @Test
    public void testAddBluetoothRoute_checkBluetoothRouteProperties() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        assertThat(bluetoothRoute.getName()).isEqualTo(BLUETOOTH_DEVICE_NAME);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void testAddBluetoothRoute_checkBluetoothRouteProperties_apiJbMr2() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        assertThat(bluetoothRoute.getDescription()).isEqualTo("Bluetooth audio");
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void testAddBluetoothRoute_checkBluetoothRouteProperties_apiN() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        assertThat(bluetoothRoute.getDeviceType()).isEqualTo(DEVICE_TYPE_BLUETOOTH);
    }

    @Test
    public void testSelectBluetoothRoute_getsSetAsSelectedRoute() {
        // Although this isn't something faked out by the shadow we should ensure that the Bluetooth
        // route can be selected after it's been added.
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        mediaRouter.selectRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO, bluetoothRoute);
        assertThat(mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isEqualTo(bluetoothRoute);
    }

    @Test
    public void testRemoveBluetoothRoute_whenBluetoothSelected_defaultRouteAvailableAndSelected() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        Shadows.shadowOf(mediaRouter).removeBluetoothRoute();
        assertThat(mediaRouter.getRouteCount()).isEqualTo(1);
        assertThat(mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isEqualTo(getDefaultRoute());
    }

    @Test
    public void testRemoveBluetoothRoute_whenDefaultSelected_defaultRouteAvailableAndSelected() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        mediaRouter.selectRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO, bluetoothRoute);
        Shadows.shadowOf(mediaRouter).removeBluetoothRoute();
        assertThat(mediaRouter.getRouteCount()).isEqualTo(1);
        assertThat(mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isEqualTo(getDefaultRoute());
    }

    @Test
    public void testIsBluetoothRouteSelected_bluetoothRouteNotAdded_returnsFalse() {
        assertThat(Shadows.shadowOf(mediaRouter).isBluetoothRouteSelected(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isFalse();
    }

    // Pre-API 18, non-user routes weren't able to be selected.
    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void testIsBluetoothRouteSelected_bluetoothRouteAddedButNotSelected_returnsFalse() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        mediaRouter.selectRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO, getDefaultRoute());
        assertThat(Shadows.shadowOf(mediaRouter).isBluetoothRouteSelected(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testIsBluetoothRouteSelected_bluetoothRouteSelectedForDifferentType_returnsFalse() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        // Select the Bluetooth route for AUDIO and the default route for AUDIO.
        mediaRouter.selectRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO, bluetoothRoute);
        mediaRouter.selectRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, getDefaultRoute());
        assertThat(Shadows.shadowOf(mediaRouter).isBluetoothRouteSelected(MediaRouter.ROUTE_TYPE_LIVE_VIDEO)).isFalse();
    }

    @Test
    public void testIsBluetoothRouteSelected_bluetoothRouteSelected_returnsTrue() {
        Shadows.shadowOf(mediaRouter).addBluetoothRoute();
        RouteInfo bluetoothRoute = mediaRouter.getRouteAt(1);
        mediaRouter.selectRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO, bluetoothRoute);
        assertThat(Shadows.shadowOf(mediaRouter).isBluetoothRouteSelected(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)).isTrue();
    }
}

