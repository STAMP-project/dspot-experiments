/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.nsg.versioning;


import java.util.Date;
import java.util.List;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.nsg.TestsUtils;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.util.Converters;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.w3c.dom.Document;


public final class TimeVersioningTest extends GeoServerSystemTestSupport {
    private XpathEngine WFS20_XPATH_ENGINE;

    @Test
    public void testInsertVersionedFeature() throws Exception {
        Document doc = postAsDOM("wfs", TestsUtils.readResource("/requests/insert_request_1.xml"));
        assertTransactionResponse(doc);
        List<SimpleFeature> features = TestsUtils.searchFeatures(getCatalog(), "gs:versioned");
        List<SimpleFeature> foundFeatures = TestsUtils.searchFeatures(features, "NAME", "TIME", "Feature_3", new Date(), 300);
        Assert.assertThat(foundFeatures.size(), Matchers.is(1));
        SimpleFeature foundFeature = foundFeatures.get(0);
        String description = ((String) (foundFeature.getAttribute("DESCRIPTION")));
        Assert.assertThat(description, Matchers.is("INSERT_1"));
    }

    @Test
    public void testGetFeatureVersionedEarlyDate() throws Exception {
        String earlyDateRequest = TestsUtils.readResource("/requests/get_request_1.xml").replace("${startDate}", "2017-01-01T12:00:00");
        Document doc = postAsDOM("wfs", earlyDateRequest);
        // print(doc);
        // all three states of the feature
        Assert.assertEquals("3", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned)", doc));
        // sorted by time, descending
        Assert.assertEquals("v.2", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[1]/@gml:id", doc));
        Assert.assertEquals("v.3", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[2]/@gml:id", doc));
        Assert.assertEquals("v.1", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[3]/@gml:id", doc));
    }

    @Test
    public void testGetFeatureVersionedLateDate() throws Exception {
        String earlyDateRequest = TestsUtils.readResource("/requests/get_request_1.xml").replace("${startDate}", "2017-07-24T00:00:00Z");
        Document doc = postAsDOM("wfs", earlyDateRequest);
        // print(doc);
        // only the last states of the feature
        Assert.assertEquals("1", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned)", doc));
        // sorted by time, descending
        Assert.assertEquals("v.2", WFS20_XPATH_ENGINE.evaluate("//gs:versioned/@gml:id", doc));
    }

    @Test
    public void testGetFeatureVersionedExtraFilter() throws Exception {
        String request = TestsUtils.readResource("/requests/get_request_2.xml").replace("${startDate}", "2017-01-01T00:00:00Z");
        Document doc = postAsDOM("wfs", request);
        // print(doc);
        // only one matches
        Assert.assertEquals("1", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned)", doc));
        Assert.assertEquals("v.3", WFS20_XPATH_ENGINE.evaluate("//gs:versioned/@gml:id", doc));
    }

    @Test
    public void testUpdateSingleVersionedFeature() throws Exception {
        Document updateDoc = postAsDOM("wfs", TestsUtils.readResource("/requests/update_request_1.xml"));
        // print(insertDoc);
        // TODO: check it returns an update statement, not a insert like it now does
        assertTransactionResponse(updateDoc);
        String getRequest = TestsUtils.readResource("/requests/get_request_3.xml").replace("${startDate}", "2017-01-01T00:00:00");
        Document getDoc = postAsDOM("wfs", getRequest);
        // print(getDoc);
        // one more state for the feature, there was only two
        Assert.assertEquals("3", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned)", getDoc));
        // sorted by time, the update created this new instance and it should have the old value but
        // the new time
        // and the new
        Assert.assertEquals("Feature_2", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[1]/gs:NAME", getDoc));
        Assert.assertEquals("-2 2", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[1]/gs:GEOMETRY/gml:Point/gml:pos", getDoc));
        Assert.assertEquals("UPDATE_NOW", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[1]/gs:DESCRIPTION", getDoc));
        String time = WFS20_XPATH_ENGINE.evaluate("(//gs:versioned)[1]/gs:TIME", getDoc);
        Date updateTime = Converters.convert(time, Date.class);
        Assert.assertThat(((System.currentTimeMillis()) - (updateTime.getTime())), Matchers.lessThan((300 * 1000L)));
    }

    @Test
    public void testUpdateMultipleVersionedFeature() throws Exception {
        Document updateDoc = postAsDOM("wfs", TestsUtils.readResource("/requests/update_request_2.xml"));
        // print(insertDoc);
        assertTransactionResponse(updateDoc);
        // TODO: check it returns an update statement, not a insert like it now does
        String getRequest = TestsUtils.readResource("/requests/get_request_4.xml");
        Document getDoc = postAsDOM("wfs", getRequest);
        // print(getDoc);
        // one more state for each feature, the property file has 5
        Assert.assertEquals("7", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned)", getDoc));
        // sorted by time, the update created this new instance and it should have the old value but
        // the new time
        // and the new
        Assert.assertEquals("UPDATE_NOW", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned[gs:NAME='Feature_1'])[1]/gs:DESCRIPTION", getDoc));
        Assert.assertEquals("-1 -1", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned[gs:NAME='Feature_1'])[1]/gs:GEOMETRY/gml:Point/gml:pos", getDoc));
        Assert.assertEquals("UPDATE_NOW", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned[gs:NAME='Feature_2'])[1]/gs:DESCRIPTION", getDoc));
        Assert.assertEquals("-2 2", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned[gs:NAME='Feature_2'])[1]/gs:GEOMETRY/gml:Point/gml:pos", getDoc));
        String time = WFS20_XPATH_ENGINE.evaluate("(//gs:versioned[gs:NAME='Feature_1'])[1]/gs:TIME", getDoc);
        Date updateTime = Converters.convert(time, Date.class);
        Assert.assertThat(((System.currentTimeMillis()) - (updateTime.getTime())), Matchers.lessThan((300 * 1000L)));
        Assert.assertEquals("UPDATE_NOW", WFS20_XPATH_ENGINE.evaluate("(//gs:versioned[gs:NAME='Feature_2'])[1]/gs:DESCRIPTION", getDoc));
    }

    @Test
    public void testDeleteFeature2() throws Exception {
        // wipe out entire feature
        String deleteRequest = TestsUtils.readResource("/requests/delete_request_1.xml");
        Document deleteResponse = postAsDOM("wfs", deleteRequest);
        // print(deleteResponse);
        assertTransactionResponse(deleteResponse);
        // read back and make sure there is none left
        String getRequest = TestsUtils.readResource("/requests/get_request_4.xml");
        Document getDoc = postAsDOM("wfs", getRequest);
        // only Feature_1 left
        Assert.assertEquals("3", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned)", getDoc));
        Assert.assertEquals("3", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned[gs:NAME='Feature_1'])", getDoc));
    }

    @Test
    public void testDeleteBetweenDates() throws Exception {
        // wipe out entire feature
        String deleteRequest = TestsUtils.readResource("/requests/delete_request_2.xml");
        Document deleteResponse = postAsDOM("wfs", deleteRequest);
        // print(deleteResponse);
        assertTransactionResponse(deleteResponse);
        // read back and make sure there is none left
        String getRequest = TestsUtils.readResource("/requests/get_request_4.xml");
        Document getDoc = postAsDOM("wfs", getRequest);
        // Feature_1 fully left, Feature_2 only has one
        Assert.assertEquals("3", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned[gs:NAME='Feature_1'])", getDoc));
        Assert.assertEquals("1", WFS20_XPATH_ENGINE.evaluate("count(//gs:versioned[gs:NAME='Feature_2'])", getDoc));
        Assert.assertEquals("UPDATE_2", WFS20_XPATH_ENGINE.evaluate("//gs:versioned[gs:NAME='Feature_2']/gs:DESCRIPTION", getDoc));
    }
}

