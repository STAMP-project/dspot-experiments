package org.opentripplanner.geocoder;


import GeocoderNullImpl.ERROR_MSG;
import org.junit.Assert;
import org.junit.Test;


public class GeocoderNullImplTest {
    @Test
    public void testGeocode() {
        Geocoder nullGeocoder = new GeocoderNullImpl();
        GeocoderResults result = nullGeocoder.geocode("121 elm street", null);
        Assert.assertEquals("stub response", ERROR_MSG, result.getError());
    }
}

