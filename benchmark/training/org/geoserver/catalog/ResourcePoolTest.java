/**
 * (c) 2014-2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import FeatureTypeInfo.JDBC_VIRTUAL_TABLE;
import SortBy.UNSORTED;
import WFSDataStoreFactory.URL.key;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.media.jai.PlanarImage;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.geoserver.catalog.util.ReaderUtils;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.platform.GeoServerEnvironment;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.RunTestSetup;
import org.geoserver.test.SystemTest;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.StructuredGridCoverage2DReader;
import org.geotools.coverage.util.CoverageUtilities;
import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.collection.DecoratingFeatureCollection;
import org.geotools.feature.collection.SortedSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.image.util.ImageUtilities;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.VirtualTable;
import org.geotools.jdbc.VirtualTableParameter;
import org.geotools.ows.ServiceException;
import org.geotools.styling.AbstractStyleVisitor;
import org.geotools.styling.Mark;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.util.URLs;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.factory.Hints;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.locationtech.jts.geom.Point;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.style.ExternalGraphic;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Tests for {@link ResourcePool}.
 *
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 */
@Category(SystemTest.class)
public class ResourcePoolTest extends GeoServerSystemTestSupport {
    private static final String SQLVIEW_DATASTORE = "sqlviews";

    private static final String VT_NAME = "pgeo_view";

    private static final String HUMANS = "humans";

    static {
        System.setProperty("ALLOW_ENV_PARAMETRIZATION", "true");
    }

    private static File rockFillSymbolFile;

    protected static QName TIMERANGES = new QName(MockData.SF_URI, "timeranges", MockData.SF_PREFIX);

    private static final String EXTERNAL_ENTITIES = "externalEntities";

    /**
     * Test that the {@link FeatureType} cache returns the same instance every time. This is assumed
     * by some nasty code in other places that tampers with the CRS. If a new {@link FeatureType} is
     * constructed for the same {@link FeatureTypeInfo}, Bad Things Happen (TM).
     */
    @Test
    public void testFeatureTypeCacheInstance() throws Exception {
        ResourcePool pool = ResourcePool.create(getCatalog());
        FeatureTypeInfo info = getCatalog().getFeatureTypeByName(MockData.LAKES.getNamespaceURI(), MockData.LAKES.getLocalPart());
        FeatureType ft1 = pool.getFeatureType(info);
        FeatureType ft2 = pool.getFeatureType(info);
        FeatureType ft3 = pool.getFeatureType(info);
        Assert.assertSame(ft1, ft2);
        Assert.assertSame(ft1, ft3);
    }

    @Test
    public void testAttributeCache() throws Exception {
        final Catalog catalog = getCatalog();
        ResourcePool pool = ResourcePool.create(catalog);
        // clean up the lakes type
        FeatureTypeInfo oldInfo = catalog.getFeatureTypeByName(MockData.LAKES.getNamespaceURI(), MockData.LAKES.getLocalPart());
        List<LayerInfo> layers = catalog.getLayers(oldInfo);
        for (LayerInfo layerInfo : layers) {
            catalog.remove(layerInfo);
        }
        catalog.remove(oldInfo);
        // rebuild as new
        CatalogBuilder builder = new CatalogBuilder(catalog);
        builder.setStore(catalog.getStoreByName(MockData.CITE_PREFIX, MockData.CITE_PREFIX, DataStoreInfo.class));
        FeatureTypeInfo info = builder.buildFeatureType(new NameImpl(MockData.LAKES.getNamespaceURI(), MockData.LAKES.getLocalPart()));
        // non persisted state, caching should not occurr
        List<AttributeTypeInfo> att1 = pool.getAttributes(info);
        List<AttributeTypeInfo> att2 = pool.getAttributes(info);
        Assert.assertNotSame(att1, att2);
        Assert.assertEquals(att1, att2);
        // save it, making it persistent
        catalog.add(info);
        // first check caching actually works against persisted type infos
        List<AttributeTypeInfo> att3 = pool.getAttributes(info);
        List<AttributeTypeInfo> att4 = pool.getAttributes(info);
        Assert.assertSame(att3, att4);
        Assert.assertNotSame(att1, att3);
        Assert.assertEquals(att1, att3);
    }

    boolean cleared = false;

    @Test
    public void testCacheClearing() throws IOException {
        cleared = false;
        ResourcePool pool = new ResourcePool(getCatalog()) {
            @Override
            public void clear(FeatureTypeInfo info) {
                cleared = true;
                super.clear(info);
            }
        };
        FeatureTypeInfo info = getCatalog().getFeatureTypeByName(MockData.LAKES.getNamespaceURI(), MockData.LAKES.getLocalPart());
        Assert.assertNotNull(pool.getFeatureType(info));
        info.setTitle("changed");
        Assert.assertFalse(cleared);
        getCatalog().save(info);
        Assert.assertTrue(cleared);
        cleared = false;
        Assert.assertNotNull(pool.getFeatureType(info));
        for (LayerInfo l : getCatalog().getLayers(info)) {
            getCatalog().remove(l);
        }
        getCatalog().remove(info);
        Assert.assertTrue(cleared);
    }

    boolean disposeCalled;

    /**
     * Make sure {@link ResourcePool#clear(DataStoreInfo)} and {@link ResourcePool#dispose()} call
     * {@link DataAccess#dispose()}
     */
    @Test
    public void testDispose() throws IOException {
        disposeCalled = false;
        class ResourcePool2 extends ResourcePool {
            @SuppressWarnings("serial")
            public ResourcePool2(Catalog catalog) {
                super(catalog);
                dataStoreCache = new DataStoreCache() {
                    @SuppressWarnings("unchecked")
                    @Override
                    protected void dispose(String name, DataAccess dataStore) {
                        disposeCalled = true;
                        super.dispose(name, dataStore);
                    }
                };
            }
        }
        Catalog catalog = getCatalog();
        ResourcePool pool = new ResourcePool2(catalog);
        catalog.setResourcePool(pool);
        DataStoreInfo info = catalog.getDataStores().get(0);
        // force the datastore to be created
        DataAccess<? extends FeatureType, ? extends Feature> dataStore = pool.getDataStore(info);
        Assert.assertNotNull(dataStore);
        Assert.assertFalse(disposeCalled);
        pool.clear(info);
        Assert.assertTrue(disposeCalled);
        // force the datastore to be created
        dataStore = pool.getDataStore(info);
        Assert.assertNotNull(dataStore);
        disposeCalled = false;
        pool.dispose();
        Assert.assertTrue(disposeCalled);
    }

    @Test
    public void testConfigureFeatureTypeCacheSize() {
        GeoServer gs = getGeoServer();
        GeoServerInfo global = gs.getGlobal();
        global.setFeatureTypeCacheSize(200);
        gs.save(global);
        Catalog catalog = getCatalog();
        // we actually keep two versions of the feature type in the cache, so we need it
        // twice as big
        Assert.assertEquals(400, getHardReferencesCount());
    }

    @Test
    public void testDropCoverageStore() throws Exception {
        // build the store
        Catalog cat = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(cat);
        CoverageStoreInfo store = cb.buildCoverageStore("dem");
        store.setURL(MockData.class.getResource("tazdem.tiff").toExternalForm());
        store.setType("GeoTIFF");
        cat.add(store);
        // build the coverage
        cb.setStore(store);
        CoverageInfo ci = cb.buildCoverage();
        cat.add(ci);
        // build the layer
        LayerInfo layer = cb.buildLayer(ci);
        cat.add(layer);
        // grab a reader just to inizialize the code
        ci.getGridCoverage(null, null);
        ci.getGridCoverageReader(null, GeoTools.getDefaultHints());
        // now drop the store
        CascadeDeleteVisitor visitor = new CascadeDeleteVisitor(cat);
        visitor.visit(store);
        // and reload (GEOS-4782 -> BOOM!)
        getGeoServer().reload();
    }

    @RunTestSetup
    @Test
    public void testGeoServerReload() throws Exception {
        Catalog cat = getCatalog();
        FeatureTypeInfo lakes = cat.getFeatureTypeByName(MockData.LAKES.getNamespaceURI(), MockData.LAKES.getLocalPart());
        Assert.assertFalse("foo".equals(lakes.getTitle()));
        GeoServerDataDirectory dd = new GeoServerDataDirectory(getResourceLoader());
        File info = dd.findResourceFile(lakes);
        // File info = getResourceLoader().find("featureTypes", "cite_Lakes", "info.xml");
        FileReader in = new FileReader(info);
        Element dom = ReaderUtils.parse(in);
        Element title = ReaderUtils.getChildElement(dom, "title");
        title.getFirstChild().setNodeValue("foo");
        OutputStream output = new FileOutputStream(info);
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(dom), new StreamResult(output));
        } finally {
            output.close();
        }
        getGeoServer().reload();
        lakes = cat.getFeatureTypeByName(MockData.LAKES.getNamespaceURI(), MockData.LAKES.getLocalPart());
        Assert.assertEquals("foo", lakes.getTitle());
    }

    @Test
    public void testSEStyleWithRelativePath() throws IOException {
        StyleInfo si = getCatalog().getStyleByName("relative");
        Assert.assertNotNull(si);
        Style style = si.getStyle();
        PolygonSymbolizer ps = ((PolygonSymbolizer) (style.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        ExternalGraphic eg = ((ExternalGraphic) (ps.getFill().getGraphicFill().graphicalSymbols().get(0)));
        URI uri = eg.getOnlineResource().getLinkage();
        Assert.assertNotNull(uri);
        File actual = URLs.urlToFile(uri.toURL()).getCanonicalFile();
        Assert.assertEquals(ResourcePoolTest.rockFillSymbolFile, actual);
    }

    @Test
    public void testSEStyleWithRelativePathProtocol() throws IOException {
        StyleInfo si = getCatalog().getStyleByName("relative_protocol");
        Assert.assertNotNull(si);
        Style style = si.getStyle();
        PolygonSymbolizer ps = ((PolygonSymbolizer) (style.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        ExternalGraphic eg = ((ExternalGraphic) (ps.getFill().getGraphicFill().graphicalSymbols().get(0)));
        URI uri = eg.getOnlineResource().getLinkage();
        Assert.assertNotNull(uri);
        File actual = URLs.urlToFile(uri.toURL()).getCanonicalFile();
        Assert.assertEquals(ResourcePoolTest.rockFillSymbolFile, actual);
    }

    @Test
    public void testPreserveStructuredReader() throws IOException {
        // we have to make sure time ranges native name is set to trigger the bug in question
        CoverageInfo ci = getCatalog().getCoverageByName(getLayerId(ResourcePoolTest.TIMERANGES));
        Assert.assertTrue(((ci.getGridCoverageReader(null, null)) instanceof StructuredGridCoverage2DReader));
        String name = ci.getGridCoverageReader(null, null).getGridCoverageNames()[0];
        ci.setNativeCoverageName(name);
        getCatalog().save(ci);
        ci = getCatalog().getCoverageByName(getLayerId(ResourcePoolTest.TIMERANGES));
        Assert.assertTrue(((ci.getGridCoverageReader(null, null)) instanceof StructuredGridCoverage2DReader));
    }

    @Test
    public void testMissingNullValuesInCoverageDimensions() throws IOException {
        CoverageInfo ci = getCatalog().getCoverageByName(getLayerId(MockData.TASMANIA_DEM));
        List<CoverageDimensionInfo> dimensions = ci.getDimensions();
        // legacy layers have no null value list
        dimensions.get(0).getNullValues().clear();
        getCatalog().save(ci);
        // and now go back and ask for the reader
        ci = getCatalog().getCoverageByName(getLayerId(MockData.TASMANIA_DEM));
        GridCoverageReader reader = ci.getGridCoverageReader(null, null);
        GridCoverage2D gc = null;
        try {
            // check that we maintain the native info if we don't have any
            gc = ((GridCoverage2D) (reader.read(null)));
            Assert.assertEquals((-9999.0), CoverageUtilities.getNoDataProperty(gc).getAsSingleValue(), 0.0);
        } finally {
            if (gc != null) {
                RenderedImage ri = gc.getRenderedImage();
                if (gc instanceof GridCoverage2D) {
                    gc.dispose(true);
                }
                if (ri instanceof PlanarImage) {
                    ImageUtilities.disposePlanarImageChain(((PlanarImage) (ri)));
                }
            }
        }
    }

    @RunTestSetup
    @Test
    public void testEnvParametrizationValues() throws Exception {
        final GeoServerEnvironment gsEnvironment = GeoServerExtensions.bean(GeoServerEnvironment.class);
        DataStoreInfo ds = getCatalog().getFactory().createDataStore();
        ds.getConnectionParameters().put("host", "${jdbc.host}");
        ds.getConnectionParameters().put("port", "${jdbc.port}");
        try {
            final String dsName = "GS-ENV-TEST-DS";
            ds.setName(dsName);
            getCatalog().save(ds);
            ds = getCatalog().getDataStoreByName(dsName);
            DataStoreInfo expandedDs = getCatalog().getResourcePool().clone(ds, true);
            Assert.assertTrue(ds.getConnectionParameters().get("host").equals("${jdbc.host}"));
            Assert.assertTrue(ds.getConnectionParameters().get("port").equals("${jdbc.port}"));
            if (GeoServerEnvironment.ALLOW_ENV_PARAMETRIZATION) {
                Assert.assertTrue(expandedDs.getConnectionParameters().get("host").equals(gsEnvironment.resolveValue("${jdbc.host}")));
                Assert.assertTrue(expandedDs.getConnectionParameters().get("port").equals(gsEnvironment.resolveValue("${jdbc.port}")));
            } else {
                Assert.assertTrue(expandedDs.getConnectionParameters().get("host").equals("${jdbc.host}"));
                Assert.assertTrue(expandedDs.getConnectionParameters().get("port").equals("${jdbc.port}"));
            }
        } finally {
            getCatalog().remove(ds);
        }
    }

    @Test
    public void testCloneStoreInfo() throws Exception {
        Catalog catalog = getCatalog();
        DataStoreInfo source1 = catalog.getDataStores().get(0);
        DataStoreInfo clonedDs = catalog.getResourcePool().clone(source1, false);
        Assert.assertNotNull(source1);
        Assert.assertNotNull(clonedDs);
        Assert.assertEquals(source1, clonedDs);
        CoverageStoreInfo source2 = catalog.getCoverageStores().get(0);
        CoverageStoreInfo clonedCs = catalog.getResourcePool().clone(source2, false);
        Assert.assertNotNull(source2);
        Assert.assertNotNull(clonedCs);
        Assert.assertEquals(source2, clonedCs);
    }

    @Test
    public void testAddFilePathWithSpaces() throws Exception {
        // Other tests mess with or reset the resourcePool, so lets make it is initialised properly
        GeoServerExtensions.extensions(ResourcePoolInitializer.class).get(0).initialize(getGeoServer());
        ResourcePool rp = getCatalog().getResourcePool();
        CoverageStoreInfo info = getCatalog().getFactory().createCoverageStore();
        info.setName("spaces");
        info.setType("ImagePyramid");
        info.setEnabled(true);
        info.setURL("file://./src/test/resources/data_dir/nested_layer_groups/data/pyramid with space");
        try {
            rp.getGridCoverageReader(info, null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unable to add an imagepyramid with a space in it's name");
        }
        rp.dispose();
    }

    @Test
    public void testWmsCascadeEntityExpansion() throws Exception {
        // Other tests mess with or reset the resourcePool, so lets make it is initialized properly
        GeoServerExtensions.extensions(ResourcePoolInitializer.class).get(0).initialize(getGeoServer());
        ResourcePool rp = getCatalog().getResourcePool();
        WMSStoreInfo info = getCatalog().getFactory().createWebMapServer();
        URL url = getClass().getResource("1.3.0Capabilities-xxe.xml");
        info.setCapabilitiesURL(url.toExternalForm());
        info.setEnabled(true);
        // the connection pooling client does not support file references, disable it
        info.setUseConnectionPooling(false);
        try {
            rp.getWebMapServer(info);
            Assert.fail("WebMapServer instantiation should fail");
        } catch (IOException e) {
            Assert.assertThat(e.getCause(), instanceOf(ServiceException.class));
            ServiceException serviceException = ((ServiceException) (e.getCause()));
            Assert.assertThat(serviceException.getMessage(), CoreMatchers.containsString("Error while parsing XML"));
            SAXException saxException = ((SAXException) (serviceException.getCause()));
            Exception cause = saxException.getException();
            Assert.assertFalse("Expect external entity cause", ((cause != null) && (cause instanceof FileNotFoundException)));
        }
        // make sure clearing the catalog does not clear the EntityResolver
        getGeoServer().reload();
        rp = getCatalog().getResourcePool();
        try {
            rp.getWebMapServer(info);
            Assert.fail("WebMapServer instantiation should fail");
        } catch (IOException e) {
            Assert.assertThat(e.getCause(), instanceOf(ServiceException.class));
            ServiceException serviceException = ((ServiceException) (e.getCause()));
            Assert.assertThat(serviceException.getMessage(), CoreMatchers.containsString("Error while parsing XML"));
            SAXException saxException = ((SAXException) (serviceException.getCause()));
            Exception cause = saxException.getException();
            Assert.assertFalse("Expect external entity cause", ((cause != null) && (cause instanceof FileNotFoundException)));
        }
    }

    @Test
    public void testWfsCascadeEntityExpansion() throws Exception {
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        DataStoreInfo ds = cb.buildDataStore("wfs-xxe");
        URL url = getClass().getResource("wfs1.1.0Capabilities-xxe.xml");
        ds.getConnectionParameters().put(key, url);
        // required or the store won't fetch caps from a file
        ds.getConnectionParameters().put("TESTING", Boolean.TRUE);
        final ResourcePool rp = getCatalog().getResourcePool();
        try {
            rp.getDataStore(ds);
            Assert.fail("Store creation should have failed to to XXE attack");
        } catch (Exception e) {
            String message = e.getMessage();
            Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
            Assert.assertThat(message, CoreMatchers.containsString("file:///file/not/there"));
        }
    }

    @Test
    public void testStyleWithExternalEntities() throws Exception {
        StyleInfo si = getCatalog().getStyleByName(ResourcePoolTest.EXTERNAL_ENTITIES);
        try {
            si.getStyle();
            Assert.fail("Should have failed with a parse error");
        } catch (Exception e) {
            String message = e.getMessage();
            Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
            Assert.assertThat(message, CoreMatchers.containsString("/this/file/does/not/exist"));
        }
    }

    @Test
    public void testParseExternalMark() throws Exception {
        StyleInfo si = getCatalog().getStyleByName(ResourcePoolTest.HUMANS);
        // used to blow here with an NPE
        Style s = si.getStyle();
        s.accept(new AbstractStyleVisitor() {
            @Override
            public void visit(Mark mark) {
                Assert.assertEquals("ttf://Webdings", mark.getExternalMark().getOnlineResource().getLinkage().toASCIIString());
            }
        });
    }

    @Test
    public void testDataStoreScan() throws Exception {
        final Catalog catalog = getCatalog();
        // prepare a store that supports sql views
        Catalog cat = getCatalog();
        DataStoreInfo ds = cat.getFactory().createDataStore();
        ds.setName(ResourcePoolTest.SQLVIEW_DATASTORE);
        WorkspaceInfo ws = cat.getDefaultWorkspace();
        ds.setWorkspace(ws);
        ds.setEnabled(true);
        Map params = ds.getConnectionParameters();
        params.put("dbtype", "h2");
        File dbFile = new File(getTestData().getDataDirectoryRoot().getAbsolutePath(), "data/h2test");
        params.put("database", dbFile.getAbsolutePath());
        cat.add(ds);
        SimpleFeatureSource fsp = getFeatureSource(SystemTestData.PRIMITIVEGEOFEATURE);
        DataStore store = ((DataStore) (ds.getDataStore(null)));
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.init(fsp.getSchema());
        tb.remove("surfaceProperty");// the store cannot create multi-geom tables it seems

        tb.remove("curveProperty");// the store cannot create multi-geom tables it seems

        tb.remove("uriProperty");// this would render the store read only

        tb.setName("pgeo");
        SimpleFeatureType schema = tb.buildFeatureType();
        store.createSchema(schema);
        SimpleFeatureStore featureStore = ((SimpleFeatureStore) (store.getFeatureSource("pgeo")));
        featureStore.addFeatures(fsp.getFeatures());
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(ds);
        FeatureTypeInfo tft = cb.buildFeatureType(featureStore);
        cat.add(tft);
        // create the sql view
        JDBCDataStore jds = ((JDBCDataStore) (ds.getDataStore(null)));
        VirtualTable vt = new VirtualTable(ResourcePoolTest.VT_NAME, "select \"name\", \"pointProperty\" from \"pgeo\" where \"booleanProperty\" = %bool% and \"name\" = \'%name%\'");
        vt.addParameter(new VirtualTableParameter("bool", "true"));
        vt.addParameter(new VirtualTableParameter("name", "name-f001"));
        vt.addGeometryMetadatata("pointProperty", Point.class, 4326);
        jds.createVirtualTable(vt);
        FeatureTypeInfo vft = cb.buildFeatureType(jds.getFeatureSource(vt.getName()));
        vft.getMetadata().put(JDBC_VIRTUAL_TABLE, vt);
        cat.add(vft);
        AtomicInteger counter = new AtomicInteger();
        ResourcePool testPool = new ResourcePool() {
            /* This is the method making the expensive call to the data store (especially if the store is an Oracle one without a schema specified).
            Make sure it's not being called unless the feature type is really not cacheable.
             */
            @Override
            protected Name getTemporaryName(FeatureTypeInfo info, DataAccess<? extends FeatureType, ? extends Feature> dataAccess, FeatureTypeCallback initializer) throws IOException {
                if (ResourcePoolTest.VT_NAME.equals(info.getNativeName())) {
                    counter.incrementAndGet();
                }
                return super.getTemporaryName(info, dataAccess, initializer);
            }
        };
        testPool.setCatalog(catalog);
        FeatureTypeInfo ft = catalog.getFeatureTypeByName(ResourcePoolTest.VT_NAME);
        testPool.getFeatureSource(ft, null);
        Assert.assertEquals(0, counter.get());
        // now try with a dirty feature type, the call should be made
        ft.setName("foobar");
        testPool.getFeatureSource(ft, null);
        Assert.assertThat(counter.get(), greaterThan(0));
    }

    @Test
    public void testRepositoryHints() throws Exception {
        Catalog catalog = getCatalog();
        ResourcePool pool = new ResourcePool(catalog) {
            // cannot clone the mock objects
            public CoverageStoreInfo clone(CoverageStoreInfo source, boolean allowEnvParametrization) {
                return source;
            }
        };
        // setup all the mocks
        final String url = "http://www.geoserver.org/mock/format";
        AbstractGridCoverage2DReader reader = createNiceMock("theReader", AbstractGridCoverage2DReader.class);
        replay(reader);
        AbstractGridFormat format = createNiceMock("theFormat", AbstractGridFormat.class);
        Capture<Hints> capturedHints = new Capture();
        expect(format.getReader(EasyMock.EasyMock.eq(url), capture(capturedHints))).andReturn(reader).anyTimes();
        replay(format);
        CoverageStoreInfo storeInfo = createNiceMock("storeInfo", CoverageStoreInfo.class);
        expect(storeInfo.getURL()).andReturn(url).anyTimes();
        expect(storeInfo.getFormat()).andReturn(format).anyTimes();
        replay(storeInfo);
        // pass no hints
        GridCoverageReader returnedReader = pool.getGridCoverageReader(storeInfo, null);
        Assert.assertThat(reader, equalTo(returnedReader));
        final Hints hints1 = capturedHints.getValue();
        Assert.assertThat(hints1, notNullValue());
        Assert.assertThat(hints1, hasEntry(Hints.REPOSITORY, pool.repository));
        // pass some hints
        capturedHints.reset();
        GridCoverageReader returnedReader2 = pool.getGridCoverageReader(storeInfo, new Hints(Hints.KEY_ANTIALIASING, Hints.VALUE_ANTIALIAS_ON));
        Assert.assertThat(reader, equalTo(returnedReader2));
        final Hints hints2 = capturedHints.getValue();
        Assert.assertThat(hints2, notNullValue());
        Assert.assertThat(hints2, hasEntry(Hints.REPOSITORY, pool.repository));
        Assert.assertThat(hints2, hasEntry(Hints.KEY_ANTIALIASING, Hints.VALUE_ANTIALIAS_ON));
    }

    @Test
    public void testGetParamsFixesDatabaseFilePath() {
        Catalog catalog = getCatalog();
        ResourcePool pool = new ResourcePool(catalog);
        DataStoreInfo ds = getCatalog().getFactory().createDataStore();
        ds.getConnectionParameters().put("database", "file:data/test.gpkg");
        Map newParams = pool.getParams(ds.getConnectionParameters(), getResourceLoader());
        GeoServerDataDirectory dataDir = new GeoServerDataDirectory(getResourceLoader());
        String absolutePath = dataDir.get("data/test.gpkg").dir().getAbsolutePath();
        Assert.assertNotEquals(newParams.get("database"), "file:data/test.gpkg");
        Assert.assertTrue(((String) (newParams.get("database"))).contains(absolutePath));
    }

    @Test
    public void testEmptySort() throws IOException, IllegalAccessException {
        SimpleFeatureSource fsp = getFeatureSource(SystemTestData.PRIMITIVEGEOFEATURE);
        Query q = new Query();
        q.setSortBy(UNSORTED);
        SimpleFeatureCollection fc = fsp.getFeatures(q);
        // the above call used to add a SortedSimpleFeatureCollection wrapper that in turn killed
        // some visit optimizations
        while (fc instanceof DecoratingFeatureCollection) {
            Assert.assertThat(fc, not(instanceOf(SortedSimpleFeatureCollection.class)));
            Field field = FieldUtils.getDeclaredField(SortedSimpleFeatureCollection.class, "delegate");
            field.setAccessible(true);
            Object delegate = field.get(fc);
            fc = ((SimpleFeatureCollection) (delegate));
        } 
    }

    @Test
    public void testDefaultGeometry() throws IOException {
        FeatureTypeInfo featureType = getCatalog().getResourceByName("cdf", "Nulls", FeatureTypeInfo.class);
        GeometryDescriptor schemaDefaultGeometry = featureType.getFeatureType().getGeometryDescriptor();
        FeatureIterator i = featureType.getFeatureSource(null, null).getFeatures().features();
        GeometryDescriptor featureDefaultGeometry = i.next().getDefaultGeometryProperty().getDescriptor();
        Assert.assertNotNull(schemaDefaultGeometry);
        Assert.assertNotNull(featureDefaultGeometry);
        Assert.assertEquals("pointProperty", schemaDefaultGeometry.getLocalName());
        Assert.assertEquals(schemaDefaultGeometry, featureDefaultGeometry);
    }
}

