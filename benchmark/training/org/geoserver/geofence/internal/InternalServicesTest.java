/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.internal;


import org.geoserver.geofence.ServicesTest;
import org.geoserver.geofence.server.rest.RulesRestController;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.RuleReaderServiceImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class InternalServicesTest extends ServicesTest {
    protected RulesRestController controller;

    protected RuleAdminService adminService;

    @Test
    public void testConfigurationInternal() {
        Assert.assertTrue(configManager.getConfiguration().isInternal());
        if ((geofenceService) != null) {
            Assert.assertTrue(((geofenceService) instanceof RuleReaderServiceImpl));
        }
    }
}

