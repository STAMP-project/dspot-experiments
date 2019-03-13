/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import CatalogBuilder.DEFAULT_SRS;
import DefaultGeographicCRS.WGS84;
import LayerGroupInfo.Mode.EO;
import ProjectionPolicy.FORCE_DECLARED;
import ProjectionPolicy.NONE;
import ProjectionPolicy.REPROJECT_TO_DECLARED;
import PublishedType.VECTOR;
import PublishedType.WMS;
import Query.ALL;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import javax.media.jai.ImageLayout;
import org.easymock.EasyMock;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageDimensionInfo;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.Keyword;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ProjectionPolicy;
import org.geoserver.catalog.ResourcePool;
import org.geoserver.catalog.TestHttpClientProvider;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WMTSLayerInfo;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.MockTestData;
import org.geoserver.test.GeoServerBaseTestSupport;
import org.geoserver.test.GeoServerMockTestSupport;
import org.geoserver.test.RemoteOWSTestSupport;
import org.geoserver.test.http.MockHttpClient;
import org.geoserver.test.http.MockHttpResponse;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.data.FeatureSource;
import org.geotools.data.ResourceInfo;
import org.geotools.feature.NameImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.builder.GridToEnvelopeMapper;
import org.geotools.referencing.operation.projection.MapProjection;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.geotools.util.factory.Hints;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.type.FeatureType;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;


public class CatalogBuilderTest extends GeoServerMockTestSupport {
    @Test
    public void testFeatureTypeNoSRS() throws Exception {
        // build a feature type (it's already in the catalog, but we just want to
        // check it's built as expected
        // LINES is a feature type with a native SRS, so we want the bounds to be there
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.BRIDGES.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.BRIDGES));
        // perform basic checks, this has no srs, so default one will be set
        Assert.assertEquals(DEFAULT_SRS, fti.getSRS());
        Assert.assertNull(fti.getNativeCRS());
        Assert.assertNull(fti.getNativeBoundingBox());
        Assert.assertNull(fti.getLatLonBoundingBox());
        // force bounds computation
        cb.setupBounds(fti);
        Assert.assertNotNull(fti.getNativeBoundingBox());
        Assert.assertNull(fti.getNativeBoundingBox().getCoordinateReferenceSystem());
        Assert.assertNotNull(fti.getLatLonBoundingBox());
    }

    @Test
    public void testGetBoundsFromCRS() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.LINES.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.LINES));
        CoordinateReferenceSystem resourceCRS = fti.getCRS();
        Assert.assertNotNull(resourceCRS);
        // make sure the srs is as expected, otherwise the rest of the tests don't make sense
        Assert.assertEquals("EPSG:32615", fti.getSRS());
        ReferencedEnvelope crsBounds = cb.getBoundsFromCRS(fti);
        Assert.assertNotNull(crsBounds);
        CoordinateReferenceSystem exptectedCRS = CRS.decode("EPSG:32615");
        Assert.assertEquals(new ReferencedEnvelope(CRS.getEnvelope(exptectedCRS)), crsBounds);
        // if we change the srs when there's no reproject policy, should still be the same bounding
        // box
        fti.setSRS("EPSG:4326");
        fti.setProjectionPolicy(NONE);
        crsBounds = cb.getBoundsFromCRS(fti);
        Assert.assertEquals(new ReferencedEnvelope(CRS.getEnvelope(exptectedCRS)), crsBounds);
        // if we use reproject policy, bounds should now be different
        fti.setProjectionPolicy(FORCE_DECLARED);
        crsBounds = cb.getBoundsFromCRS(fti);
        Assert.assertNotEquals(new ReferencedEnvelope(CRS.getEnvelope(exptectedCRS)), crsBounds);
        // should now be 4326 bounds
        CoordinateReferenceSystem crs4326 = CRS.decode("EPSG:4326");
        Assert.assertEquals(new ReferencedEnvelope(CRS.getEnvelope(crs4326)), crsBounds);
        fti.setProjectionPolicy(REPROJECT_TO_DECLARED);
        Assert.assertEquals(new ReferencedEnvelope(CRS.getEnvelope(crs4326)), crsBounds);
    }

    @Test
    public void testFeatureType() throws Exception {
        // build a feature type (it's already in the catalog, but we just want to
        // check it's built as expected
        // LINES is a feature type with a native SRS, so we want the bounds to be there
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.LINES.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.LINES));
        // perform basic checks
        Assert.assertEquals("EPSG:32615", fti.getSRS());
        Assert.assertEquals(CRS.decode("EPSG:32615", true), fti.getCRS());
        Assert.assertNull(fti.getNativeBoundingBox());
        Assert.assertNull(fti.getLatLonBoundingBox());
        // force bounds computation
        cb.setupBounds(fti);
        Assert.assertNotNull(fti.getNativeBoundingBox());
        Assert.assertNotNull(fti.getNativeBoundingBox().getCoordinateReferenceSystem());
        Assert.assertNotNull(fti.getLatLonBoundingBox());
    }

    @Test
    public void testGenericStyle() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.GENERICENTITY.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.GENERICENTITY));
        LayerInfo li = cb.buildLayer(fti);
        // check we assigned the generic style
        Assert.assertEquals("generic", li.getDefaultStyle().getName());
    }

    @Test
    public void testGeometryless() throws Exception {
        // build a feature type (it's already in the catalog, but we just want to
        // check it's built as expected
        // LINES is a feature type with a native SRS, so we want the bounds to be there
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.GEOMETRYLESS.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.GEOMETRYLESS));
        LayerInfo layer = cb.buildLayer(fti);
        cb.setupBounds(fti);
        // perform basic checks
        Assert.assertEquals(DEFAULT_SRS, fti.getSRS());
        Assert.assertNotNull(fti.getNativeBoundingBox());
        Assert.assertTrue(fti.getNativeBoundingBox().isEmpty());
        Assert.assertNotNull(fti.getLatLonBoundingBox());
        Assert.assertFalse(fti.getLatLonBoundingBox().isEmpty());
        Assert.assertNull(layer.getDefaultStyle());
    }

    @Test
    public void testSingleBandedCoverage() throws Exception {
        // build a feature type (it's already in the catalog, but we just want to
        // check it's built as expected
        // LINES is a feature type with a native SRS, so we want the bounds to be there
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getCoverageStoreByName(MockData.TASMANIA_DEM.getLocalPart()));
        CoverageInfo ci = cb.buildCoverage();
        // perform basic checks
        Assert.assertEquals(CRS.decode("EPSG:4326", true), ci.getCRS());
        Assert.assertEquals("EPSG:4326", ci.getSRS());
        Assert.assertNotNull(ci.getNativeCRS());
        Assert.assertNotNull(ci.getNativeBoundingBox());
        Assert.assertNotNull(ci.getLatLonBoundingBox());
        // check the coverage dimensions
        List<CoverageDimensionInfo> dimensions = ci.getDimensions();
        Assert.assertEquals(1, dimensions.size());
        CoverageDimensionInfo dimension = dimensions.get(0);
        Assert.assertEquals("GRAY_INDEX", dimension.getName());
        Assert.assertEquals(1, dimension.getNullValues().size());
        Assert.assertEquals((-9999), dimension.getNullValues().get(0), 0.0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, dimension.getRange().getMinimum(), 0.0);
        // Huston, we have a problem here...
        // assertEquals(9999, dimension.getRange().getMaximum(), 0d);
        Assert.assertNull(dimension.getUnit());
    }

    @Test
    public void testInitCoverageSRSLookup_GEOS8973() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getCoverageStoreByName(MockData.WORLD.getLocalPart()));
        CoverageInfo cinfo = cb.buildCoverage();
        cinfo.setSRS(null);
        String wkt = "GEOGCS[\"ED50\",\n" + ((("  DATUM[\"European Datum 1950\",\n" + "  SPHEROID[\"International 1924\", 6378388.0, 297.0]],\n") + "PRIMEM[\"Greenwich\", 0.0],\n") + "UNIT[\"degree\", 0.017453292519943295]]");
        CoordinateReferenceSystem testCRS = CRS.parseWKT(wkt);
        cinfo.setNativeCRS(testCRS);
        cb.initCoverage(cinfo, "srs lookup");
        Assert.assertEquals("EPSG:4230", cinfo.getSRS());
    }

    @Test
    public void testNativeBoundsDefensiveCopy() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getCoverageStoreByName(MockData.TASMANIA_DEM.getLocalPart()));
        CoverageInfo ci = cb.buildCoverage();
        // setup the reproject to declared policy, the issue happens only under this condition
        ReferencedEnvelope nativeBounds = ci.getNativeBoundingBox();
        for (ProjectionPolicy pp : ProjectionPolicy.values()) {
            ci.setProjectionPolicy(pp);
            ReferencedEnvelope bbox = ci.boundingBox();
            Assert.assertNotSame(nativeBounds, bbox);
        }
    }

    @Test
    public void testSingleBandedCoverage_GEOS7311() throws Exception {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("es", "ES"));
        testSingleBandedCoverage();
        Locale.setDefault(new Locale("fr", "FR"));
        testSingleBandedCoverage();
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testMultiBandCoverage() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getCoverageStoreByName(MockData.TASMANIA_BM.getLocalPart()));
        CoverageInfo ci = cb.buildCoverage();
        // perform basic checks
        Assert.assertEquals(CRS.decode("EPSG:4326", true), ci.getCRS());
        Assert.assertEquals("EPSG:4326", ci.getSRS());
        Assert.assertNotNull(ci.getNativeCRS());
        Assert.assertNotNull(ci.getNativeBoundingBox());
        Assert.assertNotNull(ci.getLatLonBoundingBox());
        // check the coverage dimensions
        List<CoverageDimensionInfo> dimensions = ci.getDimensions();
        Assert.assertEquals(3, dimensions.size());
        CoverageDimensionInfo dimension = dimensions.get(0);
        Assert.assertEquals("RED_BAND", dimension.getName());
        Assert.assertEquals(0, dimension.getNullValues().size());
        Assert.assertEquals(Double.NEGATIVE_INFINITY, dimension.getRange().getMinimum(), 0.0);
        Assert.assertEquals(Double.POSITIVE_INFINITY, dimension.getRange().getMaximum(), 0.0);
        Assert.assertEquals("W.m-2.Sr-1", dimension.getUnit());
    }

    @Test
    public void testEmptyBounds() throws Exception {
        // test the bounds of a single point
        Catalog cat = getCatalog();
        FeatureTypeInfo fti = cat.getFeatureTypeByName(toString(MockData.POINTS));
        Assert.assertEquals(Point.class, fti.getFeatureType().getGeometryDescriptor().getType().getBinding());
        Assert.assertEquals(1, fti.getFeatureSource(null, null).getCount(ALL));
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getStoreByName(MockData.CGF_PREFIX, DataStoreInfo.class));
        FeatureTypeInfo built = cb.buildFeatureType(fti.getQualifiedName());
        cb.setupBounds(built);
        Assert.assertTrue(((built.getNativeBoundingBox().getWidth()) > 0));
        Assert.assertTrue(((built.getNativeBoundingBox().getHeight()) > 0));
    }

    @Test
    public void testEmptyLayerGroupBounds() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo group = cat.getFactory().createLayerGroup();
        group.setName("empty_group");
        Assert.assertNull(group.getBounds());
        // force bounds computation
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.calculateLayerGroupBounds(group);
        Assert.assertNull(group.getBounds());
    }

    @Test
    public void testLayerGroupBounds() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.LINES.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.LINES));
        cb.setupBounds(fti);
        LayerInfo layer = cat.getFactory().createLayer();
        layer.setResource(fti);
        layer.setName(fti.getName());
        layer.setEnabled(true);
        layer.setType(VECTOR);
        LayerGroupInfo group = cat.getFactory().createLayerGroup();
        group.setName("group");
        group.getLayers().add(layer);
        group.getStyles().add(null);
        Assert.assertNull(group.getBounds());
        // force bounds computation
        cb.calculateLayerGroupBounds(group);
        Assert.assertNotNull(group.getBounds());
        Assert.assertEquals(fti.getNativeBoundingBox(), group.getBounds());
    }

    @Test
    public void testLayerGroupEoBounds() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(cat.getDataStoreByName(MockData.LINES.getPrefix()));
        FeatureTypeInfo fti = cb.buildFeatureType(toName(MockData.LINES));
        cb.setupBounds(fti);
        LayerInfo layer = cat.getFactory().createLayer();
        layer.setResource(fti);
        layer.setName(fti.getName());
        layer.setEnabled(true);
        layer.setType(VECTOR);
        LayerGroupInfo group = cat.getFactory().createLayerGroup();
        group.setMode(EO);
        group.setName("group_EO");
        group.setRootLayer(layer);
        Assert.assertNull(group.getBounds());
        // force bounds computation
        cb.calculateLayerGroupBounds(group);
        Assert.assertNotNull(group.getBounds());
        Assert.assertEquals(fti.getNativeBoundingBox(), group.getBounds());
    }

    /**
     * Tests we can build properly the WMS store and the WMS layer
     */
    @Test
    public void testWMS() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(GeoServerBaseTestSupport.LOGGER))) {
            GeoServerBaseTestSupport.LOGGER.warning("Remote OWS tests disabled, skipping catalog builder wms tests");
            return;
        }
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        WMSStoreInfo wms = cb.buildWMSStore("demo");
        wms.setCapabilitiesURL(((RemoteOWSTestSupport.WMS_SERVER_URL) + "service=WMS&request=GetCapabilities&version=1.1.0"));
        cb.setStore(wms);
        WMSLayerInfo wmsLayer = cb.buildWMSLayer("topp:states");
        assertWMSLayer(wmsLayer);
        LayerInfo layer = cb.buildLayer(wmsLayer);
        Assert.assertEquals(WMS, layer.getType());
        wmsLayer = cat.getFactory().createWMSLayer();
        wmsLayer.setName("states");
        wmsLayer.setNativeName("topp:states");
        cb.initWMSLayer(wmsLayer);
        assertWMSLayer(wmsLayer);
    }

    @Test
    public void testLookupSRSDetached() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        DataStoreInfo sf = cat.getDataStoreByName("sf");
        FeatureSource fs = sf.getDataStore(null).getFeatureSource(toName(MockData.PRIMITIVEGEOFEATURE));
        FeatureTypeInfo ft = cat.getFactory().createFeatureType();
        ft.setNativeName("PrimitiveGeoFeature");
        Assert.assertNull(ft.getSRS());
        Assert.assertNull(ft.getCRS());
        cb.lookupSRS(ft, fs, true);
        Assert.assertNotNull(ft.getSRS());
        Assert.assertNotNull(ft.getCRS());
    }

    @Test
    public void testSetupBoundsDetached() throws Exception {
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        DataStoreInfo sf = cat.getDataStoreByName("sf");
        FeatureSource fs = sf.getDataStore(null).getFeatureSource(toName(MockData.PRIMITIVEGEOFEATURE));
        FeatureTypeInfo ft = cat.getFactory().createFeatureType();
        ft.setNativeName("PrimitiveGeoFeature");
        Assert.assertNull(ft.getNativeBoundingBox());
        Assert.assertNull(ft.getLatLonBoundingBox());
        cb.lookupSRS(ft, fs, true);
        cb.setupBounds(ft, fs);
        Assert.assertNotNull(ft.getNativeBoundingBox());
        Assert.assertNotNull(ft.getLatLonBoundingBox());
    }

    @Test
    public void testMetadataFromFeatueSource() throws Exception {
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        cb.setStore(cb.buildDataStore("fooStore"));
        FeatureType ft = EasyMock.createMock(FeatureType.class);
        EasyMock.expect(ft.getName()).andReturn(new NameImpl("foo")).anyTimes();
        EasyMock.expect(ft.getCoordinateReferenceSystem()).andReturn(null).anyTimes();
        EasyMock.expect(ft.getGeometryDescriptor()).andReturn(null).anyTimes();
        EasyMock.replay(ft);
        ResourceInfo rInfo = EasyMock.createMock(ResourceInfo.class);
        EasyMock.expect(rInfo.getTitle()).andReturn("foo title");
        EasyMock.expect(rInfo.getDescription()).andReturn("foo description");
        EasyMock.expect(rInfo.getKeywords()).andReturn(new LinkedHashSet<String>(Arrays.asList("foo", "bar", "baz", ""))).anyTimes();
        EasyMock.replay(rInfo);
        FeatureSource fs = EasyMock.createMock(FeatureSource.class);
        EasyMock.expect(fs.getSchema()).andReturn(ft).anyTimes();
        EasyMock.expect(fs.getInfo()).andReturn(rInfo).anyTimes();
        EasyMock.expect(fs.getName()).andReturn(ft.getName()).anyTimes();
        EasyMock.replay(fs);
        FeatureTypeInfo ftInfo = cb.buildFeatureType(fs);
        Assert.assertEquals("foo title", ftInfo.getTitle());
        Assert.assertEquals("foo description", ftInfo.getDescription());
        Assert.assertTrue(ftInfo.getKeywords().contains(new Keyword("foo")));
        Assert.assertTrue(ftInfo.getKeywords().contains(new Keyword("bar")));
        Assert.assertTrue(ftInfo.getKeywords().contains(new Keyword("baz")));
    }

    @Test
    public void testSetupMetadataResourceInfoException() throws Exception {
        FeatureTypeInfo ftInfo = EasyMock.createMock(FeatureTypeInfo.class);
        EasyMock.expect(ftInfo.getTitle()).andReturn("foo");
        EasyMock.expect(ftInfo.getDescription()).andReturn("foo");
        EasyMock.expect(ftInfo.getKeywords()).andReturn(null);
        EasyMock.replay(ftInfo);
        FeatureSource fs = EasyMock.createMock(FeatureSource.class);
        EasyMock.expect(fs.getInfo()).andThrow(new UnsupportedOperationException());
        EasyMock.replay(fs);
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        cb.setupMetadata(ftInfo, fs);
    }

    @Test
    public void testLatLonBounds() throws Exception {
        ReferencedEnvelope nativeBounds = new ReferencedEnvelope(700000, 800000, 4000000, 4100000, null);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:32632", true);
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        ReferencedEnvelope re = cb.getLatLonBounds(nativeBounds, crs);
        Assert.assertEquals(WGS84, re.getCoordinateReferenceSystem());
        Assert.assertEquals(11.22, re.getMinX(), 0.01);
        Assert.assertEquals(36.1, re.getMinY(), 0.01);
    }

    @Test
    public void testWMSLayer111() throws Exception {
        TestHttpClientProvider.startTest();
        try {
            String baseURL = (TestHttpClientProvider.MOCKSERVER) + "/wms11";
            MockHttpClient client = new MockHttpClient();
            URL capsURL = new URL((baseURL + "?service=WMS&request=GetCapabilities&version=1.1.0"));
            client.expectGet(capsURL, new MockHttpResponse(getClass().getResource("caps111.xml"), "text/xml"));
            TestHttpClientProvider.bind(client, capsURL);
            CatalogBuilder cb = new CatalogBuilder(getCatalog());
            WMSStoreInfo store = cb.buildWMSStore("test-store");
            store.setCapabilitiesURL(capsURL.toExternalForm());
            cb.setStore(store);
            WMSLayerInfo layer = cb.buildWMSLayer("world4326");
            // check the bbox has the proper axis order
            Assert.assertEquals("EPSG:4326", layer.getSRS());
            ReferencedEnvelope bbox = layer.getLatLonBoundingBox();
            Assert.assertEquals((-180), bbox.getMinX(), 0.0);
            Assert.assertEquals((-90), bbox.getMinY(), 0.0);
            Assert.assertEquals(180, bbox.getMaxX(), 0.0);
            Assert.assertEquals(90, bbox.getMaxY(), 0.0);
        } finally {
            TestHttpClientProvider.endTest();
        }
    }

    @Test
    public void testWMSLayer130() throws Exception {
        TestHttpClientProvider.startTest();
        try {
            String baseURL = (TestHttpClientProvider.MOCKSERVER) + "/wms13";
            MockHttpClient client = new MockHttpClient();
            URL capsURL = new URL((baseURL + "?service=WMS&request=GetCapabilities&version=1.3.0"));
            client.expectGet(capsURL, new MockHttpResponse(getClass().getResource("caps130.xml"), "text/xml"));
            TestHttpClientProvider.bind(client, capsURL);
            CatalogBuilder cb = new CatalogBuilder(getCatalog());
            WMSStoreInfo store = cb.buildWMSStore("test-store");
            store.setCapabilitiesURL(capsURL.toExternalForm());
            cb.setStore(store);
            WMSLayerInfo layer = cb.buildWMSLayer("world4326");
            // check the bbox has the proper axis order
            Assert.assertEquals("EPSG:4326", layer.getSRS());
            ReferencedEnvelope bbox = layer.getLatLonBoundingBox();
            Assert.assertEquals((-180), bbox.getMinX(), 0.0);
            Assert.assertEquals((-90), bbox.getMinY(), 0.0);
            Assert.assertEquals(180, bbox.getMaxX(), 0.0);
            Assert.assertEquals(90, bbox.getMaxY(), 0.0);
        } finally {
            TestHttpClientProvider.endTest();
        }
    }

    @Test
    public void testWMSLayer130crs84() throws Exception {
        TestHttpClientProvider.startTest();
        try {
            String baseURL = (TestHttpClientProvider.MOCKSERVER) + "/wms13";
            MockHttpClient client = new MockHttpClient();
            URL capsURL = new URL((baseURL + "?service=WMS&request=GetCapabilities&version=1.3.0"));
            client.expectGet(capsURL, new MockHttpResponse(getClass().getResource("caps130_crs84.xml"), "text/xml"));
            TestHttpClientProvider.bind(client, capsURL);
            CatalogBuilder cb = new CatalogBuilder(getCatalog());
            WMSStoreInfo store = cb.buildWMSStore("test-store");
            store.setCapabilitiesURL(capsURL.toExternalForm());
            cb.setStore(store);
            WMSLayerInfo layer = cb.buildWMSLayer("world4326");
            // check the bbox has the proper axis order
            Assert.assertEquals("EPSG:4326", layer.getSRS());
            ReferencedEnvelope bbox = layer.getLatLonBoundingBox();
            Assert.assertEquals((-180), bbox.getMinX(), 0.0);
            Assert.assertEquals((-90), bbox.getMinY(), 0.0);
            Assert.assertEquals(180, bbox.getMaxX(), 0.0);
            Assert.assertEquals(90, bbox.getMaxY(), 0.0);
        } finally {
            TestHttpClientProvider.endTest();
        }
    }

    @Test
    public void testWMTSLayer100() throws Exception {
        TestHttpClientProvider.startTest();
        try {
            String baseURL = (TestHttpClientProvider.MOCKSERVER) + "/wmts100";
            MockHttpClient client = new MockHttpClient();
            URL capsURL = new URL((baseURL + "?REQUEST=GetCapabilities&VERSION=1.0.0&SERVICE=WMTS"));
            client.expectGet(capsURL, new MockHttpResponse(getClass().getResource("nasa.getcapa.xml"), "text/xml"));
            TestHttpClientProvider.bind(client, capsURL);
            CatalogBuilder cb = new CatalogBuilder(getCatalog());
            WMTSStoreInfo store = cb.buildWMTSStore("test-wmts-store");
            store.setCapabilitiesURL(capsURL.toExternalForm());
            cb.setStore(store);
            WMTSLayerInfo layer = cb.buildWMTSLayer("AMSR2_Wind_Speed_Night");
            // check the bbox has the proper axis order
            Assert.assertEquals("Wind Speed (Night, AMSR2, GCOM-W1)", layer.getTitle());
            Assert.assertEquals("EPSG:4326", layer.getSRS());
            ReferencedEnvelope bbox = layer.getLatLonBoundingBox();
            Assert.assertEquals((-180), bbox.getMinX(), 0.0);
            Assert.assertEquals((-90), bbox.getMinY(), 0.0);
            Assert.assertEquals(180, bbox.getMaxX(), 0.0);
            Assert.assertEquals(90, bbox.getMaxY(), 0.0);
        } finally {
            TestHttpClientProvider.endTest();
        }
    }

    @Test
    public void testWgs84BoundsFromCompoundCRS() throws Exception {
        try {
            MapProjection.SKIP_SANITY_CHECKS = true;
            CatalogBuilder cb = new CatalogBuilder(getCatalog());
            ReferencedEnvelope3D bounds = new ReferencedEnvelope3D(142892, 470783, 16, 142900, 470790, 20, CRS.decode("EPSG:7415"));
            // used to throw an exception here
            ReferencedEnvelope latLonBounds = cb.getLatLonBounds(bounds, bounds.getCoordinateReferenceSystem());
            Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), latLonBounds.getCoordinateReferenceSystem()));
            // System.out.println(latLonBounds);
        } finally {
            MapProjection.SKIP_SANITY_CHECKS = false;
        }
    }

    @Test
    public void testSetupCoverageOnEmptyRead() throws Exception {
        // fake coverage info
        ReferencedEnvelope envelope = MockTestData.DEFAULT_LATLON_ENVELOPE;
        BufferedImage bi = new BufferedImage(360, 180, BufferedImage.TYPE_3BYTE_BGR);
        ImageLayout layout = new ImageLayout(bi);
        GridEnvelope2D gridRange = new GridEnvelope2D(0, 0, 360, 180);
        GridToEnvelopeMapper mapper = new GridToEnvelopeMapper(gridRange, envelope);
        AffineTransform gridToWorld = mapper.createAffineTransform();
        final String rasterSource = "http://www.geoserver.org/foo";
        // setup the format and the reader
        AbstractGridCoverage2DReader reader = EasyMock.createMock(AbstractGridCoverage2DReader.class);
        AbstractGridFormat format = EasyMock.createMock(AbstractGridFormat.class);
        EasyMock.expect(reader.getOriginalEnvelope()).andReturn(new org.geotools.geometry.GeneralEnvelope(envelope)).anyTimes();
        EasyMock.expect(reader.getCoordinateReferenceSystem()).andReturn(envelope.getCoordinateReferenceSystem()).anyTimes();
        EasyMock.expect(reader.getOriginalGridRange()).andReturn(gridRange).anyTimes();
        EasyMock.expect(reader.getImageLayout()).andReturn(layout).anyTimes();
        EasyMock.expect(reader.getFormat()).andReturn(format).anyTimes();
        EasyMock.expect(reader.getGridCoverageCount()).andReturn(1);
        EasyMock.expect(reader.getOriginalGridToWorld(org.easymock.classextension.EasyMock.anyObject(PixelInCell.class))).andReturn(new AffineTransform2D(gridToWorld)).anyTimes();
        EasyMock.expect(reader.read(org.easymock.classextension.EasyMock.anyObject(GeneralParameterValue[].class))).andReturn(null);
        EasyMock.expect(reader.getGridCoverageNames()).andReturn(new String[]{ "TheCoverage" });
        EasyMock.replay(reader);
        EasyMock.expect(format.getReader(org.easymock.classextension.EasyMock.eq(rasterSource), org.easymock.classextension.EasyMock.anyObject(Hints.class))).andReturn(reader).anyTimes();
        EasyMock.expect(format.getName()).andReturn("TheFormat").anyTimes();
        EasyMock.expect(format.getReadParameters()).andReturn(getReadParameters()).anyTimes();
        EasyMock.replay(format);
        CoverageStoreInfo csi = EasyMock.createMock(CoverageStoreInfo.class);
        EasyMock.expect(csi.getURL()).andReturn(rasterSource).anyTimes();
        EasyMock.expect(csi.getFormat()).andReturn(format).anyTimes();
        EasyMock.expect(csi.getId()).andReturn("ThisIsMe").anyTimes();
        EasyMock.expect(csi.getName()).andReturn("ThisIsMe").anyTimes();
        EasyMock.expect(csi.getWorkspace()).andReturn(getCatalog().getDefaultWorkspace()).anyTimes();
        EasyMock.replay(csi);
        // setup a non cloning resource pool and catalog
        Catalog cat = new CatalogImpl();
        ResourcePool rp = new ResourcePool(cat) {
            public CoverageStoreInfo clone(CoverageStoreInfo source, boolean allowEnvParametrization) {
                return source;
            }
        };
        cat.setResourcePool(rp);
        // make it build the coverage info without access to a grid coverage 2d
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(csi);
        CoverageInfo ci = cb.buildCoverage();
        Assert.assertEquals("TheCoverage", ci.getName());
        List<CoverageDimensionInfo> dimensions = ci.getDimensions();
        Assert.assertEquals(3, dimensions.size());
        Assert.assertEquals("RED_BAND", dimensions.get(0).getName());
        Assert.assertEquals("GREEN_BAND", dimensions.get(1).getName());
        Assert.assertEquals("BLUE_BAND", dimensions.get(2).getName());
    }
}

