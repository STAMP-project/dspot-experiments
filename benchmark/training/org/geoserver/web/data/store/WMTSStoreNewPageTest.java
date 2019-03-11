/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.panel.WorkspacePanel;
import org.junit.Assert;
import org.junit.Test;


public class WMTSStoreNewPageTest extends GeoServerWicketTestSupport {
    /**
     * print page structure?
     */
    private static final boolean debugMode = true;

    /**
     * A kind of smoke test that only asserts the page is rendered when first loaded
     */
    @Test
    public void testPageRendersOnLoad() {
        startPage();
        GeoServerWicketTestSupport.tester.assertComponent("form:workspacePanel", WorkspacePanel.class);
    }

    @Test
    public void testInitialModelState() {
        WMTSStoreNewPage page = startPage();
        // print(page, true, true);
        Assert.assertNull(page.getDefaultModelObject());
        GeoServerWicketTestSupport.tester.assertModelValue("form:enabledPanel:paramValue", Boolean.TRUE);
        GeoServerWicketTestSupport.tester.assertModelValue("form:workspacePanel:border:border_body:paramValue", getCatalog().getDefaultWorkspace());
    }

    @Test
    public void testSaveNewStore() {
        WMTSStoreNewPage page = startPage();
        // print(page, true, true);
        Assert.assertNull(page.getDefaultModelObject());
        final Catalog catalog = getCatalog();
        WMTSStoreInfo info = catalog.getFactory().createWebMapTileServer();
        info.setName("foo");
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.select("workspacePanel:border:border_body:paramValue", 4);
        Component wsDropDown = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:workspacePanel:border:border_body:paramValue");
        GeoServerWicketTestSupport.tester.executeAjaxEvent(wsDropDown, "change");
        form.setValue("namePanel:border:border_body:paramValue", "foo");
        form.setValue("capabilitiesURL:border:border_body:paramValue", "http://foo");
        GeoServerWicketTestSupport.tester.clickLink("form:save", true);
        GeoServerWicketTestSupport.tester.assertErrorMessages("WMTS Connection test failed: foo");
        catalog.save(info);
        Assert.assertNotNull(info.getId());
        WMTSStoreInfo expandedStore = catalog.getResourcePool().clone(info, true);
        Assert.assertNotNull(expandedStore.getId());
        Assert.assertNotNull(expandedStore.getCatalog());
        catalog.validate(expandedStore, false).throwIfInvalid();
    }
}

