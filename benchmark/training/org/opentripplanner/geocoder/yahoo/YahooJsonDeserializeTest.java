package org.opentripplanner.geocoder.yahoo;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class YahooJsonDeserializeTest {
    @Test
    public void testDeserialize() throws Exception {
        String json = getJsonString();
        YahooJsonDeserializer yahooJsonDeserializer = new YahooJsonDeserializer();
        YahooGeocoderResults geocoderResults = yahooJsonDeserializer.parseResults(json);
        YahooGeocoderResultSet resultSet = geocoderResults.getResultSet();
        Assert.assertNotNull("didn't parse yahoo results correctly", resultSet);
        List<YahooGeocoderResult> results = resultSet.getResults();
        Assert.assertNotNull("didn't parse yahoo results correctly", results);
        Assert.assertEquals("unexpected number of results", 1, results.size());
        YahooGeocoderResult yahooGeocoderResult = results.get(0);
        // verify geometry
        double lat = yahooGeocoderResult.getLatDouble();
        double lng = yahooGeocoderResult.getLngDouble();
        Assert.assertEquals(37.77916, lat, 1.0E-5);
        Assert.assertEquals((-122.420049), lng, 1.0E-5);
        // verify address lines
        String line1 = yahooGeocoderResult.getLine1();
        String line2 = yahooGeocoderResult.getLine2();
        Assert.assertEquals("first yahoo address line wrong", "", line1);
        Assert.assertEquals("second yahoo address line wrong", "San Francisco, CA", line2);
    }
}

