/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.netcdf;


import NetCDFSettingsContainer.NETCDFOUT_KEY;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.admin.GlobalSettingsPage;
import org.junit.Assert;
import org.junit.Test;


public class NetCDFOutSettingsPanelTest extends GeoServerWicketTestSupport {
    @Test
    public void testComponent() {
        GeoServerInfo info = getGeoServerApplication().getGeoServer().getGlobal();
        MetadataMap map = info.getSettings().getMetadata();
        login();
        // Opening the selected page
        tester.startPage(new GlobalSettingsPage());
        tester.assertRenderedPage(GlobalSettingsPage.class);
        tester.assertNoErrorMessage();
        // check if the component is present and initialized
        tester.assertComponent("form:extensions:0:content", NetCDFOutSettingsPanel.class);
        tester.assertComponent("form:extensions:0:content:panel", NetCDFPanel.class);
        NetCDFSettingsContainer container = map.get(NETCDFOUT_KEY, NetCDFSettingsContainer.class);
        // Ensure the element is in the map
        Assert.assertNotNull(container);
        // Ensure the panel is present
        NetCDFPanel panel = ((NetCDFPanel) (tester.getComponentFromLastRenderedPage("form:extensions:0:content:panel")));
        Assert.assertNotNull(panel);
        // Check that the values are the same
        NetCDFSettingsContainer container2 = ((NetCDFSettingsContainer) (panel.getModelObject()));
        Assert.assertNotNull(container2);
        Assert.assertEquals(container.getCompressionLevel(), container2.getCompressionLevel(), 0.001);
        // assertEquals(container.getNetcdfVersion(), container2.getNetcdfVersion());
        Assert.assertEquals(container.isShuffle(), container2.isShuffle());
        Assert.assertEquals(container.isCopyAttributes(), container2.isCopyAttributes());
        Assert.assertEquals(container.isCopyGlobalAttributes(), container2.isCopyGlobalAttributes());
        Assert.assertNotNull(container.getGlobalAttributes());
        Assert.assertNotNull(container2.getGlobalAttributes());
        Assert.assertEquals(container.getGlobalAttributes(), container2.getGlobalAttributes());
        Assert.assertNotNull(container.getVariableAttributes());
        Assert.assertNotNull(container2.getVariableAttributes());
        Assert.assertEquals(container.getVariableAttributes(), container2.getVariableAttributes());
        Assert.assertNotNull(container.getExtraVariables());
        Assert.assertNotNull(container2.getExtraVariables());
        Assert.assertEquals(container.getExtraVariables(), container2.getExtraVariables());
    }
}

