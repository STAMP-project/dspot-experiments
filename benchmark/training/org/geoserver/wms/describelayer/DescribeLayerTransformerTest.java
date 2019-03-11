/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.describelayer;


import java.io.StringWriter;
import javax.xml.transform.TransformerException;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.CoverageInfoImpl;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.wms.DescribeLayerRequest;
import org.geoserver.wms.MapLayerInfo;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


/**
 * Unit test suite for {@link DescribeLayerTransformer}
 *
 * @author Gabriel Roldan
 * @version $Id$
 */
public class DescribeLayerTransformerTest {
    /**
     * A request for the tests to fill up with the test spficic parameters. setUp creates it whit a
     * mocked up catalog
     */
    private DescribeLayerRequest request;

    private DescribeLayerTransformer transformer;

    private XpathEngine XPATH;

    private CatalogImpl catalog;

    private FeatureTypeInfoImpl featureTypeInfo;

    private CoverageInfoImpl coverageInfo;

    private LayerInfoImpl vectorLayerInfo;

    private LayerInfoImpl coverageLayerInfo;

    @Test
    public void testPreconditions() throws TransformerException {
        try {
            new DescribeLayerTransformer(null);
            Assert.fail("expected NPE on null base url");
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        transformer = new DescribeLayerTransformer("http://geoserver.org");
        try {
            transformer.transform(null);
            Assert.fail("expected IAE on null request");
        } catch (TransformerException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
        try {
            transformer.transform(new Object());
            fail("expected IAE on argument non a DescribeLayerRequest instance");
        } catch (TransformerException e) {
            assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
    }

    /**
     * Test the root element name and version attribute.
     *
     * <p>This test does not set a requested layer to the request and {@link DescribeLayerTransformer} does not care since checking the mandatory arguments shall be done
     * prior to using the transformer, so it'll return an empty root element in this case.
     */
    @Test
    public void testRootElement() throws Exception {
        transformer = new DescribeLayerTransformer("http://geoserver.org");
        Document dom = WMSTestSupport.transform(request, transformer);
        Element root = dom.getDocumentElement();
        assertEquals("WMS_DescribeLayerResponse", root.getNodeName());
        assertEquals("1.1.1", root.getAttribute("version"));
    }

    @Test
    public void testDTDLocation() throws Exception {
        final String expected = "!DOCTYPE WMS_DescribeLayerResponse SYSTEM \"http://geoserver.org/schemas/wms/1.1.1/WMS_DescribeLayerResponse.dtd\"";
        transformer = new DescribeLayerTransformer("http://geoserver.org");
        StringWriter writer = new StringWriter();
        transformer.transform(request, writer);
        assertTrue(((writer.getBuffer().indexOf(expected)) > 0));
    }

    @Test
    public void testSingleVectorLayer() throws Exception {
        MapLayerInfo mapLayerInfo = new MapLayerInfo(vectorLayerInfo);
        request.addLayer(mapLayerInfo);
        final String serverBaseUrl = "http://geoserver.org";
        transformer = new DescribeLayerTransformer(serverBaseUrl);
        final Document dom = WMSTestSupport.transform(request, transformer);
        final String layerDescPath = "/WMS_DescribeLayerResponse/LayerDescription";
        assertXpathExists(layerDescPath, dom);
        assertXpathEvaluatesTo("fakeWs:states", (layerDescPath + "/@name"), dom);
        final String expectedWfsAtt = serverBaseUrl + "/wfs?";
        assertXpathExists((layerDescPath + "/@wfs"), dom);
        assertXpathEvaluatesTo(expectedWfsAtt, (layerDescPath + "/@wfs"), dom);
        assertXpathExists((layerDescPath + "/@owsURL"), dom);
        assertXpathEvaluatesTo(expectedWfsAtt, (layerDescPath + "/@owsURL"), dom);
        assertXpathExists((layerDescPath + "/@owsType"), dom);
        assertXpathEvaluatesTo("WFS", (layerDescPath + "/@owsType"), dom);
        assertXpathExists((layerDescPath + "/Query"), dom);
        assertXpathEvaluatesTo("fakeWs:states", (layerDescPath + "/Query/@typeName"), dom);
    }

    @Test
    public void testSingleRasterLayer() throws Exception {
        MapLayerInfo mapLayerInfo = new MapLayerInfo(coverageLayerInfo);
        request.addLayer(mapLayerInfo);
        final String serverBaseUrl = "http://geoserver.org";
        transformer = new DescribeLayerTransformer(serverBaseUrl);
        final Document dom = WMSTestSupport.transform(request, transformer);
        final String layerDescPath = "/WMS_DescribeLayerResponse/LayerDescription";
        assertXpathExists(layerDescPath, dom);
        assertXpathEvaluatesTo("fakeWs:fakeCoverage", (layerDescPath + "/@name"), dom);
        // no wfs attribute for a coverage layer
        assertXpathEvaluatesTo("", (layerDescPath + "/@wfs"), dom);
        assertXpathExists((layerDescPath + "/@owsURL"), dom);
        final String expectedOWSURLAtt = serverBaseUrl + "/wcs?";
        assertXpathEvaluatesTo(expectedOWSURLAtt, (layerDescPath + "/@owsURL"), dom);
        assertXpathExists((layerDescPath + "/@owsType"), dom);
        assertXpathEvaluatesTo("WCS", (layerDescPath + "/@owsType"), dom);
        assertXpathExists((layerDescPath + "/Query"), dom);
        assertXpathEvaluatesTo("fakeWs:fakeCoverage", (layerDescPath + "/Query/@typeName"), dom);
    }

    @Test
    public void testMultipleLayers() throws Exception {
        request.addLayer(new MapLayerInfo(vectorLayerInfo));
        request.addLayer(new MapLayerInfo(coverageLayerInfo));
        final String serverBaseUrl = "http://geoserver.org";
        transformer = new DescribeLayerTransformer(serverBaseUrl);
        final Document dom = WMSTestSupport.transform(request, transformer);
        final String layerDescPath1 = "/WMS_DescribeLayerResponse/LayerDescription[1]";
        final String layerDescPath2 = "/WMS_DescribeLayerResponse/LayerDescription[2]";
        assertXpathExists(layerDescPath1, dom);
        assertXpathExists(layerDescPath2, dom);
        assertXpathEvaluatesTo("fakeWs:states", (layerDescPath1 + "/@name"), dom);
        assertXpathEvaluatesTo("fakeWs:fakeCoverage", (layerDescPath2 + "/@name"), dom);
    }
}

