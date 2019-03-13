/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.impl.handlers.configuration;


import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JmsWorkspaceHandlerTest extends GeoServerSystemTestSupport {
    private WorkspaceInfo testWorkspace;

    @Test
    public void testSettingsSimpleCrud() throws Exception {
        // settings events handler
        JMSSettingsHandler handler = createHandler();
        // create a new settings
        handler.synchronize(createNewSettingsEvent("settings1", "settings1"));
        checkSettingsExists("settings1");
        // update settings
        handler.synchronize(createModifySettingsEvent("settings2"));
        checkSettingsExists("settings2");
        // delete settings
        handler.synchronize(createRemoveSettings());
        Assert.assertThat(getGeoServer().getSettings(testWorkspace), CoreMatchers.nullValue());
    }
}

