/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.eo;


import Query.ALL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geoserver.opensearch.eo.store.OpenSearchAccess;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsEqualTo;


public class JDBCOpenSearchAccessTest {
    public static final String TEST_NAMESPACE = "http://www.test.com/os/eo";

    private static final Name LAYERS_NAME = OpenSearchAccess.LAYERS_PROPERTY_NAME;

    private static JDBCDataStore store;

    private static OpenSearchAccess osAccess;

    private static FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    public static final ProductClass GS_PRODUCT = new ProductClass("geoServer", "gs", "http://www.geoserver.org/eo/test");

    @Test
    public void testCollectionFeatureType() throws Exception {
        // check expected name
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getCollectionSource().getSchema();
        Name name = schema.getName();
        Assert.assertEquals(JDBCOpenSearchAccessTest.TEST_NAMESPACE, name.getNamespaceURI());
        Assert.assertThat(name.getLocalPart(), Matchers.equalToIgnoringCase("collection"));
        // test the schema
        assertPropertyNamespace(schema, "wavelength", EO_NAMESPACE);
    }

    @Test
    public void testProductFeatureType() throws Exception {
        // check expected name
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getProductSource().getSchema();
        Name name = schema.getName();
        Assert.assertEquals(JDBCOpenSearchAccessTest.TEST_NAMESPACE, name.getNamespaceURI());
        Assert.assertThat(name.getLocalPart(), Matchers.equalToIgnoringCase("product"));
        // get the schema
        assertPropertyNamespace(schema, "cloudCover", ProductClass.OPTICAL.getNamespace());
        assertPropertyNamespace(schema, "track", ProductClass.GENERIC.getNamespace());
        assertPropertyNamespace(schema, "polarisationMode", ProductClass.RADAR.getNamespace());
        assertPropertyNamespace(schema, "test", JDBCOpenSearchAccessTest.GS_PRODUCT.getNamespace());
    }

    @Test
    public void testTypeNames() throws Exception {
        List<Name> names = JDBCOpenSearchAccessTest.osAccess.getNames();
        // product, collection, SENTINEL1, SENTINEL2, LANDSAT8, ATM1,
        Assert.assertThat(names, Matchers.hasSize(27));
        Set<String> localNames = new HashSet<>();
        for (Name name : names) {
            Assert.assertEquals(JDBCOpenSearchAccessTest.TEST_NAMESPACE, name.getNamespaceURI());
            localNames.add(name.getLocalPart());
        }
        Assert.assertThat(localNames, Matchers.containsInAnyOrder("collection", "product", "SENTINEL1", "LANDSAT8", "GS_TEST", "ATMTEST", "SENTINEL2__B01", "SENTINEL2__B02", "SENTINEL2__B03", "SENTINEL2__B04", "SENTINEL2__B05", "SENTINEL2__B06", "SENTINEL2__B07", "SENTINEL2__B08", "SENTINEL2__B09", "SENTINEL2__B10", "SENTINEL2__B11", "SENTINEL2__B12", "LANDSAT8__B01", "LANDSAT8__B02", "LANDSAT8__B03", "LANDSAT8__B04", "LANDSAT8__B05", "LANDSAT8__B06", "LANDSAT8__B07", "LANDSAT8__B08", "LANDSAT8__B09"));
    }

    @Test
    public void testSentinel1Schema() throws Exception {
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getSchema(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "SENTINEL1"));
        assertGranulesViewSchema(schema, ProductClass.RADAR);
    }

    @Test
    public void testSentinel2Schema() throws Exception {
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getSchema(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "SENTINEL2__B01"));
        assertGranulesViewSchema(schema, ProductClass.OPTICAL);
    }

    @Test
    public void testLandsat8Schema() throws Exception {
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getSchema(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "LANDSAT8"));
        assertGranulesViewSchema(schema, ProductClass.OPTICAL);
    }

    @Test
    public void testCustomClassSchema() throws Exception {
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getSchema(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "GS_TEST"));
        assertGranulesViewSchema(schema, JDBCOpenSearchAccessTest.GS_PRODUCT);
    }

    @Test
    public void testSentinel1Granules() throws Exception {
        FeatureSource<FeatureType, Feature> featureSource = JDBCOpenSearchAccessTest.osAccess.getFeatureSource(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "SENTINEL1"));
        Assert.assertEquals(0, featureSource.getCount(ALL));
        FeatureCollection<FeatureType, Feature> fc = featureSource.getFeatures();
        Assert.assertEquals(0, fc.size());
        fc.accepts(( f) -> {
        }, null);// just check trying to scroll over the feature does not make it blow

    }

    @Test
    public void testSentinel2Granules() throws Exception {
        FeatureSource<FeatureType, Feature> featureSource = JDBCOpenSearchAccessTest.osAccess.getFeatureSource(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "SENTINEL2__B01"));
        FeatureCollection<FeatureType, Feature> fc = featureSource.getFeatures();
        assertGranulesViewSchema(fc.getSchema(), ProductClass.OPTICAL);
        Assert.assertThat(fc.size(), Matchers.greaterThan(1));
        fc.accepts(( f) -> {
            // check the primary key has been mapped
            assertThat(f, instanceOf(.class));
            SimpleFeature sf = ((SimpleFeature) (f));
            final String id = sf.getID();
            assertTrue(id.matches("\\w+\\.\\d+"));
        }, null);
    }

    @Test
    public void testCustomProductClassGranules() throws Exception {
        System.out.println(JDBCOpenSearchAccessTest.osAccess.getNames());
        FeatureSource<FeatureType, Feature> featureSource = JDBCOpenSearchAccessTest.osAccess.getFeatureSource(new NameImpl(JDBCOpenSearchAccessTest.TEST_NAMESPACE, "GS_TEST"));
        Assert.assertEquals(0, featureSource.getCount(ALL));
        FeatureCollection<FeatureType, Feature> fc = featureSource.getFeatures();
        assertGranulesViewSchema(fc.getSchema(), JDBCOpenSearchAccessTest.GS_PRODUCT);
        Assert.assertEquals(0, fc.size());
        fc.accepts(( f) -> {
        }, null);// just check trying to scroll over the feature does not make it blow

    }

    @Test
    public void testCollectionLayerInformation() throws Exception {
        // check expected property is there
        FeatureType schema = JDBCOpenSearchAccessTest.osAccess.getCollectionSource().getSchema();
        Name name = schema.getName();
        Assert.assertEquals(JDBCOpenSearchAccessTest.TEST_NAMESPACE, name.getNamespaceURI());
        final PropertyDescriptor layerDescriptor = schema.getDescriptor(JDBCOpenSearchAccessTest.LAYERS_NAME);
        Assert.assertNotNull(layerDescriptor);
        // read it
        FeatureSource<FeatureType, Feature> source = JDBCOpenSearchAccessTest.osAccess.getCollectionSource();
        Query q = new Query();
        q.setProperties(Arrays.asList(JDBCOpenSearchAccessTest.FF.property(JDBCOpenSearchAccessTest.LAYERS_NAME)));
        q.setFilter(JDBCOpenSearchAccessTest.FF.equal(JDBCOpenSearchAccessTest.FF.property(new NameImpl(OpenSearchAccess.EO_NAMESPACE, "identifier")), JDBCOpenSearchAccessTest.FF.literal("SENTINEL2"), false));
        FeatureCollection<FeatureType, Feature> features = source.getFeatures(q);
        // get the collection and check it
        Feature collection = DataUtilities.first(features);
        Assert.assertNotNull(collection);
        Property layerProperty = collection.getProperty(JDBCOpenSearchAccessTest.LAYERS_NAME);
        final Feature layerValue = ((Feature) (layerProperty));
        Assert.assertThat(layerValue, Matchers.notNullValue());
        Assert.assertEquals("gs", getAttribute(layerValue, "workspace"));
        Assert.assertEquals("sentinel2", getAttribute(layerValue, "layer"));
        Assert.assertEquals(Boolean.TRUE, getAttribute(layerValue, "separateBands"));
        Assert.assertThat(getAttribute(layerValue, "bands"), Matchers.equalTo(new String[]{ "B01", "B02", "B03", "B04", "B05", "B06", "B07", "B08", "B09", "B10", "B11", "B12" }));
        Assert.assertThat(getAttribute(layerValue, "browseBands"), Matchers.equalTo(new String[]{ "B04", "B03", "B02" }));
        Assert.assertEquals(Boolean.TRUE, getAttribute(layerValue, "heterogeneousCRS"));
        Assert.assertEquals("EPSG:4326", getAttribute(layerValue, "mosaicCRS"));
    }

    @Test
    public void testTwoCollectionLayers() throws Exception {
        // read it
        FeatureStore<FeatureType, Feature> store = ((FeatureStore<FeatureType, Feature>) (JDBCOpenSearchAccessTest.osAccess.getCollectionSource()));
        Query q = new Query();
        q.setProperties(Arrays.asList(JDBCOpenSearchAccessTest.FF.property(JDBCOpenSearchAccessTest.LAYERS_NAME)));
        final PropertyIsEqualTo filter = JDBCOpenSearchAccessTest.FF.equal(JDBCOpenSearchAccessTest.FF.property(new NameImpl(OpenSearchAccess.EO_NAMESPACE, "identifier")), JDBCOpenSearchAccessTest.FF.literal("LANDSAT8"), false);
        q.setFilter(filter);
        FeatureCollection<FeatureType, Feature> features = store.getFeatures(q);
        Map<String, SimpleFeature> layerFeatures = getLayerPropertiesFromCollection(features);
        Assert.assertThat(layerFeatures.keySet(), Matchers.hasItems("landsat8-SINGLE", "landsat8-SEPARATE"));
        // first layer
        SimpleFeature single = layerFeatures.get("landsat8-SINGLE");
        Assert.assertEquals("gs", getAttribute(single, "workspace"));
        Assert.assertEquals("landsat8-SINGLE", getAttribute(single, "layer"));
        Assert.assertEquals(Boolean.FALSE, getAttribute(single, "separateBands"));
        Assert.assertNull(getAttribute(single, "bands"));
        Assert.assertNull(getAttribute(single, "browseBands"));
        Assert.assertEquals(Boolean.TRUE, getAttribute(single, "heterogeneousCRS"));
        Assert.assertEquals("EPSG:4326", getAttribute(single, "mosaicCRS"));
        // second layer
        SimpleFeature separate = layerFeatures.get("landsat8-SEPARATE");
        Assert.assertEquals("gs", getAttribute(separate, "workspace"));
        Assert.assertEquals("landsat8-SEPARATE", getAttribute(separate, "layer"));
        Assert.assertEquals(Boolean.TRUE, getAttribute(separate, "separateBands"));
        Assert.assertThat(getAttribute(separate, "bands"), Matchers.equalTo(new String[]{ "B01", "B02", "B03", "B04", "B05", "B06", "B07", "B08", "B09" }));
        Assert.assertThat(getAttribute(separate, "browseBands"), Matchers.equalTo(new String[]{ "B04", "B03", "B02" }));
        Assert.assertEquals(Boolean.TRUE, getAttribute(separate, "heterogeneousCRS"));
        Assert.assertEquals("EPSG:4326", getAttribute(separate, "mosaicCRS"));
    }

    @Test
    public void testCollectionLayerUpdate() throws Exception {
        // read it
        FeatureStore<FeatureType, Feature> store = ((FeatureStore<FeatureType, Feature>) (JDBCOpenSearchAccessTest.osAccess.getCollectionSource()));
        Query q = new Query();
        q.setProperties(Arrays.asList(JDBCOpenSearchAccessTest.FF.property(JDBCOpenSearchAccessTest.LAYERS_NAME)));
        final PropertyIsEqualTo filter = JDBCOpenSearchAccessTest.FF.equal(JDBCOpenSearchAccessTest.FF.property(new NameImpl(OpenSearchAccess.EO_NAMESPACE, "identifier")), JDBCOpenSearchAccessTest.FF.literal("SENTINEL2"), false);
        q.setFilter(filter);
        FeatureCollection<FeatureType, Feature> features = store.getFeatures(q);
        final SimpleFeature layerValue = getLayerPropertyFromCollection(features);
        // modify it
        setAttribute(layerValue, "workspace", "gs2");
        setAttribute(layerValue, "layer", "sentinel12345");
        setAttribute(layerValue, "separateBands", false);
        setAttribute(layerValue, "bands", new String[]{ "B01", "B04", "B06" });
        setAttribute(layerValue, "browseBands", null);
        setAttribute(layerValue, "heterogeneousCRS", false);
        setAttribute(layerValue, "mosaicCRS", "EPSG:3857");
        ListFeatureCollection layers = new ListFeatureCollection(JDBCOpenSearchAccessTest.osAccess.getCollectionLayerSchema());
        layers.add(layerValue);
        // update the feature
        store.modifyFeatures(new Name[]{ JDBCOpenSearchAccessTest.LAYERS_NAME }, new Object[]{ layers }, filter);
        // read it back and check
        final Feature layerValue2 = getLayerPropertyFromCollection(store.getFeatures(q));
        Assert.assertEquals("gs2", getAttribute(layerValue2, "workspace"));
        Assert.assertEquals("sentinel12345", getAttribute(layerValue2, "layer"));
        Assert.assertEquals(Boolean.FALSE, getAttribute(layerValue2, "separateBands"));
        Assert.assertArrayEquals(new String[]{ "B01", "B04", "B06" }, ((String[]) (getAttribute(layerValue2, "bands"))));
        Assert.assertThat(getAttribute(layerValue2, "browseBands"), Matchers.nullValue());
        Assert.assertEquals(Boolean.FALSE, getAttribute(layerValue2, "heterogeneousCRS"));
        Assert.assertEquals("EPSG:3857", getAttribute(layerValue2, "mosaicCRS"));
    }

    @Test
    public void testCollectionLayerUpdateMulti() throws Exception {
        // read it
        FeatureStore<FeatureType, Feature> store = ((FeatureStore<FeatureType, Feature>) (JDBCOpenSearchAccessTest.osAccess.getCollectionSource()));
        Query q = new Query();
        q.setProperties(Arrays.asList(JDBCOpenSearchAccessTest.FF.property(JDBCOpenSearchAccessTest.LAYERS_NAME)));
        final PropertyIsEqualTo filter = JDBCOpenSearchAccessTest.FF.equal(JDBCOpenSearchAccessTest.FF.property(new NameImpl(OpenSearchAccess.EO_NAMESPACE, "identifier")), JDBCOpenSearchAccessTest.FF.literal("LANDSAT8"), false);
        q.setFilter(filter);
        FeatureCollection<FeatureType, Feature> features = store.getFeatures(q);
        Map<String, SimpleFeature> layerFeatures = getLayerPropertiesFromCollection(features);
        Assert.assertThat(layerFeatures.keySet(), Matchers.hasItems("landsat8-SINGLE", "landsat8-SEPARATE"));
        SimpleFeature layerSingle = layerFeatures.get("landsat8-SINGLE");
        // modify the single layer one
        setAttribute(layerSingle, "workspace", "gs2");
        setAttribute(layerSingle, "layer", "landsat-foobar");
        setAttribute(layerSingle, "separateBands", false);
        setAttribute(layerSingle, "bands", new String[]{ "B01", "B04", "B06" });
        setAttribute(layerSingle, "browseBands", null);
        setAttribute(layerSingle, "heterogeneousCRS", false);
        setAttribute(layerSingle, "mosaicCRS", "EPSG:3857");
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(JDBCOpenSearchAccessTest.osAccess.getCollectionLayerSchema());
        fb.set("workspace", "gs2");
        fb.set("layer", "landsat-third");
        fb.set("separateBands", false);
        fb.set("bands", null);
        fb.set("browseBands", null);
        fb.set("heterogeneousCRS", true);
        fb.set("mosaicCRS", "EPSG:32632");
        SimpleFeature newLayer = fb.buildFeature(null);
        // new list of layers, some will be gone
        ListFeatureCollection layers = new ListFeatureCollection(JDBCOpenSearchAccessTest.osAccess.getCollectionLayerSchema());
        layers.add(layerSingle);
        layers.add(newLayer);
        // update the feature
        store.modifyFeatures(new Name[]{ JDBCOpenSearchAccessTest.LAYERS_NAME }, new Object[]{ layers }, filter);
        // read it back and check
        layerFeatures = getLayerPropertiesFromCollection(features);
        Assert.assertThat(layerFeatures.keySet(), Matchers.hasItems("landsat-foobar", "landsat-third"));
        final Feature layerFooBar = layerFeatures.get("landsat-foobar");
        Assert.assertEquals("gs2", getAttribute(layerFooBar, "workspace"));
        Assert.assertEquals("landsat-foobar", getAttribute(layerFooBar, "layer"));
        Assert.assertEquals(Boolean.FALSE, getAttribute(layerFooBar, "separateBands"));
        Assert.assertArrayEquals(new String[]{ "B01", "B04", "B06" }, ((String[]) (getAttribute(layerFooBar, "bands"))));
        Assert.assertThat(getAttribute(layerFooBar, "browseBands"), Matchers.nullValue());
        Assert.assertEquals(Boolean.FALSE, getAttribute(layerFooBar, "heterogeneousCRS"));
        Assert.assertEquals("EPSG:3857", getAttribute(layerFooBar, "mosaicCRS"));
        final Feature layerThird = layerFeatures.get("landsat-third");
        Assert.assertEquals("gs2", getAttribute(layerThird, "workspace"));
        Assert.assertEquals("landsat-third", getAttribute(layerThird, "layer"));
        Assert.assertEquals(Boolean.FALSE, getAttribute(layerThird, "separateBands"));
        Assert.assertThat(getAttribute(layerThird, "bands"), Matchers.nullValue());
        Assert.assertThat(getAttribute(layerThird, "browseBands"), Matchers.nullValue());
        Assert.assertEquals(Boolean.TRUE, getAttribute(layerThird, "heterogeneousCRS"));
        Assert.assertEquals("EPSG:32632", getAttribute(layerThird, "mosaicCRS"));
    }

    @Test
    public void testCollectionLayerRemoval() throws Exception {
        // read it
        FeatureStore<FeatureType, Feature> store = ((FeatureStore<FeatureType, Feature>) (JDBCOpenSearchAccessTest.osAccess.getCollectionSource()));
        Query q = new Query();
        q.setProperties(Arrays.asList(JDBCOpenSearchAccessTest.FF.property(JDBCOpenSearchAccessTest.LAYERS_NAME)));
        final PropertyIsEqualTo filter = JDBCOpenSearchAccessTest.FF.equal(JDBCOpenSearchAccessTest.FF.property(new NameImpl(OpenSearchAccess.EO_NAMESPACE, "identifier")), JDBCOpenSearchAccessTest.FF.literal("SENTINEL2"), false);
        q.setFilter(filter);
        // update the feature to remove the layer information
        store.modifyFeatures(new Name[]{ OpenSearchAccess.LAYERS_PROPERTY_NAME }, new Object[]{ null }, filter);
        // read it back and check it's not set
        Feature collection = DataUtilities.first(store.getFeatures(q));
        Assert.assertNotNull(collection);
        Property layerProperty = collection.getProperty(JDBCOpenSearchAccessTest.LAYERS_NAME);
        Assert.assertNull(layerProperty);
    }
}

