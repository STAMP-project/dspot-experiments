/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import org.apache.commons.io.IOUtils;
import org.geoserver.wfs.StoredQueryProvider;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Tests that namespaces are correctly handled by WFS and app-schema when features belonging to
 * different namespaces are chained together.
 */
public final class NamespacesWfsTest extends StationsAppSchemaTestSupport {
    private static final String TEST_STORED_QUERY_ID = "NamespacesTestStoredQuery";

    /* Should return the same result as a GetFeature request against the Station feature type */
    private static final String TEST_STORED_QUERY_DEFINITION = ((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<wfs:StoredQueryDescription id='") + (NamespacesWfsTest.TEST_STORED_QUERY_ID)) + "'") + " xmlns:xlink=\"http://www.w3.org/1999/xlink\"") + " xmlns:ows=\"http://www.opengis.net/ows/1.1\"") + " xmlns:gml=\"${GML_NAMESPACE}\"") + " xmlns:wfs=\"http://www.opengis.net/wfs/2.0\"") + " xmlns:fes=\"http://www.opengis.net/fes/2.0\">>\n") + "  <wfs:QueryExpressionText\n") + "   returnFeatureTypes=\'st_${GML_PREFIX}:Station_${GML_PREFIX}\'\n") + "   language=\'urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression\'\n") + "   isPrivate=\'false\'>\n") + "    <wfs:Query typeNames=\'st_${GML_PREFIX}:Station_${GML_PREFIX}\'>\n") + "      <fes:Filter>\n") + "        <fes:PropertyIsEqualTo>\n") + "          <fes:ValueReference>st_${GML_PREFIX}:measurements/ms_${GML_PREFIX}:Measurement_${GML_PREFIX}/ms_${GML_PREFIX}:name</fes:ValueReference>\n") + "          <fes:Literal>wind</fes:Literal>\n") + "        </fes:PropertyIsEqualTo>\n") + "      </fes:Filter>\n") + "    </wfs:Query>\n") + "  </wfs:QueryExpressionText>\n") + "</wfs:StoredQueryDescription>";

    @Test
    public void globalServiceGetFeatureNamespacesWfs11() {
        Document document = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=st_gml31:Station_gml31");
        checkWfs11StationsGetFeatureResult(document);
    }

    @Test
    public void virtualServiceGetFeatureNamespacesWfs11() {
        Document document = getAsDOM("st_gml31/wfs?request=GetFeature&version=1.1.0&typename=st_gml31:Station_gml31");
        checkWfs11StationsGetFeatureResult(document);
    }

    @Test
    public void globalServiceGetFeatureNamespacesWfs20() {
        Document document = getAsDOM("wfs?request=GetFeature&version=2.0&typename=st_gml32:Station_gml32");
        checkWfs20StationsGetFeatureResult(document);
    }

    @Test
    public void virtualServiceGetFeatureNamespacesWfs20() {
        Document document = getAsDOM("st_gml32/wfs?request=GetFeature&version=2.0&typename=st_gml32:Station_gml32");
        checkWfs20StationsGetFeatureResult(document);
    }

    /**
     * * GetPropertyValue tests **
     */
    @Test
    public void globalServiceGetPropertyValueNamespacesGml32() {
        Document document = getAsDOM("wfs?request=GetPropertyValue&version=2.0&typename=st_gml32:Station_gml32&valueReference=st_gml32:measurements");
        checkGml32StationsGetPropertyValueResult(document);
    }

    @Test
    public void virtualServiceGetPropertyValueNamespacesGml32() {
        Document document = getAsDOM("st_gml32/wfs?request=GetPropertyValue&version=2.0&typename=st_gml32:Station_gml32&valueReference=st_gml32:measurements");
        checkGml32StationsGetPropertyValueResult(document);
    }

    /**
     * * StoredQuery tests **
     */
    @Test
    public void globalServiceStoredQueryNamespacesGml32() throws Exception {
        StoredQueryProvider storedQueryProvider = new StoredQueryProvider(getCatalog());
        try {
            createTestStoredQuery(storedQueryProvider, GML32_PARAMETERS);
            Document document = getAsDOM(("wfs?request=GetFeature&version=2.0&StoredQueryID=" + (NamespacesWfsTest.TEST_STORED_QUERY_ID)));
            checkWfs20StationsGetFeatureResult(document);
        } finally {
            storedQueryProvider.removeAll();
            Assert.assertTrue(((storedQueryProvider.listStoredQueries().size()) == 1));
        }
    }

    @Test
    public void virtualServiceStoredQueryNamespacesGml32() throws Exception {
        StoredQueryProvider storedQueryProvider = new StoredQueryProvider(getCatalog());
        try {
            createTestStoredQuery(storedQueryProvider, GML32_PARAMETERS);
            Document document = getAsDOM(("st_gml32/wfs?request=GetFeature&version=2.0&StoredQueryID=" + (NamespacesWfsTest.TEST_STORED_QUERY_ID)));
            checkWfs20StationsGetFeatureResult(document);
        } finally {
            storedQueryProvider.removeAll();
            Assert.assertTrue(((storedQueryProvider.listStoredQueries().size()) == 1));
        }
    }

    @Test
    public void globalServiceStoredQueryNamespacesGml31() throws Exception {
        StoredQueryProvider storedQueryProvider = new StoredQueryProvider(getCatalog());
        try {
            createTestStoredQuery(storedQueryProvider, GML31_PARAMETERS);
            Document document = getAsDOM(("wfs?request=GetFeature&version=2.0&outputFormat=gml3&StoredQueryID=" + (NamespacesWfsTest.TEST_STORED_QUERY_ID)));
            checkWfs11StationsGetFeatureResult(document);
        } finally {
            storedQueryProvider.removeAll();
            Assert.assertTrue(((storedQueryProvider.listStoredQueries().size()) == 1));
        }
    }

    @Test
    public void virtualServiceStoredQueryNamespacesGml31() throws Exception {
        StoredQueryProvider storedQueryProvider = new StoredQueryProvider(getCatalog());
        try {
            createTestStoredQuery(storedQueryProvider, GML31_PARAMETERS);
            Document document = getAsDOM(("st_gml31/wfs?request=GetFeature&version=2.0&outputFormat=gml3&StoredQueryID=" + (NamespacesWfsTest.TEST_STORED_QUERY_ID)));
            checkWfs11StationsGetFeatureResult(document);
        } finally {
            storedQueryProvider.removeAll();
            Assert.assertTrue(((storedQueryProvider.listStoredQueries().size()) == 1));
        }
    }

    /**
     * Test a request with two queries (to different featureTypes and namespaces) Checks null
     * prefixes issue on multiple query WFS 2.0.0 GML 3.2 versions
     */
    @Test
    public void testTwoQueriesNamespacesGml32() throws Exception {
        String wfsQuery = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("test-data/stations/stations_two_queries.xml"));
        Document document = postAsDOM("wfs", wfsQuery);
        checkCount(WFS20_XPATH_ENGINE, document, 1, "//wfs:FeatureCollection/wfs:member/wfs:FeatureCollection/wfs:member/st_gml32:Station_gml32");
        checkCount(WFS20_XPATH_ENGINE, document, 1, ("/wfs:FeatureCollection/wfs:member/wfs:FeatureCollection/wfs:member/" + "ms_gml32:Measurement_gml32"));
        // check prefixes:
        String output = toString(document);
        Assert.assertTrue(((output.indexOf("null:Measurement_gml32")) < 0));
        Assert.assertTrue(((output.indexOf("null:Station_gml32")) < 0));
        Assert.assertTrue(((output.indexOf("ms_gml32:Measurement_gml32")) > (-1)));
        Assert.assertTrue(((output.indexOf("st_gml32:Station_gml32")) > (-1)));
        // check test1 namespace injected:
        Assert.assertTrue(((output.indexOf("xmlns:test1=\"http://www.test1.org/test1\"")) >= 0));
    }

    /**
     * Test a request with two queries (to different featureTypes and namespaces) Checks null
     * prefixes issue on multiple query WFS 1.1.0 GML 3.1 version
     */
    @Test
    public void testTwoQueriesNamespacesGml31() throws Exception {
        String wfsQuery = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("test-data/stations/stations_two_queries_1.1.xml"));
        Document document = postAsDOM("wfs", wfsQuery);
        String output = toString(document);
        checkCount(WFS11_XPATH_ENGINE, document, 1, "//wfs:FeatureCollection/gml:featureMember/st_gml31:Station_gml31");
        checkCount(WFS11_XPATH_ENGINE, document, 1, ("/wfs:FeatureCollection/gml:featureMember/" + "ms_gml31:Measurement_gml31"));
        // check prefixes:
        Assert.assertTrue(((output.indexOf("null:Measurement_gml31")) < 0));
        Assert.assertTrue(((output.indexOf("null:Station_gml31")) < 0));
        Assert.assertTrue(((output.indexOf("ms_gml31:Measurement_gml31")) > (-1)));
        Assert.assertTrue(((output.indexOf("st_gml31:Station_gml31")) > (-1)));
        // check test1 namespace injected:
        Assert.assertTrue(((output.indexOf("xmlns:test1=\"http://www.test1.org/test1\"")) >= 0));
    }
}

