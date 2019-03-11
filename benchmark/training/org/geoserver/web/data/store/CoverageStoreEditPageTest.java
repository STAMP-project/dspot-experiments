/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import MockData.WCS_PREFIX;
import java.util.List;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class CoverageStoreEditPageTest extends GeoServerWicketTestSupport {
    CoverageStoreInfo coverageStore;

    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(CoverageStoreEditPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertLabel("rasterStoreForm:storeType", "GeoTIFF");
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:namePanel:border:border_body:paramValue", "BlueMarble");
    }

    @Test
    public void testChangeName() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("rasterStoreForm");
        form.setValue("namePanel:border:border_body:paramValue", "BlueMarbleModified");
        form.submit();
        GeoServerWicketTestSupport.tester.clickLink("rasterStoreForm:save");
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertRenderedPage(StorePage.class);
        Assert.assertNotNull(getCatalog().getStoreByName("BlueMarbleModified", CoverageStoreInfo.class));
    }

    @Test
    public void testNameRequired() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("rasterStoreForm");
        form.setValue("namePanel:border:border_body:paramValue", null);
        form.submit();
        GeoServerWicketTestSupport.tester.clickLink("rasterStoreForm:save");
        GeoServerWicketTestSupport.tester.assertRenderedPage(CoverageStoreEditPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Field 'Data Source Name' is required." });
    }

    /**
     * Test that changing a datastore's workspace updates the datastore's "namespace" parameter as
     * well as the namespace of its previously configured resources
     */
    @Test
    public void testWorkspaceSyncsUpWithNamespace() {
        final Catalog catalog = getCatalog();
        final FormTester formTester = GeoServerWicketTestSupport.tester.newFormTester("rasterStoreForm");
        final String wsDropdownPath = "rasterStoreForm:workspacePanel:border:border_body:paramValue";
        GeoServerWicketTestSupport.tester.assertModelValue(wsDropdownPath, catalog.getWorkspaceByName(WCS_PREFIX));
        // select the fifth item in the drop down, which is the cdf workspace
        formTester.select("workspacePanel:border:border_body:paramValue", 2);
        // weird on this test I need to both call form.submit() and also simulate clicking on the
        // ajax "save" link for the model to be updated. On a running geoserver instance it works ok
        // though
        formTester.submit();
        final boolean isAjax = true;
        GeoServerWicketTestSupport.tester.clickLink("rasterStoreForm:save", isAjax);
        // did the save finish normally?
        GeoServerWicketTestSupport.tester.assertRenderedPage(StorePage.class);
        CoverageStoreInfo store = catalog.getCoverageStore(coverageStore.getId());
        WorkspaceInfo workspace = store.getWorkspace();
        Assert.assertFalse(WCS_PREFIX.equals(workspace.getName()));
        // was the namespace for the datastore resources updated?
        List<CoverageInfo> resourcesByStore;
        resourcesByStore = catalog.getResourcesByStore(store, CoverageInfo.class);
        Assert.assertTrue(((resourcesByStore.size()) > 0));
        for (CoverageInfo cv : resourcesByStore) {
            Assert.assertEquals((("Namespace for " + (cv.getName())) + " was not updated"), workspace.getName(), cv.getNamespace().getPrefix());
        }
    }

    @Test
    public void testEditDetached() throws Exception {
        final Catalog catalog = getCatalog();
        CoverageStoreInfo store = catalog.getFactory().createCoverageStore();
        new org.geoserver.catalog.CatalogBuilder(catalog).updateCoverageStore(store, coverageStore);
        Assert.assertNull(store.getId());
        try {
            GeoServerWicketTestSupport.tester.startPage(new CoverageStoreEditPage(store));
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            FormTester form = GeoServerWicketTestSupport.tester.newFormTester("rasterStoreForm");
            form.setValue("namePanel:border:border_body:paramValue", "foo");
            form.submit();
            GeoServerWicketTestSupport.tester.clickLink("rasterStoreForm:save");
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            Assert.assertNotNull(store.getId());
            Assert.assertEquals("foo", store.getName());
            Assert.assertNotNull(catalog.getStoreByName(coverageStore.getName(), CoverageStoreInfo.class));
            Assert.assertNotNull(catalog.getStoreByName("foo", CoverageStoreInfo.class));
        } finally {
            catalog.remove(store);
        }
    }

    @Test
    public void testCoverageStoreEdit() throws Exception {
        final Catalog catalog = getCatalog();
        CoverageStoreInfo store = catalog.getFactory().createCoverageStore();
        new org.geoserver.catalog.CatalogBuilder(catalog).updateCoverageStore(store, coverageStore);
        Assert.assertNull(store.getId());
        try {
            GeoServerWicketTestSupport.tester.startPage(new CoverageStoreEditPage(store));
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            FormTester form = GeoServerWicketTestSupport.tester.newFormTester("rasterStoreForm");
            form.setValue("namePanel:border:border_body:paramValue", "foo");
            form.submit();
            GeoServerWicketTestSupport.tester.clickLink("rasterStoreForm:save");
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            Assert.assertNotNull(store.getId());
            CoverageStoreInfo expandedStore = catalog.getResourcePool().clone(store, true);
            Assert.assertNotNull(expandedStore.getId());
            Assert.assertNotNull(expandedStore.getCatalog());
            catalog.validate(expandedStore, false).throwIfInvalid();
        } finally {
            catalog.remove(store);
        }
    }
}

