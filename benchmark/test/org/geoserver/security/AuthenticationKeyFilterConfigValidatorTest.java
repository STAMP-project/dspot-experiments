/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import java.util.logging.Logger;
import org.geoserver.test.GeoServerMockTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Test;


public class AuthenticationKeyFilterConfigValidatorTest extends GeoServerMockTestSupport {
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.security");

    AuthenticationKeyFilterConfigValidator validator;

    @Test
    public void testCasFilterConfigValidation() throws Exception {
        AuthenticationKeyFilterConfig config = new AuthenticationKeyFilterConfig();
        config.setClassName(GeoServerAuthenticationKeyFilter.class.getName());
        config.setName("testAuthKey");
        check(config);
        // validator.validateFilterConfig(config);
    }
}

