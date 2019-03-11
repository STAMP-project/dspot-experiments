/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;


import CacheResult.MISS;
import GeoServerTileLayer.WEB_MAP;
import GwcServiceDispatcherCallback.GWC_OPERATION;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.Keyword;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.LayerGroupInfoImpl;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.catalog.impl.NamespaceInfoImpl;
import org.geoserver.catalog.impl.StyleInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.wms.GetLegendGraphicOutputFormat;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.capabilities.LegendSample;
import org.geoserver.wms.map.RenderedImageMap;
import org.geoserver.wms.map.RenderedImageMapResponse;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.XMLGridSubset;
import org.geowebcache.config.legends.LegendInfo;
import org.geowebcache.conveyor.ConveyorTile;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.grid.GridSubset;
import org.geowebcache.grid.OutsideCoverageException;
import org.geowebcache.io.Resource;
import org.geowebcache.layer.ExpirationRule;
import org.geowebcache.layer.meta.LayerMetaInformation;
import org.geowebcache.layer.meta.MetadataURL;
import org.geowebcache.mime.MimeType;
import org.geowebcache.storage.StorageBroker;
import org.geowebcache.storage.TileObject;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertThat;


public class GeoServerTileLayerTest {
    private LayerInfoImpl layerInfo;

    private GeoServerTileLayer layerInfoTileLayer;

    private LayerGroupInfoImpl layerGroup;

    private GeoServerTileLayer layerGroupInfoTileLayer;

    private Catalog catalog;

    private GridSetBroker gridSetBroker;

    private GWCConfig defaults;

    private GWC mockGWC;

    private FeatureTypeInfoImpl resource;

    @Test
    public void testEnabled() {
        layerInfo.setEnabled(true);
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertTrue(layerInfoTileLayer.isEnabled());
        layerInfo.setEnabled(false);
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertFalse(layerInfoTileLayer.isEnabled());
        layerInfo.setEnabled(true);
        layerInfoTileLayer.setEnabled(true);
        Assert.assertTrue(layerInfoTileLayer.isEnabled());
        Assert.assertTrue(layerInfoTileLayer.getInfo().isEnabled());
        layerInfoTileLayer.setConfigErrorMessage("fake error message");
        Assert.assertFalse(layerInfoTileLayer.isEnabled());
        layerInfoTileLayer.setConfigErrorMessage(null);
        layerInfoTileLayer.setEnabled(false);
        Assert.assertFalse(layerInfoTileLayer.isEnabled());
        Assert.assertFalse(layerInfoTileLayer.getInfo().isEnabled());
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        Assert.assertTrue(layerGroupInfoTileLayer.isEnabled());
    }

    @Test
    public void testGetMetaTilingFactors() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        int[] metaTilingFactors = layerInfoTileLayer.getMetaTilingFactors();
        Assert.assertEquals(defaults.getMetaTilingX(), metaTilingFactors[0]);
        Assert.assertEquals(defaults.getMetaTilingY(), metaTilingFactors[1]);
        GeoServerTileLayerInfo info = layerInfoTileLayer.getInfo();
        info.setMetaTilingX((1 + (defaults.getMetaTilingX())));
        info.setMetaTilingY((2 + (defaults.getMetaTilingY())));
        LegacyTileLayerInfoLoader.save(info, layerInfo.getMetadata());
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        metaTilingFactors = layerInfoTileLayer.getMetaTilingFactors();
        Assert.assertEquals((1 + (defaults.getMetaTilingX())), metaTilingFactors[0]);
        Assert.assertEquals((2 + (defaults.getMetaTilingY())), metaTilingFactors[1]);
    }

    @Test
    public void testIsQueryable() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Mockito.when(mockGWC.isQueryable(ArgumentMatchers.same(layerInfoTileLayer))).thenReturn(true);
        Assert.assertTrue(layerInfoTileLayer.isQueryable());
        Mockito.when(mockGWC.isQueryable(ArgumentMatchers.same(layerInfoTileLayer))).thenReturn(false);
        Assert.assertFalse(layerInfoTileLayer.isQueryable());
        Mockito.verify(mockGWC, Mockito.times(2)).isQueryable(ArgumentMatchers.same(layerInfoTileLayer));
    }

    @Test
    public void testGetName() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertEquals(GWC.tileLayerName(layerInfo), layerInfoTileLayer.getName());
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        Assert.assertEquals(GWC.tileLayerName(layerGroup), layerGroupInfoTileLayer.getName());
    }

    @Test
    public void testGetParameterFilters() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        List<ParameterFilter> parameterFilters = layerInfoTileLayer.getParameterFilters();
        Assert.assertNotNull(parameterFilters);
        Assert.assertEquals(1, parameterFilters.size());
        Assert.assertTrue(((parameterFilters.get(0)) instanceof StyleParameterFilter));
        StyleParameterFilter styleFilter = ((StyleParameterFilter) (parameterFilters.get(0)));
        Assert.assertEquals("STYLES", styleFilter.getKey());
        Assert.assertEquals("default_style", styleFilter.getDefaultValue());
        Assert.assertEquals(new HashSet(Arrays.asList("default_style", "alternateStyle-1", "alternateStyle-2")), new HashSet(styleFilter.getLegalValues()));
        // layerInfoTileLayer.getInfo().getCachedStyles().add("alternateStyle-2");
    }

    @Test
    public void testGetDefaultParameterFilters() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Map<String, String> defaultFilters = layerInfoTileLayer.getDefaultParameterFilters();
        Assert.assertEquals(1, defaultFilters.size());
        Assert.assertEquals("default_style", defaultFilters.get("STYLES"));
    }

    // public void testResetParameterFilters() {
    // 
    // layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
    // 
    // layerInfoTileLayer.getInfo().cachedStyles().clear();
    // layerInfoTileLayer.getInfo().cachedStyles().add("alternateStyle-2");
    // 
    // layerInfoTileLayer.resetParameterFilters();
    // List<ParameterFilter> parameterFilters = layerInfoTileLayer.getParameterFilters();
    // StringParameterFilter styleFilter = (StringParameterFilter) parameterFilters.get(0);
    // assertEquals(new HashSet<String>(Arrays.asList("default_style", "alternateStyle-2")),
    // new HashSet<String>(styleFilter.getLegalValues()));
    // 
    // }
    @Test
    public void testGetModifiableParameters() throws GeoWebCacheException {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        ParameterFilter stylesParamFilter = layerInfoTileLayer.getParameterFilters().get(0);
        List<String> legalValues = stylesParamFilter.getLegalValues();
        Map<String, String> requestParams;
        Map<String, String> modifiedParams;
        requestParams = Collections.singletonMap("sTyLeS", "");
        modifiedParams = layerInfoTileLayer.getModifiableParameters(requestParams, "UTF-8");
        Assert.assertEquals(0, modifiedParams.size());
        for (String legalStyle : legalValues) {
            requestParams = new HashMap<String, String>();
            requestParams.put("sTyLeS", legalStyle);
            modifiedParams = layerInfoTileLayer.getModifiableParameters(requestParams, "UTF-8");
            if (legalStyle.equals(stylesParamFilter.getDefaultValue())) {
                Assert.assertEquals(0, modifiedParams.size());
            } else {
                Assert.assertEquals(Collections.singletonMap("STYLES", legalStyle), modifiedParams);
            }
        }
    }

    @Test
    public void testGetMetaInformation() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        LayerMetaInformation metaInformation = layerInfoTileLayer.getMetaInformation();
        Assert.assertNotNull(metaInformation);
        String title = metaInformation.getTitle();
        String description = metaInformation.getDescription();
        List<String> keywords = metaInformation.getKeywords();
        Assert.assertEquals(layerInfo.getResource().getTitle(), title);
        Assert.assertEquals(layerInfo.getResource().getAbstract(), description);
        Assert.assertEquals(layerInfo.getResource().getKeywords().size(), keywords.size());
        for (String kw : keywords) {
            Assert.assertTrue(layerInfo.getResource().getKeywords().contains(new Keyword(kw)));
        }
        metaInformation = layerGroupInfoTileLayer.getMetaInformation();
        Assert.assertNotNull(metaInformation);
        title = metaInformation.getTitle();
        description = metaInformation.getDescription();
        keywords = metaInformation.getKeywords();
        // these properties are missing from LayerGroupInfo interface
        Assert.assertEquals("Group title", title);
        Assert.assertEquals("Group abstract", description);
        Assert.assertEquals(0, keywords.size());
    }

    @Test
    public void testGetStyles() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        Assert.assertEquals("default_style", layerInfoTileLayer.getStyles());
        Assert.assertNull(layerGroupInfoTileLayer.getStyles());
        StyleInfo newDefaultStyle = new StyleInfoImpl(null);
        newDefaultStyle.setName("newDefault");
        layerInfo.setDefaultStyle(newDefaultStyle);
        Assert.assertEquals("newDefault", layerInfoTileLayer.getStyles());
    }

    @Test
    public void testGetGridSubsets() throws Exception {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Set<String> gridSubsets = layerInfoTileLayer.getGridSubsets();
        Assert.assertNotNull(gridSubsets);
        Assert.assertEquals(2, gridSubsets.size());
        Set<XMLGridSubset> subsets = layerInfoTileLayer.getInfo().getGridSubsets();
        subsets.clear();
        XMLGridSubset xmlGridSubset = new XMLGridSubset();
        xmlGridSubset.setGridSetName("EPSG:900913");
        subsets.add(xmlGridSubset);
        LegacyTileLayerInfoLoader.save(layerInfoTileLayer.getInfo(), layerInfo.getMetadata());
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        gridSubsets = layerInfoTileLayer.getGridSubsets();
        Assert.assertNotNull(gridSubsets);
        Assert.assertEquals(1, gridSubsets.size());
        layerGroup.setBounds(layerInfo.getResource().getLatLonBoundingBox());
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        gridSubsets = layerGroupInfoTileLayer.getGridSubsets();
        Assert.assertNotNull(gridSubsets);
        Assert.assertEquals(2, gridSubsets.size());
    }

    @Test
    public void testGetGridSubsetsDynamic() throws Exception {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        GridSubset subset = layerInfoTileLayer.getGridSubset("EPSG:4326");
        assertThat(subset, Matchers.instanceOf(DynamicGridSubset.class));
        assertThat(subset, Matchers.hasProperty("originalExtent", Matchers.hasProperty("minX", Matchers.closeTo((-180.0), 1.0E-7))));
        layerInfoTileLayer.removeGridSubset("EPSG:4326");
        layerInfoTileLayer.addGridSubset(subset);
        resource.setLatLonBoundingBox(new ReferencedEnvelope((-90), (-90), 0, 0, DefaultGeographicCRS.WGS84));
        resource.setNativeBoundingBox(new ReferencedEnvelope((-90), (-90), 0, 0, DefaultGeographicCRS.WGS84));
        GridSubset subset2 = layerInfoTileLayer.getGridSubset("EPSG:4326");
        // the extent should be that of resource
        assertThat(subset2, Matchers.hasProperty("originalExtent", Matchers.hasProperty("minX", Matchers.closeTo((-90.0), 1.0E-7))));
    }

    @Test
    public void testGetGridSubsetsStatic() throws Exception {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        GridSubset subset = layerInfoTileLayer.getGridSubset("EPSG:4326");
        assertThat(subset, Matchers.instanceOf(DynamicGridSubset.class));
        assertThat(subset, Matchers.hasProperty("originalExtent", Matchers.hasProperty("minX", Matchers.closeTo((-180.0), 1.0E-7))));
        layerInfoTileLayer.removeGridSubset("EPSG:4326");
        layerInfoTileLayer.addGridSubset(new GridSubset(subset));// Makes the dynamic extent static

        resource.setLatLonBoundingBox(new ReferencedEnvelope((-90), (-90), 0, 0, DefaultGeographicCRS.WGS84));
        resource.setNativeBoundingBox(new ReferencedEnvelope((-90), (-90), 0, 0, DefaultGeographicCRS.WGS84));
        GridSubset subset2 = layerInfoTileLayer.getGridSubset("EPSG:4326");
        // the extent should not change with that of resource
        assertThat(subset2, Matchers.hasProperty("originalExtent", Matchers.hasProperty("minX", Matchers.closeTo((-180.0), 1.0E-7))));
    }

    @Test
    public void testGridSubsetBoundsClippedToTargetCrsAreaOfValidity() throws Exception {
        CoordinateReferenceSystem nativeCrs = CRS.decode("EPSG:4326", true);
        ReferencedEnvelope nativeBounds = new ReferencedEnvelope((-180), 180, (-90), 90, nativeCrs);
        layerGroup.setBounds(nativeBounds);
        defaults.getDefaultCachingGridSetIds().clear();
        defaults.getDefaultCachingGridSetIds().add("EPSG:900913");
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        // force building and setting the bounds to the saved representation
        layerGroupInfoTileLayer.getGridSubsets();
        XMLGridSubset savedSubset = layerGroupInfoTileLayer.getInfo().getGridSubsets().iterator().next();
        BoundingBox gridSubsetExtent = savedSubset.getExtent();
        BoundingBox expected = gridSetBroker.getWorldEpsg3857().getOriginalExtent();
        // don't use equals(), it uses an equality threshold we want to avoid here
        double threshold = 1.0E-16;
        Assert.assertTrue(((("Expected " + expected) + ", got ") + gridSubsetExtent), expected.equals(gridSubsetExtent, threshold));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetFeatureInfo() throws Exception {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        ConveyorTile convTile = new ConveyorTile(null, null, null, null);
        convTile.setTileLayer(layerInfoTileLayer);
        convTile.setMimeType(MimeType.createFromFormat("image/png"));
        convTile.setGridSetId("EPSG:4326");
        convTile.servletReq = new MockHttpServletRequest();
        BoundingBox bbox = new BoundingBox(0, 0, 10, 10);
        Resource mockResult = Mockito.mock(Resource.class);
        ArgumentCaptor<Map> argument = ArgumentCaptor.forClass(Map.class);
        Mockito.when(mockGWC.dispatchOwsRequest(argument.capture(), ((Cookie[]) (ArgumentMatchers.anyObject())))).thenReturn(mockResult);
        Resource result = layerInfoTileLayer.getFeatureInfo(convTile, bbox, 100, 100, 50, 50);
        Assert.assertSame(mockResult, result);
        final Map<String, String> capturedParams = argument.getValue();
        Assert.assertEquals("image/png", capturedParams.get("INFO_FORMAT"));
        Assert.assertEquals("0.0,0.0,10.0,10.0", capturedParams.get("BBOX"));
        Assert.assertEquals("test:MockLayerInfoName", capturedParams.get("QUERY_LAYERS"));
        Assert.assertEquals("WMS", capturedParams.get("SERVICE"));
        Assert.assertEquals("100", capturedParams.get("HEIGHT"));
        Assert.assertEquals("100", capturedParams.get("WIDTH"));
        Assert.assertEquals("GetFeatureInfo", capturedParams.get("REQUEST"));
        Assert.assertEquals("default_style", capturedParams.get("STYLES"));
        Assert.assertEquals("SE_XML", capturedParams.get("EXCEPTIONS"));
        Assert.assertEquals("1.1.1", capturedParams.get("VERSION"));
        Assert.assertEquals("image/png", capturedParams.get("FORMAT"));
        Assert.assertEquals("test:MockLayerInfoName", capturedParams.get("LAYERS"));
        Assert.assertEquals("EPSG:4326", capturedParams.get("SRS"));
        Assert.assertEquals("50", capturedParams.get("X"));
        Assert.assertEquals("50", capturedParams.get("Y"));
        Mockito.verify(mockGWC, Mockito.times(1)).dispatchOwsRequest(((Map) (ArgumentMatchers.anyObject())), ((Cookie[]) (ArgumentMatchers.anyObject())));
        Mockito.when(mockGWC.dispatchOwsRequest(((Map) (ArgumentMatchers.anyObject())), ((Cookie[]) (ArgumentMatchers.anyObject())))).thenThrow(new RuntimeException("mock exception"));
        try {
            layerInfoTileLayer.getFeatureInfo(convTile, bbox, 100, 100, 50, 50);
            Assert.fail("Expected GeoWebCacheException");
        } catch (GeoWebCacheException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetTilePreconditions() throws Exception {
        StorageBroker storageBroker = Mockito.mock(StorageBroker.class);
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        MockHttpServletRequest servletReq = new MockHttpServletRequest();
        HttpServletResponse servletResp = new MockHttpServletResponse();
        ConveyorTile tile = new ConveyorTile(storageBroker, layerInfoTileLayer.getName(), servletReq, servletResp);
        tile.setMimeType(MimeType.createFromFormat("image/gif"));
        try {
            layerInfoTileLayer.getTile(tile);
            Assert.fail("Expected exception, requested mime is invalid for the layer");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("is not a supported format"));
        }
        tile.setMimeType(MimeType.createFromFormat("image/png"));
        tile.setGridSetId("EPSG:2003");
        try {
            layerInfoTileLayer.getTile(tile);
            Assert.fail("Expected IAE");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("gridset not found"));
        }
        // layer bounds (in WGS84) is -180, -90, 0, 0
        long[][] outsideTiles = new long[][]{ new long[]{ 0, 1, 1 }, new long[]{ 1, 1, 1 }, new long[]{ 1, 0, 1 } };
        for (long[] tileIndex : outsideTiles) {
            MimeType mimeType = MimeType.createFromFormat("image/png");
            tile = new ConveyorTile(storageBroker, layerInfoTileLayer.getName(), "EPSG:900913", tileIndex, mimeType, null, servletReq, servletResp);
            try {
                layerInfoTileLayer.getTile(tile);
                Assert.fail("Expected outside coverage exception");
            } catch (OutsideCoverageException e) {
                Assert.assertTrue(true);
            }
        }
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetTile() throws Exception {
        Resource mockResult = Mockito.mock(Resource.class);
        ArgumentCaptor<Map> argument = ArgumentCaptor.forClass(Map.class);
        Mockito.when(mockGWC.dispatchOwsRequest(argument.capture(), ((Cookie[]) (ArgumentMatchers.anyObject())))).thenReturn(mockResult);
        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        RenderedImageMap fakeDispatchedMap = new RenderedImageMap(new WMSMapContent(), image, "image/png");
        RenderedImageMapResponse fakeResponseEncoder = Mockito.mock(RenderedImageMapResponse.class);
        MimeType mimeType = MimeType.createFromFormat("image/png");
        Mockito.when(mockGWC.getResponseEncoder(ArgumentMatchers.eq(mimeType), ((RenderedImageMap) (ArgumentMatchers.anyObject())))).thenReturn(fakeResponseEncoder);
        StorageBroker storageBroker = Mockito.mock(StorageBroker.class);
        Mockito.when(storageBroker.get(((TileObject) (ArgumentMatchers.anyObject())))).thenReturn(false);
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        MockHttpServletRequest servletReq = new MockHttpServletRequest();
        HttpServletResponse servletResp = new MockHttpServletResponse();
        long[] tileIndex = new long[]{ 0, 0, 0 };
        ConveyorTile tile = new ConveyorTile(storageBroker, layerInfoTileLayer.getName(), "EPSG:4326", tileIndex, mimeType, null, servletReq, servletResp);
        WEB_MAP.set(fakeDispatchedMap);
        ConveyorTile returned = layerInfoTileLayer.getTile(tile);
        Assert.assertNotNull(returned);
        Assert.assertNotNull(returned.getBlob());
        Assert.assertEquals(MISS, returned.getCacheResult());
        Assert.assertEquals(200, returned.getStatus());
        Mockito.verify(storageBroker, Mockito.atLeastOnce()).get(((TileObject) (ArgumentMatchers.anyObject())));
        Mockito.verify(mockGWC, Mockito.times(1)).getResponseEncoder(ArgumentMatchers.eq(mimeType), ArgumentMatchers.isA(RenderedImageMap.class));
    }

    @Test
    public void testGetMimeTypes() throws Exception {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        List<MimeType> mimeTypes = layerInfoTileLayer.getMimeTypes();
        Assert.assertEquals(defaults.getDefaultOtherCacheFormats().size(), mimeTypes.size());
        layerInfoTileLayer.getInfo().getMimeFormats().clear();
        layerInfoTileLayer.getInfo().getMimeFormats().add("image/gif");
        mimeTypes = layerInfoTileLayer.getMimeTypes();
        Assert.assertEquals(1, mimeTypes.size());
        Assert.assertEquals(MimeType.createFromFormat("image/gif"), mimeTypes.get(0));
    }

    @Test
    public void testTileExpirationList() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        List<ExpirationRule> list = new ArrayList<ExpirationRule>();
        list.add(new ExpirationRule(0, 10));
        list.add(new ExpirationRule(10, 20));
        layerInfoTileLayer.getInfo().setExpireCacheList(list);
        Assert.assertEquals(10, layerInfoTileLayer.getExpireCache(0));
        Assert.assertEquals(10, layerInfoTileLayer.getExpireCache(9));
        Assert.assertEquals(20, layerInfoTileLayer.getExpireCache(10));
        Assert.assertEquals(20, layerInfoTileLayer.getExpireCache(15));
        Assert.assertEquals(0, layerInfoTileLayer.getExpireCache((-1)));
    }

    @Test
    public void testCacheExpiration() {
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertEquals(0, layerInfoTileLayer.getInfo().getExpireCache());
        layerInfoTileLayer.getInfo().setExpireCache(40);
        Assert.assertEquals(40, layerInfoTileLayer.getInfo().getExpireCache());
    }

    @Test
    public void testAdvertised() {
        // Testing the advertised parameter
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertTrue(layerInfoTileLayer.isAdvertised());
    }

    @Test
    public void testTransient() {
        // Testing the transient parameter
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertFalse(layerInfoTileLayer.isTransientLayer());
    }

    @Test
    public void testGetPublishedInfo() {
        // Checking that the getLayerInfo and getLayerGroupInfo methods
        // returns a not null object
        layerInfoTileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        Assert.assertNotNull(layerInfoTileLayer.getLayerInfo());
        Assert.assertNull(layerInfoTileLayer.getLayerGroupInfo());
        layerGroupInfoTileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        Assert.assertNull(layerGroupInfoTileLayer.getLayerInfo());
        Assert.assertNotNull(layerGroupInfoTileLayer.getLayerGroupInfo());
    }

    @Test
    public void testGetLayerNameForGetCapabilitiesRequest() throws IllegalAccessException, NoSuchFieldException {
        // workspace namespace
        NamespaceInfo nameSpaceA = new NamespaceInfoImpl();
        nameSpaceA.setPrefix("workspace-a");
        nameSpaceA.setURI("http://goserver.org/test");
        // create the workspace
        WorkspaceInfo workspaceA = new WorkspaceInfoImpl();
        workspaceA.setName("workspace-a");
        // register the workspace in catalog
        Mockito.when(catalog.getWorkspaceByName("workspace-a")).thenReturn(workspaceA);
        // layer resource
        FeatureTypeInfoImpl resourceA = new FeatureTypeInfoImpl(null);
        resourceA.setNamespace(nameSpaceA);
        // create the layer
        LayerInfoImpl layerA = new LayerInfoImpl();
        layerA.setResource(resourceA);
        layerA.setName("layer-a");
        layerA.setId("layer-a");
        // register the layer in catalog
        Mockito.when(catalog.getLayer("layer-a")).thenReturn(layerA);
        // creating a layer group without workspace
        LayerGroupInfoImpl layerGroupA = new LayerGroupInfoImpl();
        layerGroupA.setName("random-prefix:layer-group-a");
        layerGroupA.setId("layer-group-a");
        layerGroupA.setLayers(Collections.singletonList(layerA));
        // register the layer group in catalog
        Mockito.when(catalog.getLayerGroup("layer-group-a")).thenReturn(layerGroupA);
        // creating the tiled layers
        GeoServerTileLayer tileLayerA = new GeoServerTileLayer(layerA, defaults, gridSetBroker);
        GeoServerTileLayer tileLayerB = new GeoServerTileLayer(layerGroupA, defaults, gridSetBroker);
        // setting the catalog in both tile layers using reflection
        Field catalogField = GeoServerTileLayer.class.getDeclaredField("catalog");
        catalogField.setAccessible(true);
        catalogField.set(tileLayerA, catalog);
        catalogField.set(tileLayerB, catalog);
        // no local workspace, no gwc operation
        GWC_OPERATION.remove();
        assertThat(tileLayerA.getName(), Matchers.is("workspace-a:layer-a"));
        assertThat(tileLayerB.getName(), Matchers.is("random-prefix:layer-group-a"));
        // no local workspace, some gwc operation
        GWC_OPERATION.set("some-operation");
        assertThat(tileLayerA.getName(), Matchers.is("workspace-a:layer-a"));
        assertThat(tileLayerB.getName(), Matchers.is("random-prefix:layer-group-a"));
        // no local workspace, get capabilities gwc operation
        GWC_OPERATION.set("GetCapabilities");
        assertThat(tileLayerA.getName(), Matchers.is("workspace-a:layer-a"));
        assertThat(tileLayerB.getName(), Matchers.is("random-prefix:layer-group-a"));
        try {
            // setting a local workspace (workspace-a)
            LocalWorkspace.set(workspaceA);
            // local workspace, no gwc operation
            GWC_OPERATION.remove();
            assertThat(tileLayerA.getName(), Matchers.is("workspace-a:layer-a"));
            assertThat(tileLayerB.getName(), Matchers.is("random-prefix:layer-group-a"));
            // local workspace, some gwc operation
            GWC_OPERATION.set("some-operation");
            assertThat(tileLayerA.getName(), Matchers.is("workspace-a:layer-a"));
            assertThat(tileLayerB.getName(), Matchers.is("random-prefix:layer-group-a"));
            // local workspace, get capabilities gwc operation
            GWC_OPERATION.set("GetCapabilities");
            assertThat(tileLayerA.getName(), Matchers.is("layer-a"));
            assertThat(tileLayerB.getName(), Matchers.is("random-prefix:layer-group-a"));
        } finally {
            // cleaning
            LocalWorkspace.remove();
        }
    }

    @Test
    public void testGetMetadataUrlsFromLayer() throws MalformedURLException {
        setupUrlContext();
        // create a tile layer using a layer
        GeoServerTileLayer tileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        List<MetadataURL> metadata = tileLayer.getMetadataURLs();
        assertThat(metadata.size(), Matchers.is(1));
        assertThat(metadata.get(0).getType(), Matchers.is("metadata-type"));
        assertThat(metadata.get(0).getFormat(), Matchers.is("metadata-format"));
        assertThat(metadata.get(0).getUrl(), Matchers.is(new URL("http://localhost:8080/geoserver/metadata-content")));
    }

    @Test
    public void testGetMetadataUrlsFromLayerGroup() throws MalformedURLException {
        setupUrlContext();
        // create a tile layer using a layer group
        GeoServerTileLayer tileLayer = new GeoServerTileLayer(layerGroup, defaults, gridSetBroker);
        List<MetadataURL> metadata = tileLayer.getMetadataURLs();
        assertThat(metadata.size(), Matchers.is(1));
        assertThat(metadata.get(0).getType(), Matchers.is("metadata-type"));
        assertThat(metadata.get(0).getFormat(), Matchers.is("metadata-format"));
        assertThat(metadata.get(0).getUrl(), Matchers.is(new URL("http://localhost:8080/geoserver/metadata-content")));
    }

    @Test
    public void testGetLegendsLayer() throws Exception {
        setupUrlContext();
        LegendSample legendSample = Mockito.mock(LegendSample.class);
        Mockito.when(legendSample.getLegendURLSize(ArgumentMatchers.any(StyleInfo.class))).thenReturn(new Dimension(120, 150));
        WMS wms = Mockito.mock(WMS.class);
        GetLegendGraphicOutputFormat outputFormat = Mockito.mock(GetLegendGraphicOutputFormat.class);
        Mockito.when(wms.getLegendGraphicOutputFormat("image/png")).thenReturn(outputFormat);
        GeoServerTileLayer tileLayer = new GeoServerTileLayer(layerInfo, defaults, gridSetBroker);
        tileLayer.setLegendSample(legendSample);
        tileLayer.setWms(wms);
        Map<String, LegendInfo> legendsInfo = tileLayer.getLayerLegendsInfo();
        assertThat(legendsInfo.size(), Matchers.is(3));
        // default_style
        assertThat(legendsInfo.get("default_style"), Matchers.notNullValue());
        assertThat(legendsInfo.get("default_style").getWidth(), Matchers.is(120));
        assertThat(legendsInfo.get("default_style").getHeight(), Matchers.is(150));
        assertThat(legendsInfo.get("default_style").getFormat(), Matchers.is("image/png"));
        assertThat(legendsInfo.get("default_style").getLegendUrl(), Matchers.is(("http://localhost:8080/geoserver/ows?service=" + "WMS&request=GetLegendGraphic&format=image%2Fpng&width=120&height=150&layer=workspace%3AMockLayerInfoName")));
        // alternateStyle-1
        assertThat(legendsInfo.get("alternateStyle-1"), Matchers.notNullValue());
        assertThat(legendsInfo.get("alternateStyle-1").getWidth(), Matchers.is(120));
        assertThat(legendsInfo.get("alternateStyle-1").getHeight(), Matchers.is(150));
        assertThat(legendsInfo.get("alternateStyle-1").getFormat(), Matchers.is("image/png"));
        assertThat(legendsInfo.get("alternateStyle-1").getLegendUrl(), Matchers.is(("http://localhost:8080/geoserver/ows?service" + "=WMS&request=GetLegendGraphic&format=image%2Fpng&width=120&height=150&layer=workspace%3AMockLayerInfoName&style=alternateStyle-1")));
        // alternateStyle-2
        assertThat(legendsInfo.get("alternateStyle-2"), Matchers.notNullValue());
        assertThat(legendsInfo.get("alternateStyle-2").getWidth(), Matchers.is(150));
        assertThat(legendsInfo.get("alternateStyle-2").getHeight(), Matchers.is(200));
        assertThat(legendsInfo.get("alternateStyle-2").getFormat(), Matchers.is("image/png"));
        assertThat(legendsInfo.get("alternateStyle-2").getLegendUrl().trim(), Matchers.is("http://localhost:8080/geoserver/some-url"));
    }
}

