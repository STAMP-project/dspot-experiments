/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.config;


import GeoserverXMLResourceProvider.GEOWEBCACHE_CACHE_DIR_PROPERTY;
import GeoserverXMLResourceProvider.GEOWEBCACHE_CONFIG_DIR_PROPERTY;
import java.io.File;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.util.IOUtils;
import org.geoserver.util.PropertyRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests that is possible to set a different GWC configuration directory using properties
 * GEOWEBCACHE_CONFIG_DIR_PROPERTY and GEOWEBCACHE_CACHE_DIR_PROPERTY.
 */
public final class GWCExternalConfigTest extends GeoServerSystemTestSupport {
    @Rule
    public PropertyRule configProp = PropertyRule.system(GEOWEBCACHE_CONFIG_DIR_PROPERTY);

    @Rule
    public PropertyRule cacheProp = PropertyRule.system(GEOWEBCACHE_CACHE_DIR_PROPERTY);

    private static final File rootTempDirectory;

    private static final String tempDirectory1;

    private static final String tempDirectory2;

    private static final String tempDirectory3;

    private static final String tempDirectory4;

    static {
        try {
            // init target directories
            rootTempDirectory = IOUtils.createTempDirectory("gwc");
            tempDirectory1 = new File(GWCExternalConfigTest.rootTempDirectory, "test-case-1").getCanonicalPath();
            tempDirectory2 = new File(GWCExternalConfigTest.rootTempDirectory, "test-case-2").getCanonicalPath();
            tempDirectory3 = new File(GWCExternalConfigTest.rootTempDirectory, "test-case-3").getCanonicalPath();
            tempDirectory4 = new File(GWCExternalConfigTest.rootTempDirectory, "test-case-4").getCanonicalPath();
        } catch (Exception exception) {
            throw new RuntimeException("Error initializing temporary directory.", exception);
        }
    }

    @Test
    public void testThatExternalDirectoryIsUsed() throws Exception {
        testUseCase(GWCExternalConfigTest.tempDirectory1, null, GWCExternalConfigTest.tempDirectory1);
        testUseCase(null, GWCExternalConfigTest.tempDirectory2, GWCExternalConfigTest.tempDirectory2);
        testUseCase(GWCExternalConfigTest.tempDirectory3, GWCExternalConfigTest.tempDirectory4, GWCExternalConfigTest.tempDirectory3);
    }
}

