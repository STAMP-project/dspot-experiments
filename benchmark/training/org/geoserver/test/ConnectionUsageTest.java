/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import org.geotools.appschema.filter.FilterFactoryImplNamespaceAware;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.complex.MappingFeatureCollection;
import org.geotools.data.complex.MappingFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.PropertyIsEqualTo;


public class ConnectionUsageTest extends AbstractAppSchemaTestSupport {
    private FilterFactoryImplNamespaceAware ff;

    private MockConnectionLifecycleListener connListener;

    private MappingFeatureSource mappingFs;

    private Transaction transaction;

    private JDBCDataStore sourceDataStore;

    private int nestedFeaturesCount;

    @Test
    public void testConnectionSharedAmongNestedIterators() throws Exception {
        PropertyIsEqualTo equals = ff.equals(ff.property("ex:nestedFeature/ex:ConnectionUsageFirstNested/ex:nestedFeature/ex:ConnectionUsageSecondNested/gml:name"), ff.literal("C_nested_second"));
        try (FeatureIterator fIt = mappingFs.getFeatures(equals).features()) {
            testNestedIterators(fIt);
        }
        Assert.assertEquals(1, connListener.actionCountByDataStore.size());
        Assert.assertTrue(connListener.actionCountByDataStore.containsKey(sourceDataStore));
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).borrowCount);
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
        Assert.assertEquals(2, nestedFeaturesCount);
    }

    @Test
    public void testConnectionSharedIfTransactionIs() throws Exception {
        PropertyIsEqualTo equals = ff.equals(ff.property("ex:nestedFeature/ex:ConnectionUsageFirstNested/ex:nestedFeature/ex:ConnectionUsageSecondNested/gml:name"), ff.literal("C_nested_second"));
        FeatureCollection fc = mappingFs.getFeatures(equals);
        Assert.assertTrue((fc instanceof MappingFeatureCollection));
        MappingFeatureCollection mfc = ((MappingFeatureCollection) (fc));
        try (Transaction tx = new DefaultTransaction()) {
            // explicitly specifying the transaction to use
            try (FeatureIterator fIt = mfc.features(tx)) {
                testNestedIterators(fIt);
            }
            Assert.assertEquals(1, connListener.actionCountByDataStore.size());
            Assert.assertTrue(connListener.actionCountByDataStore.containsKey(sourceDataStore));
            Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).borrowCount);
            Assert.assertEquals(0, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
            Assert.assertEquals(2, nestedFeaturesCount);
            // open another iterator using the same transaction
            try (FeatureIterator fIt = mfc.features(tx)) {
                testNestedIterators(fIt);
            }
            // no new connection has been opened
            Assert.assertEquals(1, connListener.actionCountByDataStore.size());
            Assert.assertTrue(connListener.actionCountByDataStore.containsKey(sourceDataStore));
            Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).borrowCount);
            Assert.assertEquals(0, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
            Assert.assertEquals(4, nestedFeaturesCount);
        }
        // at this point transaction has been closed and so the connection has been released
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
    }

    @Test
    public void testConnectionNotSharedIfTransactionIsNot() throws Exception {
        PropertyIsEqualTo equals = ff.equals(ff.property("ex:nestedFeature/ex:ConnectionUsageFirstNested/ex:nestedFeature/ex:ConnectionUsageSecondNested/gml:name"), ff.literal("C_nested_second"));
        FeatureCollection fc = mappingFs.getFeatures(equals);
        Assert.assertTrue((fc instanceof MappingFeatureCollection));
        MappingFeatureCollection mfc = ((MappingFeatureCollection) (fc));
        try (Transaction tx = new DefaultTransaction()) {
            // explicitly specifying the transaction to use
            try (FeatureIterator fIt = mfc.features(tx)) {
                testNestedIterators(fIt);
            }
            Assert.assertEquals(1, connListener.actionCountByDataStore.size());
            Assert.assertTrue(connListener.actionCountByDataStore.containsKey(sourceDataStore));
            Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).borrowCount);
            Assert.assertEquals(0, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
            Assert.assertEquals(2, nestedFeaturesCount);
        }
        // at this point transaction has been closed and so the connection has been released
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
        try (Transaction tx = new DefaultTransaction()) {
            // open another iterator using a different transaction
            try (FeatureIterator fIt = mfc.features(tx)) {
                testNestedIterators(fIt);
            }
            // a new connection has been opened
            Assert.assertEquals(1, connListener.actionCountByDataStore.size());
            Assert.assertTrue(connListener.actionCountByDataStore.containsKey(sourceDataStore));
            Assert.assertEquals(2, connListener.actionCountByDataStore.get(sourceDataStore).borrowCount);
            Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
            Assert.assertEquals(4, nestedFeaturesCount);
        }
        // at this point transaction has been closed and so the connection has been released
        Assert.assertEquals(2, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
    }

    /**
     * This test uses a conditionally joined feature with a broken mapping configuration to trigger
     * a RuntimeException when iterator.next() is called and verifies that no connection leak
     * occurs, even if the caller forgets to catch unchecked exceptions.
     *
     * @throws Exception
     * 		
     */
    @Test
    @SuppressWarnings("TryFailThrowable")
    public void testNoConnectionLeakIfExceptionThrown() throws Exception {
        FilterFactoryImplNamespaceAware ff = new FilterFactoryImplNamespaceAware();
        ff.setNamepaceContext(mappingFs.getMapping().getNamespaces());
        // this filter selects the feature with GML ID "scp.1", the only one which joins the broken
        // feature type ex:ConnectionUsageThirdNested
        PropertyIsEqualTo equals = ff.equals(ff.property("ex:nestedFeature/ex:ConnectionUsageFirstNested/ex:nestedFeature/ex:ConnectionUsageSecondNested/gml:name"), ff.literal("A_nested_second"));
        FeatureIterator fIt = mappingFs.getFeatures(equals).features();
        try {
            testNestedIterators(fIt);
            Assert.fail("Expected exception was not thrown!");
        } catch (Throwable e) {
            // This was expected
        }
        // iterator should have been automatically closed, so no connection should be leaked
        Assert.assertEquals(1, connListener.actionCountByDataStore.size());
        Assert.assertTrue(connListener.actionCountByDataStore.containsKey(sourceDataStore));
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).borrowCount);
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(sourceDataStore).releaseCount);
    }
}

