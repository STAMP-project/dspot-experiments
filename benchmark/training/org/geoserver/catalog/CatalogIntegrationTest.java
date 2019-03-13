/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;


import ModificationType.DELETE;
import Resource.Type.RESOURCE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geoserver.catalog.event.CatalogEvent;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.config.GeoServerConfigPersister;
import org.geoserver.data.test.MockData;
import org.geoserver.platform.resource.Resource;
import org.geoserver.security.decorators.SecuredLayerGroupInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


@Category(SystemTest.class)
@TestSetup(run = TestSetupFrequency.REPEAT)
public class CatalogIntegrationTest extends GeoServerSystemTestSupport {
    @Test
    public void testWorkspaceRemoveAndReadd() {
        // remove all workspaces
        Catalog catalog = getCatalog();
        NamespaceInfo defaultNamespace = catalog.getDefaultNamespace();
        WorkspaceInfo defaultWs = catalog.getDefaultWorkspace();
        List<WorkspaceInfo> workspaces = catalog.getWorkspaces();
        CascadeDeleteVisitor visitor = new CascadeDeleteVisitor(catalog);
        for (WorkspaceInfo ws : workspaces) {
            visitor.visit(ws);
        }
        Assert.assertEquals(0, catalog.getWorkspaces().size());
        Assert.assertEquals(0, catalog.getNamespaces().size());
        // add back one (this would NPE)
        catalog.add(defaultNamespace);
        catalog.add(defaultWs);
        Assert.assertEquals(1, catalog.getWorkspaces().size());
        Assert.assertEquals(1, catalog.getNamespaces().size());
        // get back by name (this would NPE too)
        Assert.assertNotNull(catalog.getNamespaceByURI(defaultNamespace.getURI()));
    }

    /**
     * Checks that the namespace/workspace listener keeps on working after a catalog reload
     */
    @Test
    public void testNamespaceWorkspaceListenerAttached() throws Exception {
        Catalog catalog = getCatalog();
        NamespaceInfo ns = catalog.getNamespaceByPrefix(MockData.CITE_PREFIX);
        String newName = "XYWZ1234";
        ns.setPrefix(newName);
        catalog.save(ns);
        Assert.assertNotNull(catalog.getWorkspaceByName(newName));
        Assert.assertNotNull(catalog.getNamespaceByPrefix(newName));
        // force a reload
        int listenersBefore = catalog.getListeners().size();
        getGeoServer().reload();
        int listenersAfter = catalog.getListeners().size();
        Assert.assertEquals(listenersBefore, listenersAfter);
        // check the NamespaceWorkspaceListener is still attached and working
        ns = catalog.getNamespaceByPrefix(newName);
        ns.setPrefix(MockData.CITE_PREFIX);
        catalog.save(ns);
        Assert.assertNotNull(catalog.getWorkspaceByName(MockData.CITE_PREFIX));
        // make sure we only have one resource pool listener and one catalog persister
        int countCleaner = 0;
        int countPersister = 0;
        for (CatalogListener listener : catalog.getListeners()) {
            if (listener instanceof ResourcePool.CacheClearingListener) {
                countCleaner++;
            } else
                if (listener instanceof GeoServerConfigPersister) {
                    countPersister++;
                }

        }
        Assert.assertEquals(1, countCleaner);
        Assert.assertEquals(1, countPersister);
    }

    @Test
    public void modificationProxySerializeTest() throws Exception {
        Catalog catalog = getCatalog();
        // workspace
        WorkspaceInfo ws = catalog.getWorkspaceByName(MockData.CITE_PREFIX);
        WorkspaceInfo ws2 = serialize(ws);
        Assert.assertSame(ModificationProxy.unwrap(ws), ModificationProxy.unwrap(ws2));
        // namespace
        NamespaceInfo ns = catalog.getNamespaceByPrefix(MockData.CITE_PREFIX);
        NamespaceInfo ns2 = serialize(ns);
        Assert.assertSame(ModificationProxy.unwrap(ns), ModificationProxy.unwrap(ns2));
        // data store and related objects
        DataStoreInfo ds = catalog.getDataStoreByName(MockData.CITE_PREFIX);
        DataStoreInfo ds2 = serialize(ds);
        Assert.assertSame(ModificationProxy.unwrap(ds), ModificationProxy.unwrap(ds2));
        Assert.assertSame(ModificationProxy.unwrap(ds.getWorkspace()), ModificationProxy.unwrap(ds2.getWorkspace()));
        // coverage store and related objects
        CoverageStoreInfo cs = catalog.getCoverageStoreByName(MockData.TASMANIA_DEM.getLocalPart());
        CoverageStoreInfo cs2 = serialize(cs);
        Assert.assertSame(ModificationProxy.unwrap(cs), ModificationProxy.unwrap(cs2));
        Assert.assertSame(ModificationProxy.unwrap(cs.getWorkspace()), ModificationProxy.unwrap(cs2.getWorkspace()));
        // feature type and related objects
        FeatureTypeInfo ft = catalog.getFeatureTypeByName(getLayerId(MockData.BRIDGES));
        FeatureTypeInfo ft2 = serialize(ft);
        Assert.assertSame(ModificationProxy.unwrap(ft), ModificationProxy.unwrap(ft2));
        Assert.assertSame(ModificationProxy.unwrap(ft.getStore()), ModificationProxy.unwrap(ft2.getStore()));
        // coverage and related objects
        CoverageInfo ci = catalog.getCoverageByName(getLayerId(MockData.TASMANIA_DEM));
        CoverageInfo ci2 = serialize(ci);
        Assert.assertSame(ModificationProxy.unwrap(ci), ModificationProxy.unwrap(ci2));
        Assert.assertSame(ModificationProxy.unwrap(ci.getStore()), ModificationProxy.unwrap(ci.getStore()));
        // style
        StyleInfo streamsStyle = catalog.getStyleByName("Streams");
        StyleInfo si2 = serialize(streamsStyle);
        Assert.assertSame(ModificationProxy.unwrap(streamsStyle), ModificationProxy.unwrap(si2));
        // layer and related objects
        LayerInfo li = catalog.getLayerByName(getLayerId(MockData.BRIDGES));
        // ... let's add an extra style
        li.getStyles().add(streamsStyle);
        catalog.save(li);
        LayerInfo li2 = serialize(li);
        Assert.assertSame(ModificationProxy.unwrap(li), ModificationProxy.unwrap(li2));
        Assert.assertSame(ModificationProxy.unwrap(li.getResource()), ModificationProxy.unwrap(li2.getResource()));
        Assert.assertSame(ModificationProxy.unwrap(li.getDefaultStyle()), ModificationProxy.unwrap(li2.getDefaultStyle()));
        Assert.assertSame(ModificationProxy.unwrap(li.getStyles().iterator().next()), ModificationProxy.unwrap(li2.getStyles().iterator().next()));
        // try a group layer
        CatalogBuilder cb = new CatalogBuilder(catalog);
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS)));
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.PONDS)));
        lg.getStyles().add(null);
        lg.getStyles().add(null);
        cb.calculateLayerGroupBounds(lg);
        lg.setName("test-lg");
        catalog.add(lg);
        // ... make sure we get a proxy
        lg = catalog.getLayerGroupByName("test-lg");
        if (lg instanceof SecuredLayerGroupInfo) {
            lg = unwrap(LayerGroupInfo.class);
        }
        LayerGroupInfo lg2 = serialize(lg);
        Assert.assertSame(ModificationProxy.unwrap(lg), ModificationProxy.unwrap(lg2));
        Assert.assertSame(ModificationProxy.unwrap(lg.getLayers().get(0)), ModificationProxy.unwrap(lg2.getLayers().get(0)));
        Assert.assertSame(ModificationProxy.unwrap(lg.getLayers().get(1)), ModificationProxy.unwrap(lg2.getLayers().get(1)));
        // now check a half modified proxy
        LayerInfo lim = catalog.getLayerByName(getLayerId(MockData.BRIDGES));
        // ... let's add an extra style
        lim.setDefaultStyle(streamsStyle);
        lim.getStyles().add(streamsStyle);
        // clone and check
        LayerInfo lim2 = serialize(lim);
        Assert.assertSame(ModificationProxy.unwrap(lim.getDefaultStyle()), ModificationProxy.unwrap(lim2.getDefaultStyle()));
        Assert.assertSame(ModificationProxy.unwrap(lim.getStyles().iterator().next()), ModificationProxy.unwrap(lim2.getStyles().iterator().next()));
        // mess a bit with the metadata map too
        String key = "workspaceKey";
        lim.getMetadata().put(key, ws);
        LayerInfo lim3 = serialize(lim);
        Assert.assertSame(ModificationProxy.unwrap(lim), ModificationProxy.unwrap(lim3));
        Assert.assertSame(ModificationProxy.unwrap(lim.getMetadata().get(key)), ModificationProxy.unwrap(lim3.getMetadata().get(key)));
    }

    @Test
    public void testCascadeDeleteWorkspaceSpecific() throws Exception {
        Catalog catalog = getCatalog();
        WorkspaceInfo ws = catalog.getWorkspaceByName(MockData.ROAD_SEGMENTS.getPrefix());
        // create a workspace specific group
        CatalogBuilder cb = new CatalogBuilder(catalog);
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS)));
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.STREAMS)));
        lg.getStyles().add(null);
        lg.getStyles().add(null);
        cb.calculateLayerGroupBounds(lg);
        lg.setName("test-lg");
        lg.setWorkspace(ws);
        catalog.add(lg);
        // make a style a workspace specific
        StyleInfo style = catalog.getStyleByName(MockData.ROAD_SEGMENTS.getLocalPart());
        style.setWorkspace(ws);
        catalog.save(style);
        // check we are getting the groups and styles reported properly
        CascadeRemovalReporter reporter = new CascadeRemovalReporter(catalog);
        ws.accept(reporter);
        List<StyleInfo> styles = reporter.getObjects(StyleInfo.class, DELETE);
        Assert.assertEquals(1, styles.size());
        Assert.assertEquals(style, styles.get(0));
        List<LayerGroupInfo> groups = reporter.getObjects(LayerGroupInfo.class, DELETE);
        Assert.assertEquals(1, groups.size());
        Assert.assertEquals(lg, groups.get(0));
        // now remove for real
        CascadeDeleteVisitor remover = new CascadeDeleteVisitor(catalog);
        ws.accept(remover);
        Assert.assertNull(catalog.getWorkspaceByName(ws.getName()));
        Assert.assertNull(catalog.getStyleByName(style.getName()));
        Assert.assertNull(catalog.getLayerGroupByName(lg.getName()));
    }

    @Test
    public void testReprojectLayerGroup() throws Exception, FactoryException, NoSuchAuthorityCodeException {
        Catalog catalog = getCatalog();
        CatalogBuilder cb = new CatalogBuilder(catalog);
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        LayerInfo l = catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS));
        lg.getLayers().add(l);
        lg.getStyles().add(null);
        lg.setName("test-reproject");
        // EPSG:4901 "equivalent" but different uom
        String wkt = "GEOGCS[\"GCS_ATF_Paris\",DATUM[\"D_ATF\",SPHEROID[\"Plessis_1817\",6376523.0,308.64]],PRIMEM[\"Paris\",2.337229166666667],UNIT[\"Grad\",0.01570796326794897]]";
        CoordinateReferenceSystem lCrs = CRS.parseWKT(wkt);
        setSRS(null);
        ((FeatureTypeInfo) (l.getResource())).setNativeCRS(lCrs);
        Assert.assertNull(CRS.lookupEpsgCode(lCrs, false));
        // Use the real thing now
        CoordinateReferenceSystem lgCrs = CRS.decode("EPSG:4901");
        Assert.assertNotNull(CRS.lookupEpsgCode(lgCrs, false));
        // Reproject our layer group to EPSG:4901. We expect it to have an EPSG code.
        cb.calculateLayerGroupBounds(lg, lgCrs);
        Assert.assertNotNull(CRS.lookupEpsgCode(lg.getBounds().getCoordinateReferenceSystem(), false));
    }

    @Test
    public void testLayerGroupNullLayerOrStyleReferences() throws IOException {
        Catalog catalog = getCatalog();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        LayerInfo l = catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS));
        StyleInfo s = catalog.getStyleByName("singleStyleGroup");
        lg.setWorkspace(null);
        lg.setName("threeTypeLayerGroup");
        // layer with default style
        lg.getLayers().add(l);
        lg.getStyles().add(null);
        // style group
        lg.getLayers().add(null);
        lg.getStyles().add(s);
        lg.getLayers().add(null);
        lg.getStyles().add(null);
        catalog.add(lg);
        LayerGroupInfo resolved = catalog.getLayerGroupByName("threeTypeLayerGroup");
        Assert.assertEquals(2, resolved.layers().size());
        Assert.assertEquals(2, resolved.styles().size());
        Assert.assertEquals(l, resolved.layers().get(0));
        Assert.assertEquals(s.getStyle(), resolved.styles().get(1).getStyle());
    }

    @Test
    public void testInvalidStyleGroup() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        StyleInfo s = catalog.getStyleByName("polygon");
        lg.setWorkspace(null);
        lg.setName("invalidStyleLayerGroup");
        lg.getLayers().add(null);
        lg.getStyles().add(s);
        try {
            catalog.add(lg);
            Assert.fail("Should not be able to add an invalid style group to the catalog");
        } catch (Exception e) {
            Assert.assertEquals("Invalid style group: No layer or layer group named 'Default Polygon' found in the catalog", e.getMessage());
        }
    }

    @Test
    public void testSingleStyleGroup() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        StyleInfo s = catalog.getStyleByName("singleStyleGroup");
        lg.setWorkspace(null);
        lg.setName("singleStyleLayerGroup");
        lg.getLayers().add(null);
        lg.getStyles().add(s);
        catalog.add(lg);
        LayerGroupInfo resolved = catalog.getLayerGroupByName("singleStyleLayerGroup");
        Assert.assertEquals(1, resolved.layers().size());
        Assert.assertEquals(1, resolved.styles().size());
        Assert.assertEquals(s.getStyle(), resolved.styles().get(0).getStyle());
        // Test bounds calculation
        calculateBounds();
        Assert.assertEquals(catalog.getLayerByName(getLayerId(MockData.STREAMS)).getResource().getLatLonBoundingBox(), lg.getBounds());
    }

    @Test
    public void testMultiStyleGroup() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        StyleInfo s = catalog.getStyleByName("multiStyleGroup");
        LayerGroupInfo nestedLg = catalog.getFactory().createLayerGroup();
        nestedLg.setName("nestedLayerGroup");
        nestedLg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.PONDS)));
        nestedLg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.BUILDINGS)));
        nestedLg.getStyles().add(null);
        nestedLg.getStyles().add(null);
        catalog.add(nestedLg);
        lg.setWorkspace(null);
        lg.setName("multiStyleLayerGroup");
        lg.getLayers().add(null);
        lg.getStyles().add(s);
        catalog.add(lg);
        LayerGroupInfo resolved = catalog.getLayerGroupByName("multiStyleLayerGroup");
        List<LayerInfo> layers = resolved.layers();
        List<StyleInfo> styles = resolved.styles();
        Assert.assertEquals(6, layers.size());
        Assert.assertEquals(6, styles.size());
        // assertEquals(s.getStyle(), resolved.styles().get(0).getStyle());
        // named layer with user style -> 1 layer
        Assert.assertEquals(catalog.getLayerByName(getLayerId(MockData.STREAMS)), layers.get(0));
        Assert.assertTrue(((styles.get(0).getStyle().featureTypeStyles().get(0).rules().get(0).getSymbolizers()[0]) instanceof LineSymbolizer));
        // named layer with user style + named style -> 2 layers
        Assert.assertEquals(catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS)), layers.get(1));
        Assert.assertTrue(((styles.get(1).getStyle().featureTypeStyles().get(0).rules().get(0).getSymbolizers()[0]) instanceof LineSymbolizer));
        Assert.assertEquals(catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS)), layers.get(2));
        Assert.assertEquals(catalog.getStyleByName("line").getStyle(), styles.get(2).getStyle());
        // named layer (layer group) with no style -> 2 layers
        Assert.assertEquals(catalog.getLayerByName(getLayerId(MockData.PONDS)), layers.get(3));
        Assert.assertEquals(catalog.getLayerByName(getLayerId(MockData.BUILDINGS)), layers.get(4));
        // user layer (inline feature) with user style -> 1 layer
        SimpleFeature inlineFeature = ((SimpleFeature) (((FeatureTypeInfo) (layers.get(5).getResource())).getFeatureSource(null, null).getFeatures().features().next()));
        Assert.assertTrue(((inlineFeature.getDefaultGeometry()) instanceof Point));
        Assert.assertEquals("POINT (115.741666667 -64.6583333333)", toText());
        Assert.assertTrue(((styles.get(5).getStyle().featureTypeStyles().get(0).rules().get(0).getSymbolizers()[0]) instanceof PointSymbolizer));
        // Test bounds calculation
        calculateBounds();
        Assert.assertEquals(new org.geotools.geometry.jts.ReferencedEnvelope((-180), 180, (-90), 90, DefaultGeographicCRS.WGS84), lg.getBounds());
    }

    @Test
    public void testRecursiveStyleGroup() throws IOException {
        Catalog catalog = getCatalog();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        StyleInfo s = catalog.getStyleByName("recursiveStyleGroup");
        lg.setWorkspace(null);
        lg.setName("recursiveLayerGroup");
        lg.getLayers().add(null);
        lg.getStyles().add(s);
        try {
            catalog.add(lg);
            Assert.fail("Should not be able to add invalid layer group to catalog");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testRenameWorspaceAfterReload() throws Exception {
        // reload
        getGeoServer().reload();
        // rename workspace
        Catalog catalog = getCatalog();
        List<CatalogEvent> events = new ArrayList<>();
        final WorkspaceInfo ws = catalog.getDefaultWorkspace();
        String name = ws.getName();
        try {
            final String newName = "renamed_" + name;
            ws.setName(newName);
            catalog.save(ws);
            // check rename occurred
            final WorkspaceInfo wsRenamed = getCatalog().getWorkspaceByName(newName);
            Assert.assertNotNull(wsRenamed);
            Assert.assertEquals(newName, wsRenamed.getName());
            final NamespaceInfo nsRenamed = getCatalog().getNamespaceByPrefix(newName);
            Assert.assertNotNull(nsRenamed);
            Assert.assertEquals(newName, nsRenamed.getName());
            // do a reload
            getGeoServer().reload();
            // check it was actually successfully stored. Get the catalog again,
            // as it has been replaced
            catalog = getCatalog();
            final WorkspaceInfo wsRenamed2 = catalog.getWorkspaceByName(newName);
            Assert.assertNotNull(wsRenamed2);
            Assert.assertEquals(newName, wsRenamed.getName());
            final NamespaceInfo nsRenamed2 = getCatalog().getNamespaceByPrefix(newName);
            Assert.assertNotNull(nsRenamed2);
            Assert.assertEquals(newName, nsRenamed2.getName());
            // the old one is gone, too
            Assert.assertNull(catalog.getWorkspaceByName(name));
        } finally {
            ws.setName(name);
            catalog.save(ws);
        }
    }

    @Test
    public void testReloadDefaultStyles() throws Exception {
        // clear up all "point" styles
        final Resource styles = getDataDirectory().getStyles();
        styles.list().stream().filter(( r) -> ((r.getType()) == Resource.Type.RESOURCE) && (r.name().contains("point"))).forEach(( r) -> r.delete());
        // reload
        getGeoServer().reload();
        // check the default point style has been re-created
        final StyleInfo point = getCatalog().getStyleByName("point");
        Assert.assertNotNull(point);
    }

    @Test
    public void testChangeStyleWorkspaceRelativeResources() throws Exception {
        // move style to a different workspace
        final Catalog catalog = getCatalog();
        final StyleInfo style = catalog.getStyleByName("relative");
        final WorkspaceInfo secondaryWs = catalog.getWorkspaceByName("secondary");
        style.setWorkspace(secondaryWs);
        catalog.save(style);
        // check the referenced image and svg has been moved keeping the relative position
        final Resource relativeImage = getDataDirectory().getStyles(secondaryWs, "images", "rockFillSymbol.png");
        Assert.assertEquals(RESOURCE, relativeImage.getType());
        final Resource relativeSvg = getDataDirectory().getStyles(secondaryWs, "images", "square16.svg");
        Assert.assertEquals(RESOURCE, relativeSvg.getType());
    }
}

