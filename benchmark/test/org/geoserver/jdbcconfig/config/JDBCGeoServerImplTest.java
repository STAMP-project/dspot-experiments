/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.jdbcconfig.config;


import java.util.Collection;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServerImplTest;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.impl.SettingsInfoImpl;
import org.geoserver.jdbcconfig.JDBCConfigTestSupport;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.WFSInfoImpl;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSInfoImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JDBCGeoServerImplTest extends GeoServerImplTest {
    private JDBCGeoServerFacade facade;

    private JDBCConfigTestSupport testSupport;

    public JDBCGeoServerImplTest(JDBCConfigTestSupport.DBConfig dbConfig) {
        testSupport = new JDBCConfigTestSupport(dbConfig);
    }

    @Test
    public void testGlobalSettingsWithId() throws Exception {
        SettingsInfoImpl settings = new SettingsInfoImpl();
        settings.setId("settings");
        GeoServerInfo global = geoServer.getFactory().createGlobal();
        global.setSettings(settings);
        geoServer.setGlobal(global);
        Assert.assertEquals(global, geoServer.getGlobal());
    }

    @Override
    @Test
    public void testModifyService() throws Exception {
        ServiceInfo service = geoServer.getFactory().createService();
        setId("id");
        service.setName("foo");
        service.setTitle("bar");
        service.setMaintainer("quux");
        geoServer.add(service);
        ServiceInfo s1 = geoServer.getServiceByName("foo", ServiceInfo.class);
        s1.setMaintainer("quam");
        ServiceInfo s2 = geoServer.getServiceByName("foo", ServiceInfo.class);
        Assert.assertEquals("quux", s2.getMaintainer());
        ServiceInfo s3 = geoServer.getService(ServiceInfo.class);
        Assert.assertEquals("quux", s3.getMaintainer());
        geoServer.save(s1);
        s2 = geoServer.getServiceByName("foo", ServiceInfo.class);
        Assert.assertEquals("quam", s2.getMaintainer());
        s3 = geoServer.getService(ServiceInfo.class);
        Assert.assertEquals("quam", s3.getMaintainer());
        geoServer.remove(s1);
        s2 = geoServer.getServiceByName("foo", ServiceInfo.class);
        Assert.assertNull(s2);
        s3 = geoServer.getService(ServiceInfo.class);
        Assert.assertNull(s3);
    }

    // Would have put this on GeoServerImplTest, but it depends on WMS and WFS InfoImpl classes
    // which would lead to circular dependencies.
    @SuppressWarnings("unchecked")
    @Test
    public void testTypedServicesWithWorkspace() throws Exception {
        // Make a workspace
        WorkspaceInfo ws1 = geoServer.getCatalog().getFactory().createWorkspace();
        ws1.setName("TEST-WORKSPACE-1");
        geoServer.getCatalog().add(ws1);
        // Make a service for that workspace
        ServiceInfo ws1wms = new WMSInfoImpl();
        ws1wms.setWorkspace(ws1);
        ws1wms.setName("WMS1");
        ws1wms.setTitle("WMS for WS1");
        geoServer.add(ws1wms);
        // Make a second for that workspace
        ServiceInfo ws1wfs = new WFSInfoImpl();
        ws1wfs.setWorkspace(ws1);
        ws1wfs.setName("WFS1");
        ws1wfs.setTitle("WFS for WS1");
        geoServer.add(ws1wfs);
        // Make a global service
        ServiceInfo gwms = new WMSInfoImpl();
        gwms.setName("WMSG");
        gwms.setTitle("Global WMS");
        geoServer.add(gwms);
        // Make a second global service
        ServiceInfo gwfs = new WFSInfoImpl();
        gwfs.setName("WFSG");
        gwfs.setTitle("Global WFS");
        geoServer.add(gwfs);
        // Make a workspace
        WorkspaceInfo ws2 = geoServer.getCatalog().getFactory().createWorkspace();
        ws2.setName("TEST-WORKSPACE-2");
        geoServer.getCatalog().add(ws2);
        // Make a service for that workspace
        ServiceInfo ws2wms = new WMSInfoImpl();
        ws2wms.setWorkspace(ws2);
        ws2wms.setName("WMS2");
        ws2wms.setTitle("WMS for WS2");
        geoServer.add(ws2wms);
        // Make a second for that workspace
        ServiceInfo ws2wfs = new WFSInfoImpl();
        ws2wfs.setWorkspace(ws2);
        ws2wfs.setName("WFS2");
        ws2wfs.setTitle("WFS for WS2");
        geoServer.add(ws2wfs);
        // Check that we get the services we expect to
        Assert.assertThat(geoServer.getService(WMSInfo.class), CoreMatchers.equalTo(gwms));
        Assert.assertThat(geoServer.getService(WFSInfo.class), CoreMatchers.equalTo(gwfs));
        Assert.assertThat(((Collection<ServiceInfo>) (geoServer.getServices())), CoreMatchers.allOf(CoreMatchers.hasItems(gwms, gwfs), CoreMatchers.not(CoreMatchers.hasItems(ws1wms, ws1wfs, ws2wms, ws2wfs))));
        Assert.assertThat(geoServer.getService(ws1, WMSInfo.class), CoreMatchers.equalTo(ws1wms));
        Assert.assertThat(geoServer.getService(ws1, WFSInfo.class), CoreMatchers.equalTo(ws1wfs));
        Assert.assertThat(((Collection<ServiceInfo>) (geoServer.getServices(ws1))), CoreMatchers.allOf(CoreMatchers.hasItems(ws1wms, ws1wfs), CoreMatchers.not(CoreMatchers.hasItems(gwms, gwfs, ws2wms, ws2wfs))));
        Assert.assertThat(geoServer.getService(ws2, WMSInfo.class), CoreMatchers.equalTo(ws2wms));
        Assert.assertThat(geoServer.getService(ws2, WFSInfo.class), CoreMatchers.equalTo(ws2wfs));
        Assert.assertThat(((Collection<ServiceInfo>) (geoServer.getServices(ws2))), CoreMatchers.allOf(CoreMatchers.hasItems(ws2wms, ws2wfs), CoreMatchers.not(CoreMatchers.hasItems(gwms, gwfs, ws1wms, ws1wfs))));
    }
}

