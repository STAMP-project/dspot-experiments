/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import Catalog.DEFAULT;
import LayerGroupInfo.Mode.CONTAINER;
import LayerGroupInfo.Mode.EO;
import LayerGroupInfo.Mode.NAMED;
import LayerGroupInfo.Mode.OPAQUE_CONTAINER;
import LayerGroupInfo.Mode.SINGLE;
import MatchAction.ALL;
import MatchAction.ANY;
import MatchAction.ONE;
import StyleInfo.DEFAULT_LINE;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogException;
import org.geoserver.catalog.CatalogFactory;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataLinkInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.Keyword;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.Predicates;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WMTSLayerInfo;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.event.CatalogAddEvent;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.catalog.event.CatalogPostModifyEvent;
import org.geoserver.catalog.event.CatalogRemoveEvent;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.logging.Logging;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;


public class CatalogImplTest {
    protected Catalog catalog;

    protected WorkspaceInfo ws;

    protected WorkspaceInfo wsA;

    protected WorkspaceInfo wsB;

    protected NamespaceInfo ns;

    protected NamespaceInfo nsA;

    protected NamespaceInfo nsB;

    protected DataStoreInfo ds;

    protected DataStoreInfo dsA;

    protected DataStoreInfo dsB;

    protected CoverageStoreInfo cs;

    protected WMSStoreInfo wms;

    protected WMTSStoreInfo wmtss;

    protected FeatureTypeInfo ft;

    protected CoverageInfo cv;

    protected WMSLayerInfo wl;

    protected WMTSLayerInfo wmtsl;

    protected LayerInfo l;

    protected StyleInfo s;

    protected StyleInfo defaultLineStyle;

    protected LayerGroupInfo lg;

    @Test
    public void testAddNamespace() {
        Assert.assertTrue(catalog.getNamespaces().isEmpty());
        catalog.add(ns);
        Assert.assertEquals(1, catalog.getNamespaces().size());
        NamespaceInfo ns2 = catalog.getFactory().createNamespace();
        try {
            catalog.add(ns2);
            Assert.fail("adding without a prefix should throw exception");
        } catch (Exception e) {
        }
        ns2.setPrefix("ns2Prefix");
        try {
            catalog.add(ns2);
            Assert.fail("adding without a uri should throw exception");
        } catch (Exception e) {
        }
        ns2.setURI("bad uri");
        try {
            catalog.add(ns2);
            Assert.fail("adding an invalid uri should throw exception");
        } catch (Exception e) {
        }
        ns2.setURI("ns2URI");
        try {
            catalog.getNamespaces().add(ns2);
            Assert.fail("adding directly should throw an exception");
        } catch (Exception e) {
        }
        catalog.add(ns2);
    }

    @Test
    public void testAddIsolatedNamespace() {
        // create non isolated namespace
        NamespaceInfoImpl namespace1 = new NamespaceInfoImpl();
        namespace1.setPrefix("isolated_namespace_1");
        namespace1.setURI("http://www.isolated_namespace.com");
        // create isolated namespace with the same URI
        NamespaceInfoImpl namespace2 = new NamespaceInfoImpl();
        namespace2.setPrefix("isolated_namespace_2");
        namespace2.setURI("http://www.isolated_namespace.com");
        namespace2.setIsolated(true);
        try {
            // add the namespaces to the catalog
            catalog.add(namespace1);
            catalog.add(namespace2);
            // retrieve the non isolated namespace by prefix
            NamespaceInfo foundNamespace1 = catalog.getNamespaceByPrefix("isolated_namespace_1");
            Assert.assertThat(foundNamespace1.getPrefix(), Matchers.is("isolated_namespace_1"));
            Assert.assertThat(foundNamespace1.getURI(), Matchers.is("http://www.isolated_namespace.com"));
            Assert.assertThat(foundNamespace1.isIsolated(), Matchers.is(false));
            // retrieve the isolated namespace by prefix
            NamespaceInfo foundNamespace2 = catalog.getNamespaceByPrefix("isolated_namespace_2");
            Assert.assertThat(foundNamespace2.getPrefix(), Matchers.is("isolated_namespace_2"));
            Assert.assertThat(foundNamespace2.getURI(), Matchers.is("http://www.isolated_namespace.com"));
            Assert.assertThat(foundNamespace2.isIsolated(), Matchers.is(true));
            // retrieve the namespace by URI, the non isolated one should be returned
            NamespaceInfo foundNamespace3 = catalog.getNamespaceByURI("http://www.isolated_namespace.com");
            Assert.assertThat(foundNamespace3.getPrefix(), Matchers.is("isolated_namespace_1"));
            Assert.assertThat(foundNamespace3.getURI(), Matchers.is("http://www.isolated_namespace.com"));
            Assert.assertThat(foundNamespace3.isIsolated(), Matchers.is(false));
            // remove the non isolated namespace
            catalog.remove(foundNamespace1);
            // retrieve the namespace by URI, NULL should be returned
            NamespaceInfo foundNamespace4 = catalog.getNamespaceByURI("http://www.isolated_namespace.com");
            Assert.assertThat(foundNamespace4, Matchers.nullValue());
        } finally {
            // remove the namespaces
            catalog.remove(namespace1);
            catalog.remove(namespace2);
        }
    }

    @Test
    public void testRemoveNamespace() {
        catalog.add(ns);
        Assert.assertEquals(1, catalog.getNamespaces().size());
        try {
            Assert.assertFalse(catalog.getNamespaces().remove(ns));
            Assert.fail("removing directly should throw an exception");
        } catch (Exception e) {
        }
        catalog.remove(ns);
        Assert.assertTrue(catalog.getNamespaces().isEmpty());
    }

    @Test
    public void testGetNamespaceById() {
        catalog.add(ns);
        NamespaceInfo ns2 = catalog.getNamespace(ns.getId());
        Assert.assertNotNull(ns2);
        Assert.assertFalse(((ns) == ns2));
        Assert.assertEquals(ns, ns2);
    }

    @Test
    public void testGetNamespaceByPrefix() {
        catalog.add(ns);
        NamespaceInfo ns2 = catalog.getNamespaceByPrefix(ns.getPrefix());
        Assert.assertNotNull(ns2);
        Assert.assertFalse(((ns) == ns2));
        Assert.assertEquals(ns, ns2);
        NamespaceInfo ns3 = catalog.getNamespaceByPrefix(null);
        Assert.assertNotNull(ns3);
        Assert.assertFalse(((ns) == ns3));
        Assert.assertEquals(ns, ns3);
        NamespaceInfo ns4 = catalog.getNamespaceByPrefix(DEFAULT);
        Assert.assertNotNull(ns4);
        Assert.assertFalse(((ns) == ns4));
        Assert.assertEquals(ns, ns4);
    }

    @Test
    public void testGetNamespaceByURI() {
        catalog.add(ns);
        NamespaceInfo ns2 = catalog.getNamespaceByURI(ns.getURI());
        Assert.assertNotNull(ns2);
        Assert.assertFalse(((ns) == ns2));
        Assert.assertEquals(ns, ns2);
    }

    @Test
    public void testSetDefaultNamespaceInvalid() {
        try {
            catalog.setDefaultNamespace(ns);
            Assert.fail("Default namespace must exist in catalog");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No such namespace: 'wsName'", e.getMessage());
        }
    }

    @Test
    public void testModifyNamespace() {
        catalog.add(ns);
        NamespaceInfo ns2 = catalog.getNamespaceByPrefix(ns.getPrefix());
        ns2.setPrefix(null);
        ns2.setURI(null);
        try {
            catalog.save(ns2);
            Assert.fail("setting prefix to null should throw exception");
        } catch (Exception e) {
        }
        ns2.setPrefix("ns2Prefix");
        try {
            catalog.save(ns2);
            Assert.fail("setting uri to null should throw exception");
        } catch (Exception e) {
        }
        ns2.setURI("ns2URI");
        NamespaceInfo ns3 = catalog.getNamespaceByPrefix(ns.getPrefix());
        Assert.assertEquals(ns.getPrefix(), ns3.getPrefix());
        Assert.assertEquals(ns.getURI(), ns3.getURI());
        catalog.save(ns2);
        // ns3 = catalog.getNamespaceByPrefix(ns.getPrefix());
        ns3 = catalog.getNamespaceByPrefix("ns2Prefix");
        Assert.assertEquals(ns2, ns3);
        Assert.assertEquals("ns2Prefix", ns3.getPrefix());
        Assert.assertEquals("ns2URI", ns3.getURI());
    }

    @Test
    public void testNamespaceEvents() {
        CatalogImplTest.TestListener l = new CatalogImplTest.TestListener();
        catalog.addListener(l);
        NamespaceInfo ns = catalog.getFactory().createNamespace();
        ns.setPrefix("ns2Prefix");
        ns.setURI("ns2URI");
        Assert.assertTrue(l.added.isEmpty());
        Assert.assertTrue(l.modified.isEmpty());
        catalog.add(ns);
        Assert.assertEquals(1, l.added.size());
        Assert.assertEquals(ns, l.added.get(0).getSource());
        Assert.assertEquals(1, l.modified.size());
        Assert.assertEquals(catalog, l.modified.get(0).getSource());
        Assert.assertEquals("defaultNamespace", l.modified.get(0).getPropertyNames().get(0));
        Assert.assertEquals(1, l.postModified.size());
        Assert.assertEquals(catalog, l.postModified.get(0).getSource());
        Assert.assertEquals("defaultNamespace", l.postModified.get(0).getPropertyNames().get(0));
        ns = catalog.getNamespaceByPrefix("ns2Prefix");
        ns.setURI("changed");
        catalog.save(ns);
        Assert.assertEquals(2, l.modified.size());
        Assert.assertEquals(1, l.modified.get(1).getPropertyNames().size());
        Assert.assertTrue(l.modified.get(1).getPropertyNames().get(0).equalsIgnoreCase("uri"));
        Assert.assertTrue(l.modified.get(1).getOldValues().contains("ns2URI"));
        Assert.assertTrue(l.modified.get(1).getNewValues().contains("changed"));
        Assert.assertEquals(2, l.postModified.size());
        Assert.assertEquals(1, l.postModified.get(1).getPropertyNames().size());
        Assert.assertTrue(l.postModified.get(1).getPropertyNames().get(0).equalsIgnoreCase("uri"));
        Assert.assertTrue(l.postModified.get(1).getOldValues().contains("ns2URI"));
        Assert.assertTrue(l.postModified.get(1).getNewValues().contains("changed"));
        Assert.assertTrue(l.removed.isEmpty());
        catalog.remove(ns);
        Assert.assertEquals(1, l.removed.size());
        Assert.assertEquals(ns, l.removed.get(0).getSource());
    }

    @Test
    public void testAddWorkspace() {
        Assert.assertTrue(catalog.getWorkspaces().isEmpty());
        catalog.add(ws);
        Assert.assertEquals(1, catalog.getWorkspaces().size());
        WorkspaceInfo ws2 = catalog.getFactory().createWorkspace();
        try {
            catalog.getWorkspaces().add(ws2);
            Assert.fail("adding directly should throw an exception");
        } catch (Exception e) {
        }
        try {
            catalog.add(ws2);
            Assert.fail("addign without a name should throw an exception");
        } catch (Exception e) {
        }
        ws2.setName("ws2");
        catalog.add(ws2);
    }

    @Test
    public void testRemoveWorkspace() {
        catalog.add(ws);
        Assert.assertEquals(1, catalog.getWorkspaces().size());
        try {
            Assert.assertFalse(catalog.getWorkspaces().remove(ws));
            Assert.fail("removing directly should throw an exception");
        } catch (Exception e) {
        }
        catalog.remove(ws);
        Assert.assertTrue(catalog.getWorkspaces().isEmpty());
    }

    @Test
    public void testAddIsolatedWorkspace() {
        // create isolated workspace
        WorkspaceInfoImpl workspace = new WorkspaceInfoImpl();
        workspace.setName("isolated_workspace");
        workspace.setIsolated(true);
        try {
            // add it to the catalog
            catalog.add(workspace);
            // retrieve the isolated workspace
            WorkspaceInfo foundWorkspace = catalog.getWorkspaceByName("isolated_workspace");
            Assert.assertThat(foundWorkspace.isIsolated(), Matchers.is(true));
        } finally {
            // remove the isolated workspace
            catalog.remove(workspace);
        }
    }

    @Test
    public void testAutoSetDefaultWorkspace() {
        catalog.add(ws);
        Assert.assertEquals(1, catalog.getWorkspaces().size());
        Assert.assertEquals(ws, catalog.getDefaultWorkspace());
        Assert.assertNull(catalog.getDefaultNamespace());
    }

    @Test
    public void testRemoveDefaultWorkspace() {
        catalog.add(ws);
        Assert.assertNotNull(catalog.getDefaultWorkspace());
        catalog.remove(ws);
        Assert.assertNull(catalog.getDefaultWorkspace());
    }

    @Test
    public void testAutoCascadeDefaultWorksapce() {
        CatalogFactory factory = catalog.getFactory();
        WorkspaceInfo ws1 = factory.createWorkspace();
        ws1.setName("ws1Name");
        WorkspaceInfo ws2 = factory.createWorkspace();
        ws2.setName("ws2Name");
        catalog.add(ws1);
        catalog.add(ws2);
        Assert.assertEquals(ws1, catalog.getDefaultWorkspace());
        catalog.remove(ws1);
        Assert.assertEquals(ws2, catalog.getDefaultWorkspace());
    }

    @Test
    public void testAutoSetDefaultNamespace() {
        catalog.add(ns);
        Assert.assertEquals(1, catalog.getNamespaces().size());
        Assert.assertEquals(ns, catalog.getDefaultNamespace());
    }

    @Test
    public void testRemoveDefaultNamespace() {
        catalog.add(ns);
        catalog.remove(ns);
        Assert.assertNull(catalog.getDefaultNamespace());
    }

    @Test
    public void testAutoCascadeDefaultNamespace() {
        CatalogFactory factory = catalog.getFactory();
        NamespaceInfo ns1 = factory.createNamespace();
        ns1.setPrefix("1");
        ns1.setURI("http://www.geoserver.org/1");
        NamespaceInfo ns2 = factory.createNamespace();
        ns2.setPrefix("2");
        ns2.setURI("http://www.geoserver.org/2");
        catalog.add(ns1);
        catalog.add(ns2);
        Assert.assertEquals(ns1, catalog.getDefaultNamespace());
        catalog.remove(ns1);
        Assert.assertEquals(ns2, catalog.getDefaultNamespace());
    }

    @Test
    public void testAutoSetDefaultStore() {
        catalog.add(ws);
        catalog.add(ds);
        Assert.assertEquals(1, catalog.getDataStores().size());
        Assert.assertEquals(ds, catalog.getDefaultDataStore(ws));
    }

    @Test
    public void testRemoveDefaultStore() {
        catalog.add(ws);
        catalog.add(ds);
        catalog.remove(ds);
        Assert.assertNull(catalog.getDefaultDataStore(ws));
    }

    @Test
    public void testGetWorkspaceById() {
        catalog.add(ws);
        WorkspaceInfo ws2 = catalog.getWorkspace(ws.getId());
        Assert.assertNotNull(ws2);
        Assert.assertFalse(((ws) == ws2));
        Assert.assertEquals(ws, ws2);
    }

    @Test
    public void testGetWorkspaceByName() {
        catalog.add(ws);
        WorkspaceInfo ws2 = catalog.getWorkspaceByName(ws.getName());
        Assert.assertNotNull(ws2);
        Assert.assertFalse(((ws) == ws2));
        Assert.assertEquals(ws, ws2);
        WorkspaceInfo ws3 = catalog.getWorkspaceByName(null);
        Assert.assertNotNull(ws3);
        Assert.assertFalse(((ws) == ws3));
        Assert.assertEquals(ws, ws3);
        WorkspaceInfo ws4 = catalog.getWorkspaceByName(DEFAULT);
        Assert.assertNotNull(ws4);
        Assert.assertFalse(((ws) == ws4));
        Assert.assertEquals(ws, ws4);
    }

    @Test
    public void testSetDefaultWorkspaceInvalid() {
        try {
            catalog.setDefaultWorkspace(ws);
            Assert.fail("Default workspace must exist in catalog");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No such workspace: 'wsName'", e.getMessage());
        }
    }

    @Test
    public void testModifyWorkspace() {
        catalog.add(ws);
        WorkspaceInfo ws2 = catalog.getWorkspaceByName(ws.getName());
        ws2.setName(null);
        try {
            catalog.save(ws2);
            Assert.fail("setting name to null should throw exception");
        } catch (Exception e) {
        }
        ws2.setName("ws2");
        WorkspaceInfo ws3 = catalog.getWorkspaceByName(ws.getName());
        Assert.assertEquals("wsName", ws3.getName());
        catalog.save(ws2);
        ws3 = catalog.getWorkspaceByName(ws2.getName());
        Assert.assertEquals(ws2, ws3);
        Assert.assertEquals("ws2", ws3.getName());
    }

    @Test
    public void testWorkspaceEvents() {
        CatalogImplTest.TestListener l = new CatalogImplTest.TestListener();
        catalog.addListener(l);
        WorkspaceInfo ws = catalog.getFactory().createWorkspace();
        ws.setName("ws2");
        Assert.assertTrue(l.added.isEmpty());
        Assert.assertTrue(l.modified.isEmpty());
        catalog.add(ws);
        Assert.assertEquals(1, l.added.size());
        Assert.assertEquals(ws, l.added.get(0).getSource());
        Assert.assertEquals(catalog, l.modified.get(0).getSource());
        Assert.assertEquals("defaultWorkspace", l.modified.get(0).getPropertyNames().get(0));
        Assert.assertEquals(catalog, l.postModified.get(0).getSource());
        Assert.assertEquals("defaultWorkspace", l.postModified.get(0).getPropertyNames().get(0));
        ws = catalog.getWorkspaceByName("ws2");
        ws.setName("changed");
        catalog.save(ws);
        Assert.assertEquals(2, l.modified.size());
        Assert.assertTrue(l.modified.get(1).getPropertyNames().contains("name"));
        Assert.assertTrue(l.modified.get(1).getOldValues().contains("ws2"));
        Assert.assertTrue(l.modified.get(1).getNewValues().contains("changed"));
        Assert.assertTrue(l.postModified.get(1).getPropertyNames().contains("name"));
        Assert.assertTrue(l.postModified.get(1).getOldValues().contains("ws2"));
        Assert.assertTrue(l.postModified.get(1).getNewValues().contains("changed"));
        Assert.assertTrue(l.removed.isEmpty());
        catalog.remove(ws);
        Assert.assertEquals(1, l.removed.size());
        Assert.assertEquals(ws, l.removed.get(0).getSource());
    }

    @Test
    public void testAddDataStore() {
        Assert.assertTrue(catalog.getDataStores().isEmpty());
        ds.setWorkspace(null);
        try {
            catalog.add(ds);
            Assert.fail("adding with no workspace should throw exception");
        } catch (Exception e) {
        }
        ds.setWorkspace(ws);
        catalog.add(ws);
        catalog.add(ds);
        Assert.assertEquals(1, catalog.getDataStores().size());
        DataStoreInfo retrieved = catalog.getDataStore(ds.getId());
        DataStoreInfo ds2 = catalog.getFactory().createDataStore();
        try {
            catalog.add(ds2);
            Assert.fail("adding without a name should throw exception");
        } catch (Exception e) {
        }
        ds2.setName("ds2Name");
        try {
            catalog.getDataStores().add(ds2);
            Assert.fail("adding directly should throw an exception");
        } catch (Exception e) {
        }
        ds2.setWorkspace(ws);
        catalog.add(ds2);
        Assert.assertEquals(2, catalog.getDataStores().size());
    }

    @Test
    public void testAddDataStoreDefaultWorkspace() {
        catalog.add(ws);
        catalog.setDefaultWorkspace(ws);
        DataStoreInfo ds2 = catalog.getFactory().createDataStore();
        ds2.setName("ds2Name");
        catalog.add(ds2);
        Assert.assertEquals(ws, ds2.getWorkspace());
    }

    @Test
    public void testRemoveDataStore() {
        addDataStore();
        Assert.assertEquals(1, catalog.getDataStores().size());
        try {
            Assert.assertFalse(catalog.getDataStores().remove(ds));
            Assert.fail("removing directly should throw an exception");
        } catch (Exception e) {
        }
        catalog.remove(ds);
        Assert.assertTrue(catalog.getDataStores().isEmpty());
    }

    @Test
    public void testGetDataStoreById() {
        addDataStore();
        DataStoreInfo ds2 = catalog.getDataStore(ds.getId());
        Assert.assertNotNull(ds2);
        Assert.assertFalse(((ds) == ds2));
        Assert.assertEquals(ds, ds2);
    }

    @Test
    public void testGetDataStoreByName() {
        addDataStore();
        DataStoreInfo ds2 = catalog.getDataStoreByName(ds.getName());
        Assert.assertNotNull(ds2);
        Assert.assertFalse(((ds) == ds2));
        Assert.assertEquals(ds, ds2);
        DataStoreInfo ds3 = catalog.getDataStoreByName(ws, null);
        Assert.assertNotNull(ds3);
        Assert.assertFalse(((ds) == ds3));
        Assert.assertEquals(ds, ds3);
        DataStoreInfo ds4 = catalog.getDataStoreByName(ws, DEFAULT);
        Assert.assertNotNull(ds4);
        Assert.assertFalse(((ds) == ds4));
        Assert.assertEquals(ds, ds4);
        DataStoreInfo ds5 = catalog.getDataStoreByName(DEFAULT, DEFAULT);
        Assert.assertNotNull(ds5);
        Assert.assertFalse(((ds) == ds5));
        Assert.assertEquals(ds, ds5);
    }

    @Test
    public void testGetStoreByName() {
        addDataStore();
        StoreInfo ds2 = catalog.getStoreByName(ds.getName(), StoreInfo.class);
        Assert.assertNotNull(ds2);
        Assert.assertFalse(((ds) == ds2));
        Assert.assertEquals(ds, ds2);
        StoreInfo ds3 = catalog.getStoreByName(ws, null, StoreInfo.class);
        Assert.assertNotNull(ds3);
        Assert.assertFalse(((ds) == ds3));
        Assert.assertEquals(ds, ds3);
        StoreInfo ds4 = catalog.getStoreByName(ws, DEFAULT, StoreInfo.class);
        Assert.assertNotNull(ds4);
        Assert.assertFalse(((ds) == ds4));
        Assert.assertEquals(ds, ds4);
        StoreInfo ds5 = catalog.getStoreByName(DEFAULT, DEFAULT, StoreInfo.class);
        Assert.assertNotNull(ds5);
        Assert.assertFalse(((ds) == ds5));
        Assert.assertEquals(ds, ds5);
        StoreInfo ds6 = catalog.getStoreByName(((String) (null)), null, StoreInfo.class);
        Assert.assertNotNull(ds6);
        Assert.assertFalse(((ds) == ds6));
        Assert.assertEquals(ds, ds3);
        StoreInfo ds7 = catalog.getStoreByName(DEFAULT, DEFAULT, StoreInfo.class);
        Assert.assertNotNull(ds7);
        Assert.assertFalse(((ds) == ds7));
        Assert.assertEquals(ds6, ds7);
    }

    @Test
    public void testModifyDataStore() {
        addDataStore();
        DataStoreInfo ds2 = catalog.getDataStoreByName(ds.getName());
        ds2.setName("dsName2");
        ds2.setDescription("dsDescription2");
        DataStoreInfo ds3 = catalog.getDataStoreByName(ds.getName());
        Assert.assertEquals("dsName", ds3.getName());
        Assert.assertEquals("dsDescription", ds3.getDescription());
        catalog.save(ds2);
        ds3 = catalog.getDataStoreByName("dsName2");
        Assert.assertEquals(ds2, ds3);
        Assert.assertEquals("dsName2", ds3.getName());
        Assert.assertEquals("dsDescription2", ds3.getDescription());
    }

    @Test
    public void testChangeDataStoreWorkspace() throws Exception {
        addDataStore();
        WorkspaceInfo ws2 = catalog.getFactory().createWorkspace();
        ws2.setName("newWorkspace");
        catalog.add(ws2);
        ws2 = catalog.getWorkspaceByName(ws2.getName());
        DataStoreInfo ds2 = catalog.getDataStoreByName(ds.getName());
        ds2.setWorkspace(ws2);
        catalog.save(ds2);
        Assert.assertNull(catalog.getDataStoreByName(ws, ds2.getName()));
        Assert.assertNotNull(catalog.getDataStoreByName(ws2, ds2.getName()));
    }

    @Test
    public void testDataStoreEvents() {
        addWorkspace();
        CatalogImplTest.TestListener l = new CatalogImplTest.TestListener();
        catalog.addListener(l);
        Assert.assertEquals(0, l.added.size());
        catalog.add(ds);
        Assert.assertEquals(1, l.added.size());
        Assert.assertEquals(ds, l.added.get(0).getSource());
        Assert.assertEquals(1, l.modified.size());
        Assert.assertEquals(catalog, l.modified.get(0).getSource());
        Assert.assertEquals(1, l.postModified.size());
        Assert.assertEquals(catalog, l.postModified.get(0).getSource());
        DataStoreInfo ds2 = catalog.getDataStoreByName(ds.getName());
        ds2.setDescription("changed");
        Assert.assertEquals(1, l.modified.size());
        catalog.save(ds2);
        Assert.assertEquals(2, l.modified.size());
        CatalogModifyEvent me = l.modified.get(1);
        Assert.assertEquals(ds2, me.getSource());
        Assert.assertEquals(1, me.getPropertyNames().size());
        Assert.assertEquals("description", me.getPropertyNames().get(0));
        Assert.assertEquals(1, me.getOldValues().size());
        Assert.assertEquals(1, me.getNewValues().size());
        Assert.assertEquals("dsDescription", me.getOldValues().get(0));
        Assert.assertEquals("changed", me.getNewValues().get(0));
        CatalogPostModifyEvent pme = l.postModified.get(1);
        Assert.assertEquals(ds2, pme.getSource());
        Assert.assertEquals(1, pme.getPropertyNames().size());
        Assert.assertEquals("description", pme.getPropertyNames().get(0));
        Assert.assertEquals(1, pme.getOldValues().size());
        Assert.assertEquals(1, pme.getNewValues().size());
        Assert.assertEquals("dsDescription", pme.getOldValues().get(0));
        Assert.assertEquals("changed", pme.getNewValues().get(0));
        Assert.assertEquals(0, l.removed.size());
        catalog.remove(ds);
        Assert.assertEquals(1, l.removed.size());
        Assert.assertEquals(ds, l.removed.get(0).getSource());
    }

    @Test
    public void testAddFeatureType() {
        Assert.assertTrue(catalog.getFeatureTypes().isEmpty());
        addFeatureType();
        Assert.assertEquals(1, catalog.getFeatureTypes().size());
        FeatureTypeInfo ft2 = catalog.getFactory().createFeatureType();
        try {
            catalog.add(ft2);
            Assert.fail("adding with no name should throw exception");
        } catch (Exception e) {
        }
        ft2.setName("ft2Name");
        try {
            catalog.add(ft2);
            Assert.fail("adding with no store should throw exception");
        } catch (Exception e) {
        }
        ft2.setStore(ds);
        ft2.getKeywords().add(new Keyword("keyword"));
        catalog.add(ft2);
        FeatureTypeInfo ft3 = catalog.getFactory().createFeatureType();
        ft3.setName("ft3Name");
        try {
            catalog.getFeatureTypes().add(ft3);
            Assert.fail("adding directly should throw an exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void testAddCoverage() {
        // set a default namespace
        Assert.assertNotNull(catalog.getCoverages());
        Assert.assertTrue(catalog.getCoverages().isEmpty());
        addCoverage();
        Assert.assertEquals(1, catalog.getCoverages().size());
        CoverageInfo cv2 = catalog.getFactory().createCoverage();
        try {
            catalog.add(cv2);
            Assert.fail("adding with no name should throw exception");
        } catch (Exception e) {
        }
        cv2.setName("cv2Name");
        try {
            catalog.add(cv2);
            Assert.fail("adding with no store should throw exception");
        } catch (Exception e) {
        }
        cv2.setStore(cs);
        catalog.add(cv2);
        Assert.assertEquals(2, catalog.getCoverages().size());
        CoverageInfo fromCatalog = catalog.getCoverageByName("cv2Name");
        Assert.assertNotNull(fromCatalog);
        // ensure the collection properties are set to NullObjects and not to null
        Assert.assertNotNull(fromCatalog.getParameters());
        CoverageInfo cv3 = catalog.getFactory().createCoverage();
        cv3.setName("cv3Name");
        try {
            catalog.getCoverages().add(cv3);
            Assert.fail("adding directly should throw an exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void testAddWMSLayer() {
        // set a default namespace
        Assert.assertTrue(catalog.getResources(WMSLayerInfo.class).isEmpty());
        addWMSLayer();
        Assert.assertEquals(1, catalog.getResources(WMSLayerInfo.class).size());
    }

    @Test
    public void testAddWMTSLayer() {
        Assert.assertTrue(catalog.getResources(WMTSLayerInfo.class).isEmpty());
        addWMTSLayer();
        Assert.assertEquals(1, catalog.getResources(WMTSLayerInfo.class).size());
    }

    @Test
    public void testRemoveFeatureType() {
        addFeatureType();
        Assert.assertFalse(catalog.getFeatureTypes().isEmpty());
        try {
            catalog.getFeatureTypes().remove(ft);
            Assert.fail("removing directly should cause exception");
        } catch (Exception e) {
        }
        catalog.remove(ft);
        Assert.assertTrue(catalog.getFeatureTypes().isEmpty());
    }

    @Test
    public void testRemoveWMSLayer() {
        addWMSLayer();
        Assert.assertFalse(catalog.getResources(WMSLayerInfo.class).isEmpty());
        catalog.remove(wl);
        Assert.assertTrue(catalog.getResources(WMSLayerInfo.class).isEmpty());
    }

    @Test
    public void testRemoveWMTSLayer() {
        addWMTSLayer();
        Assert.assertFalse(catalog.getResources(WMTSLayerInfo.class).isEmpty());
        catalog.remove(wmtsl);
        Assert.assertTrue(catalog.getResources(WMTSLayerInfo.class).isEmpty());
    }

    @Test
    public void testGetFeatureTypeById() {
        addFeatureType();
        FeatureTypeInfo ft2 = catalog.getFeatureType(ft.getId());
        Assert.assertNotNull(ft2);
        Assert.assertFalse(((ft) == ft2));
        Assert.assertEquals(ft, ft2);
    }

    @Test
    public void testGetFeatureTypeByName() {
        addFeatureType();
        FeatureTypeInfo ft2 = catalog.getFeatureTypeByName(ft.getName());
        Assert.assertNotNull(ft2);
        Assert.assertFalse(((ft) == ft2));
        Assert.assertEquals(ft, ft2);
        NamespaceInfo ns2 = catalog.getFactory().createNamespace();
        ns2.setPrefix("ns2Prefix");
        ns2.setURI("ns2URI");
        catalog.add(ns2);
        FeatureTypeInfo ft3 = catalog.getFactory().createFeatureType();
        ft3.setName("ft3Name");
        ft3.setStore(ds);
        ft3.setNamespace(ns2);
        catalog.add(ft3);
        FeatureTypeInfo ft4 = catalog.getFeatureTypeByName(ns2.getPrefix(), ft3.getName());
        Assert.assertNotNull(ft4);
        Assert.assertFalse((ft4 == ft3));
        Assert.assertEquals(ft3, ft4);
        ft4 = catalog.getFeatureTypeByName(ns2.getURI(), ft3.getName());
        Assert.assertNotNull(ft4);
        Assert.assertFalse((ft4 == ft3));
        Assert.assertEquals(ft3, ft4);
    }

    @Test
    public void testGetFeatureTypesByStore() {
        catalog.add(ns);
        catalog.add(ws);
        catalog.setDefaultNamespace(ns);
        catalog.setDefaultWorkspace(ws);
        DataStoreInfo ds1 = catalog.getFactory().createDataStore();
        ds1.setName("ds1");
        catalog.add(ds1);
        FeatureTypeInfo ft1 = catalog.getFactory().createFeatureType();
        ft1.setName("ft1");
        ft1.setStore(ds1);
        catalog.add(ft1);
        FeatureTypeInfo ft2 = catalog.getFactory().createFeatureType();
        ft2.setName("ft2");
        ft2.setStore(ds1);
        catalog.add(ft2);
        DataStoreInfo ds2 = catalog.getFactory().createDataStore();
        ds2.setName("ds2");
        catalog.add(ds2);
        FeatureTypeInfo ft3 = catalog.getFactory().createFeatureType();
        ft3.setName("ft3");
        ft3.setStore(ds2);
        catalog.add(ft3);
        List<FeatureTypeInfo> ft = catalog.getFeatureTypesByStore(ds1);
        Assert.assertEquals(2, ft.size());
        ft = catalog.getFeatureTypesByStore(ds2);
        Assert.assertEquals(1, ft.size());
        List<ResourceInfo> r = catalog.getResourcesByStore(ds1, ResourceInfo.class);
        Assert.assertEquals(2, r.size());
        Assert.assertTrue(r.contains(ft1));
        Assert.assertTrue(r.contains(ft2));
    }

    @Test
    public void testModifyFeatureType() {
        addFeatureType();
        FeatureTypeInfo ft2 = catalog.getFeatureTypeByName(ft.getName());
        ft2.setDescription("ft2Description");
        ft2.getKeywords().add(new Keyword("ft2"));
        FeatureTypeInfo ft3 = catalog.getFeatureTypeByName(ft.getName());
        Assert.assertEquals("ftName", ft3.getName());
        Assert.assertEquals("ftDescription", ft3.getDescription());
        Assert.assertTrue(ft3.getKeywords().isEmpty());
        catalog.save(ft2);
        ft3 = catalog.getFeatureTypeByName(ft.getName());
        Assert.assertEquals(ft2, ft3);
        Assert.assertEquals("ft2Description", ft3.getDescription());
        Assert.assertEquals(1, ft3.getKeywords().size());
    }

    @Test
    public void testModifyMetadataLinks() {
        addFeatureType();
        FeatureTypeInfo ft2 = catalog.getFeatureTypeByName(ft.getName());
        MetadataLinkInfo ml = catalog.getFactory().createMetadataLink();
        ml.setContent("http://www.geoserver.org/meta");
        ml.setType("text/plain");
        ml.setMetadataType("iso");
        ft2.getMetadataLinks().clear();
        ft2.getMetadataLinks().add(ml);
        catalog.save(ft2);
        FeatureTypeInfo ft3 = catalog.getFeatureTypeByName(ft.getName());
        MetadataLinkInfo ml3 = ft3.getMetadataLinks().get(0);
        ml3.setType("application/json");
        // do not save and grab another, the metadata link must not have been modified
        FeatureTypeInfo ft4 = catalog.getFeatureTypeByName(ft.getName());
        MetadataLinkInfo ml4 = ft4.getMetadataLinks().get(0);
        Assert.assertEquals("text/plain", ml4.getType());
        // now save and grab yet another, the modification must have happened
        catalog.save(ft3);
        FeatureTypeInfo ft5 = catalog.getFeatureTypeByName(ft.getName());
        MetadataLinkInfo ml5 = ft5.getMetadataLinks().get(0);
        Assert.assertEquals("application/json", ml5.getType());
    }

    @Test
    public void testModifyDataLinks() {
        addFeatureType();
        FeatureTypeInfo ft2 = catalog.getFeatureTypeByName(ft.getName());
        DataLinkInfo ml = catalog.getFactory().createDataLink();
        ml.setContent("http://www.geoserver.org/meta");
        ml.setType("text/plain");
        ft2.getDataLinks().clear();
        ft2.getDataLinks().add(ml);
        catalog.save(ft2);
        FeatureTypeInfo ft3 = catalog.getFeatureTypeByName(ft.getName());
        DataLinkInfo ml3 = ft3.getDataLinks().get(0);
        ml3.setType("application/json");
        // do not save and grab another, the metadata link must not have been modified
        FeatureTypeInfo ft4 = catalog.getFeatureTypeByName(ft.getName());
        DataLinkInfo ml4 = ft4.getDataLinks().get(0);
        Assert.assertEquals("text/plain", ml4.getType());
        // now save and grab yet another, the modification must have happened
        catalog.save(ft3);
        FeatureTypeInfo ft5 = catalog.getFeatureTypeByName(ft.getName());
        DataLinkInfo ml5 = ft5.getDataLinks().get(0);
        Assert.assertEquals("application/json", ml5.getType());
    }

    @Test
    public void testFeatureTypeEvents() {
        // set default namespace
        addNamespace();
        addDataStore();
        CatalogImplTest.TestListener l = new CatalogImplTest.TestListener();
        catalog.addListener(l);
        FeatureTypeInfo ft = catalog.getFactory().createFeatureType();
        ft.setName("ftName");
        ft.setDescription("ftDescription");
        ft.setStore(ds);
        Assert.assertTrue(l.added.isEmpty());
        catalog.add(ft);
        Assert.assertEquals(1, l.added.size());
        Assert.assertEquals(ft, l.added.get(0).getSource());
        ft = catalog.getFeatureTypeByName("ftName");
        ft.setDescription("changed");
        Assert.assertTrue(l.modified.isEmpty());
        catalog.save(ft);
        Assert.assertEquals(1, l.modified.size());
        Assert.assertEquals(ft, l.modified.get(0).getSource());
        Assert.assertTrue(l.modified.get(0).getPropertyNames().contains("description"));
        Assert.assertTrue(l.modified.get(0).getOldValues().contains("ftDescription"));
        Assert.assertTrue(l.modified.get(0).getNewValues().contains("changed"));
        Assert.assertEquals(1, l.modified.size());
        Assert.assertEquals(ft, l.postModified.get(0).getSource());
        Assert.assertTrue(l.postModified.get(0).getPropertyNames().contains("description"));
        Assert.assertTrue(l.postModified.get(0).getOldValues().contains("ftDescription"));
        Assert.assertTrue(l.postModified.get(0).getNewValues().contains("changed"));
        Assert.assertTrue(l.removed.isEmpty());
        catalog.remove(ft);
        Assert.assertEquals(1, l.removed.size());
        Assert.assertEquals(ft, l.removed.get(0).getSource());
    }

    @Test
    public void testModifyMetadata() {
        // set default namespace
        addNamespace();
        addDataStore();
        CatalogImplTest.TestListener l = new CatalogImplTest.TestListener();
        catalog.addListener(l);
        FeatureTypeInfo ft = catalog.getFactory().createFeatureType();
        ft.setName("ftName");
        ft.setDescription("ftDescription");
        ft.setStore(ds);
        Assert.assertTrue(l.added.isEmpty());
        catalog.add(ft);
        Assert.assertEquals(1, l.added.size());
        Assert.assertEquals(ft, l.added.get(0).getSource());
        ft = catalog.getFeatureTypeByName("ftName");
        ft.getMetadata().put("newValue", "abcd");
        MetadataMap newMetadata = new MetadataMap(ft.getMetadata());
        catalog.save(ft);
        Assert.assertEquals(1, l.modified.size());
        Assert.assertEquals(ft, l.modified.get(0).getSource());
        Assert.assertTrue(l.modified.get(0).getPropertyNames().contains("metadata"));
        Assert.assertTrue(l.modified.get(0).getOldValues().contains(new MetadataMap()));
        Assert.assertTrue(l.modified.get(0).getNewValues().contains(newMetadata));
    }

    @Test
    public void testAddLayer() {
        Assert.assertTrue(catalog.getLayers().isEmpty());
        addLayer();
        Assert.assertEquals(1, catalog.getLayers().size());
        LayerInfo l2 = catalog.getFactory().createLayer();
        try {
            catalog.add(l2);
            Assert.fail("adding with no name should throw exception");
        } catch (Exception e) {
        }
        // l2.setName( "l2" );
        try {
            catalog.add(l2);
            Assert.fail("adding with no resource should throw exception");
        } catch (Exception e) {
        }
        l2.setResource(ft);
        // try {
        // catalog.add( l2 );
        // fail( "adding with no default style should throw exception");
        // }
        // catch( Exception e) {}
        // 
        l2.setDefaultStyle(s);
        try {
            catalog.add(l2);
            Assert.fail("Adding a second layer for the same resource should throw exception, layer name is tied to resource name and would end up with two layers named the same or a broken catalog");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("already exists"));
        }
        Assert.assertEquals(1, catalog.getLayers().size());
    }

    @Test
    public void testGetLayerById() {
        addLayer();
        LayerInfo l2 = catalog.getLayer(l.getId());
        Assert.assertNotNull(l2);
        Assert.assertNotSame(l, l2);
        Assert.assertEquals(l, l2);
    }

    @Test
    public void testGetLayerByName() {
        addLayer();
        LayerInfo l2 = catalog.getLayerByName(l.getName());
        Assert.assertNotNull(l2);
        Assert.assertNotSame(l, l2);
        Assert.assertEquals(l, l2);
    }

    @Test
    public void testGetLayerByNameWithoutColon() {
        // create two workspaces
        catalog.add(nsA);
        catalog.add(nsB);
        catalog.add(wsA);
        catalog.add(wsB);
        catalog.setDefaultNamespace(nsB);
        catalog.setDefaultWorkspace(wsB);
        catalog.add(dsA);
        catalog.add(dsB);
        // create three resources, aaa:bar, bbb:bar, aaa:bar2
        FeatureTypeInfo ftA = catalog.getFactory().createFeatureType();
        ftA.setEnabled(true);
        ftA.setName("bar");
        ftA.setAbstract("ftAbstract");
        ftA.setDescription("ftDescription");
        ftA.setStore(dsA);
        ftA.setNamespace(nsA);
        FeatureTypeInfo ftB = catalog.getFactory().createFeatureType();
        ftB.setName("bar");
        ftB.setAbstract("ftAbstract");
        ftB.setDescription("ftDescription");
        ftB.setStore(dsB);
        ftB.setNamespace(nsB);
        FeatureTypeInfo ftC = catalog.getFactory().createFeatureType();
        ftC.setName("bar2");
        ftC.setAbstract("ftAbstract");
        ftC.setDescription("ftDescription");
        ftC.setStore(dsA);
        ftC.setNamespace(nsA);
        ftC.setEnabled(true);
        ftB.setEnabled(true);
        catalog.add(ftA);
        catalog.add(ftB);
        catalog.add(ftC);
        addStyle();
        LayerInfo lA = catalog.getFactory().createLayer();
        lA.setResource(ftA);
        lA.setDefaultStyle(s);
        lA.setEnabled(true);
        LayerInfo lB = catalog.getFactory().createLayer();
        lB.setResource(ftB);
        lB.setDefaultStyle(s);
        lB.setEnabled(true);
        LayerInfo lC = catalog.getFactory().createLayer();
        lC.setResource(ftC);
        lC.setDefaultStyle(s);
        lC.setEnabled(true);
        catalog.add(lA);
        catalog.add(lB);
        catalog.add(lC);
        // this search should give us back the bar in the default worksapce
        LayerInfo searchedResult = catalog.getLayerByName("bar");
        Assert.assertNotNull(searchedResult);
        Assert.assertEquals(lB, searchedResult);
        // this search should give us back the bar in the other workspace
        searchedResult = catalog.getLayerByName("aaa:bar");
        Assert.assertNotNull(searchedResult);
        Assert.assertEquals(lA, searchedResult);
        // unqualified, it should give us the only bar2 available
        searchedResult = catalog.getLayerByName("bar2");
        Assert.assertNotNull(searchedResult);
        Assert.assertEquals(lC, searchedResult);
        // qualified should work the same
        searchedResult = catalog.getLayerByName("aaa:bar2");
        Assert.assertNotNull(searchedResult);
        Assert.assertEquals(lC, searchedResult);
        // with the wrong workspace, should give us nothing
        searchedResult = catalog.getLayerByName("bbb:bar2");
        Assert.assertNull(searchedResult);
    }

    @Test
    public void testGetLayerByNameWithColon() {
        addNamespace();
        addDataStore();
        FeatureTypeInfo ft = catalog.getFactory().createFeatureType();
        ft.setEnabled(true);
        ft.setName("foo:bar");
        ft.setAbstract("ftAbstract");
        ft.setDescription("ftDescription");
        ft.setStore(ds);
        ft.setNamespace(ns);
        catalog.add(ft);
        addStyle();
        LayerInfo l = catalog.getFactory().createLayer();
        l.setResource(ft);
        l.setEnabled(true);
        l.setDefaultStyle(s);
        catalog.add(l);
        Assert.assertNotNull(catalog.getLayerByName("foo:bar"));
    }

    @Test
    public void testGetLayerByResource() {
        addLayer();
        List<LayerInfo> layers = catalog.getLayers(ft);
        Assert.assertEquals(1, layers.size());
        LayerInfo l2 = layers.get(0);
        Assert.assertNotSame(l, l2);
        Assert.assertEquals(l, l2);
    }

    @Test
    public void testRemoveLayer() {
        addLayer();
        Assert.assertEquals(1, catalog.getLayers().size());
        catalog.remove(l);
        Assert.assertTrue(catalog.getLayers().isEmpty());
    }

    @Test
    public void testModifyLayer() {
        addLayer();
        LayerInfo l2 = catalog.getLayerByName(l.getName());
        // l2.setName( null );
        l2.setResource(null);
        LayerInfo l3 = catalog.getLayerByName(l.getName());
        Assert.assertEquals(l.getName(), l3.getName());
        // try {
        // catalog.save(l2);
        // fail( "setting name to null should throw exception");
        // }
        // catch( Exception e ) {}
        // 
        // l2.setName( "changed" );
        try {
            catalog.save(l2);
            Assert.fail("setting resource to null should throw exception");
        } catch (Exception e) {
        }
        l2.setResource(ft);
        catalog.save(l2);
        // TODO: reinstate with resource/publishing split done
        // l3 = catalog.getLayerByName( "changed" );
        l3 = catalog.getLayerByName(ft.getName());
        Assert.assertNotNull(l3);
    }

    @Test
    public void testModifyLayerDefaultStyle() {
        // create new style
        CatalogFactory factory = catalog.getFactory();
        StyleInfo s2 = factory.createStyle();
        s2.setName("styleName2");
        s2.setFilename("styleFilename2");
        catalog.add(s2);
        // change the layer style
        addLayer();
        LayerInfo l2 = catalog.getLayerByName(l.getName());
        l2.setDefaultStyle(catalog.getStyleByName("styleName2"));
        catalog.save(l2);
        // get back and compare with itself
        LayerInfo l3 = catalog.getLayerByName(l.getName());
        LayerInfo l4 = catalog.getLayerByName(l.getName());
        Assert.assertEquals(l3, l4);
    }

    @Test
    public void testEnableLayer() {
        addLayer();
        LayerInfo l2 = catalog.getLayerByName(l.getName());
        Assert.assertTrue(l2.isEnabled());
        Assert.assertTrue(l2.enabled());
        Assert.assertTrue(l2.getResource().isEnabled());
        l2.setEnabled(false);
        catalog.save(l2);
        // GR: if not saving also the associated resource, we're assuming saving the layer also
        // saves its ResourceInfo, which is wrong, but works on the in-memory catalog by accident
        catalog.save(l2.getResource());
        l2 = catalog.getLayerByName(l2.getName());
        Assert.assertFalse(l2.isEnabled());
        Assert.assertFalse(l2.enabled());
        Assert.assertFalse(l2.getResource().isEnabled());
    }

    @Test
    public void testLayerEvents() {
        addFeatureType();
        addStyle();
        CatalogImplTest.TestListener tl = new CatalogImplTest.TestListener();
        catalog.addListener(tl);
        Assert.assertTrue(tl.added.isEmpty());
        catalog.add(l);
        Assert.assertEquals(1, tl.added.size());
        Assert.assertEquals(l, tl.added.get(0).getSource());
        LayerInfo l2 = catalog.getLayerByName(l.getName());
        l2.setPath("newPath");
        Assert.assertTrue(tl.modified.isEmpty());
        catalog.save(l2);
        Assert.assertEquals(1, tl.modified.size());
        Assert.assertEquals(l2, tl.modified.get(0).getSource());
        Assert.assertTrue(tl.modified.get(0).getPropertyNames().contains("path"));
        Assert.assertTrue(tl.modified.get(0).getOldValues().contains(null));
        Assert.assertTrue(tl.modified.get(0).getNewValues().contains("newPath"));
        Assert.assertEquals(1, tl.postModified.size());
        Assert.assertEquals(l2, tl.postModified.get(0).getSource());
        Assert.assertTrue(tl.postModified.get(0).getPropertyNames().contains("path"));
        Assert.assertTrue(tl.postModified.get(0).getOldValues().contains(null));
        Assert.assertTrue(tl.postModified.get(0).getNewValues().contains("newPath"));
        Assert.assertTrue(tl.removed.isEmpty());
        catalog.remove(l2);
        Assert.assertEquals(1, tl.removed.size());
        Assert.assertEquals(l2, tl.removed.get(0).getSource());
    }

    @Test
    public void testAddStyle() {
        Assert.assertTrue(catalog.getStyles().isEmpty());
        addStyle();
        Assert.assertEquals(1, catalog.getStyles().size());
        StyleInfo s2 = catalog.getFactory().createStyle();
        try {
            catalog.add(s2);
            Assert.fail("adding without name should throw exception");
        } catch (Exception e) {
        }
        s2.setName("s2Name");
        try {
            catalog.add(s2);
            Assert.fail("adding without fileName should throw exception");
        } catch (Exception e) {
        }
        s2.setFilename("s2Filename");
        try {
            catalog.getStyles().add(s2);
            Assert.fail("adding directly should throw exception");
        } catch (Exception e) {
        }
        catalog.add(s2);
        Assert.assertEquals(2, catalog.getStyles().size());
    }

    @Test
    public void testAddStyleWithNameConflict() throws Exception {
        addWorkspace();
        addStyle();
        StyleInfo s2 = catalog.getFactory().createStyle();
        s2.setName(s.getName());
        s2.setFilename(s.getFilename());
        try {
            catalog.add(s2);
            Assert.fail("Shoudl have failed with existing global style with same name");
        } catch (IllegalArgumentException expected) {
        }
        List<StyleInfo> currStyles = catalog.getStyles();
        // should pass after setting workspace
        s2.setWorkspace(ws);
        catalog.add(s2);
        Assert.assertFalse(new HashSet<StyleInfo>(currStyles).equals(new HashSet<StyleInfo>(catalog.getStyles())));
        StyleInfo s3 = catalog.getFactory().createStyle();
        s3.setName(s2.getName());
        s3.setFilename(s2.getFilename());
        try {
            catalog.add(s3);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        s3.setWorkspace(ws);
        try {
            catalog.add(s3);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetStyleById() {
        addStyle();
        StyleInfo s2 = catalog.getStyle(s.getId());
        Assert.assertNotNull(s2);
        Assert.assertNotSame(s, s2);
        Assert.assertEquals(s, s2);
    }

    @Test
    public void testGetStyleByName() {
        addStyle();
        StyleInfo s2 = catalog.getStyleByName(s.getName());
        Assert.assertNotNull(s2);
        Assert.assertNotSame(s, s2);
        Assert.assertEquals(s, s2);
    }

    @Test
    public void testGetStyleByNameWithWorkspace() {
        addWorkspace();
        addStyle();
        StyleInfo s2 = catalog.getFactory().createStyle();
        s2.setName("styleNameWithWorkspace");
        s2.setFilename("styleFilenameWithWorkspace");
        s2.setWorkspace(ws);
        catalog.add(s2);
        Assert.assertNotNull(catalog.getStyleByName("styleNameWithWorkspace"));
        Assert.assertNotNull(catalog.getStyleByName(ws.getName(), "styleNameWithWorkspace"));
        Assert.assertNotNull(catalog.getStyleByName(ws, "styleNameWithWorkspace"));
        Assert.assertNull(catalog.getStyleByName(((WorkspaceInfo) (null)), "styleNameWithWorkspace"));
        Assert.assertNull(catalog.getStyleByName(ws.getName(), "styleName"));
        Assert.assertNull(catalog.getStyleByName(ws, "styleName"));
        Assert.assertNotNull(catalog.getStyleByName(((WorkspaceInfo) (null)), "styleName"));
    }

    @Test
    public void testGetStyleByNameWithWorkspace2() throws Exception {
        addWorkspace();
        WorkspaceInfo ws2 = catalog.getFactory().createWorkspace();
        ws2.setName("wsName2");
        catalog.add(ws2);
        // add style with same name in each workspace
        StyleInfo s1 = catalog.getFactory().createStyle();
        s1.setName("foo");
        s1.setFilename("foo1.sld");
        s1.setWorkspace(ws);
        catalog.add(s1);
        StyleInfo s2 = catalog.getFactory().createStyle();
        s2.setName("foo");
        s2.setFilename("foo2.sld");
        s2.setWorkspace(ws2);
        catalog.add(s2);
        Assert.assertEquals(s1, catalog.getStyleByName("foo"));
        Assert.assertEquals(s1, catalog.getStyleByName(ws.getName(), "foo"));
        Assert.assertEquals(s1, catalog.getStyleByName(ws, "foo"));
        Assert.assertEquals(s2, catalog.getStyleByName(ws2.getName(), "foo"));
        Assert.assertEquals(s2, catalog.getStyleByName(ws2, "foo"));
    }

    @Test
    public void testGetStyles() {
        addWorkspace();
        addStyle();
        Assert.assertEquals(1, catalog.getStyles().size());
        Assert.assertEquals(0, catalog.getStylesByWorkspace(ws.getName()).size());
        Assert.assertEquals(0, catalog.getStylesByWorkspace(ws).size());
        Assert.assertEquals(0, catalog.getStylesByWorkspace(((WorkspaceInfo) (null))).size());
        StyleInfo s2 = catalog.getFactory().createStyle();
        s2.setName("styleNameWithWorkspace");
        s2.setFilename("styleFilenameWithWorkspace");
        s2.setWorkspace(ws);
        catalog.add(s2);
        Assert.assertEquals(2, catalog.getStyles().size());
        Assert.assertEquals(1, catalog.getStylesByWorkspace(ws.getName()).size());
        Assert.assertEquals(1, catalog.getStylesByWorkspace(ws).size());
        Assert.assertEquals(1, catalog.getStylesByWorkspace(((WorkspaceInfo) (null))).size());
    }

    @Test
    public void testModifyStyle() {
        addStyle();
        StyleInfo s2 = catalog.getStyleByName(s.getName());
        s2.setName(null);
        s2.setFilename(null);
        StyleInfo s3 = catalog.getStyleByName(s.getName());
        Assert.assertEquals(s, s3);
        try {
            catalog.save(s2);
            Assert.fail("setting name to null should fail");
        } catch (Exception e) {
        }
        s2.setName("s2Name");
        try {
            catalog.save(s2);
            Assert.fail("setting filename to null should fail");
        } catch (Exception e) {
        }
        s2.setFilename("s2Filename");
        catalog.save(s2);
        s3 = catalog.getStyleByName("styleName");
        Assert.assertNull(s3);
        s3 = catalog.getStyleByName(s2.getName());
        Assert.assertEquals(s2, s3);
    }

    @Test
    public void testModifyDefaultStyle() {
        addWorkspace();
        addDefaultStyle();
        StyleInfo s = catalog.getStyleByName(DEFAULT_LINE);
        s.setName("foo");
        try {
            catalog.save(s);
            Assert.fail("changing name of default style should fail");
        } catch (Exception e) {
        }
        s = catalog.getStyleByName(DEFAULT_LINE);
        s.setWorkspace(ws);
        try {
            catalog.save(s);
            Assert.fail("changing workspace of default style should fail");
        } catch (Exception e) {
        }
    }

    @Test
    public void testRemoveStyle() {
        addStyle();
        Assert.assertEquals(1, catalog.getStyles().size());
        catalog.remove(s);
        Assert.assertTrue(catalog.getStyles().isEmpty());
    }

    @Test
    public void testRemoveDefaultStyle() {
        addWorkspace();
        addDefaultStyle();
        StyleInfo s = catalog.getStyleByName(DEFAULT_LINE);
        try {
            catalog.remove(s);
            Assert.fail("removing default style should fail");
        } catch (Exception e) {
        }
    }

    @Test
    public void testStyleEvents() {
        CatalogImplTest.TestListener l = new CatalogImplTest.TestListener();
        catalog.addListener(l);
        Assert.assertTrue(l.added.isEmpty());
        catalog.add(s);
        Assert.assertEquals(1, l.added.size());
        Assert.assertEquals(s, l.added.get(0).getSource());
        StyleInfo s2 = catalog.getStyleByName(s.getName());
        s2.setFilename("changed");
        Assert.assertTrue(l.modified.isEmpty());
        Assert.assertTrue(l.postModified.isEmpty());
        catalog.save(s2);
        Assert.assertEquals(1, l.modified.size());
        Assert.assertEquals(s2, l.modified.get(0).getSource());
        Assert.assertTrue(l.modified.get(0).getPropertyNames().contains("filename"));
        Assert.assertTrue(l.modified.get(0).getOldValues().contains("styleFilename"));
        Assert.assertTrue(l.modified.get(0).getNewValues().contains("changed"));
        Assert.assertEquals(1, l.postModified.size());
        Assert.assertEquals(s2, l.postModified.get(0).getSource());
        Assert.assertTrue(l.postModified.get(0).getPropertyNames().contains("filename"));
        Assert.assertTrue(l.postModified.get(0).getOldValues().contains("styleFilename"));
        Assert.assertTrue(l.postModified.get(0).getNewValues().contains("changed"));
        Assert.assertTrue(l.removed.isEmpty());
        catalog.remove(s2);
        Assert.assertEquals(1, l.removed.size());
        Assert.assertEquals(s2, l.removed.get(0).getSource());
    }

    @Test
    public void testProxyBehaviour() throws Exception {
        testAddLayer();
        // l = catalog.getLayerByName( "layerName");
        LayerInfo l = catalog.getLayerByName(ft.getName());
        Assert.assertTrue((l instanceof Proxy));
        ResourceInfo r = l.getResource();
        Assert.assertTrue((r instanceof Proxy));
        String oldName = ft.getName();
        r.setName("changed");
        catalog.save(r);
        Assert.assertNull(catalog.getLayerByName(oldName));
        l = catalog.getLayerByName(r.getName());
        Assert.assertNotNull(l);
        Assert.assertEquals("changed", l.getResource().getName());
    }

    @Test
    public void testProxyListBehaviour() throws Exception {
        catalog.add(s);
        StyleInfo s2 = catalog.getFactory().createStyle();
        s2.setName(("a" + (s.getName())));
        s2.setFilename("a.sld");
        catalog.add(s2);
        List<StyleInfo> styles = catalog.getStyles();
        Assert.assertEquals(2, styles.size());
        // test immutability
        Comparator<StyleInfo> comparator = new Comparator<StyleInfo>() {
            public int compare(StyleInfo o1, StyleInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        try {
            Collections.sort(styles, comparator);
            Assert.fail("Expected runtime exception, immutable collection");
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
        styles = new ArrayList<StyleInfo>(styles);
        Collections.sort(styles, comparator);
        Assert.assertEquals(("a" + (s.getName())), styles.get(0).getName());
        Assert.assertEquals(s.getName(), styles.get(1).getName());
    }

    @Test
    public void testExceptionThrowingListener() throws Exception {
        CatalogImplTest.ExceptionThrowingListener l = new CatalogImplTest.ExceptionThrowingListener();
        catalog.addListener(l);
        l.throwCatalogException = false;
        WorkspaceInfo ws = catalog.getFactory().createWorkspace();
        ws.setName("foo");
        // no exception thrown back
        catalog.add(ws);
        l.throwCatalogException = true;
        ws = catalog.getFactory().createWorkspace();
        ws.setName("bar");
        try {
            catalog.add(ws);
            Assert.fail();
        } catch (CatalogException ce) {
            // good
        }
    }

    @Test
    public void testAddWMSStore() {
        Assert.assertTrue(catalog.getStores(WMSStoreInfo.class).isEmpty());
        addWMSStore();
        Assert.assertEquals(1, catalog.getStores(WMSStoreInfo.class).size());
        WMSStoreInfo retrieved = catalog.getStore(wms.getId(), WMSStoreInfo.class);
        WMSStoreInfo wms2 = catalog.getFactory().createWebMapServer();
        wms2.setName("wms2Name");
        wms2.setWorkspace(ws);
        catalog.add(wms2);
        Assert.assertEquals(2, catalog.getStores(WMSStoreInfo.class).size());
    }

    @Test
    public void testAddWMTSStore() {
        Assert.assertTrue(catalog.getStores(WMTSStoreInfo.class).isEmpty());
        addWMTSStore();
        Assert.assertEquals(1, catalog.getStores(WMTSStoreInfo.class).size());
        WMTSStoreInfo retrieved = catalog.getStore(wmtss.getId(), WMTSStoreInfo.class);
        Assert.assertNotNull(retrieved);
        WMTSStoreInfo wmts2 = catalog.getFactory().createWebMapTileServer();
        wmts2.setName("wmts2Name");
        wmts2.setWorkspace(ws);
        catalog.add(wmts2);
        Assert.assertEquals(2, catalog.getStores(WMTSStoreInfo.class).size());
    }

    protected int GET_LAYER_BY_ID_WITH_CONCURRENT_ADD_TEST_COUNT = 500;

    private static final int GET_LAYER_BY_ID_WITH_CONCURRENT_ADD_THREAD_COUNT = 10;

    @Test
    public void testAddLayerGroupNameConflict() throws Exception {
        addLayerGroup();
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setName("layerGroup");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        try {
            catalog.add(lg2);
            Assert.fail("should have failed because same name and no workspace set");
        } catch (IllegalArgumentException expected) {
        }
        // setting a workspace shluld pass
        lg2.setWorkspace(ws);
        catalog.add(lg2);
    }

    @Test
    public void testAddLayerGroupWithWorkspaceWithResourceFromAnotherWorkspace() {
        WorkspaceInfo ws = catalog.getFactory().createWorkspace();
        ws.setName("other");
        catalog.add(ws);
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setWorkspace(ws);
        lg2.setName("layerGroup2");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        try {
            catalog.add(lg2);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetLayerGroupByName() {
        addLayerGroup();
        Assert.assertNotNull(catalog.getLayerGroupByName("layerGroup"));
        Assert.assertNotNull(catalog.getLayerGroupByName(((WorkspaceInfo) (null)), "layerGroup"));
        Assert.assertNull(catalog.getLayerGroupByName(catalog.getDefaultWorkspace(), "layerGroup"));
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setWorkspace(ws);
        Assert.assertEquals(ws, catalog.getDefaultWorkspace());
        lg2.setName("layerGroup2");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        catalog.add(lg2);
        // When in the default workspace, we should be able to find it without the prefix
        Assert.assertNotNull(catalog.getLayerGroupByName("layerGroup2"));
        Assert.assertNotNull(catalog.getLayerGroupByName(((ws.getName()) + ":layerGroup2")));
        Assert.assertNotNull(catalog.getLayerGroupByName(catalog.getDefaultWorkspace(), "layerGroup2"));
        Assert.assertNull(catalog.getLayerGroupByName("cite", "layerGroup2"));
        // Repeat in a non-default workspace
        WorkspaceInfo ws2 = catalog.getFactory().createWorkspace();
        ws2.setName("ws2");
        catalog.add(ws2);
        catalog.setDefaultWorkspace(ws2);
        Assert.assertNull("layerGroup2 is not global, should not be found", catalog.getLayerGroupByName("layerGroup2"));
        Assert.assertNotNull(catalog.getLayerGroupByName(((ws.getName()) + ":layerGroup2")));
        Assert.assertNotNull(catalog.getLayerGroupByName(ws, "layerGroup2"));
        Assert.assertNull(catalog.getLayerGroupByName("cite", "layerGroup2"));
    }

    @Test
    public void testGetLayerGroupByNameWithColon() {
        addLayer();
        CatalogFactory factory = catalog.getFactory();
        LayerGroupInfo lg = factory.createLayerGroup();
        String lgName = "MyFakeWorkspace:layerGroup";
        lg.setName(lgName);
        lg.setWorkspace(ws);
        lg.getLayers().add(l);
        lg.getStyles().add(s);
        catalog.add(lg);
        // lg is not global, should not be found at least we specify a prefixed name
        Assert.assertNull("MyFakeWorkspace:layerGroup is not global, should not be found", catalog.getLayerGroupByName(lgName));
        Assert.assertEquals(lg, catalog.getLayerGroupByName(ws.getName(), lgName));
        Assert.assertEquals(lg, catalog.getLayerGroupByName(ws, lgName));
        Assert.assertEquals(lg, catalog.getLayerGroupByName((((ws.getName()) + ":") + lgName)));
    }

    @Test
    public void testGetLayerGroupByNameWithWorkspace() {
        addLayer();
        Assert.assertEquals(ws, catalog.getDefaultWorkspace());
        CatalogFactory factory = catalog.getFactory();
        LayerGroupInfo lg1 = factory.createLayerGroup();
        lg1.setName("lg");
        lg1.setWorkspace(ws);
        lg1.getLayers().add(l);
        lg1.getStyles().add(s);
        catalog.add(lg1);
        WorkspaceInfo ws2 = factory.createWorkspace();
        ws2.setName("ws2");
        catalog.add(ws2);
        NamespaceInfo ns2 = factory.createNamespace();
        // namespace prefix shall match workspace name, until we decide it cannot
        ns2.setPrefix("ns2");
        // ns2.setPrefix(ws2.getName());
        ns2.setURI("http://ns2");
        catalog.add(ns2);
        DataStoreInfo ds2 = factory.createDataStore();
        ds2.setEnabled(true);
        ds2.setName("dsName");
        ds2.setDescription("dsDescription");
        ds2.setWorkspace(ws2);
        catalog.add(ds2);
        FeatureTypeInfo ft2 = factory.createFeatureType();
        ft2.setEnabled(true);
        ft2.setName("ftName");
        ft2.setAbstract("ftAbstract");
        ft2.setDescription("ftDescription");
        ft2.setStore(ds2);
        ft2.setNamespace(ns2);
        catalog.add(ft2);
        StyleInfo s2 = factory.createStyle();
        s2.setName("styleName");
        s2.setFilename("styleFilename");
        s2.setWorkspace(ws2);
        catalog.add(s2);
        LayerInfo l2 = factory.createLayer();
        l2.setResource(ft2);
        l2.setEnabled(true);
        l2.setDefaultStyle(s2);
        catalog.add(l2);
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setName("lg");
        lg2.setWorkspace(ws2);
        lg2.getLayers().add(l2);
        lg2.getStyles().add(s2);
        catalog.add(lg2);
        // lg is not global, but it is in the default workspace, so it should be found if we don't
        // specify the workspace
        Assert.assertEquals(lg1, catalog.getLayerGroupByName("lg"));
        Assert.assertEquals(lg1, catalog.getLayerGroupByName(ws.getName(), "lg"));
        Assert.assertEquals(lg1, catalog.getLayerGroupByName(ws, "lg"));
        Assert.assertEquals(lg1, catalog.getLayerGroupByName(((ws.getName()) + ":lg")));
        Assert.assertEquals(lg2, catalog.getLayerGroupByName(ws2, "lg"));
        Assert.assertEquals(lg2, catalog.getLayerGroupByName(ws2, "lg"));
        Assert.assertEquals(lg2, catalog.getLayerGroupByName(((ws2.getName()) + ":lg")));
    }

    @Test
    public void testGetLayerGroups() {
        addLayerGroup();
        Assert.assertEquals(1, catalog.getLayerGroups().size());
        Assert.assertEquals(0, catalog.getLayerGroupsByWorkspace(ws.getName()).size());
        Assert.assertEquals(0, catalog.getLayerGroupsByWorkspace(ws).size());
        Assert.assertEquals(0, catalog.getLayerGroupsByWorkspace(((WorkspaceInfo) (null))).size());
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setWorkspace(catalog.getDefaultWorkspace());
        lg2.setName("layerGroup2");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        catalog.add(lg2);
        Assert.assertEquals(2, catalog.getLayerGroups().size());
        Assert.assertEquals(1, catalog.getLayerGroupsByWorkspace(ws.getName()).size());
        Assert.assertEquals(1, catalog.getLayerGroupsByWorkspace(ws).size());
        Assert.assertEquals(1, catalog.getLayerGroupsByWorkspace(((WorkspaceInfo) (null))).size());
    }

    @Test
    public void testLayerGroupTitle() {
        addLayer();
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        // lg2.setWorkspace(catalog.getDefaultWorkspace());
        lg2.setName("layerGroup2");
        lg2.setTitle("layerGroup2 title");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        catalog.add(lg2);
        Assert.assertEquals(1, catalog.getLayerGroups().size());
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals("layerGroup2 title", lg2.getTitle());
        lg2.setTitle("another title");
        catalog.save(lg2);
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals("another title", lg2.getTitle());
    }

    @Test
    public void testLayerGroupAbstract() {
        addLayer();
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        // lg2.setWorkspace(catalog.getDefaultWorkspace());
        lg2.setName("layerGroup2");
        lg2.setAbstract("layerGroup2 abstract");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        catalog.add(lg2);
        Assert.assertEquals(1, catalog.getLayerGroups().size());
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals("layerGroup2 abstract", lg2.getAbstract());
        lg2.setAbstract("another abstract");
        catalog.save(lg2);
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals("another abstract", lg2.getAbstract());
    }

    @Test
    public void testLayerGroupType() {
        addLayer();
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setWorkspace(null);
        lg2.setName("layerGroup2");
        lg2.setMode(NAMED);
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        catalog.add(lg2);
        Assert.assertEquals(1, catalog.getLayerGroups().size());
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals(NAMED, lg2.getMode());
        lg2.setMode(SINGLE);
        catalog.save(lg2);
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals(SINGLE, lg2.getMode());
    }

    @Test
    public void testLayerGroupRootLayer() {
        addLayer();
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setWorkspace(null);
        lg2.setName("layerGroup2");
        lg2.getLayers().add(l);
        lg2.getStyles().add(s);
        lg2.setRootLayer(l);
        lg2.setMode(SINGLE);
        try {
            catalog.add(lg2);
            Assert.fail("only EO layer groups can have a root layer");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        lg2.setMode(NAMED);
        try {
            catalog.add(lg2);
            Assert.fail("only EO layer groups can have a root layer");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        lg2.setMode(CONTAINER);
        try {
            catalog.add(lg2);
            Assert.fail("only EO layer groups can have a root layer");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        lg2.setMode(EO);
        lg2.setRootLayer(null);
        try {
            catalog.add(lg2);
            Assert.fail("EO layer groups must have a root layer");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        lg2.setRootLayer(l);
        try {
            catalog.add(lg2);
            Assert.fail("EO layer groups must have a root layer style");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        lg2.setRootLayerStyle(s);
        catalog.add(lg2);
        Assert.assertEquals(1, catalog.getLayerGroups().size());
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals(EO, lg2.getMode());
        Assert.assertEquals(l, lg2.getRootLayer());
        Assert.assertEquals(s, lg2.getRootLayerStyle());
    }

    @Test
    public void testLayerGroupNullLayerReferences() {
        addLayer();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.setWorkspace(null);
        lg.setName("layerGroup2");
        lg.getLayers().add(null);
        lg.getStyles().add(null);
        lg.getLayers().add(l);
        lg.getStyles().add(s);
        lg.getLayers().add(null);
        lg.getStyles().add(null);
        catalog.add(lg);
        LayerGroupInfo resolved = catalog.getLayerGroupByName("layerGroup2");
        Assert.assertEquals(1, resolved.layers().size());
        Assert.assertEquals(1, resolved.styles().size());
        Assert.assertEquals(s, resolved.styles().get(0));
    }

    @Test
    public void testLayerGroupRenderingLayers() {
        addDataStore();
        addNamespace();
        FeatureTypeInfo ft1;
        FeatureTypeInfo ft2;
        FeatureTypeInfo ft3;
        catalog.add((ft1 = newFeatureType("ft1", ds)));
        catalog.add((ft2 = newFeatureType("ft2", ds)));
        catalog.add((ft3 = newFeatureType("ft3", ds)));
        StyleInfo s1;
        StyleInfo s2;
        StyleInfo s3;
        catalog.add((s1 = newStyle("s1", "s1Filename")));
        catalog.add((s2 = newStyle("s2", "s2Filename")));
        catalog.add((s3 = newStyle("s3", "s3Filename")));
        LayerInfo l1;
        LayerInfo l2;
        LayerInfo l3;
        catalog.add((l1 = newLayer(ft1, s1)));
        catalog.add((l2 = newLayer(ft2, s2)));
        catalog.add((l3 = newLayer(ft3, s3)));
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setWorkspace(catalog.getDefaultWorkspace());
        lg2.setName("layerGroup2");
        lg2.getLayers().add(l1);
        lg2.getLayers().add(l2);
        lg2.getLayers().add(l3);
        lg2.getStyles().add(s1);
        lg2.getStyles().add(s2);
        lg2.getStyles().add(s3);
        lg2.setRootLayer(l);
        lg2.setRootLayerStyle(s);
        lg2.setMode(SINGLE);
        Assert.assertEquals(lg2.getLayers(), lg2.layers());
        Assert.assertEquals(lg2.getStyles(), lg2.styles());
        lg2.setMode(OPAQUE_CONTAINER);
        Assert.assertEquals(lg2.getLayers(), lg2.layers());
        Assert.assertEquals(lg2.getStyles(), lg2.styles());
        lg2.setMode(NAMED);
        Assert.assertEquals(lg2.getLayers(), lg2.layers());
        Assert.assertEquals(lg2.getStyles(), lg2.styles());
        lg2.setMode(CONTAINER);
        try {
            Assert.assertEquals(lg2.getLayers(), lg2.layers());
            Assert.fail("Layer group of Type Container can not be rendered");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(true);
        }
        try {
            Assert.assertEquals(lg2.getStyles(), lg2.styles());
            Assert.fail("Layer group of Type Container can not be rendered");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(true);
        }
        lg2.setMode(EO);
        Assert.assertEquals(1, lg2.layers().size());
        Assert.assertEquals(1, lg2.styles().size());
        Assert.assertEquals(l, lg2.layers().iterator().next());
        Assert.assertEquals(s, lg2.styles().iterator().next());
    }

    @Test
    public void testRemoveLayerGroupInLayerGroup() throws Exception {
        addLayerGroup();
        LayerGroupInfo lg2 = catalog.getFactory().createLayerGroup();
        lg2.setName("layerGroup2");
        lg2.getLayers().add(lg);
        lg2.getStyles().add(s);
        catalog.add(lg2);
        try {
            catalog.remove(lg);
            Assert.fail("should have failed because lg is in another lg");
        } catch (IllegalArgumentException expected) {
        }
        // removing the containing layer first should work
        catalog.remove(lg2);
        catalog.remove(lg);
    }

    static class TestListener implements CatalogListener {
        public List<CatalogAddEvent> added = new CopyOnWriteArrayList<>();

        public List<CatalogModifyEvent> modified = new CopyOnWriteArrayList<>();

        public List<CatalogPostModifyEvent> postModified = new CopyOnWriteArrayList<>();

        public List<CatalogRemoveEvent> removed = new CopyOnWriteArrayList<>();

        public void handleAddEvent(CatalogAddEvent event) {
            added.add(event);
        }

        public void handleModifyEvent(CatalogModifyEvent event) {
            modified.add(event);
        }

        public void handlePostModifyEvent(CatalogPostModifyEvent event) {
            postModified.add(event);
        }

        public void handleRemoveEvent(CatalogRemoveEvent event) {
            removed.add(event);
        }

        public void reloaded() {
        }
    }

    static class ExceptionThrowingListener implements CatalogListener {
        public boolean throwCatalogException;

        public void handleAddEvent(CatalogAddEvent event) throws CatalogException {
            if (throwCatalogException) {
                throw new CatalogException();
            } else {
                throw new RuntimeException();
            }
        }

        public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {
        }

        public void handlePostModifyEvent(CatalogPostModifyEvent event) throws CatalogException {
        }

        public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
        }

        public void reloaded() {
        }
    }

    class LayerAddRunner extends RunnerBase {
        private int idx;

        protected LayerAddRunner(CountDownLatch ready, CountDownLatch done, int idx) {
            super(ready, done);
            this.idx = idx;
        }

        protected void runInternal() throws Exception {
            CatalogFactory factory = catalog.getFactory();
            for (int i = 0; i < (GET_LAYER_BY_ID_WITH_CONCURRENT_ADD_TEST_COUNT); i++) {
                // GR: Adding a new feature type info too, we can't really add multiple layers per
                // feature type yet. Setting the name of the layer changes the name of the resource,
                // then all previous layers for that resource get screwed
                String name = (("LAYER-" + i) + "-") + (idx);
                FeatureTypeInfo resource = factory.createFeatureType();
                resource.setName(name);
                resource.setNamespace(ns);
                resource.setStore(ds);
                catalog.add(resource);
                LayerInfo layer = factory.createLayer();
                layer.setResource(resource);
                layer.setName(name);
                catalog.add(layer);
            }
        }
    }

    @Test
    public void testGet() {
        addDataStore();
        addNamespace();
        FeatureTypeInfo ft1 = newFeatureType("ft1", ds);
        ft1.getKeywords().add(new Keyword("kw1_ft1"));
        ft1.getKeywords().add(new Keyword("kw2_ft1"));
        ft1.getKeywords().add(new Keyword("repeatedKw"));
        FeatureTypeInfo ft2 = newFeatureType("ft2", ds);
        ft2.getKeywords().add(new Keyword("kw1_ft2"));
        ft2.getKeywords().add(new Keyword("kw2_ft2"));
        ft2.getKeywords().add(new Keyword("repeatedKw"));
        catalog.add(ft1);
        catalog.add(ft2);
        StyleInfo s1;
        StyleInfo s2;
        StyleInfo s3;
        catalog.add((s1 = newStyle("s1", "s1Filename")));
        catalog.add((s2 = newStyle("s2", "s2Filename")));
        catalog.add((s3 = newStyle("s3", "s3Filename")));
        LayerInfo l1 = newLayer(ft1, s1, s2, s3);
        LayerInfo l2 = newLayer(ft2, s2, s1, s3);
        catalog.add(l1);
        catalog.add(l2);
        Filter filter = Predicates.acceptAll();
        try {
            catalog.get(null, filter);
            Assert.fail("Expected precondition validation exception");
        } catch (RuntimeException nullCheck) {
            Assert.assertTrue(true);
        }
        try {
            catalog.get(FeatureTypeInfo.class, null);
            Assert.fail("Expected precondition validation exception");
        } catch (RuntimeException nullCheck) {
            Assert.assertTrue(true);
        }
        try {
            catalog.get(FeatureTypeInfo.class, filter);
            Assert.fail("Expected IAE on multiple results");
        } catch (IllegalArgumentException multipleResults) {
            Assert.assertTrue(true);
        }
        filter = Predicates.equal("id", ft1.getId());
        FeatureTypeInfo featureTypeInfo = catalog.get(FeatureTypeInfo.class, filter);
        Assert.assertEquals(ft1.getId(), featureTypeInfo.getId());
        filter = Predicates.equal("name", ft2.getName());
        Assert.assertEquals(ft2.getName(), catalog.get(ResourceInfo.class, filter).getName());
        filter = Predicates.equal("keywords[1].value", ft1.getKeywords().get(0).getValue());
        Assert.assertEquals(ft1.getName(), catalog.get(ResourceInfo.class, filter).getName());
        filter = Predicates.equal("keywords[2]", ft2.getKeywords().get(1));
        Assert.assertEquals(ft2.getName(), catalog.get(FeatureTypeInfo.class, filter).getName());
        filter = Predicates.equal("keywords[3].value", "repeatedKw");
        try {
            catalog.get(FeatureTypeInfo.class, filter).getName();
            Assert.fail("Expected IAE on multiple results");
        } catch (IllegalArgumentException multipleResults) {
            Assert.assertTrue(true);
        }
        filter = Predicates.equal("defaultStyle.filename", "s1Filename");
        Assert.assertEquals(l1.getId(), catalog.get(LayerInfo.class, filter).getId());
        filter = Predicates.equal("defaultStyle.name", s2.getName());
        Assert.assertEquals(l2.getId(), catalog.get(LayerInfo.class, filter).getId());
        // Waiting for fix of MultiCompareFilterImpl.evaluate for Sets
        // filter = equal("styles", l2.getStyles(), MatchAction.ALL);
        // assertEquals(l2.getId(), catalog.get(LayerInfo.class, filter).getId());
        filter = Predicates.equal("styles.id", s2.getId(), ONE);
        Assert.assertEquals(l1.getId(), catalog.get(LayerInfo.class, filter).getId());
        filter = Predicates.equal("styles.id", s3.getId(), ANY);// s3 is shared by l1 and l2

        try {
            catalog.get(LayerInfo.class, filter);
            Assert.fail("Expected IAE on multiple results");
        } catch (IllegalArgumentException multipleResults) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testListPredicate() {
        addDataStore();
        addNamespace();
        FeatureTypeInfo ft1;
        FeatureTypeInfo ft2;
        FeatureTypeInfo ft3;
        catalog.add((ft1 = newFeatureType("ft1", ds)));
        catalog.add((ft2 = newFeatureType("ft2", ds)));
        catalog.add((ft3 = newFeatureType("ft3", ds)));
        ft1 = catalog.getFeatureType(ft1.getId());
        ft2 = catalog.getFeatureType(ft2.getId());
        ft3 = catalog.getFeatureType(ft3.getId());
        Filter filter = Predicates.acceptAll();
        Set<? extends CatalogInfo> expected;
        Set<? extends CatalogInfo> actual;
        expected = Sets.newHashSet(ft1, ft2, ft3);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(expected, actual);
        filter = Predicates.contains("name", "t");
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertTrue(expected.equals(actual));
        Assert.assertEquals(expected, actual);
        filter = Predicates.or(Predicates.contains("name", "t2"), Predicates.contains("name", "t1"));
        expected = Sets.newHashSet(ft1, ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        StyleInfo s1;
        StyleInfo s2;
        StyleInfo s3;
        StyleInfo s4;
        StyleInfo s5;
        StyleInfo s6;
        catalog.add((s1 = newStyle("s1", "s1Filename")));
        catalog.add((s2 = newStyle("s2", "s2Filename")));
        catalog.add((s3 = newStyle("s3", "s3Filename")));
        catalog.add((s4 = newStyle("s4", "s4Filename")));
        catalog.add((s5 = newStyle("s5", "s5Filename")));
        catalog.add((s6 = newStyle("s6", "s6Filename")));
        LayerInfo l1;
        LayerInfo l2;
        LayerInfo l3;
        catalog.add((l1 = newLayer(ft1, s1)));
        catalog.add((l2 = newLayer(ft2, s2, s3, s4)));
        catalog.add((l3 = newLayer(ft3, s3, s5, s6)));
        filter = Predicates.contains("styles.name", "s6");
        expected = Sets.newHashSet(l3);
        actual = Sets.newHashSet(catalog.list(LayerInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = Predicates.equal("defaultStyle.name", "s1");
        expected = Sets.newHashSet(l1);
        actual = Sets.newHashSet(catalog.list(LayerInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = Predicates.or(Predicates.contains("styles.name", "s6"), Predicates.equal("defaultStyle.name", "s1"));
        expected = Sets.newHashSet(l1, l3);
        actual = Sets.newHashSet(catalog.list(LayerInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = Predicates.acceptAll();
        ArrayList<LayerInfo> naturalOrder = Lists.newArrayList(catalog.list(LayerInfo.class, filter));
        Assert.assertEquals(3, naturalOrder.size());
        int offset = 0;
        int limit = 2;
        Assert.assertEquals(naturalOrder.subList(0, 2), Lists.newArrayList(catalog.list(LayerInfo.class, filter, offset, limit, null)));
        offset = 1;
        Assert.assertEquals(naturalOrder.subList(1, 3), Lists.newArrayList(catalog.list(LayerInfo.class, filter, offset, limit, null)));
        limit = 1;
        Assert.assertEquals(naturalOrder.subList(1, 2), Lists.newArrayList(catalog.list(LayerInfo.class, filter, offset, limit, null)));
    }

    /**
     * This tests more advanced filters: multi-valued filters, opposite equations, field equations
     */
    @Test
    public void testListPredicateExtended() {
        addDataStore();
        addNamespace();
        final FilterFactory factory = CommonFactoryFinder.getFilterFactory();
        FeatureTypeInfo ft1;
        FeatureTypeInfo ft2;
        FeatureTypeInfo ft3;
        catalog.add((ft1 = newFeatureType("ft1", ds)));
        catalog.add((ft2 = newFeatureType("ft2", ds)));
        catalog.add((ft3 = newFeatureType("ft3", ds)));
        ft1 = catalog.getFeatureType(ft1.getId());
        ft2 = catalog.getFeatureType(ft2.getId());
        ft3 = catalog.getFeatureType(ft3.getId());
        ft1.getKeywords().add(new Keyword("keyword1"));
        ft1.getKeywords().add(new Keyword("keyword2"));
        ft1.getKeywords().add(new Keyword("ft1"));
        ft1.setDescription("ft1 description");
        catalog.save(ft1);
        ft2.getKeywords().add(new Keyword("keyword1"));
        ft2.getKeywords().add(new Keyword("keyword1"));
        ft2.setDescription("ft2");
        catalog.save(ft2);
        ft3.getKeywords().add(new Keyword("ft3"));
        ft3.getKeywords().add(new Keyword("ft3"));
        ft3.setDescription("FT3");
        catalog.save(ft3);
        Filter filter = Predicates.acceptAll();
        Set<? extends CatalogInfo> expected;
        Set<? extends CatalogInfo> actual;
        // opposite equality
        filter = factory.equal(factory.literal(ft1.getId()), factory.property("id"), true);
        expected = Sets.newHashSet(ft1);
        actual = Sets.newHashSet(catalog.list(ResourceInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // match case
        filter = factory.equal(factory.literal("FT1"), factory.property("name"), false);
        expected = Sets.newHashSet(ft1);
        actual = Sets.newHashSet(catalog.list(ResourceInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // equality of fields
        filter = factory.equal(factory.property("name"), factory.property("description"), true);
        expected = Sets.newHashSet(ft2);
        actual = Sets.newHashSet(catalog.list(ResourceInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // match case
        filter = factory.equal(factory.property("name"), factory.property("description"), false);
        expected = Sets.newHashSet(ft2, ft3);
        actual = Sets.newHashSet(catalog.list(ResourceInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // match action
        filter = factory.equal(factory.literal(new Keyword("keyword1")), factory.property("keywords"), true, ANY);
        expected = Sets.newHashSet(ft1, ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = factory.equal(factory.literal(new Keyword("keyword1")), factory.property("keywords"), true, ALL);
        expected = Sets.newHashSet(ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = factory.equal(factory.literal(new Keyword("keyword1")), factory.property("keywords"), true, ONE);
        expected = Sets.newHashSet(ft1);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // match action - like
        filter = factory.like(factory.property("keywords"), "key*d1", "*", "?", "\\", true, ANY);
        expected = Sets.newHashSet(ft1, ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = factory.like(factory.property("keywords"), "key*d1", "*", "?", "\\", true, ALL);
        expected = Sets.newHashSet(ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        filter = factory.like(factory.property("keywords"), "key*d1", "*", "?", "\\", true, ONE);
        expected = Sets.newHashSet(ft1);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // multivalued literals
        List values = new ArrayList<String>();
        values.add("ft1");
        values.add("ft2");
        filter = factory.equal(factory.literal(values), factory.property("name"), true, ANY);
        expected = Sets.newHashSet(ft1, ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        values = new ArrayList<String>();
        values.add("ft1");
        values.add("ft1");
        filter = factory.equal(factory.literal(values), factory.property("name"), true, ALL);
        expected = Sets.newHashSet(ft1);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        values = new ArrayList<String>();
        values.add("ft1");
        values.add("ft2");
        filter = factory.equal(factory.literal(values), factory.property("name"), true, ALL);
        expected = Sets.newHashSet();
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        values = new ArrayList<String>();
        values.add("ft1");
        values.add("ft1");
        values.add("ft2");
        filter = factory.equal(factory.literal(values), factory.property("name"), true, ONE);
        expected = Sets.newHashSet(ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        // multivalued literals with multivalued fields
        values = new ArrayList<Keyword>();
        values.add(new Keyword("keyword1"));
        values.add(new Keyword("keyword2"));
        filter = factory.equal(factory.literal(values), factory.property("keywords"), true, ANY);
        expected = Sets.newHashSet(ft1, ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        values = new ArrayList<Keyword>();
        values.add(new Keyword("keyword1"));
        values.add(new Keyword("keyword1"));
        filter = factory.equal(factory.literal(values), factory.property("keywords"), true, ALL);
        expected = Sets.newHashSet(ft2);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
        values = new ArrayList<Keyword>();
        values.add(new Keyword("keyword1"));
        values.add(new Keyword("blah"));
        filter = factory.equal(factory.literal(values), factory.property("keywords"), true, ONE);
        expected = Sets.newHashSet(ft1);
        actual = Sets.newHashSet(catalog.list(FeatureTypeInfo.class, filter));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testOrderBy() {
        addDataStore();
        addNamespace();
        FeatureTypeInfo ft1 = newFeatureType("ft1", ds);
        FeatureTypeInfo ft2 = newFeatureType("ft2", ds);
        FeatureTypeInfo ft3 = newFeatureType("ft3", ds);
        ft2.getKeywords().add(new Keyword("keyword1"));
        ft2.getKeywords().add(new Keyword("keyword2"));
        catalog.add(ft1);
        catalog.add(ft2);
        catalog.add(ft3);
        StyleInfo s1;
        StyleInfo s2;
        StyleInfo s3;
        StyleInfo s4;
        StyleInfo s5;
        StyleInfo s6;
        catalog.add((s1 = newStyle("s1", "s1Filename")));
        catalog.add((s2 = newStyle("s2", "s2Filename")));
        catalog.add((s3 = newStyle("s3", "s3Filename")));
        catalog.add((s4 = newStyle("s4", "s4Filename")));
        catalog.add((s5 = newStyle("s5", "s5Filename")));
        catalog.add((s6 = newStyle("s6", "s6Filename")));
        LayerInfo l1 = newLayer(ft1, s1);
        LayerInfo l2 = newLayer(ft2, s1, s3, s4);
        LayerInfo l3 = newLayer(ft3, s2, s5, s6);
        catalog.add(l1);
        catalog.add(l2);
        catalog.add(l3);
        Assert.assertEquals(3, catalog.getLayers().size());
        Filter filter;
        SortBy sortOrder;
        List<LayerInfo> expected;
        filter = Predicates.acceptAll();
        sortOrder = Predicates.asc("resource.name");
        expected = Lists.newArrayList(l1, l2, l3);
        testOrderBy(LayerInfo.class, filter, null, null, sortOrder, expected);
        sortOrder = Predicates.desc("resource.name");
        expected = Lists.newArrayList(l3, l2, l1);
        testOrderBy(LayerInfo.class, filter, null, null, sortOrder, expected);
        sortOrder = Predicates.asc("defaultStyle.name");
        expected = Lists.newArrayList(l1, l2, l3);
        testOrderBy(LayerInfo.class, filter, null, null, sortOrder, expected);
        sortOrder = Predicates.desc("defaultStyle.name");
        expected = Lists.newArrayList(l3, l2, l1);
        testOrderBy(LayerInfo.class, filter, null, null, sortOrder, expected);
        expected = Lists.newArrayList(l2, l1);
        testOrderBy(LayerInfo.class, filter, 1, null, sortOrder, expected);
        expected = Lists.newArrayList(l2);
        testOrderBy(LayerInfo.class, filter, 1, 1, sortOrder, expected);
        sortOrder = Predicates.asc("defaultStyle.name");
        expected = Lists.newArrayList(l2, l3);
        testOrderBy(LayerInfo.class, filter, 1, 10, sortOrder, expected);
        filter = Predicates.equal("styles.name", s3.getName());
        expected = Lists.newArrayList(l2);
        testOrderBy(LayerInfo.class, filter, 0, 10, sortOrder, expected);
    }

    @Test
    public void testFullTextSearch() {
        // test layer title search
        ft.setTitle("Global .5 deg Air Temperature [C]");
        cv.setTitle("Global .5 deg Dewpoint Depression [C]");
        ft.setDescription("FeatureType description");
        ft.setAbstract("GeoServer OpenSource GIS");
        cv.setDescription("Coverage description");
        cv.setAbstract("GeoServer uses GeoTools");
        l.setResource(ft);
        addLayer();
        catalog.add(cs);
        catalog.add(cv);
        LayerInfo l2 = newLayer(cv, s);
        catalog.add(l2);
        Filter filter = Predicates.fullTextSearch("Description");
        Assert.assertEquals(Sets.newHashSet(ft, cv), asSet(catalog.list(ResourceInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(ft), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(cv), asSet(catalog.list(CoverageInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(l, l2), asSet(catalog.list(LayerInfo.class, filter)));
        filter = Predicates.fullTextSearch("opensource");
        Assert.assertEquals(Sets.newHashSet(l), asSet(catalog.list(LayerInfo.class, filter)));
        filter = Predicates.fullTextSearch("geotools");
        Assert.assertEquals(Sets.newHashSet(l2), asSet(catalog.list(LayerInfo.class, filter)));
        filter = Predicates.fullTextSearch("Global");
        Assert.assertEquals(Sets.newHashSet(l, l2), asSet(catalog.list(LayerInfo.class, filter)));
        filter = Predicates.fullTextSearch("Temperature");
        Assert.assertEquals(Sets.newHashSet(l), asSet(catalog.list(LayerInfo.class, filter)));
        filter = Predicates.fullTextSearch("Depression");
        Assert.assertEquals(Sets.newHashSet(l2), asSet(catalog.list(LayerInfo.class, filter)));
    }

    @Test
    public void testFullTextSearchLayerGroupTitle() {
        addLayer();
        // geos-6882
        lg.setTitle("LayerGroup title");
        catalog.add(lg);
        // test layer group title and abstract search
        Filter filter = Predicates.fullTextSearch("title");
        Assert.assertEquals(Sets.newHashSet(lg), asSet(catalog.list(LayerGroupInfo.class, filter)));
    }

    @Test
    public void testFullTextSearchLayerGroupName() {
        addLayer();
        // geos-6882
        catalog.add(lg);
        Filter filter = Predicates.fullTextSearch("Group");
        Assert.assertEquals(Sets.newHashSet(lg), asSet(catalog.list(LayerGroupInfo.class, filter)));
    }

    @Test
    public void testFullTextSearchLayerGroupAbstract() {
        addLayer();
        lg.setAbstract("GeoServer OpenSource GIS");
        catalog.add(lg);
        Filter filter = Predicates.fullTextSearch("geoserver");
        Assert.assertEquals(Sets.newHashSet(lg), asSet(catalog.list(LayerGroupInfo.class, filter)));
    }

    @Test
    public void testFullTextSearchKeywords() {
        ft.getKeywords().add(new Keyword("air_temp"));
        ft.getKeywords().add(new Keyword("temperatureAir"));
        cv.getKeywords().add(new Keyword("dwpt_dprs"));
        cv.getKeywords().add(new Keyword("temperatureDewpointDepression"));
        l.setResource(ft);
        addLayer();
        catalog.add(cs);
        catalog.add(cv);
        LayerInfo l2 = newLayer(cv, s);
        catalog.add(l2);
        Filter filter = Predicates.fullTextSearch("temperature");
        Assert.assertEquals(Sets.newHashSet(l, l2), asSet(catalog.list(LayerInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(ft, cv), asSet(catalog.list(ResourceInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(ft), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(cv), asSet(catalog.list(CoverageInfo.class, filter)));
        filter = Predicates.fullTextSearch("air");
        Assert.assertEquals(Sets.newHashSet(l), asSet(catalog.list(LayerInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(ft), asSet(catalog.list(ResourceInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(ft), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(), asSet(catalog.list(CoverageInfo.class, filter)));
        filter = Predicates.fullTextSearch("dewpoint");
        Assert.assertEquals(Sets.newHashSet(l2), asSet(catalog.list(LayerInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(cv), asSet(catalog.list(ResourceInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(cv), asSet(catalog.list(CoverageInfo.class, filter)));
        filter = Predicates.fullTextSearch("pressure");
        Assert.assertEquals(Sets.newHashSet(), asSet(catalog.list(LayerInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(), asSet(catalog.list(ResourceInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(), asSet(catalog.list(CoverageInfo.class, filter)));
    }

    @Test
    public void testFullTextSearchAddedKeyword() {
        ft.getKeywords().add(new Keyword("air_temp"));
        ft.getKeywords().add(new Keyword("temperatureAir"));
        l.setResource(ft);
        addLayer();
        LayerInfo lproxy = catalog.getLayer(l.getId());
        FeatureTypeInfo ftproxy = ((FeatureTypeInfo) (lproxy.getResource()));
        ftproxy.getKeywords().add(new Keyword("newKeyword"));
        catalog.save(ftproxy);
        Filter filter = Predicates.fullTextSearch("newKeyword");
        Assert.assertEquals(Sets.newHashSet(ftproxy), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        Assert.assertEquals(Sets.newHashSet(lproxy), asSet(catalog.list(LayerInfo.class, filter)));
    }

    @Test
    public void testConcurrentCatalogModification() throws Exception {
        Logger logger = Logging.getLogger(CatalogImpl.class);
        final int tasks = 8;
        ExecutorService executor = Executors.newFixedThreadPool((tasks / 2));
        Level previousLevel = logger.getLevel();
        // clear previous listeners
        new ArrayList(catalog.getListeners()).forEach(( l) -> catalog.removeListener(l));
        try {
            // disable logging for this test, it will stay a while in case of failure otherwise
            logger.setLevel(Level.OFF);
            ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
            for (int i = 0; i < tasks; i++) {
                completionService.submit(() -> {
                    // attach listeners
                    List<CatalogImplTest.TestListener> listeners = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        CatalogImplTest.TestListener tl = new CatalogImplTest.TestListener();
                        listeners.add(tl);
                        catalog.addListener(tl);
                    }
                    // simulate catalog removals, check the events get to destination
                    CatalogInfo catalogInfo = new CoverageInfoImpl();
                    catalog.fireRemoved(catalogInfo);
                    // make sure each listener actually got the message
                    for (CatalogImplTest.TestListener testListener : listeners) {
                        Assert.assertTrue("Did not find the expected even in the listener", testListener.removed.stream().anyMatch(( event) -> (event.getSource()) == catalogInfo));
                    }
                    // clear the listeners
                    listeners.forEach(( l) -> catalog.removeListener(l));
                }, null);
            }
            for (int i = 0; i < tasks; ++i) {
                completionService.take().get();
            }
        } finally {
            executor.shutdown();
            logger.setLevel(previousLevel);
        }
    }

    @Test
    public void testChangeLayerGroupOrder() {
        addLayerGroup();
        // create second layer
        FeatureTypeInfo ft2 = catalog.getFactory().createFeatureType();
        ft2.setName("ft2Name");
        ft2.setStore(ds);
        ft2.setNamespace(ns);
        catalog.add(ft2);
        LayerInfo l2 = catalog.getFactory().createLayer();
        l2.setResource(ft2);
        l2.setDefaultStyle(s);
        catalog.add(l2);
        // add to the group
        LayerGroupInfo group = catalog.getLayerGroupByName(lg.getName());
        group.getLayers().add(l2);
        group.getStyles().add(null);
        catalog.save(group);
        // change the layer group order
        group = catalog.getLayerGroupByName(lg.getName());
        PublishedInfo pi = group.getLayers().remove(1);
        group.getLayers().add(0, pi);
        catalog.save(group);
        // create a new style
        StyleInfo s2 = catalog.getFactory().createStyle();
        s2.setName("s2Name");
        s2.setFilename("s2Filename");
        catalog.add(s2);
        // change the default style of l
        LayerInfo ll = catalog.getLayerByName(l.prefixedName());
        ll.setDefaultStyle(catalog.getStyleByName(s2.getName()));
        catalog.save(ll);
        // now check that the facade can be compared to itself
        LayerGroupInfo g1 = catalog.getFacade().getLayerGroupByName(lg.getName());
        LayerGroupInfo g2 = catalog.getFacade().getLayerGroupByName(lg.getName());
        Assert.assertTrue(LayerGroupInfo.equals(g1, g2));
    }
}

