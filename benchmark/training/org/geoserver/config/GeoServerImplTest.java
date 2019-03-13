/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.impl.GeoServerInfoImpl;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.geoserver.ows.LocalWorkspace;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerImplTest {
    protected GeoServerImpl geoServer;

    @Test
    public void testGlobal() throws Exception {
        GeoServerInfo global = geoServer.getFactory().createGlobal();
        geoServer.setGlobal(global);
        Assert.assertEquals(global, geoServer.getGlobal());
        // GEOS-7890
        GeoServerInfo global1 = geoServer.getFactory().createGlobal();
        GeoServerInfo global2 = geoServer.getFactory().createGlobal();
        global1.setGlobalServices(Boolean.valueOf(true));
        global1.setXmlExternalEntitiesEnabled(Boolean.valueOf(false));
        global1.setVerbose(Boolean.valueOf(false));
        global1.setVerboseExceptions(Boolean.valueOf(false));
        global2.setGlobalServices(Boolean.valueOf(true));
        global2.setXmlExternalEntitiesEnabled(Boolean.valueOf(false));
        global2.setVerbose(Boolean.valueOf(false));
        global2.setVerboseExceptions(Boolean.valueOf(false));
        Assert.assertEquals(global1, global2);
    }

    @Test
    public void testModifyGlobal() throws Exception {
        GeoServerInfo global = geoServer.getFactory().createGlobal();
        geoServer.setGlobal(global);
        GeoServerInfo g1 = geoServer.getGlobal();
        g1.setAdminPassword("newAdminPassword");
        GeoServerInfo g2 = geoServer.getGlobal();
        Assert.assertNull(g2.getAdminPassword());
        geoServer.save(g1);
        g2 = geoServer.getGlobal();
        Assert.assertEquals("newAdminPassword", g2.getAdminPassword());
    }

    @Test
    public void testAddService() throws Exception {
        ServiceInfo service = geoServer.getFactory().createService();
        service.setName("foo");
        geoServer.add(service);
        ServiceInfo s2 = geoServer.getFactory().createService();
        ((ServiceInfoImpl) (s2)).setId(service.getId());
        try {
            geoServer.add(s2);
            Assert.fail("adding service with duplicate id should throw exception");
        } catch (Exception e) {
        }
        ServiceInfo s = geoServer.getServiceByName("foo", ServiceInfo.class);
        Assert.assertTrue((s != service));
        Assert.assertEquals(service, s);
    }

    @Test
    public void testModifyService() throws Exception {
        ServiceInfo service = geoServer.getFactory().createService();
        setId("id");
        service.setName("foo");
        service.setTitle("bar");
        geoServer.add(service);
        ServiceInfo s1 = geoServer.getServiceByName("foo", ServiceInfo.class);
        s1.setTitle("changed");
        ServiceInfo s2 = geoServer.getServiceByName("foo", ServiceInfo.class);
        Assert.assertEquals("bar", s2.getTitle());
        geoServer.save(s1);
        s2 = geoServer.getServiceByName("foo", ServiceInfo.class);
        Assert.assertEquals("changed", s2.getTitle());
    }

    @Test
    public void testGlobalEvents() throws Exception {
        GeoServerImplTest.TestListener tl = new GeoServerImplTest.TestListener();
        geoServer.addListener(tl);
        GeoServerInfo global = geoServer.getFactory().createGlobal();
        geoServer.setGlobal(global);
        global = geoServer.getGlobal();
        global.setAdminPassword("foo");
        global.setOnlineResource("bar");
        Assert.assertEquals(0, tl.gPropertyNames.size());
        geoServer.save(global);
        Assert.assertEquals(2, tl.gPropertyNames.size());
        Assert.assertTrue(tl.gPropertyNames.contains("adminPassword"));
        Assert.assertTrue(tl.gPropertyNames.contains("onlineResource"));
    }

    static class TestListener extends ConfigurationListenerAdapter {
        List<String> gPropertyNames = new ArrayList();

        List<Object> gOldValues = new ArrayList();

        List<Object> gNewValues = new ArrayList();

        List<String> sPropertyNames = new ArrayList();

        List<Object> sOldValues = new ArrayList();

        List<Object> sNewValues = new ArrayList();

        public void handleGlobalChange(GeoServerInfo global, List<String> propertyNames, List<Object> oldValues, List<Object> newValues) {
            gPropertyNames.addAll(propertyNames);
            gOldValues.addAll(oldValues);
            gNewValues.addAll(newValues);
        }

        public void handleServiceChange(ServiceInfo service, List<String> propertyNames, List<Object> oldValues, List<Object> newValues) {
            sPropertyNames.addAll(propertyNames);
            sOldValues.addAll(oldValues);
            sNewValues.addAll(newValues);
        }
    }

    @Test
    public void testSetClientPropsHasEffect() throws Exception {
        GeoServerInfoImpl gsii = new GeoServerInfoImpl(geoServer);
        Map<Object, Object> before = gsii.getClientProperties();
        Map<Object, Object> newProps = new HashMap<Object, Object>();
        newProps.put("123", "456");
        gsii.setClientProperties(newProps);
        Assert.assertFalse(before.equals(newProps));
    }

    @Test
    public void testGetSettings() throws Exception {
        GeoServerInfo global = geoServer.getFactory().createGlobal();
        geoServer.setGlobal(global);
        SettingsInfo s = geoServer.getSettings();
        Assert.assertNotNull(s);
        Assert.assertEquals(4, s.getNumDecimals());
        WorkspaceInfo ws = geoServer.getCatalog().getFactory().createWorkspace();
        ws.setName("acme");
        geoServer.getCatalog().add(ws);
        SettingsInfo t = geoServer.getFactory().createSettings();
        t.setNumDecimals(7);
        t.setWorkspace(ws);
        geoServer.add(t);
        Assert.assertNotNull(geoServer.getSettings(ws));
        Assert.assertEquals(7, geoServer.getSettings(ws).getNumDecimals());
        Assert.assertEquals(4, geoServer.getSettings().getNumDecimals());
        LocalWorkspace.set(ws);
        try {
            Assert.assertNotNull(geoServer.getSettings());
            Assert.assertEquals(7, geoServer.getSettings().getNumDecimals());
        } finally {
            LocalWorkspace.remove();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testServiceWithWorkspace() throws Exception {
        // Make a workspace
        WorkspaceInfo ws1 = geoServer.getCatalog().getFactory().createWorkspace();
        ws1.setName("TEST-WORKSPACE-1");
        geoServer.getCatalog().add(ws1);
        // Make a service for that workspace
        ServiceInfo newService1 = geoServer.getFactory().createService();
        newService1.setWorkspace(ws1);
        newService1.setName("SERVICE-1-WS-1");
        newService1.setTitle("Service for WS1");
        geoServer.add(newService1);
        // Make sure we have a global service
        ServiceInfo globalService = geoServer.getFactory().createService();
        globalService.setName("SERVICE-2-GLOBAL");
        globalService.setTitle("Global Service");
        geoServer.add(globalService);
        // Make another workspace
        WorkspaceInfo ws2 = geoServer.getCatalog().getFactory().createWorkspace();
        ws2.setName("TEST-WORKSPACE-2");
        geoServer.getCatalog().add(ws2);
        // Make a service for that workspace
        ServiceInfo newService2 = geoServer.getFactory().createService();
        newService2.setWorkspace(ws2);
        newService2.setName("SERVICE-3-WS-2");
        newService2.setTitle("Service for WS2");
        geoServer.add(newService2);
        // Check that we get the services we expect to
        Assert.assertThat(geoServer.getService(ServiceInfo.class), CoreMatchers.equalTo(globalService));
        Assert.assertThat(geoServer.getService(ws1, ServiceInfo.class), CoreMatchers.equalTo(newService1));
        Assert.assertThat(geoServer.getService(ws2, ServiceInfo.class), CoreMatchers.equalTo(newService2));
        Assert.assertThat(((Collection<ServiceInfo>) (geoServer.getServices())), CoreMatchers.allOf(CoreMatchers.hasItem(globalService), CoreMatchers.not(CoreMatchers.hasItems(newService1, newService2))));
        Assert.assertThat(((Collection<ServiceInfo>) (geoServer.getServices(ws1))), CoreMatchers.allOf(CoreMatchers.hasItem(newService1), CoreMatchers.not(CoreMatchers.hasItems(globalService, newService2))));
        Assert.assertThat(((Collection<ServiceInfo>) (geoServer.getServices(ws2))), CoreMatchers.allOf(CoreMatchers.hasItem(newService2), CoreMatchers.not(CoreMatchers.hasItems(newService1, globalService))));
    }

    @Test
    public void testModifyLogging() {
        LoggingInfo logging = geoServer.getLogging();
        logging.setLevel("VERBOSE_LOGGING.properties");
        geoServer.save(logging);
        Assert.assertEquals(logging, geoServer.getLogging());
    }
}

