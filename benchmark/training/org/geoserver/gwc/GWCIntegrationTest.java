/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import Dispatcher.REQUEST;
import HttpServletResponse.SC_NOT_MODIFIED;
import HttpServletResponse.SC_OK;
import MockData.BUILDINGS;
import MockData.CITE_PREFIX;
import MockData.MPOINTS;
import ResourceInfo.CACHE_AGE_MAX;
import ResourceInfo.CACHING_ENABLED;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerLoader;
import org.geoserver.data.test.MockData;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.gwc.layer.CatalogConfiguration;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.gwc.layer.GeoServerTileLayerInfo;
import org.geoserver.gwc.wmts.WMTSInfo;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.ows.Request;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geowebcache.GeoWebCacheDispatcher;
import org.geowebcache.GeoWebCacheException;
import org.geowebcache.GeoWebCacheExtensions;
import org.geowebcache.diskquota.DiskQuotaConfig;
import org.geowebcache.diskquota.jdbc.JDBCConfiguration;
import org.geowebcache.diskquota.jdbc.JDBCConfiguration.ConnectionPoolConfiguration;
import org.geowebcache.diskquota.jdbc.JDBCQuotaStore;
import org.geowebcache.filter.parameters.StringParameterFilter;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.grid.GridSubset;
import org.geowebcache.grid.GridSubsetFactory;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.service.wmts.WMTSService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class GWCIntegrationTest extends GeoServerSystemTestSupport {
    // WMTS 1.0 namespaces
    private static final Map<String, String> WMTS_NAMESPACES_10 = new HashMap<>();

    static {
        // populate WMTS 1.0 namespaces map
        GWCIntegrationTest.WMTS_NAMESPACES_10.put("xlink", "http://www.w3.org/1999/xlink");
        GWCIntegrationTest.WMTS_NAMESPACES_10.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        GWCIntegrationTest.WMTS_NAMESPACES_10.put("ows", "http://www.opengis.net/ows/1.1");
        GWCIntegrationTest.WMTS_NAMESPACES_10.put("wmts", "http://www.opengis.net/wmts/1.0");
        // set this as the default namespaces
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(GWCIntegrationTest.WMTS_NAMESPACES_10));
    }

    // WMTS 1.0  XPATH engine
    private static final XpathEngine WMTS_XPATH_10 = XMLUnit.newXpathEngine();

    static final String SIMPLE_LAYER_GROUP = "SIMPLE_LAYER_GROUP";

    static final String FLAT_LAYER_GROUP = "flatLayerGroup";

    static final String NESTED_LAYER_GROUP = "nestedLayerGroup";

    static final String CONTAINER_LAYER_GROUP = "containerLayerGroup";

    static final String WORKSPACED_LAYER_GROUP = "workspacedLayerGroup";

    static final String TEST_WORKSPACE_NAME = "testWorkspace";

    static final String TEST_WORKSPACE_URI = "http://geoserver.org/GWCIntegerationTest/" + (GWCIntegrationTest.TEST_WORKSPACE_NAME);

    static final String WORKSPACED_STYLE_NAME = "workspacedStyle";

    static final String WORKSPACED_STYLE_FILE = "workspacedStyle.sld";

    static final String WORKSPACED_LAYER = "workspacedLayer";

    static final QName WORKSPACED_LAYER_QNAME = new QName(GWCIntegrationTest.TEST_WORKSPACE_URI, GWCIntegrationTest.WORKSPACED_LAYER, GWCIntegrationTest.TEST_WORKSPACE_NAME);

    static QName BASIC_POLYGONS_NO_CRS = new QName(MockData.CITE_URI, "BasicPolygonsNoCrs", MockData.CITE_PREFIX);

    @Value("${gwc.context.suffix}")
    private String suffix;

    @Test
    public void testPngIntegration() throws Exception {
        String layerId = getLayerId(MockData.BASIC_POLYGONS);
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + layerId) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
    }

    @Test
    public void testRequestReplacement() throws Exception {
        String layerId = getLayerId(MockData.BASIC_POLYGONS);
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + layerId) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=1"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
    }

    @Test
    public void testWorkspacedStyle() throws Exception {
        String layerId = getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + layerId) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
    }

    @Test
    public void testGetLegendGraphics() throws Exception {
        String layerId = getLayerId(MockData.BASIC_POLYGONS);
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wms?service=wms&version=1.1.1&request=GetLegendGraphic&layer=" + layerId) + "&style=&format=image/png"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
    }

    @Test
    public void testCachingHeadersSingleLayer() throws Exception {
        String layerId = getLayerId(MockData.BASIC_POLYGONS);
        setCachingMetadata(layerId, true, 7200);
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + layerId) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertEquals("max-age=7200, must-revalidate", sr.getHeader("Cache-Control"));
    }

    @Test
    public void testCachingHeadersSingleLayerNoHeaders() throws Exception {
        String layerId = getLayerId(MockData.BASIC_POLYGONS);
        setCachingMetadata(layerId, false, (-1));
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + layerId) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertNull(sr.getHeader("Cache-Control"));
    }

    @Test
    public void testCachingHeadersFlatLayerGroup() throws Exception {
        // set two different caching headers for the two layers
        String bpLayerId = getLayerId(MockData.BASIC_POLYGONS);
        setCachingMetadata(bpLayerId, true, 7200);
        String mpLayerId = getLayerId(MPOINTS);
        setCachingMetadata(mpLayerId, true, 1000);
        // build a flat layer group with them
        LayerGroupInfo lg = getCatalog().getFactory().createLayerGroup();
        lg.setName(GWCIntegrationTest.FLAT_LAYER_GROUP);
        lg.getLayers().add(getCatalog().getLayerByName(bpLayerId));
        lg.getLayers().add(getCatalog().getLayerByName(mpLayerId));
        lg.getStyles().add(null);
        lg.getStyles().add(null);
        new CatalogBuilder(getCatalog()).calculateLayerGroupBounds(lg);
        getCatalog().add(lg);
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + (GWCIntegrationTest.FLAT_LAYER_GROUP)) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertEquals("max-age=1000, must-revalidate", sr.getHeader("Cache-Control"));
    }

    @Test
    public void testCachingHeadersNestedLayerGroup() throws Exception {
        // set two different caching headers for the two layers
        String bpLayerId = getLayerId(MockData.BASIC_POLYGONS);
        setCachingMetadata(bpLayerId, true, 7200);
        String mpLayerId = getLayerId(MPOINTS);
        setCachingMetadata(mpLayerId, true, 1000);
        CatalogBuilder builder = new CatalogBuilder(getCatalog());
        // build the nested layer group, only one layer in it
        LayerGroupInfo nested = getCatalog().getFactory().createLayerGroup();
        nested.setName(GWCIntegrationTest.NESTED_LAYER_GROUP);
        nested.getLayers().add(getCatalog().getLayerByName(bpLayerId));
        nested.getStyles().add(null);
        builder.calculateLayerGroupBounds(nested);
        getCatalog().add(nested);
        // build the container layer group
        LayerGroupInfo container = getCatalog().getFactory().createLayerGroup();
        container.setName(GWCIntegrationTest.CONTAINER_LAYER_GROUP);
        container.getLayers().add(getCatalog().getLayerByName(mpLayerId));
        container.getLayers().add(getCatalog().getLayerGroupByName(GWCIntegrationTest.NESTED_LAYER_GROUP));
        container.getStyles().add(null);
        container.getStyles().add(null);
        builder.calculateLayerGroupBounds(container);
        getCatalog().add(container);
        // check the caching headers on the nested group
        MockHttpServletResponse sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + (GWCIntegrationTest.NESTED_LAYER_GROUP)) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertEquals("max-age=7200, must-revalidate", sr.getHeader("Cache-Control"));
        // check the caching headers on the container layer group
        sr = getAsServletResponse((("gwc/service/wmts?request=GetTile&layer=" + (GWCIntegrationTest.CONTAINER_LAYER_GROUP)) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertEquals("max-age=1000, must-revalidate", sr.getHeader("Cache-Control"));
    }

    /**
     * If direct WMS integration is enabled, a GetMap requests that hits the regular WMS but matches
     * a gwc tile should return with the proper {@code geowebcache-tile-index} HTTP response header.
     */
    @Test
    public void testDirectWMSIntegration() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        String request;
        MockHttpServletResponse response;
        request = buildGetMap(true, layerName, "EPSG:4326", null);
        response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertNull(response.getHeader("geowebcache-tile-index"));
        request = request + "&tiled=true";
        response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testDirectWMSIntegrationResponseHeaders() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        String request = (buildGetMap(true, layerName, "EPSG:4326", null)) + "&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals(layerName, response.getHeader("geowebcache-layer"));
        Assert.assertEquals("[0, 0, 0]", response.getHeader("geowebcache-tile-index"));
        Assert.assertEquals("-180.0,-90.0,0.0,90.0", response.getHeader("geowebcache-tile-bounds"));
        Assert.assertEquals("EPSG:4326", response.getHeader("geowebcache-gridset"));
        Assert.assertEquals("EPSG:4326", response.getHeader("geowebcache-crs"));
    }

    @Test
    public void testDirectWMSIntegrationResponseHeaders13() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        String request = ("wms?service=wms&version=1.3.0&request=GetMap&styles=&layers=" + layerName) + "&srs=EPSG:4326&bbox=-90,-180,90,0&format=image/png&width=256&height=256&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals(layerName, response.getHeader("geowebcache-layer"));
        Assert.assertEquals("[0, 0, 0]", response.getHeader("geowebcache-tile-index"));
        Assert.assertEquals("-180.0,-90.0,0.0,90.0", response.getHeader("geowebcache-tile-bounds"));
        Assert.assertEquals("EPSG:4326", response.getHeader("geowebcache-gridset"));
        Assert.assertEquals("EPSG:4326", response.getHeader("geowebcache-crs"));
    }

    @Test
    public void testDirectWMSIntegrationIfModifiedSinceSupport() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        final String path = (buildGetMap(true, layerName, "EPSG:4326", null)) + "&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        String lastModifiedHeader = response.getHeader("Last-Modified");
        Assert.assertNotNull(lastModifiedHeader);
        Date lastModified = DateUtils.parseDate(lastModifiedHeader);
        MockHttpServletRequest httpReq = createGetRequest(path);
        httpReq.addHeader("If-Modified-Since", lastModifiedHeader);
        response = dispatch(httpReq, "UTF-8");
        Assert.assertEquals(SC_NOT_MODIFIED, response.getStatus());
        // set the If-Modified-Since header to some point in the past of the last modified value
        Date past = new Date(((lastModified.getTime()) - 5000));
        String ifModifiedSince = DateUtils.formatDate(past);
        httpReq = createGetRequest(path);
        httpReq.addHeader("If-Modified-Since", ifModifiedSince);
        response = dispatch(httpReq, "UTF-8");
        Assert.assertEquals(SC_OK, response.getStatus());
        Date future = new Date(((lastModified.getTime()) + 5000));
        ifModifiedSince = DateUtils.formatDate(future);
        httpReq = createGetRequest(path);
        httpReq.addHeader("If-Modified-Since", ifModifiedSince);
        response = dispatch(httpReq, "UTF-8");
        Assert.assertEquals(SC_NOT_MODIFIED, response.getStatus());
    }

    @Test
    public void testDirectWMSIntegrationMaxAge() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        final String path = (buildGetMap(true, layerName, "EPSG:4326", null)) + "&tiled=true";
        final String qualifiedName = super.getLayerId(BASIC_POLYGONS);
        final GeoServerTileLayer tileLayer = ((GeoServerTileLayer) (gwc.getTileLayerByName(qualifiedName)));
        tileLayer.getLayerInfo().getResource().getMetadata().put(CACHING_ENABLED, "true");
        tileLayer.getLayerInfo().getResource().getMetadata().put(CACHE_AGE_MAX, 3456);
        MockHttpServletResponse response = getAsServletResponse(path);
        String cacheControl = response.getHeader("Cache-Control");
        Assert.assertEquals("max-age=3456", cacheControl);
        Assert.assertNotNull(response.getHeader("Last-Modified"));
        tileLayer.getLayerInfo().getResource().getMetadata().put(CACHING_ENABLED, "false");
        response = getAsServletResponse(path);
        cacheControl = response.getHeader("Cache-Control");
        Assert.assertEquals("no-cache", cacheControl);
        // make sure a boolean is handled, too - see comment in CachingWebMapService
        tileLayer.getLayerInfo().getResource().getMetadata().put(CACHING_ENABLED, Boolean.FALSE);
        response = getAsServletResponse(path);
        cacheControl = response.getHeader("Cache-Control");
        Assert.assertEquals("no-cache", cacheControl);
    }

    @Test
    public void testDirectWMSIntegrationWithVirtualServices() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(BASIC_POLYGONS);
        final String localName = BASIC_POLYGONS.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        boolean directWMSIntegrationEndpoint = true;
        String request = (((MockData.CITE_PREFIX) + "/") + (buildGetMap(directWMSIntegrationEndpoint, localName, "EPSG:4326", null, tileLayer))) + "&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals(qualifiedName, response.getHeader("geowebcache-layer"));
    }

    @Test
    public void testDirectWMSIntegrationWithVirtualServicesAndWorkspacedStyle() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        final String localName = GWCIntegrationTest.WORKSPACED_LAYER_QNAME.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        boolean directWMSIntegrationEndpoint = true;
        String request = (((GWCIntegrationTest.TEST_WORKSPACE_NAME) + "/") + (buildGetMap(directWMSIntegrationEndpoint, localName, "EPSG:4326", null, tileLayer))) + "&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals(qualifiedName, response.getHeader("geowebcache-layer"));
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        MockHttpServletResponse response2 = getAsServletResponse(request);
        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("image/png", response2.getContentType());
        Assert.assertEquals(qualifiedName, response2.getHeader("geowebcache-layer"));
        Assert.assertThat(response2.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // now try with the style name too, should be another hit
        request = (((GWCIntegrationTest.TEST_WORKSPACE_NAME) + "/") + (buildGetMap(directWMSIntegrationEndpoint, localName, "EPSG:4326", GWCIntegrationTest.WORKSPACED_STYLE_NAME, tileLayer))) + "&tiled=true";
        MockHttpServletResponse response3 = getAsServletResponse(request);
        Assert.assertEquals(200, response3.getStatus());
        Assert.assertEquals("image/png", response3.getContentType());
        Assert.assertEquals(qualifiedName, response3.getHeader("geowebcache-layer"));
        Assert.assertThat(response3.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // finally, rename the workspace
        String oldWorkspaceName = GWCIntegrationTest.TEST_WORKSPACE_NAME;
        WorkspaceInfo ws = getCatalog().getWorkspaceByName(oldWorkspaceName);
        String newWorkspaceName = oldWorkspaceName + "_renamed";
        ws.setName(newWorkspaceName);
        getCatalog().save(ws);
        // rename the bits in the request, it should be another hit
        request = ((newWorkspaceName + "/") + (buildGetMap(directWMSIntegrationEndpoint, localName, "EPSG:4326", GWCIntegrationTest.WORKSPACED_STYLE_NAME, tileLayer))) + "&tiled=true";
        MockHttpServletResponse response4 = getAsServletResponse(request);
        Assert.assertEquals(200, response4.getStatus());
        Assert.assertEquals("image/png", response4.getContentType());
        Assert.assertEquals(((newWorkspaceName + ":") + (GWCIntegrationTest.WORKSPACED_LAYER)), response4.getHeader("geowebcache-layer"));
        Assert.assertThat(response4.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
    }

    @Test
    public void testAutomaticTruncationDefaultStyleContentsChange() throws Exception {
        final GWC gwc = GWC.get();
        final Catalog catalog = getCatalog();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        final String localName = GWCIntegrationTest.WORKSPACED_LAYER_QNAME.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        LayerInfo layer = catalog.getLayerByName(qualifiedName);
        Assert.assertNotNull(layer);
        String request = ("gwc/service/wmts?request=GetTile&layer=" + qualifiedName) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse response = getAsServletResponse(request);
        // First request should be a MISS
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // Second request should be a HIT
        MockHttpServletResponse response2 = getAsServletResponse(request);
        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("image/png", response2.getContentType());
        Assert.assertThat(response2.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // Rewrite the contents of the style; this should truncate the blobStore
        // write out the SLD, we try to use the old style so the same path is used
        StyleInfo styleToRewrite = layer.getDefaultStyle();
        StyleInfo generic = catalog.getStyleByName("generic");
        // ask the catalog to write the style
        catalog.getResourcePool().writeStyle(styleToRewrite, new GeoServerDataDirectory(catalog.getResourceLoader()).style(generic).in());
        // update the catalog
        catalog.save(styleToRewrite);
        waitTileBreederCompletion();
        MockHttpServletResponse response3 = getAsServletResponse(request);
        Assert.assertEquals(200, response3.getStatus());
        Assert.assertEquals("image/png", response3.getContentType());
        Assert.assertThat(response3.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
    }

    @Test
    public void testAutomaticTruncationDefaultStyleChange() throws Exception {
        final GWC gwc = GWC.get();
        final Catalog catalog = getCatalog();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        final String localName = GWCIntegrationTest.WORKSPACED_LAYER_QNAME.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        LayerInfo layer = catalog.getLayerByName(qualifiedName);
        Assert.assertNotNull(layer);
        String request = ("gwc/service/wmts?request=GetTile&layer=" + qualifiedName) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse response = getAsServletResponse(request);
        // First request should be a MISS
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // Second request should be a HIT
        MockHttpServletResponse response2 = getAsServletResponse(request);
        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("image/png", response2.getContentType());
        Assert.assertThat(response2.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // Change the style; this should truncate the blobStore
        layer.setDefaultStyle(catalog.getStyleByName("generic"));
        catalog.save(layer);
        waitTileBreederCompletion();
        MockHttpServletResponse response3 = getAsServletResponse(request);
        Assert.assertEquals(200, response3.getStatus());
        Assert.assertEquals("image/png", response3.getContentType());
        Assert.assertThat(response3.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
    }

    @Test
    public void testAutomaticTruncationLayerStyleChange() throws Exception {
        final GWC gwc = GWC.get();
        final Catalog catalog = getCatalog();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        final String localName = GWCIntegrationTest.WORKSPACED_LAYER_QNAME.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        LayerInfo layer = catalog.getLayerByName(qualifiedName);
        Assert.assertNotNull(layer);
        layer.getStyles().add(catalog.getStyleByName("generic"));
        catalog.save(layer);
        layer = catalog.getLayerByName(qualifiedName);
        String request = (("gwc/service/wmts?request=GetTile&layer=" + qualifiedName) + "&style=generic") + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse response = getAsServletResponse(request);
        // First request should be a MISS
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // Second request should be a HIT
        MockHttpServletResponse response2 = getAsServletResponse(request);
        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("image/png", response2.getContentType());
        Assert.assertThat(response2.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // Remove the "generic" style, and add it back; this should truncate the layer
        layer.getStyles().remove(catalog.getStyleByName("generic"));
        catalog.save(layer);
        layer = catalog.getLayerByName(qualifiedName);
        layer.getStyles().add(catalog.getStyleByName("generic"));
        catalog.save(layer);
        layer = catalog.getLayerByName(qualifiedName);
        waitTileBreederCompletion();
        MockHttpServletResponse response3 = getAsServletResponse(request);
        Assert.assertEquals(200, response3.getStatus());
        Assert.assertEquals("image/png", response3.getContentType());
        Assert.assertThat(response3.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
    }

    @Test
    public void testAutomaticTruncationLayerStyleContentsChange() throws Exception {
        final GWC gwc = GWC.get();
        final Catalog catalog = getCatalog();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        final String localName = GWCIntegrationTest.WORKSPACED_LAYER_QNAME.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        LayerInfo layer = catalog.getLayerByName(qualifiedName);
        Assert.assertNotNull(layer);
        layer.getStyles().add(catalog.getStyleByName("generic"));
        catalog.save(layer);
        layer = catalog.getLayerByName(qualifiedName);
        String request = (("gwc/service/wmts?request=GetTile&layer=" + qualifiedName) + "&style=generic") + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse response = getAsServletResponse(request);
        // First request should be a MISS
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // Second request should be a HIT
        MockHttpServletResponse response2 = getAsServletResponse(request);
        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("image/png", response2.getContentType());
        Assert.assertThat(response2.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // Rewrite the contents of the style; this should truncate the blobStore
        StyleInfo styleToRewrite = catalog.getStyleByName("generic");
        try (InputStream is = GeoServerLoader.class.getResourceAsStream("default_generic.sld")) {
            catalog.getResourcePool().writeStyle(styleToRewrite, is);
        }
        catalog.save(styleToRewrite);
        waitTileBreederCompletion();
        MockHttpServletResponse response3 = getAsServletResponse(request);
        Assert.assertEquals(200, response3.getStatus());
        Assert.assertEquals("image/png", response3.getContentType());
        Assert.assertThat(response3.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
    }

    @Test
    public void testAutomaticTruncationFeatureChange() throws Exception {
        final GWC gwc = GWC.get();
        final Catalog catalog = getCatalog();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(GWCIntegrationTest.WORKSPACED_LAYER_QNAME);
        final String localName = GWCIntegrationTest.WORKSPACED_LAYER_QNAME.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        LayerInfo layer = catalog.getLayerByName(qualifiedName);
        Assert.assertNotNull(layer);
        String request = ("gwc/service/wmts?request=GetTile&layer=" + qualifiedName) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse response = getAsServletResponse(request);
        // First request should be a MISS
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertThat(response.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // Second request should be a HIT
        MockHttpServletResponse response2 = getAsServletResponse(request);
        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("image/png", response2.getContentType());
        Assert.assertThat(response2.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // Change the feature via the GeoServerFeatureStore wrapper; this should truncate the
        // blobStore
        // SimpleFeatureStore store = DataUtilities.simple((FeatureStore)
        // ((FeatureTypeInfo)layer.getResource()).getFeatureSource(null,null));
        // Do a WFS Insert against the layer. This should trigger a cache miss.
        final String wfsInsert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:testWorkspace=\"http://geoserver.org/GWCIntegerationTest/testWorkspace\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-transaction.xsd http://geoserver.org/GWCIntegerationTest/testWorkspace http://localhost:8080/geoserver/wfs/DescribeFeatureType?typename=testWorkspace:workspacedLayer\">\n" + ((((((((((("    <wfs:Insert>\n" + "        <testWorkspace:workspacedLayer>\n") + "            <testWorkspace:location>\n") + "                    <gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\" >\n") + "                      <gml:coordinates decimal=\".\" cs=\",\" ts=\" \">0,0</gml:coordinates>\n") + "                    </gml:Point>\n") + "            </testWorkspace:location>\n") + "            <testWorkspace:name>origin</testWorkspace:name>\n") + "            <testWorkspace:value>0</testWorkspace:value>\n") + "        </testWorkspace:workspacedLayer>\n") + "    </wfs:Insert>\n") + "</wfs:Transaction>");
        String wfsRequest = (GWCIntegrationTest.TEST_WORKSPACE_NAME) + "/wfs?service=WFS&version=1.0.0&request=Transaction";
        MockHttpServletResponse wfsResponse = postAsServletResponse(wfsRequest, wfsInsert);
        Assert.assertEquals(200, wfsResponse.getStatus());
        waitTileBreederCompletion();
        MockHttpServletResponse response3 = getAsServletResponse(request);
        Assert.assertEquals(200, response3.getStatus());
        Assert.assertEquals("image/png", response3.getContentType());
        Assert.assertThat(response3.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
    }

    @Test
    public void testLayerGroupInWorkspace() throws Exception {
        // the workspace for the tests
        String workspaceName = MockData.BASIC_POLYGONS.getPrefix();
        // build a flat layer group with them, in the test workspace
        LayerGroupInfo lg = getCatalog().getFactory().createLayerGroup();
        lg.setName(GWCIntegrationTest.WORKSPACED_LAYER_GROUP);
        String bpLayerId = getLayerId(MockData.BASIC_POLYGONS);
        String mpLayerId = getLayerId(MockData.LAKES);
        lg.getLayers().add(getCatalog().getLayerByName(bpLayerId));
        lg.getLayers().add(getCatalog().getLayerByName(mpLayerId));
        lg.getStyles().add(null);
        lg.getStyles().add(null);
        lg.setWorkspace(getCatalog().getWorkspaceByName(workspaceName));
        new CatalogBuilder(getCatalog()).calculateLayerGroupBounds(lg);
        getCatalog().add(lg);
        // wmts request, use the qualified name, first request, works, but it's a cache miss of
        // course
        String request = ((("gwc/service/wmts?request=GetTile&layer=" + workspaceName) + ":") + (GWCIntegrationTest.WORKSPACED_LAYER_GROUP)) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("MISS"));
        // run again, it should be a hit
        sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // try direct integration too
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final TileLayer tileLayer = gwc.getTileLayerByName(lg.prefixedName());
        request = (buildGetMap(true, lg.prefixedName(), "EPSG:4326", null, tileLayer)) + "&tiled=true";
        sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertEquals(lg.prefixedName(), sr.getHeader("geowebcache-layer"));
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // and direct integration against the workspace local name
        request = ((workspaceName + "/") + (buildGetMap(true, lg.getName(), "EPSG:4326", null, tileLayer))) + "&tiled=true";
        sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals(lg.prefixedName(), sr.getHeader("geowebcache-layer"));
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // now change the workspace name
        WorkspaceInfo ws = getCatalog().getWorkspaceByName(workspaceName);
        String newWorkspaceName = workspaceName + "_renamed";
        ws.setName(newWorkspaceName);
        getCatalog().save(ws);
        // prepare the wmts request anew, it should be a hit, the cache should be preserved
        request = ((("gwc/service/wmts?request=GetTile&layer=" + newWorkspaceName) + ":") + (GWCIntegrationTest.WORKSPACED_LAYER_GROUP)) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // and now direct integration
        String newQualifiedName = (newWorkspaceName + ":") + (lg.getName());
        request = (buildGetMap(true, newQualifiedName, "EPSG:4326", null, tileLayer)) + "&tiled=true";
        sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals("image/png", sr.getContentType());
        Assert.assertEquals(lg.prefixedName(), sr.getHeader("geowebcache-layer"));
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
        // and direct integration against the workspace local name
        request = ((newWorkspaceName + "/") + (buildGetMap(true, lg.getName(), "EPSG:4326", null, tileLayer))) + "&tiled=true";
        sr = getAsServletResponse(request);
        Assert.assertEquals(200, sr.getStatus());
        Assert.assertEquals(newQualifiedName, sr.getHeader("geowebcache-layer"));
        Assert.assertThat(sr.getHeader("geowebcache-cache-result"), Matchers.equalToIgnoringCase("HIT"));
    }

    @Test
    public void testDirectWMSIntegrationWithVirtualServicesHiddenLayer() throws Exception {
        /* Nothing special needs to be done at the GWC integration level for this to work. The hard
        work should already be done by WMSWorkspaceQualifier so that when the request hits GWC
        the layer name is already qualified
         */
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String qualifiedName = super.getLayerId(BASIC_POLYGONS);
        final String localName = BASIC_POLYGONS.getLocalPart();
        final TileLayer tileLayer = gwc.getTileLayerByName(qualifiedName);
        Assert.assertNotNull(tileLayer);
        boolean directWMSIntegrationEndpoint = true;
        String request = (((MockData.CDF_PREFIX)// asking /geoserver/cdf/wms? for cite:BasicPolygons
         + "/") + (buildGetMap(directWMSIntegrationEndpoint, localName, "EPSG:4326", null, tileLayer))) + "&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(response.getContentType(), response.getContentType().startsWith("application/vnd.ogc.se_xml"));
        Assert.assertTrue(response.getContentAsString(), response.getContentAsString().contains("Could not find layer cdf:BasicPolygons"));
    }

    @Test
    public void testDirectWMSIntegrationCustomHost() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        String requestURL = (buildGetMap(true, layerName, "EPSG:4326", null)) + "&tiled=true";
        MockHttpServletRequest request = createRequest(requestURL);
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        final String THE_HOST = "foobar";
        request.setRemoteHost(THE_HOST);
        MockHttpServletResponse response = dispatch(request, null);
        // check everything went as expected
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals(layerName, response.getHeader("geowebcache-layer"));
        Assert.assertEquals("[0, 0, 0]", response.getHeader("geowebcache-tile-index"));
        Assert.assertEquals("-180.0,-90.0,0.0,90.0", response.getHeader("geowebcache-tile-bounds"));
        Assert.assertEquals("EPSG:4326", response.getHeader("geowebcache-gridset"));
        Assert.assertEquals("EPSG:4326", response.getHeader("geowebcache-crs"));
        // check we have the two requests recorded, and the
        ArrayList<HttpServletRequest> requests = HttpRequestRecorderCallback.getRequests();
        Assert.assertEquals(2, requests.size());
        Assert.assertThat(requests.get(1), CoreMatchers.instanceOf(FakeHttpServletRequest.class));
        FakeHttpServletRequest fake = ((FakeHttpServletRequest) (requests.get(1)));
        Assert.assertEquals(THE_HOST, fake.getRemoteHost());
    }

    @Test
    public void testReloadConfiguration() throws Exception {
        String path = "/gwc/rest/reload";
        String content = "reload_configuration=1";
        String contentType = "application/x-www-form-urlencoded";
        MockHttpServletResponse response = postAsServletResponse(path, content, contentType);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testBasicIntegration() throws Exception {
        Catalog cat = getCatalog();
        TileLayerDispatcher tld = GeoWebCacheExtensions.bean(TileLayerDispatcher.class);
        Assert.assertNotNull(tld);
        GridSetBroker gridSetBroker = GeoWebCacheExtensions.bean(GridSetBroker.class);
        Assert.assertNotNull(gridSetBroker);
        try {
            tld.getTileLayer("");
        } catch (Exception gwce) {
        }
        // 1) Check that cite:Lakes is present
        boolean foundLakes = false;
        for (TileLayer tl : tld.getLayerList()) {
            if (tl.getName().equals("cite:Lakes")) {
                foundLakes = true;
                break;
            }
        }
        Assert.assertTrue(foundLakes);
        // 2) Check sf:GenerictEntity is present and initialized
        boolean foudAGF = false;
        for (TileLayer tl : tld.getLayerList()) {
            if (tl.getName().equals("sf:AggregateGeoFeature")) {
                // tl.isInitialized();
                foudAGF = true;
                GridSubset epsg4326 = tl.getGridSubset(gridSetBroker.getWorldEpsg4326().getName());
                Assert.assertTrue(epsg4326.getGridSetBounds().equals(new BoundingBox((-180.0), (-90.0), 180.0, 90.0)));
                String mime = tl.getMimeTypes().get(1).getMimeType();
                Assert.assertTrue(((mime.startsWith("image/")) || (mime.startsWith("application/vnd.google-earth.kml+xml"))));
            }
        }
        Assert.assertTrue(foudAGF);
        // 3) Basic get
        LayerInfo li = cat.getLayerByName(super.getLayerId(MPOINTS));
        String layerName = GWC.tileLayerName(li);
        TileLayer tl = tld.getTileLayer(layerName);
        Assert.assertEquals(layerName, tl.getName());
        // 4) Removal of LayerInfo from catalog
        cat.remove(li);
        Assert.assertNull(cat.getLayerByName(tl.getName()));
        try {
            tld.getTileLayer(layerName);
            Assert.fail("Layer should not exist");
        } catch (GeoWebCacheException gwce) {
            Assert.assertTrue(true);
        }
    }

    /**
     * See GEOS-5092, check server startup is not hurt by a tile layer out of sync (say someone
     * manually removed the GeoServer layer)
     */
    @Test
    public void testMissingGeoServerLayerAtStartUp() throws Exception {
        Catalog catalog = getCatalog();
        GWC mediator = GWC.get();
        final String layerName = getLayerId(BASIC_POLYGONS);
        LayerInfo layerInfo = catalog.getLayerByName(layerName);
        Assert.assertNotNull(layerInfo);
        TileLayer tileLayer = mediator.getTileLayerByName(layerName);
        Assert.assertNotNull(tileLayer);
        Assert.assertTrue(tileLayer.isEnabled());
        getCatalog().remove(layerInfo);
        getGeoServer().reload();
        Assert.assertNull(catalog.getLayerByName(layerName));
        CatalogConfiguration config = GeoServerExtensions.bean(CatalogConfiguration.class);
        Assert.assertFalse(config.getLayer(layerName).isPresent());
        try {
            mediator.getTileLayerByName(layerName);
            Assert.fail("Expected IAE");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRemoveLayerAfterReload() throws Exception {
        Catalog cat = getCatalog();
        TileLayerDispatcher tld = GeoWebCacheExtensions.bean(TileLayerDispatcher.class);
        LayerInfo li = cat.getLayerByName(super.getLayerId(MPOINTS));
        String layerName = GWC.tileLayerName(li);
        Assert.assertNotNull(tld.getTileLayer(layerName));
        // force reload
        getGeoServer().reload();
        // now remove the layer and check it has been removed from GWC as well
        cat.remove(li);
        try {
            tld.getTileLayer(layerName);
            Assert.fail("Layer should not exist");
        } catch (GeoWebCacheException gwce) {
            // fine
        }
    }

    @Test
    public void testDiskQuotaStorage() throws Exception {
        // normal state, quota is not enabled by default
        GWC gwc = GWC.get();
        ConfigurableQuotaStoreProvider provider = GeoServerExtensions.bean(ConfigurableQuotaStoreProvider.class);
        DiskQuotaConfig quota = gwc.getDiskQuotaConfig();
        JDBCConfiguration jdbc = gwc.getJDBCDiskQuotaConfig();
        Assert.assertFalse("Disk quota is enabled??", quota.isEnabled());
        Assert.assertNull("jdbc quota config should be missing", jdbc);
        Assert.assertTrue(((getActualStore(provider)) instanceof DummyQuotaStore));
        // enable disk quota in H2 mode
        quota.setEnabled(true);
        quota.setQuotaStore("H2");
        gwc.saveDiskQuotaConfig(quota, null);
        GeoServerDataDirectory dd = GeoServerExtensions.bean(GeoServerDataDirectory.class);
        String jdbcConfigPath = "gwc/geowebcache-diskquota-jdbc.xml";
        Assert.assertNull((("jdbc config (" + jdbcConfigPath) + ") should not be there"), dd.findFile(jdbcConfigPath));
        String h2StorePath = "gwc/diskquota_page_store_h2";
        Assert.assertNotNull((("jdbc store (" + h2StorePath) + ") should be there"), dd.findFile(h2StorePath));
        Assert.assertTrue(((getActualStore(provider)) instanceof JDBCQuotaStore));
        // disable again and clean up
        quota.setEnabled(false);
        gwc.saveDiskQuotaConfig(quota, null);
        FileUtils.deleteDirectory(dd.findFile("gwc/diskquota_page_store_h2"));
        // now enable it in JDBC mode, with H2 local storage
        quota.setEnabled(true);
        quota.setQuotaStore("JDBC");
        jdbc = new JDBCConfiguration();
        jdbc.setDialect("H2");
        ConnectionPoolConfiguration pool = new ConnectionPoolConfiguration();
        pool.setDriver("org.h2.Driver");
        pool.setUrl("jdbc:h2:./target/quota-h2");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setMinConnections(1);
        pool.setMaxConnections(1);
        pool.setMaxOpenPreparedStatements(50);
        jdbc.setConnectionPool(pool);
        gwc.saveDiskQuotaConfig(quota, jdbc);
        Assert.assertNotNull((("jdbc config (" + jdbcConfigPath) + ") should be there"), dd.findFile(jdbcConfigPath));
        Assert.assertNull((("jdbc store (" + h2StorePath) + ") should be there"), dd.findDataFile(h2StorePath));
        File newQuotaStore = new File("./target/quota-h2.data.db");
        Assert.assertTrue(newQuotaStore.exists());
        File jdbcConfigFile = dd.findFile(jdbcConfigPath);
        try (FileInputStream fis = new FileInputStream(jdbcConfigFile)) {
            Document dom = dom(fis);
            // print(dom);
            String storedPassword = XMLUnit.newXpathEngine().evaluate("/gwcJdbcConfiguration/connectionPool/password", dom);
            // check the password has been encoded properly
            Assert.assertTrue(storedPassword.startsWith("crypt1:"));
        }
    }

    @Test
    public void testPreserveHeaders() throws Exception {
        // the code defaults to localhost:8080/geoserver, but the tests work otherwise
        GeoWebCacheDispatcher dispatcher = GeoServerExtensions.bean(GeoWebCacheDispatcher.class);
        // dispatcher.setServletPrefix("http://localhost/geoserver/");
        MockHttpServletResponse response = getAsServletResponse("gwc/service/wms?service=wms&version=1.1.0&request=GetCapabilities");
        // System.out.println(response.getContentAsString());
        Assert.assertEquals("application/vnd.ogc.wms_xml", response.getContentType());
        Assert.assertEquals("inline;filename=wms-getcapabilities.xml", response.getHeader("content-disposition"));
    }

    @Test
    public void testGutter() throws Exception {
        GeoServerTileLayer tileLayer = ((GeoServerTileLayer) (GWC.get().getTileLayerByName(getLayerId(BASIC_POLYGONS))));
        GeoServerTileLayerInfo info = tileLayer.getInfo();
        info.setGutter(100);
        GWC.get().save(tileLayer);
        String request = "gwc/service/wms?LAYERS=cite%3ABasicPolygons&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A4326&BBOX=0,0,11.25,11.25&WIDTH=256&HEIGHT=256";
        BufferedImage image = getAsImage(request, "image/png");
        // with GEOS-5786 active we would have gotten back a 356px image
        Assert.assertEquals(256, image.getWidth());
        Assert.assertEquals(256, image.getHeight());
    }

    @Test
    public void testSaveConfig() throws Exception {
        GWCConfig config = GWC.get().getConfig();
        // set a large gutter
        config.setGutter(100);
        // save the config
        GWC.get().saveConfig(config);
        // force a reload
        getGeoServer().reload();
        // grab the config, make sure it was saved as expected
        Assert.assertEquals(100, GWC.get().getConfig().getGutter());
    }

    @Test
    public void testRenameWorkspace() throws Exception {
        String wsName = MockData.CITE_PREFIX;
        String wsRenamed = (MockData.CITE_PREFIX) + "Renamed";
        Catalog catalog = getCatalog();
        WorkspaceInfo ws = catalog.getWorkspaceByName(wsName);
        try {
            // collect all the layer names that are in the CITE workspace
            List<String> layerNames = new ArrayList<String>();
            for (LayerInfo layer : catalog.getLayers()) {
                if (wsName.equals(layer.getResource().getStore().getWorkspace().getName())) {
                    String prefixedName = layer.prefixedName();
                    try {
                        // filter out geometryless layers and other stuff that cannot be hanlded by
                        // GWC
                        GWC.get().getTileLayerByName(prefixedName);
                        layerNames.add(layer.getName());
                    } catch (IllegalArgumentException e) {
                        // fine, we are skipping layers that cannot be handled
                    }
                }
            }
            // rename the workspace
            ws.setName(wsRenamed);
            catalog.save(ws);
            // check all the preview layers have been renamed too
            for (String name : layerNames) {
                String prefixedName = (wsRenamed + ":") + name;
                GWC.get().getTileLayerByName(prefixedName);
            }
        } finally {
            if (wsRenamed.equals(ws.getName())) {
                ws.setName(wsName);
                catalog.save(ws);
            }
        }
    }

    /**
     * Test that removing a layer from the catalog also removes its tile cache.
     */
    @Test
    public void testRemoveCachedLayer() throws Exception {
        // the prefixed name of the layer under test
        String layerName = getLayerId(MockData.BASIC_POLYGONS);
        Assert.assertEquals("cite:BasicPolygons", layerName);
        // resource path to cache directory (FileBlobStore)
        String cacheDirectory = "gwc/cite_BasicPolygons";
        // resource path to cached tile (FileBlobStore)
        String cachedTile = "gwc/cite_BasicPolygons/EPSG_4326_00/0_0/00_00.png";
        GeoServerResourceLoader loader = getResourceLoader();
        // cache directory and cached tile should not yet exist
        Assert.assertNull(("Unexpected cache directory " + cacheDirectory), loader.find(cacheDirectory));
        Assert.assertNull(("Unexpected cached tile " + cachedTile), loader.find(cachedTile));
        // trigger tile caching with a WMTS request
        MockHttpServletResponse response = getAsServletResponse(((((((("gwc/service/wmts"// 
         + ("?request=GetTile"// 
         + "&layer=")) + layerName)// 
         + "&format=image/png")// 
         + "&tilematrixset=EPSG:4326")// 
         + "&tilematrix=EPSG:4326:0")// 
         + "&tilerow=0")// 
         + "&tilecol=0"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        // cache directory and cached tile should now be present
        Assert.assertNotNull(("Missing cache directory " + cacheDirectory), loader.find(cacheDirectory));
        Assert.assertNotNull(("Missing cached tile " + cachedTile), loader.find(cachedTile));
        // remove layer from the catalog, which should also remove cache directory and thus cached
        // tile
        getCatalog().remove(getCatalog().getLayerByName(layerName));
        // cache directory and cached tile should now not exist
        Assert.assertNull(("Unexpected cache directory " + cacheDirectory), loader.find(cacheDirectory));
        Assert.assertNull(("Unexpected cached tile " + cachedTile), loader.find(cachedTile));
    }

    @Test
    public void testGetCapabilitiesWithLocalWorkspace() throws Exception {
        final Document doc = assertGetCapabilitiesWithLocalWorkspace();
        // print(doc);
        Assert.assertThat(GWCIntegrationTest.WMTS_XPATH_10.evaluate("//wmts:ServiceMetadataURL[2]/@xlink:href", doc), Matchers.equalTo("http://localhost:8080/geoserver/cite/gwc/service/wmts/rest/WMTSCapabilities.xml"));
    }

    @Test
    public void testGetCapabilitiesWithLocalWorkspaceAndProxyBase() throws Exception {
        final GeoServer gs = getGeoServer();
        try {
            setProxyBase(gs, "http://fooBar/geoserver");
            final Document doc = assertGetCapabilitiesWithLocalWorkspace();
            // print(doc);
            Assert.assertThat(GWCIntegrationTest.WMTS_XPATH_10.evaluate("//wmts:ServiceMetadataURL[2]/@xlink:href", doc), Matchers.equalTo("http://fooBar/geoserver/cite/gwc/service/wmts/rest/WMTSCapabilities.xml"));
        } finally {
            setProxyBase(gs, null);
        }
    }

    @Test
    public void testComputeGridsetBounds() throws Exception {
        // set native bounds whose CRS is null (which happens when going through the UI)
        Catalog catalog = getCatalog();
        FeatureTypeInfo ft = catalog.getFeatureTypeByName(getLayerId(GWCIntegrationTest.BASIC_POLYGONS_NO_CRS));
        CatalogBuilder cb = new CatalogBuilder(catalog);
        ft.setNativeCRS(null);
        cb.setupBounds(ft);
        catalog.save(ft);
        // force recomputing the grid subsets by adding one (at runtime this happens while editing a
        // layer)
        GridSetBroker gridSetBroker = GWC.get().getGridSetBroker();
        GridSet testGridSet = namedGridsetCopy("TEST", gridSetBroker.getDefaults().worldEpsg4326());
        GridSubset testGridSubset = GridSubsetFactory.createGridSubSet(testGridSet, new BoundingBox((-180), 0, 0, 90), 0, ((testGridSet.getGridLevels().length) - 1));
        GeoServerTileLayer tileLayer = ((GeoServerTileLayer) (GWC.get().getTileLayerByName(getLayerId(GWCIntegrationTest.BASIC_POLYGONS_NO_CRS))));
        tileLayer.addGridSubset(testGridSubset);
        // get the capabilities and check the gridset bounds (not the whole world)
        Document document = getAsDOM(((((MockData.CITE_PREFIX) + "/") + (GWCIntegrationTest.BASIC_POLYGONS_NO_CRS.getLocalPart())) + "/gwc/service/wmts?request=GetCapabilities"));
        // print(document);
        String basePath = "//wmts:Contents/wmts:Layer[ows:Title='BasicPolygonsNoCrs']" + ("/wmts:TileMatrixSetLink[wmts:TileMatrixSet='EPSG:4326']/wmts:TileMatrixSetLimits" + "/wmts:TileMatrixLimits[wmts:TileMatrix='EPSG:4326:1']");
        Assert.assertEquals("0", GWCIntegrationTest.WMTS_XPATH_10.evaluate((basePath + "/wmts:MinTileRow"), document));
        Assert.assertEquals("1", GWCIntegrationTest.WMTS_XPATH_10.evaluate((basePath + "/wmts:MaxTileRow"), document));
        Assert.assertEquals("1", GWCIntegrationTest.WMTS_XPATH_10.evaluate((basePath + "/wmts:MinTileCol"), document));
        Assert.assertEquals("2", GWCIntegrationTest.WMTS_XPATH_10.evaluate((basePath + "/wmts:MaxTileCol"), document));
    }

    @Test
    public void testGetCapabilitiesWithLocalLayer() throws Exception {
        // getting capabilities document for CITE workspace
        Document document = getAsDOM(((((MockData.CITE_PREFIX) + "/") + (BUILDINGS.getLocalPart())) + "/gwc/service/wmts?request=GetCapabilities"));
        // checking get capabilities result for CITE workspace
        List<LayerInfo> citeLayers = getWorkspaceLayers(CITE_PREFIX);
        Assert.assertThat(Integer.parseInt(GWCIntegrationTest.WMTS_XPATH_10.evaluate("count(//wmts:Contents/wmts:Layer)", document)), Matchers.equalTo(1));
        Assert.assertThat(GWCIntegrationTest.WMTS_XPATH_10.evaluate((("count(//wmts:Contents/wmts:Layer[ows:Identifier='" + (BUILDINGS.getLocalPart())) + "'])"), document), Matchers.is("1"));
    }

    @Test
    public void testGetCapabilitiesWithLocalGroup() throws Exception {
        // getting capabilities document for CITE workspace
        Document document = getAsDOM(((GWCIntegrationTest.SIMPLE_LAYER_GROUP) + "/gwc/service/wmts?request=GetCapabilities"));
        // checking get capabilities result for CITE workspace
        Assert.assertThat(Integer.parseInt(GWCIntegrationTest.WMTS_XPATH_10.evaluate("count(//wmts:Contents/wmts:Layer)", document)), Matchers.equalTo(1));
        Assert.assertThat(GWCIntegrationTest.WMTS_XPATH_10.evaluate((("count(//wmts:Contents/wmts:Layer[ows:Identifier='" + (GWCIntegrationTest.SIMPLE_LAYER_GROUP)) + "'])"), document), Matchers.is("1"));
    }

    @Test
    public void testGetTileWithLocalWorkspace() throws Exception {
        // perform a get tile request using a virtual service
        MockHttpServletResponse response = getAsServletResponse(((((MockData.CITE_PREFIX) + "/gwc/service/wmts?request=GetTile&layer=") + (MockData.BASIC_POLYGONS.getLocalPart())) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        // redo the same request
        response = getAsServletResponse(((((MockData.CITE_PREFIX) + "/gwc/service/wmts?request=GetTile&layer=") + (MockData.BASIC_POLYGONS.getLocalPart())) + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        // check that we got an hit
        String cacheResult = ((String) (response.getHeaderValue("geowebcache-cache-result")));
        Assert.assertThat(cacheResult, Matchers.notNullValue());
        Assert.assertThat(cacheResult, Matchers.is("HIT"));
    }

    @Test
    public void testWMTSEnabling() throws Exception {
        // store original value to restore it
        boolean initialValue = getGeoServer().getService(WMTSInfo.class).isEnabled();
        try {
            LocalWorkspace.set(null);
            WMTSInfo wmtsInfo = getGeoServer().getService(WMTSInfo.class);
            wmtsInfo.setEnabled(false);
            getGeoServer().save(wmtsInfo);
            MockHttpServletResponse response = getAsServletResponse("gwc/service/wmts?service=wmts&version=1.0.0&request=GetCapabilities");
            Assert.assertEquals(400, response.getStatus());
            wmtsInfo.setEnabled(true);
            getGeoServer().save(wmtsInfo);
            response = getAsServletResponse("gwc/service/wmts?service=wmts&version=1.0.0&request=GetCapabilities");
            Assert.assertEquals(200, response.getStatus());
        } finally {
            // restoring initial configuration value
            getGeoServer().getService(WMTSInfo.class).setEnabled(initialValue);
            LocalWorkspace.set(null);
        }
    }

    @Test
    public void testWmtsGetCapabilitiesRequest() throws Exception {
        // getting the capabilities document
        MockHttpServletResponse response = getAsServletResponse("/gwc/service/wmts?request=GetCapabilities");
        // check that the request was successful
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        // parse XML response content
        Document document = dom(response, false);
        print(document);
        // check that default styles are advertised
        String result = GWCIntegrationTest.WMTS_XPATH_10.evaluate(("count(//wmts:Contents/wmts:Layer/wmts:Style[@isDefault='true']" + "/ows:Identifier[text()='Default'])"), document);
        Assert.assertThat(Integer.parseInt(result), Matchers.greaterThan(0));
        // check that GeoServer service metadata is available
        result = GWCIntegrationTest.WMTS_XPATH_10.evaluate("count(//ows:ServiceProvider/ows:ProviderName[text()='http://geoserver.org'])", document);
        Assert.assertThat(Integer.parseInt(result), Matchers.is(1));
        // check that 0.0 and positive infinite scales are not advertised
        result = GWCIntegrationTest.WMTS_XPATH_10.evaluate(("count(//wmts:Contents/wmts:Layer/wmts:Style/" + "wmts:LegendURL[@minScaleDenominator='0.0'])"), document);
        Assert.assertThat(Integer.parseInt(result), Matchers.is(0));
        result = GWCIntegrationTest.WMTS_XPATH_10.evaluate(("count(//wmts:Contents/wmts:Layer/wmts:Style/" + "wmts:LegendURL[@maxScaleDenominator='NaN'])"), document);
        Assert.assertThat(Integer.parseInt(result), Matchers.is(0));
        // check that min and max scales are advertised
        result = GWCIntegrationTest.WMTS_XPATH_10.evaluate(("count(//wmts:Contents/wmts:Layer/wmts:Style/" + "wmts:LegendURL[@minScaleDenominator='100000.0'][@maxScaleDenominator='300000.0'])"), document);
        Assert.assertThat(Integer.parseInt(result), Matchers.greaterThan(0));
        // check the style group is reported
        result = GWCIntegrationTest.WMTS_XPATH_10.evaluate("count(//wmts:Contents/wmts:Layer[ows:Identifier='stylegroup'])", document);
        Assert.assertThat(Integer.parseInt(result), Matchers.equalTo(1));
        // check that legend URI are correctly encoded in the context of a local workspace
        WorkspaceInfo workspace = getCatalog().getWorkspaceByName(GWCIntegrationTest.TEST_WORKSPACE_NAME);
        Assert.assertThat(workspace, Matchers.notNullValue());
        LocalWorkspace.set(workspace);
        try {
            response = getAsServletResponse(((GWCIntegrationTest.TEST_WORKSPACE_NAME) + "/gwc/service/wmts?request=GetCapabilities"));
            document = dom(response, false);
            result = GWCIntegrationTest.WMTS_XPATH_10.evaluate(((("count(//wmts:Contents/wmts:Layer/wmts:Style/" + "wmts:LegendURL[contains(@xlink:href,'geoserver/") + (GWCIntegrationTest.TEST_WORKSPACE_NAME)) + "/ows')])"), document);
            Assert.assertThat(Integer.parseInt(result), Matchers.greaterThan(0));
        } finally {
            // make sure we remove the local workspace
            LocalWorkspace.set(null);
        }
    }

    @Test
    public void testGetCapabilitiesRequestRestEndpoints() throws Exception {
        int totLayers = (getCatalog().getLayers().size()) + 1;// one cached layer group

        // getting capabilities document for CITE workspace
        Document doc = getAsDOM("/gwc/service/wmts?request=GetCapabilities");
        // checking ResourceURL
        Assert.assertEquals(String.valueOf(totLayers), GWCIntegrationTest.WMTS_XPATH_10.evaluate((((("count(//wmts:Contents/wmts:Layer/wmts:ResourceURL[@resourceType='tile']" + ("[@format='image/png']" + "[contains(@template,'http://localhost:8080/geoserver/gwc")) + (WMTSService.REST_PATH)) + "/')]") + "[contains(@template,'/{style}/{TileMatrixSet}/{TileMatrix}/{TileRow}/{TileCol}?format=image/png')])"), doc));
        Assert.assertEquals(String.valueOf(totLayers), GWCIntegrationTest.WMTS_XPATH_10.evaluate((((("count(//wmts:Contents/wmts:Layer/wmts:ResourceURL[@resourceType='FeatureInfo']" + ("[@format='text/plain']" + "[contains(@template,'http://localhost:8080/geoserver/gwc")) + (WMTSService.REST_PATH)) + "/')]") + "[contains(@template,'/{style}/{TileMatrixSet}/{TileMatrix}/{TileRow}/{TileCol}/{J}/{I}?format=text/plain')])"), doc));
        // checking the service metadata URL
        Assert.assertEquals("1", GWCIntegrationTest.WMTS_XPATH_10.evaluate((("count(//wmts:ServiceMetadataURL[@xlink:href='http://localhost:8080/geoserver/gwc" + (WMTSService.SERVICE_PATH)) + "?SERVICE=wmts&REQUEST=getcapabilities&VERSION=1.0.0'])"), doc));
        Assert.assertEquals("1", GWCIntegrationTest.WMTS_XPATH_10.evaluate((("count(//wmts:ServiceMetadataURL[@xlink:href='http://localhost:8080/geoserver/gwc" + (WMTSService.REST_PATH)) + "/WMTSCapabilities.xml'])"), doc));
    }

    @Test
    public void testGetCapabilitiesWithRestEndpoints() throws Exception {
        MockHttpServletRequest request = createRequest((("/gwc" + (WMTSService.REST_PATH)) + "/WMTSCapabilities.xml"));
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        // check that the request was successful
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        assertContentType("text/xml;charset=UTF-8", response);
    }

    @Test
    public void testGetCapabilitiesWithRestEndpointsWorkspaceService() throws Exception {
        MockHttpServletRequest request = createRequest(((((MockData.CITE_PREFIX) + "/gwc") + (WMTSService.REST_PATH)) + "/WMTSCapabilities.xml"));
        request.setMethod("GET");
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        Document doc = dom(new ByteArrayInputStream(response.getContentAsByteArray()), true);
        print(doc);
        // check legend backlink and that it has the workspace specification
        Assert.assertEquals("http://localhost:8080/geoserver/cite/ows?service=WMS&request=GetLegendGraphic&format=image%2Fpng&width=20&height=20&layer=cite%3ABasicPolygons", GWCIntegrationTest.WMTS_XPATH_10.evaluate("//wmts:Contents/wmts:Layer[ows:Title='BasicPolygons']/wmts:Style/wmts:LegendURL/@xlink:href", doc));
        // check tile resources
        Assert.assertEquals("http://localhost:8080/geoserver/cite/gwc/service/wmts/rest/BasicPolygons/{style}/{TileMatrixSet}/{TileMatrix}/{TileRow}/{TileCol}?format=image/png", GWCIntegrationTest.WMTS_XPATH_10.evaluate("//wmts:Contents/wmts:Layer[ows:Title='BasicPolygons']/wmts:ResourceURL[@format='image/png' and @resourceType='tile']/@template", doc));
        // check featureinfo resource
        Assert.assertEquals("http://localhost:8080/geoserver/cite/gwc/service/wmts/rest/BasicPolygons/{style}/{TileMatrixSet}/{TileMatrix}/{TileRow}/{TileCol}/{J}/{I}?format=text/plain", GWCIntegrationTest.WMTS_XPATH_10.evaluate("//wmts:Contents/wmts:Layer[ows:Title='BasicPolygons']/wmts:ResourceURL[@format='text/plain' and @resourceType='FeatureInfo']/@template", doc));
        // check backlink too
        Assert.assertEquals("1", GWCIntegrationTest.WMTS_XPATH_10.evaluate("count(//wmts:Capabilities/wmts:ServiceMetadataURL[@xlink:href='http://localhost:8080/geoserver/cite/gwc/service/wmts/rest/WMTSCapabilities.xml'])", doc));
    }

    @Test
    public void testGetTileWithRestEndpoints() throws Exception {
        MockHttpServletRequest request = createRequest((((((("/gwc" + (WMTSService.REST_PATH)) + "/") + (MockData.BASIC_POLYGONS.getPrefix())) + ":") + (MockData.BASIC_POLYGONS.getLocalPart())) + "/EPSG:4326/EPSG:4326:0/0/0?format=image/png"));
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        // check that the request was successful
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        assertContentType("image/png", response);
    }

    @Test
    public void testGetTileWithRestEndpointsInVirtualService() throws Exception {
        // get tile
        MockHttpServletRequest request = createRequest(((((((MockData.BASIC_POLYGONS.getPrefix()) + "/gwc") + (WMTSService.REST_PATH)) + "/") + (MockData.BASIC_POLYGONS.getLocalPart())) + "/EPSG:4326/EPSG:4326:0/0/0?format=image/png"));
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        // mock the request
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        // check that the request was successful
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        assertContentType("image/png", response);
    }

    @Test
    public void testFeatureInfoWithRestEndpoints() throws Exception {
        // get feature info
        MockHttpServletRequest request = createRequest((((((("/gwc" + (WMTSService.REST_PATH)) + "/") + (MockData.BASIC_POLYGONS.getPrefix())) + ":") + (MockData.BASIC_POLYGONS.getLocalPart())) + "/EPSG:4326/EPSG:4326:0/0/0/0/0?format=text/plain"));
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        // mock the request
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        // check that the request was successful
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        assertContentType("text/plain", response);
    }

    @Test
    public void testFeatureInfoWithRestEndpointsInVirtualService() throws Exception {
        // getting feature info
        MockHttpServletRequest request = createRequest(((((((((MockData.BASIC_POLYGONS.getPrefix()) + "/gwc") + (WMTSService.REST_PATH)) + "/") + (MockData.BASIC_POLYGONS.getPrefix())) + ":") + (MockData.BASIC_POLYGONS.getLocalPart())) + "/EPSG:4326/EPSG:4326:0/0/0/0/0?format=text/plain"));
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        // mock the request
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        // check that the request was successful
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        assertContentType("text/plain", response);
    }

    @Test
    public void testGetCapabilitiesWithRestEndpointsAndDimensions() throws Exception {
        GeoServerTileLayer tileLayer = ((GeoServerTileLayer) (GWC.get().getTileLayerByName(getLayerId(MockData.BASIC_POLYGONS))));
        GeoServerTileLayerInfo info = tileLayer.getInfo();
        // Add dimensions to layer
        StringParameterFilter time = new StringParameterFilter();
        time.setKey("time");
        time.setValues(Arrays.asList("2016-02-23T03:00:00.000Z"));
        info.addParameterFilter(time);
        StringParameterFilter elevation = new StringParameterFilter();
        elevation.setKey("elevation");
        elevation.setValues(Arrays.asList("500"));
        info.addParameterFilter(elevation);
        GWC.get().save(tileLayer);
        MockHttpServletRequest request = createRequest((("/gwc" + (WMTSService.REST_PATH)) + "/WMTSCapabilities.xml"));
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        Request mockRequest = Mockito.mock(Request.class);
        Mockito.when(mockRequest.getHttpRequest()).thenReturn(request);
        REQUEST.set(mockRequest);
        MockHttpServletResponse response = dispatch(request, null);
        ByteArrayInputStream bain = new ByteArrayInputStream(response.getContentAsString().getBytes());
        Document doc = dom(bain, true);
        Assert.assertEquals("1", GWCIntegrationTest.WMTS_XPATH_10.evaluate(((((((("count(//wmts:Contents/wmts:Layer/wmts:ResourceURL[@resourceType='tile']" + ("[@format='image/png']" + "[contains(@template,'http://localhost:8080/geoserver/gwc")) + (WMTSService.REST_PATH)) + "/") + (MockData.BASIC_POLYGONS.getPrefix())) + ":") + (MockData.BASIC_POLYGONS.getLocalPart())) + "/{style}/{TileMatrixSet}/{TileMatrix}/{TileRow}/{TileCol}?format=image/png')])"), doc));
    }

    /**
     * Test that using an invalid \ non-existing style in a GetTile request will throw the correct
     * OWS exception.
     */
    @Test
    public void testCetTileWithInvalidStyle() throws Exception {
        // using cite:BasicPolygons layer for testing
        String layerName = getLayerId(MockData.BASIC_POLYGONS);
        // get tile request with an invalid style, this should return an exception report
        MockHttpServletResponse response = getAsServletResponse((((("gwc/service/wmts" + "?request=GetTile&layer=") + layerName) + "&style=invalid&format=image/png") + "&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0"));
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("text/xml", response.getContentType());
        // parse XML response content
        Document document = dom(response, false);
        // let's check the content of the exception report
        String result = GWCIntegrationTest.WMTS_XPATH_10.evaluate(("count(//ows:ExceptionReport/ows:Exception" + "[@exceptionCode='InvalidParameterValue'][@locator='Style'])"), document);
        Assert.assertThat(Integer.parseInt(result), Matchers.is(1));
    }
}

