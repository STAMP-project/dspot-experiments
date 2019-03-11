/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import Filter.INCLUDE;
import org.geoserver.security.WrapperPolicy;
import org.geoserver.security.impl.SecureObjectsTest;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;


public class SecuredFeatureSourceTest extends SecureObjectsTest {
    @Test
    public void testReadOnlyFeatureSourceDataStore() throws Exception {
        // build up the mock
        DataStore ds = createNiceMock(DataStore.class);
        replay(ds);
        FeatureSource fs = createNiceMock(FeatureSource.class);
        FeatureCollection fc = createNiceMock(FeatureCollection.class);
        expect(fs.getDataStore()).andReturn(ds);
        expect(fs.getFeatures()).andReturn(fc).anyTimes();
        expect(fs.getFeatures(((Filter) (anyObject())))).andReturn(fc).anyTimes();
        expect(fs.getFeatures(((Query) (anyObject())))).andReturn(fc).anyTimes();
        replay(fs);
        SecuredFeatureSource ro = new SecuredFeatureSource(fs, WrapperPolicy.hide(null));
        Assert.assertTrue(((ro.getDataStore()) instanceof ReadOnlyDataStore));
        SecuredFeatureCollection collection = ((SecuredFeatureCollection) (ro.getFeatures()));
        Assert.assertTrue(ro.policy.isHide());
        Assert.assertTrue(((ro.getFeatures(INCLUDE)) instanceof SecuredFeatureCollection));
        Assert.assertTrue(((ro.getFeatures(new Query())) instanceof SecuredFeatureCollection));
    }

    @Test
    public void testReadOnlyFeatureStore() throws Exception {
        // build up the mock
        SimpleFeatureType schema = createNiceMock(SimpleFeatureType.class);
        expect(schema.getName()).andReturn(new NameImpl("testFT"));
        replay(schema);
        FeatureStore fs = createNiceMock(FeatureStore.class);
        expect(fs.getSchema()).andReturn(schema);
        replay(fs);
        SecuredFeatureStore ro = new SecuredFeatureStore(fs, WrapperPolicy.readOnlyChallenge(null));
        try {
            ro.addFeatures(createNiceMock(FeatureCollection.class));
            Assert.fail("This should have thrown a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
    }

    @Test
    public void testReadOnlyFeatureSourceDataAccess() throws Exception {
        // build the mock up
        DataAccess da = createNiceMock(DataAccess.class);
        replay(da);
        FeatureSource fs = createNiceMock(FeatureSource.class);
        expect(fs.getDataStore()).andReturn(da);
        replay(fs);
        SecuredFeatureSource ro = new SecuredFeatureSource(fs, WrapperPolicy.readOnlyChallenge(null));
        Assert.assertTrue(((ro.getDataStore()) instanceof ReadOnlyDataAccess));
    }
}

