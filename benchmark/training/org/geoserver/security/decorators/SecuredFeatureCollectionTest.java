/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import Filter.EXCLUDE;
import Filter.INCLUDE;
import SortBy.NATURAL_ORDER;
import org.geoserver.security.WrapperPolicy;
import org.geoserver.security.impl.SecureObjectsTest;
import org.geotools.data.FeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;


public class SecuredFeatureCollectionTest extends SecureObjectsTest {
    private FeatureStore store;

    private SimpleFeature feature;

    @Test
    public void testHide() throws Exception {
        SecuredFeatureStore ro = new SecuredFeatureStore(store, WrapperPolicy.hide(null));
        DefaultFeatureCollection fc = new DefaultFeatureCollection();
        fc.add(feature);
        // check the easy ones, those that are not implemented in a read only
        // collection
        try {
            ro.addFeatures(fc);
            Assert.fail("Should have failed with an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            ro.removeFeatures(INCLUDE);
            Assert.fail("Should have failed with an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    @Test
    public void testReadOnly() throws Exception {
        SecuredFeatureStore ro = new SecuredFeatureStore(store, WrapperPolicy.readOnlyHide(null));
        // let's check the iterator, should allow read but not remove
        FeatureCollection rofc = ro.getFeatures();
        FeatureIterator roit = rofc.features();
        roit.hasNext();
        roit.next();
        // check derived collections are still read only and share the same
        // challenge policy
        SecuredFeatureCollection sorted = ((SecuredFeatureCollection) (rofc.sort(NATURAL_ORDER)));
        Assert.assertEquals(ro.policy, sorted.policy);
        SecuredFeatureCollection sub = ((SecuredFeatureCollection) (rofc.subCollection(INCLUDE)));
        Assert.assertEquals(ro.policy, sorted.policy);
    }

    @Test
    public void testChallenge() throws Exception {
        SecuredFeatureStore ro = new SecuredFeatureStore(store, WrapperPolicy.readOnlyChallenge(null));
        DefaultFeatureCollection fc = new DefaultFeatureCollection();
        fc.add(feature);
        // check the easy ones, those that are not implemented in a read only
        // collection
        try {
            ro.addFeatures(fc);
            Assert.fail("Should have failed with a spring security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.removeFeatures(INCLUDE);
            Assert.fail("Should have failed with a spring security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.removeFeatures(ECQL.toFilter("IN ('testSchema.1')"));
            Assert.fail("Should have failed with a spring security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.removeFeatures(EXCLUDE);
            Assert.fail("Should have failed with a spring security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        // let's check the iterator, should allow read but not remove
        FeatureCollection rofc = ro.getFeatures();
        FeatureIterator roit = rofc.features();
        roit.hasNext();
        roit.next();
        // check derived collections are still read only and share the same
        // challenge policy
        SecuredFeatureCollection sorted = ((SecuredFeatureCollection) (rofc.sort(NATURAL_ORDER)));
        Assert.assertEquals(ro.policy, sorted.policy);
        SecuredFeatureCollection sub = ((SecuredFeatureCollection) (rofc.subCollection(INCLUDE)));
        Assert.assertEquals(ro.policy, sorted.policy);
    }
}

