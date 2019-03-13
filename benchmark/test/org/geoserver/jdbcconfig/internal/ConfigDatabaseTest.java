/**
 * GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geoserver.jdbcconfig.internal;


import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import org.geoserver.catalog.CatalogException;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.event.CatalogAddEvent;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.catalog.event.CatalogPostModifyEvent;
import org.geoserver.catalog.event.CatalogRemoveEvent;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServer;
import org.geoserver.config.ServiceInfo;
import org.geoserver.jdbcconfig.JDBCConfigTestSupport;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSInfoImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author groldan
 */
@RunWith(Parameterized.class)
public class ConfigDatabaseTest {
    private JDBCConfigTestSupport testSupport;

    private ConfigDatabase database;

    private GeoServer geoServer;

    public ConfigDatabaseTest(JDBCConfigTestSupport.DBConfig dbConfig) {
        testSupport = new JDBCConfigTestSupport(dbConfig);
    }

    @Test
    public void testAdd() throws Exception {
        WorkspaceInfoImpl ws = new WorkspaceInfoImpl();
        try {
            database.add(ws);
            Assert.fail("Expected NPE on null id");
        } catch (NullPointerException e) {
            Assert.assertEquals("Object has no id", e.getMessage());
        }
        ws.setId("wsid");
        ws.setName("ws1");
        WorkspaceInfo addedWs = database.add(ws);
        Assert.assertNotNull(addedWs);
        Assert.assertTrue((addedWs instanceof Proxy));
        Assert.assertEquals(ws, addedWs);
        DataStoreInfo addedDs = addDataStore(ws);
        Assert.assertNotNull(addedDs);
    }

    @Test
    public void testModifyWorkspace() throws Exception {
        WorkspaceInfo ws = addWorkspace();
        ws.setName("newName");
        testSaved(ws);
    }

    @Test
    public void testRemoveWorkspace() {
        WorkspaceInfo ws = new WorkspaceInfoImpl();
        setId("removeid");
        ws.setName("remove");
        ws = database.add(ws);
        Assert.assertNotNull(database.getById(ws.getId(), WorkspaceInfo.class));
        // org.geoserver.catalog.NamespaceWorkspaceConsistencyListener.handleRemoveEvent(CatalogRemoveEvent)
        // can cause remove to actually be called twice on the workspace.
        database.remove(ws);
        database.remove(ws);
        // Notify of update
        database.getCatalog().fireRemoved(ws);
        Assert.assertNull(database.getById(ws.getId(), WorkspaceInfo.class));
    }

    @Test
    public void testModifyService() {
        // Create a service to modify
        WMSInfo service = new WMSInfoImpl();
        setId("WMS-TEST");
        service.setName("WMS");
        service.setMaintainer("Foo");
        service = database.add(service);
        Assert.assertEquals(service.getMaintainer(), "Foo");
        service.setMaintainer("Bar");
        testSaved(service);
    }

    @Test
    public void testCacheCatalog() throws Exception {
        // Simulates the situation where multiple GeoServer instances are sharing a database.
        WorkspaceInfo ws = addWorkspace();
        ws.setName("name1");
        testSaved(ws);
        // test identity cache
        ws = database.getByIdentity(WorkspaceInfo.class, "name", "name1");
        Assert.assertNotNull(ws);
        // Change the stored configuration
        // KS: sorry, this is an utter kludge
        Connection conn = testSupport.getDataSource().getConnection();
        try {
            Statement stmt = conn.createStatement();
            Assert.assertEquals(1, stmt.executeUpdate((("UPDATE object_property SET value='name2' WHERE property_type=(SELECT oid FROM property_type WHERE type_id = (SELECT oid FROM type WHERE typename='org.geoserver.catalog.WorkspaceInfo') AND name='name') AND id = '" + (ws.getId())) + "'")));
            Assert.assertEquals(1, stmt.executeUpdate((("UPDATE object SET blob=(SELECT replace(blob, '<name>name1</name>', '<name>name2</name>') FROM object WHERE id = '" + (ws.getId())) + "')")));
        } finally {
            conn.close();
        }
        // Should be cached
        WorkspaceInfo ws2 = database.getById(ws.getId(), WorkspaceInfo.class);
        Assert.assertEquals("name1", ws2.getName());
        // Notify of update
        testSupport.getCatalog().fireModified(ws2, Arrays.asList("name"), Arrays.asList("name1"), Arrays.asList("name2"));
        ws2.setName("name2");
        ModificationProxy.handler(ws2).commit();
        testSupport.getCatalog().firePostModified(ws2, Arrays.asList("name"), Arrays.asList("name1"), Arrays.asList("name2"));
        // Should show the new value
        WorkspaceInfo ws3 = database.getById(ws.getId(), WorkspaceInfo.class);
        Assert.assertEquals("name2", ws3.getName());
        // test identity cache update
        ws3 = database.getByIdentity(WorkspaceInfo.class, "name", "name1");
        Assert.assertNull(ws3);
        ws3 = database.getByIdentity(WorkspaceInfo.class, "name", "name2");
        Assert.assertNotNull(ws3);
    }

    @Test
    public void testCacheResourceLayer() throws Exception {
        // check that saving a resource updates the layer cache
        LayerInfo layer = addLayer();
        ResourceInfo resourceInfo = database.getById(layer.getResource().getId(), ResourceInfo.class);
        resourceInfo.setName("rs2");
        testSaved(resourceInfo);
        layer = database.getById(layer.getId(), LayerInfo.class);
        Assert.assertEquals("rs2", layer.getResource().getName());
    }

    @Test
    public void testCacheResourceLayerLocked() throws Exception {
        // check that saving a resource updates the layer cache
        LayerInfo layer = addLayer();
        ResourceInfo resourceInfo = database.getById(layer.getResource().getId(), ResourceInfo.class);
        resourceInfo.setName("rs2");
        testSupport.getCatalog().addListener(new CatalogListener() {
            @Override
            public void handleAddEvent(CatalogAddEvent event) throws CatalogException {
            }

            @Override
            public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
            }

            @Override
            public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {
                // this shouldn't cause re-caching because of lock
                database.getById(layer.getId(), LayerInfo.class);
            }

            @Override
            public void handlePostModifyEvent(CatalogPostModifyEvent event) throws CatalogException {
            }

            @Override
            public void reloaded() {
            }
        });
        testSupport.getFacade().save(resourceInfo);
        LayerInfo layer2 = database.getById(layer.getId(), LayerInfo.class);
        Assert.assertEquals("rs2", layer2.getResource().getName());
    }

    @Test
    public void testCacheConfig() throws Exception {
        // Simulates the situation where multiple GeoServer instances are sharing a database.
        ServiceInfo service = new WMSInfoImpl();
        setId("WMS-TEST");
        service.setName("WMS");
        service.setMaintainer("Foo");
        service = database.add(service);
        Assert.assertEquals(service.getMaintainer(), "Foo");
        // Change the stored configuration
        // KS: sorry, this is an utter kludge
        Connection conn = testSupport.getDataSource().getConnection();
        try {
            Statement stmt = conn.createStatement();
            // assertEquals(1, stmt.executeUpdate("UPDATE object_property SET value='Bar' WHERE
            // property_type=(SELECT oid FROM property_type WHERE type_id = (SELECT oid FROM type
            // WHERE typename='org.geoserver.wms.ServiceInfo') AND name='maintainer') AND id =
            // '"+service.getId()+"';"));
            Assert.assertEquals(1, stmt.executeUpdate((("UPDATE object SET blob=(SELECT replace(blob, '<maintainer>Foo</maintainer>', '<maintainer>Bar</maintainer>') FROM object WHERE id = '" + (service.getId())) + "')")));
        } finally {
            conn.close();
        }
        // Should be cached
        service = database.getById(service.getId(), ServiceInfo.class);
        Assert.assertEquals("Foo", service.getMaintainer());
        // Notify of update
        service.setMaintainer("Bar");
        for (ConfigurationListener l : database.getGeoServer().getListeners()) {
            l.handleServiceChange(service, null, null, null);
        }
        ModificationProxy.handler(service).commit();
        for (ConfigurationListener l : database.getGeoServer().getListeners()) {
            l.handlePostServiceChange(service);
        }
        // Should show the new value
        service = database.getById(service.getId(), ServiceInfo.class);
        Assert.assertEquals("Bar", service.getMaintainer());
    }

    @Test
    public void testGetServiceWithGeoServerRef() {
        WMSInfo service = new WMSInfoImpl();
        setId("WMS-TEST");
        service.setName("WMS");
        service.setMaintainer("Foo");
        service = database.add(service);
        database.clearCache(service);
        service = database.getAll(WMSInfo.class).iterator().next();
        Assert.assertNotNull(service.getGeoServer());
    }
}

