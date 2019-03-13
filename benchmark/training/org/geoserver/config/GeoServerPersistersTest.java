/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WMTSLayerInfo;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.test.GeoServerBaseTestSupport;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;


@Category(SystemTest.class)
public class GeoServerPersistersTest extends GeoServerSystemTestSupport {
    Catalog catalog;

    @Test
    public void testAddWorkspace() throws Exception {
        File ws = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme");
        Assert.assertFalse(ws.exists());
        WorkspaceInfo acme = catalog.getFactory().createWorkspace();
        acme.setName("acme");
        catalog.add(acme);
        Assert.assertTrue(ws.exists());
    }

    @Test
    public void testRemoveWorkspace() throws Exception {
        testAddWorkspace();
        File ws = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme");
        Assert.assertTrue(ws.exists());
        WorkspaceInfo acme = catalog.getWorkspaceByName("acme");
        catalog.remove(acme);
        Assert.assertFalse(ws.exists());
    }

    @Test
    public void testDefaultWorkspace() throws Exception {
        testAddWorkspace();
        WorkspaceInfo ws = catalog.getWorkspaceByName("acme");
        catalog.setDefaultWorkspace(ws);
        File dws = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/default.xml");
        Assert.assertTrue(dws.exists());
        Document dom = dom(dws);
        assertXpathEvaluatesTo("acme", "/workspace/name", dom);
    }

    @Test
    public void testAddDataStore() throws Exception {
        testAddWorkspace();
        File dir = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore");
        Assert.assertFalse(dir.exists());
        DataStoreInfo ds = catalog.getFactory().createDataStore();
        ds.setName("foostore");
        ds.setWorkspace(catalog.getWorkspaceByName("acme"));
        catalog.add(ds);
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(new File(dir, "datastore.xml").exists());
    }

    @Test
    public void testModifyDataStore() throws Exception {
        testAddDataStore();
        DataStoreInfo ds = catalog.getDataStoreByName("acme", "foostore");
        Assert.assertTrue(ds.getConnectionParameters().isEmpty());
        ds.getConnectionParameters().put("foo", "bar");
        catalog.save(ds);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/datastore.xml");
        Document dom = dom(f);
        assertXpathExists("/dataStore/connectionParameters/entry[@key='foo']", dom);
    }

    @Test
    public void testChangeDataStoreWorkspace() throws Exception {
        testAddDataStore();
        File f1 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/datastore.xml");
        Assert.assertTrue(f1.exists());
        WorkspaceInfo nws = catalog.getFactory().createWorkspace();
        nws.setName("topp");
        catalog.add(nws);
        DataStoreInfo ds = catalog.getDataStoreByName("acme", "foostore");
        ds.setWorkspace(nws);
        catalog.save(ds);
        Assert.assertFalse(f1.exists());
        File f2 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/topp/foostore/datastore.xml");
        Assert.assertTrue(f2.exists());
    }

    @Test
    public void testRemoveDataStore() throws Exception {
        testAddDataStore();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore");
        Assert.assertTrue(f.exists());
        DataStoreInfo ds = catalog.getDataStoreByName("acme", "foostore");
        catalog.remove(ds);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testAddFeatureType() throws Exception {
        testAddDataStore();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo");
        Assert.assertFalse(d.exists());
        NamespaceInfo ns = catalog.getFactory().createNamespace();
        ns.setPrefix("bar");
        ns.setURI("http://bar");
        catalog.add(ns);
        FeatureTypeInfo ft = catalog.getFactory().createFeatureType();
        ft.setName("foo");
        ft.setNamespace(ns);
        ft.setStore(catalog.getDataStoreByName("acme", "foostore"));
        catalog.add(ft);
        Assert.assertTrue(d.exists());
    }

    @Test
    public void testChangeFeatureTypeStore() throws Exception {
        testAddFeatureType();
        File f1 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo/featuretype.xml");
        Assert.assertTrue(f1.exists());
        DataStoreInfo ds = catalog.getFactory().createDataStore();
        ds.setName("barstore");
        ds.setWorkspace(catalog.getWorkspaceByName("acme"));
        catalog.add(ds);
        FeatureTypeInfo ft = catalog.getFeatureTypeByName("foo");
        ft.setStore(ds);
        catalog.save(ft);
        Assert.assertFalse(f1.exists());
        File f2 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/barstore/foo/featuretype.xml");
        Assert.assertTrue(f2.exists());
    }

    @Test
    public void testModifyFeatureType() throws Exception {
        testAddFeatureType();
        FeatureTypeInfo ft = catalog.getFeatureTypeByName("bar", "foo");
        ft.setTitle("fooTitle");
        catalog.save(ft);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo/featuretype.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("fooTitle", "/featureType/title", dom);
    }

    @Test
    public void testRemoveFeatureType() throws Exception {
        testAddFeatureType();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo");
        Assert.assertTrue(d.exists());
        FeatureTypeInfo ft = catalog.getFeatureTypeByName("bar", "foo");
        catalog.remove(ft);
        Assert.assertFalse(d.exists());
    }

    @Test
    public void testAddCoverageStore() throws Exception {
        testAddWorkspace();
        File dir = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore");
        Assert.assertFalse(dir.exists());
        CoverageStoreInfo cs = catalog.getFactory().createCoverageStore();
        cs.setName("foostore");
        cs.setWorkspace(catalog.getWorkspaceByName("acme"));
        catalog.add(cs);
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(new File(dir, "coveragestore.xml").exists());
    }

    @Test
    public void testModifyCoverageStore() throws Exception {
        testAddCoverageStore();
        CoverageStoreInfo cs = catalog.getCoverageStoreByName("acme", "foostore");
        Assert.assertNull(cs.getURL());
        cs.setURL("file:data/foo.tiff");
        catalog.save(cs);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/coveragestore.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("file:data/foo.tiff", "/coverageStore/url/text()", dom);
    }

    @Test
    public void testRemoveCoverageStore() throws Exception {
        testAddCoverageStore();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore");
        Assert.assertTrue(f.exists());
        CoverageStoreInfo cs = catalog.getCoverageStoreByName("acme", "foostore");
        catalog.remove(cs);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testAddCoverage() throws Exception {
        testAddCoverageStore();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo");
        Assert.assertFalse(d.exists());
        NamespaceInfo ns = catalog.getFactory().createNamespace();
        ns.setPrefix("bar");
        ns.setURI("http://bar");
        catalog.add(ns);
        CoverageInfo ft = catalog.getFactory().createCoverage();
        ft.setName("foo");
        ft.setNamespace(ns);
        ft.setStore(catalog.getCoverageStoreByName("acme", "foostore"));
        catalog.add(ft);
        Assert.assertTrue(d.exists());
    }

    @Test
    public void testModifyCoverage() throws Exception {
        testAddCoverage();
        CoverageInfo ft = catalog.getCoverageByName("bar", "foo");
        ft.setTitle("fooTitle");
        catalog.save(ft);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo/coverage.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("fooTitle", "/coverage/title", dom);
    }

    @Test
    public void testRemoveCoverage() throws Exception {
        testAddCoverage();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo");
        Assert.assertTrue(d.exists());
        CoverageInfo ft = catalog.getCoverageByName("bar", "foo");
        catalog.remove(ft);
        Assert.assertFalse(d.exists());
    }

    @Test
    public void testAddWMSStore() throws Exception {
        testAddWorkspace();
        File dir = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowms");
        Assert.assertFalse(dir.exists());
        WMSStoreInfo wms = catalog.getFactory().createWebMapServer();
        wms.setName("demowms");
        wms.setWorkspace(catalog.getWorkspaceByName("acme"));
        catalog.add(wms);
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(new File(dir, "wmsstore.xml").exists());
    }

    @Test
    public void testModifyWMSStore() throws Exception {
        testAddWMSStore();
        WMSStoreInfo wms = catalog.getStoreByName("acme", "demowms", WMSStoreInfo.class);
        Assert.assertNull(wms.getCapabilitiesURL());
        String capsURL = "http://demo.opengeo.org:8080/geoserver/wms?request=GetCapabilites&service=WMS";
        wms.setCapabilitiesURL(capsURL);
        catalog.save(wms);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowms/wmsstore.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo(capsURL, "/wmsStore/capabilitiesURL/text()", dom);
    }

    @Test
    public void testRemoveWMSStore() throws Exception {
        testAddWMSStore();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowms");
        Assert.assertTrue(f.exists());
        WMSStoreInfo wms = catalog.getStoreByName("acme", "demowms", WMSStoreInfo.class);
        catalog.remove(wms);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testAddWMSLayer() throws Exception {
        testAddWMSStore();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowms/foo");
        Assert.assertFalse(d.exists());
        NamespaceInfo ns = catalog.getFactory().createNamespace();
        ns.setPrefix("bar");
        ns.setURI("http://bar");
        catalog.add(ns);
        WMSLayerInfo wms = catalog.getFactory().createWMSLayer();
        wms.setName("foo");
        wms.setNamespace(ns);
        wms.setStore(catalog.getStoreByName("acme", "demowms", WMSStoreInfo.class));
        catalog.add(wms);
        Assert.assertTrue(d.exists());
        Assert.assertTrue(new File(d, "wmslayer.xml").exists());
    }

    @Test
    public void testModifyWMSLayer() throws Exception {
        testAddWMSLayer();
        WMSLayerInfo wli = catalog.getResourceByName("bar", "foo", WMSLayerInfo.class);
        wli.setTitle("fooTitle");
        catalog.save(wli);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowms/foo/wmslayer.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("fooTitle", "/wmsLayer/title", dom);
    }

    @Test
    public void testRemoveWMSLayer() throws Exception {
        testAddWMSLayer();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowms/foo");
        Assert.assertTrue(d.exists());
        WMSLayerInfo wli = catalog.getResourceByName("bar", "foo", WMSLayerInfo.class);
        catalog.remove(wli);
        Assert.assertFalse(d.exists());
    }

    @Test
    public void testAddWMTSStore() throws Exception {
        testAddWorkspace();
        File dir = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowmts");
        Assert.assertFalse(dir.exists());
        WMTSStoreInfo wmts = catalog.getFactory().createWebMapTileServer();
        wmts.setName("demowmts");
        wmts.setWorkspace(catalog.getWorkspaceByName("acme"));
        catalog.add(wmts);
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(new File(dir, "wmtsstore.xml").exists());
    }

    @Test
    public void testModifyWMTSStore() throws Exception {
        testAddWMTSStore();
        WMTSStoreInfo wmts = catalog.getStoreByName("acme", "demowmts", WMTSStoreInfo.class);
        Assert.assertNull(wmts.getCapabilitiesURL());
        String capsURL = "http://demo.opengeo.org:8080/geoserver/gwc?request=GetCapabilites&service=WMTS";
        wmts.setCapabilitiesURL(capsURL);
        catalog.save(wmts);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowmts/wmtsstore.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo(capsURL, "/wmtsStore/capabilitiesURL/text()", dom);
    }

    @Test
    public void testRemoveWMTSStore() throws Exception {
        testAddWMTSStore();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowmts");
        Assert.assertTrue(f.exists());
        WMTSStoreInfo wmts = catalog.getStoreByName("acme", "demowmts", WMTSStoreInfo.class);
        catalog.remove(wmts);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testAddWMTSLayer() throws Exception {
        testAddWMTSStore();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowmts/foo_wmts");
        Assert.assertFalse(d.exists());
        NamespaceInfo ns = catalog.getFactory().createNamespace();
        ns.setPrefix("bar");
        ns.setURI("http://bar");
        catalog.add(ns);
        WMTSLayerInfo wmts = catalog.getFactory().createWMTSLayer();
        wmts.setName("foo_wmts");
        wmts.setNamespace(ns);
        wmts.setStore(catalog.getStoreByName("acme", "demowmts", WMTSStoreInfo.class));
        catalog.add(wmts);
        Assert.assertTrue(d.exists());
        Assert.assertTrue(new File(d, "wmtslayer.xml").exists());
    }

    @Test
    public void testModifyWMTSLayer() throws Exception {
        testAddWMTSLayer();
        WMTSLayerInfo wli = catalog.getResourceByName("bar", "foo_wmts", WMTSLayerInfo.class);
        wli.setTitle("fooTitle");
        catalog.save(wli);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowmts/foo_wmts/wmtslayer.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("fooTitle", "/wmtsLayer/title", dom);
    }

    @Test
    public void testRemoveWMTSLayer() throws Exception {
        testAddWMTSLayer();
        File d = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/demowmts/foo_wmts");
        Assert.assertTrue(d.exists());
        WMTSLayerInfo wli = catalog.getResourceByName("bar", "foo_wmts", WMTSLayerInfo.class);
        catalog.remove(wli);
        Assert.assertFalse(d.exists());
    }

    @Test
    public void testAddLayer() throws Exception {
        testAddFeatureType();
        testAddStyle();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo/layer.xml");
        Assert.assertFalse(f.exists());
        LayerInfo l = catalog.getFactory().createLayer();
        // l.setName("foo");
        l.setResource(catalog.getFeatureTypeByName("bar", "foo"));
        StyleInfo s = catalog.getStyleByName("foostyle");
        l.setDefaultStyle(s);
        catalog.add(l);
        Assert.assertTrue(f.exists());
    }

    @Test
    public void testModifyLayer() throws Exception {
        testAddLayer();
        LayerInfo l = catalog.getLayerByName("foo");
        l.setPath("/foo/bar");
        catalog.save(l);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo/layer.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("/foo/bar", "/layer/path", dom);
    }

    @Test
    public void testRemoveLayer() throws Exception {
        testAddLayer();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/foostore/foo/layer.xml");
        Assert.assertTrue(f.exists());
        LayerInfo l = catalog.getLayerByName("foo");
        catalog.remove(l);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testAddStyle() throws Exception {
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml");
        Assert.assertFalse(f.exists());
        StyleInfo s = catalog.getFactory().createStyle();
        s.setName("foostyle");
        s.setFilename("foostyle.sld");
        catalog.add(s);
        Assert.assertTrue(f.exists());
    }

    @Test
    public void testAddStyleWithWorkspace() throws Exception {
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml");
        Assert.assertFalse(f.exists());
        StyleInfo s = catalog.getFactory().createStyle();
        s.setName("foostyle");
        s.setFilename("foostyle.sld");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.add(s);
        Assert.assertTrue(f.exists());
        Document dom = dom(f);
        assertXpathEvaluatesTo(catalog.getDefaultWorkspace().getId(), "/style/workspace/id", dom);
    }

    @Test
    public void testModifyStyle() throws Exception {
        testAddStyle();
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setFilename("foostyle2.sld");
        catalog.save(s);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("foostyle2.sld", "/style/filename", dom);
    }

    @Test
    public void testRenameStyle() throws Exception {
        testAddStyle();
        File sldFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        sldFile.createNewFile();
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setName("boostyle");
        catalog.save(s);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/boostyle.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("boostyle.sld", "/style/filename", dom);
        File renamedSldFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/boostyle.sld");
        Assert.assertThat(sldFile, Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(renamedSldFile, FileExistsMatcher.fileExists());
    }

    @Test
    public void testRenameStyleConflict() throws Exception {
        testAddStyle();
        File sldFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        sldFile.createNewFile();
        File conflictingFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/boostyle.sld");
        conflictingFile.createNewFile();
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setName("boostyle");
        catalog.save(s);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/boostyle.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("boostyle1.sld", "/style/filename", dom);
        File renamedSldFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/boostyle1.sld");
        Assert.assertThat(sldFile, Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(renamedSldFile, FileExistsMatcher.fileExists());
    }

    @Test
    public void testRenameStyleWithExistingIncrementedVersion() throws Exception {
        testAddStyle();
        File sldFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        sldFile.createNewFile();
        File sldFile1 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle1.sld");
        sldFile1.createNewFile();
        File xmlFile1 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle1.xml");
        xmlFile1.createNewFile();
        File sldFile2 = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle2.sld");
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setName("foostyle");
        catalog.save(s);
        Assert.assertThat(sldFile, Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(sldFile1, FileExistsMatcher.fileExists());
        Assert.assertThat(xmlFile1, FileExistsMatcher.fileExists());
        Assert.assertThat(sldFile2, FileExistsMatcher.fileExists());
        sldFile1.delete();
        xmlFile1.delete();
    }

    @Test
    public void testModifyStyleChangeWorkspace() throws Exception {
        testAddStyle();
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setFilename("foostyle2.sld");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml");
        Assert.assertFalse(f.exists());
        f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("foostyle2.sld", "/style/filename", dom);
    }

    @Test
    public void testModifyStyleChangeWorkspace2() throws Exception {
        testAddStyle();
        // copy an sld into place
        FileUtils.copyURLToFile(getClass().getResource("default_line.sld"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"));
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml").exists());
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld").exists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertFalse(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml").exists());
        Assert.assertFalse(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld").exists());
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml").exists());
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld").exists());
    }

    @Test
    public void testModifyStyleChangeWorkspaceToGlobal() throws Exception {
        testAddStyleWithWorkspace();
        // copy an sld into place
        FileUtils.copyURLToFile(getClass().getResource("default_line.sld"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld"));
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml").exists());
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld").exists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(null);
        catalog.save(s);
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml").exists());
        Assert.assertTrue(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld").exists());
        Assert.assertFalse(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml").exists());
        Assert.assertFalse(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld").exists());
    }

    @Test
    public void testModifyStyleWithResourceChangeWorkspace() throws Exception {
        testAddStyle();
        // copy an sld with its resource into place
        FileUtils.copyURLToFile(getClass().getResource("burg.sld"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"));
        FileUtils.copyURLToFile(getClass().getResource("burg02.svg"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg02.svg"), FileExistsMatcher.fileExists());
    }

    @Test
    public void testModifyStyleWithResourcesInParentDirChangeWorkspace() throws Exception {
        testAddStyle();
        // If a relative URI with parent references is used, give up on trying to copy the resource.
        // The style will break but copying arbitrary files from parent directories around is a bad
        // idea.  Handle the rest normally. KS
        FileUtils.copyURLToFile(getClass().getResource("burgParentReference.sld"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"));
        FileUtils.copyURLToFile(getClass().getResource("burg02.svg"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"));
        FileUtils.copyURLToFile(getClass().getResource("burg02.svg"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "burg03.svg"));
        new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg03.svg").delete();
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "burg03.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "burg03.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg02.svg"), FileExistsMatcher.fileExists());
    }

    @Test
    public void testModifyStyleWithResourcesAbsoluteChangeWorkspace() throws Exception {
        testAddStyle();
        // If an absolute uri is used, don't copy it anywhere.  The reference is absolute
        // so it will still work.
        File styleFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        FileUtils.copyURLToFile(getClass().getResource("burgParentReference.sld"), styleFile);
        FileUtils.copyURLToFile(getClass().getResource("burg02.svg"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"));
        File target = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "burg03.svg");
        FileUtils.copyURLToFile(getClass().getResource("burg02.svg"), target);
        // Insert an absolute path to test
        String content = new String(Files.readAllBytes(styleFile.toPath()), StandardCharsets.UTF_8);
        content = content.replaceAll("./burg03.svg", "http://doesnotexist.example.org/burg03.svg");
        Files.write(styleFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg03.svg").delete();
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(target, FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(target, FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), ("workspaces/gs" + (target.getPath()))), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), ("workspaces/gs/styles" + (target.getPath()))), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg02.svg"), FileExistsMatcher.fileExists());
    }

    @Test
    public void testModifyStyleWithResourcesRemoteChangeWorkspace() throws Exception {
        testAddStyle();
        // If an absolute uri is used, don't copy it anywhere.  The reference is absolute
        // so it will still work.
        File styleFile = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        FileUtils.copyURLToFile(getClass().getResource("burgRemoteReference.sld"), styleFile);
        FileUtils.copyURLToFile(getClass().getResource("burg02.svg"), new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"));
        new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg03.svg").delete();
        new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "burg03.svg").delete();
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        StyleInfo s = catalog.getStyleByName("foostyle");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.save(s);
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/burg02.svg"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.sld"), FileExistsMatcher.fileExists());
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/example.com/burg03.svg"), Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/burg02.svg"), FileExistsMatcher.fileExists());
    }

    @Test
    public void testRemoveStyle() throws Exception {
        testAddStyle();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.xml");
        Assert.assertTrue(f.exists());
        File sf = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        sf.createNewFile();
        Assert.assertTrue(sf.exists());
        StyleInfo s = catalog.getStyleByName("foostyle");
        catalog.remove(s);
        Assert.assertThat(f, Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(sf, Matchers.not(FileExistsMatcher.fileExists()));
        File sfb = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld.bak");
        Assert.assertThat(sfb, FileExistsMatcher.fileExists());
        // do it a second time
        testAddStyle();
        sf = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld");
        sf.createNewFile();
        Assert.assertTrue(sf.exists());
        s = catalog.getStyleByName("foostyle");
        catalog.remove(s);
        Assert.assertThat(f, Matchers.not(FileExistsMatcher.fileExists()));
        Assert.assertThat(sf, Matchers.not(FileExistsMatcher.fileExists()));
        sfb = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "styles/foostyle.sld.bak.1");
        Assert.assertThat(sfb, FileExistsMatcher.fileExists());
    }

    @Test
    public void testRemoveStyleWithWorkspace() throws Exception {
        StyleInfo s = catalog.getFactory().createStyle();
        s.setName("foostyle");
        s.setFilename("foostyle.sld");
        s.setWorkspace(catalog.getDefaultWorkspace());
        catalog.add(s);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/gs/styles/foostyle.xml");
        Assert.assertTrue(f.exists());
        s = catalog.getStyleByName("foostyle");
        Assert.assertNotNull(s);
        s = catalog.getStyleByName(catalog.getDefaultWorkspace(), "foostyle");
        catalog.remove(s);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testAddLayerGroup() throws Exception {
        testAddLayer();
        // testAddStyle();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "layergroups/lg.xml");
        Assert.assertFalse(f.exists());
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.setName("lg");
        lg.getLayers().add(catalog.getLayerByName("foo"));
        lg.getStyles().add(catalog.getStyleByName("foostyle"));
        lg.getLayers().add(catalog.getLayerByName("foo"));
        /* default style */
        lg.getStyles().add(null);
        lg.getLayers().add(catalog.getLayerByName("foo"));
        lg.getStyles().add(catalog.getStyleByName("foostyle"));
        catalog.add(lg);
        Assert.assertTrue(f.exists());
    }

    @Test
    public void testAddLayerGroupWithWorkspace() throws Exception {
        File f = file(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/layergroups/foolayergroup.xml");
        Assert.assertFalse(f.exists());
        testAddLayer();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.setName("foolayergroup");
        lg.setWorkspace(catalog.getWorkspaceByName("acme"));
        lg.getLayers().add(catalog.getLayerByName("foo"));
        lg.getStyles().add(null);
        catalog.add(lg);
        Assert.assertTrue(f.exists());
        Document dom = dom(f);
        assertXpathEvaluatesTo(catalog.getWorkspaceByName("acme").getId(), "/layerGroup/workspace/id", dom);
    }

    @Test
    public void testModifyLayerGroup() throws Exception {
        testAddLayerGroup();
        LayerGroupInfo lg = catalog.getLayerGroupByName("lg");
        StyleInfo s = catalog.getFactory().createStyle();
        s.setName("foostyle2");
        s.setFilename("foostyle2.sld");
        catalog.add(s);
        lg.getStyles().set(0, s);
        catalog.save(lg);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "layergroups/lg.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo(s.getId(), "/layerGroup/styles/style/id", dom);
    }

    @Test
    public void testModifyLayerGroupChangeWorkspace() throws Exception {
        testAddLayerGroup();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "layergroups/lg.xml");
        Assert.assertTrue(f.exists());
        LayerGroupInfo lg = catalog.getLayerGroupByName("lg");
        WorkspaceInfo workspace = catalog.getWorkspaceByName("acme");
        Assert.assertNotNull(workspace);
        lg.setWorkspace(workspace);
        catalog.save(lg);
        Assert.assertFalse(f.exists());
        String path = GeoServerBaseTestSupport.testData.getDataDirectoryRoot().getAbsolutePath();
        Assert.assertTrue(("data directory " + path), GeoServerBaseTestSupport.testData.getDataDirectoryRoot().exists());
        File file = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/layergroups/lg.xml");
        Assert.assertTrue(file.getPath(), file.exists());
    }

    @Test
    public void testRemoveLayerGroup() throws Exception {
        testAddLayerGroup();
        File dataDirectoryRoot = GeoServerBaseTestSupport.testData.getDataDirectoryRoot();
        File f = new File(dataDirectoryRoot, "layergroups/lg.xml");
        Assert.assertTrue(f.exists());
        LayerGroupInfo lg = catalog.getLayerGroupByName("lg");
        catalog.remove(lg);
        Assert.assertFalse("removed lg", f.exists());
    }

    @Test
    public void testRemoveLayerGroupWithWorkspace() throws Exception {
        testModifyLayerGroupChangeWorkspace();
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "workspaces/acme/layergroups/lg.xml");
        Assert.assertTrue(f.exists());
        LayerGroupInfo lg = catalog.getLayerGroupByName("lg");
        Assert.assertNull(lg);
        lg = catalog.getLayerGroupByName("acme:lg");
        Assert.assertNotNull(lg);
        catalog.remove(lg);
        Assert.assertFalse(f.exists());
    }

    @Test
    public void testModifyGlobal() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setAdminUsername("roadRunner");
        global.setTitle("ACME");
        getGeoServer().save(global);
        File f = new File(GeoServerBaseTestSupport.testData.getDataDirectoryRoot(), "global.xml");
        Document dom = dom(f);
        assertXpathEvaluatesTo("roadRunner", "/global/adminUsername", dom);
        assertXpathEvaluatesTo("ACME", "/global/settings/title", dom);
    }

    @Test
    public void testAddSettings() throws Exception {
        testAddWorkspace();
        WorkspaceInfo ws = catalog.getWorkspaceByName("acme");
        SettingsInfo settings = getGeoServer().getFactory().createSettings();
        settings.setTitle("ACME");
        settings.setWorkspace(ws);
        File f = catalog.getResourceLoader().find("workspaces", ws.getName(), "settings.xml");
        Assert.assertNull(f);
        getGeoServer().add(settings);
        f = catalog.getResourceLoader().find("workspaces", ws.getName(), "settings.xml");
        Assert.assertNotNull(f);
        Document dom = dom(f);
        assertXpathEvaluatesTo("ACME", "/settings/title", dom);
    }

    @Test
    public void testModifySettings() throws Exception {
        testAddSettings();
        WorkspaceInfo ws = catalog.getWorkspaceByName("acme");
        SettingsInfo settings = getGeoServer().getSettings(ws);
        settings.setTitle("FOO");
        getGeoServer().save(settings);
        File f = catalog.getResourceLoader().find("workspaces", ws.getName(), "settings.xml");
        Assert.assertNotNull(f);
        Document dom = dom(f);
        assertXpathEvaluatesTo("FOO", "/settings/title", dom);
    }

    @Test
    public void testModifySettingsChangeWorkspace() throws Exception {
        testAddSettings();
        WorkspaceInfo ws1 = catalog.getWorkspaceByName("acme");
        WorkspaceInfo ws2 = catalog.getFactory().createWorkspace();
        ws2.setName("foo");
        catalog.add(ws2);
        SettingsInfo settings = getGeoServer().getSettings(ws1);
        settings.setWorkspace(ws2);
        getGeoServer().save(settings);
        File f = catalog.getResourceLoader().find("workspaces", ws1.getName(), "settings.xml");
        Assert.assertNull(f);
        f = catalog.getResourceLoader().find("workspaces", ws2.getName(), "settings.xml");
        Assert.assertNotNull(f);
        Document dom = dom(f);
        assertXpathEvaluatesTo(ws2.getId(), "/settings/workspace/id", dom);
    }

    @Test
    public void testRemoveSettings() throws Exception {
        testAddSettings();
        WorkspaceInfo ws = catalog.getWorkspaceByName("acme");
        File f = catalog.getResourceLoader().find("workspaces", ws.getName(), "settings.xml");
        Assert.assertNotNull(f);
        SettingsInfo settings = getGeoServer().getSettings(ws);
        getGeoServer().remove(settings);
        f = catalog.getResourceLoader().find("workspaces", ws.getName(), "settings.xml");
        Assert.assertNull(f);
    }
}

