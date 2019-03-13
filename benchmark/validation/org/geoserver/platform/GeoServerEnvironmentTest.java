/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform;


import java.util.logging.Logger;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;

import static GeoServerEnvironment.ALLOW_ENV_PARAMETRIZATION;


/**
 * Unit test suite for {@link GeoServerEnvironment}
 *
 * @author Alessio Fabiani, GeoSolutions
 */
public class GeoServerEnvironmentTest {
    /**
     * logger
     */
    protected static final Logger LOGGER = Logging.getLogger("org.geoserver.platform");

    @Test
    public void testSystemProperty() {
        // check for a property we did set up in the setUp
        GeoServerEnvironment genv = new GeoServerEnvironment();
        GeoServerEnvironmentTest.LOGGER.info(("GeoServerEnvironment = " + (ALLOW_ENV_PARAMETRIZATION)));
        Assert.assertEquals("ABC", genv.resolveValue("${TEST_SYS_PROPERTY}"));
        Assert.assertEquals("${TEST_PROPERTY}", genv.resolveValue("${TEST_PROPERTY}"));
    }
}

