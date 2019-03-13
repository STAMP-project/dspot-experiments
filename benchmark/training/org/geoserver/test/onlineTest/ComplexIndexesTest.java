/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test.onlineTest;


import java.io.File;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


public class ComplexIndexesTest extends GeoServerSystemTestSupport {
    // xpath engines used to check WFS responses
    private XpathEngine WFS11_XPATH_ENGINE;

    private XpathEngine WFS20_XPATH_ENGINE;

    private static String solrUrl;

    private static String solrCoreName;

    // HTTP Apache Solr client
    private static HttpSolrClient solrClient;

    private static PostgresqlProperties pgProps;

    // test root directory
    private static final File TESTS_ROOT_DIR = ComplexIndexesTest.createTempDirectory("complex-indexes");

    public static final String STATIONS_NAMESPACE = "http://www.stations.org/1.0";

    public static final String OBSERVATIONS_MAPPING_NAME = "ObservationType-e17fbd44-fd26-46e7-bd71-e2568073c6c5";

    public static final String STATIONS_MAPPING_NAME = "StationType-f46d72da-5591-4873-b210-5ed30a6ffb0d";

    public static final StationsMappingsSetup stationSetup = new StationsMappingsSetup();

    @Test
    public void testPagination() throws Exception {
        // pagination-test-query.xml
        setupXmlUnitNamespaces();
        String wfsQuery = Resources.resourceToString(((Resources.TEST_DATA_DIR) + "/pagination-test-query.xml"));
        Document responseDoc = postAsDOM("wfs", wfsQuery);
        // without pagination-limit we'd get 3 features, test we got only 1:
        checkCount(WFS20_XPATH_ENGINE, responseDoc, 1, "//wfs:FeatureCollection/wfs:member/st:Station");
    }

    @Test
    public void testQueryComplex() throws Exception {
        setupXmlUnitNamespaces();
        String wfsQuery = Resources.resourceToString(((Resources.TEST_DATA_DIR) + "/complex-wildcard-query.xml"));
        Document responseDoc = postAsDOM("wfs", wfsQuery);
        checkCount(WFS20_XPATH_ENGINE, responseDoc, 1, "//wfs:FeatureCollection/wfs:member/st:Station");
        checkCount(WFS20_XPATH_ENGINE, responseDoc, 1, String.format("//wfs:FeatureCollection/wfs:member/st:Station[@gml:id='%s']", "13"));
        // st:stationName = 1_Alessandria
        XMLAssert.assertXpathEvaluatesTo("1_Alessandria", "//wfs:FeatureCollection/wfs:member/st:Station[@gml:id='13']/st:stationName", responseDoc);
        // wfs:FeatureCollection/wfs:member/st:Station/st:observation/st:Observation
        checkCount(WFS20_XPATH_ENGINE, responseDoc, 2, "//wfs:FeatureCollection/wfs:member/st:Station[@gml:id='13']/st:observation/st:Observation");
        XMLAssert.assertXpathEvaluatesTo("wrapper", ("//wfs:FeatureCollection/wfs:member/st:Station[@gml:id='13']/st:observation" + "/st:Observation[@gml:id='1']/st:description"), responseDoc);
    }
}

