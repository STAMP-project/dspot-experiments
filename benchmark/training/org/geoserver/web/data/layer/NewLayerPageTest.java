/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.layer;


import MockData.CDF_PREFIX;
import MockData.CITE_PREFIX;
import WFSDataStoreFactory.URL.key;
import java.net.URL;
import java.util.Arrays;
import org.apache.wicket.Component;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.ResourcePool;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.data.DataStore;
import org.junit.Assert;
import org.junit.Test;


public class NewLayerPageTest extends GeoServerWicketTestSupport {
    private static final String TABLE_PATH = "selectLayersContainer:selectLayers:layers";

    @Test
    public void testKnownStore() {
        login();
        DataStoreInfo store = getCatalog().getStoreByName(CDF_PREFIX, DataStoreInfo.class);
        GeoServerWicketTestSupport.tester.startPage(new NewLayerPage(store.getId()));
        GeoServerWicketTestSupport.tester.assertRenderedPage(NewLayerPage.class);
        Assert.assertNull(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("selector"));
        GeoServerTablePanel table = ((GeoServerTablePanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage(NewLayerPageTest.TABLE_PATH)));
        Assert.assertEquals(getCatalog().getResourcesByStore(store, FeatureTypeInfo.class).size(), table.getDataProvider().size());
    }

    @Test
    public void testAjaxChooser() {
        login();
        GeoServerWicketTestSupport.tester.startPage(new NewLayerPage());
        GeoServerWicketTestSupport.tester.assertRenderedPage(NewLayerPage.class);
        // the tester will return null if the component is there, but not visible
        Assert.assertNull(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("selectLayersContainer:selectLayers"));
        // select the first datastore
        GeoServerWicketTestSupport.tester.newFormTester("selector").select("storesDropDown", 1);
        GeoServerWicketTestSupport.tester.executeAjaxEvent("selector:storesDropDown", "change");
        // now it should be there
        Assert.assertNotNull(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("selectLayersContainer:selectLayers"));
        // select "choose one" item (unselect the form)
        GeoServerWicketTestSupport.tester.newFormTester("selector").setValue("storesDropDown", "");
        GeoServerWicketTestSupport.tester.executeAjaxEvent("selector:storesDropDown", "change");
        // now it should be there
        Assert.assertNull(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("selectLayersContainer:selectLayers"));
    }

    @Test
    public void testAddLayer() throws Exception {
        login();
        DataStoreInfo store = getCatalog().getStoreByName(CITE_PREFIX, DataStoreInfo.class);
        NewLayerPage page = new NewLayerPage(store.getId());
        GeoServerWicketTestSupport.tester.startPage(page);
        // get the name of the first layer in the list
        String[] names = getTypeNames();
        Arrays.sort(names);
        GeoServerWicketTestSupport.tester.clickLink(((NewLayerPageTest.TABLE_PATH) + ":listContainer:items:1:itemProperties:2:component:link"), true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(ResourceConfigurationPage.class);
        Assert.assertEquals(names[0], getResourceInfo().getName());
    }

    @Test
    public void testAddLayerFromWFSDataStore() throws Exception {
        login();
        CatalogBuilder cb = new CatalogBuilder(getCatalog());
        DataStoreInfo storeInfo = cb.buildDataStore(CITE_PREFIX);
        setId("1");
        getCatalog().add(storeInfo);
        try {
            URL url = getClass().getResource("/WFSCapabilities.xml");
            storeInfo.getConnectionParameters().put(key, url);
            // required or the store won't fetch caps from a file
            storeInfo.getConnectionParameters().put("TESTING", Boolean.TRUE);
            final ResourcePool rp = getCatalog().getResourcePool();
            DataStore store = ((DataStore) (rp.getDataStore(storeInfo)));
            NewLayerPage page = new NewLayerPage(storeInfo.getId());
            GeoServerWicketTestSupport.tester.startPage(page);
            Component link = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("selectLayersContainer").get("createCascadedWFSStoredQueryContainer");
            Assert.assertTrue(link.isVisible());
        } finally {
            getCatalog().remove(storeInfo);
        }
    }

    @Test
    public void testAddLayerFromNotWFSDataStore() throws Exception {
        login();
        DataStoreInfo store = getCatalog().getStoreByName(CITE_PREFIX, DataStoreInfo.class);
        NewLayerPage page = new NewLayerPage(store.getId());
        GeoServerWicketTestSupport.tester.startPage(page);
        Component link = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("selectLayersContainer").get("createCascadedWFSStoredQueryContainer");
        Assert.assertFalse(link.isVisible());
    }
}

