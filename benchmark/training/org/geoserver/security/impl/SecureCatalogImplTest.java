/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import Dispatcher.REQUEST;
import Filter.EXCLUDE;
import Filter.INCLUDE;
import GeoServerExtensionsHelper.ExtensionsHelperRule;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.easymock.classextension.EasyMock;
import org.geoserver.catalog.impl.AbstractCatalogDecorator;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.ows.Request;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geoserver.security.decorators.ReadOnlyDataStoreTest;
import org.geoserver.security.decorators.SecuredCoverageInfo;
import org.geoserver.security.decorators.SecuredFeatureTypeInfo;
import org.geotools.util.logging.Logging;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecureCatalogImplTest extends AbstractAuthorizationTest {
    public static final Logger LOGGER = Logging.getLogger(SecureCatalogImplTest.class);

    @Rule
    public ExtensionsHelperRule extensions = new GeoServerExtensionsHelper.ExtensionsHelperRule();

    @Test
    public void testWideOpen() throws Exception {
        buildManager("wideOpen.properties");
        // use no user at all
        SecurityContextHolder.getContext().setAuthentication(anonymous);
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        Assert.assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertSame(cascaded, sc.getResourceByName("topp:cascaded", WMSLayerInfo.class));
        Assert.assertSame(cascadedWmts, sc.getResourceByName("topp:cascadedWmts", WMTSLayerInfo.class));
        Assert.assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        Assert.assertSame(statesStore, sc.getDataStoreByName("states"));
        Assert.assertSame(roadsStore, sc.getDataStoreByName("roads"));
        Assert.assertSame(arcGridStore, sc.getCoverageStoreByName("arcGrid"));
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), equalTo(featureTypes));
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), equalTo(coverages));
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), equalTo(workspaces));
    }

    @Test
    public void testLockedDown() throws Exception {
        buildManager("lockedDown.properties");
        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNull(sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertNull(sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        Assert.assertNull(sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertNull(sc.getWorkspaceByName("topp"));
        Assert.assertNull(sc.getDataStoreByName("states"));
        Assert.assertNull(sc.getDataStoreByName("roads"));
        Assert.assertNull(sc.getCoverageStoreByName("arcGrid"));
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), empty());
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), empty());
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), empty());
        // try with write enabled user
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        Assert.assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        Assert.assertSame(statesStore, sc.getDataStoreByName("states"));
        Assert.assertSame(roadsStore, sc.getDataStoreByName("roads"));
        Assert.assertSame(arcGridStore, sc.getCoverageStoreByName("arcGrid"));
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), equalTo(featureTypes));
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), equalTo(coverages));
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), equalTo(workspaces));
    }

    @Test
    public void testLockedChallenge() throws Exception {
        buildManager("lockedDownChallenge.properties");
        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // check a direct access to the data does trigger a security challenge
        try {
            sc.getFeatureTypeByName("topp:states").getFeatureSource(null, null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getCoverageByName("nurc:arcgrid").getGridCoverage(null, null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getResourceByName("topp:states", FeatureTypeInfo.class).getFeatureSource(null, null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getResourceByName("nurc:arcgrid", CoverageInfo.class).getGridCoverage(null, null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        sc.getWorkspaceByName("topp");
        try {
            sc.getDataStoreByName("states").getDataStore(null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getDataStoreByName("roads").getDataStore(null);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getCoverageStoreByName("arcGrid").getFormat();
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        // check we still get the lists out so that capabilities can be built
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), allOf(((org.hamcrest.Matcher) (hasSize(featureTypes.size()))), ((org.hamcrest.Matcher) (everyItem(Matchers.Matchers.<FeatureTypeInfo>instanceOf(SecuredFeatureTypeInfo.class))))));
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), allOf(((org.hamcrest.Matcher) (hasSize(coverages.size()))), ((org.hamcrest.Matcher) (everyItem(Matchers.Matchers.<CoverageInfo>instanceOf(SecuredCoverageInfo.class))))));
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), equalTo(workspaces));
        // try with write enabled user
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        Assert.assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        Assert.assertSame(statesStore, sc.getDataStoreByName("states"));
        Assert.assertSame(roadsStore, sc.getDataStoreByName("roads"));
        Assert.assertSame(arcGridStore, sc.getCoverageStoreByName("arcGrid"));
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), equalTo(featureTypes));
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), equalTo(coverages));
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), equalTo(workspaces));
    }

    @Test
    public void testLockedMixed() throws Exception {
        buildManager("lockedDownMixed.properties");
        // try with read only user and GetFeatures request
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Request request = EasyMock.createNiceMock(Request.class);
        EasyMock.expect(request.getRequest()).andReturn("GetFeatures").anyTimes();
        EasyMock.replay(request);
        REQUEST.set(request);
        // check a direct access does trigger a security challenge
        try {
            sc.getFeatureTypeByName("topp:states");
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getCoverageByName("nurc:arcgrid");
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getResourceByName("topp:states", FeatureTypeInfo.class);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getResourceByName("nurc:arcgrid", CoverageInfo.class);
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getWorkspaceByName("topp");
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getDataStoreByName("states");
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getDataStoreByName("roads");
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        try {
            sc.getCoverageStoreByName("arcGrid");
            Assert.fail("Should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        // try with a getCapabilities, make sure the lists are empty
        request = EasyMock.createNiceMock(Request.class);
        EasyMock.expect(request.getRequest()).andReturn("GetCapabilities").anyTimes();
        EasyMock.replay(request);
        REQUEST.set(request);
        // check the lists used to build capabilities are empty
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), empty());
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), empty());
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), empty());
        // try with write enabled user
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        Assert.assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        Assert.assertSame(statesStore, sc.getDataStoreByName("states"));
        Assert.assertSame(roadsStore, sc.getDataStoreByName("roads"));
        Assert.assertSame(arcGridStore, sc.getCoverageStoreByName("arcGrid"));
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), equalTo(featureTypes));
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), equalTo(coverages));
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), equalTo(workspaces));
    }

    @Test
    public void testPublicRead() throws Exception {
        buildManager("publicRead.properties");
        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        Assert.assertSame(arcGridStore, sc.getCoverageStoreByName("arcGrid"));
        // .. the following should have been wrapped
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:states")) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((sc.getResourceByName("topp:states", FeatureTypeInfo.class)) instanceof SecuredFeatureTypeInfo));
        SecureCatalogImplTest.assertThatBoth(sc.getFeatureTypes(), sc.list(FeatureTypeInfo.class, Predicates.acceptAll()), allOf(((org.hamcrest.Matcher) (hasSize(featureTypes.size()))), ((org.hamcrest.Matcher) (everyItem(Matchers.Matchers.<FeatureTypeInfo>instanceOf(SecuredFeatureTypeInfo.class))))));
        SecureCatalogImplTest.assertThatBoth(sc.getCoverages(), sc.list(CoverageInfo.class, Predicates.acceptAll()), equalTo(coverages));
        SecureCatalogImplTest.assertThatBoth(sc.getWorkspaces(), sc.list(WorkspaceInfo.class, Predicates.acceptAll()), equalTo(workspaces));
        Assert.assertNotNull(sc.getLayerByName("topp:states"));
        Assert.assertTrue(((sc.getLayerByName("topp:states")) instanceof SecuredLayerInfo));
        Assert.assertTrue(((sc.getDataStoreByName("states")) instanceof SecuredDataStoreInfo));
        Assert.assertTrue(((sc.getDataStoreByName("roads")) instanceof SecuredDataStoreInfo));
        // try with write enabled user (nothing has been wrapped)
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        Assert.assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        Assert.assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        Assert.assertEquals(featureTypes, sc.getFeatureTypes());
        Assert.assertEquals(coverages, sc.getCoverages());
        Assert.assertEquals(workspaces, sc.getWorkspaces());
        Assert.assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        Assert.assertSame(statesStore, sc.getDataStoreByName("states"));
        Assert.assertSame(roadsStore, sc.getDataStoreByName("roads"));
        Assert.assertSame(arcGridStore, sc.getCoverageStoreByName("arcGrid"));
    }

    @SuppressWarnings("serial")
    @Test
    public void testCatalogFilteredGetLayers() throws Exception {
        CatalogFilterAccessManager filter = new CatalogFilterAccessManager();
        // make a catalog that uses our layers
        Catalog withLayers = new AbstractCatalogDecorator(catalog) {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy sortBy) {
                return new org.geoserver.catalog.util.CloseableIteratorAdapter<T>(((Iterator<T>) (layers.iterator())));
            }
        };
        this.catalog = withLayers;
        extensions.singleton("catalog", catalog, Catalog.class);
        // and the secure catalog with the filter
        buildManager("publicRead.properties", filter);
        // base behavior sanity
        Assert.assertTrue(((layers.size()) > 1));
        Assert.assertTrue(((sc.getLayers().size()) > 1));
        // setup a catalog filter that will hide the layer
        // an example of this happening is when the LocalWorkspaceCatalogFilter
        // detects 'LocalLayer.get' contains the local layer
        // the result is it gets filtered out
        filter.setCatalogFilters(Collections.singletonList(new AbstractCatalogFilter() {
            @Override
            public boolean hideLayer(LayerInfo layer) {
                return layer != (statesLayer);
            }
        }));
        Assert.assertEquals(1, sc.getLayers().size());
        Assert.assertEquals(statesLayer.getName(), sc.getLayers().get(0).getName());
    }

    @Test
    public void testCatalogCloseWrappedIterator() throws Exception {
        // create a mock CloseableIterator that expects to be closed
        final CloseableIterator<?> mockIterator = createNiceMock(CloseableIterator.class);
        mockIterator.close();
        expectLastCall().once();
        replay(mockIterator);
        // make a catalog that uses the mock CloseableIterator
        Catalog withLayers = new AbstractCatalogDecorator(catalog) {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy sortBy) {
                return ((CloseableIterator<T>) (mockIterator));
            }
        };
        this.catalog = withLayers;
        GeoServerExtensionsHelper.singleton("catalog", catalog, Catalog.class);
        buildManager("publicRead.properties");
        // get the CloseableIterator from SecureCatalogImpl and close it
        CloseableIterator<LayerInfo> iterator;
        iterator = sc.list(LayerInfo.class, Predicates.acceptAll());
        iterator.close();
        // verify that the mock CloseableIterator was closed
        verify(mockIterator);
    }

    @Test
    public void testComplex() throws Exception {
        buildManager("complex.properties");
        // try with anonymous user
        SecurityContextHolder.getContext().setAuthentication(anonymous);
        // ... roads follows generic ns rule, read only, nobody can write it
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:roads")) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((sc.getDataStoreByName("roads")) instanceof SecuredDataStoreInfo));
        // ... states requires READER role
        Assert.assertNull(sc.getFeatureTypeByName("topp:states"));
        // ... but the datastore is visible since the namespace rules do apply instead
        Assert.assertTrue(((sc.getDataStoreByName("states")) instanceof SecuredDataStoreInfo));
        // ... landmarks requires WRITER role to be written
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:landmarks")) instanceof SecuredFeatureTypeInfo));
        // ... bases requires one to be in the military
        Assert.assertNull(sc.getFeatureTypeByName("topp:bases"));
        // ok, let's try the same with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:roads")) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((sc.getDataStoreByName("roads")) instanceof SecuredDataStoreInfo));
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:states")) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((sc.getDataStoreByName("states")) instanceof SecuredDataStoreInfo));
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:landmarks")) instanceof SecuredFeatureTypeInfo));
        Assert.assertNull(sc.getFeatureTypeByName("topp:bases"));
        // now with the write enabled user
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:roads")) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((sc.getDataStoreByName("roads")) instanceof SecuredDataStoreInfo));
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertTrue(((sc.getDataStoreByName("states")) instanceof SecuredDataStoreInfo));
        Assert.assertSame(landmarks, sc.getFeatureTypeByName("topp:landmarks"));
        Assert.assertNull(sc.getFeatureTypeByName("topp:bases"));
        // finally let's try the military type
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:roads")) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((sc.getDataStoreByName("roads")) instanceof SecuredDataStoreInfo));
        Assert.assertNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertTrue(((sc.getDataStoreByName("states")) instanceof SecuredDataStoreInfo));
        Assert.assertTrue(((sc.getFeatureTypeByName("topp:landmarks")) instanceof SecuredFeatureTypeInfo));
        // ... bases requires one to be in the military
        Assert.assertSame(bases, sc.getFeatureTypeByName("topp:bases"));
    }

    @Test
    public void testLockedLayerInGroupMustNotHideGroup() throws Exception {
        buildManager("lockedLayerInLayerGroup.properties");
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        Assert.assertSame(states, sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(roads, sc.getFeatureTypeByName("topp:roads"));
        LayerGroupInfo layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer");
        Assert.assertEquals(2, layerGroup.getLayers().size());
        // try with read-only user, not empty LayerGroup should be returned
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertSame(roads, sc.getFeatureTypeByName("topp:roads"));
        layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer");
        Assert.assertNotNull(layerGroup);
        Assert.assertTrue((layerGroup instanceof SecuredLayerGroupInfo));
        Assert.assertEquals(1, layerGroup.getLayers().size());
        // try with anonymous user, empty LayerGroup should be returned
        SecurityContextHolder.getContext().setAuthentication(anonymous);
        Assert.assertNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNull(sc.getFeatureTypeByName("topp:roads"));
        layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer");
        Assert.assertNotNull(layerGroup);
        Assert.assertTrue((layerGroup instanceof SecuredLayerGroupInfo));
        Assert.assertEquals(0, layerGroup.getLayers().size());
    }

    @Test
    public void testEoLayerGroupMustBeHiddenIfItsRootLayerIsHidden() throws Exception {
        LayerGroupInfo eoRoadsLayerGroup = buildEOLayerGroup("eoRoadsLayerGroup", roadsLayer, lineStyle, toppWs, statesLayer);
        LayerGroupInfo eoStatesLayerGroup = buildEOLayerGroup("eoStatesLayerGroup", statesLayer, lineStyle, toppWs, roadsLayer);
        Catalog eoCatalog = createNiceMock(Catalog.class);
        expect(eoCatalog.getLayerGroupByName("topp", eoRoadsLayerGroup.getName())).andReturn(eoRoadsLayerGroup).anyTimes();
        expect(eoCatalog.getLayerGroupByName("topp", eoStatesLayerGroup.getName())).andReturn(eoStatesLayerGroup).anyTimes();
        expect(eoCatalog.getLayerGroups()).andReturn(Arrays.asList(eoRoadsLayerGroup, eoStatesLayerGroup));
        expect(eoCatalog.list(eq(LayerGroupInfo.class), anyObject(Filter.class))).andReturn(new org.geoserver.catalog.util.CloseableIteratorAdapter<LayerGroupInfo>(Collections.emptyIterator())).anyTimes();
        replay(eoCatalog);
        this.catalog = eoCatalog;
        extensions.singleton("catalog", eoCatalog, Catalog.class);
        buildManager("lockedLayerInLayerGroup.properties");
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // if root layer is not hidden
        LayerGroupInfo layerGroup = sc.getLayerGroupByName("topp", "eoRoadsLayerGroup");
        Assert.assertNotNull(layerGroup);
        Assert.assertNotNull(layerGroup.getRootLayer());
        // if root layer is hidden
        layerGroup = sc.getLayerGroupByName("topp", "eoStatesLayerGroup");
        Assert.assertNull(layerGroup);
    }

    @Test
    public void testSecurityFilterWideOpen() throws Exception {
        // getting the resourceAccessManager
        ResourceAccessManager resourceManager = getResourceAccessManager(buildAccessManager("wideOpen.properties"));
        // Workspace test
        Class<? extends CatalogInfo> clazz = WorkspaceInfo.class;
        // Creating filter for anonymous user
        Filter security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        Filter security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Since we should see all the CatalogInfo elements, we should have an include filter
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        // PublishedInfo test
        clazz = PublishedInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Since we should see all the CatalogInfo elements, we should have an include filter
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        // Style test
        clazz = StyleInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Since we should see all the CatalogInfo elements, we should have an include filter
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        // Resource test
        clazz = ResourceInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Since we should see all the CatalogInfo elements, we should have an include filter
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        // Coverage
        clazz = CoverageInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Since we should see all the CatalogInfo elements, we should have an include filter
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
    }

    @Test
    public void testSecurityFilterLockedDown() throws Exception {
        // getting the resourceAccessManager
        ResourceAccessManager resourceManager = getResourceAccessManager(buildAccessManager("lockedDown.properties"));
        // Workspace test
        Class<? extends CatalogInfo> clazz = WorkspaceInfo.class;
        // Creating filter for anonymous user
        Filter security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        Filter security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        Filter security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we cannot see the CatalogInfo elements, we should have an exclude filter
        // for all the users except those having WRITER role
        Assert.assertSame(security, EXCLUDE);
        Assert.assertSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // PublishedInfo test
        clazz = PublishedInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we cannot see the CatalogInfo elements, we should have an exclude filter
        // for all the users except those having WRITER role
        Assert.assertSame(security, EXCLUDE);
        Assert.assertSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // Style test
        clazz = StyleInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we cannot see the CatalogInfo elements, we should have an exclude filter
        // for all the users except those having WRITER role
        Assert.assertSame(security, EXCLUDE);
        Assert.assertSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // Resource test
        clazz = ResourceInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we cannot see the CatalogInfo elements, we should have an exclude filter
        // for all the users except those having WRITER role
        Assert.assertSame(security, EXCLUDE);
        Assert.assertSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // Coverage
        clazz = CoverageInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we cannot see the CatalogInfo elements, we should have an exclude filter
        // for all the users except those having WRITER role
        Assert.assertSame(security, EXCLUDE);
        Assert.assertSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
    }

    @Test
    public void testSecurityFilterWsLock() throws Exception {
        // getting the resourceAccessManager
        ResourceAccessManager resourceManager = getResourceAccessManager(buildAccessManager("wsLock.properties"));
        // Workspace test
        Class<? extends CatalogInfo> clazz = WorkspaceInfo.class;
        // Creating filter for anonymous user
        Filter security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        Filter security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        Filter security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since only military role can see the topp WorkSpace, we should have a more complex filter
        // for all the users except those having military role
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the workspaces
        List<WorkspaceInfo> ws = catalog.getWorkspaces();
        Iterator<WorkspaceInfo> it = Iterators.filter(ws.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it.hasNext()) {
            Assert.assertSame(it.next(), nurcWs);
        } 
        it = Iterators.filter(ws.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it.hasNext()) {
            Assert.assertSame(it.next(), nurcWs);
        } 
        // PublishedInfo test
        clazz = PublishedInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since only military role can see the topp WorkSpace, we should have a more complex filter
        // for all the users except those having military role
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the workspaces
        List<LayerInfo> ly = catalog.getLayers();
        Iterator<LayerInfo> it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            String wsName = next.getResource().getNamespace().getName();
            Assert.assertTrue(wsName.equalsIgnoreCase("nurc"));
        } 
        it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            String wsName = next.getResource().getNamespace().getName();
            Assert.assertTrue(wsName.equalsIgnoreCase("nurc"));
        } 
        // Style test
        clazz = StyleInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since only military role can see the topp WorkSpace, we should have a more complex filter
        // for all the users except those having military role
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the workspaces
        List<StyleInfo> sy = catalog.getStyles();
        Iterator<StyleInfo> it3 = Iterators.filter(sy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it3.hasNext()) {
            StyleInfo next = it3.next();
            WorkspaceInfo wsi = next.getWorkspace();
            if (wsi != null) {
                String wsName = wsi.getName();
                Assert.assertTrue(wsName.equalsIgnoreCase("nurc"));
            }
        } 
        it3 = Iterators.filter(sy.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it3.hasNext()) {
            StyleInfo next = it3.next();
            WorkspaceInfo wsi = next.getWorkspace();
            if (wsi != null) {
                String wsName = wsi.getName();
                Assert.assertTrue(wsName.equalsIgnoreCase("nurc"));
            }
        } 
        // Resource test
        clazz = ResourceInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since only military role can see the topp WorkSpace, we should have a more complex filter
        // for all the users except those having military role
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the workspaces
        List<FeatureTypeInfo> fy = catalog.getFeatureTypes();
        Iterator<FeatureTypeInfo> it4 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it4.hasNext()) {
            FeatureTypeInfo next = it4.next();
            String name = next.getNamespace().getName();
            Assert.assertTrue(name.equalsIgnoreCase("nurc"));
        } 
        it4 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it4.hasNext()) {
            FeatureTypeInfo next = it4.next();
            String name = next.getNamespace().getName();
            Assert.assertTrue(name.equalsIgnoreCase("nurc"));
        } 
        // Coverage
        clazz = CoverageInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for military user
        security2 = resourceManager.getSecurityFilter(milUser, clazz);
        // Creating filter for writer role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since only military role can see the topp WorkSpace, we should have a more complex filter
        // for all the users except those having military role
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the workspaces
        List<CoverageInfo> cy = catalog.getCoverages();
        Iterator<CoverageInfo> it5 = Iterators.filter(cy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it5.hasNext()) {
            CoverageInfo next = it5.next();
            String name = next.getNamespace().getName();
            Assert.assertTrue(name.equalsIgnoreCase("nurc"));
        } 
        it5 = Iterators.filter(cy.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it5.hasNext()) {
            CoverageInfo next = it5.next();
            String name = next.getNamespace().getName();
            Assert.assertTrue(name.equalsIgnoreCase("nurc"));
        } 
    }

    @Test
    public void testSecurityFilterLayerLock() throws Exception {
        // getting the resourceAccessManager
        ResourceAccessManager resourceManager = getResourceAccessManager(buildAccessManager("layerLock.properties"));
        // Workspace test
        Class<? extends CatalogInfo> clazz = WorkspaceInfo.class;
        // Creating filter for anonymous user
        Filter security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read only user
        Filter security2 = resourceManager.getSecurityFilter(roUser, clazz);
        // Creating filter for rw role user
        Filter security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since the restriction is only at layer level, workspaces may be seen without problems
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // PublishedInfo test
        clazz = PublishedInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read only user
        security2 = resourceManager.getSecurityFilter(roUser, clazz);
        // Creating filter for rw role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we are restricting access on a single layer, all the users
        // except the rw one will have a more complex filter
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security2, INCLUDE);
        Assert.assertNotSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // Checks on the layers
        List<LayerInfo> ly = catalog.getLayers();
        Iterator<LayerInfo> it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        // Checking if the roads layer is present
        boolean hasRoadsLayer = false;
        // Ensure the base layer is present
        boolean hasBasesLayer = false;
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            Assert.assertNotSame(next, roadsLayer);
            Assert.assertNotSame(next, statesLayer);
            hasBasesLayer |= next.equals(basesLayer);
        } 
        Assert.assertTrue(hasBasesLayer);
        hasRoadsLayer = false;
        hasBasesLayer = false;
        it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security2));
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            Assert.assertNotSame(next, statesLayer);
            hasRoadsLayer |= next.equals(roadsLayer);
            hasBasesLayer |= next.equals(basesLayer);
        } 
        Assert.assertTrue(hasRoadsLayer);
        Assert.assertTrue(hasRoadsLayer);
        // Style test
        clazz = StyleInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read only user
        security2 = resourceManager.getSecurityFilter(roUser, clazz);
        // Creating filter for rw role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since the restriction is only at layer level, workspaces may be seen without problems
        Assert.assertSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // Resource test
        clazz = ResourceInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read only user
        security2 = resourceManager.getSecurityFilter(roUser, clazz);
        // Creating filter for rw role user
        security3 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Since we are restricting access on a single layer, all the users
        // except the rw one will have a more complex filter
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security2, INCLUDE);
        Assert.assertNotSame(security2, EXCLUDE);
        Assert.assertSame(security3, INCLUDE);
        // Checks on the featuretypes
        List<FeatureTypeInfo> fy = catalog.getFeatureTypes();
        Iterator<FeatureTypeInfo> it3 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        hasBasesLayer = false;
        while (it3.hasNext()) {
            FeatureTypeInfo next = it3.next();
            Assert.assertNotSame(next, roads);
            Assert.assertNotSame(next, states);
            hasBasesLayer |= next.equals(bases);
        } 
        Assert.assertTrue(hasBasesLayer);
        hasRoadsLayer = false;
        hasBasesLayer = false;
        it3 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security2));
        while (it3.hasNext()) {
            FeatureTypeInfo next = it3.next();
            hasRoadsLayer |= next.equals(roads);
            hasBasesLayer |= next.equals(bases);
            Assert.assertNotSame(next, states);
        } 
        Assert.assertTrue(hasBasesLayer);
        Assert.assertTrue(hasRoadsLayer);
    }

    @Test
    public void testSecurityFilterComplex() throws Exception {
        // getting the resourceAccessManager
        ResourceAccessManager resourceManager = getResourceAccessManager(buildAccessManager("complex.properties"));
        // Workspace test
        Class<? extends CatalogInfo> clazz = WorkspaceInfo.class;
        // Creating filter for anonymous user
        Filter security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read write user
        Filter security2 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Creating filter for military user
        Filter security3 = resourceManager.getSecurityFilter(milUser, clazz);
        // anonymous and military can access only to topp
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        // Checks on the workspaces
        List<WorkspaceInfo> ws = catalog.getWorkspaces();
        Iterator<WorkspaceInfo> it = Iterators.filter(ws.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it.hasNext()) {
            Assert.assertSame(it.next(), toppWs);
        } 
        it = Iterators.filter(ws.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it.hasNext()) {
            Assert.assertSame(it.next(), toppWs);
        } 
        // PublishedInfo test
        clazz = PublishedInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read write user
        security2 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Creating filter for military user
        security3 = resourceManager.getSecurityFilter(milUser, clazz);
        // Anonymous can access to topp layers except for states and bases
        // Read/Writer can access all layers except for bases and arcgrid
        // Military can access only topp layers except for states and can access to arcgrid
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security2, INCLUDE);
        Assert.assertNotSame(security2, EXCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the layers
        List<LayerInfo> ly = catalog.getLayers();
        // ANON
        Iterator<LayerInfo> it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        // Boolean checking the various layers
        boolean hasRoadsLayer = false;
        boolean hasLandmLayer = false;
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            // topp
            Assert.assertNotSame(("Unexpectedly found bases with security filter " + security), next, basesLayer);
            Assert.assertNotSame(("Unexpectedly found states with security filter " + security), next, statesLayer);
            hasLandmLayer |= next.equals(landmarksLayer);
            hasRoadsLayer |= next.equals(roadsLayer);
            // Nurc
            Assert.assertNotSame(next, arcGridLayer);
        } 
        // We see the roads and landmarks layer
        Assert.assertTrue(hasRoadsLayer);
        Assert.assertTrue(hasLandmLayer);
        // READER/WRITER
        // Reset boolean
        hasRoadsLayer = false;
        boolean hasStatesLayer = false;
        hasLandmLayer = false;
        it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security2));
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            // Topp
            Assert.assertNotSame(next, basesLayer);
            hasStatesLayer |= next.equals(statesLayer);
            hasLandmLayer |= next.equals(landmarksLayer);
            hasRoadsLayer |= next.equals(roadsLayer);
            // Nurc
            Assert.assertNotSame(next, arcGridLayer);
        } 
        // We see landmarks,states and roads
        Assert.assertTrue(hasLandmLayer);
        Assert.assertTrue(hasStatesLayer);
        Assert.assertTrue(hasRoadsLayer);
        // MILITARY
        // Reset boolean
        boolean hasArcGridLayer = false;
        boolean hasBasesLayer = false;
        hasLandmLayer = false;
        hasRoadsLayer = false;
        it1 = Iterators.filter(ly.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it1.hasNext()) {
            LayerInfo next = it1.next();
            // Topp
            Assert.assertNotSame(next, statesLayer);
            hasLandmLayer |= next.equals(landmarksLayer);
            hasRoadsLayer |= next.equals(roadsLayer);
            hasBasesLayer |= next.equals(basesLayer);
            // Nurc
            hasArcGridLayer |= next.equals(arcGridLayer);
        } 
        // We see landmarks,bases,arcgrid and roads
        Assert.assertTrue(hasLandmLayer);
        Assert.assertTrue(hasBasesLayer);
        Assert.assertTrue(hasArcGridLayer);
        Assert.assertTrue(hasRoadsLayer);
        // Style test
        clazz = StyleInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read write user
        security2 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Creating filter for military user
        security3 = resourceManager.getSecurityFilter(milUser, clazz);
        // anonymous and military can access only to topp
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertSame(security2, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        // Checks on the workspaces
        List<StyleInfo> sy = catalog.getStyles();
        Iterator<StyleInfo> it2 = Iterators.filter(sy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        while (it2.hasNext()) {
            StyleInfo next = it2.next();
            WorkspaceInfo wsi = next.getWorkspace();
            if (wsi != null) {
                String wsName = wsi.getName();
                Assert.assertTrue(wsName.equalsIgnoreCase("topp"));
            }
        } 
        it2 = Iterators.filter(sy.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it2.hasNext()) {
            StyleInfo next = it2.next();
            WorkspaceInfo wsi = next.getWorkspace();
            if (wsi != null) {
                String wsName = wsi.getName();
                Assert.assertTrue(wsName.equalsIgnoreCase("topp"));
            }
        } 
        // Resource test
        clazz = ResourceInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read write user
        security2 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Creating filter for military user
        security3 = resourceManager.getSecurityFilter(milUser, clazz);
        // Anonymous can access to topp layers except for states and bases
        // Read/Writer can access all layers except for bases and arcgrid
        // Military can access only topp layers except for states and can access to arcgrid
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security2, INCLUDE);
        Assert.assertNotSame(security2, EXCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the featuretypes
        List<FeatureTypeInfo> fy = catalog.getFeatureTypes();
        Iterator<FeatureTypeInfo> it3 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        // Boolean checking the various layers
        hasRoadsLayer = false;
        hasLandmLayer = false;
        while (it3.hasNext()) {
            FeatureTypeInfo next = it3.next();
            // topp
            Assert.assertNotSame(next, bases);
            Assert.assertNotSame(next, states);
            hasLandmLayer |= next.equals(landmarks);
            hasRoadsLayer |= next.equals(roads);
            // Nurc
            Assert.assertNotSame(next, arcGrid);
        } 
        // We see the roads and landmarks layer
        Assert.assertTrue(hasRoadsLayer);
        Assert.assertTrue(hasLandmLayer);
        // READER/WRITER
        // Reset boolean
        hasRoadsLayer = false;
        hasStatesLayer = false;
        hasLandmLayer = false;
        it3 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security2));
        while (it3.hasNext()) {
            FeatureTypeInfo next = it3.next();
            // Topp
            Assert.assertNotSame(next, bases);
            hasStatesLayer |= next.equals(states);
            hasLandmLayer |= next.equals(landmarks);
            hasRoadsLayer |= next.equals(roads);
            // Nurc
            Assert.assertNotSame(next, arcGrid);
        } 
        // We see landmarks,states and roads
        Assert.assertTrue(hasLandmLayer);
        Assert.assertTrue(hasStatesLayer);
        Assert.assertTrue(hasRoadsLayer);
        // MILITARY
        // Reset boolean
        hasBasesLayer = false;
        hasLandmLayer = false;
        hasRoadsLayer = false;
        it3 = Iterators.filter(fy.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it3.hasNext()) {
            FeatureTypeInfo next = it3.next();
            // Topp
            Assert.assertNotSame(next, states);
            hasLandmLayer |= next.equals(landmarks);
            hasRoadsLayer |= next.equals(roads);
            hasBasesLayer |= next.equals(bases);
        } 
        // We see landmarks,bases and roads
        Assert.assertTrue(hasLandmLayer);
        Assert.assertTrue(hasBasesLayer);
        Assert.assertTrue(hasRoadsLayer);
        // Coverage
        clazz = CoverageInfo.class;
        // Creating filter for anonymous user
        security = resourceManager.getSecurityFilter(anonymous, clazz);
        // Creating filter for read write user
        security2 = resourceManager.getSecurityFilter(rwUser, clazz);
        // Creating filter for military user
        security3 = resourceManager.getSecurityFilter(milUser, clazz);
        // Anonymous can access to topp layers except for states and bases
        // Read/Writer can access all layers except for bases and arcgrid
        // Military can access only topp layers except for states and can access to arcgrid
        Assert.assertNotSame(security, INCLUDE);
        Assert.assertNotSame(security, EXCLUDE);
        Assert.assertNotSame(security2, INCLUDE);
        Assert.assertNotSame(security2, EXCLUDE);
        Assert.assertNotSame(security3, INCLUDE);
        Assert.assertNotSame(security3, EXCLUDE);
        // Checks on the featuretypes
        List<CoverageInfo> cy = catalog.getCoverages();
        Iterator<CoverageInfo> it4 = Iterators.filter(cy.iterator(), new SecureCatalogImplTest.PredicateFilter(security));
        // Boolean checking the various coverages
        while (it4.hasNext()) {
            CoverageInfo next = it4.next();
            // Nurc
            Assert.assertNotSame(next, arcGrid);
        } 
        // READER/WRITER
        // Reset boolean
        it4 = Iterators.filter(cy.iterator(), new SecureCatalogImplTest.PredicateFilter(security2));
        while (it4.hasNext()) {
            CoverageInfo next = it4.next();
            // Nurc
            Assert.assertNotSame(next, arcGrid);
        } 
        // MILITARY
        // Reset boolean
        hasArcGridLayer = false;
        it4 = Iterators.filter(cy.iterator(), new SecureCatalogImplTest.PredicateFilter(security3));
        while (it4.hasNext()) {
            CoverageInfo next = it4.next();
            // Nurc
            hasArcGridLayer |= next.equals(arcGrid);
        } 
        // We see arcgrid
        Assert.assertTrue(hasArcGridLayer);
    }

    static class PredicateFilter implements Predicate<CatalogInfo> {
        private Filter f;

        public PredicateFilter(Filter f) {
            this.f = f;
        }

        @Override
        public boolean apply(@Nullable
        CatalogInfo input) {
            if (input != null) {
                return f.evaluate(input);
            }
            return false;
        }
    }

    @Test
    public void testUnwrapping() {
        // we create a mock a policy without defining any behavior since it will not be used
        WrapperPolicy policy = createNiceMock(org.geoserver.security.decorators.WrapperPolicy.class);
        // test that a secured coverage info info is correctly unwrapped to a coverage info
        Assert.assertThat(SecureCatalogImpl.unwrap(new SecuredCoverageInfo(arcGrid, policy)), Matchers.not(instanceOf(SecuredCoverageInfo.class)));
        // test that a secured feature info info is correctly unwrapped to a feature info
        Assert.assertThat(SecureCatalogImpl.unwrap(new SecuredFeatureTypeInfo(states, policy)), Matchers.not(instanceOf(SecuredFeatureTypeInfo.class)));
        // test that a secured WMS layer info info is correctly unwrapped to a WMS layer info
        Assert.assertThat(SecureCatalogImpl.unwrap(new SecuredWMSLayerInfo(cascaded, policy)), Matchers.not(instanceOf(SecuredWMSLayerInfo.class)));
        // test that a secured WMTS layer info info is correctly unwrapped to a WMTS layer info
        Assert.assertThat(SecureCatalogImpl.unwrap(new SecuredWMTSLayerInfo(cascadedWmts, policy)), Matchers.not(instanceOf(SecuredWMTSLayerInfo.class)));
    }

    @Test
    public void testSettingResourceOnSecureLayerInfo() {
        // we create a mock a policy without defining any behavior since it will not be used
        WrapperPolicy policy = createNiceMock(org.geoserver.security.decorators.WrapperPolicy.class);
        // testing for coverages
        LayerInfo coverageLayerInfo = new LayerInfoImpl();
        SecuredLayerInfo secureCoverageLayerInfo = new SecuredLayerInfo(coverageLayerInfo, policy);
        secureCoverageLayerInfo.setResource(new SecuredCoverageInfo(arcGrid, policy));
        Assert.assertThat(coverageLayerInfo.getResource(), Matchers.not(instanceOf(SecuredCoverageInfo.class)));
        Assert.assertThat(coverageLayerInfo.getResource(), instanceOf(CoverageInfo.class));
        // testing for features
        LayerInfo featureLayerInfo = new LayerInfoImpl();
        SecuredLayerInfo secureFeatureLayerInfo = new SecuredLayerInfo(featureLayerInfo, policy);
        secureFeatureLayerInfo.setResource(new SecuredFeatureTypeInfo(states, policy));
        Assert.assertThat(featureLayerInfo.getResource(), Matchers.not(instanceOf(SecuredFeatureTypeInfo.class)));
        Assert.assertThat(featureLayerInfo.getResource(), instanceOf(FeatureTypeInfo.class));
        // testing for WMS layers
        LayerInfo wmsLayerInfo = new LayerInfoImpl();
        SecuredLayerInfo secureWmsLayerInfo = new SecuredLayerInfo(wmsLayerInfo, policy);
        secureWmsLayerInfo.setResource(new SecuredWMSLayerInfo(cascaded, policy));
        Assert.assertThat(wmsLayerInfo.getResource(), Matchers.not(instanceOf(SecuredWMSLayerInfo.class)));
        Assert.assertThat(wmsLayerInfo.getResource(), instanceOf(WMSLayerInfo.class));
        // testing for WMTS layers
        LayerInfo wmtsLayerInfo = new LayerInfoImpl();
        SecuredLayerInfo secureWmtsLayerInfo = new SecuredLayerInfo(wmtsLayerInfo, policy);
        secureWmtsLayerInfo.setResource(new SecuredWMTSLayerInfo(cascadedWmts, policy));
        Assert.assertThat(wmtsLayerInfo.getResource(), Matchers.not(instanceOf(SecuredWMTSLayerInfo.class)));
        Assert.assertThat(wmtsLayerInfo.getResource(), instanceOf(WMTSLayerInfo.class));
    }

    @Test
    public void testWmsNamedTreeAMilitaryOnly() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("namedTreeAMilitaryOnly.properties");
        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNull(sc.getFeatureTypeByName("topp:states"));
        // cannot see the named tree
        Assert.assertNull(sc.getLayerGroupByName(namedTreeA.getName()));
        // only contained in the hidden group and in a "single mode" one
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        // not shared
        Assert.assertNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // this layer is contained also in containerTreeB
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // the other layers in groups are also available
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(nestedContainerE.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(forestsLayer.prefixedName()));
        // check the single group is there but lost the states layer
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(1, securedSingleGroup.layers().size());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        // check the mil user sees everything instead
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
    }

    @Test
    public void testWmsNamedTreeAMilitaryOnlyGroupContents() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("lockDownStates.properties");
        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        final LayerGroupInfo group = sc.getLayerGroupByName(namedTreeA.getName());
        Assert.assertNotNull(group);
        // the group should not contain states any more
        final List<LayerInfo> layers = group.layers();
        Assert.assertEquals(2, layers.size());
        final List<StyleInfo> styles = group.styles();
        Assert.assertEquals(2, styles.size());
        // check the layers and styles are not mis-aligned
        Assert.assertEquals("roads", layers.get(0).getName());
        Assert.assertEquals("topp-roads-style", styles.get(0).getName());
        Assert.assertEquals("cities", layers.get(1).getName());
        Assert.assertEquals("nurc-cities-style", styles.get(1).getName());
    }

    @Test
    public void testWfsNamedTreeAMilitaryOnly() throws Exception {
        // prepare the stage, this time for a WFS test, the containment rules won't apply anymore
        setupRequestThreadLocal("WFS");
        buildManager("namedTreeAMilitaryOnly.properties");
        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        // cannot see the named tree
        Assert.assertNull(sc.getLayerGroupByName(namedTreeA.getName()));
        // only contained in the hidden group and in a "single mode" one
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        // not shared
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // this layer is contained also in containerTreeB
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // the other layers in groups are also available
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(nestedContainerE.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(forestsLayer.prefixedName()));
        // check the single group is there but lost the states layer
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
        // check the mil user sees everything instead
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
    }

    @Test
    public void testWmsContainerTreeBMilitaryOnly() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("containerTreeGroupBMilitaryOnly.properties");
        // try with read only user, layer group A and its contents should be fine
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B and landmarks should not be accessible
        Assert.assertNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the nested group and its sub-layer is also not available
        Assert.assertNull(sc.getLayerGroupByName(nestedContainerE.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        // check the single group is there and fully available
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
        // check the mil user sees everything instead
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
    }

    @Test
    public void testWmsBothGroupABMilitaryOnlyMilitaryOnly() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("bothGroupABMilitaryOnly.properties");
        // try with read only user, layer group A and its contents should not be available
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B and landmarks should not be accessible
        Assert.assertNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the nested group and its sub-layer is also not available
        Assert.assertNull(sc.getLayerGroupByName(nestedContainerE.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        // check the single group is there and states is gone
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(1, securedSingleGroup.layers().size());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        // check the mil user sees everything instead
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
    }

    @Test
    public void testWmsSingleGroupCMilitaryOnly() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("singleGroupCMilitaryOnly.properties");
        // try with read only user, layer group A and its contents should be fine
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B and landmarks should also be accessible
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // check the single group is not available, but its extra layer is
        Assert.assertNull(sc.getLayerGroupByName(singleGroupC.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(basesLayer.prefixedName()));
        // check the mil user sees everything instead
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
    }

    @Test
    public void testWmsWsContainerGroupDMilitaryOnly() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("wsContainerGroupDMilitaryOnly.properties");
        // try with read only user, layer group A and its contents should be fine
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B and landmarks should also be accessible
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the single group is  available too
        Assert.assertNotNull(sc.getLayerGroupByName(singleGroupC.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(basesLayer.prefixedName()));
        // the ws specific group is not available instead, nor its contained layers
        Assert.assertNull(sc.getLayerGroupByName("nurc", "wsContainerD"));
        Assert.assertNull(sc.getLayerByName(arcGridLayer.prefixedName()));
        // check the mil user sees everything instead
        SecurityContextHolder.getContext().setAuthentication(milUser);
        Assert.assertNotNull(sc.getFeatureTypeByName("topp:states"));
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(singleGroupC.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.layers().size());
        Assert.assertEquals(statesLayer.prefixedName(), securedSingleGroup.layers().get(0).prefixedName());
        Assert.assertEquals(basesLayer.prefixedName(), securedSingleGroup.layers().get(1).prefixedName());
        LayerGroupInfo wsSpecificGroup = sc.getLayerGroupByName("nurc", "wsContainerD");
        Assert.assertNotNull(wsSpecificGroup);
        Assert.assertEquals(1, wsSpecificGroup.getLayers().size());
        Assert.assertNotNull(sc.getLayerByName(arcGridLayer.prefixedName()));
    }

    @Test
    public void testWMSLayerGroupAllowsAccess() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("namedTreeAllow.properties");
        // try with read only user, only layer group A and its contents should be visible
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B should not be accessible
        Assert.assertNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the single group not available either
        Assert.assertNull(sc.getLayerGroupByName(singleGroupC.prefixedName()));
        Assert.assertNull(sc.getLayerByName(basesLayer.prefixedName()));
        // the ws specific group is not available either
        Assert.assertNull(sc.getLayerGroupByName("nurc", "wsContainerD"));
        Assert.assertNull(sc.getLayerByName(arcGridLayer.prefixedName()));
    }

    @Test
    public void testWMSLayerGroupAllowLayerOverride() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("namedTreeAllowLayerOverride.properties");
        // try with read only user, only layer group A and its contents should be visible, but
        // not topp:states
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B should not be accessible
        Assert.assertNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the single group not available either
        Assert.assertNull(sc.getLayerGroupByName(singleGroupC.prefixedName()));
        Assert.assertNull(sc.getLayerByName(basesLayer.prefixedName()));
        // the ws specific group is not available either
        Assert.assertNull(sc.getLayerGroupByName("nurc", "wsContainerD"));
        Assert.assertNull(sc.getLayerByName(arcGridLayer.prefixedName()));
    }

    @Test
    public void testWMSLayerGroupAllowWorkspaceOverride() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("namedTreeAllowWorkspaceOverride.properties");
        // try with read only user, only layer group A and its contents should be visible
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNotNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B should not be accessible
        Assert.assertNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the single group not available either
        Assert.assertNull(sc.getLayerGroupByName(singleGroupC.prefixedName()));
        Assert.assertNull(sc.getLayerByName(basesLayer.prefixedName()));
        // the ws specific group is not available either
        Assert.assertNull(sc.getLayerGroupByName("nurc", "wsContainerD"));
        Assert.assertNull(sc.getLayerByName(arcGridLayer.prefixedName()));
    }

    @Test
    public void testWMSLayerGroupDenyWSAllow() throws Exception {
        // prepare the stage
        setupRequestThreadLocal("WMS");
        buildManager("namedTreeDenyWSAllow.properties");
        // try with read only user, the layer group A is not allowed
        SecurityContextHolder.getContext().setAuthentication(roUser);
        Assert.assertNull(sc.getLayerGroupByName(namedTreeA.getName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // however cities are allowed explicitly because they are in the nurc ws
        Assert.assertNotNull(sc.getLayerByName(citiesLayer.prefixedName()));
        // layer group B should not be accessible
        Assert.assertNull(sc.getLayerGroupByName(containerTreeB.prefixedName()));
        Assert.assertNull(sc.getLayerByName(landmarksLayer.prefixedName()));
        // the single group not available either
        Assert.assertNull(sc.getLayerGroupByName(singleGroupC.prefixedName()));
        Assert.assertNull(sc.getLayerByName(basesLayer.prefixedName()));
        // the ws specific group is made available by the workspace rule
        Assert.assertNotNull(sc.getLayerGroupByName("nurc", "wsContainerD"));
        Assert.assertNotNull(sc.getLayerByName(arcGridLayer.prefixedName()));
    }
}

