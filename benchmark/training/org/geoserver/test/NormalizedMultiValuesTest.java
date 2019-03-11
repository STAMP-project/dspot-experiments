/**
 * GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2018, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geoserver.test;


import java.util.HashMap;
import java.util.Map;
import org.custommonkey.xmlunit.XpathEngine;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Contains tests related with JDBC multiple values support.
 */
public final class NormalizedMultiValuesTest extends AbstractAppSchemaTestSupport {
    // xpath engines used to check WFS responses
    private XpathEngine WFS11_XPATH_ENGINE;

    private XpathEngine WFS20_XPATH_ENGINE;

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
            Map<String, String> gml31Parameters = new HashMap<>();
            gml31Parameters.put("GML_PREFIX", "gml31");
            gml31Parameters.put("GML_PREFIX_UPPER", "GML31");
            gml31Parameters.put("GML_NAMESPACE", "http://www.opengis.net/gml");
            gml31Parameters.put("GML_LOCATION", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
            addAppSchemaFeatureType(StationsMockData.STATIONS_PREFIX_GML31, "gml31", "Station_gml31", "/test-data/stations/multiValues/stations.xml", gml31Parameters, "/test-data/stations/multiValues/stations.xsd", "/test-data/stations/multiValues/stations.properties", "/test-data/stations/multiValues/measurements.xml", "/test-data/stations/multiValues/measurements.xsd", "/test-data/stations/multiValues/measurements.properties", "/test-data/stations/multiValues/tags.properties");
            // add GML 3.2 feature types
            Map<String, String> gml32Parameters = new HashMap<>();
            gml32Parameters.put("GML_PREFIX", "gml32");
            gml32Parameters.put("GML_PREFIX_UPPER", "GML32");
            gml32Parameters.put("GML_NAMESPACE", "http://www.opengis.net/gml/3.2");
            gml32Parameters.put("GML_LOCATION", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");
            addAppSchemaFeatureType(StationsMockData.STATIONS_PREFIX_GML32, "gml32", "Station_gml32", "/test-data/stations/multiValues/stations.xml", gml32Parameters, "/test-data/stations/multiValues/stations.xsd", "/test-data/stations/multiValues/stations.properties", "/test-data/stations/multiValues/measurements.xml", "/test-data/stations/multiValues/measurements.xsd", "/test-data/stations/multiValues/measurements.properties", "/test-data/stations/multiValues/tags.properties");
        }
    }

    @Test
    public void testGetAllNormalizedMultiValuesWfs11() throws Exception {
        // check if this is an online test with a JDBC based data store
        if (notJdbcBased()) {
            // not a JDBC online test
            return;
        }
        // execute the WFS 1.1.0 request
        String request = "wfs?request=GetFeature&version=1.1.0&typename=st_gml31:Station_gml31";
        Document document = getAsDOM(request);
        // check that we have two complex features
        checkCount(WFS11_XPATH_ENGINE, document, 2, "/wfs:FeatureCollection/gml:featureMember/st_gml31:Station_gml31");
        // check that the expected stations and measurements are present
        checkStation1Gml31(document);
        checkStation2Gml31(document);
    }

    @Test
    public void testGetAllNormalizedMultiValuesWfs20() throws Exception {
        // check if this is an online test with a JDBC based data store
        if (notJdbcBased()) {
            // not a JDBC online test
            return;
        }
        // execute the WFS 2.0 request
        String request = "wfs?request=GetFeature&version=2.0&typename=st_gml32:Station_gml32";
        Document document = getAsDOM(request);
        // check that we have two complex features
        checkCount(WFS20_XPATH_ENGINE, document, 2, "/wfs:FeatureCollection/wfs:member/st_gml32:Station_gml32");
        // check that the expected stations and measurements are present
        checkStation1Gml32(document);
        checkStation2Gml32(document);
    }

    @Test
    public void testGetFilteredNormalizedMultiValuesWfs11() throws Exception {
        // check if this is an online test with a JDBC based data store
        if (notJdbcBased()) {
            // not a JDBC online test
            return;
        }
        // execute the WFS 1.1.0 request
        String request = "wfs";
        Document document = postAsDOM(request, readResource("/test-data/stations/multiValues/requests/station_tag_filter_wfs11_1.xml"));
        // check that we got he correct station
        checkStation1Gml31(document);
    }

    @Test
    public void testGetFilteredNormalizedMultiValuesWfs20() throws Exception {
        // check if this is an online test with a JDBC based data store
        if (notJdbcBased()) {
            // not a JDBC online test
            return;
        }
        // execute the WFS 2.0 request
        String request = "wfs";
        Document document = postAsDOM(request, readResource("/test-data/stations/multiValues/requests/station_tag_filter_wfs20_1.xml"));
        // check that we got he correct station
        checkStation1Gml32(document);
    }
}

