/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.feature.retype;


import Transaction.AUTO_COMMIT;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.easymock.EasyMock;
import org.geoserver.data.test.MockData;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureLock;
import org.geotools.data.FeatureLockFactory;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureLocking;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;


public class FullyRetypingDataStoreTest {
    SimpleFeatureType primitive;

    static final String RENAMED = "primitive";

    RetypingDataStore rts;

    private File data;

    private Id fidFilter;

    private String fid;

    @Test
    public void testLookupFeatureType() throws Exception {
        try {
            rts.getSchema(MockData.GENERICENTITY.getLocalPart());
            Assert.fail("The original type name should not be visible");
        } catch (IOException e) {
            // cool, as expected
        }
        final SimpleFeatureType schema = rts.getSchema(FullyRetypingDataStoreTest.RENAMED);
        Assert.assertNotNull(schema);
        Assert.assertEquals(primitive, schema);
    }

    @Test
    public void testGetFeaturesFeatureSource() throws Exception {
        // check the schemas in feature source and feature collection
        SimpleFeatureSource fs = rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED);
        Assert.assertEquals(primitive, fs.getSchema());
        SimpleFeatureCollection fc = fs.getFeatures();
        Assert.assertEquals(primitive, fc.getSchema());
        Assert.assertTrue(((fc.size()) > 0));
        // make sure the feature schema is good as well
        FeatureIterator<SimpleFeature> it = fc.features();
        SimpleFeature sf = it.next();
        it.close();
        Assert.assertEquals(primitive, sf.getFeatureType());
        // check the feature ids have been renamed as well
        Assert.assertTrue(("Feature id has not been renamed, it's still " + (sf.getID())), sf.getID().startsWith(FullyRetypingDataStoreTest.RENAMED));
        // check mappings occurred
        Assert.assertEquals("description-f001", sf.getAttribute("description"));
        Assert.assertTrue(new WKTReader().read("MULTIPOINT(39.73245 2.00342)").equalsExact(((Geometry) (sf.getAttribute("pointProperty")))));
        Assert.assertEquals(Long.valueOf(155), sf.getAttribute("intProperty"));
        Assert.assertNull(sf.getAttribute("newProperty"));
    }

    @Test
    public void testGetFeaturesReader() throws Exception {
        FeatureReader<SimpleFeatureType, SimpleFeature> fr;
        fr = rts.getFeatureReader(new Query(FullyRetypingDataStoreTest.RENAMED), AUTO_COMMIT);
        SimpleFeature sf = fr.next();
        fr.close();
        Assert.assertEquals(primitive, sf.getFeatureType());
        // check the feature ids have been renamed as well
        Assert.assertTrue(("Feature id has not been renamed, it's still " + (sf.getID())), sf.getID().startsWith(FullyRetypingDataStoreTest.RENAMED));
    }

    @Test
    public void testFeautureSourceFidFilter() throws Exception {
        // grab the last feature in the collection (there are more than one)
        SimpleFeatureSource fs = rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED);
        // build a filter that will retrieve that feature only
        FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
        final String fid = (FullyRetypingDataStoreTest.RENAMED) + ".f001";
        Filter fidFilter = ff.id(Collections.singleton(ff.featureId(fid)));
        SimpleFeatureCollection fc = fs.getFeatures(new Query(FullyRetypingDataStoreTest.RENAMED, fidFilter));
        Assert.assertEquals(FullyRetypingDataStoreTest.RENAMED, fc.getSchema().getName().getLocalPart());
        Assert.assertEquals(1, fc.size());
        FeatureIterator<SimpleFeature> it = fc.features();
        Assert.assertTrue(it.hasNext());
        SimpleFeature sf = it.next();
        Assert.assertFalse(it.hasNext());
        it.close();
        Assert.assertEquals(fid, sf.getID());
    }

    @Test
    public void testFeatureReaderFidFilter() throws Exception {
        FeatureReader<SimpleFeatureType, SimpleFeature> fr;
        fr = rts.getFeatureReader(new Query(FullyRetypingDataStoreTest.RENAMED, fidFilter), AUTO_COMMIT);
        Assert.assertEquals(primitive, fr.getFeatureType());
        Assert.assertTrue(fr.hasNext());
        SimpleFeature sf = fr.next();
        Assert.assertFalse(fr.hasNext());
        fr.close();
        Assert.assertEquals(fid, sf.getID());
    }

    @Test
    public void testDelete() throws Exception {
        final Query queryAll = new Query(FullyRetypingDataStoreTest.RENAMED);
        SimpleFeatureStore store;
        store = ((SimpleFeatureStore) (rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED)));
        int count = store.getCount(queryAll);
        store.removeFeatures(fidFilter);
        Assert.assertEquals((count - 1), store.getCount(queryAll));
    }

    @Test
    public void testModify() throws Exception {
        final Query queryAll = new Query(FullyRetypingDataStoreTest.RENAMED);
        SimpleFeatureStore store;
        store = ((SimpleFeatureStore) (rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED)));
        SimpleFeature original = store.getFeatures(fidFilter).features().next();
        // test a non mapped attribute
        String newDescription = ((String) (original.getAttribute("description"))) + " xxx";
        store.modifyFeatures(original.getFeatureType().getDescriptor("description"), newDescription, fidFilter);
        SimpleFeature modified = store.getFeatures(fidFilter).features().next();
        Assert.assertEquals(newDescription, modified.getAttribute("description"));
        // test a mapped attribute
        MultiPoint mpo = ((MultiPoint) (original.getAttribute("pointProperty")));
        MultiPoint mpm = mpo.getFactory().createMultiPoint(new Coordinate[]{ new Coordinate(10, 12) });
        store.modifyFeatures(original.getFeatureType().getDescriptor("pointProperty"), mpm, fidFilter);
        modified = store.getFeatures(fidFilter).features().next();
        Assert.assertTrue(mpm.equalsExact(((Geometry) (modified.getAttribute("pointProperty")))));
    }

    /**
     * This test is made with mock objects because the property data store does not generate fids in
     * the <type>.<id> form
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAppend() throws Exception {
        SimpleFeatureType type = DataUtilities.createType("trees", "the_geom:Point,FID:String,NAME:String");
        SimpleFeatureStore fs = EasyMock.createMock(SimpleFeatureStore.class);
        EasyMock.expect(fs.addFeatures(EasyMock.isA(FeatureCollection.class))).andReturn(Collections.singletonList(((FeatureId) (new FeatureIdImpl("trees.105")))));
        EasyMock.replay(fs);
        DataStore ds = EasyMock.createMock(DataStore.class);
        EasyMock.expect(ds.getTypeNames()).andReturn(new String[]{ "trees" }).anyTimes();
        EasyMock.expect(ds.getSchema("trees")).andReturn(type).anyTimes();
        EasyMock.expect(ds.getFeatureSource("trees")).andReturn(fs);
        EasyMock.replay(ds);
        RetypingDataStore rts = new RetypingDataStore(ds) {
            @Override
            protected String transformFeatureTypeName(String originalName) {
                return "oaks";
            }
        };
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(type);
        WKTReader reader = new WKTReader();
        sfb.set("the_geom", reader.read("POINT (0.002 0.0008)"));
        sfb.set("FID", "023");
        sfb.set("NAME", "Old oak");
        SimpleFeature feature = sfb.buildFeature(null);
        SimpleFeatureCollection fc = DataUtilities.collection(feature);
        SimpleFeatureStore store = ((SimpleFeatureStore) (rts.getFeatureSource("oaks")));
        List<FeatureId> ids = store.addFeatures(fc);
        Assert.assertEquals(1, ids.size());
        String id = getID();
        Assert.assertTrue((("Id does not start with " + ("oaks" + " it's ")) + id), id.startsWith("oaks"));
    }

    @Test
    public void testLockUnlockFilter() throws Exception {
        SimpleFeatureLocking fl;
        fl = ((SimpleFeatureLocking) (rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED)));
        final FeatureLock lock = FeatureLockFactory.generate(((10 * 60) * 1000));
        Transaction t = new DefaultTransaction();
        t.addAuthorization(lock.getAuthorization());
        fl.setTransaction(t);
        fl.setFeatureLock(lock);
        SimpleFeatureLocking fl2;
        fl2 = ((SimpleFeatureLocking) (rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED)));
        fl.setFeatureLock(lock);
        fl2.setTransaction(new DefaultTransaction());
        Assert.assertEquals(1, fl.lockFeatures(fidFilter));
        Assert.assertEquals(0, fl2.lockFeatures(fidFilter));
        fl.unLockFeatures(fidFilter);
        Assert.assertEquals(1, fl2.lockFeatures(fidFilter));
    }

    @Test
    public void testLockUnlockQuery() throws Exception {
        SimpleFeatureLocking fl;
        fl = ((SimpleFeatureLocking) (rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED)));
        final FeatureLock lock = FeatureLockFactory.generate(((10 * 60) * 1000));
        Transaction t = new DefaultTransaction();
        t.addAuthorization(lock.getAuthorization());
        fl.setTransaction(t);
        fl.setFeatureLock(lock);
        SimpleFeatureLocking fl2;
        fl2 = ((SimpleFeatureLocking) (rts.getFeatureSource(FullyRetypingDataStoreTest.RENAMED)));
        fl.setFeatureLock(lock);
        fl2.setTransaction(new DefaultTransaction());
        Query q = new Query(FullyRetypingDataStoreTest.RENAMED, fidFilter);
        Assert.assertEquals(1, fl.lockFeatures(q));
        Assert.assertEquals(0, fl2.lockFeatures(q));
        fl.unLockFeatures(q);
        Assert.assertEquals(1, fl2.lockFeatures(q));
    }
}

