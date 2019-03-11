/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config.util;


import DefaultGeographicCRS.WGS84;
import FeatureTypeInfo.JDBC_VIRTUAL_TABLE;
import LayerGroupInfo.Mode;
import LayerGroupInfo.Mode.SINGLE;
import WMSStoreInfoImpl.DEFAULT_CONNECT_TIMEOUT;
import WMSStoreInfoImpl.DEFAULT_MAX_CONNECTIONS;
import WMSStoreInfoImpl.DEFAULT_READ_TIMEOUT;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogFactory;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.CoverageView;
import org.geoserver.catalog.CoverageView.CompositionType;
import org.geoserver.catalog.CoverageView.CoverageBand;
import org.geoserver.catalog.CoverageView.InputCoverageBand;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.Keyword;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WMTSLayerInfo;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.CoverageDimensionImpl;
import org.geoserver.catalog.impl.MetadataLinkInfoImpl;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.GeoServerFactory;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.geoserver.config.util.XStreamPersister.CRSConverter;
import org.geoserver.config.util.XStreamPersister.SRSConverter;
import org.geotools.jdbc.RegexpValidator;
import org.geotools.jdbc.VirtualTable;
import org.geotools.jdbc.VirtualTableParameter;
import org.geotools.referencing.CRS;
import org.geotools.referencing.wkt.UnformattableObjectException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import si.uom.SI;


public class XStreamPersisterTest {
    GeoServerFactory factory;

    CatalogFactory cfactory;

    XStreamPersister persister;

    @Test
    public void testGlobal() throws Exception {
        GeoServerInfo g1 = factory.createGlobal();
        g1.setAdminPassword("foo");
        g1.setAdminUsername("bar");
        g1.setCharset("ISO-8859-1");
        ContactInfo contact = factory.createContact();
        g1.setContact(contact);
        contact.setAddress("123");
        contact.setAddressCity("Victoria");
        contact.setAddressCountry("Canada");
        contact.setAddressPostalCode("V1T3T8");
        contact.setAddressState("BC");
        contact.setAddressType("house");
        contact.setContactEmail("bob@acme.org");
        contact.setContactFacsimile("+1 250 123 4567");
        contact.setContactOrganization("Acme");
        contact.setContactPerson("Bob");
        contact.setContactPosition("hacker");
        contact.setContactVoice("+1 250 765 4321");
        g1.setNumDecimals(2);
        g1.setOnlineResource("http://acme.org");
        g1.setProxyBaseUrl("http://proxy.acme.org");
        g1.setSchemaBaseUrl("http://schemas.acme.org");
        g1.setTitle("Acme's GeoServer");
        g1.setUpdateSequence(123);
        g1.setVerbose(true);
        g1.setVerboseExceptions(true);
        g1.getMetadata().put("one", Integer.valueOf(1));
        g1.getMetadata().put("two", new Double(2.2));
        ByteArrayOutputStream out = out();
        persister.save(g1, out);
        GeoServerInfo g2 = persister.load(in(out), GeoServerInfo.class);
        Assert.assertEquals(g1, g2);
        Document dom = dom(in(out));
        Assert.assertEquals("global", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testLogging() throws Exception {
        LoggingInfo logging = factory.createLogging();
        logging.setLevel("CRAZY_LOGGING");
        logging.setLocation("some/place/geoserver.log");
        logging.setStdOutLogging(true);
        ByteArrayOutputStream out = out();
        persister.save(logging, out);
        LoggingInfo logging2 = persister.load(in(out), LoggingInfo.class);
        Assert.assertEquals(logging, logging2);
        Document dom = dom(in(out));
        Assert.assertEquals("logging", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testGobalContactDefault() throws Exception {
        GeoServerInfo g1 = factory.createGlobal();
        ContactInfo contact = factory.createContact();
        g1.setContact(contact);
        ByteArrayOutputStream out = out();
        persister.save(g1, out);
        ByteArrayInputStream in = in(out);
        Document dom = dom(in);
        Element e = ((Element) (dom.getElementsByTagName("contact").item(0)));
        e.removeAttribute("class");
        in = in(dom);
        GeoServerInfo g2 = persister.load(in, GeoServerInfo.class);
        Assert.assertEquals(g1, g2);
    }

    static class MyServiceInfo extends ServiceInfoImpl {
        String foo;

        String getFoo() {
            return foo;
        }

        void setFoo(String foo) {
            this.foo = foo;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof XStreamPersisterTest.MyServiceInfo)) {
                return false;
            }
            XStreamPersisterTest.MyServiceInfo other = ((XStreamPersisterTest.MyServiceInfo) (obj));
            if ((foo) == null) {
                if ((other.foo) != null) {
                    return false;
                }
            } else {
                if (!(foo.equals(other.foo))) {
                    return false;
                }
            }
            return super.equals(other);
        }
    }

    @Test
    public void testService() throws Exception {
        XStreamPersisterTest.MyServiceInfo s1 = new XStreamPersisterTest.MyServiceInfo();
        setAbstract("my service abstract");
        setAccessConstraints("no constraints");
        setCiteCompliant(true);
        setEnabled(true);
        setFees("no fees");
        s1.setFoo("bar");
        s1.setId("id");
        setMaintainer("Bob");
        s1.setMetadataLink(factory.createMetadataLink());
        setName("MS");
        setOnlineResource("http://acme.org?service=myservice");
        setOutputStrategy("FAST");
        setSchemaBaseURL("http://schemas.acme.org/");
        setTitle("My Service");
        setVerbose(true);
        ByteArrayOutputStream out = out();
        persister.save(s1, out);
        XStreamPersisterTest.MyServiceInfo s2 = persister.load(in(out), XStreamPersisterTest.MyServiceInfo.class);
        Assert.assertEquals(getAbstract(), getAbstract());
        Assert.assertEquals(getAccessConstraints(), getAccessConstraints());
        Assert.assertEquals(isCiteCompliant(), isCiteCompliant());
        Assert.assertEquals(isEnabled(), isEnabled());
        Assert.assertEquals(getFees(), getFees());
        Assert.assertEquals(s1.getFoo(), s2.getFoo());
        Assert.assertEquals(getId(), getId());
        Assert.assertEquals(getMaintainer(), getMaintainer());
        Assert.assertEquals(getMetadataLink(), getMetadataLink());
        Assert.assertEquals(getName(), getName());
        Assert.assertEquals(getOnlineResource(), getOnlineResource());
        Assert.assertEquals(getOutputStrategy(), getOutputStrategy());
        Assert.assertEquals(getSchemaBaseURL(), getSchemaBaseURL());
        Assert.assertEquals(getTitle(), getTitle());
        Assert.assertEquals(isVerbose(), isVerbose());
    }

    @Test
    public void testServiceOmitGlobal() throws Exception {
        XStreamPersisterTest.MyServiceInfo s1 = new XStreamPersisterTest.MyServiceInfo();
        s1.setGeoServer(new GeoServerImpl());
        ByteArrayOutputStream out = out();
        persister.save(s1, out);
        XStreamPersisterTest.MyServiceInfo s2 = persister.load(in(out), XStreamPersisterTest.MyServiceInfo.class);
        Assert.assertNull(getGeoServer());
    }

    @Test
    public void testServiceCustomAlias() throws Exception {
        XStreamPersister p = persister = new XStreamPersisterFactory().createXMLPersister();
        p.getXStream().alias("ms", XStreamPersisterTest.MyServiceInfo.class);
        XStreamPersisterTest.MyServiceInfo s1 = new XStreamPersisterTest.MyServiceInfo();
        ByteArrayOutputStream out = out();
        p.save(s1, out);
        Document dom = dom(in(out));
        Assert.assertEquals("ms", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testDataStore() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        DataStoreInfo ds1 = cFactory.createDataStore();
        ds1.setName("bar");
        ds1.setWorkspace(ws);
        ByteArrayOutputStream out = out();
        persister.save(ds1, out);
        DataStoreInfo ds2 = persister.load(in(out), DataStoreInfo.class);
        Assert.assertEquals("bar", ds2.getName());
        Assert.assertNotNull(ds2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("dataStore", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testDataStoreReferencedByName() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        DataStoreInfo ds1 = cFactory.createDataStore();
        ds1.setName("bar");
        ds1.setWorkspace(ws);
        catalog.detach(ds1);
        setId(null);
        ByteArrayOutputStream out = out();
        XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
        persister.setReferenceByName(true);
        persister.save(ds1, out);
        DataStoreInfo ds2 = persister.load(in(out), DataStoreInfo.class);
        Assert.assertEquals("bar", ds2.getName());
        Assert.assertNotNull(ds2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("dataStore", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testCoverageStore() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        CoverageStoreInfo cs1 = cFactory.createCoverageStore();
        cs1.setName("bar");
        cs1.setWorkspace(ws);
        ByteArrayOutputStream out = out();
        persister.save(cs1, out);
        CoverageStoreInfo ds2 = persister.load(in(out), CoverageStoreInfo.class);
        Assert.assertEquals("bar", ds2.getName());
        Assert.assertNotNull(ds2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("coverageStore", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testCoverageStoreReferencedByName() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        CoverageStoreInfo cs1 = cFactory.createCoverageStore();
        cs1.setName("bar");
        cs1.setWorkspace(ws);
        catalog.detach(cs1);
        setId(null);
        ByteArrayOutputStream out = out();
        XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
        persister.setReferenceByName(true);
        persister.save(cs1, out);
        CoverageStoreInfo ds2 = persister.load(in(out), CoverageStoreInfo.class);
        Assert.assertEquals("bar", ds2.getName());
        Assert.assertNotNull(ds2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("coverageStore", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testWMSStore() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        WMSStoreInfo wms1 = cFactory.createWebMapServer();
        wms1.setName("bar");
        wms1.setWorkspace(ws);
        wms1.setCapabilitiesURL("http://fake.host/wms?request=GetCapabilities&service=wms");
        ByteArrayOutputStream out = out();
        persister.save(wms1, out);
        WMSStoreInfo wms2 = persister.load(in(out), WMSStoreInfo.class);
        Assert.assertEquals("bar", wms2.getName());
        Assert.assertEquals(DEFAULT_MAX_CONNECTIONS, wms2.getMaxConnections());
        Assert.assertEquals(DEFAULT_CONNECT_TIMEOUT, wms2.getConnectTimeout());
        Assert.assertEquals(DEFAULT_READ_TIMEOUT, wms2.getReadTimeout());
        Assert.assertNotNull(wms2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("wmsStore", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testWMSStoreReferencedByName() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        WMSStoreInfo wms1 = cFactory.createWebMapServer();
        wms1.setName("bar");
        wms1.setWorkspace(ws);
        wms1.setCapabilitiesURL("http://fake.host/wms?request=GetCapabilities&service=wms");
        catalog.detach(wms1);
        setId(null);
        ByteArrayOutputStream out = out();
        XStreamPersister persister = new XStreamPersisterFactory().createXMLPersister();
        persister.setReferenceByName(true);
        persister.save(wms1, out);
        WMSStoreInfo wms2 = persister.load(in(out), WMSStoreInfo.class);
        Assert.assertEquals("bar", wms2.getName());
        Assert.assertEquals(DEFAULT_MAX_CONNECTIONS, wms2.getMaxConnections());
        Assert.assertEquals(DEFAULT_CONNECT_TIMEOUT, wms2.getConnectTimeout());
        Assert.assertEquals(DEFAULT_READ_TIMEOUT, wms2.getReadTimeout());
        Assert.assertNotNull(wms2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("wmsStore", dom.getDocumentElement().getNodeName());
    }

    /**
     * Check maxConnections, connectTimeout, and readTimeout, stored as metadata properties in a
     * 2.1.3+ configuration are read back as actual properties.
     */
    @Test
    public void testWMSStoreBackwardsCompatibility() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        WMSStoreInfo wms1 = cFactory.createWebMapServer();
        wms1.setName("bar");
        wms1.setWorkspace(ws);
        wms1.setCapabilitiesURL("http://fake.host/wms?request=GetCapabilities&service=wms");
        wms1.getMetadata().put("maxConnections", Integer.valueOf(18));
        wms1.getMetadata().put("connectTimeout", Integer.valueOf(25));
        wms1.getMetadata().put("readTimeout", Integer.valueOf(78));
        ByteArrayOutputStream out = out();
        persister.save(wms1, out);
        WMSStoreInfo wms2 = persister.load(in(out), WMSStoreInfo.class);
        Assert.assertEquals("bar", wms2.getName());
        Assert.assertEquals(18, wms2.getMaxConnections());
        Assert.assertEquals(25, wms2.getConnectTimeout());
        Assert.assertEquals(78, wms2.getReadTimeout());
        Assert.assertNull(wms2.getMetadata().get("maxConnections"));
        Assert.assertNull(wms2.getMetadata().get("connectTimeout"));
        Assert.assertNull(wms2.getMetadata().get("readTimeout"));
    }

    @Test
    public void testStyle() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        StyleInfo s1 = cFactory.createStyle();
        s1.setName("foo");
        s1.setFilename("foo.sld");
        ByteArrayOutputStream out = out();
        persister.save(s1, out);
        ByteArrayInputStream in = in(out);
        StyleInfo s2 = persister.load(in, StyleInfo.class);
        Assert.assertEquals(s1, s2);
        Document dom = dom(in(out));
        Assert.assertEquals("style", dom.getDocumentElement().getNodeName());
        catalog.add(s2);
        Assert.assertNull(s2.getWorkspace());
    }

    @Test
    public void testWorkspaceStyle() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        StyleInfo s1 = cFactory.createStyle();
        s1.setName("bar");
        s1.setFilename("bar.sld");
        s1.setWorkspace(ws);
        ByteArrayOutputStream out = out();
        persister.save(s1, out);
        ByteArrayInputStream in = in(out);
        StyleInfo s2 = persister.load(in, StyleInfo.class);
        Assert.assertEquals("bar", s2.getName());
        Assert.assertNotNull(s2.getWorkspace());
        Assert.assertEquals("foo", getId());
        Document dom = dom(in(out));
        Assert.assertEquals("style", dom.getDocumentElement().getNodeName());
        catalog.add(ws);
        catalog.add(s2);
        // Make sure the catalog resolves the workspace
        Assert.assertEquals("foo", getName());
    }

    @Test
    public void testFeatureType() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        DataStoreInfo ds = cFactory.createDataStore();
        ds.setWorkspace(ws);
        ds.setName("foo");
        catalog.add(ds);
        FeatureTypeInfo ft = cFactory.createFeatureType();
        ft.setStore(ds);
        ft.setNamespace(ns);
        ft.setName("ft");
        ft.setAbstract("abstract");
        ft.setSRS("EPSG:4326");
        ft.setNativeCRS(CRS.decode("EPSG:4326"));
        ft.setLinearizationTolerance(new org.geotools.measure.Measure(10, SI.METRE));
        ByteArrayOutputStream out = out();
        persister.save(ft, out);
        persister.setCatalog(catalog);
        ft = persister.load(in(out), FeatureTypeInfo.class);
        Assert.assertNotNull(ft);
        Assert.assertEquals("ft", ft.getName());
        Assert.assertEquals(ds, ft.getStore());
        Assert.assertEquals(ns, ft.getNamespace());
        Assert.assertEquals("EPSG:4326", ft.getSRS());
        Assert.assertEquals(new org.geotools.measure.Measure(10, SI.METRE), ft.getLinearizationTolerance());
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), ft.getNativeCRS()));
    }

    @Test
    public void testCoverage() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        CoverageStoreInfo cs = cFactory.createCoverageStore();
        cs.setWorkspace(ws);
        cs.setName("foo");
        catalog.add(cs);
        CoverageInfo cv = cFactory.createCoverage();
        cv.setStore(cs);
        cv.setNamespace(ns);
        cv.setName("cv");
        cv.setAbstract("abstract");
        cv.setSRS("EPSG:4326");
        cv.setNativeCRS(CRS.decode("EPSG:4326"));
        cv.getParameters().put("foo", null);
        ByteArrayOutputStream out = out();
        persister.save(cv, out);
        persister.setCatalog(catalog);
        cv = persister.load(in(out), CoverageInfo.class);
        Assert.assertNotNull(cv);
        Assert.assertEquals("cv", cv.getName());
        Assert.assertEquals(cs, cv.getStore());
        Assert.assertEquals(ns, cv.getNamespace());
        Assert.assertEquals("EPSG:4326", cv.getSRS());
        Assert.assertTrue(cv.getParameters().containsKey("foo"));
        Assert.assertNull(cv.getParameters().get("foo"));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), cv.getNativeCRS()));
    }

    @Test
    public void testWMTSLayer() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        WMTSStoreInfo wmts = cFactory.createWebMapTileServer();
        wmts.setWorkspace(ws);
        wmts.setName("foo");
        wmts.setCapabilitiesURL("http://fake.host/wmts?request=getCapabilities");
        catalog.add(wmts);
        WMTSLayerInfo wl = cFactory.createWMTSLayer();
        wl.setStore(wmts);
        wl.setNamespace(ns);
        wl.setName("wmtsLayer");
        wl.setAbstract("abstract");
        wl.setSRS("EPSG:4326");
        wl.setNativeCRS(CRS.decode("EPSG:4326"));
        ByteArrayOutputStream out = out();
        persister.save(wl, out);
        persister.setCatalog(catalog);
        wl = persister.load(in(out), WMTSLayerInfo.class);
        Assert.assertNotNull(wl);
        Assert.assertEquals("wmtsLayer", wl.getName());
        Assert.assertEquals(wmts, wl.getStore());
        Assert.assertEquals(ns, wl.getNamespace());
        Assert.assertEquals("EPSG:4326", wl.getSRS());
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), wl.getNativeCRS()));
        Document dom = dom(in(out));
        Assert.assertEquals("wmtsLayer", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testWMSLayer() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        WMSStoreInfo wms = cFactory.createWebMapServer();
        wms.setWorkspace(ws);
        wms.setName("foo");
        wms.setCapabilitiesURL("http://fake.host/wms?request=getCapabilities");
        catalog.add(wms);
        WMSLayerInfo wl = cFactory.createWMSLayer();
        wl.setStore(wms);
        wl.setNamespace(ns);
        wl.setName("wmsLayer");
        wl.setAbstract("abstract");
        wl.setSRS("EPSG:4326");
        wl.setNativeCRS(CRS.decode("EPSG:4326"));
        ByteArrayOutputStream out = out();
        persister.save(wl, out);
        // System.out.println( new String(out.toByteArray()) );
        persister.setCatalog(catalog);
        wl = persister.load(in(out), WMSLayerInfo.class);
        Assert.assertNotNull(wl);
        Assert.assertEquals("wmsLayer", wl.getName());
        Assert.assertEquals(wms, wl.getStore());
        Assert.assertEquals(ns, wl.getNamespace());
        Assert.assertEquals("EPSG:4326", wl.getSRS());
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), wl.getNativeCRS()));
        Document dom = dom(in(out));
        Assert.assertEquals("wmsLayer", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testLayer() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        DataStoreInfo ds = cFactory.createDataStore();
        ds.setWorkspace(ws);
        ds.setName("foo");
        catalog.add(ds);
        FeatureTypeInfo ft = cFactory.createFeatureType();
        ft.setStore(ds);
        ft.setNamespace(ns);
        ft.setName("ft");
        ft.setAbstract("abstract");
        ft.setSRS("EPSG:4326");
        ft.setNativeCRS(CRS.decode("EPSG:4326"));
        catalog.add(ft);
        StyleInfo s = cFactory.createStyle();
        s.setName("style");
        s.setFilename("style.sld");
        catalog.add(s);
        LayerInfo l = cFactory.createLayer();
        // TODO: reinstate when layer/publish slipt is actually in place
        // l.setName( "layer" );
        l.setResource(ft);
        l.setDefaultStyle(s);
        l.getStyles().add(s);
        catalog.add(l);
        ByteArrayOutputStream out = out();
        persister.save(l, out);
        persister.setCatalog(catalog);
        l = persister.load(in(out), LayerInfo.class);
        Assert.assertEquals(getName(), l.getName());
        Assert.assertEquals(ft, l.getResource());
        Assert.assertEquals(s, l.getDefaultStyle());
        Assert.assertNotNull(l.getStyles());
        Assert.assertEquals(1, l.getStyles().size());
        Assert.assertTrue(l.getStyles().contains(s));
    }

    @Test
    public void testLayerGroupInfo() throws Exception {
        for (LayerGroupInfo.Mode mode : Mode.values()) {
            testSerializationWithMode(mode);
        }
    }

    @Test
    public void testLegacyLayerGroupWithoutMode() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((((((((((((((("<layerGroup>\n" + "<name>foo</name>\n") + "<title>foo title</title>\n") + "<abstractTxt>foo abstract</abstractTxt>\n") + "<layers>\n") + "<layer>\n") + "<id>LayerInfoImpl--570ae188:124761b8d78:-7fb0</id>\n") + "</layer>\n") + "</layers>\n") + "<styles>\n") + "<style/>\n") + "</styles>\n") + "<bounds>\n") + "<minx>589425.9342365642</minx>\n") + "<maxx>609518.6719560538</maxx>\n") + "<miny>4913959.224611808</miny>\n") + "<maxy>4928082.949945881</maxy>\n") + "<crs class=\"projected\">EPSG:26713</crs>\n") + "</bounds>\n") + "</layerGroup>\n");
        LayerGroupInfo group = persister.load(new ByteArrayInputStream(xml.getBytes()), LayerGroupInfo.class);
        Assert.assertEquals(SINGLE, group.getMode());
        Catalog catalog = new CatalogImpl();
        Assert.assertTrue(catalog.validate(group, false).isValid());
    }

    @Test
    public void testVirtualTable() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        DataStoreInfo ds = cFactory.createDataStore();
        ds.setWorkspace(ws);
        ds.setName("foo");
        catalog.add(ds);
        VirtualTable vt = new VirtualTable("riverReduced", "select a, b, c * %mulparam% \n from table \n where x > 1 %andparam%");
        vt.addGeometryMetadatata("geom", LineString.class, 4326);
        vt.setPrimaryKeyColumns(Arrays.asList("a", "b"));
        vt.addParameter(new VirtualTableParameter("mulparam", "1", new RegexpValidator("\\d+")));
        vt.addParameter(new VirtualTableParameter("andparam", null));
        FeatureTypeInfo ft = cFactory.createFeatureType();
        ft.setStore(ds);
        ft.setNamespace(ns);
        ft.setName("ft");
        ft.setAbstract("abstract");
        ft.setSRS("EPSG:4326");
        ft.setNativeCRS(CRS.decode("EPSG:4326"));
        ft.getMetadata().put(JDBC_VIRTUAL_TABLE, vt);
        catalog.add(ft);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        persister.save(ft, out);
        // System.out.println(out.toString());
        persister.setCatalog(catalog);
        ft = persister.load(in(out), FeatureTypeInfo.class);
        VirtualTable vt2 = ((VirtualTable) (ft.getMetadata().get(JDBC_VIRTUAL_TABLE)));
        Assert.assertNotNull(vt2);
        Assert.assertEquals(vt, vt2);
    }

    /**
     * Test for GEOS-6052
     */
    @Test
    public void testVirtualTableMissingEscapeSql() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        DataStoreInfo ds = cFactory.createDataStore();
        ds.setWorkspace(ws);
        ds.setName("foo");
        catalog.add(ds);
        persister.setCatalog(catalog);
        FeatureTypeInfo ft = persister.load(getClass().getResourceAsStream("/org/geoserver/config/virtualtable_error.xml"), FeatureTypeInfo.class);
        VirtualTable vt2 = ((VirtualTable) (ft.getMetadata().get(JDBC_VIRTUAL_TABLE)));
        Assert.assertNotNull(vt2);
        Assert.assertEquals(1, ft.getMetadata().size());
    }

    /**
     * Another Test for GEOS-6052
     */
    @Test
    public void testVirtualTableMissingEscapeSqlDoesntSkipElements() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        DataStoreInfo ds = cFactory.createDataStore();
        ds.setWorkspace(ws);
        ds.setName("foo");
        catalog.add(ds);
        persister.setCatalog(catalog);
        FeatureTypeInfo ft = persister.load(getClass().getResourceAsStream("/org/geoserver/config/virtualtable_error_2.xml"), FeatureTypeInfo.class);
        VirtualTable vt2 = ((VirtualTable) (ft.getMetadata().get(JDBC_VIRTUAL_TABLE)));
        Assert.assertNotNull(vt2);
        Assert.assertEquals(1, ft.getMetadata().size());
        Assert.assertEquals(1, vt2.getGeometries().size());
        String geometryName = vt2.getGeometries().iterator().next();
        Assert.assertEquals("geometry", geometryName);
        Assert.assertNotNull(vt2.getGeometryType(geometryName));
        Assert.assertNotNull(vt2.getNativeSrid(geometryName));
    }

    /* Test for GEOS-8929 */
    @Test
    public void testOldJTSBindingConversion() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        DataStoreInfo ds = cFactory.createDataStore();
        ds.setWorkspace(ws);
        ds.setName("foo");
        catalog.add(ds);
        persister.setCatalog(catalog);
        FeatureTypeInfo ft = persister.load(getClass().getResourceAsStream("/org/geoserver/config/old_jts_binding.xml"), FeatureTypeInfo.class);
        Assert.assertNotNull(ft);
        Assert.assertEquals(LineString.class, ft.getAttributes().get(0).getBinding());
    }

    @Test
    public void testCRSConverter() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        CRSConverter c = new CRSConverter();
        Assert.assertEquals(crs.toWKT(), c.toString(crs));
        Assert.assertEquals(WGS84.toWKT(), c.toString(WGS84));
        CoordinateReferenceSystem crs2 = ((CoordinateReferenceSystem) (c.fromString(crs.toWKT())));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(crs, crs2));
        crs2 = ((CoordinateReferenceSystem) (c.fromString("EPSG:4326")));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(crs, crs2));
    }

    @Test
    public void testSRSConverter() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4901");
        SRSConverter c = new SRSConverter();
        Assert.assertEquals("EPSG:4901", c.toString(crs));
        // definition with odd UOM that won't be matched to the EPSG one
        Assert.assertFalse("EPSG:4901".equals(c.toString(CRS.parseWKT("GEOGCS[\"GCS_ATF_Paris\",DATUM[\"D_ATF\",SPHEROID[\"Plessis_1817\",6376523.0,308.64]],PRIMEM[\"Paris\",2.337229166666667],UNIT[\"Grad\",0.01570796326794897]]"))));
    }

    @Test
    public void testCRSConverterInvalidWKT() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3575");
        try {
            toWKT(2, true);
            Assert.fail("expected exception");
        } catch (UnformattableObjectException e) {
        }
        String wkt = null;
        try {
            wkt = new CRSConverter().toString(crs);
        } catch (UnformattableObjectException e) {
            Assert.fail("Should have thrown exception");
        }
        CoordinateReferenceSystem crs2 = ((CoordinateReferenceSystem) (new CRSConverter().fromString(wkt)));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(crs, crs2));
    }

    @Test
    public void testMultimapConverter() throws Exception {
        XStreamPersisterFactory factory = new XStreamPersisterFactory();
        XStreamPersister xmlPersister = factory.createXMLPersister();
        XStream xs = xmlPersister.getXStream();
        Multimap<String, Object> mmap = ArrayListMultimap.create();
        mmap.put("one", "abc");
        mmap.put("one", Integer.valueOf(2));
        mmap.put("two", new org.geotools.util.NumberRange<Integer>(Integer.class, 10, 20));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        persister.save(mmap, out);
        // print(in(out));
        Multimap mmap2 = persister.load(in(out), Multimap.class);
        Assert.assertEquals(mmap, mmap2);
    }

    @Test
    public void testPersisterCustomization() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        ws.getMetadata().put("banana", new XStreamPersisterTest.SweetBanana("Musa acuminata"));
        XStreamPersisterFactory factory = new XStreamPersisterFactory();
        factory.addInitializer(new XStreamPersisterInitializer() {
            @Override
            public void init(XStreamPersister persister) {
                persister.getXStream().alias("sweetBanana", XStreamPersisterTest.SweetBanana.class);
                persister.getXStream().aliasAttribute(XStreamPersisterTest.SweetBanana.class, "scientificName", "name");
                persister.registerBreifMapComplexType("sweetBanana", XStreamPersisterTest.SweetBanana.class);
            }
        });
        XStreamPersister persister = factory.createXMLPersister();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        persister.save(ws, out);
        WorkspaceInfo ws2 = persister.load(in(out), WorkspaceInfo.class);
        Assert.assertEquals(ws, ws2);
        Document dom = dom(in(out));
        // print(in(out));
        XMLAssert.assertXpathEvaluatesTo("Musa acuminata", "/workspace/metadata/entry[@key='banana']/sweetBanana/@name", dom);
    }

    @Test
    public void testCoverageView() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        CoverageInfo coverage = cFactory.createCoverage();
        MetadataMap metadata = coverage.getMetadata();
        coverage.setName("test");
        coverage.setEnabled(true);
        coverage.getAlias().add("alias");
        coverage.getKeywords().add(new Keyword("key"));
        MetadataLinkInfoImpl metadataLink = new MetadataLinkInfoImpl();
        metadataLink.setAbout("about");
        coverage.getMetadataLinks().add(metadataLink);
        CoverageDimensionImpl coverageDimension = new CoverageDimensionImpl("time");
        coverageDimension.setNullValues(Collections.singletonList(new Double(0)));
        coverage.getDimensions().add(coverageDimension);
        coverage.getInterpolationMethods().add("Bilinear");
        coverage.getParameters().put("ParameterKey", "ParameterValue");
        coverage.getSupportedFormats().add("GEOTIFF");
        coverage.getRequestSRS().add("EPSG:4326");
        coverage.getResponseSRS().add("EPSG:4326");
        final InputCoverageBand band_u = new InputCoverageBand("u-component_of_current_surface", "0");
        final CoverageBand outputBand_u = new CoverageBand(Collections.singletonList(band_u), "u-component_of_current_surface@0", 0, CompositionType.BAND_SELECT);
        final InputCoverageBand band_v = new InputCoverageBand("v-component_of_current_surface", "0");
        final CoverageBand outputBand_v = new CoverageBand(Collections.singletonList(band_v), "v-component_of_current_surface@0", 1, CompositionType.BAND_SELECT);
        final List<CoverageBand> coverageBands = new ArrayList<CoverageBand>(2);
        coverageBands.add(outputBand_u);
        coverageBands.add(outputBand_v);
        CoverageView coverageView = new CoverageView("regional_currents", coverageBands);
        metadata.put("COVERAGE_VIEW", coverageView);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        persister.save(coverage, out);
        CoverageInfo coverage2 = persister.load(in(out), CoverageInfo.class);
        Assert.assertEquals(coverage, coverage2);
    }

    @Test
    public void testVirtualTableOrder() throws Exception {
        FeatureTypeInfo ft = persister.load(getClass().getResourceAsStream("/org/geoserver/config/virtualtable_order_error.xml"), FeatureTypeInfo.class);
        VirtualTable vtc = ((VirtualTable) (ft.getMetadata().get(JDBC_VIRTUAL_TABLE)));
        Assert.assertEquals(vtc.getSql(), "select * from table\n");
        Assert.assertEquals(vtc.getName(), "sqlview");
    }

    @SuppressWarnings("serial")
    @Test
    public void testVirtualTableMultipleGeoms() throws IOException {
        Map<String, String> types = new HashMap<String, String>() {
            {
                put("southernmost_point", "org.locationtech.jts.geom.Geometry");
                put("location_polygon", "org.locationtech.jts.geom.Geometry");
                put("centroid", "org.locationtech.jts.geom.Geometry");
                put("northernmost_point", "org.locationtech.jts.geom.Geometry");
                put("easternmost_point", "org.locationtech.jts.geom.Geometry");
                put("location", "org.locationtech.jts.geom.Geometry");
                put("location_original", "org.locationtech.jts.geom.Geometry");
                put("westernmost_point", "org.locationtech.jts.geom.Geometry");
            }
        };
        Map<String, Integer> srids = new HashMap<String, Integer>() {
            {
                put("southernmost_point", 4326);
                put("location_polygon", 3003);
                put("centroid", 3004);
                put("northernmost_point", 3857);
                put("easternmost_point", 4326);
                put("location", 3003);
                put("location_original", 3004);
                put("westernmost_point", 3857);
            }
        };
        FeatureTypeInfo ft = persister.load(getClass().getResourceAsStream("/org/geoserver/config/virtualtable_error_GEOS-7400.xml"), FeatureTypeInfo.class);
        VirtualTable vt3 = ((VirtualTable) (ft.getMetadata().get(JDBC_VIRTUAL_TABLE)));
        Assert.assertEquals(8, vt3.getGeometries().size());
        for (String g : vt3.getGeometries()) {
            Class<? extends Geometry> geom = vt3.getGeometryType(g);
            Assert.assertEquals(srids.get(g).intValue(), vt3.getNativeSrid(g));
            Assert.assertEquals(types.get(g), geom.getName());
        }
    }

    /**
     * Test for GEOS-7444. Check GridGeometry is correctly unmarshaled when XML elements are
     * provided on an different order than the marshaling one
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGridGeometry2DConverterUnmarshalling() throws Exception {
        Catalog catalog = new CatalogImpl();
        CatalogFactory cFactory = catalog.getFactory();
        WorkspaceInfo ws = cFactory.createWorkspace();
        ws.setName("foo");
        catalog.add(ws);
        NamespaceInfo ns = cFactory.createNamespace();
        ns.setPrefix("acme");
        ns.setURI("http://acme.org");
        catalog.add(ns);
        CoverageStoreInfo cs = cFactory.createCoverageStore();
        cs.setWorkspace(ws);
        cs.setName("coveragestore");
        catalog.add(cs);
        CoverageInfo cv = cFactory.createCoverage();
        cv.setStore(cs);
        cv.setNamespace(ns);
        cv.setName("coverage");
        cv.setAbstract("abstract");
        cv.setSRS("EPSG:4326");
        cv.setNativeCRS(CRS.decode("EPSG:4326"));
        cv.getParameters().put("foo", null);
        ByteArrayOutputStream out = out();
        persister.save(cv, out);
        ByteArrayInputStream in = in(out);
        Document dom = dom(in);
        Element crs = dom.createElement("crs");
        Text t = dom.createTextNode("EPSG:4326");
        crs.appendChild(t);
        Element high = dom.createElement("high");
        t = dom.createTextNode("4029 4029");
        high.appendChild(t);
        Element low = dom.createElement("low");
        t = dom.createTextNode("0 0");
        low.appendChild(t);
        Element range = dom.createElement("range");
        range.appendChild(high);
        range.appendChild(low);
        Element translateX = dom.createElement("translateX");
        t = dom.createTextNode("0");
        translateX.appendChild(t);
        Element translateY = dom.createElement("translateY");
        t = dom.createTextNode("0");
        translateY.appendChild(t);
        Element scaleX = dom.createElement("scaleX");
        t = dom.createTextNode("1");
        scaleX.appendChild(t);
        Element scaleY = dom.createElement("scaleY");
        t = dom.createTextNode("1");
        scaleY.appendChild(t);
        Element shearX = dom.createElement("shearX");
        t = dom.createTextNode("0");
        shearX.appendChild(t);
        Element shearY = dom.createElement("shearY");
        t = dom.createTextNode("0");
        shearY.appendChild(t);
        Element transform = dom.createElement("transform");
        transform.appendChild(translateX);
        transform.appendChild(translateY);
        transform.appendChild(scaleX);
        transform.appendChild(scaleY);
        transform.appendChild(shearX);
        transform.appendChild(shearY);
        Element grid = dom.createElement("grid");
        grid.setAttribute("dimension", "2");
        grid.appendChild(crs);
        grid.appendChild(range);
        grid.appendChild(transform);
        Element e = ((Element) (dom.getElementsByTagName("coverage").item(0)));
        Element params = ((Element) (dom.getElementsByTagName("parameters").item(0)));
        e.insertBefore(grid, params);
        in = in(dom);
        persister.setCatalog(catalog);
        cv = persister.load(in, CoverageInfo.class);
        Assert.assertNotNull(cv);
        Assert.assertNotNull(cv.getGrid());
        Assert.assertNotNull(cv.getGrid().getGridRange());
        Assert.assertNotNull(cv.getCRS());
        Assert.assertNotNull(cv.getGrid().getGridToCRS());
        Assert.assertEquals(cv.getGrid().getGridRange().getLow(0), 0);
    }

    @Test
    public void readSettingsMetadataInvalidEntry() throws Exception {
        String xml = "<global>\n" + ((((((((((((((((((((((((("  <settings>\n" + "    <metadata>\n") + "      <map>\n") + "        <entry>\n") + "            <string>key1</string>\n") + "            <string>value1</string>\n") + "        </entry>\n") + "        <entry>\n") + "          <string>NetCDFOutput.Key</string>\n") + "          <netCDFSettings>\n") + "            <compressionLevel>0</compressionLevel>\n") + "            <shuffle>true</shuffle>\n") + "            <copyAttributes>false</copyAttributes>\n") + "            <copyGlobalAttributes>false</copyGlobalAttributes>\n") + "            <dataPacking>NONE</dataPacking>\n") + "          </netCDFSettings>\n") + "        </entry>\n") + "        <entry>\n") + "            <string>key2</string>\n") + "            <string>value2</string>\n") + "        </entry>\n") + "      </map>\n") + "    </metadata>\n") + "    <localWorkspaceIncludesPrefix>true</localWorkspaceIncludesPrefix>\n") + "  </settings>\n") + "</global>\n");
        GeoServerInfo gs = persister.load(new ByteArrayInputStream(xml.getBytes()), GeoServerInfo.class);
        SettingsInfo settings = gs.getSettings();
        MetadataMap metadata = settings.getMetadata();
        Assert.assertEquals(2, metadata.size());
        Assert.assertThat(metadata, Matchers.hasEntry("key1", "value1"));
        Assert.assertThat(metadata, Matchers.hasEntry("key2", "value2"));
        Assert.assertTrue(settings.isLocalWorkspaceIncludesPrefix());
        // check it round trips the same way it came in, minus the bit we could not read
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        persister.save(gs, bos);
        // System.out.println(new String(bos.toByteArray()));
        Document doc = dom(new ByteArrayInputStream(bos.toByteArray()));
        XMLAssert.assertXpathExists("//settings/metadata/map", doc);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//settings/metadata/map/entry)", doc);
        XMLAssert.assertXpathEvaluatesTo("key1", "//settings/metadata/map/entry[1]/string[1]", doc);
        XMLAssert.assertXpathEvaluatesTo("value1", "//settings/metadata/map/entry[1]/string[2]", doc);
        XMLAssert.assertXpathEvaluatesTo("key2", "//settings/metadata/map/entry[2]/string[1]", doc);
        XMLAssert.assertXpathEvaluatesTo("value2", "//settings/metadata/map/entry[2]/string[2]", doc);
    }

    @Test
    public void readCoverageMetadataInvalidEntry() throws Exception {
        String xml = "<coverage>\n" + ((((((((((((("  <metadata>\n" + "    <entry key=\"key1\">value1</entry>\n") + "    <entry key=\"netcdf\">\n") + "      <netCDFSettings>\n") + "            <compressionLevel>0</compressionLevel>\n") + "            <shuffle>true</shuffle>\n") + "            <copyAttributes>false</copyAttributes>\n") + "            <copyGlobalAttributes>false</copyGlobalAttributes>\n") + "            <dataPacking>NONE</dataPacking>\n") + "      </netCDFSettings>\n") + "    </entry>\n") + "    <entry key=\"key2\">value2</entry>\n") + "  </metadata>\n") + "</coverage>");
        CoverageInfo ci = persister.load(new ByteArrayInputStream(xml.getBytes()), CoverageInfo.class);
        MetadataMap metadata = ci.getMetadata();
        Assert.assertEquals(3, metadata.size());
        Assert.assertThat(metadata, Matchers.hasEntry("key1", "value1"));
        Assert.assertThat(metadata, Matchers.hasEntry("key2", "value2"));
        Assert.assertThat(metadata, Matchers.hasEntry("netcdf", null));
    }

    static class SweetBanana implements Serializable {
        String scientificName;

        public SweetBanana(String scientificName) {
            super();
            this.scientificName = scientificName;
        }
    }
}

