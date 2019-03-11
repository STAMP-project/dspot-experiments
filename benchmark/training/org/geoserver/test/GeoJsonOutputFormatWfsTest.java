/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import net.sf.json.JSON;
import org.junit.Test;


/**
 * Validates JSON output format for complex features.
 */
public final class GeoJsonOutputFormatWfsTest extends AbstractAppSchemaTestSupport {
    /**
     * Helper class that will setup custom complex feature types using the stations data set.
     */
    private static final class MockData extends StationsMockData {
        @Override
        public void addContent() {
            // add GML 3.1 namespaces
            putNamespace(StationsMockData.STATIONS_PREFIX_GML31, StationsMockData.STATIONS_URI_GML31);
            putNamespace(StationsMockData.MEASUREMENTS_PREFIX_GML31, StationsMockData.MEASUREMENTS_URI_GML31);
            // add GML 3.2 namespaces
            putNamespace(StationsMockData.STATIONS_PREFIX_GML32, StationsMockData.STATIONS_URI_GML32);
            putNamespace(StationsMockData.MEASUREMENTS_PREFIX_GML32, StationsMockData.MEASUREMENTS_URI_GML32);
            // add GML 3.1 feature types
            addAppSchemaFeatureType(StationsMockData.STATIONS_PREFIX_GML31, "gml31", "Station_gml31", "/test-data/stations/geoJson/stations.xml", StationsMockData.getGml31StandardParamaters(), "/test-data/stations/geoJson/measurements.xml", "/test-data/stations/geoJson/stations.xsd", "/test-data/stations/geoJson/stations.properties", "/test-data/stations/geoJson/measurements.properties");
            // add GML 3.2 feature types
            addAppSchemaFeatureType(StationsMockData.STATIONS_PREFIX_GML32, "gml32", "Station_gml32", "/test-data/stations/geoJson/stations.xml", StationsMockData.getGml32StandardParamaters(), "/test-data/stations/geoJson/measurements.xml", "/test-data/stations/geoJson/stations.xsd", "/test-data/stations/geoJson/stations.properties", "/test-data/stations/geoJson/measurements.properties");
        }
    }

    @Test
    public void testGetGeoJsonResponseWfs11() throws Exception {
        // execute the WFS 1.1.0 request
        JSON response = getAsJSON(("wfs?request=GetFeature&version=1.1.0" + "&typename=st_gml31:Station_gml31&outputFormat=application/json"));
        // validate the obtained response
        checkStation1Exists(response);
    }

    @Test
    public void testGetGeoJsonResponseWfs20() throws Exception {
        // execute the WFS 2.0 request
        JSON response = getAsJSON(("wfs?request=GetFeature&version=2.0" + "&typenames=st_gml32:Station_gml32&outputFormat=application/json"));
        // validate the obtained response
        checkStation1Exists(response);
    }
}

