/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.vector;


import MapBoxTileBuilderFactory.LEGACY_MIME_TYPE;
import MapBoxTileBuilderFactory.MIME_TYPE;
import com.jayway.jsonpath.DocumentContext;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class VectorTilesIntegrationTest extends WMSTestSupport {
    @Test
    public void testSimple() throws Exception {
        String request = (("wms?service=WMS&version=1.1.0&request=GetMap&layers=" + (getLayerId(MockData.ROAD_SEGMENTS))) + "&styles=&bbox=-1,-1,1,1&width=768&height=330&srs=EPSG:4326") + "&format=application%2Fjson%3Btype%3Dgeojson";
        DocumentContext json = getAsJSONPath(request, 200);
        // all features returned, with a geometry and a name attribute
        Assert.assertEquals(5, size());
        Assert.assertEquals(5, size());
        Assert.assertEquals(3, size());
        Assert.assertEquals(1, size());
        Assert.assertEquals(1, size());
    }

    @Test
    public void testSimpleMVT() throws Exception {
        checkSimpleMVT(MIME_TYPE);
    }

    @Test
    public void testSimpleMVTLegacyMime() throws Exception {
        checkSimpleMVT(LEGACY_MIME_TYPE);
    }

    @Test
    public void testCqlFilter() throws Exception {
        String request = (("wms?service=WMS&version=1.1.0&request=GetMap&layers=" + (getLayerId(MockData.ROAD_SEGMENTS))) + "&styles=&bbox=-1,-1,1,1&width=768&height=330&srs=EPSG:4326") + "&CQL_FILTER=NAME='Main Street'&format=application%2Fjson%3Btype%3Dgeojson";
        DocumentContext json = getAsJSONPath(request, 200);
        // all features returned, with a geometry and a name attribute
        Assert.assertEquals(1, size());
        Assert.assertEquals(1, size());
        Assert.assertEquals(0, size());
        Assert.assertEquals(1, size());
        Assert.assertEquals(0, size());
    }

    @Test
    public void testFilterById() throws Exception {
        String request = (("wms?service=WMS&version=1.1.0&request=GetMap&layers=" + (getLayerId(MockData.ROAD_SEGMENTS))) + "&styles=&bbox=-1,-1,1,1&width=768&height=330&srs=EPSG:4326") + "&featureId=RoadSegments.1107532045091&format=application%2Fjson%3Btype%3Dgeojson";
        DocumentContext json = getAsJSONPath(request, 200);
        // all features returned, with a geometry and a name attribute
        Assert.assertEquals(1, size());
        Assert.assertEquals(1, size());
        Assert.assertEquals(0, size());
        Assert.assertEquals(0, size());
        Assert.assertEquals(1, size());
    }
}

