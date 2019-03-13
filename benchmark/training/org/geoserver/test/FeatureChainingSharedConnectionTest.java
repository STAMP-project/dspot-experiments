/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.appschema.filter.FilterFactoryImplNamespaceAware;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.complex.DataAccessMappingFeatureIterator;
import org.geotools.data.complex.MappingFeatureSource;
import org.geotools.data.complex.config.AppSchemaDataAccessConfigurator;
import org.geotools.data.util.NullProgressListener;
import org.geotools.feature.FeatureIterator;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.filter.PropertyIsLike;


public class FeatureChainingSharedConnectionTest extends AbstractAppSchemaTestSupport {
    private int nestedFeaturesCount;

    private Transaction mfTransaction;

    private Transaction guTransaction;

    private JDBCDataStore mfSourceDataStore;

    private JDBCDataStore guSourceDataStore;

    /**
     * Tests that connection is automatically shared among top feature iterators and nested feature
     * iterators, but only in the context of a single AppSchemaDataAccess instance.
     *
     * <p>What this means in practice is:
     *
     * <ul>
     *   <li><em>MappedFeature</em> and <em>GeologicUnit</em> belong to different
     *       AppSchemaDataAccess instances, so an iterator on MappedFeature will open a new database
     *       connection to retrieve the nested GeologicUnit features
     *   <li><em>GeologicUnit, CompositionPart, ControlledConcept, CGI_TermValue</em> belong to the
     *       same AppSchemaDataAccess instances, so an iterator on GeologicUnit will NOT open a new
     *       database connection to retrieve the nested <em>CompositionPart, ControlledConcept,
     *       CGI_TermValue</em> "features"
     * </ul>
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSharedConnection() throws Exception {
        MockConnectionLifecycleListener connListener = new MockConnectionLifecycleListener();
        FeatureTypeInfo mfTypeInfo = getCatalog().getFeatureTypeByName("gsml", "MappedFeature");
        Assert.assertNotNull(mfTypeInfo);
        FeatureTypeInfo guTypeInfo = getCatalog().getFeatureTypeByName("gsml", "GeologicUnit");
        Assert.assertNotNull(guTypeInfo);
        FeatureSource fs = mfTypeInfo.getFeatureSource(new NullProgressListener(), null);
        Assert.assertTrue((fs instanceof MappingFeatureSource));
        MappingFeatureSource mfFs = ((MappingFeatureSource) (fs));
        FeatureSource mfSourceFs = mfFs.getMapping().getSource();
        fs = guTypeInfo.getFeatureSource(new NullProgressListener(), null);
        Assert.assertTrue((fs instanceof MappingFeatureSource));
        MappingFeatureSource guFs = ((MappingFeatureSource) (fs));
        FeatureSource guSourceFs = guFs.getMapping().getSource();
        // The test only makes sense if we have a databae backend and joining is enabled
        Assume.assumeTrue(((((mfSourceFs.getDataStore()) instanceof JDBCDataStore) && ((guSourceFs.getDataStore()) instanceof JDBCDataStore)) && (AppSchemaDataAccessConfigurator.isJoining())));
        mfSourceDataStore = ((JDBCDataStore) (mfSourceFs.getDataStore()));
        guSourceDataStore = ((JDBCDataStore) (guSourceFs.getDataStore()));
        // retrieve one feature to trigger all necessary initialization steps so they don't
        // interfere
        // with the test's outcome
        try (FeatureIterator fIt = mfFs.getFeatures().features()) {
            Assert.assertTrue(fIt.hasNext());
            Assert.assertNotNull(fIt.next());
        }
        // register connection listeners
        mfSourceDataStore.getConnectionLifecycleListeners().add(connListener);
        guSourceDataStore.getConnectionLifecycleListeners().add(connListener);
        FilterFactoryImplNamespaceAware ff = new FilterFactoryImplNamespaceAware();
        ff.setNamepaceContext(mfFs.getMapping().getNamespaces());
        PropertyIsLike like = ff.like(ff.property("gsml:specification/gsml:GeologicUnit/gml:description"), "*sedimentary*");
        try (FeatureIterator fIt = mfFs.getFeatures(like).features()) {
            Assert.assertTrue((fIt instanceof DataAccessMappingFeatureIterator));
            DataAccessMappingFeatureIterator mappingIt = ((DataAccessMappingFeatureIterator) (fIt));
            Assert.assertTrue(fIt.hasNext());
            // fetch one feature to trigger opening of nested iterators
            Feature f = fIt.next();
            Assert.assertNotNull(f);
            FeatureSource mappedSource = mappingIt.getMappedSource();
            Assert.assertTrue(((mappedSource.getDataStore()) == (mfSourceDataStore)));
            Assert.assertNotNull(mappingIt.getTransaction());
            mfTransaction = mappingIt.getTransaction();
            testSharedConnectionRecursively(mfFs.getMapping(), mappingIt, mfSourceDataStore, mfTransaction);
        }
        Assert.assertEquals(2, connListener.actionCountByDataStore.size());
        Assert.assertTrue(connListener.actionCountByDataStore.containsKey(mfSourceDataStore));
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(mfSourceDataStore).borrowCount);
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(mfSourceDataStore).releaseCount);
        Assert.assertTrue(connListener.actionCountByDataStore.containsKey(guSourceDataStore));
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(guSourceDataStore).borrowCount);
        Assert.assertEquals(1, connListener.actionCountByDataStore.get(guSourceDataStore).releaseCount);
        Assert.assertEquals(8, nestedFeaturesCount);
    }
}

