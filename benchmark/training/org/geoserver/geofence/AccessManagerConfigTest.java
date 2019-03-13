/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import org.apache.commons.io.IOUtils;
import org.geoserver.geofence.config.GeoFenceConfiguration;
import org.geoserver.geofence.config.GeoFenceConfigurationManager;
import org.geoserver.geofence.config.GeoFencePropertyPlaceholderConfigurer;
import org.geoserver.geofence.utils.GeofenceTestUtils;
import org.geoserver.platform.resource.Resource;
import org.geoserver.test.GeoServerTestSupport;
import org.junit.Test;


public class AccessManagerConfigTest extends GeoServerTestSupport {
    // protected GeofenceAccessManager manager;
    // protected RuleReaderService geofenceService;
    GeoFencePropertyPlaceholderConfigurer configurer;

    GeoFenceConfigurationManager manager;

    @Test
    public void testSave() throws IOException, URISyntaxException {
        GeofenceTestUtils.emptyFile("test-config.properties");
        GeoFenceConfiguration config = new GeoFenceConfiguration();
        config.setInstanceName("TEST_INSTANCE");
        config.setServicesUrl("http://fakeservice");
        config.setAllowRemoteAndInlineLayers(true);
        config.setGrantWriteToWorkspacesToAuthenticatedUsers(true);
        config.setUseRolesToFilter(true);
        config.setAcceptedRoles("A,B");
        manager.setConfiguration(config);
        Resource configurationFile = configurer.getConfigFile();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(configurationFile.out()));
            writer.write("newUserProperty=custom_property_value\n");
        } finally {
            IOUtils.closeQuietly(writer);
        }
        manager.storeConfiguration();
        File configFile = configurer.getConfigFile().file();
        LOGGER.info(("Config file is " + configFile));
        String content = GeofenceTestUtils.readConfig(configFile);
        assertTrue(content.contains("fakeservice"));
        assertTrue(content.contains("TEST_INSTANCE"));
        assertTrue((!(content.contains("custom_property_value"))));
    }
}

