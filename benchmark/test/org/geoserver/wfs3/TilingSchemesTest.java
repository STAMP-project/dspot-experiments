/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs3;


import MockData.ROAD_SEGMENTS;
import com.jayway.jsonpath.DocumentContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class TilingSchemesTest extends WFS3TestSupport {
    private Locale originalLocale;

    /**
     * Tests the "wfs3/tilingScheme" json response
     */
    @Test
    public void testTilingSchemesResponse() throws Exception {
        DocumentContext jsonDoc = getAsJSONPath("wfs3/tilingSchemes", 200);
        String scheme1 = jsonDoc.read("$.tilingSchemes[0]", String.class);
        String scheme2 = jsonDoc.read("$.tilingSchemes[1]", String.class);
        HashSet<String> schemesSet = new HashSet<>(Arrays.asList(scheme1, scheme2));
        Assert.assertTrue(schemesSet.contains("GlobalCRS84Geometric"));
        Assert.assertTrue(schemesSet.contains("GoogleMapsCompatible"));
        Assert.assertTrue(((schemesSet.size()) == 2));
    }

    @Test
    public void testTilingSchemeDescriptionGoogleMapsCompatible() throws Exception {
        DocumentContext jsonDoc = getAsJSONPath("wfs3/tilingSchemes/GoogleMapsCompatible", 200);
        checkTilingSchemeData(jsonDoc, "http://www.opengis.net/def/crs/EPSG/0/3857", new Double[]{ -2.003750834E7, -2.003750834E7 }, "http://www.opengis.net/def/wkss/OGC/1.0/GoogleMapsCompatible", 5.590822639508929E8, 0.001, 1073741824, "tileMatrix[30].matrixWidth");
    }

    @Test
    public void testTilingSchemeDescriptionGoogleMapsCompatibleOnCollections() throws Exception {
        String roadSegments = getEncodedName(ROAD_SEGMENTS);
        DocumentContext jsonDoc = getAsJSONPath((("wfs3/collections/" + roadSegments) + "/tiles/GoogleMapsCompatible"), 200);
        checkTilingSchemeData(jsonDoc, "http://www.opengis.net/def/crs/EPSG/0/3857", new Double[]{ -2.003750834E7, -2.003750834E7 }, "http://www.opengis.net/def/wkss/OGC/1.0/GoogleMapsCompatible", 5.590822639508929E8, 0.001, 1073741824, "tileMatrix[30].matrixWidth");
    }

    @Test
    public void testTilingSchemeDescriptionGlobalCRS84Geometric() throws Exception {
        DocumentContext jsonDoc = getAsJSONPath("wfs3/tilingSchemes/GlobalCRS84Geometric", 200);
        checkTilingSchemeData(jsonDoc, "http://www.opengis.net/def/crs/EPSG/0/4326", new Double[]{ -180.0, -90.0 }, "http://www.opengis.net/def/wkss/OGC/1.0/GlobalCRS84Geometric", 2.795411320143589E8, 1.0E-7, 4194304, "tileMatrix[21].matrixWidth");
        Assert.assertEquals(90.0, jsonDoc.read("tileMatrix[21].topLeftCorner[0]", Double.class), 0.001);
        Assert.assertEquals((-180.0), jsonDoc.read("tileMatrix[21].topLeftCorner[1]", Double.class), 0.001);
    }

    @Test
    public void testTilingSchemeDescriptionError() throws Exception {
        DocumentContext jsonDoc = getAsJSONPath("wfs3/tilingSchemes/errorNameX", 500);
        Assert.assertEquals("Invalid gridset name errorNameX", jsonDoc.read("description", String.class));
    }
}

