/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.wms;


import GeoGigDataStoreFactory.BRANCH.key;
import GeogigLayerIntegrationListener.AUTHORITY_URL_NAME;
import java.util.List;
import org.geogig.geoserver.GeoGigTestData;
import org.geoserver.catalog.AuthorityURLInfo;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geoserver.wms.WMSInfo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


@TestSetup(run = TestSetupFrequency.REPEAT)
public class GeogigLayerIntegrationListenerTest extends GeoServerSystemTestSupport {
    @Rule
    public GeoGigTestData geogigData = new GeoGigTestData();

    @Test
    public void testAddGeogigLayerForcesCreationOfRootAuthURL() {
        addAvailableGeogigLayers();
        WMSInfo service = getGeoServer().getService(WMSInfo.class);
        List<AuthorityURLInfo> authorityURLs = service.getAuthorityURLs();
        AuthorityURLInfo expected = null;
        for (AuthorityURLInfo auth : authorityURLs) {
            if (AUTHORITY_URL_NAME.equals(auth.getName())) {
                expected = auth;
                break;
            }
        }
        Assert.assertNotNull(("No geogig auth url found: " + authorityURLs), expected);
    }

    @Test
    public void testAddGeogigLayerAddsLayerIdentifier() {
        addAvailableGeogigLayers();
        Catalog catalog = getCatalog();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        String layerName = (catalogBuilder.workspaceName()) + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(pointLayerInfo);
        layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(lineLayerInfo);
    }

    @Test
    public void testAddGeogigLayerAddsLayerIdentifierWithExplicitBranch() {
        addAvailableGeogigLayers();
        Catalog catalog = getCatalog();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        DataStoreInfo store = catalog.getDataStoreByName(catalogBuilder.storeName());
        store.getConnectionParameters().put(key, "master");
        catalog.save(store);
        String layerName = (catalogBuilder.workspaceName()) + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(pointLayerInfo);
        layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(lineLayerInfo);
    }

    @Test
    public void testAddGeogigLayerAddsLayerIdentifierWithExplicitHead() {
        addAvailableGeogigLayers();
        Catalog catalog = getCatalog();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        DataStoreInfo store = catalog.getDataStoreByName(catalogBuilder.storeName());
        store.getConnectionParameters().put(GeoGigDataStoreFactory.HEAD.key, "fakeBranch");
        catalog.save(store);
        String layerName = (catalogBuilder.workspaceName()) + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(pointLayerInfo);
        layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(lineLayerInfo);
    }

    @Test
    public void testRenameStore() {
        addAvailableGeogigLayers();
        Catalog catalog = getCatalog();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        String storeName = catalogBuilder.storeName();
        DataStoreInfo store = catalog.getStoreByName(storeName, DataStoreInfo.class);
        store.setName("new_store_name");
        catalog.save(store);
        String layerName = (catalogBuilder.workspaceName()) + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(pointLayerInfo);
        layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(lineLayerInfo);
    }

    @Test
    public void testRenameWorkspace() {
        addAvailableGeogigLayers();
        Catalog catalog = getCatalog();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        String wsName = catalogBuilder.workspaceName();
        WorkspaceInfo ws = catalog.getWorkspaceByName(wsName);
        String newWsName = "new_ws_name";
        ws.setName(newWsName);
        catalog.save(ws);
        String layerName = newWsName + ":points";
        LayerInfo pointLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(pointLayerInfo);
        layerName = newWsName + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        assertIdentifier(lineLayerInfo);
    }
}

