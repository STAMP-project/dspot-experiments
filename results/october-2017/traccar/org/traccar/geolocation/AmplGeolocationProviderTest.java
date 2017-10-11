package org.traccar.geolocation;


public class AmplGeolocationProviderTest extends org.traccar.BaseTest {
    private boolean enable = false;

    @org.junit.Test
    public void test() throws java.lang.Exception {
        if (enable) {
            testLocationProvider();
        }
    }

    public void testLocationProvider() throws java.lang.Exception {
        org.traccar.geolocation.MozillaGeolocationProvider provider = new org.traccar.geolocation.MozillaGeolocationProvider();
        org.traccar.model.Network network = new org.traccar.model.Network(org.traccar.model.CellTower.from(208, 1, 2, 1234567));
        provider.getLocation(network, new org.traccar.geolocation.GeolocationProvider.LocationProviderCallback() {
            @java.lang.Override
            public void onSuccess(double latitude, double longitude, double accuracy) {
                org.junit.Assert.assertEquals(60.07254, latitude, 1.0E-5);
                org.junit.Assert.assertEquals(30.30996, longitude, 1.0E-5);
            }

            @java.lang.Override
            public void onFailure(java.lang.Throwable e) {
                org.junit.Assert.fail();
            }
        });
        java.lang.Thread.sleep(java.lang.Long.MAX_VALUE);
    }
}

