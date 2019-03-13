/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import java.net.URL;
import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.panel.WorkspacePanel;
import org.junit.Assert;
import org.junit.Test;


public class WMSStoreNewPageTest extends GeoServerWicketTestSupport {
    /**
     * print page structure?
     */
    private static final boolean debugMode = false;

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
        WMSStoreNewPage page = startPage();
        // print(page, true, true);
        Assert.assertNull(page.getDefaultModelObject());
        GeoServerWicketTestSupport.tester.assertModelValue("form:enabledPanel:paramValue", Boolean.TRUE);
        GeoServerWicketTestSupport.tester.assertModelValue("form:workspacePanel:border:border_body:paramValue", getCatalog().getDefaultWorkspace());
    }

    @Test
    public void testSaveNewStore() {
        WMSStoreNewPage page = startPage();
        // print(page, true, true);
        Assert.assertNull(page.getDefaultModelObject());
        final Catalog catalog = getCatalog();
        WMSStoreInfo info = catalog.getFactory().createWebMapServer();
        info.setName("foo");
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.select("workspacePanel:border:border_body:paramValue", 4);
        Component wsDropDown = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:workspacePanel:border:border_body:paramValue");
        GeoServerWicketTestSupport.tester.executeAjaxEvent(wsDropDown, "change");
        form.setValue("namePanel:border:border_body:paramValue", "foo");
        form.setValue("capabilitiesURL:border:border_body:paramValue", "http://foo");
        GeoServerWicketTestSupport.tester.clickLink("form:save", true);
        GeoServerWicketTestSupport.tester.assertErrorMessages("Connection test failed: foo");
        catalog.save(info);
        Assert.assertNotNull(info.getId());
        WMSStoreInfo expandedStore = catalog.getResourcePool().clone(info, true);
        Assert.assertNotNull(expandedStore.getId());
        Assert.assertNotNull(expandedStore.getCatalog());
        catalog.validate(expandedStore, false).throwIfInvalid();
    }

    @Test
    public void testSaveNewStoreEntityExpansion() throws Exception {
        WMSStoreNewPage page = startPage();
        Assert.assertNull(page.getDefaultModelObject());
        final Catalog catalog = getCatalog();
        WMSStoreInfo info = getCatalog().getFactory().createWebMapServer();
        URL url = getClass().getResource("1.3.0Capabilities-xxe.xml");
        info.setName("bar");
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.select("workspacePanel:border:border_body:paramValue", 4);
        Component wsDropDown = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:workspacePanel:border:border_body:paramValue");
        GeoServerWicketTestSupport.tester.executeAjaxEvent(wsDropDown, "change");
        form.setValue("namePanel:border:border_body:paramValue", "bar");
        form.setValue("capabilitiesURL:border:border_body:paramValue", url.toExternalForm());
        GeoServerWicketTestSupport.tester.clickLink("form:save", true);
        GeoServerWicketTestSupport.tester.assertErrorMessages("Connection test failed: Error while parsing XML.");
        // make sure clearing the catalog does not clear the EntityResolver
        getGeoServer().reload();
        GeoServerWicketTestSupport.tester.clickLink("form:save", true);
        GeoServerWicketTestSupport.tester.assertErrorMessages("Connection test failed: Error while parsing XML.");
        catalog.save(info);
        Assert.assertNotNull(info.getId());
        WMSStoreInfo expandedStore = catalog.getResourcePool().clone(info, true);
        Assert.assertNotNull(expandedStore.getId());
        Assert.assertNotNull(expandedStore.getCatalog());
        catalog.validate(expandedStore, false).throwIfInvalid();
    }
}

