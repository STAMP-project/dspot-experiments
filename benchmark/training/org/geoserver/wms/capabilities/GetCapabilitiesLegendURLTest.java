/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.capabilities;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.impl.GeoServerInfoImpl;
import org.geoserver.data.test.MockData;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wms.GetCapabilitiesRequest;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSInfoImpl;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.xml.transform.TransformerBase;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static LegendSampleImpl.LEGEND_SAMPLES_FOLDER;


/**
 * Base class for legendURL support in GetCapabilities tests.
 *
 * @author Mauro Bartolomeoli (mauro.bartolomeoli at geo-solutions.it)
 */
public abstract class GetCapabilitiesLegendURLTest extends WMSTestSupport {
    /**
     * default base url to feed a GetCapabilitiesTransformer with for it to append the DTD location
     */
    protected static final String baseUrl = "http://localhost/geoserver";

    /**
     * test map formats to feed a GetCapabilitiesTransformer with
     */
    protected static final Set<String> mapFormats = Collections.singleton("image/png");

    /**
     * test legend formats to feed a GetCapabilitiesTransformer with
     */
    protected static final Set<String> legendFormats = Collections.singleton("image/png");

    /**
     * a mocked up {@link GeoServer} config, almost empty after setUp(), except for the {@link WMSInfo}, {@link GeoServerInfo} and empty {@link Catalog}, Specific tests should add content
     * as needed
     */
    protected GeoServerImpl geosConfig;

    /**
     * a mocked up {@link GeoServerInfo} for {@link #geosConfig}. Specific tests should set its
     * properties as needed
     */
    protected GeoServerInfoImpl geosInfo;

    /**
     * a mocked up {@link WMSInfo} for {@link #geosConfig}, empty except for the WMSInfo after
     * setUp(), Specific tests should set its properties as needed
     */
    protected WMSInfoImpl wmsInfo;

    /**
     * a mocked up {@link Catalog} for {@link #geosConfig}, empty after setUp(), Specific tests
     * should add content as needed
     */
    protected Catalog catalog;

    protected GetCapabilitiesRequest req;

    protected WMS wmsConfig;

    protected XpathEngine XPATH;

    /**
     * Test layers
     */
    public static QName SQUARES = new QName(MockData.CITE_URI, "squares", MockData.CITE_PREFIX);

    public static QName STATES = new QName(MockData.CITE_URI, "states", MockData.CITE_PREFIX);

    public static QName WORLD = new QName("http://www.geo-solutions.it", "world", "gs");

    /**
     * Tests that already cached icons are read from disk and used to calculate size.
     */
    @Test
    public void testCachedLegendURLSize() throws Exception {
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList legendURLs = XPATH.getMatchingNodes(getLegendURLXPath("cite:BasicPolygons"), dom);
        Assert.assertEquals(1, legendURLs.getLength());
        Element legendURL = ((Element) (legendURLs.item(0)));
        Assert.assertTrue(legendURL.hasAttribute("width"));
        Assert.assertEquals("50", legendURL.getAttribute("width"));
        Assert.assertTrue(legendURL.hasAttribute("height"));
        Assert.assertEquals("10", legendURL.getAttribute("height"));
    }

    /**
     * Tests that folder for legend samples is created, if missing.
     */
    @Test
    public void testCachedLegendURLFolderCreated() throws Exception {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        File samplesFolder = new File((((loader.getBaseDirectory().getAbsolutePath()) + (File.separator)) + (LEGEND_SAMPLES_FOLDER)));
        removeFileOrFolder(samplesFolder);
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        Assert.assertTrue(samplesFolder.exists());
    }

    /**
     * Tests the layer names are workspace qualified
     */
    @Test
    public void testLayerWorkspaceQualified() throws Exception {
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        // print(dom);
        String legendURL = XPATH.evaluate(((((getLegendURLXPath("cite:squares")) + "/") + (getElementPrefix())) + "OnlineResource/@xlink:href"), dom);
        Map<String, Object> kvp = KvpUtils.parseQueryString(legendURL);
        Assert.assertEquals("cite:squares", kvp.get("layer"));
    }

    /**
     * Tests that not existing icons are created on disk and used to calculate size.
     */
    @Test
    public void testCreatedLegendURLSize() throws Exception {
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList legendURLs = XPATH.getMatchingNodes(getLegendURLXPath("cite:squares"), dom);
        Assert.assertEquals(1, legendURLs.getLength());
        Element legendURL = ((Element) (legendURLs.item(0)));
        Assert.assertTrue(legendURL.hasAttribute("width"));
        Assert.assertFalse("20".equals(legendURL.getAttribute("width")));
        Assert.assertTrue(legendURL.hasAttribute("height"));
        Assert.assertFalse("20".equals(legendURL.getAttribute("height")));
        File sampleFile = getSampleFile("squares");
        Assert.assertTrue(sampleFile.exists());
    }

    @Test
    public void testCreatedRasterLegendURLSize() throws Exception {
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList legendURLs = XPATH.getMatchingNodes(getLegendURLXPath("gs:world"), dom);
        Assert.assertEquals(1, legendURLs.getLength());
        Element legendURL = ((Element) (legendURLs.item(0)));
        Assert.assertTrue(legendURL.hasAttribute("width"));
        Assert.assertFalse("20".equals(legendURL.getAttribute("width")));
        Assert.assertTrue(legendURL.hasAttribute("height"));
        Assert.assertFalse("20".equals(legendURL.getAttribute("height")));
        File sampleFile = getSampleFile("temperature");
        Assert.assertTrue(sampleFile.exists());
    }

    /**
     * Tests that not existing icons for workspace bound styles are created on disk in the workspace
     * styles folder.
     */
    @Test
    public void testCreatedLegendURLFromWorkspaceSize() throws Exception {
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList legendURLs = XPATH.getMatchingNodes(getLegendURLXPath("cite:states"), dom);
        Assert.assertEquals(1, legendURLs.getLength());
        Element legendURL = ((Element) (legendURLs.item(0)));
        Assert.assertTrue(legendURL.hasAttribute("width"));
        Assert.assertFalse("20".equals(legendURL.getAttribute("width")));
        Assert.assertTrue(legendURL.hasAttribute("height"));
        Assert.assertFalse("20".equals(legendURL.getAttribute("height")));
        File sampleFile = getSampleFile("cite_states");
        Assert.assertTrue(sampleFile.exists());
    }

    /**
     * Tests that already cached icons are recreated if related SLD is newer.
     */
    @Test
    public void testCachedLegendURLUpdatedSize() throws Exception {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        Resource sldResource = loader.get(Paths.path("styles", "Bridges.sld"));
        File sampleFile = getSampleFile("Bridges");
        long lastTime = sampleFile.lastModified();
        long lastLength = sampleFile.length();
        long previousTime = sldResource.lastmodified();
        sldResource.file().setLastModified((lastTime + 1000));
        // force cleaning of samples cache, to get updates on files
        reloaded();
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList legendURLs = XPATH.getMatchingNodes(getLegendURLXPath("cite:Bridges"), dom);
        Assert.assertEquals(1, legendURLs.getLength());
        Element legendURL = ((Element) (legendURLs.item(0)));
        Assert.assertTrue(legendURL.hasAttribute("width"));
        Assert.assertEquals("20", legendURL.getAttribute("width"));
        Assert.assertTrue(legendURL.hasAttribute("height"));
        Assert.assertEquals("20", legendURL.getAttribute("height"));
        Assert.assertFalse(((getSampleFile("Bridges").length()) == lastLength));
        sldResource.file().setLastModified(previousTime);
    }

    /**
     * Tests that already cached icons are recreated if related SLD is newer (using Catalog events).
     */
    @Test
    public void testCachedLegendURLUpdatedSize2() throws Exception {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        Resource sldResource = loader.get(Paths.path("styles", "Bridges.sld"));
        File sampleFile = getSampleFile("Bridges");
        long lastTime = sampleFile.lastModified();
        long lastLength = sampleFile.length();
        long previousTime = sldResource.lastmodified();
        sldResource.file().setLastModified((lastTime + 1000));
        catalog.firePostModified(catalog.getStyleByName("Bridges"), new ArrayList<String>(), new ArrayList(), new ArrayList());
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList legendURLs = XPATH.getMatchingNodes(getLegendURLXPath("cite:Bridges"), dom);
        Assert.assertEquals(1, legendURLs.getLength());
        Element legendURL = ((Element) (legendURLs.item(0)));
        Assert.assertTrue(legendURL.hasAttribute("width"));
        Assert.assertEquals("20", legendURL.getAttribute("width"));
        Assert.assertTrue(legendURL.hasAttribute("height"));
        Assert.assertEquals("20", legendURL.getAttribute("height"));
        Assert.assertFalse(((getSampleFile("Bridges").length()) == lastLength));
        sldResource.file().setLastModified(previousTime);
    }

    /**
     * Tests that already cached icons are read from disk and used to calculate size.
     */
    @Test
    public void testOnlineResourceWidthHeight() throws Exception {
        TransformerBase tr = createTransformer();
        tr.setIndentation(2);
        Document dom = WMSTestSupport.transform(req, tr);
        NodeList onlineResources = XPATH.getMatchingNodes(getOnlineResourceXPath("cite:BasicPolygons"), dom);
        Assert.assertEquals(1, onlineResources.getLength());
        Element onlineResource = ((Element) (onlineResources.item(0)));
        String href = onlineResource.getAttribute("xlink:href");
        Assert.assertNotNull(href);
        Assert.assertTrue(href.contains("width=20"));
        Assert.assertTrue(href.contains("height=20"));
    }
}

