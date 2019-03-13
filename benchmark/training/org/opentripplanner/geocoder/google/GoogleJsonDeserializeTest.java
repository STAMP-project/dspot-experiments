package org.opentripplanner.geocoder.google;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GoogleJsonDeserializeTest {
    @Test
    public void testDeserialize() throws Exception {
        String json = getJsonString();
        GoogleJsonDeserializer googleJsonDeserializer = new GoogleJsonDeserializer();
        GoogleGeocoderResults geocoderResults = googleJsonDeserializer.parseResults(json);
        List<GoogleGeocoderResult> results = geocoderResults.getResults();
        Assert.assertEquals("unexpected number of results", 1, results.size());
        GoogleGeocoderResult googleGeocoderResult = results.get(0);
        // verify geometry
        Geometry geometry = googleGeocoderResult.getGeometry();
        Location location = geometry.getLocation();
        double lat = location.getLat();
        double lng = location.getLng();
        Assert.assertEquals(37.421708, lat, 1.0E-5);
        Assert.assertEquals((-122.0829964), lng, 1.0E-5);
        // verify formatted address of response
        String formattedAddress = googleGeocoderResult.getFormatted_address();
        Assert.assertEquals("invalid address", "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA", formattedAddress);
    }
}

