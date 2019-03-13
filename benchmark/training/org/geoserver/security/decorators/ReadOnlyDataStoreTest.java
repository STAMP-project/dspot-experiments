/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import Filter.INCLUDE;
import Query.ALL;
import Transaction.AUTO_COMMIT;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.WrapperPolicy;
import org.geoserver.security.impl.SecureObjectsTest;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.Name;


public class ReadOnlyDataStoreTest extends SecureObjectsTest {
    private DataStore ds;

    @Test
    public void testDisallowedAPI() throws Exception {
        ReadOnlyDataStore ro = new ReadOnlyDataStore(ds, WrapperPolicy.hide(null));
        try {
            ro.createSchema(null);
            Assert.fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // 
        }
        try {
            ro.updateSchema(((String) (null)), null);
            Assert.fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // 
        }
        try {
            ro.updateSchema(((Name) (null)), null);
            Assert.fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // 
        }
        try {
            ro.getFeatureWriter("states", AUTO_COMMIT);
            Assert.fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // 
        }
        try {
            ro.getFeatureWriter("states", INCLUDE, AUTO_COMMIT);
            Assert.fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // 
        }
        try {
            ro.getFeatureWriterAppend("states", AUTO_COMMIT);
            Assert.fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // 
        }
    }

    @Test
    public void testChallenge() throws Exception {
        ReadOnlyDataStore ro = new ReadOnlyDataStore(ds, WrapperPolicy.readOnlyChallenge(null));
        try {
            ro.createSchema(null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.updateSchema(((String) (null)), null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.updateSchema(((Name) (null)), null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.getFeatureWriter("states", AUTO_COMMIT);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.getFeatureWriter("states", INCLUDE, AUTO_COMMIT);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            ro.getFeatureWriterAppend("states", AUTO_COMMIT);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
    }

    @Test
    public void testReadOnlySource() throws Exception {
        ReadOnlyDataStore ro = new ReadOnlyDataStore(ds, WrapperPolicy.readOnlyHide(new org.geoserver.security.WorkspaceAccessLimits(CatalogMode.HIDE, true, false, false)));
        SimpleFeatureSource fs = ro.getFeatureSource("blah");
        // used to go boom here
        SimpleFeatureCollection fc = fs.getFeatures(ALL);
        Assert.assertEquals(0, fc.size());
    }
}

