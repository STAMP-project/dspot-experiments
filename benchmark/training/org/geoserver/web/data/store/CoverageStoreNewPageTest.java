/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import org.apache.wicket.Component;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.panel.FileParamPanel;
import org.geoserver.web.data.store.panel.WorkspacePanel;
import org.geotools.geopkg.mosaic.GeoPackageFormat;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CoverageStoreNewPageTest extends GeoServerWicketTestSupport {
    /**
     * print page structure?
     */
    private static final boolean debugMode = false;

    String formatType;

    String formatDescription;

    @Test
    public void testInitCreateNewCoverageStoreInvalidDataStoreFactoryName() {
        final String formatName = "_invalid_";
        try {
            login();
            new CoverageStoreNewPage(formatName);
            Assert.fail("Expected IAE on invalid format name");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("Can't obtain the factory"));
        }
    }

    /**
     * A kind of smoke test that only asserts the page is rendered when first loaded
     */
    @Test
    public void testPageRendersOnLoad() {
        startPage();
        GeoServerWicketTestSupport.tester.assertLabel("rasterStoreForm:storeType", formatType);
        GeoServerWicketTestSupport.tester.assertLabel("rasterStoreForm:storeTypeDescription", formatDescription);
        GeoServerWicketTestSupport.tester.assertComponent("rasterStoreForm:workspacePanel", WorkspacePanel.class);
    }

    @Test
    public void testInitialModelState() {
        CoverageStoreNewPage page = startPage();
        // print(page, true, true);
        Assert.assertNull(page.getDefaultModelObject());
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:enabledPanel:paramValue", Boolean.TRUE);
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:workspacePanel:border:border_body:paramValue", getCatalog().getDefaultWorkspace());
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:parametersPanel:url", "file:data/example.extension");
    }

    @Test
    public void testMultipleResources() {
        CoverageStoreNewPage page = startPage();
        Assert.assertNull(page.getDefaultModelObject());
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:enabledPanel:paramValue", Boolean.TRUE);
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:workspacePanel:border:border_body:paramValue", getCatalog().getDefaultWorkspace());
        GeoServerWicketTestSupport.tester.assertModelValue("rasterStoreForm:parametersPanel:url", "file:data/example.extension");
    }

    @Test
    public void testGeoPackageRaster() {
        login();
        formatType = new GeoPackageFormat().getName();
        final CoverageStoreNewPage page = new CoverageStoreNewPage(formatType);
        GeoServerWicketTestSupport.tester.startPage(page);
        GeoServerWicketTestSupport.tester.debugComponentTrees();
        Component urlComponent = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("rasterStoreForm:parametersPanel:url");
        Assert.assertThat(urlComponent, CoreMatchers.instanceOf(FileParamPanel.class));
    }
}

