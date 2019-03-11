/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.cas;


import java.util.logging.Logger;
import org.geoserver.test.GeoServerMockTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Test;


public class CasFilterConfigValidatorTest extends GeoServerMockTestSupport {
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.security");

    CasFilterConfigValidator validator;

    @Test
    public void testCasFilterConfigValidation() throws Exception {
        CasAuthenticationFilterConfig config = new CasAuthenticationFilterConfig();
        config.setClassName(GeoServerCasAuthenticationFilter.class.getName());
        config.setName("testCAS");
        check(config);
        validator.validateCASFilterConfig(config);
    }
}

