/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import java.util.Collections;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Validates that reprojection and axis flipping are correctly handled.
 */
public final class ReprojectionAxisFlipTest extends AbstractAppSchemaTestSupport {
    private static final String STATIONS_PREFIX = "st";

    private static final String STATIONS_URI = "http://www.stations.org/1.0";

    /**
     * Helper class that will setup custom complex feature types using the stations data set.
     */
    private static final class MockData extends StationsMockData {
        @Override
        public void addContent() {
            // add stations namespaces
            putNamespace(ReprojectionAxisFlipTest.STATIONS_PREFIX, ReprojectionAxisFlipTest.STATIONS_URI);
            // add stations feature type
            addAppSchemaFeatureType(ReprojectionAxisFlipTest.STATIONS_PREFIX, null, "Station", "/test-data/stations/noDefaultGeometry/stations.xml", Collections.emptyMap(), "/test-data/stations/noDefaultGeometry/stations.xsd", "/test-data/stations/noDefaultGeometry/stations.properties");
        }
    }

    @Test
    public void testWfsGetFeatureWithBbox() throws Exception {
        genericWfsGetFeatureWithBboxTest(() -> getAsServletResponse(("wfs?service=WFS" + ("&version=2.0&request=GetFeature&typeName=st:Station&maxFeatures=1" + "&outputFormat=gml32&srsName=urn:ogc:def:crs:EPSG::4052&bbox=3,-3,6,0"))));
    }

    @Test
    public void testWfsGetFeatureWithBboxPost() throws Exception {
        // execute the WFS 2.0 request
        genericWfsGetFeatureWithBboxTest(() -> postAsServletResponse("wfs", readResource("/test-data/stations/noDefaultGeometry/requests/wfs20_get_feature_1.xml")));
    }

    @FunctionalInterface
    private interface Request {
        // executes a request allowing an exception to be throw
        MockHttpServletResponse execute() throws Exception;
    }
}

