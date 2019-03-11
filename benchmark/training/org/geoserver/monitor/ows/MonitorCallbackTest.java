/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor.ows;


import WfsFactory.eINSTANCE;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.namespace.QName;
import net.opengis.ows11.BoundingBoxType;
import net.opengis.ows11.CodeType;
import net.opengis.wcs10.DescribeCoverageType;
import net.opengis.wcs10.DomainSubsetType;
import net.opengis.wcs10.GetCoverageType;
import net.opengis.wcs10.SpatialSubsetType;
import net.opengis.wfs.DeleteElementType;
import net.opengis.wfs.DescribeFeatureTypeType;
import net.opengis.wfs.GetFeatureType;
import net.opengis.wfs.InsertElementType;
import net.opengis.wfs.LockFeatureType;
import net.opengis.wfs.LockType;
import net.opengis.wfs.QueryType;
import net.opengis.wfs.TransactionType;
import net.opengis.wfs.UpdateElementType;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServer;
import org.geoserver.monitor.BBoxAsserts;
import org.geoserver.monitor.Monitor;
import org.geoserver.monitor.RequestData;
import org.geoserver.ows.Request;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.Operation;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resources;
import org.geoserver.security.PropertyFileWatcher;
import org.geoserver.wms.GetFeatureInfoRequest;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.WMS;
import org.geotools.feature.NameImpl;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class MonitorCallbackTest {
    static Monitor monitor;

    MonitorCallback callback;

    RequestData data;

    static Catalog catalog;

    @Test
    public void testBasic() throws Exception {
        callback.operationDispatched(new Request(), op("foo", "bar", "1.2.3", null));
        Assert.assertEquals("BAR", data.getService());
        Assert.assertEquals("foo", data.getOperation());
        Assert.assertEquals("1.2.3", data.getOwsVersion());
    }

    @Test
    public void testWFSDescribeFeatureType() throws Exception {
        DescribeFeatureTypeType dft = eINSTANCE.createDescribeFeatureTypeType();
        dft.getTypeName().add(new QName("http://acme.org", "foo", "acme"));
        dft.getTypeName().add(new QName("http://acme.org", "bar", "acme"));
        Operation op = op("DescribeFeatureType", "WFS", "1.0.0", dft);
        callback.operationDispatched(new Request(), op);
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
    }

    @Test
    public void testWFSGetFeature() throws Exception {
        GetFeatureType gf = eINSTANCE.createGetFeatureType();
        Filter f1 = MonitorCallbackTest.parseFilter("BBOX(the_geom, 40, -90, 45, -60)");
        Filter f2 = MonitorCallbackTest.parseFilter("BBOX(the_geom, 5988504.35,851278.90, 7585113.55,1950872.01)");
        QueryType q = eINSTANCE.createQueryType();
        q.setTypeName(Arrays.asList(new QName("http://acme.org", "foo", "acme")));
        q.setFilter(f1);
        gf.getQuery().add(q);
        q = eINSTANCE.createQueryType();
        q.setTypeName(Arrays.asList(new QName("http://acme.org", "bar", "acme")));
        gf.getQuery().add(q);
        getClass();
        q.setFilter(f2);
        Operation op = op("GetFeature", "WFS", "1.0.0", gf);
        callback.operationDispatched(new Request(), op);
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
        BoundingBox expected = new ReferencedEnvelope(53.73, 40, (-60), (-95.1193), CRS.decode("EPSG:4326"));
        // xMin,yMin -95.1193,40 : xMax,yMax -60,53.73
        BBoxAsserts.assertEqualsBbox(expected, data.getBbox(), 0.01);
    }

    @Test
    public void testWFSLockFeature() throws Exception {
        LockFeatureType lf = eINSTANCE.createLockFeatureType();
        LockType l = eINSTANCE.createLockType();
        l.setTypeName(new QName("http://acme.org", "foo", "acme"));
        lf.getLock().add(l);
        Operation op = op("LockFeature", "WFS", "1.0.0", lf);
        callback.operationDispatched(new Request(), op);
        Assert.assertEquals("acme:foo", data.getResources().get(0));
    }

    @Test
    public void testWFSTransaction() throws Exception {
        TransactionType t = eINSTANCE.createTransactionType();
        Filter f1 = MonitorCallbackTest.parseFilter("BBOX(the_geom, 40, -90, 45, -60)");
        Filter f2 = MonitorCallbackTest.parseFilter("BBOX(the_geom, 5988504.35,851278.90, 7585113.55,1950872.01)");
        UpdateElementType ue = eINSTANCE.createUpdateElementType();
        ue.setTypeName(new QName("http://acme.org", "foo", "acme"));
        ue.setFilter(f1);
        t.getUpdate().add(ue);
        DeleteElementType de = eINSTANCE.createDeleteElementType();
        de.setTypeName(new QName("http://acme.org", "bar", "acme"));
        de.setFilter(f2);
        t.getDelete().add(de);
        Operation op = op("Transaction", "WFS", "1.1.0", t);
        callback.operationDispatched(new Request(), op);
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
        BoundingBox expected = new ReferencedEnvelope(53.73, 40, (-60), (-95.1193), CRS.decode("EPSG:4326"));
        // xMin,yMin -95.1193,40 : xMax,yMax -60,53.73
        BBoxAsserts.assertEqualsBbox(expected, data.getBbox(), 0.01);
    }

    @Test
    public void testWFSTransactionInsert() throws Exception {
        TransactionType t = eINSTANCE.createTransactionType();
        InsertElementType ie = eINSTANCE.createInsertElementType();
        t.getInsert().add(ie);
        // ie.setSrsName(new URI("epsg:4326"));
        BoundingBox expected = new ReferencedEnvelope(53.73, 40, (-60), (-95.1193), CRS.decode("EPSG:4326"));
        SimpleFeatureType ft = createNiceMock(SimpleFeatureType.class);
        expect(ft.getTypeName()).andReturn("acme:foo").anyTimes();
        replay(ft);
        SimpleFeature f = createNiceMock(SimpleFeature.class);
        expect(f.getBounds()).andReturn(expected).anyTimes();
        expect(f.getType()).andReturn(ft).anyTimes();
        replay(f);
        ie.getFeature().add(f);
        Operation op = op("Transaction", "WFS", "1.1.0", t);
        callback.operationDispatched(new Request(), op);
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        // xMin,yMin -95.1193,40 : xMax,yMax -60,53.73
        BBoxAsserts.assertEqualsBbox(expected, data.getBbox(), 0.01);
    }

    @Test
    public void testWMSGetMap() throws Exception {
        GetMapRequest gm = new GetMapRequest();
        gm.setLayers(Arrays.asList(createMapLayer("foo", "acme")));
        Envelope env = new Envelope(100, 110, 70, 80);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
        CoordinateReferenceSystem logCrs = CRS.decode("EPSG:4326");
        gm.setBbox(env);
        gm.setCrs(crs);
        callback.operationDispatched(new Request(), op("GetMap", "WMS", "1.1.1", gm));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        BBoxAsserts.assertEqualsBbox(new ReferencedEnvelope(env, crs).toBounds(logCrs), data.getBbox(), 0.1);
    }

    @Test
    public void testWMSReflect() throws Exception {
        GetMapRequest gm = new GetMapRequest();
        gm.setLayers(Arrays.asList(createMapLayer("foo", "acme")));
        callback.operationDispatched(new Request(), op("reflect", "WMS", "1.1.1", gm));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
    }

    @Test
    public void testWMSGetFeatureInfo() throws Exception {
        GetFeatureInfoRequest gfi = new GetFeatureInfoRequest();
        GetMapRequest gm = new GetMapRequest();
        gm.setHeight(330);
        gm.setWidth(780);
        Envelope env = new ReferencedEnvelope((-126.81851), (-115.818992), 44.852958, 49.5066, null);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
        CoordinateReferenceSystem logCrs = CRS.decode("EPSG:4326", false);
        gm.setBbox(env);
        gm.setCrs(crs);
        gfi.setGetMapRequest(gm);
        gfi.setXPixel(260);
        gfi.setYPixel(63);
        gfi.setVersion("1.1.1");
        gfi.setQueryLayers(Arrays.asList(createMapLayer("foo", "acme"), createMapLayer("bar", "acme")));
        callback.operationDispatched(new Request(), op("GetFeatureInfo", "WMS", "1.1.1", gfi));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
        BBoxAsserts.assertEqualsBbox(new ReferencedEnvelope(48.62, 48.62, (-123.15), (-123.15), logCrs), data.getBbox(), 0.01);
    }

    @Test
    public void testWMSGetLegendGraphic() throws Exception {
        WMS wms = new WMS(createMock(GeoServer.class));
        GetLegendGraphicRequest glg = new GetLegendGraphicRequest();
        FeatureType type = createMock(FeatureType.class);
        expect(type.getName()).andReturn(new NameImpl("http://acme.org", "foo")).anyTimes();
        replay(type);
        glg.setLayer(type);
        callback.operationDispatched(new Request(), op("GetFeatureInfo", "WMS", "1.1.1", glg));
        Assert.assertEquals("http://acme.org:foo", data.getResources().get(0));
    }

    @Test
    public void testWCS10DescribeCoverage() throws Exception {
        DescribeCoverageType dc = Wcs10Factory.eINSTANCE.createDescribeCoverageType();
        dc.getCoverage().add("acme:foo");
        dc.getCoverage().add("acme:bar");
        callback.operationDispatched(new Request(), op("DescribeCoverage", "WCS", "1.0.0", dc));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
    }

    @Test
    public void testWCS10GetCoverage() throws Exception {
        GetCoverageType gc = Wcs10Factory.eINSTANCE.createGetCoverageType();
        SpatialSubsetType spatialSubset = Wcs10Factory.eINSTANCE.createSpatialSubsetType();
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        GeneralEnvelope env = new GeneralEnvelope(new double[]{ -123.4, 48.2 }, new double[]{ -120.9, 50.1 });
        env.setCoordinateReferenceSystem(crs);
        BoundingBox bbox = new ReferencedEnvelope(env);
        spatialSubset.getEnvelope().clear();
        spatialSubset.getEnvelope().add(env);
        DomainSubsetType domainSubset = Wcs10Factory.eINSTANCE.createDomainSubsetType();
        domainSubset.setSpatialSubset(spatialSubset);
        gc.setSourceCoverage("acme:foo");
        gc.setDomainSubset(domainSubset);
        callback.operationDispatched(new Request(), op("GetCoverage", "WCS", "1.0.0", gc));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        BBoxAsserts.assertEqualsBbox(bbox, data.getBbox(), 0.1);
    }

    @Test
    public void testWCS11DescribeCoverage() throws Exception {
        net.opengis.wcs11.DescribeCoverageType dc = Wcs11Factory.eINSTANCE.createDescribeCoverageType();
        dc.getIdentifier().add("acme:foo");
        dc.getIdentifier().add("acme:bar");
        callback.operationDispatched(new Request(), op("DescribeCoverage", "WCS", "1.1.0", dc));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
    }

    @Test
    public void testWCS11GetCoverage() throws Exception {
        net.opengis.wcs11.GetCoverageType gc = Wcs11Factory.eINSTANCE.createGetCoverageType();
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        GeneralEnvelope env = new GeneralEnvelope(new double[]{ 48.2, -123.4 }, new double[]{ 50.1, -120.9 });
        env.setCoordinateReferenceSystem(crs);
        BoundingBox bbox = new ReferencedEnvelope(env);
        BoundingBoxType wcsBbox = net.opengis.ows11.Ows11Factory.eINSTANCE.createBoundingBoxType();
        wcsBbox.setLowerCorner(Arrays.asList(48.2, (-123.4)));
        wcsBbox.setUpperCorner(Arrays.asList(50.1, (-120.9)));
        // wcsBbox.setCrs("urn:ogc:def:crs:OGC:1.3:CRS84");
        wcsBbox.setCrs("urn:ogc:def:crs:EPSG:4326");
        net.opengis.wcs11.DomainSubsetType domainSubset = Wcs11Factory.eINSTANCE.createDomainSubsetType();
        domainSubset.setBoundingBox(wcsBbox);
        gc.setDomainSubset(domainSubset);
        CodeType c = Ows11Factory.eINSTANCE.createCodeType();
        c.setValue("acme:bar");
        gc.setIdentifier(c);
        callback.operationDispatched(new Request(), op("GetCoverage", "WCS", "1.1.0", gc));
        Assert.assertEquals("acme:bar", data.getResources().get(0));
        BBoxAsserts.assertEqualsBbox(bbox, data.getBbox(), 0.1);
    }

    @Test
    public void testWCS10GetCoverageDifferentCrs() throws Exception {
        // xMin,yMin 5988504.35,851278.90 : xMax,yMax 7585113.55,1950872.01
        // xMin,yMin -95.1193,42.2802 : xMax,yMax -71.295,53.73
        GetCoverageType gc = Wcs10Factory.eINSTANCE.createGetCoverageType();
        SpatialSubsetType spatialSubset = Wcs10Factory.eINSTANCE.createSpatialSubsetType();
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3348", false);
        CoordinateReferenceSystem logCrs = CRS.decode("EPSG:4326", false);
        GeneralEnvelope env = new GeneralEnvelope(new double[]{ 5988504.35, 851278.9 }, new double[]{ 7585113.55, 1950872.01 });
        env.setCoordinateReferenceSystem(crs);
        BoundingBox bbox = new ReferencedEnvelope(42.2802, 53.73, (-95.1193), (-71.295), logCrs);
        spatialSubset.getEnvelope().clear();
        spatialSubset.getEnvelope().add(env);
        DomainSubsetType domainSubset = Wcs10Factory.eINSTANCE.createDomainSubsetType();
        domainSubset.setSpatialSubset(spatialSubset);
        gc.setSourceCoverage("acme:foo");
        gc.setDomainSubset(domainSubset);
        callback.operationDispatched(new Request(), op("GetCoverage", "WCS", "1.0.0", gc));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        BBoxAsserts.assertEqualsBbox(bbox, data.getBbox(), 0.1);
    }

    @Test
    public void testWCS11GetCoverageDifferentCrs() throws Exception {
        net.opengis.wcs11.GetCoverageType gc = Wcs11Factory.eINSTANCE.createGetCoverageType();
        // xMin,yMin 5988504.35,851278.90 : xMax,yMax 7585113.55,1950872.01
        // xMin,yMin -95.1193,42.2802 : xMax,yMax -71.295,53.73
        CoordinateReferenceSystem logCrs = CRS.decode("EPSG:4326", false);
        BoundingBox bbox = new ReferencedEnvelope(42.2802, 53.73, (-95.1193), (-71.295), logCrs);
        BoundingBoxType wcsBbox = net.opengis.ows11.Ows11Factory.eINSTANCE.createBoundingBoxType();
        wcsBbox.setLowerCorner(Arrays.asList(5988504.35, 851278.9));
        wcsBbox.setUpperCorner(Arrays.asList(7585113.55, 1950872.01));
        wcsBbox.setCrs("urn:ogc:def:crs:EPSG:3348");
        net.opengis.wcs11.DomainSubsetType domainSubset = Wcs11Factory.eINSTANCE.createDomainSubsetType();
        domainSubset.setBoundingBox(wcsBbox);
        gc.setDomainSubset(domainSubset);
        CodeType c = Ows11Factory.eINSTANCE.createCodeType();
        c.setValue("acme:bar");
        gc.setIdentifier(c);
        callback.operationDispatched(new Request(), op("GetCoverage", "WCS", "1.1.0", gc));
        Assert.assertEquals("acme:bar", data.getResources().get(0));
        BBoxAsserts.assertEqualsBbox(bbox, data.getBbox(), 0.1);
    }

    @Test
    public void testWMSGetMapDifferentCrs() throws Exception {
        // xMin,yMin 5988504.35,851278.90 : xMax,yMax 7585113.55,1950872.01
        // xMin,yMin -95.1193,42.2802 : xMax,yMax -71.295,53.73
        GetMapRequest gm = new GetMapRequest();
        gm.setLayers(Arrays.asList(createMapLayer("foo", "acme")));
        Envelope env = new Envelope(5988504.35, 7585113.55, 851278.9, 1950872.01);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3348", true);
        gm.setBbox(env);
        gm.setCrs(crs);
        callback.operationDispatched(new Request(), op("GetMap", "WMS", "1.1.1", gm));
        CoordinateReferenceSystem logCrs = CRS.decode("EPSG:4326", false);
        BoundingBox bbox = new ReferencedEnvelope(42.2802, 53.73, (-95.1193), (-71.295), logCrs);
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        BBoxAsserts.assertEqualsBbox(bbox, data.getBbox(), 0.1);
    }

    @Test
    public void testWMSGetFeatureInfoDifferentCrs() throws Exception {
        /* BBOX 3833170.221556,1841755.690829, 4083455.358596,2048534.231783
        EXCEPTIONS      application/vnd.ogc.se_xml
        FEATURE_COUNT   50
        HEIGHT  423
        INFO_FORMAT     text/html
        Layers  monitor-test:prov3348
        QUERY_LAYERS    monitor-test:prov3348
        REQUEST GetFeatureInfo
        SERVICE WMS
        WIDTH   512
        format  image/png
        srs     EPSG:3348
        styles
        version 1.1.1
        x       259
        y       241
         */
        /* -123.34927,48.44669,3960017.648,1933344.872 */
        GetFeatureInfoRequest gfi = new GetFeatureInfoRequest();
        GetMapRequest gm = new GetMapRequest();
        gm.setHeight(423);
        gm.setWidth(512);
        Envelope env = new ReferencedEnvelope(3833170.221556, 4083455.358596, 1841755.690829, 2048534.231783, null);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3348", true);
        CoordinateReferenceSystem logCrs = CRS.decode("EPSG:4326", false);
        gm.setBbox(env);
        gm.setCrs(crs);
        gfi.setGetMapRequest(gm);
        gfi.setXPixel(259);
        gfi.setYPixel(241);
        gfi.setVersion("1.1.1");
        gfi.setQueryLayers(Arrays.asList(createMapLayer("foo", "acme"), createMapLayer("bar", "acme")));
        callback.operationDispatched(new Request(), op("GetFeatureInfo", "WMS", "1.1.1", gfi));
        Assert.assertEquals("acme:foo", data.getResources().get(0));
        Assert.assertEquals("acme:bar", data.getResources().get(1));
        BBoxAsserts.assertEqualsBbox(new ReferencedEnvelope(48.4, 48.4, (-123.3), (-123.3), logCrs), data.getBbox(), 0.1);
    }

    @Test
    public void testConfiguration() throws IOException {
        // store the properties into a temp folder and relaod
        Assert.assertTrue(MonitorCallbackTest.monitor.getConfig().getFileLocations().isEmpty());
        File tmpDir = MonitorCallbackTest.createTempDir();
        GeoServerResourceLoader resourceLoader = new GeoServerResourceLoader(tmpDir);
        MonitorCallbackTest.monitor.getConfig().saveConfiguration(resourceLoader);
        Resource controlFlowProps = Files.asResource(resourceLoader.find("monitor.properties"));
        Assert.assertTrue(Resources.exists(controlFlowProps));
        PropertyFileWatcher savedProps = new PropertyFileWatcher(controlFlowProps);
        Assert.assertEquals(savedProps.getProperties(), MonitorCallbackTest.monitor.getConfig().getProperties());
    }
}

